package me.jingbin.sbannerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);
        banner2 = findViewById(R.id.banner2);

        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("药妆店必BUY扫货指南-" + i);
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
                Toast.makeText(getApplicationContext(), list.get(position), Toast.LENGTH_LONG).show();
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

    class CustomViewHolder implements BannerViewHolder<String> {

        private TextView mTextView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            mTextView = (TextView) view.findViewById(R.id.text);
            return view;
        }

        @Override
        public void onBind(Context context, int position, String data) {
            // 数据绑定
            mTextView.setText(position + "：" + data);
        }
    }

    class CustomViewHolder2 implements BannerViewHolder<String> {

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner_two, null);
            return view;
        }

        @Override
        public void onBind(Context context, int position, String data) {
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
    protected void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
        banner2.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
        banner2.stopAutoPlay();
    }

}
