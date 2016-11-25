package com.kt.largesreen.player.view;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.widget.AbsoluteLayout;

import com.kt.largesreen.player.autoscrollview.AutoScrollViewPager;

public class GroupItemView extends AbsoluteLayout {

	private Context mContext;
	private Element mElement;
	private String filePath;

	public GroupItemView(Context context, Element node,String filePath) {
		super(context);
		this.mContext = context;
		this.mElement = node;
		this.filePath = filePath;
		parseElement(mElement);
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
			} else if ("GroupFolder".equalsIgnoreCase(name)) {
				new GroupView(mContext, this, node,filePath);
			}
		}
	}
}
