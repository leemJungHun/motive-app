package kr.rowan.motive_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import kr.rowan.motive_app.R;

public class CustomDialog_v2 extends Dialog {
    private String mContent;


    private View.OnClickListener mBtnClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.33f;
        Objects.requireNonNull(getWindow()).setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog_v2);

        TextView mContentView =  findViewById(R.id.dialog_content);
        Button mOkButton =  findViewById(R.id.dialog_Ok);
        Button mCancelButton =  findViewById(R.id.dialog_cancel);

        // 제목과 내용을 생성자에서 셋팅한다.
        mContentView.setText(mContent);

        // 클릭 이벤트 셋팅

        if (mBtnClickListener != null) {
            mOkButton.setOnClickListener(mBtnClickListener);
            mCancelButton.setOnClickListener(mBtnClickListener);
        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CustomDialog_v2(Context context, String content,
                           View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContent = content;
        this.mBtnClickListener = singleListener;
    }

}
