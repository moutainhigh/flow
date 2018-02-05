package com.aspire.etl.flowmetadata;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {

    private static int BUFFER = 1024;
    private static String ENCODING = "GBK";
    /** *//**
     * ѹ���ļ�
     * @param src Դ�ļ�/Ŀ¼
     * @param dest ѹ������ļ�/Ŀ¼
     */
    public static void zip(String src, String dest) {
        File srcFile = new File(src);
        File destFile = new File(dest);
        if(destFile.isDirectory()){
            //����ѹ���ļ�����ȡ��ǰ�ļ�/Ŀ¼����Ϊѹ���ļ�����
            String name = srcFile.getName();
            name = name.indexOf(".")>0?name.substring(0,name.indexOf(".")):name;
            name = name+".zip";
            destFile = new File(destFile+"/"+name);
        }
        zip(srcFile, destFile);
    }

    /** *//**
     * ѹ���ļ�
     * @param src
     * @param dest
     */
    public static void zip(File src, File dest) {
        try {
            FileOutputStream fout = new FileOutputStream(dest);
            CheckedOutputStream chc = new CheckedOutputStream(fout, new CRC32());
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(chc));
            //zip(out,src,src.getName());//�����ǰĿ¼
            zip(out,src,"");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** *//**
     * �ݹ�ѹ���ļ���
     * @param out �����
     * @param srcFile ѹ���ļ���
     * @param path ѹ���ļ�·��
     */
    public static void zip(ZipOutputStream out, File srcFile, String path) {

        try {
            if (srcFile.isDirectory()) {
                File[] f = srcFile.listFiles();
                out.putNextEntry(new org.apache.tools.zip.ZipEntry(path + "/"));
                path = path.equals("") ? "" : path + "/";

                for (int i = 0; i < f.length; i++) {
                    zip(out, f[i], path + f[i].getName());
                }
            } else {
                out.putNextEntry(new org.apache.tools.zip.ZipEntry(path));
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(srcFile), "iso8859-1"));

                int c;
                while (-1 != (c = in.read())) {
                    out.write(c);
                }
                in.close();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

 

    /** *//**
     * �����ѹ��zip�ļ��ķ�����
     * δ��������ļ�������
     * @param zipFileName
     * @param outputDirectory
     */
    public static boolean unZip(String zipFileName, String outputDirectory) {
    	//System.out.println( " outputDirectory:" + outputDirectory);
            ZipInputStream in = null;
            BufferedOutputStream bos = null;
			try
			{
				in = new ZipInputStream(new BufferedInputStream(
				        new FileInputStream(zipFileName)));
//				��ȡZipInputStream�е�ZipEntry��Ŀ��һ��zip�ļ��п��ܰ������ZipEntry��
	            //��getNextEntry�����ķ���ֵΪnull�������ZipInputStream��û����һ��ZipEntry��
	            //��������ȡ��ɣ�
	            ZipEntry entry;
	            while ((entry = in.getNextEntry()) != null) {

	                //������zip���ļ���ΪĿ¼���ĸ�Ŀ¼
	                File f = new File(outputDirectory);
	                f.mkdir();
	                if (entry.isDirectory()) {
	                    String name = entry.getName();
	                    name = name.substring(0, name.length() - 1);
	                    
	                   /* System.err.println("dir: " + name);*/
	                    
	                    f = new File(outputDirectory + File.separator + name);
	                    f.mkdir();
	                } else {
	                    f = new File(outputDirectory + File.separator
	                            + entry.getName());
	                    
	                    //���Ŀ¼�����ڣ���Ҫ����Ŀ¼��
	                    if (!f.getParentFile().exists()){
	            			f.getParentFile().mkdirs();
	            		}

	                   /* System.out.println("file: " + outputDirectory + File.separator
	                            + entry.getName());*/
	                    f.createNewFile();
	                    FileOutputStream out = new FileOutputStream(f);
	                    bos = new BufferedOutputStream(out, BUFFER);
	                    int b;
	                    byte data[] = new byte[BUFFER];
	                    while ((b = in.read(data, 0, BUFFER)) != -1) {
	                        bos.write(data, 0, b);
	                    }
	                    
	                    //��Ҫflushһ�ѣ���Ȼ�ᶪ����
	                    bos.flush();
	                    bos.close();
	                }
	            }
	            
			}
			catch (FileNotFoundException e)
			{
				System.out.println("�ļ�δ�ҵ���" + zipFileName);
				e.printStackTrace();
				 return false;
			}
			catch (IOException e)
			{
				System.out.println("�ļ�IOException��" + zipFileName);
				e.printStackTrace();
				return false;
			}
			finally{
				try
				{
					if (in != null) in.close();
					if (bos != null) bos.close();
				}
				catch (IOException e)
				{
				}
				
			}
            return true;
    }

    /** *//**
     * ���û�������С
     * @param size
     */
    public static void setBuffer(int size) {
        BUFFER = size;
    }

    /** *//**
     * �����ַ�����
     * @param size
     */
    public static void setEncoding(String  encoding) {
        ENCODING = encoding;
    }
    
    
    public static void delDir(String filepath){   
		 File f = new File(filepath);//�����ļ�·��          
		 if(f.exists() && f.isDirectory()){//�ж����ļ�����Ŀ¼   
		     if(f.listFiles().length==0){//��Ŀ¼��û���ļ���ֱ��ɾ��   
		         f.delete();   
		     }else{//��������ļ��Ž����飬���ж��Ƿ����¼�Ŀ¼   
		         File delFile[]=f.listFiles();   
		         int i =f.listFiles().length;   
		         for(int j=0;j<i;j++){   
		             if(delFile[j].isDirectory()){   
		            	 delDir(delFile[j].getAbsolutePath());//�ݹ����del������ȡ����Ŀ¼·��   
		             }   
		             delFile[j].delete();//ɾ���ļ�   
		         }   
		     }   
		 }       
		}  
    
    public static boolean stringTofile(String str, String path){
		if (path == null)
		{
			return false;
		}


		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter(new BufferedWriter(new FileWriter(path)));
			pw.println(str);
			pw.flush();
			return true;
		}
		catch (IOException e)
		{
			//		   e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				pw.close();
			}
			catch (Exception e)
			{
			}
		}
	}
    
    /**
     * ��ȡĳĿ¼�µ������ļ���(������Ŀ¼�µ��ļ���)
     * @param list �洢�ļ�����ArrayList����
     * @param srcDirectory Ŀ¼����
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static void getFileNamesFromDir(ArrayList list ,String srcDirectory)
    { // �õ�Ŀ¼�µ��ļ���Ŀ¼����
    	File f = new File(srcDirectory);//�����ļ�·��          
    	if(f.exists() && f.isDirectory()){//�ж����ļ�����Ŀ¼   
    		if(f.listFiles().length==0){//��Ŀ¼��û���ļ�....
    			
    		}else{//��������ļ��Ž����飬���ж��Ƿ����¼�Ŀ¼   
    			File files[]=f.listFiles();   
    			int i =f.listFiles().length;   
    			for(int j=0;j<i;j++){   
    				if(files[j].isDirectory()){   
    					getFileNamesFromDir(list,files[j].getAbsolutePath());//�ݹ����del������ȡ����Ŀ¼·��   
    				}
    					list.add(files[j].getAbsolutePath());
    			}   
    		}   
    	}      
    }

    /**
	 * ���ļ���list��
	 * @param path
	 * @return List
	 * @roseuid 456BE5C901DA
	 */
	public static String fileToString(String path)
	{
		StringBuffer sb = new StringBuffer();
		BufferedReader myBufferedReader = null;
		String myString = null;
		FileReader fr = null;
		try
		{
			fr = new FileReader(path);
			myBufferedReader = new BufferedReader(fr);
			while ((myString = myBufferedReader.readLine()) != null)
			{
				if (!myString.equals(""))
					sb.append(myString + "\r\n");
			}
			if (myString != null)
			{
				myString = null;
			}
			if (fr != null)
			{
				fr.close();
				fr = null;
			}
			if (myBufferedReader != null)
			{
				myBufferedReader.close();
				myBufferedReader = null;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if (myString != null)
				{
					myString = null;
				}
				if (fr != null)
				{
					fr.close();
					fr = null;
				}
				if (myBufferedReader != null)
				{
					myBufferedReader.close();
					myBufferedReader = null;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return sb.toString();
	}



    public static void main(String[] args) throws IOException {

    	String path1 = "d:/tmp/";
        String path2 = "d:/test.zip";
        String path3 = "d:\\My Documents\\ETL_BAS1.1\\temp\\";

        ZipUtil.zip(path3,path2);
        ZipUtil.unZip(path2,path1);
        System.out.println("ѹ���ɹ���");
    }


}
