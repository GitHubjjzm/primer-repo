package com.c5durango.alertalsm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertalsm.Clases.ModelDescripcion;
import com.c5durango.alertalsm.Clases.ModelEmergencia;
import com.c5durango.alertalsm.Clases.ModelMultimedia;
import com.c5durango.alertalsm.Clases.ModelQuien;
import com.c5durango.alertalsm.Clases.ModelReportesLocal;
import com.c5durango.alertalsm.Clases.ModeloLogin;
import com.c5durango.alertalsm.Clases.ModeloUbicacion;
import com.c5durango.alertalsm.Dialogs.Dialogs;
import com.c5durango.alertalsm.Fragments.MapsFragment;
import com.c5durango.alertalsm.Fragments.QuienFragment;
import com.c5durango.alertalsm.Fragments.DescripcionFragment;
import com.c5durango.alertalsm.Fragments.EmergFragment;
import com.c5durango.alertalsm.Fragments.MultimediaFragment;
import com.c5durango.alertalsm.Fragments.UbicacionFragment;
import com.c5durango.alertalsm.Servicios.AudioService;
import com.c5durango.alertalsm.Servicios.FotografiaService;
import com.c5durango.alertalsm.Servicios.GPSService;
import com.c5durango.alertalsm.Servicios.VideoService;
import com.c5durango.alertalsm.Utilidades.Audio;
import com.c5durango.alertalsm.Utilidades.EnviarImagenes;
import com.c5durango.alertalsm.Utilidades.Imagenes;
import com.c5durango.alertalsm.Utilidades.Notificaciones;
import com.c5durango.alertalsm.Utilidades.PreferencesReporte;
import com.c5durango.alertalsm.Utilidades.UriUtils;
import com.c5durango.alertalsm.Utilidades.Utilidades;
import com.c5durango.alertalsm.DB.DBReportes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.c5durango.alertalsm.Constantes.CHANNEL_ID;
import static com.c5durango.alertalsm.Constantes.ERROR;
import static com.c5durango.alertalsm.Constantes.ID_SERVICIO_WIDGET_GENERAR_ALERTA;
import static com.c5durango.alertalsm.Constantes.SUCCESS;


