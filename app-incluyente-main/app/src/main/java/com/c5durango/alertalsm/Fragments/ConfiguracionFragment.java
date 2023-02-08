package com.c5durango.alertalsm.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Servicios.NotificacionService;
import com.c5durango.alertalsm.Utilidades.PreferencesCiclo;
import com.c5durango.alertalsm.Utilidades.Utilidades;

public class ConfiguracionFragment extends Fragment implements View.OnClickListener {

    /*
    * Se encarga de crear y destruir el servicio que mantiene la notificación persistente
    * necesaria para escuchar cuando se presiona 3 veces seguidas el botón de bloqueo.
    * */

    private Switch switchServicioActivo;
    private View root;
    private Boolean isActive = false;
    private ImageButton btnSaveCiclo;
    private EditText txtNoCiclo;
    boolean conf_nueva = false;
    private Boolean create = false;

    // Permisos
    private TextView txtDescAlertaRapida;
    private ImageView imgPermisos;
    private Button btnModificarPermiso;
    private int inicio = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_configuracion, container, false);
        btnSaveCiclo = root.findViewById(R.id.iBtnGuardarCiclo);
        btnSaveCiclo.setOnClickListener(this);
        txtNoCiclo = root.findViewById(R.id.txtNoCiclo);
        switchServicioActivo = root.findViewById(R.id.switchServicioActivo);
        switchServicioActivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if(!create)
                        mostrarDivulgacion();
                } else {
                    detenerServicioPersistente();
                    conf_nueva = false;
                }
            }
        });

        txtDescAlertaRapida = root.findViewById(R.id.txtAlertaRapida);
        imgPermisos = root.findViewById(R.id.imgAlertaRapida);
        btnModificarPermiso = root.findViewById(R.id.btnModificarAlertaRapida);
        btnModificarPermiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (inicio){
                    case 0:
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.MY_PERMISSIONS_REQUEST_UBICAC);
                        break;
                    case 1:
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constantes.MY_PERMISSIONS_REQUEST_UBICAC_BACK);
                        break;
                }
            }
        });

        obtenerPreferenciasNotificacion();
        obtenerPreferenciasCiclo();
        inicio = validarPermisos();
        return root;
    }

    private int validarPermisos(){
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            txtDescAlertaRapida.setText("Para enviar el reporte lo mas completo es necesario nos permitas acceso a tu ubicación.\nUnicamente accederemos a ella si se genera una alerta, de lo contrario no.");
            txtDescAlertaRapida.setTextSize(18);
            txtDescAlertaRapida.setTextColor(getResources().getColor(R.color.colorAzulCGob));
            imgPermisos.setVisibility(View.GONE);
            btnModificarPermiso.setText("Dar permiso");
            btnModificarPermiso.setVisibility(View.VISIBLE);
            return 0;
        } else if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            txtDescAlertaRapida.setText("Por ultimo, para enviar el reporte lo mas completo es necesario nos permitas el acceso todo el tiempo a tu ubicación (como se muestra en la imagen).\nAunque unicamente accederemos a ella si se genera una alerta, de lo contrario no.");
            txtDescAlertaRapida.setTextSize(16);
            txtDescAlertaRapida.setTextColor(getResources().getColor(R.color.colorRojoClaro));
            imgPermisos.setVisibility(View.VISIBLE);
            btnModificarPermiso.setText("Modificar permiso");
            btnModificarPermiso.setVisibility(View.VISIBLE);
            return 1;
        } else {
            txtDescAlertaRapida.setVisibility(View.GONE);
            imgPermisos.setVisibility(View.GONE);
            btnModificarPermiso.setVisibility(View.GONE);
            return 2;
        }
    }


    private void iniciarServicioPersistente(){
        try {
            Intent notificationIntent = new Intent(getContext(), NotificacionService.class);
            notificationIntent.putExtra("padre", "App");
            getContext().startService(notificationIntent);

            conf_nueva = true;
            create = true;
            isActive = Utilidades.isMyServiceRunning(getContext(), NotificacionService.class);

            // NotificacionService --> Clase que escucha cuando se presiona 3 veces el botón de bloqueo.
            switchServicioActivo.setChecked(Utilidades.isMyServiceRunning(getContext(), NotificacionService.class));
            // Guardar que esta activado el servicio.
            actualizarPreferenciasNotificacion(isActive);
        } catch (Exception io){
            Toast.makeText(getContext(), "¡Error al actualizar los datos locales!", Toast.LENGTH_LONG).show();
        }
    }

    private void detenerServicioPersistente(){
        create = false;
        Intent notificationIntent = new Intent(getContext(), NotificacionService.class);
        getContext().stopService(notificationIntent);
        isActive = Utilidades.isMyServiceRunning(getContext(), NotificacionService.class);
        actualizarPreferenciasNotificacion(isActive);
    }

    private void actualizarPreferenciasNotificacion(boolean nuevoValor){
        SharedPreferences preferences = getContext().getSharedPreferences("NotificacionPersistente", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notificacionActiva", nuevoValor);
        editor.commit();
    }

    private void obtenerPreferenciasNotificacion(){
        SharedPreferences preferences = getContext().getSharedPreferences("NotificacionPersistente", Context.MODE_PRIVATE);
        if (preferences.contains("notificacionActiva")){
            isActive = Utilidades.isMyServiceRunning(getContext(), NotificacionService.class);
            create = (isActive)? true: false;
            switchServicioActivo.setChecked(isActive);
            conf_nueva = Utilidades.isMyServiceRunning(getContext(), NotificacionService.class);
        } else {
            isActive = Utilidades.isMyServiceRunning(getContext(), NotificacionService.class);
            create = (isActive)? true: false;
            switchServicioActivo.setChecked(isActive);
            conf_nueva = false;
        }
    }

    private void mostrarDivulgacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_divulgacion, null);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                switchServicioActivo.setChecked(isActive);
            }
        });
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.btnCancelarDivulg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchServicioActivo.setChecked(isActive);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.btnAceptarDivulg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarServicioPersistente();
                dialog.dismiss();
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Reiniciar servicio unicamente para API menor a OREO
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                && !Utilidades.isMyServiceRunning(getContext(), NotificacionService.class)
                && conf_nueva){
            Intent notificationIntent = new Intent(getContext(), NotificacionService.class);
            notificationIntent.putExtra("padre", "App");
            getContext().startService(notificationIntent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iBtnGuardarCiclo:
                try{
                    if (txtNoCiclo.getText()== null || Integer.parseInt(txtNoCiclo.getText().toString()) <= 0 ){
                        Toast.makeText(getContext(), "¡Por favor ingrese un valor válido!" , Toast.LENGTH_SHORT).show();
                    } else if ( Integer.parseInt(txtNoCiclo.getText().toString()) > 3  ){
                        Toast.makeText(getContext(), "¡Por favor ingrese un valor entre 1 y 3!" , Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(txtNoCiclo.getText().toString()) >= 1 && Integer.parseInt(txtNoCiclo.getText().toString()) <= 3){
                        guardarPreferenciasCiclo(Integer.parseInt(txtNoCiclo.getText().toString()));
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "¡Por favor ingrese un valor válido!" , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        inicio = validarPermisos();
    }

    private void guardarPreferenciasCiclo(int ciclo){
        PreferencesCiclo preferencesCiclo = new PreferencesCiclo();
        Boolean res = preferencesCiclo.guardarCicloFotografias(getContext(), ciclo);
        if(res){
            Toast.makeText(getContext(), "¡Número de ciclos guardados con éxito!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "¡Error al guardar el número de ciclos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerPreferenciasCiclo(){
        PreferencesCiclo preferencesCiclo = new PreferencesCiclo();
        int ciclos = preferencesCiclo.obtenerCicloFotografias(getContext());
        txtNoCiclo.setText(String.valueOf(ciclos));
    }
}