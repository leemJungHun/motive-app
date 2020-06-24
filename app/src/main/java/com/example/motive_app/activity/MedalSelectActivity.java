package com.example.motive_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.databinding.ActivityMedalSelectBinding;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.dto.PutMedalSelectResultRequest;
import com.example.motive_app.network.vo.UserInfoVO;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedalSelectActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMedalSelectBinding binding;
    Animation goldAnim;
    Animation silverAnim;
    Animation downScale;
    UserInfoVO vo;
    String medalVideo;
    String videoIdx;
    int goldMedalCnt = 0;
    int silverMedalCnt = 0;
    AnimationDrawable effectAnim;
    AnimationDrawable effectAnim2;
    private HttpRequestService httpRequestService;
    private int nowSort = 0;
    String groupCode;
    private String subTitle;


    private final String TAG = MedalSelectActivity.class.getSimpleName();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    public MedalSelectActivity() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_medal_select);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            vo = (UserInfoVO) intent.getSerializableExtra("userInfoVO");


            medalVideo = intent.getExtras().getString("medalVideo");
            videoIdx = intent.getExtras().getString("videoIdx");
            subTitle = intent.getExtras().getString("subTitle");

            if (vo != null) {
                Log.d("getName", vo.getName());
                Log.d("getId", vo.getId());
                Log.d("getEmail", vo.getEmail());
                Log.d("getOrganizationCode", vo.getOrganizationCode());
                Log.d("getBirth", vo.getBirth());
                Log.d("getPhone", vo.getPhone());
                Log.d("getGroupCode", vo.getGroupCode());
                groupCode = vo.getGroupCode();
                if (vo.getProfileImageUrl() != null) {
                    Log.d("getImageUrl", vo.getProfileImageUrl());
                }
            }

        }


        effectAnim = (AnimationDrawable) binding.goldEffect.getBackground();

        effectAnim2 = (AnimationDrawable) binding.silverEffect.getBackground();


        goldAnim = AnimationUtils.loadAnimation
                (getApplicationContext(), R.anim.scale_anim);

        silverAnim = AnimationUtils.loadAnimation
                (getApplicationContext(), R.anim.scale_anim);

        downScale = AnimationUtils.loadAnimation
                (getApplicationContext(), R.anim.down_scale_anim);


        binding.goldMedal.setOnClickListener(this);
        binding.silverMedal.setOnClickListener(this);
        binding.selectOkBtn.setOnClickListener(this);

        try {
            nowSort = currentWeekCount(vo.getStartDate());
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(nowSort==1){
            goldMedalCnt = 1;
            binding.selectOkBtn.setEnabled(true);
            binding.selectOkBtn.performClick();
        }

    }

    private int currentWeekCount(String start) throws Exception {
        Calendar calendar = Calendar.getInstance();
        String[] starts = start.split("-");
        calendar.set(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]) - 1, Integer.parseInt(starts[2]));
        int startDayNum = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e(TAG, "startDayNum = " + startDayNum);

        Date endDate = new Date();

        String today = simpleDateFormat.format(endDate);
        String[] todays = today.split("-");
        calendar.set(Integer.parseInt(todays[0]), Integer.parseInt(todays[1]) - 1, Integer.parseInt(todays[2]));
        int todayNum = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e(TAG, "todayNum = " + todayNum);

        Date startDate = simpleDateFormat.parse(start);
        assert startDate != null;
        long diff = endDate.getTime() - startDate.getTime();
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
        diffDays = ((diffDays - todayNum + startDayNum) / 7) + 1;
        Log.e(TAG, "diff days = " + diffDays);

        return diffDays;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.goldMedal) {
            binding.selectOkBtn.setEnabled(true);
            binding.selectOkBtn.setBackgroundResource(R.drawable.btn_back_blue);
            binding.goldMedal.startAnimation(goldAnim);
            binding.silverMedal.startAnimation(downScale);
            binding.goldEffect.setVisibility(View.VISIBLE);
            binding.silverEffect.setVisibility(View.GONE);
            goldMedalCnt = 1;
            silverMedalCnt = 0;
            effectAnim.start();
            effectAnim2.stop();
        } else if (v == binding.silverMedal) {
            binding.selectOkBtn.setEnabled(true);
            binding.selectOkBtn.setBackgroundResource(R.drawable.btn_back_blue);
            binding.silverMedal.startAnimation(silverAnim);
            binding.goldMedal.startAnimation(downScale);
            binding.goldEffect.setVisibility(View.GONE);
            binding.silverEffect.setVisibility(View.VISIBLE);
            goldMedalCnt = 0;
            silverMedalCnt = 1;
            effectAnim2.start();
            effectAnim.stop();
        } else if (v == binding.selectOkBtn) {
            PutMedalSelectResultRequest putMedalSelectResultRequest = new PutMedalSelectResultRequest();
            putMedalSelectResultRequest.setUserId(vo.getId());
            putMedalSelectResultRequest.setVideoIdx(videoIdx);
            putMedalSelectResultRequest.setWeekSort(Integer.toString(nowSort));
            putMedalSelectResultRequest.setGoldMedalCnt(Integer.toString(goldMedalCnt));
            putMedalSelectResultRequest.setSilverMedalCnt(Integer.toString(silverMedalCnt));
            putMedalSelectResultRequest.setGroupCode(groupCode);
            Log.d("vo.getId",vo.getId()+" ");
            Log.d("videoIdx",videoIdx+" ");
            Log.d("nowSort",nowSort+" ");
            Log.d("goldMedalCnt",goldMedalCnt+" ");
            Log.d("silverMedalCnt",silverMedalCnt+" ");
            Log.d("groupCode",groupCode);
            httpRequestService.putMedalSelectResultRequest(putMedalSelectResultRequest).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.body() != null) {
                        String result = response.body().get("result").toString().replace("\"", "");
                        Log.d("putMedal-result", " " + result);
                        switch (result) {
                            case "ok":
                                SharedPreferences videoYN = getSharedPreferences("videoYN", Activity.MODE_PRIVATE);
                                //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
                                SharedPreferences.Editor medalVideoValue = videoYN.edit();
                                medalVideoValue.putString("videoValue", "N");
                                medalVideoValue.apply();
                                Intent intent = new Intent(MedalSelectActivity.this, CheeringMessageActivity.class);
                                intent.putExtra("medalVideo", "N");
                                intent.putExtra("userInfoVO", vo);
                                intent.putExtra("whatMedal",goldMedalCnt);
                                intent.putExtra("subTitle",subTitle);
                                startActivity(intent);
                                finish();
                                break;
                            case "error":
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                }
            });

        }
    }

}
