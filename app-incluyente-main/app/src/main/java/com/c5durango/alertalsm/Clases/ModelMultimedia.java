package com.c5durango.alertalsm.Clases;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ModelMultimedia {

    ArrayList<Uri> arrayImagenURI;
    ArrayList<Uri> arrayAudioURI;
    ArrayList<Uri> arrayVideoURI;
    ArrayList<String> listPathAudio;

    public ModelMultimedia() {
    }

    public ModelMultimedia(ArrayList<Uri> arrayImagenURI, ArrayList<Uri> arrayAudioURI, ArrayList<Uri> arrayVideoURI, ArrayList<String> listPathAudio) {
        this.arrayImagenURI = arrayImagenURI;
        this.arrayAudioURI = arrayAudioURI;
        this.arrayVideoURI = arrayVideoURI;
        this.listPathAudio = listPathAudio;
    }

    public void eliminarPosImagenURI(int pos){
        this.arrayImagenURI.remove(pos);
    }

    public void eliminarPosAudioURI(int pos){
        this.arrayAudioURI.remove(pos);
    }

    public void eliminarPosVideoURI(int pos){
        this.arrayVideoURI.remove(pos);
    }

    public void eliminarPosPathAudio(int pos){
        this.listPathAudio.remove(pos);
    }

    public ArrayList<Uri> getArrayImagenURI() {
        return arrayImagenURI;
    }

    public void setArrayImagenURI(ArrayList<Uri> arrayImagenURI) {
        this.arrayImagenURI = arrayImagenURI;
    }

    public ArrayList<Uri> getArrayAudioURI() {
        return arrayAudioURI;
    }

    public void setArrayAudioURI(ArrayList<Uri> arrayAudioURI) {
        this.arrayAudioURI = arrayAudioURI;
    }

    public ArrayList<Uri> getArrayVideoURI() {
        return arrayVideoURI;
    }

    public void setArrayVideoURI(ArrayList<Uri> arrayVideoURI) {
        this.arrayVideoURI = arrayVideoURI;
    }

    public ArrayList<String> getListPathAudio() {
        return listPathAudio;
    }

    public void setListPathAudio(ArrayList<String> listPathAudio) {
        this.listPathAudio = listPathAudio;
    }
}
