package com.example.lab2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.lab2.adapters.CityPagerAdapter;
import com.example.lab2.api.WeatherApi;
import com.example.lab2.fragments.DetailsFragment;
import com.example.lab2.fragments.ForecastFragment;
import com.example.lab2.fragments.WeatherFragment;
import com.example.lab2.storage.FavoritesManager;
import com.example.lab2.storage.WeatherCache;
import com.example.lab2.utils.DepthPageTransformer;
import com.example.lab2.utils.JSonParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main extends AppCompatActivity
{
    private String defaultCity;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultCity = loadCityFromPreferences();

        List<String> cities = FavoritesManager.loadFavorites(this);
        CityPagerAdapter adapter = new CityPagerAdapter(this, cities);

        Log.d("FAVORITES", "Загруженные города: " + cities);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new DepthPageTransformer());

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(cities.get(position))
        ).attach();

        new Thread(() ->
        {
            try
            {
                String json = WeatherApi.fetchWeatherData(defaultCity);
                WeatherCache.saveToCache(getApplicationContext(), json);

                Map<String, String> weatherData = JSonParser.parseWeather(json);
                Log.d("WeatherData", weatherData.toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.menu_change_city)
        {
            showChangeCityDialog();
            return true;
        }
        else if (item.getItemId() == R.id.menu_remove_city)
        {
            showRemoveCityDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showRemoveCityDialog()
    {
        List<String> cities = FavoritesManager.loadFavorites(this);
        String[] cityArray = cities.toArray(new String[0]);

        if (cities.size() == 1)
        {
            Toast.makeText(this, "Нельзя удалить последний город", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите город для удаления");

        builder.setItems(cityArray, (dialog, which) ->
        {
            String cityToRemove = cityArray[which];
            FavoritesManager.removeCity(this, cityToRemove);
            Toast.makeText(this, "Удалено: " + cityToRemove, Toast.LENGTH_SHORT).show();

            String currentCity = loadCityFromPreferences();
            if (currentCity.equals(cityToRemove))
            {
                saveCityToPreferences("Lodz");
            }

            recreate();
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showChangeCityDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите город");

        final EditText input = new EditText(this);
        input.setHint("Например: Warsaw");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) ->
        {
            String newCity = input.getText().toString().trim();
            if (!newCity.isEmpty())
            {
                FavoritesManager.addCity(this, newCity);
                saveCityToPreferences(newCity);
                recreate();
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveCityToPreferences(String city)
    {
        getSharedPreferences("weather_prefs", MODE_PRIVATE)
                .edit()
                .putString("last_city", city)
                .apply();
    }

    private String loadCityFromPreferences()
    {
        return getSharedPreferences("weather_prefs", MODE_PRIVATE)
                .getString("last_city", "Lodz");
    }
}
