package com.example.motive_app.fragment.family;

import android.content.Context;
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
import com.example.motive_app.network.dto.GetParentsInfoRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.vo.FamilyInfoVO;
import com.example.motive_app.network.vo.MyFamilyListVO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
    private FamilyMainActivity activity;


    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        vo = (FamilyInfoVO) args.getSerializable("familyInfoVO");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (FamilyMainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_my_family, container, false);


        adapter = new MyFamilyRecyclerAdapter((FamilyMainActivity) getActivity());
        binding.rvFamily.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFamily.setAdapter(adapter);

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
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("result");

                    for (JsonElement element : jsonArray) {
                        MyFamilyListVO myFamilyListVO = gson.fromJson(element, MyFamilyListVO.class);
                        MyFamilyItem myFamilyItem = new MyFamilyItem();
                        if (myFamilyListVO.getProfileImageUrl() != null) {
                            myFamilyItem.setMyFamilyImgUrl(myFamilyListVO.getProfileImageUrl());
                        } else {
                            myFamilyItem.setMyFamilyImgUrl("");
                        }
                        myFamilyItem.setMyFamilyId(myFamilyListVO.getId());
                        myFamilyItem.setMyFamilyName(myFamilyListVO.getName());
                        mItem.add(myFamilyItem);
                    }

                    adapter.updateData(mItem);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });

        //here data must be an instance of the class MarsDataProvider
        return binding.getRoot();

    }
}
