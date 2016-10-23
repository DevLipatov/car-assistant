package com.main.carassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.main.carassistant.R;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
    }

    public void onCreateNewStats(View view){
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(intent);
    }



}
