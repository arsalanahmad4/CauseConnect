package com.example.causeconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrganizationPage extends AppCompatActivity {

    RadioButton createEvent;
    RadioButton hireVolunteer;
    RadioButton startCause;
    ImageButton chatButton,logoutButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_page);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

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
}