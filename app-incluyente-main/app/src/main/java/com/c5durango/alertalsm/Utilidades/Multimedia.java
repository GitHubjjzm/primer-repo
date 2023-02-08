package com.c5durango.alertalsm.Utilidades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Multimedia {

    public static List<Bitmap> decodificarImagenes(Context context, ArrayList<Uri> arrayUris){
        List<Bitmap> bitmapList = new ArrayList<>();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        for (int i = 0; i< arrayUris.size() ; i++){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                try {
                    Bitmap image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), arrayUris.get(i)));
                    bitmapList.add(image);
                    Log.d("MULTIMEDIA B1", "EXITO " + i + " RUTA " + ImageDecoder.createSource(context.getContentResolver(), arrayUris.get(i)));

                } catch (IOException e) {
                    Log.d("MULTIMEDIA E1", e.getMessage());
                    e.printStackTrace();
                }
            } else {
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), arrayUris.get(i));
                    bitmapList.add(image);
                    Log.d("MULTIMEDIA B2", "EXITO");
                } catch (IOException e) {
                    Log.d("MULTIMEDIA E2", e.getMessage());
                    e.printStackTrace();
                }
            }

        }
        return bitmapList;
    }
}
