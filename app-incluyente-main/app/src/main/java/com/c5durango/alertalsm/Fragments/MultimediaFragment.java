package com.c5durango.alertalsm.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.c5durango.alertalsm.BuildConfig;
import com.c5durango.alertalsm.Clases.ModelMultimedia;
import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.Utilidades.Utilidades;
import com.c5durango.alertalsm.R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;



public class MultimediaFragment extends Fragment implements View.OnClickListener {

    /* Se toma la multimedia añadida (fotos, audio y video)
     * y se envia al ReporteActivity para que posteriormente envie el reporte completo.
     * */

    static String TAG = "MULTIMEDIA";

    // UI
    private ImageButton btnBasura;
    private CardView cardSeleccionarImagen;
    private CardView cardSeleccionarAudio;
    private CardView cardSeleccionarVideo;
    private CardView cardTomarFotografia;
    private CardView cardTomarAudio;
    private CardView cardTomarVideo;


    private TextView txtArchivos;

    // UI MULTIMEDIA VIEW
    ImageView imagNO;
    // Layout 1
    LinearLayout layout1;
    ImageView img1;
    TextView txt1;
    ImageButton btn1;
    // Layout 2
    LinearLayout layout2;
    ImageView img2;
    TextView txt2;
    ImageButton btn2;
    // Layout 3
    LinearLayout layout3;
    ImageView img3;
    TextView txt3;
    ImageButton btn3;
    View multimediaView;



    // MEDIA RECORDER
    MediaRecorder mediaRecorder;
    private Uri urifoto;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog.Builder dialogBuilderMensage;
    private AlertDialog.Builder dialogBuilderMultimedia;
    private AlertDialog dialog;
    private AlertDialog dialogMessage;
    private AlertDialog dialogMultimedia;
    private Button btnIniciarAudio, btnCancelarAudio, btnAceptarAudio;
    private String pathActualAudio = "";
    private DataListener callback;


    // MessageView
    TextView txtTituloMessage, txtMessage;
    ImageView imgMessage;
    ProgressBar progressBarMessage;

    int REQUEST_CODE_IMAGEN = 10, REQUEST_CODE_AUDIO = 20, REQUEST_CODE_VIDEO = 30;
    int REQUEST_IMAGE_CAPTURE = 40, REQUEST_AUDIO_CAPTURE = 50, REQUEST_VIDEO_CAPTURE = 60;
    private int ARCHIVOS_ADJUNTOS = 0, MAXIMO_ARCHIVOS = 3;

    Intent myFileIntent;

    // LISTAS TIPO URI DE LOS ARCHIVOS
    ArrayList<Uri> arrayImagenURI = new ArrayList<>();
    ArrayList<Uri> arrayAudioURI = new ArrayList<>();
    ArrayList<Uri> arrayVideoURI = new ArrayList<>();
    ArrayList<String> listPathAudios = new ArrayList<>();


