package com.usac.brayan.mensajeriaarquitectura;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Brayan on 11/02/2017.
 */
public class Publicacion implements Parcelable{

    String contenido;
    String para;
    String fecha;
    int idPublicacion;
    String titulo;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Publicacion(String mensaje){
        this.contenido=mensaje;
    }

    public Publicacion(String mensaje,String emisor){
        this.contenido=mensaje;
        this.para=emisor;
    }

    public Publicacion(String mensaje,String para,String fecha,int idPublicacion,String titulo){
        this.contenido=mensaje;
        this.para=para;
        this.fecha=fecha;
        this.idPublicacion=idPublicacion;
        this.titulo= titulo;
    }


    protected Publicacion(Parcel in) {
        contenido = in.readString();
        para = in.readString();
        fecha = in.readString();
        idPublicacion = in.readInt();
    }

    public static final Creator<Publicacion> CREATOR = new Creator<Publicacion>() {
        @Override
        public Publicacion createFromParcel(Parcel in) {
            return new Publicacion(in);
        }

        @Override
        public Publicacion[] newArray(int size) {
            return new Publicacion[size];
        }
    };

    public void readFromParcel(Parcel in) {
        contenido = in.readString();
        para = in.readString();
        fecha = in.readString();
        idPublicacion = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(contenido);
        parcel.writeValue(para);
        parcel.writeValue(fecha);
        parcel.writeValue(idPublicacion);
    }
}
