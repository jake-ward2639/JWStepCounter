package com.example.jwstepcounter;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private TextView stepCountTextView;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private SharedPreferences sharedPref;
    private int currentStepCount;
    private int oldStepCount;
    private String currentDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    400);
        }

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        currentStepCount = sharedPref.getInt("currentStepCount", 0);
        oldStepCount = sharedPref.getInt("oldStepCount", 0);
        currentDate = sharedPref.getString("currentDate", "");

        // Check if the date is different from the old one
        String todayDate = getTodayDate();
        if (!todayDate.equals(currentDate)) {
            // Save the old step count and update the current date
            sharedPref.edit()
                    .putInt("oldStepCount", oldStepCount + currentStepCount)
                    .putString("currentDate", todayDate)
                    .apply();
            oldStepCount = oldStepCount + currentStepCount;
            currentStepCount = 0;
        }

        // Initialize the step counter sensor
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Initialize the TextView that will display the step count
        stepCountTextView = view.findViewById(R.id.stepCountTextView);
        stepCountTextView.setText(String.format("Step count: %d", currentStepCount));

        return view;
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                // Reset the old step count if the sensor results are less than it
                if (event.values[0] < oldStepCount) {
                    oldStepCount = 0;
                    sharedPref.edit().putInt("oldStepCount", oldStepCount).apply();
                }

                // Calculate the current day's step count
                int currentDayStepCount = (int) event.values[0] - oldStepCount;

                // Save the step count and update the TextView
                sharedPref.edit().putInt("currentStepCount", currentDayStepCount).apply();
                stepCountTextView.setText(String.format("Step count: %d", currentDayStepCount));
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorEventListener);
    }

}
