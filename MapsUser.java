package com.example.ngouthamkumar.abulance108;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
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
//import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MapsUser extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static GoogleMap mMap;
    private GoogleMap m1Map;
    private GPSTracker gpsTracker;
    private Location mLocation;
 //  double latitude=14.6819, longitude=77.6006;
  double latitude, longitude;
    FirebaseUser user;
    DatabaseReference mdata, mdata1,mdata2;
    Button btn,btn1,btn2,btn3;
    double ulat,ulon,dlat,dlon;
     double distance;
     String temkey;
    double templat,templon;
    LatLng current,driver;
    double lati,longi;
   // String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_user);
       // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mdata = FirebaseDatabase.getInstance().getReference("user");
        mdata1 = FirebaseDatabase.getInstance().getReference("driverlocation");
        btn = findViewById(R.id.signout);
        btn1=findViewById(R.id.Book);
btn2=findViewById(R.id.Short);
btn3=findViewById(R.id.cancel);
btn3.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mdata2 = FirebaseDatabase.getInstance().getReference("driver").child(temkey);
                                mdata2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                                        String user_name = map.get("name");
                                        String ph_no = map.get("phone_no");
                                        String msg = "Hi" +" "+ user_name + " your current booking has been canceled you can complete your trip";
                                        SmsManager manager = SmsManager.getDefault();
                                        manager.sendTextMessage(ph_no, null, msg, null, null);
                                        // manager.sendTextMessage(no, null, msg, null, null);
                                        Toast.makeText(getApplicationContext(), "Cancellation Confirmed", Toast.LENGTH_LONG).show();
                                        Intent next=new Intent(MapsUser.this,MapsUser.class);
                                        startActivity(next);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
btn2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mMap.clear();
        current = new LatLng(ulat, ulon);
        Log.d("Lat",""+ulat+","+ulon);
        mMap.addMarker(new MarkerOptions().position(current).title("user").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mdata1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                int size = (int) dataSnapshot.getChildrenCount();
                while(size!=0) {
                    // for (DataSnapshot f1 :dataSnapshot.getChildren()){
                    dlat = dataSnapshot.child("lat").getValue(Double.class);
                    dlon = dataSnapshot.child("lon").getValue(Double.class);
                    String key = dataSnapshot.getKey();
                    driver = new LatLng(dlat, dlon);
                    double dist = CalculationByDistance(current, driver);
                    if (distance == 0) {
                        distance = dist;
                        //results[0]=0;
                        templat = dlat;
                        templon = dlon;
                        temkey = key;
                    } else {
                        if (dist < distance) {
                            distance = dist;
                            // results[0]=0;
                            templat = dlat;
                            templon = dlon;
                            temkey = key;
                        }
                    }
                    size--;
                }//i = 0; driver = new LatLng(templat, templon);
                mMap.addMarker(new MarkerOptions().position(driver).title("driver").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }
});

        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                mdata.child(user.getUid()).child("lat").setValue(null);
                mdata.child(user.getUid()).child("lon").setValue(null);
                startActivity(new Intent(MapsUser.this, MainActivity.class));
                 current=new LatLng(ulat,ulon);


            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mMap.clear();


                current = new LatLng(ulat, ulon);
                        mMap.addMarker(new MarkerOptions().position(current).title("user").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                 driver = new LatLng(templat, templon);
                mMap.addMarker(new MarkerOptions().position(driver).title("driver").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mdata1.child(temkey).child("userlat").setValue(ulat);
                mdata1.child(temkey).child("userlon").setValue(ulon);
                String url = getRequestUrl(current, driver);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
                mdata2=FirebaseDatabase.getInstance().getReference("driver").child(temkey);
                sendSms();


            }

        });



        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();

       // latitude = mLocation.getLatitude();
       // longitude = mLocation.getLongitude();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void sendSms() {
        mdata2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                 String user_name = map.get("name");
                String ph_no = map.get("phone_no");
               String msg="Hi"+user_name+" you have a new booking please start the booking";


                SmsManager manager=SmsManager.getDefault();
                manager.sendTextMessage(ph_no,null,msg,null,null);
                // manager.sendTextMessage(no, null, msg, null, null);
                Toast.makeText(getApplicationContext(), "Booking Confirmed", Toast.LENGTH_LONG).show();


            }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private String getRequestUrl(LatLng current, LatLng driver) {
        //Value of origin
        String str_org = "origin=" + current.latitude + "," + current.longitude;
        //Value of destination
        String str_dest = "destination=" + driver.latitude + "," + driver.longitude;
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

    @Override
    public void onLocationChanged(Location location) {
        lati=location.getLatitude();
        longi=location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }


    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
            TaskParser taskParser = new TaskParser();
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
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        m1Map=googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMyLocationEnabled(true);

         ulat = Double.parseDouble(String.valueOf(latitude));

         ulon = Double.parseDouble(String.valueOf(longitude));

        mdata.child(user.getUid()).child("lat").setValue(ulat);
        //Toast.makeText(MapsUser.this, ""+user.getUid(), Toast.LENGTH_LONG).show();
        mdata.child(user.getUid()).child("lon").setValue(ulon);
        // Add a marker in Sydney and move the camera
       final LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("I'm here..."));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10.2f));
        mdata1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
double lat= dataSnapshot.child("lat").getValue(Double.class);

double lon= dataSnapshot.child("lon").getValue(Double.class);
                LatLng newLocation = new LatLng(lat,lon);
             double distance=CalculationByDistance(sydney, newLocation);
                m1Map.addMarker(new MarkerOptions()
                        .position(newLocation)
                        .title(distance+dataSnapshot.getKey()))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



    }
    private double CalculationByDistance(LatLng position, LatLng p1) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = position.latitude;
        double lat2 = p1.latitude;
        double lon1 = position.longitude;
        double lon2 = p1.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        //Toast.makeText(this, ""+Radius * c, Toast.LENGTH_SHORT).show();
        return Radius * c;

    }

}
