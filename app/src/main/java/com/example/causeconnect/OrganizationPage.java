package com.example.causeconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class OrganizationPage extends AppCompatActivity {

    RadioButton createEvent;
    RadioButton hireVolunteer;
    RadioButton startCause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_page);

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


    }
}