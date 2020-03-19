package com.example.motive_app.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.motive_app.R;
import com.example.motive_app.activity.VideoSendSelectActivity;
import com.example.motive_app.data.MyFamilyItem;
import com.example.motive_app.network.VO.SelectFamilyVo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UploadSelectRecyclerAdapter extends RecyclerView.Adapter<UploadSelectRecyclerAdapter.ViewHolder> {
    private ArrayList<MyFamilyItem> mData = null ;
    VideoSendSelectActivity activity;
    CheckBox preCheckBox;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public UploadSelectRecyclerAdapter(ArrayList<MyFamilyItem> list, VideoSendSelectActivity activity) {
        mData = list ;
        this.activity = activity ;
    }
    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public UploadSelectRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        assert inflater != null;
        View view = inflater.inflate(R.layout.item_select_family, parent, false) ;
        UploadSelectRecyclerAdapter.ViewHolder vh = new UploadSelectRecyclerAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(UploadSelectRecyclerAdapter.ViewHolder holder, int position) {

        MyFamilyItem item = mData.get(position) ;

        //holder.scheduleIcon.setImageDrawable(item.getIcon()) ;
        holder.familyName.setText(item.getMyFamilyName()) ;
        holder.familyImg.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            holder.familyImg.setClipToOutline(true);
        }
        if(!item.getMyFamilyImgUrl().equals("")){
            FirebaseStorage fs = FirebaseStorage.getInstance();
            StorageReference imagesRef = fs.getReference().child(item.getMyFamilyImgUrl());
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

    public void updateData(ArrayList<MyFamilyItem> myFamilyItems) {
        mData.clear();
        mData.addAll(myFamilyItems);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView familyImg ;
        TextView familyName ;
        CheckBox familySelect;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            familyName = itemView.findViewById(R.id.family_name) ;
            familyImg = itemView.findViewById(R.id.family_img) ;
            familySelect = itemView.findViewById(R.id.family_select);

            familySelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    SelectFamilyVo selectVo = new SelectFamilyVo();
                    selectVo.setSelectId(mData.get(pos).getMyFamilyId());
                    selectVo.setSelectRelation(mData.get(pos).getMyFamilyRelation());
                    if(preCheckBox!=null&&familySelect!=preCheckBox){
                        preCheckBox.setChecked(false);
                    }
                    preCheckBox=familySelect;
                    activity.onSendButton(familySelect.isChecked(),selectVo);
                }
            });
        }
    }
}
