package com.example.motive_app.fragment.member;

import android.content.Context;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.motive_app.R;
import com.example.motive_app.activity.MemberMainActivity;
import com.example.motive_app.adapter.PlayVideoRecyclerAdapter;
import com.example.motive_app.data.VideoListItem;
import com.example.motive_app.databinding.FragmentMymedalBinding;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.dto.GetAllMedalInfoRequest;
import com.example.motive_app.network.dto.UserIdRequest;
import com.example.motive_app.network.vo.MedalInfoVO;
import com.example.motive_app.network.vo.UserInfoVO;
import com.example.motive_app.network.vo.VideoListVO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyMedalFragment extends Fragment {

    private final String TAG = MyMedalFragment.class.getSimpleName();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    private FragmentMymedalBinding binding;
    private UserInfoVO vo;
    private MemberMainActivity activity;
    private int mScrollViewHeight;
    private int mScreenWidth;
    private int mNewScroll;

    private int goldMedalCnt, silverMedalCnt, blackMedalCnt, totalPoint, currentCnt;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_mymedal, container, false);
        try {
            currentCnt = currentWeekCount(vo.getStartDate());

        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.myNameTxtView.setText(vo.getName());
        binding.simpleMyNameTxtView.setText(vo.getName());


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;

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

        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HttpRequestService httpRequestService = retrofit.create(HttpRequestService.class);

        GetAllMedalInfoRequest request = new GetAllMedalInfoRequest();
        request.setUserId(vo.getId());
        request.setGroupCode(vo.getGroupCode());


        httpRequestService.getAllMedalInfoRequest(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    String result = response.body().get("result").toString();
                    if (!result.equals("null")) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = response.body();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");


                        for (int index = 0; index < jsonArray.size(); index++) {
                            if(index < currentCnt) {
                                final MedalInfoVO medalInfoVO = gson.fromJson(jsonArray.get(index).toString(), MedalInfoVO.class);
                                if (medalInfoVO.getGoldMedalCnt() == 1) {
                                    goldMedalCnt++;
                                    totalPoint += 100;
                                }
                                else if (medalInfoVO.getSilverMedalCnt() == 1) {
                                    silverMedalCnt++;
                                    totalPoint += 100;
                                }
                                else {
                                    blackMedalCnt++;
                                    totalPoint -= 50;
                                }
                            }
                        }

                        binding.goldCntTxtView.setText(String.valueOf(goldMedalCnt));
                        binding.silverCntTxtView.setText(String.valueOf(silverMedalCnt));
                        binding.blackCntTxtView.setText(String.valueOf(blackMedalCnt));
                        binding.totalCntTxtView.setText(totalPoint + "만냥");
                        double achievePercentage = ((double) currentCnt / jsonArray.size()) * 100.0;
                        achievePercentage = Double.parseDouble(String.format(Locale.KOREAN, "%.1f", achievePercentage));

                        binding.myGoalTxtView.setText("달성률 " + achievePercentage + "%");

                        binding.achieveView.getLayoutParams().width = (int) (mScreenWidth * (achievePercentage / 100));
                        int barWidth = binding.barGray.getWidth();
                        binding.achieveBarGreen.getLayoutParams().width = (int) (barWidth *(achievePercentage / 100));

                        binding.simpleGoldCntTxtView.setText(String.valueOf(goldMedalCnt));
                        binding.simpleSilverCntTxtView.setText(String.valueOf(silverMedalCnt));
                        binding.simpleBlackCntTxtView.setText(String.valueOf(blackMedalCnt));
                        binding.simpleTotalCntTxtView.setText(totalPoint + "만냥");

                        Log.e(TAG, "medal info \n gold = " + goldMedalCnt + " \n silver = " + silverMedalCnt + "\n black = " + blackMedalCnt + "\n totalPoint = " + totalPoint);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

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

    private int currentWeekCount(String start) throws Exception {
        Calendar calendar = Calendar.getInstance();
        String[] starts = start.split("-");
        calendar.set(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]) - 1, Integer.parseInt(starts[2]));
        int startDayNum = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e(TAG, "startDayNum = " + startDayNum);

        Date endDate = new Date();

        String today = simpleDateFormat.format(endDate);
        String[] todays = today.split("-");
        calendar.set(Integer.parseInt(todays[0]), Integer.parseInt(todays[1]) - 1, Integer.parseInt(todays[2]));
        int todayNum = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e(TAG, "todayNum = " + todayNum);

        Date startDate = simpleDateFormat.parse(start);
        long diff = endDate.getTime() - startDate.getTime();
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
        diffDays = ((diffDays - todayNum + startDayNum) / 7) + 1;
        Log.e(TAG, "diff days = " + diffDays);
        return diffDays;
    }
}
