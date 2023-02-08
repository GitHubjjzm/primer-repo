package com.c5durango.alertalsm.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Utilidades.PreferencesReporte;

public class InicioFragment extends Fragment  implements View.OnClickListener {

    public DataListener callback;

    private ImageButton btnReporteNotificacion;
    private ImageButton btnReporteRapido;

    public View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_inicio, container, false);
        btnReporteNotificacion = root.findViewById(R.id.btnGenerarReporte);
        btnReporteNotificacion.setOnClickListener(this);
        btnReporteRapido = root.findViewById(R.id.btnReporteRapido);
        btnReporteRapido.setOnClickListener(this);
        comprobarColorBoton();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DataListener) context;
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGenerarReporte:
                callback.generarReporteNotificacion();
                break;
            case R.id.btnReporteRapido:
                cambiarIcono("verde");
                callback.generarReporteRapido();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        comprobarColorBoton();
    }
    private void comprobarColorBoton(){
        if(PreferencesReporte.puedeEnviarReporteBool(getContext(), System.currentTimeMillis())){
            cambiarIcono("rojo");
        } else {
            cambiarIcono("verde");
        }
    }

    private void cambiarIcono(String color){
        switch (color){
            case "rojo":
                btnReporteRapido.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_bg_circ_rojo, null));
                btnReporteRapido.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alerta_4_roja, null));
                break;
            case "verde":
                btnReporteRapido.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_bg_circ_verde, null));
                btnReporteRapido.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alerta_4_verde, null));
                break;
            case "naranja":
                btnReporteRapido.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_bg_circ_naranja, null));
                btnReporteRapido.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alerta_4_naranja, null));
                break;
            case "gris":
                btnReporteRapido.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_bg_circ_gris, null));
                btnReporteRapido.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alerta_4_gris, null));
                break;
        }
    }

    public interface DataListener{
        void generarReporteNotificacion();
        void generarReporteRapido();
    }
}