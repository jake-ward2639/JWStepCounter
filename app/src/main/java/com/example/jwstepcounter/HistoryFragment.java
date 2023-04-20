package com.example.jwstepcounter;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

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
        //initialize the database helper
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
        displayDatabaseContents(view);

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add new contents to the table
                currentStepCount = sharedPref.getInt("currentStepCount", 0);
                currentDate = sharedPref.getString("currentDate", "");

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_DATE, currentDate);
                values.put(DatabaseHelper.COLUMN_STEP_COUNT, currentStepCount);

                db.insert(DatabaseHelper.TABLE_NAME, null, values);
                displayDatabaseContents(view);
            }
        });

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            //Clear contents of the table
            @Override
            public void onClick(View v) {

                db.delete(DatabaseHelper.TABLE_NAME, null, null);
                displayDatabaseContents(view);
            }
        });

    }

    private void displayDatabaseContents(View view) {
        // Retrieve data from the database
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<String> dataList = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                int stepCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEP_COUNT));
                dataList.add(0, date + " - " + stepCount);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Display data in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        ListView listView = view.findViewById(R.id.historyListView);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

}