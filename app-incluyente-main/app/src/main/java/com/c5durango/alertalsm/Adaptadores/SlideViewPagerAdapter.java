package com.c5durango.alertalsm.Adaptadores;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.c5durango.alertalsm.Constantes;
import com.c5durango.alertalsm.MainActivity;
import com.c5durango.alertalsm.R;
import com.c5durango.alertalsm.SlideActivity;

public class SlideViewPagerAdapter extends PagerAdapter {

    /*
    * Clase para el adaptador que se muestra al inicio con la información
    * de para que es necesario cada permiso.
    *
    * */

    Context ctx;
    Activity act;

    ImageView logo, ind1, ind2, ind3, ind4, ind5;
    TextView txtTitulo;
    TextView txtDescripcion ;
    ImageView back;
    ImageView next;
    Button btnComenzar, btnDarPermiso;
    LayoutInflater layoutInflater;
    View view;

    int posicion;

    public SlideViewPagerAdapter(Context ctx, Activity act) {
        this.ctx = ctx;
        this.act = act;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.slide_screen, container, false);
        posicion = position;

        logo = view.findViewById(R.id.imgLogoSlide);
        ind1 = view.findViewById(R.id.ind1);
        ind2 = view.findViewById(R.id.ind2);
        ind3 = view.findViewById(R.id.ind3);
        ind4 = view.findViewById(R.id.ind4);
        ind5 = view.findViewById(R.id.ind5);

        txtTitulo = view.findViewById(R.id.txtTituloSlide);
        txtDescripcion = view.findViewById(R.id.txtDescripcionSlide);

        back = view.findViewById(R.id.imgBackSlide);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideActivity.viewPager.setCurrentItem(position-1);
            }
        });

        next = view.findViewById(R.id.imgNextSlide);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideActivity.viewPager.setCurrentItem(position+1);
            }
        });

        btnComenzar = view.findViewById(R.id.btnStartedSlide);
        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

        btnDarPermiso = view.findViewById(R.id.btnDarPermiso);
        btnDarPermiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 1:
                        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.MY_PERMISSIONS_REQUEST_UBICAC);
                        } else {
                            Toast.makeText(ctx, "¡Permiso ya otorgado!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.CAMERA}, Constantes.MY_PERMISSIONS_REQUEST_ALMAC_WRITE);
                        } else {
                            Toast.makeText(ctx, "¡Permiso ya otorgado!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.RECORD_AUDIO}, Constantes.MY_PERMISSIONS_REQUEST_CAMERA);
                        } else {
                            Toast.makeText(ctx, "¡Permiso ya otorgado!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 4:
                        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constantes.MY_PERMISSIONS_REQUEST_MICROF);
                        } else {
                            Toast.makeText(ctx, "¡Permiso ya otorgado!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        dibujarInterfaz(position);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    private void dibujarInterfaz(int position){
        switch (position){
            case 0:
                logo.setImageResource(R.drawable.slider_alerta);
                ind1.setImageResource(R.drawable.selected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);

                txtTitulo.setText("Aviso");
                txtDescripcion.setText(R.string.datos_personales);
                btnDarPermiso.setVisibility(View.INVISIBLE);
                back.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                btnComenzar.setVisibility(View.GONE);

                break;
            case 1:
                logo.setImageResource(R.drawable.slider_ubicacion);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.selected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);


                txtTitulo.setText("Ubicación");
                txtDescripcion.setTextSize(15);
                txtDescripcion.setText("Esta app recopila datos de ubicación para habilitar la función de alerta de pánico, incluso cuando la app está cerrada o no está en uso." +
                        "\nUnicamente accederemos a tu ubicación cuando presiones el botón de alerta, de lo contrario no.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                btnComenzar.setVisibility(View.VISIBLE);

                if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    btnDarPermiso.setVisibility(View.INVISIBLE);
                } else{
                    btnDarPermiso.setVisibility(View.VISIBLE);
                }
                break;

            case 2:
                logo.setImageResource(R.drawable.slider_fotos);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.selected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);

                txtTitulo.setText("Cámara");
                txtDescripcion.setText("Es necesario para tomar fotografías cuando generes una alerta, unicamente en ese momento se tomarán.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                btnComenzar.setVisibility(View.VISIBLE);

                if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    btnDarPermiso.setVisibility(View.INVISIBLE);
                } else{
                    btnDarPermiso.setVisibility(View.VISIBLE);
                }
                break;

            case 3:
                logo.setImageResource(R.drawable.slider_microfono);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.selected);
                ind5.setImageResource(R.drawable.unselected);

                txtTitulo.setText("Micrófono");
                txtDescripcion.setText("Con este acceso podremos grabar un audio de 30 segundos cuando la alerta sea generada.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                btnComenzar.setVisibility(View.VISIBLE);

                if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    btnDarPermiso.setVisibility(View.INVISIBLE);
                } else{
                    btnDarPermiso.setVisibility(View.VISIBLE);
                }
                break;

            case 4:
                logo.setImageResource(R.drawable.slider_almacenamiento);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.selected);

                txtTitulo.setText("Almacenamiento");
                txtDescripcion.setText("Para continuar danos acceso al almacenamiento, este permiso nos ayudará a guardar las grabaciones de audio en este dispositivo.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                btnComenzar.setVisibility(View.VISIBLE);

                if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    btnDarPermiso.setVisibility(View.INVISIBLE);
                } else{
                    btnDarPermiso.setVisibility(View.VISIBLE);
                }
                break;

        }
    }


}
