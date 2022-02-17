package com.example.causeconnect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VolunteerAdapter extends ArrayAdapter<Volunteer> {


    public VolunteerAdapter(@NonNull Context context, @NonNull ArrayList<Volunteer> objects) {
        super(context, 0, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.volunteer_list_item, parent, false);
        }
        Volunteer currentVolunteer = getItem(position);

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);
        nameTextView.setText(currentVolunteer.getName());

        TextView phoneTextView = (TextView) listItemView.findViewById(R.id.phone);
        phoneTextView.setText(currentVolunteer.getNumber());



        return listItemView;
    }
}
