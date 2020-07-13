package kr.rowan.motive_app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.MemberMainActivity;
import kr.rowan.motive_app.data.VideoListItem;

public class PlayVideoRecyclerAdapter extends RecyclerView.Adapter<PlayVideoRecyclerAdapter.ViewHolder> {
    private ArrayList<VideoListItem> mData;
    private MemberMainActivity activity;
    private FirebaseStorage fs = FirebaseStorage.getInstance();
    private StorageReference videoRef;
    private ProgressDialog pd;
    private Context context;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public PlayVideoRecyclerAdapter(ArrayList<VideoListItem> list, MemberMainActivity activity, Context context) {
        mData = list;
        this.activity = activity;
        this.context = context;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    @NonNull
    public PlayVideoRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_play_video, parent, false);
        return new PlayVideoRecyclerAdapter.ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull PlayVideoRecyclerAdapter.ViewHolder holder, int position) {

        VideoListItem item = mData.get(position);

        FirebaseStorage fs = FirebaseStorage.getInstance();

        if (!item.getThumbnailUrl().equals("")) {
            StorageReference thumbnail = fs.getReference().child(item.getThumbnailUrl());
            Log.d("thumbnail", thumbnail.toString());
            Glide.with(activity)
                    .load(thumbnail)
                    .thumbnail(0.5f).into(holder.videoThumbnail);
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
                    .thumbnail(0.5f)
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
    public class ViewHolder extends RecyclerView.ViewHolder {
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

/*            startBtn.setOnClickListener(view -> {
                showProgress("동영상 재생 준비 중");
                videoRef = fs.getReference().child(mData.get(getAdapterPosition()).getFileUrl());
                videoRef.getDownloadUrl().addOnCompleteListener(task -> {
                    if(task.isComplete()) {
                        Log.e("task.isComplete()", task.isComplete() + "");
                        Log.e("task.isComplete()", task.isSuccessful() + "");
                    }
                });

                videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Got the download URL for 'users/me/profile.png'
                    hideProgress();
                    Log.d("Success", uri.toString());
                    activity.playVideo(uri.toString(), " ");
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                    hideProgress();
                    Log.d("onFailure", exception.toString());
                });
            });*/
        }

        private void showProgress(String msg) {
            if (pd == null) {                  // 객체를 1회만 생성한다.
                pd = new ProgressDialog(context);  // 생성한다.
                pd.setCancelable(false);        // 백키로 닫는 기능을 제거한다.
            }

            pd.setMessage(msg); // 원하는 메시지를 세팅한다.
            pd.show();          // 화면에 띠워라
        }

        // 프로그레스 다이얼로그 숨기기
        private void hideProgress() {
            if (pd != null && pd.isShowing()) { // 닫는다 : 객체가 존재하고, 보일때만
                pd.dismiss();
            }
        }
    }
}
