package com.main.carassistant.http;

import com.main.carassistant.BuildConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherHttpClient {

//    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String APIKEY = "&APPID=c3859f38427daf4b538322c8c539736c";
    private static String PARAMS = "&units=metric";

    public String getWeatherData(String cityID) {
        HttpURLConnection httpURLConnection = null ;
        InputStream inputStream;
        String response = null;

        try {
            URL url = new URL(BuildConfig.WEATHER_URL + cityID + PARAMS + APIKEY);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            StringBuilder stringBuilder = new StringBuilder();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            response = stringBuilder.toString();

            inputStream.close();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return response;
    }
}
