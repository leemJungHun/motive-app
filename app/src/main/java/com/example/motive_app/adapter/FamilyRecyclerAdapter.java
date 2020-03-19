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

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.motive_app.R;
import com.example.motive_app.activity.registration.FindFamilyActivity;
import com.example.motive_app.data.FamilyItem;
import com.example.motive_app.data.FamilyFindItem;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class FamilyRecyclerAdapter extends RecyclerView.Adapter<FamilyRecyclerAdapter.ViewHolder> {
    private ArrayList<FamilyFindItem> mData = new ArrayList<FamilyFindItem>() ;
    private ArrayList<FamilyItem> familyList = new ArrayList<>();
    private FindFamilyActivity activity;
    private boolean duplicatedId = false;

    public FamilyRecyclerAdapter(FindFamilyActivity activity) {
        this.activity = activity ;
    }
    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public FamilyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_family, parent, false) ;
        FamilyRecyclerAdapter.ViewHolder vh = new FamilyRecyclerAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(FamilyRecyclerAdapter.ViewHolder holder, int position) {

        FamilyFindItem item = mData.get(position) ;



        holder.familyName.setText(item.getName());
        holder.familyImg.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            holder.familyImg.setClipToOutline(true);
        }

        if(!item.getImageUrl().equals("")){
            FirebaseStorage fs = FirebaseStorage.getInstance();
            StorageReference imagesRef = fs.getReference().child(item.getImageUrl());
            Glide.with(activity)
                    .load(imagesRef)
                    .into(holder.familyImg);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public void addData(FamilyFindItem familyFindItem){
        for(int i=0;i<mData.size();i++){
            if(mData.get(i).getUserId().equals(familyFindItem.getUserId())){
                duplicatedId=true;
            }
        }
        if(!duplicatedId){
            mData.add(familyFindItem);
            FamilyItem familyData = new FamilyItem(familyFindItem.getUserId(),familyFindItem.getRelation());
            familyList.add(familyData);
        }
        duplicatedId=false;
    }

    public void updateData(ArrayList<FamilyFindItem> familyFindItem) {
        mData.clear();
        mData.addAll(familyFindItem);
        notifyDataSetChanged();
    }

    public ArrayList<FamilyItem> getFamilyData(){
        return familyList;
    }

    public void deleteData(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView familyImg ;
        TextView familyName ;
        ImageView closeImg;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            familyImg = itemView.findViewById(R.id.family_profile) ;
            familyName = itemView.findViewById(R.id.family_name) ;
            closeImg = itemView.findViewById(R.id.family_delete) ;

            closeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    deleteData(pos);
                    ((FindFamilyActivity) Objects.requireNonNull(activity)).rvVisible();
                }
            });
        }
    }
}
