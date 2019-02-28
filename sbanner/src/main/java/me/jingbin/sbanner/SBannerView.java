package me.jingbin.sbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
import java.util.List;

import me.jingbin.sbanner.config.BannerConfig;
import me.jingbin.sbanner.config.BannerScroller;
import me.jingbin.sbanner.config.BannerViewPager;
import me.jingbin.sbanner.config.OnBannerClickListener;
import me.jingbin.sbanner.config.WeakHandler;
import me.jingbin.sbanner.holder.BannerViewHolder;
import me.jingbin.sbanner.holder.HolderCreator;

import static android.support.v4.view.ViewPager.OnPageChangeListener;
import static android.support.v4.view.ViewPager.PageTransformer;

/**
 * @author jingbin
 */
public class SBannerView extends FrameLayout implements OnPageChangeListener {

    private int mIndicatorPadding = BannerConfig.PADDING_SIZE;
    private int mIndicatorMargin = BannerConfig.MARGIN_BOTTOM;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int indicatorSize;
    private int bannerStyle = BannerConfig.CIRCLE_INDICATOR;
    private int delayTime = BannerConfig.TIME;
    private int scrollTime = BannerConfig.DURATION;
    private boolean isAutoPlay = BannerConfig.IS_AUTO_PLAY;
    private boolean isScroll = BannerConfig.IS_SCROLL;
    private boolean isLoop = BannerConfig.IS_LOOP;
    private int mIndicatorSelectedResId = R.drawable.gray_radius;
    private int mIndicatorUnselectedResId = R.drawable.white_radius;
    private Drawable mIndicatorSelectedDrawable;
    private Drawable mIndicatorUnselectedDrawable;
    private int count = 0;
    private int currentItem;
    private int gravity = -1;
    private int lastPosition;
    private List mDatas;
    private HolderCreator<BannerViewHolder> creator;
    private List<ImageView> indicatorImages;
    private Context context;
    private BannerViewPager viewPager;
    private int widthPixels;
    // 指示器
    private LinearLayout indicator;

    private BannerPagerAdapter adapter;
    private OnPageChangeListener mOnPageChangeListener;
    private OnBannerClickListener listener;
    private int mPageLeftMargin;
    private int mPageRightMargin;
    private WeakHandler handler = new WeakHandler();

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
        View view = LayoutInflater.from(context).inflate(R.layout.banner, this, true);
        viewPager = (BannerViewPager) view.findViewById(R.id.bannerViewPager);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.leftMargin = mPageLeftMargin;
        params.rightMargin = mPageRightMargin;
        viewPager.setLayoutParams(params);
        indicator = (LinearLayout) view.findViewById(R.id.circleIndicator);
        RelativeLayout.LayoutParams indicatorParam = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        indicatorParam.bottomMargin = mIndicatorMargin;
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
        mIndicatorSelectedResId = typedArray.getResourceId(R.styleable.SBannerView_indicator_drawable_selected, R.drawable.gray_radius);
        mIndicatorUnselectedResId = typedArray.getResourceId(R.styleable.SBannerView_indicator_drawable_unselected, R.drawable.white_radius);
        delayTime = typedArray.getInt(R.styleable.SBannerView_delay_time, BannerConfig.TIME);
        scrollTime = typedArray.getInt(R.styleable.SBannerView_scroll_time, BannerConfig.DURATION);
        isAutoPlay = typedArray.getBoolean(R.styleable.SBannerView_is_auto_play, BannerConfig.IS_AUTO_PLAY);
        isLoop = typedArray.getBoolean(R.styleable.SBannerView_is_loop, BannerConfig.IS_LOOP);
        mPageLeftMargin = typedArray.getDimensionPixelSize(R.styleable.SBannerView_page_left_margin, BannerConfig.PAGE_MARGIN);
        mPageRightMargin = typedArray.getDimensionPixelSize(R.styleable.SBannerView_page_right_margin, BannerConfig.PAGE_MARGIN);
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

    public SBannerView setPages(List<?> datas, HolderCreator<BannerViewHolder> creator) {
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
        if (isLoop) {
            currentItem = 0;
            lastPosition = 0;
        } else {
            currentItem = 0;
            lastPosition = 0;
        }
        if (adapter == null) {
            adapter = new BannerPagerAdapter();
            viewPager.addOnPageChangeListener(this);
        }
        viewPager.setAdapter(adapter);
        Log.e("currentItem", currentItem + "");
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
//                        Log.e("currentItem1", currentItem + "");
                        viewPager.setCurrentItem(currentItem);
                        handler.post(task);
                    } else {
//                        Log.e("currentItem2", currentItem + "");
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

        @Override
        public int getCount() {
            if (mDatas.size() == 1) {
                return mDatas.size();
            } else if (mDatas.size() < 1) {
                return 0;
            } else {
                return mDatas.size();
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
            BannerViewHolder holder = creator.createViewHolder();

            View view = holder.createView(container.getContext());
            container.addView(view);

            if (mDatas != null && mDatas.size() > 0) {
                holder.onBind(container.getContext(), position, mDatas.get(position));
            }
            if (listener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onBannerClick(position);
                    }
                });
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
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
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        currentItem = position;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        if (bannerStyle == BannerConfig.CIRCLE_INDICATOR ||
                bannerStyle == BannerConfig.CUSTOM_INDICATOR) {
            if (isLoop) {
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
                    indicatorImages.get(lastPosition).setImageDrawable(mIndicatorUnselectedDrawable);
                    indicatorImages.get(position).setImageDrawable(mIndicatorSelectedDrawable);
                } else {
                    indicatorImages.get(lastPosition).setImageResource(mIndicatorUnselectedResId);
                    indicatorImages.get(position).setImageResource(mIndicatorSelectedResId);
                }
            }
            lastPosition = position;
        }
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
