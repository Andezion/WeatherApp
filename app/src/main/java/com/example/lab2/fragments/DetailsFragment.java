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

public class DetailsFragment extends Fragment
{
    private String wind, humidity, visibility;

    public DetailsFragment(String wind, String humidity, String visibility)
    {
        this.wind = wind;
        this.humidity = humidity;
        this.visibility = visibility;
    }

    public DetailsFragment() {}

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_weather_details, container, false);

        ((TextView) view.findViewById(R.id.text_wind)).setText("Ветер: " + wind + " м/с");
        ((TextView) view.findViewById(R.id.text_humidity)).setText("Влажность: " + humidity + "%");
        ((TextView) view.findViewById(R.id.text_visibility)).setText("Видимость: " + visibility + " м");

        return view;
    }
}
