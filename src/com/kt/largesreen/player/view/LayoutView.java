package com.kt.largesreen.player.view;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.kt.largesreen.player.autoscrollview.AutoScrollViewPager;
import com.kt.largesreen.player.data.LayoutInfo;
import com.kt.largesreen.player.data.MediaInfo;
import com.kt.largesreen.player.data.TimeInfo;
import com.kt.largesreen.player.utils.Util;

public class LayoutView extends AbsoluteLayout {

	private Context mContext;
	private Element mElement;
	private String playTime;
	private String filePath;
	
	public LayoutView(Context context, ViewGroup container, Element element,String filePath) {
		super(context);
		this.mContext = context;
		this.mElement = element;
		this.filePath = filePath;
		parseElement(mElement);
		LayoutInfo layoutInfo = Util.getLayoutData(element);
		setDisplay(container, layoutInfo);
		
	}

	public void parseElement(Element element) {
		NodeList nodelist = element.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (!(nodelist.item(i) instanceof Element)) {
				continue;
			}
			Element node = (Element) nodelist.item(i);
			String name = node.getNodeName();
			if ("TextView".equalsIgnoreCase(name)) {
				new MyTextView(mContext, this, node);
			} else if ("ImageView".equalsIgnoreCase(name)) {
				new AutoScrollViewPager(mContext, this, node,filePath);
			} else if ("VideoView".equalsIgnoreCase(name)) {
				new MediaView(mContext, this, node,filePath);
			} else if ("GroupView".equalsIgnoreCase(name)) {
				new GroupView(mContext, this, node,filePath);
			} else if ("Layout".equalsIgnoreCase(name)) { // Layout嵌套Layout的情况
				AbsoluteLayout layout = new LayoutView(mContext, this, node,filePath);
			}else if("PlayTime".equalsIgnoreCase(name)){
				//解析TimeView获取时间
				TimeInfo timeInfo = Util.getTimeData(node);
				setPlayTime(timeInfo.getPlayTime());
			}else if("WebView".equalsIgnoreCase(name)){
				new MyWebView(mContext, this, node);
			}
		}
	}

	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

	@SuppressLint("NewApi")
	public void setDisplay(ViewGroup container, LayoutInfo layoutInfo) {
		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(1,
				1, 1, 0);
		params.width = layoutInfo.getWidth();
		params.height = layoutInfo.getHeight();
		params.x = layoutInfo.getX();
		params.y = layoutInfo.getY();
		System.out.println("LayoutView 宽 长 x y ："+params.width+"--"+params.height+"--"+params.x+"--"+params.y);
		if (layoutInfo.getBackground() != null) {
			Drawable drawable = new BitmapDrawable(Util.getImage(filePath
					+ layoutInfo.getBackground(), 1));
			this.setBackground(drawable);
		}
		container.addView(this, params);
	}
}
