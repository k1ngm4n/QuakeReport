package com.example.android.quakereport;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


/**
 * Created by KingMan on 31-Jul-17.
 */

public class EqUtils {

    public static final String LOG_TAG = EqUtils.class.getSimpleName();

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    public static Pair<Boolean, String> getJsonFromApi(String reqUrl) {
        String returnMsg = "";
        boolean flagStatus = false;

        URL url = createUrl(reqUrl);

        if (url != null) {
            try {
                Pair<Boolean, String> result = makeHttpRequest(url);
                flagStatus = result.first;
                returnMsg = result.second;
            } catch (Exception e) {
                Log.e("kingman", "Error making the call", e);
                flagStatus = false;
                returnMsg = e.getMessage();
            }
        }

        //if flagError = false, then the returnMsg will contain the errorMessage to display to the main UI
        //if flagError = true, then it will contain JsonResult
        return Pair.create(flagStatus, returnMsg);
    }

    private static Pair<Boolean, String> makeHttpRequest(URL url) throws IOException, TimeoutException {
        String returnMSg = "";
        boolean flagStatus = false;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            Log.i("kingman", "openConn");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000 /* milliseconds */);
            urlConnection.setConnectTimeout(10000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            Log.i("kingman", "start connect");
            urlConnection.connect();
            Log.i("kingman", "connected");

            if (urlConnection.getResponseCode() == 200) {
                Log.i("kingman", "start read from stream");
                flagStatus = true;
                inputStream = urlConnection.getInputStream();
                returnMSg = readFromStream(inputStream);
                Log.i("kingman", "finish read from stream");
            } else {
                int errCode = urlConnection.getResponseCode();
                Log.i("kingman", "Error response code: " + errCode);
                flagStatus = false;
                returnMSg = "Error response code: " + errCode;
            }
        } catch (SocketTimeoutException e) {
            Log.i ("kingman", "TIMEOUTTTTTTTTTTTT" + e);
            flagStatus = false;
            returnMSg = e.getMessage();
        } catch (IOException e) {
            Log.i ("kingman", "json retriving error " + e);
            flagStatus = false;
            returnMSg = e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            Log.i ("kingman", "finish makeHttpRequest");
        }
        return Pair.create(flagStatus, returnMSg);
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<EarthquakeDataClass> getEqArrayData(String jsonStr) {

        ArrayList<EarthquakeDataClass> eq = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonStr);

            JSONArray eqData = root.getJSONArray("features");

            for (int i = 0; i < eqData.length(); i++) {
                JSONObject eqDetail = eqData.getJSONObject(i);

                JSONObject eqDetailProp = eqDetail.getJSONObject("properties");

                String location = eqDetailProp.getString("place");

                Double magnitude = eqDetailProp.getDouble("mag");

                long datetime = eqDetailProp.getLong("time");

                String url = eqDetailProp.getString("url") + "#map";

                eq.add(new EarthquakeDataClass(location, magnitude, datetime, url));
            }
        } catch (JSONException ex) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", ex);
            eq = null;
        }

        return eq;
    }
}
