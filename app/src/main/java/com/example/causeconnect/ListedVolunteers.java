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
import java.util.HashMap;
import java.util.Map;

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
         Map<Marker, Volunteer> markersMap = new HashMap<Marker, Volunteer>();
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
                                    String number =(String)document.get("phone");
                                    String uid_vol = (String)document.get("userid");
                                    double latitude = (double) document.get("volunteerLatitude");
                                    double longitude = (double) document.get("volunteerLongitude");


                                        LatLng latLng = new LatLng(latitude, longitude);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        Marker marker1 = mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                                        markersMap.put(marker1,new Volunteer(uid_vol,name,number));
                                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(@NonNull Marker marker) {
//                                                Log.d("volunteer", "onChat: "+uid_vol);
//                                                Log.d("volunteer", "onChat: "+name);
//                                                Log.d("volunteer", "onChat: "+number);
                                                String uid_marker = markersMap.get(marker).getUid();
                                                String number_marker = markersMap.get(marker).getNumber();

                                                AlertDialog.Builder builder = new AlertDialog.Builder(ListedVolunteers.this);
                                                builder.setTitle("Do you want to contact the organization?");
                                                builder.setItems(new CharSequence[]
                                                                {"Chat", "Call"},
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // The 'which' argument contains the index position
                                                                // of the selected item
                                                                switch (which) {
                                                                    case 0:
                                                                        Toast.makeText(ListedVolunteers.this, "Chat...", Toast.LENGTH_SHORT).show();
                                                                        Intent intent1 = new Intent(ListedVolunteers.this, MessageActivity.class);
                                                                        intent1.putExtra("userid", uid_marker);
                                                                        startActivity(intent1);
                                                                        break;
                                                                    case 1:
                                                                        Toast.makeText(ListedVolunteers.this, "Calling...", Toast.LENGTH_SHORT).show();
                                                                        String posted_by = number_marker;
                                                                        String uri = "tel:" + posted_by.trim() ;
                                                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                                                        intent.setData(Uri.parse(uri));
                                                                        startActivity(intent);
                                                                        break;
                                                                }
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
                        } else {
                            Log.d(TAG, "Error fetching data: ", task.getException());
                        }
                    }
                });


    }



}