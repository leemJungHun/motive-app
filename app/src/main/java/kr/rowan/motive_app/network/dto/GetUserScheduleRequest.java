package kr.rowan.motive_app.network.dto;

public class GetUserScheduleRequest {
    private String userId;
    private String groupCode;

    public GetUserScheduleRequest(String userId, String groupCode) {
        this.userId = userId;
        this.groupCode = groupCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
