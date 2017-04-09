package com.usac.brayan.mensajeriaarquitectura;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ServicioNotificacionesFARUSAC extends Service{
    public static SocketIO sc;
    public static SessionManager sm;

    @Override
    public void onCreate() {
        super.onCreate();
        sm = new SessionManager(this);
        sc=new SocketIO(this);
        sc.escucharNotificaciones();
        sc.registrarse();
        Toast.makeText(this, "Servicio NOTIFICACIONES FARUSAC corriendo!", Toast.LENGTH_SHORT).show();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sc.registrarse();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Servicio NOTIFICACIONES FARUSAC detenido!", Toast.LENGTH_SHORT).show();
        sc.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }



}