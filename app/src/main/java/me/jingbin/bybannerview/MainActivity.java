package me.jingbin.bybannerview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.banner.ByBannerView;
import me.jingbin.banner.config.OnBannerClickListener;
import me.jingbin.banner.config.OnBannerFilterClickListener;
import me.jingbin.banner.config.ScaleRightTransformer;
import me.jingbin.banner.holder.ByBannerViewHolder;
import me.jingbin.banner.holder.HolderCreator;

/**
 * @author jingbin
 */
public class MainActivity extends AppCompatActivity {

    private ByBannerView banner;
    private ByBannerView banner2;
    private ByBannerView banner3;
    // 用于退出activity,避免countdown，造成资源浪费。
    private final SparseArray<CountDownTimer> countDownMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);
        banner2 = findViewById(R.id.banner2);
        banner3 = findViewById(R.id.banner3);

        final List<BannerItemBean> list = getList(4);
        setBannerView(list);
        setBanner2View(list);
        setBanner3View(list);
    }

    private void setBannerView(final List<BannerItemBean> list) {
        banner.setPageRightMargin(dip2px(this, 59))
                .setCanClickSideRoll(true)// 是否点击边缘会滚动，默认是
//                .setAutoPlay(true)
//                .setBannerStyle(BannerConfig.NOT_INDICATOR)
                .setBannerAnimation(ScaleRightTransformer.class)
                .setOffscreenPageLimit(list.size())
                .setDelayTime(3000)
                .setPages(list, new HolderCreator<ByBannerViewHolder>() {
                    @Override
                    public ByBannerViewHolder createViewHolder() {
                        return new CustomViewHolder();
                    }
                })
                .start();
        banner.setOnBannerClickListener(new OnBannerFilterClickListener() {
            @Override
            public void onSingleClick(int position) {
                // OnBannerFilterClickListener 防止重复点击
                if (banner.getCurrentItem() == position) {
                    // 一屏多页时，如果点击的是当前的position则跳转
                    MainActivity.this.startActivity(new Intent(banner.getContext(), RecyclerViewBannerActivity.class));
                }
            }
        });
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("onPageSelected", "position:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setBanner2View(final List<BannerItemBean> list) {
        banner2
//                .setAutoPlay(true)
//                .setBannerStyle(BannerConfig.NOT_INDICATOR)
                .setBannerAnimation(ScaleRightTransformer.class)
                .setOffscreenPageLimit(list.size())
                .setDelayTime(3000)
                .setPages(list, new HolderCreator<ByBannerViewHolder>() {
                    @Override
                    public ByBannerViewHolder createViewHolder() {
                        return new CustomViewHolder2();
                    }
                })
                .start();
        banner2.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                Toast.makeText(getApplicationContext(), list.get(position).getTitle(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setBanner3View(final List<BannerItemBean> list) {
        banner3.setAutoPlay(true)
                .setOffscreenPageLimit(list.size())
                .setDelayTime(3000)
                .setPages(list, new HolderCreator<ByBannerViewHolder>() {
                    @Override
                    public ByBannerViewHolder createViewHolder() {
                        return new CustomViewHolder3();
                    }
                })
                .start();
        banner3.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                Toast.makeText(getApplicationContext(), list.get(position).getTitle(), Toast.LENGTH_LONG).show();
            }
        });
    }

    class CustomViewHolder implements ByBannerViewHolder<BannerItemBean> {

        private TextView mTextView;
        private TextView tvDay;
        private TextView tvHour;
        private TextView tvMin;
        private TextView tvMiao;
        private AppCompatButton btRefresh;
        CountDownTimer countDownTimer;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            mTextView = (TextView) view.findViewById(R.id.text);
            btRefresh = (AppCompatButton) view.findViewById(R.id.bt_refresh);
            tvDay = (TextView) view.findViewById(R.id.tv_day);
            tvHour = (TextView) view.findViewById(R.id.tv_hour);
            tvMin = (TextView) view.findViewById(R.id.tv_min);
            tvMiao = (TextView) view.findViewById(R.id.tv_miao);
            return view;
        }

        @Override
        public void onBind(Context context, int position, BannerItemBean data) {
            if (position == 3) {
                btRefresh.setText("刷新");
                btRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBannerView(getList(2));
                        setBanner2View(getList(2));
                    }
                });
            } else {
                btRefresh.setText("立即申请");
                btRefresh.setOnClickListener(null);
            }
            // 数据绑定
            mTextView.setText(String.format("%d：%s", position, data.getTitle()));

            long applyEndTime = data.getApplyEndTime();
            long currentTime = System.currentTimeMillis();
            long end = (applyEndTime - currentTime / 1000);
            if (end > 0) {
                //将前一个缓存清除
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                countDownTimer = new CountDownTimer(end * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        ArrayList<String> time = TimeUtil.getCountTimeByLong(millisUntilFinished / 1000);
                        if (time.size() == 4) {
                            CustomViewHolder.this.tvDay.setText(time.get(0));
                            CustomViewHolder.this.tvHour.setText(time.get(1));
                            CustomViewHolder.this.tvMin.setText(time.get(2));
                            CustomViewHolder.this.tvMiao.setText(time.get(3));
                        }
                    }

                    @Override
                    public void onFinish() {
                    }
                }.start();
                countDownMap.put(tvDay.hashCode(), countDownTimer);
            } else {
                tvDay.setText("0");
                tvHour.setText("0");
                tvMin.setText("0");
                tvMiao.setText("0");
            }
        }
    }

    static class CustomViewHolder2 implements ByBannerViewHolder<BannerItemBean> {

        @Override
        public View createView(Context context) {
            return LayoutInflater.from(context).inflate(R.layout.item_banner_two, null);
        }

        @Override
        public void onBind(Context context, int position, BannerItemBean data) {
        }
    }

    static class CustomViewHolder3 implements ByBannerViewHolder<BannerItemBean> {

        @Override
        public View createView(Context context) {
            return LayoutInflater.from(context).inflate(R.layout.item_banner_three, null);
        }

        @Override
        public void onBind(Context context, int position, BannerItemBean data) {
        }
    }

    /**
     * 清空资源
     */
    public void cancelAllTimers() {
        if (countDownMap != null) {
            Log.e("TAG", "size :  " + countDownMap.size());
            for (int i = 0, length = countDownMap.size(); i < length; i++) {
                CountDownTimer cdt = countDownMap.get(countDownMap.keyAt(i));
                if (cdt != null) {
                    cdt.cancel();
                }
            }
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * xml文件里的dp --->  手机像素里的px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static List<BannerItemBean> getList(int size) {
        List<BannerItemBean> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            BannerItemBean itemBean = new BannerItemBean();
            itemBean.setTitle("我是标题-" + i);
            long applyEndTime;
            if (i == 0) {
                applyEndTime = System.currentTimeMillis() / 1000 + 6000;
            } else if (i == 1) {
                applyEndTime = System.currentTimeMillis() / 1000 + 5000;
            } else {
                applyEndTime = System.currentTimeMillis() / 1000 + 4000;
            }
            itemBean.setApplyEndTime(applyEndTime);
            list.add(itemBean);
        }
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始轮播
        banner.startAutoPlay();
        banner2.startAutoPlay();
        banner3.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //结束轮播
        banner.stopAutoPlay();
        banner2.stopAutoPlay();
        banner3.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        banner.releaseBanner();
        banner2.releaseBanner();
        banner3.releaseBanner();
        cancelAllTimers();
    }
}
