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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.databinding.ActivityChangePassBinding;
import com.example.motive_app.dialog.CustomDialog;
import com.example.motive_app.network.dto.LoginRequest;
import com.example.motive_app.network.dto.UpdatePswdRequest;
import com.example.motive_app.network.HttpRequestService;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePassActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityChangePassBinding binding;
    EditText nowErrorText;
    EditText passErrorText;
    EditText passCheckErrorText;
    boolean success[];
    String userId;
    String password;
    String nowPass;
    String type;
    private HttpRequestService httpRequestService;
    private boolean changePassOk=false;

    //다이얼로그
    private String dialogContent;
    CustomDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pass);
        binding.backArrow.setOnClickListener(this);
        binding.confirmNextBtn.setOnClickListener(this);



        //SetErrorMessage
        setErrorMessage( 1, binding.nowPassTextInputLayout, binding.nowPassView);
        setErrorMessage( 2, binding.changePassTextInputLayout, binding.changePassView);
        setErrorMessage( 3, binding.changePassCheckTextInputLayout, binding.changePassCheckView);


        //서버 통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);


        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            userId = intent.getExtras().getString("userId");
            if (userId != null) {
                Log.d("userId", userId);
            }
            type = intent.getExtras().getString("type");
        }

        success = new boolean[3];
        for (int i = 0; i < success.length; i++) {
            success[i] = false;
        }

    }

    @Override
    public void onBackPressed() {
        finish();

        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backArrow) {
            finish();

            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }else if(v==binding.confirmNextBtn){
            UpdatePswdRequest request = new UpdatePswdRequest();
            Log.d("setId", " "+userId);
            Log.d("setNowPass"," "+nowPass);
            Log.d("setNewpass"," "+password);
            Log.d("setType"," "+type);
            request.setId(userId);
            request.setCurrentPswd(nowPass);
            request.setNewPswd(BCrypt.hashpw(password, BCrypt.gensalt()));
            request.setType(type);
            httpRequestService.updatePswdRequest(request).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.body()!=null){
                        String result = response.body().get("result").toString().replace("\"","");
                        if(result.equals("ok")){
                            Log.d("result", result);
                            dialogContent = "비밀번호가\n변경되었습니다.";
                            changePassOk = true;
                            Dialog();
                        }else{
                            Log.d("result", result);
                            dialogContent = "ERROR";
                            changePassOk = false;
                            Dialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    }

    public void setErrorMessage(final int type, final TextInputLayout inputLayout, final ConstraintLayout editTextView) {

        switch (type) {
            case 1:
                nowErrorText = inputLayout.getEditText();
                assert nowErrorText != null;
                nowErrorText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        LoginRequest loginRequest = new LoginRequest(userId, binding.nowPassText.getText().toString());
                        httpRequestService.loginRequest(loginRequest).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.body() != null) {
                                    String result = response.body().get("result").toString().replace("\"", "");
                                    Log.d("result", " " + result);
                                    if (nowErrorText.getText().length() == 0) {
                                        Log.e("length", "0");
                                        inputLayout.setErrorEnabled(false);
                                        editTextView.setBackgroundResource(R.drawable.text_border_5);
                                        binding.nowPassIcon.setVisibility(View.GONE);
                                        binding.confirmNextBtn.setEnabled(false);
                                        binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                        success[0] = false;
                                        nowPass = "";
                                    } else if (nowErrorText.getText().toString().matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,16}$")) {
                                        if (result.equals("ok")) {
                                            inputLayout.setErrorEnabled(false);
                                            editTextView.setBackgroundResource(R.drawable.text_border_succes);
                                            binding.nowPassIcon.setVisibility(View.VISIBLE);
                                            binding.nowPassIcon.setImageResource(R.drawable.motive_icon_check_on);
                                            success[0] = true;
                                            nowPass = nowErrorText.getText().toString();
                                            for (boolean b : success) {
                                                if (!b) {
                                                    break;
                                                }
                                                if (success[2]) {
                                                    binding.confirmNextBtn.setEnabled(true);
                                                    binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                                }
                                            }
                                        } else if (result.equals("error") || result.equals("withdrawal")) {
                                            inputLayout.setError("비밀번호가 틀립니다.");
                                            editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                            binding.nowPassIcon.setVisibility(View.VISIBLE);
                                            binding.nowPassIcon.setImageResource(R.drawable.motive_icon_no);
                                            success[0] = false;
                                            nowPass = "";
                                        }
                                    } else {
                                        inputLayout.setError("영문/숫자/특수문자 조합으로 8자리~16자리");
                                        editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                        binding.nowPassIcon.setVisibility(View.VISIBLE);
                                        binding.nowPassIcon.setImageResource(R.drawable.motive_icon_no);
                                        binding.confirmNextBtn.setEnabled(false);
                                        binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                        success[0] = false;
                                        nowPass = "";
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("response null", "null");
                                inputLayout.setError("네트워크 연결 에러");
                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                binding.confirmNextBtn.setEnabled(false);
                                binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                success[0] = false;
                                nowPass = "";
                            }
                        });


                    }
                });
                break;
            case 2:
                passErrorText = inputLayout.getEditText();
                assert passErrorText != null;
                passErrorText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (passErrorText.getText().length() == 0) {
                            inputLayout.setErrorEnabled(false);
                            editTextView.setBackgroundResource(R.drawable.text_border_5);
                            binding.confirmNextBtn.setEnabled(false);
                            binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            success[1] = false;
                            binding.changePassIcon.setVisibility(View.GONE);
                            password = "";
                        } else {
                            if (passErrorText.getText().toString().matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,16}$")) {
                                if (nowPass.equals(passErrorText.getText().toString())) {
                                    inputLayout.setError("현재 비밀번호와 같습니다");
                                    editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                    binding.changePassIcon.setVisibility(View.VISIBLE);
                                    binding.changePassIcon.setImageResource(R.drawable.motive_icon_no);
                                    binding.confirmNextBtn.setEnabled(false);
                                    binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                    success[1] = false;
                                    password = "";
                                } else {
                                    inputLayout.setErrorEnabled(false);
                                    editTextView.setBackgroundResource(R.drawable.text_border_succes);
                                    binding.changePassIcon.setVisibility(View.VISIBLE);
                                    binding.changePassIcon.setImageResource(R.drawable.motive_icon_check_on);
                                    password = passErrorText.getText().toString();
                                    success[1] = true;
                                    for (boolean b : success) {
                                        if (!b) {
                                            break;
                                        }
                                        if (success[2]) {
                                            binding.confirmNextBtn.setEnabled(true);
                                            binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                        }
                                    }
                                }
                            } else {
                                inputLayout.setError("영문/숫자/특수문자 조합으로 8자리~16자리");
                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                binding.changePassIcon.setVisibility(View.VISIBLE);
                                binding.changePassIcon.setImageResource(R.drawable.motive_icon_no);
                                binding.confirmNextBtn.setEnabled(false);
                                binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                success[1] = false;
                                password = "";
                            }
                        }
                    }
                });
                break;
            case 3:
                passCheckErrorText = inputLayout.getEditText();
                assert passCheckErrorText != null;
                passCheckErrorText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (passCheckErrorText.getText().length() == 0) {
                            inputLayout.setErrorEnabled(false);
                            editTextView.setBackgroundResource(R.drawable.text_border_5);
                            binding.changePassCheckIcon.setVisibility(View.GONE);
                            binding.confirmNextBtn.setEnabled(false);
                            binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            success[2] = false;
                        } else {
                            String passCheck = passCheckErrorText.getText().toString();
                            if (passCheck.equals(password)) {
                                inputLayout.setErrorEnabled(false);
                                editTextView.setBackgroundResource(R.drawable.text_border_succes);
                                binding.changePassCheckIcon.setVisibility(View.VISIBLE);
                                binding.changePassCheckIcon.setImageResource(R.drawable.motive_icon_check_on);
                                success[2] = true;
                                for (boolean b : success) {
                                    if (!b) {
                                        break;
                                    }
                                    if (success[2]) {
                                        binding.confirmNextBtn.setEnabled(true);
                                        binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                    }
                                }
                            } else {
                                inputLayout.setError("비밀번호가 불일치 합니다.");
                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                binding.changePassCheckIcon.setVisibility(View.VISIBLE);
                                binding.changePassCheckIcon.setImageResource(R.drawable.motive_icon_no);
                                binding.confirmNextBtn.setEnabled(false);
                                binding.confirmNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                success[2] = false;
                            }
                        }
                    }
                });
                break;
        }
    }

    public void Dialog(){
        dialog = new CustomDialog(ChangePassActivity.this,
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
            if(changePassOk){
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
            dialog.dismiss();
        }
    };
}
