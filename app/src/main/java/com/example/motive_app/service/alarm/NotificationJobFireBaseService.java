package com.example.motive_app.service.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.motive_app.network.dto.GetUserAlarmRequest;
import com.example.motive_app.network.HttpRequestService;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.gson.JsonObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationJobFireBaseService extends JobService {
    Retrofit retrofit;
    private HttpRequestService httpRequestService;
    Long alarmTimeMillis = null;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d("NotificationJobService", "onStartJob");

        retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String loginId = auto.getString("saveId", null);
        assert loginId != null;
        if(!loginId.equals("null")) {
            Log.d("onStartJob loginId", " " + loginId);
            GetUserAlarmRequest getUserAlarmRequest = new GetUserAlarmRequest();
            getUserAlarmRequest.setId(loginId);
            httpRequestService.getUserAlarmRequest(getUserAlarmRequest).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        String newAlarmTime = response.body().get("alarmTime").toString().replace("\"", "");
                        if(!newAlarmTime.equals("null") && !newAlarmTime.equals("NULL")) {
                            Log.d("newAlarmTime", newAlarmTime + " ");
                            SharedPreferences getAlarmTime = getSharedPreferences("getAlarmTime", Activity.MODE_PRIVATE);
                            //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
                            SharedPreferences.Editor saveAlarmTime = getAlarmTime.edit();
                            saveAlarmTime.putString("alarmTime", newAlarmTime);
                            saveAlarmTime.apply();
                        }
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
        SharedPreferences getAlarmTime = getSharedPreferences("getAlarmTime", Activity.MODE_PRIVATE);
        String alarmTime = getAlarmTime.getString("alarmTime", null);

        if(alarmTime != null && !alarmTime.equals("null")) {
            Log.d("onStartJob alarmTime", alarmTime);
            alarmTimeMillis = getDate(alarmTime);
        }


        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        /**
         * Intent 플래그
         *    FLAG_ONE_SHOT : 한번만 사용하고 다음에 이 PendingIntent가 불려지면 Fail을 함
         *    FLAG_NO_CREATE : PendingIntent를 생성하지 않음. PendingIntent가 실행중인것을 체크를 함
         *    FLAG_CANCEL_CURRENT : 실행중인 PendingIntent가 있다면 기존 인텐트를 취소하고 새로만듬
         *    FLAG_UPDATE_CURRENT : 실행중인 PendingIntent가 있다면  Extra Data만 교체함
         */


        if(alarmTimeMillis!=null) {
            Calendar nowCal = Calendar.getInstance();
            Log.d("nowDayOfWeek",nowCal.get(Calendar.DAY_OF_WEEK)+" ");
            Log.d("nowHour",nowCal.get(Calendar.HOUR_OF_DAY)+" ");
            Log.d("nowMinute",nowCal.get(Calendar.MINUTE)+" ");
            Log.d("alarmTimeMillis", " "+alarmTimeMillis);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                assert manager != null;
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                assert manager != null;
                manager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
            } else {
                assert manager != null;
                manager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
            }
        }else{
            Log.d("alarmTimeMillis", "null");
        }

        /**
         * AlarmType
         *    RTC_WAKEUP : 대기모드에서도 알람이 작동함을 의미함
         *    RTC : 대기모드에선 알람을 작동안함
         */

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }

    private Long getDate(String alarmTime) {
        Long timeMillis=null;
        int getDayOfWeek = Integer.parseInt(alarmTime.substring(0, 2));
        Log.d("getDayOfWeek", getDayOfWeek + " ");
        int getHour = Integer.parseInt(alarmTime.substring(3, 5));
        Log.d("getHour", getHour + " ");
        int getMinute = Integer.parseInt(alarmTime.substring(6));
        Log.d("getMinute", getMinute + " ");

        Calendar nowCal = Calendar.getInstance();
        Calendar alarmCal = Calendar.getInstance();

        alarmCal.set(Calendar.DAY_OF_WEEK, getDayOfWeek);
        alarmCal.set(Calendar.HOUR_OF_DAY, getHour);
        alarmCal.set(Calendar.MINUTE, getMinute);

        if(nowCal.getTimeInMillis()<alarmCal.getTimeInMillis()){
               timeMillis = alarmCal.getTimeInMillis();
        }

        return timeMillis;
    }
}