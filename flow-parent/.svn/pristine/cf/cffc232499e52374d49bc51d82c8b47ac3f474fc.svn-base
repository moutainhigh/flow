package com.aspire.etl.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.aspire.etl.flowmetadata.dao.FlowMetaData;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.aspire.etl.flowdefine.TaskHistory;

public class Excel {
	/** 定义Logger类对象 */
	Logger log = null;
	private FlowMetaData flowMetaData;
	private final static int TASK_FAILED = -1;
	private final static int TASK_SUCCESSED = 2;
	private String stra;

	public int execute(Map<String, Object> map) {

		/** 定义使用ftp传送文件状态标志位为-1(发送失败标志) */
		int isSuccess = TASK_FAILED;

		/** 定义taskFlow局部变量，获取当前运行插件对应的流程名 */
		String taskFlow = (String) map.get("TASKFLOW");

		/** 如果 taskFlow 变量不为空 TODO */
		if (taskFlow != null) {
			/** 根据流程名，初始化log变量 */
			log = Logger.getLogger(taskFlow);
		}

		log.debug("Ftp.execute()... map =" + map);

		try {
			flowMetaData = FlowMetaData.getInstance();
			log.info("开始从数据库中获取filename");
			List<TaskHistory> historyList = flowMetaData.getTaskHistoryByFlowIdAndStartTime(map);
			// 根据history成功任务条数,判断需不需要再次创建目录
			// int num = flowMetaData.getTaskHistoryNumByFlowId(map);
			stra = historyList.get(0).getFile();
			String filePath = (String) map.get("filePath");
			String flowId = (String) map.get("FLOW_ID");

			List<String> files = new ArrayList<>();
			if (historyList != null && historyList.size() > 0) {
				for (TaskHistory taskHistory : historyList) {
					log.info("taskflowID: " + taskHistory.getTaskflowID());
					log.info("file: " + taskHistory.getFile());
					log.info("id " + taskHistory.getId());
				}
			}
			for (TaskHistory taskHistory : historyList) {
				String file = taskHistory.getFile();
				if (StringUtils.isNotBlank(file)) {
					log.info("file: " + file);
					// String fileNew = file.substring(0, file.length() - 2);
					String newFile = flowId + "_" + file + "_0000.txt.gz";
					files.add(filePath + "/" + flowId + "/data/" + newFile);
				}
			}
			/* 解压文件 */
			unGzipFile(files);
			/* 将txt文件转换成excel文件 */
			txt2Excel(files);
			/** excel文件转换状态标志位置为2(上传成功) */
			isSuccess = TASK_SUCCESSED;
		} catch (Exception e) {
			if (log != null) {
				// e.printStackTrace();
				log.debug("catch (Exception e): ", e);
				log.info(" Excel.execute() Excel上传文件错误,请检查配置是否正确！  ");
				log.info(e);
			}
		}

		/** 返回发送邮件状态标志位 */
		return isSuccess;
	}

	/**
	 * 解压文件
	 * 
	 * @param sourceFiles
	 */
	public void unGzipFile(List<String> sourceFiles) {
		String ouputfile = "";
		if (sourceFiles != null && sourceFiles.size() > 0) {
			for (String sourceFile : sourceFiles) {
				try {
					// 建立gzip压缩文件输入流
					FileInputStream fin = new FileInputStream(sourceFile);
					// 建立gzip解压工作流
					GZIPInputStream gzin = new GZIPInputStream(fin);
					// 建立解压文件输出流
					ouputfile = sourceFile.substring(0, sourceFile.lastIndexOf('.'));
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
					log.info(ex.toString());
				}
			}
		}
	}

	/**
	 * 将txt转换成
	 * 
	 * @param sourceFiles
	 */
	public void txt2Excel(List<String> sourceFiles) {
		if (sourceFiles != null && sourceFiles.size() > 0) {
			for (String string : sourceFiles) {
				String sourceFile = StringUtils.substringBefore(string, ".");
				File file = new File(sourceFile);// 读取的txt文件
				File file2 = new File(sourceFile + ".xls");// 将生成的excel表格
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
						FileInputStream fis = new FileInputStream(file);
						Scanner scanner = new Scanner(fis);
						while (scanner.hasNextLine()) {
							scanner.nextLine();
							count++;
						}
						int k = count / 1048576;
						int j = k;
						if (k * 1048576 < count) {
							j = k + 1;
						}
						for (int s = 0; s < j; s++) {
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
					log.info("txt转换excel结束");
				} else {
					log.info("file is not exists or not a file");
				}
			}
		}
	}
}
