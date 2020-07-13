package kr.rowan.motive_app.activity.registration;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.mindrot.jbcrypt.BCrypt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.LoginActivity;
import kr.rowan.motive_app.data.FamilyItem;
import kr.rowan.motive_app.databinding.ActivityRegisterBinding;
import kr.rowan.motive_app.dialog.CustomDialog;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.FamilyRegistrationRequest;
import kr.rowan.motive_app.network.dto.RegistrationRequest;
import kr.rowan.motive_app.network.dto.RelationItem;
import kr.rowan.motive_app.network.dto.UserIdRequest;
import kr.rowan.motive_app.network.dto.UserPhoneRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private EditText nameErrorText;
    private EditText idErrorText;
    private EditText passErrorText;
    private EditText passCheckErrorText;
    private EditText phoneNumErrorText;
    private EditText birthErrorText;
    private HttpRequestService httpRequestService;
    private String result;
    private String password;
    private int birthCheck;
    private int monthCheck;
    private int dayCheck;
    private boolean[] success;

    //intent data
    private String code;
    private String email;
    private ArrayList<FamilyItem> familyList = null;
    private String type;

    //다이얼로그
    private String dialogContent;
    CustomDialog dialog;
    private boolean registerationOk=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        //listener
        binding.backArrow.setOnClickListener(onClickListener);
        binding.registerCheckBox.setOnClickListener(onClickListener);
        binding.okRegisterBtn.setOnClickListener(onClickListener);
        binding.registerPhoneNumText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        success = new boolean[7];
        for(int i=0;i<success.length;i++){
            success[i] = false;
        }


        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if(intent.getExtras()!=null) {
            type = intent.getExtras().getString("type");
            assert type != null;
            if(type.equals("users")){
                code = intent.getExtras().getString("code");
                success[1] = true;
                success[2] = true;
                success[3] = true;
                binding.id.setVisibility(View.GONE);
                binding.pass.setVisibility(View.GONE);
                binding.passCheck.setVisibility(View.GONE);
                binding.registerIdView.setVisibility(View.GONE);
                binding.registerPassView.setVisibility(View.GONE);
                binding.registerPassCheckView.setVisibility(View.GONE);
            }else if(type.equals("family")){
                binding.birth.setVisibility(View.GONE);
                binding.registerBirthView.setVisibility(View.GONE);
                success[5] = true;
                familyList = intent.getParcelableArrayListExtra("familyList");
            }
            email = intent.getExtras().getString("email");
            if(email!=null) {
                Log.d("email", email);
            }
        }

        //입력 설정
        setInputLimit(Objects.requireNonNull(binding.registerNameTextInputLayout.getEditText()), "^[ㄱ-ㅣ가-힣a-zA-Z|\u318D\u119E\u11A2\u2022\u2025a\u00B7\uFE55]*$");
        setInputLimit(Objects.requireNonNull(binding.registerIdTextInputLayout.getEditText()), "^[a-zA-Z0-9_]*$");

        //에딧 텍스트 별 에러메세지 추가
        setErrorMessage( 0, binding.registerNameTextInputLayout, binding.registerNameView);
        setErrorMessage( 1, binding.registerIdTextInputLayout, binding.registerIdView);
        setErrorMessage( 2, binding.registerPassTextInputLayout, binding.registerPassView);
        setErrorMessage( 3, binding.registerPassCheckTextInputLayout, binding.registerPassCheckView);
        setErrorMessage( 4, binding.registerPhoneNumTextInputLayout, binding.registerPhoneNumView);
        setErrorMessage( 5, binding.registerBirthTextTextInputLayout, binding.registerBirthView);




        //텍스트뷰 밑줄
        binding.termsOfUse.setPaintFlags(binding.termsOfUse.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            if (v == binding.backArrow) {

                intent = new Intent(getApplicationContext(), EmailCheckActivity.class);
                if (type.equals("users")) {
                    intent.putExtra("code", code);
                } else if (type.equals("family")) {
                    intent.putParcelableArrayListExtra("familyList", familyList);
                }
                intent.putExtra("type", type);
                intent.putExtra("email", email);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                finish();
            }else if(v==binding.registerCheckBox){
                success[6] = binding.registerCheckBox.isChecked();
                for (boolean b : success) {
                    if (!b) {
                        binding.okRegisterBtn.setEnabled(false);
                        binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                        break;
                    }
                    if (success[6]) {
                        binding.okRegisterBtn.setEnabled(true);
                        binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_blue);
                    }
                }
            }else if(v==binding.okRegisterBtn){
                if(binding.birth.getVisibility() == View.VISIBLE) {
                    RegistrationRequest registrationRequest = new RegistrationRequest();
                    Log.d("register data","------------------");
                    Log.d("name", Objects.requireNonNull(binding.registerNameText.getText()).toString());
                    Log.d("phone", Objects.requireNonNull(binding.registerPhoneNumText.getText()).toString().replace("-",""));
                    Log.d("birth", Objects.requireNonNull(binding.registerBirthText.getText()).toString());
                    Log.d("email",email);
                    Log.d("code",code);
                    Log.d("-","----------------------------------");
                    if(type.equals("users")) {
                        registrationRequest.setId(binding.registerPhoneNumText.getText().toString().replace("-",""));
                        registrationRequest.setPswd(BCrypt.hashpw(Objects.requireNonNull(binding.registerBirthText.getText()).toString().substring(4), BCrypt.gensalt()));
                        Log.d("id", Objects.requireNonNull(binding.registerPhoneNumText.getText().toString().replace("-","")));
                        Log.d("pswd", Objects.requireNonNull(binding.registerBirthText.getText()).toString().substring(4));
                    }else{
                        registrationRequest.setId(Objects.requireNonNull(binding.registerIdText.getText()).toString());
                        registrationRequest.setPswd(BCrypt.hashpw(Objects.requireNonNull(binding.registerPassText.getText()).toString(), BCrypt.gensalt()));
                        Log.d("id", Objects.requireNonNull(binding.registerIdText.getText()).toString());
                        Log.d("pswd", Objects.requireNonNull(binding.registerPassText.getText()).toString());
                    }
                    registrationRequest.setName(Objects.requireNonNull(binding.registerNameText.getText()).toString());
                    registrationRequest.setPhone(binding.registerPhoneNumText.getText().toString().replace("-",""));
                    registrationRequest.setBirth(Integer.parseInt(Objects.requireNonNull(binding.registerBirthText.getText()).toString()));
                    registrationRequest.setEmail(email);
                    registrationRequest.setOrganizationCode(code);
                    httpRequestService.registrationRequest(registrationRequest).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                            if(response.body()!=null) {
                                Log.d("result",response.body().get("result").toString());
                                if (response.body().get("result").toString().replace("\"","").equals("ok")) {

                                    dialogContent = "회원가입이 완료 되었습니다";
                                    registerationOk = true;
                                    Dialog();

                                } else {
                                    dialogContent = "ERROR";
                                    registerationOk = false;
                                    Dialog();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.e("response null", "null");
                            dialogContent="네트워크 에러";
                            Dialog();
                        }
                    });
                }else{
                    FamilyRegistrationRequest familyRegistrationRequest = new FamilyRegistrationRequest();
                    List<RelationItem> relationItems = new ArrayList<>();
                    Log.d("register data","------------------");
                    Log.d("id", Objects.requireNonNull(binding.registerIdText.getText()).toString());
                    Log.d("name", Objects.requireNonNull(binding.registerNameText.getText()).toString());
                    Log.d("pswd", Objects.requireNonNull(binding.registerPassText.getText()).toString());
                    Log.d("phone", Objects.requireNonNull(binding.registerPhoneNumText.getText()).toString().replace("-",""));
                    Log.d("email",email);
                    for(int i=0;i<familyList.size();i++){
                        RelationItem relationItem = new RelationItem();
                        relationItem.setUserId(familyList.get(i).getUserId());
                        relationItem.setRelation(familyList.get(i).getRelation());
                        relationItems.add(relationItem);
                        Log.d("familyListId"+i,familyList.get(i).getUserId());
                        Log.d("familyListRelation"+i,familyList.get(i).getRelation());
                    }
                    Log.d("-","----------------------------------");
                    familyRegistrationRequest.setId(Objects.requireNonNull(binding.registerIdText.getText()).toString());
                    familyRegistrationRequest.setName(Objects.requireNonNull(binding.registerNameText.getText()).toString());
                    familyRegistrationRequest.setPswd(BCrypt.hashpw(Objects.requireNonNull(binding.registerPassText.getText()).toString(), BCrypt.gensalt()));
                    familyRegistrationRequest.setPhone(binding.registerPhoneNumText.getText().toString().replace("-",""));
                    familyRegistrationRequest.setRelations(relationItems);
                    familyRegistrationRequest.setEmail(email);

                    httpRequestService.familyRegistrationRequest(familyRegistrationRequest).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                            if(response.body()!=null) {
                                Log.d("response.body()",response.body().toString());
                                Log.d("result",response.body().get("result").toString());
                                if (response.body().get("result").toString().replace("\"","").equals("success")) {

                                    dialogContent = "회원가입이 완료 되었습니다";
                                    registerationOk = true;
                                    Dialog();

                                } else {
                                    dialogContent = "ERROR";
                                    registerationOk = false;
                                    Dialog();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.e("response null", "null");
                            dialogContent="네트워크 에러";
                            Dialog();
                        }
                    });
                }

            }
        }
    };

    public void setErrorMessage(final int type, final TextInputLayout inputLayout, final ConstraintLayout editTextView) {

        switch (type) {
            case 0:
                nameErrorText = inputLayout.getEditText();
                assert nameErrorText != null;
                nameErrorText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (nameErrorText.getText().length() == 1) {
                            inputLayout.setError("두 글자 이상 입력해주세요.");
                            editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                            binding.registerNameIcon.setVisibility(View.VISIBLE);
                            binding.registerNameIcon.setImageResource(R.drawable.motive_icon_no);
                            binding.registerNameIcon.setTag("icon_no");
                            binding.okRegisterBtn.setEnabled(false);
                            binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            success[0] = false;
                        } else if (nameErrorText.getText().length() == 0) {
                            inputLayout.setErrorEnabled(false);
                            editTextView.setBackgroundResource(R.drawable.text_border_5);
                            binding.registerNameIcon.setVisibility(View.GONE);
                            binding.okRegisterBtn.setEnabled(false);
                            binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            success[0] = false;
                        } else {
                            inputLayout.setErrorEnabled(false);
                            editTextView.setBackgroundResource(R.drawable.text_border_succes);
                            binding.registerNameIcon.setVisibility(View.VISIBLE);
                            binding.registerNameIcon.setImageResource(R.drawable.motive_icon_check_on);
                            binding.registerNameIcon.setTag("icon_check_on");
                            success[0] = true;
                            for (boolean b : success) {
                                if (!b) {
                                    break;
                                }
                                if (success[6]) {
                                    binding.okRegisterBtn.setEnabled(true);
                                    binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                }
                            }
                        }
                    }
                });
                break;
            case 1:
                idErrorText = inputLayout.getEditText();
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
                        UserIdRequest userIdRequest = new UserIdRequest();
                        userIdRequest.setUserId(Objects.requireNonNull(binding.registerIdText.getText()).toString());
                        httpRequestService.searchDuplicatedIdRequest(userIdRequest).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.body() != null) {
                                    result = response.body().get("result").toString().replace("\"","");
                                    Log.d("Id Result",result);
                                    if (result.equals("registered")) {
                                        inputLayout.setError("중복되는 아이디입니다.");
                                        editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                        binding.registerIdIcon.setVisibility(View.VISIBLE);
                                        binding.registerIdIcon.setImageResource(R.drawable.motive_icon_no);
                                        binding.registerIdIcon.setTag("icon_no");
                                        binding.okRegisterBtn.setEnabled(false);
                                        binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                        success[1] = false;
                                    } else if (idErrorText.getText().length() == 0) {
                                        Log.e("length", "0");
                                        inputLayout.setErrorEnabled(false);
                                        editTextView.setBackgroundResource(R.drawable.text_border_5);
                                        binding.registerIdIcon.setVisibility(View.GONE);
                                        binding.okRegisterBtn.setEnabled(false);
                                        binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                        success[1] = false;
                                    } else {
                                        Log.e("length", "1");
                                        if (idErrorText.getText().toString().matches("^[a-zA-Z0-9]{4,16}$")) {
                                            Log.e("length", "2");
                                            inputLayout.setErrorEnabled(false);
                                            editTextView.setBackgroundResource(R.drawable.text_border_succes);
                                            binding.registerIdIcon.setVisibility(View.VISIBLE);
                                            binding.registerIdIcon.setImageResource(R.drawable.motive_icon_check_on);
                                            binding.registerIdIcon.setTag("icon_check_on");
                                            success[1] = true;
                                            for (boolean b : success) {
                                                if (!b) {
                                                    break;
                                                }
                                                if (success[6]) {
                                                    binding.okRegisterBtn.setEnabled(true);
                                                    binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                                }
                                            }
                                        } else {
                                            Log.e("length", "3");
                                            inputLayout.setError("영문/숫자 조합으로 4자리~16자리");
                                            editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                            binding.registerIdIcon.setVisibility(View.VISIBLE);
                                            binding.registerIdIcon.setImageResource(R.drawable.motive_icon_no);
                                            binding.registerIdIcon.setTag("icon_no");
                                            binding.okRegisterBtn.setEnabled(false);
                                            binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                            success[1] = false;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("response null", "null");
                                inputLayout.setError("네트워크 연결 에러");
                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                binding.okRegisterBtn.setEnabled(false);
                                binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
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
                            binding.okRegisterBtn.setEnabled(false);
                            binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            success[2] = false;
                            binding.registerPassIcon.setVisibility(View.GONE);
                            password = "";
                        } else {
                            if (passErrorText.getText().toString().matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,16}$")) {
                                inputLayout.setErrorEnabled(false);
                                editTextView.setBackgroundResource(R.drawable.text_border_succes);
                                binding.registerPassIcon.setVisibility(View.VISIBLE);
                                binding.registerPassIcon.setImageResource(R.drawable.motive_icon_check_on);
                                binding.registerPassIcon.setTag("icon_check_on");
                                password = passErrorText.getText().toString();
                                success[2] = true;
                                for (boolean b : success) {
                                    if (!b) {
                                        break;
                                    }
                                    if (success[6]) {
                                        binding.okRegisterBtn.setEnabled(true);
                                        binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                    }
                                }
                            } else {
                                inputLayout.setError("영문/숫자/특수문자 조합으로 8자리~16자리");
                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                binding.registerPassIcon.setVisibility(View.VISIBLE);
                                binding.registerPassIcon.setImageResource(R.drawable.motive_icon_no);
                                binding.registerPassIcon.setTag("icon_no");
                                binding.okRegisterBtn.setEnabled(false);
                                binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                success[2] = false;
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
                            binding.registerPassCheckIcon.setVisibility(View.GONE);
                            binding.okRegisterBtn.setEnabled(false);
                            binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            success[3] = false;
                        } else {
                            String passCheck = passCheckErrorText.getText().toString();
                            if (passCheck.equals(password)) {
                                inputLayout.setErrorEnabled(false);
                                editTextView.setBackgroundResource(R.drawable.text_border_succes);
                                binding.registerPassCheckIcon.setVisibility(View.VISIBLE);
                                binding.registerPassCheckIcon.setImageResource(R.drawable.motive_icon_check_on);
                                binding.registerPassCheckIcon.setTag("icon_check_on");
                                success[3] = true;
                                for (boolean b : success) {
                                    if (!b) {
                                        break;
                                    }
                                    if (success[6]) {
                                        binding.okRegisterBtn.setEnabled(true);
                                        binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                    }
                                }
                            } else {
                                inputLayout.setError("비밀번호가 불일치 합니다.");
                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                binding.registerPassCheckIcon.setVisibility(View.VISIBLE);
                                binding.registerPassCheckIcon.setImageResource(R.drawable.motive_icon_no);
                                binding.registerPassCheckIcon.setTag("icon_no");
                                binding.okRegisterBtn.setEnabled(false);
                                binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                success[3] = false;
                            }
                        }
                    }
                });
                break;
            case 4:
                phoneNumErrorText = inputLayout.getEditText();
                assert phoneNumErrorText != null;
                phoneNumErrorText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("phone", Objects.requireNonNull(binding.registerPhoneNumText.getText()).toString().replace("-",""));
                        if (phoneNumErrorText.getText().length() == 0) {
                            inputLayout.setErrorEnabled(false);
                            editTextView.setBackgroundResource(R.drawable.text_border_5);
                            binding.registerPhoneNumIcon.setVisibility(View.GONE);
                            binding.okRegisterBtn.setEnabled(false);
                            binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            success[4] = false;
                        } else {
                            if (phoneNumErrorText.getText().length()>7) {
                                UserPhoneRequest request= new UserPhoneRequest();
                                request.setUserPhone(phoneNumErrorText.getText().toString().replace("-",""));
                                httpRequestService.userPhoneRequest(request).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if(response.body()!=null){
                                            if(!response.body().get("memberInfoVO").toString().equals("null")){
                                                inputLayout.setError("이미 가입되어 있는 전화번호 입니다.");
                                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                                binding.registerPhoneNumIcon.setVisibility(View.VISIBLE);
                                                binding.registerPhoneNumIcon.setImageResource(R.drawable.motive_icon_no);
                                                binding.registerPhoneNumIcon.setTag("icon_no");
                                                binding.okRegisterBtn.setEnabled(false);
                                                binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                                success[4] = false;
                                            }else{
                                                inputLayout.setErrorEnabled(false);
                                                editTextView.setBackgroundResource(R.drawable.text_border_succes);
                                                binding.registerPhoneNumIcon.setVisibility(View.VISIBLE);
                                                binding.registerPhoneNumIcon.setImageResource(R.drawable.motive_icon_check_on);
                                                binding.registerPhoneNumIcon.setTag("icon_check_on");
                                                success[4] = true;
                                                for (boolean b : success) {
                                                    if (!b) {
                                                        break;
                                                    }
                                                    if (success[6]) {
                                                        binding.okRegisterBtn.setEnabled(true);
                                                        binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        Log.d("실패","onFailure");
                                    }
                                });

                            } else {
                                inputLayout.setError("전화번호를 입력해주세요.");
                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                binding.registerPhoneNumIcon.setVisibility(View.VISIBLE);
                                binding.registerPhoneNumIcon.setImageResource(R.drawable.motive_icon_no);
                                binding.registerPhoneNumIcon.setTag("icon_no");
                                binding.okRegisterBtn.setEnabled(false);
                                binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                success[4] = false;
                            }
                        }
                    }
                });
                break;
            case 5:
                birthErrorText = inputLayout.getEditText();
                assert birthErrorText != null;
                birthErrorText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (birthErrorText.getText().length() == 0) {
                            inputLayout.setErrorEnabled(false);
                            editTextView.setBackgroundResource(R.drawable.text_border_5);
                            binding.registerBirthIcon.setVisibility(View.GONE);
                            binding.okRegisterBtn.setEnabled(false);
                            binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                            success[5] = false;
                        } else {
                            Date currentTime = Calendar.getInstance().getTime();
                            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                            int year = Integer.parseInt(yearFormat.format(currentTime));
                            if(birthErrorText.getText().length()==8) {
                                birthCheck = Integer.parseInt(birthErrorText.getText().toString().substring(0, 4));
                                monthCheck = Integer.parseInt(birthErrorText.getText().toString().substring(4,6));
                                dayCheck = Integer.parseInt(birthErrorText.getText().toString().substring(6,8));
                            }
                            if (birthErrorText.getText().length()==8&&birthCheck>1899&&birthCheck<=year&&monthCheck<13&&0<monthCheck&&dayCheck<32&&0<dayCheck) {
                                inputLayout.setErrorEnabled(false);
                                editTextView.setBackgroundResource(R.drawable.text_border_succes);
                                binding.registerBirthIcon.setVisibility(View.VISIBLE);
                                binding.registerBirthIcon.setImageResource(R.drawable.motive_icon_check_on);
                                binding.registerBirthIcon.setTag("icon_check_on");
                                success[5] = true;
                                for (boolean b : success) {
                                    if (!b) {
                                        break;
                                    }
                                    if (success[6]) {
                                        binding.okRegisterBtn.setEnabled(true);
                                        binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                    }
                                }
                            } else {
                                inputLayout.setError("생년월일을 입력해주세요.");
                                editTextView.setBackgroundResource(R.drawable.text_border_on_error);
                                binding.registerBirthIcon.setVisibility(View.VISIBLE);
                                binding.registerBirthIcon.setImageResource(R.drawable.motive_icon_no);
                                binding.registerBirthIcon.setTag("icon_no");
                                binding.okRegisterBtn.setEnabled(false);
                                binding.okRegisterBtn.setBackgroundResource(R.drawable.btn_back_gray);
                                success[5] = false;
                            }
                        }
                    }
                });
                break;
        }
    }

    public void iconClick(View view){
        if(view.getTag().toString().equals("icon_no")){
            switch (view.getId()){
                case R.id.register_name_icon:
                    binding.registerNameText.setText("");
                    break;
                case R.id.register_id_icon:
                    binding.registerIdText.setText("");
                    break;
                case R.id.register_pass_icon:
                    binding.registerPassText.setText("");
                    break;
                case R.id.register_passCheck_icon:
                    binding.registerPassCheckText.setText("");
                    break;
                case R.id.register_phoneNum_icon:
                    binding.registerPhoneNumText.setText("");
                    break;
                case R.id.register_birth_icon:
                    binding.registerBirthText.setText("");
                    break;
            }
        }
    }

    public void setInputLimit(EditText editText, final String regex) {

        InputFilter filterAlphaNum = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile(regex);
                if (!ps.matcher(source).matches()) {
                    return "";
                }
                return null;
            }
        };

        editText.setFilters(new InputFilter[]{filterAlphaNum});
    }

    public void Dialog(){
        dialog = new CustomDialog(RegisterActivity.this,
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
            if(registerationOk){
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }
            dialog.dismiss();
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(getApplicationContext(), EmailCheckActivity.class);
        if (type.equals("users")) {
            intent.putExtra("code", code);
        } else if (type.equals("family")) {
            intent.putParcelableArrayListExtra("familyList", familyList);
        }
        intent.putExtra("type", type);
        intent.putExtra("email", email);
        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        finish();
    }

}
