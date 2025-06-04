package com.example.lab2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.lab2.adapters.CityPagerAdapter;
import com.example.lab2.storage.FavoritesManager;
import com.example.lab2.utils.DepthPageTransformer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends AppCompatActivity
{
    private ViewPager2 viewPager;
    private CityPagerAdapter adapter;
    private List<String> cities;
    private TabLayout tabLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cities = new ArrayList<>(FavoritesManager.loadFavorites(this));
        if (cities.isEmpty())
        {
            cities.add("Lodz");
            FavoritesManager.saveFavorites(this, cities);
        }

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        adapter = new CityPagerAdapter(this, cities);
        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer(new DepthPageTransformer());

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(cities.get(position))
        ).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.menu_change_city)
        {
            showChangeCityDialog();
            return true;
        }
        else if (item.getItemId() == R.id.menu_remove_city)
        {
            showRemoveCityDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRemoveCityDialog()
    {
        if (cities.size() == 1)
        {
            Toast.makeText(this, "You can't delete the last city", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] cityArray = cities.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a city for deletion");
        builder.setItems(cityArray, (dialog, which) -> {
            String cityToRemove = cities.get(which);
            int currentIndex = viewPager.getCurrentItem();

            FavoritesManager.removeCity(this, cityToRemove);
            cities.remove(cityToRemove);

            adapter = new CityPagerAdapter(this, cities);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> tab.setText(cities.get(position))
            ).attach();

            // Выбираем ближайшую вкладку
            if (currentIndex >= cities.size())
            {
                currentIndex = cities.size() - 1;
            }
            viewPager.setCurrentItem(currentIndex, false);

            Toast.makeText(this, "Deleted: " + cityToRemove, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showChangeCityDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a city");

        final EditText input = new EditText(this);
        input.setHint("For example: Warsaw");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newCity = input.getText().toString().trim();
            if (!newCity.isEmpty() && !cities.contains(newCity))
            {
                FavoritesManager.addCity(this, newCity);
                cities.add(newCity);

                adapter = new CityPagerAdapter(this, cities);
                viewPager.setAdapter(adapter);

                new TabLayoutMediator(tabLayout, viewPager,
                        (tab, position) -> tab.setText(cities.get(position))
                ).attach();

                viewPager.setCurrentItem(cities.size() - 1, false);

                viewPager.post(() -> {
                    viewPager.measure(
                            View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(viewPager.getHeight(), View.MeasureSpec.EXACTLY)
                    );
                    viewPager.requestLayout();
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
