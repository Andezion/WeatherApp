package com.example.lab2.api;

import com.example.lab2.secret.APIKey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApi
{
    private static final String API_KEY = APIKey.WEATHER_API_KEY;
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";

    public static String fetchWeatherData(String city) throws Exception
    {
        String urlString = String.format(BASE_URL, city, API_KEY);
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200)
        {
            throw new RuntimeException("HTTP error: " + conn.getResponseCode());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
        {
            json.append(line);
        }

        reader.close();
        conn.disconnect();

        return json.toString();
    }
}
