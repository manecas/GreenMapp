package com.example.luis.greenmapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class OptionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.hide();
    }


    public void OnChangeImage(View v){

        switch (v.getId()){
            case R.id.wc:
                ((ImageView)findViewById(R.id.wc)).setImageResource(R.drawable.n_wc);
                break;
            case R.id.bancos:
                break;
        }
    }
}
