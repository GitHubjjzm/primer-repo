package com.c5durango.alertalsm.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.c5durango.alertalsm.Clases.ModelEmergencia;
import com.c5durango.alertalsm.R;

import java.util.Arrays;
import java.util.List;


public class EmergFragment extends Fragment implements CardView.OnClickListener{


    /* Se toma la información del tipo de emergencia solicitada
    * y se enviar al ReporteActivity para que posteriormente envie el reporte completo.
    * */

    public CardView cardBombero, cardMedico, cardSeguridad, cardOtro;
    private static String TAG = "EmergFragment";

    // Instancias
    ModelEmergencia modelEmergencia;
    private DataListener callback;

    Spinner spinerTipoEmergencia;
    List<String> listMedico = Arrays.asList(
            "DESCONOCIDO",
            "ACCIDENTE",
            "ACCIDENTE DE MOTOCICLETA",
            "ACCIDENTE DE VEHÍCULO",
            "AHOGAMIENTO",
            "ASFIXIA",
            "ATROPELLAMIENTO",
            "CAIDA",
            "CONVULSIONES",
            "DIFICULTAD RESPIRATORIA",
            "FALLECIDO",
            "INFARTO",
            "LESIONADO POR ARMA BLANCA",
            "PACIENTE EMBARAZADA",
            "PARO CARDIORESPIRATORIO",
            "PERSONA INCONSCIENTE",
            "QUEMADURA",
            "TRABAJO DE PARTO",
            "TRAUMATISMO");
    List<String> listSeguridad = Arrays.asList(
            "DESCONOCIDO",
            "ABUSO SEXUAL",
            "ACOSO SEXUAL",
            "ALTERACIÓN AL ORDEN PÚBLICO",
            "AMENAZA",
            "CRISTALAZO O ROBO AL INTERIOR DE VEHÍCULO",
            "DAÑO A PROPIEDAD AJENA",
            "DETONACIÓN DE ARMA DE FUEGO",
            "DETONACIÓN DE ARMAS O CARTUCHOS",
            "EXTORSIÓN",
            "FALTA A REGLAMENTO DE TRÁNSITO",
            "MALTRATO INFANTIL",
            "OTROS ACTOS RELACIONADOS CON LA FAMILIA",
            "OTROS ROBOS",
            "PERSONA NO LOCALIZADA",
            "PERSONA SOSPECHOSA",
            "PERSONA TIRADA EN VÍA PÚBLICA",
            "PRIVACIÓN DE LA LIBERTAD",
            "ROBO A CAJERO AUTOMÁTICO",
            "ROBO A CASA HABITACIÓN",
            "ROBO A ESCUELA",
            "ROBO A GASOLINERA",
            "ROBO A NEGOCIO",
            "SUICIDIO",
            "VEHÍCULO ABANDONADO",
            "VEHÍCULO SOSPECHOSO",
            "VIOLACIÓN",
            "VIOLENCIA CONTRA LA MUJER",
            "VIOLENCIA DE GÉNERO",
            "VIOLENCIA DE PAREJA",
            "VIOLENCIA FAMILIAR");
    List<String> listBomberos = Arrays.asList(
            "DESCONOCIDO",
            "ANIMAL PELIGROSO",
            "ÁRBOL CAÍDO O POR CAER",
            "AUXILIO",
            "CONTAMINACIÓN DE SUELO, AIRE Y AGUA",
            "DERRUMBES",
            "ENJAMBRE DE ABEJAS",
            "EXPLOSIÓN",
            "FRENTES FRÍOS",
            "HURACANES",
            "INCENDIO DE CASA",
            "INCENDIO DE COMERCO",
            "INCENDIO DE EDIFICIO",
            "INCENDIO DE ESCUELA",
            "INCENDIO DE VEHÍCULO",
            "INCENDIO FORESTAL",
            "INUNDACIONES",
            "OTROS INCENDIOS",
            "PLAGAS",
            "RESCATE ANIMAL",
            "RESCATE PERSONA ATRAPADA",
            "SISMO",
            "TORNADO",
            "TSUNAMI");
    List<String> listOtros = Arrays.asList(
            "DESCONOCIDO",
            "ALCANTARILLA OBSTRUIDA",
            "ALERTA DE PRUEBA",
            "ANIMAL MUERTO",
            "CAÍDA DE BARDA/POSTE",
            "CORTO CIRCUITO",
            "INCIDENTE DESCONOCIDO",
            "SOLICITUD DE OTROS SERVICIOS PÚBLICOS");


