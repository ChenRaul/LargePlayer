package com.kt.largesreen.player.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.kt.largesreen.player.MainActivity;
import com.kt.largesreen.player.R;
import com.kt.largesreen.player.SplashScreenActivity;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.DownloadListener;
import android.widget.Toast;

/*
 * 
  *��Ĭ��װ������
 * */
public class AppInstallAndLanuch {
	private Activity mainActivity;
	public AppInstallAndLanuch(Activity activity){
		this.mainActivity = activity;
	}
	/*��װ�������apk*/
	 public void openApk(String downloadPath ) {
		 	PackageManager manager = mainActivity.getPackageManager();
	        // ������������غõ��ļ�·��
	        //String downloadPath = mainActivity.getResources().getString(R.string.app_download_filepath)+ "Player.apk";
	        PackageInfo info = manager.getPackageArchiveInfo(downloadPath, PackageManager.GET_ACTIVITIES);
	        if (info != null) {
	            Intent intent = manager.getLaunchIntentForPackage(info.applicationInfo.packageName);
	            mainActivity.startActivity(intent);
	        }
   }
	/**
     * ��Ĭ��װ����װ֮ǰ����Ҫ��ȡ��ROOTȨ�ޣ��д�����
     * ԭ��:1.�Ȼ�ȡ��ROOTȨ��
     *  2.��ͨ������ķ�ʽֱ�Ӱ�װAPK
     * @return
     */
    public boolean silenceInstall(String downloadPath){
        Process process = null;
        OutputStream out = null;
        DataOutputStream dataOutputStream = null;
       // String downloadPath = mainActivity.getResources().getString(R.string.app_download_filepath)+ "Player.apk";
        try {
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            dataOutputStream = new DataOutputStream(out);
            dataOutputStream.writeBytes("chmod 777 " + downloadPath + "\n");
            dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + downloadPath);
            // �ύ����
            dataOutputStream.flush();
            int value = process.waitFor();
            if( value == 0){
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
  
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }finally{
            try {
                if( dataOutputStream != null ){
                    dataOutputStream.close();
                }
                if( out != null ){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
}
