package com.example.motive_app.network.DTO;

public class LoginRequest {

    private String userId;
    private String pswd;

    public LoginRequest(String userId, String pswd) {
        this.userId = userId;
        this.pswd = pswd;
    }
}