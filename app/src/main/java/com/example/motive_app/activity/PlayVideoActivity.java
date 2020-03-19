package com.example.motive_app.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.motive_app.R;
import com.example.motive_app.databinding.ActivityPlayVideoBinding;
import com.example.motive_app.network.VO.UserInfoVO;
import com.example.motive_app.util.VideoControllerView;

public class PlayVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl  {
    ActivityPlayVideoBinding binding;
    UserInfoVO vo;
    String fileUrl;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;

    // add Controller
    VideoControllerView mcontroller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_video);

        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            vo = (UserInfoVO) intent.getSerializableExtra("userInfoVO");

            fileUrl = intent.getExtras().getString("fileUrl");

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
            if (fileUrl != null) {
                Log.d("fileUrl", fileUrl);
                mcontroller = new VideoControllerView(this);
                surfaceHolder = binding.playVideoView.getHolder();
                surfaceHolder.addCallback(this);
            }
        }


    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        mcontroller.show();
        return false;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("surfaceCreated", "surfaceCreated");
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {

            String path = fileUrl;
            Log.d("path", " " + path);
            mediaPlayer.setDataSource(path);

            //mediaPlayer.setVolume(0, 0); //볼륨 제거
            mediaPlayer.setDisplay(surfaceHolder); // 화면 호출
            mediaPlayer.prepare(); // 비디오 load 준비

            //mediaPlayer.setOnCompletionListener(completionListener); // 비디오 재생 완료 리스너

            mcontroller = new VideoControllerView(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            Log.e("MyTag", "surface view error : " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        mcontroller.setMediaPlayer(this);
        mcontroller.setAnchorView(binding.playVideoViewContainer);
        mediaPlayer.start();
    }
    // End MediaPlayer.OnPreparedListener
    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }
    @Override
    public boolean canSeekBackward() {
        return true;
    }
    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }
    @Override
    public void start() {
        mediaPlayer.start();
    }
}
