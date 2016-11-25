package com.kt.largesreen.player.utils;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kt.largescreen.lib.NetAndServerDetection;
import com.kt.largescreen.lib.ServersUrlPath;
import com.kt.largesreen.player.R;
import com.kt.largesreen.player.SplashScreenActivity;

/*
 * 登陆认证，并下载闪屏图片以及更新节目（本地没有节目存在）
 *http://ip:port/moss/portal/portalLogin!getApkUpgradeInfo.action?mac=1A:1B:1C:1D:1E:2F&sn=10002102102102102102
 * */
public class LoginAuthentication {
	private Activity activity;
	private Context context;
	private String splashImgZipFilePath;//闪屏图片压缩包存放地址（包括文件名）
	private String channelScheduleFilePath;//日程xml文件存放地址
	private String programZipFilePath;//节目文件压缩包存放地址（包括文件名）
	
	public LoginAuthentication(Activity activity,Context context) {
		this.activity =activity;
		this.context=context;
	}
	public boolean loginAuth(String server_ip){
		NetAndServerDetection nasd = new NetAndServerDetection(activity, context);
		String mac = nasd.getMacAddress();
		String serial= android.os.Build.SERIAL;
		String loginAuthUrl = new ServersUrlPath(context).getLoginAuthUrl(server_ip)+"mac="+mac+"&sn="+serial;
		//String loginAuthUrl = new ServersUrlPath(context).getLoginAuthUrl(server_ip)+"mac=1A:1B:1C:1D:1E:2F&sn=10002102102102102102";
		System.out.println("LoginAuthentication---43:loginAuthUrl = "+ loginAuthUrl);
		HttpGet req = new HttpGet(loginAuthUrl);
		HttpClient httpCli = new DefaultHttpClient();
		try {
			HttpResponse httpResponse = httpCli.execute(req);
			if(httpResponse.getStatusLine().getStatusCode() == 200){
				SharedPreferences sp = context.getSharedPreferences("loginretrunString", 0);
				Editor editor = sp.edit();
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				JSONTokener json = new JSONTokener(strResult);
				JSONObject jsonObj = (JSONObject) json.nextValue();
				if(jsonObj.getString("success").equals("true")){
					System.out.println("LoginAuthentication -- 57请求成功");
					if(jsonObj.getString("certified").equals("true")){
						editor.putString("strResult", strResult);
						editor.commit();
						System.out.println("LoginAuthentication -- 58认证成功");
						return true;
					}else if(jsonObj.getString("certified").equals("false")){
						System.out.println("LoginAuthentication -- 61认证失败");
						editor.putString("strResult", "false");
						editor.commit();
						return false;
					}
					
//					String splashImgUrl = jsonObj.getString("backImg");
//					JSONObject channel = jsonObj.getJSONObject("channel");
//					String channelName = channel.getString("name");
//					String channelScheduleUrl = channel.getString("scheduleUrl");
//				//	fd.download(channelScheduleUrl, channelScheduleFilePath);//下载日程文件
//				//	fd.download(splashImgUrl, splashImgZipFilePath);//下载闪屏图片文件包
//					JSONArray programList = channel.getJSONArray("programList");
//					for(int i = 0; i < programList.length(); i++){
//						JSONObject programListObj = programList.getJSONObject(i);
//						String programUrl = programListObj.getString("url");
//						//fd.download(programUrl, programZipFilePath);//下载节目资源文件包
//					}
//					//下载完毕后进行解压缩，后面待做
					
				}else if(jsonObj.getString("success").equals("false")){
					editor.putString("strResult", "false");
					editor.commit();
					System.out.println("LoginAuthentication --84请求失败");
					return false;
				}else{
					editor.putString("strResult", "false");
					editor.commit();
					return false;
				}
				
			}else{
				System.out.println("LoginAuthentication -- 93 网络连接错误");
				return false;
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
		return false;
		
	}
}
