package com.kt.largesreen.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

//全屏实现播放视频内容，关键是复写onMeasure方法；
public class FullScreenVideoView extends VideoView{

	public FullScreenVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FullScreenVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
