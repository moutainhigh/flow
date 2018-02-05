/*
 * @(#)Utils.java  1.0 2004-9-1
 * 
 * Copyright (c) 2003-2005 ASPire Technologies, Inc.
 * 6/F,IER BUILDING, SOUTH AREA,SHENZHEN HI-TECH INDUSTRIAL PARK Mail Box:11# 12#.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * ASPire Technologies, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Aspire.
 **/

package com.aspire.etl.tool;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Class description goes here.
 * 
 * @version 1.0 2004-9-1
 * @author luoqi
 */
public class Utils {
	
	/**
	 * 生成一个正的随机数
	 * @return
	 */
	public static int getRandomIntValue() {
		return Math.abs(new Random().nextInt());
	}
	
	/**
	 * 初始化一个log4j的每天自动截断的文件logger,省略用配置文件初始化.
	 * @param loggerName  logger名
	 * @param logFilePath 文件保存路径
	 * @param logFileNamePrefix 文件前缀
	 * @param level 日志级别
	 * @return
	 */
	public static Logger initFileLog(String loggerName,String logFilePath,String logFileNamePrefix,String level) {
		//初始化一个文件logger		
		Logger log = Logger.getLogger(loggerName);
		log.setLevel(Level.toLevel(level));
		DailyRollingFileAppender fileAppender = new DailyRollingFileAppender();
		fileAppender.setEncoding("GB2312");
		fileAppender.setDatePattern("'.'yyyyMMdd");
		fileAppender.setName(loggerName);
		fileAppender.setLayout(new PatternLayout("[%-5p]%d{yyyy-MM-dd HH:mm:ss}  %m%n"));
		fileAppender
				.setFile(logFilePath + System.getProperty("file.separator") + logFileNamePrefix+".log");
		fileAppender.activateOptions();		

		log.addAppender(fileAppender);
		return log;
	}
	
	public static void println(String msg) {
		System.out.println(msg);
	}
	
	public static String getLocalIP(){
		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostAddress();
		} catch (UnknownHostException e) {
			//
		}
		return "ip not found";
	}

	/**
	 * 暂停period秒
	 * 
	 * @param period
	 *            单位：秒
	 */
	public static void sleeping(int period) {
		try {
			Thread.sleep(period * 1000);
		} catch (InterruptedException e) {
			System.out.println("休眠被中断， InterruptedException " + e.getMessage());
		}
	}

	/**
	 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
	 * hexStr2ByteArr(String strIn) 互为可逆的转换过程
	 * 
	 * @param arrB
	 *            需要转换的byte数组
	 * @return 转换后的字符串
	 * @throws Exception
	 *             本方法不处理任何异常，所有异常全部抛出
	 * @author <a href="mailto:zhangji@aspire-tech.com">ZhangJi</a>
	 */
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 * 
	 * @param strIn
	 *            需要转换的字符串
	 * @return 转换后的byte数组
	 * @throws Exception
	 *             本方法不处理任何异常，所有异常全部抛出
	 * @author <a href="mailto:zhangji@aspire-tech.com">ZhangJi</a>
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * 按类名动态加载类
	 * 
	 * @param path
	 * @param className
	 * @return
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */

	public static Class loadClassByName(String path, String className)
			throws MalformedURLException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		String url = "file:" + path ;
		URL u = new URL(url);

		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Class c = ucl.loadClass(className);

		return c;
	}

	/**
	 * 返回指定目录下指定后缀名的文件数组
	 * @param dirPath
	 * @param postfix
	 * @return
	 */
	public static File[] scanDir(String dirPath, final String postfix) {
		File dir = new File(dirPath);

		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File f, String postfix) {
				String fName = f.getName();
				if ((fName.toUpperCase().endsWith(postfix.toUpperCase()))) {
					return true;
				}
				return false;
			}
		});
		return files;
	}

	/**
	 * 返回指定目录的子目录
	 * @param dirPath
	 * @return
	 */
	public static File[] scanChildDir(String dirPath) {
		File dir = new File(dirPath);	
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return false;
			}
		});
		return files;
	}
	
	public static void main(String[] args){
		System.out.println(getLocalIP());
	}

}
