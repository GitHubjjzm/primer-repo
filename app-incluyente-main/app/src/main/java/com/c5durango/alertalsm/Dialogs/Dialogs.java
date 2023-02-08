package com.c5durango.alertalsm.Dialogs;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.c5durango.alertalsm.R;

public class Dialogs {

    private static AlertDialog.Builder dialogBuilderMensage;
    private static AlertDialog dialogMessage;
    private static TextView txtTituloMessage, txtMessage;
    private static ImageView imgMessage;
    private static ProgressBar progressBarMessage;

    private static String TAG_DIALOGS = "DIALOGS";

    public static void abrirVentanaMensaje(Activity activity, String titulo, String mensaje, int icono, int hasProgress, int hasImagen, Boolean cancelable){

        if(!isVentanaMensaje()) {
            try {
                dialogBuilderMensage = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View messageView = inflater.inflate(R.layout.meesage_view, null);

                txtTituloMessage = messageView.findViewById(R.id.txtTituloView);
                txtMessage = messageView.findViewById(R.id.txtMensajeView);
                progressBarMessage = messageView.findViewById(R.id.progressBarView);
                imgMessage = messageView.findViewById(R.id.imgIconoView);

                txtTituloMessage.setText(titulo);
                txtMessage.setText(mensaje);
                imgMessage.setImageResource(icono);

                progressBarMessage.setVisibility(hasProgress);
                imgMessage.setVisibility(hasImagen);

                dialogBuilderMensage.setView(messageView);
                dialogBuilderMensage.setCancelable(cancelable);
                dialogMessage = dialogBuilderMensage.create();
                dialogMessage.show();
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            Log.d(TAG_DIALOGS, "Ya hay una ventana abierta");
        }

    }

    public static void cerrarVentanaMensaje(){
        if (dialogMessage != null){
            dialogMessage.dismiss();
            dialogMessage = null;
        }
    }

    public static Boolean isVentanaMensaje(){
        return dialogMessage != null;
    }
}
