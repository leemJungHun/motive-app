package com.example.motive_app.activity.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_app.R;
import com.example.motive_app.adapter.SimpleTextAdapter;
import com.example.motive_app.databinding.ActivityFindInstitutionBinding;
import com.example.motive_app.network.HttpRequestService;
import com.example.motive_app.network.DTO.FindInstitutionRequest;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindInstitutionActivity extends AppCompatActivity {
    ActivityFindInstitutionBinding binding;
    Retrofit retrofit;
    HttpRequestService  httpRequestService;
    Context context;
    Activity activity;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> code = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_institution);


        activity = this;
        context = this;
        //retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        //listener
        binding.institutionNextBtn.setOnClickListener(onClickListener);
        binding.backArrow.setOnClickListener(onClickListener);
        binding.back1.setOnClickListener(onClickListener);
        binding.back2.setOnClickListener(onClickListener);
        binding.back3.setOnClickListener(onClickListener);
        binding.back4.setOnClickListener(onClickListener);
        binding.institutionSearchBtn.setOnClickListener(onClickListener);
        binding.institutionView.setOnClickListener(onClickListener);
        binding.adminSearchBtn.setOnClickListener(onClickListener);

    }
    @Override
    public void onBackPressed() {
        if(binding.institutionSearchDialog.getVisibility()==View.GONE) {
            startActivity(new Intent(getApplicationContext(), TypeChoiceActivity.class));

            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            finish();
        }else{
            binding.institutionSearchDialog.setVisibility(View.GONE);
            binding.institutionSearchBtn.setEnabled(true);
            binding.adminSearchBtn.setEnabled(false);
            binding.institutionText.setText("기관의 이름을 입력하세요");
            binding.institutionText.setTextColor(Color.parseColor("#b3b3b3"));
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("v",v.toString());
            Intent intent;
            if(v==binding.institutionNextBtn){

                intent = new Intent(getApplicationContext(), EmailCheckActivity.class);

                intent.putExtra("code",binding.institutionText.getTag().toString());

                //회원, 가족 구분
                intent.putExtra("type","users");
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }else if(v==binding.backArrow){
                intent = new Intent(getApplicationContext(), TypeChoiceActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                finish();
            }else if(v==binding.institutionSearchBtn||v==binding.institutionView){
                binding.institutionSearchDialog.setVisibility(View.VISIBLE);
                binding.institutionSearchBtn.setEnabled(false);
                binding.adminSearchBtn.setEnabled(true);
                binding.noneText.setVisibility(View.VISIBLE);
                binding.institutionList.setVisibility(View.GONE);
                binding.institutionNextBtn.setEnabled(false);
                binding.institutionNextBtn.setBackgroundResource(R.drawable.btn_back_gray);
            }else if(v==binding.back1||v==binding.back2||v==binding.back3||v==binding.back4){
                binding.institutionSearchDialog.setVisibility(View.GONE);
                binding.institutionSearchBtn.setEnabled(true);
                binding.adminSearchBtn.setEnabled(false);
                binding.institutionText.setText("기관의 이름을 입력하세요");
                binding.institutionText.setTextColor(Color.parseColor("#b3b3b3"));
            }else if(v==binding.adminSearchBtn){
                Log.d("admin", binding.institutionSearchText.getText().toString());

                FindInstitutionRequest request = new FindInstitutionRequest();
                request.setAdmin(binding.institutionSearchText.getText().toString());

                httpRequestService.getOrganizationListRequest(request).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.body()!=null){

                            if(list!=null){
                                list = new ArrayList<>();
                                code = new ArrayList<>();
                            }
                            String[] organizationListResponse = response.body().get("organizationList").toString()
                                    .replace("\"","").replace("{","").replace("}","").replace("[","").replace("]","")
                                    .split(",");
                            for (String s : organizationListResponse) {
                                String[] compare = s.split(":");
                                if (compare[0].equals("name")) {
                                    Log.d("name", compare[1]);
                                    list.add(compare[1]);
                                } else if (compare[0].equals("code")) {
                                    Log.d("code", compare[1]);
                                    code.add(compare[1]);
                                }
                            }

                            final RecyclerView recyclerView = binding.institutionList ;
                            recyclerView.setLayoutManager(new LinearLayoutManager(context)) ;

                            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                            SimpleTextAdapter adapter = new SimpleTextAdapter(list,code) ;
                            recyclerView.setAdapter(adapter) ;

                            if(list.size()!=0) {
                                binding.noneText.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }else{
                                binding.noneText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                            recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                                @Override
                                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                                    if(child!=null) {
                                        TextView institutionName = child.findViewById(R.id.institution_name);
                                        binding.institutionText.setText(institutionName.getText().toString());
                                        binding.institutionText.setTag(institutionName.getTag());
                                        binding.institutionText.setTextColor(Color.parseColor("#202020"));
                                        binding.institutionSearchDialog.setVisibility(View.GONE);
                                        binding.institutionSearchBtn.setEnabled(true);
                                        binding.adminSearchBtn.setEnabled(false);
                                        binding.institutionNextBtn.setEnabled(true);
                                        binding.institutionNextBtn.setBackgroundResource(R.drawable.btn_back_blue);
                                        binding.institutionSearchText.setText("");
                                        Log.d("institutionTextCode", binding.institutionText.getTag().toString());
                                    }
                                    return false;
                                }

                                @Override
                                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                                }

                                @Override
                                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                                }
                            });


                        }else{
                            Log.e("response null","null");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.e("err", Objects.requireNonNull(t.getMessage()));
                    }
                });

            }
        }
    };
}
