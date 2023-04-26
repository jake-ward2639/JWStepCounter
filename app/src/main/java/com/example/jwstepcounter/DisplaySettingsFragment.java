package com.example.jwstepcounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class DisplaySettingsFragment extends PreferenceFragmentCompat {

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

        // Get the EditTextPreference and set the input type
        androidx.preference.EditTextPreference editTextPreference = findPreference("step_goal");
        editTextPreference.setOnBindEditTextListener(new androidx.preference.EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            }
        });
    }
}
