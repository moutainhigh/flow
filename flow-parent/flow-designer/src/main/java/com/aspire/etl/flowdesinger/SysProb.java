package com.aspire.etl.flowdesinger;
import java.util.*;
import java.io.*;

/**
 * �ļ�����SysProb.java
 *	������ ȡ�õ�ǰϵͳ�����ĳ��� java�е�System.getPropertyֻ�����JVM���ģ����Ҫȡ��ϵͳ������������Ҫ�õ�ϵͳ��صĺ���
 * @author x_jiangts
 *
 */
class SysProb {
	// ���ص�ǰϵͳ�����ĺ������������һ��Properties��ߣ�����ֻ���win2k���ϵģ�����ϵͳ�����Լ��Ľ�
	public static Properties getEnv() throws Exception {
		Properties prop = new Properties();
		String OS = System.getProperty("os.name").toLowerCase();
		Process p = null;
		if (OS.indexOf("windows") > -1) {
			p = Runtime.getRuntime().exec("cmd /c set"); // �����Ĳ���ϵͳ�������д���
															// ��������win2k
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

	// �����÷�
	public static void main(String[] args) {
		try {
			SysProb sp = new SysProb();
			Properties p = sp.getEnv();
			System.out.println(p.getProperty("JAVA_HOME")); // ע���Сд�����д��path�Ͳ�����
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
