package com.c5durango.alertalsm.Utilidades;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Servicios.AudioService;

import static com.c5durango.alertalsm.Constantes.CHANNEL_ID;
import static com.c5durango.alertalsm.Constantes.ID_SERVICIO_AUDIO;

public class Notificaciones {

    public static void crearNotificacionNormal(Context context, String CHANNEL, int icon, String titulo, String contenido, int ID_SERVICIO){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL);
            builder.setSmallIcon(icon);
            if(titulo.length() > 0)
                builder.setContentTitle(titulo);
            builder.setContentText(contenido);
            builder.setColor(Color.rgb(216,27,96));
            builder.setPriority(NotificationCompat.PRIORITY_LOW);
            //builder.setLights(Color.MAGENTA, 1000, 1000);
            //builder.setDefaults(Notification.DEFAULT_SOUND);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(ID_SERVICIO, builder.build());

        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            final NotificationManager mNotific = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int importancia = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL, Constantes.NOMBRE_APP, importancia);
            notificationChannel.setDescription(contenido);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

            if(titulo.length() > 0 ){
                Notification notification =
                        new Notification.Builder(context, CHANNEL)
                                .setContentTitle(titulo)
                                .setContentText(contenido)
                                .setColor(context.getColor(R.color.colorAccent))
                                .setSmallIcon(icon)
                                .build();
                mNotific.notify(ID_SERVICIO, notification);
            } else {
                Notification notification =
                        new Notification.Builder(context, CHANNEL)
                                .setContentText(contenido)
                                .setColor(context.getColor(R.color.colorAccent))
                                .setSmallIcon(icon)
                                .build();
                mNotific.notify(ID_SERVICIO, notification);
            }
        }
    }


    public static Notification crearNotificacionPersistente(Context context, Class clase, String texto, String descripcionINV, int icono, String CHANNEL_ID, int color){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(context, clase);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, notificationIntent, 0);

            // Crear notificación de servicio activo
            Notification notification =
                    new Notification.Builder(context, CHANNEL_ID)
                            .setColor(Color.WHITE)
                            .setContentText(texto)
                            .setSmallIcon(icono)
                            .setColor(color)
                            .setContentIntent(pendingIntent)
                            .build();

            int importancia = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, Constantes.NOMBRE_APP, importancia);
            notificationChannel.setDescription(descripcionINV);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            return notification;
        } else{
            Intent notificationIntent = new Intent(context, clase);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, notificationIntent, 0);

            // Crear notificación de servicio activo
            Notification notification =
                    new Notification.Builder(context)
                            .setColor(Color.WHITE)
                            .setContentText(texto)
                            .setSmallIcon(icono)
                            .setColor(color)
                            .setContentIntent(pendingIntent)
                            .build();
            return notification;
        }
    }
}
