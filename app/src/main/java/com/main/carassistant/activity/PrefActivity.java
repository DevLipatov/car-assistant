package com.main.carassistant.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.main.carassistant.R;

public class PrefActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}
