package com.example.motive_app.activity.registration;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.activity.LoginActivity;
import com.example.motive_app.databinding.ActivityTeachIdBinding;

public class TeachIdActivity extends AppCompatActivity {
    ActivityTeachIdBinding binding;
    String findId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_teach_id);

        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            findId = intent.getExtras().getString("findId");
            if (findId != null) {
                Log.d("findId", findId);
            }
        }
        String teachId = "아이디는 " + findId + " 입니다.";
        SpannableStringBuilder ssb = new SpannableStringBuilder(teachId);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#808080")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#808080")), teachId.length() - 4, teachId.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.findIdText.setText(ssb);

        binding.backArrow.setOnClickListener(onClickListener);
        binding.goLoginBtn.setOnClickListener(onClickListener);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        finish();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            if (v == binding.goLoginBtn) {
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            } else if (v == binding.backArrow) {
                intent = new Intent(getApplicationContext(), FindIdActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                finish();
            }
        }
    };
}
