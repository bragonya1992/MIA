package com.usac.brayan.mensajeriaarquitectura;

/**
 * Created by Brayan on 2/01/2017.
 */
public class Mensaje {

    private String curso;
    private String seccion;
    private String mensaje;
    private String fecha;

    public Mensaje(String seccion, String curso, String mensaje, String fecha) {
        this.seccion = seccion;
        this.curso = curso;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getCurso() {
        return curso;
    }

    public String getFecha(){
        return this.fecha;
    }

    public void setFecha(String fecha){
        this.fecha=fecha;
    }

    public String getSeccion() {
        return seccion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
