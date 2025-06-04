package com.example.lab2.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.lab2.R;
import com.example.lab2.api.WeatherApi;
import com.example.lab2.utils.JSonParser;
import com.example.lab2.storage.WeatherCache;

import java.util.Map;

public class WeatherFragment extends Fragment {

    private TextView textCity, textCoordinates, textTemperature;
    private ImageView imageWeather;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String city, lat, lon, temp, iconCode;

    public WeatherFragment(String city, String lat, String lon, String temp, String iconCode) {
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.temp = temp;
        this.iconCode = iconCode;
    }

    public WeatherFragment() {} // Обязательный пустой конструктор

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather_basic, container, false);

        // Инициализация
        textCity = view.findViewById(R.id.text_city);
        textCoordinates = view.findViewById(R.id.text_coordinates);
        textTemperature = view.findViewById(R.id.text_temperature);
        imageWeather = view.findViewById(R.id.image_weather);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        // Первичная отрисовка
        updateUI();

        // Обработка свайпа вниз
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Thread(() -> {
                try {
                    String json = WeatherApi.fetchWeatherData(city);
                    WeatherCache.saveToCache(requireContext(), json);
                    Map<String, String> updatedData = JSonParser.parseWeather(json);

                    requireActivity().runOnUiThread(() -> {
                        // Обновляем поля
                        this.lat = updatedData.get("lat");
                        this.lon = updatedData.get("lon");
                        this.temp = updatedData.get("temp");
                        this.iconCode = updatedData.get("icon");
                        updateUI();
                        swipeRefreshLayout.setRefreshing(false);
                    });

                } catch (Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }).start();
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        textCity.setText("City: " + city);
        textCoordinates.setText("Coordinates: " + lat + ", " + lon);
        textTemperature.setText(temp + "°C");

        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
        Glide.with(requireContext())
                .load(iconUrl)
                .into(imageWeather);
    }
}
