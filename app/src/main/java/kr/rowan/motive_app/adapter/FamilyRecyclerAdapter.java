package kr.rowan.motive_app.adapter;

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
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.registration.FindFamilyActivity;
import kr.rowan.motive_app.data.FamilyFindItem;
import kr.rowan.motive_app.data.FamilyItem;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.RelationItem;
import kr.rowan.motive_app.network.dto.RemoveUserFamilyRelationRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FamilyRecyclerAdapter extends RecyclerView.Adapter<FamilyRecyclerAdapter.ViewHolder> {
    private ArrayList<FamilyFindItem> mData = new ArrayList<>();
    private ArrayList<FamilyItem> familyList = new ArrayList<>();
    private ArrayList<RelationItem> addFamilyList = new ArrayList<>();
    private FindFamilyActivity activity;
    private boolean duplicatedId = false;
    private String familyId;
    private HttpRequestService httpRequestService;


    public FamilyRecyclerAdapter(FindFamilyActivity activity,String familyId) {
        this.activity = activity;
        this.familyId = familyId;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    @NonNull
    public FamilyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_family, parent, false);
        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpRequestService = retrofit.create(HttpRequestService.class);

        return new FamilyRecyclerAdapter.ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(FamilyRecyclerAdapter.ViewHolder holder, int position) {

        FamilyFindItem item = mData.get(position);


        holder.familyName.setText(item.getName());
        holder.familyImg.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            holder.familyImg.setClipToOutline(true);
        }

        if (!item.getImageUrl().equals("")) {
            FirebaseStorage fs = FirebaseStorage.getInstance();
            StorageReference imagesRef = fs.getReference().child(item.getImageUrl());
            Glide.with(activity)
                    .load(imagesRef)
                    .into(holder.familyImg);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(FamilyFindItem familyFindItem) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getUserId().equals(familyFindItem.getUserId())) {
                duplicatedId = true;
            }
        }
        if (!duplicatedId) {
            mData.add(familyFindItem);
            FamilyItem familyData = new FamilyItem(familyFindItem.getUserId(), familyFindItem.getRelation());
            familyList.add(familyData);
            RelationItem relationItem = new RelationItem();
            relationItem.setUserId(familyFindItem.getUserId());
            relationItem.setRelation(familyFindItem.getRelation());
            addFamilyList.add(relationItem);
        }
        duplicatedId = false;
    }

    public ArrayList<RelationItem> getAddFamilyList(){
        return addFamilyList;
    }

    public void setData(FamilyFindItem familyFindItem){
        mData.add(familyFindItem);
        FamilyItem familyData = new FamilyItem(familyFindItem.getUserId(), familyFindItem.getRelation());
        familyList.add(familyData);
    }

    public void updateData(ArrayList<FamilyFindItem> familyFindItem) {
        mData.clear();
        mData.addAll(familyFindItem);
        notifyDataSetChanged();
    }

    public ArrayList<FamilyItem> getFamilyData() {
        return familyList;
    }

    private void deleteData(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView familyImg;
        TextView familyName;
        ImageView closeImg;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            familyImg = itemView.findViewById(R.id.family_profile);
            familyName = itemView.findViewById(R.id.family_name);
            closeImg = itemView.findViewById(R.id.family_delete);

            closeImg.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                RemoveUserFamilyRelationRequest request = new RemoveUserFamilyRelationRequest();
                request.setFamilyId(familyId);
                request.setUserId(mData.get(pos).getUserId());
                Log.d("familyId",familyId+" ");
                Log.d("userId", mData.get(pos).getUserId()+" ");
                httpRequestService.removeUserFamilyRelationRequest(request).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                        if(response.body()!=null){
                            Log.d("response.body",response.body().get("result")+" ");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {

                    }
                });

                deleteData(pos);
                Objects.requireNonNull(activity).rvVisible();
            });
        }
    }
}
