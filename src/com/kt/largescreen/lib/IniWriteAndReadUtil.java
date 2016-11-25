package com.kt.largescreen.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;

public class IniWriteAndReadUtil {

	public static String SD_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	public static String INI_PATH = SD_PATH + "/LargeScreen/config_setting.ini";

	public static void copyIniFile(Context context, boolean always) {
		try {
			File file = new File(SD_PATH + "/LargeScreen");
			if (!file.exists() || always) {
				file.mkdir();
			}
			File db = new File(file, "config_setting.ini");
			if (db.exists() && !always) {
				return;
			}
			if (db.exists()) {
				db.delete();
			}
			db.createNewFile();
			InputStream is = context.getAssets().open("config_setting.ini");
			OutputStream os = new FileOutputStream(db);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readIniFile(String path, InputStream is,String section, String option) {
		try {
			IniEditor inieditor = new IniEditor();
			if(path == null){
				inieditor.load(is);
			}else if(is == null){
				inieditor.load(path);
			}
			return inieditor.get(section, option);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readIniOption(String path,InputStream is,String section, String option) {
		//path由读取文件的人来决定
		//		String path = Environment.getExternalStorageDirectory()
//				.getAbsoluteFile() + "/LargeScreen/config_setting.ini";
		return readIniFile(path,is,section, option);
	}
	public static String readIniFile(String path, String section, String option) {
		try {
			IniEditor inieditor = new IniEditor();
			inieditor.load(path);
			return inieditor.get(section, option);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readIniOption(String section, String option) {
		String path = Environment.getExternalStorageDirectory()
				.getAbsoluteFile() + "/LargeScreen/config_setting.ini";
		return readIniFile(path, section, option);
	}
	public static void writeIniFile(String path, String section, String option, String value) {
		try {
			IniEditor inieditor = new IniEditor();
			inieditor.load(path);
			inieditor.set(section, option, value);// 修改值
			inieditor.save(path);// 保存
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeIniOption(String section, String option, String value){
		String path = Environment.getExternalStorageDirectory()
				.getAbsoluteFile() + "/LargeScreen/config_setting.ini";
		writeIniFile(path, section, option, value);
	}
}
