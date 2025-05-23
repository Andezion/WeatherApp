package com.example.lab2.utils;

import com.example.lab2.model.ForecastItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Чтобы было понятно что отображать и за когда

public class ForecastParser
{
    public static List<ForecastItem> parseForecast(String json)
    {
        List<ForecastItem> forecastList = new ArrayList<>();

        try
        {
            JSONObject obj = new JSONObject(json);
            JSONArray list = obj.getJSONArray("list");

            for (int i = 0; i < list.length(); i++)
            {
                JSONObject item = list.getJSONObject(i);
                String dt_txt = item.getString("dt_txt");

                if (dt_txt.contains("12:00:00"))
                {
                    String temp = item.getJSONObject("main").getString("temp");
                    String description = item.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description");
                    String iconCode = item.getJSONArray("weather").getJSONObject(0).getString("icon");

                    forecastList.add(new ForecastItem(dt_txt, temp, description, iconCode));
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return forecastList;
    }
}
