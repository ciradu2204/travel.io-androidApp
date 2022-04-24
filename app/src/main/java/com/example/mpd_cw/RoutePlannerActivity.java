package com.example.mpd_cw;

 import android.app.DatePickerDialog;
 import android.content.DialogInterface;
 import android.os.Bundle;
import android.view.View;
import android.widget.Button;
 import android.widget.DatePicker;
 import android.widget.EditText;
 import android.widget.LinearLayout;

import androidx.annotation.Nullable;
 import androidx.appcompat.app.AlertDialog;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.lifecycle.Observer;
 import androidx.lifecycle.ViewModelProvider;

 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
//Cynthia Iradukunda - s1906581

public class RoutePlannerActivity extends AppCompatActivity {


    private Button planRoute;
    private SharedViewModel viewModel;
    private final Calendar myCalendar= Calendar.getInstance();

    private LinearLayout progressBarLayout;
    private EditText journeyDate;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routeplanner);

        planRoute = findViewById(R.id.plannerButton);
        progressBarLayout = findViewById(R.id.road_planner_progressbar);
        journeyDate = findViewById(R.id.journeyDate);
        journeyDate.setEnabled(false);

        //Date picker dialog to be displayed
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };



        journeyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RoutePlannerActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //view model to get maintain data between fragments
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        //create Repository
        Repository repository = Repository.getInstance();

        //create map fragment
        MapCustomFragment customFragment = new MapCustomFragment();

        if(repository.getFullList() == null){
            repository.processTask(viewModel, RoutePlannerActivity.this);
        }else{
            viewModel.setEventList(repository.getFullList());
        }

        //observe the view model
        viewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {

                journeyDate.setEnabled(true);
                progressBarLayout.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.plannerFrameLayout, customFragment).commit();

            }
        });


    }

    public void goBackHome(View view) {

        finish();
    }


    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.UK);
        journeyDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void findEvents(View view) throws ParseException {

        String userDate = journeyDate.getText().toString();
        String myFormat="MM/dd/yy";
        String myFormat2 = "yyyy-MM-dd'T'HH:mm:ss";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat2, Locale.UK);

        //when a user clicks find event without the date
        if(userDate.equals("")){
            journeyDate.setError("The Date is Required");
        }else{
            Date userChosenDate = new SimpleDateFormat(myFormat, Locale.UK).parse(userDate);
            progressBarLayout.setVisibility(View.VISIBLE);
            ArrayList<Event> filteredList = new ArrayList<>();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Event> list  = viewModel.getEventList().getValue();

                     //convert the string to date
                     for(Event event: list){
                         Date eventStart = new Date();
                         Date eventEnd = new Date();
                         try {
                              eventStart = sdf.parse(event.getEventStart());
                              eventEnd = sdf.parse(event.getEventEnd());
                         } catch (ParseException e) {
                             e.printStackTrace();
                         }

                         SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.UK);

                         if(fmt.format(userChosenDate).equals(fmt.format(eventStart)) || fmt.format(userChosenDate).equals(fmt.format(eventEnd))){
                             filteredList.add(event);
                         }else if(userChosenDate.after(eventStart) && userChosenDate.before(eventEnd)){

                             filteredList.add(event);
                         }

                     }

                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {

                             if(filteredList.size() != 0){
                                 viewModel.setFilteredData(filteredList);
                             }else{
                                 noInfoDialog();
                             }
                             progressBarLayout.setVisibility(View.GONE);

                         }
                     });
                }


            }).start();



        }

    }

    private void  noInfoDialog(){
        new AlertDialog.Builder(this)
                .setTitle("No events found")
                .setMessage("They are no events found on that particular date. Go back!")
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModel.setFilteredData(viewModel.getEventList().getValue());
            }
        }).show();
    }


}


