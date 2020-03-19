package com.example.motive_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.activity.VideoSendSelectActivity;
import com.example.motive_app.databinding.CustomDialogV5Binding;
import com.example.motive_app.network.HttpRequestService;

import java.util.Objects;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomDialog_v5 extends Dialog {
    CustomDialogV5Binding binding;

    Retrofit retrofit;
    HttpRequestService httpRequestService;

    String name;
    private View.OnClickListener mBtnClickListener;
    Context context;

    //다이얼로그
    CustomDialog dialog;
    private String dialogContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.33f;
        Objects.requireNonNull(getWindow()).setAttributes(lpWindow);

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.custom_dialog_v5, null, false);
        setContentView(binding.getRoot());


        retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);


        // 클릭 이벤트 셋팅
        if (mBtnClickListener != null) {
            binding.dialogOk.setOnClickListener(mVIdeoSend);
            binding.dialogCancel.setOnClickListener(mBtnClickListener);
        }
    }

    //생성자 함수로 클릭이벤트를 받는다.
    public CustomDialog_v5(Context context, View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.mBtnClickListener = singleListener;
    }

    private View.OnClickListener mVIdeoSend = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(binding.videoTitle.getText().toString().equals("")) {
                dialogContent = "영상 제목을 입력해주세요";
                Dialog();
            }else{
                ((VideoSendSelectActivity) context).sendVideo(binding.videoTitle.getText().toString());
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
        }
    };
}
