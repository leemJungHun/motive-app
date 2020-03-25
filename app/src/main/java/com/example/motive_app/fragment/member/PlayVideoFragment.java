package com.example.motive_app.fragment.member;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.motive_app.R;
import com.example.motive_app.activity.MemberMainActivity;
import com.example.motive_app.adapter.PlayVideoRecyclerAdapter;
import com.example.motive_app.data.VideoListItem;
import com.example.motive_app.databinding.FragmentPlayVideoBinding;
import com.example.motive_app.network.DTO.UserIdRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.VO.UserInfoVO;
import com.example.motive_app.network.VO.VideoListVO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

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
    private FirebaseStorage fs = FirebaseStorage.getInstance();
    private StorageReference videoRef;
    private ProgressDialog pd;

    private HttpRequestService httpRequestService;

    private String medalVideo;

    public PlayVideoFragment(UserInfoVO vo,String medalVideo) {
        this.vo=vo;
        this.medalVideo = medalVideo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_play_video, container, false);


        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        UserIdRequest request = new UserIdRequest();
        request.setUserId(vo.getId());


        httpRequestService.getVideoListRequest(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body()!=null){
                    String result = Objects.requireNonNull(response).body().get("result").toString();
                    if(!result.equals("null")) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = response.body();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");


                        for (int index = 0; index < jsonArray.size(); index++) {
                            final VideoListVO videoListVO = gson.fromJson(jsonArray.get(index).toString(), VideoListVO.class);
                            if (index==0&&medalVideo.equals("Y")){
                                showProgress("동영상 재생 준비 중");
                                videoRef = fs.getReference().child(videoListVO.getFileUrl());
                                videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Got the download URL for 'users/me/profile.png'
                                        hideProgress();
                                        Log.d("Success",uri.toString());
                                        ((MemberMainActivity) Objects.requireNonNull(getActivity())).playVideo(uri.toString(),Integer.toString(videoListVO.getIdx()));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        hideProgress();
                                        Log.d("onFailure",exception.toString());
                                    }
                                });
                            }
                            Log.d("videoIdx"+index, " "+videoListVO.getIdx());
                            Log.d("getFileUrl"+index, " "+videoListVO.getFileUrl());
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

                        adapter = new PlayVideoRecyclerAdapter(mItem, (MemberMainActivity) getActivity(),getContext());
                        binding.rvVideoList.setAdapter(adapter);
                        binding.rvVideoList.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });


        //here data must be an instance of the class MarsDataProvider
        return binding.getRoot();

    }

    public void showProgress(String msg) {
        if( pd == null ) {                  // 객체를 1회만 생성한다.
            pd = new ProgressDialog(getContext());  // 생성한다.
            pd.setCancelable(false);        // 백키로 닫는 기능을 제거한다.
        }

        pd.setMessage(msg); // 원하는 메시지를 세팅한다.
        pd.show();          // 화면에 띠워라
    }

    // 프로그레스 다이얼로그 숨기기
    public void hideProgress(){
        if( pd != null && pd.isShowing() ) { // 닫는다 : 객체가 존재하고, 보일때만
            pd.dismiss();
        }
    }
}
