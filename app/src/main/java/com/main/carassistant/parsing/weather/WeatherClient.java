package com.main.carassistant.parsing.weather;

import android.os.Handler;
import com.main.carassistant.Constants.WeatherConst;
import com.main.carassistant.http.OwnHttpClient;
import com.main.carassistant.model.Weather;
import com.main.carassistant.threads.ResultCallback;
import org.json.JSONException;

public class WeatherClient {

    static Weather weather = new Weather();
    public static void getWeather(final String dataUrl, final Handler handler, final ResultCallback<Weather> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                OwnHttpClient ownHttpClient = new OwnHttpClient();
                String stringWeatherJson = ownHttpClient.getData(dataUrl);

                try {
                    //parse weather data
                    weather = JsonWeatherParser.getWeather(stringWeatherJson);

                    String weatherImgUrl = WeatherConst.IMG_URL + weather.getIconCode() + WeatherConst.IMG_TYPE;
                    //parse icon data
                    weather.iconData = ownHttpClient.getImage(weatherImgUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(weather);
                    }
                });
            }
        }).start();
    }
}
