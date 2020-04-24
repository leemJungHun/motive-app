package com.example.motive_app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.motive_app.R;
import com.example.motive_app.activity.ExampleVideoListActivity;
import com.example.motive_app.data.VideoListItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ExampleVideoRecyclerAdapter extends RecyclerView.Adapter<ExampleVideoRecyclerAdapter.ViewHolder> {
    private ArrayList<VideoListItem> mData;
    private ExampleVideoListActivity activity;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public ExampleVideoRecyclerAdapter(ArrayList<VideoListItem> list, ExampleVideoListActivity activity) {
        mData = list;
        this.activity = activity;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    @NonNull
    public ExampleVideoRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_play_video, parent, false);

        return new ExampleVideoRecyclerAdapter.ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ExampleVideoRecyclerAdapter.ViewHolder holder, int position) {

        VideoListItem item = mData.get(position);

        FirebaseStorage fs = FirebaseStorage.getInstance();

        if (!item.getThumbnailUrl().equals("")) {
            StorageReference thumbnail = fs.getReference().child(item.getThumbnailUrl());
            Log.d("thumbnail", thumbnail.toString());
            Glide.with(activity)
                    .load(thumbnail)
                    .into(holder.videoThumbnail);
        }

        String nameStr = item.getRegisterName() + "(" + item.getRegisterRelationship() + ")";
        holder.uploaderName.setText(nameStr);
        holder.uploadeTitle.setText(item.getFileName());
        holder.uploadeTime.setText(item.getRegistrationDate());
        holder.uploaderImg.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            holder.uploaderImg.setClipToOutline(true);
        }
        if (item.getRegisterProfile() != null) {
            StorageReference imagesRef = fs.getReference().child(item.getRegisterProfile());
            Log.d("imagesRef", imagesRef.toString());
            Glide.with(activity)
                    .load(imagesRef)
                    .into(holder.uploaderImg);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public VideoListItem selectedVideoInfo(int pos) {
        return mData.get(pos);
    }

    public void updateData(ArrayList<VideoListItem> videoListItems) {
        mData.clear();
        mData.addAll(videoListItems);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        ImageView startBtn;
        ImageView uploaderImg;
        TextView uploaderName;
        TextView uploadeTime;
        TextView uploadeTitle;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            videoThumbnail = itemView.findViewById(R.id.video_main);
            startBtn = itemView.findViewById(R.id.video_start);
            uploaderImg = itemView.findViewById(R.id.uploader_img);
            uploaderName = itemView.findViewById(R.id.uploader_name);
            uploadeTime = itemView.findViewById(R.id.upload_time);
            uploadeTitle = itemView.findViewById(R.id.upload_title);

        }
    }
}
