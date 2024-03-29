package com.example.causeconnect;

//import static com.example.causeconnect.Register.username;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements LocationListener {
    private EditText e_mail, pass_word;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private FirebaseFirestore cloudstorage;
    String userID;
    public static final String TAG = "TAG";



    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;

    private static double lat;
    private static double lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        e_mail = findViewById(R.id.emailLogin);
        pass_word = findViewById(R.id.password);
        Button btn_login = findViewById(R.id.btn_login);
        Button btn_sign = findViewById(R.id.btn_signup);



        this.fStore = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);




        btn_login.setOnClickListener(v -> {
            String email= e_mail.getText().toString().trim();
            String password=pass_word.getText().toString().trim();

            if(email.isEmpty())
            {
                e_mail.setError("Email is empty");
                e_mail.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                e_mail.setError("Enter the valid email");
                e_mail.requestFocus();
                return;
            }
            if(password.isEmpty())
            {
                pass_word.setError("Password is empty");
                pass_word.requestFocus();
                return;
            }
            if(password.length()<6)
            {
                pass_word.setError("Length of password is more than 6");
                pass_word.requestFocus();
                return;
            }
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {

                if(task.isSuccessful())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle("Registration")
                            .setMessage("What do you want to register as ? ")
                            .setCancelable(false)
                            .setPositiveButton("Organization", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent organization = new Intent(Login.this,OrganizationPage.class);
                                    startActivity(organization);
                                    Login.this.finish();
                                }
                            })
                            .setNegativeButton("Volunteer", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    uploadAndGetData();

                                    Intent volunteer = new Intent(Login.this,VolunteerPage.class);
                                    startActivity(volunteer);
                                    Login.this.finish();}
                            });
                    //Creating dialog box
                    AlertDialog dialog  = builder.create();
                    dialog.show();

                }
                else
                {
                    Toast.makeText(Login.this,
                            "Please Check Your login Credentials",
                            Toast.LENGTH_SHORT).show();
                }

            });



        });
        btn_sign.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
        });
    }
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat =location.getLatitude();
        lng = location.getLongitude();
        Log.v("Activity location ", String.valueOf(lat)+"latitude of volunteer");
    }
    public void uploadData(){

        userID = mAuth.getCurrentUser().getUid();




        double latitude = lat;
        double longitude = lng;



        Log.v("Activity location ", String.valueOf(latitude));



        CollectionReference collectionReference = fStore.collection("Volunteer data");
        Map<String,Object> user = new HashMap<>();
        user.put("timestamp", FieldValue.serverTimestamp());
//        user.put("name", phoneNumber);
//        user.put("phone",name);
        user.put("volunteerLatitude",latitude);
        user.put("volunteerLongitude",longitude);
        user.put("userid",userID);
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