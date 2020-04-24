package com.example.motive_app.network.dto;

public class RegistrationRequest {
    String id;
    String name;
    String pswd;
    String phone;
    int birth;
    String email;
    String organizationCode;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirth(int birth) {
        this.birth = birth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
}
