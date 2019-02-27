package me.jingbin.sbannerview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import me.jingbin.sbannerview.config.OnBannerClickListener;
import me.jingbin.sbannerview.config.ScaleRightTransformer;
import me.jingbin.sbannerview.holder.BannerViewHolder;
import me.jingbin.sbannerview.holder.HolderCreator;

/**
 * @author jingbin
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SBannerView banner = findViewById(R.id.banner);

        final List<String> list = new ArrayList<>();
        list.add("地方撒发生0");
        list.add("地方撒发生1");
        list.add("地方撒发生2");
        list.add("地方撒发生3");
        list.add("地方撒发生4");
        list.add("地方撒发生5");
        banner.setAutoPlay(true)
//                .setBannerAnimation(Transformer.ScaleRight)
                .setBannerAnimation(ScaleRightTransformer.class)
                .setOffscreenPageLimit(list.size())
                .setDelayTime(3000)
                .setPages(list, new HolderCreator<BannerViewHolder>() {
                    @Override
                    public BannerViewHolder createViewHolder() {
                        return new CustomViewHolder();
                    }
                })
//                .setBannerStyle(BannerConfig.RIGHT)
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
            mTextView.setText(position + ":" + data);
        }
    }
}
