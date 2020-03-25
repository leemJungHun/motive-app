package com.example.motive_app.fragment.family;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.motive_app.R;
import com.example.motive_app.activity.FamilyMainActivity;
import com.example.motive_app.databinding.FragmentUploadVideoBinding;
import com.example.motive_app.dialog.CustomDialog;
import com.example.motive_app.network.VO.FamilyInfoVO;
import com.example.motive_app.util.RealPathUtil;

import java.io.File;
import java.util.Objects;

public class VideoUploadFragment extends Fragment implements View.OnClickListener {
    private FragmentUploadVideoBinding binding;

    private FamilyInfoVO vo;
    private static final int RESULT_OK = -1;
    private String filePath;
    private String size;
    private float FileSize;
    private Context context;


    private ProgressDialog pd;

    //dialog
    CustomDialog dialog;
    private String dialogContent;

    public VideoUploadFragment(FamilyInfoVO vo , Context context){
        this.vo = vo;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_upload_video, container, false);

        binding.uploadVideoBtn.setOnClickListener(this);
        binding.exampleVideoBtn.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.uploadVideoBtn) {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "동영상을 선택하세요."), 0);
        }else if(v==binding.exampleVideoBtn){
            ((FamilyMainActivity) Objects.requireNonNull(getActivity())).exampleVideoOpen();
        }
    }

    //결과 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Log.d("data.getData", data.getData() + "");

                Log.d("path", RealPathUtil.getRealPath(context,data.getData()) + "");
                filePath = RealPathUtil.getRealPath(context,data.getData());
                Log.d("filePath",filePath);
                File selectFIle = new File(filePath);
                if(selectFIle.exists()){
                    FileSize = selectFIle.length()/(float)1024/1024;
                    size = FileSize + "MB";
                }else{
                    size = "File not exist";
                }
                Log.d("selectFile", "size:" + size);
                Log.d("filePath", filePath);
                int playTime = getPlayTime(filePath);
                Log.d("playTime",playTime+" ");
                if(playTime<=60) {
                    ((FamilyMainActivity) Objects.requireNonNull(getActivity())).videoSelectActivty(filePath, Integer.toString(playTime));
                }else{
                    dialogContent="1분 이하 영상만 업로드 가능합니다.";
                    Dialog();
                }
            }
        }
    }

    private int getPlayTime(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Log.d("path",path);
        retriever.setDataSource(path);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong( time );
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        return (int)duration;
    }

    //다이얼로그 클릭이벤트
    public void Dialog() {
        dialog = new CustomDialog(context,
                dialogContent,// 내용
                OkListener); // 왼쪽 버튼 이벤트
        // 오른쪽 버튼 이벤트

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
        dialog.show();
    }

    //다이얼로그 클릭이벤트
    private View.OnClickListener OkListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };
}
