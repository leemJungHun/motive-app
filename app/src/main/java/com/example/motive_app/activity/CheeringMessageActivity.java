package com.example.motive_app.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.databinding.ActivityCheeringMessageBinding;
import com.example.motive_app.network.vo.UserInfoVO;

public class CheeringMessageActivity extends AppCompatActivity{
    ActivityCheeringMessageBinding binding;
    UserInfoVO vo;
    String medalVideo;
    int whatMedal;
    AnimationDrawable effectAnim;
    String subTitle;
    MediaPlayer fanfare;
    MediaPlayer clap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cheering_message);

        fanfare = MediaPlayer.create(CheeringMessageActivity.this, R.raw.fanfare);
        clap = MediaPlayer.create(CheeringMessageActivity.this, R.raw.clap_sound);
        fanfare.start();

        Intent intent = getIntent(); //데이터 수신
        if (intent.getExtras() != null) {
            vo = (UserInfoVO) intent.getSerializableExtra("userInfoVO");

            whatMedal = intent.getExtras().getInt("whatMedal");
            medalVideo = intent.getExtras().getString("medalVideo");
            subTitle = intent.getExtras().getString("subTitle");

            if (subTitle!=null&&!subTitle.equals("")){
                binding.medalSelectText.setText(subTitle);
            }else{
                if(whatMedal==0){
                    binding.medalSelectText.setText(R.string.cheering_message_s);
                }else if(whatMedal==1){
                    binding.medalSelectText.setText(R.string.cheering_message_g);
                }
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
        Handler handler2 = new Handler();

        handler.postDelayed(() -> {
            clap.start();
        }, 3000);

        handler2.postDelayed(() -> {
            fanfare.stop();
            clap.stop();
            // 초기화
            fanfare.reset();
            clap.reset();
            Intent moveIntent = new Intent(CheeringMessageActivity.this, MemberMainActivity.class);
            moveIntent.putExtra("medalVideo", "N");
            moveIntent.putExtra("userInfoVO", vo);
            startActivity(moveIntent);
            finish();
        }, 8000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MediaPlayer 해지
        if(fanfare != null) {
            fanfare.release();
            fanfare = null;
        }else if(clap != null) {
            clap.release();
            clap = null;
        }
    }

}
