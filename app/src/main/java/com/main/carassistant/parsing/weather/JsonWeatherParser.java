package com.main.carassistant.parsing.weather;

import com.main.carassistant.model.Weather;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonWeatherParser {

    public static Weather getWeather(String s) throws JSONException {

        Weather weather = new Weather();
        JSONObject jsonObject = new JSONObject(s);
        JSONObject mainJsonObject = jsonObject.getJSONObject("main");
        String mTemp = mainJsonObject.getString("temp");
        weather.setTemp(mTemp);

        return weather;
    }
}
