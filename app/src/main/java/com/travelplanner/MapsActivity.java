package com.travelplanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private EditText mSearchText;
    private ImageView mGps;
    private ImageView mList;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location mLastKnownLocation;
    private AutocompleteSessionToken token;

    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    protected DatabaseReference database;
    private ArrayList<Poi> pois;
    private ArrayList<Poi> selected;

    // for route generating
    private Polyline currentPolyline;
    private List<MarkerOptions> places;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();
        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Places.initialize(MapsActivity.this, getResources().getString(R.string.google_api_key));
        placesClient = Places.createClient(this);
        token = AutocompleteSessionToken.newInstance();

        loadInPOIData();
        loadInSelectedPOIData();

        mList = findViewById(R.id.ic_menu_edit);
        mList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showList();
            }
        });
    }

    private void loadInPOIData() {
        pois = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("POI");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot poiSnapshot : dataSnapshot.getChildren()){
                    Poi poi = poiSnapshot.getValue(Poi.class);
                    pois.add(poi);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Error occurred");
            }
        });
        addPOIToScrollView();
    }

    private void loadInSelectedPOIData() {
        selected = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("Selected");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot poiSnapshot : dataSnapshot.getChildren()){
                    Poi poi = poiSnapshot.getValue(Poi.class);
                    selected.add(poi);
                }
                /** setup markerOptions & generate routes */
                //
                loadMarkers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Error occurred");
            }
        });
    }
    private void addPOIToScrollView() {
        Log.d(TAG, "Scroll View is being initialized.");
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView poiRecyclerView = findViewById(R.id.poi_scroll_item);
        poiRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new PoiScrollViewAdapter(this, pois);
        poiRecyclerView.setAdapter(adapter);
    }

    private void showList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void init() {
        Log.d(TAG, "init: initializing");
        mSearchText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                geoLocate();
            }
            return false;
        });
        mGps.setOnClickListener((View v) -> {
            Log.d(TAG, "onClick: clicking on GPS icon");
            getDeviceLocation();
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: locating the geo");
        String searchString = mSearchText.getText().toString();

        // Places API
        FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                .setCountry("us")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(searchString)
                .build();
        placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                if (task.isSuccessful()) {
                    FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                    if (predictionsResponse != null) {
                        Log.d(TAG, "onComplete: prediction found!!!");
                        predictionList = predictionsResponse.getAutocompletePredictions();
                    }
                } else {
                    Log.d(TAG, "onComplete: prediction fetching task unsuccessful");
                }
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.d(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the current location");
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener((Task task) -> {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = (Location) task.getResult();
                        if (mLastKnownLocation != null) {
                            Log.d(TAG, "onComplete: found current location");
                            moveCamera(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "getDeviceLocation: Current location is null");
                        }
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: is called");
        mMap = googleMap;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }

//        // add markers for each POI in "List<Poi> pois"
//        if (places != null && !places.isEmpty()) {
//            for (MarkerOptions place : places) {
//                mMap.addMarker(place);
//            }
//            System.out.println(places.size());
//        }
    }


    /** Helper function:
     * fetch URLs of route & add markers
     */
    private void loadMarkers() {
        places = new  ArrayList<>();
        /** generate route between two POIs */
        if (selected != null && !selected.isEmpty()) {
            for (int i = 0; i < selected.size(); i++) {
                // create MarkerOptions for each POI
                String poiName = selected.get(i).getPoi_name();
                places.add(new MarkerOptions()
                        // lat & long are opposite here
                        .position(new LatLng(getGeoInfo(selected.get(i).getPoi_latitude()), getGeoInfo(selected.get(i).getPoi_longitude())))
                        .title(poiName));

                // for each pair of POIs, get URLs for Directions API to draw routes
                if (places.size() >= 2) {
                    new FetchURL(MapsActivity.this)
                            .execute(getUrl(places.get(i - 1).getPosition(), places.get(i).getPosition(), "driving"), "driving");
                }
            }
        }
        /** add markers for each POI */
        if (places != null && !places.isEmpty()) {
            for (MarkerOptions place : places) {
                mMap.addMarker(place);
            }
        }
    }

    /** Helper function:
     * convert string latLng to double
     */
    private double getGeoInfo(String lat) {
        double num = 0;
        boolean flag = false;
        int i = 0;
        // 1. deal with integer part
        while (lat.charAt(i) != '.') {
            if (lat.charAt(i) == '-') {
                flag = true;
            } else {
                num = num * 10 + (lat.charAt(i) - '0');
            }
            i++;
        }
        i++;
        // 2. deal with decimal part
        int count  = 1; // divide by 10^k
        while (i < lat.length()) {
            num += (lat.charAt(i++) - '0' + 0.0) / Math.pow(10, count++);
        }
        return flag? -num : num;
    }
    /**
     * This is the helper function for getting the url of two locations from Google Directions API
     */
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode: driving
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    /**
     * This is the helper for drawing lines
     */
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
//        currentPolyline.setColor();
    }
}
