package com.example.motive_app.network.vo;

public class MedalInfoVO {
    private String userId;
    private int videoIdx;
    private int weekSort;
    private String selectedDate;
    private int goldMedalCnt;
    private int silverMedalCnt;
    private String breakAway;
    private String groupCode;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVideoIdx() {
        return videoIdx;
    }

    public void setVideoIdx(int videoIdx) {
        this.videoIdx = videoIdx;
    }

    public int getWeekSort() {
        return weekSort;
    }

    public void setWeekSort(int weekSort) {
        this.weekSort = weekSort;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public int getGoldMedalCnt() {
        return goldMedalCnt;
    }

    public void setGoldMedalCnt(int goldMedalCnt) {
        this.goldMedalCnt = goldMedalCnt;
    }

    public int getSilverMedalCnt() {
        return silverMedalCnt;
    }

    public void setSilverMedalCnt(int silverMedalCnt) {
        this.silverMedalCnt = silverMedalCnt;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getBreakAway() {
        return breakAway;
    }

    public void setBreakAway(String breakAway) {
        this.breakAway = breakAway;
    }
}
