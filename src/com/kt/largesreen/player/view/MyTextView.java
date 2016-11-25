package com.kt.largesreen.player.view;

import org.w3c.dom.Element;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.TextView;

import com.kt.largesreen.player.R;
import com.kt.largesreen.player.data.TextInfo;
import com.kt.largesreen.player.utils.Util;

public class MyTextView extends TextView {

	private Context mContext;
	private WindowManager mWm;
	private String text = "";// 文本内容
	private String[] textArray = null;
	private int size = 50;// 文本大小
	private float baseY = 0f;// 文字的纵坐标
	private float baseX = 0f;// 不滚动时的横坐标
	private float viewWidth = 0f;// 控件宽度
	private float textWidth = 0f;// 文本长度
	private Paint paint = null;// 绘图样式
	private float step = 0f;// 文字的横坐标
	private float temp_width1 = 0.0f;// 用于计算的临时变量
	private float temp_width2 = 0.0f;// 用于计算的临时变量
	private boolean isStartScroll = false ;// 是否滚动
	private boolean first = true;
	private int color = Color.WHITE;
	private int width = 0;
	private int height = 0;
	private Animation anim = null;
	private String animType = null;
	private int frequence = 2000;
	private boolean singleLine = false;
	private int interval = 3000;
	private Handler handler;
	private Message message;
	private int count;

	public MyTextView(Context context, ViewGroup container, Element node) {
		super(context);
		this.mContext = context;
		mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		init(mWm);
		TextInfo textInfo = Util.getTextData(node);
		setDisplay(container, textInfo);
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(WindowManager windowManager) {
		paint = getPaint();
		paint.setColor(color);
		paint.setTextSize(size);
		textWidth = paint.measureText(text);
		viewWidth = width;
		if (viewWidth == 0) {
			if (windowManager != null) {
				Display display = windowManager.getDefaultDisplay();
				viewWidth = display.getWidth();
			}
		}
		if (textWidth > viewWidth) {
			isStartScroll = true;
		}
		step = textWidth;
		temp_width1 = viewWidth + textWidth;
		temp_width2 = viewWidth + textWidth * 2;
		// baseY = getTextSize() + getPaddingTop();
		// baseX = getPaddingLeft();
		baseY = getTextSize();
		baseX = 0;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (width != 0 && height != 0) {
			setMeasuredDimension(width, height);
		} else {
			width = MeasureSpec.getSize(widthMeasureSpec);
			height = MeasureSpec.getSize(heightMeasureSpec);
			Log.i("Text", "width:" + width + ",height" + height);
		}
	}

	public void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		if (first) {
			viewWidth = getWidth();
			Log.i("Text", "width=" + viewWidth);
			first = false;
		}
		drawText(canvas, text);
	}

	public void drawText(Canvas canvas, String str) {
		// Log.i("Value", "size-------:"+size);
		if (!singleLine) {//一个文本框完整显示一段内容文字，如果singleLine为fasle
			int lineCount = 0;
			// text.replaceAll("\n", "\r\n");
			if (str == null) {
				return;
			}
			char[] textCharArray = str.toCharArray();
			// 已绘的宽度
			float drawedWidth = 0;
			float charWidth;
			float textShowWidth = getWidth();
			for (int i = 0; i < textCharArray.length; i++) {
				charWidth = paint.measureText(textCharArray, i, 1);
				if (textCharArray[i] == '\n') {
					lineCount++;
					drawedWidth = 0;
					continue;
				}
				if (textShowWidth - drawedWidth < charWidth) {
					lineCount++;
					drawedWidth = 0;
				}
				canvas.drawText(textCharArray, i, 1, drawedWidth,
						(lineCount + 1) * size, paint);
				drawedWidth += charWidth;
			}
			setHeight(((lineCount + 1) * (int) size) + 10);
		} else {
			if (!isStartScroll) {//直接显示单行从坐标X为baseX为起点
				canvas.drawText(str, baseX, baseY, paint);
				return;
			} else {//跑马灯
				canvas.drawText(str, temp_width1 - step, baseY, paint);
				step += 3.0;
				if (step > temp_width2)
					step = textWidth;
				invalidate();
			}
		}
	}

