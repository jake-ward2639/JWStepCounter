package com.example.jwstepcounter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

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

    private CircularProgressBar stepCountProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Request the permission
            requestPermissionLauncher.launch(new String[]{Manifest.permission.ACTIVITY_RECOGNITION});
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
                    .putInt("currentStepCount", 0)
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

        // Initialize the Progressbar and set progress
        stepCountProgressBar = view.findViewById(R.id.circularProgressBar);
        int maxProgress = sharedPref.getInt("step_goal", 200);
        stepCountProgressBar.setProgressMax((float) maxProgress);
        stepCountProgressBar.setProgressWithAnimation((float) currentStepCount, 1000L);

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
                stepCountProgressBar.setProgressWithAnimation((float) currentDayStepCount, 1000L);
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

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean isAllGranted = true;
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    if (!entry.getValue()) {
                        isAllGranted = false;
                        break;
                    }
                }

                if (!isAllGranted) {
                    Snackbar.make(requireView(), "Permissions are required to use this app", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Settings", view -> {
                                // Open app settings
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                                startActivity(intent);
                            })
                            .show();
                }
            }
    );

}
