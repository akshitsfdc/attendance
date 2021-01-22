package com.enalytix.faceattendance.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.services.GeoService;

public class NotAtLocationActivity extends BaseActivity {

    private TextView currentLatLongText;
    private TextView targetLatLongText;
    private TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_at_location);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.parent);

        super.setThumbnail();
        super.setTitle();
        super.setCurrentDate();

        currentLatLongText = findViewById(R.id.currentLatLongText);
        targetLatLongText = findViewById(R.id.targetLatLongText);
        distanceText = findViewById(R.id.distanceText);

        boolean isException = (boolean)routing.getParam("isException");
        String targetLatLog = (String) routing.getParam("target");
        String current, target, distance;
        if(isException){
            current = "Current : Lat/ Long : "+GeoService.currentLatLong+" with error ";
        }else{
            current = "Current : Lat/ Long : "+GeoService.currentLatLong;
        }
        target = "Target : "+targetLatLog;
        distance = "Distance : "+ GeoService.distanceFrom;

        currentLatLongText.setText(current);
        targetLatLongText.setText(target);
        distanceText.setText(distance);
    }
}