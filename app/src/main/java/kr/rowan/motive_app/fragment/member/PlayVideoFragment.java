package kr.rowan.motive_app.fragment.member;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.MemberMainActivity;
import kr.rowan.motive_app.adapter.PlayVideoRecyclerAdapter;
import kr.rowan.motive_app.data.VideoListItem;
import kr.rowan.motive_app.databinding.FragmentPlayVideoBinding;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.UserIdRequest;
import kr.rowan.motive_app.network.dto.UserPhoneRequest;
import kr.rowan.motive_app.network.vo.MemberInfoVO;
import kr.rowan.motive_app.network.vo.UserInfoVO;
import kr.rowan.motive_app.network.vo.VideoListVO;
import kr.rowan.motive_app.util.MyComparator;
import kr.rowan.motive_app.util.video.VideoPlayerControl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlayVideoFragment extends Fragment {
    private FragmentPlayVideoBinding binding;
    private PlayVideoRecyclerAdapter adapter;
    private ArrayList<VideoListItem> mItem = new ArrayList<>();

    private UserInfoVO vo;
    private MemberInfoVO memberInfoVO;
    private FirebaseStorage fs = FirebaseStorage.getInstance();
    private StorageReference videoRef;
    private ProgressDialog pd;

    private String medalVideo;
    private boolean itemTouch = false;
    private boolean longTouch = false;
    private MemberMainActivity activity;
    private String videoUri;
    private boolean isSelectMedal = false;
    private boolean mNoUploadVideo = false;

    private long then = 0;


    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        vo = (UserInfoVO) args.getSerializable("userInfoVO");
        medalVideo = args.getString("medalVideo");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MemberMainActivity) context;
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if ((enter && nextAnim == R.anim.pull_in_right) || (enter && nextAnim == R.anim.pull_in_left)) {
            Animation anim = AnimationUtils.loadAnimation(getContext(), nextAnim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //retrofit
                    //통신
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(HttpRequestService.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    HttpRequestService httpRequestService = retrofit.create(HttpRequestService.class);

                    UserIdRequest request = new UserIdRequest();
                    request.setUserId(vo.getId());

                    UserPhoneRequest userPhoneRequest = new UserPhoneRequest();
                    userPhoneRequest.setUserPhone(vo.getId());

                    httpRequestService.getVideoListRequest(request).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            if (response.body() != null) {
                                String result = response.body().get("result").toString();
                                if (!result.equals("null")) {
                                    JsonObject jsonObject = response.body();
                                    JsonArray jsonArray = jsonObject.getAsJsonArray("result");

                                    Type listType = new TypeToken<List<VideoListVO>>() {
                                    }.getType();
                                    List<VideoListVO> myList = new Gson().fromJson(jsonArray, listType);

                                    Collections.sort(myList, new MyComparator());

                                    /*if(myList.size()==0){
                                        showProgress("동영상 재생 준비 중");
                                        videoRef = fs.getReference().child(videoListVO.getFileUrl());
                                        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            // Got the download URL for 'users/me/profile.png'
                                            hideProgress();
                                            Log.d("Success", uri.toString());
                                            // 메달을 선택 해야하는 경우 비디오 보고 후에 메달 선택 페이지로 넘어가야함.
                                            isSelectMedal = true;
                                            activity.fileUrl = videoListVO.getFileUrl();
                                            activity.videoIdx = String.valueOf(videoListVO.getIdx());
                                            videoUri = uri.toString();
                                            activity.playVideoVisibility(true);
                                            activity.videoHolder.addCallback(callback);

                                            //activity.playVideo(uri.toString(), Integer.toString(videoListVO.getIdx()));
                                        }).addOnFailureListener(exception -> {
                                            // Handle any errors
                                            hideProgress();
                                            Log.d("onFailure", exception.toString());
                                        });
                                    }*/

                                    int videoIndex=0;

                                    if(myList.size()!=0){
                                        Calendar nowCal = Calendar.getInstance();
                                        Calendar registerCal = Calendar.getInstance();
                                        Log.d("RegisterDate",myList.get(0).getRegistrationDate());
                                        String[] registerDate = myList.get(0).getRegistrationDate().split(" ");
                                        String[] registerDates = registerDate[0].split("-");
                                        registerCal.set(Calendar.DATE,Integer.parseInt(registerDates[2]));
                                        if(nowCal.get(Calendar.YEAR)==Integer.parseInt(registerDates[0])&&(nowCal.get(Calendar.MONTH)+1)==Integer.parseInt(registerDates[1])){
                                            if(nowCal.get(Calendar.DATE)-14>registerCal.get(Calendar.DATE)){
                                                videoIndex = (int)(Math.random() * myList.size());
                                            }
                                        }
                                    }

                                    for (int index = 0; index < myList.size(); index++) {
                                        final VideoListVO videoListVO = myList.get(index);
                                        if (index == videoIndex && medalVideo.equals("Y")) {
                                            showProgress("동영상 재생 준비 중");
                                            videoRef = fs.getReference().child(videoListVO.getFileUrl());
                                            videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                // Got the download URL for 'users/me/profile.png'
                                                hideProgress();
                                                Log.d("Success", uri.toString());
                                                // 메달을 선택 해야하는 경우 비디오 보고 후에 메달 선택 페이지로 넘어가야함.
                                                isSelectMedal = true;
                                                activity.fileUrl = videoListVO.getFileUrl();
                                                activity.videoIdx = String.valueOf(videoListVO.getIdx());
                                                videoUri = uri.toString();
                                                activity.playVideoVisibility(true);
                                                activity.videoHolder.addCallback(callback);


                                                //activity.playVideo(uri.toString(), Integer.toString(videoListVO.getIdx()));
                                            }).addOnFailureListener(exception -> {
                                                // Handle any errors
                                                hideProgress();
                                                Log.d("onFailure", exception.toString());
                                            });
                                        }
                                        Log.d("videoIdx" + index, " " + videoListVO.getIdx());
                                        Log.d("getFileUrl" + index, " " + videoListVO.getFileUrl());
                                        Log.d("getThumbnailUrl" + index, " " + videoListVO.getThumbnailUrl());
                                        VideoListItem videoListItem = new VideoListItem();
                                        videoListItem.setFileName(videoListVO.getFileName());
                                        videoListItem.setFileUrl(videoListVO.getFileUrl());
                                        videoListItem.setRegisterId(videoListVO.getRegisterId());
                                        videoListItem.setRegisterName(videoListVO.getRegisterName());
                                        videoListItem.setRegisterRelationship(videoListVO.getRegisterRelationship());
                                        videoListItem.setRegistrationDate(videoListVO.getRegistrationDate());
                                        videoListItem.setThumbnailUrl(videoListVO.getThumbnailUrl());
                                        if (videoListVO.getProfileImageUrl() != null) {
                                            videoListItem.setRegisterProfile(videoListVO.getProfileImageUrl());
                                        }
                                        mItem.add(videoListItem);
                                    }
                                }else {
                                    mNoUploadVideo = true;
                                }
                            }else{
                                mNoUploadVideo = true;
                            }
                            httpRequestService.userPhoneRequest(userPhoneRequest).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                                    if (response.body() != null) {
                                        if (!response.body().get("memberInfoVO").toString().equals("null")) {
                                            Gson gson = new Gson();
                                            memberInfoVO = gson.fromJson(response.body().get("memberInfoVO").toString(), MemberInfoVO.class);
                                            Log.d("mNoUploadVideo",mNoUploadVideo+" ");
                                            for (int i = 0; i < 5; i++) {
                                                VideoListItem videoListItem = new VideoListItem();
                                                if(mNoUploadVideo && medalVideo.equals("Y")){
                                                    Log.d("default 영상 재생"," ");
                                                    showProgress("동영상 재생 준비 중");
                                                    int randomIdx = (int)(Math.random()*4)+1;
                                                    String startFileUrl = "default/videos/default_" + randomIdx + ".mp4";
                                                    videoRef = fs.getReference().child(startFileUrl);
                                                    videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                        // Got the download URL for 'users/me/profile.png'
                                                        hideProgress();
                                                        Log.d("Success", uri.toString());
                                                        // 메달을 선택 해야하는 경우 비디오 보고 후에 메달 선택 페이지로 넘어가야함.
                                                        isSelectMedal = true;
                                                        activity.fileUrl = startFileUrl;
                                                        activity.videoIdx = String.valueOf(randomIdx);
                                                        videoUri = uri.toString();
                                                        activity.playVideoVisibility(true);
                                                        activity.videoHolder.addCallback(callback);
                                                        //activity.playVideo(uri.toString(), Integer.toString(videoListVO.getIdx()));
                                                    }).addOnFailureListener(exception -> {
                                                        // Handle any errors
                                                        hideProgress();
                                                        Log.d("onFailure", exception.toString());
                                                    });
                                                }
                                                videoListItem.setFileName("어르신을 응원합니다");
                                                videoListItem.setFileUrl("default/videos/default_" + i + ".mp4");
                                                videoListItem.setRegisterId(" ");
                                                videoListItem.setRegisterName(memberInfoVO.getOrganizationName());
                                                videoListItem.setRegisterRelationship("기관");
                                                videoListItem.setRegistrationDate(" ");
                                                videoListItem.setThumbnailUrl("default/videos/default_" + i + ".mp4");
                                                videoListItem.setRegisterProfile("images/superbrain_icon.jpg");
                                                mItem.add(videoListItem);
                                            }
                                            binding.loadingTxtView.setVisibility(View.INVISIBLE);
                                            adapter = new PlayVideoRecyclerAdapter(mItem, (MemberMainActivity) getActivity(), getContext());
                                            binding.rvVideoList.setAdapter(adapter);
                                            binding.rvVideoList.setLayoutManager(new LinearLayoutManager(getContext()));
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                        }
                    });

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            return anim;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_play_video, container, false);

        binding.rvVideoList.addOnScrollListener(videoScrollListener);
        binding.rvVideoList.addOnItemTouchListener(videoItemTouchListener);

        //here data must be an instance of the class MarsDataProvider
        return binding.getRoot();

    }

    private void showProgress(String msg) {
        if (pd == null) {                  // 객체를 1회만 생성한다.
            pd = new ProgressDialog(getContext());  // 생성한다.
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

    private RecyclerView.OnItemTouchListener videoItemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            if (MotionEvent.ACTION_UP == e.getAction() && itemTouch) {
                Log.d("then"," " + - then);
                Log.d("currentTimeMillis"," " + (System.currentTimeMillis() - then));
                longTouch = (System.currentTimeMillis() - then) < 250 ;

                if (longTouch){
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child != null) {
                        int pos = rv.getChildAdapterPosition(child);
                        if (pos != -1) {
                            VideoListItem selectedItem = adapter.selectedVideoInfo(pos);
                            showProgress("응원 영상을 불러오는 중입니다......");
                            videoRef = fs.getReference().child(selectedItem.getFileUrl());
                            videoRef.getDownloadUrl().addOnCompleteListener(task -> {
                                if (task.isComplete()) {
                                    Log.e("task.isComplete()", task.isComplete() + "");
                                    Log.e("task.isComplete()", task.isSuccessful() + "");
                                }
                            });


                            //MediaController mediaController = new MediaController(activity);
                            //mediaController.setAnchorView(activity.binding.cheerUpVideoView);
                            videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                videoUri = uri.toString();
                                activity.playVideoVisibility(true);
                                activity.videoHolder.addCallback(callback);

                                // Got the download URL for 'users/me/profile.png'
                                Log.d("Success", uri.toString());
/*                            activity.binding.cheerUpVideoView.setVisibility(View.VISIBLE);
                            activity.binding.bottomNav.setVisibility(View.GONE);
                            activity.binding.memberToolbar.setVisibility(View.GONE);
                            activity.binding.cheerUpVideoView.setMediaController(mediaController);
                            activity.binding.cheerUpVideoView.setVideoURI(uri);
                            activity.binding.cheerUpVideoView.setOnPreparedListener(prepare -> {
                                Log.d("prepare", uri.toString());
                                Log.d("getVideoWidth", prepare.getVideoWidth() + "");
                                Log.d("getVideoHeight", prepare.getVideoHeight() + "");
                                prepare.setOnVideoSizeChangedListener(videoSizeChangedListener);
                                activity.binding.cheerUpVideoView.setOnClickListener(null);
                                activity.binding.cheerUpVideoView.start();
                                hideProgress();
                            });
                            activity.binding.cheerUpVideoView.setOnCompletionListener(complete -> {
                                activity.binding.cheerUpVideoView.setVisibility(View.GONE);
                                activity.binding.bottomNav.setVisibility(View.VISIBLE);
                                activity.binding.memberToolbar.setVisibility(View.VISIBLE);
                            });*/
                                //activity.playVideo(uri.toString(), " ");
                            }).addOnFailureListener(exception -> {
                                // Handle any errors
                                hideProgress();
                                Log.d("onFailure", exception.toString());
                            });
                        }
                    }


                }
            } else if (MotionEvent.ACTION_DOWN == e.getAction()) {
                itemTouch = true;
                then = System.currentTimeMillis();
                Log.d("then",then+" ");
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



    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            activity.videoPlayer = new MediaPlayer();

            activity.mediaController = new MediaController(activity);
            activity.videoPlayer.setDisplay(activity.videoHolder);
            activity.mediaController.setAnchorView(activity.binding.cheerUpVideoView);

            try {
                activity.videoPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                activity.videoPlayer.setDataSource(videoUri);
                activity.videoPlayer.prepareAsync();
                activity.binding.cheerUpVideoView.requestFocus();
                activity.videoPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
                activity.videoPlayer.setOnPreparedListener(mp -> {
                    activity.binding.cheerUpVideoView.setOnClickListener(v -> {
                        if (activity.mediaController.isShowing()) {
                            activity.mediaController.hide();
                        } else {
                            activity.mediaController.show();
                        }
                    });
                    activity.mediaController.setMediaPlayer(new VideoPlayerControl(activity.videoPlayer));
                    activity.videoPlayer.start();
                    hideProgress();
                });

                activity.videoPlayer.setOnCompletionListener(complete -> {
                    //activity.videoHolder.removeCallback(callback);
                    activity.videoPlayer.stop();
                    activity.videoPlayer.release();
                    activity.videoPlayer = null;
                    activity.playVideoVisibility(false);
                    activity.setFullScreen(false, isSelectMedal);

                });
                activity.videoPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        activity.playVideoVisibility(false);
    }

    private void releaseMediaPlayer() {
        if (activity.videoPlayer != null) {
            activity.videoPlayer.release();
            activity.videoPlayer = null;
        }
    }

    private RecyclerView.OnScrollListener videoScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            itemTouch = false;
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = (player, width, height) -> {
        Log.e("PlayVideoFragment", "width = " + width + " | height = " + height);
        if (width > height) {
            // 가로모드로 변경
            activity.setFullScreen(true, false);
        } else {
            // 세로모드 유지
            activity.setFullScreen(false, false);
        }
        activity.playVideoVisibility(true);
    };


}
