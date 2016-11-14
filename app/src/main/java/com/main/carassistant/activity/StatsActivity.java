package com.main.carassistant.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Stats;
import com.main.carassistant.threads.ResultCallback;

public class StatsActivity extends AppCompatActivity {

    EditText editMileage;
    EditText editFueling;
    EditText editOilFilled;
    EditText editCurrentFuel;
    EditText editComment;

    DbHelper dbHelper;

    ContentValues contentValues = new ContentValues();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        editMileage = (EditText) findViewById(R.id.editMileage);
        editFueling = (EditText) findViewById(R.id.editFueling);
        editOilFilled = (EditText) findViewById(R.id.editOilFilled);
        editCurrentFuel = (EditText) findViewById(R.id.editCurrentFuel);
        editComment = (EditText) findViewById(R.id.editComment);

        dbHelper = DbHelper.getHelper(getApplicationContext(), "CarAssistant.db", 2);
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
        if (!m.equals("") && !f.equals("") && !c.equals("") && !o.equals("")) {

            contentValues.put(Stats.MILEAGE, Integer.valueOf(m));
            contentValues.put(Stats.FUELING, Integer.valueOf(f));
            contentValues.put(Stats.CURRENT_FUEL, Integer.valueOf(c));
            contentValues.put(Stats.OIL_FILLED, Integer.valueOf(o));
            contentValues.put(Stats.COMMENT, editComment.getText().toString());
            contentValues.put(Stats.DATE, System.currentTimeMillis());

            insertOperation(new ResultCallback<Long>() {
                @Override
                public void onSuccess(Long id) {
                    //TODO production - remove id from toast
                    Toast.makeText(getApplicationContext(), "Data in database  " + id, Toast.LENGTH_SHORT).show();
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

    private void insertOperation(final ResultCallback<Long> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get total fueling for fuel consumption
                final Cursor cursor = dbHelper.query("SELECT total_fueling FROM " + DbHelper.getTableName(Stats.class));
                cursor.moveToLast();
                //update total fuel consumption
                Integer total = Integer.valueOf(editFueling.getText().toString()) + cursor.getInt(cursor.getColumnIndex("total_fueling"));
                cursor.close();
                contentValues.put(Stats.TOTAL_FUELING, total);
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
