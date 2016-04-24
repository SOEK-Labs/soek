package com.soek.ponnex.angelhack;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

/**
 * Created by Jimbo Alvarez on 4/24/2016.
 */
public class ScanningActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;
    private RippleBackground rippleBackground;
    private boolean isShowing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_le);

        rippleBackground = (RippleBackground)findViewById(R.id.content_ripple);
        rippleBackground.startRippleAnimation();

        setmBeaconManager();

        Log.e("ScanningActivity", "CREATED");
    }

    private void setmBeaconManager() {
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24")); //working
        mBeaconManager.setRangeNotifier(this);

        mBeaconManager.bind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
        Log.e("ScanningActivity", "STOP");
    }


    @Override
    public void onBeaconServiceConnect() {
        new JSONParse().execute();
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        if (beacons.size() > 0) {
            final Beacon inRangeBeacon = beacons.iterator().next();

            Log.e("inRangeBeacon", inRangeBeacon.getId3().toString());

            SharedPreferences product = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = product.edit();
            editor.putString("detected_beacon_minor", inRangeBeacon.getId3().toString());
            editor.apply();

            if(!isShowing) {
                isShowing = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!inRangeBeacon.getId3().toString().equals("1")) showDialog();
                        Log.e("runOnUiThread", "we are here");
                        Log.e("runOnUiThread", isShowing + "");
                    }
                });

            }
        }
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String urlString = "http://192.168.2.101:3000/db";

            try{
                JSONObject jsonObject = getJSONObjectFromURL(urlString);

                String shirt_minor = "", shoes_minor = "", bags_minor = "", shirt_desc = "" ,shoes_desc = "", bags_desc = "", shirt_brand = "",
                        shoes_brand = "", bags_brand = "", shirt_price = "", shoes_price = "", bags_price = "", shirt_quantity = "",
                        shoes_quantity = "", bags_quantity = "";
                JSONArray jsonMainArr = jsonObject.getJSONArray("shirt");
                for (int i = 0; i < jsonMainArr.length(); i++) {
                    JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                    shirt_minor = childJSONObject.getString("minor");
                    shirt_desc = childJSONObject.getString("desc");
                    shirt_brand = childJSONObject.getString("brand");
                    shirt_price = childJSONObject.getString("price");
                    shirt_quantity = childJSONObject.getString("quantity");
                }

                JSONArray jsonMainArr1 = jsonObject.getJSONArray("shoes");
                for (int i = 0; i < jsonMainArr1.length(); i++) {
                    JSONObject childJSONObject = jsonMainArr1.getJSONObject(i);
                    shoes_minor = childJSONObject.getString("minor");
                    shoes_desc = childJSONObject.getString("desc");
                    shoes_brand = childJSONObject.getString("brand");
                    shoes_price = childJSONObject.getString("price");
                    shoes_quantity = childJSONObject.getString("quantity");
                }

                JSONArray jsonMainArr2 = jsonObject.getJSONArray("bags");
                for (int i = 0; i < jsonMainArr2.length(); i++) {
                    JSONObject childJSONObject = jsonMainArr2.getJSONObject(i);
                    bags_minor = childJSONObject.getString("minor");
                    bags_desc = childJSONObject.getString("desc");
                    bags_brand = childJSONObject.getString("brand");
                    bags_price = childJSONObject.getString("price");
                    bags_quantity = childJSONObject.getString("quantity");
                }

                Log.e("ScanningActivity", shirt_minor + "," + shoes_minor + "," + bags_minor);

                SharedPreferences product = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = product.edit();

                editor.putString("shirt_minor", shirt_minor);
                editor.apply();

                editor.putString("shoes_minor", shoes_minor);
                editor.apply();

                editor.putString("bags_minor", bags_minor);
                editor.apply();

                editor.putString("shirt_desc", shirt_desc);
                editor.apply();

                editor.putString("shoes_desc", shoes_desc);
                editor.apply();

                editor.putString("bags_desc", bags_desc);
                editor.apply();

                editor.putString("shirt_brand", shirt_brand);
                editor.apply();

                editor.putString("shoes_brand", shoes_brand);
                editor.apply();

                editor.putString("bags_brand", bags_brand);
                editor.apply();

                editor.putString("shirt_price", shirt_price);
                editor.apply();

                editor.putString("shoes_price", shoes_price);
                editor.apply();

                editor.putString("bags_price", bags_price);
                editor.apply();

                editor.putString("shirt_quantity", shirt_quantity);
                editor.apply();

                editor.putString("shoes_quantity", shoes_quantity);
                editor.apply();

                editor.putString("bags_quantity", bags_quantity);
                editor.apply();

            } catch (IOException e) {
                Log.e("ScanningActivity", e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ScanningActivity", e.toString());
            }

            return null;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                mBeaconManager.startRangingBeaconsInRegion(new Region("detected_beacon", null, null, null));
            } catch (RemoteException e) {
                Log.e("ScanningActivity", "Stop scan beacon problem", e);
            }
        }
    }


    public void showDialog() {
        final Dialog dialog = new Dialog(ScanningActivity.this);
        dialog.setContentView(R.layout.dialog_layout);

        SharedPreferences mSharedPreference2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String detected_beacon = (mSharedPreference2.getString("detected_beacon_minor", "0"));
        String shirt_minor = (mSharedPreference2.getString("shirt_minor", "3"));
        String shoes_minor = (mSharedPreference2.getString("shoes_minor", "2"));
        String bags_minor = (mSharedPreference2.getString("bags_minor", "4"));

        String shirt_desc = (mSharedPreference2.getString("shirt_desc", "Lorem Ipsum"));
        String shoes_desc = (mSharedPreference2.getString("shoes_desc", "Lorem Ipsum"));
        String bags_desc = (mSharedPreference2.getString("bags_desc", "Lorem Ipsum"));

        String shirt_brand = (mSharedPreference2.getString("shirt_brand", "Brand X"));
        String shoes_brand = (mSharedPreference2.getString("shoes_brand", "Brand Y"));
        String bags_brand = (mSharedPreference2.getString("bags_brand", "Brand Z"));

        String shirt_price = (mSharedPreference2.getString("shirt_price", "0"));
        String shoes_price = (mSharedPreference2.getString("shoes_price", "0"));
        String bags_price = (mSharedPreference2.getString("bags_price", "0"));

        String shirt_quantity = (mSharedPreference2.getString("shirt_quantity", ""));
        String shoes_quantity = (mSharedPreference2.getString("shoes_quantity", ""));
        String bags_quantity = (mSharedPreference2.getString("bags_quantity", ""));

        Log.e("Detected Beacon", detected_beacon);

        String title = "TITLE";

        if (detected_beacon.equals(shoes_minor)) {
            dialog.setTitle(detected_beacon);

            TextView text_price = (TextView) dialog.findViewById(R.id.text_price);
            text_price.setText("Php" + shoes_price);

            TextView text_quantity = (TextView) dialog.findViewById(R.id.text_quantity);
            text_quantity.setText("Qty: " + shoes_quantity);

            TextView text_title = (TextView) dialog.findViewById(R.id.text_title);
            text_title.setText(shoes_brand);

            TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText(shoes_desc);

            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            image.setImageResource(R.drawable.shoes);
        }

        if (detected_beacon.equals(shirt_minor)) {
            dialog.setTitle(detected_beacon);

            TextView text_price = (TextView) dialog.findViewById(R.id.text_price);
            text_price.setText("Php " + shirt_price);

            TextView text_quantity = (TextView) dialog.findViewById(R.id.text_quantity);
            text_quantity.setText("Qty:" + shirt_quantity);

            TextView text_title = (TextView) dialog.findViewById(R.id.text_title);
            text_title.setText(shirt_brand);

            TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText(shirt_desc);

            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            image.setImageResource(R.drawable.shirt);
        }

        if (detected_beacon.equals(bags_minor)) {
            dialog.setTitle(detected_beacon);

            TextView text_price = (TextView) dialog.findViewById(R.id.text_price);
            text_price.setText("Php " + bags_price);

            TextView text_quantity = (TextView) dialog.findViewById(R.id.text_quantity);
            text_quantity.setText("Qty:" + bags_quantity);

            TextView text_title = (TextView) dialog.findViewById(R.id.text_title);
            text_title.setText(bags_brand);

            TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText(bags_desc);

            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            image.setImageResource(R.drawable.bags);
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.button_accept);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isShowing = false;
                isShowing = false;

                Log.e("runOnUiThread", isShowing + "");
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

}
