package com.kt.largesreen.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kt.largescreen.lib.UnZipFile;
import com.kt.largesreen.player.utils.GetFileMd5;
import com.kt.largesreen.player.utils.MutilThreadBreakPointDownload;
import com.kt.largesreen.player.utils.USBBroadCastReceiver;
import com.kt.largesreen.player.utils.Util;
import com.kt.largesreen.player.utils.MutilThreadBreakPointDownload.DownloadThread;
import com.kt.largesreen.player.view.LayoutView;

/**
 * 
 * **/
public class MainActivity extends Activity {

	private Context mContext;
	AbsoluteLayout container;
	private String splashImgZipFilePath;
	private String splashImgName = "splashImg.zip";
	private String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	private Timer timer;
	private TimerTask timerTask;
	private Handler timerHandler;
	private LayoutView lv;
	private USBBroadCastReceiver mBroadcastReceiver;
	private XmppMessageBroadCast xmppMessageBroadCast;
	private long period;//定时器的时间
	private int i =0;
	private String programFileDir;
	private int touchCount =0;//触摸屏幕的次数
	private int count;//已经建立的文件夹的数量
	private int zipcount;//已经建立的节目压缩包的数量
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		setContentView(R.layout.activity_main);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
		
		container = (AbsoluteLayout) findViewById(R.id.root);
		
		usbBroadCast();
		xmppMessageBroadCast = new XmppMessageBroadCast();
		IntentFilter iFilter = new IntentFilter();		
		iFilter.addAction("com.kt.largescreen.PUSH_NOTIFICATION");
		registerReceiver(xmppMessageBroadCast, iFilter);
		
