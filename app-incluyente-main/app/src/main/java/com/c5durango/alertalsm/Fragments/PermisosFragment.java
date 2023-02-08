package com.c5durango.alertalsm.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.androidhiddencamera.HiddenCameraUtils;
import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.R;

public class PermisosFragment extends Fragment implements View.OnClickListener {

    View root;
    ImageButton btnAlmacWrite, btnCam, btnMicrof, btnUbic, btnEncima;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_permisos, container, false);

        btnAlmacWrite = root.findViewById(R.id.btnAlmacenamientoWrite);
        btnAlmacWrite.setOnClickListener(this);
        btnCam = root.findViewById(R.id.btnCamara);
        btnCam.setOnClickListener(this);
        btnMicrof = root.findViewById(R.id.btnMicrofono);
        btnMicrof.setOnClickListener(this);
        btnEncima = root.findViewById(R.id.btnAparecerEncima);
        btnEncima.setOnClickListener(this);
        btnUbic = root.findViewById(R.id.btnUbicacion);
        btnUbic.setOnClickListener(this);

        // Detectar permisos
        permisoAlmacWrite();
        permisoCam();
        permisoMicrof();
        permisoUbic();
        permisoEncima();


        return root;
    }


    private void permisoAlmacWrite(){
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            btnAlmacWrite.setBackgroundColor(Color.rgb(127,186,130));
        }
    }

    private void permisoCam(){
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            btnCam.setBackgroundColor(Color.rgb(127,186,130));
        }
    }

    private void permisoMicrof(){
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            btnMicrof.setBackgroundColor(Color.rgb(127,186,130));
        }
    }

    private void permisoUbic(){
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            btnUbic.setBackgroundColor(Color.rgb(127,186,130));
        }
    }

    public void permisoEncima(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            btnEncima.setBackgroundColor(Color.rgb(127,186,130));
        } else {
            if(Settings.canDrawOverlays(getContext()))
                btnEncima.setBackgroundColor(Color.rgb(127,186,130));
        }
    }


    public void activarPermisoAlmacWrite(){
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constantes.MY_PERMISSIONS_REQUEST_ALMAC_WRITE);
    }

    public void activarPermisoCam(){
       requestPermissions(new String[]{Manifest.permission.CAMERA}, Constantes.MY_PERMISSIONS_REQUEST_CAMERA);
    }

    public void activarPermisoMicrof(){
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, Constantes.MY_PERMISSIONS_REQUEST_MICROF);
    }

    public void activarPermisoUbic(){
       requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.MY_PERMISSIONS_REQUEST_UBICAC);
    }

    public void aparecerEncima(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(getContext()))
                HiddenCameraUtils.openDrawOverPermissionSetting(getContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case Constantes.MY_PERMISSIONS_REQUEST_ALMAC_WRITE: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permisoAlmacWrite();
                    } else {
                        Toast.makeText(getContext(), "¡Permiso de escritura denegado!", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                case Constantes.MY_PERMISSIONS_REQUEST_CAMERA: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permisoCam();
                    } else {
                        Toast.makeText(getContext(), "¡Permiso de cámara denegado!", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                case Constantes.MY_PERMISSIONS_REQUEST_MICROF: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permisoMicrof();
                    } else {
                        Toast.makeText(getContext(), "¡Permiso de micrófono denegado!", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                case Constantes.MY_PERMISSIONS_REQUEST_UBICAC: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permisoUbic();
                    } else {
                        Toast.makeText(getContext(), "¡Permiso de ubicación denegado!", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }
        } catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAlmacenamientoWrite:
                activarPermisoAlmacWrite();
                break;
            case R.id.btnCamara:
                activarPermisoCam();
                break;
            case R.id.btnMicrofono:
                activarPermisoMicrof();
                break;
            case R.id.btnAparecerEncima:
                aparecerEncima();
                break;
            case R.id.btnUbicacion:
                activarPermisoUbic();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        permisoEncima();
    }
}