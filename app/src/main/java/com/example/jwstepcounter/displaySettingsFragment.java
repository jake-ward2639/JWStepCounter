package com.example.jwstepcounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class displaySettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Get the dark mode preference and register a listener to override
        SwitchPreference darkModePref = findPreference("dark_mode");
        darkModePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isDarkMode = (boolean) newValue;

                // Update the app-wide theme based on the value of the switch
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                return true;
            }
        });

        // Get the step goal preference and register a listener to override
        EditTextPreference stepGoalPref = findPreference("step_goal");
        stepGoalPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                String stepGoalString = (String) newValue;
                int stepGoal = Integer.parseInt(stepGoalString);

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                sharedPref.edit().putInt("step_goal", stepGoal).apply();

                return true;
            }
        });
    }
}