    ModelMultimedia modelMultimedia = new ModelMultimedia(arrayImagenURI, arrayAudioURI, arrayVideoURI, listPathAudios);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                if (getArguments().containsKey("arrayImagenURI"))
                    modelMultimedia.setArrayImagenURI((ArrayList<Uri>) (ArrayList<?>) (getArguments().getStringArrayList("arrayImagenURI")));
                if (getArguments().containsKey("arrayAudioURI"))
                    modelMultimedia.setArrayAudioURI((ArrayList<Uri>) (ArrayList<?>) (getArguments().getStringArrayList("arrayAudioURI")));
                if (getArguments().containsKey("arrayVideoURI"))
                    modelMultimedia.setArrayVideoURI((ArrayList<Uri>) (ArrayList<?>) (getArguments().getStringArrayList("arrayVideoURI")));
                if (getArguments().containsKey("listPathAudios"))
                    modelMultimedia.setListPathAudio(getArguments().getStringArrayList("listPathAudios"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "#1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multimedia, container, false);

        // EVENTOS PARA CARDS VIEW
        cardSeleccionarImagen = (CardView) view.findViewById(R.id.cardSeleccionarImagen);
        cardSeleccionarImagen.setOnClickListener(this);
        cardSeleccionarAudio = (CardView) view.findViewById(R.id.cardSeleccionarAudio);
        cardSeleccionarAudio.setOnClickListener(this);
        cardSeleccionarVideo = (CardView) view.findViewById(R.id.cardSeleccionarVideo);
        cardSeleccionarVideo.setOnClickListener(this);

        cardTomarFotografia = (CardView) view.findViewById(R.id.cardTomarFotografia);
        cardTomarFotografia.setOnClickListener(this);
        cardTomarAudio = (CardView) view.findViewById(R.id.cardTomarAudio);
        cardTomarAudio.setOnClickListener(this);
        cardTomarVideo = (CardView) view.findViewById(R.id.cardTomarVideo);
        cardTomarVideo.setOnClickListener(this);

        txtArchivos = view.findViewById(R.id.txtNumMultimedia);

        btnBasura = (ImageButton) view.findViewById(R.id.imgBtnBasura);
        btnBasura.setOnClickListener(this);

        try {
            calcularArchivosAdjuntos();
        } catch (Exception e) {
            Toast.makeText(getContext(), "#2: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DataListener) context;
        } catch (Exception e) {
            Toast.makeText(context, "ERROR ON ATTACH " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void abrirVentanaMensaje(String titulo, String mensaje, int icono, int hasProgress, int hasImagen) {
        dialogBuilderMensage = new AlertDialog.Builder(getContext());
        final View messageView = getLayoutInflater().inflate(R.layout.meesage_view, null);
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
        dialogBuilderMensage.setCancelable(false);
        dialogMessage = dialogBuilderMensage.create();
        dialogMessage.show();

    }


    /*private void renderizarBasura(ArrayList<String> arrayTipo, ArrayList<String> arrayDescripcion, ArrayList<String> arrayOrigen, ArrayList<Integer> arrayPosicion){
        /*ArrayList<String> arrayTipo = new ArrayList<>();
        ArrayList<String> arrayDescripcion = new ArrayList<>();
        final ArrayList<String> arrayOrigen = new ArrayList<>();
        final ArrayList<Integer> arrayPosicion = new ArrayList<>();


    } */

    public void abrirVentanaEliminar() {

        dialogBuilderMultimedia = new AlertDialog.Builder(getContext());
        multimediaView = getLayoutInflater().inflate(R.layout.multimedia_view, null);

        imagNO = multimediaView.findViewById(R.id.imgNO);
        imagNO.setVisibility(View.GONE);

        // Layout 1
        layout1 = multimediaView.findViewById(R.id.layout1);
        img1 = multimediaView.findViewById(R.id.img1);
        txt1 = multimediaView.findViewById(R.id.txt1);
        btn1 = multimediaView.findViewById(R.id.btn1);
        layout1.setVisibility(View.GONE);

        // Layout 2
        layout2 = multimediaView.findViewById(R.id.layout2);
        img2 = multimediaView.findViewById(R.id.img2);
        txt2 = multimediaView.findViewById(R.id.txt2);
        btn2 = multimediaView.findViewById(R.id.btn2);
        layout2.setVisibility(View.GONE);

        // Layout 3
        layout3 = multimediaView.findViewById(R.id.layout3);
        img3 = multimediaView.findViewById(R.id.img3);
        txt3 = multimediaView.findViewById(R.id.txt3);
        btn3 = multimediaView.findViewById(R.id.btn3);
        layout3.setVisibility(View.GONE);

        final ArrayList<String> arrayTipo = new ArrayList<>();
        final ArrayList<String> arrayDescripcion = new ArrayList<>();
        final ArrayList<String> arrayOrigen = new ArrayList<>();
        final ArrayList<Integer> arrayPosicion = new ArrayList<>();

        try {
            // Ocultar todos los layouts
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);

            for (int i = 0; i < modelMultimedia.getArrayImagenURI().size(); i++) {
                arrayTipo.add("imagen");
                arrayDescripcion.add("Imagen");
                arrayOrigen.add("ArrayImagenURI");
                arrayPosicion.add(i);
            }
            for (int i = 0; i < modelMultimedia.getArrayAudioURI().size(); i++) {
                arrayTipo.add("audio");
                arrayDescripcion.add("Audio");
                arrayOrigen.add("ArrayAudioURI");
                arrayPosicion.add(i);
            }
            for (int i = 0; i < modelMultimedia.getListPathAudio().size(); i++) {
                arrayTipo.add("audio");
                arrayDescripcion.add("Grabación");
                arrayOrigen.add("ListPathAudio");
                arrayPosicion.add(i);
            }
            for (int i = 0; i < modelMultimedia.getArrayVideoURI().size(); i++) {
                arrayTipo.add("video");
                arrayDescripcion.add("Video");
                arrayOrigen.add("ArrayVideoURI");
                arrayPosicion.add(i);
            }

            if (arrayTipo.size() == 0) {
                imagNO.setVisibility(View.VISIBLE);
            }

            for (int e = 0; e < arrayTipo.size(); e++) {
                switch (e) {
                    case 0:
                        layout1.setVisibility(View.VISIBLE);
                        if (arrayTipo.get(e).equals("audio")) {
                            int ig = (arrayDescripcion.get(e).equals("Audio")) ? R.drawable.ic_audio : R.drawable.ic_microfono;
                            img1.setImageResource(ig);
                            txt1.setText(arrayDescripcion.get(e));
                        } else if (arrayTipo.get(e).equals("video")) {
                            img1.setImageResource(R.drawable.ic_v_recorder);
                            txt1.setText(arrayDescripcion.get(e));
                        } else if (arrayTipo.get(e).equals("imagen")) {
                            img1.setImageResource(R.drawable.ic_image);
                            txt1.setText(arrayDescripcion.get(e));
                        }
                        break;

                    case 1:
                        layout2.setVisibility(View.VISIBLE);
                        if (arrayTipo.get(e).equals("audio")) {
                            int ig = (arrayDescripcion.get(e).equals("Audio")) ? R.drawable.ic_audio : R.drawable.ic_microfono;
                            img2.setImageResource(ig);
                            txt2.setText(arrayDescripcion.get(e));
                        } else if (arrayTipo.get(e).equals("video")) {
                            img2.setImageResource(R.drawable.ic_v_recorder);
                            txt2.setText(arrayDescripcion.get(e));
                        } else if (arrayTipo.get(e).equals("imagen")) {
                            img2.setImageResource(R.drawable.ic_image);
                            txt2.setText(arrayDescripcion.get(e));
                        }
                        break;

                    case 2:
                        layout3.setVisibility(View.VISIBLE);
                        if (arrayTipo.get(e).equals("audio")) {
                            int ig = (arrayDescripcion.get(e).equals("Audio")) ? R.drawable.ic_audio : R.drawable.ic_microfono;
                            img3.setImageResource(ig);
                            txt3.setText(arrayDescripcion.get(e));
                        } else if (arrayTipo.get(e).equals("video")) {
                            img3.setImageResource(R.drawable.ic_v_recorder);
                            txt3.setText(arrayDescripcion.get(e));
                        } else if (arrayTipo.get(e).equals("imagen")) {
                            img3.setImageResource(R.drawable.ic_image);
                            txt3.setText(arrayDescripcion.get(e));
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayOrigen.get(0).equals("ArrayImagenURI")) {
                    modelMultimedia.eliminarPosImagenURI(arrayPosicion.get(0));
                } else if (arrayOrigen.get(0).equals("ArrayAudioURI")) {
                    modelMultimedia.eliminarPosAudioURI(arrayPosicion.get(0));
                } else if (arrayOrigen.get(0).equals("ListPathAudio")) {
                    modelMultimedia.eliminarPosPathAudio(arrayPosicion.get(0));
                } else if (arrayOrigen.get(0).equals("ArrayVideoURI")) {
                    modelMultimedia.eliminarPosVideoURI(arrayPosicion.get(0));
                }
                llamarCallback();
                dialogMultimedia.dismiss();
                calcularArchivosAdjuntos();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayOrigen.get(1).equals("ArrayImagenURI")) {
                    modelMultimedia.eliminarPosImagenURI(arrayPosicion.get(1));
                } else if (arrayOrigen.get(1).equals("ArrayAudioURI")) {
                    modelMultimedia.eliminarPosAudioURI(arrayPosicion.get(1));
                } else if (arrayOrigen.get(1).equals("ListPathAudio")) {
                    modelMultimedia.eliminarPosPathAudio(arrayPosicion.get(1));
                } else if (arrayOrigen.get(1).equals("ArrayVideoURI")) {
                    modelMultimedia.eliminarPosVideoURI(arrayPosicion.get(1));
                }
                llamarCallback();
                dialogMultimedia.dismiss();
                calcularArchivosAdjuntos();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayOrigen.get(2).equals("ArrayImagenURI")) {
                    modelMultimedia.eliminarPosImagenURI(arrayPosicion.get(2));
                } else if (arrayOrigen.get(2).equals("ArrayAudioURI")) {
                    modelMultimedia.eliminarPosAudioURI(arrayPosicion.get(2));
                } else if (arrayOrigen.get(2).equals("ListPathAudio")) {
                    modelMultimedia.eliminarPosPathAudio(arrayPosicion.get(2));
                } else if (arrayOrigen.get(2).equals("ArrayVideoURI")) {
                    modelMultimedia.eliminarPosVideoURI(arrayPosicion.get(2));
                }
                llamarCallback();
                dialogMultimedia.dismiss();
                calcularArchivosAdjuntos();
            }
        });

        dialogBuilderMultimedia.setView(multimediaView);
        dialogBuilderMultimedia.setCancelable(true);
        dialogMultimedia = dialogBuilderMultimedia.create();
        dialogMultimedia.show();

    }

    public void abrirViewRecorder() {
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View recordView = getLayoutInflater().inflate(R.layout.record_view, null);
        btnCancelarAudio = recordView.findViewById(R.id.btnCancelarRecords);
        btnAceptarAudio = recordView.findViewById(R.id.btnAceptarRecords);
        btnIniciarAudio = recordView.findViewById(R.id.btnGrabarRecords);

        btnIniciarAudio.setVisibility(View.VISIBLE);
        btnAceptarAudio.setVisibility(View.GONE);

        controlesGrabacionAudio(true, false, true);

        dialogBuilder.setView(recordView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.show();


        btnIniciarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btnIniciarAudio.setVisibility(View.GONE);
                    btnAceptarAudio.setVisibility(View.VISIBLE);
                    comenzarGrabarAudio();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error al grabar audio", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        btnAceptarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaRecorder != null) {
                    btnIniciarAudio.setVisibility(View.VISIBLE);
                    btnAceptarAudio.setVisibility(View.GONE);
                    detenerGrabacionAudio(true);
                }
                dialog.dismiss();
            }
        });
        btnCancelarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mediaRecorder != null)
                        detenerGrabacionAudio(false);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
                dialog.dismiss();
            }
        });
    }


    /* **********************************************************************
     *                                                                       *
     *                       RESULT DE FILE EXPLORER                         *
     *                                                                       *
     ************************************************************************/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (resultCode == RESULT_OK) {
            // MULTIMEDIA GRABADA POR EL USUARIO
            if (requestCode > 30) {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {          // CAPTURA DE FOOGRAFIA
                    try {
                        if (data != null && data.getData() != null) {
                            arrayImagenURI.add(data.getData());
                            modelMultimedia.setArrayImagenURI(arrayImagenURI);
                            calcularArchivosAdjuntos();
                            llamarCallback();
                        } else {
                            if (urifoto != null) {
                                arrayImagenURI.add(urifoto);
                                modelMultimedia.setArrayImagenURI(arrayImagenURI);
                                calcularArchivosAdjuntos();
                                llamarCallback();
                            } else
                                Toast.makeText(getContext(), "No se pudo tomar la fotografía", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al capturar fotografía.\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (requestCode == REQUEST_VIDEO_CAPTURE) {   // CAPTURA DE VIDEO
                    try {
                        if (data != null && data.getData() != null) {
                            arrayVideoURI.add(data.getData());
                            modelMultimedia.setArrayVideoURI(arrayVideoURI);
                            calcularArchivosAdjuntos();
                            llamarCallback();
                        } else {
                            Toast.makeText(getContext(), "No se pudo tomar el video", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al capturar video.\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            // MULTIMEDIA PRE-SELECCIONADA
            if (data != null && requestCode <= 30) {
                // ClipData es cuando son MUCHOS ARCHIVOS
                if (null != data.getClipData()) {
                    switch (requestCode) {
                        case 10: // IMAGEN
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                arrayImagenURI.add(data.getClipData().getItemAt(i).getUri());
                                modelMultimedia.setArrayImagenURI(arrayImagenURI);
                                calcularArchivosAdjuntos();
                                llamarCallback();
                            }
                            break;
                        case 20: // AUDIO
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                arrayAudioURI.add(data.getClipData().getItemAt(i).getUri());
                                modelMultimedia.setArrayAudioURI(arrayAudioURI);
                                calcularArchivosAdjuntos();
                                llamarCallback();
                            }
                            break;
                        case 30: // VIDEO
                            Toast.makeText(getContext(), "30", Toast.LENGTH_LONG).show();
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                arrayVideoURI.add(data.getClipData().getItemAt(i).getUri());
                                modelMultimedia.setArrayVideoURI(arrayVideoURI);
                                calcularArchivosAdjuntos();
                                llamarCallback();
                            }
                            break;
                    }
                } else {
                    Uri uri = data.getData();
                    // CUANDO SOLO ES UN ARCHIVO
                    switch (requestCode) {
                        case 10: // IMAGEN
                            arrayImagenURI.add(uri);
                            modelMultimedia.setArrayImagenURI(arrayImagenURI);
                            calcularArchivosAdjuntos();
                            llamarCallback();
                            break;
                        case 20: // AUDIO
                            arrayAudioURI.add(uri);
                            modelMultimedia.setArrayAudioURI(arrayAudioURI);
                            calcularArchivosAdjuntos();
                            llamarCallback();
                            break;
                        case 30: // VIDEO
                            arrayVideoURI.add(uri);
                            modelMultimedia.setArrayVideoURI(arrayVideoURI);


                            /*String PATH = UriUtils.getPathFromUri(getContext(), uri);
                            iniciarServicioVideo(PATH);*/

                            calcularArchivosAdjuntos();
                            llamarCallback();
                            break;
                    }
                }
            }
        }
    }

    /* **********************************************************************
     *                                                                       *
     *                                 VIDEO                                 *
     *                                                                       *
     ************************************************************************/

    public void grabarVideo() {
        Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intentVideo.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Constantes.MAX_DURATION_VIDEO_20);
        intentVideo.putExtra(MediaStore.EXTRA_SIZE_LIMIT, Constantes.MAX_VIDEO_15MB);
        intentVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

        if (intentVideo.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intentVideo, REQUEST_VIDEO_CAPTURE);
        }
    }

    /* **********************************************************************
     *                                                                       *
     *                                 AUDIO                                 *
     *                                                                       *
     ************************************************************************/

    public void comenzarGrabarAudio() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setMaxDuration(Constantes.DURACION_AUDIO_GRABACION);

        pathActualAudio = Utilidades.generarNombreAudioHora();
        mediaRecorder.setOutputFile(pathActualAudio);
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        mediaRecorder.stop();
                        if (new File(pathActualAudio).exists()) {
                            listPathAudios.add(pathActualAudio);
                            modelMultimedia.setListPathAudio(listPathAudios);
                            calcularArchivosAdjuntos();
                            llamarCallback();
                        }
                        break;
                    case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
                        Log.d(TAG, "ERROR DESCONOCIDO EN MEDIA RECORDER");
                        break;
                }
            }
        });
        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.d(TAG, "ERROR CONOCIDO");
            }
        });

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            controlesGrabacionAudio(false, true, true);
        } catch (IllegalStateException e) {
            Log.d(TAG, "(Audio) Catch 1" + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "(Audio) Catch 2" + e.getMessage());
        }
    }


    public void detenerGrabacionAudio(Boolean guardar) {
        try {
            mediaRecorder.stop();
            if (new File(pathActualAudio).exists() && guardar) {
                listPathAudios.add(pathActualAudio);
                modelMultimedia.setListPathAudio(listPathAudios);
                calcularArchivosAdjuntos();
                llamarCallback();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void controlesGrabacionAudio(Boolean iniciar, Boolean aceptar, Boolean cancelar) {
        btnIniciarAudio.setEnabled(iniciar);
        btnAceptarAudio.setEnabled(aceptar);
        btnCancelarAudio.setEnabled(cancelar);
    }

    /* **********************************************************************
     *                                                                       *
     *                            EVENTOS  BOTONES                           *
     *                                                                       *
     ************************************************************************/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.cardSeleccionarImagen):
                if(ARCHIVOS_ADJUNTOS >= MAXIMO_ARCHIVOS){
                    Toast.makeText(getContext(), "¡Ya no puede seleccionar más archivos!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectMultimedia("image/*", REQUEST_CODE_IMAGEN, false);
                } else {
                    Toast.makeText(getContext(), "Acceso a almacenamiento denegado.", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.cardSeleccionarAudio):
                if(ARCHIVOS_ADJUNTOS >= MAXIMO_ARCHIVOS){
                    Toast.makeText(getContext(), "¡Ya no puede seleccionar más archivos!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectMultimedia("audio/*", REQUEST_CODE_AUDIO, false);
                } else {
                    Toast.makeText(getContext(), "Acceso a almacenamiento denegado.", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.cardSeleccionarVideo):
                if(ARCHIVOS_ADJUNTOS >= MAXIMO_ARCHIVOS){
                    Toast.makeText(getContext(), "¡Ya no puede seleccionar más archivos!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectMultimedia("video/*", REQUEST_CODE_VIDEO, false);
                } else {
                    Toast.makeText(getContext(), "Acceso a almacenamiento denegado.", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.cardTomarFotografia):
                if(ARCHIVOS_ADJUNTOS >= MAXIMO_ARCHIVOS){
                    Toast.makeText(getContext(), "¡Ya no puede seleccionar más archivos!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    tomarFoto();
                } else {
                    Toast.makeText(getContext(), "Acceso a cámara denegado.", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.cardTomarAudio):
                if(ARCHIVOS_ADJUNTOS >= MAXIMO_ARCHIVOS){
                    Toast.makeText(getContext(), "¡Ya no puede seleccionar más archivos!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    abrirViewRecorder();
                } else {
                    Toast.makeText(getContext(), "Acceso a micrófono denegado.", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.cardTomarVideo):
                if(ARCHIVOS_ADJUNTOS >= MAXIMO_ARCHIVOS){
                    Toast.makeText(getContext(), "¡Ya no puede seleccionar más archivos!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    grabarVideo();
                } else {
                    Toast.makeText(getContext(), "Acceso a cámara denegado.", Toast.LENGTH_LONG).show();
                }
                break;
            case (R.id.imgBtnBasura):
                abrirVentanaEliminar();
                break;
        }

    }

    public void selectMultimedia(String tipo, int RESULT_CODE, Boolean multiple) {
        myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType(tipo);
        if(tipo.equals("video/*"))
            myFileIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Constantes.MAX_DURATION_VIDEO_20);
        myFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
        startActivityForResult(myFileIntent, RESULT_CODE);
    }


    /* **********************************************************************
     *                                                                       *
     *                                FOTOGRAFIAS                            *
     *                                                                       *
     ************************************************************************/

    public void tomarFoto() {
        //Validar permiso de cámara
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new File(Utilidades.generarNombreImgHora(getActivity()));

            } catch (IOException ex) {
                Log.d(TAG, ex.getMessage());
            }

            try {
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(requireContext()),
                            BuildConfig.APPLICATION_ID  + ".fileprovider", photoFile);
                    /* CUALQUIERA DE LOS DOS
                    Uri photoURI = FileProvider.getUriForFile(this.getContext(),
                            "com.c5durango.alertalsm.fileprovider", photoFile);*/
                    urifoto = photoURI;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    private void calcularArchivosAdjuntos(){
        try{
            int image = modelMultimedia.getArrayImagenURI().size();
            int audio = modelMultimedia.getListPathAudio().size() + modelMultimedia.getArrayAudioURI().size();
            int video = modelMultimedia.getArrayVideoURI().size();
            ARCHIVOS_ADJUNTOS = image + audio + video;

            // Validar color
            int color = (ARCHIVOS_ADJUNTOS >= MAXIMO_ARCHIVOS)? R.color.rojo: R.color.negro;
            txtArchivos.setTextColor(getResources().getColor(color));
            txtArchivos.setText("Archivos adjuntos: " + ARCHIVOS_ADJUNTOS + "/" + MAXIMO_ARCHIVOS);
        }catch (Exception e ){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    /* **********************************************************************
     *                                                                       *
     *                            CALL BACK MAIN                             *
     *                                                                       *
     ************************************************************************/

    private void llamarCallback(){
        callback.sendMultimedia(
                modelMultimedia.getArrayImagenURI(),
                modelMultimedia.getArrayAudioURI(),
                modelMultimedia.getArrayVideoURI(),
                modelMultimedia.getListPathAudio());
    }

    public interface DataListener{
        void sendMultimedia(ArrayList<Uri> arrayListImagenURI, ArrayList<Uri> arrayListAudioURI, ArrayList<Uri> arrayListVideoURI, ArrayList<String> listPathAudios );
    }


    /* **********************************************************************
     *                                                                       *
     *                                ASYNC TASK                             *
     *                                                                       *
     ************************************************************************/

    /*public class DecodificarImagenes extends AsyncTask<ArrayList<Uri>, Integer, List<Bitmap>> {
        ArrayList<Uri> array;
        List<Bitmap> bitmapList;
        Context context;

        public DecodificarImagenes(Context context){
            this.context = context;
        }

        @Override
        protected List<Bitmap> doInBackground(ArrayList<Uri>... lists) {
            this.array = lists[0];
            return null;
        }

        public List<Bitmap> decodificarImagenes(){
            bitmapList = new ArrayList<>();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            for (int i = 0; i < this.array.size() ; i ++){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    try {
                        Bitmap image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), this.array.get(i)));
                        bitmapList.add(image);
                        calcularArchivosAdjuntos();
                        Log.d("MULTIMEDIA B1", "EXITO " + i + " RUTA " + ImageDecoder.createSource(context.getContentResolver(), this.array.get(i)));
                    } catch (IOException e) {
                        Log.d("MULTIMEDIA E1", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Bitmap image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), this.array.get(i));
                        bitmapList.add(image);
                        calcularArchivosAdjuntos();
                        Log.d("MULTIMEDIA B2", "EXITO");
                    } catch (IOException e) {
                        Log.d("MULTIMEDIA E2", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return bitmapList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            //setBitMapListaImagenes(decodificarImagenes());
        }
    }*/
}
