package com.example.mpd_cw.repositories;


import android.app.Activity;


import com.example.mpd_cw.objects.Event;
import com.example.mpd_cw.parsers.RssParser;
import com.example.mpd_cw.readers.RssReader;
import com.example.mpd_cw.viewModels.SharedViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//Cynthia Iradukunda - s1906581

public class Repository {

    private ArrayList<Event> eventList;
    private HashSet<String> category;
    private HashSet<String> road;
    private HashSet<String> region;

    private ExecutorService executorService;

    //ensure that only one instance is created at the moment
    private static Repository sSoleInstance;


    private Repository(){
        executorService = Executors.newSingleThreadExecutor();
    }

    public synchronized static Repository getInstance(){
        if(sSoleInstance == null){
            sSoleInstance = new Repository();
        }

        return sSoleInstance;
    }


    public void processTask(SharedViewModel viewModel, Activity activity){

         //execute the parsing in a new thread
          executorService.execute(new Runnable() {
            @Override
            public void run() {
                RssReader rssReader  = new RssReader("https://m.highwaysengland.co.uk/feeds/rss/AllEvents.xml");
                String data = rssReader.ReadData();
                RssParser rssParser = new RssParser();
                rssParser.parse(data);
                setEventList(rssParser.getIncidents());
                setCategory(rssParser.getCategories());
                setRoad(rssParser.getRoad());
                setRegion(rssParser.getRegion());

               activity.runOnUiThread(new Runnable(){
                   @Override
                   public void run(){
                       viewModel.setEventList(getFullList());
                    }
               });
            }
        });

        executorService.shutdown();
    }



    public HashSet<String> getCategory() {
        return category;
    }

    public void setCategory(HashSet<String> category) {
        this.category = category;
    }

    public HashSet<String> getRoad() {
        return road;
    }

    public void setRoad(HashSet<String> road) {
        this.road = road;
    }

    public HashSet<String> getRegion() {
        return region;
    }

    public void setRegion(HashSet<String> region) {
        this.region = region;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public ArrayList<Event> getFullList(){
        return  eventList;
    }

}
