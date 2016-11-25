package com.kt.largescreen.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

public class ServersUrlPath {
	
	private String HTTP = "http://";
	private String server_field = "/ds";
	private String port ;//默认服务器端口
	private String server_ip;//默认服务器ip地址
	private String apkUpInfo_field_path;
	private String appUpdate_field_path;
	private String loginAuth_field_path;
	private String terminalUpdate_field_path;
	
	
	private IniWriteAndReadUtil iwaru;//定义读取配置文件
	private String ININAME = "config_setting.ini";
	private String SD_PATH = Configs.getRootFolder();
	private String INIPATH =SD_PATH+"/LargeScreen/config_setting.ini" ;
	private Context context;
	public ServersUrlPath(Context context) {
		this.context = context;
		 iwaru = new IniWriteAndReadUtil();
		 if(new File(INIPATH).exists()){
			 this.port = iwaru.readIniOption(INIPATH, null, "port", "server_port");
			 this.apkUpInfo_field_path = iwaru.readIniOption(INIPATH, null, "interfaceAddress", "apkUpInfo_field_path");
			 this.appUpdate_field_path = iwaru.readIniOption(INIPATH, null, "interfaceAddress", "appUpdate_field_path");
			 this.loginAuth_field_path = iwaru.readIniOption(INIPATH, null, "interfaceAddress", "loginAuth_field_path");
			 this.terminalUpdate_field_path = iwaru.readIniOption(INIPATH, null, "interfaceAddress", "terminalUpdate_field_path");
		 }else{
			 try {
				InputStream is =context.getAssets().open(ININAME);
				 this.port = iwaru.readIniOption(null, is, "port", "server_port");
				 this.apkUpInfo_field_path = iwaru.readIniOption(null, is, "interfaceAddress", "apkUpInfo_field_path");
				 this.appUpdate_field_path = iwaru.readIniOption(null, is, "interfaceAddress", "appUpdate_field_path");
				 this.loginAuth_field_path = iwaru.readIniOption(null, is, "interfaceAddress", "loginAuth_field_path");
				 this.terminalUpdate_field_path = iwaru.readIniOption(null, is, "interfaceAddress", "terminalUpdate_field_path");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		// TODO Auto-generated constructor stub
	}
	public String getMainServerIp(){
		String main_server_ip;
		if(new File(INIPATH).exists()){
			main_server_ip = iwaru.readIniOption(INIPATH,null,"server", "main_server");
			if(main_server_ip == null){
				return null;
			}else{
				return main_server_ip;
			}
		}else{//如果不存在 ，就读取assets里面的默认配置文件
			try {
				main_server_ip = iwaru.readIniOption(null,context.getAssets().open(ININAME),"server", "main_server");
				if(main_server_ip == null){
					return null;
				}else{
					return main_server_ip;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	public String getSecondServerIp(){
		String second_server_ip;
		if(new File(INIPATH).exists()){
			second_server_ip = iwaru.readIniOption(INIPATH,null,"server", "second_server");
			if(second_server_ip == null){
				return null;
			}else{
				return second_server_ip;
			}
		}else{//如果不存在 ，就读取assets里面的默认配置文件
			try {
				second_server_ip = iwaru.readIniOption(null,context.getAssets().open(ININAME),"server", "second_server");
				if(second_server_ip == null){
					return null;
				}else{
					return second_server_ip;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	public String getMainServerUrl(){
		//主服务器url
		String mainServerUrl = HTTP+getMainServerIp()+":"+port+server_field;	
		return mainServerUrl;
	}
	public String gerSecondServerUrl(){
		String secondServerUrl = HTTP+getSecondServerIp()+":"+port+server_field;
		return secondServerUrl;
		
	}
	public String getAppUpInfoUrl(String server_ip){
		String appUpinfoUrl =  HTTP+server_ip+":"+port+apkUpInfo_field_path;
		return appUpinfoUrl;
	}
	public String getAppUpdateUrl(String server_ip){
		String appUpdateUrl = HTTP+server_ip+":"+port+appUpdate_field_path;
		return appUpdateUrl;
	}
	public String getLoginAuthUrl(String server_ip){
		String loginAuthUrl = HTTP+server_ip+":"+port+loginAuth_field_path;
		return loginAuthUrl;
	}
	public String getTerminalUpdateUrl(String server_ip){
		String terminalUpdateUrl = HTTP+server_ip+":"+port+terminalUpdate_field_path;
		return terminalUpdateUrl;
	}
}
