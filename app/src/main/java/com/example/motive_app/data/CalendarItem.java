package com.example.motive_app.data;

public class CalendarItem {
    private String day;
    private boolean isSelect;
    private boolean isSchedule;
    private boolean haveGoldMedal;
    private boolean haveSilverMedal;
    private boolean isFuture;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isSchedule() {
        return isSchedule;
    }

    public void setSchedule(boolean schedule) {
        isSchedule = schedule;
    }

    public boolean isHaveGoldMedal() {
        return haveGoldMedal;
    }

    public void setHaveGoldMedal(boolean haveGoldMedal) {
        this.haveGoldMedal = haveGoldMedal;
    }

    public boolean isHaveSilverMedal() {
        return haveSilverMedal;
    }

    public void setHaveSilverMedal(boolean haveSilverMedal) {
        this.haveSilverMedal = haveSilverMedal;
    }

    public boolean isFuture() {
        return isFuture;
    }

    public void setFuture(boolean future) {
        isFuture = future;
    }
}
