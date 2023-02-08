                   package com.c5durango.alertalsm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertalsm.Clases.ModeloLogin;
import com.c5durango.alertalsm.Dialogs.Dialogs;
import com.c5durango.alertalsm.Envios.EnviarCoordenadas;
import com.c5durango.alertalsm.Servicios.AudioService;
import com.c5durango.alertalsm.Servicios.FotografiaService;
import com.c5durango.alertalsm.Servicios.GPSService;
import com.c5durango.alertalsm.Utilidades.EnviarImagenes;
import com.c5durango.alertalsm.Utilidades.Notificaciones;
import com.c5durango.alertalsm.Utilidades.PreferencesReporte;
import com.c5durango.alertalsm.Utilidades.Utilidades;
import com.c5durango.alertalsm.Fragments.InicioFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import static com.c5durango.alertalsm.Constantes.CHANNEL_ID;
import static com.c5durango.alertalsm.Constantes.ID_SERVICIO_WIDGET_GENERAR_ALERTA;

public class MainActivity extends AppCompatActivity implements InicioFragment.DataListener {

    private static AppBarConfiguration mAppBarConfiguration;
    private static ModeloLogin modelo_login;
    static String TAG = "MENU_ACTIVITY";

    // VARIABLES PARA USO DE MULTIMEDIA
    static ImageTraseraResultReceiver resultTReceiver;
    static ImageFrontalResultReceiver resultFReceiver;

    // Variables de uso general
    static WeakReference<Context> contextoGlobal;
    public static int reporteCreado = -1;
    public static int contador = 0;
    public static boolean btnPresionado = false;
    static Intent intentGPS;

    public static final int SUCCESS = 1;
    public static final int ERROR = 0;

    static Boolean procesoImagenFrontal = false;
    static Boolean  procesoImageTrasera = false;

    static String IMAGEN_FRONTAL = "Ninguna";
    static String IMAGEN_TRASERA = "Ninguna";

    static String FECHA_FRONTAL = "";
    static String FECHA_TRASERA = "";

    static boolean ESCUCHADOR_GPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtener los IDs del comercio y usuario registrado
        modelo_login = new ModeloLogin(0,0, 0, 0);
        SharedPreferences preferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        if (preferences.contains("comercio") && preferences.contains("usuario") && preferences.contains("id_municipio") && preferences.contains("clave_municipio")){
            modelo_login.setId_comercio (preferences.getInt("comercio" ,0));
            modelo_login.setId_usuarios_app(preferences.getInt("usuario", 0));
            modelo_login.setId_municipio(preferences.getInt("id_municipio", 0));
            modelo_login.setClave_municipio(preferences.getInt("clave_municipio", 0));

            SharedPreferences sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("slide", false)){
                Intent intentBienv = new Intent(MainActivity.this, SlideActivity.class);
                startActivity(intentBienv);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }

        contador = 0;
        contextoGlobal = new WeakReference<>(getApplicationContext());
        // TODO: DESCOMENTAR CUANDO SEA VISIBLE EL BOTÓN DE PÁNICO
        /*if(!ESCUCHADOR_GPS)
            registrarEscuchadorGPS(contextoGlobal.get());*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // MENU BAR
        // R.id.nav_config,
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio, R.id.nav_reportes, R.id.nav_perfil,
                R.id.nav_permisos, R.id.nav_info)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void generarReporteNotificacion() {
        Intent i = new Intent(getApplicationContext(), ReporteActivity.class);
        i.putExtra("id_comercio", modelo_login.getId_comercio());
        i.putExtra("id_usuario", modelo_login.getId_usuarios_app());
        i.putExtra("id_municipio", modelo_login.getId_municipio());
        i.putExtra("clave_municipio", modelo_login.getClave_municipio());
        startActivity(i);
    }

    @Override
    public void generarReporteRapido() {
        botonActivado();
    }



    /**********************************************************************************************
    *                                 GENERAR  ALERTA                                             *
    **********************************************************************************************/

