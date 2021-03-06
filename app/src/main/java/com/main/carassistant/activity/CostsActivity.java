package com.main.carassistant.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.main.carassistant.App;
import com.main.carassistant.R;
import com.main.carassistant.constants.DataConst;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Costs;
import com.main.carassistant.threads.ResultCallback;
import java.util.Calendar;

public class CostsActivity extends AppCompatActivity {

    private EditText editDate;
    private EditText editCost;
    private Spinner spinnerCategory;
    private EditText editComment;
    private DbHelper dbHelper;
    private ContentValues contentValues = new ContentValues();
    private Calendar calendar = Calendar.getInstance();
    private Handler handler;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costs);

        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        editDate = (EditText) findViewById(R.id.editDate);
        editCost = (EditText) findViewById(R.id.editCost);
        editComment = (EditText) findViewById(R.id.editComment);
        dbHelper = ((App) getApplication()).getDbHelper();
        handler = new Handler();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

//        setInitialDateTime();

        //TODO add category ad/selection from db
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DataConst.CATEGORY);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setDate(View v) {
        new DatePickerDialog(CostsActivity.this, listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void setInitialDateTime() {
        editDate.setText(DateUtils.formatDateTime(this,
                calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    public void onAddNewCosts(View view) {
        String date = editDate.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String cost = editCost.getText().toString();
        String comment = editComment.getText().toString();

        if (!category.isEmpty() && !cost.isEmpty() && !comment.isEmpty()) {

            contentValues.put(Costs.DATE, editDate.getText().toString());
            contentValues.put(Costs.CATEGORY, spinnerCategory.getSelectedItem().toString());
            contentValues.put(Costs.COST, Long.valueOf(cost));
            contentValues.put(Costs.COMMENT, editComment.getText().toString());

            insertOperation(new ResultCallback<Long>() {
                @Override
                public void onSuccess(Long id) {
                    //TODO production - remove id from toast
                    Toast.makeText(getApplicationContext(), "Data in database: " + id + " field", Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        } else {
            showErrorDialog(createErrorDialog(date, category, cost, comment));
        }
    }

    //TODO move to universal method
    private String createErrorDialog(String date, String category, String cost, String comment) {
        String dialogMessage = "Fill ";
        int i = 0;
        if (date.equals("")) {
            dialogMessage += "'Date' ";
            i++;
        }
        if (category.equals("")) {
            if (i != 0) {
                dialogMessage += ", ";
            }
            dialogMessage += "'Category' ";
            i++;
        }
        if (cost.equals("")) {
            if (i != 0) {
                dialogMessage += ", ";
            }
            dialogMessage += "'Cost' ";
            i++;
        }
        if (comment.equals("")) {
            if (i != 0) {
                dialogMessage += ", ";
            }
            dialogMessage += "'Comment' ";
            i++;
        }
        if (i > 1) {
            dialogMessage += "fields.";
        } else {
            dialogMessage += "field.";
        }
        return dialogMessage;
    }

    private void showErrorDialog(final String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wrong data");
        builder.setMessage(dialogMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void insertOperation(final ResultCallback<Long> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long id = dbHelper.insert(Costs.class, contentValues);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(id);
                    }
                });
            }
        }).start();
    }
}
