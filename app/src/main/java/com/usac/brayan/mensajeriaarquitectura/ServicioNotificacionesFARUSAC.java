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
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
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
    private Thread workerThread = null;
    CountDownTimer timer;
    private PowerManager.WakeLock wl;

    @Override
    public void onCreate() {
        super.onCreate();
        sm = new SessionManager(this);
        sc=new SocketIO(this);
        sc.escucharNotificaciones();
        //sc.registrarse();
        Log.d("SocketIO","Service onCreate");
        PowerManager pm = (PowerManager)getApplicationContext().getSystemService(
                Context.POWER_SERVICE);
        this.wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                "socketIO");
        wl.acquire();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //sc.registrarse();
        Log.d("SocketIO","OnStartCommand");
        //verifyStatusSocket();
        return Service.START_STICKY;
    }

    private void verifyStatusSocket(){
        if(timer==null) {
            timer = new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    if (!sc.isConnected()) {
                        sc.connect();
                        Log.d("SocketIO", "Reconnecting...");
                    } else {
                        Log.d("SocketIO", "Is connected...");
                    }
                    timer=null;
                    verifyStatusSocket();
                }
            }.start();
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent){

        super.onTaskRemoved(rootIntent);

        this.startService(new Intent(this,ServicioNotificacionesFARUSAC.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SocketIO","OnDestroy");
        Toast.makeText(this,"SocketIO onDestroy",Toast.LENGTH_LONG).show();
        sc.close();
        wl.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }



}