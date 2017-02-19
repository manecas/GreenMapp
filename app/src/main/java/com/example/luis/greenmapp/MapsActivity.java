package com.example.luis.greenmapp;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String PREFS_NAME = "OPTIONS_HASHMAP";

    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    public static final int MAX_DPACK_SIZE = 1024;
    private HashMap<String, Boolean> last_search;
    private static final int ITEMS_REQUEST = 1;
    private ArrayList<Marker> mMarkers;
    private String lastCity = null;

    private static String my_ip = "192.168.2.112";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        last_search = new HashMap<>();
        mMarkers = new ArrayList<>();
        loadOptionsFromSharedPreferences();
    }

    private void loadOptionsFromSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        for(Map.Entry<String, ?> entry : prefs.getAll().entrySet())
            last_search.put(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString()));

        for (Map.Entry<String, Boolean> entry : last_search.entrySet()) {
            Log.d("LoadOnStart", entry.getKey() + " " + entry.getValue());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
        {
            @Override
            public void onCameraIdle()
            {
                Log.d("idle", "idle");
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(LatLng latLng)
            {
                //
                Intent intent = new Intent(MapsActivity.this, SugestionActivity.class);
                intent.putExtra("lat", latLng.latitude);
                intent.putExtra("longi", latLng.longitude);
                startActivity(intent);

                //Toast.makeText(MapsActivity.this, "Long", Toast.LENGTH_SHORT).show();

            }
        });

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        if(ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }else{
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }
        }else{
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try{
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0)
                    loadNewLocations(addresses.get(0).getLocality());

            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(MapsActivity.this, "Location not found!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isConnectingToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your internet seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MapsActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try{
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(MapsActivity.this, "Location not found. Turn your GPS ON!", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_LONG).show();
                }
        }
    }

    public void OnSearchLocation(View v){

        if(!isConnectingToInternet(this)){
            buildAlertMessageNoInternet();
        }

        EditText edtLocation = (EditText) findViewById(R.id.edtLocation);

        if(edtLocation.getText().toString().length() > 0) {

            String location = edtLocation.getText().toString();
            lastCity = location;
            List<Address> addressList = null;
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList != null) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                loadNewLocations(edtLocation.getText().toString());
            }
            //
            edtLocation.getText().clear();
        }
    }

    public void loadNewLocations(final String city)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String input_sock;
                    JSONArray jsonarray;
                    JSONObject json = new JSONObject();
                    json.put("type", "search");
                    json.put("city_name", city);
                    if(last_search.isEmpty())
                    {
                        json.put("wants", null);
                        json.put("nwants", null);
                    }
                    else
                    {
                        ArrayList<String> wants = new ArrayList<>();
                        ArrayList<String> nwants = new ArrayList<>();
                        for(Map.Entry<String, Boolean> entry : last_search.entrySet())
                        {
                            String key = entry.getKey();
                            Boolean value = entry.getValue();

                            if(value)
                            {
                                wants.add(key);
                            }
                            else
                            {
                                nwants.add(key);
                            }
                        }
                        json.put("wants", wants);
                        json.put("nwants", nwants);
                    }
                    DatagramSocket socket_udp = new DatagramSocket();
                    DatagramPacket packet;
                    packet = new DatagramPacket(json.toJSONString().getBytes(),
                            json.toJSONString().length(), InetAddress.getByName(my_ip), 5600);
                    socket_udp.send(packet);

                    packet = new DatagramPacket(new byte[MAX_DPACK_SIZE], MAX_DPACK_SIZE);
                    socket_udp.receive(packet);

                    input_sock = new String(packet.getData(), 0, packet.getLength());
                    jsonarray = new JSONArray(input_sock);

                    //clear all

                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Marker marker: mMarkers)
                            {
                                marker.remove();
                            }
                            mMarkers.clear();
                            mMap.clear();
                        }
                    });

                    //show new locations on map

                    for(int x = 0; x < jsonarray.length(); x++)
                    {
                        JSONParser parser = new JSONParser();
                        final JSONObject o = (JSONObject) parser.parse(jsonarray.get(x).toString());

                        MapsActivity.this.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Marker myNewMarker;
                                myNewMarker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng((double)o.get("lat"), (double)o.get("long")))
                                        .title((String)o.get("name")));
                                myNewMarker.setTag(Long.parseLong(((JSONObject)o.get("ref")).get("$numberLong").toString()));
                            }
                        });
                    }

                }
                catch (IOException | JSONException | ParseException e)
                {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Long ref = (Long) marker.getTag();

        // Check if a click count was set, then display the click count.

        Intent intent = new Intent(MapsActivity.this, InformationActivity.class);
        intent.putExtra("ref", ref);
        startActivity(intent);

        /*if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }*/

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    public void ShowOptions(View view)
    {
        startActivityForResult(new Intent(MapsActivity.this, OptionActivity.class), ITEMS_REQUEST);

//        loadNewLocations();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ITEMS_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    last_search = (HashMap<String, Boolean>) data.getSerializableExtra("options");
                    if (last_search != null){
                        for (Map.Entry<String, Boolean> entry : last_search.entrySet()) {
                            Log.d("hashmap", entry.getKey() + " " + entry.getValue());
                        }
                    }

                    if(lastCity != null){
                        loadNewLocations(lastCity);
                    }
                }catch(ClassCastException e){
                    e.printStackTrace();
                }
                // Do something with the contact here (bigger example below)
            }
        }
    }
}
