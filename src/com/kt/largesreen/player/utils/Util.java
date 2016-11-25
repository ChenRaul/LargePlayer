package com.kt.largesreen.player.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.kt.largesreen.player.R;
import com.kt.largesreen.player.data.GroupInfo;
import com.kt.largesreen.player.data.ImgInfo;
import com.kt.largesreen.player.data.LayoutInfo;
import com.kt.largesreen.player.data.MediaInfo;
import com.kt.largesreen.player.data.TextInfo;
import com.kt.largesreen.player.data.TimeInfo;
import com.kt.largesreen.player.data.WebInfo;

public class Util {

	public static final String PACKAGE_NAME = "com.kt.largesreen.player";
	public static final String XML_NAME = "data.xml";
//	public static final String DATA_PATH = "/data"
//			+ Environment.getDataDirectory().getAbsolutePath() + "/"
//			+ PACKAGE_NAME + "/xml";
//	public static String XML_PATH = Util.DATA_PATH + "/" + Util.XML_NAME;
	public static String SD_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	public static String SD_XML_PATH = SD_PATH+"/PlayerData/data.xml";

//	public static void copyXmlData(Context context, boolean always) {
//		try {
//			File file = new File(DATA_PATH);
//			if (!file.exists() || always) {
//				file.mkdir();
//			}
//			File db = new File(file, XML_NAME);
//			if (db.exists() && !always) {
//				return;
//			}
//			if (db.exists()) {
//				db.delete();
//			}
//			db.createNewFile();
//			InputStream is = context.getResources().openRawResource(R.raw.data);
//			OutputStream os = new FileOutputStream(db);
//			byte[] buffer = new byte[1024];
//			int length;
//			while ((length = is.read(buffer)) > 0) {
//				os.write(buffer, 0, length);
//			}
//			os.flush();
//			os.close();
//			is.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public static Document getDocument(String xmlPath) {
		DocumentBuilder db = null;
		Document document = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(new File(xmlPath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	public static TextInfo getTextData(Element element) {
		TextInfo textInfo = new TextInfo();
		NamedNodeMap namedNodeMap = element.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			String strName = item.getNodeName();
			String strValue = item.getTextContent();
			if ("x".equalsIgnoreCase(strName)) {
				textInfo.setX(Integer.parseInt(strValue));
			} else if ("y".equalsIgnoreCase(strName)) {
				textInfo.setY(Integer.parseInt(strValue));
			} else if ("width".equalsIgnoreCase(strName)) {
				textInfo.setWidth(Integer.parseInt(strValue));
			} else if ("height".equalsIgnoreCase(strName)) {
				textInfo.setHeight(Integer.parseInt(strValue));
			} else if ("text".equalsIgnoreCase(strName)) {
				String[] str = strValue.split("br");
				textInfo.setText(str);
			} else if ("size".equalsIgnoreCase(strName)) {
				textInfo.setSize(Integer.parseInt(strValue));
			} else if ("color".equalsIgnoreCase(strName)) {
				textInfo.setColor(strValue);
			} else if ("style".equalsIgnoreCase(strName)) {
				textInfo.setStyle(strValue);
			} else if ("background".equalsIgnoreCase(strName)) {
				textInfo.setBackground(strValue);
			} else if ("anim".equalsIgnoreCase(strName)) {
				textInfo.setAnim(strValue);
			} else if ("singleline".equalsIgnoreCase(strName)) {
				textInfo.setSingleline(Boolean.parseBoolean(strValue));
			} else if ("frequence".equalsIgnoreCase(strName)) {
				textInfo.setFrequence(Integer.parseInt(strValue));
			} else if ("interval".equalsIgnoreCase(strName)) {
				textInfo.setInterval(Integer.parseInt(strValue));
			} else if ("startScroll".equalsIgnoreCase(strName)){
				textInfo.setStartScroll(Boolean.parseBoolean(strValue));
			}
		}
		return textInfo;
	}

	public static ImgInfo getImgData(Element element) {
		ImgInfo imgInfo = new ImgInfo();
		NamedNodeMap namedNodeMap = element.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			String strName = item.getNodeName();
			String strValue = item.getNodeValue();
			if ("x".equalsIgnoreCase(strName)) {
				imgInfo.setX(Integer.parseInt(strValue));
			} else if ("y".equalsIgnoreCase(strName)) {
				imgInfo.setY(Integer.parseInt(strValue));
			} else if ("width".equalsIgnoreCase(strName)) {
				imgInfo.setWidth(Integer.parseInt(strValue));
			} else if ("height".equalsIgnoreCase(strName)) {
				imgInfo.setHeight(Integer.parseInt(strValue));
			} else if ("src".equalsIgnoreCase(strName)) {
				String[] str = strValue.split(",");
				imgInfo.setSrc(str);
			} else if ("anim".equalsIgnoreCase(strName)) {
				imgInfo.setAnim(strValue);
			} else if ("frequence".equalsIgnoreCase(strName)) {
				imgInfo.setFrequence(Integer.parseInt(strValue));
			} else if ("interval".equalsIgnoreCase(strName)) {
				imgInfo.setInterval(Integer.parseInt(strValue));
			}else if ("buttonUrl".equalsIgnoreCase(strName)) {
				imgInfo.setButtonUrl(strValue);
			}else if ("isInfiniteLoop".equalsIgnoreCase(strName)) {
				imgInfo.setInfiniteLoop(Boolean.parseBoolean(strValue));
			}
		}
		return imgInfo;
	}

