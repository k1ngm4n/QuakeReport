/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<Pair<Boolean, String>> {

    private static final int EARTHQUAKE_LOADER_ID = 1;

    ListView earthquakeListView;
    TextView emptyTextView;
    EarthquakeAdapter EQAdapter;
    ProgressBar pgSpinner;
    Button btnRetry;

    static final String API_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            EarthquakeDataClass currentEQ = EQAdapter.getItem(position);

            Uri earthquakeUri = Uri.parse(currentEQ.getUrl());
            Intent i = new Intent(Intent.ACTION_VIEW, earthquakeUri);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        earthquakeListView = (ListView) findViewById(R.id.list);
        emptyTextView = (TextView) findViewById(R.id.empty_text_view);

        pgSpinner = (ProgressBar) findViewById(R.id.loading_spiner);
        pgSpinner.setVisibility(View.INVISIBLE);

        EQAdapter = new EarthquakeAdapter(this, new ArrayList<EarthquakeDataClass>());
        earthquakeListView.setAdapter(EQAdapter);

        btnRetry = (Button) findViewById(R.id.retryButton);

        earthquakeListView.setOnItemClickListener(mItemClickListener);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            emptyTextView.setText(R.string.no_conn);
            showErrorUI(true);
        }
    }

    public void showErrorUI(boolean flag) {
        if (flag) {
            emptyTextView.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.INVISIBLE);
            btnRetry.setVisibility(View.INVISIBLE);
        }
    }

    public void retryCall(View view) {
        showErrorUI(false);

        Log.i("kingman", "RETRYING NOW");
        LoaderManager loaderManager = getLoaderManager();

        loaderManager.destroyLoader(EARTHQUAKE_LOADER_ID);
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Pair<Boolean, String>> onCreateLoader(int i, Bundle bundle) {
        pgSpinner.setVisibility(View.VISIBLE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String minMag = sharedPrefs.getString(getString(R.string.settings_min_magnitude_key), getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        String limit = sharedPrefs.getString(getString(R.string.settings_limit_key), getString(R.string.settings_limit_default));

        Uri baseUri = Uri.parse(API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", limit);
        uriBuilder.appendQueryParameter("minmag", minMag);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Pair<Boolean, String>> loader, Pair<Boolean, String> data) {
        EQAdapter.clear();

        pgSpinner.setVisibility(View.INVISIBLE);

        boolean no_data;

        if (data.first) {
            if (data.second == "") {
                emptyTextView.setText(R.string.no_eq);
                no_data = true;
            } else {
                List<EarthquakeDataClass> arr_eq = EqUtils.getEqArrayData(data.second);

                if (arr_eq.size() == 0) {
                    emptyTextView.setText(R.string.no_eq);
                    no_data = true;
                } else {
                    EQAdapter.addAll(arr_eq);
                    no_data = false;
                }
            }
        } else {
            emptyTextView.setText(data.second);
             no_data = true;
        }

        showErrorUI(no_data);

        Log.i("kingman", "FINISHED");
        Log.i("kingman", "          ");
    }

    @Override
    public void onLoaderReset(Loader<Pair<Boolean, String>> loader) {
        Log.i("kingman", "RESET");
        EQAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
