package com.kt.largesreen.player.view;

import org.w3c.dom.Element;

import com.kt.largesreen.player.data.MediaInfo;
import com.kt.largesreen.player.data.WebInfo;
import com.kt.largesreen.player.utils.Util;

import android.content.Context;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;

public class MyWebView extends AbsoluteLayout {
	private Context context;
	private Element element;
	private String[] mUrl;
	private WebView webView;
	public MyWebView(Context context, ViewGroup container, Element node) {
		super(context);
		this.context = context;
		this.element = node;
		WebInfo webInfo = Util.getWebData(node);
		mUrl = webInfo.getUrl();
		System.out.println("MyWebView: mUrl = "+ mUrl);
		if(mUrl != null){
			webView = new WebView(context);
			webView.getSettings().setJavaScriptEnabled(true);  
			webView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
	        //加载需要显示的网页  
			webView.loadUrl(mUrl[0]);  
	       // webview.loadUrl("content://com.android.htmlfileprovider/sdcard/ebansers/project/7/79f4b1cb-3581-4578-a92e-cbb0e359c2ad.html");
			setDisplay(container, webInfo);
		}
		// TODO Auto-generated constructor stub
	}
	private void setDisplay(ViewGroup container, WebInfo webInfo) {
		// TODO Auto-generated method stub
		AbsoluteLayout.LayoutParams params_mWebView = new AbsoluteLayout.LayoutParams(
				AbsoluteLayout.LayoutParams.MATCH_PARENT,
				AbsoluteLayout.LayoutParams.MATCH_PARENT, 0, 0);
		this.addView(webView, params_mWebView);
		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(1,
				1, 1, 0);
		params.width = webInfo.getWidth();
		params.height = webInfo.getHeight();
		params.x = webInfo.getX();
		params.y = webInfo.getY();
		container.addView(this, params);
	}

	
}
