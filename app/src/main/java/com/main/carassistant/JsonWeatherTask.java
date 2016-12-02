package com.main.carassistant;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.main.carassistant.http.ConnectionChecker;
import com.main.carassistant.http.WeatherHttpClient;
import com.main.carassistant.model.Weather;
import com.main.carassistant.parsing.weather.JsonWeatherParser;

import org.json.JSONException;

public class JsonWeatherTask<T> extends AsyncTask<String, Void, T> {

    public interface ICallback<T> {
        void onStartRequest();

        void onSuccess(T result);
    }

    private ICallback mCallback;

    private Context mContext;

    public JsonWeatherTask(Context context, ICallback pCallback) {
        mCallback = pCallback;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback.onStartRequest();

    }

    @Override
    protected T doInBackground(String... params) {
        Weather weather = new Weather();
        if (ConnectionChecker.checkConnection(mContext)) {
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
        return (T) weather;
    }

    @Override
    protected void onPostExecute(T weather) {
        super.onPostExecute(weather);
        mCallback.onSuccess(weather);
    }
}
