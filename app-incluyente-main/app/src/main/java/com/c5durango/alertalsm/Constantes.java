package com.c5durango.alertalsm;

public class Constantes {



    // CONSTANTES GENERALES
    public static final String CHANNEL_ID = "911_INCLUYENTE_DGO";
    public static String NOMBRE_APP = "9-1-1 Incluyente Dgo";
    public static final int PRIORITY_MAX = 2;
    public static final int ID_SERVICIO_PANICO = 100;
    public static final int ID_SERVICIO_AUDIO = 102;
    public static final int ID_SERVICIO_WIDGET = 101;
    public static final int ID_SERVICIO_VIDEO = 103;
    public static final int ID_SERVICIO_GPS = 104;
    public static final int ID_SERVICIO_BOTONAZO = 105;


    // AUDIO
    public static int DURACION_AUDIO = 30000;
    public static int DURACION_AUDIO_GRABACION = 60000;
    public static String EXTENSION_AUDIO = "mp3";
    public static String NOMBRE_AUDIO = "Grabacion911Incluyente";

    // WIDGET
    public static final int ID_SERVICIO_WIDGET_CREAR_REPORTE = 200;
    public static final int ID_SERVICIO_WIDGET_GENERAR_ALERTA = 201;
    public static final int ID_SERVICIO_WIDGET_ENVIAR_IMG = 202;
    public static final int ID_SERVICIO_WIDGET_GRABAR_AUDIO = 203;
    public static final int ID_SERVICIO_WIDGET_TOMAR_UBICAC = 204;

    public static final int ID_SERVICIO_NOTIFICACION_REPORTE_RECIBIDO = 205;
    // PERMISOS
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final int MY_PERMISSIONS_REQUEST_ALMAC_READ = 101;
    public static final int MY_PERMISSIONS_REQUEST_ALMAC_WRITE = 102;
    public static final int MY_PERMISSIONS_REQUEST_UBICAC = 103;
    public static final int MY_PERMISSIONS_REQUEST_MICROF = 104;
    public static final int MY_PERMISSIONS_REQUEST_UBICAC_BACK = 105;

    // Constantes para reporte
    public static final int DIFERENCIA_ENTRE_REPORTES = 60000; //  60,000 = 1 minuto (600000) = 10Min // 3600000 = 1 hr
    public static final int LAPSO_PARA_CANCELAR_REPORTE = 90000; // 60,000 = 1 minuto

    public static final String LUGAR_ACTUAL = "Actual", LUGAR_CASA = "Casa", LUGAR_GOOGLE = "Otra";

    /* VIDEO */
    public static final long MAX_VIDEO_5MB  = 5242880L;
    public static final long MAX_VIDEO_10MB = 10485760L;
    public static final long MAX_VIDEO_15MB = 15728640L;
    public static final long MAX_VIDEO_20MB = 20971520L;

    public static final int MAX_DURATION_VIDEO_10 = 10;
    public static final int MAX_DURATION_VIDEO_15 = 15;
    public static final int MAX_DURATION_VIDEO_20 = 20;

    public static int LONGITUD_MAX_MB_VIDEO = 30000000; //1,000,000 = 1MB // 30,000,000 = 30MB  ---------> Cada seg pesa 2MB x lo cual 30MB = 15SEG APROX


    // FOTOGRAFIAS
    public static final int SUCCESS = 1;
    public static final int ERROR = 0;

    // URL
    public static String URL = "http://10.11.127.70:8888"; // LOCAL
  //  public static String URL = "http://189.254.158.196:8888";    // IP PUBLICA
  //   public static String URL = "http://10.11.118.91:8888";    // IP PRIVADA
}
