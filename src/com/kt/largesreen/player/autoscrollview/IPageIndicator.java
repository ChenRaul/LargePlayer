package com.kt.largesreen.player.autoscrollview;

import android.support.v4.view.ViewPager;

/**
 * 自动滑动View的指示器接口
 */
public interface IPageIndicator extends ViewPager.OnPageChangeListener {
	/**
	 * 设置绑定对象ViewPager
	 */
	void setViewPager(ViewPager view);

	/**
	 * 设置绑定对象ViewPager，初始化位置
	 */
	void setViewPager(ViewPager view, int initialPosition);

	/**
	 * 设置ViewPager和Indicator的当前page
	 */
	void setCurrentItem(int item);

	/**
	 * 设置Page变化监听
	 */
	void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

	/**
	 * 通知indicator变化
	 */
	void notifyDataSetChanged();
}
