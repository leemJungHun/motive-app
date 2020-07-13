package kr.rowan.motive_app.network.dto;

public class LoginRequest {

    private String userId;
    private String pswd;

    public LoginRequest(String userId, String pswd) {
        this.userId = userId;
        this.pswd = pswd;
    }
}