package com.c5durango.alertalsm.Utilidades;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class EnviarBotonazo {

    /*
     * Clase para manejar los botonazos ++
     * una vez generada la alerta.
     * */

    static String TAG = "Botonazo";

    public static void enviarBotonazo(final Context context, int id_reporte_creado, String fecha_hora){
        StringRequest requestBotonazo;
        String URL = Constantes.URL + "/activaciones/" + id_reporte_creado;

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fecha_activacion", fecha_hora);
        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        final String requestBody = jsonObject.toString();
        requestBotonazo = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "La respues al enviar otro botonazo es: " + response);
                Notificaciones.crearNotificacionNormal(context, Constantes.CHANNEL_ID, R.drawable.ic_color_info, "", "Alerta con folio #"+id_reporte_creado+ " REENVIADA con éxito.", Constantes.ID_SERVICIO_BOTONAZO);
                Toast.makeText(context, "Alerta con folio #"+id_reporte_creado+ " REENVIADA con éxito.", Toast.LENGTH_SHORT).show();
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorResp = "Error #2: " + Utilidades.tipoErrorVolley(error);
                // Toast.makeText(context, errorResp, Toast.LENGTH_SHORT).show();
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
        requestQueue.add(requestBotonazo);
    }


}
