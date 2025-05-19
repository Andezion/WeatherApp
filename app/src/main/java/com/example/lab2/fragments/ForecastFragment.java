package com.example.lab2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.R;
import com.example.lab2.adapters.ForecastAdapter;
import com.example.lab2.api.WeatherApi;
import com.example.lab2.utils.ForecastParser;
import com.example.lab2.model.ForecastItem;

import java.util.List;

public class ForecastFragment extends Fragment
{
    private RecyclerView recyclerView;

    private List<ForecastItem> forecastList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        recyclerView = view.findViewById(R.id.recycler_forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new Thread(() -> {
            try {
                String forecastJson = WeatherApi.fetchForecastData("Lodz"); // или lastSelectedCity
                List<ForecastItem> forecastList = ForecastParser.parseForecast(forecastJson);

                requireActivity().runOnUiThread(() -> {
                    ForecastAdapter adapter = new ForecastAdapter(forecastList);
                    recyclerView.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return view;
    }

    public static ForecastFragment newInstance(List<ForecastItem> list)
    {
        ForecastFragment fragment = new ForecastFragment();
        fragment.forecastList = list;
        return fragment;
    }
}
