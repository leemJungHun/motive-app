package com.example.motive_app.network.vo;


import java.io.Serializable;

public class EmailAuthVO implements Serializable {
    String authCode;
    String userId;

    public EmailAuthVO(String authCode, String userId) {
        this.authCode = authCode;
        this.userId = userId;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getUserId() {
        return userId;
    }
}

