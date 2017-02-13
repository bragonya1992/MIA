package com.usac.brayan.mensajeriaarquitectura;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Brayan on 22/11/2016.
 */
public class Curso {
    public String nombre;
    public String seccion;
    public String catedratico;
    public int contador;
    public ChatArrayAdapter mensajes;
    public LinkedList<ChatMessage> cola=new LinkedList<>();
    public Curso(String nombre, String seccion) {

        this.nombre = nombre;
        this.seccion = seccion;
    }

    public Curso(String nombre, String seccion, String catedratico,int contador) {
        this.nombre = nombre;
        this.seccion = seccion;
        this.catedratico= catedratico;
        this.contador=contador;
    }

    public Curso(String nombre, String seccion, String catedratico) {
        this.nombre = nombre;
        this.seccion = seccion;
        this.catedratico= catedratico;
        this.contador=contador;
    }

    public void setLista(Context ct, int recurso){
        if(mensajes==null)
            mensajes=new ChatArrayAdapter(ct,recurso);
    }

    public Curso() {
    }
}
