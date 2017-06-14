package com.usac.brayan.mensajeriaarquitectura.fcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.usac.brayan.mensajeriaarquitectura.ServicioNotificacionesFARUSAC;
import com.usac.brayan.mensajeriaarquitectura.SessionManager;
import com.usac.brayan.mensajeriaarquitectura.SocketIOSubscriber;

/**
 * Created by Brayan on 22/05/2017.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    static String TAG = "GCMID";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if(ServicioNotificacionesFARUSAC.sm!=null && ServicioNotificacionesFARUSAC.sm.isLoggedIn())
            sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        ServicioNotificacionesFARUSAC.newInstance(this, new SocketIOSubscriber(){
            @Override
            public void onNext(Object o) {
                /**
                 *
                 **/
                super.onNext(o);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);

            }
        });
        ServicioNotificacionesFARUSAC.sc.registrarse(token);
    }
}
