package com.example.motive_app.activity.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.activity.LoginActivity;
import com.example.motive_app.databinding.ActivityTypeChoiceBinding;

public class TypeChoiceActivity extends AppCompatActivity {
    ActivityTypeChoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_type_choice);

        binding.selectMemberBtn.setOnClickListener(onClickListener);
        binding.backArrow.setOnClickListener(onClickListener);
        binding.selectFamilyBtn.setOnClickListener(onClickListener);
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
            if (v == binding.selectMemberBtn) {
                intent = new Intent(getApplicationContext(), FindInstitutionActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            } else if (v == binding.selectFamilyBtn) {
                intent = new Intent(getApplicationContext(), FindFamilyActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            } else if (v == binding.backArrow) {
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
            finish();
        }
    };
}
