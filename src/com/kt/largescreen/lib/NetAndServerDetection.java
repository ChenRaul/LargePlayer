package com.kt.largescreen.lib;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;



import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetAndServerDetection {
	private Context context;
	private Activity activity;
	public NetAndServerDetection(Activity activity,Context context){
		this.context = context;
		this.activity = activity;
	}

	/**
	 *检测网络是否连接
	 * @return true，可用；false，不可用
	 */
	public boolean isOpenNetwork(){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getActiveNetworkInfo() == null){
			return false;
		}
		return connectivityManager.getActiveNetworkInfo().isAvailable();
	}
	/**
	 *检测是否连通服务器
	 *@param path,服务器地址
	 * @return 
	 */
	public boolean isServerConnect(String serverPath){
		URL url;
		HttpURLConnection connection;
		try {
			url = new URL(serverPath);
			connection= (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(5000);
			if(connection.getResponseCode() == HttpStatus.SC_OK){
			   return true;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		  HttpClient httpClient = new DefaultHttpClient();
//		  HttpGet httpGet = new HttpGet(path);
//		  HttpResponse httpResponse;
//		try {
//			httpResponse = httpClient.execute(httpGet);
//			int statusCode = httpResponse.getStatusLine().getStatusCode();
//			if(statusCode == HttpStatus.SC_OK){
//				   return true;
//				 }
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		  
		return false;
		
	}
	/**
	 *上传MAC、SN、硬件型号、APK版本号、IP地址、机顶盒的激活时间和最后使用时间相关信息。
	 *@param path,服务器地址
	 * @return 
	 * @throws MalformedURLException 
	 */
	public void uploadTerminalAndAppInfo(String server_ip) throws MalformedURLException{
		//机顶盒的激活时间和最后使用时间后面进行确认
		String macAddress = getMacAddress();
		String apkVersionCode = ApkVersionCode(context);
		InetAddress ip = getLocalInetAddress();
		String ip_string = ip.toString().substring(1, ip.toString().length());
		String model = android.os.Build.MODEL;//硬件型号
		String serial= android.os.Build.SERIAL;
		String apk_name = activity.getString(R.string.app_name);
		String httpUrl = new ServersUrlPath(context).getAppUpInfoUrl(server_ip)+"mac=" + macAddress + "&model=" + model + "&apk_version=" + apkVersionCode+ "&apk_name="+apk_name+"&apkip="+ip_string;
		//去除httpUrl中的空格
		String url = httpUrl.replaceAll(" ", "");
		HttpGet req = new HttpGet(url);
		System.out.println("httpUrl ="+url);
		HttpClient httpCli = new DefaultHttpClient();
		httpCli.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,2000);
		try {
			HttpResponse httpResp = httpCli.execute(req);
			int code = httpResp.getStatusLine().getStatusCode();
			System.out.println("code  = "+ code);
			if(code == 200){
				System.out.println("上报信息请求成功");
			}else{
				System.out.println("上报信息请求失败");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*获取机顶盒的有线网络的mac地址*/
	public String getMacAddress(){  
			    String strMacAddr = null;  
			    try {  
			        InetAddress ip = getLocalInetAddress();  
			  
			        byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();  
			        StringBuffer buffer = new StringBuffer();  
			        for (int i = 0; i < b.length; i++) {  
			            if (i != 0) {  
			                buffer.append('-');  
			            }  
			  
			            String str = Integer.toHexString(b[i] & 0xFF);  
			            buffer.append(str.length() == 1 ? 0 + str : str);  
			        }  
			        strMacAddr = buffer.toString().toUpperCase();  
			    } catch (Exception e) {  
			        // TODO Auto-generated catch block  
			        e.printStackTrace();  
			    }  
			  
			    return strMacAddr;  
			} 
		/*getLocalInetAddress方法为getMacAddress方法调用*/
	private InetAddress getLocalInetAddress() {  
		    InetAddress ip = null;  
		    try {  
		        Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();  
		        while (en_netInterface.hasMoreElements()) {  
		            NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();  
		            Enumeration<InetAddress> en_ip = ni.getInetAddresses();  
		            while (en_ip.hasMoreElements()) {  
		                ip = en_ip.nextElement();  
		                if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)  
		                    break;  
		                else  
		                    ip = null;  
		            }  
		  
		            if (ip != null) {  
		                break;  
		            }  
		        }  
		    } catch (SocketException e) {  
		        // TODO Auto-generated catch block  
		        e.printStackTrace();  
		    }  
		    return ip;  
		}  		
	/**
	* 获得APK的版本号
	* return 
	**/
	private static String ApkVersionCode(Context context)
	{
		PackageManager pack = context.getPackageManager();
		PackageInfo packageInfo = null;
		String versionCode = null;
		try {
		packageInfo = pack.getPackageInfo(context.getPackageName(), 0);
		versionCode = packageInfo.versionName;
		} catch (NameNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return versionCode;
	}
}
