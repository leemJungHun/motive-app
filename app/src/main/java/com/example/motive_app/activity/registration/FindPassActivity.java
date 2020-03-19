package com.example.motive_app.activity.registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.activity.LoginActivity;
import com.example.motive_app.databinding.ActivityFindPassBinding;
import com.example.motive_app.dialog.CustomDialog;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.DTO.SearchPswdRequest;
import com.google.gson.JsonObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindPassActivity extends AppCompatActivity {
    ActivityFindPassBinding binding;
    Retrofit retrofit;
    HttpRequestService httpRequestService;
    EditText email;
    EditText idErrorText;
    SearchPswdRequest request;


    private boolean inputId = false;
    private boolean inputEmail = false;
    //다이얼로그
    CustomDialog dialog;
    private String dialogContent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_pass);

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
                            inputEmail = true;
                            if(inputId){
                                binding.confirmBtn.setEnabled(true);
                                binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_blue);
                            }
                        }else {
                            binding.emailTextInputLayout.setError("이메일 형식에 맞지 않습니다.");
                            binding.confirmBtn.setEnabled(false);
                            binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            inputEmail = false;
                        }
                    }else {
                        binding.emailTextInputLayout.setError("이메일 형식에 맞지 않습니다.");
                        binding.confirmBtn.setEnabled(false);
                        binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                        inputEmail = false;
                    }
                } else {
                    binding.emailTextInputLayout.setError("이메일 형식에 맞지 않습니다.");
                    binding.confirmBtn.setEnabled(false);
                    binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                    inputEmail = false;
                }
            }
        });

        //id 리스너
        idErrorText = binding.findPassIdtextInputLayout.getEditText();
        assert idErrorText != null;
        idErrorText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (idErrorText.getText().length() == 0) {
                    binding.findPassIdtextInputLayout.setErrorEnabled(false);
                    binding.confirmBtn.setEnabled(false);
                    binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                    inputId=false;
                } else {
                    if (idErrorText.getText().toString().matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{4,16}$")) {
                        binding.findPassIdtextInputLayout.setErrorEnabled(false);
                        inputId=true;
                        if (inputEmail){
                            binding.confirmBtn.setEnabled(true);
                            binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_blue);
                        }
                    } else {
                        binding.findPassIdtextInputLayout.setError("아이디 형식에 맞지 않습니다.");
                        binding.confirmBtn.setEnabled(false);
                        binding.confirmBtn.setBackgroundResource(R.drawable.btn_back_gray);
                        inputId=false;
                    }
                }
            }
        });

        //onClickListener
        binding.backArrow.setOnClickListener(onClickListener);
        binding.findPassBtn.setOnClickListener(onClickListener);
        binding.confirmBtn.setOnClickListener(onClickListener);

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
            if(v==binding.backArrow){
                intent = new Intent(getApplicationContext(), LoginHelpActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

                finish();
            }else if(v==binding.findPassBtn){
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

                finish();
            }else if(v==binding.confirmBtn){
                request = new SearchPswdRequest();
                request.setEmail(binding.findPassEmailText.getText().toString());
                request.setId(binding.findPassIdText.getText().toString());
                request.setType("users");
                httpRequestService.searchPswdRequest(request).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.body() != null) {
                            if(response.body().get("result").toString().replace("\"","").equals("unregistered")){
                                request.setType("family");
                                httpRequestService.searchPswdRequest(request).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if (response.body() != null) {
                                            if(response.body().get("result").toString().replace("\"","").equals("unregistered")){
                                                dialogContent="이메일과 맞는 아이디가\n검색되지 않습니다.";
                                                Dialog();
                                            }else{
                                                email.setEnabled(false);
                                                binding.confirmBtn.setText("임시 비밀번호 다시 보내기");
                                                dialogContent="임시비밀번호가\n이메일로 전송 되었습니다.";
                                                Dialog();
                                                //Log.e("response success", authToken);
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
                                binding.confirmBtn.setText("임시 비밀번호 다시 보내기");
                                dialogContent="임시비밀번호가\n이메일로 전송 되었습니다.";
                                Dialog();
                                //Log.e("response success", authToken);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("response null", "null");
                    }
                });
            }
        }
    };

    public void Dialog(){
        dialog = new CustomDialog(FindPassActivity.this,
                dialogContent,// 내용
                OkListener); // 왼쪽 버튼 이벤트
        // 오른쪽 버튼 이벤트

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener OkListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };
}
