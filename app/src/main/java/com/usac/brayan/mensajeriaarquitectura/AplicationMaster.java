package com.usac.brayan.mensajeriaarquitectura;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import com.usac.brayan.mensajeriaarquitectura.oneSignal.MessageDataHandler;

import com.onesignal.OneSignal;

/**
 * Created by Brayan on 31/05/2017.
 */
public class AplicationMaster extends Application {
    private int numRunningActivities=0;
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationReceivedHandler(new MessageDataHandler())
                .init();


        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {


            @Override
            public void onActivityStarted(Activity activity) {
                numRunningActivities++;
                if (numRunningActivities == 1) {
                    if(ServicioNotificacionesFARUSAC.sc!=null){
                        if(ServicioNotificacionesFARUSAC.sc.isConnected()){
                            ServicioNotificacionesFARUSAC.sc.escucharNotificaciones();

                        }else{
                            ServicioNotificacionesFARUSAC.sc.connect("master", new SocketIOSubscriber(){
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
                            ServicioNotificacionesFARUSAC.sc.escucharNotificaciones();
                            Log.d("ActivityLifeCycle","socket connect");
                        }
                    }else{
                        ServicioNotificacionesFARUSAC.newInstance(activity, new SocketIOSubscriber(){
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
                        Log.d("SocketIO","LifeCycle new instance");
                    }

                }

            }

            @Override
            public void onActivityStopped(Activity activity) {

                numRunningActivities--;
                if (numRunningActivities == 0) {
                    //ServicioNotificacionesFARUSAC.sc.disconnect();
                    ServicioNotificacionesFARUSAC.sc.disconnect();
                    ServicioNotificacionesFARUSAC.sc= null;
                    Log.d("SocketIO","LifeCycle delete instance");
                }
            }


            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
        });

    }
}
