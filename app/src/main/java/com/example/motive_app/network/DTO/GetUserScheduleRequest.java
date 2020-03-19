package com.example.motive_app.network.DTO;

public class GetUserScheduleRequest {
    private String userId;
    private String groupCode;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
