package com.example.motive_app.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.databinding.ActivityMedalSelectBinding;
import com.example.motive_app.network.DTO.GetUserScheduleRequest;
import com.example.motive_app.network.DTO.PutMedalSelectResultRequest;
import com.example.motive_app.network.DTO.UserPhoneRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.VO.GroupScheduleVO;
import com.example.motive_app.network.VO.MemberInfoVO;
import com.example.motive_app.network.VO.UserInfoVO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Calendar;

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
    private Calendar cal = Calendar.getInstance();
    private Calendar preCal = Calendar.getInstance();
    private Calendar nextCal = Calendar.getInstance();
    private String preAttendDate;
    int goldMedalCnt = 0;
    int silverMedalCnt = 0;
    AnimationDrawable effectAnim;
    AnimationDrawable effectAnim2;
    private MemberInfoVO memberInfoVO;
    private HttpRequestService httpRequestService;
    private int weekSortCnt = 1;
    private int nowSort = 1;
    String groupCode;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_medal_select);

        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            vo = (UserInfoVO) intent.getSerializableExtra("userInfoVO");


            medalVideo = intent.getExtras().getString("medalVideo");
            videoIdx = intent.getExtras().getString("videoIdx");

            if (vo != null) {
                Log.d("getName", vo.getName());
                Log.d("getId", vo.getId());
                Log.d("getEmail", vo.getEmail());
                Log.d("getOrganizationCode", vo.getOrganizationCode());
                Log.d("getBirth", vo.getBirth());
                Log.d("getPhone", vo.getPhone());
                if (vo.getProfileImageUrl() != null) {
                    Log.d("getImageUrl", vo.getProfileImageUrl());
                }
            }

        }

        getWeekSort();

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
    }

    private void getWeekSort() {
        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        UserPhoneRequest userPhoneRequest = new UserPhoneRequest();
        userPhoneRequest.setUserPhone(vo.getPhone());

        httpRequestService.userPhoneRequest(userPhoneRequest).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    Gson gson = new Gson();
                    memberInfoVO = gson.fromJson(response.body().get("memberInfoVO").toString(), MemberInfoVO.class);
                    Log.d("getGroupCode", " " + memberInfoVO.getGroupCode());
                    if (memberInfoVO.getGroupCode() != null) {
                        GetUserScheduleRequest getUserScheduleRequest = new GetUserScheduleRequest();
                        getUserScheduleRequest.setUserId(vo.getId());
                        getUserScheduleRequest.setGroupCode(memberInfoVO.getGroupCode());
                        groupCode = memberInfoVO.getGroupCode();
                        httpRequestService.getUserScheduleRequest(getUserScheduleRequest).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.body() != null) {
                                    Gson gson = new Gson();
                                    JsonObject jsonObject = response.body().getAsJsonObject("result");
                                    JsonArray jsonArray = jsonObject.getAsJsonArray("schedule");

                                    for (int index = jsonArray.size() - 1; 0 <= index; index--) {
                                        GroupScheduleVO groupScheduleVO = gson.fromJson(jsonArray.get(index).toString(), GroupScheduleVO.class);
                                        if (groupScheduleVO.getBreakAway().equals("n") || groupScheduleVO.getBreakAway().equals("N")) {
                                            Log.d("groupVO.getWeekSort", " " + groupScheduleVO.getWeekSort());
                                            if (weekSortCnt == Integer.parseInt(groupScheduleVO.getWeekSort())) {
                                                String attendDate = groupScheduleVO.getAttendDate().substring(0, 10);
                                                Log.d("weekSortCnt", " " + weekSortCnt);
                                                if (weekSortCnt == 1) {
                                                    preAttendDate = attendDate;
                                                } else {
                                                    String[] attendDates = attendDate.split("-");
                                                    String[] preAttendDates = preAttendDate.split("-");
                                                    preCal.set(Calendar.YEAR, Integer.parseInt(preAttendDates[0]));
                                                    preCal.set(Calendar.MONTH, Integer.parseInt(preAttendDates[1]) - 1);
                                                    preCal.set(Calendar.DATE, Integer.parseInt(preAttendDates[2]));
                                                    nextCal.set(Calendar.YEAR, Integer.parseInt(attendDates[0]));
                                                    nextCal.set(Calendar.MONTH, Integer.parseInt(attendDates[1]) - 1);
                                                    nextCal.set(Calendar.DATE, Integer.parseInt(attendDates[2]));
                                                    Log.d("preCal.getTimeInMillis", " " + preCal.getTimeInMillis());
                                                    Log.d("cal.getTimeInMillis", " " + cal.getTimeInMillis());
                                                    Log.d("nextCal.getTimeInMillis", " " + nextCal.getTimeInMillis());
                                                    if (preCal.getTimeInMillis() < cal.getTimeInMillis() && cal.getTimeInMillis() < nextCal.getTimeInMillis()) {
                                                        nowSort = weekSortCnt;
                                                    }
                                                    preAttendDate = attendDate;
                                                }
                                                weekSortCnt++;
                                            }
                                        }
                                    }

                                    Log.d("nowSort", " " + nowSort);

                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
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
            Log.d("vo.getId",vo.getId()+" ");
            Log.d("videoIdx",videoIdx+" ");
            Log.d("nowSort",nowSort+" ");
            Log.d("goldMedalCnt",goldMedalCnt+" ");
            Log.d("silverMedalCnt",silverMedalCnt+" ");
            Log.d("groupCode",groupCode);
            httpRequestService.putMedalSelectResultRequest(putMedalSelectResultRequest).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        String result = response.body().get("result").toString().replace("\"", "");
                        Log.d("putMedal-result", " " + result);
                        switch (result) {
                            case "ok":
                                Intent intent;
                                break;
                            case "error":
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });

        }
    }

}
