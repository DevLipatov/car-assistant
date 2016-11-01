package com.main.carassistant.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        imgWeather = (ImageView) findViewById(R.id.imgWeather);

        if (ConnectionChecker.checkConnection((getApplicationContext()))) {
            String city = "Hrodna";
            jsonWeatherTask = new JsonWeatherTask();
            jsonWeatherTask.execute(city);
        } else {
            txtTemperature.setTextSize(16);
            txtTemperature.setText(R.string.connection_error);
        }
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

    public void onNewDatabase (View view){
        final ContentValues values = new ContentValues();

        values.put(Stats.MILEAGE, 10);
        values.put(Stats.FUEL, 20);
        values.put(Stats.OIL, 30);
        values.put(Stats.DATE, System.currentTimeMillis());
        values.put(Stats.COMMENT, "Comment");

//        final IDbOperations operations = new DbHelper(MainActivity.this, "test.db", 1);

        final DbHelper operations = new DbHelper(MainActivity.this, "test.db", 1);

        final long id = operations.insert(Stats.class, values);

        if (id != 0) {
            Toast.makeText(MainActivity.this, "Successful operation INSERT  " + String.valueOf(id), Toast.LENGTH_SHORT).show();
        }

//        final Cursor cursor = operations.query("SELECT * FROM " + DbHelper.getTableName(Stats.class));
//
//        Toast.makeText(MainActivity.this, String.valueOf(cursor.getColumnCount()), Toast.LENGTH_SHORT).show();
//
//        cursor.close();
    }
}
