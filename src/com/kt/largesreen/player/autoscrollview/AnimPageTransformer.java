package com.kt.largesreen.player.autoscrollview;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 * 定义图片切换动画的类
 * 
 **/
public class AnimPageTransformer implements PageTransformer {

	private static final float MIN_SCALE = 0.75f;
	private static final float MIN_ALPHA = 0.5f;
	private int animType = 0;

	public AnimPageTransformer(int animType) {
		super();
		this.animType = animType;
	}

	public void transformPage(View view, float position) {
		switch (animType) {
		case 0:
			Standard(view, position);
			break;
		case 1:
			ZoomOut(view, position);
			break;
		default:
			Standard(view, position);
			break;
		}

	}

	public void Standard(View view, float position) {
		int pageWidth = view.getWidth();

		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.
			view.setAlpha(0);

		} else if (position <= 0) { // [-1,0]
			// Use the default slide transition when moving to the left page
			view.setAlpha(1);
			view.setTranslationX(0);
			view.setScaleX(1);
			view.setScaleY(1);

		} else if (position <= 1) { // (0,1]
			// Fade the page out.
			view.setAlpha(1 - position);

			// Counteract the default slide transition
			view.setTranslationX(pageWidth * -position);

			// Scale the page down (between MIN_SCALE and 1)
			float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
					* (1 - Math.abs(position));
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);

		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			view.setAlpha(0);
		}
	}

	public void ZoomOut(View view, float position) {
		int pageWidth = view.getWidth();
		int pageHeight = view.getHeight();

		if (position < -1) { // [-Infinity,-1)
								// This page is way off-screen to the left.
			view.setAlpha(0);

		} else if (position <= 1) // a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
		{ // [-1,1]
			// Modify the default slide transition to shrink the page as well
			float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
			float vertMargin = pageHeight * (1 - scaleFactor) / 2;
			float horzMargin = pageWidth * (1 - scaleFactor) / 2;
			if (position < 0) {
				view.setTranslationX(horzMargin - vertMargin / 2);
			} else {
				view.setTranslationX(-horzMargin + vertMargin / 2);
			}

			// Scale the page down (between MIN_SCALE and 1)
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);

			// Fade the page relative to its size.
			view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
					/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));

		} else { // (1,+Infinity]
					// This page is way off-screen to the right.
			view.setAlpha(0);
		}
	}
	public void DepthPageTransformer(View view, float position){
	    float MIN_SCALE = 0.75f;
	    int pageWidth = view.getWidth();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 0) { // [-1,0]
	            // Use the default slide transition when moving to the left page
	            view.setAlpha(1);
	            view.setTranslationX(0);
	            view.setScaleX(1);
	            view.setScaleY(1);

	        } else if (position <= 1) { // (0,1]
	            // Fade the page out.
	            view.setAlpha(1 - position);

	            // Counteract the default slide transition
	            view.setTranslationX(pageWidth * -position);

	            // Scale the page down (between MIN_SCALE and 1)
	            float scaleFactor = MIN_SCALE
	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	

}