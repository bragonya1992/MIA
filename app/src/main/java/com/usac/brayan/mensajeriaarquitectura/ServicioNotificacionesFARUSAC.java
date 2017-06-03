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


public class ServicioNotificacionesFARUSAC{
    public static SocketIO sc;
    public static SessionManager sm;
    private Thread workerThread = null;
    static CountDownTimer timer;

    public static void newInstance(Context c) {
        sm = new SessionManager(c);
        sc = new SocketIO(c);
        sc.escucharNotificaciones();
        //verifyStatusSocket();
    }

/*    public static void verifyStatusSocket(){
        if(timer==null) {
            timer = new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    if(sc!=null){
                        Log.d("SocketIO","is in memory");
                        if(sc.isConnected()){
                            Log.d("SocketIO","is connected");
                        }else{
                            Log.d("SocketIO","is disconnected");
                        }
                    }
                    timer=null;
                    verifyStatusSocket();
                }
            }.start();
        }
    }*/



}