package com.example.motive_app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        VideoListItem exampleFamilyItem = new VideoListItem();
        exampleFamilyItem.setFileName("가족");
        exampleFamilyItem.setFileUrl("example/videos/family.mp4");
        exampleFamilyItem.setRegisterId("예시");
        exampleFamilyItem.setRegisterName("동기강화 앱");
        exampleFamilyItem.setRegisterRelationship("가족");
        exampleFamilyItem.setRegistrationDate("");
        exampleFamilyItem.setThumbnailUrl("example/thumbnails/family_snapshot.jpg");
        exampleFamilyItem.setRegisterProfile("예시");

        mItem.add(exampleFamilyItem);

        //남자어린이
        VideoListItem exampleBoyItem = new VideoListItem();
        exampleBoyItem.setFileName("남자어린이");
        exampleBoyItem.setFileUrl("example/videos/boy.mp4");
        exampleBoyItem.setRegisterId("예시");
        exampleBoyItem.setRegisterName("동기강화 앱");
        exampleBoyItem.setRegisterRelationship("남자어린이");
        exampleBoyItem.setRegistrationDate("");
        exampleBoyItem.setThumbnailUrl("example/thumbnails/boy_snapshot.jpg");
        exampleBoyItem.setRegisterProfile("예시");

        mItem.add(exampleBoyItem);

        //여자어린이
        VideoListItem exampleLittleGirlItem = new VideoListItem();
        exampleLittleGirlItem.setFileName("여자어린이");
        exampleLittleGirlItem.setFileUrl("example/videos/little_girl.mp4");
        exampleLittleGirlItem.setRegisterId("예시");
        exampleLittleGirlItem.setRegisterName("동기강화 앱");
        exampleLittleGirlItem.setRegisterRelationship("여자어린이");
        exampleLittleGirlItem.setRegistrationDate("");
        exampleLittleGirlItem.setThumbnailUrl("example/thumbnails/little_girl_snapshot.jpg");
        exampleLittleGirlItem.setRegisterProfile("예시");

        mItem.add(exampleLittleGirlItem);

        //성인여성
        VideoListItem exampleFemaleItem = new VideoListItem();
        exampleFemaleItem.setFileName("성인여성");
        exampleFemaleItem.setFileUrl("example/videos/female.mp4");
        exampleFemaleItem.setRegisterId("예시");
        exampleFemaleItem.setRegisterName("동기강화 앱");
        exampleFemaleItem.setRegisterRelationship("성인여성");
        exampleFemaleItem.setRegistrationDate("");
        exampleFemaleItem.setThumbnailUrl("example/thumbnails/female_snapshot.jpg");
        exampleFemaleItem.setRegisterProfile("예시");

        mItem.add(exampleFemaleItem);

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

                        videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                //hideProgress();
                                Log.d("Success", uri.toString());
                                mVideoUri = uri.toString();
                                binding.exampleVideoView.setVisibility(View.VISIBLE);
                                mVideoHolder.addCallback(callback);
                                //playVideo(uri.toString(), " ");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                hideProgress();
                                Log.d("onFailure", exception.toString());
                            }
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

    public void playVideo(String fileUrl, String videoIdx) {
        Intent intent;
        intent = new Intent(getApplicationContext(), PlayVideoActivity.class);

        intent.putExtra("fileUrl", fileUrl);
        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

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

            params.height = ViewGroup.LayoutParams.MATCH_PARENT;;

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
