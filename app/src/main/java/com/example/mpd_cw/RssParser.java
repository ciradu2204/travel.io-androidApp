package com.example.mpd_cw;


import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
//Cynthia Iradukunda - s1906581

public class RssParser {

    private final ArrayList<Event> events = new ArrayList<>();
    private final HashSet<String> categories = new HashSet<>();
    private final HashSet<String> road = new HashSet<>();
    private final HashSet<String> region = new HashSet<>();
    private Event event;
    private String text;

    //get all the events parsed
    public ArrayList<Event> getIncidents(){
        return events;
    }

    //get the categories for filter
    public HashSet<String> getCategories(){return categories;}

    //get the road for filter
    public HashSet<String > getRoad(){return road;}

    //get the region for filter
    public HashSet<String> getRegion(){return region;}
    //parse the url
    public void parse(String is){

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(is));
            int eventType = parser.getEventType();

            for(int i=0; i<47; i++){
                eventType = parser.next();
            }
            while (eventType != XmlPullParser.END_DOCUMENT){
                String tagName = parser.getName();

                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(tagName.equalsIgnoreCase("item")){
                            event = new Event();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("item")) {

                            //check the event status

                            String status = calculateStatus();
                            event.setStatus(status);
                            // add event object to list
                            events.add(event);
                        }else if (tagName.equalsIgnoreCase("author")) {
                            event.setAuthor(text);
                        }  else if (tagName.equalsIgnoreCase("category")) {

                            if(event.getCategory() == null){
                                event.setCategory(text);
                                categories.add(text);
                            }
                        } else if (tagName.equalsIgnoreCase("description")) {
                            event.setDescription(text);
                        }else if(tagName.equalsIgnoreCase("link")) {
                            event.setUrl(text);
                        }else if(tagName.equalsIgnoreCase("pubDate")) {
                            event.setPubDate(text);
                        }else if(tagName.equalsIgnoreCase("title")) {
                            event.setTitle(text);
                        }else if(tagName.equalsIgnoreCase("road")){
                            event.setRoad(text);
                            road.add(text);
                        }else if(tagName.equalsIgnoreCase("region")) {
                            event.setRegion(text);
                            region.add(text);
                        }else if(tagName.equalsIgnoreCase("county")) {
                            event.setCountry(text);
                        }else if(tagName.equalsIgnoreCase("latitude")) {
                            event.setLatitude(text);
                        }else if(tagName.equalsIgnoreCase("longitude")) {
                            event.setLongitude(text);
                        }else if(tagName.equalsIgnoreCase("overallStart")) {
                            event.setEventStart(text);
                        }else if(tagName.equalsIgnoreCase("overallEnd")) {
                            event.setEventEnd(text);
                        }
                        break;

                    default:
                        break;
                }

                eventType = parser.next();

            }

            } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

         }

       public String calculateStatus()  {
        String status = "";
           try {

               Date dateStart =  new SimpleDateFormat("yyyy-MM-dd", Locale.UK).parse(event.getEventStart());
               Date  dateEnd = new SimpleDateFormat("yyyy-MM-dd", Locale.UK).parse(event.getEventEnd());
               assert dateStart != null;
               if(dateStart.before(new Date())){

                   assert dateEnd != null;
                   if(dateEnd.before(new Date())){

                       status = "Past";
                   }else{
                       status = "Ongoing";
                   }

               }else{

                   status = "Upcoming";

               }
           } catch (ParseException e) {
               e.printStackTrace();
           }

           return status;
       }
    }

