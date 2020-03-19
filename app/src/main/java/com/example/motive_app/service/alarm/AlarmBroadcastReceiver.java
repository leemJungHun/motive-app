package com.example.motive_app.service.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.motive_app.R;
import com.example.motive_app.activity.LoginActivity;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private final static int NOTICATION_ID = 222;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmBroadcastReceiver", "onReceive");

        Intent gointent;
        gointent = new Intent(context, LoginActivity.class);
        gointent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, gointent,
                PendingIntent.FLAG_ONE_SHOT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channel = "";
            String channel_nm = "동기강화 앱 push 알림";

            NotificationManager notificationChannel = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm,
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription("동기강화 앱 개인 및 그룹 push");
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(false);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
            assert notificationChannel != null;
            notificationChannel.createNotificationChannel(channelMessage);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channel)
                            .setSmallIcon(R.drawable.login_brain_icon)
                            .setContentTitle("동기강화 앱!")  //알람 제목
                            .setContentText("응원영상을 보시고 메달을 체크하세요!") //알람 내용
                            .setChannelId(channel)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setOngoing(true)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.notify(9999, notificationBuilder.build());

        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, "")
                            .setSmallIcon(R.drawable.login_brain_icon)
                            .setContentTitle("동기강화 앱")  //알람 제목
                            .setContentText("응원영상을 보시고 메달을 체크하세요!") //알람 내용
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setOngoing(true)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.notify(9999, notificationBuilder.build());

        }


        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.login_brain_icon) //알람 아이콘
                .setContentTitle("동기강화 앱!")  //알람 제목
                .setContentText("응원영상을 보시고 메달을 체크하세요!") //알람 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //알람 중요도

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTICATION_ID, builder.build()); //알람 생성*/
    }
}