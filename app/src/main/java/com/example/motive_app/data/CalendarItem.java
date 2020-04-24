package com.example.motive_app.data;

public class CalendarItem {
    private String day;
    private boolean isSelect;
    private boolean isSchedule;
    private boolean haveGoldMedal;
    private boolean haveSilverMedal;
    private boolean isFuture;
    private boolean isThisMonth;

    public CalendarItem(){}

    public CalendarItem(String day, boolean isSelect, boolean isSchedule, boolean haveGoldMedal, boolean haveSilverMedal, boolean isFuture, boolean isThisMonth) {
        this.day = day;
        this.isSelect = isSelect;
        this.isSchedule = isSchedule;
        this.haveGoldMedal = haveGoldMedal;
        this.haveSilverMedal = haveSilverMedal;
        this.isFuture = isFuture;
        this.isThisMonth = isThisMonth;
    }

    public CalendarItem(String day, boolean isSelect, boolean isSchedule, boolean haveGoldMedal, boolean haveSilverMedal, boolean isFuture) {
        this.day = day;
        this.isSelect = isSelect;
        this.isSchedule = isSchedule;
        this.haveGoldMedal = haveGoldMedal;
        this.haveSilverMedal = haveSilverMedal;
        this.isFuture = isFuture;
    }

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

    public boolean isThisMonth() {
        return isThisMonth;
    }
}
