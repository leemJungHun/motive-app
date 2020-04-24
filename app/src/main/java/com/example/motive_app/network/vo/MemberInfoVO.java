package com.example.motive_app.network.vo;

import java.io.Serializable;

public class MemberInfoVO implements Serializable {
    private String userId;
    private String userName;
    private String userPhone;
    private String userProfileImageUrl;
    private String organizationName;
    private String groupCode;

    public MemberInfoVO(String userId, String userName, String userPhone,String userProfileImageUrl , String organizationName, String groupCode) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userProfileImageUrl = userProfileImageUrl;
        this.organizationName = organizationName;
        this.groupCode = groupCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserProfileImageUrl() { return userProfileImageUrl; }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getGroupCode() { return groupCode; }
}
