package com.c5durango.alertalsm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertalsm.Clases.ModeloLogin;
import com.c5durango.alertalsm.Firebase.Config;
import com.c5durango.alertalsm.Utilidades.Utilidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtCodigoActivacion;
    private Button btnComenzar;
    private String CODIGO_ACTIVACION = "";
    private String TAG = "Login";
    private Boolean tieneAcceso = false;
    private ModeloLogin modeloLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        modeloLogin = new ModeloLogin();


        txtCodigoActivacion = findViewById(R.id.txtCodigoActivacion);
        btnComenzar = findViewById(R.id.btnIniciarSesion);
        btnComenzar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnIniciarSesion){
            iniciarSesion();
        }
    }


    private void iniciarSesion(){
        CODIGO_ACTIVACION = txtCodigoActivacion.getText().toString().toUpperCase();
        if(CODIGO_ACTIVACION.length() <= 0 ){
            Toast.makeText(getApplicationContext(), "¡Ingrese un código de activación válido!", Toast.LENGTH_LONG).show();
        } else {

            BigInteger codigo_activacion = new BigInteger(CODIGO_ACTIVACION);
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            String URL = Constantes.URL + "/codigoactivacion";

            JSONObject jsonObjectBody = new JSONObject();
            try {
                jsonObjectBody.put("codigo_activacion", codigo_activacion);
                jsonObjectBody.put("fecha_apertura", Utilidades.obtenerFecha());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
            Log.d(TAG, URL);

            final String requestBody = jsonObjectBody.toString();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // display response
                            Log.d(TAG, "Respuesta:" + response.toString());

                            String json = response.toString();
                            JSONObject object = null;
                            JSONObject object_comercio = null;
                            try {
                                object = new JSONObject(json);
                                Boolean ok = object.getBoolean("ok");
                                if (ok) {
                                    Log.d(TAG, object.toString());
                                    if (object.has("resultado")) {

                                        final String resultado = object.getString("resultado");
                                        if (resultado.equals("Código de activación abierto con éxito")) {
                                            Toast.makeText(getApplicationContext(), "¡BIENVENIDO! \n" + resultado, Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
                                        }

                                    }

                                    String token = "S";
                                    if(object.has("token")){
                                        token = object.getString("token");
                                    }

                                    if (object.has("comercio") &&
                                            object.getString("resultado").equals("Código de activación abierto con éxito")) {
                                        // Obtener cada dato de comercio
                                        object_comercio = object.optJSONObject("comercio");

                                        modeloLogin.setId_comercio(object_comercio.getInt("id_comercio"));
                                        modeloLogin.setId_dir_comercio(object_comercio.getInt("id_dir_comercio"));
                                        modeloLogin.setNum_empleados(object_comercio.getInt("num_empleados"));
                                        modeloLogin.setNombre_comercio(object_comercio.getString("nombre_comercio"));
                                        modeloLogin.setGiro(object_comercio.getString("giro"));
                                        modeloLogin.setTelefono_fijo(object_comercio.getString("telefono_fijo"));
                                        modeloLogin.setFolio_com(object_comercio.getString("folio_comercio"));
                                        modeloLogin.setRazon_social(object_comercio.getString("razon_social"));
                                        modeloLogin.setCalle(object_comercio.getString("calle"));
                                        modeloLogin.setNumero(object_comercio.getString("numero"));
                                        modeloLogin.setColonia(object_comercio.getString("colonia"));
                                        modeloLogin.setCp(object_comercio.getInt("cp"));
                                        modeloLogin.setEntre_calle_1(object_comercio.getString("entre_calle_1"));
                                        modeloLogin.setEntre_calle_2(object_comercio.getString("entre_calle_2"));
                                        modeloLogin.setFachada(object_comercio.getString("fachada"));
                                        modeloLogin.setId_localidad(object_comercio.getInt("id_localidad"));
                                        modeloLogin.setNombre_localidad(object_comercio.getString("nombre_localidad"));
                                        modeloLogin.setId_municipio(object_comercio.getInt("id_municipios"));
                                        modeloLogin.setClave_municipio(object_comercio.getInt("clave_municipio"));
                                        modeloLogin.setNombre_municipio(object_comercio.getString("nombre_municipio"));
                                        modeloLogin.setNombre_estado(object_comercio.getString("nombre_estado"));

                                        modeloLogin.setId_usuarios_app(object_comercio.getInt("id_usuarios_app"));
                                        modeloLogin.setNombres_usuarios_app(object_comercio.getString("nombres_usuarios_app"));
                                        modeloLogin.setApell_pat(object_comercio.getString("apell_pat"));
                                        modeloLogin.setApell_mat(object_comercio.getString("apell_mat"));
                                        modeloLogin.setFecha_nacimiento(object_comercio.getString("fecha_nacimiento"));
                                        modeloLogin.setSexo_app(object_comercio.getString("sexo_app"));
                                        modeloLogin.setPadecimientos(object_comercio.getString("padecimientos"));
                                        modeloLogin.setTel_movil(object_comercio.getString("tel_movil"));
                                        modeloLogin.setAlergias(object_comercio.getString("alergias"));
                                        modeloLogin.setTipo_sangre(object_comercio.getString("tipo_sangre"));

                                        if (modeloLogin.getId_comercio() != 0) {
                                            Boolean resp = com.c5durango.alertalsm.Utilidades.PreferencesUsuario.guardarDatosComercio(getApplicationContext(),
                                                    modeloLogin.getId_comercio(),
                                                    modeloLogin.getId_dir_comercio(),
                                                    modeloLogin.getNum_empleados(),
                                                    modeloLogin.getNombre_comercio(),
                                                    modeloLogin.getGiro(),
                                                    modeloLogin.getTelefono_fijo(),
                                                    modeloLogin.getFolio_com(),
                                                    modeloLogin.getRazon_social(),
                                                    modeloLogin.getCalle(),
                                                    modeloLogin.getColonia(),
                                                    modeloLogin.getNumero(),
                                                    modeloLogin.getCp(),
                                                    modeloLogin.getEntre_calle_1(),
                                                    modeloLogin.getEntre_calle_2(),
                                                    modeloLogin.getFachada(),
                                                    modeloLogin.getId_localidad(),
                                                    modeloLogin.getNombre_localidad(),
                                                    modeloLogin.getId_municipio(),
                                                    modeloLogin.getClave_municipio(),
                                                    modeloLogin.getNombre_municipio(),
                                                    modeloLogin.getNombre_estado(),
                                                    modeloLogin.getId_usuarios_app(),
                                                    modeloLogin.getNombres_usuarios_app(),
                                                    modeloLogin.getApell_pat(),
                                                    modeloLogin.getApell_mat(),
                                                    modeloLogin.getFecha_nacimiento(),
                                                    modeloLogin.getSexo_app(),
                                                    modeloLogin.getPadecimientos(),
                                                    modeloLogin.getTel_movil(),
                                                    modeloLogin.getAlergias(),
                                                    modeloLogin.getTipo_sangre(),
                                                    token);

                                            Log.d(TAG, "La respuesta al guardar es: " + resp);
                                            // (Toast.makeText(getApplicationContext(), "La respuesta al guardar es: " + resp, Toast.LENGTH_SHORT)).show();
                                            if (resp) {
                                                tieneAcceso = true;
                                            } else {
                                                Log.d(TAG, "No se pudieron guardar los datos");
                                                (Toast.makeText(getApplicationContext(), "Error al guardar la información", Toast.LENGTH_SHORT)).show();
                                            }
                                        } else {
                                            Log.d(TAG, "No se pudieron obtener los datos");
                                            (Toast.makeText(getApplicationContext(), "Error al obtener la información", Toast.LENGTH_SHORT)).show();
                                        }
                                    } else if (object.getString("resultado").equals("Código de activación abierto con éxito")) {
                                        Log.d(TAG, "NO viene comercio");
                                        (Toast.makeText(getApplicationContext(), "Información de comercio inválida", Toast.LENGTH_SHORT)).show();
                                    } else {
                                        Log.d(TAG, "No viene comercio por que el codigo está mal");
                                    }

                                } else {
                                    Log.d(TAG, "Error al traer los datos" + object.optJSONObject("error").toString());
                                    (Toast.makeText(getApplicationContext(), "Error al traer los datos" + object.optJSONObject("error").toString(), Toast.LENGTH_SHORT)).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (tieneAcceso) {
                                Config.getTokenInstance(getApplicationContext(), modeloLogin.getId_usuarios_app());
                                //enviarTokenFirebase(modeloLogin.getId_usuarios_app(), Config.getTokenPreferences(getApplicationContext()));
                                iniciarBienvenida();
                            } else {
                                //(Toast.makeText(getApplicationContext(), "Acceso denegado #1", Toast.LENGTH_SHORT)).show();
                            }
                            requestQueue.stop();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.toString());

                            String errorResp = Utilidades.tipoErrorVolley(error);
                            Toast.makeText(getApplicationContext(), errorResp, Toast.LENGTH_SHORT).show();
                            requestQueue.stop();
                        }
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(getRequest);
        }
    }

    private void iniciarBienvenida(){
        Intent intent = new Intent(LoginActivity.this, SlideActivity.class);
        startActivity(intent);
    }



}


