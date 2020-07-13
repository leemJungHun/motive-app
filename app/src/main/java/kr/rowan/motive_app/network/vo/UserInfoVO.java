package kr.rowan.motive_app.network.vo;

import java.io.Serializable;

public class UserInfoVO implements Serializable {

    private String id;
    private String name;
    private String pswd;
    private String phone;
    private String birth;
    private String email;
    private String organizationCode;
    private String withDrawalYn;
    private String profileImageUrl;
    private String groupCode;
    private String alarmTime;
    private String registrationDate;
    private String startDate;


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

    public UserInfoVO(String id, String name, String pswd, String phone, String birth, String email, String organizationCode, String profileImageUrl, String groupCode) {
        this.id = id;
        this.name = name;
        this.pswd = pswd;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
        this.organizationCode = organizationCode;
        this.profileImageUrl = profileImageUrl;
        this.groupCode = groupCode;
    }

    public UserInfoVO(String id, String name, String pswd, String phone, String birth, String email, String organizationCode, String profileImageUrl, String groupCode, String registrationDate) {
        this.id = id;
        this.name = name;
        this.pswd = pswd;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
        this.organizationCode = organizationCode;
        this.profileImageUrl = profileImageUrl;
        this.groupCode = groupCode;
        this.registrationDate = registrationDate;
    }

    public UserInfoVO(String id, String name, String pswd, String phone, String birth, String email, String organizationCode, String withDrawalYn, String profileImageUrl, String groupCode, String alarmTime, String registrationDate) {
        this.id = id;
        this.name = name;
        this.pswd = pswd;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
        this.organizationCode = organizationCode;
        this.withDrawalYn = withDrawalYn;
        this.profileImageUrl = profileImageUrl;
        this.groupCode = groupCode;
        this.alarmTime = alarmTime;
        this.registrationDate = registrationDate;
    }

    public UserInfoVO(String id, String name, String pswd, String phone, String birth, String email, String organizationCode, String withDrawalYn, String profileImageUrl, String groupCode, String alarmTime, String registrationDate, String startDate) {
        this.id = id;
        this.name = name;
        this.pswd = pswd;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
        this.organizationCode = organizationCode;
        this.withDrawalYn = withDrawalYn;
        this.profileImageUrl = profileImageUrl;
        this.groupCode = groupCode;
        this.alarmTime = alarmTime;
        this.registrationDate = registrationDate;
        this.startDate = startDate;
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

    public String getGroupCode() {
        return groupCode;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getWithDrawalYn() {
        return withDrawalYn;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public String getStartDate() {
        return startDate;
    }
}
