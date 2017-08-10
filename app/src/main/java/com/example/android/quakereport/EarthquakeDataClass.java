package com.example.android.quakereport;

/**
 * Created by KingMan on 31-Jul-17.
 */

public class EarthquakeDataClass {

    private String mLocation;
    private Double mMagnitude;
    private long mDateTime;
    private String mUrl;

    public EarthquakeDataClass(String location, Double magnitude, long datetime, String url) {
        mLocation = location;
        mMagnitude = magnitude;
        mDateTime = datetime;
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getLocation() {
        return mLocation;
    }

    public Double getMagnitude() {
        return mMagnitude;
    }

    public long getDateTime() {
        return mDateTime;
    }
}
