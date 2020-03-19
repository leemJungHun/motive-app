package com.example.motive_app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.motive_app.R;
import com.example.motive_app.activity.registration.ChangePassActivity;
import com.example.motive_app.databinding.ActivityFamilyMainBinding;
import com.example.motive_app.fragment.family.FamilyInfoFragment;
import com.example.motive_app.fragment.family.MyFamilyFragment;
import com.example.motive_app.fragment.family.MyFamilyScheduleFragment;
import com.example.motive_app.fragment.family.VideoUploadFragment;
import com.example.motive_app.network.DTO.RegistrationTokenRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.VO.FamilyInfoVO;
import com.example.motive_app.service.alarm.JobSchedulerStart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FamilyMainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ActivityFamilyMainBinding binding;
    int check;
    int preCheck=0;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;
    FamilyInfoVO vo;
    String textColorBlack = "#666666";
    String textColorBlue = "#2699fb";
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_family_main);

        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if(intent.getExtras()!=null) {
            vo = (FamilyInfoVO) intent.getSerializableExtra("familyInfoVO");
            if(vo!=null) {
                Log.d("getName", vo.getName());
                Log.d("getId", vo.getId());
                Log.d("getEmail", vo.getEmail());
                Log.d("getPhone", vo.getPhone());
            }
            type = intent.getExtras().getString("type");
        }

        //토큰 등록
        getToken();

        fragmentManager = getSupportFragmentManager();
        setStartFragment(new MyFamilyFragment(vo));

    }


    public void setFragment(View view) {
        switch (view.getId()) {
            case R.id.star_text:
            case R.id.star_icon:
                check = 0;
                setIcons(R.drawable.motive_icon_menu_heart_on, R.drawable.motive_icon_menu_camera_off, R.drawable.motive_icon_menu_calendar_off, R.drawable.motive_icon_menu_teacher_off);
                setTextColor(textColorBlue,textColorBlack,textColorBlack,textColorBlack);
                setStartFragment(new MyFamilyFragment(vo));
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                }
                break;
            case R.id.play_icon:
            case R.id.play_text:
                check = 2;
                setIcons(R.drawable.motive_icon_menu_heart_off, R.drawable.motive_icon_menu_camera_on, R.drawable.motive_icon_menu_calendar_off, R.drawable.motive_icon_menu_teacher_off);
                setTextColor(textColorBlack,textColorBlue,textColorBlack,textColorBlack);
                setStartFragment(new VideoUploadFragment(vo,this));
                break;
            case R.id.calender_icon:
            case R.id.calender_text:
                check = 3;
                setIcons(R.drawable.motive_icon_menu_heart_off, R.drawable.motive_icon_menu_camera_off, R.drawable.motive_icon_menu_calendar_on, R.drawable.motive_icon_menu_teacher_off);
                setTextColor(textColorBlack,textColorBlack,textColorBlue,textColorBlack);
                setStartFragment(new MyFamilyScheduleFragment(vo));
                break;
            case R.id.teacher_icon:
            case R.id.teacher_text:
                check = 4;
                setIcons(R.drawable.motive_icon_menu_heart_off, R.drawable.motive_icon_menu_camera_off, R.drawable.motive_icon_menu_calendar_off, R.drawable.motive_icon_menu_teacher_on);
                setTextColor(textColorBlack,textColorBlack,textColorBlack,textColorBlue);
                setStartFragment(new FamilyInfoFragment(vo));
                break;
        }

    }

    public void setIcons(int... icons){
        binding.starIcon.setImageResource(icons[0]);
        binding.playIcon.setImageResource(icons[1]);
        binding.calenderIcon.setImageResource(icons[2]);
        binding.teacherIcon.setImageResource(icons[3]);
    }

    public void setTextColor(String... colorValue){
        binding.starText.setTextColor(Color.parseColor(colorValue[0]));
        binding.playText.setTextColor(Color.parseColor(colorValue[1]));
        binding.calenderText.setTextColor(Color.parseColor(colorValue[2]));
        binding.teacherText.setTextColor(Color.parseColor(colorValue[3]));
    }

    public void setStartFragment(Fragment fragment){
        fragmentTransaction = fragmentManager.beginTransaction();

        if(preCheck<check) {
            fragmentTransaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(preCheck>check){
            fragmentTransaction.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right);
        }
        fragmentTransaction.replace(R.id.main_container, fragment).commitAllowingStateLoss();

        preCheck = check;
    }

    public void moveScheduleFregment(String myFamilyId){
        check = 3;
        setIcons(R.drawable.motive_icon_menu_heart_off, R.drawable.motive_icon_menu_camera_off, R.drawable.motive_icon_menu_calendar_on, R.drawable.motive_icon_menu_teacher_off);
        setTextColor(textColorBlack,textColorBlack,textColorBlue,textColorBlack);
        setStartFragment(new MyFamilyScheduleFragment(vo, myFamilyId));
    }

    public void videoSelectActivty(String filePath, String playTime){
        Intent intent;
        intent = new Intent(getApplicationContext(), VideoSendSelectActivity.class);

        intent.putExtra("familyInfoVO", vo);
        intent.putExtra("type","family");
        intent.putExtra("filePath",filePath);
        intent.putExtra("playTime",playTime);
        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void changePassOpen(){
        Intent intent;
        intent = new Intent(getApplicationContext(), ChangePassActivity.class);

        intent.putExtra("userId",vo.getId());
        intent.putExtra("type",type);

        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void logOut(String toastText){
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = auto.edit();
        //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
        editor.clear();
        editor.apply();

        JobSchedulerStart.stop(this);

        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();

        Intent intent;
        intent = new Intent(getApplicationContext(), LoginActivity.class);

        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

        finish();
    }


    @Override
    public void onBackPressed() {

        if(check==0){
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;
            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
            {
                super.onBackPressed();
            }
            else
            {
                backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기를 누르시면 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            }
        }else{
            check=0;
            setIcons(R.drawable.motive_icon_menu_star_on, R.drawable.motive_icon_menu_play_off, R.drawable.motive_icon_menu_teacher_off, R.drawable.motive_icon_menu_calendar_off);
            setTextColor(textColorBlue,textColorBlack,textColorBlack,textColorBlack);
            setStartFragment(new MyFamilyFragment(vo));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void getToken() {
        //토큰값을 받아옵니다.
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = Objects.requireNonNull(task.getResult()).getToken(); // 사용자가 입력한 저장할 데이터
                        Log.d("token", " " + token);


                        //기기 토큰 등록
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(HttpRequestService.URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        HttpRequestService httpRequestService = retrofit.create(HttpRequestService.class);

                        RegistrationTokenRequest request = new RegistrationTokenRequest();
                        request.setToken(token);
                        request.setUserId(vo.getId());
                        httpRequestService.registrationTokenRequest(request).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.body() != null) {
                                    Log.d("result", " " + response.body().get("result").toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
                    }
                });
    }

}