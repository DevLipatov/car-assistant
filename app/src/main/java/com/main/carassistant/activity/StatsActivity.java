package com.main.carassistant.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Stats;
import com.main.carassistant.threads.ResultCallback;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatsActivity extends AppCompatActivity {

    private EditText editMileage;
    private EditText editFueling;
    private EditText editOilFilled;
    private EditText editCurrentFuel;
    private EditText editComment;
    private DbHelper dbHelper;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        editMileage = (EditText) findViewById(R.id.editMileage);
        editFueling = (EditText) findViewById(R.id.editFueling);
        editOilFilled = (EditText) findViewById(R.id.editOilFilled);
        editCurrentFuel = (EditText) findViewById(R.id.editCurrentFuel);
        editComment = (EditText) findViewById(R.id.editComment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        dbHelper = DbHelper.getHelper(getApplicationContext(), "CarAssistant.db", 4);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddNewStats (View view) {
        String m = editMileage.getText().toString();
        String f = editFueling.getText().toString();
        String c = editCurrentFuel.getText().toString();
        String o = editOilFilled.getText().toString();

        //TODO ask how to do better
//        if (!editMileage.getText().toString().equals("")) {
//            contentValues.put(Stats.MILEAGE, Integer.valueOf(m));
//            if (!editFueling.getText().toString().equals("")) {
//                contentValues.put(Stats.FUELING, Integer.valueOf(m));
//                if ()
//            }
//        }

        //check for empty fields
        if (!m.isEmpty() && !f.isEmpty() && !c.isEmpty() && !o.isEmpty()) {

            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy   HH:mm", Locale.US);
            String today = formatter.format(date);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Stats.MILEAGE, Integer.valueOf(m));
            contentValues.put(Stats.FUELING, Integer.valueOf(f));
            contentValues.put(Stats.CURRENT_FUEL, Integer.valueOf(c));
            contentValues.put(Stats.OIL_FILLED, Integer.valueOf(o));
            contentValues.put(Stats.COMMENT, editComment.getText().toString());
            contentValues.put(Stats.DATE, today);

            insertOperation(contentValues, new ResultCallback<Long>() {
                @Override
                public void onSuccess(Long id) {
                    //TODO production - remove id from toast
                    Toast.makeText(getApplicationContext(), "Data in database: " + id + " fields", Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        } else {
            //creating dialog message
            String dialogMessage = "Fill ";
            int i = 0;
            if (m.equals("")) {
                dialogMessage += "Mileage ";
                i++;
            }
            if (f.equals("")) {
                if (i != 0) {
                    dialogMessage += ", ";
                }
                dialogMessage += "Fueling ";
                i++;
            }
            if (c.equals("")) {
                if (i != 0) {
                    dialogMessage += ", ";
                }
                dialogMessage += "Current fuel ";
                i++;
            }
            if (o.equals("")) {
                if (i != 0) {
                    dialogMessage += ", ";
                }
                dialogMessage += "Oil filled ";
                i++;
            }
            if (i > 1) {
                dialogMessage += "fields.";
            } else {
                dialogMessage += "field.";
            }
            showErrorDialog(dialogMessage);
        }
    }

    private void insertOperation(final ContentValues contentValues, final ResultCallback<Long> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get total fueling for fuel consumption
                //TODO move to another method.
                //TODO method insertOperation should do only inserting
                final Cursor cursor = dbHelper.query("SELECT total_fueling FROM " + DbHelper.getTableName(Stats.class));
                if (cursor.getCount()>0) {
                    cursor.moveToLast();
                    //update total fuel consumption
                    Integer total = Integer.valueOf(editFueling.getText().toString()) + cursor.getInt(cursor.getColumnIndex(Stats.TOTAL_FUELING));
                    cursor.close();
                    contentValues.put(Stats.TOTAL_FUELING, total);
                } else {
                    contentValues.put(Stats.TOTAL_FUELING, 0);

                }
                long id = dbHelper.insert(Stats.class, contentValues);
                callback.onSuccess(id);
            }
        }).run();
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
}
