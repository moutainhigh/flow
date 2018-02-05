package com.aspire.plugin.excel.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class txt2excel {
	public static void main(String[] args) {
		unGzipFile("/Users/chenhaitao/Desktop/excel/1177_20171101_0000.txt.gz");
		File file = new File("/Users/chenhaitao/Desktop/excel/1177_20171101_0000");// 将读取的txt文件
		File file2 = new File("/Users/chenhaitao/Desktop/excel/1177_20171101_0000.xls");// 将生成的excel表格
		File file3 = new File("/Users/chenhaitao/Downloads/4229_201711022330_0000.txt");
		if (file.exists() && file.isFile()) {

			InputStreamReader read = null;
			String line = "";
			BufferedReader input = null;
			WritableWorkbook wbook = null;
			WritableSheet sheet = null;

			try {
				read = new InputStreamReader(new FileInputStream(file), "gbk");
				input = new BufferedReader(read);

				wbook = Workbook.createWorkbook(file2);// 根据路径生成excel文件
				int count = 1;
				if (file3.exists() && file.isFile()) {
					FileInputStream fis = new FileInputStream(file);
					Scanner scanner = new Scanner(fis);
					while (scanner.hasNextLine()) {
						scanner.nextLine();
						count++;
					}
				}else{
					System.out.println("文件不存在");
				}
				int k = count / 1048576;
				int j = k;
				if(k * 1048576 < count){
					j = k + 1;
				}
				for(int s = 0; s < j; s++){
					sheet = wbook.createSheet("sheet" + s, s);// 新标签页
				}
				int m = 0;// excel行数
				int n = 0;// excel列数
				Label t;
				while ((line = input.readLine()) != null) {

					// String[] words = line.split("[ \t]");//
					// 把读出来的这行根据空格或tab分割开
					String[] words = line.split("[|]");// 把读出来的这行根据空格或tab分割开

					for (int i = 0; i < words.length; i++) {
						if (!words[i].matches("\\s*")) { // 当不是空行时
							t = new Label(n, m, words[i].trim());
							sheet.addCell(t);
							n++;
						}
					}
					n = 0;// 回到列头部
					m++;// 向下移动一行
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			} finally {
				try {
					wbook.write();
					wbook.close();
					input.close();
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
			System.out.println("over!");
			System.exit(0);
		} else {
			System.out.println("file is not exists or not a file");
			System.exit(0);
		}
	}

	public static void unGzipFile(String sourcedir) {
		String ouputfile = "";
		try {
			// 建立gzip压缩文件输入流
			FileInputStream fin = new FileInputStream(sourcedir);
			// 建立gzip解压工作流
			GZIPInputStream gzin = new GZIPInputStream(fin);
			// 建立解压文件输出流
			ouputfile = sourcedir.substring(0, sourcedir.lastIndexOf('.'));
			ouputfile = ouputfile.substring(0, ouputfile.lastIndexOf('.'));
			FileOutputStream fout = new FileOutputStream(ouputfile);

			int num;
			byte[] buf = new byte[1024];

			while ((num = gzin.read(buf, 0, buf.length)) != -1) {
				fout.write(buf, 0, num);
			}

			gzin.close();
			fout.close();
			fin.close();
		} catch (Exception ex) {
			System.err.println(ex.toString());
		}
		return;
	}
}
