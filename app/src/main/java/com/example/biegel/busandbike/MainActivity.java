package com.example.biegel.busandbike;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Boolean alreadySet = false;
    private Location mLastLocation;
    private LatLng currentCoordinates;

    private Vector<VelohStation> velohStations = new Vector<VelohStation>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        String restURL = "https://developer.jcdecaux.com/rest/vls/stations/Luxembourg.json";
        new RestOperation().execute(restURL);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng ulu = new LatLng(49.626883, 6.159250);
        mMap.addMarker(new MarkerOptions().position(ulu).title("Marker at University of Luxemburg"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ulu, 18));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Done: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            currentCoordinates = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            if (!alreadySet) {
                mMap.addMarker(new MarkerOptions().position(currentCoordinates).title("HOME"));
                alreadySet = true;
            }

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class RestOperation extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String data;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           // progressDialog.setTitle("Please wait...");
            //progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {
            String content = loadJSONFromAsset();
            //System.out.println(content);
            try {
                JSONArray arr = new JSONArray(content);
                for (int i = 0; i< arr.length(); i++){
                    JSONObject obj = arr.getJSONObject(i);
                    int numb = obj.getInt("number");
                    String name = obj.getString("name");
                    String address = obj.getString("address");
                    Double lat = obj.getDouble("latitude");
                    Double lng = obj.getDouble("longitude");
                    VelohStation vs = new VelohStation(numb,name,address,lat,lng);
                    Log.i("OUT",vs.toString());
                    velohStations.addElement(vs);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (velohStations.size() > 1){
                for (int j =0; j< velohStations.size();j++){
                    Log.i("Mark",velohStations.get(j).toString());
                    LatLng pos = new LatLng(velohStations.get(j).getLatitude(),velohStations.get(j).getLongitude());
                    mMap.addMarker(new MarkerOptions().position(pos)
                            .title(velohStations.get(j).getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    );
                }
            }
            //progressDialog.dismiss();

        }

    }


    public String loadJSONFromAsset() {
        String json = "";
        try {

            InputStream is = getAssets().open("Luxembourg.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        Log.i("Out","ending here?");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
