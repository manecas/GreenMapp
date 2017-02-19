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
                    ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.c_wc);
                    options.put(WC, true);
                }
                else{
                    if(options.get(WC)){
                        ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.n_wc);
                        options.put(WC, false);
                    }else{
                        options.remove(WC);
                        ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.wc);
                    }
                }
                break;
            case R.id.bancos:
                if(options.get(BANCOS) == null){
                    ((ImageView)findViewById(R.id.bancos)).setImageResource(R.drawable.c_bancos);
                    options.put(BANCOS, true);
                }
                else{
                    if(options.get(BANCOS)){
                        ((ImageView)findViewById(R.id.bancos)).setImageResource(R.drawable.n_bancos);
                        options.put(BANCOS, false);
                    }else{
                        options.remove(BANCOS);
                        ((ImageView)findViewById(R.id.bancos)).setImageResource(R.drawable.bancos);
                    }
                }
                break;
            case R.id.lixo:
                if(options.get(LIXO) == null){
                    ((ImageView)findViewById(R.id.lixo)).setImageResource(R.drawable.c_lixo);
                    options.put(LIXO, true);
                }
                else{
                    if(options.get(LIXO)){
                        ((ImageView)findViewById(R.id.lixo)).setImageResource(R.drawable.n_lixo);
                        options.put(LIXO, false);
                    }else{
                        options.remove(LIXO);
                        ((ImageView)findViewById(R.id.lixo)).setImageResource(R.drawable.lixo);
                    }
                }
                break;
            case R.id.animais:
                if(options.get(ANIMAIS) == null){
                    ((ImageView)findViewById(R.id.animais)).setImageResource(R.drawable.c_animais);
                    options.put(ANIMAIS, true);
                }
                else{
                    if(options.get(ANIMAIS)){
                        ((ImageView)findViewById(R.id.animais)).setImageResource(R.drawable.n_animais);
                        options.put(ANIMAIS, false);
                    }else{
                        options.remove(ANIMAIS);
                        ((ImageView)findViewById(R.id.animais)).setImageResource(R.drawable.animais);
                    }
                }
                break;
            case R.id.musculacao:
                if(options.get(MUSCULACAO) == null){
                    ((ImageView)findViewById(R.id.musculacao)).setImageResource(R.drawable.c_musculacao);
                    options.put(MUSCULACAO, true);
                }
                else{
                    if(options.get(MUSCULACAO)){
                        ((ImageView)findViewById(R.id.musculacao)).setImageResource(R.drawable.n_musculacao);
                        options.put(MUSCULACAO, false);
                    }else{
                        options.remove(MUSCULACAO);
                        ((ImageView)findViewById(R.id.musculacao)).setImageResource(R.drawable.musculacao);
                    }
                }
                break;
            case R.id.bicicletas:
                if(options.get(BICICLETAS) == null){
                    ((ImageView)findViewById(R.id.bicicletas)).setImageResource(R.drawable.c_byke);
                    options.put(BICICLETAS, true);
                }
                else{
                    if(options.get(BICICLETAS)){
                        ((ImageView)findViewById(R.id.bicicletas)).setImageResource(R.drawable.n_byke);
                        options.put(BICICLETAS, false);
                    }else{
                        options.remove(BICICLETAS);
                        ((ImageView)findViewById(R.id.bicicletas)).setImageResource(R.drawable.byke);
                    }
                }
                break;
            case R.id.rio:
                if(options.get(RIO) == null){
                    ((ImageView)findViewById(R.id.rio)).setImageResource(R.drawable.c_rio);
                    options.put(RIO, true);
                }
                else{
                    if(options.get(RIO)){
                        ((ImageView)findViewById(R.id.rio)).setImageResource(R.drawable.n_rio);
                        options.put(RIO, false);
                    }else{
                        options.remove(RIO);
                        ((ImageView)findViewById(R.id.rio)).setImageResource(R.drawable.rio);
                    }
                }
                break;
            case R.id.mar:
                if(options.get(MAR) == null){
                    ((ImageView)findViewById(R.id.mar)).setImageResource(R.drawable.c_mar);
                    options.put(MAR, true);
                }
                else{
                    if(options.get(MAR)){
                        ((ImageView)findViewById(R.id.mar)).setImageResource(R.drawable.n_mar);
                        options.put(MAR, false);
                    }else{
                        options.remove(MAR);
                        ((ImageView)findViewById(R.id.mar)).setImageResource(R.drawable.mar);
                    }
                }
                break;
            case R.id.churrasco:
                if(options.get(CHURRASCO) == null){
                    ((ImageView)findViewById(R.id.churrasco)).setImageResource(R.drawable.c_churrasco);
                    options.put(CHURRASCO, true);
                }
                else{
                    if(options.get(CHURRASCO)){
                        ((ImageView)findViewById(R.id.churrasco)).setImageResource(R.drawable.n_churrasco);
                        options.put(CHURRASCO, false);
                    }else{
                        options.remove(CHURRASCO);
                        ((ImageView)findViewById(R.id.churrasco)).setImageResource(R.drawable.churrasco);
                    }
                }
                break;
            case R.id.sombra:
                if(options.get(SOMBRA) == null){
                    ((ImageView)findViewById(R.id.sombra)).setImageResource(R.drawable.c_sombra);
                    options.put(SOMBRA, true);
                }
                else{
                    if(options.get(SOMBRA)){
                        ((ImageView)findViewById(R.id.sombra)).setImageResource(R.drawable.n_sombra);
                        options.put(SOMBRA, false);
                    }else{
                        options.remove(SOMBRA);
                        ((ImageView)findViewById(R.id.sombra)).setImageResource(R.drawable.sombra);
                    }
                }
                break;
            case R.id.desporto:
                if(options.get(DESPORTO) == null){
                    ((ImageView)findViewById(R.id.desporto)).setImageResource(R.drawable.c_desporto);
                    options.put(DESPORTO, true);
                }
                else{
                    if(options.get(DESPORTO)){
                        ((ImageView)findViewById(R.id.desporto)).setImageResource(R.drawable.n_desporto);
                        options.put(DESPORTO, false);
                    }else{
                        options.remove(DESPORTO);
                        ((ImageView)findViewById(R.id.desporto)).setImageResource(R.drawable.desporto);
                    }
                }
                break;
            case R.id.cultura:
                if(options.get(CULTURA) == null){
                    ((ImageView)findViewById(R.id.cultura)).setImageResource(R.drawable.c_cultura);
                    options.put(CULTURA, true);
                }
                else{
                    if(options.get(CULTURA)){
                        ((ImageView)findViewById(R.id.cultura)).setImageResource(R.drawable.n_cultura);
                        options.put(CULTURA, false);
                    }else{
                        options.remove(CULTURA);
                        ((ImageView)findViewById(R.id.cultura)).setImageResource(R.drawable.cultura);
                    }
                }
                break;
            case R.id.fraldario:
                if(options.get(FRALDARIO) == null){
                    ((ImageView)findViewById(R.id.fraldario)).setImageResource(R.drawable.c_wcinfantil);
                    options.put(FRALDARIO, true);
                }
                else{
                    if(options.get(FRALDARIO)){
//                        ((ImageView)findViewById(R.id.fraldario)).setImageResource(R.drawable.);
                        options.put(FRALDARIO, false);
                    }else{
                        options.remove(FRALDARIO);
                        ((ImageView)findViewById(R.id.fraldario)).setImageResource(R.drawable.wcinfantil);
                    }
                }
                break;
            case R.id.deficientes:
                if(options.get(DEFICIENTES) == null){
                    ((ImageView)findViewById(R.id.deficientes)).setImageResource(R.drawable.c_deficientes);
                    options.put(DEFICIENTES, true);
                }
                else{
                    if(options.get(DEFICIENTES)){
                        ((ImageView)findViewById(R.id.deficientes)).setImageResource(R.drawable.n_deficientes);
                        options.put(DEFICIENTES, false);
                    }else{
                        options.remove(DEFICIENTES);
                        ((ImageView)findViewById(R.id.deficientes)).setImageResource(R.drawable.deficientes);
                    }
                }
                break;
            case R.id.parquei:
                if(options.get(PARQUE_INFANTIL) == null){
                    ((ImageView)findViewById(R.id.parquei)).setImageResource(R.drawable.c_parque);
                    options.put(PARQUE_INFANTIL, true);
                }
                else{
                    if(options.get(PARQUE_INFANTIL)){
                        ((ImageView)findViewById(R.id.parquei)).setImageResource(R.drawable.n_parque);
                        options.put(PARQUE_INFANTIL, false);
                    }else{
                        options.remove(PARQUE_INFANTIL);
                        ((ImageView)findViewById(R.id.parquei)).setImageResource(R.drawable.parquei);
                    }
                }
                break;
        }
    }
}
