package com.kt.largesreen.player;

import java.io.File;
import java.net.MalformedURLException;

import com.kt.largescreen.lib.NetAndServerDetection;
import com.kt.largescreen.lib.ServersUrlPath;
import com.kt.largesreen.player.FullScreenVideoView;
import com.kt.largesreen.player.utils.LoginAuthentication;
import com.kt.largesreen.player.utils.USBBroadCastReceiver;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.Toast;

public class VideoGasketPlayActivity extends Activity implements OnCompletionListener,OnErrorListener{

	private String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	private  String videoGasketPath;
	com.kt.largesreen.player.FullScreenVideoView videoView;
	private USBBroadCastReceiver mBroadcastReceiver;
	private Boolean flag = true;
	private String server_ip;
	private NetAndServerDetection nasd = new NetAndServerDetection(this,this);
	private LoginAuthentication la = new LoginAuthentication(this, this);
	private int touchCount = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videogasket_play_main);
		
		usbBroadCast();
		
		videoView= (FullScreenVideoView)this.findViewById(R.id.gasket); 
		videoView.setOnCompletionListener(this);
		videoView.setMediaController(new MediaController(this));
	    Display localDisplay = getWindowManager().getDefaultDisplay();
	    int screenWidth = localDisplay.getWidth();
	    int screenHeight = localDisplay.getHeight();
	    videoView.onMeasure(screenWidth, screenHeight);
	    videoGasketPath = this.getString(R.string.video_gasket_path);
	    Uri uri = null;
	    if(new File(SD_PATH+videoGasketPath+"videogasket.mp4").exists()){
	    	uri = Uri.parse(SD_PATH+videoGasketPath+"videogasket.mp4");
	    }else{
	    	uri = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.videogasket);
	    }
	    videoView.setVideoURI(uri); 
		videoView.start(); 
		videoView.requestFocus();
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {  
	        @Override  
	        public void onPrepared(MediaPlayer mp) {  
	           // mp.start();  
	            mp.setLooping(true);  
	        }  
	    });
		new Thread(){
			@Override
			public void run() {
				while(flag){
					try {
						Thread.sleep(10000);
						System.out.println("播放垫片时检测网络");
						netCheck();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}.start();
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
	}
	public void usbBroadCast(){
		//注册监听Usb存储设备广播
		
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		iFilter.addDataScheme("file");
	    mBroadcastReceiver = new USBBroadCastReceiver();
		registerReceiver(mBroadcastReceiver, iFilter);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Display localDisplay = getWindowManager().getDefaultDisplay();
	    int screenWidth = localDisplay.getWidth();
	    int screenHeight = localDisplay.getHeight();
		if((event.getX() > screenWidth*49/50) && (event.getY() < screenHeight/50)){
			touchCount++;
			if(touchCount ==5){
				PackageManager  packageManager = getPackageManager();
    			Intent intent = packageManager.getLaunchIntentForPackage("com.kt.largescreen.manager");
    			startActivity(intent);  
                this.finish();
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		flag = false;
		super.onDestroy();
	}
	public void netCheck() throws MalformedURLException{
		if(nasd.isOpenNetwork()){			
			ServersUrlPath sup = new ServersUrlPath(this);
			String mainServerUrl = sup.getMainServerUrl();
			String secondServerUrl = sup.gerSecondServerUrl();
			boolean isMainServerConnect = nasd.isServerConnect(mainServerUrl);//主服务器连接
			System.out.println("开始连接主服务器代码");
			if(isMainServerConnect){//主服务器连接成功
				System.out.println("连接主服务器成功");
				server_ip = sup.getMainServerIp();
				System.out.println("server_ip = "+server_ip);
				SharedPreferences sp = this.getSharedPreferences("server_ip", 0);
				Editor editor = sp.edit();
				editor.putString("ip", server_ip);
				editor.commit();
				nasd.uploadTerminalAndAppInfo(server_ip);//上报信息
				if(la.loginAuth(server_ip)){
					flag = false;
					Intent intent = new Intent();  
                    intent.setClass(VideoGasketPlayActivity.this, MainActivity.class);  
                    startActivity(intent);  
                    VideoGasketPlayActivity.this.finish();
				}
			}else if(!isMainServerConnect){//主服务器连接失败
				System.out.println("连接备用服务器代码");
				boolean isSecondServerConnect = nasd.isServerConnect(secondServerUrl);//备份服务器连接
				if(isSecondServerConnect){//备份服务器连接成功
					System.out.println("备用服务器代码连接成功");
					server_ip = sup.getSecondServerIp();
					SharedPreferences sp = this.getSharedPreferences("server_ip", 0);
					Editor editor = sp.edit();
					editor.putString("ip", server_ip);
					editor.commit();
					nasd.uploadTerminalAndAppInfo(server_ip);//上报信息
					if(la.loginAuth(server_ip)){
						flag = false;
						Intent intent = new Intent();  
	                    intent.setClass(VideoGasketPlayActivity.this, MainActivity.class);  
	                    startActivity(intent);  
	                    VideoGasketPlayActivity.this.finish();
					}
				}
			}
		}
	}

}
