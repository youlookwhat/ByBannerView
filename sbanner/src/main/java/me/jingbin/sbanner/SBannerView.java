package me.jingbin.sbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.jingbin.sbanner.config.BannerConfig;
import me.jingbin.sbanner.config.BannerScroller;
import me.jingbin.sbanner.config.BannerViewPager;
import me.jingbin.sbanner.config.OnBannerClickListener;
import me.jingbin.sbanner.config.WeakHandler;
import me.jingbin.sbanner.holder.SBannerViewHolder;
import me.jingbin.sbanner.holder.HolderCreator;

import static android.support.v4.view.ViewPager.OnPageChangeListener;
import static android.support.v4.view.ViewPager.PageTransformer;

/**
 * @author jingbin
 * link: https://github.com/youlookwhat/SBannerView
 */
public class SBannerView extends FrameLayout implements OnPageChangeListener {

    // 单个指示器左右的间距
    private int mIndicatorPadding = BannerConfig.PADDING_SIZE;
    // 指示器距离底部的高度
    private int mIndicatorMargin = BannerConfig.MARGIN_BOTTOM;
    // 指示器距离左侧的宽度
    private int mIndicatorMarginLeft = 0;
    // 指示器距离右侧的宽度
    private int mIndicatorMarginRight = 0;
    // 单个指示器的宽度
    private int mIndicatorWidth;
    // 单个指示器的高度
    private int mIndicatorHeight;
    // 指示器显示样式：不显示/自带/自定义
    private int bannerStyle = BannerConfig.CIRCLE_INDICATOR;
    // 滚动间隔时间
    private int delayTime = BannerConfig.TIME;
    // ViewPager切换滑动速度 时间越大速度越慢
    private int scrollTime = BannerConfig.DURATION;
    // 是否自动循环滚动，默认true
    private boolean isAutoPlay = BannerConfig.IS_AUTO_PLAY;
    // ViewPager是否能手动滑动，默认true
    private boolean isScroll = BannerConfig.IS_SCROLL;
    // 是否循环播放，false则循环一轮后停止，默认true
    private boolean isLoop = BannerConfig.IS_LOOP;
    // 滑动到头后，是否返回滑动，默认true，返回滑动！
    private boolean isBackLoop = BannerConfig.IS_BACK_LOOP;
    // 指示器的默认宽高
    private final int indicatorSize;
    private static final int NUM = 5000;

    private int count = 0;
    private int gravity = -1;
    private List mDatas;
    private int widthPixels;
    private int lastPosition;
    private int currentItem;
    private int mPageLeftMargin;
    private int mPageRightMargin;
    private List<ImageView> indicatorImages;
    private HolderCreator<SBannerViewHolder> creator;
    private final WeakHandler handler = new WeakHandler();

    private Context context;
    private LinearLayout indicator;
    private BannerViewPager viewPager;
    private BannerPagerAdapter adapter;
    private OnBannerClickListener listener;
    private Drawable mIndicatorSelectedDrawable;
    private Drawable mIndicatorUnselectedDrawable;
    private OnPageChangeListener mOnPageChangeListener;
    private int mIndicatorSelectedResId = R.drawable.gray_radius;
    private int mIndicatorUnselectedResId = R.drawable.white_radius;

    public SBannerView(Context context) {
        this(context, null);
    }

