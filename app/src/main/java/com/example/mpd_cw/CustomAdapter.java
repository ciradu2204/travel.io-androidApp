package com.example.mpd_cw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//Cynthia Iradukunda - s1906581
public class CustomAdapter extends BaseAdapter implements Filterable {

    private final Activity mActivity;
    private  List<Event> list;
    private List<Event> filteredList;


    CustomAdapter(Activity mActivity, List<Event> list){
        this.mActivity = mActivity;
        this.list = list;
        this.filteredList = list;
    }

    public void updateItems(ArrayList<Event> list){
        this.list = list;
        this.filteredList = list;

        //notify the adapter to refresh the listview
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Event getItem(int i) {
        return
                filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position,  View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.items_list, parent, false);

        TextView titleView = convertView.findViewById(R.id.title);
        TextView categoryView = convertView.findViewById(R.id.category);
        TextView locationView = convertView.findViewById(R.id.location);
        TextView roadView = convertView.findViewById(R.id.road);
        TextView priority = convertView.findViewById(R.id.priority);


        Event event = getItem(position);
        String title = event.getTitle();
        String status = event.getStatus();

        //get the drawable to set on the priority text view
        Drawable leftDrawable = AppCompatResources.getDrawable(mActivity, R.drawable.circle);


        //based on the status, change the color, and update the text view
        if(status.equals("Past")){
            assert leftDrawable != null;
            leftDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN );
            priority.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null , null);
        }else if(status.equals("Upcoming")){
            assert leftDrawable != null;
            leftDrawable.setColorFilter(mActivity.getColor(R.color.green), PorterDuff.Mode.SRC_IN );
            priority.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null , null);        }

        String location = event.getCountry()  + "," + event.getRegion();
        titleView.setText(title.substring(0, title.indexOf('|')));
        categoryView.setText(event.getCategory());
        locationView.setText(location);
        roadView.setText(event.getRoad());
        priority.setText(event.getStatus());


        return convertView ;


     }

     //An overriden method to filter list
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                //when the user search str is of size 0 then don't change anything
                if(charSequence == null || charSequence.length() == 0){
                    filterResults.count = list.size();
                    filterResults.values = list;
                }else{
                    //else look for event which contains the keyword to update the filter results
                    String searchStr = charSequence.toString().toLowerCase();

                    List<Event> filteredEvents  = new ArrayList<>();

                    for(Event event: list){

                        String road = event.getRoad().toLowerCase();
                        String region = event.getRegion().toLowerCase();
                        String status = event.getStatus().toLowerCase();
                        String category = event.getCategory().toLowerCase();
                        String title = event.getTitle().toLowerCase();
                        String country = event.getCountry().toLowerCase();

                        if(road.contains(searchStr) || region.contains(searchStr) || status.contains(searchStr) || category.contains(searchStr) || title.contains(searchStr) || country.contains(searchStr)){
                            filteredEvents.add(event);
                        }

                        filterResults.count = filteredEvents.size();
                        filterResults.values = filteredEvents;
                    }

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                // a method to be called to display filtered results on ui
                 filteredList = (List<Event>) filterResults.values;
                 notifyDataSetChanged();
            }
        };
    }


}

