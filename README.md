### 示例图

![](https://github.com/youlookwhat/SBannerView/blob/master/sbannerview.gif)

[![](https://jitpack.io/v/youlookwhat/ByBannerView.svg)](https://jitpack.io/#youlookwhat/ByBannerView)


### 重大更新!!
> 上个版本1.1.7，如果是左右留边的banner样式，首次进去会有黏在一起的情况，但是没有人反馈，所以我认为这个库基本除了我没人使用，哈哈。如果有人使用看这里就好。

- 1.更改依赖库名：`com.github.youlookwhat:ByBannerView:1.3.0`
- 2.xml引入更换：`<me.jingbin.banner.ByBannerView/>`
- 3.混淆修正：`-keep class me.jingbin.banner.** {*;}`
- 4.如果默认是返回轮播，查看是否添加属性：`app:is_back_loop="true"`


#### how to use
Step 1. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
     repositories {
          ...
          maven { url 'https://jitpack.io' }
     }
}
```

Step 2. Add the dependency

```java
dependencies {
	implementation 'com.github.youlookwhat:ByBannerView:Tag'
}
```


#### 功能
 - 1.默认滑动到最后一条时，往回轮播
 - 2.可设置右边距的轮播图，也可支持左右都有间距的轮播图
 - 3.支持正常轮播图，并往回轮播或者循环轮播

#### 属性解释

|  属性   | 类型  | 默认值 | 属性说明 |
|  ----  | ----  | ---- | --- |
| delay_time  | integer | 2000ms | 延迟多少毫秒开始滚动 |
| scroll_time  | integer | 800ms | 滚动一页需要多少毫秒|
| is_auto_play  | boolean | true | 是否自动滚动 |
| is_loop  | boolean | true | 是否无限滚动，false则滚动到最后一个时停止滚动 |
| is_back_loop  | boolean |true|**滑到到最后一个时，是否返回滑动，false则循环播放**|
| indicator_width  | dimension | DisplayWidth / 80 | 指示器的宽度 |
| indicator_height  | dimension | DisplayWidth / 80 | 指示器的高度 |
| indicator_margin  | dimension | 10dp | 指示器距banner最底部的距离 |
| indicator_padding  | dimension | 5dp | 指示器之间的左右边距 |
| indicator_drawable_selected  | reference | gray_radius.xml | 选中的指示器样式 |
| indicator_drawable_unselected  | reference | white_radius.xml | 未选中的指示器样式 |
| page_left_margin  | dimension | 0 | banner距屏幕的左边距 |
| page_right_margin  | dimension | 0 | banner距屏幕的右边距 |

其他方法：

 - `setIndicatorGravity(int type)`: 设置指示器的位置 (BannerConfig.LEFT/CENTER/RIGHT)，默认居中`CENTER`
 - `setBannerStyle(int bannerStyle)`: 设置指示器样式 (默认`BannerConfig.CIRCLE_INDICATOR`)
 	 - NOT_INDICATOR:    取消指示器
    - CIRCLE_INDICATOR: 自带的指示器
    - CUSTOM_INDICATOR: 手动设置的指示器，不规定指示器宽高，随指示器自身的宽高

#### 使用示例
```xml
<me.jingbin.sbanner.SBannerView
    android:id="@+id/banner"
    android:layout_width="match_parent"
    android:layout_height="330dp"
    app:indicator_height="6dp"
    app:indicator_margin="15dp"
    app:indicator_padding="6dp"
    app:indicator_width="6dp" />
```

```java
banner.setPageRightMargin(dip2px(this, 59))
        .setBannerAnimation(ScaleRightTransformer.class)
        .setOffscreenPageLimit(list.size())
        .setDelayTime(3000)
        .setPages(list, new HolderCreator<SBannerViewHolder>() {
            @Override
            public SBannerViewHolder createViewHolder() {
                return new CustomViewHolder();
            }
        })
        .start();
banner.setOnBannerClickListener(new OnBannerClickListener() {
    @Override
    public void onBannerClick(int position) {
        
    }
});


class CustomViewHolder implements SBannerViewHolder<BannerItemBean> {

    private TextView mTextView;

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
        mTextView = (TextView) view.findViewById(R.id.text);
        return view;
    }

    @Override
    public void onBind(Context context, int position, BannerItemBean data) {
        
    }
}
```

#### 混淆
```java
-keep class me.jingbin.banner.** {*;}
```