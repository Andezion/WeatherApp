package com.example.lab2.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lab2.R;

// Показывает наш главный экран

public class WeatherFragment extends Fragment
{
    private TextView textCity, textCoordinates, textTemperature;

    private String city, lat, lon, temp, iconCode;

    public WeatherFragment(String city, String lat, String lon, String temp, String iconCode)
    {
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.temp = temp;
        this.iconCode = iconCode;
    }

    public WeatherFragment() {}  // пустой конструктор обязателен

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_weather_basic, container, false);
        textCity = view.findViewById(R.id.text_city);
        textCoordinates = view.findViewById(R.id.text_coordinates);
        textTemperature = view.findViewById(R.id.text_temperature);

        textCity.setText("Город: " + city);
        textCoordinates.setText("Координаты: " + lat + ", " + lon);
        textTemperature.setText(temp + "°C");
        ImageView imageView = view.findViewById(R.id.image_weather);
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";

        Glide.with(requireContext())
                .load(iconUrl)
                .into(imageView);
        return view;
    }
}
