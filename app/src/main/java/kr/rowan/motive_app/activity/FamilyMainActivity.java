package kr.rowan.motive_app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.registration.ChangePassActivity;
import kr.rowan.motive_app.activity.registration.FindFamilyActivity;
import kr.rowan.motive_app.data.FamilyFindItem;
import kr.rowan.motive_app.databinding.ActivityFamilyMainBinding;
import kr.rowan.motive_app.fragment.family.FamilyInfoFragment;
import kr.rowan.motive_app.fragment.family.MyFamilyFragment;
import kr.rowan.motive_app.fragment.family.MyFamilyScheduleFragment;
import kr.rowan.motive_app.fragment.family.VideoUploadFragment;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.GetParentsInfoRequest;
import kr.rowan.motive_app.network.dto.LogoutRequest;
import kr.rowan.motive_app.network.dto.RegistrationTokenRequest;
import kr.rowan.motive_app.network.vo.FamilyInfoVO;
import kr.rowan.motive_app.network.vo.MyFamilyListVO;
import kr.rowan.motive_app.service.alarm.JobSchedulerStart;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FamilyMainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    ActivityFamilyMainBinding binding;
    int check;
    int preCheck = 0;
    private static final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    FamilyInfoVO vo;
    public Fragment nowFragment;
    Bundle args;
    String type;
    private HttpRequestService httpRequestService;
    private String token;
    private Context context = this;
    private ArrayList<FamilyFindItem> familyItems = new ArrayList<>();
    private boolean moveFragment = false;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_family_main);

        //기기 토큰 등록
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        //데이터 전달
        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            vo = (FamilyInfoVO) intent.getSerializableExtra("familyInfoVO");
            if (vo != null) {
                Log.d("getName", vo.getName());
                Log.d("getId", vo.getId());
                Log.d("getEmail", vo.getEmail());
                Log.d("getPhone", vo.getPhone());
            }
            type = intent.getExtras().getString("type");
        }

        GetParentsInfoRequest request = new GetParentsInfoRequest();
        request.setFamilyId(vo.getId());


        httpRequestService.getParentsInfoRequest(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("result");

                    for (JsonElement element : jsonArray) {
                        MyFamilyListVO myFamilyListVO = gson.fromJson(element, MyFamilyListVO.class);
                        FamilyFindItem familyFindItem = new FamilyFindItem();
                        familyFindItem.setUserId(myFamilyListVO.getId());
                        familyFindItem.setRelation(myFamilyListVO.getRelation());
                        if (myFamilyListVO.getProfileImageUrl()!=null) {
                            familyFindItem.setImageUrl(myFamilyListVO.getProfileImageUrl());
                        }else{
                            familyFindItem.setImageUrl("");
                        }
                        familyFindItem.setName(myFamilyListVO.getName());
                        familyItems.add(familyFindItem);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });

        binding.leftIconImageView.setOnClickListener(this::onIconClick);
        binding.rightIconImageView.setOnClickListener(this::onIconClick);
        binding.addFamily.setOnClickListener(this::onIconClick);

        //토큰 등록
        getToken();
        binding.bottomNav.setItemTextColor(getResources().getColorStateList(R.drawable.bottom_navigation_colors));
        binding.bottomNav.setItemIconTintList(getResources().getColorStateList(R.drawable.bottom_navigation_colors));
        fragmentManager = getSupportFragmentManager();
        binding.bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        args = new Bundle();
        args.putSerializable("familyInfoVO", vo);
        nowFragment = new MyFamilyFragment();
        nowFragment.setArguments(args);
        fragmentManager = getSupportFragmentManager();
        setStartFragment();

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        if(!moveFragment) {
            args = new Bundle();
            args.putSerializable("familyInfoVO", vo);
        }
        switch (item.getItemId()) {
            case R.id.myFamilyInfo:
                check = 0;
                nowFragment = new MyFamilyFragment();
                nowFragment.setArguments(args);
                binding.currentFragmentNameTextView.setText(getString(R.string.my_family));
                binding.leftIconImageView.setVisibility(View.GONE);
                binding.rightIconImageView.setVisibility(View.GONE);
                setStartFragment();
                return true;
            case R.id.myFamilyVideoInfo:
                check = 1;
                nowFragment = new VideoUploadFragment();
                nowFragment.setArguments(args);
                binding.currentFragmentNameTextView.setText(getString(R.string.my_video_info));
                binding.rightIconImageView.setVisibility(View.GONE);
                binding.leftIconImageView.setVisibility(View.GONE);
                setStartFragment();
                return true;
            case R.id.myFamilyScheduleInfo:
                check = 2;
                nowFragment = new MyFamilyScheduleFragment();
                nowFragment.setArguments(args);
                binding.currentFragmentNameTextView.setText(getString(R.string.my_schedule_info));
                setStartFragment();
                moveFragment=false;
                return true;
            case R.id.myInfo:
                check = 3;
                nowFragment = new FamilyInfoFragment();
                nowFragment.setArguments(args);
                binding.currentFragmentNameTextView.setText(getString(R.string.my_info));
                binding.rightIconImageView.setVisibility(View.GONE);
                binding.leftIconImageView.setVisibility(View.GONE);
                setStartFragment();
                return true;
        }
        return false;
    };

    public void showArrow() {
        binding.leftIconImageView.setVisibility(View.VISIBLE);
        binding.rightIconImageView.setVisibility(View.VISIBLE);
    }

    public void onIconClick(View view) {
        if (nowFragment instanceof MyFamilyScheduleFragment) {
            switch (view.getId()) {
                case R.id.leftIconImageView:
                    if(((MyFamilyScheduleFragment) nowFragment).familyIndex != 0) {
                        ((MyFamilyScheduleFragment) nowFragment).familyIndex--;
                    }
                    break;
                case R.id.rightIconImageView:
                    if(((MyFamilyScheduleFragment) nowFragment).familyIndex < ((MyFamilyScheduleFragment) nowFragment).familyListVOS.size() - 1) {
                        ((MyFamilyScheduleFragment) nowFragment).familyIndex++;
                    }
                    break;
            }
            ((MyFamilyScheduleFragment) nowFragment).getUserSchedule();
        }
        if (view.getId()==R.id.add_family){
            Intent intent = new Intent(getApplicationContext(), FindFamilyActivity.class);
            intent.putExtra("familyInfoVO", vo);
            intent.putExtra("type", "family");
            intent.putExtra("addFamily", "Y");
            intent.putParcelableArrayListExtra("nowFamilyList",familyItems);
            startActivity(intent);

            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            finish();
        }
    }

    public void updateFamilyName(String familyName) {
        binding.currentFragmentNameTextView.setText(familyName);
    }

    public void setStartFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (preCheck < check) {
            fragmentTransaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (preCheck > check) {
            fragmentTransaction.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right);
        }

        fragmentTransaction.replace(R.id.main_container, nowFragment).commitAllowingStateLoss();

        preCheck = check;
    }

    public void moveScheduleFragment(String myFamilyId) {
        check = 3;
        args.putSerializable("familyInfoVO", vo);
        args.putString("myFamilyId", myFamilyId);
        moveFragment = true;
        nowFragment = new MyFamilyScheduleFragment();
        nowFragment.setArguments(args);
        binding.bottomNav.setSelectedItemId(R.id.myFamilyScheduleInfo);
        setStartFragment();
    }

    public void videoSelectActivity(String filePath, String playTime) {
        Intent intent;
        intent = new Intent(getApplicationContext(), VideoSendSelectActivity.class);

        intent.putExtra("familyInfoVO", vo);
        intent.putExtra("type", "family");
        intent.putExtra("filePath", filePath);
        intent.putExtra("playTime", playTime);
        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void changePassOpen() {
        Intent intent;
        intent = new Intent(getApplicationContext(), ChangePassActivity.class);

        intent.putExtra("userId", vo.getId());
        intent.putExtra("type", type);

        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @SuppressLint("NewApi")
    public void exampleVideoOpen() {
        Intent intent;
        intent = new Intent(getApplicationContext(), ExampleVideoListActivity.class);

        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @SuppressLint("NewApi")
    public void logOut(String toastText) {
        LogoutRequest request = new LogoutRequest();
        request.setId(vo.getId());
        request.setToken(token);
        httpRequestService.logOut(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                if(response.body()!=null){
                    Log.d("logout", " " + response.body().get("result").toString());
                    if(response.body().get("result").toString().replace("\"","").equals("ok")) {
                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = auto.edit();
                        //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                        editor.clear();
                        editor.apply();

                        JobSchedulerStart.stop(context);
                        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                        Intent intent;
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
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
    }



    @Override
    public void onBackPressed() {

        if (check == 0) {
            if (System.currentTimeMillis() - backPressedTime < FINISH_INTERVAL_TIME) {
                finish();
                return;
            }
            backPressedTime = System.currentTimeMillis();
            Snackbar.make(binding.bottomNav, "한번 더 뒤로가기를 누르시면 앱을 종료합니다.", Snackbar.LENGTH_SHORT).show();

        } else {
            check = 0;
            binding.bottomNav.setSelectedItemId(R.id.myFamilyInfo);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        // permission was granted, yay! Do the
        // contacts-related task you need to do.
        // permission denied, boo! Disable the
        // functionality that depends on this permission.
        //return;

        // other 'case' lines to check for other
        // permissions this app might request.
    }

    public void getToken() {
        //토큰값을 받아옵니다.
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    token = Objects.requireNonNull(task.getResult()).getToken(); // 사용자가 입력한 저장할 데이터
                    Log.d("token", " " + token);

                    RegistrationTokenRequest request = new RegistrationTokenRequest();
                    request.setToken(token);
                    request.setUserId(vo.getId());
                    httpRequestService.registrationTokenRequest(request).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            if (response.body() != null) {
                                Log.d("result", " " + response.body().get("result").toString());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                        }
                    });
                });
    }

}