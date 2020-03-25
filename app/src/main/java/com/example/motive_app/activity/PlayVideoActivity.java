package com.example.motive_app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

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
    String medalVideo;
    String videoIdx;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    boolean onController=false;

    private Thread timeThread = null;
    private Boolean isRunning = true;

    // add Controller
    VideoControllerView mcontroller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_video);
        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            vo = (UserInfoVO) intent.getSerializableExtra("userInfoVO");

            fileUrl = intent.getExtras().getString("fileUrl");
            medalVideo = intent.getExtras().getString("medalVideo");
            videoIdx = intent.getExtras().getString("videoIdx");

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

    @Override
    public void onBackPressed() {
        timeThread.interrupt();
        super.onBackPressed();
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            Log.d("isplaying",isPlaying()+" ");
            Log.d("duration",getDuration()+" ");
            Log.d("getCurrentPosition",getCurrentPosition()+" ");

            if (!onController) {
                mcontroller.show();
                onController = true;
            } else {
                mcontroller.hide();
                onController = false;
            }
        }
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
        timeThread = new Thread(new timeThread());
        timeThread.start();
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
        isRunning = !isRunning;
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }
    @Override
    public void start() {
        mediaPlayer.start();
        isRunning = !isRunning;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mSec = msg.arg1*10;
            if(getDuration()<=mSec&& !isPlaying()&&getCurrentPosition()>=getDuration()) {
                timeThread.interrupt();
                if(medalVideo.equals("Y")){
                    Intent intent;
                    intent = new Intent(getApplicationContext(), MedalSelectActivity.class);

                    intent.putExtra("userInfoVO", vo);
                    intent.putExtra("fileUrl", fileUrl);
                    intent.putExtra("medalVideo",medalVideo);
                    intent.putExtra("videoIdx",videoIdx);
                    startActivity(intent);

                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                    finish();
                }
            }
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {

                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }
}
