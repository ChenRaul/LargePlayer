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
	private long period;//��ʱ����ʱ��
	private int i =0;
	private String programFileDir;
	private int touchCount =0;//������Ļ�Ĵ���
	private int count;//�Ѿ��������ļ��е�����
	private int zipcount;//�Ѿ������Ľ�Ŀѹ����������
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
						int tempLength = 0;//ͳ��data.xml������
						for(int j = 0; j < length;j++){
							if(new File(fileString[j]+"/data.xml").exists()){
								tempLength++;
							}
						}
						System.out.println("tempLength="+tempLength);
						if(tempLength == 0){//data.xml����Ϊ0��û�н�Ŀ
							TextView textView = new TextView(mContext);
							textView.setText("û�з��ֽ�Ŀ,����ƽ̨������Ŀ������������");
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
							System.out.println("tempLength����0����ʼ���Ž�Ŀ");
							i = i%length;
							System.out.println("i = "+ i);
							document = Util.getDocument(fileString[i]+"/data.xml");
							if(lv != null){
								container.removeAllViews();
							}
							System.out.println("documentΪnull");
							if(document != null){
								System.out.println("document ��Ϊnull");
								Element root = document.getDocumentElement();
							    lv = new LayoutView(mContext, container, root,fileString[i].toString());//LayoutView�ṩһ�����������ز���ʱ�䣬�ٿ�����ʱ�����й�����
							    String playTime = lv.getPlayTime();
							    System.out.println("��ʼִ���µĶ�ʱ��");
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
					}else{//���ŵ�Ƭ
						TextView textView = new TextView(mContext);
						textView.setText("û�з��ֽ�Ŀ,����ƽ̨������Ŀ������������");
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
		System.out.println("MainActivity--- 121: ��ʼ���붨ʱ��ִ��");
		timer.schedule(timerTask,0);
		new Thread(){
			public void run() {
				splashImgZipFilePath = getString(R.string.program_zipfile_path);
				SharedPreferences sp = getSharedPreferences("loginretrunString", 0);		
				String loginReturnString = sp.getString("strResult", "false");
				System.out.println("MainActivity---140: loginReturnString"+ loginReturnString);
				UnZipFile uzf = new UnZipFile();
				if(!loginReturnString.equals("false")){
					System.out.println("MainAcitivty---144: ��ȡ��loginReturnString��������");
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
					//	mtbpd.download(1, splashImgUrl, scheduleZipFilePath);//�����ճ��ļ�
						
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
								if(md5Count == length){//˵���ļ�����û����MD5��ȵ��ļ����ڣ������ص�url��������downloadUrl��ȥ;
									downloadUrl[i] = programUrl;
								}
							}
						 	System.out.println("downloadUrl.length ="+ downloadUrl.length);
						 	for(int k =0;k < downloadUrl.length; k++){
						 		System.out.println("�������ػ���");
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
		String programZipFilePath = SD_PATH+getString(R.string.program_zipfile_path)+zipcount+".zip";//���صĽ�Ŀѹ������ŵ�ַ�������ļ�����
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
		String programFilePath = SD_PATH+getString(R.string.program_file_path)+count+"/";//��Ŀ���·��
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
				//�ڿͻ��˱��ش���һ����С���������ϵ��ļ�һ����С����ʱ�ļ�
				//ʹ�� ����ļ����ʵ���RandomAccessFile()����,"rwd"��ʾ�ļ���ֱ�Ӵ洢��Ӳ�̣������ȱ����ڻ�����
				RandomAccessFile raf = new RandomAccessFile(fileNamePath, "rwd");
				//ָ���������ļ��ĳ���
				raf.setLength(length);
				raf.close();
				//����3���߳�ȥ����
				//ƽ��ÿ���߳����ص��ļ����ݵĴ�С
				int blockSize = length/threadCount;
				for(int threadId = 1;threadId <= threadCount;threadId++){
					//��һ���߳����صĿ�ʼλ��
					int startIndex = (threadId-1)*blockSize;
					int endIndex = threadId*blockSize-1;
					if(threadId == threadCount){
						//���һ���߳����صĳ��ȵ�ĩβֱ�ӵ����ļ���ĩβ
						endIndex = length;
					}
					System.out.println("�����̿�ʼ����");
				new DownloadThread(startIndex,endIndex,path,fileNamePath,name,md5,unZipPath).start();
				}
			}else{
				System.out.println("����������");
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
		private String name;//����ϵ������ļ�ʱ��������
		private String md5;
		private String unZipPath;
		/**
		 * @param path�����ļ��ڷ������ϵ�·��
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
					System.out.println(fileNamePath+"������");
					//����Ƿ���ڼ�¼���س��ȵ��ļ����������ȡ
					//----------���潫������ݿ��ȡ-------�滻�����ݿ�(android��ʹ��)----------//
//					File tempFile = new File(SD_PATH+"/"+name+".txt");
//					
//					if(tempFile.exists() && tempFile.length() > 0){
//						SharedPreferences sppath = getSharedPreferences("downloadUrlSave", 0);
//						String pathname = sppath.getString("downloadsaveurl", "null");
//						System.out.println("MutiThreadBreadkPointDownload---107:tempFile"+".txt���� pathname = "+ pathname);
//						//��������path�Ͷϵ���ڶ������ص�pathһ�������ȡ�ϵ㱣��������ļ�
//						if(pathname.equals(path)){
//							FileInputStream fis = new FileInputStream(tempFile);
//							byte[] temp = new byte[1024];
//							int leng = fis.read(temp);
//							String downloadLen = new String(temp, 0, leng);
//							int dowloadInt = Integer.parseInt(downloadLen);
//							startIndex = dowloadInt;//�޸���ʵ�����ؿ�ʼλ��
//							fis.close();
//						}
//					}
//					//---------------------------//
//					//����Ҫ����path���棬�Ա��ڶϵ����������ʱ��ȡ�ϵ㱣����ļ�ʱ����������path�Ͷϵ���ڶ������ص�pathһ�������ȡ�ϵ㱣����ļ�
//					SharedPreferences sppath = getSharedPreferences("downloadUrlSave", 0);
//					Editor editor = sppath.edit();
//					editor.putString(name, path);
//					editor.commit();
					
					url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					//���� ���������ز��ֵ��ļ���ָ���ļ���λ��
					conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
					conn.setConnectTimeout(5000);
					int code = conn.getResponseCode();//�ӷ���������ȫ����Դ�ɹ� ����200 �ӷ��������󲿷��ļ���Դ�ɹ�����206
					if(code == 206){
						System.out.println(fileNamePath+"����ɹ�");
						InputStream is = conn.getInputStream();
						RandomAccessFile raf = new RandomAccessFile(fileNamePath, "rwd");
						raf.seek(startIndex);//���д�ļ���ʱ����ĸ�λ�ÿ�ʼд
						int len = 0;
						byte[] buffer = new byte[1024];
						int total = 0;//�Ѿ����ص����ݳ���
						while((len = is.read(buffer)) != -1){
							
							raf.write(buffer, 0, len);
							//-------�������ݿ�---�������ݿ�----//
//							RandomAccessFile file = new RandomAccessFile(SD_PATH+"/"+name+".txt", "rwd");
//							total += len;
							//System.out.println("����"+fileNamePath+"�ĳ��ȣ�"+total);
							//���Ѿ����ص����ݳ���д��threadId.txt�ļ���ȥ
//							file.write((""+total+startIndex).getBytes());
//							file.close();
							//-----------------------//
						}
						System.out.println(fileNamePath+"��д���");
						is.close();
						raf.close();
					}else{
						//����ʧ��
						System.out.println("�ļ�����ʧ��");
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
						//���������߳�������ϣ���ɾ�����еļ�¼�ļ����س��ȵ�txt�ļ�						
							//--- �����ݿ���ɾ��--//
//							File file = new File(SD_PATH+"/"+name+".txt");
//							file.delete();
							//-----//
						//	System.out.println("ɾ���ļ�"+name+".txt");
				
							System.out.println("fileNamePathmd5 = "+ md5+"--"+GetFileMd5.getMd5(fileNamePath));
							if(md5.equals(GetFileMd5.getMd5(fileNamePath))){
								
								try {
									UnZipFile.unzip(fileNamePath,unZipPath);
									System.out.println(fileNamePath+"��ѹ�ļ��ɹ�");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
				}							
			}
	}
	public void usbBroadCast(){
		//ע�����Usb�洢�豸�㲥
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
