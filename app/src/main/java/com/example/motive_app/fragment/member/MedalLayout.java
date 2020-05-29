package com.example.motive_app.fragment.member;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.motive_app.R;

public class MedalLayout extends ConstraintLayout {
    private String weekCount;
    private ImageView medalImg;
    private TextView medalText;

    float mCurrX, mCurrY;

    public MedalLayout(Context context) {
        super(context);
        init(context);
    }

    public MedalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(inflaterService);
        View view = inflater.inflate(R.layout.item_medal, this, false);
        addView(view);

        medalImg = view.findViewById(R.id.medalImg);
        medalText = view.findViewById(R.id.medalText);
    }

    public MedalLayout setMedal(int img) {
        medalImg.setImageResource(img);
        medalImg.setVisibility(VISIBLE);
        return this;
    }

    public MedalLayout setTextBack(int img) {
        medalText.setBackgroundResource(img);

        return this;
    }

    public MedalLayout setMedalTextVisibility(boolean isVisible) {
        if(isVisible){
            medalText.setVisibility(VISIBLE);
        }else{
            medalText.setVisibility(GONE);
        }

        return this;
    }

    public MedalLayout setTextColor(int color) {
        medalText.setTextColor(color);

        return this;
    }

    public MedalLayout setWeekCount(String weekCount) {
        this.weekCount = weekCount;
        medalText.setText(weekCount);
        return this;
    }

    public String getWeekCount() {
        return this.weekCount;
    }

    public void setXY(float currX, float currY) {
        this.mCurrX = currX;
        this.mCurrY = currY;

        setX(mCurrX);
        setY(mCurrY);
    }
}
