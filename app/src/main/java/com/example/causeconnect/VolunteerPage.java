package com.example.causeconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class VolunteerPage extends AppCompatActivity {
    RadioButton findCause;
    RadioButton startCause;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_page);

        findCause = findViewById(R.id.findCause_radioButton);
        findCause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VolunteerPage.this, ListedEvents.class);
                startActivity(i);
            }
        });

        startCause = findViewById(R.id.startCause_radioButton);
        startCause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VolunteerPage.this,StartCause.class);
                startActivity(i);
            }
        });
    }
}