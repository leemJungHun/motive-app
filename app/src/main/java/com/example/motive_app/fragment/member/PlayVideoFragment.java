package com.example.motive_app.fragment.member;

import android.os.Bundle;
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

    private HttpRequestService httpRequestService;


    public PlayVideoFragment(UserInfoVO vo) {
        this.vo=vo;
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
                            VideoListVO videoListVO = gson.fromJson(jsonArray.get(index).toString(), VideoListVO.class);
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
}
