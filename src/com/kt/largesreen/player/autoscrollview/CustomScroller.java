package com.kt.largesreen.player.autoscrollview;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 自定义Scroller，自定义设置动画滑动时间
 */
public class CustomScroller extends Scroller {

	private int scrollDuration = 2000;
	public CustomScroller(Context context) {
		super(context);
	}

	public CustomScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	public void setScrollDurationFactor(int scrollFactor) {
		this.scrollDuration = scrollFactor;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, scrollDuration);
	}
}
