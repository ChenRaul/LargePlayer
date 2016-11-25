package com.kt.largesreen.player.data;

public class ImgInfo {
	private int x;
	private int y;
	private int width;
	private int height;
	private String[] src;
	private String anim;
	private int frequence;
	private int interval;
	private String buttonUrl;
	private boolean isInfiniteLoop;
	
	
	public boolean getIsInfiniteLoop() {
		return isInfiniteLoop;
	}
	public void setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
	}
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return the src
	 */
	public String[] getSrc() {
		return src;
	}
	/**
	 * @param src the src to set
	 */
	public void setSrc(String[] src) {
		this.src = src;
	}
	/**
	 * @return the anim
	 */
	public String getAnim() {
		return anim;
	}
	/**
	 * @param anim the anim to set
	 */
	public void setAnim(String anim) {
		this.anim = anim;
	}
	/**
	 * @return the frequence
	 */
	public int getFrequence() {
		return frequence;
	}
	/**
	 * @param frequence the frequence to set
	 */
	public void setFrequence(int frequence) {
		this.frequence = frequence;
	}
	/**
	 * @return the interval
	 */
	public int getInterval() {
		return interval;
	}
	/**
	 * @param interval the interval to set
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public String getButtonUrl() {
		return buttonUrl;
	}
	public void setButtonUrl(String buttonUrl) {
		this.buttonUrl = buttonUrl;
	}
}