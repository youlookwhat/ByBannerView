package me.jingbin.sbanner.holder;

import android.content.Context;
import android.view.View;

public interface SBannerViewHolder<T> {

    /**
     * 创建View
     */
    View createView(Context context);

    /**
     * 绑定数据
     */
    void onBind(Context context, int position, T data);
}
