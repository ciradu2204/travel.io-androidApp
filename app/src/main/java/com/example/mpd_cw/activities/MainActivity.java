package com.example.mpd_cw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mpd_cw.R;
import com.example.mpd_cw.repositories.Repository;

//Cynthia Iradukunda - s1906581

public class MainActivity extends AppCompatActivity {

    private Button incident;
    private Button planRoute;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the buttons id of the activity main
        incident = findViewById(R.id.incidentButton);
        planRoute = findViewById(R.id.routePlanner);


     }

    public void planRoute(View view) {
        if(isNetworkAvailable()){
            Intent i = new Intent(this, RoutePlannerActivity.class);
            startActivity(i);
        }else{
            Toast.makeText(this, "Enable internet first", Toast.LENGTH_SHORT).show();

        }



    }

    public void showIncidents(View view) {


        if(isNetworkAvailable()){
            Intent i = new Intent(this, RoadEventsActivity.class);
            startActivity(i);
        }else{
            Toast.makeText(this, "Enable internet first", Toast.LENGTH_SHORT).show();
        }



    }

    public boolean isNetworkAvailable(){
        //The code from https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
        ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();


    }
}