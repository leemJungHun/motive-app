package kr.rowan.motive_app.activity.registration;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.mindrot.jbcrypt.BCrypt;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.databinding.ActivityFindIdBinding;
import kr.rowan.motive_app.dialog.CustomDialog;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.EmailRequest;
import kr.rowan.motive_app.network.vo.EmailAuthVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindIdActivity extends AppCompatActivity {
    ActivityFindIdBinding binding;
    Retrofit retrofit;
    HttpRequestService httpRequestService;
    EditText email;
    EditText confirmNum;

    EmailRequest request;
    private String authToken;
    private String emailText;
    private String findId;

    //인증 시간
    CountDownTimer countDownTimer;
    private static final int MILLISINFUTURE = 179*1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private boolean countStop;
    private int second = 59;
    private int minute = 2;

    //다이얼로그
    CustomDialog dialog;
    private String dialogContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_id);

        //retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        //email 리스너
        email = binding.emailTextInputLayout.getEditText();
        assert email != null;
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("@")) {
                    String[] compare = s.toString().split("@");
                    if(compare.length==2) {
                        if (compare[1].contains(".")) {
                            binding.emailTextInputLayout.setErrorEnabled(false);
                            binding.confirmBtn.setEnabled(true);
                            binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_blue);
                        }else {
                            binding.emailTextInputLayout.setError("이메일 형식에 맞지 않습니다.");
                            binding.confirmBtn.setEnabled(false);
                            binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                        }
                    }else {
                        binding.emailTextInputLayout.setError("이메일 형식에 맞지 않습니다.");
                        binding.confirmBtn.setEnabled(false);
                        binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                    }
                } else {
                    binding.emailTextInputLayout.setError("이메일 형식에 맞지 않습니다.");
                    binding.confirmBtn.setEnabled(false);
                    binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                }
            }
        });


        //confirmNum 리스너
        confirmNum = binding.confirmTextInputLayout.getEditText();

        assert confirmNum != null;
        confirmNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==6&&!countStop){
                    binding.confirmOk.setEnabled(true);
                    binding.confirmOk.setBackgroundResource(R.drawable.btn_back_blue);
                }else{
                    binding.confirmOk.setEnabled(false);
                    binding.confirmOk.setBackgroundResource(R.drawable.btn_back_gray);
                }
            }
        });


        //ClickListener
        binding.backArrow.setOnClickListener(onClickListener);
        binding.confirmBtn.setOnClickListener(onClickListener);
        binding.findIdBtn.setOnClickListener(onClickListener);
        binding.confirmOk.setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginHelpActivity.class));

        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        finish();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            if(v==binding.findIdBtn){

                intent = new Intent(getApplicationContext(), TeachIdActivity.class);
                intent.putExtra("findId", findId);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }else if(v==binding.backArrow){
                intent = new Intent(getApplicationContext(), LoginHelpActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                finish();
            }else if(v==binding.confirmBtn){
                request = new EmailRequest();
                if(email.isEnabled()) {

                    emailText = email.getText().toString();

                    binding.confirmBtn.setText("인증메일 다시 보내기");

                    if (email.getText() != null) {
                        Log.d("email", email.getText().toString());
                        request.setEmail(email.getText().toString());
                    }
                    request.setType("users");
                    httpRequestService.searchIdRequest(request).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.body() != null) {

                                if(response.body().get("result").toString().replace("\"","").equals("unregistered")) {
                                    request.setType("family");
                                    httpRequestService.searchIdRequest(request).enqueue(new Callback<JsonObject>() {
                                        @Override
                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                            if (response.body() != null) {

                                                if(response.body().get("result").toString().replace("\"","").equals("unregistered")) {
                                                    dialogContent="이메일과 맞는 아이디가\n검색되지 않습니다.";
                                                    Dialog();
                                                }else{
                                                    email.setEnabled(false);
                                                    countDownTimer();
                                                    countDownTimer.start();
                                                    Gson gson = new Gson();
                                                    EmailAuthVO vo = gson.fromJson(response.body().get("result").toString(), EmailAuthVO.class);
                                                    authToken = vo.getAuthCode().replace("\"","");
                                                    findId = vo.getUserId().replace("\"","");
                                                    dialogContent="인증메일이 발송되었습니다.";
                                                    Dialog();
                                                    Log.e("response success", authToken);
                                                    Log.e("findId", findId);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<JsonObject> call, Throwable t) {
                                            Log.e("response null", "null");
                                        }
                                    });
                                }else{
                                    email.setEnabled(false);
                                    countDownTimer();
                                    countDownTimer.start();
                                    Gson gson = new Gson();
                                    EmailAuthVO vo = gson.fromJson(response.body().get("result").toString(), EmailAuthVO.class);
                                    authToken = vo.getAuthCode().replace("\"","");
                                    findId = vo.getUserId().replace("\"","");
                                    dialogContent="인증메일이 발송되었습니다.";
                                    Dialog();
                                    Log.e("response success", authToken);
                                    Log.e("findId", findId);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.e("response null", "null");
                        }
                    });
                }else{
                    emailText="";
                    email.setEnabled(true);
                    binding.confirmBtn.setText("인증메일 보내기");
                    email.setText("");
                    binding.confirmBtn.setEnabled(false);
                    binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                    binding.findIdBtn.setEnabled(false);
                    binding.findIdBtn.setBackgroundResource(R.drawable.btn_back_gray);
                    binding.emailTextInputLayout.setErrorEnabled(false);
                    onDestroy();
                    binding.confirmNum.setHint("인증번호 6자리를 입력하세요");
                    binding.confirmNum.setText("");
                }
            }else if(v==binding.confirmOk){
                Log.d("confirmNum",confirmNum.getText().toString() + "    authToken:"+authToken);
                if(BCrypt.checkpw(confirmNum.getText().toString(), authToken)){
                    Log.d("인증","성공");
                    dialogContent="인증이 확인되었습니다";
                    confirmNum.setEnabled(false);
                    binding.findIdBtn.setEnabled(true);
                    binding.findIdBtn.setBackgroundResource(R.drawable.btn_back_blue);
                }else{
                    Log.d("인증","실패");
                    dialogContent="잘못된 인증 번호입니다";
                }
                Dialog();
            }
        }
    };

    public void countDownTimer(){

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                countStop=false;
                if(second<10){
                    binding.confirmNum.setHint("0"+minute+":0"+second);
                }else{
                    binding.confirmNum.setHint("0"+minute+":"+second);
                }
                if(second==0&&minute!=0) {
                    minute--;
                    second = 60;
                }
                second --;
            }
            public void onFinish() {
                countStop=true;
                binding.confirmNum.setHint("다시 인증해주세요");
                authToken=null;
            }
        };
    }

    public void Dialog(){
        dialog = new CustomDialog(FindIdActivity.this,
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            countDownTimer.cancel();
        } catch (Exception ignored) {}
        countDownTimer=null;
    }
}
