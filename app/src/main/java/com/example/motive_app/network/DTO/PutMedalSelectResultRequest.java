package com.example.motive_app.network.DTO;

public class PutMedalSelectResultRequest {
    private String userId;
    private String videoIdx;
    private String weekSort;
    private String goldMedalCnt;
    private String silverMedalCnt;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setVideoIdx(String videoIdx) {
        this.videoIdx = videoIdx;
    }

    public void setWeekSort(String weekSort) {
        this.weekSort = weekSort;
    }

    public void setGoldMedalCnt(String goldMedalCnt) {
        this.goldMedalCnt = goldMedalCnt;
    }

    public void setSilverMedalCnt(String silverMedalCnt) {
        this.silverMedalCnt = silverMedalCnt;
    }
}
