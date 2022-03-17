package com.example.causeconnect;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.causeconnect.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private AutoCompleteTextView mSearchText;
    private EditText mName;
    private EditText mNumber;
    private Spinner mOrg;
    private EditText mCause;

    public static double latitude;
    public static double longitude;
    public static String enteredAddress;

    public static String name;
    public static String organizationName;
    public static String cause;
    public static String phoneNumber;

    private FirebaseFirestore cloudstorage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        this.fStore = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        mName= findViewById(R.id.name_of_event_creator);
        autoFillName();

        mOrg= findViewById(R.id.org_name);
        mNumber = findViewById(R.id.phone_no);


        getOrganization();

        mCause= findViewById(R.id.cause_of_event);



        mSearchText= findViewById(R.id.address);

        Places.initialize(getApplicationContext(), "AIzaSyCsS0dkByXBXXuGxuXFT4kfguhD8MONRuc");

        Button createEvent = findViewById(R.id.create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = mName.getText().toString().trim();
                organizationName = mOrg.getSelectedItem().toString().trim();
                cause = mCause.getText().toString().trim();
                phoneNumber = mNumber.getText().toString().trim();


                if(name.isEmpty()){
                    mName.setError("Enter the name");
                    mName.requestFocus();
                    return;
                }
                if(organizationName.isEmpty()){
                    mOrg.requestFocus();
                    return;
                }
                if(cause.isEmpty()){
                    mCause.setError("Enter the cause");
                    mCause.requestFocus();
                    return;
                }
                if(phoneNumber.isEmpty()){
                    mNumber.setError("Enter the phone number");
                    mNumber.requestFocus();
                    return;
                }

                geoLocate();
                Intent map = new Intent(CreateEvent.this,EventLocation.class);
                startActivity(map);
            }
        });
    }


    private void geoLocate(){
        Log.d("CreateEvent","geoLocate: geoLocating");
        String stringSearch = mSearchText.getText().toString();

        if(stringSearch.isEmpty()){
            mSearchText.setError("Enter address");
            mSearchText.requestFocus();
            return;
        }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this,"Select an organization. If nothing to select create an org by Start a cause",Toast.LENGTH_LONG).show();
    }
    public void getOrganization(){
        mOrg.setPrompt("Select the organization");
        List<String> organizations = new ArrayList<String>();
        mOrg.setOnItemSelectedListener(CreateEvent.this);
        this.cloudstorage = FirebaseFirestore.getInstance();
        cloudstorage.collection("Organization created data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.contains("organizationName")) {
                                    String org = (String) document.get("organizationName");
                                    String uid = (String) document.get("userid");
                                        organizations.add(org);


                                }
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CreateEvent.this,
                                        android.R.layout.simple_spinner_item,
                                        organizations);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                mOrg.setAdapter(dataAdapter);
                            }
                        } else {

                        }
                    }
                });
    }

    public void autoFillName(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mName.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}