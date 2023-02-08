package com.c5durango.alertalsm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MensajeActivity extends AppCompatActivity {

    TextView txtFolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState.getString("folio");
        txtFolio = findViewById(R.id.txtFolio);
        txtFolio.setText("#"+savedInstanceState.getString("folio"));
        setContentView(R.layout.activity_mensaje);
    }
}