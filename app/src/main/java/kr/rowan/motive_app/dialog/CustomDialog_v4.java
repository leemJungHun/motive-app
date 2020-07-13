package kr.rowan.motive_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.databinding.CustomDialogV4Binding;

public class CustomDialog_v4 extends Dialog implements View.OnClickListener{
    private CustomDialogV4Binding binding;

    private String name;
    private String phoneNum;
    private String institution;
    private CustomDialogListener customDialogListener;
    private Context context;
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

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.custom_dialog_v4, null, false);
        setContentView(binding.getRoot());

        binding.dialogName.setText(name);
        binding.findFamilyPhone.setText(phoneNum);
        binding.findFamilyInstitution.setText(institution);

        // 클릭 이벤트 셋팅
        binding.dialogOk.setOnClickListener(this);
        binding.dialogCancel.setOnClickListener(this);

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CustomDialog_v4(Context context, String name,
                           String phoneNum, String institution) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.name = name;
        this.phoneNum = phoneNum;
        this.institution = institution;
    }

    //인터페이스 설정
    public interface CustomDialogListener{
        void onPositiveClicked(String getRelation);
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_Ok: //확인 버튼을 눌렀을 때
                //각각의 변수에 EidtText에서 가져온 값을 저장
                //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
                if(!binding.findFamilyRelation.getText().toString().equals("")){
                    customDialogListener.onPositiveClicked(binding.findFamilyRelation.getText().toString());
                    dismiss();
                }else{
                    dialogContent = "환자와의 관계를 입력해주세요.";
                    Dialog();
                }
                break;
            case R.id.dialog_cancel: //취소 버튼을 눌렀을 때
                cancel();
                break;
        }
    }

    public void Dialog() {
        dialog = new CustomDialog(context,
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
