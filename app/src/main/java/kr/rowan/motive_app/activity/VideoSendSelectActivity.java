package kr.rowan.motive_app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.adapter.UploadSelectRecyclerAdapter;
import kr.rowan.motive_app.data.MyFamilyItem;
import kr.rowan.motive_app.databinding.ActivityVideoSelectBinding;
import kr.rowan.motive_app.dialog.CustomDialog;
import kr.rowan.motive_app.dialog.CustomDialog_v5;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.GetParentsInfoRequest;
import kr.rowan.motive_app.network.dto.UploadVideoRequest;
import kr.rowan.motive_app.network.dto.UserIdRequest;
import kr.rowan.motive_app.network.vo.FamilyInfoVO;
import kr.rowan.motive_app.network.vo.MyFamilyListVO;
import kr.rowan.motive_app.network.vo.SelectFamilyVo;
import kr.rowan.motive_app.util.video.VideoCompress;
import kr.rowan.motive_app.util.video.VideoCompressUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoSendSelectActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityVideoSelectBinding binding;
    private UploadSelectRecyclerAdapter adapter;
    private ArrayList<MyFamilyItem> mItem = new ArrayList<>();
    private ProgressDialog pd;

    private String outputDir;
    float FileSize;

    private Uri thumbnailPath;

    private long startTime, endTime;

    //dialog

    private CustomDialog_v5 dialog_v5;
    CustomDialog dialog;
    private String dialogContent;

    //data
    private FamilyInfoVO vo;
    private String filePath;
    private String playTime;
    @SuppressLint("SimpleDateFormat")
    private
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
    private Date now = new Date();

    private Uri uploadUri;
    private String title;
    VideoSendSelectActivity activity;
    HttpRequestService httpRequestService;
    private SelectFamilyVo selectFamilyVo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_select);


        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if(intent.getExtras()!=null) {
            vo = (FamilyInfoVO) intent.getSerializableExtra("familyInfoVO");
            if(vo!=null) {
                Log.d("getName", vo.getName());
                Log.d("getId", vo.getId());
                Log.d("getEmail", vo.getEmail());
                Log.d("getPhone", vo.getPhone());
            }
            filePath = intent.getExtras().getString("filePath");
            playTime = intent.getExtras().getString("playTime");
        }
        activity=this;


        //리사이클러 뷰 셋팅
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
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                if(response.body()!=null){
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("result");


                    for(int index=0;index<jsonArray.size();index++) {
                        MyFamilyListVO myFamilyListVO = gson.fromJson(jsonArray.get(index).toString(), MyFamilyListVO.class);
                        MyFamilyItem myFamilyItem = new MyFamilyItem();
                        Log.d("MyFamilyListVO Id"+index, myFamilyListVO.getId());
                        Log.d("MyFamilyListVO Name"+index, myFamilyListVO.getName());
                        if(myFamilyListVO.getProfileImageUrl()!=null) {
                            Log.d("MyFamilyListVO ImgUrl" + index, myFamilyListVO.getProfileImageUrl());
                            myFamilyItem.setMyFamilyImgUrl(myFamilyListVO.getProfileImageUrl());
                        }else{
                            myFamilyItem.setMyFamilyImgUrl("");
                        }
                        myFamilyItem.setMyFamilyId(myFamilyListVO.getId());
                        myFamilyItem.setMyFamilyName(myFamilyListVO.getName());
                        myFamilyItem.setMyFamilyRelation(myFamilyListVO.getRelation());
                        mItem.add(myFamilyItem);
                    }

                    adapter = new UploadSelectRecyclerAdapter(mItem, activity);
                    binding.rvFamily.setAdapter(adapter);
                    binding.rvFamily.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapter.notifyDataSetChanged() ;
                }
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {

            }
        });


        //onClickListener
        binding.backArrow.setOnClickListener(this);
        binding.okSendBtn.setOnClickListener(this);


        File fe = getExternalFilesDir("/motive_app");
        assert fe != null;
        outputDir = fe.getAbsolutePath();

        Log.d("outputDirSDK29", outputDir);

        setPermissions();
        Log.d("Fragment", "VideoUploadFragment");
    }

    public void videoCompress() {
        final String destPath = outputDir + File.separator + "VID_" + formatter.format(now) + ".mp4";
        Log.d("destPath",destPath);
        VideoCompress.compressVideoLow(filePath, destPath, playTime, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                VideoCompressUtil.writeFile(getApplicationContext(), "Start at: " + formatter.format(now) + "\n");
                startTime = System.currentTimeMillis();
            }

            @Override
            public void onSuccess() {
                showProgress("동영상을 압축 중 (100%)");
                hideProgress();
                VideoCompressUtil.writeFile(getApplicationContext(), "End at: " + formatter.format(now) + "\n");
                VideoCompressUtil.writeFile(getApplicationContext());
                endTime = System.currentTimeMillis();
                Log.d("count time", "Total: " + ((endTime - startTime) / 1000) + "s");
                String size;
                File destFile = new File(destPath);
                if(destFile.exists()){
                    FileSize = destFile.length();
                    size = FileSize/(float)1024/1024 + "";
                }else{
                    size = "File not exist";
                }
                Log.d("response", "EncodingVideoSize:" + size + "MB");
                uploadUri = Uri.fromFile(new File(destPath));
                uploadFile();

            }

            @Override
            public void onFail() {
                VideoCompressUtil.writeFile(getApplicationContext(), "Failed Compress!!!" + formatter.format(now));
                endTime = System.currentTimeMillis();
                Log.d("count time", "Total: " + ((endTime - startTime) / 1000) + "s");
                hideProgress();
            }

            @Override
            public void onProgress(float percent) {
                showProgress("동영상을 압축 중 ("+(int) percent+"%)");
            }
        });
    }

    public void setPermissions() {
        // 메니패스트에 권한이 있는지 확인
        int permiCheck = ContextCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permiCheck2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d("permiCheck", permiCheck + " ");
        //앱권한이 없으면 권한 요청
        if (permiCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        //앱권한이 없으면 권한 요청
        if (permiCheck2 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    //권한 승인 결과에 따라서 실행될 메서드


    public void showProgress(String msg) {
        if( pd == null ) {                  // 객체를 1회만 생성한다.
            pd = new ProgressDialog(this);  // 생성한다.
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

    public void onSendButton(Boolean isSelect, SelectFamilyVo selectFamilyVo){
        this.selectFamilyVo = selectFamilyVo;
        if(isSelect){
            binding.okSendBtn.setEnabled(true);
            binding.okSendBtn.setBackgroundResource(R.drawable.btn_back_blue);
        }else {
            binding.okSendBtn.setEnabled(false);
            binding.okSendBtn.setBackgroundResource(R.drawable.btn_back_gray);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == 0) {
                Toast.makeText(getApplicationContext(), "권한이 승인됨", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "권한 거절", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void uploadFile() {
        if (uploadUri != null) {
            //storage
            final FirebaseStorage storage = FirebaseStorage.getInstance();
            Log.d("uploadUri", " "+uploadUri);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
            Date now = new Date();
            final String filename = vo.getId() + "_" + formatter.format(now) + "_cheering.mp4";
            final String thumbnailName = vo.getId() + "_" + formatter.format(now) + "_cheering.jpg";
            final StorageReference storageRef = storage.getReferenceFromUrl("gs://motive-app-3061c.appspot.com").child("videos/" + filename);
            storageRef.putFile(uploadUri)
                    //성공시
                    .addOnSuccessListener(taskSnapshot -> {
                        hideProgress();
                        showProgress("전송중..");
                        Bitmap thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                        assert thumbnailBitmap != null;
                        saveBitmapToJpeg(thumbnailBitmap,thumbnailName);
                        //다이얼로그 띄우기
                        final StorageReference storageRef2 = storage.getReferenceFromUrl("gs://motive-app-3061c.appspot.com").child("thumbnails/" + thumbnailName);
                        storageRef2.putFile(thumbnailPath).addOnSuccessListener(taskSnapshot1 -> {
                            Log.d("썸네일 업로드","성공");
                            UploadVideoRequest request = new UploadVideoRequest();
                            Log.d("upload Id", selectFamilyVo.getSelectId());
                            Log.d("setFileName", title);
                            Log.d("setFileUrl", "videos/" + filename);
                            Log.d("setFileSize", Integer.toString((int)FileSize));
                            Log.d("setRegisterId", vo.getId());
                            Log.d("setRegisterName", vo.getName());
                            Log.d("setRegisterRelationship", selectFamilyVo.getSelectRelation());
                            Log.d("setThumbnailUrl", "thumbnails/" + thumbnailName);
                            ArrayList<UserIdRequest> userIds = new ArrayList<>();
                            UserIdRequest userIdRequest = new UserIdRequest();
                            userIdRequest.setUserId(selectFamilyVo.getSelectId());
                            userIds.add(userIdRequest);
                            request.setUserIds(userIds);
                            request.setFileName(title);
                            request.setFileUrl("videos/" + filename);
                            request.setFileSize(Integer.toString((int)FileSize));
                            request.setRegisterId(vo.getId());
                            request.setRegisterName(vo.getName());
                            request.setRegisterRelationship(selectFamilyVo.getSelectRelation());
                            request.setThumbnailUrl("thumbnails/" + thumbnailName);
                            httpRequestService.uploadVideoRequest(request).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                                    Log.d("response","onResponse" + response.message());
                                    if(response.body()!=null){
                                        Log.d("response.body"," "+response.body().get("result").toString());
                                        hideProgress();
                                        dialogContent = "영상이 전송되었습니다.";
                                        Dialog();
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                                    Log.d("failed","onFailed" + t.getMessage());
                                    hideProgress();
                                    dialogContent = "영상 전송 실패";
                                    Dialog();
                                }
                            });
                        }).addOnFailureListener(e -> Log.d("썸네일 업로드","실패" + e.getMessage())).addOnProgressListener(taskSnapshot12 -> {

                        });
                    })
                    //실패시
                    .addOnFailureListener(e -> {
                        hideProgress();
                        Toast.makeText(getApplicationContext(), "동영상 업로드 실패", Toast.LENGTH_SHORT).show();
                        Log.d("failed", Objects.requireNonNull(e.getMessage()));
                    })
                    //진행중
                    .addOnProgressListener(taskSnapshot -> {
                        @SuppressWarnings("VisibleForTests")
                        double progress = (double)((100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount());
                        //dialog에 진행률을 퍼센트로 출력해 준다
                        showProgress("Uploaded " + ((int) progress) + "% ...");
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendVideo(String title){
        this.title = title;
        File selectFIle = new File(filePath);
        if(selectFIle.exists()){
            FileSize = selectFIle.length()/(float)1024/1024;
        }

        if(FileSize>10) {
            videoCompress();
        }else{
            uploadUri = Uri.fromFile(new File(filePath));
            uploadFile();
        }
    }

    public void Dialog_v5(){
        dialog_v5 = new CustomDialog_v5(this,// 내용
                btnListener_v5); // 버튼 이벤트

        dialog_v5.setCancelable(true);
        Objects.requireNonNull(dialog_v5.getWindow()).setGravity(Gravity.CENTER);
        dialog_v5.show();
    }

    //다이얼로그_v5 클릭이벤트
    private View.OnClickListener btnListener_v5 = new View.OnClickListener() {
        public void onClick(View v) {
            dialog_v5.dismiss();
        }
    };


    //다이얼로그 클릭이벤트
    public void Dialog() {
        dialog = new CustomDialog(this,
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
            Toast.makeText(getApplicationContext(), "동영상 업로드", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    public void onClick(View v) {
        if(v==binding.okSendBtn){
            Dialog_v5();
        }else if(v==binding.backArrow){
            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
    }

    private void saveBitmapToJpeg(Bitmap bitmap, String name) {
        //저장할 파일 이름

        //storage 에 파일 인스턴스를 생성합니다.
        File tempFile = new File(outputDir, name);

        try {
            // 자동으로 빈 파일을 생성합니다.
            tempFile.createNewFile();

            // 파일을 쓸 수 있는 스트림을 준비합니다.
            FileOutputStream out = new FileOutputStream(tempFile);

            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // 스트림 사용후 닫아줍니다.
            out.close();

            thumbnailPath =  Uri.fromFile(tempFile);
            Log.d("thumbnailPath",Uri.fromFile(tempFile).toString());
        } catch (FileNotFoundException e) {
            Log.e("MyTag","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("MyTag","IOException : " + e.getMessage());
        }
    }
}
