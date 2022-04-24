package com.example.mpd_cw.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.mpd_cw.R;
//Cynthia Iradukunda - s1906581

public class ListItemActivity extends AppCompatActivity {

    private LinearLayout backgroundImage;
    private ImageView prevButton;
    private TextView titleView;
    private TextView locationView;
    private TextView dateView;
    private TextView timeView;
    private TextView descriptionView;
    private TextView statusView;
    private TextView categoryView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);

        prevButton = findViewById(R.id.prev);
        statusView = findViewById(R.id.itemStatus);
        titleView = findViewById(R.id.itemTitle);
        backgroundImage = findViewById(R.id.backgroundImage);
        locationView = findViewById(R.id.itemLocation);
        dateView = findViewById(R.id.itemDate);
        timeView = findViewById(R.id.itemTime);
        descriptionView = findViewById(R.id.itemDescription);
        categoryView = findViewById(R.id.itemCategory);


        Intent intent = getIntent();



        if(intent != null ){
            String status = intent.getStringExtra("status");
            String title = intent.getStringExtra("title");
            String location = intent.getStringExtra("location");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String description = intent.getStringExtra("description");
            String category = intent.getStringExtra("category");
            Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.circle);




            //based on the status, change the color, and update the text view
            if(status.equals("Past")){
                assert leftDrawable != null;
                leftDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN );
                statusView.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null , null);
            }else if(status.equals("Upcoming")){
                assert leftDrawable != null;
                leftDrawable.setColorFilter(getColor(R.color.green), PorterDuff.Mode.SRC_IN );
                statusView.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null , null);        }

            if(!category.equals("Road Works")){

                Drawable background = AppCompatResources.getDrawable(this, R.drawable.incidents);
                backgroundImage.setBackground(background);
            }

            statusView.setText(status);
            titleView.setText(title);
            locationView.setText(location);
            dateView.setText(date);
            timeView.setText(time);
            descriptionView.setText(description);
            categoryView.setText(category);

        }



    }


    public void goToEvents(View view) {
        finish();
    }
}
