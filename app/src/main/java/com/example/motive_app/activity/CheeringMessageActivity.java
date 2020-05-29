package com.example.motive_app.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.databinding.ActivityCheeringMessageBinding;
import com.example.motive_app.network.vo.UserInfoVO;

public class CheeringMessageActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityCheeringMessageBinding binding;
    UserInfoVO vo;
    String medalVideo;
    int whatMedal;
    AnimationDrawable effectAnim;
    String subTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cheering_message);

        Intent intent = getIntent(); //데이터 수신
        if (intent.getExtras() != null) {
            vo = (UserInfoVO) intent.getSerializableExtra("userInfoVO");

            whatMedal = intent.getExtras().getInt("whatMedal");
            medalVideo = intent.getExtras().getString("medalVideo");
            subTitle = intent.getExtras().getString("subTitle");

            if (subTitle!=null&&!subTitle.equals("")){
                binding.medalSelectText.setText(subTitle);
            }

            if (vo != null) {
                Log.d("getName", vo.getName());
                Log.d("getId", vo.getId());
                Log.d("getEmail", vo.getEmail());
                Log.d("getOrganizationCode", vo.getOrganizationCode());
                Log.d("getBirth", vo.getBirth());
                Log.d("getPhone", vo.getPhone());
                if (vo.getProfileImageUrl() != null) {
                    Log.d("getImageUrl", vo.getProfileImageUrl());
                }
            }
        }

        effectAnim = (AnimationDrawable) binding.medalEffect.getBackground();


        effectAnim.start();

        if(whatMedal==0){
            binding.medal.setImageResource(R.drawable.motive_img_009_medal_s);
        }else if(whatMedal==1){
            binding.medal.setImageResource(R.drawable.motive_img_009_medal_g);
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent moveIntent = new Intent(CheeringMessageActivity.this, MemberMainActivity.class);
            moveIntent.putExtra("medalVideo", "N");
            moveIntent.putExtra("userInfoVO", vo);
            startActivity(moveIntent);
            finish();
        }, 5000);
    }

    @Override
    public void onClick(View v) {

    }
}
