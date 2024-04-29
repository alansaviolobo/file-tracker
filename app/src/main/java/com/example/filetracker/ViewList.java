package com.example.filetracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewList extends AppCompatActivity {

    // creating variables for our array list,
    // dbhandler, adapter and recycler view.
    private ArrayList<DataModal> courseModalArrayList;
    private DBHandler dbHandler;
    private Adapter courseRVAdapter;
    private RecyclerView coursesRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        // initializing our all variables.
        courseModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(ViewList.this);

        // getting our course array
        // list from db handler class.
        //courseModalArrayList = dbHandler.readCourses();

        // on below line passing our array list to our adapter class.
        courseRVAdapter = new Adapter(courseModalArrayList, ViewList.this);
        coursesRV = findViewById(R.id.ViewData);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewList.this, RecyclerView.VERTICAL, false);
        coursesRV.setLayoutManager(linearLayoutManager);

        // setting our adapter to recycler view.
        coursesRV.setAdapter(courseRVAdapter);
    }
}
