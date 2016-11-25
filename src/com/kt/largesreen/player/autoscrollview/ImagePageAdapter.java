package com.kt.largesreen.player.autoscrollview;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kt.largesreen.player.MainActivity;
import com.kt.largesreen.player.data.ImgInfo;
import com.kt.largesreen.player.utils.Util;

/**
 * ImagePagerAdapter
 */
public class ImagePageAdapter extends RecyclingPagerAdapter {

	private Context context;
	private List<String> imageIdList;

	private int size;
	private boolean isInfiniteLoop;
	private ImgInfo imgInfo;
	public ImagePageAdapter(Context context, List<String> imageIdList,ImgInfo imgInfo) {
		this.context = context;
		this.imageIdList = imageIdList;
		this.size = Util.getSize(imageIdList);
		this.imgInfo = imgInfo;
		isInfiniteLoop =imgInfo.getIsInfiniteLoop() ;
	}

	@Override
	public int getCount() {
		return isInfiniteLoop ? Integer.MAX_VALUE : Util.getSize(imageIdList);
	}

	private int getPosition(int position) {
		return isInfiniteLoop ? position % size : position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup container) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = holder.imageView = new ImageView(context);
			holder.imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 设置点击方法
					String url =  imgInfo.getButtonUrl();
					if(url == null){
						
					}else{
						Uri uri = Uri.parse(url);
				        Intent intent = new Intent(Intent.ACTION_VIEW, uri);		        
//				       //intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
				        context.startActivity(intent);
				       
					}
					
				}
			});
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.imageView.setImageBitmap(Util.getImage(
				imageIdList.get(getPosition(position)), 1));
		holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		return view;
	}

	private static class ViewHolder {

		ImageView imageView;
	}

	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	public ImagePageAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}

}
