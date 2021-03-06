package com.example.luis.greenmapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class InformationActivity extends Activity {

    private Button button;
    public static final int MAX_DPACK_SIZE = 1024;
    private ImageView[] imagens = new ImageView[15];
    private boolean[] existe = new boolean[15];
    private String[][] mensagens = new String[15][2];
    private Integer[][] ids = new Integer[15][2];
    private String[] mensagem = new String[15];
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
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
//        escreveMensagensIcons();
//        button = (Button) findViewById(R.id.sugestao);
//        imagens[0] = (ImageView) findViewById(R.id.icon_wc);
//        imagens[1] = (ImageView) findViewById(R.id.icon_banco);
//        imagens[2] = (ImageView) findViewById(R.id.icon_lixo);
//        imagens[3] = (ImageView) findViewById(R.id.icon_animais);
//        imagens[4] = (ImageView) findViewById(R.id.icon_exercicio);
//        imagens[5] = (ImageView) findViewById(R.id.icon_bicicletas);
//        imagens[6] = (ImageView) findViewById(R.id.icon_rios);
//        imagens[7] = (ImageView) findViewById(R.id.icon_churrasco);
//        imagens[8] = (ImageView) findViewById(R.id.icon_mar);
//        imagens[9] = (ImageView) findViewById(R.id.icon_sombra);
//        imagens[10] = (ImageView) findViewById(R.id.icon_desporto);
//        imagens[11] = (ImageView) findViewById(R.id.icon_cultura);
//        imagens[12]  = (ImageView) findViewById(R.id.icon_fraldario);
//        imagens[13]  = (ImageView) findViewById(R.id.icon_deficientes);
//        imagens[14]  = (ImageView) findViewById(R.id.icon_parquei);
//        validaExistencia();
//        insereMensagensIcons();
//        registaListeners();
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

                    //
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

                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }

                final JSONObject fo = o;

                InformationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ola", fo.toJSONString());
                        Log.d("nome", (String)fo.get("name"));
                        ((TextView)findViewById(R.id.sin)).setText((String)fo.get("name"));

                        int t = 0;

                        LinearLayout last_linear;
                        last_linear = (LinearLayout)findViewById(R.id.first_linear) ;
                        //LinearLayout linear_no_parque;
                        //linear_no_parque = (LinearLayout)findViewById(R.id.no_parque) ;
                        //
                        File imgFile = new File(getApplicationContext().getFilesDir(), "picture.jpg");
                        ArrayList<String> dd = (ArrayList<String>)fo.get("contains");
                        while(t++ < dd.size())
                        {
                            /*if(t%3 == 0)
                            {
                                LinearLayout layout = new LinearLayout(InformationActivity.this);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                                //adicionar
                                linear_no_parque.addView(layout);
                                last_linear = layout;
                            }*/

                            String ss = dd.get(t);
                            ImageView img = new ImageView(InformationActivity.this);
                            img.setImageResource(R.drawable.wc);
                            if(ss.equals(WC)) last_linear.addView(img);
                            img = new ImageView(InformationActivity.this);
                            img.setImageResource(R.drawable.bancos);
                            if(ss.equals(BANCOS)) last_linear.addView(img);
                            img = new ImageView(InformationActivity.this);
                            img.setImageResource(R.drawable.lixo);
                            if(ss.equals(LIXO)) last_linear.addView(img);
                        }

                        if(imgFile.exists()){

                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            ImageView myImage = (ImageView) findViewById(R.id.parq_image);

                            myImage.setImageBitmap(myBitmap);

                        }
                        else
                        {
                            Log.d("fdghj", "no image");
                        }
                    }
                });

            }
        }).start();
    }

//    protected void registaListeners(){
//        for(int i = 0; i < 15; i++)
//            registaListenerImagem(imagens[i], mensagem[i]);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //JDialog com cena...
//            }
//        });
//    }

