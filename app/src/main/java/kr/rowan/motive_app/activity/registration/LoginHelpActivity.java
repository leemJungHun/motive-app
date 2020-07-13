package kr.rowan.motive_app.activity.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.LoginActivity;
import kr.rowan.motive_app.databinding.ActivityLoginhelpBinding;

public class LoginHelpActivity extends AppCompatActivity {
    ActivityLoginhelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loginhelp);

        binding.idHelp.setOnClickListener(onClickListener);
        binding.passHelp.setOnClickListener(onClickListener);
        binding.backArrow.setOnClickListener(onClickListener);
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
            if(v==binding.idHelp){
                startActivity(new Intent(getApplicationContext(), FindIdActivity.class));

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }else if(v==binding.passHelp){
                startActivity(new Intent(getApplicationContext(), FindPassActivity.class));

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }else if(v==binding.backArrow){
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                finish();
            }
        }
    };
}
