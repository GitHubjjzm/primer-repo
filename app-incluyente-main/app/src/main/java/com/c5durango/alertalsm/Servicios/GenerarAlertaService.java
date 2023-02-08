package com.c5durango.alertalsm.Servicios;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertalsm.Clases.ModeloUbicacion;
import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.Utilidades.PreferencesReporte;
import com.c5durango.alertalsm.Utilidades.Utilidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

public class GenerarAlertaService extends Service {

    static String TAG = "Notificacion";
    // ===============================
    // VARIABLES GENERALES
    // ===============================
    private int idComercio;
    private int idUsuario;
    private int id_municipio;
    private int clave_municipio;
    private String fecha;
    public static int reporteCreado;
    public static Boolean alertaRecibida;

    public static WeakReference<Context> contextoGlobal;
    public static Intent intentGlobal;

    private static ModeloUbicacion modeloUbicacion = new ModeloUbicacion("", "", 0.0,0.0);

    public GenerarAlertaService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("onBind no implementado");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        contextoGlobal = new WeakReference<>(getApplicationContext());
        intentGlobal = intent;

        if(intent == null){
            darResultados(getApplicationContext(), 0, false, false, false, "Intent NULL");
            stopSelf();
        }

        // Recuperar valores de entrada
        try{
            idComercio = intent.getIntExtra("comercio", 0);
            idUsuario = intent.getIntExtra("usuario", 0);
            fecha = intent.getStringExtra("fecha");
            id_municipio = intent.getIntExtra("id_municipio", 0);
            clave_municipio = intent.getIntExtra("clave_municipio", 0);

            modeloUbicacion.setLatitud(intent.getDoubleExtra("latitud", 0.0));
            modeloUbicacion.setLongitud(intent.getDoubleExtra("longitud", 0.0));
            modeloUbicacion.setFechaHora(intent.getStringExtra("fecha"));
            modeloUbicacion.setLugar(intent.getStringExtra("lugar"));


        } catch ( Exception e ){
            darResultados(getApplicationContext(), 0, false, false, false, "Los datos del grupo son incorrectos");
            stopSelf();
        }
        if (idComercio != 0 && idUsuario != 0){
            generarReporte();
        } else {
            darResultados(getApplicationContext(),0, false, false, false, "Los datos del grupo son incorrectos");
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void generarReporte(){
        enviarAlerta(getApplicationContext(), idComercio, idUsuario, id_municipio, clave_municipio);
    }

    // ************************************************ comandante quiroz fiscalia anti corrupción. Palacio federal -
    // ALERTA ALERTA ALERTA ALERTA ALERTA ALERTA ALERTA
    // ************************************************
    private static void enviarAlerta(final Context context, int idComercio, int idUsuario, int id_municipio, int clave_municipio){
        JsonObjectRequest requestAlerta;
        String URL = Constantes.URL + "/alerta/coordenadas/";

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idComercio", idComercio);
            jsonObject.put("idUsuario", idUsuario);
            jsonObject.put("fecha" , Utilidades.obtenerFecha());

            jsonObject.put("id_municipio", id_municipio);
            jsonObject.put("clave_municipio", clave_municipio);

            jsonObject.put("latitud", modeloUbicacion.getLatitud());
            jsonObject.put("longitud", modeloUbicacion.getLongitud());
            jsonObject.put("lugar", modeloUbicacion.getLugar());
            jsonObject.put("tipo", modeloUbicacion.getLugar());

        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        final String requestBody = jsonObject.toString();
        requestAlerta = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean ok = response.getBoolean("ok");

                            if(ok){
                                reporteCreado = response.getInt("reporteCreado");
                                Log.d(TAG, "Se creó el reporte con folio #" + reporteCreado);

                                // GUARDAR INFORMACION DEL ULTIMO REPORTE GENERADO
                                PreferencesReporte.actualizarUltimoReporte(context, reporteCreado);

                                alertaRecibida = true;
                                darResultados(context, reporteCreado, true, true, true, response.getString("message"));
                            } else {
                                darResultados(context, 0, false, true, true, response.getString("message"));
                                Log.d(TAG, "El reporte no ha sido creado");
                                alertaRecibida = false;
                                reporteCreado = 0;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Error al obtener valores del JSON de respuesta");
                        }
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorResp = "Error #6: " + Utilidades.tipoErrorVolley(error);
                Log.e(TAG, errorResp);
                darResultados(context, 0, false, false, false, errorResp);
            }
        }) {
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
        requestQueue.add(requestAlerta);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.err.println("on Destroy > is Finishing ");
        stopSelf();
    }

    private static void darResultados(Context context, int reporteCreado, boolean envioArchivos, boolean respondioServer, boolean datosComercio, String message){
        Intent intent = new Intent("generarAlertaService");
        intent.putExtra("reporteCreado", reporteCreado);
        intent.putExtra("envioArchivos",  envioArchivos);
        intent.putExtra("respondioServer", respondioServer);
        intent.putExtra("datosComercio", datosComercio);
        intent.putExtra("message", message);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        //stopSelf();
    }
}