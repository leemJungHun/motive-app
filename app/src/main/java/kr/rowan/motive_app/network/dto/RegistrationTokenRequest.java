package kr.rowan.motive_app.network.dto;

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
