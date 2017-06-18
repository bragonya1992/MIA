package com.usac.brayan.mensajeriaarquitectura.oneSignal;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.usac.brayan.mensajeriaarquitectura.ChatMessage;
import com.usac.brayan.mensajeriaarquitectura.Curso;
import com.usac.brayan.mensajeriaarquitectura.Publicacion;
import com.usac.brayan.mensajeriaarquitectura.ServicioNotificacionesFARUSAC;
import com.usac.brayan.mensajeriaarquitectura.principal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by brayan on 16/06/17.
 */

public class MessageDataHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;

        if (data != null) {
            try {
                if(data.getString("type").equals("notification")) {
                    tryToShowMessage(data.getString("section"),data.getString("curse"),data.getString("content"),data.getString("date"));
                }else{
                    tryToShowPublication(data.getString("content"),data.getString("to"),data.getString("date"),data.getString("publication"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public static void tryToShowMessage(String Section,String Curse,String Content,String Date){
        final ChatMessage temp = new ChatMessage(0,Section,Curse,Content,"",Date);
        final Curso temporal = principal.buscarCurso(Curse, Section);
        String curse="";
        String section="";
        if (temporal != null) {
            if (temporal.mensajes != null) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Code to run on UI thread
                        temporal.mensajes.add(temp);
                    }
                });

                curse=Curse;
                section=Section;
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

    public static void tryToShowPublication(String Content,String To,String Date,String Publication){
        if(principal.publications_list!=null){
            Publicacion pub = new Publicacion(Content,To,Date,Integer.parseInt(Publication));
            final List<Publicacion> list = new ArrayList<>();
            list.add(pub);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Code to run on UI thread
                    principal.addPublicationFirst(list);
                }
            });

        }
    }
}


