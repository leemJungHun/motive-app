package com.example.motive_app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
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
import com.example.motive_app.fragment.member.MyInfoFragment;
import com.example.motive_app.fragment.member.MyMedalFragment;
import com.example.motive_app.fragment.member.PlayVideoFragment;
import com.example.motive_app.fragment.member.ScheduleFragment;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.dto.GroupCodeRequest;
import com.example.motive_app.network.dto.LogoutRequest;
import com.example.motive_app.network.dto.RegistrationTokenRequest;
import com.example.motive_app.network.vo.GroupInfoVO;
import com.example.motive_app.network.vo.UserInfoVO;
import com.example.motive_app.service.alarm.JobSchedulerStart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MemberMainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    public ActivityMemberMainBinding binding;
    int check;
    int preCheck = 0;
    private static final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    String medalVideo = "N";
    UserInfoVO vo;
    String type;
    public Fragment nowFragment;
    Bundle args;

    public SurfaceHolder videoHolder;
    public MediaPlayer videoPlayer;
    public MediaController mediaController;
    boolean isFull = false;
    public String fileUrl;
    public String videoIdx;
    private String subTitle;
    private HttpRequestService httpRequestService;
    private String token;
    private Context context = this;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_member_main);
        videoHolder = binding.cheerUpVideoView.getHolder();
        binding.cheerUpVideoView.setOnClickListener(null);
        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            vo = (UserInfoVO) intent.getSerializableExtra("userInfoVO");
            medalVideo = intent.getExtras().getString("medalVideo");
            if (medalVideo == null) {
                medalVideo = "N";
            }
            type = intent.getExtras().getString("type");
            if (vo != null) {
                Log.d("getName", vo.getName());
                Log.d("getId", vo.getId());
                Log.d("getEmail", vo.getEmail());
                Log.d("getOrganizationCode", vo.getOrganizationCode());
                Log.d("getBirth", vo.getBirth());
                Log.d("getPhone", vo.getPhone());
                if(vo.getGroupCode()!=null) {
                    Log.d("getGroupCode", vo.getGroupCode());
                }
                Log.d("getRegistrationDate", vo.getRegistrationDate() + "");
                Log.d("getStartDate", vo.getStartDate() + "");
                if (vo.getProfileImageUrl() != null) {
                    Log.d("getImageUrl", vo.getProfileImageUrl());
                }
            }
        }

        binding.mainContainer.setScrollContainer(false);
        binding.leftIconImageView.setOnClickListener(this::onIconClick);
        binding.rightIconImageView.setOnClickListener(this::onIconClick);

        //토큰 등록
        getToken();
        binding.bottomNav.setItemTextColor(getResources().getColorStateList(R.drawable.bottom_navigation_colors));
        binding.bottomNav.setItemIconTintList(getResources().getColorStateList(R.drawable.bottom_navigation_colors));
        fragmentManager = getSupportFragmentManager();
        binding.bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //args = new Bundle();
        //args.putSerializable("userInfoVO", vo);
        if (medalVideo.equals("Y")) {
            binding.bottomNav.setSelectedItemId(R.id.myVideoInfo);
            //nowFragment = new PlayVideoFragment();
            //args.putString("medalVideo", medalVideo);
        } else {
            binding.bottomNav.setSelectedItemId(R.id.myMedalInfo);
            //nowFragment = new MyMedalFragment();
            binding.rightIconImageView.setVisibility(View.VISIBLE);
            binding.rightIconImageView.setImageResource(R.drawable.motive_icon_settings);
        }
        //nowFragment.setArguments(args);
        //setStartFragment();
    }

    public void onIconClick(View view) {
        switch (view.getId()) {
            case R.id.leftIconImageView:
                check = 0;
                binding.rightIconImageView.setVisibility(View.VISIBLE);
                binding.leftIconImageView.setVisibility(View.GONE);
                binding.currentFragmentNameTextView.setText(getString(R.string.my_medal));
                nowFragment = new MyMedalFragment();
                nowFragment.setArguments(args);
                setStartFragment();
                break;
            case R.id.rightIconImageView:
                check = 3;
                binding.currentFragmentNameTextView.setText(getString(R.string.my_info));
                binding.rightIconImageView.setVisibility(View.GONE);
                binding.leftIconImageView.setVisibility(View.VISIBLE);
                nowFragment = new MyInfoFragment();
                nowFragment.setArguments(args);
                setStartFragment();
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        args = new Bundle();
        args.putSerializable("userInfoVO", vo);
        switch (item.getItemId()) {
            case R.id.myMedalInfo:
                if (!(nowFragment instanceof MyMedalFragment)) {
                    item.setIcon(R.drawable.motive_icon_menu_star_on);
                    check = 0;
                    nowFragment = new MyMedalFragment();
                    nowFragment.setArguments(args);
                    binding.currentFragmentNameTextView.setText(getString(R.string.my_medal));
                    binding.leftIconImageView.setVisibility(View.GONE);
                    binding.rightIconImageView.setVisibility(View.VISIBLE);
                    binding.rightIconImageView.setImageResource(R.drawable.motive_icon_settings);

                    setStartFragment();
                }
                return true;
            case R.id.myVideoInfo:
                if (!(nowFragment instanceof PlayVideoFragment)) {
                    check = 1;
                    nowFragment = new PlayVideoFragment();
                    args.putString("medalVideo", medalVideo);
                    nowFragment.setArguments(args);
                    binding.currentFragmentNameTextView.setText(getString(R.string.my_video_info));
                    binding.rightIconImageView.setVisibility(View.GONE);
                    binding.leftIconImageView.setVisibility(View.GONE);
                    setStartFragment();
                }
                return true;
            case R.id.myScheduleInfo:
                if (!(nowFragment instanceof ScheduleFragment)) {
                    check = 2;
                    nowFragment = new ScheduleFragment();
                    nowFragment.setArguments(args);
                    binding.currentFragmentNameTextView.setText(getString(R.string.my_schedule_info));
                    binding.rightIconImageView.setVisibility(View.GONE);
                    binding.leftIconImageView.setVisibility(View.GONE);
                    setStartFragment();
                }
                return true;
        }
        return false;
    };

    public void setStartFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (preCheck < check) {
            transaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_right, R.anim.push_out_left);
        } else if (preCheck > check) {
            transaction.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right, R.anim.pull_in_left, R.anim.push_out_right);
        } else if(check==0){
            transaction.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right, R.anim.pull_in_left, R.anim.push_out_right);
        }

        transaction
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .replace(R.id.main_container, nowFragment).commitAllowingStateLoss();
        preCheck = check;
    }


    public void changePassOpen() {
        Intent intent;
        intent = new Intent(getApplicationContext(), ChangePassActivity.class);

        intent.putExtra("userId", vo.getId());
        intent.putExtra("type", type);

        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

/*    public void playVideo(String fileUrl, String videoIdx) {
        Intent intent;
        intent = new Intent(getApplicationContext(), PlayVideoActivity.class);

        intent.putExtra("userInfoVO", vo);
        intent.putExtra("fileUrl", fileUrl);
        intent.putExtra("medalVideo", medalVideo);
        intent.putExtra("videoIdx", videoIdx);
        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }*/

    public void logOut(String toastText) {
        LogoutRequest request = new LogoutRequest();
        request.setId(vo.getId());
        request.setToken(token);
        httpRequestService.logOut(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                if(response.body()!=null){
                    Log.d("logout", " " + response.body().get("result").toString());
                    if(response.body().get("result").toString().replace("\"","").equals("ok")) {
                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = auto.edit();
                        //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                        editor.clear();
                        editor.apply();
                        SharedPreferences videoYN = getSharedPreferences("videoYN", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor medalVideoValue = videoYN.edit();
                        medalVideoValue.putString("videoValue", "N");
                        medalVideoValue.apply();

                        JobSchedulerStart.stop(context);
                        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                        Intent intent;
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (check == 0) {
            if (System.currentTimeMillis() - backPressedTime < FINISH_INTERVAL_TIME) {
                finish();
                return;
            }
            backPressedTime = System.currentTimeMillis();
            Snackbar.make(binding.bottomNav, "한번 더 뒤로가기를 누르시면 앱을 종료합니다.", Snackbar.LENGTH_SHORT).show();

        } else if (binding.cheerUpVideoView.getVisibility() == View.VISIBLE) {
            if (videoPlayer != null) {
                videoPlayer.stop();
                videoPlayer.release();
                videoPlayer = null;
            }
            setFullScreen(false, false);
            playVideoVisibility(false);

        } else {
            check = 0;
            //setIcons(R.drawable.motive_icon_menu_star_on, R.drawable.motive_icon_menu_play_off, R.drawable.motive_icon_menu_calendar_off);
            //setTextColor(textColorBlue, textColorBlack, textColorBlack);
            binding.bottomNav.setSelectedItemId(R.id.myMedalInfo);
        }
    }


    public void getToken() {
        //토큰값을 받아옵니다.
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    assert task.getResult() != null;
                    token = task.getResult().getToken(); // 사용자가 입력한 저장할 데이터
                    Log.d("token", " " + token);


                    //기기 토큰 등록
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(HttpRequestService.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    httpRequestService = retrofit.create(HttpRequestService.class);

                    RegistrationTokenRequest request = new RegistrationTokenRequest();
                    request.setToken(token);
                    request.setUserId(vo.getId());
                    httpRequestService.registrationTokenRequest(request).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            if (response.body() != null) {
                                Log.d("setToken", " " + response.body().get("result").toString());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                        }
                    });
                });
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setFullScreen(boolean full, boolean isSelectMedal) {
        isFull = full;

        ViewGroup.LayoutParams params = binding.cheerUpVideoView.getLayoutParams();

        if (full) {
            isFull = true;
            // 전체화면 만들 때 가로모드인 경우 폰 픽셀이 아닌 동영상 픽셀에 크기를 맞춘다.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            isFull = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            //int height = ViewGroup.LayoutParams.MATCH_PARENT;

            params.height = ViewGroup.LayoutParams.MATCH_PARENT;

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if(isSelectMedal) {
                GroupCodeRequest groupCodeRequest = new GroupCodeRequest();
                groupCodeRequest.setGroupCode(vo.getGroupCode());
                httpRequestService.getGroupInfoRequest(groupCodeRequest).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.body() != null) {
                            Gson gson = new Gson();
                            GroupInfoVO groupInfoVO = gson.fromJson(response.body().get("groupInfo").toString(), GroupInfoVO.class);
                            Log.d("groupInfo getName" , groupInfoVO.getName());
                            Log.d("groupInfo getStartDate" , groupInfoVO.getStartDate());
                            Log.d("groupInfo getWeekCount" , groupInfoVO.getWeekCount());
                            if(groupInfoVO.getSubtitle()!=null&&!groupInfoVO.getSubtitle().equals("")) {
                                Log.d("groupInfo getSubtitle", groupInfoVO.getSubtitle());
                                subTitle = groupInfoVO.getSubtitle();
                            }else{
                                subTitle = "";
                            }

                            Intent intent = new Intent(getApplicationContext(), MedalSelectActivity.class);

                            intent.putExtra("userInfoVO", vo);
                            intent.putExtra("fileUrl", fileUrl);
                            intent.putExtra("medalVideo",medalVideo);
                            intent.putExtra("videoIdx",videoIdx);
                            intent.putExtra("subTitle",subTitle);
                            startActivity(intent);

                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                    }
                });
            }
        }
    }

    public void playVideoVisibility(boolean visible) {
        binding.cheerUpVideoView.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.memberToolbar.setVisibility(visible ? View.GONE : View.VISIBLE);
        binding.bottomNav.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}