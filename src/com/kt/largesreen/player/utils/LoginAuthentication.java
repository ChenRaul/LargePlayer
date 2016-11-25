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
 * ��½��֤������������ͼƬ�Լ����½�Ŀ������û�н�Ŀ���ڣ�
 *http://ip:port/moss/portal/portalLogin!getApkUpgradeInfo.action?mac=1A:1B:1C:1D:1E:2F&sn=10002102102102102102
 * */
public class LoginAuthentication {
	private Activity activity;
	private Context context;
	private String splashImgZipFilePath;//����ͼƬѹ������ŵ�ַ�������ļ�����
	private String channelScheduleFilePath;//�ճ�xml�ļ���ŵ�ַ
	private String programZipFilePath;//��Ŀ�ļ�ѹ������ŵ�ַ�������ļ�����
	
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
					System.out.println("LoginAuthentication -- 57����ɹ�");
					if(jsonObj.getString("certified").equals("true")){
						editor.putString("strResult", strResult);
						editor.commit();
						System.out.println("LoginAuthentication -- 58��֤�ɹ�");
						return true;
					}else if(jsonObj.getString("certified").equals("false")){
						System.out.println("LoginAuthentication -- 61��֤ʧ��");
						editor.putString("strResult", "false");
						editor.commit();
						return false;
					}
					
//					String splashImgUrl = jsonObj.getString("backImg");
//					JSONObject channel = jsonObj.getJSONObject("channel");
//					String channelName = channel.getString("name");
//					String channelScheduleUrl = channel.getString("scheduleUrl");
//				//	fd.download(channelScheduleUrl, channelScheduleFilePath);//�����ճ��ļ�
//				//	fd.download(splashImgUrl, splashImgZipFilePath);//��������ͼƬ�ļ���
//					JSONArray programList = channel.getJSONArray("programList");
//					for(int i = 0; i < programList.length(); i++){
//						JSONObject programListObj = programList.getJSONObject(i);
//						String programUrl = programListObj.getString("url");
//						//fd.download(programUrl, programZipFilePath);//���ؽ�Ŀ��Դ�ļ���
//					}
//					//������Ϻ���н�ѹ�����������
					
				}else if(jsonObj.getString("success").equals("false")){
					editor.putString("strResult", "false");
					editor.commit();
					System.out.println("LoginAuthentication --84����ʧ��");
					return false;
				}else{
					editor.putString("strResult", "false");
					editor.commit();
					return false;
				}
				
			}else{
				System.out.println("LoginAuthentication -- 93 �������Ӵ���");
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
