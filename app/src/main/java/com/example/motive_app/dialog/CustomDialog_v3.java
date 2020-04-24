package com.example.motive_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.example.motive_app.activity.MemberMainActivity;
import com.example.motive_app.R;
import com.example.motive_app.databinding.CustomDialogV3Binding;
import com.example.motive_app.network.dto.FamilyWithDrawalRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.dto.WithDrawalRequest;
import com.google.gson.JsonObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomDialog_v3 extends Dialog {
    private CustomDialogV3Binding binding;

    private Retrofit retrofit;
    private HttpRequestService httpRequestService;

    private String name;
    private View.OnClickListener mBtnClickListener;
    private Context context;
    private String type;

    //다이얼로그
    private CustomDialog dialog;
    private String dialogContent;

    //다이얼로그
    private CustomDialog_v2 dialog_v2;
    private String dialogContent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.33f;
        Objects.requireNonNull(getWindow()).setAttributes(lpWindow);

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.custom_dialog_v3, null, false);
        setContentView(binding.getRoot());


        retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);


        // 클릭 이벤트 셋팅
        if (mBtnClickListener != null) {
            binding.dialogOk.setOnClickListener(mWithdrawal);
            binding.dialogCancel.setOnClickListener(mBtnClickListener);
        }
    }

    //생성자 함수로 클릭이벤트를 받는다.
    public CustomDialog_v3(Context context, String name, View.OnClickListener singleListener, String type) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.mBtnClickListener = singleListener;
        this.name = name;
        this.type = type;
    }

    private View.OnClickListener mWithdrawal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Objects.requireNonNull(binding.withdrawalName.getText()).toString().equals("")|| Objects.requireNonNull(binding.withdrawalId.getText()).toString().equals("")|| Objects.requireNonNull(binding.withdrawalPass.getText()).toString().equals("")) {
                dialogContent = "회원정보를 입력해주세요.";
                Dialog();
            }else{
                dialogContent2 = "정말로 탈퇴하시겠습니까?";
                Dialog_v2();
                dismiss();
            }
        }
    };

    public void Dialog() {
        dialog = new CustomDialog(context,
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
            if(dialogContent.equals("회원탈퇴가 정상적으로 이루어졌습니다.\n그 동안 슈퍼브레인 동기강화앱을\n사용해주셔서 감사합니다.")) {
                ((MemberMainActivity) context).logOut("회원 탈퇴 완료");
            }
        }
    };


    public void Dialog_v2() {
        dialog_v2 = new CustomDialog_v2(context,
                dialogContent2,// 내용
                btnListener_v2); // 버튼 이벤트

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog_v2.setCancelable(true);
        Objects.requireNonNull(dialog_v2.getWindow()).setGravity(Gravity.CENTER);
        dialog_v2.show();
    }

    //다이얼로그_v2 클릭이벤트
    private View.OnClickListener btnListener_v2 = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.dialog_Ok) {
                if (Objects.requireNonNull(binding.withdrawalName.getText()).toString().equals(name)) {
                    if(type.equals("user")) {
                        WithDrawalRequest request = new WithDrawalRequest();
                        request.setId(Objects.requireNonNull(binding.withdrawalId.getText()).toString());
                        request.setPswd(Objects.requireNonNull(binding.withdrawalPass.getText()).toString());
                        httpRequestService.withDrawalRequest(request).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.body() != null) {
                                    String result = response.body().get("result").toString().replace("\"", "");
                                    if (result.equals("ok")) {
                                        dialogContent = "회원탈퇴가 정상적으로 이루어졌습니다.\n그 동안 슈퍼브레인 동기강화앱을\n사용해주셔서 감사합니다.";
                                        Dialog();
                                    } else {
                                        dialogContent = "회원정보가 일치하지 않습니다.";
                                        Dialog();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
                    }else{
                        FamilyWithDrawalRequest request = new FamilyWithDrawalRequest();
                        request.setFamilyId(Objects.requireNonNull(binding.withdrawalId.getText()).toString());
                        request.setPswd(Objects.requireNonNull(binding.withdrawalPass.getText()).toString());
                        httpRequestService.familyWithDrawalRequest(request).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.body() != null) {
                                    String result = response.body().get("result").toString().replace("\"", "");
                                    if (result.equals("ok")) {
                                        dialogContent = "회원탈퇴가 정상적으로 이루어졌습니다.\n그 동안 슈퍼브레인 동기강화앱을\n사용해주셔서 감사합니다.";
                                        Dialog();
                                    } else {
                                        dialogContent = "회원정보가 일치하지 않습니다.";
                                        Dialog();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
                    }
                }else{
                    dialogContent = "회원정보가 일치하지 않습니다.";
                    Dialog();
                }
            }
            dialog_v2.dismiss();
        }
    };
}
