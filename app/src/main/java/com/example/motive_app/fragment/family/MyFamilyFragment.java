package com.example.motive_app.fragment.family;

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
import com.example.motive_app.activity.FamilyMainActivity;
import com.example.motive_app.adapter.MyFamilyRecyclerAdapter;
import com.example.motive_app.data.MyFamilyItem;
import com.example.motive_app.databinding.FragmentMyFamilyBinding;
import com.example.motive_app.network.DTO.GetParentsInfoRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.VO.FamilyInfoVO;
import com.example.motive_app.network.VO.MyFamilyListVO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyFamilyFragment extends Fragment {
    private FragmentMyFamilyBinding binding;
    private MyFamilyRecyclerAdapter adapter;
    private ArrayList<MyFamilyItem> mItem = new ArrayList<>();


    private FamilyInfoVO vo;


    public MyFamilyFragment(FamilyInfoVO vo) {
        this.vo=vo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_my_family, container, false);


        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HttpRequestService httpRequestService = retrofit.create(HttpRequestService.class);

        GetParentsInfoRequest request = new GetParentsInfoRequest();
        request.setFamilyId(vo.getId());


        httpRequestService.getParentsInfoRequest(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body()!=null){
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("result");


                    for(int index=0;index<jsonArray.size();index++) {
                        MyFamilyListVO myFamilyListVO = gson.fromJson(jsonArray.get(index).toString(), MyFamilyListVO.class);
                        MyFamilyItem myFamilyItem = new MyFamilyItem();
                        Log.d("MyFamilyListVO Id"+index, myFamilyListVO.getId());
                        Log.d("MyFamilyListVO Name"+index, myFamilyListVO.getName());
                        if(myFamilyListVO.getProfileImageUrl()!=null) {
                            Log.d("MyFamilyListVO ImgUrl" + index, myFamilyListVO.getProfileImageUrl());
                            myFamilyItem.setMyFamilyImgUrl(myFamilyListVO.getProfileImageUrl());
                        }else{
                            myFamilyItem.setMyFamilyImgUrl("");
                        }
                        myFamilyItem.setMyFamilyId(myFamilyListVO.getId());
                        myFamilyItem.setMyFamilyName(myFamilyListVO.getName());
                        mItem.add(myFamilyItem);
                    }

                    adapter = new MyFamilyRecyclerAdapter(mItem, (FamilyMainActivity) getActivity());
                    binding.rvFamily.setAdapter(adapter);
                    binding.rvFamily.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter.notifyDataSetChanged() ;
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
