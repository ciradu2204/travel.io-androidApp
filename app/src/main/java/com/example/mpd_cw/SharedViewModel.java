package com.example.mpd_cw;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//Cynthia Iradukunda - s1906581

public class SharedViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Event>> eventList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Event>> filteredDataList = new MutableLiveData<>();


    public void setEventList(ArrayList<Event> events){
         eventList.setValue(events);
    }

    public LiveData<ArrayList<Event>> getEventList(){
        return eventList;
    }

    public LiveData<ArrayList<Event>> getFilteredData() {
        return filteredDataList;
    }

    public void setFilteredData(ArrayList<Event> filteredData) {
        filteredDataList.setValue(filteredData);
    }

}
