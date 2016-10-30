package com.main.carassistant.threads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.main.carassistant.http.WeatherHttpClient;
import com.main.carassistant.model.Weather;
import com.main.carassistant.parsing.weather.JsonWeatherParser;
import org.json.JSONException;

public class WeatherThread {

    public static Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Weather weather = new Weather();
            WeatherHttpClient weatherHttpClient = new WeatherHttpClient();
            String stringWeatherJson = weatherHttpClient.getWeatherData("Hrodna");

            try {
                //parse weather data
                weather = JsonWeatherParser.getWeather(stringWeatherJson);

                //parse icon data
                weather.iconData = weatherHttpClient.getImage(weather.getIconCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String temp = Integer.toString(weather.getTemp()) + "°C";

            Handler handler = null;
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("weather", temp);
            bundle.putByteArray("icon", weather.iconData);
            bundle.putBoolean("success", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    };
}
