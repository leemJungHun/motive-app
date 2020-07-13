package kr.rowan.motive_app.network.dto;

public class SearchPswdRequest {
    String email;
    String id;
    String type;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
