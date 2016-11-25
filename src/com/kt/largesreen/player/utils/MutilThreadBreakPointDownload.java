package com.kt.largesreen.player.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.kt.largescreen.lib.UnZipFile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*多线程断点下载，后面有待将存储在文本中的下载进度改为存储在数据库中
 * threadCount 线程个数， runningThread 正在运行的线程个数（其实跟threadCount一样）
 *download方法中的参数： count 线程个数，downloadUrl 下载地址 ，  fileName 下载文件在本地保存的路径（包括文件名）
 * 
 * 移植到android中需要进行修改代码格式：将download中的代码直接复制到onCreate先开的线程中，再在主Acticity中创建DownloadThread线程
 *或者直接new MutilThreadBreakPointDownload这个对象，直接调用download即可；
 *
 *下载不同的文件的时候，需要将本地保存的断点保存文件名字用下载的文件名字和threandId来区别
 * */
public class  MutilThreadBreakPointDownload {
	private int runningThread;
	private int threadCount;
	private Context context;
	public MutilThreadBreakPointDownload(Context context){
		this.context = context;
	}
	public  synchronized void download(int count,String downloadUrl,String fileNamePath,String name,String md5,String unZipPath){
		runningThread = count;
		threadCount = count;
		String path = downloadUrl;
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setRequestMethod("GET");
			int code = httpURLConnection.getResponseCode();
			if(code == 200){
				int length = httpURLConnection.getContentLength();
				//在客户端本地创建一个大小跟服务器上的文件一样大小的临时文件
				//使用 随机文件访问的类RandomAccessFile()创建,"rwd"表示文件会直接存储在硬盘，不会先保存在缓存区
				RandomAccessFile raf = new RandomAccessFile(fileNamePath, "rwd");
				//指定创建的文件的长度
				raf.setLength(length);
				raf.close();
				//假设3个线程去下载
				//平均每个线程下载的文件内容的大小
				int blockSize = length/threadCount;
				for(int threadId = 1;threadId <= threadCount;threadId++){
					//第一个线程下载的开始位置
					int startIndex = (threadId-1)*blockSize;
					int endIndex = threadId*blockSize-1;
					if(threadId == threadCount){
						//最后一个线程下载的长度的末尾直接到该文件的末尾
						endIndex = length;
					}
					System.out.println("线程"+threadId+"下载:"+startIndex+"--"+endIndex);
					new DownloadThread(startIndex, endIndex, path,fileNamePath,md5,unZipPath).start();
				}
			}else{
				System.out.println("服务器错误");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public  class DownloadThread extends Thread{
		private int startIndex;
		private int endIndex;
		private String path;
		private String fileName;
		private String md5;
		private String unZipPath;
		/**
		 * @param path下载文件在服务器上的路径
		 * 
		 */
		public DownloadThread(int startIndex,int endIndex,String path,String fileNamePath,String md5
				,String unZipPath) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.path = path;
			this.fileName = fileNamePath;
			this.md5 = md5;
			this.unZipPath = unZipPath;
			
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
				URL url;
				try {
					url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					//设置 服务器下载部分的文件，指定文件的位置
					conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
					conn.setConnectTimeout(5000);
					int code = conn.getResponseCode();//从服务器请求全部资源成功 返回200 从服务器请求部分文件资源成功返回206
					if(code == 206){
						InputStream is = conn.getInputStream();
						RandomAccessFile raf = new RandomAccessFile(fileName, "rwd");
						raf.seek(startIndex);//随机写文件的时候从哪个位置开始写
						int len = 0;
						byte[] buffer = new byte[1024];
						int total = 0;//已经下载的数据长度
						while((len = is.read(buffer)) != -1){
							raf.write(buffer, 0, len);
						}
						is.close();
						raf.close();
						System.out.println("文件读写完毕");
					}else{
						//下载失败
					}
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					System.out.println("fileNamePathmd5 = "+ md5+"--"+GetFileMd5.getMd5(fileName));
					if(md5.equals(GetFileMd5.getMd5(fileName))){
						try {
							UnZipFile.unzip(fileName,unZipPath);
							System.out.println(fileName+"解压文件成功");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}											
					
				}	
			}		
	}	
}
