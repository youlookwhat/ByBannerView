### 示例图

![](https://github.com/youlookwhat/SBannerView/blob/master/sbannerview.gif)

[![](https://jitpack.io/v/youlookwhat/SBannerView.svg)](https://jitpack.io/#youlookwhat/SBannerView)

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
	com.github.youlookwhat:SBannerView:1.1.5
}
```

#### 功能
 - 1.默认滑动到最后一条时，往回轮播
 - 2.可设置右边距的轮播图，也可支持左右都有间距的轮播图
 - 3.支持正常轮播图，并往回轮播或者循环轮播

#### 混淆
```java
-keep class me.jingbin.sbanner.** {*;}
```