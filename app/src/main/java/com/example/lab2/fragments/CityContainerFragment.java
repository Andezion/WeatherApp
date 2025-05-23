package com.example.lab2.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lab2.R;
import com.example.lab2.api.WeatherApi;
import com.example.lab2.model.ForecastItem;
import com.example.lab2.storage.WeatherCache;
import com.example.lab2.utils.ForecastParser;
import com.example.lab2.utils.JSonParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Map;

// Отвечает за переключение между экранами

public class CityContainerFragment extends Fragment
{
    private String city;
    private Map<String, String> weatherData;
    private List<ForecastItem> forecastList;

    public static CityContainerFragment newInstance(String city)
    {
        CityContainerFragment fragment = new CityContainerFragment();
        Bundle args = new Bundle();
        args.putString("city", city);
        fragment.setArguments(args);
        return fragment;
    }

    public CityContainerFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_city_container, container, false);

        if (getArguments() != null)
        {
            city = getArguments().getString("city");
        }

        BottomNavigationView nav = view.findViewById(R.id.bottom_navigation);

//        ViewCompat.setOnApplyWindowInsetsListener(nav, (v, insets) -> {
//            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
//            v.setPadding(0, 0, 0, bottomInset);
//            return insets;
//        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(() ->
        {
            int[] location = new int[2];
            nav.getLocationOnScreen(location);

            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            int navBottom = location[1] + nav.getHeight();

            int overlap = navBottom - screenHeight;
            if (overlap > 0)
            {
                nav.setPadding(0, 0, 0, overlap + 16);
            }
        });

        new Thread(() ->
        {
            try
            {
                String json = WeatherApi.fetchWeatherData(city);
                String forecastJson = WeatherApi.fetchForecastData(city);

                WeatherCache.saveToCache(requireContext(), json);
                weatherData = JSonParser.parseWeather(json);
                forecastList = ForecastParser.parseForecast(forecastJson);

                requireActivity().runOnUiThread(() -> {
                    Fragment firstFragment = new WeatherFragment(
                            weatherData.get("city"),
                            weatherData.get("lat"),
                            weatherData.get("lon"),
                            weatherData.get("temp"),
                            weatherData.get("icon")
                    );

                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.inner_fragment_container, firstFragment)
                            .commit();

                    nav.setOnItemSelectedListener(item -> {
                        Fragment selected = null;

                        int id = item.getItemId();
                        if (id == R.id.nav_basic)
                        {
                            selected = new WeatherFragment(
                                    weatherData.get("city"),
                                    weatherData.get("lat"),
                                    weatherData.get("lon"),
                                    weatherData.get("temp"),
                                    weatherData.get("description")
                            );
                        }
                        else if (id == R.id.nav_details)
                        {
                            selected = new DetailsFragment(weatherData);
                        }
                        else if (id == R.id.nav_forecast)
                        {
                            selected = ForecastFragment.newInstance(city);
                        }

                        if (selected != null)
                        {
                            getChildFragmentManager().beginTransaction()
                                    .replace(R.id.inner_fragment_container, selected)
                                    .commit();
                            return true;
                        }
                        return false;
                    });
                });

            }
            catch (Exception e)
            {
                Log.e("CityFragment", "Ошибка загрузки погоды", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Ошибка загрузки погоды", Toast.LENGTH_SHORT).show());
            }
        }).start();

        return view;
    }
}
