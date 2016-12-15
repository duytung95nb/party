package com.uitstu.party.supports;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by duy tung dao on 12/12/2016.
 */

public class StaticViewPager extends ViewPager {
    public StaticViewPager(Context context) {
        super(context);
    }

    public StaticViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
