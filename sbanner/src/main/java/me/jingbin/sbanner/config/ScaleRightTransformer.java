package me.jingbin.sbanner.config;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class ScaleRightTransformer implements ViewPager.PageTransformer {

    private ViewPager viewPager;
    private static final float SCALE_X = 0.08f;

    @Override
    public void transformPage(View view, float position) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.getParent();
        }
        int leftInScreen = view.getLeft() - viewPager.getScrollX();
        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
        int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
        float offsetRate = (float) offsetX * SCALE_X / viewPager.getMeasuredWidth();
        float scaleFactor = 1 - Math.abs(offsetRate);
        if (scaleFactor > 0) {
            view.setScaleX(scaleFactor);
        }
    }
}
