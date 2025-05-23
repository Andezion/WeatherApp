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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import java.util.Locale;
import java.util.Map;

// Это для обработки раздела о детальной информации

public class DetailsFragment extends Fragment
{
    private final Map<String, String> data;

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
        View view = inflater.inflate(R.layout.fragment_weather_details, container, false);

        setText(view, R.id.text_wind, "Скорость ветра: " + data.get("wind_speed") + " м/с");
        setText(view, R.id.text_wind_direction, "Направление ветра: " + data.get("wind_deg") + "°");
        setText(view, R.id.text_humidity, "Влажность: " + data.get("humidity") + " %");
        setText(view, R.id.text_visibility, "Видимость: " + data.get("visibility") + " м");
        setText(view, R.id.text_pressure, "Давление: " + data.get("pressure") + " гПа");
        setText(view, R.id.text_feels_like, "Ощущается как: " + data.get("feels_like") + "°C");
        setText(view, R.id.text_temp_range, "Мин/Макс температура: " + data.get("temp_min") + "°C / " + data.get("temp_max") + "°C");
        setText(view, R.id.text_coordinates, "Координаты: " + data.get("lat") + ", " + data.get("lon"));
        setText(view, R.id.text_sunrise, "Восход: " + convertUnixTime(data.get("sunrise")));
        setText(view, R.id.text_sunset, "Закат: " + convertUnixTime(data.get("sunset")));

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