package me.jingbin.banner.holder;

/**
 * Created by songwenchao
 * on 2018/5/16 0016.
 * <p>
 * 类名
 * 需要 --
 * 可以 --
 */
public interface HolderCreator<VH extends ByBannerViewHolder> {

    /**
     * 创建ViewHolder
     */
    VH createViewHolder();
}
