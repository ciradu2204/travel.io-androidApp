package com.example.mpd_cw;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//Event object to get all the events
public class Event implements Comparable<Event>, ClusterItem {

    private String author;
    private String description;
    private String category;
    private String url;
    private String pubDate;
    private String title;
    private String road;
    private String region;
    private String country;
    private String latitude;
    private String longitude;
    private String eventStart;
    private String eventEnd;
    private String status;

    public Event() {
    }

    public Event(String title, String category, String road, String country, String region, String eventStart, String eventEnd, String status){
        this.title = title;
        this.category = category;
        this.road = road;
        this.country = country;
        this.region = region;
        this.eventEnd = eventEnd;
        this.eventStart = eventStart;
        this.status = status;
    }



    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public LatLng getPosition() {
        Double latitude = Double.valueOf(getLatitude());
        Double longitude = Double.valueOf(getLongitude());
        return new LatLng(latitude, longitude);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateTime(){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-dd-MM", Locale.UK).parse(getEventStart());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }




    @Override
    public int compareTo(Event event) {
        return getDateTime().compareTo(event.getDateTime());

     }
}
