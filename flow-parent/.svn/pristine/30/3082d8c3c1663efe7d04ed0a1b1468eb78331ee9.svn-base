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
 * 执行命令行类型的任务上下文环境，适用于调用文件系统上程序的任务。
 * 
 * @author 罗奇
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
			log.error("执行ETL引擎插件时出现异常：" + e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error("执行ETL引擎插件时出现异常：" + e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug(cmdLineExec + taskRunInfo.toCmdLineParamStr());
		log.debug("程序返回值:" + retValue);

		
		// 用执行结果更新任务的状态		
		//有一种情况，C++版本的任务，如果该任务启动的进程被人从操作系统上kill,该任务会返回失败。
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
		 * 插件返回参数约定： 0:程序运行成功 1:传入参数或获取数据库连接信息出错 2:程序已运行,启动失败 3:程序运行失败
		 * 参考：
		 *  kill -9 xxx 发现返回值为137
		 	kill xxx  输出返回值143
		 	kill -11 xxx 返回值为139
		 */
		switch (retValue) {
		case 0:
			newStatus = Task.SUCCESSED;
			break;
		case 1:
			newStatus = Task.FAILED;
			log.error("传入参数或获取数据库连接信息出错");
			break;
		case 2:
			newStatus = Task.FAILED;
			log.error("程序已运行,启动失败");
			break;
		case 3:
			newStatus = Task.FAILED;
			log.error("程序运行失败");
			break;
		default:
			log.error("未定义的返回值：" + retValue+",可能原因是数据库异常中断.");
			break;
		}
		return  newStatus;

	}
	/**
	 * 从java虚拟机调用文件系统上的某个程序
	 * 
	 * @param cmdStr
	 *            程序名
	 * @param paramStr
	 *            参数字符串
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
			// 等待该程序退出，并接收返回值
			ret = p.waitFor();
		} catch (InterruptedException e) {
			// 重新抛出，让调用者可以做其他处理。
			throw e;
		}finally{
			// 必须杀掉进程，否则可能重复执行。
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
			// 取操作系统类型
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
			log.debug("运行 :" + command + " " + parameters);
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
			
			// 重新抛出，让调用者可以做其他处理。
			throw e;
		} catch (Exception x) {
			System.out.println("error happened " + x);
			throw x;
		}finally{
			//必须杀掉进程，否则可能重复执行。
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