package com.main.carassistant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.main.carassistant.R;
import com.main.carassistant.model.CarStats;

public class StatsActivity extends AppCompatActivity {

    EditText editMileage;
    EditText editFueling;
    EditText editOiled;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        editMileage = (EditText) findViewById(R.id.editMileage);
        editFueling = (EditText) findViewById(R.id.editFueling);
        editOiled = (EditText) findViewById(R.id.editOilFilled);
    }

    public void onStatsAdded (View view){

        try {
            CarStats carStats = new CarStats.Builder()
                    .setMileage(Integer.valueOf(editMileage.getText().toString()))
                    .setFueling(Integer.valueOf(editFueling.getText().toString()))
                    .setOil(Integer.valueOf(editOiled.getText().toString()))
                    .build();
        } catch (NumberFormatException e){
            Toast.makeText(getApplicationContext(), "Enter correct data please", Toast.LENGTH_SHORT).show();
        }
    }
}
