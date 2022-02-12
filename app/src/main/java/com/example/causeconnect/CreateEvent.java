package com.example.causeconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateEvent extends AppCompatActivity {

    private AutoCompleteTextView mSearchText;
    private EditText mName;
    private EditText mOrg;
    private EditText mCause;

    public static double latitude;
    public static double longitude;
    public static String enteredAddress;

    public static String  name;
    public static String organizationName;
    public static String cause;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mName= findViewById(R.id.name_of_event_creator);
        mOrg= findViewById(R.id.org_name);
        mCause= findViewById(R.id.cause_of_event);



        mSearchText= findViewById(R.id.address);

        Places.initialize(getApplicationContext(), "AIzaSyCsS0dkByXBXXuGxuXFT4kfguhD8MONRuc");

        Button createEvent = findViewById(R.id.create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = mName.getText().toString();
                organizationName = mOrg.getText().toString();
                cause = mCause.getText().toString();

                geoLocate();
                Intent map = new Intent(CreateEvent.this,EventLocation.class);
                startActivity(map);
            }
        });
    }


    private void geoLocate(){
        Log.d("CreateEvent","geoLocate: geoLocating");
        String stringSearch = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(CreateEvent.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(stringSearch,1);
        }catch (IOException e){
            Log.e("MainActivity","geoLocate:IOException"+e.getMessage());
        }
        if(list.size()>0){
            Address address = list.get(0);
            Log.d("MainActivity","geoLocate : found a location: "+address.toString());
            latitude = address.getLatitude();
            longitude = address.getLongitude();
            enteredAddress = stringSearch;
        }
    }
}