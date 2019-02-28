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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);

        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("药妆店必BUY扫货指南-" + i);
        }
        banner
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
    }

    class CustomViewHolder implements BannerViewHolder<String> {

        //        private CardView mCardView;
        private TextView mTextView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
//            mCardView = (CardView) view.findViewById(R.id.group);
            mTextView = (TextView) view.findViewById(R.id.text);
            return view;
        }

        @Override
        public void onBind(Context context, int position, String data) {
            // 数据绑定
//            mCardView.setCardBackgroundColor(Color.parseColor(data));
            mTextView.setText(position + "：" + data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

}
