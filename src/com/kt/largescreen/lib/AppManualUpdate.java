package com.kt.largescreen.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;



public class AppManualUpdate {
	
	/*
	 * App 手动更新
	 * @return 0,服务器更新出现问题  1，更新文件已经下载成功  2，无需更新
	 * 手动更新的类里面没有写安装和启动
	 * 
	 * */
	public int Update(Activity activity){
		ServersUrlPath sup = new ServersUrlPath(activity);
		String server_ip = sup.getMainServerIp();
		String url = sup.getAppUpdateUrl(server_ip);
		NetAndServerDetection nasd = new NetAndServerDetection(activity, null);
		String mac = nasd.getMacAddress();
		String apk_name = activity.getString(R.string.app_name);
		String urlPath = url+"mac="+"&apk_name="+apk_name;
		int mainRet = download(activity, urlPath);
		if(mainRet == 0){//使用备用服务器更新
			server_ip = sup.getSecondServerIp();
			url = sup.getAppUpdateUrl(server_ip);
			urlPath = url+"mac="+"&apk_name="+apk_name;;
			int secondRet = download(activity, urlPath);
			if(secondRet == 0){
				return 0;
			}else if(mainRet == 1){
				return 1;
			}else if(mainRet == 2){
				return 2;
			}
		}else if(mainRet == 1){
			return 1;
		}else if(mainRet == 2){
			return 2;
		}
		
		return 0;
		
	}
	private int download(Activity activity,String urlPath){
		HttpClient httpClient = new DefaultHttpClient();
		  HttpGet httpGet = new HttpGet(urlPath);
		  httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
			HttpResponse httpResp;
			try {
				httpResp = httpClient.execute(httpGet);
				if((httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)){
					String strResult = EntityUtils.toString(httpResp.getEntity());
					JSONTokener jsonPar = new JSONTokener(strResult);			
					JSONObject pers = (JSONObject) jsonPar.nextValue();
					if(pers.getString("resultcode").equals("1")){
						String download_url = pers.getString("download_url");
						String md5 = pers.getString("md5");
						String size = pers.getString("size");
						String downloadPath = activity.getString(R.string.app_download_filepath);
				 		try {
				 			URL durl = new URL(download_url);
				 			URLConnection con = durl.openConnection();
				 			InputStream is = con.getInputStream();
				 			byte[] bt = new byte[1024];
				 			int leng = 0;
				 			int sum = 0;
				 			 String filename = con.getHeaderField("Content-Disposition");
				 			File file = new File(createFileForDownload(downloadPath+"Player.zip"));
				 			OutputStream os = new FileOutputStream(file);
				 			while((leng = is.read(bt)) != -1){
				 				os.write(bt, 0, leng);
				 			}
				 			os.close();
				 			is.close();
				 			if(md5.equals(new GetFileMd5().getMd5(downloadPath+"Player.zip"))){
				 				UnZipFile uzf = new UnZipFile();
				 				uzf.unzip(downloadPath+"Player.zip", downloadPath);
				 			}
				 		} catch (MalformedURLException e) {
				 			// TODO Auto-generated catch block
				 			e.printStackTrace();
				 		} catch (IOException e) {
				 			// TODO Auto-generated catch block
				 			e.printStackTrace();
				 		}
				 		return 1;
					}else if(pers.getString("resultcode").equals("0")){
						System.out.println("无需更新");
						return 2;
					}
					return 0;
				}else{//使用配用服务器检查更新
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
	 private String createFileForDownload(String filePath) {
			File file = new File(filePath);
			if (file.exists()) { 							// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
				file.delete();
			}
			return filePath;
		}
}

