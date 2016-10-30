package com.main.carassistant.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherHttpClient {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String APIKEY = "&APPID=c3859f38427daf4b538322c8c539736c";
    private static String PARAMS = "&units=metric";
    private static String IMG_URL = "http://openweathermap.org/img/w/";

    public String getWeatherData(String cityID) {
        HttpURLConnection httpURLConnection = null ;
        String response = null;

        try {
            URL url = new URL(BASE_URL + cityID + PARAMS + APIKEY);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = httpURLConnection.getInputStream();
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

    public byte[] getImage (String imgCode){

        HttpURLConnection httpURLConnection = null ;

        try {
            URL url = new URL(IMG_URL + imgCode + ".png");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] buffer = new byte[inputStream.available()];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            while ( inputStream.read(buffer) != -1) {
                byteArrayOutputStream.write(buffer);
            }

            inputStream.close();

            return byteArrayOutputStream.toByteArray();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }
}
