package com.kt.largesreen.player.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.kt.largescreen.lib.UnZipFile;
import com.kt.largesreen.player.R;
import com.kt.largesreen.player.SplashScreenActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/*ÿһ��activity��Ҫע������һ��activity��ʧ����Ҫȡ��ע�����finish����������棬��Ȼ������Ӧͬһ���㲥*/
public class USBBroadCastReceiver extends BroadcastReceiver {
	private ProgressDialog progressDialog;
	private Handler usbCopyHandler;
	private String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		 String action = intent.getAction();
	       if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
	           System.out.println("usb�洢�豸�Ƴ�");
	       } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){
	          //USB�豸���أ�����UI
	    	   System.out.println("usb�洢�豸����");
	    	   String usb_path = intent.getData().getPath();//��ȡ�²����U�̻���SD���洢�豸��·��
	    	   	 progressDialog = new ProgressDialog(context);
			     progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			     progressDialog.setMessage("���ڼ�鸴������ļ�");
			     progressDialog.setTitle("������usb�洢�豸����");
			     progressDialog.show();
			     String filePath = usb_path+context.getString(R.string.Usb_zipfile_path);
			     String toFilepath = SD_PATH+context.getString(R.string.program_zipfile_path);
			   
			     usbCopyHandler = new  Handler(){
			        	public void handleMessage(Message msg){
			        		switch (msg.what) {
			    			case 1:
			    					progressDialog.setTitle("�ļ����Ƴɹ�");
			    					progressDialog.setMessage("�ļ����Ƴɹ�������");
									break;
			    			case 2: 
			    					progressDialog.setTitle("�ļ������쳣");
			    					progressDialog.setMessage("�ļ������쳣����������ļ�����ж��U�����²��U������һ�Σ�");
			    					break;
			    			case 3: 
									progressDialog.cancel();
		    					break;
			    			default:
			    				break;
			    			}
			        	}
			        };
			       new UsbCopyThread(new File(filePath), toFilepath,context).start();
	      }
	}
	class UsbCopyThread extends Thread{
		private File file;
		private String toFilePath;
		private Context context;
		public UsbCopyThread(File file, String toFilePath,Context context) {
			this.file = file;
			this.toFilePath = toFilePath;
			this.context = context;
		}
		@Override
		public void run() {
			boolean bl = copyFile(file,toFilePath,context);
			Message usbCopymessage = new Message();
			if(bl){		
				try {
					usbCopymessage.what = 1;
					usbCopyHandler.sendMessage(usbCopymessage);
					Thread.sleep(3000);
					Message message3 = new Message();
					message3.what = 3;//ȡ��progressDialog  �������޸�
					usbCopyHandler.sendMessage(message3);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					Thread.sleep(3000);
					usbCopymessage.what = 2;
					usbCopyHandler.sendMessage(usbCopymessage);
					Thread.sleep(3000);
					Message message3 = new Message();
					message3.what = 3;//ȡ��progressDialog  �������޸�
					usbCopyHandler.sendMessage(message3);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	public boolean copyFile(File file,String toFilePath,Context context) { 
 		if (file.exists() == false) { 
 			return false; 
 		} else { 
 			if (file.isFile()) { 
 				return false; 
 			} 
 			if (file.isDirectory()) { 
 				File[] childFile = file.listFiles(); 
 				if (childFile == null || childFile.length == 0) { 
 					
 						return false; 
 				} 
 				for (File f : childFile) { 
 					String filename = f.getName();
 					long fileLength = f.length();
 					if(filename.substring(filename.lastIndexOf(".")+1).equals("zip")){
 						FileInputStream fosfrom;
						try {
							fosfrom = new FileInputStream(f);
							File toFile = new File(toFilePath+filename);
	 						FileOutputStream fosto = new FileOutputStream(toFile);
	 							byte bt[] = new byte[1024];
	 							int len;
	 							while ((len = fosfrom.read(bt)) > 0) {
	 								fosto.write(bt, 0, len); //������д�����ļ�����
	 							}
	 							fosfrom.close();
	 							fosto.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							return false;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							return false;
						}finally{
							if (fileLength == new File(toFilePath+filename).length()){
								try {
									UnZipFile.unzip(toFilePath+filename, programDirFilemake(context));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									return false;
								}
							}
						}
 						
 					}
 				} 
 				return true;
 			} 
 		}
		return false; 
 	} 
	private String programDirFilemake(Context context){
		int count;
		SharedPreferences sp = context.getSharedPreferences("DirFileCount", 0);
		if(sp.getInt("count",0) == 0){
			count = 1;
		}else{
			count = sp.getInt("count",0);
		}
		String unZipFilePath = SD_PATH+context.getString(R.string.program_file_path)+count+"/";
		System.out.println("count = " +count);
		File file = new File(unZipFilePath);
		if(!file.exists()){
			file.mkdir();
			count++;
			Editor editor = sp.edit();
			editor.putInt("count", count);
			editor.commit();
			return unZipFilePath;
		}
		return unZipFilePath;
	}
}