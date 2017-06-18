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
import android.view.View;
import android.widget.Toast;


import com.onesignal.OneSignal;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

public class SocketIO {
    private Socket mSocket;
    private Context miContexto;
    private String nombreHost="node-server-bragonya.c9users.io";
    private String puertoHost="8080";
    private static final int NOTIFICATION_ID = 101;
    private NotificationCompat.Builder builder;
    private boolean forceNoConnect=false;

    public boolean isForceNoConnect() {
        return forceNoConnect;
    }

    public void setForceNoConnect(boolean forceNoConnect) {
        this.forceNoConnect = forceNoConnect;
    }

    public SocketIO(Context mc, SocketIOSubscriber subscriber) {
        this.miContexto=mc;
        builder = new NotificationCompat.Builder(miContexto);
        connect("constructor",subscriber);
    }

    public void connect(String caller, final SocketIOSubscriber subscriber){
        Log.d("SocketIO Connect",caller);
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = false;
            opts.forceNew= true;
            mSocket = IO.socket("http://" + nombreHost + ":" + puertoHost,opts);
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    subscriber.onComplete();
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    forceNoConnect=true;
                    subscriber.onError(new Throwable("El Socket se desconecto"));
                }

            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    forceNoConnect=true;
                    subscriber.onError(new Throwable("Error de conexión"));
                }

            }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    forceNoConnect=true;
                    subscriber.onError(new Throwable("Error de time out"));
                }

            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    forceNoConnect=true;
                    subscriber.onError(new Throwable("Error en el evento"));
                }

            });
        } catch (URISyntaxException e) {}
        if(!mSocket.connected())
            mSocket.connect();

    }

    public void disconnect(){
        mSocket.disconnect();
        mSocket.close();
        mSocket.off();
    }

    public boolean isConnected(){
        if(forceNoConnect) {
            forceNoConnect=false;
            return false;
        }
        return mSocket.connected();
    }



    public void registrarse(String keyChain){
        mSocket.emit("app_user","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\",\"role\":\""+ServicioNotificacionesFARUSAC.sm.getRole()+"\",\"keyChain\":\""+keyChain+"\"}");
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
        mSocket.on("recieverPublications",recieverPublications);
        mSocket.on("newPublication",newPublication);
        mSocket.on("responsePublicacion",responsePublicacion);
        mSocket.on("recieverRealTimePublications",recieverRealTimePublications);
        mSocket.on("responseAuthPublication",responseAuthPublication);
        mSocket.on("recieverAlumnos",recieverAlumnos);
        mSocket.on("recibirEstadoSesion",recibirEstadoSesion);
    }

    public void pedirCursosMaestro(){
        mSocket.emit("listaCursosMaestro","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\"}");
    }

    public void authPublication(){
        mSocket.emit("authPublication","{\"codigo\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\"}");
    }

    public void getLastPublicacion(){
        mSocket.emit("getLastPublicacion","{\"para\":\""+ServicioNotificacionesFARUSAC.sm.getRole()+"\",\"lastId\":\""+ServicioNotificacionesFARUSAC.sm.getLastPublicationRegister()+"\"}");
        Log.d("SocketIO","getLastPublicacion "+"{\"para\":\""+ServicioNotificacionesFARUSAC.sm.getRole()+"\",\"lastId\":\""+ServicioNotificacionesFARUSAC.sm.getLastPublicationRegister()+"\"}");
    }

    public void pedirCursosAlumno(){
        mSocket.emit("listaCursosAlumno","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\"}");
    }

    public void getPublicaciones(int para, int pagination){
        mSocket.emit("getPublicacion","{\"para\":\""+para+"\",\"pagination\":\""+pagination+"\"}");
    }

    public void publicar(int para, String mensaje){
        mSocket.emit("publicar","{\"para\":\""+para+"\",\"contenido\":\""+mensaje+"\",\"codigo\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\"}");
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

    public void getAlumnos(String curso, String seccion){
        mSocket.emit("getAlumnos","{\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
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


    public void deleteSesion(){
        String cui=ServicioNotificacionesFARUSAC.sm.getId();
        Log.d("deleteSesion",ServicioNotificacionesFARUSAC.sm.getId());
        mSocket.emit("deleteSesion","{\"username\":\""+ServicioNotificacionesFARUSAC.sm.getId()+"\"}",new Ack() {
            @Override
            public void call(Object... args) {
                String response = (String) args[0];
                if(response.equals("exitoso")){
                    Autenticacion.sm.logoutUser();
                    System.exit(0);
                }
            }
        });
    }
    public void close(){

        mSocket.close();
        Log.d("SocketIO","Close");
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

    private Emitter.Listener recibirEstadoSesion = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject o= new JSONObject(args[0].toString().trim());
                        if(!o.getString("estado").equals("exitoso")){
                            /*ServicioNotificacionesFARUSAC.sc.registrarse(FirebaseInstanceId.getInstance().getToken());*/
                        }else{
                            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                                @Override
                                public void idsAvailable(String userId, String registrationId) {
                                    Log.d("debug", "User:" + userId);
                                    ServicioNotificacionesFARUSAC.sm.setToken(userId);

                                }
                            });

                        }
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
                        if(o.getString("estado").equals("exitoso")){
                            miContexto.sendBroadcast(new Intent("xyz").putExtra("estado",true));
                        }else{
                            miContexto.sendBroadcast(new Intent("xyz").putExtra("estado",false));
                        }
                        //principal.AsignarCursos(args[0].toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener responseAuthPublication = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject o = new JSONObject(args[0].toString().trim());
                        int llave = o.getInt("auth");
                        if(llave!=1) {
                            Toast.makeText(miContexto, "No tienes permisos para hacer publicaciones generales en FARUSAC ", Toast.LENGTH_LONG).show();
                        }else{
                            if(principal.writer!=null){
                                principal.writer.setVisibility(View.VISIBLE);
                            }
                        }
                        //principal.AsignarCursos(args[0].toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener recieverPublications = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(principal.publications_list!=null){
                            principal.addPublications(MensajesManager.convertJsonToPublications(args[0].toString().trim()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener recieverRealTimePublications = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(principal.publications_list!=null){
                            principal.addPublicationFirst(MensajesManager.convertJsonToPublications(args[0].toString().trim()));
                        }
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
                        if(o.isNull("error")) {
                            if (o.length() > 1) {
                                Autenticacion.entrar(o.getString("nombre"), o.getString("carne"), o.getInt("role"));
                            }
                        }else{
                            Toast.makeText(miContexto,"Sus datos son invalidos, por favor vuelva a intentarlo",Toast.LENGTH_LONG).show();
                            Autenticacion.noEntrar();
                        }
                    } catch (JSONException e) {
                        try {
                            JSONObject o = new JSONObject(args[0].toString().trim());
                            Toast.makeText(miContexto,o.getString("error"),Toast.LENGTH_LONG).show();
                        } catch (JSONException e1) {
                            Toast.makeText(miContexto,"Sus datos son invalidos, por favor vuelva a intentarlo",Toast.LENGTH_LONG).show();
                            Autenticacion.noEntrar();
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


    private Emitter.Listener recieverAlumnos = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        ArrayList<Estudiante> list = MensajesManager.convertJsonToEstudiantes(args[0].toString().trim());
                        miContexto.sendBroadcast(new Intent("recieverForEstudiantes").putExtra("listaAlumnos",list));
                    } catch (JSONException e) {
                        e.printStackTrace();
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
    private Emitter.Listener newPublication = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d("NotificationBuilder","NewNotification");
                    newPublicationNotification(args[0].toString().trim());
                }
            });
        }
    };

    private Emitter.Listener responsePublicacion = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(miContexto,args[0].toString().trim(),Toast.LENGTH_LONG).show();
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
    public void newPublicationNotification(String valor){
        if(!principal.mIsInForegroundMode && !MensajesAlumnos.mIsInForegroundMode && !MensajesMaestros.mIsInForegroundMode) {
                    Intent intent = new Intent(miContexto, Autenticacion.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(miContexto, 0, intent, 0);
                    //Se construye la notificacion
                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle("Mensajeria FARUSAC");
                    builder.setSmallIcon(R.drawable.ic_chat_bubble);
                    builder.setContentIntent(pendingIntent);
                    builder.setAutoCancel(true);
                    builder.setLargeIcon(BitmapFactory.decodeResource(miContexto.getResources(), R.drawable.ic_chat_bubble));
                    builder.setContentTitle("Mensajeria FARUSAC");
                    builder.setContentText("Tienes publicaciones nuevas");
                    builder.setTicker("Nuevas publicaciones de la unidad central de FARUSAC");
                    //Vibracion
                    builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    //LED
                    builder.setLights(Color.RED, 3000, 3000);
                    //Tono
                    builder.setSound(Uri.parse("android.resource://com.usac.brayan.mensajeriaarquitectura/" + R.raw.dog));
                    inboxStyle.addLine("Ve a la pagina princiapal de MIA");
                    builder.setStyle(inboxStyle);
                    // Enviar la notificacion
                    NotificationManager notificationManager = (NotificationManager) miContexto.getSystemService(miContexto.NOTIFICATION_SERVICE);
                    notificationManager.notify("MensajeriaFARUSAC", NOTIFICATION_ID, builder.build());
        }
    }

    public void nuevaNotificacion(String valor){
        if(!principal.mIsInForegroundMode && !MensajesAlumnos.mIsInForegroundMode && !MensajesMaestros.mIsInForegroundMode) {
            try {
                LinkedList<ChatMessage> notificaciones=MensajesManager.convertJsonToMensajeWithNoDate(valor);
                if(notificaciones.size()>0) {
                    Intent intent = new Intent(miContexto, Autenticacion.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(miContexto, 0, intent, 0);
                    //Se construye la notificacion
                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle("Mensajeria FARUSAC");
                    builder.setSmallIcon(R.drawable.ic_chat_bubble_new);
                    builder.setContentIntent(pendingIntent);
                    builder.setAutoCancel(true);
                    builder.setLargeIcon(BitmapFactory.decodeResource(miContexto.getResources(), R.drawable.ic_chat_bubble_new));
                    builder.setContentTitle("Mensajeria FARUSAC");
                    if (notificaciones.size() < 5) {
                        builder.setContentText("Tienes " + notificaciones.size() + " mensajes nuevos");
                    } else {
                        builder.setContentText("Tienes varios mensajes nuevos");
                    }
                    builder.setTicker(notificaciones.get(0).getCurso() + "-" + notificaciones.get(0).getSeccion() + ":" + notificaciones.get(0).getMessage());
                    //Vibracion
                    builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    //LED
                    builder.setLights(Color.RED, 3000, 3000);
                    //Tono
                    builder.setSound(Uri.parse("android.resource://com.usac.brayan.mensajeriaarquitectura/" + R.raw.dog));
                    for (int i = 0; i < notificaciones.size(); i++) {
                        ChatMessage temp = notificaciones.get(i);
                        inboxStyle.addLine(temp.getCurso() + "-" + temp.getSeccion() + ":" + temp.getMessage().replace("$32", " ").replace("$33","\"").replace("$34","\'"));

                    }
                    builder.setStyle(inboxStyle);
                    // Enviar la notificacion
                    NotificationManager notificationManager = (NotificationManager) miContexto.getSystemService(miContexto.NOTIFICATION_SERVICE);
                    notificationManager.notify("MensajeriaFARUSAC", NOTIFICATION_ID, builder.build());
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }


    }

