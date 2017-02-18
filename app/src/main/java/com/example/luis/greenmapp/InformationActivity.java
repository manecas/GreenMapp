package com.example.luis.greenmapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InformationActivity extends Activity {

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        button = (Button) findViewById(R.id.sugestao);
    }

    protected void registaListeners(){
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //JDialog com cena...
            }
        });
    }
}
