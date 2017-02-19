package com.example.luis.greenmapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class SugestionActivity extends Activity {

    private int PICK_IMAGE_REQUEST = 1;

    public static final int MAX_DPACK_SIZE = 1024;
    private static String my_ip = "192.168.2.112";

    public static final String WC = "WC";
    public static final String BANCOS = "BANCOS";
    public static final String LIXO = "LIXO";
    public static final String ANIMAIS = "ANIMAIS";
    public static final String MUSCULACAO = "MUSCULACAO";
    public static final String BICICLETAS = "BICICLETAS";
    public static final String RIO = "RIO";
    public static final String CHURRASCO = "CHURRASCO";
    public static final String MAR = "MAR";
    public static final String SOMBRA = "SOMBRA";
    public static final String DESPORTO = "DESPORTO";
    public static final String CULTURA = "CULTURA";
    public static final String FRALDARIO = "FRALDARIO";
    public static final String DEFICIENTES = "DEFICIENTES";
    public static final String PARQUE_INFANTIL = "PARQUE_INFANTIL";

    Double lat;
    Double longi;

    Uri uri_image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugestion);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                lat= null;
                longi = null;
            } else {
                lat= extras.getDouble("lat");
                longi= extras.getDouble("longi");
            }
        } else {
            lat= (Double) savedInstanceState.getSerializable("lat");
            longi= (Double) savedInstanceState.getSerializable("longi");
        }
    }

    public void ClickAlterarImagem(View v)
    {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void EnviarSugestao(View v)
    {

        Toast.makeText(SugestionActivity.this, "Carregando imagem ... Aguarde", Toast.LENGTH_SHORT).show();

        if(((EditText) findViewById(R.id.et_nome)).getText().toString().length() == 0)
        {
            Toast.makeText(SugestionActivity.this, "É necessário definir um nome!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(((EditText) findViewById(R.id.et_cidade)).getText().toString().length() == 0)
        {
            Toast.makeText(SugestionActivity.this, "É necessário definir uma cidade!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(uri_image == null)
        {
            Toast.makeText(SugestionActivity.this, "É necessário definir uma imagem!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String nome = ((EditText) findViewById(R.id.et_nome)).getText().toString();
                    String cidade = ((EditText) findViewById(R.id.et_cidade)).getText().toString();
                    //String contains_o = ((EditText) findViewById(R.id.et_contem)).getText().toString();

                    ArrayList<String> contem = new ArrayList<>();
                    if(((Switch)findViewById(R.id.swc)).isChecked()) contem.add(WC);
                    if(((Switch)findViewById(R.id.sbancos)).isChecked()) contem.add(BANCOS);
                    if(((Switch)findViewById(R.id.sclixo)).isChecked()) contem.add(LIXO);
                    if(((Switch)findViewById(R.id.spanimais)).isChecked()) contem.add(ANIMAIS);

                    JSONObject json;
                    DatagramSocket socket_udp;
                    DatagramPacket packet;

                    //String[] marr = contains_o.split(",");

                    json = new JSONObject();
                    json.put("type", "new");
                    json.put("name", nome);
                    json.put("city", cidade);
                    json.put("lat", lat);
                    json.put("long", longi);
                    json.put("contains", Arrays.asList(contem));//Arrays.asList(marr));

                    socket_udp = new DatagramSocket();
                    packet = new DatagramPacket(json.toJSONString().getBytes(),
                            json.toJSONString().length(), InetAddress.getByName(my_ip), 5600);
                    socket_udp.send(packet);

                    //

                    Socket socket;
                    socket = new Socket(my_ip, 3434);

                    InputStream in = getContentResolver().openInputStream(uri_image);//new  FileInputStream(new File(uri_image.getPath()));
                    OutputStream out = socket.getOutputStream();
                    //
                    byte[] buf = new byte[8192];
                    int len = 0;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    //
                    out.close();
                    in.close();


                    SugestionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SugestionActivity.this, "Terminado!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri_image = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_image);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.img_parque);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
