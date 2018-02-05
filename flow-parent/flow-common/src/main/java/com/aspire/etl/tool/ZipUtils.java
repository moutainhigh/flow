/*
 * @(#)ZipUtils.java  1.0 2005-7-27
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.zip.ZipOutputStream;

/**
 * @author libaoyu
 * @version 2007-04-30
 */
public class ZipUtils {
 /**
  *@param inputFileName, file or directory waiting for zipping 
  *@param outputFileName, output file name
  *@param fileNameList, file or directory waiting for zipping (type : List)  
  *
  */
 public static void zip(String inputFileName,String outputFileName) throws Exception {
	 ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFileName));
	 zip(out, new File(inputFileName), "");
	 System.out.println("zip done.");
	 out.close();
 }
 
 public static void zip(List fileNameList, String outputFileName)throws Exception{
	 ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFileName));
	 for (Iterator iter = fileNameList.iterator(); iter.hasNext();) {
		 String element = (String) iter.next();
		 zip(out, new File(element), "");
	 }
	 System.out.println("zip done.(type: List)");
	 out.close();
 }
 
public static void zip(File inputFile,String zipFileName) throws Exception {
	ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
			zipFileName));
	zip(out, inputFile, "");
	out.close();
}
	
 
 private static void zip(ZipOutputStream out, File f, String base) throws Exception {
  if (f.isDirectory()) {
   File[] fl = f.listFiles();
   //if (System.getProperty("os.name").startsWith("Windows")){
   if (!File.separator.equals("/")){
	   base = base.length() == 0 ? "" : base + "\\";
	   if (base.length()!=0){
		   out.putNextEntry(new org.apache.tools.zip.ZipEntry(base+ "\\"));
		   
	   }
   }
   else{	
	base = base.length() == 0 ? "" : base + "/";	
	if (base.length()!=0){
		out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
	}
   }
   for (int i = 0; i < fl.length; i++) {	 
    zip(out, fl[i], base + fl[i].getName());
   }
  }
  else {
   if (base.length()==0){
	   base = f.getName();
   }
   out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
   FileInputStream in = new FileInputStream(f);
   int b;
   while ( (b = in.read()) != -1) {
    out.write(b);
   }
   in.close();
  }
 }
 
 public static void main(String[] args){
//	// 测试。。。
//    ZipUtils m_zip=new ZipUtils();
//    List filename = new ArrayList();
//    filename.add("D:\\aaa\\unix常用.txt");
//    filename.add("D:\\aa.txt");
//    filename.add("D:\\ExtractData.sh");
//    filename.add("D:\\彩信SP统计基础报表_200607_000001.txt");
//    filename.add("D:\\中央抽取月报\\中央每月发送报表抽取\\SQL脚本\\MMS业务\\彩信业务综合报表\\彩信SP统计基础报表\\彩信SP统计基础报表_月_CS.sql");
//    filename.add("D:\\数据抽取_数据文件增加换行.rar");
//  try{
//   m_zip.zip(filename, "D:\\BB.zip");
////   m_zip.zip("D:\\aaa","D:\\aaaaaa.zip");
//  }catch(Exception ex){
//   ex.printStackTrace();
//  }
 }
}


