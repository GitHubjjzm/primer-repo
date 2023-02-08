package com.c5durango.alertalsm.Clases;

public class ModelReportesLocal {
    int id_reporte;
    String fecha_hora;
    Double latitud;
    Double longitud;
    String lugar;
    String incidente;
    String subtipo;
    String victimas;
    String descripcion;
    int imagenes;
    int audio;
    int video;
    String estatus;

    public ModelReportesLocal(){}

    public ModelReportesLocal(int id_reporte, String fecha_hora, Double latitud, Double longitud, String lugar, String incidente, String subtipo, String victimas, String descripcion, int imagenes, int audio, int video, String estatus) {
        this.id_reporte = id_reporte;
        this.fecha_hora = fecha_hora;
        this.latitud = latitud;
        this.longitud = longitud;
        this.lugar = lugar;
        this.incidente = incidente;
        this.subtipo = subtipo;
        this.victimas = victimas;
        this.descripcion = descripcion;
        this.imagenes = imagenes;
        this.audio = audio;
        this.video = video;
        this.estatus = estatus;
    }

    public int getId_reporte() {
        return id_reporte;
    }

    public void setId_reporte(int id_reporte) {
        this.id_reporte = id_reporte;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getIncidente() {
        return incidente;
    }

    public void setIncidente(String incidente) {
        this.incidente = incidente;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public String getVictimas() {
        return victimas;
    }

    public void setVictimas(String victimas) {
        this.victimas = victimas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getImagenes() {
        return imagenes;
    }

    public void setImagenes(int imagenes) {
        this.imagenes = imagenes;
    }

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
