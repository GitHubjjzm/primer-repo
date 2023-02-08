package com.c5durango.alertalsm.Servicios;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;

import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Retrofit.NetworkTask;
import com.c5durango.alertalsm.Utilidades.Notificaciones;
import com.c5durango.alertalsm.Utilidades.UriUtils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.c5durango.alertalsm.Constantes.CHANNEL_ID;
import static com.c5durango.alertalsm.Constantes.ID_SERVICIO_VIDEO;

public class VideoService extends Service {

    String TAG = "VIDEO_SERVICE";
    private ArrayList<Uri> arrayVideoURI;
    private ArrayList<String> arrayPaths = new ArrayList<>();
    private NetworkTask networkTask;

    public VideoService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            if (intent.getExtras() != null){

                try {
                    networkTask = new NetworkTask();
                } catch (Exception e){
                    e.printStackTrace();
                }

                String fecha = intent.getExtras().getString("fecha");
                int id_reporte  = intent.getExtras().getInt("id_reporte");
                if(intent.getExtras().containsKey("arrayVideoURI")) {
                    arrayVideoURI = (ArrayList<Uri>) (ArrayList<?>) (intent.getExtras().getStringArrayList("arrayVideoURI"));
                    for(int i=0; i < arrayVideoURI.size(); i++){
                        try {
                            arrayPaths.add(UriUtils.getPathFromUri(getApplicationContext(), arrayVideoURI.get(i)));
                        } catch (Exception f){
                            Log.d(TAG, f.getMessage());
                        }
                    }

                }

                for (int y = 0; y< arrayPaths.size() ; y++){
                    Log.d(TAG, arrayPaths.get(y));
                }
                // Crear notificación para avisar que se está enviando el video
                crearNotificacionPersistente(
                        Notificaciones.crearNotificacionPersistente(
                                getApplicationContext(),
                                this.getClass(),
                                "Enviando video, por favor espere..",
                                "",
                                R.drawable.ic_video_bco,
                                CHANNEL_ID,
                                Color.RED));

                uploadVideoUsingRetrofitArray(fecha, id_reporte);
            } else {
                stopSelf();
                // No recibí path
            }
        } else {
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }



    private void uploadVideoUsingRetrofitArray(String fecha, int id_reporte) {

        Log.d(TAG,"Uplaoding Video Array....." );
        try {
            retrofit2.Call<String> response;

            MultipartBody.Part[] arrayVideos = new MultipartBody.Part[ arrayPaths.size() ];
            for (int i = 0; i < arrayPaths.size(); i++){
                File file = new File(arrayPaths.get(i));
                arrayVideos[i] = MultipartBody.Part.createFormData("video", "video_file_" + i + "." +
                        FilenameUtils.getExtension(file.toString()), RequestBody.create(MediaType.parse("video/*"), file));
            }

            response = networkTask.uploadVideo(id_reporte, arrayVideos);

            response.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if(response.code() == 200){
                        Log.e(TAG, "Success: " + response.code());
                    } else {
                        Log.e(TAG, "No se proceso correctamente: " + response.code() );
                    }
                    stopSelf();
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.e(TAG, "Failed:" + t.getMessage());
                    stopSelf();
                }
            });
        } catch (Exception e){
            Log.e(TAG, "Exception: " + e.getMessage());
            stopSelf();
        }

    }

    /*private void uploadVideoUsingRetrofit(File videoFile) {

        Log.d(TAG,"Uplaoding Video....." );
        try {
            retrofit2.Call<String> response;
            MultipartBody.Part vFile;
            RequestBody fbody;
            String fileExt;

            fbody = RequestBody.create(MediaType.parse("video/*"), videoFile);
            fileExt = FilenameUtils.getExtension(videoFile.toString());
            vFile = MultipartBody.Part.createFormData("video", "video_file." + fileExt, fbody);

            response = networkTask.uploadVideo("upload", vFile);

            response.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if(response.code() == 200){
                        Log.e(TAG, "Success: " + response.code());
                    }
                    stopSelf();
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.e(TAG, "Failed:" + t.getMessage());
                    stopSelf();
                }
            });
        } catch (Exception e){
            Log.e(TAG, "Exception: " + e.getMessage());
            stopSelf();
        }

    }*/

    public void crearNotificacionPersistente(Notification n){
        startForeground(ID_SERVICIO_VIDEO, n);
    }

    public class BodyVideo {
        int id_reporte;
        String fecha;

        public BodyVideo(int id_reporte, String fecha){
            this.id_reporte = id_reporte;
            this.fecha = fecha;
        }

        public int getId_reporte() {
            return id_reporte;
        }

        public void setId_reporte(int id_reporte) {
            this.id_reporte = id_reporte;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }
    }
}