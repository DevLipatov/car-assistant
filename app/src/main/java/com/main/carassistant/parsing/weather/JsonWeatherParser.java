package com.main.carassistant.parsing.weather;

import com.main.carassistant.model.Weather;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonWeatherParser {

    public static Weather getWeather(String s) throws JSONException {

        Weather weather = new Weather();
        Integer mTemp;
        String mIconCode;

        //Our json
        JSONObject jsonObject = new JSONObject(s);

        //Get temepature
        JSONObject mainJsonObject = jsonObject.getJSONObject("main");
        mTemp = mainJsonObject.getInt("temp");
        weather.setTemp(mTemp);

        //Get weather icon code
        JSONArray jsonArray = jsonObject.getJSONArray("weather");
        JSONObject zeroJsonObject = jsonArray.getJSONObject(0);
        mIconCode  = zeroJsonObject.getString("icon");
        weather.setIconCode(mIconCode);

        return weather;
    }
}
