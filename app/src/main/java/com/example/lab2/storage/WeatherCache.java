package com.example.lab2.storage;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.FileInputStream;

// Сохраняем данные и читаем из из внутреннего файла приложения

public class WeatherCache
{
    private static final String FILE_NAME = "weather_cache.json";

    public static void saveForecastToCache(Context context, String city, String json)
    {
        context.getSharedPreferences("forecast_cache", Context.MODE_PRIVATE)
                .edit()
                .putString(city.toLowerCase(), json)
                .apply();
    }

    public static String readForecastFromCache(Context context, String city)
    {
        if (city == null)
        {
            Log.w("WeatherCache", "readForecastFromCache called with null city");
            return null;
        }
        return context.getSharedPreferences("forecast_cache", Context.MODE_PRIVATE)
                .getString(city.toLowerCase(), null);
    }

    public static void saveToCache(Context context, String jsonData)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(jsonData.getBytes());
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String readFromCache(Context context)
    {
        try
        {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            StringBuilder sb = new StringBuilder();

            int ch;
            while ((ch = fis.read()) != -1)
            {
                sb.append((char) ch);
            }

            fis.close();
            return sb.toString();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
