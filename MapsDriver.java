package com.example.ngouthamkumar.abulance108;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsDriver extends FragmentActivity implements OnMapReadyCallback {
    public  double latitude=14.7452,longitude=77.6896;
    //latitude=14.7452;
   // longitude=77.6896;
    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    //double latitude, longitude;
    FirebaseUser user,user1;
    Button b1,b2;
    DatabaseReference mdata;

    DatabaseReference mdata2;
    LatLng sydney, victim;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_driver);
        //mMap.setLocationSource(E);
        user= FirebaseAuth.getInstance().getCurrentUser();
        user1=FirebaseAuth.getInstance().getCurrentUser();

        mdata2= FirebaseDatabase.getInstance().getReference("driverlocation").child(user1.getUid());
        //double lat= mdata2.child("userlat").getValue();
        mdata= FirebaseDatabase.getInstance().getReference("driverlocation");
        //String msg="Hi"+user_name+" you have a new booking please start the booking";
        // mdata2=FirebaseDatabase.getInstance().getReference("driverlocation");
        btn=findViewById(R.id.logout);
        b1=findViewById(R.id.start);
        b2=findViewById(R.id.completed);
        Toast.makeText(MapsDriver.this, ""+user1.getUid(), Toast.LENGTH_LONG).show();
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MapsDriver.this,MapsDriver.class));

            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdata2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        double lat1=dataSnapshot.child("userlat").getValue(Double.class);
                        //Toast.makeText(MapsDriver.this, ""+lat1, Toast.LENGTH_LONG).show();
                        double lon1=dataSnapshot.child("userlon").getValue(Double.class);
                        //Toast.makeText(MapsDriver.this, ""+lon1, Toast.LENGTH_LONG).show();
                        victim =new LatLng(lat1,lon1);
                        mMap.addMarker(new MarkerOptions()
                                .position(victim)
                                .title(dataSnapshot.getKey()))
                                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        String url = getRequestUrl(victim, sydney);
                        MapsDriver.TaskRequestDirections taskRequestDirections = new MapsDriver.TaskRequestDirections();
                        taskRequestDirections.execute(url);
                        //String msg="Hi"+user_name+" you have a new booking please start the booking";
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                mdata.child(user.getUid()).child("lat").setValue(null);
                mdata.child(user.getUid()).child("lon").setValue(null);
                startActivity(new Intent(MapsDriver.this,MainActivity.class));

            }
        });

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();
        if(mLocation!=null){
           // latitude = mLocation.getLatitude();
            //longitude = mLocation.getLongitude();
            //latitude=14.7452;
        //longitude=77.6896;
            }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private String getRequestUrl(LatLng victim, LatLng sydney) {
        //Value of origin
        String str_org = "origin=" + sydney.latitude + "," + sydney.longitude;
        //Value of destination
        String str_dest = "destination=" + victim.latitude + "," + victim.longitude;
        // String str_middle = "middle=" + middle.latitude + "," + middle.longitude;

        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + "&" + sensor + "&" + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;

    }
    private static String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }


    class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MapsDriver.TaskParser taskParser = new MapsDriver.TaskParser();
            taskParser.execute(s);
        }
    }

    class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {



        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionParser directionsParser = new DirectionParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.GRAY);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
                //Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            } else {
               Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
                //mMap.addPolyline(polylineOptions);
            }

        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double lat= Double.parseDouble(String.valueOf(latitude));
        //Toast.makeText(MapsDriver.this, ""+lat, Toast.LENGTH_LONG).show();
        double lon= Double.parseDouble(String.valueOf(longitude));
        //Toast.makeText(MapsDriver.this, ""+lon, Toast.LENGTH_LONG).show();
        mdata.child(user.getUid()).child("lat").setValue(lat);
        mdata.child(user.getUid()).child("lon").setValue(lon);
        // Add a marker in Sydney and move the camera
        sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("I'm here..."));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10.2f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10.2f));
    }
}
