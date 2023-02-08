package com.c5durango.alertalsm.Utilidades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Imagenes {

    static String TAG = "IMAGENES_CODIFICACION";

    public static Bitmap convertirImgURIBitmap(Context context, Uri uri){
        Bitmap bitmap = null;

        if(Build.VERSION.SDK_INT < 28){
            try {
                if(uri == null){
                    return null;
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), uri);
                }
                return bitmap;
            } catch (FileNotFoundException notf){
                Log.d(TAG, "Archivo no encontrado URI -> Bitmap (API < 28)" );
                return null;
            } catch (IOException io){
                Log.d(TAG, "IO Exception Img URI -> Bitmap (API < 28)" );
                return null;
            }
        } else {
            try{
                ImageDecoder.Source source = ImageDecoder.createSource(context.getApplicationContext().getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);
                return bitmap;
            } catch (IOException io){
                Log.d(TAG, "IO Exception Img URI -> Bitmap (API >= 28)" );
                return null;
            }
        }
    }

    public static String convertirImgString(Bitmap bitmap){
        try{
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
            byte[] imagenByte = array.toByteArray();
            String imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT);
            return imagenString;
        } catch (Exception e){
            return "";
        }
    }




}
