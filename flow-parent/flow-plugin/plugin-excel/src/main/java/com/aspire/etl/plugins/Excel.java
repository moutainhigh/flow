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
	/** ����Logger����� */
	Logger log = null;
	private FlowMetaData flowMetaData;
	private final static int TASK_FAILED = -1;
	private final static int TASK_SUCCESSED = 2;
	private String stra;

	public int execute(Map<String, Object> map) {

		/** ����ʹ��ftp�����ļ�״̬��־λΪ-1(����ʧ�ܱ�־) */
		int isSuccess = TASK_FAILED;

		/** ����taskFlow�ֲ���������ȡ��ǰ���в����Ӧ�������� */
		String taskFlow = (String) map.get("TASKFLOW");

		/** ��� taskFlow ������Ϊ�� TODO */
		if (taskFlow != null) {
			/** ��������������ʼ��log���� */
			log = Logger.getLogger(taskFlow);
		}

		log.debug("Ftp.execute()... map =" + map);

		try {
			flowMetaData = FlowMetaData.getInstance();
			log.info("��ʼ�����ݿ��л�ȡfilename");
			List<TaskHistory> historyList = flowMetaData.getTaskHistoryByFlowIdAndStartTime(map);
			// ����history�ɹ���������,�ж��費��Ҫ�ٴδ���Ŀ¼
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
			/* ��ѹ�ļ� */
			unGzipFile(files);
			/* ��txt�ļ�ת����excel�ļ� */
			txt2Excel(files);
			/** excel�ļ�ת��״̬��־λ��Ϊ2(�ϴ��ɹ�) */
			isSuccess = TASK_SUCCESSED;
		} catch (Exception e) {
			if (log != null) {
				// e.printStackTrace();
				log.debug("catch (Exception e): ", e);
				log.info(" Excel.execute() Excel�ϴ��ļ�����,���������Ƿ���ȷ��  ");
				log.info(e);
			}
		}

		/** ���ط����ʼ�״̬��־λ */
		return isSuccess;
	}

	/**
	 * ��ѹ�ļ�
	 * 
	 * @param sourceFiles
	 */
	public void unGzipFile(List<String> sourceFiles) {
		String ouputfile = "";
		if (sourceFiles != null && sourceFiles.size() > 0) {
			for (String sourceFile : sourceFiles) {
				try {
					// ����gzipѹ���ļ�������
					FileInputStream fin = new FileInputStream(sourceFile);
					// ����gzip��ѹ������
					GZIPInputStream gzin = new GZIPInputStream(fin);
					// ������ѹ�ļ������
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
	 * ��txtת����
	 * 
	 * @param sourceFiles
	 */
	public void txt2Excel(List<String> sourceFiles) {
		if (sourceFiles != null && sourceFiles.size() > 0) {
			for (String string : sourceFiles) {
				String sourceFile = StringUtils.substringBefore(string, ".");
				File file = new File(sourceFile);// ��ȡ��txt�ļ�
				File file2 = new File(sourceFile + ".xls");// �����ɵ�excel���
				if (file.exists() && file.isFile()) {

					InputStreamReader read = null;
					String line = "";
					BufferedReader input = null;
					WritableWorkbook wbook = null;
					WritableSheet sheet = null;

					try {
						read = new InputStreamReader(new FileInputStream(file), "gbk");
						input = new BufferedReader(read);

						wbook = Workbook.createWorkbook(file2);// ����·������excel�ļ�
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
							sheet = wbook.createSheet("sheet" + s, s);// �±�ǩҳ
						}
						int m = 0;// excel����
						int n = 0;// excel����
						Label t;
						while ((line = input.readLine()) != null) {

							// String[] words = line.split("[ \t]");//
							// �Ѷ����������и��ݿո��tab�ָ
							String[] words = line.split("[|]");// �Ѷ����������и��ݿո��tab�ָ

							for (int i = 0; i < words.length; i++) {
								if (!words[i].matches("\\s*")) { // �����ǿ���ʱ
									t = new Label(n, m, words[i].trim());
									sheet.addCell(t);
									n++;
								}
							}
							n = 0;// �ص���ͷ��
							m++;// �����ƶ�һ��
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
					log.info("txtת��excel����");
				} else {
					log.info("file is not exists or not a file");
				}
			}
		}
	}
}
