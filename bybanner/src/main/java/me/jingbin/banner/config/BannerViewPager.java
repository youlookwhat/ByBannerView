package me.jingbin.banner.config;

import android.content.Context;
import android.util.AttributeSet;
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
        // 解决完全隐藏ViewPager，再回来时，第一次滑动时没有动画效果，并且，经常出现view没有加载的情况的bug
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 如果设置左右有间距的banner样式时，会初次进去会黏在一起的bug。
        // 这样会有另一个问题，在banner不可见刷新banner时左右间距会出现与异常
//        if (!mHandleAttach) {
//            mHandleAttach = true;
//        }
    }

    public void setHandleAttached(boolean handleAttach) {
        if (mHandleAttach != handleAttach) {
            mHandleAttach = handleAttach;
        }
    }
}
