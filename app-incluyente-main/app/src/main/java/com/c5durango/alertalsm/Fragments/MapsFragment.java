package com.c5durango.alertalsm.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.c5durango.alertalsm.Clases.ModeloUbicacion;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.Utilidades.Utilidades;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    /* Se toma la ubicación y se envia al ReporteActivity para que posteriormente envie el reporte completo. */

    private View rootView;
    private GoogleMap mMap;
    private MapView mapView;
    private String TAG = "MAPS_ACTIVITY";
    private MarkerOptions marker;
    DataListener callback;

    ModeloUbicacion modeloUbicacion = new ModeloUbicacion(24.02465482496386 ,-104.67029277408226);

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().containsKey("lugar"))
                modeloUbicacion.setLugar(getArguments().getString("lugar"));


            if(modeloUbicacion.getLugar().equals("Otra")){
                if(getArguments().containsKey("latitud"))
                    modeloUbicacion.setLatitud(getArguments().getDouble("latitud"));
                if(getArguments().containsKey("longitud"))
                    modeloUbicacion.setLongitud(getArguments().getDouble("longitud"));
            }
            // else
            // Tomar ubicación actual

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) rootView.findViewById(R.id.map);
        if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        if(callback!= null)
            callback.mostrarLayout();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(10);

        // Agrega un marcador en Durango y mueve la cámara
        // u obtener ubicación actual
        LatLng durango = new LatLng(modeloUbicacion.getLatitud(), modeloUbicacion.getLongitud());
        marker = new MarkerOptions();
        marker.position(durango);
        marker.title("Lugar del incidente");
        marker.draggable(true);

        mMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(durango)
                .zoom(15)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "LONG");
                modeloUbicacion.setLatitud(latLng.latitude);
                modeloUbicacion.setLongitud(latLng.longitude);

                mMap.clear();

                marker.position(new LatLng(modeloUbicacion.getLatitud(), modeloUbicacion.getLongitud()));
                marker.title("¡Aquí es mi emergencia!");
                marker.draggable(true);
                mMap.addMarker(marker);

                llamarCallback();
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d(TAG, "DRAG END");
                modeloUbicacion.setLatitud(marker.getPosition().latitude);
                modeloUbicacion.setLongitud(marker.getPosition().longitude);
                llamarCallback();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DataListener) context;
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void llamarCallback(){
        // Enviar datos a Activity
        callback.sendCoordenadas("Otra", Utilidades.obtenerFecha(), modeloUbicacion.getLatitud(), modeloUbicacion.getLongitud());
    }

    public interface DataListener {
        void sendCoordenadas(String lugar, String fecha, double latitud, double longitud);
        void mostrarLayout();
    }
}
