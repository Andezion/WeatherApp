package com.example.lab2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.R;
import com.example.lab2.adapters.ForecastAdapter;
import com.example.lab2.api.WeatherApi;
import com.example.lab2.storage.WeatherCache;
import com.example.lab2.utils.ForecastParser;
import com.example.lab2.model.ForecastItem;

import java.util.List;

// Этот отвечат за показ погоды на 5 дней и даже оффлайн

public class ForecastFragment extends Fragment
{
    private RecyclerView recyclerView;
    private String city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        recyclerView = view.findViewById(R.id.recycler_forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new Thread(() -> {
            try
            {
                if (getArguments() != null)
                {
                    city = getArguments().getString("city", "Lodz"); // запасной город
                }
                String forecastJson = WeatherApi.fetchForecastData(city);
                List<ForecastItem> forecastList = ForecastParser.parseForecast(forecastJson);

                requireActivity().runOnUiThread(() -> {
                    ForecastAdapter adapter = new ForecastAdapter(forecastList);
                    recyclerView.setAdapter(adapter);
                });

            }
            catch (Exception e)
            {
                e.printStackTrace();
                String cached = WeatherCache.readForecastFromCache(requireContext(), city);
                if (cached != null)
                {
                    List<ForecastItem> forecastList = ForecastParser.parseForecast(cached);
                    requireActivity().runOnUiThread(() ->
                    {
                        ForecastAdapter adapter = new ForecastAdapter(forecastList);
                        recyclerView.setAdapter(adapter);
                        Toast.makeText(requireContext(), "Прогноз из кеша", Toast.LENGTH_SHORT).show();
                    });
                }
                else
                {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Нет данных прогноза", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        }).start();

        return view;
    }

    public static ForecastFragment newInstance(String city)
    {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putString("city", city);
        fragment.setArguments(args);
        return fragment;
    }
}