	public static MediaInfo getMediaData(Element element) {
		MediaInfo mediaInfo = new MediaInfo();
		NamedNodeMap namedNodeMap = element.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			String strName = item.getNodeName();
			String strValue = item.getNodeValue();
			if ("x".equalsIgnoreCase(strName)) {
				mediaInfo.setX(Integer.parseInt(strValue));
			} else if ("y".equalsIgnoreCase(strName)) {
				mediaInfo.setY(Integer.parseInt(strValue));
			} else if ("width".equalsIgnoreCase(strName)) {
				mediaInfo.setWidth(Integer.parseInt(strValue));
			} else if ("height".equalsIgnoreCase(strName)) {
				mediaInfo.setHeight(Integer.parseInt(strValue));
			} else if ("src".equalsIgnoreCase(strName)) {
				String[] str = strValue.split(",");
				mediaInfo.setSrc(str);
			}
		}
		return mediaInfo;
	}
	public static WebInfo getWebData(Element element) {
		WebInfo webInfo = new WebInfo();
		NamedNodeMap namedNodeMap = element.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			String strName = item.getNodeName();
			String strValue = item.getNodeValue();
			if ("x".equalsIgnoreCase(strName)) {
				webInfo.setX(Integer.parseInt(strValue));
			} else if ("y".equalsIgnoreCase(strName)) {
				webInfo.setY(Integer.parseInt(strValue));
			} else if ("width".equalsIgnoreCase(strName)) {
				webInfo.setWidth(Integer.parseInt(strValue));
			} else if ("height".equalsIgnoreCase(strName)) {
				webInfo.setHeight(Integer.parseInt(strValue));
			} else if ("url".equalsIgnoreCase(strName)) {
				String[] str = strValue.split(",");
				webInfo.setUrl(str);
			}
		}
		return webInfo;
	}
	public static TimeInfo getTimeData(Element element) {
		TimeInfo timeInfo = new TimeInfo();
		NamedNodeMap namedNodeMap = element.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			String strName = item.getNodeName();
			String strValue = item.getNodeValue();
			if ("time".equalsIgnoreCase(strName)) {
				timeInfo.setPlayTime(strValue);
			}
		}
		return timeInfo;
	}

	public static int getDuringData(Element element) {
		NamedNodeMap namedNodeMap = element.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			String strName = item.getNodeName();
			String strValue = item.getNodeValue();
			if ("during".equalsIgnoreCase(strName)) {
				return Integer.parseInt(strValue);
			}
		}
		return 0;
	}

	public static GroupInfo getGroupData(Element element) {
		GroupInfo groupFolderInfo = new GroupInfo();
		NamedNodeMap namedNodeMap = element.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			String strName = item.getNodeName();
			String strValue = item.getTextContent();
			if ("x".equalsIgnoreCase(strName)) {
				groupFolderInfo.setX(Integer.parseInt(strValue));
			} else if ("y".equalsIgnoreCase(strName)) {
				groupFolderInfo.setY(Integer.parseInt(strValue));
			} else if ("width".equalsIgnoreCase(strName)) {
				groupFolderInfo.setWidth(Integer.parseInt(strValue));
			} else if ("height".equalsIgnoreCase(strName)) {
				groupFolderInfo.setHeight(Integer.parseInt(strValue));
			} else if ("background".equalsIgnoreCase(strName)) {
				groupFolderInfo.setBackground(strValue);
			}
		}
		return groupFolderInfo;
	}

	public static LayoutInfo getLayoutData(Element element) {
		LayoutInfo layoutInfo = new LayoutInfo();
		NamedNodeMap namedNodeMap = element.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			String strName = item.getNodeName();
			String strValue = item.getTextContent();
			if ("x".equalsIgnoreCase(strName)) {
				layoutInfo.setX(Integer.parseInt(strValue));
			} else if ("y".equalsIgnoreCase(strName)) {
				layoutInfo.setY(Integer.parseInt(strValue));
			} else if ("width".equalsIgnoreCase(strName)) {
				layoutInfo.setWidth(Integer.parseInt(strValue));
			} else if ("height".equalsIgnoreCase(strName)) {
				layoutInfo.setHeight(Integer.parseInt(strValue));
			} else if ("background".equalsIgnoreCase(strName)) {
				layoutInfo.setBackground(strValue);
			}
		}
		return layoutInfo;
	}

	public static Bitmap getImage(String path, int sampleSize) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = sampleSize;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			return BitmapFactory.decodeFile(path, opts);
		} catch (Exception e) {
			return null;
		}
	}

	public static <V> int getSize(List<V> sourceList) {
		return sourceList == null ? 0 : sourceList.size();
	}
}