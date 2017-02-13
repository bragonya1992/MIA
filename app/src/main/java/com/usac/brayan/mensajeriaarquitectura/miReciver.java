package com.usac.brayan.mensajeriaarquitectura;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class miReciver extends BroadcastReceiver {
    private SessionManager sm;
    public miReciver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sm=new SessionManager(context);
        if(sm.isLoggedIn()) {
            Intent servicio = new Intent(context, ServicioNotificacionesFARUSAC.class);
            context.startService(servicio);
        }
    }
}
