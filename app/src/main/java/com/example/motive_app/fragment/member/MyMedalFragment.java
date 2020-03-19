package com.example.motive_app.fragment.member;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.motive_app.R;
import com.example.motive_app.activity.MemberMainActivity;
import com.example.motive_app.databinding.FragmentMymedalBinding;
import com.example.motive_app.network.VO.UserInfoVO;

import java.util.Objects;

public class MyMedalFragment extends Fragment {
    FragmentMymedalBinding binding;
    UserInfoVO vo;
    public MyMedalFragment(){

    }

    public MyMedalFragment(UserInfoVO vo){
        this.vo = vo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_mymedal, container, false);

        //here data must be an instance of the class MarsDataProvider

        binding.settingMenu.setOnClickListener(onClickListener);

        return binding.getRoot();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == binding.settingMenu) {
                // getActivity()로 MainActivity의 replaceFragment를 불러옵니다.
                ((MemberMainActivity) Objects.requireNonNull(getActivity())).setFragemntCheck(1);
                ((MemberMainActivity) Objects.requireNonNull(getActivity())).setStartFragment(new MyInfoFragment(vo));
            }
        }
    };
}
