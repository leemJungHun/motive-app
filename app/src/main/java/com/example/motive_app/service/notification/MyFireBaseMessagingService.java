package com.example.motive_app.service.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.motive_app.R;
import com.example.motive_app.activity.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    int setMessageCount = 0;

    @Override
    public void onNewToken(@NotNull String s) {
        super.onNewToken(s);
        Log.e("Firebase", "FirebaseInstanceIDService : " + s);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage);
        }
    }


    private void sendNotification(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("contentTitle");
        String message = remoteMessage.getData().get("contentText");

        Log.d("title", " " + title);
        Log.d("message", " " + message);

        Intent intent;
        intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE|PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire(3000);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        SharedPreferences pushCountGet = getSharedPreferences("pushCount", Activity.MODE_PRIVATE);
        if (pushCountGet.getString("setMessageCount", null) != null) {
            setMessageCount = Integer.parseInt(Objects.requireNonNull(pushCountGet.getString("setMessageCount", null)));
        }
        setMessageCount++;


        ShortcutBadger.applyCount(this, setMessageCount);

        SharedPreferences pushCount = getSharedPreferences("pushCount", Activity.MODE_PRIVATE);
        //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
        SharedPreferences.Editor pushCountValue = pushCount.edit();
        pushCountValue.putString("setMessageCount", Integer.toString(setMessageCount));
        pushCountValue.apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("push", ">Oreo");
            String channel = "9999";
            String channel_nm = "동기강화 앱 push 알림";


            NotificationManager notificationChannel = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm,
                    NotificationManager.IMPORTANCE_HIGH);
            channelMessage.setDescription("동기강화 앱 개인 및 그룹 push");
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(true);
            channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channel)
                            .setSmallIcon(R.drawable.login_brain_icon)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setChannelId(channel)
                            .setNumber(setMessageCount)
                            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setFullScreenIntent(pendingIntent, true);

            assert notificationChannel != null;

            notificationChannel.createNotificationChannel(channelMessage);

            notificationChannel.notify(9999, notificationBuilder.build());

        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "")
                            .setSmallIcon(R.drawable.login_brain_icon)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setNumber(setMessageCount)
                            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setFullScreenIntent(pendingIntent, true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.notify(9999, notificationBuilder.build());

        }
    }
}