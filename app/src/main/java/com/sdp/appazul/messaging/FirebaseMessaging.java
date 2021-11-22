package com.sdp.appazul.messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sdp.appazul.R;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "AzulNotifications";
    SecureRandom random = new SecureRandom();
    private final int notificationId = random.nextInt(50);

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        Log.d("NOTIFICATION", "Firebase Notification call");
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage);
        }
    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        Log.d("NOTIFICATION", "Firebase Notification Token :: " + s);
    }

    public void showNotification(RemoteMessage remoteMessage) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Mynotification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setShowBadge(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_logo);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setContentTitle((Html
                    .fromHtml("<b>" + remoteMessage.getData().get("title") + "</b>")));
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("details")));
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        } else {
            builder.setSmallIcon(R.drawable.ic_logo);
            builder.setContentTitle(remoteMessage.getData().get("title"));
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("details")));
        }


        builder.setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
    }
}
