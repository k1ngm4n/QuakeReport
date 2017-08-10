package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

public class EarthquakeLoader extends AsyncTaskLoader<Pair<Boolean, String>>{

    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Pair<Boolean, String> loadInBackground() {
        if (mUrl == null) return null;

        Log.i("kingman", "start fetching");
        return EqUtils.getJsonFromApi(mUrl);
    }
}
