package com.chengyu.imggetter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MainGetter {

	
	private static String url = "http://www.hydcd.com/cy/fkccy/index.htm";
	private static String imgBaseUrl = "http://www.hydcd.com/cy/fkccy/";
	private static HashMap<String, String> imgUrlInfo = new HashMap<String,String>();
	
	public static void main(String[] args) {

		getAllUrls(url);
		writeResultToFile();
		downLoadImage();
		
	}
	
	
	public static void getAllUrls(String url){
		System.out.println("开始抓取数据-----<");
		for(int i=1;i<=10;i++){
			url = "http://www.hydcd.com/cy/fkccy/index.htm";
			if(1 != i){
				
				int pointIndex = url.lastIndexOf('.');
				String first = url.substring(0, pointIndex);
				first += i;
				String last = url.substring(pointIndex);
				url = first + last;
			}
			//writeInfoToFile(url,i);
			getInfoFromFile(i);
		}
		System.out.println("抓取数据结束----->");
	}
	
	public static void writeInfoToFile(String url,int index){
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		try {
			URL imgUrl = new URL(url);
			is = imgUrl.openStream();
			byte[] bytes = new byte[1024];
			
			File file = new File("D:/chengyu"+index+".txt");
			if(!file.exists()){
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bis = new BufferedInputStream(is);
			int len;
			while(-1 !=(len = bis.read(bytes))){
				fos.write(bytes);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			
			try{
				if(null != bis){
					bis.close();
				}
				if(null != is){
					is.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}
	}
	
	public static void getInfoFromFile(int index){
		File file = new File("D:/chengyu"+index+".txt");
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line;
			while(null != (line = br.readLine())){
				
				if(line.contains("<img border=\"0\"") && line.contains("alt") && !line.contains("width")){
					handleOneLine(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try{
				if(null != br){
					br.close();
				}if(null != fr){
					fr.close();
				}
				
			}catch(Exception e){
				
			}
			
			
		}
	
	}
	
	
	public static void handleOneLine(String str){
		
		int firstEqualIndex = str.indexOf("src=");
		int lastEqualIndex = str.lastIndexOf("=");
		String url = str.substring(firstEqualIndex+5, str.indexOf(".")+4);
		String chengyu = str.substring(lastEqualIndex+2,str.lastIndexOf("\""));
		imgUrlInfo.put(url, chengyu);
	}
	
	public static void writeResultToFile(){
		
		System.out.println("开始写入数据-----<");
		File result = new File("D:/result.txt");
		FileWriter fw = null;
		if(result.exists()){
			result.delete();
		}
		if(!result.exists()){
			try {
				result.createNewFile();
				fw = new FileWriter(result);
				Set<String> keys = imgUrlInfo.keySet();
				Iterator<String> it = keys.iterator();
				while(it.hasNext()){
					
					String key = it.next();
					String value = imgUrlInfo.get(key);
					fw.write(value+":"+key+"\n");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(null != fw){
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			System.out.println("写入数据结束----->");
		}
	}
	
	public static void downLoadImage(){
		
		System.out.println("开始下载数据---<");
		long start = System.currentTimeMillis();
		Set<String> urls = imgUrlInfo.keySet();
		Iterator<String> it = urls.iterator();
		while(it.hasNext()){
			String url = it.next();
			try {
				URL imgUrl = new URL(imgBaseUrl+url);
				String fileName = url.substring(url.lastIndexOf("/")+1);
				System.out.println(fileName);
				File image = new File("D:/成语素材/"+url.substring(url.lastIndexOf("/")));
				if(!image.exists()){
					image.delete();
				}
				if(!image.exists()){
					image.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(image);
				InputStream is = imgUrl.openStream();
				byte[] bytes = new byte[1024];
				int len;
				while(-1 != (len = is.read(bytes))){
					fos.write(bytes);
				}
				fos.flush();
				if(null != fos){
					fos.close();
				}
				if(null != is){
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("下载数据结束--->");
		System.out.println("下载耗时："+(end-start)/1000.0+"秒");
	}
	
}