public class ReporteActivity extends AppCompatActivity implements
        MultimediaFragment.DataListener,
        EmergFragment.DataListener,
        QuienFragment.DataListener,
        DescripcionFragment.DataListener,
        MapsFragment.DataListener,
        UbicacionFragment.DataListener {

    static WeakReference<Context> contextoGlobal;
    static boolean ENVIO_AUDIO = false;

    // UI
    static ImageButton btnAtras, btnAdelante;
    static LinearLayout btnEnviarReporte;
    static LinearLayout btnReportePendiente;
    static ProgressBar progressBar;
    static LinearLayout linearLayoutBotones;
    static LinearLayout linearLayoutBoton;

    // PARA REPORTE CREADO
    public static int contador = 0;
    public static boolean btnPresionado = false;
    static int claseActual = 1;
    static int REPORTE_CREADO = 0;

    // ArrayListMultimedia que sirven solo para inicializar
    static ArrayList<Uri> arrayImagenURI = new ArrayList<>();
    static ArrayList<Uri> arrayAudioURI = new ArrayList<>();
    static ArrayList<Uri> arrayVideoURI = new ArrayList<>();
    static ArrayList<String> listPathAudio = new ArrayList<>();

    // INSTANCIAS DE FRAGMENTS
    static ModelEmergencia modelEmergencia = new ModelEmergencia("Desconocido", "Desconocido");
    static ModelQuien modelQuien = new ModelQuien("Yo");
    static ModelDescripcion modelDescripcion = new ModelDescripcion("");
    static ModeloUbicacion modeloUbicacion = new ModeloUbicacion("Actual", Utilidades.obtenerFecha(), 0.0, 0.0);
    static ModelMultimedia modelMultimedia = new ModelMultimedia(arrayImagenURI, arrayAudioURI, arrayVideoURI, listPathAudio);
    static ModeloLogin modeloLogin = new ModeloLogin(0,0,0, 0);
    private static ManejoFotografias modelManejoFotografias = new ManejoFotografias();

    private static String TAG = "ReporteFragment";
    private static String TAG_AUDIO = "AUDIO_CODIFICACION";
    private static String TAG_IMAGEN = "IMAGEN_CODIFICACION";
    static Boolean procesoImagenFrontal = false;
    static Boolean  procesoImageTrasera = false;
    // VARIABLES PARA USO DE MULTIMEDIA
    static ImageTraseraResultReceiver resultTReceiver;
    static ImageFrontalResultReceiver resultFReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("estatus", "oncreate");
        setContentView(R.layout.activity_reporte);
        contextoGlobal = new WeakReference<>(getApplicationContext());

        // Volver a inicializar los campos del reporte // TODO: MOVER A METODO INDEPENDIENTE
        // Model emergencia
        modelEmergencia.setCuerpoEmergencia("Desconocido");
        modelEmergencia.setTipoEmergencia("Desconocido");
        modelQuien.setQuien("Yo");
        modelDescripcion.setDescripcion("");
        // Model ubicación
        modeloUbicacion.setLugar("Actual");
        modeloUbicacion.setFechaHora(Utilidades.obtenerFecha());
        modeloUbicacion.setLatitud(0.0);
        modeloUbicacion.setLongitud(0.0);
        // Model multimedia
        arrayImagenURI.clear();
        arrayAudioURI.clear();
        arrayVideoURI.clear();
        listPathAudio.clear();
        modelMultimedia.setArrayImagenURI(arrayImagenURI);
        modelMultimedia.setArrayAudioURI(arrayAudioURI);
        modelMultimedia.setArrayVideoURI(arrayVideoURI);
        modelMultimedia.setListPathAudio(listPathAudio);
        // Model login
        modeloLogin.setId_comercio(0);
        modeloLogin.setId_usuarios_app(0);
        modeloLogin.setId_municipio(0);
        modeloLogin.setClave_municipio(0);

        try {
            Bundle argsUbi = new Bundle();
            // Enviar datos de ubicación
            argsUbi.putString("lugar", modeloUbicacion.getLugar());
            argsUbi.putString("fecha", modeloUbicacion.getFechaHora());
            argsUbi.putDouble("latitud", modeloUbicacion.getLatitud());
            argsUbi.putDouble("longitud", modeloUbicacion.getLongitud());

            UbicacionFragment ubicacionFragment = new UbicacionFragment();
            ubicacionFragment.setArguments(argsUbi);
            // Agregar fragment por default (Ubicación)
            getSupportFragmentManager().beginTransaction().add(R.id.frContenedor, ubicacionFragment).commit();

        } catch (Exception e){
            Log.d("UBICACION", e.getMessage());
        }

        btnAtras = findViewById(R.id.btnAtras);
        btnAdelante = findViewById(R.id.btnAdelante);
        btnEnviarReporte = findViewById(R.id.btnEnviarReporte);
        btnEnviarReporte.setVisibility(View.INVISIBLE);
        btnReportePendiente = findViewById(R.id.btnReportePendiente);
        btnReportePendiente.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBarReporte);
        progressBar.setVisibility(View.GONE);
        linearLayoutBotones = findViewById(R.id.linearLayoutBotones);
        linearLayoutBoton = findViewById(R.id.linearLayoutBoton);

        if(!getIntent().getExtras().containsKey("id_usuario") || !getIntent().getExtras().containsKey("id_comercio")){
            Toast.makeText(getApplicationContext(), "No se encuentra registrado para generar reportes", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            ReporteActivity.this.finish();
            return;
        }
        modeloLogin.setId_usuarios_app(getIntent().getIntExtra("id_usuario", 0));
        modeloLogin.setId_comercio(getIntent().getIntExtra("id_comercio", 0));
        modeloLogin.setId_municipio(getIntent().getIntExtra("id_municipio", 0));
        modeloLogin.setClave_municipio(getIntent().getIntExtra("clave_municipio", 0));

        // COMENZAR A GENERAR LA MULTIMEDIA
        registrarEscuchadorGPS(contextoGlobal.get());
        comenzarGPS(getApplicationContext(), -1, ReporteActivity.this);
    }



    public void adelante(View view){
        claseActual ++;
        cambiarClase(claseActual);
    }

    public void atras(View view){
        claseActual--;
        if(claseActual == 0 ) {
            claseActual = 5;
        }
        cambiarClase(claseActual);

    }

    public void volverHome(View view){
        // Cuando regresa a la pantalla principal
        contador = 0;
        cambiarClase(0);
        // comenzarGPS(getApplicationContext(), -1, ReporteActivity.this);
    }

    public void ocultarLinearBotones(){
        if(linearLayoutBotones.getVisibility() == View.GONE){
            linearLayoutBotones.setVisibility(View.VISIBLE);
        }
        else {
            linearLayoutBotones.setVisibility(View.GONE);
        }
    }

    public void ocultarLinearBoton(){
        if(linearLayoutBoton.getVisibility() == View.VISIBLE){
            linearLayoutBoton.setVisibility(View.GONE);
        }
        else {
            linearLayoutBoton.setVisibility(View.VISIBLE);
        }
    }

    public void okMaps(View view){
        ocultarLinearBotones();
        ocultarLinearBoton();
        claseActual = 1;
        cambiarClase(claseActual);
    }

    public void okNO(View view){
        ocultarLinearBotones();
        ocultarLinearBoton();
        claseActual = 1;
        cambiarClase(claseActual);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("estatus", "onresume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("estatus", "onpause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("estatus", "onrestart");
    }

    public void onFinish(View view){
        contador++;
        // Validar si se puede generar una nueva alerta
        Boolean puedeEnviar = PreferencesReporte.puedeEnviarReporte(getApplicationContext(), System.currentTimeMillis());

        if(puedeEnviar && contador <= 1) {
            Dialogs.abrirVentanaMensaje(ReporteActivity.this, "Enviando reporte", "Espere un momento", R.drawable.ic_color_info, View.VISIBLE, View.GONE, false);
            //mostrarResultadoVista(R.drawable.ic_color_send, "Enviando..");
            PreferencesReporte.guardarReporteInicializado(getApplicationContext());
            enviarAlerta(getApplicationContext());
        } else if(puedeEnviar && contador > 1 ){
            Log.d(TAG, "No puedo enviar por que estoy en espera.. " + contador);
        }
    }

    private static void comprobarEnvioVideo(int reporteCreado){
        /*  VIDEO   */
        if(modelMultimedia.getArrayVideoURI().size() > 0){
            codificarVideoURI(reporteCreado);
        } else{
            // No tomar video
        }
    }

    private static void comprobarEnvioFotografias(Activity activity){
        /*  IMAGENES    */
        if(modelMultimedia.getArrayImagenURI().size() > 0){
            codificarImagenesURI();
        } else{
            if(ActivityCompat.checkSelfPermission(contextoGlobal.get(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                Dialogs.cerrarVentanaMensaje();
                Dialogs.abrirVentanaMensaje(activity, "Atención", "Tomando fotografías", R.drawable.ic_image, View.GONE, View.VISIBLE, false);
                iniciarProcesoFotografias(contextoGlobal.get());
            } else {
                Log.e(TAG, "No tiene permisos de camara");
            }
        }
    }

    private static void codificarImagenesURI(){
        try {
            inhabilitarPantalla();
            ArrayList<String> arrayListImgString = new ArrayList<>();
            for (int i = 0; i < modelMultimedia.getArrayImagenURI().size()  ; i++){
                Log.d(TAG, modelMultimedia.getArrayImagenURI().get(i).toString());
                Bitmap imgBitmap = Imagenes.convertirImgURIBitmap(contextoGlobal.get(), modelMultimedia.getArrayImagenURI().get(i));

                if(imgBitmap!= null){
                    String imgString = Imagenes.convertirImgString(imgBitmap);
                    if(imgString.length() > 3){
                        arrayListImgString.add(imgString);
                    } else {
                        Toast.makeText(contextoGlobal.get(), "Ocurrió un error al codificar imagen", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(contextoGlobal.get(), "Ocurrió un error al codificar imagen", Toast.LENGTH_LONG).show();
                }
            }
            // Enviar imagenes ya codificadas
            for (int i = 0; i <arrayListImgString.size()  ; i++){
                EnviarImagenes.enviarImagenFrontal(contextoGlobal.get(), arrayListImgString.get(i), Utilidades.obtenerFechaAleatoria(), REPORTE_CREADO);
            }
            habilitarPantalla();
        } catch (Exception e){
            Log.e(TAG, "ERROR AL CODIFICAR IMAGENES: " + e.getMessage());
        }
    }

    private static void codificarAudioURI(){
        try {
            inhabilitarPantalla();
            ArrayList<String> pathsAudio = new ArrayList<>();
            for (int a=0; a < modelMultimedia.getArrayAudioURI().size(); a++){
                pathsAudio.add(UriUtils.getPathFromUri(contextoGlobal.get(), modelMultimedia.getArrayAudioURI().get(a)));
            }

            // Array List que se va a enviar
            ArrayList<String> base64Audio = new ArrayList<>();
            for (int p=0; p < pathsAudio.size(); p++){
                try {
                    String base64 = Audio.convertirAudioString(pathsAudio.get(p));
                    base64Audio.add(base64);
                    //Log.d(TAG_AUDIO, base64Audio.get(p).length() +" MIDE EL AUDIO EN POSICIÓN: " + p);
                } catch (Exception a){ // TAMAÑO MAX PARA STRING = 2 147 483 647
                    Toast.makeText(contextoGlobal.get(), "El audio es demasiado largo:\n El peso máximo es de 50MB\n" + a.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            for (int a=0; a < base64Audio.size(); a++){
                Audio.enviarAudioVolley(contextoGlobal.get(), base64Audio.get(a), Utilidades.obtenerFechaAleatoria(), REPORTE_CREADO);
            }
            habilitarPantalla();
        } catch (Exception e){
            e.getMessage();
            Toast.makeText(contextoGlobal.get(), "ERROR AL CODIFICAR AUDIO:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private static void codificarVideoURI(int id_reporte){
        try {
            Intent ivideo = new Intent(contextoGlobal.get(), VideoService.class);

            ivideo.putStringArrayListExtra("arrayVideoURI", (ArrayList<String>) (ArrayList<?>) modelMultimedia.getArrayVideoURI());
            ivideo.putExtra("fecha", "FECHA");
            ivideo.putExtra("id_reporte", id_reporte);
            contextoGlobal.get().startService(ivideo);
        } catch (Exception e){
            Toast.makeText(contextoGlobal.get(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void codificarAudioPath(){
        // Array List que se va a enviar
        inhabilitarPantalla();
        ArrayList<String> base64Audio = new ArrayList<>();
        for (int a = 0; a < modelMultimedia.getListPathAudio().size(); a++){
            File file = new File(modelMultimedia.getListPathAudio().get(a));
            if(file.exists()){
                try{
                    String base64 = Audio.convertirAudioString(modelMultimedia.getListPathAudio().get(a));
                    base64Audio.add(base64);
                } catch (Exception e){ // TAMAÑO MAX PARA STRING = 2 147 483 647
                    Toast.makeText(contextoGlobal.get(), "El audio es demasiado largo:\n El peso máximo es de 50MB\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else
                Log.d(TAG_AUDIO, "El audio no existe");
        }

        for (int a=0; a < base64Audio.size(); a++){
            Audio.enviarAudioVolley(contextoGlobal.get(), base64Audio.get(a), Utilidades.obtenerFechaAleatoria(), REPORTE_CREADO);
        }
        habilitarPantalla();
        Log.d(TAG_AUDIO, "Al final codifiqué " + base64Audio.size() + " AUDIOS (GRABADOS)");

    }

    private static void comprobarEnviarAudio(int reporteCreado){
        /*  AUDIO   */
        if(modelMultimedia.getArrayAudioURI().size() > 0){
            codificarAudioURI();
        }

        if(modelMultimedia.getListPathAudio().size() > 0){
            codificarAudioPath();
        }

        if(modelMultimedia.getArrayAudioURI().size() == 0 && modelMultimedia.getListPathAudio().size() == 0){
            grabarUnAudio(reporteCreado);
        }
    }

    private static void grabarUnAudio(int reporteCreado){
        Intent intent = new Intent(contextoGlobal.get(), AudioService.class);
        intent.putExtra("nombreAudio", Constantes.NOMBRE_AUDIO);
        intent.putExtra("padre", "in");
        intent.putExtra("reporteCreado", reporteCreado);
        intent.putExtra("soloUno", true);
        if(!ENVIO_AUDIO){
            ENVIO_AUDIO = true;
            contextoGlobal.get().startService(intent);
        }
    }

    /**********************************************************************************************
     *                                     FOTOGRAFIAS                                             *
     **********************************************************************************************/

    public static void iniciarProcesoFotografias(Context context){

        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            // INICIAR PROCESO CAMARA FRONTAL
            resultFReceiver = new ImageFrontalResultReceiver(new Handler());
            Intent intentFrontal = new Intent(context, FotografiaService.class);
            intentFrontal.putExtra("reporteCreado", REPORTE_CREADO);
            intentFrontal.putExtra("tipoCamara", "frontal");
            intentFrontal.putExtra("receiver", resultFReceiver);
            context.startService(intentFrontal);

        } else if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // INICIAR PROCESO CAMARA TRAERA
            resultTReceiver = new ImageTraseraResultReceiver(new Handler());
            Intent intentTrasera = new Intent(context, FotografiaService.class);
            intentTrasera.putExtra("reporteCreado", REPORTE_CREADO);
            intentTrasera.putExtra("tipoCamara", "trasera");
            intentTrasera.putExtra("receiver", resultTReceiver);
            context.startService(intentTrasera);
        } else {
            if(procesoImagenFrontal){
                Boolean res = EnviarImagenes.enviarImagenFrontal(context, modelManejoFotografias.getIMAGEN_FRONTAL(), modelManejoFotografias.getFECHA_FRONTAL(), REPORTE_CREADO);
            }
        }
    }

    @Override
    public void sendCoordenadas(String lugar, String fecha, double latitud, double longitud) {
        modeloUbicacion.setLugar(lugar);
        modeloUbicacion.setFechaHora(fecha);
        modeloUbicacion.setLatitud(latitud);
        modeloUbicacion.setLongitud(longitud);

        Log.d(TAG, modeloUbicacion.getLugar() + " : " + modeloUbicacion.getFechaHora() + " : " + modeloUbicacion.getLatitud() + " : " + modeloUbicacion.getLongitud());
    }

    @Override
    public void mostrarLayout() {
        //ocultarLinearBotones();
        ocultarLinearBoton();
    }

    @Override
    public void abrirMaps() {
        ocultarLinearBotones();
        try {

            Bundle argsMaps = new Bundle();
            argsMaps.putString("lugar", modeloUbicacion.getLugar());
            argsMaps.putDouble("latitud", modeloUbicacion.getLatitud());
            argsMaps.putDouble("longitud", modeloUbicacion.getLongitud());


            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right, R.anim.slide_right);
            MapsFragment mapsFragment = new MapsFragment();
            mapsFragment.setArguments(argsMaps);

            transaction.replace(R.id.frContenedor, mapsFragment);
            transaction.commit();
        } catch (Exception e){
            Log.d("MAPS FRAGMENT", e.getMessage());
        }
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
            modelManejoFotografias.setFECHA_TRASERA(resultData.getString("fecha"));

            // GUARDAR LA IMAGEN DE RETORNO
            String imagenTrasera = resultData.getString("imagen");
            if(!imagenTrasera.equals("Ninguna"))
                modelManejoFotografias.setIMAGEN_TRASERA(imagenTrasera);

            switch (resultCode) {
                case ERROR:
                    //Toast.makeText(contextoGlobal.get(), message, Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    if(procesoImagenFrontal){
                        Boolean res = EnviarImagenes.enviarImagenFrontal(contextoGlobal.get(), modelManejoFotografias.getIMAGEN_FRONTAL(), modelManejoFotografias.getFECHA_FRONTAL(), REPORTE_CREADO);
                    }
                    if(procesoImageTrasera){
                        Boolean res = EnviarImagenes.enviarImagenTrasera(contextoGlobal.get(), modelManejoFotografias.getIMAGEN_TRASERA(), modelManejoFotografias.getFECHA_TRASERA(), REPORTE_CREADO);
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
            modelManejoFotografias.setFECHA_FRONTAL(resultData.getString("fecha"));

            // GUARDAR LA IMAGEN DE RETORNO
            String imagenFrontal = resultData.getString("imagen");
            if(!imagenFrontal.equals("Ninguna")){
                modelManejoFotografias.setIMAGEN_FRONTAL(imagenFrontal);
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
                        intentTrasera.putExtra("reporteCreado", REPORTE_CREADO);
                        intentTrasera.putExtra("tipoCamara", "trasera");
                        intentTrasera.putExtra("receiver", resultTReceiver);
                        contextoGlobal.get().startService(intentTrasera);
                    } else {
                        if(procesoImagenFrontal){
                            // ENVIAR LA IMAGEN FRONTAL (5)
                            Boolean res = EnviarImagenes.enviarImagenFrontal(contextoGlobal.get(), modelManejoFotografias.getIMAGEN_FRONTAL(), modelManejoFotografias.getFECHA_FRONTAL(), REPORTE_CREADO);
                        }
                    }
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }
    }

    /*******************************************************
    //             GPS
    *******************************************************/

    public static void comenzarGPS(Context context, int reporteCreado, Activity activity){
        Dialogs.abrirVentanaMensaje(activity,"Ubicación", "Buscando ubicación actual..", R.drawable.ic_color_info, View.VISIBLE, View.GONE, false);
        Intent intentGPS = new Intent(context, GPSService.class);
        intentGPS.putExtra("reporteGenerado", reporteCreado);
        intentGPS.putExtra("padre", "MainActivity");
        intentGPS.putExtra("escuchador","GPSServiceReporte");
        context.startService(intentGPS);
    }

    public static void terminarGPS(Context context){
        Intent intentGPS = new Intent(context, GPSService.class);
        context.stopService(intentGPS);
    }

    public void registrarEscuchadorGPS(Context context){
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiverGPSService, new IntentFilter("GPSServiceReporte"));
    }

    public void eliminarEscuchadorGPS(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiverGPSService);
    }

    public BroadcastReceiver broadcastReceiverGPSService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle parametros = intent.getExtras();
            double latitud = parametros.getDouble("latitud", 0.0);
            double longitud = parametros.getDouble("longitud", 0.0);
            String fecha = parametros.getString("fecha", "");
            int reporte = parametros.getInt("reporteCreado", 0);

            // Log.d("BANANO", "RECIBÍ GEOLOCALIZACIÓN: " + latitud + " : " + longitud);

            Dialogs.cerrarVentanaMensaje();
            if (latitud != 0.0){
                modeloUbicacion.setFechaHora(fecha);
                modeloUbicacion.setLugar("Actual");
                modeloUbicacion.setLatitud(latitud);
                modeloUbicacion.setLongitud(longitud);
            } else {
                // Seleccionar en casa como default
                Toast.makeText(context, parametros.getString("mensaje") + "\nSe enviará por default 'EN CASA'.", Toast.LENGTH_LONG).show();
                modeloUbicacion.setFechaHora(fecha);
                modeloUbicacion.setLugar("Casa");
                modeloUbicacion.setLongitud(0.0);
                modeloUbicacion.setLatitud(0.0);
                cambiarClase(1);
            }
            // INSEGURA
            // eliminarEscuchadorGPS(contextoGlobal.get());
            terminarGPS(contextoGlobal.get());

        }
    };
    /*
    * Obtener los ID's
    * Comprobar si tengo fotos y si no, tomarlas
    * Comprobar si tengo audio
    * Comprobar si tengo localización y si no, tomarla
    * Comprobar si tengo video*/

     /*******************************************************
     //             Data Listener de fragments
     *******************************************************/

    @Override
    public void sendDataEmerg(String cuerpo, String tipoEmergencia, Boolean cerrar) {
        modelEmergencia.setTipoEmergencia(tipoEmergencia);
        modelEmergencia.setCuerpoEmergencia(cuerpo);
        Log.d(TAG, cuerpo + " :: " + tipoEmergencia);
        if (cerrar){
            claseActual ++;
            cambiarClase(claseActual);
        }
    }


    @Override
    public void sendDataQuien(String quien) {
        modelQuien.setQuien(quien);
        Log.d(TAG, "RECIBI DE QUIEN: " + quien);
    }

    @Override
    public void sendDataDescripcion(String descripcion) {
        modelDescripcion.setDescripcion(descripcion);
        Log.d(TAG, modelDescripcion.getDescripcion());
    }

    @Override
    public void sendMultimedia(ArrayList<Uri> arrayListImagenURI, ArrayList<Uri> arrayListAudioURI, ArrayList<Uri> arrayListVideoURI, ArrayList<String> listPathAudios) {
        modelMultimedia.setArrayImagenURI(arrayListImagenURI);
        modelMultimedia.setArrayAudioURI(arrayListAudioURI);
        modelMultimedia.setArrayVideoURI(arrayListVideoURI);
        modelMultimedia.setListPathAudio(listPathAudios);

        Log.d(TAG, "MODEL MULTIMEDIA IMAGEN: " + modelMultimedia.getArrayImagenURI().size());
        //Log.d(TAG, modelMultimedia.getArrayImagenURI().get(0).toString());
        Log.d(TAG, "MODEL MULTIMEDIA VIDEO: " + modelMultimedia.getArrayVideoURI().size());
        Log.d(TAG, "MODEL MULTIMEDIA PATH: " + modelMultimedia.getListPathAudio().size());

    }

    @Override
    public void sendUbicacion(String lugar, String fechaHora, double latitud, double longitug) {
        modeloUbicacion.setLugar(lugar);
        modeloUbicacion.setFechaHora(fechaHora);
        modeloUbicacion.setLatitud(latitud);
        modeloUbicacion.setLongitud(longitug);

        if (lugar.equals("Actual")){
            //Log.d("BANANO", "RECIBI UBICACIÓN ACTUAL Y DEBO ABRIR VENTANA ");
            Dialogs.abrirVentanaMensaje(ReporteActivity.this,"Ubicación", "Buscando ubicación actual..", R.drawable.ic_color_info, View.VISIBLE, View.GONE, false);
            comenzarGPS(getApplicationContext(), -1, ReporteActivity.this);
        }
        Log.d(TAG, "Recibi la geolocalización: " + modeloUbicacion.getLugar() + " : " + modeloUbicacion.getFechaHora() + " :: " + modeloUbicacion.getLatitud() + " ::: " + modeloUbicacion.getLongitud());
    }

    private static void inhabilitarPantalla(){
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private static void habilitarPantalla(){
        progressBar.setVisibility(View.GONE);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }



    public static void prepararAlertaNotificacion(final Context context, int idComercio, int idUsuario, String latitud, String longitud, String tipo_ubic, int id_municipio, int clave_municipio){
        String URL = Constantes.URL + "/alerta/coordenadas/";
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
        } catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        /*if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            comprobarEnvioGPS(REPORTE_CREADO);

        } else {
            // Enviarla sin ubicación
            Log.e(TAG, "No tiene permisos GPS");
        }*/
    }

    public void enviarAlerta(final Context context){

        JsonObjectRequest requestAlerta;
        String URL = Constantes.URL + "/alerta/notificacion/coordenadas/";
        String fechaHora = Utilidades.obtenerFecha();
        //if(modeloUbicacion.getLugar() == "Actual" || modeloUbicacion.getLugar() == "Otra")
        //    URL = URL + "coordenadas/";

        // Desactivar botón para no generar un nuevo reporte
        btnEnviarReporte.setVisibility(View.INVISIBLE);

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idComercio", modeloLogin.getId_comercio());
            jsonObject.put("idUsuario", modeloLogin.getId_usuarios_app());
            jsonObject.put("fecha" , fechaHora);
            jsonObject.put("cuerpo", modelEmergencia.getCuerpoEmergencia());
            jsonObject.put("tipoIncidUser", modelEmergencia.getTipoEmergencia());
            jsonObject.put("descripEmergUser", modelDescripcion.getDescripcion());
            jsonObject.put("quien", modelQuien.getQuien());

            //if(modeloUbicacion.getLugar() == "Actual" || modeloUbicacion.getLugar() == "Otra"){
                jsonObject.put("latitud", modeloUbicacion.getLatitud());
                jsonObject.put("longitud", modeloUbicacion.getLongitud());
                jsonObject.put("lugar", modeloUbicacion.getLugar());
                jsonObject.put("id_municipio", modeloLogin.getId_municipio());
                jsonObject.put("clave_municipio", modeloLogin.getClave_municipio());
            //}
        }catch (JSONException e){
            e.printStackTrace();
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
                                // GUARDAR INFORMACION DEL ULTIMO REPORTE GENERADO
                                REPORTE_CREADO = response.getInt("reporteCreado");
                                guardarInformacionSQLite(context, fechaHora);

                                Notificaciones notificaciones = new Notificaciones();
                                notificaciones.crearNotificacionNormal(context, CHANNEL_ID,  R.drawable.ic_color_success, "¡Reporte enviado!", "Se generó reporte con folio #" + REPORTE_CREADO, ID_SERVICIO_WIDGET_GENERAR_ALERTA);

                                //mostrarResultadoVista();
                                //Toast.makeText(getApplicationContext(), "¡Éxito al generar reporte #" + reporteCreado + "\nEnviando multimedia..", Toast.LENGTH_SHORT).show();
                                //mostrarResultadoVista(R.drawable.ic_color_success,);

                                PreferencesReporte.actualizarUltimoReporte(context, REPORTE_CREADO);
                                Dialogs.cerrarVentanaMensaje();
                                btnPresionado = true;

                                // Solo grabar audio para API 23 en adelante
                                if( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                                        // Validar si el servicio está activo
                                        if(!Utilidades.isMyServiceRunning(context, AudioService.class)){
                                            comprobarEnviarAudio(REPORTE_CREADO);
                                        }
                                    }
                                }

                                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                                    comprobarEnvioFotografias(ReporteActivity.this);
                                } else {
                                    Log.e(TAG, "No tiene permisos de camara");
                                }

                                try {
                                    comprobarEnvioVideo(REPORTE_CREADO);
                                } catch(Exception e){
                                    e.printStackTrace();
                                }

                                eliminarEscuchadorGPS(context);

                                // Volver a la pantalla de inicio y notificar éxito
                                Toast.makeText(context,"Se generó reporte con folio #" + REPORTE_CREADO, Toast.LENGTH_LONG).show();
                                /*Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);*/

                                finish();
                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                Notificaciones notificaciones = new Notificaciones();
                                notificaciones.crearNotificacionNormal(context, CHANNEL_ID,  R.drawable.ic_color_error, "¡No se pudo generar el reporte!", response.getString("message"), ID_SERVICIO_WIDGET_GENERAR_ALERTA);
                                Dialogs.cerrarVentanaMensaje();
                                btnPresionado = false;
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
                notificaciones.crearNotificacionNormal(context, CHANNEL_ID,  R.drawable.ic_color_error, "¡No se pudo generar el reporte!", errorResp, ID_SERVICIO_WIDGET_GENERAR_ALERTA);
                Dialogs.cerrarVentanaMensaje();

                // Reiniciar campos para permitir generar otra alerta
                btnPresionado = false;
                contador = 0;
                btnEnviarReporte.setVisibility(View.VISIBLE);

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

    public void cambiarClase(int nueva){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right, R.anim.slide_right);
        switch (nueva){
            case 0:
                try {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e){
                    Log.d("MENU_ACTIVITY", e.getMessage());
                }
                break;
            case 1:
                try {
                    Bundle argsUbi = new Bundle();
                    // Enviar datos de ubicación
                    argsUbi.putString("lugar", modeloUbicacion.getLugar());
                    argsUbi.putString("fecha", modeloUbicacion.getFechaHora());
                    argsUbi.putDouble("latitud", modeloUbicacion.getLatitud());
                    argsUbi.putDouble("longitud", modeloUbicacion.getLongitud());

                    UbicacionFragment ubicacionFragment = new UbicacionFragment();
                    ubicacionFragment.setArguments(argsUbi);

                    btnEnviarReporte.setVisibility(View.INVISIBLE);
                    btnReportePendiente.setVisibility(View.GONE);

                    transaction.replace(R.id.frContenedor, ubicacionFragment);
                    transaction.commit();
                } catch (Exception e){
                    Log.d("UBICACION", e.getMessage());
                }
                break;
            case 2:
                try {
                    Bundle argsEmerg = new Bundle();
                    argsEmerg.putString("cuerpo", modelEmergencia.getCuerpoEmergencia());
                    argsEmerg.putString("tipoEmerg", modelEmergencia.getTipoEmergencia());

                    EmergFragment emergFragment = new EmergFragment();
                    emergFragment.setArguments(argsEmerg);

                    btnEnviarReporte.setVisibility(View.INVISIBLE);
                    btnReportePendiente.setVisibility(View.GONE);

                    transaction.replace(R.id.frContenedor, emergFragment);
                    transaction.commit();
                } catch (Exception e){
                    Log.d("EMERGENCIA", e.getMessage());
                }

                break;
            case 3:
                try {
                    Bundle argsQuien = new Bundle();
                    argsQuien.putString("quien", modelQuien.getQuien());

                    QuienFragment quienFragment = new QuienFragment();
                    quienFragment.setArguments(argsQuien);

                    btnEnviarReporte.setVisibility(View.INVISIBLE);
                    btnReportePendiente.setVisibility(View.GONE);

                    transaction.replace(R.id.frContenedor, quienFragment);
                    transaction.commit();
                } catch (Exception e){
                    Log.d("QUIEN", e.getMessage());
                }
                break;
            case 4:
                try {
                    Bundle argsDesc = new Bundle();
                    argsDesc.putString("descripcion", modelDescripcion.getDescripcion());

                    DescripcionFragment descripcionFragment = new DescripcionFragment();
                    descripcionFragment.setArguments(argsDesc);

                    btnEnviarReporte.setVisibility(View.INVISIBLE);
                    btnReportePendiente.setVisibility(View.GONE);

                    transaction.replace(R.id.frContenedor, descripcionFragment);
                    transaction.commit();
                } catch (Exception e){
                    Log.d("DESCRIPCION", e.getMessage());
                }
                break;
            case 5:

                try {
                    Bundle argsMult = new Bundle();
                    argsMult.putStringArrayList("arrayImagenURI", (ArrayList<String>)(ArrayList<?>) modelMultimedia.getArrayImagenURI());
                    argsMult.putStringArrayList("arrayAudioURI", (ArrayList<String>)(ArrayList<?>) modelMultimedia.getArrayAudioURI());
                    argsMult.putStringArrayList("arrayVideoURI", (ArrayList<String>)(ArrayList<?>) modelMultimedia.getArrayVideoURI());
                    argsMult.putStringArrayList("listPathAudios", modelMultimedia.getListPathAudio());

                    MultimediaFragment multimediaFragment = new MultimediaFragment();
                    multimediaFragment.setArguments(argsMult);

                    // Hacer visible solo si ya pasó el tiempo limite
                    if(PreferencesReporte.puedeEnviarReporteBool(getApplicationContext(), System.currentTimeMillis())){
                        btnEnviarReporte.setVisibility(View.VISIBLE);
                        btnReportePendiente.setVisibility(View.GONE);
                    } else {
                        // Mostrar para botonazo
                        btnEnviarReporte.setVisibility(View.GONE);
                        btnReportePendiente.setVisibility(View.VISIBLE);
                    }

                    transaction.replace(R.id.frContenedor, multimediaFragment);
                    transaction.commit();
                } catch (Exception e){
                    Log.d("MULTIMEDIA", e.getMessage());
                }
                break;

            default:
                try {
                    Bundle argsUbi = new Bundle();
                    // Enviar datos de ubicación
                    argsUbi.putString("lugar", modeloUbicacion.getLugar());
                    argsUbi.putString("fecha", modeloUbicacion.getFechaHora());
                    argsUbi.putDouble("latitud", modeloUbicacion.getLatitud());
                    argsUbi.putDouble("longitud", modeloUbicacion.getLongitud());

                    UbicacionFragment ubicacionFragment = new UbicacionFragment();
                    ubicacionFragment.setArguments(argsUbi);

                    btnEnviarReporte.setVisibility(View.INVISIBLE);
                    btnReportePendiente.setVisibility(View.GONE);

                    transaction.replace(R.id.frContenedor, ubicacionFragment);
                    transaction.commit();
                    claseActual = 1;
                } catch (Exception e){
                    Log.d("UBICACION", e.getMessage());
                }

                /*Bundle argsEmerg = new Bundle();
                argsEmerg.putString("cuerpo", modelEmergencia.getCuerpoEmergencia());
                argsEmerg.putString("tipoEmerg", modelEmergencia.getTipoEmergencia());

                EmergFragment emergFragment = new EmergFragment();
                emergFragment.setArguments(argsEmerg);

                transaction.replace(R.id.frContenedor, emergFragment);
                transaction.commit();*/
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("estatus", "ondestroy");
        super.onDestroy();
        eliminarEscuchadorGPS(getApplicationContext());
    }

    private void guardarInformacionSQLite(Context context, String fechaHora){
        try {
            // Guardar la información también en SQLite
            ModelReportesLocal model = new ModelReportesLocal();
            model.setId_reporte(REPORTE_CREADO);
            model.setFecha_hora(fechaHora);
            model.setLatitud(modeloUbicacion.getLatitud());
            model.setLongitud(modeloUbicacion.getLongitud());
            model.setLugar(modeloUbicacion.getLugar());
            model.setIncidente(modelEmergencia.getCuerpoEmergencia());
            model.setSubtipo(modelEmergencia.getTipoEmergencia());
            model.setVictimas(modelQuien.getQuien());
            model.setDescripcion(modelDescripcion.getDescripcion());
            model.setImagenes(modelMultimedia.getArrayImagenURI().size());
            model.setAudio((modelMultimedia.getArrayAudioURI().size() + modelMultimedia.getListPathAudio().size()));
            model.setVideo(modelMultimedia.getArrayVideoURI().size());
            model.setEstatus("Enviado");

            DBReportes dbReportes = new DBReportes(context);
            long id = dbReportes.insertarReporte(model);
            if (id > 0) {
                Log.d(TAG, "REGISTRO GUARDADO CON ÉXITO");
            } else {
                Log.d(TAG, "ERROR AL GUARDAR REPORTE");
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public static class ManejoFotografias{
        String IMAGEN_FRONTAL = "Ninguna";
        String IMAGEN_TRASERA = "Ninguna";
        String FECHA_FRONTAL = "";
        String FECHA_TRASERA = "";

        public String getIMAGEN_FRONTAL() {
            return IMAGEN_FRONTAL;
        }

        public void setIMAGEN_FRONTAL(String IMAGEN_FRONTAL) {
            this.IMAGEN_FRONTAL = IMAGEN_FRONTAL;
        }

        public String getIMAGEN_TRASERA() {
            return IMAGEN_TRASERA;
        }

        public void setIMAGEN_TRASERA(String IMAGEN_TRASERA) {
            this.IMAGEN_TRASERA = IMAGEN_TRASERA;
        }

        public String getFECHA_FRONTAL() {
            return FECHA_FRONTAL;
        }

        public void setFECHA_FRONTAL(String FECHA_FRONTAL) {
            this.FECHA_FRONTAL = FECHA_FRONTAL;
        }

        public String getFECHA_TRASERA() {
            return FECHA_TRASERA;
        }

        public void setFECHA_TRASERA(String FECHA_TRASERA) {
            this.FECHA_TRASERA = FECHA_TRASERA;
        }
    }


}
