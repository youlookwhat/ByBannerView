package me.jingbin.sbanner.config;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class BannerViewPager extends ViewPager {

    private boolean scrollable = true;
    private boolean mHandleAttach = true;

    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return this.scrollable && super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return this.scrollable && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        Log.e("onAttachedToWindow", "mHandleAttach：" + mHandleAttach);
        /**
         * 解决完全隐藏ViewPager，再回来时，第一次滑动时没有动画效果，并且，经常出现view没有加载的情况的bug
         * 会有一个bug：如果banner设置两边有间距时，初次进去会黏在一起；
         */
        if (mHandleAttach) {
            try {
                Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
                mFirstLayout.setAccessible(true);
                mFirstLayout.set(this, false);
                setCurrentItem(getCurrentItem());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setHandleAttached(boolean handleAttach) {
        if (mHandleAttach != handleAttach) {
            mHandleAttach = handleAttach;
        }
    }
}
