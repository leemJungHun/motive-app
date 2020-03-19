package com.example.motive_app.network.VO;

import java.io.Serializable;

public class UserInfoVO implements Serializable {
    private String id;
    private String name;
    private String pswd;
    private String phone;
    private String birth;
    private String email;
    private String organizationCode;
    private String profileImageUrl;

    public UserInfoVO(String id, String name, String pswd, String phone, String birth, String email, String organizationCode, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.pswd = pswd;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
        this.organizationCode = organizationCode;
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPswd() {
        return pswd;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirth() {
        return birth;
    }

    public String getEmail() {
        return email;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
