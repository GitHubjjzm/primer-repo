package com.c5durango.alertalsm.Clases;

public class ModeloLogin {

    int id_comercio = 0;
    int id_dir_comercio;
    int num_empleados;
    String nombre_comercio;
    String giro;
    String telefono_fijo;
    String folio_com;
    String razon_social;
    String calle;
    String numero;
    String colonia;
    int cp;
    String entre_calle_1;
    String entre_calle_2;
    String fachada;
    int id_localidad;
    String nombre_localidad;
    int id_municipio;
    int clave_municipio;
    String nombre_municipio;
    String nombre_estado;

    int id_usuarios_app;
    String nombres_usuarios_app;
    String apell_pat;
    String apell_mat;
    String fecha_nacimiento;
    String sexo_app;
    String padecimientos;
    String tel_movil;
    String alergias;
    String tipo_sangre;


    public ModeloLogin(){

    }


    public ModeloLogin(int id_comercio, int id_dir_comercio, int num_empleados, String nombre_comercio, String giro, String telefono_fijo,
                       String folio_com, String razon_social, String calle, String numero, String colonia, int cp, String entre_calle_1,
                       String entre_calle_2, String fachada, int id_localidad, String nombre_localidad, int id_municipio, int clave_municipio, String nombre_municipio, String nombre_estado,
                       int id_usuarios_app, String nombres_usuarios_app, String apell_pat, String apell_mat, String fecha_nacimiento, String sexo_app,
                       String padecimientos, String tel_movil, String alergias, String tipo_sangre) {
        this.id_comercio = id_comercio;
        this.id_dir_comercio = id_dir_comercio;
        this.num_empleados = num_empleados;
        this.nombre_comercio = nombre_comercio;
        this.giro = giro;
        this.telefono_fijo = telefono_fijo;
        this.folio_com = folio_com;
        this.razon_social = razon_social;
        this.calle = calle;
        this.numero = numero;
        this.colonia = colonia;
        this.cp = cp;
        this.entre_calle_1 = entre_calle_1;
        this.entre_calle_2 = entre_calle_2;
        this.fachada = fachada;
        this.id_localidad = id_localidad;
        this.nombre_localidad = nombre_localidad;
        this.id_municipio = id_municipio;
        this.clave_municipio = clave_municipio;
        this.nombre_municipio = nombre_municipio;
        this.nombre_estado = nombre_estado;
        this.id_usuarios_app = id_usuarios_app;
        this.nombres_usuarios_app = nombres_usuarios_app;
        this.apell_pat = apell_pat;
        this.apell_mat = apell_mat;
        this.fecha_nacimiento = fecha_nacimiento;
        this.sexo_app = sexo_app;
        this.padecimientos = padecimientos;
        this.tel_movil = tel_movil;
        this.alergias = alergias;
        this.tipo_sangre = tipo_sangre;
    }

    public ModeloLogin(int id_comercio, int id_usuarios_app, int id_municipio, int clave_municipio) {
        this.id_comercio = id_comercio;
        this.id_usuarios_app = id_usuarios_app;
        this.id_municipio = id_municipio;
        this.clave_municipio = clave_municipio;
    }

    public int getId_comercio() {
        return id_comercio;
    }

    public void setId_comercio(int id_comercio) {
        this.id_comercio = id_comercio;
    }

    public int getId_dir_comercio() {
        return id_dir_comercio;
    }

    public void setId_dir_comercio(int id_dir_comercio) {
        this.id_dir_comercio = id_dir_comercio;
    }

    public int getNum_empleados() {
        return num_empleados;
    }

    public void setNum_empleados(int num_empleados) {
        this.num_empleados = num_empleados;
    }

    public String getNombre_comercio() {
        return nombre_comercio;
    }

    public void setNombre_comercio(String nombre_comercio) {
        this.nombre_comercio = nombre_comercio;
    }

    public String getGiro() {
        return giro;
    }

    public void setGiro(String giro) {
        this.giro = giro;
    }

    public String getTelefono_fijo() {
        return telefono_fijo;
    }

    public void setTelefono_fijo(String telefono_fijo) {
        this.telefono_fijo = telefono_fijo;
    }

    public String getFolio_com() {
        return folio_com;
    }

    public void setFolio_com(String folio_com) {
        this.folio_com = folio_com;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public String getEntre_calle_1() {
        return entre_calle_1;
    }

    public void setEntre_calle_1(String entre_calle_1) {
        this.entre_calle_1 = entre_calle_1;
    }

    public String getEntre_calle_2() {
        return entre_calle_2;
    }

    public void setEntre_calle_2(String entre_calle_2) {
        this.entre_calle_2 = entre_calle_2;
    }

    public String getFachada() {
        return fachada;
    }

    public void setFachada(String fachada) {
        this.fachada = fachada;
    }

    public int getId_localidad() {
        return id_localidad;
    }

    public void setId_localidad(int id_localidad) {
        this.id_localidad = id_localidad;
    }

    public String getNombre_localidad() {
        return nombre_localidad;
    }

    public void setNombre_localidad(String nombre_localidad) {
        this.nombre_localidad = nombre_localidad;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public String getNombre_municipio() {
        return nombre_municipio;
    }

    public void setNombre_municipio(String nombre_municipio) {
        this.nombre_municipio = nombre_municipio;
    }

    public String getNombre_estado() {
        return nombre_estado;
    }

    public void setNombre_estado(String nombre_estado) {
        this.nombre_estado = nombre_estado;
    }

    public int getId_usuarios_app() {
        return id_usuarios_app;
    }

    public void setId_usuarios_app(int id_usuarios_app) {
        this.id_usuarios_app = id_usuarios_app;
    }

    public String getNombres_usuarios_app() {
        return nombres_usuarios_app;
    }

    public void setNombres_usuarios_app(String nombres_usuarios_app) {
        this.nombres_usuarios_app = nombres_usuarios_app;
    }

    public String getApell_pat() {
        return apell_pat;
    }

    public void setApell_pat(String apell_pat) {
        this.apell_pat = apell_pat;
    }

    public String getApell_mat() {
        return apell_mat;
    }

    public void setApell_mat(String apell_mat) {
        this.apell_mat = apell_mat;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getSexo_app() {
        return sexo_app;
    }

    public void setSexo_app(String sexo_app) {
        this.sexo_app = sexo_app;
    }

    public String getPadecimientos() {
        return padecimientos;
    }

    public void setPadecimientos(String padecimientos) {
        this.padecimientos = padecimientos;
    }

    public String getTel_movil() {
        return tel_movil;
    }

    public void setTel_movil(String tel_movil) {
        this.tel_movil = tel_movil;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getTipo_sangre() {
        return tipo_sangre;
    }

    public void setTipo_sangre(String tipo_sangre) {
        this.tipo_sangre = tipo_sangre;
    }

    public int getClave_municipio() {
        return clave_municipio;
    }

    public void setClave_municipio(int clave_municipio) {
        this.clave_municipio = clave_municipio;
    }
}
