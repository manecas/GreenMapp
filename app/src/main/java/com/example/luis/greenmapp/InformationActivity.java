package com.example.luis.greenmapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class InformationActivity extends Activity {

    private Button button;
    public static final int MAX_DPACK_SIZE = 1024;
    private static String my_ip = "192.168.2.112";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        button = (Button) findViewById(R.id.sugestao);

        final Long ref;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                ref= null;
            } else {
                ref= extras.getLong("ref");
            }
        } else {
            ref= (Long) savedInstanceState.getSerializable("ref");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject o = new JSONObject();
                try {

                    JSONObject json = new JSONObject();
                    String input_sock;
                    json.put("type", "see");
                    json.put("ref",ref);
                    DatagramSocket socket_udp = null;
                    socket_udp = new DatagramSocket();
                    DatagramPacket packet;
                    packet = new DatagramPacket(json.toJSONString().getBytes(),
                            json.toJSONString().length(), InetAddress.getByName(my_ip), 5600);
                    socket_udp.send(packet);

                    packet = new DatagramPacket(new byte[MAX_DPACK_SIZE], MAX_DPACK_SIZE);
                    socket_udp.receive(packet);

                    input_sock = new String(packet.getData(), 0, packet.getLength());
                    Log.d("input_sock", input_sock);

                    JSONParser parser = new JSONParser();
                    o = (JSONObject) parser.parse(input_sock);

                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }

                final JSONObject fo = o;

                InformationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.sin)).setText((String)fo.get("name"));
                    }
                });

            }
        }).start();




    }

    protected void registaListeners(){
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //JDialog com cena...
            }
        });
    }
}
