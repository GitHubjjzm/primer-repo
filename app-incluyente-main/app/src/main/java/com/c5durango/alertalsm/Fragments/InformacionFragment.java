package com.c5durango.alertalsm.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.c5durango.alertalsm.R;

public class InformacionFragment extends Fragment {

    /*
    * Solo muestra el aviso de privacidad necesario que pide Google Play
    * para poder subir una app a su tienda.
    * */

    Button btnLink;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_info, container, false);
        btnLink = root.findViewById(R.id.btnLink);
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://avisoprivacidad-alertagenero.herokuapp.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return root;
    }
}