package com.kt.largesreen.player.autoscrollview;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsoluteLayout;

import com.kt.largesreen.player.data.ImgInfo;
import com.kt.largesreen.player.utils.Util;

public class AutoScrollViewPager extends ViewPager {

	private Context mContext;
	public static final int DEFAULT_INTERVAL = 1500;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int SLIDE_BORDER_MODE_NONE = 0;
	public static final int SLIDE_BORDER_MODE_CYCLE = 1;
	public static final int SLIDE_BORDER_MODE_TO_PARENT = 2;
	private long interval = DEFAULT_INTERVAL;
	private int direction = RIGHT;
	private boolean isCycle = true;
	private boolean stopScrollWhenTouch = true;
	private int slideBorderMode = SLIDE_BORDER_MODE_NONE;
	private boolean isBorderAnimation = true;
	/** 自动滑动动画持续时间，默认2000ms **/
	private int autoScrollDuration = DEFAULT_INTERVAL;
	/** 手动滑动动画持续时间，默认2000ms **/
	private int swipeScrollFactor = DEFAULT_INTERVAL;
	private Handler handler;
	private boolean isAutoScroll = false;
	private boolean isStopByTouch = false;
	private float touchX = 0f;
	private float downX = 0f;
	private CustomScroller scroller = null;
	public static final int SCROLL_WHAT = 0;
	private ImgInfo imgInfo;
	private String filePath;
	
	public AutoScrollViewPager(Context context, ViewGroup container,
			Element node,String filePath) {
		super(context);
		this.mContext = context;
		this.filePath = filePath;
		init();
		imgInfo = Util.getImgData(node);
		setDisplay(mContext, container, imgInfo);
	}

	private void init() {
		handler = new MyHandler();
		setViewPagerScroller();
	}

	public void startAutoScroll() {
		isAutoScroll = true;
		System.out.println("-----------"+interval+scroller.getDuration() / autoScrollDuration * swipeScrollFactor);
		sendScrollMessage((long) (interval+scroller.getDuration() / autoScrollDuration * swipeScrollFactor));
	}

	public void startAutoScroll(int delayTimeInMills) {
		isAutoScroll = true;
		sendScrollMessage(delayTimeInMills);
	}

	public void stopAutoScroll() {
		isAutoScroll = false;
		handler.removeMessages(SCROLL_WHAT);
	}

	public void setSwipeScrollDurationFactor(int scrollFactor) {
		swipeScrollFactor = scrollFactor;
	}

	public void setAutoScrollDurationFactor(int scrollFactor) {
		autoScrollDuration = scrollFactor;
	}

	private void sendScrollMessage(long delayTimeInMills) {
		handler.removeMessages(SCROLL_WHAT);
		//延迟delayTimeInMills秒钟再发消息，
		handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
	}

	private void setViewPagerScroller() {
		try {
			Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
			scrollerField.setAccessible(true);
			Field interpolatorField = ViewPager.class
					.getDeclaredField("sInterpolator");
			interpolatorField.setAccessible(true);
			scroller = new CustomScroller(getContext(),
					(Interpolator) interpolatorField.get(null));
			scrollerField.set(this, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void scrollOnce() {
		PagerAdapter adapter = getAdapter();
		int currentItem = getCurrentItem();
		int totalCount;
		 System.out.println(adapter+"---"+adapter.getCount());
	        System.out.println("currentItem="+currentItem);
		if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
			return;
		}
		int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
		System.out.println("nextItem ="+ nextItem );
		if (nextItem < 0) {
			if (isCycle) {
				setCurrentItem(totalCount - 1, isBorderAnimation);
			}
		} else if (nextItem == totalCount) {
			if (isCycle) {
				
				//TODO
				setCurrentItem(0, isBorderAnimation);
			}
		} else {
			setCurrentItem(nextItem, true);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev);

		if (stopScrollWhenTouch) {
			if ((action == MotionEvent.ACTION_DOWN) && isAutoScroll) {
				isStopByTouch = true;
				stopAutoScroll();
			} else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
				startAutoScroll();
			}
		}
		if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT
				|| slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
			touchX = ev.getX();
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				downX = touchX;
			}
			int currentItem = getCurrentItem();
			PagerAdapter adapter = getAdapter();
			int pageCount = adapter == null ? 0 : adapter.getCount();
			if ((currentItem == 0 && downX <= touchX)
					|| (currentItem == pageCount - 1 && downX >= touchX)) {
				if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					if (pageCount > 1) {
						setCurrentItem(pageCount - currentItem - 1,
								isBorderAnimation);
					}
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				return super.dispatchTouchEvent(ev);
			}
		}
		getParent().requestDisallowInterceptTouchEvent(true);

		return super.dispatchTouchEvent(ev);
	}

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SCROLL_WHAT:
				scroller.setScrollDurationFactor(autoScrollDuration);
				scrollOnce();
				scroller.setScrollDurationFactor(swipeScrollFactor);
				System.out.println("**********"+interval + scroller.getDuration());
				sendScrollMessage(interval + scroller.getDuration());
				
			default:
				break;
			}
		}
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public int getDirection() {
		return (direction == LEFT) ? LEFT : RIGHT;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public boolean isCycle() {
		return isCycle;
	}

	public void setCycle(boolean isCycle) {
		this.isCycle = isCycle;
	}

	public boolean isStopScrollWhenTouch() {
		return stopScrollWhenTouch;
	}

	public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
		this.stopScrollWhenTouch = stopScrollWhenTouch;
	}

	public int getSlideBorderMode() {
		return slideBorderMode;
	}

	public void setSlideBorderMode(int slideBorderMode) {
		this.slideBorderMode = slideBorderMode;
	}

	public boolean isBorderAnimation() {
		return isBorderAnimation;
	}

	public void setBorderAnimation(boolean isBorderAnimation) {
		this.isBorderAnimation = isBorderAnimation;
	}

	public void setDisplay(Context context, ViewGroup container, ImgInfo imgInfo) {
		if (imgInfo == null) {
			return;
		}
		// CirclePageIndicator indicator = new CirclePageIndicator(context);
		List<String> imageList = new ArrayList<String>();
		for (String str : imgInfo.getSrc()) {
			System.out.println(filePath + str);
			imageList.add(filePath + str);
		}
		ImagePageAdapter imageAdapter = new ImagePageAdapter(context, imageList,imgInfo);
		this.setAdapter(imageAdapter);
		// indicator.setViewPager(viewPager);
		// indicator.setRadius(10);
		// indicator.setOrientation(LinearLayout.HORIZONTAL);
		// indicator.setStrokeWidth(8);
		// indicator.setSnap(true);
		if (imgInfo.getInterval() != 0) {
			this.setInterval(imgInfo.getInterval());
		}
		this.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
		// this.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_NONE);
		// this.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_TO_PARENT);
		this.setCycle(true);
		this.setBorderAnimation(true);
		this.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % Util.getSize(imageList));
		System.out.println(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2%Util.getSize(imageList));
		this.startAutoScroll();
		this.setAutoScrollDurationFactor(3000);
		// this.setAutoScrollDurationFactor(3000);
		// this.setSwipeScrollDurationFactor(5000);
		if ("zoom_out".equalsIgnoreCase(imgInfo.getAnim())) {
			this.setPageTransformer(true, new AnimPageTransformer(1));
		} else {
			this.setPageTransformer(true, new AnimPageTransformer(0));
		}

		AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(1,
				1, 1, 0);
		param.width = imgInfo.getWidth();
		param.height = imgInfo.getHeight();
		param.x = imgInfo.getX();
		param.y = imgInfo.getY();
		container.addView(this, param);
	}
}
