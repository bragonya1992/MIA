package com.usac.brayan.mensajeriaarquitectura;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Brayan on 13/05/2017.
 */
public class Estudiante implements Serializable {
    String nombre;
    String cui;


    public String getNombre() {
        return nombre;
    }

    public Estudiante(String nombre, String cui) {
        this.nombre = nombre;
        this.cui = cui;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }


}
