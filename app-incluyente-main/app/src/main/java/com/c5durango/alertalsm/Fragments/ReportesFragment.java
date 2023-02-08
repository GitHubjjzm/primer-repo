package com.c5durango.alertalsm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.c5durango.alertalsm.Adaptadores.ListaReportesAdapter;
import com.c5durango.alertalsm.Clases.ModelReportesLocal;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.DB.DBReportes;

import java.util.ArrayList;

public class ReportesFragment extends Fragment {

    /*
    * Infla el Recycler view donde se muestran los reportes generados
    * */

    RecyclerView recyclerReportes;
    ArrayList<ModelReportesLocal> arrayReportes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);
        recyclerReportes = view.findViewById(R.id.recyclerReportes);
        recyclerReportes.setLayoutManager(new LinearLayoutManager(getContext()));

        DBReportes dbReportes = new DBReportes(getContext());
        arrayReportes = new ArrayList<>();

        ListaReportesAdapter adapter = new ListaReportesAdapter(dbReportes.mostrarReportes(), this.getContext());
        recyclerReportes.setAdapter(adapter);
        return view;
    }
}