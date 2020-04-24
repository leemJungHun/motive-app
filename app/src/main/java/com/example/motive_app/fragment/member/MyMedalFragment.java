package com.example.motive_app.fragment.member;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.motive_app.R;
import com.example.motive_app.activity.MemberMainActivity;
import com.example.motive_app.databinding.FragmentMymedalBinding;
import com.example.motive_app.network.vo.UserInfoVO;

import java.util.ArrayList;
import java.util.Objects;

public class MyMedalFragment extends Fragment {

    private final String TAG = MyMedalFragment.class.getSimpleName();

    private FragmentMymedalBinding binding;
    private UserInfoVO vo;
    private MemberMainActivity activity;
    private int mScrollViewHeight;
    private int mNewScroll;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        vo = (UserInfoVO) args.getSerializable("userInfoVO");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MemberMainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_mymedal, container, false);

        binding.myNameTxtView.setText(vo.getName());
        binding.goldCntTxtView.setText("0");
        binding.silverCntTxtView.setText("0");
        binding.blackCntTxtView.setText("0");
        binding.totalCntTxtView.setText("0");
        binding.myGoalTxtView.setText("달성률 0%");

        binding.simpleMyNameTxtView.setText(vo.getName());
        binding.simpleGoldCntTxtView.setText("0");
        binding.simpleSilverCntTxtView.setText("0");
        binding.simpleBlackCntTxtView.setText("0");
        binding.simpleTotalCntTxtView.setText("0");


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;

        binding.achieveView.getLayoutParams().width = (int) (screenWidth * 0.3);

        binding.medalMapScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            mScrollViewHeight = binding.medalResultLayout.getHeight();
            if(scrollY <= 900) {
                //show achieve card
                binding.medalResultLayout.setY(0 - (mScrollViewHeight * (scrollY / 900.0f)));
                binding.simpleMedalResultLayout.setY(-binding.simpleMedalResultLayout.getHeight());
            } else {
                if(scrollY < 1500) {
                    mNewScroll = scrollY - 900;
                } else {
                    mNewScroll = 600;
                }
                binding.simpleMedalResultLayout.setY(-binding.simpleMedalResultLayout.getHeight() + binding.simpleMedalResultLayout.getHeight() * (mNewScroll / (600.0f)));
            }

        });

        return binding.getRoot();

    }

    /*    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == binding.settingMenu) {
                // getActivity()로 MainActivity의 replaceFragment를 불러옵니다.
                ((MemberMainActivity) Objects.requireNonNull(getActivity())).setFragmentCheck(1);
                Bundle args = new Bundle();
                args.putSerializable("userInfoVO", vo);
                activity.nowFragment = new MyInfoFragment();
                activity.nowFragment.setArguments(args);
                ((MemberMainActivity) Objects.requireNonNull(getActivity())).setStartFragment();
            }
        }
    };*/
}
