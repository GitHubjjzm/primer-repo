package com.c5durango.alertalsm.Servicios;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Utilidades.Audio;
import com.c5durango.alertalsm.Utilidades.Notificaciones;
import com.c5durango.alertalsm.Utilidades.Utilidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.c5durango.alertalsm.Constantes.CHANNEL_ID;
import static com.c5durango.alertalsm.Constantes.DURACION_AUDIO;
import static com.c5durango.alertalsm.Constantes.EXTENSION_AUDIO;
import static com.c5durango.alertalsm.Constantes.ID_SERVICIO_AUDIO;

public class AudioService extends Service  {

    /*
    * Se encarga de grabar varios audios en segundo plano,
    * guardarlos, enviarlos y retornar el resultado al padre.
    * */

    public static final int RequestPermissionCode = 1;
    String AudioSavePathInDevice = null;
    private String nombreAudio = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    Boolean hasStart = false;
    String TAG = "AudioServiceT";
    //String padre = "desc";

    GrabarAudioBackground grabarAudioBackground;

    String audios[] = new String[4];
    String fechas[] = new String[4];

    int contadorRecorder = 0;
    int contadorEnvio = 0;
    int reporteCreado = 0;

    boolean soloUno = false;

    public AudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            nombreAudio = intent.getStringExtra("nombreAudio");
            reporteCreado = intent.getIntExtra("reporteCreado", 0);
            //padre = intent.getStringExtra("padre");


            // soloUno es para grabar un audio solamente
            if(intent.getExtras().containsKey("soloUno"))
                soloUno = true;

            // Crear notificación para avisar que se está enviando el audio
            crearNotificacionPersistente(
                    Notificaciones.crearNotificacionPersistente(
                            getApplicationContext(),
                            this.getClass(),
                            "Grabando audio...",
                            "",
                            R.drawable.ic_microfono,
                            CHANNEL_ID,
                            Color.RED));
            comenzarHiloGrabacionAudio(contadorRecorder);

        } else {
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void comenzarHiloGrabacionAudio(int posicionGuardar){
        grabarAudioBackground = new GrabarAudioBackground();
        grabarAudioBackground.execute(posicionGuardar);
    }

    public void comenzarHiloEnvioAudio(int posicionEnviar){
        enviarAudioVolley(posicionEnviar);
    }

    // Escucha cuando se termino el hilo de la grabación
    Handler handlerTerminoGrabacion = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Terminó de ejecutar la tarea
                    //Log.d(TAG, "Ya termino de grabar");
                    audios[contadorRecorder] = AudioSavePathInDevice;
                    contadorRecorder++;

                    Log.d(TAG, "(Hilo 1) Contador: " + contadorRecorder);
                    Log.d(TAG, "(Hilo 1) Audios[" + contadorEnvio + "]: = " + audios[contadorEnvio]);
                    if(contadorRecorder <= 3 && !soloUno)
                        comenzarHiloGrabacionAudio(contadorRecorder);

                    if(contadorEnvio <= 3)
                        comenzarHiloEnvioAudio(contadorEnvio);

                    break;
                default:
                    break;
            }
        }
    };

    Handler handlerTerminoEnvio = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Terminó de ejecutar la tareaf
                    Log.d(TAG, "(Hilo 2) ------- FIN " + contadorEnvio + "-------");
                    contadorEnvio++;

                    if(contadorEnvio == 1 && soloUno){
                        darResultados(true);
                        stopSelf();
                        Log.d(TAG, "stopSelf() Solo uno");
                        return;
                    }

                    if(contadorEnvio == 4 && !soloUno){
                        darResultados(true);
                        stopSelf();
                        Log.d(TAG, "stopSelf()");
                    }
                    break;
                default:
                    break;
            }
            // Aquí se notifica que se envió correctamente
        }
    };

    public Boolean enviarAudioVolley(int posicionEnviar){

        String pathParaEnviar = audios[posicionEnviar];
        String fechaParaEnviar = fechas[posicionEnviar];
        String audioBase64 = Audio.convertirAudioString(pathParaEnviar);
        String URL = Constantes.URL + "/upload/audio/" + reporteCreado;

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("fecha", fechaParaEnviar);
            jsonObjectBody.put("audio", audioBase64);
            jsonObjectBody.put("parte", posicionEnviar);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        final String requestBody = jsonObjectBody.toString();
        StringRequest requestAudio = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "(Respuesta audio) La respuesta del envio de audio es: " + response);

                // Detener la petición de audio
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String tipo_error = "ERROR #7 " + Utilidades.tipoErrorVolley(error);
                // Detener proceso una vez que se no se envió el audio
                requestQueue.stop();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        requestQueue.add(requestAudio);

        // Avisar que se terminó el envio
        handlerTerminoEnvio.sendEmptyMessage(0);
        return true;
    }

    public class GrabarAudioBackground extends AsyncTask<Integer, Integer, String> implements MediaRecorder.OnInfoListener {

        int posicionParaGuardar;

        @Override
        protected String doInBackground(Integer... posicion) {
            posicionParaGuardar = posicion[0];
            iniciarGrabacion();
            return "fin";
        }

        private void iniciarGrabacion(){

            AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + nombreAudio + "_" + posicionParaGuardar + "." + EXTENSION_AUDIO;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            mediaRecorder.setMaxDuration(DURACION_AUDIO);
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
            mediaRecorder.setOnInfoListener(this);


            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    darResultados(false);
                }
            });

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                fechas[ posicionParaGuardar ] = Utilidades.obtenerFecha();

                hasStart = true;
            } catch (IllegalStateException e) {
                Log.d(TAG, "(Hilo 1) Catch 1" + e.getMessage());
                darResultados(false);
            } catch (IOException e) {
                Log.d(TAG, "(Hilo 1) Catch 2" + e.getMessage());
                darResultados(false);
            }
        }

        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            switch (what){
                case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                    mediaRecorder.stop();
                    Log.d(TAG, "LLEGO AL MAXIMO LA GRABACIÓN");

                    onPostExecute(AudioSavePathInDevice);
                    break;
            }
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            Log.d(TAG, "(Hilo 1) " + path);

            if(path.length()>5 && soloUno){
                handlerTerminoGrabacion.sendEmptyMessage(0);
                return;
            }

            if(path.length()>5){ // Si se tiene la ruta correcta
                handlerTerminoGrabacion.sendEmptyMessage(0);
            } else{
                //Log.d(TAG, "Disque termino.");
            }
        }
    }

    public void crearNotificacionPersistente(Notification n){
        startForeground(ID_SERVICIO_AUDIO, n);
    }

    private void darResultados(boolean termino){
        Intent intent = new Intent("AudioBroadcast");
        intent.putExtra("termino", termino);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
        stopSelf();
    }
}

