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
     * 压缩文件
     * @param src 源文件/目录
     * @param dest 压缩后的文件/目录
     */
    public static void zip(String src, String dest) {
        File srcFile = new File(src);
        File destFile = new File(dest);
        if(destFile.isDirectory()){
            //构造压缩文件名。取当前文件/目录名称为压缩文件名。
            String name = srcFile.getName();
            name = name.indexOf(".")>0?name.substring(0,name.indexOf(".")):name;
            name = name+".zip";
            destFile = new File(destFile+"/"+name);
        }
        zip(srcFile, destFile);
    }

    /** *//**
     * 压缩文件
     * @param src
     * @param dest
     */
    public static void zip(File src, File dest) {
        try {
            FileOutputStream fout = new FileOutputStream(dest);
            CheckedOutputStream chc = new CheckedOutputStream(fout, new CRC32());
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(chc));
            //zip(out,src,src.getName());//输出当前目录
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
     * 递归压缩文件夹
     * @param out 输出流
     * @param srcFile 压缩文件名
     * @param path 压缩文件路径
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
     * 定义解压缩zip文件的方法。
     * 未解决中文文件名问题
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
//				获取ZipInputStream中的ZipEntry条目，一个zip文件中可能包含多个ZipEntry，
	            //当getNextEntry方法的返回值为null，则代表ZipInputStream中没有下一个ZipEntry，
	            //输入流读取完成；
	            ZipEntry entry;
	            while ((entry = in.getNextEntry()) != null) {

	                //创建以zip包文件名为目录名的根目录
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
	                    
	                    //如果目录不存在，需要创建目录：
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
	                    
	                    //需要flush一把，不然会丢数据
	                    bos.flush();
	                    bos.close();
	                }
	            }
	            
			}
			catch (FileNotFoundException e)
			{
				System.out.println("文件未找到：" + zipFileName);
				e.printStackTrace();
				 return false;
			}
			catch (IOException e)
			{
				System.out.println("文件IOException：" + zipFileName);
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
     * 设置缓冲区大小
     * @param size
     */
    public static void setBuffer(int size) {
        BUFFER = size;
    }

    /** *//**
     * 设置字符编码
     * @param size
     */
    public static void setEncoding(String  encoding) {
        ENCODING = encoding;
    }
    
    
    public static void delDir(String filepath){   
		 File f = new File(filepath);//定义文件路径          
		 if(f.exists() && f.isDirectory()){//判断是文件还是目录   
		     if(f.listFiles().length==0){//若目录下没有文件则直接删除   
		         f.delete();   
		     }else{//若有则把文件放进数组，并判断是否有下级目录   
		         File delFile[]=f.listFiles();   
		         int i =f.listFiles().length;   
		         for(int j=0;j<i;j++){   
		             if(delFile[j].isDirectory()){   
		            	 delDir(delFile[j].getAbsolutePath());//递归调用del方法并取得子目录路径   
		             }   
		             delFile[j].delete();//删除文件   
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
     * 获取某目录下的所有文件名(包括子目录下的文件名)
     * @param list 存储文件名的ArrayList对象
     * @param srcDirectory 目录名称
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static void getFileNamesFromDir(ArrayList list ,String srcDirectory)
    { // 得到目录下的文件和目录数组
    	File f = new File(srcDirectory);//定义文件路径          
    	if(f.exists() && f.isDirectory()){//判断是文件还是目录   
    		if(f.listFiles().length==0){//若目录下没有文件....
    			
    		}else{//若有则把文件放进数组，并判断是否有下级目录   
    			File files[]=f.listFiles();   
    			int i =f.listFiles().length;   
    			for(int j=0;j<i;j++){   
    				if(files[j].isDirectory()){   
    					getFileNamesFromDir(list,files[j].getAbsolutePath());//递归调用del方法并取得子目录路径   
    				}
    					list.add(files[j].getAbsolutePath());
    			}   
    		}   
    	}      
    }

    /**
	 * 读文件到list中
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
        System.out.println("压缩成功！");
    }


}
