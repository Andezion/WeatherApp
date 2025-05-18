package com.example.lab2.storage;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.FileInputStream;

public class WeatherCache
{
    private static final String FILE_NAME = "weather_cache.json";

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
