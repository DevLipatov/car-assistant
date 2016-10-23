package com.main.carassistant.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.main.carassistant.R;
import com.main.carassistant.model.Weather;
import com.main.carassistant.parsing.weather.JsonWeatherParser;
import com.main.carassistant.http.WeatherHttpClient;
import org.json.JSONException;

public class StatsActivity extends AppCompatActivity{

    TextView textView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);
        textView = (TextView) findViewById(R.id.txtRes);

        String city = "Hrodna";
        JsonWeatherTask jsonWeatherTask = new JsonWeatherTask();
        jsonWeatherTask.execute(city);
    }

    private class JsonWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            WeatherHttpClient weatherHttpClient = new WeatherHttpClient();
            String stringWeatherJson = weatherHttpClient.getWeatherData(params[0]);

            try {
                weather = JsonWeatherParser.getWeather(stringWeatherJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            String temp = weather.getTemp()+ "°C";
            textView.setText(temp);
        }
    }
}
