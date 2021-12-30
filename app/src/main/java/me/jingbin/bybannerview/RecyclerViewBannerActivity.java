package me.jingbin.bybannerview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.ByRecyclerView;
import me.jingbin.library.adapter.BaseByViewHolder;
import me.jingbin.library.adapter.BaseRecyclerAdapter;
import me.jingbin.banner.ByBannerView;
import me.jingbin.banner.config.OnBannerClickListener;
import me.jingbin.banner.config.ScaleRightTransformer;
import me.jingbin.banner.holder.HolderCreator;
import me.jingbin.banner.holder.ByBannerViewHolder;

/**
 * @author jingbin
 */
public class RecyclerViewBannerActivity extends AppCompatActivity {

    private ByBannerView banner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv);

        ByRecyclerView recyclerView = findViewById(R.id.recyclerView);
        View inflate = LayoutInflater.from(this).inflate(R.layout.header_banner, (ViewGroup) recyclerView.getParent(), false);
        banner2 = inflate.findViewById(R.id.banner2);
        recyclerView.addHeaderView(inflate);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            strings.add("item");
        }
        recyclerView.setAdapter(new BaseRecyclerAdapter<String>(R.layout.layout_by_default_item_skeleton, strings) {
            @Override
            protected void bindView(BaseByViewHolder<String> baseByViewHolder, String s, int i) {

            }
        });
        recyclerView.setOnItemClickListener(new ByRecyclerView.OnItemClickListener() {
            @Override
            public void onClick(View view, int i) {
                banner2.update(MainActivity.getList(4));
            }
        });
        setBanner2View(MainActivity.getList(5));

    }

    private void setBanner2View(final List<BannerItemBean> list) {
        banner2
//                .setAutoPlay(true)
//                .setBannerStyle(BannerConfig.NOT_INDICATOR)
//                .setBannerAnimation(ScaleRightTransformer.class)
                .setOffscreenPageLimit(list.size())
                .setDelayTime(3000)
                .setPages(list, new HolderCreator<ByBannerViewHolder>() {
                    @Override
                    public ByBannerViewHolder createViewHolder() {
                        return new MainActivity.CustomViewHolder2();
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
}
