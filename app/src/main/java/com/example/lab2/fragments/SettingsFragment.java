package com.example.lab2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.example.lab2.R;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "temp_unit";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group_units);
        RadioButton radioC = view.findViewById(R.id.radio_celsius);
        RadioButton radioF = view.findViewById(R.id.radio_fahrenheit);

        String unit = getPreferences().getString(UNIT_KEY, "metric");

        if (unit.equals("metric")) radioC.setChecked(true);
        else radioF.setChecked(true);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = getPreferences().edit();
            if (checkedId == R.id.radio_celsius)
                editor.putString(UNIT_KEY, "metric");
            else
                editor.putString(UNIT_KEY, "imperial");
            editor.apply();
        });

        return view;
    }

    private SharedPreferences getPreferences() {
        return requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
