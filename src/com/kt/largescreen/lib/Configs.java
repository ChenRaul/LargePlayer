package com.kt.largescreen.lib;

import android.os.Environment;
import android.os.SystemProperties;

public class Configs {

	/*凯天机顶盒的获取root权限*/
	public static String KEY_ROOT_FOLDER = "persist.sys.ktroot_folder";
	public static String SD_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	public static String DEFAULT_ROOT_FOLDER = SD_PATH;
	public static String getRootFolder() {
		return SystemProperties.get(KEY_ROOT_FOLDER, DEFAULT_ROOT_FOLDER);
	}
}
