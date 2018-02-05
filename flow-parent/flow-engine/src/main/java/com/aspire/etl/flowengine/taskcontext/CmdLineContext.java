/*
 * CmdLineContext.java
 *
 * Created on 2008-2-21 22:05:59 by luoqi
 *
 * Copyright (c) 2001-2008 ASPire Technologies, Inc.
 * 6/F,IER BUILDING, SOUTH AREA,SHENZHEN HI-TECH INDUSTRIAL PARK Mail Box:11# 12#.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ASPire Technologies, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Aspire.
 */
package com.aspire.etl.flowengine.taskcontext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowengine.TaskRunInterfaceInfo;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;

/**
 * ִ�����������͵����������Ļ����������ڵ����ļ�ϵͳ�ϳ��������
 * 
 * @author ����
 * @since 2008-2-29
 * 
 */
public class CmdLineContext {
	String cmdLineExec = "";

	TaskRunInterfaceInfo taskRunInfo = null;

	Logger log;

	public CmdLineContext(TaskRunInterfaceInfo taskRunInfo) {
		super();
		this.cmdLineExec = taskRunInfo.getEnginePlugin();
		this.taskRunInfo = taskRunInfo;
		this.log = Logger.getLogger(taskRunInfo.getTaskflowName());
	}

	public Boolean call() {

		boolean isOK = false;

		int retValue = Task.FAILED;

		try {
			retValue = runNativeCommand(cmdLineExec, taskRunInfo
					.toCmdLineParamStr());
		} catch (IOException e) {
			e.printStackTrace();
			log.error("ִ��ETL������ʱ�����쳣��" + e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error("ִ��ETL������ʱ�����쳣��" + e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug(cmdLineExec + taskRunInfo.toCmdLineParamStr());
		log.debug("���򷵻�ֵ:" + retValue);

		
		// ��ִ�н�����������״̬		
		//��һ�������C++�汾��������������������Ľ��̱��˴Ӳ���ϵͳ��kill,������᷵��ʧ�ܡ�
		try {
			//Thread.sleep(10 * 1000); // for test

			FlowMetaData.getInstance().updateTaskStatus(
					taskRunInfo.getTaskID(), translateReturnValue(retValue));
			FlowMetaData.getInstance().updateRunEndTimeOfTask(taskRunInfo.getTaskID(), new Date());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isOK;
	}
	private int translateReturnValue(int retValue){
		int newStatus = Task.FAILED;

		/*
		 * ������ز���Լ���� 0:�������гɹ� 1:����������ȡ���ݿ�������Ϣ���� 2:����������,����ʧ�� 3:��������ʧ��
		 * �ο���
		 *  kill -9 xxx ���ַ���ֵΪ137
		 	kill xxx  �������ֵ143
		 	kill -11 xxx ����ֵΪ139
		 */
		switch (retValue) {
		case 0:
			newStatus = Task.SUCCESSED;
			break;
		case 1:
			newStatus = Task.FAILED;
			log.error("����������ȡ���ݿ�������Ϣ����");
			break;
		case 2:
			newStatus = Task.FAILED;
			log.error("����������,����ʧ��");
			break;
		case 3:
			newStatus = Task.FAILED;
			log.error("��������ʧ��");
			break;
		default:
			log.error("δ����ķ���ֵ��" + retValue+",����ԭ�������ݿ��쳣�ж�.");
			break;
		}
		return  newStatus;

	}
	/**
	 * ��java����������ļ�ϵͳ�ϵ�ĳ������
	 * 
	 * @param cmdStr
	 *            ������
	 * @param paramStr
	 *            �����ַ���
	 * @throws IOException
	 * @throws InterruptedException
	 */

	protected int runCmdLineExec(String cmdStr, String paramStr)
			throws IOException, InterruptedException {
		int ret = 0;
		Process p = null;
		File workDir = new File(cmdStr).getParentFile();

		try {
			p = Runtime.getRuntime().exec(cmdStr + paramStr, null, workDir);
			// �ȴ��ó����˳��������շ���ֵ
			ret = p.waitFor();
		} catch (InterruptedException e) {
			// �����׳����õ����߿�������������
			throw e;
		}finally{
			// ����ɱ�����̣���������ظ�ִ�С�
			p.destroy();
		}
		return ret;
	}

	private int runNativeCommand(String command, String parameters) throws Exception {
		int ret = 9;
		String[] cmd = null;
		String[] args = new String[2];
		File cmdFile = new File(command);
		File workDir = cmdFile.getParentFile();
		if(workDir==null){
			workDir = new File("./plugins");
		}
		args[0] = cmdFile.getName();
		args[1] = parameters;

		Process proc = null;
		
		try {
			// ȡ����ϵͳ����
			String osName = System.getProperty("os.name");

			// only will work with Windows NT
			if (osName.equals("Windows NT")) {
				if (cmd == null)
					cmd = new String[args.length + 2];
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";
				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			// only will work with Windows 95
			else if (osName.equals("Windows 95")) {
				if (cmd == null)
					cmd = new String[args.length + 2];
				cmd[0] = "command.com";
				cmd[1] = "/C";
				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			// only will work with Windows 2000
			else if (osName.equals("Windows 2000")) {
				if (cmd == null)
					cmd = new String[args.length + 2];
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";

				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			// only will work with Windows XP
			else if (osName.equals("Windows XP")) {
				
				if (cmd == null)
					cmd = new String[args.length + 2];
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";

				for (int i = 0; i < args.length; i++)
					cmd[i + 2] = args[i];
			}
			// only will work with Linux or unix
			else if (osName.equalsIgnoreCase("Linux") || osName.equalsIgnoreCase("Unix") || osName.equalsIgnoreCase("HP-UX")) {
				args[0] = command;
				args[1] = parameters;
				workDir = new File(".");
				
				if (cmd == null)
					cmd = new String[args.length];
				cmd = args;
			}
			// will work with the rest
			else {
				if (cmd == null)
					cmd = new String[args.length];
				cmd = args;
			}

			Runtime rt = Runtime.getRuntime();
			// Executes the command
			log.debug("���� :" + command + " " + parameters);
			log.debug("cmd:"+Arrays.toString(cmd));
			log.debug("workDir:"+workDir.toString());
			
			proc = rt.exec(cmd, null, workDir);			
			
			// any error message
			StreamGobbler errorGobbler = new StreamGobbler(log, proc
					.getErrorStream(), "ErrorStream");
			errorGobbler.start();
			errorGobbler.join();
                        
			// any output
			StreamGobbler outputGobbler = new StreamGobbler(log, proc
					.getInputStream(), "InputStream");
			outputGobbler.start();
			outputGobbler.join();

			ret = proc.waitFor();
			
		}catch (InterruptedException e) {
			
			// �����׳����õ����߿�������������
			throw e;
		} catch (Exception x) {
			System.out.println("error happened " + x);
			throw x;
		}finally{
			//����ɱ�����̣���������ظ�ִ�С�
			proc.destroy();
		}
		
		return ret;
	}

}

class StreamGobbler extends Thread {
	// This variable represents the message
	InputStream is;

	// This variable represents the type(ERROR and OUTPUT)
	String type;

	// Constructor
	Logger log = null;

	StreamGobbler(Logger log, InputStream is, String type) {
		this.log = log;
		this.is = is;
		this.type = type;
	}

	// This is the thread called when the process is ended
	public void run() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer outBuffer = new StringBuffer();
			while ((line = br.readLine()) != null){
				outBuffer.append(line);
			}
			log.debug(type + ">" + outBuffer.toString());
			
			
		} catch (IOException e) {
			log.error(e.toString());
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block				
			}
		}
	}
}