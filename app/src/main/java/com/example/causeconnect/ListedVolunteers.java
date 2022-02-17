package com.example.causeconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
                                    String uid = (String)document.get("userid");
                                    String number =(String)document.get("phone");
                                    double latitude = (double) document.get("volunteerLatitude");
                                    double longitude = (double) document.get("volunteerLongitude");

                                    if(latitude!=0 && longitude!=0){
                                        LatLng latLng = new LatLng(latitude, longitude);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(@NonNull Marker marker) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ListedVolunteers.this);
                                                builder.setTitle("Contact")
                                                        .setMessage("Do you want to call the volunteer ? ")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                String posted_by = number;

                                                                String uri = "tel:" + posted_by.trim() ;
                                                                Intent intent = new Intent(Intent.ACTION_CALL);
                                                                intent.setData(Uri.parse(uri));
                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                //Creating dialog box
                                                AlertDialog dialog  = builder.create();
                                                dialog.show();
                                                return false;
                                            }
                                        });
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