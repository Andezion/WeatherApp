package com.example.lab2;

import static android.text.TextUtils.replace;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.lab2.adapters.CityPagerAdapter;
import com.example.lab2.api.WeatherApi;
import com.example.lab2.fragments.DetailsFragment;
import com.example.lab2.fragments.ForecastFragment;
import com.example.lab2.fragments.SettingsFragment;
import com.example.lab2.fragments.WeatherFragment;
import com.example.lab2.model.ForecastItem;
import com.example.lab2.storage.FavoritesManager;
import com.example.lab2.storage.WeatherCache;
import com.example.lab2.utils.DepthPageTransformer;
import com.example.lab2.utils.ForecastParser;
import com.example.lab2.utils.JSonParser;
import com.example.lab2.utils.NetworkUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Main extends AppCompatActivity
{
    private ViewPager2 viewPager;
    private CityPagerAdapter adapter;
    private List<String> cities;
    private TabLayout tabLayout;

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable weatherUpdater = new Runnable() {
        @Override
        public void run()
        {
            if (NetworkUtils.isNetworkAvailable(Main.this))
            {
                loadWeatherFromApi();
            }
            handler.postDelayed(this, 5 * 60 * 1000);
        }
    };

    private String getPreferredUnits() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("units", "metric"); // или "imperial"
    }
    private void loadWeatherFromApi() {
        if (cities == null || cities.isEmpty()) return;

        String units = getPreferredUnits(); // например: "metric"
        for (String city : cities) {
            new Thread(() -> {
                try {
                    String forecastJson = WeatherApi.fetchForecastData(city, units);

                    runOnUiThread(() -> {
                        WeatherCache.saveForecastToCache(Main.this, city, forecastJson);
                        Log.d("WeatherUpdate", "updated: " + city);
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> Log.e("WeatherUpdate", "error for: " + city, e));
                }
            }).start();
        }
    }

    private boolean isTablet() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void forceEnglishLocale() {
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        forceEnglishLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);


        // tablet

        if(isTablet())
        {
            cities = new ArrayList<>(FavoritesManager.loadFavorites(this));
            if (cities.isEmpty()) {
                cities.add("Lodz");
                FavoritesManager.saveFavorites(this, cities);
            }
            String defaultCity = cities.get(0);

//            String cachedJson = WeatherCache.readForecastFromCache(this, defaultCity);
//            Map<String, String> data = null;
//            if (cachedJson != null) {
//                data = JSonParser.parseWeather(cachedJson);
//            }

            Map<String, String> data = null;

            if (!NetworkUtils.isNetworkAvailable(this))
            {
                String cachedJson = WeatherCache.readForecastFromCache(this, defaultCity);
                if (cachedJson != null)
                {
                    data = JSonParser.parseWeather(cachedJson);
                }
            }

            Bundle detailsBundle = new Bundle();
            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    detailsBundle.putString(entry.getKey(), entry.getValue());
                }
            }

            WeatherFragment weatherFragment = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                weatherFragment = WeatherFragment.newInstance(defaultCity,
                        data != null ? data.getOrDefault("lat", "0") : "0",
                        data != null ? data.getOrDefault("lon", "0") : "0",
                        data != null ? data.getOrDefault("temp", "0") : "0",
                        data != null ? data.getOrDefault("icon", "01d") : "01d"
                );
            }

            DetailsFragment detailsFragment = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                detailsFragment = DetailsFragment.newInstance(
                        data != null ? data.getOrDefault("wind_speed", "0") : "0",
                        data != null ? data.getOrDefault("wind_deg", "0") : "0",
                        data != null ? data.getOrDefault("humidity", "0") : "0",
                        data != null ? data.getOrDefault("visibility", "0") : "0",
                        data != null ? data.getOrDefault("pressure", "0") : "0",
                        data != null ? data.getOrDefault("feels_like", "0") : "0",
                        data != null ? data.getOrDefault("temp_min", "0") : "0",
                        data != null ? data.getOrDefault("temp_max", "0") : "0",
                        data != null ? data.getOrDefault("lat", "0") : "0",
                        data != null ? data.getOrDefault("lon", "0") : "0",
                        data != null ? data.getOrDefault("sunrise", "0") : "0",
                        data != null ? data.getOrDefault("sunset", "0") : "0"
                        );
            }
            ForecastFragment forecastFragment = ForecastFragment.newInstance(defaultCity);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_weather_basic, weatherFragment)
                    .replace(R.id.fragment_weather_details, detailsFragment)
                    .replace(R.id.fragment_weather_forecast, forecastFragment)
                    .commit();
        }
        else {

            cities = new ArrayList<>(FavoritesManager.loadFavorites(this));
            if (cities.isEmpty()) {
                cities.add("Lodz");
                FavoritesManager.saveFavorites(this, cities);
            }

            viewPager = findViewById(R.id.view_pager);
            tabLayout = findViewById(R.id.tab_layout);

            adapter = new CityPagerAdapter(this, cities);
            viewPager.setAdapter(adapter);

            viewPager.setPageTransformer(new DepthPageTransformer());

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> tab.setText(cities.get(position))
            ).attach();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    private void showUnitsDialog() {
        final String[] units = {"Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"};
        final String[] unitValues = {"metric", "imperial", "standard"};

        SharedPreferences prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE);
        String currentUnit = prefs.getString("temp_unit", "metric");

        int checkedItem = 0;
        for (int i = 0; i < unitValues.length; i++) {
            if (unitValues[i].equals(currentUnit)) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Select temperature unit")
                .setSingleChoiceItems(units, checkedItem, (dialog, which) -> {
                    // Сохраняем выбранную единицу
                    prefs.edit().putString("temp_unit", unitValues[which]).apply();

                    // Обновляем фрагменты и интерфейс
                    recreate(); // или вручную пересоздать адаптер + viewPager

                    dialog.dismiss(); // Закрыть диалог после выбора
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
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
        else if (item.getItemId() == R.id.menu_settings)
        {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(android.R.id.content, new SettingsFragment())
//                    .addToBackStack(null)
//                    .commit();
            showUnitsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRemoveCityDialog()
    {
        if (cities.size() == 1)
        {
            Toast.makeText(this, "You can't delete the last city", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] cityArray = cities.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a city for deletion");
        builder.setItems(cityArray, (dialog, which) -> {
            String cityToRemove = cities.get(which);
            int currentIndex = viewPager.getCurrentItem();

            FavoritesManager.removeCity(this, cityToRemove);
            cities.remove(cityToRemove);

            adapter = new CityPagerAdapter(this, cities);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> tab.setText(cities.get(position))
            ).attach();

            // Выбираем ближайшую вкладку
            if (currentIndex >= cities.size())
            {
                currentIndex = cities.size() - 1;
            }
            viewPager.setCurrentItem(currentIndex, false);

            Toast.makeText(this, "Deleted: " + cityToRemove, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showChangeCityDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a city");

        final EditText input = new EditText(this);
        input.setHint("For example: Warsaw");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newCity = input.getText().toString().trim();
            if (!newCity.isEmpty() && !cities.contains(newCity))
            {
                FavoritesManager.addCity(this, newCity);
                cities.add(newCity);

                adapter = new CityPagerAdapter(this, cities);
                viewPager.setAdapter(adapter);

                new TabLayoutMediator(tabLayout, viewPager,
                        (tab, position) -> tab.setText(cities.get(position))
                ).attach();

                viewPager.setCurrentItem(cities.size() - 1, false);

                viewPager.post(() -> {
                    viewPager.measure(
                            View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(viewPager.getHeight(), View.MeasureSpec.EXACTLY)
                    );
                    viewPager.requestLayout();
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
