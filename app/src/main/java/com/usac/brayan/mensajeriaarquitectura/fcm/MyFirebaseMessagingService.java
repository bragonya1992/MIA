package com.usac.brayan.mensajeriaarquitectura.fcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.usac.brayan.mensajeriaarquitectura.ChatMessage;
import com.usac.brayan.mensajeriaarquitectura.Curso;
import com.usac.brayan.mensajeriaarquitectura.MensajesManager;
import com.usac.brayan.mensajeriaarquitectura.Publicacion;
import com.usac.brayan.mensajeriaarquitectura.ServicioNotificacionesFARUSAC;
import com.usac.brayan.mensajeriaarquitectura.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Brayan on 22/05/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if(remoteMessage.getData().get("type").equals("notification")) {
            tryToShowMessage(remoteMessage.getData());
        }else{
            tryToShowPublication(remoteMessage.getData());
        }
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

    public static void tryToShowMessage(Map<String,String> data){
        ChatMessage temp = new ChatMessage(0,data.get("section"),data.get("curse"),data.get("content"),"",data.get("date"));
        Curso temporal = principal.buscarCurso(data.get("curse"), data.get("section"));
        String curse="";
        String section="";
        if (temporal != null) {
            if (temporal.mensajes != null) {
                temporal.mensajes.add(temp);
                curse=data.get("curse");
                section=data.get("section");
            } else {
                temporal.cola.add(temp);
            }
        }
        if(!curse.equals("") && !section.equals("")) {
            ServicioNotificacionesFARUSAC.sc.cambiarVisibilidad(curse, section);
        }
        if(principal.mIsInForegroundMode) {
            ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
        }
    }

    public static void tryToShowPublication(Map<String,String> data){
        if(principal.publications_list!=null){
            Publicacion pub = new Publicacion(data.get("content"),data.get("to"),data.get("date"),Integer.parseInt(data.get("publication")));
            List<Publicacion> list = new ArrayList<>();
            list.add(pub);
            principal.addPublicationFirst(list);
        }
    }


}
