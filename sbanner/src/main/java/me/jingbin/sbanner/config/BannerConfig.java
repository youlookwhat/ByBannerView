package me.jingbin.sbanner.config;

public class BannerConfig {

    /**
     * indicator style
     * NOT_INDICATOR:    取消指示器
     * CIRCLE_INDICATOR: 自带的指示器
     * CUSTOM_INDICATOR: 手动设置的指示器
     */
    public static final int NOT_INDICATOR = 0;
    public static final int CIRCLE_INDICATOR = 1;
    public static final int CUSTOM_INDICATOR = 2;

    /**
     * indicator gravity
     */
    public static final int LEFT = 5;
    public static final int CENTER = 6;
    public static final int RIGHT = 7;

    /**
     * banner
     * PADDING_SIZE:  指示器大小
     * MARGIN_BOTTOM: 指示器距底部的距离
     * TIME:          滚动时间间隔
     * DURATION:      ViewPager切换滑动速度 时间越大速度越慢
     * IS_AUTO_PLAY:  是否自动循环
     * IS_SCROLL:     ViewPager是否能手动滑动
     * IS_LOOP:       是否循环播放，false则循环一轮后停止
     * IS_BACK_LOOP:  滑到到最后一个时，是否返回滑动，false则循环播放
     */
    public static final int PADDING_SIZE = 5;
    public static final int MARGIN_BOTTOM = 10;
    public static final int TIME = 2000;
    public static final int DURATION = 800;
    public static final boolean IS_AUTO_PLAY = true;
    public static final boolean IS_SCROLL = true;
    public static final boolean IS_LOOP = true;
    public static final boolean IS_BACK_LOOP = true;

    /**
     * margin 左右的间距
     */
    public static final int PAGE_MARGIN = 0;

}
