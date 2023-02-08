package com.c5durango.alertalsm.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.c5durango.alertalsm.Clases.ModeloLogin;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Utilidades.PreferencesUsuario;


public class PerfilFragment extends Fragment {

    TextView txtNombre;
    TextView txtTelefono;
    TextView txtCalle;
    TextView txtNumero;
    TextView txtColonia;
    TextView txtCP;
    TextView txtLocalidad;
    //TextView txt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        txtNombre = view.findViewById(R.id.txtNombrePerfil);
        txtTelefono = view.findViewById(R.id.txtTelefonoPerfil);
        txtCalle = view.findViewById(R.id.txtCallePerfil);
        txtNumero = view.findViewById(R.id.txtNumeroPerfil);
        txtColonia = view.findViewById(R.id.txtColoniaPerfil);
        txtCP = view.findViewById(R.id.txtCPPerfil);
        txtLocalidad = view.findViewById(R.id.txtLocalidadPerfil);

        llenarData();

        return view;
    }

    private void llenarData(){
        ModeloLogin model = PreferencesUsuario.getDatosComercioUsuario(getContext());

         txtNombre.setText(model.getNombres_usuarios_app());
         txtTelefono.setText(model.getTel_movil());
         txtCalle.setText(model.getCalle());
         txtNumero.setText(model.getNumero());
         txtColonia.setText(model.getColonia());
         txtCP.setText(model.getCp()+"");
         txtLocalidad.setText(model.getNombre_localidad());
    }
}