package com.kt.largesreen.player.utils;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.kt.largescreen.lib.NetAndServerDetection;
import com.kt.largescreen.lib.ServersUrlPath;
import com.kt.largesreen.player.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

public class TimingUpdateDownlodaThread extends Thread {
	private Context context;
	private String data;
	private int zipcount;
	private String server_ip;
	private String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public TimingUpdateDownlodaThread(Context context,String data){
		this.context = context;
		this.data = data;
		
	}
	@Override
	public void run() {
		while(true){
			try {
				System.out.println("正在等待定时检测下载更新内容");
				Thread.sleep(600000);//每一次间隔10分钟检测内容更新一次
				System.out.println("正在进行定时检测下载更新内容");
				HttpGet req = new HttpGet(data);
				HttpClient httpCli = new DefaultHttpClient();	
				httpCli.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
					HttpResponse httpResponse;
					try {
						httpResponse = httpCli.execute(req);
						if(httpResponse.getStatusLine().getStatusCode() == 200){
							String strResult = EntityUtils.toString(httpResponse.getEntity());
							JSONTokener json = new JSONTokener(strResult);
							JSONObject jsonObj = (JSONObject) json.nextValue();
							if(jsonObj.getString("success").equals("true")){
								if(jsonObj.getString("certified").equals("true")){
									JSONObject channel = jsonObj.getJSONObject("channel");
									String channelName = channel.getString("name");
									JSONArray programList = channel.getJSONArray("programList");
									 MutilThreadBreakPointDownload mtbpd = new MutilThreadBreakPointDownload(context);
									 int programListLength = programList.length();
									 String [] downloadUrl = new String[programListLength];
									 String playOrder = null;
									 String md5 = null;
									 for(int i = 0; i < programListLength; i++){
											JSONObject programListObj = programList.getJSONObject(i);
											String programUrl = programListObj.getString("url");
											playOrder = programListObj.getString("playOrder");
											 md5 = programListObj.getString("md5");
											File file = new File(SD_PATH+context.getString(R.string.program_zipfile_path));
											String [] fileString = file.list();
											int md5Count = 0;
											int length = fileString.length; 
											for(int j = 0; j < length;j++){
												System.out.println(GetFileMd5.getMd5(SD_PATH+context.getString(R.string.program_zipfile_path)+fileString[j]));
												if(!GetFileMd5.getMd5(SD_PATH+context.getString(R.string.program_zipfile_path)+fileString[j]).equals(md5)){
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
												mtbpd.download(3, downloadUrl[k], programFilemake(context),"program"+playOrder,md5,programDirFilemake(context));
									 		}
									 	}
									 	downloadUrl = null;
								}
							}
						}
					}finally{
						
					} 
			}catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}
	private String programFilemake(Context context){
		SharedPreferences sp = context.getSharedPreferences("ZIPFileCount", context.MODE_PRIVATE);
		if(sp.getInt("zipcount",0) == 0){
			zipcount = 1;
		}else{
			zipcount = sp.getInt("zipcount",0);
		}
		String programZipFilePath = SD_PATH+context.getString(R.string.program_zipfile_path)+zipcount+".zip";//下载的节目压缩包存放地址（包括文件名）
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
	
	private String programDirFilemake(Context context){
		int count;
		SharedPreferences sp = context.getSharedPreferences("DirFileCount",0);
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
