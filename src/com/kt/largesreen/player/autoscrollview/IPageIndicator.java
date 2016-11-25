package com.kt.largesreen.player.autoscrollview;

import android.support.v4.view.ViewPager;

/**
 * �Զ�����View��ָʾ���ӿ�
 */
public interface IPageIndicator extends ViewPager.OnPageChangeListener {
	/**
	 * ���ð󶨶���ViewPager
	 */
	void setViewPager(ViewPager view);

	/**
	 * ���ð󶨶���ViewPager����ʼ��λ��
	 */
	void setViewPager(ViewPager view, int initialPosition);

	/**
	 * ����ViewPager��Indicator�ĵ�ǰpage
	 */
	void setCurrentItem(int item);

	/**
	 * ����Page�仯����
	 */
	void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

	/**
	 * ֪ͨindicator�仯
	 */
	void notifyDataSetChanged();
}
