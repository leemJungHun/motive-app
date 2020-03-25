package com.example.motive_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.activity.registration.LoginHelpActivity;
import com.example.motive_app.activity.registration.TypeChoiceActivity;
import com.example.motive_app.databinding.ActivityLoginBinding;
import com.example.motive_app.dialog.CustomDialog;
import com.example.motive_app.network.DTO.FamilyLoginRequest;
import com.example.motive_app.network.DTO.LoginRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.VO.FamilyInfoVO;
import com.example.motive_app.network.VO.UserInfoVO;
import com.example.motive_app.service.alarm.JobSchedulerStart;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity{
    ActivityLoginBinding binding;
    private HttpRequestService httpRequestService;

    //다이얼로그
    CustomDialog dialog;
    private String dialogContent;
    String loginId;
    String loginPwd;
    boolean memberLogin=false;
    boolean familyLogin=false;
    Retrofit retrofit;

    Intent foregroundServiceIntent;

    String medalVideo="N";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);

        Intent intent = getIntent(); /*데이터 수신*/
        if(intent.getExtras()!=null) {
            medalVideo = intent.getExtras().getString("medalVideo");
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
        loginId = auto.getString("saveId",null);
        loginPwd = auto.getString("savePwd",null);

        if(loginId !=null && loginPwd != null) {
            binding.loginId.setText(loginId);
            binding.loginPassword.setText(loginPwd);
            binding.loginBtn.callOnClick();
        }
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v==binding.registerBtn){
                startActivity(new Intent(getApplicationContext(), TypeChoiceActivity.class));

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }else if(v==binding.loginBtn){
                LoginRequest loginRequest = new LoginRequest(binding.loginId.getText().toString(),binding.loginPassword.getText().toString());
                httpRequestService.loginRequest(loginRequest).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.body()!=null) {
                            Log.d("userResponse", response.body().get("result").toString());
                            if(!response.body().get("result").toString().replace("\"","").equals("error")) {
                                memberLogin = true;
                                Login(response);
                            }else{
                                FamilyLoginRequest familyLoginRequest = new FamilyLoginRequest(binding.loginId.getText().toString(), binding.loginPassword.getText().toString());
                                httpRequestService.familyLoginRequest(familyLoginRequest).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if (response.body() != null) {
                                            familyLogin = true;
                                            Log.d("familyResponse", response.body().get("result").toString());
                                            Login(response);
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
                    public void onFailure(Call<JsonObject> call, Throwable t) { }
                });


            }else if(v==binding.findText){
                startActivity(new Intent(getApplicationContext(), LoginHelpActivity.class));

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }
        }
    };

    public void Login(Response<JsonObject> okResponse){
        assert okResponse.body() != null;
        String result = okResponse.body().get("result").toString().replace("\"","");
        Log.d("result"," "+ result);
        if (result.equals("ok")) {
            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
            //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
            SharedPreferences.Editor autoLogin = auto.edit();
            autoLogin.putString("saveId", binding.loginId.getText().toString());
            autoLogin.putString("savePwd", binding.loginPassword.getText().toString());
            autoLogin.apply();

            Intent intent;
            Gson gson = new Gson();
            if(memberLogin) {
                JobSchedulerStart.start(this);
                UserInfoVO userInfoVO = gson.fromJson(okResponse.body().get("userInfoVO").toString(), UserInfoVO.class);
                Log.d("userInfoVO", userInfoVO.getId());
                Log.d("medalVideo",medalVideo);
                if(medalVideo.equals("N")) {
                    intent = new Intent(getApplicationContext(), MemberMainActivity.class);
                    intent.putExtra("userInfoVO", userInfoVO);
                    intent.putExtra("type", "users");
                    intent.putExtra("medalVideo", medalVideo);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    finish();
                }else if(medalVideo.equals("Y")){
                    intent = new Intent(getApplicationContext(), MemberMainActivity.class);
                    intent.putExtra("userInfoVO", userInfoVO);
                    intent.putExtra("type", "users");
                    intent.putExtra("medalVideo", medalVideo);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    finish();
                }
            }else if(familyLogin){
                JobSchedulerStart.start(this);
                FamilyInfoVO familyInfoVO = gson.fromJson(okResponse.body().get("familyInfoVO").toString(), FamilyInfoVO.class);
                Log.d("familyVo",familyInfoVO.getId());
                intent = new Intent(getApplicationContext(), FamilyMainActivity.class);
                intent.putExtra("familyInfoVO", familyInfoVO);
                intent.putExtra("type","family");
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }
        } else if (result.equals("error")) {
            dialogContent = "가입하지 않은 아이디이거나,\n잘못된 비밀번호 입니다.";
            Dialog();
        } else if (result.equals("withdrawal")) {
            dialogContent = "탈퇴한 아이디입니다.";
            Dialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != foregroundServiceIntent) {
            stopService(foregroundServiceIntent);
            foregroundServiceIntent = null;
        }
    }

    public void Dialog(){
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
}