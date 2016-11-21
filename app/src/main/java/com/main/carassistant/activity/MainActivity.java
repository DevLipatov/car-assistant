package com.main.carassistant.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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

public class MainActivity extends AppCompatActivity {

    TextView txtTemperature;
    ImageView imgWeather;
    JsonWeatherTask jsonWeatherTask;
    DbHelper dbHelper;
    ViewPager viewPager;
    Toolbar toolbar;
    SamplePagerAdapter samplePagerAdapter;
    View page;

//    private ThreadManager threadManager = new ThreadManager(Executors.newFixedThreadPool(ThreadManager.COUNT_CORE));

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        imgWeather = (ImageView) findViewById(R.id.imgWeather);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        if (ConnectionChecker.checkConnection(getApplicationContext())) {
            //TODO add city selection
            String city = "Hrodna";
            jsonWeatherTask = new JsonWeatherTask();
            jsonWeatherTask.execute(city);
        } else {
            txtTemperature.setTextSize(16);
            txtTemperature.setText(R.string.connection_error);
        }

        // TODO --to App
        dbHelper = DbHelper.getHelper(getApplicationContext(), "CarAssistant.db", 4);

        //TODO ask about inflater declaration there
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        page = inflater.inflate(R.layout.pager_adapter_page, null);
        //TODO ask about TextView declaration there
        TextView textView = (TextView) page.findViewById(R.id.text_view);
        textView.setText("Страница 1");
        pages.add(page);

        page = inflater.inflate(R.layout.pager_adapter_page, null);
        textView = (TextView) page.findViewById(R.id.text_view);
        textView.setText("Страница 2");
        pages.add(page);

        samplePagerAdapter = new SamplePagerAdapter(pages);
        viewPager.setAdapter(samplePagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setCurrentItem(0);
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
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            String temp = Integer.toString(weather.getTemp()) + "°C";
            txtTemperature.setText(temp);

            //set image if exists
            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap image = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                pb.setVisibility(View.GONE);
                imgWeather.setImageBitmap(image);
            }
        }
    }

    public void onQuery (View view) {
        statsQuery(new ResultCallback<ContentValues>() {
            @Override
            public void onSuccess(ContentValues result) {
                if (result != null) {
//                    txtFConsAfterLast.setText(result.getAsString("Fuel_consumption_after_last"));
                    Toast.makeText(getApplicationContext(), result.getAsString("Fuel_consumption_after_last"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No data to calculate", Toast.LENGTH_SHORT).show();
//                    txtFConsAfterLast.setText("No data to calculate");
                }
            }
        });
    }

    public void onCreateNewStats(View view) {
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(intent);
    }

    public void onPhotoLayout(View view) {
        Intent intent2 = new Intent(getApplicationContext(), PhotosActivity.class);
        startActivity(intent2);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:55.754283,37.62002"));
        startActivity(intent);
    }

    public void onAllStatsActivity (View view) {
        Intent intent = new Intent(getApplicationContext(), AllStatsActivity.class);
        startActivity(intent);
    }

    public void onSettingsActivity (View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void statsQuery(final ResultCallback<ContentValues> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues lastRowValues = null;
                ContentValues contentValues = null;
                final Cursor cursor = dbHelper.query("SELECT mileage, fueling, current_fuel, oil_filled, total_fueling  FROM " + DbHelper.getTableName(Stats.class));
                if (cursor.getCount()>1) {
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
