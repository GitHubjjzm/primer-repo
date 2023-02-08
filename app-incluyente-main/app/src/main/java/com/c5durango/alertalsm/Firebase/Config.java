package com.c5durango.alertalsm.Firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Utilidades.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Config {

    /*
    * Se encarga de obtener el token del dispositivo y enviarlo al servidor
    * para posteriormente poder recibir notificaciones con Firebase.
    * */

    static String TAG = "Firebase";

    public static void getTokenInstance(final Context context, final int id_usuario){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        enviarTokenFirebase(context, id_usuario, token);
                        //guardarTokenPreferences(context, token);
                        // Log and toast
                        String tok = context.getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, tok);
                    }
                });
    }

    public static void suscribirTema(final Context context, String tema){
        FirebaseMessaging.getInstance().subscribeToTopic(tema)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Boolean s = true;
                        if (!task.isSuccessful()) {
                            s = false;
                        }
                        Log.d(TAG, "La suscripción fue: " + s);
                    }
                });
    }

    private static void guardarTokenPreferences(Context context, String token){
        try {
            android.content.SharedPreferences preferencesFirebase = context.getSharedPreferences("Firebase", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editorFirebase = preferencesFirebase.edit();
            editorFirebase.putString("token", token);
            Log.d(TAG, "ESTOY GUARDANGO TOKEN: " + token);
            editorFirebase.commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String getTokenPreferences(Context context){
        String token = "";

        android.content.SharedPreferences preferences = context.getSharedPreferences("Firebase", Context.MODE_PRIVATE);
        if (preferences.contains("token")) {
            token = preferences.getString("token", token);
        } else {
            token = "notengotoken";
        }

        return token;
    }


    public static void enviarTokenFirebase(final Context context, int id_usuario, String token){
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = Constantes.URL + "/token/" + id_usuario;

        Log.d(TAG, "ENVIARÉ TOKEN: " + token);
        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        final String requestBody = jsonObjectBody.toString();

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.PUT, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        // Si se envió tod correcto suscribirse a un grupo
                        // para solo recibir notificaciones de su grupo correspondiente.
                        suscribirTema(context, "GRUPO_ALERTA_LSM");
                        requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorResp = "Error #2A: " + Utilidades.tipoErrorVolley(error);
                        Log.d(TAG, errorResp);
                        requestQueue.stop();
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        requestQueue.add(getRequest);

    }



}