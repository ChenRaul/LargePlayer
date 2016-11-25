package com.kt.largescreen.lib;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class GetFileMd5 {
	public static String getMd5(String path){
        try {
            // 获取一个文件的特征信息，签名信息。
            File file = new File(path);
            // md5
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb  = new StringBuffer();
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 
                String str = Integer.toHexString(number);
                // System.out.println(str);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
