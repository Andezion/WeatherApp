package com.example.lab2;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lab2.api.WeatherApi;
import com.example.lab2.fragments.DetailsFragment;
import com.example.lab2.fragments.ForecastFragment;
import com.example.lab2.fragments.WeatherFragment;
import com.example.lab2.storage.WeatherCache;
import com.example.lab2.utils.JSonParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.lab2.R;

import java.util.Map;

public class Main extends AppCompatActivity
{
    private final String defaultCity = "Lodz";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(() ->
        {
            try
            {
                String json = WeatherApi.fetchWeatherData(defaultCity);
                WeatherCache.saveToCache(getApplicationContext(), json);

                Map<String, String> weatherData = JSonParser.parseWeather(json);
                Log.d("WeatherData", weatherData.toString());

                runOnUiThread(() -> {
                    WeatherFragment fragment = new WeatherFragment(
                            weatherData.get("city"),
                            weatherData.get("lat"),
                            weatherData.get("lon"),
                            weatherData.get("temp"),
                            weatherData.get("description")
                    );

                    // ✅ правильно вставляем во FrameLayout
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();

                    BottomNavigationView nav = findViewById(R.id.bottom_navigation);
                    nav.setOnItemSelectedListener(item -> {
                        Fragment selectedFragment = null;
                        int itemId = item.getItemId();

                        if (itemId == R.id.nav_basic) {
                            selectedFragment = new WeatherFragment(
                                    weatherData.get("city"),
                                    weatherData.get("lat"),
                                    weatherData.get("lon"),
                                    weatherData.get("temp"),
                                    weatherData.get("description")
                            );
                        } else if (itemId == R.id.nav_details) {
                                selectedFragment = new DetailsFragment(
                                        weatherData.get("wind_speed"),
                                        weatherData.get("humidity"),
                                        weatherData.containsKey("visibility")
                                                ? weatherData.get("visibility")
                                                : "10000"
                                );

                        } else if (itemId == R.id.nav_forecast) {
                            selectedFragment = new ForecastFragment();
                        }

                        if (selectedFragment != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, selectedFragment)
                                    .commit();
                            return true;
                        }

                        return false;
                    });
                });

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
