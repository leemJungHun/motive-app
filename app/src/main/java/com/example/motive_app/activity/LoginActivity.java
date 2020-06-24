package com.example.motive_app.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.activity.registration.LoginHelpActivity;
import com.example.motive_app.activity.registration.TypeChoiceActivity;
import com.example.motive_app.databinding.ActivityLoginBinding;
import com.example.motive_app.dialog.CustomDialog;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.dto.FamilyLoginRequest;
import com.example.motive_app.network.dto.LoginRequest;
import com.example.motive_app.network.vo.FamilyInfoVO;
import com.example.motive_app.network.vo.UserInfoVO;
import com.example.motive_app.service.alarm.JobSchedulerStart;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private HttpRequestService httpRequestService;

    //다이얼로그
    CustomDialog dialog;
    private String dialogContent;
    String loginId;
    String loginPwd;
    boolean memberLogin = false;
    boolean familyLogin = false;
    Retrofit retrofit;

    Intent foregroundServiceIntent;

    String medalVideo = "N";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.progressBar.hide();

        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            medalVideo = intent.getExtras().getString("medalVideo");
            if(medalVideo!=null&&medalVideo.equals("Y")) {
                SharedPreferences videoYN = getSharedPreferences("videoYN", Activity.MODE_PRIVATE);
                //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
                SharedPreferences.Editor medalVideoValue = videoYN.edit();
                medalVideoValue.putString("videoValue", medalVideo);
                medalVideoValue.apply();
            }
        }



        retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        binding.registerBtn.setOnClickListener(onClickListener);
        binding.loginBtn.setOnClickListener(onClickListener);
        binding.findText.setOnClickListener(onClickListener);


        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("saveId", null);
        loginPwd = auto.getString("savePwd", null);
        removeNotification();
        if (loginId != null && loginPwd != null) {
            binding.progressBar.show();
            binding.loginId.setText(loginId);
            binding.loginPassword.setText(loginPwd);
            binding.loginBtn.callOnClick();
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == binding.registerBtn) {
                startActivity(new Intent(getApplicationContext(), TypeChoiceActivity.class));

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            } else if (v == binding.loginBtn) {
                binding.progressBar.show();
                Log.e("progressBar", "progressBar");
                LoginRequest loginRequest = new LoginRequest(binding.loginId.getText().toString(), binding.loginPassword.getText().toString());
                httpRequestService.loginRequest(loginRequest).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.body() != null) {
                            Log.d("userResponse", response.body().get("result").toString());
                            if (!response.body().get("result").toString().contains("error")) {
                                memberLogin = true;
                                Login(response);
                            } else {
                                FamilyLoginRequest familyLoginRequest = new FamilyLoginRequest(binding.loginId.getText().toString(), binding.loginPassword.getText().toString());
                                httpRequestService.familyLoginRequest(familyLoginRequest).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                        if (response.body() != null) {
                                            familyLogin = true;
                                            Log.d("familyResponse", response.body().get("result").toString());
                                            Login(response);
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    }
                });


            } else if (v == binding.findText) {
                startActivity(new Intent(getApplicationContext(), LoginHelpActivity.class));

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }
        }
    };

    public void Login(Response<JsonObject> okResponse) {
        if (!binding.progressBar.isShown()) {
            binding.progressBar.show();
        }
        assert okResponse.body() != null;
        String result = okResponse.body().get("result").toString().replace("\"", "");
        Log.e("result", " " + result);
        switch (result) {
            case "ok":
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
                SharedPreferences.Editor autoLogin = auto.edit();
                autoLogin.putString("saveId", binding.loginId.getText().toString());
                autoLogin.putString("savePwd", binding.loginPassword.getText().toString());
                autoLogin.apply();

                Intent intent;
                Gson gson = new Gson();
                if (memberLogin) {
                    try {
                        JobSchedulerStart.start(this);
                    } catch (Exception ignored) {
                        JobSchedulerStart.stop(this);
                        JobSchedulerStart.start(this);
                    }

                    SharedPreferences videoYN = getSharedPreferences("videoYN", Activity.MODE_PRIVATE);
                    if(videoYN.getString("videoValue", null)!=null) {
                        medalVideo = videoYN.getString("videoValue", null);
                    }
                    Log.d("videoValue",videoYN.getString("videoValue", null) +" ");
                    UserInfoVO userInfoVO = gson.fromJson(okResponse.body().get("userInfoVO").toString(), UserInfoVO.class);
                    Log.d("userInfoVO", userInfoVO.getId());
                    Log.d("medalVideo", medalVideo + "");
                    Log.d("medalVideo", userInfoVO.getRegistrationDate() + "");

                    intent = new Intent(getApplicationContext(), MemberMainActivity.class);
                    intent.putExtra("userInfoVO", userInfoVO);
                    intent.putExtra("type", "users");
                    intent.putExtra("medalVideo", medalVideo);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    finish();

                } else if (familyLogin) {
                    JobSchedulerStart.start(this);
                    FamilyInfoVO familyInfoVO = gson.fromJson(okResponse.body().get("familyInfoVO").toString(), FamilyInfoVO.class);
                    Log.d("familyVo", familyInfoVO.getId());
                    intent = new Intent(getApplicationContext(), FamilyMainActivity.class);
                    intent.putExtra("familyInfoVO", familyInfoVO);
                    intent.putExtra("type", "family");
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    finish();
                }
                break;
            case "error":
                binding.progressBar.hide();
                dialogContent = "가입하지 않은 아이디이거나,\n잘못된 비밀번호 입니다.";
                Dialog();
                break;
            case "withdrawal":
                binding.progressBar.hide();
                dialogContent = "탈퇴한 아이디입니다.";
                Dialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding.progressBar.isShown()) {
            binding.progressBar.hide();
        }
        if (null != foregroundServiceIntent) {
            stopService(foregroundServiceIntent);
            foregroundServiceIntent = null;
        }
    }

    public void Dialog() {
        dialog = new CustomDialog(LoginActivity.this,
                dialogContent,// 내용
                OkListener); // 왼쪽 버튼 이벤트
        // 오른쪽 버튼 이벤트

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    //다이얼로그 클릭이벤트
    private View.OnClickListener OkListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    public void removeNotification() {
        SharedPreferences pushCount = getSharedPreferences("pushCount", Activity.MODE_PRIVATE);
        //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
        SharedPreferences.Editor pushCountValue = pushCount.edit();
        pushCountValue.putString("setMessageCount", "0");
        pushCountValue.apply();
        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        //하위 버전에서 동작하라고...??, 아직 하위버전은 테스트 해보지 못함.
        ShortcutBadger.removeCount(this);
        assert notificationManager != null;
        notificationManager.cancel(9999);
    }
}