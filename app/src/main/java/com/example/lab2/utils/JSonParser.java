package com.example.lab2.utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JSonParser
{
    public static Map<String, String> parseWeather(String jsonString)
    {
        Map<String, String> weatherData = new HashMap<>();

        try
        {
            JSONObject obj = new JSONObject(jsonString);

            JSONObject main = obj.getJSONObject("main");
            JSONObject wind = obj.getJSONObject("wind");
            JSONObject weather = obj.getJSONArray("weather").getJSONObject(0);
            JSONObject coord = obj.getJSONObject("coord");

            weatherData.put("city", obj.getString("name"));
            weatherData.put("temp", main.getString("temp"));
            weatherData.put("pressure", main.getString("pressure"));
            weatherData.put("humidity", main.getString("humidity"));
            weatherData.put("description", weather.getString("description"));
            weatherData.put("icon", weather.getString("icon"));
            weatherData.put("wind_speed", wind.getString("speed"));
            weatherData.put("wind_deg", wind.getString("deg"));
            weatherData.put("lat", coord.getString("lat"));
            weatherData.put("lon", coord.getString("lon"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return weatherData;
    }
}
