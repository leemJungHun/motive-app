package com.example.motive_app.network.VO;

public class GroupScheduleVO {
    private String userId;
    private String weekSort;
    private String dayOfWeek;
    private String attendDate;
    private String isAttend;
    private String groupCode;
    private String breakAway;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWeekSort() {
        return weekSort;
    }

    public void setWeekSort(String weekSort) {
        this.weekSort = weekSort;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(String attendDate) {
        this.attendDate = attendDate;
    }

    public String getIsAttend() {
        return isAttend;
    }

    public void setIsAttend(String isAttend) {
        this.isAttend = isAttend;
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
