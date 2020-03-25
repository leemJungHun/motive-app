package com.example.motive_app.fragment.family;

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
import com.example.motive_app.databinding.FragmentMyFamilyScheduleBinding;
import com.example.motive_app.network.DTO.GetParentsInfoRequest;
import com.example.motive_app.network.DTO.GetUserScheduleRequest;
import com.example.motive_app.network.DTO.UserPhoneRequest;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.VO.FamilyInfoVO;
import com.example.motive_app.network.VO.GroupScheduleVO;
import com.example.motive_app.network.VO.MemberInfoVO;
import com.example.motive_app.network.VO.MyFamilyListVO;
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

public class MyFamilyScheduleFragment extends Fragment implements View.OnClickListener{
    private FragmentMyFamilyScheduleBinding binding;
    private Calendar cal = Calendar.getInstance();
    private Calendar firstCal = Calendar.getInstance();
    private Calendar nowCal = Calendar.getInstance();
    private CalendarRecyclerAdapter adapter;
    private ArrayList<CalendarItem> mItem = new ArrayList<>();
    private ArrayList<MyFamilyListVO> familyListVOS = new ArrayList<>();
    private boolean dayStart = true;
    private int familyIndex=0;
    private String groupCode;
    private ArrayList<String> attendDates = new ArrayList<>();


    private int scheduleIndex=0;

    private boolean isFuture=false;

    private MyFamilyScheduleFragment mFragment=this;

    private MemberInfoVO memberInfoVO;
    private FamilyInfoVO vo;
    private String myFamilyId;
    private HttpRequestService httpRequestService;



    public MyFamilyScheduleFragment(FamilyInfoVO vo) {
        this.vo=vo;
    }

    public MyFamilyScheduleFragment(FamilyInfoVO vo, String myFamilyId) {
        this.vo=vo;
        this.myFamilyId = myFamilyId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_my_family_schedule, container, false);

        binding.tvPrevMonth.setOnClickListener(this);
        binding.tvNextMonth.setOnClickListener(this);
        binding.familyPrev.setOnClickListener(this);
        binding.familyNext.setOnClickListener(this);

        binding.scheduleContentIcon.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            binding.scheduleContentIcon.setClipToOutline(true);
        }

        //cal.add(Calendar.MONTH,+1);
        //cal.add(Calendar.MONTH,-1);

        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

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
                        Log.d("MyFamilyListVO Id"+index, myFamilyListVO.getId());
                        Log.d("MyFamilyListVO Name"+index, myFamilyListVO.getName());
                        familyListVOS.add(myFamilyListVO);
                    }
                    getUserSchedule(false);
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
        //here data must be an instance of the class MarsDataProvider
        return binding.getRoot();

    }

    private void setCalendarView(boolean isUpdate){
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

        binding.scheduleContentView.setVisibility(View.GONE);


        Log.d("attendDates", attendDates.size()+" ");

        for(int i=0;i<attendDates.size();i++){
            String[] scheduleDate = attendDates.get(i).split("-");
            if(Integer.parseInt(scheduleDate[0])==cal.get(Calendar.YEAR)&&Integer.parseInt(scheduleDate[1])==(cal.get(Calendar.MONTH)+1)){
                haveSchedule.add(Integer.parseInt(scheduleDate[2]));
                if(Integer.parseInt(scheduleDate[2])==cal.get(Calendar.DATE)){
                    binding.scheduleContentView.setVisibility(View.VISIBLE);
                }
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
                        addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), true, false, false, isFuture);
                        if(scheduleIndex<haveSchedule.size()-1){
                            scheduleIndex++;
                        }
                    }else {
                        addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, false, false, isFuture);
                    }
                }else{
                    addItem(Integer.toString(i), i == cal.get(Calendar.DAY_OF_MONTH), false, false, false, isFuture);
                }
            }
        }

        if(isUpdate){
            adapter.updateData(mItem);
        }

    }

    private void getUserSchedule(final boolean isUpdate){
        attendDates = new ArrayList<>();
        String setName="";
        if(myFamilyId!=null){
            for(int i=0 ;i<familyListVOS.size();i++){
                if(familyListVOS.get(i).getId().equals(myFamilyId)){
                    setName = familyListVOS.get(i).getName()+"님의 일정";
                    familyIndex = i;
                    myFamilyId=null;
                }
            }
        }else{
            setName = familyListVOS.get(familyIndex).getName()+"님의 일정";
        }


        binding.familyNameBar.setText(setName);
        UserPhoneRequest userPhoneRequest = new UserPhoneRequest();
        userPhoneRequest.setUserPhone(familyListVOS.get(familyIndex).getPhone());
        httpRequestService.userPhoneRequest(userPhoneRequest).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body()!=null){
                    Gson gson = new Gson();
                    memberInfoVO = gson.fromJson(response.body().get("memberInfoVO").toString(), MemberInfoVO.class);
                    Log.d("getGroupCode", " "+ memberInfoVO.getGroupCode());
                    if(memberInfoVO.getGroupCode()!=null){
                        GetUserScheduleRequest getUserScheduleRequest = new GetUserScheduleRequest();
                        getUserScheduleRequest.setUserId(familyListVOS.get(familyIndex).getId());
                        getUserScheduleRequest.setGroupCode(memberInfoVO.getGroupCode());
                        httpRequestService.getUserScheduleRequest(getUserScheduleRequest).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if(response.body()!=null){
                                    Gson gson = new Gson();
                                    JsonObject jsonObject = response.body().getAsJsonObject("result");
                                    JsonArray jsonArray = jsonObject.getAsJsonArray("schedule");

                                    for(int index=0;index<jsonArray.size();index++) {
                                        GroupScheduleVO groupScheduleVO = gson.fromJson(jsonArray.get(index).toString(), GroupScheduleVO.class);
                                        if(groupScheduleVO.getBreakAway().equals("n")||groupScheduleVO.getBreakAway().equals("N")){
                                            String attendDate = groupScheduleVO.getAttendDate().substring(0,10);
                                            attendDates.add(attendDate);
                                        }
                                    }

                                    setCalendarView(isUpdate);

                                    adapter = new CalendarRecyclerAdapter(mItem,mFragment, "family");
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
    }

    private void addItem(String day, boolean isSelect, boolean haveSchedule, boolean haveGold, boolean haveSilver, boolean isFuture) {
        CalendarItem item = new CalendarItem();

        item.setDay(day);
        item.setSelect(isSelect);
        item.setSchedule(haveSchedule);
        item.setHaveGoldMedal(haveGold);
        item.setHaveGoldMedal(haveSilver);
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
            setCalendarView(true);
        }else if(v==binding.tvNextMonth){
            cal.add(Calendar.MONTH,+1);
            cal.set(Calendar.DATE,1);
            firstCal.add(Calendar.MONTH,+1);
            setCalendarView(true);
        }else if(v==binding.familyPrev){
            if(familyIndex!=0){
                familyIndex--;
            }
            getUserSchedule(true);
        }else if(v==binding.familyNext){
            if(familyIndex<familyListVOS.size()-1) {
                familyIndex++;
            }
            getUserSchedule(true);
        }
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