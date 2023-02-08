package com.c5durango.alertalsm.Utilidades;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertalsm.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Audio {
    static String TAG = "AUDIO_CODIFICACION";

    public static String convertirAudioString(String pathAudio) {

        String audioString;
        byte[] audioBytes;
        try {
            // Log.d(TAG, "El peso del archivo es: " + new File(pathAudio).length());
            // 25 KB aprox para 15 segundos

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(pathAudio));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            audioBytes = baos.toByteArray();

            audioString = Base64.encodeToString(audioBytes, Base64.DEFAULT);
            return audioString;
        } catch (FileNotFoundException fnf) {
            Log.d(TAG, "Ocurrió un error al codificar audio a Base64 (FileNotFoundException).");
            fnf.printStackTrace();
            return "";
        } catch (IOException io){
            Log.d(TAG, "Ocurrió un error al codificar audio a Base64 (IOException).");
            io.printStackTrace();
            return "";
        } catch (Exception e){
            Log.d(TAG, "Ocurrió un error al codificar audio a Base64 (Exception).");
            e.printStackTrace();
            return "";
        }

    }
    public static Boolean enviarAudioVolley(Context context, String audioBase64, String fecha, int reporteCreado){

        String URL = Constantes.URL + "/upload/audio/" + reporteCreado;

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("fecha", fecha);
            jsonObjectBody.put("audio", audioBase64);
            jsonObjectBody.put("parte", 0);
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
        return true;
    }

}
