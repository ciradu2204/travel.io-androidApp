package com.example.mpd_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListFragment extends Fragment {

    private SharedViewModel viewModel;
    private ArrayList<Event> listEvents;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //initialize the view
        View v = inflater.inflate(R.layout.incident_fragmentlist, container, false);

        //define the list view and arraylist to store events

        ListView list = v.findViewById(R.id.listView);
        listEvents = new ArrayList<>();
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), listEvents);
        list.setAdapter(customAdapter);

        //when an item is clicked
        list.setClickable(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent itemIntent = new Intent(getActivity(), ListItemActivity.class);

                addIntentData(listEvents.get(i), itemIntent);


                startActivity(itemIntent);


            }
        });


        //listen for search changes
        SearchView searchView = getActivity().findViewById(R.id.searchItems);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                customAdapter.getFilter().filter(s);
                return true;
            }
        });




         //create view model instance
         viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

            viewModel.getEventList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
             @Override
             public void onChanged(ArrayList<Event> events) {
                 listEvents = events;
                 customAdapter.updateItems(events);

             }
         });

         //observe for any changes of filtered data
         viewModel.getFilteredData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
             @Override
             public void onChanged(ArrayList<Event> events) {
                 listEvents = events;
                 customAdapter.updateItems(events);
             }
         });

        return v;

    }

    public static void addIntentData(Event event, Intent itemIntent){

        itemIntent.putExtra("category", event.getCategory());
        itemIntent.putExtra("status", event.getStatus());
        itemIntent.putExtra("description", event.getDescription());
        itemIntent.putExtra("title", event.getTitle());

        String location = event.getCountry() + ", " + event.getRegion();
        Date dateStart = new Date();
        Date dateEnd = new Date();

        try {
            dateStart =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.UK).parse(event.getEventStart());
            dateEnd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.UK).parse(event.getEventEnd());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //format Date
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.UK);
        String startDate = dateFormat.format(dateStart);
        String endDate = dateFormat.format(dateEnd);

        String date = startDate + " - " + endDate;


        //format time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa", Locale.UK);
        String startTime = sdf.format(dateStart);
        String endTime = sdf.format(dateEnd);
        String time = startTime + " - " + endTime;


        itemIntent.putExtra("location", location);
        itemIntent.putExtra("date", date);
        itemIntent.putExtra("time", time);

    }



}
