package com.example.luis.greenmapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

public class OptionActivity extends Activity {

    public static final String PREFS_NAME = "OPTIONS_HASHMAP";

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

    private HashMap<String, Boolean> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.hide();
        options = new HashMap<>();
        loadOptionsFromSharedPreferences();
        loadSavedOptionImages();
        for (Map.Entry<String, Boolean> entry : options.entrySet()) {
            Log.d("oncreate", entry.getKey() + " " + entry.getValue());
        }
    }

    private void loadSavedOptionImages(){
        if(options.get(WC) == null){
            ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.wc);
            (findViewById(R.id.wc)).setAlpha(0.5f);
            options.put(WC, true);
        }
        else{
            (findViewById(R.id.wc)).setAlpha(1f);
            if(options.get(WC)){
                ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.wc);
                options.put(WC, false);
            }else{
                options.remove(WC);
                ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.n_wc);
            }
        }
    }

    private void finishWithResult() {
        Intent intent = new Intent();
        intent.putExtra("options", options);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void loadOptionsFromSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        for(Map.Entry<String, ?> entry : prefs.getAll().entrySet())
            options.put(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString()));
    }

    private void saveOptionsToSharedPreferences(){
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        for(Map.Entry<String, Boolean> entry : options.entrySet())
            editor.putBoolean(entry.getKey(), entry.getValue());
        editor.apply();
    }

    public void OnFilterParks(View v){
        switch (v.getId()){
            case R.id.btnFilter:
                saveOptionsToSharedPreferences();
                finishWithResult();
                break;
        }
    }

    public void OnChangeImage(View v){

        switch (v.getId()){
            case R.id.wc:
                if(options.get(WC) == null){
                    ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.wc);
                    (findViewById(R.id.wc)).setAlpha(1f);
                    options.put(WC, true);
                }
                else{
                    if(options.get(WC)){
                        ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.n_wc);
                        options.put(WC, false);
                    }else{
                        options.remove(WC);
                        ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.wc);
                        (findViewById(R.id.wc)).setAlpha(0.5f);
                    }
                }
                break;
            case R.id.bancos:
                break;
            case R.id.lixo:
                break;
            case R.id.animais:
                break;
            case R.id.musculacao:
                break;
            case R.id.bicicletas:
                break;
            case R.id.rio:
                break;
            case R.id.churrasco:
                break;
            case R.id.sombra:
                break;
            case R.id.desporto:
                break;
            case R.id.cultura:
                break;
            case R.id.fraldario:
                break;
            case R.id.deficientes:
                break;
            case R.id.parquei:
                break;
        }
    }
}
