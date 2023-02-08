package com.c5durango.alertalsm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.c5durango.alertalsm.Clases.ModelReportesLocal;
import com.c5durango.alertalsm.DB.DBReportes;

public class InfoReporteActivity extends AppCompatActivity {

    Button btnVolver;
    TextView txtIDReporte;
    TextView txtEstatus;
    TextView txtFecha;
    TextView txtLugar;
    TextView txtCoordenadas;
    TextView txtTipoIncidente;
    TextView txtSubtipo;
    TextView txtVictima;
    TextView txtDescripcion;
    TextView txtImagenes;
    TextView txtAudio;
    TextView txtVideo;
    ImageView imageLugar;

    private int id_reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_reporte);
        btnVolver = findViewById(R.id.btnCancelarInfo);

        txtIDReporte = findViewById(R.id.txtIDReporteInfo);
        txtEstatus = findViewById(R.id.txtEstatusInfo);
        txtFecha = findViewById(R.id.txtFechaInfo);
        txtLugar = findViewById(R.id.txtLugarInfo);
        txtCoordenadas = findViewById(R.id.txtCoordenadasInfo);
        txtTipoIncidente = findViewById(R.id.txtIncidenteInfo);
        txtSubtipo = findViewById(R.id.txtSubtipoInfo);
        txtVictima = findViewById(R.id.txtVictimasInfo);
        txtDescripcion = findViewById(R.id.txtDescripcionInfo);
        txtImagenes = findViewById(R.id.txtNumImaInfo);
        txtAudio = findViewById(R.id.txtNumAudInfo);
        txtVideo = findViewById(R.id.txtNumVideoInfo);
        imageLugar = findViewById(R.id.imageLugar);
        if(getIntent().getExtras().containsKey("id_reporte")){
            id_reporte = getIntent().getExtras().getInt("id_reporte");
        } else {
            Toast.makeText(getApplicationContext(), "No se pudo leer el reporte.", Toast.LENGTH_LONG).show();
            finish();
        }
        llenarCampos(id_reporte);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void llenarCampos(int id_reporte){
        DBReportes dbReportes = new DBReportes(getApplicationContext());
        ModelReportesLocal reporte = dbReportes.mostrarReporte(id_reporte);
        if(reporte == null){
            Toast.makeText(getApplicationContext(), "No se pudo leer el reporte", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        txtIDReporte.setText("" + reporte.getId_reporte());
        txtEstatus.setText(reporte.getEstatus());
        txtFecha.setText(reporte.getFecha_hora());
        txtLugar.setText(reporte.getLugar());
        txtCoordenadas.setText(reporte.getLatitud() + ", " + reporte.getLongitud());
        txtTipoIncidente.setText(reporte.getIncidente());
        txtSubtipo.setText(reporte.getSubtipo());
        txtVictima.setText(reporte.getVictimas());
        txtDescripcion.setText(reporte.getDescripcion());
        txtImagenes.setText(reporte.getImagenes()+"");
        txtAudio.setText(reporte.getAudio()+"");
        txtVideo.setText(reporte.getVideo()+"");
        switch (reporte.getLugar()){
            case Constantes.LUGAR_CASA:
                imageLugar.setImageResource(R.drawable.ic_home);
                break;
            case Constantes.LUGAR_ACTUAL:
                imageLugar.setImageResource(R.drawable.ic_marker);
                break;
            case Constantes.LUGAR_GOOGLE:
                imageLugar.setImageResource(R.drawable.ic_maps);
                break;
        }
    }
}