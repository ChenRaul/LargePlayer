package com.kt.largesreen.player.view;

import org.w3c.dom.Element;

import android.content.Context;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.kt.largesreen.player.data.MediaInfo;
import com.kt.largesreen.player.utils.Util;

public class MediaView extends AbsoluteLayout {

	private Context mContext;
	private Element mElement;
	private String[] mUrl;
	private SurfaceView mPreview;
	private SurfaceHolder holder;
	private MyVideoPlayer videoPlayer;
	private int mCount = 0;

	public MediaView(Context context, ViewGroup container, Element node,String filePath) {
		super(context);
		this.mContext = context;
		this.mElement = node;
		MediaInfo mediaInfo = Util.getMediaData(node);
		mUrl = mediaInfo.getSrc();
		mPreview = new SurfaceView(mContext);
		if(mUrl != null){
			videoPlayer = new MyVideoPlayer( mContext, mPreview, mUrl,filePath);
			setDisplay(container, mediaInfo);
		}
	}

	public void setDisplay(ViewGroup container, MediaInfo mediaInfo) {
		AbsoluteLayout.LayoutParams params_mVideoView = new AbsoluteLayout.LayoutParams(
				AbsoluteLayout.LayoutParams.MATCH_PARENT,
				AbsoluteLayout.LayoutParams.MATCH_PARENT, 0, 0);
		this.addView(mPreview, params_mVideoView);
		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(1,
				1, 1, 0);
		params.width = mediaInfo.getWidth();
		params.height = mediaInfo.getHeight();
		params.x = mediaInfo.getX();
		params.y = mediaInfo.getY();
		container.addView(this, params);
	}
}