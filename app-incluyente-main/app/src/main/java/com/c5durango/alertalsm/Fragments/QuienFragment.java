package com.c5durango.alertalsm.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.c5durango.alertalsm.Clases.ModelQuien;
import com.c5durango.alertalsm.R;


public class QuienFragment extends Fragment implements CardView.OnClickListener{

    /* Se toma información acerca de la persona que necesita el apoyo
     * y se envia al ReporteActivity para que posteriormente envie el reporte completo.
     * */

    private ModelQuien modelQuien;
    private CardView cardYo;
    private CardView cardElElla;
    private CardView cardNosotros;
    private CardView cardEllos;

    private String tituloYo = "Yo";
    private String tituloElella = "El/Ella";
    private String tituloNosotros = "Nosotros";
    private String tituloEllos = "Ellos";

    public TextView txtTitulo;
    private String antQuien = "";
    private String TAG = "QuienFragm";
    private DataListener callback;
    View view;

    public QuienFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_quien, container, false);
        txtTitulo = (TextView) view.findViewById(R.id.tQuien);
        cardYo = (CardView) view.findViewById(R.id.cardYo);
        cardYo.setOnClickListener(this);
        cardElElla = (CardView) view.findViewById(R.id.cardElElla);
        cardElElla.setOnClickListener(this);
        cardNosotros = (CardView) view.findViewById(R.id.cardNosotros);
        cardNosotros.setOnClickListener(this);
        cardEllos = (CardView) view.findViewById(R.id.cardEllos);
        cardEllos.setOnClickListener(this);

        modelQuien = new ModelQuien("Yo");
        if(getArguments()!=null){
            if (getArguments().containsKey("quien")){
                modelQuien.setQuien(getArguments().getString("quien"));
                antQuien = modelQuien.getQuien();

                switch (modelQuien.getQuien()){
                    case "Yo":
                        colorearCard(cardYo);
                        break;
                    case "El/Ella":
                        colorearCard(cardElElla);
                        break;
                    case "Nosotros":
                        colorearCard(cardNosotros);
                        break;
                    case "Ellos":
                        colorearCard(cardEllos);
                        break;
                }
            }

        }
        return view;
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


    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void colorearCard(CardView cardView){
        if(cardView == cardYo){
            cardYo.setBackgroundColor(getResources().getColor(R.color.grisClaro));
            cardElElla.setBackgroundColor(Color.WHITE);
            cardNosotros.setBackgroundColor(Color.WHITE);
            cardEllos.setBackgroundColor(Color.WHITE);
        }
        if(cardView == cardElElla){
            cardYo.setBackgroundColor(Color.WHITE);
            cardElElla.setBackgroundColor(getResources().getColor(R.color.grisClaro));
            cardNosotros.setBackgroundColor(Color.WHITE);
            cardEllos.setBackgroundColor(Color.WHITE);
        }
        if(cardView == cardNosotros){
            cardYo.setBackgroundColor(Color.WHITE);
            cardElElla.setBackgroundColor(Color.WHITE);
            cardNosotros.setBackgroundColor(getResources().getColor(R.color.grisClaro));
            cardEllos.setBackgroundColor(Color.WHITE);
        }
        if(cardView == cardEllos){
            cardYo.setBackgroundColor(Color.WHITE);
            cardElElla.setBackgroundColor(Color.WHITE);
            cardNosotros.setBackgroundColor(Color.WHITE);
            cardEllos.setBackgroundColor(getResources().getColor(R.color.grisClaro));
        }

    }

    @Override
    public void onClick(View v) {
       if (v.getId() == R.id.cardYo){
           modelQuien.setQuien(tituloYo);
           colorearCard(cardYo);
           if (!antQuien.equals(modelQuien.getQuien()))
               callbackMain();
       }
       if (v.getId() == R.id.cardElElla){
           modelQuien.setQuien(tituloElella);
           colorearCard(cardElElla);
           if (!antQuien.equals(modelQuien.getQuien()))
               callbackMain();
       }
       if (v.getId() == R.id.cardNosotros){
           modelQuien.setQuien(tituloNosotros);
           colorearCard(cardNosotros);
           if (!antQuien.equals(modelQuien.getQuien()))
               callbackMain();
       }
       if (v.getId() == R.id.cardEllos){
           modelQuien.setQuien(tituloEllos);
           colorearCard(cardEllos);
           if (!antQuien.equals(modelQuien.getQuien()))
               callbackMain();
       }
    }

    public void callbackMain(){
        callback.sendDataQuien(modelQuien.getQuien());
    }

    public interface DataListener{
        void sendDataQuien(String quien);
    }


}
