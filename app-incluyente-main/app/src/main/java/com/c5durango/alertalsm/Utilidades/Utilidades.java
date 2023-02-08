package com.c5durango.alertalsm.Utilidades;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.c5durango.alertalsm.Constantes;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilidades {

    static String TAG = "Utilidades";

    public static String obtenerFecha(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date()); // Salida:  2019-10-28 15:24:55
    }

    public static String obtenerFechaAleatoria(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String f = dateFormat.format(new Date());
        if(f.length() == 19){
            f = f.substring(0, 17);
            f = f + (int) (Math.random() * 50 + 1);
        }
        return f; // Salida:  2019-10-28 15:24:55
    }



    public static String generarNombreImgHora(Context context) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // getExternalFilesDir = fotografías privadas de la app
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);

        return image.getAbsolutePath();
    }

    public static String generarNombreAudioHora() {
        String imageFileName = "AUDIO_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) ;
        return  Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + imageFileName + "." + Constantes.EXTENSION_AUDIO;
    }


    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static String tipoErrorVolley(VolleyError error){
        if (error instanceof TimeoutError) {
            return "Falló la comunicación con el servidor";
        } else if (error instanceof NoConnectionError) {
            return "Sin conexión";
        } else if (error instanceof AuthFailureError) {
            return "Error de autenticación";
        } else if (error instanceof ServerError) {
            return "El servidor no responde";
        } else if (error instanceof NetworkError) {
            return "Error de red";
        } else if (error instanceof ParseError) {
            return "Error de parseo";
        } else {
            return "Error desconocido";
        }
    }

}
