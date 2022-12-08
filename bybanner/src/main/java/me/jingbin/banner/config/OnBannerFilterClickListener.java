package me.jingbin.banner.config;

/**
 * banner防止重复点击
 *
 * @author jingbin
 */
public abstract class OnBannerFilterClickListener implements OnBannerClickListener {

    private long mLastClickTime = 0L;
    private long mTimeInterval = 1000L;

    public OnBannerFilterClickListener() {
    }

    public OnBannerFilterClickListener(long interval) {
        this.mTimeInterval = interval;
    }

    @Override
    public void onBannerClick(int position) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - this.mLastClickTime > this.mTimeInterval) {
            this.mLastClickTime = nowTime;
            this.onSingleClick(position);
        }
    }

    protected abstract void onSingleClick(int position);
}

