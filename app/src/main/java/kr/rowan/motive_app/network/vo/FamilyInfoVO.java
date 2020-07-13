package kr.rowan.motive_app.network.vo;

import java.io.Serializable;

public class FamilyInfoVO implements Serializable {
    private String id;
    private String name;
    private String pswd;
    private String phone;
    private String email;
    private String withDrawalYn;
    private String profileImageUrl;

    public FamilyInfoVO(String id, String name, String pswd, String phone, String email, String withDrawalYn, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.pswd = pswd;
        this.phone = phone;
        this.email = email;
        this.withDrawalYn = withDrawalYn;
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

    public String getEmail() {
        return email;
    }

    public String getWithDrawalYn() {
        return withDrawalYn;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
