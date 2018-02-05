package com.aspire.etl.tool;

import java.io.*;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtil {
	
	/**
	 * 数组生成excel
	 * @param fileName
	 * @param ss
	 * @return
	 * @throws Exception
	 */
	public static boolean createExcel(String fileName, String[][] ss) throws Exception{
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new Exception("创建文件" + fileName + "失败!" + e.getMessage());
		}
		WritableWorkbook wwb = Workbook.createWorkbook(file);
		WritableSheet sheet = wwb.createSheet("Sheet1", 0);
		int x = 0;
		int y = 0;
		for(String[] s : ss){
			for(String content : s){
				sheet.addCell(new Label(x, y, content));
				x++;
			}
			x = 0;
			y++;
		}
		wwb.write();
		wwb.close();
		return true;
	}
	
	/**
	 * csv转excel
	 * @param csvName
	 * @param excelName
	 * @return
	 * @throws Exception
	 */
	public static boolean csvToExcel(String csvName, String excelName) throws Exception{
		File csv = new File(csvName);
		if(!csv.exists()){
			throw new Exception("CSV文件" + csvName + "没找到!!!");
		}
		File excel = new File(excelName);
		try {
			excel.createNewFile();
		} catch (IOException e) {
			throw new Exception("创建文件" + excel + "失败!" + e.getMessage());
		}
		WritableWorkbook wwb = Workbook.createWorkbook(excel);
		WritableSheet sheet = wwb.createSheet("Sheet1", 0);
		int x = 0;
		int y = 0;
		BufferedReader br = new BufferedReader(new FileReader(csv));
		String line = null;
		String[] ss = null; 
		while((line = br.readLine()) != null){
			ss = line.split(",");
			for(String content : ss){
				sheet.addCell(new Label(x, y, content));
				x++;
			}
			x = 0;
			y++;
		}
		br.close();
		wwb.write();
		wwb.close();
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//			createExcel("F:/temp/cao/fuck.xls", new String[][]{{"a", "b", "c"}, {"11", "12", "13"}, {"121", "122", "123"}, {"131", "132", "133"}});
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			csvToExcel("F:/temp/cao/tmd.csv", "F:/temp/cao/tmdcao.xls");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
