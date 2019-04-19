package me.jingbin.sbannerview;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.sbanner.SBannerView;
import me.jingbin.sbanner.config.OnBannerClickListener;
import me.jingbin.sbanner.config.ScaleRightTransformer;
import me.jingbin.sbanner.holder.BannerViewHolder;
import me.jingbin.sbanner.holder.HolderCreator;

/**
 * @author jingbin
 */
public class MainActivity extends AppCompatActivity {

    private SBannerView banner;
    private SBannerView banner2;
    /**
     * 用于退出activity,避免countdown，造成资源浪费。
     */
    private SparseArray<CountDownTimer> countDownMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);
        banner2 = findViewById(R.id.banner2);

        final List<BannerItemBean> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BannerItemBean itemBean = new BannerItemBean();
            itemBean.setTitle("药妆店必BUY扫货指南-" + i);
//            list.add("药妆店必BUY扫货指南-" + i);
            long applyEndTime = 1555671600;
            if (i == 0) {
                applyEndTime = 1555671600;
            } else if (i == 1) {
                applyEndTime = 1555689600;
            } else if (i == 2) {
                applyEndTime = 1556035200;
            }
            itemBean.setApplyEndTime(applyEndTime);
            list.add(itemBean);
        }
        banner
                .setPageRightMargin(dip2px(this, 59))
//                .setAutoPlay(true)
//                .setBannerStyle(BannerConfig.NOT_INDICATOR)
                .setBannerAnimation(ScaleRightTransformer.class)
                .setOffscreenPageLimit(list.size())
                .setDelayTime(3000)
                .setPages(list, new HolderCreator<BannerViewHolder>() {
                    @Override
                    public BannerViewHolder createViewHolder() {
                        return new CustomViewHolder();
                    }
                })
                .start();
        banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                Toast.makeText(getApplicationContext(), list.get(position).getTitle(), Toast.LENGTH_LONG).show();
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

        banner2
//                .setAutoPlay(true)
//                .setBannerStyle(BannerConfig.NOT_INDICATOR)
                .setBannerAnimation(ScaleRightTransformer.class)
                .setOffscreenPageLimit(list.size())
                .setDelayTime(3000)
                .setPages(list, new HolderCreator<BannerViewHolder>() {
                    @Override
                    public BannerViewHolder createViewHolder() {
                        return new CustomViewHolder2();
                    }
                })
                .start();

    }

    class CustomViewHolder implements BannerViewHolder<BannerItemBean> {

        private TextView mTextView;
        private TextView tvDay;
        private TextView tvHour;
        private TextView tvMin;
        private TextView tvMiao;
        CountDownTimer countDownTimer;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            mTextView = (TextView) view.findViewById(R.id.text);
            tvDay = (TextView) view.findViewById(R.id.tv_day);
            tvHour = (TextView) view.findViewById(R.id.tv_hour);
            tvMin = (TextView) view.findViewById(R.id.tv_min);
            tvMiao = (TextView) view.findViewById(R.id.tv_miao);
            return view;
        }

        @Override
        public void onBind(Context context, int position, BannerItemBean data) {
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

    class CustomViewHolder2 implements BannerViewHolder<BannerItemBean> {

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner_two, null);
            return view;
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

    @Override
    protected void onPause() {
        super.onPause();
        //结束轮播
        banner.stopAutoPlay();
        banner2.stopAutoPlay();
        cancelAllTimers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始轮播
        banner.startAutoPlay();
        banner2.startAutoPlay();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        //开始轮播
//        banner.startAutoPlay();
//        banner2.startAutoPlay();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
        banner2.stopAutoPlay();
        cancelAllTimers();
    }

}
