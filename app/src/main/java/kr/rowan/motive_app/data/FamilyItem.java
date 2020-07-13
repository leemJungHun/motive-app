package kr.rowan.motive_app.data;

import android.os.Parcel;
import android.os.Parcelable;

public class FamilyItem implements Parcelable {
    private String userId;
    private String relation;

    public FamilyItem(String userId, String relation) {
        this.userId = userId;
        this.relation = relation;
    }

    protected FamilyItem(Parcel in) {
        userId = in.readString();
        relation = in.readString();
    }

    public static final Creator<FamilyItem> CREATOR = new Creator<FamilyItem>() {
        @Override
        public FamilyItem createFromParcel(Parcel in) {
            return new FamilyItem(in);
        }

        @Override
        public FamilyItem[] newArray(int size) {
            return new FamilyItem[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getRelation() {
        return relation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(relation);
    }
}
