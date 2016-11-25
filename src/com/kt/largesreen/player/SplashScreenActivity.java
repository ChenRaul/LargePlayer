package com.kt.largesreen.player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.kt.largescreen.lib.Configs;
import com.kt.largescreen.lib.IniWriteAndReadUtil;
import com.kt.largescreen.lib.NetAndServerDetection;
import com.kt.largescreen.lib.ServersUrlPath;
import com.kt.largesreen.player.utils.AppInstallAndLanuch;
import com.kt.largesreen.player.utils.LoginAuthentication;
import com.kt.largesreen.player.utils.MakeFilePath;
import com.kt.largesreen.player.utils.TimingUpdateDownlodaThread;
import com.kt.largesreen.player.utils.USBBroadCastReceiver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {
	
	private USBBroadCastReceiver mBroadcastReceiver;
	private String server_ip;
	private NetAndServerDetection nasd = new NetAndServerDetection(this,this);
	private LoginAuthentication la = new LoginAuthentication(this, this);
	private String splashImgpath;//�����ļ���ŵ�ַ�������ļ�����
	private ProgressDialog progressDialog;
	private AppInstallAndLanuch adai;
	private String download_url;
	private String downloadPath;
	private int fileSize ;
	private SphHandler sphHandler;
	private int isfinish = 0;
	private String SD_PATH = Configs.getRootFolder();
	private String INIPATH =SD_PATH+"/LargeScreen/config_setting.ini";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
        WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.splash_screen_main);  
        
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
		 
        usbBroadCast();
        MakeFilePath mkp = new MakeFilePath(this);
        mkp.makeFileDir();
        System.out.println(SD_PATH);      
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        
        splashImgpath = SD_PATH+this.getString(R.string.splashImg_path)+"splashimg.jpg";
        if(new File(splashImgpath).exists()){
        	 Drawable drawable = Drawable.createFromPath(splashImgpath);
             ll.setBackground(drawable);
        }else{
        	ll.setBackgroundResource(R.drawable.splashimg);
        }
       
        sphHandler = new SphHandler(this);
        SplashThread splashThread = new SplashThread();
        splashThread.start(); 
	}
	 class SplashThread extends Thread{
		@Override
		public void run() {
			//todo�߳���Ҫִ�������ϱ���Ϣ������
			try {
				Thread.sleep(1000);
				netCheck();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	 
	public void netCheck() throws MalformedURLException{
		if(nasd.isOpenNetwork()){			
			ServersUrlPath sup = new ServersUrlPath(this);
			String mainServerUrl = sup.getMainServerUrl();
			String secondServerUrl = sup.gerSecondServerUrl();
			System.out.println("mainServerUrl = "+ mainServerUrl);
			boolean isMainServerConnect = nasd.isServerConnect(mainServerUrl);//������������
			System.out.println("��ʼ����������������");
			if(isMainServerConnect){//�����������ӳɹ�
				System.out.println("�������������ɹ�");
				server_ip = sup.getMainServerIp();
				System.out.println("server_ip = "+server_ip);
				SharedPreferences sp = this.getSharedPreferences("server_ip", 0);
				Editor editor = sp.edit();
				editor.putString("ip", server_ip);
				editor.commit();
				nasd.uploadTerminalAndAppInfo(server_ip);//�ϱ���Ϣ
				//System.out.println("��Ϣ�ϱ��ɹ�");
				System.out.println("��ʼ���apk����");
//				AppCheckAndUpdate acau = new AppCheckAndUpdate();
				int ret = appUpdate(SplashScreenActivity.this,server_ip);//���apk����������
				System.out.println("ret = "+ ret);
				System.out.println("apk����������");
				if(ret == 0){
					System.out.println("���·�������");
				}else if(ret == 2){
					System.out.println("�����������");
				}
				String mac = new NetAndServerDetection(this, this).getMacAddress();
				String serial= android.os.Build.SERIAL;
				//String loginAuthUrl = new ServersUrlPath().getLoginAuthUrl(server_ip)+"mac="+mac+"&sn="+serial;
			    String loginAuthUrl = new ServersUrlPath(this).getLoginAuthUrl(server_ip)+"mac=1A:1B:1C:1D:1E:2F&sn=10002102102102102102";
				new TimingUpdateDownlodaThread(this, loginAuthUrl).start();//ÿ��10���Ӽ����µ��߳�
				
				auth(server_ip);
				
			}else if(!isMainServerConnect){//������������ʧ��
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(SplashScreenActivity.this, "�����������Ӵ������ڼ�ⱸ�÷�������", 1).show();
					}
				});
				System.out.println("���ӱ��÷���������");
				boolean isSecondServerConnect = nasd.isServerConnect(secondServerUrl);//���ݷ���������
				if(isSecondServerConnect){//���ݷ��������ӳɹ�
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(SplashScreenActivity.this, "���÷��������ӳɹ���", 1).show();
						}
					});
					System.out.println("���÷������������ӳɹ�");
					server_ip = sup.getSecondServerIp();
					SharedPreferences sp = this.getSharedPreferences("server_ip", 0);
					Editor editor = sp.edit();
					editor.putString("ip", server_ip);
					editor.commit();
					nasd.uploadTerminalAndAppInfo(server_ip);//�ϱ���Ϣ
					System.out.println("��ʼ���apk����");
//					AppCheckAndUpdate acau = new AppCheckAndUpdate();
					int ret = appUpdate(SplashScreenActivity.this,server_ip);//���apk����������
					System.out.println("ret = "+ ret);
					System.out.println("apk����������");
					if(ret == 0){
						System.out.println("���·�������");
					}else if(ret == 2){
						System.out.println("�����������");
					}
					String mac = new NetAndServerDetection(this, this).getMacAddress();
					String serial= android.os.Build.SERIAL;
					//String loginAuthUrl = new ServersUrlPath().getLoginAuthUrl(server_ip)+"mac="+mac+"&sn="+serial;
				    String loginAuthUrl = new ServersUrlPath(this).getLoginAuthUrl(server_ip)+"mac=1A:1B:1C:1D:1E:2F&sn=10002102102102102102";
					new TimingUpdateDownlodaThread(this, loginAuthUrl).start();//ÿ��10���Ӽ����µ��߳�
					auth(server_ip);
				}else{//���ݷ��������Ӳ��ɹ�,���ŵ�Ƭ
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(SplashScreenActivity.this, "���÷��������Ӵ����޷�������֤�����ŵ�Ƭ���ݣ�", 1).show();
						}
					});
					Intent intent = new Intent();  
		            intent.setClass(SplashScreenActivity.this, VideoGasketPlayActivity.class);  
		            startActivity(intent);  
		            finish();
				}
			}
		}else{//����������⣬���ŵ�Ƭ
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(SplashScreenActivity.this, "û�����磬�����������ã�", 1).show();
				}
			});
			System.out.println("û�����磬�����������ã�");
			System.out.println("SplashScreenActivity---182���ݷ��������Ӳ��ɹ��������������ã�");
			Intent intent = new Intent();  
            intent.setClass(SplashScreenActivity.this, VideoGasketPlayActivity.class);  
            startActivity(intent);  
            finish();
		}
	}
	
	public void auth(String server_ip){
		boolean bl = la.loginAuth(server_ip);
    	if(bl){//��֤ͨ��������������������ת��ֻҪ��֤ͨ���˾Ϳ���ֱ����ת���Զ����Ż�������ģ�鲥�ű�������
    		String launch_mode = IniWriteAndReadUtil.readIniOption(INIPATH,null,"launch", "launch_mode");
    		System.out.println("launch_mode ="+launch_mode);
    		if(launch_mode != null){
    			if(launch_mode.equals("0")){//������Ӧ��
    				String launch_mode_local = IniWriteAndReadUtil.readIniOption(INIPATH,null,"launch", "launch_mode_local");
        			if(launch_mode_local != null){
        				if(launch_mode_local.equals("0")){
            				Intent intent = new Intent();  
                            intent.setClass(SplashScreenActivity.this, MainActivity.class);  
                            startActivity(intent);  
                            finish();
            			}else if(launch_mode_local.equals("1")){
            				PackageManager  packageManager = getPackageManager();
                			Intent intent = packageManager.getLaunchIntentForPackage("com.cylix.peopledaily");
                			startActivity(intent); 
  
                            finish();
            				System.out.println("���������ı�");
            			}
        			}else{//��ȡ�����ļ������������launch_mode_localΪnull��
        				System.out.println("��ȡ�����ļ������������launch_mode_localΪnull����ת�Զ����Ų��ű�������");
        				Intent intent = new Intent();  
                        intent.setClass(SplashScreenActivity.this, MainActivity.class);  
                        startActivity(intent);  
                        finish();
        			}
        		}else if(launch_mode.equals("1")){//����������
        			String launch_mode_third_package = IniWriteAndReadUtil.readIniOption(INIPATH,null,"launch", "launch_mode_third_package");
        			if(launch_mode_third_package != null){
        				PackageManager  packageManager = getPackageManager();
            			Intent intent = packageManager.getLaunchIntentForPackage(launch_mode_third_package);
            			startActivity(intent); 
            			finish();
        			}else{//
        				System.out.println("��ȡ�����ļ������������launch_mode_third_packageΪnull����ת�Զ����Ų��ű�������");
        				Intent intent = new Intent();  
                        intent.setClass(SplashScreenActivity.this, MainActivity.class);  
                        startActivity(intent);  
                        finish();
        			}
        		}else{//�����õ�
        			Intent intent = new Intent();  
                    intent.setClass(SplashScreenActivity.this, VideoGasketPlayActivity.class);  
                    startActivity(intent);
                    finish();
        		}
    		}else{//��ȡ�����ļ�launch_modeΪ��,��֤ͨ��������ֱ����ת���Զ����Ų��ű�������
    			System.out.println("SplashScreenActivity---215 : launch_mode == null,");
    			Intent intent = new Intent();  
                intent.setClass(SplashScreenActivity.this, MainActivity.class);  
                startActivity(intent);  
                finish();
    		}
    	}else{//��֤û��ͨ����Ĭ�ϲ��ŵ�Ƭ
    		System.out.println("SplashScreenActivity---208��֤û��ͨ����Ĭ�ϲ��ŵ�Ƭ");
			Intent intent = new Intent();  
            intent.setClass(SplashScreenActivity.this, VideoGasketPlayActivity.class);  
            startActivity(intent);  
            finish();
    	}
	}
	
	public void usbBroadCast(){
		//ע�����Usb�洢�豸�㲥
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		iFilter.addDataScheme("file");//�˴�����Ҫ������usb�洢�豸���ܱ�������
	    mBroadcastReceiver = new USBBroadCastReceiver();
		registerReceiver(mBroadcastReceiver, iFilter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}
	/*apk����*/
	public int appUpdate(final Activity activity,String server_ip){
		NetAndServerDetection nasd = new NetAndServerDetection(activity, null);
		String mac = nasd.getMacAddress();
		String apk_name = activity.getString(R.string.app_name);
		//String appUpdateUrl = new ServersUrlPath(this).getAppUpdateUrl(server_ip)+"mac=1A:1B:1C:1D:1E:2F"+"&apk_name=player";//luncher";
		String appUpdateUrl = new ServersUrlPath(this).getAppUpdateUrl(server_ip)+"mac="+mac+"&apk_name="+apk_name;//luncher";
		System.out.println("AppCheckAndUpdate---54:ʹ��httpget�ύurl����apk������� = "+ appUpdateUrl);
		 HttpClient httpClient = new DefaultHttpClient();
		  HttpGet httpGet = new HttpGet(appUpdateUrl);
		  try {
			HttpResponse httpResp = httpClient.execute(httpGet);
			if((httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)){
				System.out.println("AppCheckAndUpdate---79:apk�������ɹ�");
				String strResult = EntityUtils.toString(httpResp.getEntity());	//��ȡ���سɹ����ַ���(���������ص��ַ�����json��ʽ)
				System.out.println("AppCheckAndUpdate---81:strResult = "+ strResult);
				JSONTokener jsonPar = new JSONTokener(strResult);			
				JSONObject pers = (JSONObject) jsonPar.nextValue();
				if(pers.getString("resultcode").equals("1")){//TODO���˴���ӡ��Ϣ�о��棬Ӧ���Ƿ��ص�json����������
					System.out.println("resultcode = "+pers.getString("resultcode")+"apk��Ҫ����" );
					String action2 = pers.getString("action");
					String version = pers.getString("version");
				    download_url = pers.getString("download_url");
					final String md5 = pers.getString("md5");
					String size = pers.getString("size");
					String finalSize = size.substring(0, size.length()-1);
					System.out.println(finalSize);
					fileSize = Integer.parseInt(finalSize);
					String force = pers.getString("force");
					//�������ʱ�������Ҫ����apk�������������Ի�����ʾϵͳ����������������
					//������ɺ����������а�װ����װ��ɺ������������
					  adai = new AppInstallAndLanuch(activity);					  
					 downloadPath =SD_PATH+ activity.getResources().getString(R.string.app_download_filepath)+"Player.apk";					 
					 System.out.println("��ʼ����app����");
					 Message msg1 = new Message();
					 msg1.what = 1;
					 sphHandler.sendMessage(msg1);
					 URL url = null; 
			 		try {
			 			try {
							url = new URL(download_url);
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			 			URLConnection con = url.openConnection();
			 			InputStream is = con.getInputStream();
			 			byte[] bt = new byte[1024];
			 			int leng = 0;
			 			int sum = 0;
			 			File file = new File(createFileForDownload(downloadPath));
			 			OutputStream os = new FileOutputStream(file);
			 			System.out.println("����������");
			 			while((leng = is.read(bt)) != -1){
			 				os.write(bt, 0, leng);
			 				sum += leng;
			 				//handler.sendEmptyMessage(sum/fileSize);
			 				Message message = Message.obtain();
			 				message.what = 3;
			 				message.obj = sum*100/fileSize;
			 				//System.out.println("sum/fileSize*100 ="+sum*100/fileSize);
			 				sphHandler.sendMessage(message);
			 				//Thread.sleep(100);
			 				//progressDialog.setProgress(sum/fileSize);
			 			}
			 			if(sum == fileSize){
			 				Message msg2 = new Message();
			 				msg2.what = 2;
			 				sphHandler.sendMessage(msg2);
			 			}
			 			os.close();
			 			is.close();
//			 			if(md5 .equals(GetFileMd5.getMd5(downloadPath))){
////			        		adai.silenceInstall(downloadPath);
//			         		Intent intent = new Intent(Intent.ACTION_VIEW);
//			        		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			        		intent.setDataAndType(Uri.fromFile(new File(downloadPath)), "application/vnd.android.package-archive");
//			        	    startActivity(intent);
//			                
//							adai.openApk(downloadPath);
//							SplashScreenActivity.this.finish();
//			 			}
			 		} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
			 		}
				     return 1;
				}else if(pers.getString("resultcode").equals("0")){
					System.out.println("AppCheckAndUpdate---119:app�������");
					return 2;
				}else{
					System.out.println("AppCheckAndUpdate---122:app���³��ִ���");
					return 0;
				}
			}else{
				System.out.println("AppCheckAndUpdate---126:������³��ִ���");
				return 0;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		   
	}
    public String createFileForDownload(String filePath) {
		File file = new File(filePath);
		if (file.exists()) { 							// ���Ŀ���ļ��Ѿ����ڣ���ɾ�����������Ǿ��ļ���Ч��
			file.delete();
		}
		return filePath;
	}
    class SphHandler extends Handler{
		private Context spcontext;
		public SphHandler(Context context){
			this.spcontext = context;
		}
		 @Override
         public void handleMessage(Message msg) {
         	if(msg.what == 1){
         		System.out.println("���յ�1 ��Ϣ");
         		 progressDialog	= new ProgressDialog(spcontext);
			     progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			     progressDialog.setMessage("���ֳ������µĸ�������,���Ժ򡣡�����");
			     progressDialog.setTitle("���ڸ���Ӧ�ó���");
			   //  progressDialog.setProgress(0);
			     progressDialog.setMax(100);
			     progressDialog.show();
         	}else if(msg.what == 2){
         		System.out.println("���յ�2 ��Ϣ");     
         		progressDialog.cancel(); 
         	}else if(msg.what == 3){
         		progressDialog.setProgress((Integer) msg.obj);
         	}
             super.handleMessage(msg);
         };
	}
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			isfinish++;
			Toast.makeText(this, "�ٰ�һ�η��ؼ������˳�", 1).show();
			System.out.println("isfinish = "+ isfinish);
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onBackPressed() {
		 if(isfinish == 2){
			this.finish();
			System.exit(0);
		}
	}
}
