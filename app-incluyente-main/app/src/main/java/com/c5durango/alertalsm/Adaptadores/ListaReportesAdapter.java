package com.c5durango.alertalsm.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.c5durango.alertalsm.Clases.ModelReportesLocal;
import com.c5durango.alertalsm.InfoReporteActivity;
import com.c5durango.alertalsm.R;

import java.util.ArrayList;

public class ListaReportesAdapter extends RecyclerView.Adapter<ListaReportesAdapter.ReporteViewHolder> {

    /*
    * Adaptador para mostrar la lista de los reportes generados
    *
    */

    ArrayList<ModelReportesLocal> listaReportes;
    private String TAG = "ADAPTER_REPORTES";
    Context context;

    public ListaReportesAdapter(ArrayList<ModelReportesLocal> listaReportes, Context context){
        this.listaReportes = listaReportes;
        this.context = context;
    }
    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asignar el diseño de cada elemento de la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_reportes, null, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        // Asignar los elementos que corresponden para cada opción
        holder.txtReporte.setText("Reporte #" + listaReportes.get(position).getId_reporte());
        holder.txtFecha.setText(listaReportes.get(position).getFecha_hora());
        holder.txtEstatus.setText(listaReportes.get(position).getEstatus());
        holder.cardItemReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InfoReporteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id_reporte",listaReportes.get(position).getId_reporte());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        // Mostrar el tamaño de la lista
        return this.listaReportes.size();
    }

    public class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView txtReporte;
        TextView txtFecha;
        TextView txtEstatus;
        CardView cardItemReporte;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReporte = itemView.findViewById(R.id.viewIdReporte);
            txtFecha = itemView.findViewById(R.id.viewFecha);
            txtEstatus = itemView.findViewById(R.id.viewEstatus);
            cardItemReporte = itemView.findViewById(R.id.cardItemReporte);
        }
    }
}
