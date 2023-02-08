package com.c5durango.alertalsm.Clases;

public class ModelEmergencia {

    String cuerpoEmergencia;
    String tipoEmergencia;

    public ModelEmergencia(String cuerpoEmergencia, String tipoEmergencia){
        this.cuerpoEmergencia = cuerpoEmergencia;
        this.tipoEmergencia = tipoEmergencia;
    }

    public String getCuerpoEmergencia() {
        return cuerpoEmergencia;
    }

    public void setCuerpoEmergencia(String cuerpoEmergencia) {
        this.cuerpoEmergencia = cuerpoEmergencia;
    }

    public String getTipoEmergencia() {
        return tipoEmergencia;
    }

    public void setTipoEmergencia(String tipoEmergencia) {
        this.tipoEmergencia = tipoEmergencia;
    }
}
