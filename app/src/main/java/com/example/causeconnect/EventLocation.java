package com.example.causeconnect;

import static com.example.causeconnect.CreateEvent.cause;
import static com.example.causeconnect.CreateEvent.enteredAddress;
import static com.example.causeconnect.CreateEvent.latitude;
import static com.example.causeconnect.CreateEvent.longitude;
import static com.example.causeconnect.CreateEvent.name;
import static com.example.causeconnect.CreateEvent.organizationName;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventLocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_location);

        TextView top = findViewById(R.id.entered_address);
        top.setText(enteredAddress);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        fAuth=FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng eventLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(eventLocation)
                .title(organizationName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation,14f));


        Button submit = findViewById(R.id.confirm_event_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = fAuth.getCurrentUser().getUid();
                CollectionReference collectionReference = fStore.collection("Event created data");
                Map<String,Object> user = new HashMap<>();
                user.put("timestamp", FieldValue.serverTimestamp());
                user.put("name",name);
                user.put("organizationName",organizationName);
                user.put("cause",cause);
                user.put("address",enteredAddress);
                user.put("locationLatitude",latitude);
                user.put("locationLongitude",longitude);
                user.put("userid",userID);

                collectionReference.add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(),"Event Successfully Created!",Toast.LENGTH_SHORT).show();
                                Log.d(TAG,"Success!");
                                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                Intent intent = new Intent(EventLocation.this, OrganizationPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error!", e);
                            }
                        });

            }
        });
    }
}