    public void botonActivado(){
        contador++;
        // Validar si se puede generar una nueva alerta
        Boolean puedeEnviar = PreferencesReporte.puedeEnviarReporte(getApplicationContext(), System.currentTimeMillis());

        if(puedeEnviar && contador <= 1) {
            Log.d(TAG, "Si se puede enviar.. " + contador + " -- " + puedeEnviar);
            //mostrarResultadoVista();
            // Tomar ubicación y al volver generar alerta.
            Dialogs.abrirVentanaMensaje(MainActivity.this, "Enviando alerta", "Espere un momento", R.drawable.ic_color_info, View.VISIBLE, View.GONE, false);
            PreferencesReporte.guardarReporteInicializado(getApplicationContext());
            if(ActivityCompat.checkSelfPermission(contextoGlobal.get(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(!ESCUCHADOR_GPS)
                    registrarEscuchadorGPS(getApplicationContext());

                comenzarGPS(contextoGlobal.get());
                Log.d(TAG, "Check self permision");
            } else {
                // Realizar envio normal
                Log.d(TAG, "Realizar envio normal");
                prepararAlerta(contextoGlobal.get(), modelo_login.getId_comercio(), modelo_login.getId_usuarios_app(), modelo_login.getId_municipio(), modelo_login.getClave_municipio());
            }
        } else if(puedeEnviar && contador > 1 ){
            contador = 0;
            botonActivado();
            Log.d(TAG, "No puedo enviar por que estoy en espera (1).. " + contador + " -- " + puedeEnviar);
        }
    }


    public static void prepararAlerta(final Context context, int idComercio, int idUsuario, int id_municipio, int clave_municipio){
        String URL = Constantes.URL + "/alerta/";
        JSONObject jsonObject = new JSONObject();
        try {
            // Información del comercio
            jsonObject.put("idComercio", idComercio);
            jsonObject.put("idUsuario", idUsuario);
            jsonObject.put("fecha" , Utilidades.obtenerFecha());
            jsonObject.put("id_municipio", id_municipio);
            jsonObject.put("clave_municipio", clave_municipio);
            // Enviar alerta
            enviarAlerta(context, jsonObject, URL);
        } catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

    }

    public static void prepararAlerta(final Context context, int idComercio, int idUsuario, String latitud, String longitud, String tipo_ubic, int id_municipio, int clave_municipio){
        String URL = Constantes.URL + "/alerta/coordenadas/";
        Log.d("NOTIFICACION", "VOY AQUI");
        JSONObject jsonObject = new JSONObject();
        try {
            // Información del comercio
            jsonObject.put("idComercio", idComercio);
            jsonObject.put("idUsuario", idUsuario);
            jsonObject.put("fecha" , Utilidades.obtenerFecha());
            // Información de ubicación
            jsonObject.put("latitud", latitud);
            jsonObject.put("longitud", longitud);
            jsonObject.put("tipo", tipo_ubic);
            jsonObject.put("id_municipio", id_municipio);
            jsonObject.put("clave_municipio", clave_municipio);
            // Enviar alerta
            enviarAlerta(context, jsonObject, URL);
        } catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

    }

    public static void enviarAlerta(final Context context, JSONObject jsonObject, String URL){
        Log.d(TAG, "ENVIARALERTA()");
        JsonObjectRequest requestAlerta;
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final String requestBody = jsonObject.toString();
        requestAlerta = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "Response vale :33 : " + response);
                            Boolean ok = response.getBoolean("ok");

                            if(ok){
                                // GUARDAR INFORMACION DEL ULTIMO REPORTE GENERADO
                                reporteCreado = response.getInt("reporteCreado");
                                Notificaciones notificaciones = new Notificaciones();
                                notificaciones.crearNotificacionNormal(context, CHANNEL_ID,  R.drawable.ic_color_success, "¡Alerta enviada!", "Se generó alerta con folio #" + reporteCreado, ID_SERVICIO_WIDGET_GENERAR_ALERTA);

                                //mostrarResultadoVista(R.drawable.ic_color_success, "¡Éxito al generar alerta #" + reporteCreado + "!\nEnviando multimedia..");
                                Dialogs.cerrarVentanaMensaje();
                                PreferencesReporte.actualizarUltimoReporte(context, reporteCreado);
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                btnPresionado = true;

                                // COMENZAR A GENERAR LA MULTIMEDIA
                                if(ESCUCHADOR_GPS){
                                    eliminarEscuchadorGPS(context);
                                    terminarGPS(context);
                                }

                                // Solo grabar audio para API 23 en adelante
                                if( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                                        // Validar si el servicio está activo
                                        if(!Utilidades.isMyServiceRunning(context, AudioService.class)){
                                            // Invertí la condición y meti comenzar
                                            // terminarGrabacionAudio();
                                            // Thread.sleep(3000);
                                            Log.d(TAG, "Comenzaré a grabar audio");
                                            comenzarGrabacionAudio(context);
                                        } else {
                                            Log.d(TAG, "No puedo grabar por que el servicio se está ejecutando");
                                        }

                                    }
                                }

                                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                                    iniciarProcesoFotografias(context);
                                } else {
                                    Log.e(TAG, "No tiene permisos de camara");
                                }
                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                Notificaciones notificaciones = new Notificaciones();
                                notificaciones.crearNotificacionNormal(context, CHANNEL_ID,  R.drawable.ic_color_error, "¡No se pudo generar la alerta de pánico!", response.getString("message"), ID_SERVICIO_WIDGET_GENERAR_ALERTA);
                                Dialogs.cerrarVentanaMensaje();
                                //mostrarResultadoVista(R.drawable.ic_color_error,  response.getString("message"));
                                btnPresionado = false;
                                contador = 0;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Algún error con los permisos");
                        }
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorResp = "Error #1: " + Utilidades.tipoErrorVolley(error);
                Notificaciones notificaciones = new Notificaciones();
                notificaciones.crearNotificacionNormal(context, CHANNEL_ID,  R.drawable.ic_color_error, "¡No se pudo generar la alerta de pánico!", errorResp, ID_SERVICIO_WIDGET_GENERAR_ALERTA);
                Dialogs.cerrarVentanaMensaje();
                btnPresionado = false;
                contador = 0;
                //mostrarResultadoVista(R.drawable.ic_color_error, "¡No se pudo generar la alerta de pánico!" + "\n"+ errorResp);
                requestQueue.stop();
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



