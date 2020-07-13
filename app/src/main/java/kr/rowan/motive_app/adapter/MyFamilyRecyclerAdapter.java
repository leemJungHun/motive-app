package kr.rowan.motive_app.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.FamilyMainActivity;
import kr.rowan.motive_app.data.MyFamilyItem;

public class MyFamilyRecyclerAdapter extends RecyclerView.Adapter<MyFamilyRecyclerAdapter.ViewHolder> {
    private ArrayList<MyFamilyItem> mData = new ArrayList<>();
    private FamilyMainActivity activity;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public MyFamilyRecyclerAdapter(FamilyMainActivity activity) {
        this.activity = activity;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    @NonNull
    public MyFamilyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_myfamily, parent, false);
        return new MyFamilyRecyclerAdapter.ViewHolder(view);

    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MyFamilyRecyclerAdapter.ViewHolder holder, int position) {

        MyFamilyItem item = mData.get(position);

        //holder.scheduleIcon.setImageDrawable(item.getIcon()) ;
        holder.familyName.setText(item.getMyFamilyName());
        holder.familyImg.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            holder.familyImg.setClipToOutline(true);
        }
        if (!item.getMyFamilyImgUrl().equals("")) {
            FirebaseStorage fs = FirebaseStorage.getInstance();
            StorageReference imagesRef = fs.getReference().child(item.getMyFamilyImgUrl());
            Glide.with(activity)
                    .load(imagesRef)
                    .into(holder.familyImg);
        }
        holder.familySchedule.setTag(item.getMyFamilyId());
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(ArrayList<MyFamilyItem> myFamilyItems) {
        mData.clear();
        mData.addAll(myFamilyItems);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView familyImg;
        TextView familyName;
        TextView familySchedule;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            familyName = itemView.findViewById(R.id.family_name);
            familyImg = itemView.findViewById(R.id.family_img);
            familySchedule = itemView.findViewById(R.id.family_schedule);

            familySchedule.setOnClickListener(v -> activity.moveScheduleFragment(familySchedule.getTag().toString()));
        }
    }
}
