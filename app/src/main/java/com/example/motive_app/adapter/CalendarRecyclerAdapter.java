package com.example.motive_app.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_app.R;
import com.example.motive_app.data.CalendarItem;
import com.example.motive_app.fragment.family.MyFamilyScheduleFragment;
import com.example.motive_app.fragment.member.ScheduleFragment;

import java.util.ArrayList;

public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder> {
    private ArrayList<CalendarItem> mData = null;
    private TextView preBack;
    private ImageView preScheduleIcon;
    private ScheduleFragment mFragment;
    private MyFamilyScheduleFragment familyFragment;
    private String type;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public CalendarRecyclerAdapter(ArrayList<CalendarItem> list, MyFamilyScheduleFragment fragment, String type) {
        mData = list;
        familyFragment = fragment;
        this.type = type;
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public CalendarRecyclerAdapter(ArrayList<CalendarItem> list, ScheduleFragment fragment, String type) {
        mData = list;
        mFragment = fragment;
        this.type = type;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public CalendarRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        View view = inflater.inflate(R.layout.item_calendar, parent, false);
        CalendarRecyclerAdapter.ViewHolder vh = new CalendarRecyclerAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(CalendarRecyclerAdapter.ViewHolder holder, int position) {

        CalendarItem item = mData.get(position);

        //holder.scheduleIcon.setImageDrawable(item.getIcon()) ;
        holder.day.setText(item.getDay());
        if (item.isSelect()) {
            holder.calendarBack.setVisibility(View.VISIBLE);
            preBack = holder.calendarBack;
        }
        holder.scheduleIcon.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            holder.scheduleIcon.setClipToOutline(true);
        }
        if(item.isSchedule()){
            holder.scheduleIcon.setVisibility(View.VISIBLE);
        }else{
            holder.scheduleIcon.setVisibility(View.GONE);
        }
        if(!item.isFuture()){
            holder.scheduleIcon.setImageResource(R.drawable.calendar_schedule_past);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(ArrayList<CalendarItem> calendarItem) {
        mData.clear();
        mData.addAll(calendarItem);
        if(preBack!=null){
            preBack.setVisibility(View.GONE);
            preBack=null;
        }
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView scheduleIcon;
        TextView day;
        TextView calendarBack;
        ConstraintLayout calendarView;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            day = itemView.findViewById(R.id.tv_date);
            scheduleIcon = itemView.findViewById(R.id.schedule);
            calendarBack = itemView.findViewById(R.id.tv_back);
            calendarView = itemView.findViewById(R.id.calendar_view);

            calendarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(preBack!=null&&!day.getText().toString().equals(" ")){
                        if(preScheduleIcon!=null){
                            if(preScheduleIcon.getTag(R.string.isFuture).equals("true")) {
                                preScheduleIcon.setImageResource(R.drawable.calendar_schedule);
                            }else{
                                preScheduleIcon.setImageResource(R.drawable.calendar_schedule_past);
                            }
                        }
                        preBack.setVisibility(View.GONE);
                        calendarBack.setVisibility(View.VISIBLE);
                        preBack = calendarBack;
                        if(type.equals("member")) {
                            mFragment.selectDay(Integer.parseInt(day.getText().toString()));
                        }else if(type.equals("family")){
                            familyFragment.selectDay(Integer.parseInt(day.getText().toString()));
                        }
                        if(scheduleIcon.getVisibility()==View.VISIBLE){
                            scheduleIcon.setImageResource(R.drawable.calendar_schedule_select);
                            preScheduleIcon=scheduleIcon;
                            if(mData.get(getAdapterPosition()).isFuture()) {
                                preScheduleIcon.setTag(R.string.isFuture, "true");
                                if(type.equals("member")) {
                                    mFragment.onContentView(true,true);
                                }else if(type.equals("family")){
                                    familyFragment.onContentView(true,true);
                                }
                            }else{
                                preScheduleIcon.setTag(R.string.isFuture, "false");
                                if(type.equals("member")) {
                                    mFragment.onContentView(false,true);
                                }else if(type.equals("family")){
                                    familyFragment.onContentView(false,true);
                                }
                            }
                        }else{
                            if(type.equals("member")) {
                                mFragment.onContentView(mData.get(getAdapterPosition()).isFuture(),false);
                            }else if(type.equals("family")){
                                familyFragment.onContentView(mData.get(getAdapterPosition()).isFuture(),false);
                            }
                        }
                    }
                }
            });
        }
    }
}
