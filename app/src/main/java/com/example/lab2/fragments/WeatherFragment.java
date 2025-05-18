package com.example.lab2.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.lab2.R;

public class WeatherFragment extends Fragment
{
    private TextView textCity, textCoordinates, textTemperature, textDescription;

    private String city, lat, lon, temp, description;

    public WeatherFragment(String city, String lat, String lon, String temp, String description)
    {
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.temp = temp;
        this.description = description;
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
        textDescription = view.findViewById(R.id.text_description);

        textCity.setText("Город: " + city);
        textCoordinates.setText("Координаты: " + lat + ", " + lon);
        textTemperature.setText(temp + "°C");
        textDescription.setText("Погода: " + description);

        return view;
    }
}
