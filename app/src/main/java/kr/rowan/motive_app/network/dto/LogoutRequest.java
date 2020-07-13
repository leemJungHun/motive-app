package kr.rowan.motive_app.network.dto;

public class LogoutRequest {
    private String id;
    private String token;

    public void setId(String id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
