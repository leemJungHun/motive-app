package kr.rowan.motive_app.fragment.member;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.FamilyMainActivity;
import kr.rowan.motive_app.activity.MemberMainActivity;
import kr.rowan.motive_app.adapter.CalendarRecyclerAdapter;
import kr.rowan.motive_app.adapter.DayItemDecoration;
import kr.rowan.motive_app.data.CalendarItem;
import kr.rowan.motive_app.databinding.FragmentScheduleBinding;
import kr.rowan.motive_app.fragment.RootScheduleFragment;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.GetUserScheduleRequest;
import kr.rowan.motive_app.network.vo.GroupScheduleVO;
import kr.rowan.motive_app.network.vo.MedalInfoVO;
import kr.rowan.motive_app.network.vo.UserInfoVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScheduleFragment extends RootScheduleFragment implements View.OnClickListener {
    private FragmentScheduleBinding binding;

    private Calendar cal = Calendar.getInstance();
    private Calendar firstCal = Calendar.getInstance();
    private Calendar lastCal = Calendar.getInstance();
    private Calendar nowCal = Calendar.getInstance();
    private CalendarRecyclerAdapter adapter;
    private ArrayList<CalendarItem> mItem = new ArrayList<>();
    private ArrayList<String> attendDates = new ArrayList<>();
    private ArrayList<String> medalDates = new ArrayList<>();
    private boolean dayStart = true;

    private int scheduleIndex = 0;
    private int medalIndex = 0;

    private UserInfoVO vo;

    private FragmentActivity activity;
    private DayItemDecoration dayItemDecoration;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        vo = (UserInfoVO) args.getSerializable("userInfoVO");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MemberMainActivity) {
            this.activity = (MemberMainActivity) context;
        } else if (context instanceof FamilyMainActivity) {
            this.activity = (FamilyMainActivity) context;
        }
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter && nextAnim == R.anim.pull_in_right || enter && nextAnim == R.anim.pull_in_left) {
            Animation anim = AnimationUtils.loadAnimation(activity, nextAnim);
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

                    if (vo.getGroupCode() != null) {
                        GetUserScheduleRequest getUserScheduleRequest = new GetUserScheduleRequest(vo.getId(), vo.getGroupCode());

                        httpRequestService.getUserScheduleRequest(getUserScheduleRequest).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                                if (response.isSuccessful() && response.body() != null) {
                                    Gson gson = new Gson();
                                    JsonObject jsonObject = response.body().getAsJsonObject("result");
                                    JsonArray jsonArray = jsonObject.getAsJsonArray("schedule");
                                    JsonArray medalArray = jsonObject.getAsJsonArray("medalInfo");

                                    for (JsonElement element : jsonArray) {
                                        GroupScheduleVO groupScheduleVO = gson.fromJson(element, GroupScheduleVO.class);
                                        String breakAway = groupScheduleVO.getBreakAway();
                                        if ("N".equals(breakAway) || "n".equals(breakAway)) {
                                            String attendDate = groupScheduleVO.getAttendDate().substring(0, 10);
                                            attendDates.add(attendDate);
                                        }
                                    }

                                    for (JsonElement element : medalArray) {
                                        MedalInfoVO medalInfoVO = gson.fromJson(element, MedalInfoVO.class);
                                        String breakAway = medalInfoVO.getBreakAway();
                                        if ("N".equals(breakAway) || "n".equals(breakAway)) {
                                            String selectDate = medalInfoVO.getSelectedDate() + "-" + medalInfoVO.getGoldMedalCnt();
                                            medalDates.add(selectDate);
                                        }
                                    }
                                    binding.rvSchedule.setBackground(getResources().getDrawable(R.drawable.day_divider));
                                    setCalendarView();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                            }
                        });

                    }
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
                inflater, R.layout.fragment_schedule, container, false);


        binding.tvPrevMonth.setOnClickListener(this);
        binding.tvNextMonth.setOnClickListener(this);

        adapter = new CalendarRecyclerAdapter();
        binding.rvSchedule.setAdapter(adapter);
        dayItemDecoration = new DayItemDecoration(activity, R.drawable.day_divider);
        binding.rvSchedule.addItemDecoration(dayItemDecoration);
        binding.rvSchedule.setLayoutManager(new GridLayoutManager(getContext(), 7));
        //binding.rvSchedule.addItemDecoration(new RecyclerViewDecoration(0));
        binding.rvSchedule.addOnItemTouchListener(itemTouchListener);

        //here data must be an instance of the class MarsDataProvider
        return binding.getRoot();

    }

    private int prevPos = -1;

    private RecyclerView.OnItemTouchListener itemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            if (MotionEvent.ACTION_UP == e.getAction()) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    int pos = rv.getChildAdapterPosition(child);
                    if (pos != -1) {
                        Log.e("prevPos", String.valueOf(prevPos));
                        CalendarItem item = adapter.updateItem(pos, prevPos);
                        onContentView(item.isFuture(), item.isSchedule());
                        prevPos = pos;
                    }
                }
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

    private void setCalendarView() {
        firstCal.set(Calendar.DATE, 1);


        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);


        Log.d("올해 년도", "" + year);
        Log.d("올해 월", "" + (month + 1));
        Log.d("이번주 요일", "" + cal.get(Calendar.DAY_OF_WEEK));
        Log.d("오늘 날짜", "" + day);
        int lastDay = cal.getActualMaximum(Calendar.DATE); // 이번 달 마지막 날짜 == 28 or 29 or 30 or 31
        Log.d("마지막 날짜", "" + lastDay);
        lastCal.set(year, month, lastDay);
        int lastDayNum = lastCal.get(Calendar.DAY_OF_WEEK); // 이번 달 마지막 날짜가 어떤 요일인지, 일요알 = 1, 토요일 = 7
        Log.d("첫 요일", "" + firstCal.get(Calendar.DAY_OF_WEEK));
        String yearMonth = year + "년 " + (month + 1) + "월";

        lastCal.set(year, month - 1, 1);
        int beforeLastDay = lastCal.getActualMaximum(Calendar.DATE); // 이전 달 마지막 날짜 == 28 or 29 or 30 or 31
        lastCal.set(year, month - 1, beforeLastDay);
        int beforeLastDayNum = lastCal.get(Calendar.DAY_OF_WEEK); // 이전 달 마지막 날짜가 어떤 요일인지, 일요알 = 1, 토요일 = 7

        binding.tvCurrentMonth.setText(yearMonth);

        if (mItem != null) {
            mItem = new ArrayList<>();
            dayStart = true;
        }

        ArrayList<Integer> haveSchedule = new ArrayList<>();
        ArrayList<Integer> haveMedal = new ArrayList<>();
        ArrayList<Integer> whatMedal = new ArrayList<>();

        binding.scheduleContentView.setVisibility(View.INVISIBLE);

        for (int i = 0; i < attendDates.size(); i++) {
            String[] scheduleDate = attendDates.get(i).split("-");
            if (Integer.parseInt(scheduleDate[0]) == year && Integer.parseInt(scheduleDate[1]) == (month + 1)) {
                haveSchedule.add(Integer.parseInt(scheduleDate[2]));
                if (Integer.parseInt(scheduleDate[2]) == cal.get(Calendar.DATE)) {
                    binding.scheduleContentView.setVisibility(View.VISIBLE);
                }
            }
        }

        if (medalDates.size()!=0) {
            for (int i = medalDates.size()-1; i >= 0; i--) {
                String[] medalDate = medalDates.get(i).split("-");
                Log.e("medalDate", medalDates.get(i));
                if (Integer.parseInt(medalDate[0]) == year && Integer.parseInt(medalDate[1]) == (month + 1)) {
                    Log.e("whatMedal", Integer.parseInt(medalDate[3]) + " ");
                    haveMedal.add(Integer.parseInt(medalDate[2]));
                    whatMedal.add(Integer.parseInt(medalDate[3]));
                }
            }
        }

        Collections.sort(haveSchedule);

        boolean isFuture = !(nowCal.get(Calendar.MONTH) >= month);


        for (int i = 1; i < lastDay + 1 + (7 - lastDayNum); i++) {
            if (i < firstCal.get(Calendar.DAY_OF_WEEK) && dayStart) {
                mItem.add(new CalendarItem(String.valueOf(beforeLastDay - beforeLastDayNum + i), false, false, false, false, false, false));
            } else if (i > lastDay) {
                mItem.add(new CalendarItem(String.valueOf(i - lastDay), false, false, false, false, false, false));
            } else {
                if (dayStart) {
                    i = 1;
                    dayStart = false;
                }
                if (nowCal.get(Calendar.MONTH) == month) {
                    isFuture = nowCal.get(Calendar.DATE) <= i;
                }

                boolean hasSchedule = false;
                if (haveSchedule.size() != 0) {
                    hasSchedule = i == haveSchedule.get(scheduleIndex);
                    if (hasSchedule) {
                        if (scheduleIndex < haveSchedule.size() - 1) {
                            scheduleIndex++;
                        }
                    }
                }
                boolean hasGold = false;
                boolean hasSilver = false;
                if (haveMedal.size() != 0) {
                    if (i == haveMedal.get(medalIndex)) {
                        Log.e("haveMedal", haveMedal.get(medalIndex) + "");
                        hasGold = whatMedal.get(medalIndex) == 1;
                        hasSilver = whatMedal.get(medalIndex) == 0;
                        if (medalIndex < whatMedal.size() - 1) {
                            medalIndex++;
                        }
                    }
                }
                if (i == day) {
                    prevPos = i + firstCal.get(Calendar.DAY_OF_WEEK) - 2;
                }
                mItem.add(new CalendarItem(String.valueOf(i), i == day, hasSchedule, hasGold, hasSilver, isFuture, true));
            }
        }
        Log.e("medalIndex", medalIndex + "");
        adapter.updateData(mItem);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        scheduleIndex = 0;
        if (v == binding.tvPrevMonth) {
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DATE, 1);
            firstCal.add(Calendar.MONTH, -1);
        } else if (v == binding.tvNextMonth) {
            cal.add(Calendar.MONTH, +1);
            cal.set(Calendar.DATE, 1);
            firstCal.add(Calendar.MONTH, +1);
        }
        medalIndex = 0;
        setCalendarView();

    }


    @Override
    public void selectDay(int day) {
        cal.set(Calendar.DATE, day);
    }

    @Override
    public void onContentView(boolean isFuture, boolean onView) {
        binding.scheduleContentIcon.setImageResource(isFuture ? R.drawable.schedule_point : R.drawable.schedule_point_gray);
        binding.scheduleContentText.setText(isFuture ? "센터 방문" : "센터 방문 완료");
        binding.scheduleContentView.setVisibility(onView ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.rvSchedule.removeItemDecoration(dayItemDecoration);
    }

}