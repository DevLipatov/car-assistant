package com.main.carassistant.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Stats;

public class StatsActivity extends AppCompatActivity {

    EditText editMileage;
    EditText editFueling;
    EditText editOilFilled;
    EditText editCurrentFuel;
    EditText editComment;

    TextView txtMileage;
    TextView txtFueling;
    TextView txtCurrentFuel;
    TextView txtOilFilled;

    DbHelper dbHelper;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        editMileage = (EditText) findViewById(R.id.editMileage);
        editFueling = (EditText) findViewById(R.id.editFueling);
        editOilFilled = (EditText) findViewById(R.id.editOilFilled);
        editCurrentFuel = (EditText) findViewById(R.id.editCurrentFuel);
        editComment = (EditText) findViewById(R.id.editComment);

        dbHelper = DbHelper.getHelper(getApplicationContext(), "CarAssistant.db", 1);
    }

    public void onAddNewStats (View view) {

        final ContentValues contentValues = new ContentValues();

        String m = editMileage.getText().toString();
        String f = editFueling.getText().toString();
        String c = editCurrentFuel.getText().toString();
        String o = editOilFilled.getText().toString();

        if (!m.equals("") && !f.equals("") && !c.equals("") && !o.equals("")) {

            contentValues.put(Stats.MILEAGE, Integer.valueOf(m));
            contentValues.put(Stats.FUELING, Integer.valueOf(f));
            contentValues.put(Stats.CURRENT_FUEL, Integer.valueOf(c));
            contentValues.put(Stats.OIL_FILLED, Integer.valueOf(o));
            contentValues.put(Stats.COMMENT, editComment.getText().toString());
            contentValues.put(Stats.DATE, System.currentTimeMillis());

            long id = dbHelper.insert(Stats.class, contentValues);

            if (id != 0) {
                Toast.makeText(getApplicationContext(), "Data in database  " + id, Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wrong data");
        builder.setMessage("Fill all stat fields");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeOptionsMenu();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