    // TAGS
    String tituloBombero = "PROTECCIÓN CIVIL";
    String tituloMedico = "MEDICO";
    String tituloSeguridad = "SEGURIDAD";
    String tituloOtro = "OTRO";
    String tipoAnt = "";

    public EmergFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emerg, container, false);
        spinerTipoEmergencia = view.findViewById(R.id.spinnerTipoEmergencia);
        cardBombero = (CardView) view.findViewById(R.id.cardBombero);
        cardBombero.setOnClickListener((View.OnClickListener) this);
        cardMedico = (CardView) view.findViewById(R.id.cardMedico);
        cardMedico.setOnClickListener((View.OnClickListener) this);
        cardSeguridad = (CardView) view.findViewById(R.id.cardSeguridad);
        cardSeguridad.setOnClickListener((View.OnClickListener) this);
        cardOtro = (CardView) view.findViewById(R.id.cardOtro);
        cardOtro.setOnClickListener((View.OnClickListener) this);

        // Instancias
        modelEmergencia = new ModelEmergencia("Desconocido", "Desconocido");
        try {
            if(getArguments()!= null){
                if(getArguments().containsKey("cuerpo")){
                    modelEmergencia.setCuerpoEmergencia(getArguments().getString("cuerpo"));
                    if(modelEmergencia.getCuerpoEmergencia() == tituloBombero)
                        colorearCard(cardBombero);
                    if(modelEmergencia.getCuerpoEmergencia() == tituloMedico)
                        colorearCard(cardMedico);
                    if(modelEmergencia.getCuerpoEmergencia() == tituloSeguridad)
                        colorearCard(cardSeguridad);
                    if(modelEmergencia.getCuerpoEmergencia() == tituloOtro)
                        colorearCard(cardOtro);
                }
                if(getArguments().containsKey("tipoEmerg")){
                    modelEmergencia.setTipoEmergencia(getArguments().getString("tipoEmerg"));
                    seleccionarCard(modelEmergencia.getTipoEmergencia());
                    tipoAnt = modelEmergencia.getTipoEmergencia();
                }
            }
            Log.d(TAG, modelEmergencia.getCuerpoEmergencia() + " : " + modelEmergencia.getTipoEmergencia());

        } catch (Exception e ){
            // Marcar seleccionada X CardView
            Log.d(TAG, "ERRORSIN: " + e.getMessage());
        }

        spinerTipoEmergencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Boolean c = (position > 0)? true : false;

                if (modelEmergencia.getCuerpoEmergencia() == tituloBombero) {
                    modelEmergencia.setTipoEmergencia(listBomberos.get(position));
                    if(!tipoAnt.equals(modelEmergencia.getTipoEmergencia()))
                        callbackMain(c);

                } else if (modelEmergencia.getCuerpoEmergencia() == tituloMedico) {
                    modelEmergencia.setTipoEmergencia(listMedico.get(position));
                    if(!tipoAnt.equals(modelEmergencia.getTipoEmergencia()))
                        callbackMain(c);

                } else if (modelEmergencia.getCuerpoEmergencia() == tituloSeguridad) {
                    modelEmergencia.setTipoEmergencia(listSeguridad.get(position));
                    if(!tipoAnt.equals(modelEmergencia.getTipoEmergencia()))
                        callbackMain(c);

                } else if (modelEmergencia.getCuerpoEmergencia() == tituloOtro) {
                    modelEmergencia.setTipoEmergencia(listOtros.get(position));
                    if(!tipoAnt.equals(modelEmergencia.getTipoEmergencia()))
                        callbackMain(c);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void seleccionarCard(String tipoEmerg){
        if( modelEmergencia.getCuerpoEmergencia() == tituloBombero){
            spinerTipoEmergencia.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listBomberos));
            spinerTipoEmergencia.setSelection(obtenerPosicion(listBomberos, tipoEmerg));
            Log.d(TAG, "SELECCION: " + obtenerPosicion(listBomberos, tipoEmerg));
        } else if (modelEmergencia.getCuerpoEmergencia() == tituloMedico){
            spinerTipoEmergencia.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listMedico));
            spinerTipoEmergencia.setSelection(obtenerPosicion(listMedico, tipoEmerg));
            Log.d(TAG, "SELECCION: " + obtenerPosicion(listMedico, tipoEmerg));
        } else if (modelEmergencia.getCuerpoEmergencia() == tituloSeguridad){
            spinerTipoEmergencia.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listSeguridad));
            spinerTipoEmergencia.setSelection(obtenerPosicion(listSeguridad, tipoEmerg));
            Log.d(TAG, "SELECCION: " + obtenerPosicion(listSeguridad, tipoEmerg));
        } else if (modelEmergencia.getCuerpoEmergencia() == tituloOtro) {
            spinerTipoEmergencia.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, listOtros));
            spinerTipoEmergencia.setSelection(obtenerPosicion(listOtros, tipoEmerg));
            Log.d(TAG, "SELECCION: " + obtenerPosicion(listOtros, tipoEmerg));
        } else {
            Log.d(TAG, "NINGUN TITULO CORRESPONDE: " + tipoEmerg);
        }
    }

    private void colorearCard(CardView cardView){
        if(cardView == cardOtro){
            cardOtro.setBackgroundColor(getResources().getColor(R.color.grisClaro));
            cardSeguridad.setBackgroundColor(Color.WHITE);
            cardMedico.setBackgroundColor(Color.WHITE);
            cardBombero.setBackgroundColor(Color.WHITE);
        }

        if(cardView == cardSeguridad){
            cardOtro.setBackgroundColor(Color.WHITE);
            cardSeguridad.setBackgroundColor(getResources().getColor(R.color.grisClaro));
            cardMedico.setBackgroundColor(Color.WHITE);
            cardBombero.setBackgroundColor(Color.WHITE);
        }

        if(cardView == cardMedico){
            cardOtro.setBackgroundColor(Color.WHITE);
            cardSeguridad.setBackgroundColor(Color.WHITE);
            cardMedico.setBackgroundColor(getResources().getColor(R.color.grisClaro));
            cardBombero.setBackgroundColor(Color.WHITE);
        }

        if(cardView == cardBombero){
            cardOtro.setBackgroundColor(Color.WHITE);
            cardSeguridad.setBackgroundColor(Color.WHITE);
            cardMedico.setBackgroundColor(Color.WHITE);
            cardBombero.setBackgroundColor(getResources().getColor(R.color.grisClaro));
        }


    }

    private int obtenerPosicion(List<String> list, String datoBuscar){
        for (int e = 0; e < list.size(); e++){
            if(list.get(e) == datoBuscar)
                return e;
        }
        return 0;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DataListener) context;
        } catch(Exception e){
            Toast.makeText(context, "ERROR ON ATTACH " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void callbackMain(Boolean cerrar){
        callback.sendDataEmerg(modelEmergencia.getCuerpoEmergencia(), modelEmergencia.getTipoEmergencia(), cerrar);
    }

    public interface DataListener{
        void sendDataEmerg(String cuerpo, String tipoEmergencia, Boolean cerrar);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cardBombero){
            modelEmergencia.setCuerpoEmergencia(tituloBombero);
            callbackMain(false);
            actualizarLista(listBomberos);
            colorearCard(cardBombero);
            //Toast.makeText(getContext(), "click a " + modelEmergencia.getCuerpoEmergencia(), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.cardMedico){
            modelEmergencia.setCuerpoEmergencia(tituloMedico);
            callbackMain(false);
            actualizarLista(listMedico);
            colorearCard(cardMedico);
            //Toast.makeText(getContext(), "click a " + modelEmergencia.getCuerpoEmergencia(), Toast.LENGTH_SHORT).show();
        }  else if (v.getId() == R.id.cardSeguridad){
            modelEmergencia.setCuerpoEmergencia(tituloSeguridad);
            callbackMain(false);
            actualizarLista(listSeguridad);
            colorearCard(cardSeguridad);
            //Toast.makeText(getContext(), "click a " + modelEmergencia.getCuerpoEmergencia(), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.cardOtro){
            modelEmergencia.setCuerpoEmergencia(tituloOtro);
            callbackMain(false);
            actualizarLista(listOtros);
            colorearCard(cardOtro);
            //Toast.makeText(getContext(), "click a " + modelEmergencia.getCuerpoEmergencia(), Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarLista(List<String> lista){
        try {
            spinerTipoEmergencia.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, lista));
        } catch (Exception e){
            Toast.makeText(getContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
