package com.example.motive_app.network.dto;

public class UpdatePswdRequest {
    String id;
    String currentPswd;
    String newPswd;
    String type;

    public void setId(String id) {
        this.id = id;
    }

    public void setCurrentPswd(String currentPswd) {
        this.currentPswd = currentPswd;
    }

    public void setNewPswd(String newPswd) {
        this.newPswd = newPswd;
    }

    public void setType(String type) {
        this.type = type;
    }
}
