package com.example.mpd_cw.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mpd_cw.objects.Event;
import com.example.mpd_cw.fragments.ListFragment;
import com.example.mpd_cw.fragments.MapCustomFragment;
import com.example.mpd_cw.R;
import com.example.mpd_cw.repositories.Repository;
import com.example.mpd_cw.viewModels.SharedViewModel;

import java.util.ArrayList;
import java.util.Collections;

//Cynthia Iradukunda - s1906581

public class RoadEventsActivity extends AppCompatActivity {
     private ListFragment listFragment;
     private MapCustomFragment mapFragment;
      private LinearLayout progressBarLayout;
      private SharedViewModel viewModel;
      private RadioGroup radioGroup;
      private RadioButton list;
      private RadioButton map;
      private ImageView filter;
      private ImageView home;
       private ArrayList<Event> events;
      private ActivityResultLauncher<Intent> someActivityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incidents);

       //initialize the fragments and views
        listFragment = new ListFragment();
        mapFragment = new MapCustomFragment();
        progressBarLayout = findViewById(R.id.progress_circularlayout);
        radioGroup = findViewById(R.id.toggle);

        list = findViewById(R.id.list);
        map = findViewById(R.id.map);
        filter = findViewById(R.id.filter);
        home = findViewById(R.id.homeIcon);

        //while it is still loading data
         filter.setEnabled(false);
         home.setEnabled(false);
         list.setEnabled(false);
         map.setEnabled(false);

         //get drawables for the map and list
        Drawable leftMapDrawable = AppCompatResources.getDrawable(this, R.drawable.map_foreground);
        Drawable leftListDrawable = AppCompatResources.getDrawable(this, R.drawable.list_foreground);
        int black = ContextCompat.getColor(getApplicationContext(), R.color.black);
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        //by default the map is not selected
        assert leftMapDrawable != null;

        //get information from the filter activity
        Bundle extras = getIntent().getExtras();


        //inialise the viewmodel and observe for changes
         viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Repository repository = Repository.getInstance();


        if(extras !=null){
            progressBarLayout.setVisibility(View.VISIBLE);
            viewModel.setEventList(repository.getFullList());
            startFilterProcess(getIntent());

        }else if(repository.getFullList() != null){
            viewModel.setEventList(repository.getFullList());
        }else{

            repository.processTask(viewModel, RoadEventsActivity.this);
        }

        setCompoundDrawable(leftMapDrawable, black, map);

        //change the text color of radio button and drawable based on it is checked or not
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                 if(i != R.id.list){
                    list.setTextColor(Color.BLACK);
                     assert leftListDrawable != null;
                     setCompoundDrawable(leftListDrawable, black, list);

                }else{
                    list.setTextColor(Color.WHITE);
                     assert leftListDrawable != null;
                     setCompoundDrawable(leftListDrawable, white, list);
                     getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, listFragment).commit();



                 }

                if(i != R.id.map){
                    map.setTextColor(Color.BLACK);
                    assert leftMapDrawable != null;
                    setCompoundDrawable(leftMapDrawable, black, map);


                }else{
                    map.setTextColor(Color.WHITE);
                    assert leftMapDrawable != null;
                    setCompoundDrawable(leftMapDrawable, white, map);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mapFragment).commit();



                }
            }
        });


        viewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
                filter.setEnabled(true);
                home.setEnabled(true);
                list.setEnabled(true);
                map.setEnabled(true);
                progressBarLayout.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, listFragment).commit();
            }
        });

        //observe filtered data
        viewModel.getFilteredData().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
                progressBarLayout.setVisibility(View.GONE);
            }
        });


    }

    //a function to be called when the home icon is displayed
    public void gotoHome(View view) {

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    //a function to be called when a filter icon is clicked
    public void filter(View view) {

        Intent i = new Intent(this, filterActivity.class);
        startActivity(i);

    }

    public void startFilterProcess(Intent intent){

        //get Intent extra
        String category = intent.getStringExtra("category");
        String region = intent.getStringExtra("region");
        String road = intent.getStringExtra("road");
        String sort = intent.getStringExtra("sort");
        String status = intent.getStringExtra("status");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Event> filteredData = filterData(viewModel.getEventList().getValue(), category, region, road, sort, status );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(filteredData.size() != 0){
                            viewModel.setFilteredData(filteredData);

                        }else {
                            viewModel.setFilteredData(filteredData);
                            noInfoDialog();
                        }
                    }
                });
            }
        }).start();

        progressBarLayout.setVisibility(View.GONE);

    }

    //a function to filter data
    private ArrayList<Event> filterData(ArrayList<Event> data, String category,  String region, String road, String sort, String status){

        ArrayList<Event> filteredData = new ArrayList<>();

        if(category.equals("All") && region.equals("All") && road.equals("All") && status.equals("All")){
            filteredData = data;
        }else {
            for(Event event: data){
                String eventCategory = event.getCategory() ;
                boolean isAllCategory = category.equals("All");
                String eventRoad = event.getRoad();
                boolean isAllRoad = road.equals("All");
                String eventRegion = event.getRegion();
                boolean isAllRegion = region.equals("All");
                String eventStatus = event.getStatus();
                boolean isAllStatus = status.equals("All");
                boolean isValid = false;


                if(isAllCategory || eventCategory.equals(category)){

                    isValid = true;
                 }else{
                    isValid = false;
                }

                if(isValid && (isAllRoad || eventRoad.equals(road))){
                    isValid = true;
                }else{
                    isValid = false;
                }

                if(isValid && (isAllRegion || eventRegion.equals(region))){
                    isValid = true;
                }else{
                    isValid = false;
                }

                if(isValid && (isAllStatus || eventStatus.equals(status))){
                    isValid = true;
                }else{
                    isValid = false;
                }


                if(isValid){
                    filteredData.add(event);
                }


            }
        }


        if(sort.equals("Newest to Oldest")){

            Collections.sort(filteredData);
            Collections.reverse(filteredData);

        }else{

            Collections.sort(filteredData);
        }

        return filteredData;

    }

    private void setCompoundDrawable(Drawable drawable, int color, RadioButton view){
       drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
    }

    //a dialog to be displayed when filter does not find any information
    private void  noInfoDialog(){
        new AlertDialog.Builder(RoadEventsActivity.this)
                .setTitle("No events found")
                .setMessage("Do you want to filter again?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filter(filter);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModel.setFilteredData(viewModel.getEventList().getValue());
            }
        }).show();
    }


}
