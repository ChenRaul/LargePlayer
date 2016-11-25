package com.kt.largesreen.player.utils;

import java.io.File;

import com.kt.largesreen.player.R;

import android.content.Context;
import android.os.Environment;

/*创建本地文件夹的路径*/
public class MakeFilePath {
	private Context context;
	private String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public MakeFilePath(Context context){
		this.context = context;
	}
	public void makeFileDir(){
		//先创建根目录LargeScreen
		System.out.println("创建文件夹");
		File rootFile = new File(SD_PATH+"/LargeScreen/");
		if(!rootFile.exists()){
			rootFile.mkdir();
		}
		File programZipFile = new File(SD_PATH+context.getString(R.string.program_zipfile_path));
		if(!programZipFile.exists()){
			programZipFile.mkdir();
		}
		File appDownloadFile = new File(SD_PATH+context.getString(R.string.app_download_filepath));
		if(!appDownloadFile.exists()){
			appDownloadFile.mkdir();
		}
		File terminalDownloadFile = new File(SD_PATH+context.getString(R.string.terminal_download_filepath));
		if(!terminalDownloadFile.exists()){
			terminalDownloadFile.mkdir();
		}
		File splashFile = new File(SD_PATH+context.getString(R.string.splashImg_path));
		System.out.println("开始建立splashFile文件夹");
		if(!splashFile.exists()){
			System.out.println("splashimg文件夹创建成功");
			splashFile.mkdir();
		}
		File playProgramFile = new File(SD_PATH+context.getString(R.string.program_file_path));
		if(!playProgramFile.exists()){
			playProgramFile.mkdir();
		}
		File newsFile = new File(SD_PATH+context.getString(R.string.News_zipfile_path));
		if(!newsFile.exists()){
			newsFile.mkdir();
		}
		File videoGasketFile = new File(SD_PATH+context.getString(R.string.video_gasket_path));
		if(!videoGasketFile.exists()){
			videoGasketFile.mkdir();
		}
		System.out.println("MakeFilePath -- 54文件夹创建完毕");
	}
}