		programFileDir = SD_PATH+getString(R.string.program_file_path);
		timerHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					File file = new File(programFileDir);
					File[] fileString = file.listFiles();
					int length = fileString.length;
					Document document;
					if(length > 0){
						int tempLength = 0;//统计data.xml的数量
						for(int j = 0; j < length;j++){
							if(new File(fileString[j]+"/data.xml").exists()){
								tempLength++;
							}
						}
						System.out.println("tempLength="+tempLength);
						if(tempLength == 0){//data.xml数量为0；没有节目
							TextView textView = new TextView(mContext);
							textView.setText("没有发现节目,请在平台制作节目并发布！！！");
							textView.setTextSize(60);
							textView.setTextColor(Color.RED);
							textView.setGravity(Gravity.CENTER);
						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT,0,0);
							textView.setLayoutParams(params);
							container.addView(textView);								
							timer = new Timer();
							timerTask = new MyTimerTask();
							timer.schedule(timerTask,10000);
						}else if(tempLength > 0){
							System.out.println("tempLength大于0，开始播放节目");
							i = i%length;
							System.out.println("i = "+ i);
							document = Util.getDocument(fileString[i]+"/data.xml");
							if(lv != null){
								container.removeAllViews();
							}
							System.out.println("document为null");
							if(document != null){
								System.out.println("document 不为null");
								Element root = document.getDocumentElement();
							    lv = new LayoutView(mContext, container, root,fileString[i].toString());//LayoutView提供一个函数来返回播放时间，再开启定时器进行工作；
							    String playTime = lv.getPlayTime();
							    System.out.println("开始执行新的定时器");
							    if(playTime == null){
							    	 timer = new Timer();
									 timerTask = new MyTimerTask();
									 timer.schedule(timerTask,1000000);
							    }else{
							    	period = Long.parseLong(playTime);
							    	System.out.println("period = "+period);
							    	timer = new Timer();
									timerTask = new MyTimerTask();
									timer.schedule(timerTask,period);
							    }
							    i++;
							}else{
								i++;
								timer = new Timer();
								 timerTask = new MyTimerTask();
								 timer.schedule(timerTask,0);
							}
						}
					}else{//播放垫片
						TextView textView = new TextView(mContext);
						textView.setText("没有发现节目,请在平台制作节目并发布！！！");
						textView.setTextSize(70);
						textView.setTextColor(Color.RED);
						textView.setGravity(Gravity.CENTER);
						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT,0,0);
						textView.setLayoutParams(params);
						container.addView(textView);						
						timer = new Timer();
						timerTask = new MyTimerTask();
						timer.schedule(timerTask,10000);
						
					}
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		
		timer = new Timer();
		timerTask = new MyTimerTask();
		System.out.println("MainActivity--- 121: 开始进入定时器执行");
		timer.schedule(timerTask,0);
		new Thread(){
			public void run() {
				splashImgZipFilePath = getString(R.string.program_zipfile_path);
				SharedPreferences sp = getSharedPreferences("loginretrunString", 0);		
				String loginReturnString = sp.getString("strResult", "false");
				System.out.println("MainActivity---140: loginReturnString"+ loginReturnString);
				UnZipFile uzf = new UnZipFile();
				if(!loginReturnString.equals("false")){
					System.out.println("MainAcitivty---144: 获取到loginReturnString下载数据");
					JSONTokener json = new JSONTokener(loginReturnString);
					try {
						JSONObject jsonObj = (JSONObject) json.nextValue();
						String splashImgUrl = jsonObj.getString("backImg");
						//download(3, splashImgUrl,isFileExist(SD_PATH+splashImgZipFilePath+splashImgName),"splashImg",null,null);
						System.out.println("MainActivity-----150:"+SD_PATH+splashImgZipFilePath+splashImgName+"--"+getString(R.string.splashImg_path));
						//uzf.unzip(SD_PATH+splashImgZipFilePath+splashImgName, SD_PATH+getString(R.string.splashImg_path));
						JSONObject channel = jsonObj.getJSONObject("channel");
						String channelName = channel.getString("name");
						
						//String channelScheduleUrl = channel.getString("scheduleUrl");
					//	mtbpd.download(1, splashImgUrl, scheduleZipFilePath);//下载日程文件
						
						JSONArray programList = channel.getJSONArray("programList");
						 int programListLength = programList.length();
						 String [] downloadUrl = new String[programListLength];
						 String playOrder = null;
						 String md5 = null;
						 for(int i = 0; i < programListLength; i++){
								JSONObject programListObj = programList.getJSONObject(i);
								String programUrl = programListObj.getString("url");
								playOrder = programListObj.getString("playOrder");
								 md5 = programListObj.getString("md5");
								File file = new File(SD_PATH+mContext.getString(R.string.program_zipfile_path));
								String [] fileString = file.list();
								int md5Count = 0;
								int length = fileString.length; 
								for(int j = 0; j < length;j++){
									System.out.println(GetFileMd5.getMd5(SD_PATH+mContext.getString(R.string.program_zipfile_path)+fileString[j]));
									if(!GetFileMd5.getMd5(SD_PATH+mContext.getString(R.string.program_zipfile_path)+fileString[j]).equals(md5)){
										md5Count++;
									}
								}
								System.out.println("md5 = "+ md5);
								System.out.println("md5Count = "+md5Count+"-----"+"length = "+length);
								if(md5Count == length){//说明文件夹中没有与MD5相等的文件存在，则将下载的url存入数组downloadUrl中去;
									downloadUrl[i] = programUrl;
								}
							}
						 	System.out.println("downloadUrl.length ="+ downloadUrl.length);
						 	for(int k =0;k < downloadUrl.length; k++){
						 		System.out.println("进入下载环节");
								System.out.println(downloadUrl[k]+"----------");
						 		if(downloadUrl[k] != null){
						 			download(3, downloadUrl[k], programFilemake(),"program"+playOrder,md5,programDirFilemake());
						 		}
						 	}
						 	downloadUrl = null;
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
		}.start();
	}
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			timerHandler.sendMessage(msg);
		}
	}
	private String programFilemake(){
		SharedPreferences sp = getSharedPreferences("ZIPFileCount", MODE_PRIVATE);
		if(sp.getInt("zipcount",0) == 0){
			zipcount = 1;
		}else{
			zipcount = sp.getInt("zipcount",0);
		}
		String programZipFilePath = SD_PATH+getString(R.string.program_zipfile_path)+zipcount+".zip";//下载的节目压缩包存放地址（包括文件名）
		System.out.println("zipcount = " +zipcount);
		File file = new File(programZipFilePath);
		if(!file.exists()){
			zipcount++;
			Editor editor = sp.edit();
			editor.putInt("zipcount", zipcount);
			editor.commit();
			return programZipFilePath;
		}
		return programZipFilePath;
	}
	private String programDirFilemake(){
		SharedPreferences sp = getSharedPreferences("DirFileCount", MODE_PRIVATE);
		if(sp.getInt("count",0) == 0){
			count = 1;
		}else{
			count = sp.getInt("count",0);
		}
		String programFilePath = SD_PATH+getString(R.string.program_file_path)+count+"/";//节目存放路径
		System.out.println("count = " +count);
		File file = new File(programFilePath);
		if(!file.exists()){
			file.mkdir();
			count++;
			Editor editor = sp.edit();
			editor.putInt("count", count);
			editor.commit();
			return programFilePath;
		}
		return programFilePath;
	}
	public void download(int count,String downloadUrl,String fileNamePath,String name,String md5,String unZipPath){
		int runningThread = count;
		int threadCount = count;
		String path = downloadUrl;
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setRequestMethod("GET");
			int code = httpURLConnection.getResponseCode();
			if(code == 200){
				int length = httpURLConnection.getContentLength();
				System.out.println("length == "+ length);
				//在客户端本地创建一个大小跟服务器上的文件一样大小的临时文件
				//使用 随机文件访问的类RandomAccessFile()创建,"rwd"表示文件会直接存储在硬盘，不会先保存在缓存区
				RandomAccessFile raf = new RandomAccessFile(fileNamePath, "rwd");
				//指定创建的文件的长度
				raf.setLength(length);
				raf.close();
				//假设3个线程去下载
				//平均每个线程下载的文件内容的大小
				int blockSize = length/threadCount;
				for(int threadId = 1;threadId <= threadCount;threadId++){
					//第一个线程下载的开始位置
					int startIndex = (threadId-1)*blockSize;
					int endIndex = threadId*blockSize-1;
					if(threadId == threadCount){
						//最后一个线程下载的长度的末尾直接到该文件的末尾
						endIndex = length;
					}
					System.out.println("开进程开始下载");
				new DownloadThread(startIndex,endIndex,path,fileNamePath,name,md5,unZipPath).start();
				}
			}else{
				System.out.println("服务器错误");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} 
	public  class DownloadThread extends Thread{
		private int startIndex;
		private int endIndex;
		private String path;
		private String fileNamePath;
		private String name;//保存断点配置文件时用来命名
		private String md5;
		private String unZipPath;
		/**
		 * @param path下载文件在服务器上的路径
		 * 
		 */
		public DownloadThread(int startIndex,int endIndex,String path,String fileNamePath,String name,String md5
				,String unZipPath) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.path = path;
			this.fileNamePath = fileNamePath;		
			this.name =name;
			this.md5 = md5;
			this.unZipPath = unZipPath;
		}
		@Override
		public void run() {
				URL url;
				try {
					System.out.println(fileNamePath+"下载中");
					//检查是否存在记录下载长度的文件，存在则读取
					//----------后面将其从数据库读取-------替换成数据库(android中使用)----------//
//					File tempFile = new File(SD_PATH+"/"+name+".txt");
//					
//					if(tempFile.exists() && tempFile.length() > 0){
//						SharedPreferences sppath = getSharedPreferences("downloadUrlSave", 0);
//						String pathname = sppath.getString("downloadsaveurl", "null");
//						System.out.println("MutiThreadBreadkPointDownload---107:tempFile"+".txt存在 pathname = "+ pathname);
//						//如果保存的path和断点后后第二次下载的path一样，则读取断点保存的配置文件
//						if(pathname.equals(path)){
//							FileInputStream fis = new FileInputStream(tempFile);
//							byte[] temp = new byte[1024];
//							int leng = fis.read(temp);
//							String downloadLen = new String(temp, 0, leng);
//							int dowloadInt = Integer.parseInt(downloadLen);
//							startIndex = dowloadInt;//修改真实的下载开始位置
//							fis.close();
//						}
//					}
//					//---------------------------//
//					//将需要下载path保存，以便在断点后，重新下载时读取断点保存的文件时：如果保存的path和断点后后第二次下载的path一样，则读取断点保存的文件
//					SharedPreferences sppath = getSharedPreferences("downloadUrlSave", 0);
//					Editor editor = sppath.edit();
//					editor.putString(name, path);
//					editor.commit();
					
					url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					//设置 服务器下载部分的文件，指定文件的位置
					conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
					conn.setConnectTimeout(5000);
					int code = conn.getResponseCode();//从服务器请求全部资源成功 返回200 从服务器请求部分文件资源成功返回206
					if(code == 206){
						System.out.println(fileNamePath+"请求成功");
						InputStream is = conn.getInputStream();
						RandomAccessFile raf = new RandomAccessFile(fileNamePath, "rwd");
						raf.seek(startIndex);//随机写文件的时候从哪个位置开始写
						int len = 0;
						byte[] buffer = new byte[1024];
						int total = 0;//已经下载的数据长度
						while((len = is.read(buffer)) != -1){
							
							raf.write(buffer, 0, len);
							//-------存在数据库---换成数据库----//
//							RandomAccessFile file = new RandomAccessFile(SD_PATH+"/"+name+".txt", "rwd");
//							total += len;
							//System.out.println("下载"+fileNamePath+"的长度："+total);
							//将已经下载的数据长度写入threadId.txt文件中去
//							file.write((""+total+startIndex).getBytes());
//							file.close();
							//-----------------------//
						}
						System.out.println(fileNamePath+"读写完毕");
						is.close();
						raf.close();
					}else{
						//下载失败
						System.out.println("文件下载失败");
					}
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					System.out.println("MalformedURLException ");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("IOException ");
				}finally{;
						
						//System.out.println(fileNamePath+": threadCount"+ threadCount);
						//所有下载线程下载完毕，则删除所有的记录文件下载长度的txt文件						
							//--- 从数据库中删除--//
//							File file = new File(SD_PATH+"/"+name+".txt");
//							file.delete();
							//-----//
						//	System.out.println("删除文件"+name+".txt");
				
							System.out.println("fileNamePathmd5 = "+ md5+"--"+GetFileMd5.getMd5(fileNamePath));
							if(md5.equals(GetFileMd5.getMd5(fileNamePath))){
								
								try {
									UnZipFile.unzip(fileNamePath,unZipPath);
									System.out.println(fileNamePath+"解压文件成功");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
				}							
			}
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
	    System.out.println(event.getX()+"----"+event.getY());
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
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		unregisterReceiver(xmppMessageBroadCast);
		timerTask.cancel();
		timer.cancel();
		System.exit(0);
		super.onDestroy();
	}
}
