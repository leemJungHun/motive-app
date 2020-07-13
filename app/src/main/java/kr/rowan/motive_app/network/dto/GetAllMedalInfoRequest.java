package kr.rowan.motive_app.network.dto;

public class GetAllMedalInfoRequest {

    String userId;
    String groupCode;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
