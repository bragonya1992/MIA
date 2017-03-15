package com.usac.brayan.mensajeriaarquitectura;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.thread.EventThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

public class SocketIO {
    private Socket mSocket;
    private Context miContexto;
    private String nombreHost="192.168.42.131";
    private String puertoHost="8081";
    private static final int NOTIFICATION_ID = 101;
    private NotificationCompat.Builder builder;
    public SocketIO(Context mc) {
        this.miContexto=mc;
        builder = new NotificationCompat.Builder(miContexto);
        try {
            mSocket = IO.socket("http://"+nombreHost+":"+puertoHost);
        } catch (URISyntaxException e) {}
        if(!mSocket.connected())
            mSocket.connect();
    }


    public void registrarse(){
        mSocket.emit("app_user","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\"}");
    }

    public void solicitarAutenticacion(String carne, String role, String pass){
        mSocket.emit("autenticar","{\"carne\":\""+carne+"\",\"role\":\""+role+"\",\"pass\":\""+pass+"\"}");
    }

    public void registrarUsuario(String codigo,String username, String role, String pass){
        mSocket.emit("registrarUsuario","{\"codigo\":\""+codigo+"\",\"role\":\""+role+"\",\"username\":\""+username+"\",\"pass\":\""+pass+"\"}");
    }

    public void esperarRespuesta(){
        mSocket.on("responseAutenticar",responseAutenticar);
    }

    public void esperarRegistro(){
        mSocket.on("recibirEstadoRegistro",recibirEstadoRegistro);
    }

    public void escucharNotificaciones(){
        mSocket.on("inbox",notificacion);
        mSocket.on("recibirCursos",recibirCursos);
        mSocket.on("recibirMensajes",recibirMensajes);
        mSocket.on("recibirTop",recibirTop);
        mSocket.on("recibirListadoCursos",recibirListadoCursos);
        mSocket.on("recibirAsignacionCurso",recibirAsignacionCurso);
        mSocket.on("recibirEstadoRegistro",recibirEstadoRegistro);
    }

    public void pedirCursosMaestro(){
        mSocket.emit("listaCursosMaestro","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\"}");
    }

    public void pedirCursosAlumno(){
        mSocket.emit("listaCursosAlumno","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\"}");
    }

    public void pedirListadoCurso(){

        mSocket.emit("getListadoCursos");
    }

