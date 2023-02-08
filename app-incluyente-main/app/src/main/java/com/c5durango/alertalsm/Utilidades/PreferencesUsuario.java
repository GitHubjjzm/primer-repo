package com.c5durango.alertalsm.Utilidades;

import android.content.Context;
import android.util.Log;

import com.c5durango.alertalsm.Clases.ModeloLogin;

public class PreferencesUsuario {

    private static String PREFERENCES_USUARIO = "Usuario";
    private static String PREFERENCES_LOGIN = "Login";
    private static String PREFERENCES_COMERCIO = "Comercio";
    private static String PREFERENCES_COMERCIO_DIRECCION = "ComercioDireccion";

    public static ModeloLogin getDatosComercioUsuario(Context context){
        ModeloLogin data = new ModeloLogin();

        android.content.SharedPreferences preferencesUsuario = context.getSharedPreferences(PREFERENCES_USUARIO, Context.MODE_PRIVATE);
        android.content.SharedPreferences preferencesDireccion = context.getSharedPreferences(PREFERENCES_COMERCIO_DIRECCION, Context.MODE_PRIVATE);


        if (preferencesUsuario.contains("id_usuarios_app")){
            data.setId_usuarios_app(preferencesUsuario.getInt("id_usuarios_app",0));
            data.setNombres_usuarios_app(preferencesUsuario.getString("nombres_usuarios_app",""));
            data.setApell_pat(preferencesUsuario.getString("apell_pat",""));
            data.setApell_mat(preferencesUsuario.getString("apell_mat",""));
            data.setTel_movil(preferencesUsuario.getString("tel_movil",""));
        }

        if (preferencesDireccion.contains("id_dir_comercio")){
            data.setId_dir_comercio(preferencesDireccion.getInt("id_dir_comercio",0));
            data.setCalle(preferencesDireccion.getString("calle",""));
            data.setNumero(preferencesDireccion.getString("numero",""));
            data.setColonia(preferencesDireccion.getString("colonia",""));
            data.setCp(preferencesDireccion.getInt("cp",0));
            data.setNombre_localidad(preferencesDireccion.getString("nombre_localidad",""));
        }
        return data;
    }

    public static Boolean guardarDatosComercio(Context context,
                                               int id_comercio,
                                               int id_dir_comercio,

                                               int num_empleados,
                                               String nombre_comercio,
                                               String giro,
                                               String telefono_fijo,
                                               String folio_comercio,
                                               String razon_social,

                                               String calle,
                                               String numero,
                                               String colonia,
                                               int cp,
                                               String entre_calle_1,
                                               String entre_calle_2,
                                               String fachada,
                                               int id_localidad,
                                               String nombre_localidad,
                                               int id_municipio,
                                               int clave_municipio,
                                               String nombre_municipio,
                                               String nombre_estado,

                                               int id_usuarios_app,
                                               String nombres_usuarios_app,
                                               String apell_pat,
                                               String apell_mat,
                                               String fecha_nacimiento,
                                               String sexo_app,
                                               String padecimientos,
                                               String tel_movil,
                                               String alergias,
                                               String tipo_sangre,

                                               String token){

        try{
            android.content.SharedPreferences preferencesLogIn = context.getSharedPreferences(PREFERENCES_LOGIN, Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editorLogin = preferencesLogIn.edit();

            android.content.SharedPreferences preferencesComercio = context.getSharedPreferences(PREFERENCES_COMERCIO, Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editorComercio = preferencesComercio.edit();

            android.content.SharedPreferences preferencesComercioDireccion = context.getSharedPreferences(PREFERENCES_COMERCIO_DIRECCION, Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editorComercioDireccion = preferencesComercioDireccion.edit();

            android.content.SharedPreferences preferencesUsuario = context.getSharedPreferences(PREFERENCES_USUARIO, Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editorUsuario = preferencesUsuario.edit();

            //Datos de LogIn
            try {
                editorLogin.putInt("comercio", id_comercio);
                editorLogin.putInt("usuario", id_usuarios_app);
                editorLogin.putString("sala", "Comercios");
                editorLogin.putInt("id_municipio", id_municipio);
                editorLogin.putInt("clave_municipio", clave_municipio);
                editorLogin.putString("token", token);
                editorLogin.commit();
            } catch (Exception e){
                Log.d("Preferences", e.getMessage());
            }

            //Datos del comercio
            editorComercio.putInt("id_comercio", id_comercio);
            editorComercio.putInt("num_empleados", num_empleados);
            editorComercio.putString("nombre_comercio", nombre_comercio);
            editorComercio.putString("giro", giro);
            editorComercio.putString("telefono_fijo", telefono_fijo);
            editorComercio.putString("folio_comercio", folio_comercio);
            editorComercio.putString("razon_social", razon_social);
            editorComercio.commit();

            //Direccion del comercio
            editorComercioDireccion.putInt("id_dir_comercio", id_dir_comercio);
            editorComercioDireccion.putString("calle", calle);
            editorComercioDireccion.putString("numero", numero);
            editorComercioDireccion.putString("colonia", colonia);
            editorComercioDireccion.putInt("cp", cp);
            editorComercioDireccion.putString("entre_calle_1", entre_calle_1);
            editorComercioDireccion.putString("entre_calle_2", entre_calle_2);
            editorComercioDireccion.putString("fachada", fachada);
            editorComercioDireccion.putInt("id_localidad", id_localidad);
            editorComercioDireccion.putString("nombre_localidad", nombre_localidad);
            editorComercioDireccion.putInt("id_municipio", id_municipio);
            editorComercioDireccion.putString("nombre_municipio", nombre_municipio);
            editorComercioDireccion.putString("nombre_estado", nombre_estado);
            editorComercioDireccion.commit();

            // Datos del usuario
            editorUsuario.putInt("id_usuarios_app", id_usuarios_app);
            editorUsuario.putString("nombres_usuarios_app", nombres_usuarios_app);
            editorUsuario.putString("apell_pat", apell_pat);
            editorUsuario.putString("apell_mat", apell_mat);
            editorUsuario.putString("fecha_nacimiento",fecha_nacimiento);
            editorUsuario.putString("sexo_app",sexo_app);
            editorUsuario.putString("padecimientos",padecimientos);
            editorUsuario.putString("tel_movil",tel_movil);
            editorUsuario.putString("alergias",alergias);
            editorUsuario.putString("tipo_sangre",tipo_sangre);
            editorUsuario.commit();


            return true;
        } catch (Exception e){
            return false;
        }
    }
}
