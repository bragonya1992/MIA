package com.usac.brayan.mensajeriaarquitectura;

/**
 * Created by Brayan on 11/02/2017.
 */
public class Publicacion {

    String contenido;
    String para;
    String fecha;

    public Publicacion(String mensaje){
        this.contenido=mensaje;
    }

    public Publicacion(String mensaje,String emisor){
        this.contenido=mensaje;
        this.para=emisor;
    }

    public Publicacion(String mensaje,String para,String fecha){
        this.contenido=mensaje;
        this.para=para;
        this.fecha=fecha;
    }


    public String getContenido() {
        return contenido;
    }

    public String getPara() {
        return para;
    }

    public String getFecha() {
        return fecha;
    }
}
