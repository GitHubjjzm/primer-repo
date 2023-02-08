package com.c5durango.alertalsm.Servicios;


import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Utilidades.Notificaciones;
import com.c5durango.alertalsm.Utilidades.Utilidades;

import static com.c5durango.alertalsm.Constantes.CHANNEL_ID;
import static com.c5durango.alertalsm.Constantes.ID_SERVICIO_AUDIO;
import static com.c5durango.alertalsm.Constantes.ID_SERVICIO_GPS;

public class GPSService extends Service {

    private LocationManager mlocManager;
    Intent intentG;
    String TAG = "GPSService";
    Localizacion Local;

    // VARIABLES
    int reporteGenerado;
    String nombrePadre;
    String fechaActual;
    String escuchador = "GPSService";

    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("onBind no implementado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            intentG = intent;
            reporteGenerado = intent.getIntExtra("reporteGenerado", 0);
            nombrePadre = intent.getStringExtra("padre");
            if(intent.getExtras().containsKey("escuchador"))
                escuchador = intent.getStringExtra("escuchador");

            // Crear notificación para avisar que se está enviando el video
            crearNotificacionPersistente(
                    Notificaciones.crearNotificacionPersistente(
                            getApplicationContext(),
                            this.getClass(),
                            "Buscando ubicación geográfica..",
                            "",
                            R.drawable.ic_marker,
                            CHANNEL_ID,
                            Color.RED));

            // if (reporteGenerado != 0 || reporteGenerado == -1) {
                locationStart();
            // } else {
               //  Log.d(TAG, "El reporte es cero");
            // }
        } else {
            Log.d(TAG, "INTENT NULL ");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void locationStart() {
        Log.d(TAG, "Location Start");
        mlocManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        Local = new Localizacion();
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // GPS Desactivado
            darResultados(getApplicationContext(), 0.0, 0.0, "GPS Deshabilitado");
        } else {
            Log.d(TAG, "GPS Enabled");
            try {
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (1000 * 10 * 1), 10, Local, Looper.myLooper());
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "Network " + "no solicitar la actualización de ubicación: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "Network " + "proveedor de red no existe, " + ex.getMessage());
            }
            try {
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (1000 * 10 * 1), 10, Local, Looper.myLooper());
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "GPS " + "no solicitar la actualización de ubicación: " + ex.getMessage());
                darResultados(getApplicationContext(), 0.0, 0.0, "No se pudo obtener la ubicación actual");
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "GPS " + "proveedor de red no existe, " + ex.getMessage());
                darResultados(getApplicationContext(), 0.0, 0.0, "El proveedor GPS no existe");
            }
        }
    }


    public class Localizacion implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            Log.d(TAG, "GPS "+ lat);
            Log.d(TAG, "GPS "+ lon);
            fechaActual = Utilidades.obtenerFecha();
            mlocManager.removeUpdates(Local);
            try {
                Local.finalize();
            } catch (Throwable throwable) {
                Log.d(TAG, "GPS "+ "Error al finalizar");
                throwable.printStackTrace();
            }
            darResultados(getApplicationContext(), lat, lon, "Se localizó ubicación");
            stopSelf();
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Log.d(TAG, "GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Log.d(TAG, "GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "GPS "+ "El estatus cambió");
        }
    }

    public void crearNotificacionPersistente(Notification n){
        startForeground(ID_SERVICIO_GPS, n);
    }


    private void darResultados(Context context, Double latitud, Double longitud, String mensaje){
        Log.d(TAG, latitud + " : " + longitud + " : " + mensaje);
        Intent intent = new Intent(escuchador);
        intent.putExtra("fecha", fechaActual);
        intent.putExtra("latitud", latitud);
        intent.putExtra("longitud",  longitud);
        intent.putExtra("padre", nombrePadre);
        intent.putExtra("reporteCreado", reporteGenerado);
        intent.putExtra("mensaje", mensaje);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        stopSelf();
    }
}