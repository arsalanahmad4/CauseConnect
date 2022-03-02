package com.example.causeconnect;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrganizationPage extends AppCompatActivity implements LocationListener {

    RadioButton createEvent;
    RadioButton hireVolunteer;
    RadioButton startCause;
    ImageButton chatButton,logoutButton;
    ImageView navigate_to_volunteer;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private static double lat;
    private static double lng;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_page);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        this.fStore = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        createEvent = findViewById(R.id.CreateEvent_radioButton);

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrganizationPage.this,CreateEvent.class);
                startActivity(i);
            }
        });

        hireVolunteer= findViewById(R.id.HireVolunteer_radioButton);

        hireVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrganizationPage.this,ListedVolunteers.class);
                startActivity(i);
            }
        });

        startCause = findViewById(R.id.start_cause);
        startCause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrganizationPage.this,StartCause.class);
                startActivity(i);
            }
        });
        navigate_to_volunteer= findViewById(R.id.navigate_button_organization);
        navigate_to_volunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrganizationPage.this);
                builder.setTitle("Shift")
                        .setMessage("Do you want to move to Volunteer page?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Adding code to upload data of volunteer in data base
                                uploadAndGetData();

                                Intent intent = new Intent(OrganizationPage.this,VolunteerPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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
            }
        });
        chatButton = findViewById(R.id.chat_button_org);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrganizationPage.this,ChatActivity.class);
                startActivity(i);
            }
        });
        logoutButton = findViewById(R.id.logout_button_org);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth != null && user != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrganizationPage.this);
                    builder.setTitle("Logout")
                            .setMessage("Are you sure you want to logout? ")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    firebaseAuth.signOut();
                                    Intent intent = new Intent(OrganizationPage.this,Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
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

                }
            }
        });


    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat =location.getLatitude();
        lng = location.getLongitude();
        Log.v("Activity location ", String.valueOf(lat)+"latitude of volunteer shifted from org");
    }
    public void uploadAndGetData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        double latitude = lat;
        double longitude = lng;

        reference = firestore.collection("Users data").document(currentid);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String nameResult = task.getResult().getString("name");
                    String phoneNo = task.getResult().getString("phone");




                    Log.v("Activity location ", String.valueOf(latitude));



                    CollectionReference collectionReference = fStore.collection("Volunteer data");
                    Map<String,Object> user = new HashMap<>();
                    user.put("timestamp", FieldValue.serverTimestamp());
                    user.put("name", nameResult);
                    user.put("phone",phoneNo);
                    user.put("volunteerLatitude",latitude);
                    user.put("volunteerLongitude",longitude);
                    user.put("userid",currentid);
                    collectionReference.add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(),"Successfully Registered as Volunteer",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });


    }


}