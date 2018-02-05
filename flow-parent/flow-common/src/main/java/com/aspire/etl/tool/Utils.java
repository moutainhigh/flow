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
	 * ����һ�����������
	 * @return
	 */
	public static int getRandomIntValue() {
		return Math.abs(new Random().nextInt());
	}
	
	/**
	 * ��ʼ��һ��log4j��ÿ���Զ��ضϵ��ļ�logger,ʡ���������ļ���ʼ��.
	 * @param loggerName  logger��
	 * @param logFilePath �ļ�����·��
	 * @param logFileNamePrefix �ļ�ǰ׺
	 * @param level ��־����
	 * @return
	 */
	public static Logger initFileLog(String loggerName,String logFilePath,String logFileNamePrefix,String level) {
		//��ʼ��һ���ļ�logger		
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
	 * ��ͣperiod��
	 * 
	 * @param period
	 *            ��λ����
	 */
	public static void sleeping(int period) {
		try {
			Thread.sleep(period * 1000);
		} catch (InterruptedException e) {
			System.out.println("���߱��жϣ� InterruptedException " + e.getMessage());
		}
	}

	/**
	 * ��byte����ת��Ϊ��ʾ16����ֵ���ַ����� �磺byte[]{8,18}ת��Ϊ��0813�� ��public static byte[]
	 * hexStr2ByteArr(String strIn) ��Ϊ�����ת������
	 * 
	 * @param arrB
	 *            ��Ҫת����byte����
	 * @return ת������ַ���
	 * @throws Exception
	 *             �������������κ��쳣�������쳣ȫ���׳�
	 * @author <a href="mailto:zhangji@aspire-tech.com">ZhangJi</a>
	 */
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// ÿ��byte�������ַ����ܱ�ʾ�������ַ����ĳ��������鳤�ȵ�����
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// �Ѹ���ת��Ϊ����
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// С��0F������Ҫ��ǰ�油0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * ����ʾ16����ֵ���ַ���ת��Ϊbyte���飬 ��public static String byteArr2HexStr(byte[] arrB)
	 * ��Ϊ�����ת������
	 * 
	 * @param strIn
	 *            ��Ҫת�����ַ���
	 * @return ת�����byte����
	 * @throws Exception
	 *             �������������κ��쳣�������쳣ȫ���׳�
	 * @author <a href="mailto:zhangji@aspire-tech.com">ZhangJi</a>
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		// �����ַ���ʾһ���ֽڣ������ֽ����鳤�����ַ������ȳ���2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * ��������̬������
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
	 * ����ָ��Ŀ¼��ָ����׺�����ļ�����
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
	 * ����ָ��Ŀ¼����Ŀ¼
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
