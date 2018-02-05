package com.aspire.etl.flowdesinger;
import java.util.*;
import java.io.*;

/**
 * 文件名：SysProb.java
 *	描述： 取得当前系统变量的程序。 java中的System.getProperty只是针对JVM来的，如果要取得系统环境变量，还要用到系统相关的函数
 * @author x_jiangts
 *
 */
class SysProb {
	// 返回当前系统变量的函数，结果放在一个Properties里边，这里只针对win2k以上的，其它系统可以自己改进
	public static Properties getEnv() throws Exception {
		Properties prop = new Properties();
		String OS = System.getProperty("os.name").toLowerCase();
		Process p = null;
		if (OS.indexOf("windows") > -1) {
			p = Runtime.getRuntime().exec("cmd /c set"); // 其它的操作系统可以自行处理，
															// 我这里是win2k
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			int i = line.indexOf("=");
			if (i > -1) {
				String key = line.substring(0, i);
				String value = line.substring(i + 1);
				prop.setProperty(key, value);
			}
		}
		return prop;
	}

	// 具体用法
	public static void main(String[] args) {
		try {
			SysProb sp = new SysProb();
			Properties p = sp.getEnv();
			System.out.println(p.getProperty("JAVA_HOME")); // 注意大小写，如果写成path就不对了
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
