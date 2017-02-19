package com.example.luis.greenmapp;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    public static final int MAX_DPACK_SIZE = 256;
    private String last_search;

    private static String my_ip = "192.168.2.112";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        last_search = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d("onCameraMoveCanceled", "canceled");
            }
        });
//        mMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
//            @Override
//            public void onCameraMoveCanceled() {
//                Log.d("onCameraMoveCanceled", "canceled");
//                if(last_search != null)
//                {
//                    //loadNewLocations();
//                    Toast.makeText(MapsActivity.this, "Move", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(LatLng latLng)
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {

                            JSONObject json = new JSONObject();
                            json.put("type", "pnew");
                            Log.d("log-print","a");
                            DatagramSocket socket_udp = new DatagramSocket();
                            DatagramPacket packet;
                            Log.d("dgbfhj", json.toJSONString());
                            Log.d("dgbfhj", json.toString());
                            packet = new DatagramPacket(json.toJSONString().getBytes(),
                                    json.toJSONString().length(), InetAddress.getByName(my_ip), 5600);
                            socket_udp.send(packet);

                            Socket socket;
                            socket = new Socket(my_ip, 3434);

                            InputStream in = new FileInputStream(new File(getApplicationContext().getFilesDir(), "picture.jpg"));
                            OutputStream out = socket.getOutputStream();
                            //
                            byte[] buf = new byte[8192];
                            int len = 0;
                            while ((len = in.read(buf)) != -1)
                            {
                                out.write(buf, 0, len);
                            }
                            //
                            out.close();
                            in.close();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Toast.makeText(MapsActivity.this, "Long", Toast.LENGTH_SHORT).show();
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
        String location = edtLocation.getText().toString();
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addressList != null) {
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        }
        edtLocation.getText().clear();
    }

    public void loadNewLocations()
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
                    DatagramSocket socket_udp = new DatagramSocket();
                    DatagramPacket packet;
                    packet = new DatagramPacket(json.toJSONString().getBytes(),
                            json.toJSONString().length(), InetAddress.getByName(my_ip), 5600);
                    socket_udp.send(packet);

                    packet = new DatagramPacket(new byte[MAX_DPACK_SIZE], MAX_DPACK_SIZE);
                    socket_udp.receive(packet);

                    input_sock = new String(packet.getData(), 0, packet.getLength());
                    jsonarray = new JSONArray(input_sock);

                    //

                    //show new locations on map

                    for(int x = 0; x < jsonarray.length(); x++)
                    {
                        JSONObject o = ((JSONObject) jsonarray.get(x));
                        Marker mSydney;
                        mSydney = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng((double)o.get("lat"), (double)o.get("long")))
                                .title("Sydney"));
                        mSydney.setTag(0);
                    }

                }
                catch (IOException | JSONException e)
                {
                    e.printStackTrace();
                }
            }

        }).start();
    }


    public void ShowOptions(View view)
    {
        startActivity(new Intent(MapsActivity.this, InformationActivity.class));

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject json = new JSONObject();
                    json.put("type", "see");
                    Log.d("log-print","a");
                    DatagramSocket socket_udp = new DatagramSocket();
                    DatagramPacket packet;
                    Log.d("dgbfhj", json.toJSONString());
                    Log.d("dgbfhj", json.toString());
                    packet = new DatagramPacket(json.toJSONString().getBytes(),
                            json.toJSONString().length(), InetAddress.getByName(my_ip), 5600);
                    socket_udp.send(packet);
                    //
                    Socket socket;
                    socket = new Socket(my_ip, 3434);

                    File file = new File(getApplicationContext().getFilesDir(), "picture.jpg");
                    if(file.exists())
                        file.delete();

                    InputStream in = socket.getInputStream();
                    FileOutputStream out = openFileOutput("picture.jpg", Activity.MODE_PRIVATE);

                    //
                    byte[] buf = new byte[8192];
                    int len = 0;
                    int contador = 0;
                    while ((len = in.read(buf)) != -1)
                    {
                        System.out.println("Recebido o bloco n. " + ++contador + " com " + len + " bytes.");
                        out.write(buf, 0, len);
                        out.flush();
                        System.out.println("Acrescentados " + len + " bytes.");
                    }
                    //
                    out.close();
                    in.close();
                    Log.d("desfgh", "downloaded");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
