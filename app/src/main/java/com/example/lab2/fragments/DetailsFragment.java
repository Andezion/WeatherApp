package com.example.lab2.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.lab2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import java.util.Locale;
import java.util.Map;

// Это для обработки раздела о детальной информации

public class DetailsFragment extends Fragment
{
    private final Map<String, String> data;


    public static DetailsFragment newInstance(
            String windSpeed,
            String windDeg,
            String humidity,
            String visibility,
            String pressure,
            String feelsLike,
            String tempMin,
            String tempMax,
            String lat,
            String lon,
            String sunrise,
            String sunset
    ) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("wind_speed", windSpeed);
        args.putString("wind_deg", windDeg);
        args.putString("humidity", humidity);
        args.putString("visibility", visibility);
        args.putString("pressure", pressure);
        args.putString("feels_like", feelsLike);
        args.putString("temp_min", tempMin);
        args.putString("temp_max", tempMax);
        args.putString("lat", lat);
        args.putString("lon", lon);
        args.putString("sunrise", sunrise);
        args.putString("sunset", sunset);
        fragment.setArguments(args);
        return fragment;
    }
    public DetailsFragment(Map<String, String> data)
    {
        this.data = data;
    }

    public DetailsFragment()
    {
        this.data = new HashMap<>();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        SharedPreferences prefs = requireContext().getSharedPreferences("weather_prefs", android.content.Context.MODE_PRIVATE);
        String units = prefs.getString("temp_unit", "metric");

        String tempUnit = "°C";
        String visibilityUnit = "m";
        String windUnit = "m/s";

        switch (units) {
            case "imperial":
                tempUnit = "°F";
                windUnit = "mph";
                visibilityUnit = "mi";
                break;
            case "standard":
                tempUnit = "K";
                windUnit = "m/s";
                visibilityUnit = "m";
                break;
        }

        View view = inflater.inflate(R.layout.fragment_weather_details, container, false);

        setText(view, R.id.text_wind, "Wind speed: " + data.get("wind_speed") + " " + windUnit);
        setText(view, R.id.text_wind_direction, "Wind direction: " + data.get("wind_deg") + "°");
        setText(view, R.id.text_humidity, "Humidity: " + data.get("humidity") + " %");
        setText(view, R.id.text_visibility, "Visibility: " + data.get("visibility") + " " + visibilityUnit);
        setText(view, R.id.text_pressure, "Pressure: " + data.get("pressure") + " hPa");
        setText(view, R.id.text_feels_like, "Feels like: " + data.get("feels_like") + tempUnit);
        setText(view, R.id.text_temp_range, "Min/Max temperature: " + data.get("temp_min") + tempUnit + " / " + data.get("temp_max") + tempUnit);

        setText(view, R.id.text_coordinates, "Coordinates: " + data.get("lat") + ", " + data.get("lon"));
        setText(view, R.id.text_sunrise, "Sunrise: " + convertUnixTime(data.get("sunrise")));
        setText(view, R.id.text_sunset, "Sunset: " + convertUnixTime(data.get("sunset")));

        return view;
    }

    private void setText(View view, int id, String value)
    {
        TextView tv = view.findViewById(id);
        if (tv != null)
        {
            tv.setText(value);
        }
    }

    private String convertUnixTime(String unixTime)
    {
        try
        {
            long time = Long.parseLong(unixTime) * 1000;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(new Date(time));
        }
        catch (Exception e)
        {
            return "-";
        }
    }
}