package com.kt.largesreen.player.view;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kt.largesreen.player.utils.Util;

public class MyVideoPlayer implements
		android.media.MediaPlayer.OnPreparedListener,
		android.view.SurfaceHolder.Callback {

	private Context mContext;
	public MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private String[] mUrl;
	private String filePath;
	private int videoCount = 0;

	public MyVideoPlayer(Context context, SurfaceView surfaceview, String[] url,String filePath) {
		mContext = context;
		mUrl = url;
		this.filePath = filePath;
		surfaceHolder = surfaceview.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}


	@Override
	public void onPrepared(MediaPlayer mediaplayer) {
		
	}

	public void playVideo() {
		try {
			mediaPlayer.reset();
			mediaPlayer.setLooping(true);
			mediaPlayer.setDataSource(filePath+mUrl[videoCount]);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException illegalargumentexception) {
			illegalargumentexception.printStackTrace();
			return;
		} catch (IllegalStateException illegalstateexception) {
			illegalstateexception.printStackTrace();
			return;
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
	}

	public void stopMPlay() {
		try {
			mediaPlayer.release();
			return;
		} catch (Exception exception) {
			return;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		holder.setFixedSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDisplay(surfaceHolder);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				playVideo();
				return false;
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				System.out.println("播放完成正在开始新的八方");
				try {
					 
					if(mUrl.length > 1){
						videoCount++;
						videoCount = videoCount%mUrl.length;
						
					}
					mediaPlayer.reset();
					System.out.println("MyVideoView :filepath "+ filePath);
					mediaPlayer.setDataSource(filePath+mUrl[videoCount]);
					mediaPlayer.prepare();
					mediaPlayer.start();
					System.out.println("第二次播放");
					return;
				} catch (IllegalArgumentException illegalargumentexception) {
					illegalargumentexception.printStackTrace();
					return;
				} catch (IllegalStateException illegalstateexception) {
					illegalstateexception.printStackTrace();
					return;
				} catch (IOException ioexception) {
					ioexception.printStackTrace();
				}
			}
		});
		playVideo();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mediaPlayer.release();
	}

}
