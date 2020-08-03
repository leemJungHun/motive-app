package kr.rowan.motive_app.fragment.family;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.FamilyMainActivity;
import kr.rowan.motive_app.databinding.FragmentFamilyMyInfoBinding;
import kr.rowan.motive_app.dialog.CustomDialog_v2;
import kr.rowan.motive_app.dialog.CustomDialog_v3;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.PutProfileImageRequest;
import kr.rowan.motive_app.network.vo.FamilyInfoVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FamilyInfoFragment extends Fragment implements View.OnClickListener {
    private FragmentFamilyMyInfoBinding binding;
    //다이얼로그
    private String dialogContent;
    private CustomDialog_v2 dialog_v2;
    private CustomDialog_v3 dialog_v3;
    private String profilePath;
    private String preProfilePath;

    //data
    private FamilyInfoVO vo;

    private FamilyMainActivity activity;


    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        vo = (FamilyInfoVO) args.getSerializable("familyInfoVO");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (FamilyMainActivity) context;
    }

    private HttpRequestService httpRequestService;

    private static final int RESULT_OK = -1;


    private Uri filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_family_my_info, container, false);

        SharedPreferences profile = Objects.requireNonNull(getActivity()).getSharedPreferences("profile", Activity.MODE_PRIVATE);
        profilePath = profile.getString("saveProfile", null);

        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        binding.photoChange.setIncludeFontPadding(false);
        binding.profileView.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            binding.profileView.setClipToOutline(true);
        }


        binding.passChangeIcon.setOnClickListener(this);
        binding.TextPass.setOnClickListener(this);
        binding.explainTextLogout.setOnClickListener(this);
        binding.explainTextWithdrawal.setOnClickListener(this);
        binding.photoChange.setOnClickListener(this);
        binding.photoChangeIcon.setOnClickListener(this);
        binding.openLicence.setOnClickListener(this);

        //set
        binding.myInfoName.setText(vo.getName());
        binding.TextId.setText(vo.getId());
        binding.TextEmail.setText(vo.getEmail());

        if (profilePath != null) {
            Glide.with(this)
                    .load(profilePath)
                    .into(binding.profileView);
        } else if (vo.getProfileImageUrl() != null) {
            FirebaseStorage fs = FirebaseStorage.getInstance();
            StorageReference imagesRef = fs.getReference().child(vo.getProfileImageUrl());
            Glide.with(this)
                    .load(imagesRef)
                    .into(binding.profileView);
        }

        return binding.getRoot();

    }


    @Override
    public void onClick(View v) {
        if (v == binding.passChangeIcon || v == binding.TextPass) {
            ((FamilyMainActivity) Objects.requireNonNull(getActivity())).changePassOpen();
        } else if (v == binding.explainTextLogout) {
            dialogContent = "로그아웃 하시겠습니까?";
            Dialog_v2();
        } else if (v == binding.explainTextWithdrawal) {
            Dialog_v3();
        } else if (v == binding.photoChange || v == binding.photoChangeIcon) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
        }else if(v == binding.openLicence){
            activity.openSourceFragment();
        }
    }

    private void Dialog_v2() {
        dialog_v2 = new CustomDialog_v2(getContext(),
                dialogContent,// 내용
                btnListener_v2); // 버튼 이벤트

        dialog_v2.setCancelable(true);
        Objects.requireNonNull(dialog_v2.getWindow()).setGravity(Gravity.CENTER);
        dialog_v2.show();
    }

    //다이얼로그_v2 클릭이벤트
    private View.OnClickListener btnListener_v2 = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.dialog_Ok) {
                SharedPreferences profile = Objects.requireNonNull(getActivity()).getSharedPreferences("profile", Activity.MODE_PRIVATE);
                SharedPreferences.Editor profileSave = profile.edit();
                profileSave.putString("saveProfile", null);
                profileSave.apply();
                ((FamilyMainActivity) Objects.requireNonNull(getActivity())).logOut("로그아웃");
            }
            dialog_v2.dismiss();
        }
    };


    private void Dialog_v3() {
        dialog_v3 = new CustomDialog_v3(getContext(),// 내용
                vo.getName(),
                btnListener_v3, "family"); // 버튼 이벤트

        dialog_v3.setCancelable(true);
        Objects.requireNonNull(dialog_v3.getWindow()).setGravity(Gravity.CENTER);
        dialog_v3.show();
    }

    //다이얼로그_v3 클릭이벤트
    private View.OnClickListener btnListener_v3 = new View.OnClickListener() {
        public void onClick(View v) {
            dialog_v3.dismiss();
        }
    };

    //결과 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            filePath = data.getData();
            Log.d("data.getData", data.getData() + "");
            Log.d("FamilyInfoFragment", "uri:" + filePath);
            Glide.with(this)
                    .load(filePath)
                    .into(binding.profileView);
            SharedPreferences profile = Objects.requireNonNull(getActivity()).getSharedPreferences("profile", Activity.MODE_PRIVATE);
            //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
            SharedPreferences.Editor profileSave = profile.edit();
            profileSave.putString("saveProfile", filePath.toString());
            profileSave.apply();
            uploadFile();
        }
    }

    private void uploadFile() {
        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();


            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
            Date now = new Date();
            final String filename = vo.getId() + "_" + formatter.format(now) + "_profile.jpg";
            StorageReference storageRef = storage.getReferenceFromUrl("gs://motive-app-3061c.appspot.com").child("images/" + filename);
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                        Toast.makeText(getContext(), "프로필 변경", Toast.LENGTH_SHORT).show();
                        PutProfileImageRequest request = new PutProfileImageRequest();
                        request.setId(vo.getId());
                        request.setImageUrl("images/" + filename);
                        request.setType("family");
                        httpRequestService.putProfileImageRequest(request).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                Log.d("response", "onResponse" + response);
                                if (response.body() != null) {
                                    SharedPreferences preProfile = Objects.requireNonNull(getActivity()).getSharedPreferences("preProfile", Activity.MODE_PRIVATE);
                                    preProfilePath = preProfile.getString("preProfiles", null);
                                    Log.d("response.body", response.body().get("result").toString());
                                    if (preProfilePath != null) {
                                        FirebaseStorage fs = FirebaseStorage.getInstance();
                                        StorageReference deleteImage = fs.getReference().child(preProfilePath);
                                        deleteImage.delete().addOnSuccessListener(aVoid -> Log.d("이전 프로필 사진", "삭제"));
                                    } else if (vo.getProfileImageUrl() != null) {
                                        FirebaseStorage fs = FirebaseStorage.getInstance();
                                        StorageReference deleteImage = fs.getReference().child(vo.getProfileImageUrl());
                                        deleteImage.delete().addOnSuccessListener(aVoid -> Log.d("이전 프로필 사진", "삭제"));
                                    }
                                }
                                final SharedPreferences preProfile = Objects.requireNonNull(getActivity()).getSharedPreferences("preProfile", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor preProfiles = preProfile.edit();
                                preProfiles.putString("preProfiles", "images/" + filename);
                                preProfiles.apply();
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                            }
                        });
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "프로필 변경 실패", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(taskSnapshot -> {
                        @SuppressWarnings("VisibleForTests")
                        double progress = (double) (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //dialog에 진행률을 퍼센트로 출력해 준다
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                    });
        } else {
            Toast.makeText(getContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }


}
