package com.example.lab2.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.lab2.fragments.CityContainerFragment;

import java.util.List;


public class CityPagerAdapter extends FragmentStateAdapter
{
    private final List<String> cities;

    public CityPagerAdapter(@NonNull FragmentActivity activity, List<String> cities)
    {
        super(activity);
        this.cities = cities;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return CityContainerFragment.newInstance(cities.get(position));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }
}