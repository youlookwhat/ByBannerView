package me.jingbin.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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


import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.jingbin.banner.config.BannerConfig;
import me.jingbin.banner.config.BannerScroller;
import me.jingbin.banner.config.BannerViewPager;
import me.jingbin.banner.config.OnBannerClickListener;
import me.jingbin.banner.config.WeakHandler;
import me.jingbin.banner.holder.ByBannerViewHolder;
import me.jingbin.banner.holder.HolderCreator;


/**
 * @author jingbin
 * link: https://github.com/youlookwhat/ByBannerView
 */
public class ByBannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

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
    // 滑动到头后，是否返回滑动，默认false。返回滑动时page_left_margin无效
    private boolean isBackLoop = BannerConfig.IS_BACK_LOOP;
    // 指示器的默认宽高
    private final int indicatorSize;
    // 一屏多页模式是否可点击侧边切换，默认为true
    private boolean isCanClickSideRoll = true;
    private static final int NUM = 5000;

    private int count = 0;
    private int gravity = -1;
    private int widthPixels;
    private int lastPosition;
    private int currentItem;
    private int mPageLeftMargin;
    private int mPageRightMargin;
    private List mDatas;
    private List<ImageView> indicatorImages;
    private HolderCreator<ByBannerViewHolder> creator;
    private WeakHandler handler = null;

    private Context context;
    private LinearLayout indicator;
    private BannerViewPager viewPager;
    private BannerPagerAdapter adapter;
    private OnBannerClickListener listener;
    private Drawable mIndicatorSelectedDrawable;
    private Drawable mIndicatorUnselectedDrawable;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mIndicatorSelectedResId = R.drawable.by_gray_radius;
    private int mIndicatorUnselectedResId = R.drawable.by_white_radius;

    public ByBannerView(Context context) {
        this(context, null);
    }

    public ByBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ByBannerView(Context context, AttributeSet attrs, int defStyle) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bybanner, this, true);
        viewPager = view.findViewById(R.id.bannerViewPager);
        indicator = view.findViewById(R.id.circleIndicator);
        setPageLeftRightMargin(mPageLeftMargin, mPageRightMargin);
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ByBannerView);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.ByBannerView_indicator_width, indicatorSize);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.ByBannerView_indicator_height, indicatorSize);
        mIndicatorPadding = typedArray.getDimensionPixelSize(R.styleable.ByBannerView_indicator_padding, BannerConfig.PADDING_SIZE);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.ByBannerView_indicator_margin, BannerConfig.MARGIN_BOTTOM);
        mIndicatorMarginLeft = typedArray.getDimensionPixelSize(R.styleable.ByBannerView_indicator_margin_left, 0);
        mIndicatorMarginRight = typedArray.getDimensionPixelSize(R.styleable.ByBannerView_indicator_margin_right, 0);
        mIndicatorSelectedResId = typedArray.getResourceId(R.styleable.ByBannerView_indicator_drawable_selected, R.drawable.by_gray_radius);
        mIndicatorUnselectedResId = typedArray.getResourceId(R.styleable.ByBannerView_indicator_drawable_unselected, R.drawable.by_white_radius);
        delayTime = typedArray.getInt(R.styleable.ByBannerView_delay_time, BannerConfig.TIME);
        scrollTime = typedArray.getInt(R.styleable.ByBannerView_scroll_time, BannerConfig.DURATION);
        isAutoPlay = typedArray.getBoolean(R.styleable.ByBannerView_is_auto_play, BannerConfig.IS_AUTO_PLAY);
        isLoop = typedArray.getBoolean(R.styleable.ByBannerView_is_loop, BannerConfig.IS_LOOP);
        isBackLoop = typedArray.getBoolean(R.styleable.ByBannerView_is_back_loop, BannerConfig.IS_BACK_LOOP);
        mPageLeftMargin = typedArray.getDimensionPixelSize(R.styleable.ByBannerView_page_left_margin, 0);
        mPageRightMargin = typedArray.getDimensionPixelSize(R.styleable.ByBannerView_page_right_margin, 0);
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
     * 设置一屏多页时，点击边缘是否可切换
     */
    public ByBannerView setCanClickSideRoll(boolean canClickSideRoll) {
        isCanClickSideRoll = canClickSideRoll;
        return this;
    }

    /**
     * 一屏多页时的右边距
     *
     * @param pageRightMargin banner距屏幕的右边距
     */
    public ByBannerView setPageRightMargin(int pageRightMargin) {
        this.mPageRightMargin = pageRightMargin;
        setPageLeftRightMargin(mPageLeftMargin, mPageRightMargin);
        return this;
    }

    /**
     * 一屏多页时的左边距
     *
     * @param pageLeftMargin banner距屏幕的左边距
     */
    public ByBannerView setPageLeftMargin(int pageLeftMargin) {
        this.mPageLeftMargin = pageLeftMargin;
        setPageLeftRightMargin(mPageLeftMargin, mPageRightMargin);
        return this;
    }

    /**
     * 一屏多页时的左右边距
     *
     * @param pageLeftMargin  banner距屏幕的左边距
     * @param pageRightMargin banner距屏幕的右边距
     */
    public ByBannerView setPageLeftRightMargin(int pageLeftMargin, int pageRightMargin) {
        if (pageLeftMargin > 0 || pageRightMargin > 0) {
            this.mPageLeftMargin = pageLeftMargin;
            this.mPageRightMargin = pageRightMargin;
            viewPager.setClipChildren(false);
            viewPager.setClipToPadding(false);
            viewPager.setOffscreenPageLimit(2);
            viewPager.setPadding(mPageLeftMargin, 0, mPageRightMargin, 0);
        }
        return this;
    }


    public ByBannerView setAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    public ByBannerView setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        return this;
    }

    public ByBannerView setDelayTime(int delayTime) {
        this.delayTime = delayTime;
        return this;
    }

    public ByBannerView setIndicatorGravity(int type) {
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

    public ByBannerView setBannerAnimation(Class<? extends ViewPager.PageTransformer> transformer) {
        try {
            viewPager.setHandleAttached(false);
            viewPager.setPageTransformer(true, transformer.newInstance());
        } catch (Exception ignored) {

        }
        return this;
    }

    public ByBannerView setOffscreenPageLimit(int limit) {
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    public ByBannerView setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        viewPager.setHandleAttached(false);
        viewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    public ByBannerView setBannerStyle(int bannerStyle) {
        this.bannerStyle = bannerStyle;
        return this;
    }

    public ByBannerView setViewPagerIsScroll(boolean isScroll) {
        this.isScroll = isScroll;
        return this;
    }

    public ByBannerView setPages(List<?> datas, HolderCreator<ByBannerViewHolder> creator) {
        this.mDatas = datas;
        this.creator = creator;
        this.count = datas.size();
        return this;
    }

    public void update(List<?> imageUrls) {
        if (mDatas != null) {
            mDatas.clear();
            mDatas.addAll(imageUrls);
            count = mDatas.size();
        }
        if (indicatorImages != null) {
            indicatorImages.clear();
        }
        start();
    }

    public void updateBannerStyle(int bannerStyle) {
        indicator.setVisibility(GONE);
        this.bannerStyle = bannerStyle;
        start();
    }

    public ByBannerView start() {
        if (count > 0) {
            setStyleUI();
            setImageList();
            setData();
        }
        return this;
    }

    public ByBannerView setIndicatorRes(int select, int unSelect) {
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

    public ByBannerView setIndicatorRes(Drawable select, Drawable unSelect) {
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

    public void setCurrentItem(int item) {
        if (viewPager == null) return;
        if (isLoop && !isBackLoop) {
            // 循环滚动，不是实际的position
            int position = NUM / 2 - ((NUM / 2) % count) + 1 + item;
            if (position < count) {
                viewPager.setCurrentItem(position);
            }
        } else {
            viewPager.setCurrentItem(item);
        }
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        if (viewPager == null) return;
        if (isLoop && !isBackLoop) {
            // 循环滚动，不是实际的position
            int position = NUM / 2 - ((NUM / 2) % count) + 1 + item;
            if (position < count) {
                viewPager.setCurrentItem(position, smoothScroll);
            }
        } else {
            viewPager.setCurrentItem(item, smoothScroll);
        }
    }

    /**
     * 一屏多页状态时，点击边缘位置，定位到指定position
     *
     * @param position 点击的position
     */
    private void setClipCurrentItem(int position) {
        if (viewPager == null || mDatas == null) {
            return;
        }
        int currentItem = getCurrentItem();
        int realCurrentItem = position;
        if (!isBackLoop) {
            realCurrentItem = toRealPosition(position);
        }
        if (realCurrentItem > mDatas.size() - 1 || currentItem == realCurrentItem) {
            return;
        }
        viewPager.setCurrentItem(position);
        if (isLoop && !isBackLoop) {
            startAutoPlay();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    private void createIndicator() {
        indicatorImages.clear();
        indicator.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ScaleType.CENTER_CROP);
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
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
                params.leftMargin = mIndicatorPadding;
                params.rightMargin = mIndicatorPadding;
                indicator.addView(imageView, params);
            } else if (bannerStyle == BannerConfig.CUSTOM_INDICATOR) {
                LinearLayout.LayoutParams customParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                customParams.leftMargin = mIndicatorPadding;
                customParams.rightMargin = mIndicatorPadding;
                indicator.addView(imageView, customParams);
            }
        }
        if (gravity != -1) {
            indicator.setGravity(gravity);
        }
    }

    private void setData() {
        if (isLoop && !isBackLoop) {
            // 循环滚动 且 不是返回滚动
            currentItem = NUM / 2 - ((NUM / 2) % count) + 1;
            lastPosition = 1;
        } else {
            currentItem = 0;
            lastPosition = 0;
        }
        if (adapter == null) {
            adapter = new BannerPagerAdapter();
            viewPager.addOnPageChangeListener(this);
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentItem);
        viewPager.setOffscreenPageLimit(count);
        viewPager.setScrollable(isScroll && count > 1);
        startAutoPlay();
    }

    public void startAutoPlay() {
        if (isAutoPlay && count > 1) {
            // 设置了自动轮播且数据大于1
            if (handler == null) {
                handler = new WeakHandler();
            }
            handler.removeCallbacks(task);
            handler.postDelayed(task, delayTime);
        }
    }

    public void stopAutoPlay() {
        if (isAutoPlay && handler != null) {
            handler.removeCallbacks(task);
        }
    }

    // 是否向右滑动
    private boolean isSlipRight = true;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (count > 1) {
                if (handler == null) {
                    handler = new WeakHandler();
                }
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
            if (mDatas == null) {
                return 0;
            }
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
            ByBannerViewHolder holder;
            View view;
            if (mViewCache.size() == 0) {
                holder = creator.createViewHolder();
                view = holder.createView(container.getContext());
                view.setTag(holder);
            } else {
                view = mViewCache.removeFirst();
                holder = (ByBannerViewHolder) view.getTag();
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
                    if (isClipChildrenMode() && isCanClickSideRoll) {
                        // 一屏多页且点击的不是当前page，则滚动到对应的page
                        setClipCurrentItem(position);
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

    public int getCurrentItem() {
        if (isBackLoop) {
            return viewPager.getCurrentItem();
        } else {
            return toRealPosition(viewPager.getCurrentItem());
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
        if (bannerStyle == BannerConfig.CIRCLE_INDICATOR || bannerStyle == BannerConfig.CUSTOM_INDICATOR) {
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

    /**
     * 是否是一屏多页状态
     */
    private boolean isClipChildrenMode() {
        return (mPageLeftMargin > 0 || mPageRightMargin > 0);
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

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public void releaseBanner() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (mDatas != null) {
            mDatas.clear();
        }
        if (indicatorImages != null) {
            indicatorImages.clear();
        }
    }
}
