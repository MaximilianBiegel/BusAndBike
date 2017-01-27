package com.example.biegel.busandbike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final float WALKINGRANGE = 500;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Boolean alreadySet = false;
    private Location mLastLocation;
    private LatLng currentCoordinates;
    private boolean checkRadius = false;


    private Vector<Marker> busMarker = new Vector<Marker>();
    private Vector<Marker> velohMarker = new Vector<Marker>();



    private Vector<VelohStation> velohStations = new Vector<VelohStation>() ;
    private Vector<BusStop> busstops = new Vector<BusStop>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkRadius = true;
                } else {
                    checkRadius = false;
                }
            }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        String restURL = "http://travelplanner.mobiliteit.lu/restproxy/departureBoard?accessId=cdt&id=A=1@O=Luxembourg,%20Gare%20Centrale@X=6,133645@Y=49,600670@U=82@L=200405035@B=1@p=1459856195&format=json";     //"https://data.public.lu/en/reuses/all-bus-train-stops/";
        Log.i("switch",restURL);
        new RestOperation().execute(restURL);

    }



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

        GoogleMap.InfoWindowAdapter iwa = new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window,null);
                TextView tvbhl = (TextView) v.findViewById(R.id.lineNumbersHL);
                TextView tvb = (TextView) v.findViewById(R.id.lineNumbers);
                TextView tvdhl = (TextView) v.findViewById(R.id.directionsHL);
                TextView tvd = (TextView) v.findViewById(R.id.directions);

                String stop = marker.getTitle();
              //  new DetailOperation().execute(stop);
                String lines = "";
                String directions = "";

                BusStop currentStop = null;
                //INITIALIZE STOP
                for (int i= 0;i<busstops.size();i++){
                    if(busstops.get(i).getName() == stop){
                        currentStop = busstops.get(i);
                        break;
                    }
                }
                if(currentStop!= null) {
                    Vector<String> stopLines = currentStop.getBuslines();
                    Vector<String> stopDirections = currentStop.getDirections();

                    //ASSEMBLE DIRECTIONS
                    for (int j = 0; j < stopLines.size(); j++) {
                        lines += stopLines.get(j) + ", ";
                    }

                    //ASSEMBLE DIRECTIONS
                    for (int k = 0; k < stopDirections.size(); k++) {
                        directions += stopDirections.get(k) + ", ";
                    }

                }

                tvbhl.setText(stop);
                tvb.setText("BUSLINES:");

                tvdhl.setText("DIRECTIONS");
                tvd.setText(directions);

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };
        mMap.setInfoWindowAdapter(iwa);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(),marker.getTitle(),Toast.LENGTH_LONG);
                Log.i("CHECKER","Klappt das?Vor For");

                //for (int m =0; m< busMarker.size();m++){
                   // if(busMarker.get(m) == marker) {
                        for (int i = 0; i< busstops.size();i++) {
                           // Log.i("CHECKER",busstops.size()+ " das?: "+marker.getTitle().length()+" mit "+busstops.get(i).getName().length());
                            String mt = marker.getTitle();
                            String bsn = busstops.get(i).getName();
                           // if (bsn.contains( "Kirchberg, Uni Campus Kirchberg")){Log.i("alder",bsn);}
                           // if (mt == "Kirchberg, Uni Campus Kirchberg"){Log.i("alderi",mt);}

                            if (mt.contentEquals(bsn)) {
                                int c = busstops.get(i).getCounter();
                                c++;
                                Log.i("CHECKER","Klappt das? "+marker.getTitle()+" mit "+busstops.get(i).getName());
                                busstops.get(i).setCounter(c);
                                Log.i("TAGG","C: "+busstops.get(i).getCounter());
                            }
                        }
                    //}
                //}
                return false;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        }
    }


    public  void showTable(View view){
        Intent i = new Intent(this,TableLayoutActivity.class);
        String[] extra = new String[busstops.size()];
        for (int j =0;j< extra.length;j++){
            extra[j]= busstops.get(j).toString();
        }
        i.putExtra("busStops",extra);
        startActivity(i);
    }
    public void searchNearestBustStop(View view){
        Log.i("BUTTON","BUTTON CLICKED");
        float[] result = new float[1];
        float minDistance = -1;
        int shortestPath=0;

        // USE of UNI LU Coordinates due to me not living in Lux
        LatLng ulu = new LatLng(49.626883, 6.159250);
        Location user = new Location("home");
        for(int i=0;i<busstops.size();i++){//busstops.size()
            Location.distanceBetween(ulu.latitude,ulu.longitude,busstops.get(i).getLatitude(),busstops.get(i).getLongitude(),result);
            if (minDistance == -1){
                minDistance = result[0];
            }
            else if (minDistance > result[0]){
                shortestPath = i;
                minDistance = result[0];

            }
            if (checkRadius == true){
                Log.i("Toggle","Toggled");
                if(result[0] <= WALKINGRANGE){
                    busMarker.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)); //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
            }
            else {
                busMarker.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)); //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            }
        }
        Log.i("Distance",""+result[0]);
        LatLng nearestStop = new LatLng(busstops.get(shortestPath).getLatitude(),busstops.get(shortestPath).getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nearestStop,18));

    }

    public void searchNearestVelohStation(View view){
        Log.i("BUTTON","BUTTON CLICKED");
        float[] result = new float[1];
        float minDistance = -1;
        int shortestPath=0;



        // USE of UNI LU Coordinates due to me not living in Lux
        LatLng ulu = new LatLng(49.626883, 6.159250);
        Location user = new Location("home");
        for(int i=0;i<velohStations.size();i++){//busstops.size()
            Location.distanceBetween(ulu.latitude,ulu.longitude,velohStations.get(i).getLatitude(),velohStations.get(i).getLongitude(),result);
            if (minDistance == -1){
                minDistance = result[0];
            }
            if (minDistance > result[0]){
                shortestPath = i;
                minDistance = result[0];

            }
            if (checkRadius == true){
                Log.i("Toggle","Toggled");
                if(result[0] <= WALKINGRANGE){
                    velohMarker.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
            }
            else {
                velohMarker.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)); //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            }

        }
        Log.i("Distance",""+result[0]);
        LatLng nearestStop = new LatLng(velohStations.get(shortestPath).getLatitude(),velohStations.get(shortestPath).getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearestStop,18));
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                nearestStop, 18);
        mMap.animateCamera(location);
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
            try {
                data += "&" + URLEncoder.encode("data","UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected Void doInBackground(String... params) {
            String content = loadJSONFromAsset("Luxembourg.json");
            String busContent = loadJSONFromAsset("StopList.txt");
            Log.i("BUSOUT",busContent);

            URL url;

            //RETRIEVE BUSSTOPS
            try {

                String[] stops = busContent.split("id=A=1@O=");
                if (stops.length > 1) {
                    for (int i = 1; i < stops.length; i++){
                      //  Log.i("WHAT",stops[i]);
                        // EXTRACT BUS DATA
                        String[] temp = stops[i].split("@X=");
                        String name = temp[0];
                       // Log.i("STOP",temp[0]);
                      //  Log.i("STOP",temp[1]);

                        String[] x = temp[1].split("@Y=");
                      //  Log.i("x0",x[0]);
                        Double longitude = Double.parseDouble(x[0].replace(",","."));
                      //  Log.i("LAT",x[1]);
                        String[] y = x[1].split("@U=");
                        Double latitude = Double.parseDouble(y[0].replace(",","."));

                        BusStop stop = new BusStop(name,latitude,longitude);
                       // Log.i("STOP",stop.toString());
                        busstops.addElement(stop);
                    }
                }
 /*
                JSONArray busarr = new JSONArray(busContent);
                for (int i = 0; i< busarr.length(); i++) {
                    JSONObject obj = busarr.getJSONObject(i);
                }

                for (int i = 0; i< busarr.length(); i++){
                    JSONObject obj = busarr.getJSONObject(i);

                   // VelohStation vs = new VelohStation(numb,name,address,lat,lng);
                    Log.i("BUSOUT",obj.toString());
                }
*/
//              RETRIEVE VELOHSTATIONS
                JSONArray arr = new JSONArray(content);
                for (int i = 0; i< arr.length(); i++){
                    JSONObject obj = arr.getJSONObject(i);
                    int numb = obj.getInt("number");
                    String name = obj.getString("name");
                    String address = obj.getString("address");
                    Double lat = obj.getDouble("latitude");
                    Double lng = obj.getDouble("longitude");
                    VelohStation vs = new VelohStation(numb,name,address,lat,lng);
                  //  Log.i("OUT",vs.toString());
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
                //    Log.i("Mark",velohStations.get(j).toString());

                    LatLng pos = new LatLng(velohStations.get(j).getLatitude(),velohStations.get(j).getLongitude());
                    MarkerOptions m = new MarkerOptions().position(pos)
                            .title(velohStations.get(j).getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    velohMarker.addElement(mMap.addMarker(m));
                }
            }
            if (busstops.size() > 1){
                for (int k = 0; k< busstops.size(); k++){
                    LatLng pos = new LatLng(busstops.get(k).getLatitude(),busstops.get(k).getLongitude());

                    MarkerOptions mo = new MarkerOptions().position(pos)
                            .title(busstops.get(k).getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                    busMarker.addElement(mMap.addMarker(mo));
                }
            }
            //progressDialog.dismiss();

        }

    }


    public String loadJSONFromAsset(String path) {
        String json = "";
        try {

            InputStream is = getAssets().open(path);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

      //  Log.i("Out","ending here?");
          //  Log.i("Out",json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    private class DetailOperation extends AsyncTask<String, Void, Void> {
        String data;
        int buStopIndex;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        @Override
        protected Void doInBackground(String... params) {
            String content = loadJSONFromAsset("LuxembourgBuses.json");
            for (int x =0; x< busstops.size(); x++){
                if(busstops.get(x).getName() == params[0]){
                    buStopIndex = x;
                    break;
                }
            }

            try {
                JSONArray arr = new JSONArray(content);
                if (arr.length()>0){
                    for (int i = 0; i<arr.length();i++){
                        JSONObject obj = arr.getJSONObject(i);
                        if (obj.getString("stop") == busstops.get(buStopIndex).getName()){
                            String line = obj.getString("name");
                            String direction = obj.getString("direction");

                            busstops.get(buStopIndex).addBuslines(line);
                            busstops.get(buStopIndex).addDirections(direction);

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    }
