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

/*���̶߳ϵ����أ������д����洢���ı��е����ؽ��ȸ�Ϊ�洢�����ݿ���
 * threadCount �̸߳����� runningThread �������е��̸߳�������ʵ��threadCountһ����
 *download�����еĲ����� count �̸߳�����downloadUrl ���ص�ַ ��  fileName �����ļ��ڱ��ر����·���������ļ�����
 * 
 * ��ֲ��android����Ҫ�����޸Ĵ����ʽ����download�еĴ���ֱ�Ӹ��Ƶ�onCreate�ȿ����߳��У�������Acticity�д���DownloadThread�߳�
 *����ֱ��new MutilThreadBreakPointDownload�������ֱ�ӵ���download���ɣ�
 *
 *���ز�ͬ���ļ���ʱ����Ҫ�����ر���Ķϵ㱣���ļ����������ص��ļ����ֺ�threandId������
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
				//�ڿͻ��˱��ش���һ����С���������ϵ��ļ�һ����С����ʱ�ļ�
				//ʹ�� ����ļ����ʵ���RandomAccessFile()����,"rwd"��ʾ�ļ���ֱ�Ӵ洢��Ӳ�̣������ȱ����ڻ�����
				RandomAccessFile raf = new RandomAccessFile(fileNamePath, "rwd");
				//ָ���������ļ��ĳ���
				raf.setLength(length);
				raf.close();
				//����3���߳�ȥ����
				//ƽ��ÿ���߳����ص��ļ����ݵĴ�С
				int blockSize = length/threadCount;
				for(int threadId = 1;threadId <= threadCount;threadId++){
					//��һ���߳����صĿ�ʼλ��
					int startIndex = (threadId-1)*blockSize;
					int endIndex = threadId*blockSize-1;
					if(threadId == threadCount){
						//���һ���߳����صĳ��ȵ�ĩβֱ�ӵ����ļ���ĩβ
						endIndex = length;
					}
					System.out.println("�߳�"+threadId+"����:"+startIndex+"--"+endIndex);
					new DownloadThread(startIndex, endIndex, path,fileNamePath,md5,unZipPath).start();
				}
			}else{
				System.out.println("����������");
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
		 * @param path�����ļ��ڷ������ϵ�·��
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
					//���� ���������ز��ֵ��ļ���ָ���ļ���λ��
					conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
					conn.setConnectTimeout(5000);
					int code = conn.getResponseCode();//�ӷ���������ȫ����Դ�ɹ� ����200 �ӷ��������󲿷��ļ���Դ�ɹ�����206
					if(code == 206){
						InputStream is = conn.getInputStream();
						RandomAccessFile raf = new RandomAccessFile(fileName, "rwd");
						raf.seek(startIndex);//���д�ļ���ʱ����ĸ�λ�ÿ�ʼд
						int len = 0;
						byte[] buffer = new byte[1024];
						int total = 0;//�Ѿ����ص����ݳ���
						while((len = is.read(buffer)) != -1){
							raf.write(buffer, 0, len);
						}
						is.close();
						raf.close();
						System.out.println("�ļ���д���");
					}else{
						//����ʧ��
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
							System.out.println(fileName+"��ѹ�ļ��ɹ�");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}											
					
				}	
			}		
	}	
}
