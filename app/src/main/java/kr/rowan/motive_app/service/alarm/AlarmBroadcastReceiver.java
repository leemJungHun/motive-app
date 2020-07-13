package kr.rowan.motive_app.service.alarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Objects;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.LoginActivity;
import kr.rowan.motive_app.activity.PopupActivity;
import me.leolin.shortcutbadger.ShortcutBadger;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    int setMessageCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmBroadcastReceiver", "onReceive");

        Intent gointent;
        gointent = new Intent(context, LoginActivity.class);
        gointent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        gointent.putExtra("medalVideo","Y");

        SharedPreferences videoYN = context.getSharedPreferences("videoYN", Activity.MODE_PRIVATE);
        //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
        SharedPreferences.Editor medalVideoValue = videoYN.edit();
        medalVideoValue.putString("videoValue", "Y");
        medalVideoValue.apply();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE|PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire(3000);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, gointent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        SharedPreferences pushCountGet = context.getSharedPreferences("pushCount", Activity.MODE_PRIVATE);
        if(pushCountGet.getString("setMessageCount", null)!=null) {
            setMessageCount = Integer.parseInt(Objects.requireNonNull(pushCountGet.getString("setMessageCount", null)));
        }
        setMessageCount++;

        ShortcutBadger.applyCount(context,setMessageCount);

        SharedPreferences pushCount = context.getSharedPreferences("pushCount", Activity.MODE_PRIVATE);
        //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
        SharedPreferences.Editor pushCountValue = pushCount.edit();
        pushCountValue.putString("setMessageCount", Integer.toString(setMessageCount));
        pushCountValue.apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channel = "";
            String channel_nm = "동기강화 앱 push 알림";

            NotificationManager notificationChannel = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm,
                    NotificationManager.IMPORTANCE_HIGH);
            channelMessage.setDescription("동기강화 앱 개인 및 그룹 push");
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(true);
            channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channel)
                            .setSmallIcon(R.drawable.login_brain_icon)
                            .setContentTitle("동기강화 앱!")  //알람 제목
                            .setContentText("응원영상을 보시고 메달을 체크하세요!") //알람 내용
                            .setChannelId(channel)
                            .setAutoCancel(true)
                            .setOngoing(true)
                            .setNumber(setMessageCount)
                            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentIntent(pendingIntent);

            assert notificationChannel != null;
            notificationChannel.createNotificationChannel(channelMessage);

            notificationChannel.notify(9999, notificationBuilder.build());

        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, "")
                            .setSmallIcon(R.drawable.login_brain_icon)
                            .setContentTitle("동기강화")  //알람 제목
                            .setContentText("응원영상을 보시고 메달을 체크하세요!") //알람 내용
                            .setAutoCancel(true)
                            .setOngoing(true)
                            .setNumber(setMessageCount)
                            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentIntent(pendingIntent);

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

        Intent popupIntent = new Intent(context, PopupActivity.class);
        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        popupIntent.putExtra("data", "응원영상을 보시고 메달을 체크하세요!");
        context.startActivity(popupIntent);
    }
}