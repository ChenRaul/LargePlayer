package com.kt.largesreen.player.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*下载文件的类*/
public class FileDownload {
	public int download(String downloadUrl,String filePath){
		URL url;
		try {
			url = new URL(downloadUrl);
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			byte[] bt = new byte[1024];
			int leng;
			File file = new File(createFileForDownload(filePath));
			OutputStream os = new FileOutputStream(file);
			while((leng = is.read(bt)) != -1){
				os.write(bt, 0, leng);
			}
			os.close();
			is.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 1;
	}

	/**
	 * 删除同名的旧文件
	 * @return String
	 */
	public String createFileForDownload(String filePath) {
		File file = new File(filePath);
		if (file.exists()) { 							// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
			file.delete();
		}
		return filePath;
	}
	
}
