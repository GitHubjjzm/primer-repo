package com.c5durango.alertalsm.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.c5durango.alertalsm.Servicios.NotificacionService;

public class EncendidoBroadcast extends BroadcastReceiver {

    /*
    * En caso de que se tenga activo el servicio que esta al pendiente de escuchar los 3 botonazos
    * y el teléfono se deba reiniciar es necesario volver a activar el servicio automaticamente.
    * */

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String TAG = "BOOT_COMPLETEDDD";

        if(action != null) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED) ) {
                Log.d(TAG, "ACTION_BOOT_COMPLETED");

                // Validar si el servicio estaba activo la ultima vez
                Boolean isActive = false;
                SharedPreferences preferences = context.getSharedPreferences("NotificacionPersistente", Context.MODE_PRIVATE);
                if (preferences.contains("notificacionActiva")){
                    isActive = preferences.getBoolean("notificacionActiva", false);
                    Log.d(TAG, "EL RESULTADO DE ESTABA ES; " + isActive);
                    if(isActive){
                        try {
                            Intent service = new Intent(context.getApplicationContext(),  NotificacionService.class);
                            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                context.startForegroundService(service);
                            } else {
                                context.startService(service);
                            }
                        } catch (Exception io){
                            io.printStackTrace();
                            Log.d(TAG,  "¡Error al actualizar los datos locales del servicio! " + io.getMessage());
                        }
                    }
                }
            }
        }
    }
}
