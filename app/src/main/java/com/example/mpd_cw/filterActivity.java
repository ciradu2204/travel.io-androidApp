package com.example.mpd_cw;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//Cynthia Iradukunda - s1906581

public class filterActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private Spinner spinnerSort;
    private Spinner spinnerCategory;
    private Spinner spinnerRegion;
    private Spinner spinnerRoad;


    ArrayAdapter<String> adapterCategory;
    ArrayAdapter<String> adapterRegion;
    ArrayAdapter<String> adapterRoad;
    ArrayAdapter<CharSequence> staticAdapter;
    String category;
    String road;
    String region;
    String sort;
    int status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        //get the repository instance to access the fetched data
        Repository repository = Repository.getInstance();


        List<String> categoryList = new ArrayList<>(repository.getCategory());
        categoryList.add("All");
        List<String> roadList = new ArrayList<>(repository.getRoad());
        roadList.add("All");
        List<String> regionList = new ArrayList<>(repository.getRegion());
        regionList.add("All");

        //sort the list
        Collections.sort(categoryList);
        Collections.sort(roadList);
        Collections.sort(regionList);


        //initialize the views
        radioGroup = findViewById(R.id.categoryChoices);
        spinnerSort = findViewById(R.id.spinnerSort);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerRegion = findViewById(R.id.spinnerRegion);
        spinnerRoad = findViewById(R.id.spinnerRoad);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                RadioButton all = findViewById(R.id.All);

                if(i != R.id.All){
                    all.setTextColor(Color.BLACK);
                }

            }
        });

        //array adapters for sort
        staticAdapter = ArrayAdapter.createFromResource(this, R.array.sortArray, android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerSort.setAdapter(staticAdapter);

        //extract the different category


        //create adapter
        adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapterRegion = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regionList);
        adapterRoad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roadList);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRegion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRoad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //notify the adapter for changes
        adapterCategory.notifyDataSetChanged();
        adapterRegion.notifyDataSetChanged();
        adapterRoad.notifyDataSetChanged();

        //set adapter
        spinnerCategory.setAdapter(adapterCategory);
        spinnerRegion.setAdapter(adapterRegion);
        spinnerRoad.setAdapter(adapterRoad);

        //set default for spinner Road
        spinnerRoad.setSelection(adapterRoad.getPosition("All"));
        spinnerCategory.setSelection(adapterCategory.getPosition("All"));


        //check if saved instance is not null. If yes, update the activity
        if(savedInstanceState != null){
            spinnerCategory.setSelection(adapterCategory.getPosition(category));
            spinnerRoad.setSelection(adapterRoad.getPosition(road));
            spinnerRegion.setSelection(adapterRegion.getPosition(region));
            spinnerSort.setSelection(staticAdapter.getPosition(sort));

            radioGroup.check(status);
            //RadioButton b = findViewById(status);
           // b.setChecked(true);

        }


    }


    public void filterIncidents(View view) {
        //get user's selected items
        status = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(status);
        category = spinnerCategory.getSelectedItem().toString();
        road = spinnerRoad.getSelectedItem().toString();
        region = spinnerRegion.getSelectedItem().toString();
        sort = spinnerSort.getSelectedItem().toString();


        Intent i = new Intent(this, RoadEventsActivity.class);
        i.putExtra("category", category);
        i.putExtra("road", road);
        i.putExtra("region", region);
        i.putExtra("sort", sort);
        i.putExtra("status", radioButton.getText());

        startActivity(i);
    }

    //save activity state when activity is rotated
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString("category", category);
        outState.putString("road", road);
        outState.putString("region", region);
        outState.putString("sort", sort);
        super.onSaveInstanceState(outState);


    }

    //restore activity state
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        category = savedInstanceState.getString("category");
        road = savedInstanceState.getString("road");
        region = savedInstanceState.getString("region");
        sort = savedInstanceState.getString("sort");
        super.onRestoreInstanceState(savedInstanceState);


    }

    public void cancelFilter(View view) {
        finish();
    }
}