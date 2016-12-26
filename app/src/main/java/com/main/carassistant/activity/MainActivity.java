package com.main.carassistant.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.main.carassistant.App;
import com.main.carassistant.R;
import com.main.carassistant.adapters.SamplePagerAdapter;
import com.main.carassistant.constants.WeatherConst;
import com.main.carassistant.db.ConsumptionGetter;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.design.ZoomOutPageTransformer;
import com.main.carassistant.http.ConnectionChecker;
import com.main.carassistant.model.Weather;
import com.main.carassistant.parsing.weather.WeatherClient;
import com.main.carassistant.threads.ResultCallback;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtTemperature;
    private ImageView imgWeather;
    private DbHelper dbHelper;
    private ViewPager viewPager;
    Toolbar toolbar;
    ProgressBar pb;
    NavigationView navigationView;
    private SamplePagerAdapter samplePagerAdapter;
    private View page;
    private DrawerLayout drawerLayout;
    public static android.os.Handler handler;
    private String city;
    private String value;
    SharedPreferences preferences;
    public SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        imgWeather = (ImageView) findViewById(R.id.imgWeather);
        pb = (ProgressBar) findViewById(R.id.pbWeather);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = ((App) getApplication()).getDbHelper();
        handler = new Handler();

        pb.setVisibility(View.VISIBLE);
        setNavigationView();

        preferences = getPreferences(MODE_PRIVATE);
        //TODO add city selection
        city = "Hrodna";

        setMeasurementValue();
        setStats();
        setWeather();
    }

    @Override
    protected void onResume() {
        setMeasurementValue();
        setStats();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        editor = preferences.edit();
        editor.putString("TEMP", txtTemperature.getText().toString());
        editor.apply();
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        preferences = getPreferences(MODE_PRIVATE);
        txtTemperature.setText(preferences.getString("TEMP", ""));
        setStats();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_add_stats) {
            intent = new Intent(getApplicationContext(), StatsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_photos) {
            intent = new Intent(getApplicationContext(), PhotosActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search_stats) {
            intent = new Intent(getApplicationContext(), AllStatsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search_costs) {
            intent = new Intent(getApplicationContext(), AllCostsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_business_card) {
            intent = new Intent(getApplicationContext(), BusinessCardActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_add_cost) {
            intent = new Intent(getApplicationContext(), CostsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings2) {
            intent = new Intent(getApplicationContext(), PrefActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setWeather() {

        String weatherUrl = WeatherConst.BASE_URL + city + WeatherConst.PARAMS + WeatherConst.APIKEY;
        if (ConnectionChecker.checkConnection(getApplicationContext())) {
            WeatherClient.getWeather(weatherUrl, handler, new ResultCallback<Weather>() {
                @Override
                public void onSuccess(Weather result) {
                    if (result.getTemp() != null) {
                        String temp = Integer.toString(result.getTemp()) + "°C";
                        txtTemperature.setText(temp);
                        //set image if exists
                        if (result.iconData != null && result.iconData.length > 0) {
                            Bitmap image = BitmapFactory.decodeByteArray(result.iconData, 0, result.iconData.length);
                            pb.setVisibility(View.GONE);
                            imgWeather.setImageBitmap(image);
                        }
                    }
                }
            });
        } else {
            pb.setVisibility(View.GONE);
            txtTemperature.setTextSize(16);
            txtTemperature.setText(R.string.connection_error);
        }
    }

    private void setMeasurementValue () {
        Boolean measurement = sp.getBoolean("measurement", false);
        editor = preferences.edit();

        if (measurement) {
            editor.putString("measurement", " l/km");
//            value = " l/km";
        } else {
            editor.putString("measurement", " l/ml");

//            value = " l/ml";
        }
        editor.apply();

    }

    private void setStats() {
        ConsumptionGetter.statsQuery(dbHelper, handler, new ResultCallback<ContentValues>() {
            @Override
            public void onSuccess(ContentValues result) {
                final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                //pages for ViewPager
                final List<View> pages = new ArrayList<>();

                setMeasurementValue();
                //TODO move to strings
                page = inflater.inflate(R.layout.pager_adapter_page, null);
                TextView textConsumption = (TextView) page.findViewById(R.id.tvConsumption);
                TextView textLabel = (TextView) page.findViewById(R.id.tvLabel);
//                textConsumption.setText(getString(R.string.measurement_blank, result.getAsString("Fuel_consumption_after_last"), value));
                textConsumption.setText(result.getAsString("Fuel_consumption_after_last") + preferences.getString("measurement", ""));
                textLabel.setText(R.string.fuel_consumption_after_last);
                pages.add(page);

                page = inflater.inflate(R.layout.pager_adapter_page, null);
                textConsumption = (TextView) page.findViewById(R.id.tvConsumption);
                textLabel = (TextView) page.findViewById(R.id.tvLabel);
//                textConsumption.setText(getString(R.string.measurement_blank, result.getAsString("Total_consumption"), value));

                textConsumption.setText(result.getAsString("Total_consumption") + preferences.getString("measurement", ""));
                textLabel.setText(R.string.total_fuel_consumption);
                pages.add(page);

                samplePagerAdapter = new SamplePagerAdapter(pages);
                viewPager.setAdapter(samplePagerAdapter);
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                viewPager.setCurrentItem(0);
            }
        });
    }

    private void setNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }
}
