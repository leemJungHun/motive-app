package com.example.motive_app.fragment.member;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.motive_app.R;
import com.example.motive_app.adapter.CalendarRecyclerAdapter;
import com.example.motive_app.adapter.RecyclerViewDecoration;
import com.example.motive_app.data.CalendarItem;
import com.example.motive_app.databinding.FragmentScheduleBinding;
import com.example.motive_app.network.DTO.GetUserScheduleRequest;
import com.example.motive_app.network.DTO.UserPhoneRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.VO.GroupScheduleVO;
import com.example.motive_app.network.VO.MedalInfoVO;
import com.example.motive_app.network.VO.MemberInfoVO;
import com.example.motive_app.network.VO.UserInfoVO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScheduleFragment extends Fragment implements View.OnClickListener{
    private FragmentScheduleBinding binding;
    private Calendar cal = Calendar.getInstance();
    private Calendar firstCal = Calendar.getInstance();
    private Calendar nowCal = Calendar.getInstance();
    private CalendarRecyclerAdapter adapter;
    private ArrayList<CalendarItem> mItem = new ArrayList<CalendarItem>();
    private boolean dayStart = true;
    private ArrayList<String> attendDates = new ArrayList<>();
    private ArrayList<String> medalDates = new ArrayList<>();
    private ScheduleFragment mFragment=this;

    private int scheduleIndex=0;
    private int medalIndex=0;

    private boolean isFuture=false;

    private HttpRequestService httpRequestService;

    private UserInfoVO vo;
    private MemberInfoVO memberInfoVO;
    public ScheduleFragment(UserInfoVO vo){
        this.vo = vo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_schedule, container, false);

        binding.tvPrevMonth.setOnClickListener(this);
        binding.tvNextMonth.setOnClickListener(this);

        binding.scheduleContentIcon.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            binding.scheduleContentIcon.setClipToOutline(true);
        }
        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        UserPhoneRequest userPhoneRequest = new UserPhoneRequest();
        userPhoneRequest.setUserPhone(vo.getPhone());

        httpRequestService.userPhoneRequest(userPhoneRequest).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body()!=null){
                    Gson gson = new Gson();
                    memberInfoVO = gson.fromJson(response.body().get("memberInfoVO").toString(), MemberInfoVO.class);
                    Log.d("getGroupCode", " "+ memberInfoVO.getGroupCode());
                    if(memberInfoVO.getGroupCode()!=null){
                        GetUserScheduleRequest getUserScheduleRequest = new GetUserScheduleRequest();
                        getUserScheduleRequest.setUserId(vo.getId());
                        getUserScheduleRequest.setGroupCode(memberInfoVO.getGroupCode());
                        httpRequestService.getUserScheduleRequest(getUserScheduleRequest).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if(response.body()!=null){
                                    Gson gson = new Gson();
                                    JsonObject jsonObject = response.body().getAsJsonObject("result");
                                    JsonArray jsonArray = jsonObject.getAsJsonArray("schedule");
                                    JsonArray medalArray = jsonObject.getAsJsonArray("medalInfo");

                                    for(int index=0;index<jsonArray.size();index++) {
                                        GroupScheduleVO groupScheduleVO = gson.fromJson(jsonArray.get(index).toString(), GroupScheduleVO.class);
                                        if(groupScheduleVO.getBreakAway().equals("n")||groupScheduleVO.getBreakAway().equals("N")){
                                            String attendDate = groupScheduleVO.getAttendDate().substring(0,10);
                                            attendDates.add(attendDate);
                                        }
                                    }

                                    for (int i=0;i<medalArray.size();i++){
                                        MedalInfoVO medalInfoVO = gson.fromJson(medalArray.get(i).toString(), MedalInfoVO.class);
                                        if(medalInfoVO.getBreakAway().equals("n")||medalInfoVO.getBreakAway().equals("N")){
                                            String selectDate = medalInfoVO.getSelectedDate() + "-" + medalInfoVO.getGoldMedalCnt();
                                            medalDates.add(selectDate);
                                        }
                                    }


                                    setCalendarView();

                                    adapter = new CalendarRecyclerAdapter(mItem,mFragment,"member");
                                    binding.rvSchedule.setAdapter(adapter);
                                    binding.rvSchedule.setLayoutManager(new GridLayoutManager(getContext(),7));
                                    binding.rvSchedule.addItemDecoration(new RecyclerViewDecoration(0));
                                    adapter.notifyDataSetChanged() ;
                                }
                            }
                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
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

    public void setCalendarView(){
        firstCal.set(Calendar.DATE,1);

        Log.d("올해 년도", "" + cal.get(Calendar.YEAR));
        Log.d("올해 월", "" + (cal.get(Calendar.MONTH)+1));
        Log.d("이번주 요일", "" + cal.get(Calendar.DAY_OF_WEEK));
        Log.d("오늘 날짜", "" + cal.get(Calendar.DAY_OF_MONTH));
        Log.d("마지막 날짜", "" + cal.getActualMaximum(Calendar.DATE));
        Log.d("첫 요일", "" + firstCal.get(Calendar.DAY_OF_WEEK));

        String yearMonth = cal.get(Calendar.YEAR)+"년 "+(cal.get(Calendar.MONTH)+1)+"월";

        binding.tvCurrentMonth.setText(yearMonth);

        if(mItem!=null){
            mItem = new ArrayList<>();
            dayStart = true;
        }

        ArrayList<Integer> haveSchedule = new ArrayList<>();
        ArrayList<Integer> haveMedal = new ArrayList<>();
        ArrayList<Integer> whatMedal = new ArrayList<>();

        binding.scheduleContentView.setVisibility(View.GONE);

        for(int i=0;i<attendDates.size();i++){
            String[] scheduleDate = attendDates.get(i).split("-");
            if(Integer.parseInt(scheduleDate[0])==cal.get(Calendar.YEAR)&&Integer.parseInt(scheduleDate[1])==(cal.get(Calendar.MONTH)+1)){
                haveSchedule.add(Integer.parseInt(scheduleDate[2]));
                if(Integer.parseInt(scheduleDate[2])==cal.get(Calendar.DATE)){
                    binding.scheduleContentView.setVisibility(View.VISIBLE);
                }
            }
        }

        for(int i=0;i<medalDates.size();i++){
            String[] medalDate = medalDates.get(i).split("-");
            if(Integer.parseInt(medalDate[0])==cal.get(Calendar.YEAR)&&Integer.parseInt(medalDate[1])==(cal.get(Calendar.MONTH)+1)){
                haveMedal.add(Integer.parseInt(medalDate[2]));
                whatMedal.add(Integer.parseInt(medalDate[3]));
            }
        }



        Collections.sort(haveSchedule);

        if(nowCal.get(Calendar.MONTH)>cal.get(Calendar.MONTH)){
            isFuture=false;
        }else if(nowCal.get(Calendar.MONTH)<cal.get(Calendar.MONTH)){
            isFuture=true;
        }

        for(int i=1;i<cal.getActualMaximum(Calendar.DATE);i++){
            if(i<firstCal.get(Calendar.DAY_OF_WEEK)&&dayStart) {
                addItem(" ", false,false,false,false, false);
            }else{
                if(dayStart){
                    i = 1;
                    dayStart = false;
                }
                if(nowCal.get(Calendar.MONTH)==cal.get(Calendar.MONTH)){
                    if(nowCal.get(Calendar.DATE)<=i){
                        isFuture=true;
                    }else{
                        isFuture=false;
                    }
                }
                if(haveSchedule.size()!=0) {
                    if (i == haveSchedule.get(scheduleIndex)) {
                        if(haveMedal.size()!=0) {
                            if (i == haveMedal.get(medalIndex)) {
                                if (whatMedal.get(medalIndex) == 0) {
                                    addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), true, false, true, isFuture);
                                } else if (whatMedal.get(medalIndex) == 1) {
                                    addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), true, true, false, isFuture);
                                }
                                if (medalIndex < whatMedal.size() - 1) {
                                    medalIndex++;
                                }
                            } else {
                                addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), true, false, false, isFuture);
                            }
                        }else{
                            addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), true, false, false, isFuture);
                        }
                        if(scheduleIndex<haveSchedule.size()-1){
                            scheduleIndex++;
                        }
                    }else {
                        if(haveMedal.size()!=0){
                            if(i==haveMedal.get(medalIndex)){
                                if (whatMedal.get(medalIndex)==0) {
                                    addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, false, true, isFuture);
                                }else if(whatMedal.get(medalIndex)==1){
                                    addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, true, false, isFuture);
                                }
                                if(medalIndex<whatMedal.size()-1){
                                    medalIndex++;
                                }
                            }else{
                                addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, false, false, isFuture);
                            }
                        } else{
                            addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, false, false, isFuture);
                        }
                    }
                }else{
                    if(haveMedal.size()!=0){
                            if(i==haveMedal.get(medalIndex)){
                                if (whatMedal.get(medalIndex)==0) {
                                    addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, false, true, isFuture);
                                }else if(whatMedal.get(medalIndex)==1){
                                    addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, true, false, isFuture);
                                }
                                if(medalIndex<whatMedal.size()-1){
                                    medalIndex++;
                                }
                            }
                    } else{
                        addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, false, false, isFuture);
                    }
                }
            }
        }

    }

    private void addItem(String day, boolean isSelect, boolean haveSchedule, boolean haveGold, boolean haveSilver, boolean isFuture) {
        CalendarItem item = new CalendarItem();

        item.setDay(day);
        item.setSelect(isSelect);
        item.setSchedule(haveSchedule);
        item.setHaveGoldMedal(haveGold);
        item.setHaveSilverMedal(haveSilver);
        item.setFuture(isFuture);

        mItem.add(item);
    }

    @Override
    public void onClick(View v) {
        scheduleIndex=0;
        if(v==binding.tvPrevMonth){
            cal.add(Calendar.MONTH,-1);
            cal.set(Calendar.DATE,1);
            firstCal.add(Calendar.MONTH,-1);
        }else if(v==binding.tvNextMonth){
            cal.add(Calendar.MONTH,+1);
            cal.set(Calendar.DATE,1);
            firstCal.add(Calendar.MONTH,+1);
        }
        setCalendarView();
        adapter.updateData(mItem);

    }

    public void selectDay(int day){
        cal.set(Calendar.DATE, day);
    }

    public void onContentView(boolean isFuture,boolean onView){
        if (isFuture){
            binding.scheduleContentIcon.setImageResource(R.drawable.calendar_schedule);
            binding.scheduleContentText.setText("센터 방문");
        }else{
            binding.scheduleContentIcon.setImageResource(R.drawable.calendar_schedule_past);
            binding.scheduleContentText.setText("센터 방문 완료");
        }

        if (onView){
            binding.scheduleContentView.setVisibility(View.VISIBLE);
        }else{
            binding.scheduleContentView.setVisibility(View.GONE);
        }
    }
}