//    protected void validaExistencia(){
//        existe[0] = true;
//        existe[1] = false;
//        existe[2] = false;
//        existe[3] = false;
//        existe[4] = false;
//        existe[5] = true;
//        existe[6] = true;
//        existe[7] = true;
//        existe[8] = true;
//        existe[9] = true;
//        existe[10] = true;
//        existe[11] = true;
//        existe[12] = true;
//        existe[13] = true;
//        existe[14] = true;
//    }
//
//    protected void insereMensagensIcons(){
//        for(int i = 0; i < 15; i++){
//            if(existe[i]) {
//                mensagem[i] = mensagens[i][1];
//                imagens[i].setImageResource(ids[i][1]);
//            }else {
//                mensagem[i] = mensagens[i][0];
//                imagens[i].setImageResource(ids[i][0]);
//            }
//        }
//    }
//
//    protected void escreveMensagensIcons(){
//        mensagens[0][0] = "Não existe WC";
//        mensagens[0][1] = "Existe WC";
//        ids[0][0] = R.drawable.n_wc;
//        ids[0][1] = R.drawable.wc;
//        mensagens[1][0] = "Não existem Bancos";
//        mensagens[1][1] = "Existem Bancos";
//        ids[1][0] = R.drawable.n_bancos;
//        ids[1][1] = R.drawable.bancos;
//        mensagens[2][0] = "Não existem Caixotes de Lixo";
//        mensagens[2][1] = "Existem Caixotes de Lixo";
//        ids[2][0] = R.drawable.n_lixo;
//        ids[2][1] = R.drawable.lixo;
//        mensagens[3][0] = "Não é permitido Animais";
//        mensagens[3][1] = "É permitido Animais";
//        ids[3][0] = R.drawable.n_animais;
//        ids[3][1] = R.drawable.animais;
//        mensagens[4][0] = "Não existe Zona de Exercicio";
//        mensagens[4][1] = "Existe Zona de Exercicio";
//        ids[4][0] = R.drawable.n_musculacao;
//        ids[4][1] = R.drawable.musculacao;
//        mensagens[5][0] = "Não existe Zona de Bicicletas";
//        mensagens[5][1] = "Existe Zona de Bicicletas";
//        ids[5][0] = R.drawable.n_byke;
//        ids[5][1] = R.drawable.byke;
//        mensagens[6][0] = "Não existe Rio";
//        mensagens[6][1] = "Existe Rio";
//        ids[6][0] = R.drawable.n_rio;
//        ids[6][1] = R.drawable.rio;
//        mensagens[7][0] = "Não Existe Zona de Churrasco";
//        mensagens[7][1] = "Existe Zona de Churrasco";
//        ids[7][0] = R.drawable.n_churrasco;
//        ids[7][1] = R.drawable.churrasco;
//        mensagens[8][0] = "Não existe Mar";
//        mensagens[8][1] = "Existe Mar";
//        ids[8][0] = R.drawable.n_mar;
//        ids[8][1] = R.drawable.mar;
//        mensagens[9][0] = "Não existem Sombras";
//        mensagens[9][1] = "Existem Sombras";
//        ids[9][0] = R.drawable.n_sombra;
//        ids[9][1] = R.drawable.sombra;
//        mensagens[10][0] = "Não existe Zona de Desporto";
//        mensagens[10][1] = "Existe Zona de Desporto";
//        ids[10][0] = R.drawable.n_desporto;
//        ids[10][1] = R.drawable.desporto;
//        mensagens[11][0] = "Não existe Zona Cultural";
//        mensagens[11][1] = "Existe Zona Cultural";
//        ids[11][0] = R.drawable.n_cultura;
//        ids[11][1] = R.drawable.cultura;
//        mensagens[12][0] = "Não existe Fraldario";
//        mensagens[12][1] = "Existe Fraldario";
//        ids[12][0] = R.drawable.n_wc; //Falta Alterar
//        ids[12][1] = R.drawable.fraldario;
//        mensagens[13][0] = "Não existe acesso para deficientes";
//        mensagens[13][1] = "Existe acesso para deficientes";
//        ids[13][0] = R.drawable.n_deficientes;
//        ids[13][1] = R.drawable.deficientes;
//        mensagens[14][0] = "Não existe Parque Infantil";
//        mensagens[14][1] = "Existe Parque Infantil";
//        ids[14][0] = R.drawable.n_parque;
//        ids[14][1] = R.drawable.parquei;
//    }

//    protected void registaListenerImagem(ImageView imagem, final String strt){
//        imagem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showToast(strt);
//            }
//        });
//    }

    protected void showToast(String valor){
        Toast.makeText(InformationActivity.this, valor, Toast.LENGTH_SHORT).show();
    }
}
