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
import com.example.motive_app.activity.MemberMainActivity;
import com.example.motive_app.data.VideoListItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PlayVideoRecyclerAdapter extends RecyclerView.Adapter<PlayVideoRecyclerAdapter.ViewHolder> {
    private ArrayList<VideoListItem> mData = null ;
    private MemberMainActivity activity;
    private FirebaseStorage fs = FirebaseStorage.getInstance();
    private StorageReference videoRef;
    private ProgressDialog pd;
    private Context context;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public PlayVideoRecyclerAdapter(ArrayList<VideoListItem> list, MemberMainActivity activity, Context context) {
        mData = list ;
        this.activity = activity ;
        this.context =context;
    }
    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public PlayVideoRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        assert inflater != null;
        View view = inflater.inflate(R.layout.item_play_video, parent, false) ;
        PlayVideoRecyclerAdapter.ViewHolder vh = new PlayVideoRecyclerAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(PlayVideoRecyclerAdapter.ViewHolder holder, int position) {

        VideoListItem item = mData.get(position) ;

        FirebaseStorage fs = FirebaseStorage.getInstance();

        if(!item.getThumbnailUrl().equals("")){
            StorageReference  thumbnail= fs.getReference().child(item.getThumbnailUrl());
            Log.d("thumbnail",thumbnail.toString());
            Glide.with(activity)
                    .load(thumbnail)
                    .into(holder.videoThumbnail);
        }

        holder.uploaderName.setText(item.getRegisterName()) ;
        holder.uploadeTitle.setText(item.getFileName());
        holder.uploadeTime.setText(item.getRegistrationDate());
        holder.uploaderImg.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            holder.uploaderImg.setClipToOutline(true);
        }
        if(item.getRegisterProfile() != null){
            StorageReference imagesRef = fs.getReference().child(item.getRegisterProfile());
            Log.d("imagesRef",imagesRef.toString());
            Glide.with(activity)
                    .load(imagesRef)
                    .into(holder.uploaderImg);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public void updateData(ArrayList<VideoListItem> videoListItems) {
        mData.clear();
        mData.addAll(videoListItems);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        ImageView startBtn;
        ImageView uploaderImg;
        TextView uploaderName;
        TextView uploadeTime;
        TextView uploadeTitle;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            videoThumbnail = itemView.findViewById(R.id.video_main);
            startBtn = itemView.findViewById(R.id.video_start);
            uploaderImg = itemView.findViewById(R.id.uploader_img);
            uploaderName = itemView.findViewById(R.id.uploader_name);
            uploadeTime = itemView.findViewById(R.id.upload_time);
            uploadeTitle = itemView.findViewById(R.id.upload_title);

            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress("동영상 재생 준비 중");
                    videoRef = fs.getReference().child(mData.get(getAdapterPosition()).getFileUrl());
                    videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            hideProgress();
                            Log.d("Success",uri.toString());
                            activity.playVideo(uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            hideProgress();
                            Log.d("onFailure",exception.toString());
                        }
                    });
                }
            });
        }

        public void showProgress(String msg) {
            if( pd == null ) {                  // 객체를 1회만 생성한다.
                pd = new ProgressDialog(context);  // 생성한다.
                pd.setCancelable(false);        // 백키로 닫는 기능을 제거한다.
            }

            pd.setMessage(msg); // 원하는 메시지를 세팅한다.
            pd.show();          // 화면에 띠워라
        }

        // 프로그레스 다이얼로그 숨기기
        public void hideProgress(){
            if( pd != null && pd.isShowing() ) { // 닫는다 : 객체가 존재하고, 보일때만
                pd.dismiss();
            }
        }
    }
}
