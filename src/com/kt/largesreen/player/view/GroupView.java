package com.kt.largesreen.player.view;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ViewFlipper;

import com.kt.largesreen.player.R;
import com.kt.largesreen.player.data.GroupInfo;
import com.kt.largesreen.player.utils.Util;

public class GroupView extends AbsoluteLayout {

	private String TAG = "GroupView -> ";
	private Context mContext;
	private Element mElement;
	private List<View> mViews;
	private static final int DURING_TIME = 5000;
	private ViewFlipper mFlipper;
	private String filePath;
	
	public GroupView(Context context, ViewGroup container, Element node,String filePath) {
		super(context);
		this.mContext = context;
		this.mElement = node;
		this.filePath = filePath;
		mViews = parseElement(mElement);
		mFlipper = new ViewFlipper(mContext);
		if (mViews != null) {
			for (View view : mViews) {
				mFlipper.addView(view);
			}
			mFlipper.setDisplayedChild(0);
			GroupInfo groupFolderInfo = Util.getGroupData(node);
			setDispaly(container, groupFolderInfo);
			if (mFlipper.getChildCount() >= 2) {
				if (!myThread.isAlive()) {
					myThread.start();
				}
			}
		}
	}

	public List<View> parseElement(Element element) {
		List<View> views = new ArrayList<View>();
		NodeList nodelist = element.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (!(nodelist.item(i) instanceof Element)) {
				continue;
			}
			Element node = (Element) nodelist.item(i);
			String name = node.getNodeName();
			if ("GruopItem".equalsIgnoreCase(name)) {
				View view = new GroupItemView(mContext, node,filePath);
				// int during = Util.getDuringData(node);
				views.add(view);
			}
		}
		return views;
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			turnPage(true);
		};
	};

	private Thread myThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true) {
				try {
					myHandler.sendMessage(new Message());
					Thread.sleep(DURING_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});

	public void turnPage(final boolean isNextPage) {
		if (isNextPage) {
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_left_in));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_left_out));
			mFlipper.showNext();
		} else {
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_right_in));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_right_out));
			mFlipper.showPrevious();
		}
	}

	@SuppressLint("NewApi")
	public void setDispaly(ViewGroup container, GroupInfo groupFolderInfo) {
		if (groupFolderInfo.getBackground() != null) {
			Drawable drawable = new BitmapDrawable(Util.getImage(
					filePath+ groupFolderInfo.getBackground(), 1));
			this.setBackground(drawable);
		}
		AbsoluteLayout.LayoutParams params_mFlipper = new AbsoluteLayout.LayoutParams(
				AbsoluteLayout.LayoutParams.MATCH_PARENT,
				AbsoluteLayout.LayoutParams.MATCH_PARENT, 0, 0);
		this.addView(mFlipper, params_mFlipper);
		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(1,
				1, 1, 0);
		params.width = groupFolderInfo.getWidth();
		params.height = groupFolderInfo.getHeight();
		params.x = groupFolderInfo.getX();
		params.y = groupFolderInfo.getY();
		container.addView(this, params);
	}
}
