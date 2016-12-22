package com.main.carassistant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.main.carassistant.App;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Stats;

public class SettingsActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        dbHelper = ((App) getApplication()).getDbHelper();

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResetStats(View view) {
        dbHelper.delete(Stats.class, null, null);
    }
}
