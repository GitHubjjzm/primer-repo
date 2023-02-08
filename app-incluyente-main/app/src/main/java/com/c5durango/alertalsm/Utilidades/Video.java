package com.c5durango.alertalsm.Utilidades;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.c5durango.alertalsm.Constantes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Video {

    public static void convertirVideo(Context context, Intent data, String TAG){
        int bytesRead, bytesAvailable = 0, bufferSize;
        int maxBufferSize = 1 * 1024 * 1024;

        Uri uri = data.getData();
        String path = uri.getPath();
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        try {
            bytesAvailable = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bufferSize = Math.min(bytesAvailable, maxBufferSize);


        //int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        Log.d(TAG, "EL BUFFER MIDE: " + byteBuffer.size());
        int len = 0;
        try {
            while ( (len = inputStream.read(buffer)) != -1 ){
                byteBuffer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "CONVERTIDO A BYTE BUFFER!!! CON TAMAÑO " + byteBuffer.size());

        String videoData = "";
        if(byteBuffer.size() > 0)
            videoData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
        // Log.d(TAG, "BYTEBUFFER SIZE " + byteBuffer.size());

        Log.d(TAG, "SIN LIMPIAR " + videoData);

        String sinSaltoFinal2 = videoData.trim();
        String sinsinSalto2 = sinSaltoFinal2.replaceAll("\n", "");
        String baseVideo = sinsinSalto2;
        Log.d(TAG, "TOTALMENTE LIMPIO " + baseVideo);
    }

    public static void convertirVideoYotzin(Context context, Intent data, String TAG){
        DataOutputStream dos = null;
        int bytesRead = 0, bytesAvailable = 0, bufferSize;
        byte [] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(data.getData().getPath());
        if(!sourceFile.isFile()){
            Log.d(TAG, "NO ES FILE");
            return;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);


            while (bytesRead > 0){
                // SIN DOS
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

        } catch (FileNotFoundException e){

        } catch (IOException i){

        }

    }

    // NO CODIFICA BIEN 8(

    public static void convertirVideoStack(Context context, Intent data, String TAG){

        Uri uri = data.getData();
        String path = uri.getPath();

        //File sourceFile = new File(data.getData().getPath());
        try {
            InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
            Log.d(TAG, "EL VIDEO PESA: " + fileInputStream.available());

            if(fileInputStream.available() > Constantes.LONGITUD_MAX_MB_VIDEO)
                Log.d(TAG, "EL VIDEO SOBRE PASA EL LIMITE");



            // Si el tamaño del video es mayor, se recorta
            int tamMaxBuffer;
            // esto se extiende dinámicamente para tomar los bytes que lee, se guarda en la memoria
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            // este almacenamiento se sobrescribe en cada iteración con bytes
            int bufferSize = 2048;
            byte[] buffer = new byte[bufferSize];

            // necesitamos saber cómo se leyeron los bytes para escribirlos

            int len = 0;
            Log.d(TAG, "BYTE BUFFER " + byteBuffer.size());
            while ((len = fileInputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            fileInputStream.close();

            String videoData = "";
            if(byteBuffer.size() > 0)
                videoData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);

            // y luego podemos devolver su matriz de bytes.
            Log.d(TAG, "LONGITUD byteBuffer: " + byteBuffer.toByteArray().length) ;
            Log.d(TAG, "LONGITUD BASE 64: " + videoData.length());
            Log.d(TAG, videoData);
            String sinSaltoFinal2 = videoData.trim();
            String sinsinSalto2 = sinSaltoFinal2.replaceAll("\n", "");
            String baseVideo = sinsinSalto2;

            Log.d(TAG, baseVideo);

        } catch (FileNotFoundException e) {
            Log.d(TAG, "NO TA'");
            e.printStackTrace();
        } catch (IOException i){
            Log.d(TAG, "exception");
            i.printStackTrace();
        }




        //Log.d(TAG, "LONGITUD byteBuffer: " +  byteBuffer.toByteArray().length);
        /*DataOutputStream dos = null;
        int bytesRead = 0, bytesAvailable = 0, bufferSize;
        byte [] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(data.getData().getPath());
        if(!sourceFile.isFile()){
            Log.d(TAG, "NO ES FILE");
            return;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);


            while (bytesRead > 0){
                // SIN DOS
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

        } catch (FileNotFoundException e){

        } catch (IOException i){

        }*/

    }
}
