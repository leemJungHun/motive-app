package kr.rowan.motive_app.data;

import android.os.Parcel;
import android.os.Parcelable;

public class FamilyFindItem implements Parcelable {
    private String name;
    private String imageUrl;
    private String relation;
    private String userId;

    public FamilyFindItem(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        relation = in.readString();
        userId = in.readString();
    }

    public static final Creator<FamilyFindItem> CREATOR = new Creator<FamilyFindItem>() {
        @Override
        public FamilyFindItem createFromParcel(Parcel in) {
            return new FamilyFindItem(in);
        }

        @Override
        public FamilyFindItem[] newArray(int size) {
            return new FamilyFindItem[size];
        }
    };

    public FamilyFindItem() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(relation);
        dest.writeString(userId);
    }
}
