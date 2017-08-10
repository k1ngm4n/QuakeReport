package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<EarthquakeDataClass> {

    View listItemView;
    EarthquakeDataClass currentEQ;

    public EarthquakeAdapter(Activity context, List<EarthquakeDataClass> Eqs) {
        super(context, 0, Eqs);
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private void displayLocation() {
        String locationStr = currentEQ.getLocation();

        if (locationStr.contains(" of ")) {
            String[] locationArr = locationStr.split(" of ");

            TextView location = (TextView) listItemView.findViewById(R.id.v_location);
            location.setText(locationArr[0].trim() + " of");

            TextView location2 = (TextView) listItemView.findViewById(R.id.v_location2);
            location2.setText(locationArr[1].trim());
        } else {
            TextView location2 = (TextView) listItemView.findViewById(R.id.v_location2);
            location2.setText(locationStr.trim());
        }
    }

    private int getMagnitudeColor(double mag){

        int magFloorValue = (int) Math.floor(mag);
        int colorID = 0;

        switch (magFloorValue) {
            case 0 :
            case 1 : colorID = R.color.magnitude1;break;
            case 2 : colorID = R.color.magnitude2;break;
            case 3 : colorID = R.color.magnitude3;break;
            case 4 : colorID = R.color.magnitude4;break;
            case 5 : colorID = R.color.magnitude5;break;
            case 6 : colorID = R.color.magnitude6;break;
            case 7 : colorID = R.color.magnitude7;break;
            case 8 : colorID = R.color.magnitude8;break;
            case 9 : colorID = R.color.magnitude9;break;
            case 10 : colorID = R.color.magnitude10plus;break;
        }

        return  ContextCompat.getColor(getContext(), colorID);
    }

    private void displayMagnitude() {
        TextView magnitude = (TextView) listItemView.findViewById(R.id.v_magnitude);

        double mag = currentEQ.getMagnitude();
        DecimalFormat formatter = new DecimalFormat("0.0");
        magnitude.setText(formatter.format(mag));

        //cara ngeset warna background dari circle
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();
        int magnitudeColor = getMagnitudeColor(mag);
        magnitudeCircle.setColor(magnitudeColor);
    }

    private void displayTime() {
        long datetime = currentEQ.getDateTime();
        Date dateObj = new Date(datetime);

        TextView date = (TextView) listItemView.findViewById(R.id.v_date);
        date.setText(formatDate(dateObj));

        TextView time = (TextView) listItemView.findViewById(R.id.v_time);
        time.setText(formatTime(dateObj));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_content, parent, false);
        }

        currentEQ = getItem(position);

        displayLocation();

        displayMagnitude();

        displayTime();

        return listItemView;
    }
}
