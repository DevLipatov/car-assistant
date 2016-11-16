package com.main.carassistant.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.http.ConnectionChecker;
import com.main.carassistant.http.WeatherHttpClient;
import com.main.carassistant.model.Stats;
import com.main.carassistant.model.Weather;
import com.main.carassistant.parsing.weather.JsonWeatherParser;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity{

    TextView txtTemperature;
    ImageView imgWeather;
    JsonWeatherTask jsonWeatherTask;
    DbHelper dbHelper;

//    private ThreadManager threadManager = new ThreadManager(Executors.newFixedThreadPool(ThreadManager.COUNT_CORE));

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        imgWeather = (ImageView) findViewById(R.id.imgWeather);

        if (ConnectionChecker.checkConnection((getApplicationContext()))) {
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
    }

    @Override
    protected void onStop() {
        jsonWeatherTask.cancel(true);
        super.onStop();
    }

    public void onCreateNewStats(View view) {
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(intent);
    }

    public void onPhotoLayout(View view) {
        Intent intent2 = new Intent(getApplicationContext(), PhotosActivity.class);
        startActivity(intent2);
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

        final Cursor cursor = dbHelper.query("SELECT mileage, total_fueling FROM " + DbHelper.getTableName(Stats.class));
        cursor.moveToLast();
        String mileageValStr = String.valueOf(cursor.getInt(cursor.getColumnIndex("mileage")));
        String totalFueling = String.valueOf(cursor.getInt(cursor.getColumnIndex("total_fueling")));
        Toast.makeText(MainActivity.this, mileageValStr + "  " + totalFueling, Toast.LENGTH_SHORT).show();
        cursor.close();
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

    public void onResetStats (View view) {
        dbHelper.delete(Stats.class, null, null);
    }

    //TODO add stats view
}
