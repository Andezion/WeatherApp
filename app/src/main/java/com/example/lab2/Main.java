package com.example.lab2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.api.WeatherApi;
import com.example.lab2.storage.WeatherCache;
import com.example.lab2.utils.JSonParser;

import java.util.Map;

public class Main extends AppCompatActivity
{
    private final String defaultCity = "Lodz";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new Thread(() ->
        {
            try
            {
                String json = WeatherApi.fetchWeatherData(defaultCity);
                WeatherCache.saveToCache(getApplicationContext(), json);

                Map<String, String> weatherData = JSonParser.parseWeather(json);
                Log.d("WeatherData", weatherData.toString());

                runOnUiThread(() ->
                        Toast.makeText(this, "Погода загружена: " + weatherData.get("temp") + "°C", Toast.LENGTH_LONG).show());

            }
            catch (Exception e)
            {
                Log.e("WeatherFetch", "Ошибка при получении данных - загружаем из кеша", e);
                String cachedJson = WeatherCache.readFromCache(getApplicationContext());

                if (cachedJson != null)
                {
                    Map<String, String> weatherData = JSonParser.parseWeather(cachedJson);
                    runOnUiThread(() ->
                            Toast.makeText(this, "Кеш: " + weatherData.get("temp") + "°C", Toast.LENGTH_LONG).show());
                }
                else
                {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Нет данных о погоде", Toast.LENGTH_LONG).show());
                }
            }
        }).start();
    }
}
