package com.main.carassistant.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.main.carassistant.Constants.SqlConst;
import com.main.carassistant.R;
import com.main.carassistant.adapters.SamplePagerAdapter;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.design.ZoomOutPageTransformer;
import com.main.carassistant.http.ConnectionChecker;
import com.main.carassistant.http.WeatherHttpClient;
import com.main.carassistant.model.Stats;
import com.main.carassistant.model.Weather;
import com.main.carassistant.parsing.weather.JsonWeatherParser;
import com.main.carassistant.threads.ResultCallback;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtTemperature;
    private ImageView imgWeather;
    JsonWeatherTask jsonWeatherTask;
    private DbHelper dbHelper;
    private ViewPager viewPager;
    Toolbar toolbar;
    NavigationView navigationView;
    private SamplePagerAdapter samplePagerAdapter;
    private View page;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        imgWeather = (ImageView) findViewById(R.id.imgWeather);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        dbHelper = ((App) getApplication()).getDbHelper();

        setNavigationView();

        //TODO add city selection
        String city = "Hrodna";
        jsonWeatherTask = new JsonWeatherTask();
        jsonWeatherTask.execute(city);

        setStats();
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
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null&&drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class JsonWeatherTask extends AsyncTask<String, Void, Weather> {

        ProgressBar pb = (ProgressBar) findViewById(R.id.pbWeather);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            if (ConnectionChecker.checkConnection(getApplicationContext())) {
                WeatherHttpClient weatherHttpClient = new WeatherHttpClient();
                String stringWeatherJson = weatherHttpClient.getWeatherData(params[0]);

                try {
                    //parse weather data
                    weather = JsonWeatherParser.getWeather(stringWeatherJson);

                    //parse icon data
                    weather.iconData = weatherHttpClient.getImage(weather.getIconCode());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            if (weather.getTemp()!=null) {
                String temp = Integer.toString(weather.getTemp()) + "°C";
                txtTemperature.setText(temp);
                //set image if exists
                if (weather.iconData != null && weather.iconData.length > 0) {
                    Bitmap image = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                    pb.setVisibility(View.GONE);
                    imgWeather.setImageBitmap(image);
                }
            } else {
                pb.setVisibility(View.GONE);
                txtTemperature.setTextSize(16);
                txtTemperature.setText(R.string.connection_error);
            }
        }
    }

    public void onMapClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:55.754283,37.62002"));
        startActivity(intent);
    }

    private void setStats() {
        statsQuery(new ResultCallback<ContentValues>() {
            @Override
            public void onSuccess(ContentValues result) {
                final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                //pages for ViewPager
                final List<View> pages = new ArrayList<>();

                page = inflater.inflate(R.layout.pager_adapter_page, null);
                TextView textView = (TextView) page.findViewById(R.id.text_view);
                textView.setText(result.getAsString("Fuel_consumption_after_last"));
                pages.add(page);

                page = inflater.inflate(R.layout.pager_adapter_page, null);
                textView = (TextView) page.findViewById(R.id.text_view);
                textView.setText(result.getAsString("Total_consumption"));
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,0,0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    private void statsQuery(final ResultCallback<ContentValues> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues lastRowValues = new ContentValues();
                ContentValues contentValues = new ContentValues();
                Cursor cursor = dbHelper.query(SqlConst.QUERY_FOR_CONSUMPTION + DbHelper.getTableName(Stats.class));
                if (cursor.getCount() > 1) {
                    cursor.moveToLast();
                    lastRowValues.put(Stats.MILEAGE, cursor.getInt(cursor.getColumnIndex(Stats.MILEAGE)));
                    lastRowValues.put(Stats.FUELING, cursor.getInt(cursor.getColumnIndex(Stats.FUELING)));
//                    lastRowValues.put(Stats.OIL_FILLED, cursor.getInt(cursor.getColumnIndex(Stats.OIL_FILLED)));
                    lastRowValues.put(Stats.TOTAL_FUELING, cursor.getInt(cursor.getColumnIndex(Stats.TOTAL_FUELING)));
                    lastRowValues.put(Stats.CURRENT_FUEL, cursor.getInt(cursor.getColumnIndex(Stats.CURRENT_FUEL)));
                    cursor.moveToPrevious();

                    //potracheno topliva posle posledney zapravki
                    //TODO rename
                    int fuel_stealed = cursor.getInt(cursor.getColumnIndex(Stats.CURRENT_FUEL)) - (lastRowValues.getAsInteger(Stats.CURRENT_FUEL)-lastRowValues.getAsInteger(Stats.FUELING));
                    //probeg posle posledney zapravki
                    int last_run = lastRowValues.getAsInteger(Stats.MILEAGE) - cursor.getInt(cursor.getColumnIndex(Stats.MILEAGE));
                    //rashod posle posledney zapravki
                    contentValues.put("Fuel_consumption_after_last", fuel_stealed*100/last_run);

                    //TODO add oil consumption
//                    int oil_stealed = cursor.getInt(cursor.getColumnIndex(Stats.OIL_FILLED)) - (lastRowValues.getAsInteger(Stats.OIL_FILLED)-lastRowValues.getAsInteger(Stats.OIL_FILLED));

                    cursor.moveToFirst();
                    int total_run = lastRowValues.getAsInteger(Stats.MILEAGE) - cursor.getInt(cursor.getColumnIndex(Stats.MILEAGE));
                    contentValues.put("Total_consumption", lastRowValues.getAsInteger(Stats.TOTAL_FUELING)*100/total_run);
                    cursor.close();
                    lastRowValues.clear();
                }
                callback.onSuccess(contentValues);
            }
        }).run();
    }
}