    public void pedirTopAlumno(int i, int f,String curso, String seccion){
        mSocket.emit("getTopAlumno","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\",\"inicio\":\""+i+"\",\"final\":\""+f+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
    }

    public void pedirTopMaestro(int i, int f,String curso, String seccion){
        mSocket.emit("getTopMaestro","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\",\"inicio\":\""+i+"\",\"final\":\""+f+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
    }

    public void pedirMensajesAlumno(String curso, String seccion){
        mSocket.emit("getMensajesAlumno","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
    }

    public void pedirMensajesMaestro(String curso, String seccion){
        mSocket.emit("getMensajesMaestro","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
    }

    public void cambiarVisibilidad(String curso, String seccion){
        mSocket.emit("cambiarVisibilidad","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
    }

    public void enviarMensaje(String curso, String seccion,String mensaje){
        mSocket.emit("sendMessage","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\",\"mensaje\":\""+mensaje+"\"}");
    }

    public void enviarAsignacionCurso(String curso, String seccion){
        mSocket.emit("enviarAsignacionCurso","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
    }

    public void close(){
        mSocket.close();
    }

    private Emitter.Listener recibirCursos = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        principal.AsignarCursos(args[0].toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener recibirAsignacionCurso = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject o = new JSONObject(args[0].toString().trim());
                        Toast.makeText(miContexto,"Estado de la asignacion del curso "+o.getString("curso")+" seccion "+o.getString("seccion")+":"+o.getString("estado"),Toast.LENGTH_LONG).show();
                        //principal.AsignarCursos(args[0].toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener recibirEstadoRegistro = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject o = new JSONObject(args[0].toString().trim());
                        Toast.makeText(miContexto,"El estado de su registro ha sido: "+o.getString("estado"),Toast.LENGTH_LONG).show();
                        //principal.AsignarCursos(args[0].toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener responseAutenticar = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject o = new JSONObject(args[0].toString().trim());
                        if(o.length()>1) {
                            Autenticacion.entrar(o.getString("nombre"), o.getInt("carne"), o.getInt("role"));
                        }
                    } catch (JSONException e) {
                        try {
                            JSONObject o = new JSONObject(args[0].toString().trim());
                            Autenticacion.noEntrar(o.getString("error"));
                        } catch (JSONException e1) {
                            Autenticacion.noEntrar("Sus datos son invalidos, por favor vuelva a intentarlo");
                        }
                    }
                }
            });
        }
    };



    private Emitter.Listener recibirMensajes = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                        if (ServicioNotificacionesFARUSAC.sm.getRole() == 2) {
                            try {
                                LinkedList<ChatMessage> list = MensajesManager.convertJsonToMensaje(args[0].toString().trim());
                                int total=list.size()-1;
                                for (int i = 0; i < list.size(); i++) {
                                    ChatMessage temp = list.get(total-i);
                                    Curso temporal = principal.buscarCurso(temp.getCurso(), temp.getSeccion());
                                    if (temporal != null) {
                                        if (temporal.mensajes != null) {
                                            temporal.mensajes.add(temp);
                                        } else {
                                            temporal.cola.add(temp);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                LinkedList<ChatMessage> list = MensajesManager.convertJsonToMensaje(args[0].toString().trim());
                                int total=list.size()-1;
                                String curse="";
                                String section="";
                                for (int i = 0; i < list.size(); i++) {
                                    ChatMessage temp = list.get(total -i);
                                    Curso temporal = principal.buscarCurso(temp.getCurso(), temp.getSeccion());
                                    if (temporal != null) {
                                        if (temporal.mensajes != null) {
                                            temporal.mensajes.add(temp);
                                            curse=temp.getCurso();
                                            section=temp.getSeccion();
                                        } else {
                                            temporal.cola.add(temp);
                                        }
                                    }
                                }
                                ServicioNotificacionesFARUSAC.sc.cambiarVisibilidad(curse, section);
                                if(principal.mIsInForegroundMode) {
                                    ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                }
            });
        }
    };

    private Emitter.Listener recibirListadoCursos = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (ServicioNotificacionesFARUSAC.sm.getRole() == 2) {

                    } else {
                        try {
                            LinkedList<Curso> list = MensajesManager.convertJsonToCursos(args[0].toString().trim());
                            for(int i=0;i<list.size();i++){
                                if(AsignacionDeCursos.adaptador!=null){
                                    if(principal.buscarCurso(list.get(i).nombre,list.get(i).seccion)==null) {
                                        AsignacionDeCursos.adaptador.add(list.get(i));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    };


    private Emitter.Listener recibirTop = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(ServicioNotificacionesFARUSAC.sm.getRole()==2) {
                        try {
                            LinkedList<ChatMessage> list =MensajesManager.convertJsonToMensaje(args[0].toString().trim());
                            for(int i=0;i<list.size();i++){
                                ChatMessage temp = list.get(i);
                                Curso temporal=principal.buscarCurso(temp.getCurso(),temp.getSeccion());
                                if(temporal!=null){
                                    if(temporal.mensajes!=null) {
                                        temporal.mensajes.addFirst(temp);
                                    }else {
                                        temporal.cola.addLast(temp);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            LinkedList<ChatMessage> list =MensajesManager.convertJsonToMensaje(args[0].toString().trim());
                            for(int i=0;i<list.size();i++){
                                ChatMessage temp = list.get(i);
                                Curso temporal=principal.buscarCurso(temp.getCurso(),temp.getSeccion());
                                if(temporal!=null){
                                    if(temporal.mensajes!=null) {
                                        temporal.mensajes.addFirst(temp);
                                    }else {
                                        temporal.cola.addLast(temp);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    private Emitter.Listener notificacion = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    nuevaNotificacion(args[0].toString().trim());
                }
            });
        }
    };

    public void cancelNotification()
    {
        //you can get notificationManager like this:
        //notificationManage r= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManager notificationManager = (NotificationManager) miContexto.getSystemService(miContexto.NOTIFICATION_SERVICE);
        notificationManager.cancel("MensajeriaFARUSAC", NOTIFICATION_ID);
    }


    public void nuevaNotificacion(String valor){
        if(!principal.mIsInForegroundMode && !MensajesAlumnos.mIsInForegroundMode && !MensajesMaestros.mIsInForegroundMode) {
            try {
                LinkedList<ChatMessage> notificaciones=MensajesManager.convertJsonToMensaje(valor);
                Intent intent = new Intent(miContexto, Autenticacion.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(miContexto, 0, intent, 0);
                //Se construye la notificacion
                NotificationCompat.InboxStyle inboxStyle =new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle("Mensajeria FARUSAC");
                builder.setSmallIcon(R.drawable.ic_menu_share);
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true);
                builder.setLargeIcon(BitmapFactory.decodeResource(miContexto.getResources(), R.drawable.ic_menu_slideshow));
                builder.setContentTitle("Mensajeria FARUSAC");
                builder.setContentText("Tienes " + notificaciones.size() + " mensajes nuevos");
                builder.setTicker(notificaciones.get(0).getCurso()+ "-" + notificaciones.get(0).getSeccion() + ":" + notificaciones.get(0).getMessage());
                //Vibracion
                builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                //LED
                builder.setLights(Color.RED, 3000, 3000);
                //Tono
                builder.setSound(Uri.parse("android.resource://com.usac.brayan.mensajeriaarquitectura/"+R.raw.dog));
                for (int i = 0; i < notificaciones.size(); i++) {
                    ChatMessage temp = notificaciones.get(i);
                    inboxStyle.addLine(temp.getCurso() + "-" + temp.getSeccion() + ":" + temp.getMessage());

                }
                builder.setStyle(inboxStyle);
                // Enviar la notificacion
                NotificationManager notificationManager = (NotificationManager) miContexto.getSystemService(miContexto.NOTIFICATION_SERVICE);
                notificationManager.notify("MensajeriaFARUSAC", NOTIFICATION_ID, builder.build());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }


    }

