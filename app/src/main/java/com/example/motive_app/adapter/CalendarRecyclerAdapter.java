package com.example.motive_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_app.R;
import com.example.motive_app.data.CalendarItem;
import com.example.motive_app.databinding.ItemCalendarBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder> {
    private ArrayList<CalendarItem> mData = new ArrayList<>();
    private Context context;

    @Override
    @NonNull
    public CalendarRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemCalendarBinding binding = ItemCalendarBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CalendarRecyclerAdapter.ViewHolder(binding);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull CalendarRecyclerAdapter.ViewHolder holder, int position) {
        ItemCalendarBinding binding = holder.binding;


        CalendarItem item = mData.get(position);

        binding.tvDate.setText(item.getDay());
        binding.tvBack.setVisibility(item.isSelect() ? View.VISIBLE : View.GONE);
        binding.schedule.setVisibility(item.isSchedule() ? View.VISIBLE : View.GONE);


        if(position % 7 == 0) {
            binding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.sunday));
        } else if(position % 7 == 6) {
            binding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.saturday));
        } else {
            binding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.other_day));
        }

        if (!item.isFuture()) {
            binding.schedule.setBackground(item.isSelect() ? context.getResources().getDrawable(R.drawable.schedule_point_select) : context.getResources().getDrawable(R.drawable.schedule_point_gray));
        } else {
            binding.schedule.setBackground(item.isSelect() ? context.getResources().getDrawable(R.drawable.schedule_point_select) : context.getResources().getDrawable(R.drawable.schedule_point));
        }

        if(item.isThisMonth()) {
            binding.tvDate.setAlpha(1.0f);
        } else {
            binding.tvDate.setAlpha(0.4f);
        }

        if (item.isHaveGoldMedal()) {
            binding.medalBack.setBackground(context.getResources().getDrawable(R.drawable.medal_gold_back));
            binding.medalBack.setVisibility(View.VISIBLE);
        } else if (item.isHaveSilverMedal()) {
            binding.medalBack.setBackground(context.getResources().getDrawable(R.drawable.medal_silver_back));
            binding.medalBack.setVisibility(View.VISIBLE);
        } else {
            binding.medalBack.setVisibility(View.GONE);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public CalendarItem updateItem(int pos, int prevPos) {
        CalendarItem item = mData.get(pos);
        if (prevPos != -1) {
            CalendarItem prevItem = mData.get(prevPos);
            prevItem.setSelect(false);
            notifyItemChanged(prevPos);
        }
        item.setSelect(true);
        notifyItemChanged(pos);
        return item;
    }

    public void updateData(ArrayList<CalendarItem> calendarItem) {
        mData.clear();
        mData.addAll(calendarItem);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    static class ViewHolder extends RecyclerView.ViewHolder {

        ItemCalendarBinding binding;

        ViewHolder(@NotNull ItemCalendarBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
