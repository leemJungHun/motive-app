package com.example.motive_app.fragment;

import androidx.fragment.app.Fragment;

public abstract class RootScheduleFragment extends Fragment {

    public abstract void selectDay(int day);
    public abstract void onContentView(boolean isFuture, boolean onView);
}
