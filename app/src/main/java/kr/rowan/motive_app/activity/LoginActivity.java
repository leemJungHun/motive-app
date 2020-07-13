package kr.rowan.motive_app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

import kr.rowan.motive_app.BuildConfig;
import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.registration.LoginHelpActivity;
import kr.rowan.motive_app.activity.registration.TypeChoiceActivity;
import kr.rowan.motive_app.databinding.ActivityLoginBinding;
import kr.rowan.motive_app.dialog.CustomDialog;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.FamilyLoginRequest;
import kr.rowan.motive_app.network.dto.LoginRequest;
import kr.rowan.motive_app.network.vo.FamilyInfoVO;
import kr.rowan.motive_app.network.vo.UserInfoVO;
import kr.rowan.motive_app.service.alarm.JobSchedulerStart;
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


    //UpdateDialog
    AlertDialog.Builder mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mDialog = new AlertDialog.Builder(this);

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
        }else{
            binding.progressBar.hide();
        }

        new versionCheck().execute();
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
                        Log.d("response.body",response.body() + " ");
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
                                        Log.d("fail",t.getMessage() + " ");
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
        SharedPreferences.Editor pushCountValue = pushCount.edit();
        pushCountValue.putString("setMessageCount", "0");
        pushCountValue.apply();
        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        ShortcutBadger.removeCount(this);
        assert notificationManager != null;
        notificationManager.cancel(9999);
    }


    @SuppressLint("StaticFieldLeak")
    private class versionCheck extends AsyncTask<Void, Void, String> {
        private final String APP_VERSION_NAME = BuildConfig.VERSION_NAME;
        private final String APP_PACKAGE_NAME = BuildConfig.APPLICATION_ID;
        private final String STORE_URL = "https://play.google.com/store/apps/details?id="+APP_PACKAGE_NAME; // nexon를 예를 들었습니다.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.e("버전 확인"," " + APP_VERSION_NAME);
            Log.e("패키지 확인"," " + APP_PACKAGE_NAME);
            Log.e("STORE_URL 확인"," " + STORE_URL);
            try{
                Document doc = Jsoup.connect(STORE_URL).get();

                Elements Version = doc.select(".htlgb");

                for (int i = 0; i < Version.size(); i++) {

                    String VersionMarket = Version.get(i).text();

                    if (Pattern.matches("^[0-9]{1}.[0-9]{1}.[0-9]{1}$", VersionMarket)) {

                        return VersionMarket;
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) { //s는 마켓의 버전입니다.
            if(s != null){
                if(!s.equals(APP_VERSION_NAME)){ //APP_VERSION_NAME는 현재 앱의
                    mDialog.setMessage("최신 버전이 출시되었습니다. 업데이트 후 사용 가능합니다.")
                            .setCancelable(false)
                            .setPositiveButton("업데이트 바로가기",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            Intent marketLaunch = new Intent(
                                                    Intent.ACTION_VIEW);
                                            marketLaunch.setData(Uri
                                                    .parse(STORE_URL));
                                            startActivity(marketLaunch);
                                            finish();
                                        }
                                    });
                    AlertDialog alert = mDialog.create();
                    alert.setTitle("업데이트 알림");
                    alert.show();
                }else{
                    if (loginId != null && loginPwd != null) {
                        binding.loginBtn.callOnClick();
                    }
                }
            }
            super.onPostExecute(s);
        }
    }
}
