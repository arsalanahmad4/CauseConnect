package com.example.causeconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ListedVolunteers extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    FirebaseFirestore fStore;
    public static final String TAG = "TAG";
    private FirebaseFirestore cloudstorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.activity_listed_volunteers);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.listed_volunteer_map);
        mapFragment.getMapAsync(this);

    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showLocation();

    }

    public void showLocation() {
        this.cloudstorage = FirebaseFirestore.getInstance();
        cloudstorage.collection("Volunteer data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.contains("volunteerLatitude") && document.contains("volunteerLongitude")) {
                                    String name = (String)document.get("name");
                                    double latitude = (double) document.get("volunteerLatitude");
                                    double longitude = (double) document.get("volunteerLongitude");

                                    if(latitude!=0 && longitude!=0){
                                        LatLng latLng = new LatLng(latitude, longitude);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                                    }

                                }
                            }
                        } else {
                            Log.d(TAG, "Error fetching data: ", task.getException());
                        }
                    }
                });

    }



}