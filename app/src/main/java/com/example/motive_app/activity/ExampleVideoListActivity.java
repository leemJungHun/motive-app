package com.example.motive_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.motive_app.R;
import com.example.motive_app.adapter.ExampleVideoRecyclerAdapter;
import com.example.motive_app.data.VideoListItem;
import com.example.motive_app.databinding.ActivityExampleVideoBinding;

import java.util.ArrayList;

public class ExampleVideoListActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityExampleVideoBinding binding;
    private ArrayList<VideoListItem> mItem = new ArrayList<>();
    private ExampleVideoRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_example_video);

        binding.backArrow.setOnClickListener(this);

        //가족
        VideoListItem exampleFamilyItem = new VideoListItem();
        exampleFamilyItem.setFileName("가족");
        exampleFamilyItem.setFileUrl("example/videos/family.mp4");
        exampleFamilyItem.setRegisterId("예시");
        exampleFamilyItem.setRegisterName("동기강화 앱");
        exampleFamilyItem.setRegisterRelationship("가족");
        exampleFamilyItem.setRegistrationDate("");
        exampleFamilyItem.setThumbnailUrl("example/thumbnails/family_snapshot.jpg");
        exampleFamilyItem.setRegisterProfile("예시");

        mItem.add(exampleFamilyItem);

        //남자어린이
        VideoListItem exampleBoyItem = new VideoListItem();
        exampleBoyItem.setFileName("남자어린이");
        exampleBoyItem.setFileUrl("example/videos/boy.mp4");
        exampleBoyItem.setRegisterId("예시");
        exampleBoyItem.setRegisterName("동기강화 앱");
        exampleBoyItem.setRegisterRelationship("남자어린이");
        exampleBoyItem.setRegistrationDate("");
        exampleBoyItem.setThumbnailUrl("example/thumbnails/boy_snapshot.jpg");
        exampleBoyItem.setRegisterProfile("예시");

        mItem.add(exampleBoyItem);

        //여자어린이
        VideoListItem exampleLittleGirlItem = new VideoListItem();
        exampleLittleGirlItem.setFileName("여자어린이");
        exampleLittleGirlItem.setFileUrl("example/videos/little_girl.mp4");
        exampleLittleGirlItem.setRegisterId("예시");
        exampleLittleGirlItem.setRegisterName("동기강화 앱");
        exampleLittleGirlItem.setRegisterRelationship("여자어린이");
        exampleLittleGirlItem.setRegistrationDate("");
        exampleLittleGirlItem.setThumbnailUrl("example/thumbnails/little_girl_snapshot.jpg");
        exampleLittleGirlItem.setRegisterProfile("예시");

        mItem.add(exampleLittleGirlItem);

        //성인여성
        VideoListItem exampleFemaleItem = new VideoListItem();
        exampleFemaleItem.setFileName("성인여성");
        exampleFemaleItem.setFileUrl("example/videos/female.mp4");
        exampleFemaleItem.setRegisterId("예시");
        exampleFemaleItem.setRegisterName("동기강화 앱");
        exampleFemaleItem.setRegisterRelationship("성인여성");
        exampleFemaleItem.setRegistrationDate("");
        exampleFemaleItem.setThumbnailUrl("example/thumbnails/female_snapshot.jpg");
        exampleFemaleItem.setRegisterProfile("예시");

        mItem.add(exampleFemaleItem);


        adapter = new ExampleVideoRecyclerAdapter(mItem, this,this);
        binding.rvVideoList.setAdapter(adapter);
        binding.rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

    }

    public void playVideo(String fileUrl, String videoIdx){
        Intent intent;
        intent = new Intent(getApplicationContext(), PlayVideoActivity.class);

        intent.putExtra("fileUrl", fileUrl);
        startActivity(intent);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public void onClick(View v) {
        if(v==binding.backArrow){
            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
    }
}
