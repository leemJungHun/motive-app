package com.example.motive_app.network.DTO;

public class RegistrationTokenRequest {
    private String token;
    private String userId;

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
