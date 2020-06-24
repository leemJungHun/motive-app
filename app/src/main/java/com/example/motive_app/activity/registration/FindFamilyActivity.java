package com.example.motive_app.activity.registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.motive_app.R;
import com.example.motive_app.activity.FamilyMainActivity;
import com.example.motive_app.adapter.FamilyRecyclerAdapter;
import com.example.motive_app.data.FamilyFindItem;
import com.example.motive_app.data.FamilyItem;
import com.example.motive_app.databinding.ActivityFindFamilyBinding;
import com.example.motive_app.dialog.CustomDialog;
import com.example.motive_app.dialog.CustomDialog_v4;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.dto.AddRelationRequest;
import com.example.motive_app.network.dto.RelationItem;
import com.example.motive_app.network.dto.UserPhoneRequest;
import com.example.motive_app.network.vo.FamilyInfoVO;
import com.example.motive_app.network.vo.MemberInfoVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindFamilyActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityFindFamilyBinding binding;

    //통신
    Retrofit retrofit;
    HttpRequestService httpRequestService;

    //다이얼로그
    CustomDialog dialog;
    private String dialogContent;

    MemberInfoVO vo;
    FamilyInfoVO familyInfoVO;
    String type;
    String relation;
    String addFamily;

    //리사이클러 뷰
    FamilyRecyclerAdapter adapter;
    FamilyFindItem mItem;
    private ArrayList<FamilyItem> familyList = null;
    private ArrayList<RelationItem> relations = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_family);

        //retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        //listener
        binding.backArrow.setOnClickListener(this);
        binding.searchFamilyButton.setOnClickListener(this);
        binding.nextBtn.setOnClickListener(this);

        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            addFamily = intent.getExtras().getString("addFamily");
            assert addFamily != null;
            if (addFamily.equals("Y")) {
                binding.nextBtn.setText("확인");
                binding.tabBarText.setText("가족추가");

                familyInfoVO = (FamilyInfoVO) intent.getSerializableExtra("familyInfoVO");
                adapter = new FamilyRecyclerAdapter(FindFamilyActivity.this, familyInfoVO.getId());
                ArrayList<FamilyFindItem> nowFamilyList = intent.getParcelableArrayListExtra("nowFamilyList");
                assert nowFamilyList != null;
                for (int i = 0; i< nowFamilyList.size(); i++){
                    Log.d("nowFamilyList"+i, nowFamilyList.get(i).getUserId()+" ");
                    adapter.setData(nowFamilyList.get(i));
                    binding.rvFamily.setAdapter(adapter);
                    binding.rvFamily.setLayoutManager(new LinearLayoutManager(FindFamilyActivity.this));
                    adapter.notifyDataSetChanged();
                    rvVisible();
                }
                if (familyInfoVO != null) {
                    Log.d("getName", familyInfoVO.getName());
                    Log.d("getId", familyInfoVO.getId());
                    Log.d("getEmail", familyInfoVO.getEmail());
                    Log.d("getPhone", familyInfoVO.getPhone());
                }
                type = intent.getExtras().getString("type");
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        switch (addFamily) {
            case "Y":
                intent = new Intent(getApplicationContext(), FamilyMainActivity.class);
                intent.putExtra("familyInfoVO", familyInfoVO);
                intent.putExtra("type", "family");
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                finish();
                break;
            case "N":
                intent = new Intent(getApplicationContext(), TypeChoiceActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                finish();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        if (v == binding.backArrow) {
            switch (addFamily) {
                case "Y":
                    intent = new Intent(getApplicationContext(), FamilyMainActivity.class);
                    intent.putExtra("familyInfoVO", familyInfoVO);
                    intent.putExtra("type", "family");
                    startActivity(intent);

                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    finish();
                    break;
                case "N":
                    intent = new Intent(getApplicationContext(), TypeChoiceActivity.class);
                    startActivity(intent);

                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    finish();
                    break;
            }
        } else if (v == binding.searchFamilyButton) {
            UserPhoneRequest request = new UserPhoneRequest();
            request.setUserPhone(binding.familyPhoneNum.getText().toString().replace("-", ""));
            httpRequestService.userPhoneRequest(request).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                    if (response.body() != null) {
                        if (!response.body().get("memberInfoVO").toString().equals("null")) {
                            Gson gson = new Gson();
                            vo = gson.fromJson(response.body().get("memberInfoVO").toString(), MemberInfoVO.class);

                            CustomDialog_v4 dialog_v4 = new CustomDialog_v4(FindFamilyActivity.this, vo.getUserName().replace("\"", ""), vo.getUserPhone().replace("\"", ""), vo.getOrganizationName().replace("\"", ""));
                            dialog_v4.setDialogListener(new CustomDialog_v4.CustomDialogListener() {
                                @Override
                                public void onPositiveClicked(String getRelation) {
                                    relation = getRelation;
                                    mItem = new FamilyFindItem();
                                    String name = vo.getUserName() + "(" + relation + ")";
                                    mItem.setName(name);
                                    if (vo.getUserProfileImageUrl() != null) {
                                        mItem.setImageUrl(vo.getUserProfileImageUrl());
                                    } else {
                                        mItem.setImageUrl("");
                                    }
                                    mItem.setRelation(relation);
                                    mItem.setUserId(vo.getUserId());
                                    adapter.addData(mItem);
                                    familyList = adapter.getFamilyData();
                                    binding.rvFamily.setAdapter(adapter);
                                    binding.rvFamily.setLayoutManager(new LinearLayoutManager(FindFamilyActivity.this));
                                    adapter.notifyDataSetChanged();
                                    rvVisible();
                                }

                                @Override
                                public void onNegativeClicked() {

                                }
                            });
                            dialog_v4.setCancelable(true);
                            Objects.requireNonNull(dialog_v4.getWindow()).setGravity(Gravity.CENTER);
                            dialog_v4.show();
                        } else {
                            dialogContent = "검색 결과가 없습니다.";
                            Dialog();
                        }
                    } else {
                        dialogContent = "검색 결과가 없습니다.";
                        Dialog();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                    dialogContent = "검색 결과가 없습니다.";
                    Dialog();
                }
        });
        } else if (v == binding.nextBtn) {
            switch (addFamily) {
                case "Y":
                    AddRelationRequest request = new AddRelationRequest();
                    relations=adapter.getAddFamilyList();
                    request.setId(familyInfoVO.getId());
                    request.setRelations(relations);
                    httpRequestService.addRelationRequest(request).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                            if (response.body() != null) {
                                if (response.body().get("result").toString().replace("\"", "").equals("ok")) {
                                    Intent intent = new Intent(getApplicationContext(), FamilyMainActivity.class);
                                    intent.putExtra("familyInfoVO", familyInfoVO);
                                    intent.putExtra("type", "family");
                                    startActivity(intent);

                                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {

                        }
                    });
                    break;
                case "N":
                    intent = new Intent(getApplicationContext(), EmailCheckActivity.class);

                    //회원, 가족 구분
                    intent.putExtra("type", "family");
                    if (familyList != null) {
                        intent.putParcelableArrayListExtra("familyList", familyList);
                    }

                    startActivity(intent);

                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    break;
            }
        }
    }

    public void rvVisible() {
        if (adapter.getItemCount() > 0) {
            binding.rvFamily.setVisibility(View.VISIBLE);
            binding.findFamilyText.setVisibility(View.GONE);
            binding.nextBtn.setEnabled(true);
            binding.nextBtn.setBackgroundResource(R.drawable.btn_back_blue);
        } else {
            binding.rvFamily.setVisibility(View.GONE);
            binding.findFamilyText.setVisibility(View.VISIBLE);
            binding.nextBtn.setEnabled(false);
            binding.nextBtn.setBackgroundResource(R.drawable.btn_back_gray);
        }
    }

    public void Dialog() {
        dialog = new CustomDialog(FindFamilyActivity.this,
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