	public void setText(String[] str) {
		if (str.length == 0) {
			return;
		}
		this.text = str[0];
		this.textArray = str;
		textWidth = paint.measureText(this.text);
		// if (textWidth > viewWidth) {
		// isStarting = true;
		// } else {
		// isStarting = false;
		// }
		step = textWidth;
		temp_width1 = viewWidth + textWidth;
		temp_width2 = viewWidth + textWidth * 2;
		baseX = getPaddingLeft();
		baseY = getTextSize() + getPaddingTop();
		invalidate();
		if (this.textArray.length > 1) {
			count = 1;
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (count >= textArray.length) {
						count = 0;
					}
					text = textArray[count];
					count++;
					setAnimatiom(animType, frequence);
					invalidate();
					super.handleMessage(msg);
				}
			};
			new Thread(new MyThread()).start();
		}
	}

	public void startScroll() {
		isStartScroll = true;
		invalidate();
	}

	public void stopScroll() {
		isStartScroll = false;
		invalidate();
	}

	public void setSize(int size) {
		Log.i("Value", "size-------:" + size);
		if (size == 0) {
			return;
		}
		// DisplayParams displayParams = DisplayParams.getInstance(context);
		// this.size = DisplayUtil.sp2px(size, displayParams.fontScale);
		this.size = size;
		init(mWm);
		invalidate();
	}

	public void setColor(int color) {
		if (color == 0) {
			return;
		}
		this.color = color;
		init(mWm);
		invalidate();
	}

	public void setTextViewSize(int width, int height) {
		this.width = width;
		this.height = height;
		invalidate();
	}

	public void setStyle(String font) {
		if (font == null) {
			return;
		}
		if ("bold".equalsIgnoreCase(font)) {
			paint.setFakeBoldText(true);
		} else if ("italic".equalsIgnoreCase(font)) {
			paint.setTextSkewX(-0.5f);
		} else if ("bolditalic".equalsIgnoreCase(font)) {
			paint.setFakeBoldText(true);
			paint.setTextSkewX(-0.5f);
		}
		invalidate();
	}

	public void setAnimatiom(String animType, int frequence) {
		if (animType == null || frequence == 0) {
			return;
		}
		this.animType = animType;
		this.frequence = frequence;
		if ("horizontal_to_left".equalsIgnoreCase(animType)) {
			anim = AnimationUtils.loadAnimation(mContext,
					R.anim.horizontal_to_left);
			anim.setDuration(frequence);
			this.setAnimation(anim);
		} else if ("horizontal_to_right".equalsIgnoreCase(animType)) {
			anim = AnimationUtils.loadAnimation(mContext,
					R.anim.horizontal_to_right);
			anim.setDuration(frequence);
			this.setAnimation(anim);
		} else if ("vertical_to_up".equalsIgnoreCase(animType)) {
			anim = AnimationUtils
					.loadAnimation(mContext, R.anim.vertical_to_up);
			anim.setDuration(frequence);
			this.setAnimation(anim);
		} else if ("vertical_to_down".equalsIgnoreCase(animType)) {
			anim = AnimationUtils.loadAnimation(mContext,
					R.anim.vertical_to_down);
			anim.setDuration(frequence);
			this.setAnimation(anim);
		} else if ("fade_out_in".equalsIgnoreCase(animType)) {
			anim = AnimationUtils.loadAnimation(mContext, R.anim.fade_out_in);
			anim.setDuration(frequence);
			this.setAnimation(anim);
		}
	}

	public void setSingleLine(boolean singleLine) {
		this.singleLine = singleLine;
	}

	public void setInterval(int interval) {
		if (interval == 0) {
			return;
		}
		this.interval = interval;
	}
	
	public void setIsStartScrool(boolean isStartScroll) {
		this.isStartScroll = isStartScroll;
	}

	public void setParams(int width, int height, int x, int y) {
		setTextViewSize(width, height);
		this.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x,
				y));
	}

	public class MyThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Thread.sleep(interval);
					message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void setDisplay(ViewGroup container, TextInfo textInfo) {
		if (textInfo == null) {
			return;
		}
		this.setText(textInfo.getText());
		this.setParams(textInfo.getWidth(), textInfo.getHeight(), textInfo.getX(), textInfo.getY());
		this.setSize(textInfo.getSize());
		if (textInfo.getColor() != null) {
			this.setColor(Color.parseColor(textInfo.getColor()));
		}
		this.setStyle(textInfo.getStyle());
		if (textInfo.getBackground() != null) {
			this.setBackgroundColor(Color.parseColor(textInfo.getBackground()));
		}
		
		this.setSingleLine(textInfo.getSingleline());
		this.setInterval(textInfo.getInterval());
		if(textInfo.getStartScroll()){
			this.setAnimatiom(null, textInfo.getFrequence());
		}else{
			this.setAnimatiom(textInfo.getAnim(), textInfo.getFrequence());
		}
		this.setIsStartScrool(textInfo.getStartScroll());
		container.addView(this);
	}

}
