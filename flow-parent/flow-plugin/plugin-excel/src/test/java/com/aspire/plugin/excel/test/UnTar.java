package com.aspire.plugin.excel.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;


public class UnTar {
	public static void main(String[] args) {
		unGzipFile("/Users/chenhaitao/Desktop/mvn/1177_20171101_0000.txt.gz");
	}
	public static void unGzipFile(String sourcedir) {
        String ouputfile = "";
        try {  
            //����gzipѹ���ļ������� 
            FileInputStream fin = new FileInputStream(sourcedir);   
            //����gzip��ѹ������
            GZIPInputStream gzin = new GZIPInputStream(fin);   
            //������ѹ�ļ������  
            ouputfile = sourcedir.substring(0,sourcedir.lastIndexOf('.'));
            ouputfile = ouputfile.substring(0,ouputfile.lastIndexOf('.'));
            FileOutputStream fout = new FileOutputStream(ouputfile);   
            
            int num;
            byte[] buf=new byte[1024];

            while ((num = gzin.read(buf,0,buf.length)) != -1)
            {   
                fout.write(buf,0,num);   
            }

            gzin.close();   
            fout.close();   
            fin.close();   
        } catch (Exception ex){  
            System.err.println(ex.toString());  
        }  
        return;
    }
}
