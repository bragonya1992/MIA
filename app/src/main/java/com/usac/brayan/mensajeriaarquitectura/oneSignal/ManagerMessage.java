package com.usac.brayan.mensajeriaarquitectura.oneSignal;

/**
 * Created by brayan on 16/06/17.
 */
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationPayload;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.usac.brayan.mensajeriaarquitectura.R;

import java.math.BigInteger;

public class ManagerMessage {


    public class NotificationExtenderBareBonesExample extends NotificationExtenderService {
        @Override
        protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
            // Read properties from result
            /*OverrideSettings overrideSettings = new OverrideSettings();
            overrideSettings.extender = new NotificationCompat.Extender() {
                @Override
                public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                    // Sets the background notification color to Green on Android 5.0+ devices.
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.new_logo));
                    builder.setSmallIcon(R.drawable.ic_chat_bubble_new);
                    builder.setSound(Uri.parse("android.resource://com.usac.brayan.mensajeriaarquitectura/" + R.raw.dog));
                    return builder.setColor(new BigInteger("FF00FF00", 16).intValue());
                }
            };*/

            /*OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);*/
            /*Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);*/

            // Return true to stop the notification from displaying.
            return false;
        }
    }
}