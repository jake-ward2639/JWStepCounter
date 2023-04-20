package com.example.jwstepcounter;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HistoryFragment extends Fragment {
    private SharedPreferences sharedPref;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Button saveDataButton;
    private Button clearHistoryButton;
    private int currentStepCount;
    private String currentDate;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        saveDataButton = view.findViewById(R.id.LogData);
        clearHistoryButton = view.findViewById(R.id.ClearHistory);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        //TODO display database contents

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentStepCount = sharedPref.getInt("currentStepCount", 0);
                currentDate = sharedPref.getString("currentDate", "");

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_DATE, currentDate);
                values.put(DatabaseHelper.COLUMN_STEP_COUNT, currentStepCount);

                db.insert(DatabaseHelper.TABLE_NAME, null, values);
                //TODO display database contents
            }
        });

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.delete(DatabaseHelper.TABLE_NAME, null, null);
                //TODO display database contents
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

}