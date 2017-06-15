package com.usac.brayan.mensajeriaarquitectura.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.usac.brayan.mensajeriaarquitectura.Autenticacion;
import com.usac.brayan.mensajeriaarquitectura.ChatMessage;
import com.usac.brayan.mensajeriaarquitectura.Curso;
import com.usac.brayan.mensajeriaarquitectura.MensajesAlumnos;
import com.usac.brayan.mensajeriaarquitectura.MensajesMaestros;
import com.usac.brayan.mensajeriaarquitectura.MensajesManager;
import com.usac.brayan.mensajeriaarquitectura.Publicacion;
import com.usac.brayan.mensajeriaarquitectura.R;
import com.usac.brayan.mensajeriaarquitectura.ServicioNotificacionesFARUSAC;
import com.usac.brayan.mensajeriaarquitectura.principal;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Brayan on 22/05/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private static final int NOTIFICATION_ID = 101;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().get("type").equals("notification")) {
            nuevaNotificacion(remoteMessage.getData().get("content"),remoteMessage.getData().get("curse"),remoteMessage.getData().get("section"));
            tryToShowMessage(remoteMessage.getData());
        }else{
            nuevaPublicacion(remoteMessage.getData().get("content"));
            tryToShowPublication(remoteMessage.getData());
        }
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

    public static void tryToShowMessage(Map<String,String> data){
        final ChatMessage temp = new ChatMessage(0,data.get("section"),data.get("curse"),data.get("content"),"",data.get("date"));
        final Curso temporal = principal.buscarCurso(data.get("curse"), data.get("section"));
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

    public void nuevaNotificacion(String valor,String curso, String seccion){
        if(!principal.mIsInForegroundMode && !MensajesAlumnos.mIsInForegroundMode && !MensajesMaestros.mIsInForegroundMode) {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    Intent intent = new Intent(this, Autenticacion.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                    //Se construye la notificacion
                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle("Mensajeria FARUSAC");
                    builder.setSmallIcon(R.drawable.ic_chat_bubble_new);
                    builder.setContentIntent(pendingIntent);
                    builder.setAutoCancel(true);
                    builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_chat_bubble_new));
                    builder.setContentTitle("Mensajeria FARUSAC");
                        builder.setContentText("Tienes mensajes nuevos en "+ curso+" "+seccion);
                    builder.setTicker(curso + "-" + seccion + ":" + valor);
                    //Vibracion
                    builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    //LED
                    builder.setLights(Color.RED, 3000, 3000);
                    //Tono
                    builder.setSound(Uri.parse("android.resource://com.usac.brayan.mensajeriaarquitectura/" + R.raw.dog));

                        inboxStyle.addLine(curso + "-" + seccion + ":" + valor.replace("$32", " ").replace("$33","\"").replace("$34","\'"));

                    builder.setStyle(inboxStyle);
                    // Enviar la notificacion
                    NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
                    notificationManager.notify("MensajeriaFARUSAC", NOTIFICATION_ID, builder.build());

        }
    }

    public void nuevaPublicacion(String valor){
        if(!principal.mIsInForegroundMode && !MensajesAlumnos.mIsInForegroundMode && !MensajesMaestros.mIsInForegroundMode) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            Intent intent = new Intent(this, Autenticacion.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            //Se construye la notificacion
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Mensajeria FARUSAC");
            builder.setSmallIcon(R.drawable.ic_chat_bubble_new);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_chat_bubble_new));
            builder.setContentTitle("Mensajeria FARUSAC");
            builder.setContentText("Tienes publicaciones nuevas ");
            builder.setTicker("FARUSAC: "+valor);
            //Vibracion
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            //LED
            builder.setLights(Color.RED, 3000, 3000);
            //Tono
            builder.setSound(Uri.parse("android.resource://com.usac.brayan.mensajeriaarquitectura/" + R.raw.dog));

            inboxStyle.addLine( valor.replace("$32", " ").replace("$33","\"").replace("$34","\'"));

            builder.setStyle(inboxStyle);
            // Enviar la notificacion
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
            notificationManager.notify("MensajeriaFARUSAC", NOTIFICATION_ID, builder.build());

        }
    }

    public static void tryToShowPublication(Map<String,String> data){
        if(principal.publications_list!=null){
            Publicacion pub = new Publicacion(data.get("content"),data.get("to"),data.get("date"),Integer.parseInt(data.get("publication")));
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
