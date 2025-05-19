package com.example.lab2.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class CityContainerFragment extends Fragment {

    private String city;

    private Map<String, String> weatherData;
    private List<ForecastItem> forecastList;

    public static CityContainerFragment newInstance(String city) {
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
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_city_container, container, false);

        if (getArguments() != null) {
            city = getArguments().getString("city");
        }

        BottomNavigationView nav = view.findViewById(R.id.bottom_navigation);

        // Загрузка данных
        new Thread(() -> {
            try {
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
                            weatherData.get("description")
                    );

                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.inner_fragment_container, firstFragment)
                            .commit();

                    nav.setOnItemSelectedListener(item -> {
                        Fragment selected = null;

                        int id = item.getItemId();
                        if (id == R.id.nav_basic) {
                            selected = new WeatherFragment(
                                    weatherData.get("city"),
                                    weatherData.get("lat"),
                                    weatherData.get("lon"),
                                    weatherData.get("temp"),
                                    weatherData.get("description")
                            );
                        } else if (id == R.id.nav_details) {
                            selected = new DetailsFragment(
                                    weatherData.get("wind_speed"),
                                    weatherData.get("humidity"),
                                    weatherData.containsKey("visibility") ? weatherData.get("visibility") : "10000"
                            );
                        } else if (id == R.id.nav_forecast) {
                            selected = ForecastFragment.newInstance(forecastList);
                        }

                        if (selected != null) {
                            getChildFragmentManager().beginTransaction()
                                    .replace(R.id.inner_fragment_container, selected)
                                    .commit();
                            return true;
                        }
                        return false;
                    });
                });

            } catch (Exception e) {
                Log.e("CityFragment", "Ошибка загрузки погоды", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Ошибка загрузки погоды", Toast.LENGTH_SHORT).show());
            }
        }).start();

        return view;
    }
}
