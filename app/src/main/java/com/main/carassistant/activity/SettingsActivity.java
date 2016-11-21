package com.main.carassistant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Stats;

public class SettingsActivity extends AppCompatActivity {

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        dbHelper = DbHelper.getHelper(getApplicationContext(), "CarAssistant.db", 4);
    }

    public void onResetStats (View view) {
        dbHelper.delete(Stats.class, null, null);
    }
}