    public SBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        mDatas = new ArrayList<>();
        indicatorImages = new ArrayList<>();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        widthPixels = dm.widthPixels;
        indicatorSize = dm.widthPixels / 80;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        handleTypedArray(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_jbanner, this, true);
        viewPager = (BannerViewPager) view.findViewById(R.id.bannerViewPager);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.leftMargin = mPageLeftMargin;
        params.rightMargin = mPageRightMargin;
        viewPager.setLayoutParams(params);
        indicator = (LinearLayout) view.findViewById(R.id.circleIndicator);
        RelativeLayout.LayoutParams indicatorParam = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        indicatorParam.bottomMargin = mIndicatorMargin;
        indicatorParam.leftMargin = mIndicatorMarginLeft - mIndicatorPadding;
        indicatorParam.rightMargin = mIndicatorMarginRight - mIndicatorPadding;
        indicator.setLayoutParams(indicatorParam);
        initViewPagerScroll();
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SBannerView);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.SBannerView_indicator_width, indicatorSize);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.SBannerView_indicator_height, indicatorSize);
        mIndicatorPadding = typedArray.getDimensionPixelSize(R.styleable.SBannerView_indicator_padding, BannerConfig.PADDING_SIZE);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.SBannerView_indicator_margin, BannerConfig.MARGIN_BOTTOM);
        mIndicatorMarginLeft = typedArray.getDimensionPixelSize(R.styleable.SBannerView_indicator_margin_left, 0);
        mIndicatorMarginRight = typedArray.getDimensionPixelSize(R.styleable.SBannerView_indicator_margin_right, 0);
        mIndicatorSelectedResId = typedArray.getResourceId(R.styleable.SBannerView_indicator_drawable_selected, R.drawable.gray_radius);
        mIndicatorUnselectedResId = typedArray.getResourceId(R.styleable.SBannerView_indicator_drawable_unselected, R.drawable.white_radius);
        delayTime = typedArray.getInt(R.styleable.SBannerView_delay_time, BannerConfig.TIME);
        scrollTime = typedArray.getInt(R.styleable.SBannerView_scroll_time, BannerConfig.DURATION);
        isAutoPlay = typedArray.getBoolean(R.styleable.SBannerView_is_auto_play, BannerConfig.IS_AUTO_PLAY);
        isLoop = typedArray.getBoolean(R.styleable.SBannerView_is_loop, BannerConfig.IS_LOOP);
        isBackLoop = typedArray.getBoolean(R.styleable.SBannerView_is_back_loop, BannerConfig.IS_BACK_LOOP);
        mPageLeftMargin = typedArray.getDimensionPixelSize(R.styleable.SBannerView_page_left_margin, BannerConfig.PAGE_MARGIN);
        mPageRightMargin = typedArray.getDimensionPixelSize(R.styleable.SBannerView_page_right_margin, BannerConfig.PAGE_MARGIN);
        currentItem = isBackLoop ? 0 : -1;
        typedArray.recycle();
    }

    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            BannerScroller scroller = new BannerScroller(viewPager.getContext());
            scroller.setDuration(scrollTime);
            mField.set(viewPager, scroller);
        } catch (Exception ignored) {

        }
    }

    /**
     * @param pageRightMargin banner距屏幕的右边距
     */
    public SBannerView setPageRightMargin(int pageRightMargin) {
        this.mPageRightMargin = pageRightMargin;
        setPageLeftRightMargin(mPageLeftMargin, mPageRightMargin);
        return this;
    }

    /**
     * @param pageLeftMargin banner距屏幕的左边距
     */
    public SBannerView setPageLeftMargin(int pageLeftMargin) {
        this.mPageLeftMargin = pageLeftMargin;
        setPageLeftRightMargin(mPageLeftMargin, mPageRightMargin);
        return this;
    }

    /**
     * @param pageLeftMargin  banner距屏幕的左边距
     * @param pageRightMargin banner距屏幕的右边距
     */
    public SBannerView setPageLeftRightMargin(int pageLeftMargin, int pageRightMargin) {
        this.mPageLeftMargin = pageLeftMargin;
        this.mPageRightMargin = pageRightMargin;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.leftMargin = mPageLeftMargin;
        params.rightMargin = mPageRightMargin;
        viewPager.setLayoutParams(params);
        return this;
    }


    public SBannerView setAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    public SBannerView setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        return this;
    }

    public SBannerView setDelayTime(int delayTime) {
        this.delayTime = delayTime;
        return this;
    }

    public SBannerView setIndicatorGravity(int type) {
        switch (type) {
            case BannerConfig.LEFT:
                this.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case BannerConfig.CENTER:
                this.gravity = Gravity.CENTER;
                break;
            case BannerConfig.RIGHT:
                this.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
            default:
                break;
        }
        return this;
    }

    public SBannerView setBannerAnimation(Class<? extends PageTransformer> transformer) {
        try {
            viewPager.setPageTransformer(true, transformer.newInstance());
        } catch (Exception ignored) {

        }
        return this;
    }

    public SBannerView setOffscreenPageLimit(int limit) {
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    public SBannerView setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        viewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    public SBannerView setBannerStyle(int bannerStyle) {
        this.bannerStyle = bannerStyle;
        return this;
    }

    public SBannerView setViewPagerIsScroll(boolean isScroll) {
        this.isScroll = isScroll;
        return this;
    }

    public SBannerView setPages(List<?> datas, HolderCreator<SBannerViewHolder> creator) {
        this.mDatas = datas;
        this.creator = creator;
        this.count = datas.size();
        return this;
    }

    public void update(List<?> imageUrls) {
        this.mDatas.clear();
        this.indicatorImages.clear();
        this.mDatas.addAll(imageUrls);
        this.count = this.mDatas.size();
        start();
    }

    public void updateBannerStyle(int bannerStyle) {
        indicator.setVisibility(GONE);
        this.bannerStyle = bannerStyle;
        start();
    }

    public SBannerView start() {
        if (count > 0) {
            setStyleUI();
            setImageList();
            setData();
        }
        return this;
    }

    public SBannerView setIndicatorRes(int select, int unSelect) {
        if (select < 0) {
            throw new RuntimeException("[Banner] --> The select res is not exist");
        }
        if (unSelect < 0) {
            throw new RuntimeException("[Banner] --> The unSelect res is not exist");
        }

        mIndicatorSelectedResId = select;
        mIndicatorUnselectedResId = unSelect;
        return this;
    }

    public SBannerView setIndicatorRes(Drawable select, Drawable unSelect) {
        if (select == null || unSelect == null) {
            throw new RuntimeException("[Banner] --> The Drawable res is null");
        }

        mIndicatorSelectedDrawable = select;
        mIndicatorUnselectedDrawable = unSelect;
        return this;
    }

    private void setStyleUI() {
        int visibility = count > 1 ? View.VISIBLE : View.GONE;
        switch (bannerStyle) {
            case BannerConfig.CIRCLE_INDICATOR:
                indicator.setVisibility(visibility);
                break;
            case BannerConfig.CUSTOM_INDICATOR:
                indicator.setVisibility(visibility);
                break;
            default:
                break;
        }
    }

    private void setImageList() {
        if (bannerStyle == BannerConfig.CIRCLE_INDICATOR || bannerStyle == BannerConfig.CUSTOM_INDICATOR) {
            createIndicator();
        }
    }

    private void createIndicator() {
        indicatorImages.clear();
        indicator.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
            params.leftMargin = mIndicatorPadding;
            params.rightMargin = mIndicatorPadding;
            LinearLayout.LayoutParams customParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            customParams.leftMargin = mIndicatorPadding;
            customParams.rightMargin = mIndicatorPadding;
            if (i == 0) {
                if (mIndicatorSelectedDrawable != null) {
                    imageView.setImageDrawable(mIndicatorSelectedDrawable);
                } else {
                    imageView.setImageResource(mIndicatorSelectedResId);
                }
            } else {
                if (mIndicatorUnselectedDrawable != null) {
                    imageView.setImageDrawable(mIndicatorUnselectedDrawable);
                } else {
                    imageView.setImageResource(mIndicatorUnselectedResId);
                }
            }
            indicatorImages.add(imageView);
            if (bannerStyle == BannerConfig.CIRCLE_INDICATOR) {
                indicator.addView(imageView, params);
            } else if (bannerStyle == BannerConfig.CUSTOM_INDICATOR) {
                indicator.addView(imageView, customParams);
            }
        }
        if (gravity != -1) {
            indicator.setGravity(gravity);
        }
    }

    private void setData() {
        if (isBackLoop) {
            if (isLoop) {
                currentItem = 0;
                lastPosition = 0;
            } else {
                currentItem = 0;
                lastPosition = 0;
            }
        } else {
            if (isLoop) {
                currentItem = NUM / 2 - ((NUM / 2) % count) + 1;
                lastPosition = 1;
            } else {
                currentItem = 0;
                lastPosition = 0;
            }
        }
        if (adapter == null) {
            adapter = new BannerPagerAdapter();
            viewPager.addOnPageChangeListener(this);
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentItem);
        viewPager.setOffscreenPageLimit(count);
        if (isScroll && count > 1) {
            viewPager.setScrollable(true);
        } else {
            viewPager.setScrollable(false);
        }
        startAutoPlay();
    }

    public void startAutoPlay() {
        if (isAutoPlay) {
            handler.removeCallbacks(task);
            handler.postDelayed(task, delayTime);
        }
    }

    public void stopAutoPlay() {
        if (isAutoPlay) {
            handler.removeCallbacks(task);
        }
    }

    // 是否向右滑动
    private boolean isSlipRight = true;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (count > 1) {
                if (isBackLoop) {
                    // 下一个
                    if (isSlipRight) {

                        // > 最大值
                        int pagerCurrentItem = viewPager.getCurrentItem();
                        if (pagerCurrentItem >= adapter.getCount()) {
                            pagerCurrentItem = adapter.getCount() - 1;
                        }

                        // 2+1
                        currentItem = pagerCurrentItem + 1;
                        if (currentItem == adapter.getCount()) {
                            isSlipRight = false;
                        }
                    } else {
                        int pagerCurrentItem = viewPager.getCurrentItem();
                        if (pagerCurrentItem <= 1) {
                            pagerCurrentItem = 1;
                        }
                        currentItem = pagerCurrentItem - 1;
                        if (currentItem <= 0) {
                            isSlipRight = true;
                        }
                    }

                    if (isLoop) {
                        // 最后一个 向前滑
                        if (currentItem == adapter.getCount()) {
                            viewPager.setCurrentItem(currentItem);
                            handler.post(task);
                        } else {
                            viewPager.setCurrentItem(currentItem);
                            handler.postDelayed(task, delayTime);
                        }
                    } else {
                        if (currentItem >= adapter.getCount()) {
                            stopAutoPlay();
                        } else {
                            viewPager.setCurrentItem(currentItem);
                            handler.postDelayed(task, delayTime);
                        }
                    }
                } else {
                    currentItem = viewPager.getCurrentItem() + 1;
                    if (isLoop) {
                        if (currentItem == adapter.getCount() - 1) {
                            currentItem = 0;
                            viewPager.setCurrentItem(currentItem, false);
                            handler.post(task);
                        } else {
                            viewPager.setCurrentItem(currentItem);
                            handler.postDelayed(task, delayTime);
                        }
                    } else {
                        if (currentItem >= adapter.getCount()) {
                            stopAutoPlay();
                        } else {
                            viewPager.setCurrentItem(currentItem);
                            handler.postDelayed(task, delayTime);
                        }
                    }
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isAutoPlay) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                startAutoPlay();
                break;
            case MotionEvent.ACTION_DOWN:
                // 按下时 x坐标位置
                float touchX = ev.getRawX();
                // 去除两边间隔的区域
                if (touchX >= mPageLeftMargin && touchX < widthPixels - mPageRightMargin) {
                    stopAutoPlay();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private class BannerPagerAdapter extends PagerAdapter {

        private LinkedList<View> mViewCache;

        BannerPagerAdapter() {
            this.mViewCache = new LinkedList<>();
        }

        @Override
        public int getCount() {
            if (mDatas.size() == 1) {
                return mDatas.size();
            } else if (mDatas.size() < 1) {
                return 0;
            } else {
                if (isBackLoop) {
                    // 返回播放
                    return mDatas.size();
                } else {
                    // 循环播放
                    if (isLoop) {
                        return NUM;
                    } else {
                        return mDatas.size();
                    }
                }
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if (creator == null) {
                throw new RuntimeException("[Banner] --> The layout is not specified,请指定 holder");
            }
            SBannerViewHolder holder;
            View view;
            if (mViewCache.size() == 0) {
                holder = creator.createViewHolder();
                view = holder.createView(container.getContext());
                view.setTag(holder);
            } else {
                view = mViewCache.removeFirst();
                holder = (SBannerViewHolder) view.getTag();
            }

            // 设置点击事件，在onBind设置点击事件可将此点击事件覆盖
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if (isBackLoop) {
                            listener.onBannerClick(position);
                        } else {
                            listener.onBannerClick(toRealPosition(position));
                        }
                    }
                }
            });

            if (mDatas != null && mDatas.size() > 0) {
                if (isBackLoop) {
                    holder.onBind(container.getContext(), position, mDatas.get(position));
                } else {
                    holder.onBind(container.getContext(), toRealPosition(position), mDatas.get(toRealPosition(position)));
                }
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            this.mViewCache.add((View) object);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            if (isBackLoop) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            } else {
                mOnPageChangeListener.onPageScrolled(toRealPosition(position), positionOffset, positionOffsetPixels);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        currentItem = position;
        if (mOnPageChangeListener != null) {
            if (isBackLoop) {
                mOnPageChangeListener.onPageSelected(position);
            } else {
                mOnPageChangeListener.onPageSelected(toRealPosition(position));
            }
        }
        if (bannerStyle == BannerConfig.CIRCLE_INDICATOR ||
                bannerStyle == BannerConfig.CUSTOM_INDICATOR) {
            if (isLoop) {
                if (isBackLoop) {
                    // 返回播放
                    if (mIndicatorSelectedDrawable != null && mIndicatorUnselectedDrawable != null) {
                        // 未选择的图片
                        indicatorImages.get(lastPosition).setImageDrawable(mIndicatorUnselectedDrawable);
                        // 选择的图片
                        indicatorImages.get(position).setImageDrawable(mIndicatorSelectedDrawable);
                    } else {
                        indicatorImages.get(lastPosition).setImageResource(mIndicatorUnselectedResId);
                        indicatorImages.get(position).setImageResource(mIndicatorSelectedResId);
                    }
                } else {
                    if (mIndicatorSelectedDrawable != null && mIndicatorUnselectedDrawable != null) {
                        indicatorImages.get((lastPosition - 1 + count) % count).setImageDrawable(mIndicatorUnselectedDrawable);
                        indicatorImages.get((position - 1 + count) % count).setImageDrawable(mIndicatorSelectedDrawable);
                    } else {
                        indicatorImages.get((lastPosition - 1 + count) % count).setImageResource(mIndicatorUnselectedResId);
                        indicatorImages.get((position - 1 + count) % count).setImageResource(mIndicatorSelectedResId);
                    }
                }
            } else {
                if (isBackLoop) {
                    // 返回播放
                    if (mIndicatorSelectedDrawable != null && mIndicatorUnselectedDrawable != null) {
                        indicatorImages.get(lastPosition).setImageDrawable(mIndicatorUnselectedDrawable);
                        indicatorImages.get(position).setImageDrawable(mIndicatorSelectedDrawable);
                    } else {
                        indicatorImages.get(lastPosition).setImageResource(mIndicatorUnselectedResId);
                        indicatorImages.get(position).setImageResource(mIndicatorSelectedResId);
                    }
                } else {
                    if (mIndicatorSelectedDrawable != null && mIndicatorUnselectedDrawable != null) {
                        indicatorImages.get((lastPosition + count) % count).setImageDrawable(mIndicatorUnselectedDrawable);
                        indicatorImages.get((toRealPosition(position) + count) % count).setImageDrawable(mIndicatorSelectedDrawable);
                    } else {
                        indicatorImages.get((lastPosition + count) % count).setImageResource(mIndicatorUnselectedResId);
                        indicatorImages.get((toRealPosition(position) + count) % count).setImageResource(mIndicatorSelectedResId);
                    }
                }
            }
            lastPosition = position;
        }
    }

    private int toRealPosition(int position) {
        //int realPosition = (position - 1) % count;
        int realPosition;
        if (isLoop) {
            realPosition = (position - 1 + count) % count;
        } else {
            realPosition = (position + count) % count;
        }
        if (realPosition < 0) {
            realPosition += count;
        }
        return realPosition;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public void releaseBanner() {
        handler.removeCallbacksAndMessages(null);
    }
}
