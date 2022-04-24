package com.example.mpd_cw.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mpd_cw.objects.Event;
import com.example.mpd_cw.activities.ListItemActivity;
import com.example.mpd_cw.R;
import com.example.mpd_cw.viewModels.SharedViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
//Cynthia Iradukunda - s1906581

public class MapCustomFragment extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<Event>, ClusterManager.OnClusterItemClickListener<Event>, ClusterManager.OnClusterItemInfoWindowClickListener<Event>{

    private SharedViewModel viewModel;
    private GoogleMap mGoogleMap;
    private ArrayList<Event> list;
    private ClusterManager<Event> clusterManager;
    private SupportMapFragment supportMapFragment;
    private Event clickedevent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.incident_fragmentmap, container, false);


        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        list  = new ArrayList<>();

        //Initialise the map view
        supportMapFragment  = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

            }
        });


        viewModel.getEventList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {

                list = events;
                reloadMap();
            }
        });

        //observe for any changes of filtered data
        viewModel.getFilteredData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
               list = events;
               reloadMap();

            }
        });



        return view;

    }


    //reload the map anytime the filter changes
    public void reloadMap(){
         supportMapFragment.getMapAsync( this);
    }


    //when the map is ready
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.clear();
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        clusterManager = new ClusterManager<Event>(getActivity(), mGoogleMap);
        clusterManager.setRenderer(new MarkerClusterRenderer(getActivity(), mGoogleMap, clusterManager));
        mGoogleMap.setOnCameraIdleListener(clusterManager);
        mGoogleMap.setOnMarkerClickListener(clusterManager);
        mGoogleMap.setOnInfoWindowClickListener(clusterManager);
        mGoogleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());


        clusterManager.getMarkerCollection().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.custom_info_window, null);

                TextView title = view.findViewById(R.id.info_window_title);
                TextView status = view.findViewById(R.id.info_window_status);

                String titleText = clickedevent.getTitle();
                title.setText(titleText.substring(0, titleText.indexOf('|')));
                status.setText(clickedevent.getStatus());

                return view;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }
        });
        //load events
        addEvents();

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //change the camera position
        CameraPosition googlePlex = CameraPosition.builder()
                .target(list.get(0).getPosition())
                .zoom(10)
                .bearing(0)
                .tilt(45)
                .build();

        //move camera to that position
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

        //add listeners oon the cluster
        clusterManager.setOnClusterClickListener(MapCustomFragment.this);

        //add listeners on the item
        clusterManager.setOnClusterItemClickListener(MapCustomFragment.this);

        clusterManager.setOnClusterItemInfoWindowClickListener(MapCustomFragment.this);


        //add clusters
        clusterManager.cluster();



    }




    //add events to the cluster
    private void addEvents() {
        if(list.size() == 0 ){
            list = viewModel.getEventList().getValue();
        }
        assert list != null;

        clusterManager.addItems(list);

    }

    //zoom in camera
    public void animateZoomInCamera(LatLng latLng){

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));


    }


    //when the cluster item is clicked
    @Override
    public boolean onClusterItemClick(Event event) {
        animateZoomInCamera(event.getPosition());
        clickedevent = event;
        return false;
    }

    //when the cluster is clicked
    @Override
    public boolean onClusterClick(Cluster<Event> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for(ClusterItem item: cluster.getItems()){
            LatLng itemPosition = item.getPosition();
            builder.include(itemPosition);
        }

        final LatLngBounds bounds = builder.build();

        try {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }catch (Exception error){
            Log.e("MapCustomFragment", String.valueOf(error));

        }


        return true;
    }


    //view more when a user clicks on the info window
    @Override
    public void onClusterItemInfoWindowClick(Event item) {
        Intent mIntent = new Intent(getContext(), ListItemActivity.class);
        ListFragment.addIntentData(item, mIntent );
        startActivity(mIntent);
    }

    //change the default marker
    private class MarkerClusterRenderer extends DefaultClusterRenderer<Event> {

        private static final int MARKER_DIMENSION = 48;
        private final IconGenerator iconGenerator;
        private final ImageView markerImageView;

        public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<Event> clusterManager) {
            super(context, map, clusterManager);

            iconGenerator = new IconGenerator(context);
            markerImageView = new ImageView(context);
            markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
            iconGenerator.setContentView(markerImageView);

        }

        @Override
        protected void onBeforeClusterItemRendered(@NonNull Event item, @NonNull MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            if(item.getCategory().equals("Road Works")){
                markerImageView.setImageResource(R.drawable.roadwork_icon);

            }else if(item.getCategory().equals("Congestion")){
                markerImageView.setImageResource(R.drawable.congestion);


            }else{
                markerImageView.setImageResource(R.drawable.accident);
            }

            //change the color of the image based on status
            changeColorBasedOnStatus(item.getStatus(), markerImageView);

            Bitmap icon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

        }


    }

    //change the color based on status
    public void changeColorBasedOnStatus(String status, ImageView imageView){

        if(status.equals("Upcoming")){
            imageView.setColorFilter(getContext().getColor(R.color.green));
        }else if(status.equals("Ongoing")){
            imageView.setColorFilter(Color.RED);
        }else if(status.equals("Past")){
            imageView.setColorFilter(Color.BLACK);
        }
    }



}




