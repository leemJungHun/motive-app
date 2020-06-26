package com.example.motive_app.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_app.R;
import com.example.motive_app.adapter.ExampleVideoRecyclerAdapter;
import com.example.motive_app.data.VideoListItem;
import com.example.motive_app.databinding.ActivityExampleVideoBinding;
import com.example.motive_app.util.video.VideoPlayerControl;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ExampleVideoListActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityExampleVideoBinding binding;
    private ArrayList<VideoListItem> mItem = new ArrayList<>();
    private ProgressDialog pd;
    private FirebaseStorage fs = FirebaseStorage.getInstance();
    private StorageReference videoRef;
    ExampleVideoRecyclerAdapter adapter;

    private boolean itemTouch;

    private SurfaceHolder mVideoHolder;
    private MediaPlayer mVideoPlayer;
    private MediaController mVideoController;

    private String mVideoUri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_example_video);

        binding.backArrow.setOnClickListener(this);

        //가족
        VideoListItem exampleItem1 = new VideoListItem();
        exampleItem1.setFileName("예시 영상");
        exampleItem1.setFileUrl("default/videos/default_0.mp4");
        exampleItem1.setRegisterId("예시");
        exampleItem1.setRegisterName("동기강화 앱");
        exampleItem1.setRegisterRelationship("예시 영상");
        exampleItem1.setRegistrationDate("");
        exampleItem1.setThumbnailUrl("default/thumbnails/default_0.jpg");
        exampleItem1.setRegisterProfile("예시");

        mItem.add(exampleItem1);

        //남자어린이
        VideoListItem exampleItem2 = new VideoListItem();
        exampleItem2.setFileName("예시 영상");
        exampleItem2.setFileUrl("default/videos/default_1.mp4");
        exampleItem2.setRegisterId("예시");
        exampleItem2.setRegisterName("동기강화 앱");
        exampleItem2.setRegisterRelationship("예시 영상");
        exampleItem2.setRegistrationDate("");
        exampleItem2.setThumbnailUrl("default/thumbnails/default_1.jpg");
        exampleItem2.setRegisterProfile("예시");

        mItem.add(exampleItem2);

        //여자어린이
        VideoListItem exampleItem3 = new VideoListItem();
        exampleItem3.setFileName("예시 영상");
        exampleItem3.setFileUrl("default/videos/default_2.mp4");
        exampleItem3.setRegisterId("예시");
        exampleItem3.setRegisterName("동기강화 앱");
        exampleItem3.setRegisterRelationship("예시 영상");
        exampleItem3.setRegistrationDate("");
        exampleItem3.setThumbnailUrl("default/thumbnails/default_2.jpg");
        exampleItem3.setRegisterProfile("예시");

        mItem.add(exampleItem3);

        //성인여성
        VideoListItem exampleItem4 = new VideoListItem();
        exampleItem4.setFileName("예시 영상");
        exampleItem4.setFileUrl("default/videos/default_3.mp4");
        exampleItem4.setRegisterId("예시");
        exampleItem4.setRegisterName("동기강화 앱");
        exampleItem4.setRegisterRelationship("예시 영상");
        exampleItem4.setRegistrationDate("");
        exampleItem4.setThumbnailUrl("default/thumbnails/default_3.jpg");
        exampleItem4.setRegisterProfile("예시");

        mItem.add(exampleItem4);

        //성인여성
        VideoListItem exampleItem5 = new VideoListItem();
        exampleItem5.setFileName("예시 영상");
        exampleItem5.setFileUrl("default/videos/default_4.mp4");
        exampleItem5.setRegisterId("예시");
        exampleItem5.setRegisterName("동기강화 앱");
        exampleItem5.setRegisterRelationship("예시 영상");
        exampleItem5.setRegistrationDate("");
        exampleItem5.setThumbnailUrl("default/thumbnails/default_4.jpg");
        exampleItem5.setRegisterProfile("예시");

        mItem.add(exampleItem5);

        mVideoHolder = binding.exampleVideoView.getHolder();
        binding.exampleVideoView.setOnClickListener(null);

        binding.rvVideoList.addOnScrollListener(scrollListener);
        binding.rvVideoList.addOnItemTouchListener(itemTouchListener);


        adapter = new ExampleVideoRecyclerAdapter(mItem, this);
        binding.rvVideoList.setAdapter(adapter);
        binding.rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

    }

    private RecyclerView.OnItemTouchListener itemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            if (MotionEvent.ACTION_UP == e.getAction() && itemTouch) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    int pos = rv.getChildAdapterPosition(child);
                    if(pos != -1) {
                        showProgress("동영상 재생 준비 중");
                        VideoListItem selectedItem = adapter.selectedVideoInfo(pos);
                        videoRef = fs.getReference().child(selectedItem.getFileUrl());

                        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Got the download URL for 'users/me/profile.png'
                            //hideProgress();
                            Log.d("Success", uri.toString());
                            mVideoUri = uri.toString();
                            binding.exampleVideoView.setVisibility(View.VISIBLE);
                            mVideoHolder.addCallback(callback);
                            //playVideo(uri.toString(), " ");
                        }).addOnFailureListener(exception -> {
                            // Handle any errors
                            hideProgress();
                            Log.d("onFailure", exception.toString());
                        });
                    }
                }
            } else if (MotionEvent.ACTION_DOWN == e.getAction()) {
                itemTouch = true;
            }

            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            itemTouch = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (binding.exampleVideoView.getVisibility() == View.VISIBLE) {
            if (mVideoPlayer != null) {
                mVideoHolder.removeCallback(callback);
                mVideoPlayer.stop();
                mVideoPlayer.release();
                mVideoPlayer = null;
            }
            setFullScreen(false);
            binding.exampleVideoView.setVisibility(View.GONE);
        } else {
            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backArrow) {
            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
    }
    @SuppressLint("SourceLockedOrientationActivity")
    public void setFullScreen(boolean full) {

        ViewGroup.LayoutParams params = binding.exampleVideoView.getLayoutParams();

        if (full) {
            // 전체화면 만들 때 가로모드인 경우 폰 픽셀이 아닌 동영상 픽셀에 크기를 맞춘다.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            //int height = ViewGroup.LayoutParams.MATCH_PARENT;

            params.height = ViewGroup.LayoutParams.MATCH_PARENT;

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mVideoPlayer = new MediaPlayer();

            mVideoController = new MediaController(ExampleVideoListActivity.this);
            mVideoPlayer.setDisplay(mVideoHolder);
            mVideoController.setAnchorView(binding.exampleVideoView);

            try {
                mVideoPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mVideoPlayer.setDataSource(mVideoUri);
                mVideoPlayer.prepareAsync();
                binding.exampleVideoView.requestFocus();
                mVideoPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
                mVideoPlayer.setOnPreparedListener(mp -> {
                    binding.exampleVideoView.setOnClickListener(v -> {
                        if (mVideoController.isShowing()) {
                            mVideoController.hide();
                        } else {
                            mVideoController.show();
                        }
                    });
                    mVideoController.setMediaPlayer(new VideoPlayerControl(mVideoPlayer));
                    mVideoPlayer.start();
                    hideProgress();
                });

                mVideoPlayer.setOnCompletionListener(complete -> {
                    mVideoHolder.removeCallback(callback);
                    mVideoPlayer.stop();
                    mVideoPlayer.release();
                    mVideoPlayer = null;
                    binding.exampleVideoView.setVisibility(View.GONE);
                    setFullScreen(false);

                });
                mVideoPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = (player, width, height) -> {
        Log.e("PlayVideoFragment", "width = " + width + " | height = " + height);
        if (width > height) {
            // 가로모드로 변경
            setFullScreen(true);
        } else {
            // 세로모드 유지
            setFullScreen(false);
        }
        binding.exampleVideoView.setVisibility(View.VISIBLE);
    };



    private void showProgress(String msg) {
        if (pd == null) {                  // 객체를 1회만 생성한다.
            pd = new ProgressDialog(this);  // 생성한다.
            pd.setCancelable(false);        // 백키로 닫는 기능을 제거한다.
        }

        pd.setMessage(msg); // 원하는 메시지를 세팅한다.
        pd.show();          // 화면에 띠워라
    }

    // 프로그레스 다이얼로그 숨기기
    private void hideProgress() {
        if (pd != null && pd.isShowing()) { // 닫는다 : 객체가 존재하고, 보일때만
            pd.dismiss();
        }
    }



}
