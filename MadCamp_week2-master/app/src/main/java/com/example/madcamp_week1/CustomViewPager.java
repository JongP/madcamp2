package com.example.madcamp_week1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;
import net.daum.mf.map.api.MapView;

public class CustomViewPager extends ViewPager {
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v.getClass().getName().equals("net.daum.mf.map.api.MapView")) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}
