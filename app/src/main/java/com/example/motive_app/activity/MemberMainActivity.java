package com.example.motive_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.motive_app.R;
import com.example.motive_app.activity.registration.ChangePassActivity;
import com.example.motive_app.databinding.ActivityMemberMainBinding;
import com.example.motive_app.fragment.member.MyMedalFragment;
import com.example.motive_app.fragment.member.PlayVideoFragment;
import com.example.motive_app.fragment.member.ScheduleFragment;
import com.example.motive_app.network.DTO.RegistrationTokenRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.VO.UserInfoVO;
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

public class MemberMainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ActivityMemberMainBinding binding;
    int check;
    int preCheck=0;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;
    UserInfoVO vo;
    String type;
    String textColorBlack = "#666666";
    String textColorBlue = "#2699fb";
    Fragment nowFragmnet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_member_main);

        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if(intent.getExtras()!=null) {
            vo = (UserInfoVO) intent.getSerializableExtra("userInfoVO");

            type = intent.getExtras().getString("type");
            if(vo!=null) {
                Log.d("getName", vo.getName());
                Log.d("getId", vo.getId());
                Log.d("getEmail", vo.getEmail());
                Log.d("getOrganizationCode", vo.getOrganizationCode());
                Log.d("getBirth", vo.getBirth());
                Log.d("getPhone", vo.getPhone());
                if(vo.getProfileImageUrl()!=null) {
                    Log.d("getImageUrl", vo.getProfileImageUrl());
                }
            }
        }

        //토큰 등록
        getToken();
        fragmentManager = getSupportFragmentManager();
        setStartFragment(new MyMedalFragment(vo));

    }


    public void setFragment(View view) {
        switch (view.getId()) {
            case R.id.star_text:
            case R.id.star_icon:
                check = 0;
                setIcons(R.drawable.motive_icon_menu_star_on, R.drawable.motive_icon_menu_play_off, R.drawable.motive_icon_menu_calendar_off);
                setTextColor(textColorBlue,textColorBlack,textColorBlack);
                setStartFragment(new MyMedalFragment(vo));
                break;
            case R.id.play_icon:
            case R.id.play_text:
                check = 2;
                setIcons(R.drawable.motive_icon_menu_star_off, R.drawable.motive_icon_menu_play_on, R.drawable.motive_icon_menu_calendar_off);
                setTextColor(textColorBlack,textColorBlue,textColorBlack);
                setStartFragment(new PlayVideoFragment(vo));
                break;
            case R.id.calender_icon:
            case R.id.calender_text:
                check = 3;
                setIcons(R.drawable.motive_icon_menu_star_off, R.drawable.motive_icon_menu_play_off, R.drawable.motive_icon_menu_calendar_on);
                setTextColor(textColorBlack,textColorBlack,textColorBlue);
                setStartFragment(new ScheduleFragment(vo));
                break;
        }

    }

    public void setIcons(int... icons){
        binding.starIcon.setImageResource(icons[0]);
        binding.playIcon.setImageResource(icons[1]);
        binding.calenderIcon.setImageResource(icons[2]);
    }

    public void setTextColor(String... colorValue){
        binding.starText.setTextColor(Color.parseColor(colorValue[0]));
        binding.playText.setTextColor(Color.parseColor(colorValue[1]));
        binding.calenderText.setTextColor(Color.parseColor(colorValue[2]));
    }

    public void setStartFragment(Fragment fragment){
        nowFragmnet=fragment;
        fragmentTransaction = fragmentManager.beginTransaction();

        if(preCheck<check) {
            fragmentTransaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(preCheck>check){
            fragmentTransaction.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right);
        }
        fragmentTransaction.replace(R.id.main_container, fragment).commitAllowingStateLoss();

        preCheck = check;
    }

    public void setFragemntCheck(int check){
        this.check = check;
    }

    public void changePassOpen(){
        Intent intent;
        intent = new Intent(getApplicationContext(), ChangePassActivity.class);

        intent.putExtra("userId",vo.getId());
        intent.putExtra("type", type);

        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void playVideo(String fileUrl){
        Intent intent;
        intent = new Intent(getApplicationContext(), PlayVideoActivity.class);

        intent.putExtra("userId",vo.getId());
        intent.putExtra("fileUrl", fileUrl);

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
            setIcons(R.drawable.motive_icon_menu_star_on, R.drawable.motive_icon_menu_play_off, R.drawable.motive_icon_menu_calendar_off);
            setTextColor(textColorBlue,textColorBlack,textColorBlack);
            setStartFragment(new MyMedalFragment(vo));
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