    /**********************************************************************************************
     *                                     FOTOGRAFIAS                                             *
     **********************************************************************************************/

    public static void iniciarProcesoFotografias(Context context){

        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            // INICIAR PROCESO CAMARA FRONTAL
            resultFReceiver = new ImageFrontalResultReceiver(new Handler());
            Intent intentFrontal = new Intent(context, FotografiaService.class);
            intentFrontal.putExtra("reporteCreado", reporteCreado);
            intentFrontal.putExtra("tipoCamara", "frontal");
            intentFrontal.putExtra("receiver", resultFReceiver);


            Log.d(TAG, "INICIAR PROCESO CAMARA FRONTAL");
            context.startService(intentFrontal);

        } else if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // INICIAR PROCESO CAMARA TRAERA
            resultTReceiver = new ImageTraseraResultReceiver(new Handler());
            Intent intentTrasera = new Intent(context, FotografiaService.class);
            intentTrasera.putExtra("reporteCreado", reporteCreado);
            intentTrasera.putExtra("tipoCamara", "trasera");
            intentTrasera.putExtra("receiver", resultTReceiver);


            Log.d(TAG, "INICIAR PROCESO CAMARA TRASERA");
            context.startService(intentTrasera);
        } else {
            if(procesoImagenFrontal){
                Boolean res = EnviarImagenes.enviarImagenFrontal(contextoGlobal.get(), IMAGEN_FRONTAL, FECHA_FRONTAL, reporteCreado);
                Log.d(TAG, "El resultado del envio de frontal es: "+ res);
            } else{
                // TERMINA PROCESO Y NO ENVIA NADA (3)
                finProcesoFotografias();
            }
        }
    }

    public static void finProcesoFotografias(){
        Log.d(TAG, "Termina proceso vacio de fotografias");
        // Al menos guardar las fotografía en el dispositivo
    }

    public static void terminarFotografias(Context context){
        Intent intentFotos = new Intent(context, FotografiaService.class);
        context.stopService(intentFotos);
    }

    /**********************************************************************************************
     *                                        GPS                                                  *
     **********************************************************************************************/
    public static void comenzarGPS(Context context){
        Log.d(TAG, "GPS " + "Comienza GPS");
        intentGPS = new Intent(context, GPSService.class);
        intentGPS.putExtra("reporteGenerado", reporteCreado);
        intentGPS.putExtra("padre", "MainActivity");
        intentGPS.putExtra("escuchador", "GPSServiceMain");
        context.startService(intentGPS);
    }

    public static void terminarGPS(Context context){
        Log.d(TAG, "GPS " + "Comienza GPS");
        Intent intentGPS = new Intent(context, GPSService.class);
        context.stopService(intentGPS);
    }

    public static void registrarEscuchadorGPS(Context context){
        ESCUCHADOR_GPS = true;
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiverGPSService, new IntentFilter("GPSServiceMain"));
    }

    public static void eliminarEscuchadorGPS(Context context){
        Log.d(TAG, "Eliminando el escuchador GPS");
        ESCUCHADOR_GPS = false;
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiverGPSService);
    }

    private static BroadcastReceiver broadcastReceiverGPSService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive de GPSService");
            Bundle parametros = intent.getExtras();
            double latitud = parametros.getDouble("latitud", 0.0);
            double longitud = parametros.getDouble("longitud", 0.0);
            String fecha = parametros.getString("fecha", "");
            String padre = parametros.getString("padre");

            if (latitud != 0.0 && longitud != 0.0 && !fecha.equals("")){
                // EnviarCoordenadas enviarCoordenadas = new EnviarCoordenadas();
                Log.d("Notif", "Guenas, guenas");
                prepararAlerta(context, modelo_login.getId_comercio(), modelo_login.getId_usuarios_app(), String.valueOf(latitud), String.valueOf(longitud), "Actual", modelo_login.getId_municipio()
                , modelo_login.getClave_municipio());
                // eliminarEscuchadorGPS(context);
                // terminarGPS(context);
                //  Boolean hasGPS = enviarCoordenadas.enviarCoordenadas(context, latitud, longitud, fecha, "Actual", reporte);
                /* if(hasGPS)
                    eliminarEscuchadorGPS(context);
                terminarGPS(context); */
            } else if(latitud == 0.0 && longitud == 0.0){
                // Si reporte generado
                terminarGPS(context);
                // Si el reporte no se había generado, enviarlo sin coordenadas
                prepararAlerta(context, modelo_login.getId_comercio(), modelo_login.getId_usuarios_app(), modelo_login.getId_municipio(), modelo_login.getClave_municipio());
            }
        }
    };

    /**********************************************************************************************
     *                                          AUDIO                                              *
     **********************************************************************************************/

    public static void comenzarGrabacionAudio(Context context){
        Intent intent = new Intent(context, AudioService.class);
        intent.putExtra("nombreAudio", "GrabacionBotonDePanico");
        intent.putExtra("padre", "in");
        intent.putExtra("reporteCreado", reporteCreado);
        intent.putExtra("soloUno", true);

        context.startService(intent);
    }


    /**********************************************************************************************
     *                         RESULT RECEIVERS - FOTOGRAFIAS                                      *
     **********************************************************************************************/

    private static class ImageTraseraResultReceiver extends ResultReceiver {
        public ImageTraseraResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            procesoImageTrasera = true;
            FECHA_TRASERA = resultData.getString("fecha");

            // GUARDAR LA IMAGEN DE RETORNO
            String imagenTrasera = resultData.getString("imagen");
            if(!imagenTrasera.equals("Ninguna"))
                IMAGEN_TRASERA = imagenTrasera;

            switch (resultCode) {
                case ERROR:
                    //Toast.makeText(contextoGlobal.get(), message, Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    if(procesoImagenFrontal){
                        Boolean res = EnviarImagenes.enviarImagenFrontal(contextoGlobal.get(), IMAGEN_FRONTAL, FECHA_FRONTAL, reporteCreado);
                    }
                    if(procesoImageTrasera){
                        Boolean res = EnviarImagenes.enviarImagenTrasera(contextoGlobal.get(), IMAGEN_TRASERA, FECHA_TRASERA, reporteCreado);
                    }
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }
    }

    private static class ImageFrontalResultReceiver extends ResultReceiver {
        public ImageFrontalResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            procesoImagenFrontal = true;
            FECHA_FRONTAL = resultData.getString("fecha");

            // GUARDAR LA IMAGEN DE RETORNO
            String imagenFrontal = resultData.getString("imagen");
            if(!imagenFrontal.equals("Ninguna")){
                IMAGEN_FRONTAL = imagenFrontal;
            }
            switch (resultCode) {
                case ERROR:
                    // Toast.makeText(contextoGlobal.get(), resultData.getString("mensaje"), Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    if (contextoGlobal.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                        // INICIAR PROCESO CAMARA TRAERA
                        resultTReceiver = new ImageTraseraResultReceiver(new Handler());
                        Intent intentTrasera = new Intent( contextoGlobal.get(), FotografiaService.class);
                        intentTrasera.putExtra("reporteCreado", reporteCreado);
                        intentTrasera.putExtra("tipoCamara", "trasera");
                        intentTrasera.putExtra("receiver", resultTReceiver);

                        contextoGlobal.get().startService(intentTrasera);
                        Log.d(TAG, "SE ENVIARON LAS FOTOGRAFIAS");

                    } else {
                        if(procesoImagenFrontal){
                            // ENVIAR LA IMAGEN FRONTAL (5)
                            Boolean res = EnviarImagenes.enviarImagenFrontal(contextoGlobal.get(), IMAGEN_FRONTAL, FECHA_FRONTAL, reporteCreado);
                            Log.d(TAG, "El resultado del envio de frontal es: "+ res);
                        } else{
                            // FIN DEL PROCESO (6)
                            finProcesoFotografias();
                        }
                    }
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"Guardó datos ");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG,"Recuperó datos ");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        terminarFotografias(getApplication());
    }

    /**********************************************************************************************
     *                                 INTERACCIÓN UI                                              *
     **********************************************************************************************/

}
