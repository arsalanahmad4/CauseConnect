package com.example.causeconnect;

import static com.example.causeconnect.CreateEvent.cause;
import static com.example.causeconnect.CreateEvent.enteredAddress;
import static com.example.causeconnect.CreateEvent.latitude;
import static com.example.causeconnect.CreateEvent.longitude;
import static com.example.causeconnect.CreateEvent.name;
import static com.example.causeconnect.CreateEvent.organizationName;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.causeconnect.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StartCause extends AppCompatActivity  {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    public static final String TAG = "TAG";

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    EditText mName;
    EditText mOrgName;
    EditText mServices;
    EditText mCity;
    Button mCreateOrg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_cause);

        mName = findViewById(R.id.name_of_org_creator);
        autoFillName();


        mOrgName = findViewById(R.id.name_of_organization);


        mServices = findViewById(R.id.services_provided);


        mCity = findViewById(R.id.city);




        fAuth=FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();



        mCreateOrg = findViewById(R.id.create_organization);
        mCreateOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = mName.getText().toString();
                String org = mOrgName.getText().toString();
                String services = mServices.getText().toString();
                String city = mCity.getText().toString();
                if(name.isEmpty())
                {
                    mName.setError("Name is empty");
                    mName.requestFocus();
                    return;
                }
                if(org.isEmpty())
                {
                    mOrgName.setError("Create an Organization first");
                    mOrgName.requestFocus();
                    return;
                }
                if(services.isEmpty())
                {
                    mServices.setError("Enter services");
                    mServices.requestFocus();
                    return;
                }
                if(city.isEmpty())
                {
                    mCity.setError("Enter city name");
                    mCity.requestFocus();
                    return;
                }


                userID = fAuth.getCurrentUser().getUid();
                CollectionReference collectionReference = fStore.collection("Organization created data");
                Map<String,Object> user = new HashMap<>();
                user.put("timestamp", FieldValue.serverTimestamp());
                user.put("name",name);
                user.put("organizationName",org);
                user.put("servicesProvided",services);
                user.put("city",city);
                user.put("userid",userID);

                collectionReference.add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(),"Organization Successfully Created!",Toast.LENGTH_SHORT).show();
                                Log.d(TAG,"Success!");
                                AlertDialog.Builder builder = new AlertDialog.Builder(StartCause.this);
                                builder.setTitle("Dashboard")
                                        .setMessage("Which dashboard you want to shift to ? ")
                                        .setCancelable(false)
                                        .setPositiveButton("Organization", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(StartCause.this, OrganizationPage.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("Volunteer", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(StartCause.this, VolunteerPage.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                }
                                        });
                                //Creating dialog box
                                AlertDialog dialog  = builder.create();
                                dialog.show();
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