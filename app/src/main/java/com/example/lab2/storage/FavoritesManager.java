package com.example.lab2.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesManager
{
    private static final String PREF_NAME = "weather_prefs";
    private static final String KEY_CITIES = "favorite_cities";

    public static void saveFavorites(Context context, List<String> cities)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(cities);
        prefs.edit().putStringSet(KEY_CITIES, set).apply();
    }

    public static List<String> loadFavorites(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        //Set<String> set = prefs.getStringSet(KEY_CITIES, null); -- проверить залупу
        Set<String> set = new HashSet<>(prefs.getStringSet(KEY_CITIES, new HashSet<>()));
        if (set.isEmpty())
        {
            List<String> defaultList = new ArrayList<>();
            defaultList.add("Lodz");
            return defaultList;
        }
        return new ArrayList<>(set);
    }

    public static void addCity(Context context, String city)
    {
        List<String> cities = loadFavorites(context);
        if (!cities.contains(city))
        {
            cities.add(city);
            saveFavorites(context, cities);
        }
    }

    public static void removeCity(Context context, String city)
    {
        //List<String> cities = loadFavorites(context);
        List<String> cities = new ArrayList<>(loadFavorites(context));
        cities.remove(city);
        saveFavorites(context, cities);
    }
}
