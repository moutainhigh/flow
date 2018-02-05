package com.aspire.etl.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

public class Shell {
	Logger log = null;
	
	private final static int TASK_FAILED = -1;
	private final static int TASK_SUCCESSED = 2;

	public int execute(Map<String, String> map) throws InterruptedException {
		int retValue = -1;

		//ȡ��ǰ���̵�logger
		log = Logger.getLogger((String) map.get("TASKFLOW"));
		String osName = System.getProperty("os.name");
		Runtime rt = Runtime.getRuntime();
		// ɾ��Ŀ���ļ�
		Process p = null;
		//String output = null;
		//BufferedReader in = null;
		String srcTaskId = (String)map.get("Source_TaskId");
		String workDir = (String)map.get("Shell_WorkDir");
		String shell = (String)map.get("Shell_Name");
		
		String statTime = (String)map.get("STATTIME");	
		// add by wuyaxue 20170829  ���ӽ���ʱ��
		String endTime = (String)map.get("ENDTIME");	
		
		String taskTitle = (String)map.get("TITLE");	//���ֶ�
		String title = "NULL";
		if(taskTitle != null && !"".equals(taskTitle.trim())) title = taskTitle;
		
		String stepType = (String)map.get("STEP_TYPE");//�������������������

		log.info("STEP TYPE is =====> [ " + stepType + " ]" );
		
		if("D".equals(stepType) || "M".equals(stepType) || "W".equals(stepType)) {
			statTime = statTime.substring(0, 8);
			endTime = endTime.substring(0, 8);
		}
		else if("H".equals(stepType)) {
			statTime = statTime.substring(0, 10); 
			endTime = endTime.substring(0, 10);
			}
		else if("MI".equals(stepType)){ 
			statTime = statTime.substring(0, 12);
			endTime = endTime.substring(0, 12);
			}
		log.info("statTime is =====>" + statTime +"endTime is ====>"+endTime+ " stepType ==>" + stepType + " title ==>" + title);
		
		String adHoc = (String)map.get("AD_HOC");

		String[] command = null;
		//String lastOut = "";
		StreamGobbler errorGobbler = null;
		StreamGobbler outputGobbler = null;
		try {
			
			log.debug("Shell_WorkDir:" + workDir);
			
			if (osName.indexOf("Windows") > -1) {//windowsϵͳ
				
				//command = "CMD.EXE /k cd " + workDir + "& " + shell;
				command = new String[]{"CMD.EXE", "/C", shell};
				log.debug("command:" + Arrays.toString(command));
				p = rt.exec(command, null, new File(workDir));

			}else if(osName.equalsIgnoreCase("HP-UX") || osName.toUpperCase().indexOf("UNIX") > -1){//HP-UX
				
				shell = "sh " + workDir + "/" + shell;
				log.debug("command:" + shell);
				p = rt.exec(shell);
				
			}else{//linux����ϵͳ
				/*if("AD-HOC".equals(adHoc))
				shell = shell.concat(" ").concat(srcTaskId).concat(" ").concat(title);
				else*/ 
				
				/**
				 * Edit by wuyaxue 20170829  
				 *  ����Shell����Ĳ���˳��TaskID, STAT_TIME, END_TIME, TITLE��
				 * */
				shell = shell.concat(" ").concat(srcTaskId).concat(" ").concat(statTime).concat(" ").concat(endTime).concat(" ").concat(title);
				
				log.info(" === Shell: " + shell + " ==== ");
				
				command = shell.split(" ");
				command[0] = workDir + "/" + command[0];
				
				//command = new String[]{workDir + "/" + shell};
				log.debug("shell command:" + Arrays.toString(command));
				p = rt.exec(command);
				
			}

			//any error message
			errorGobbler = new StreamGobbler(log, p
					.getErrorStream(), "ErrorStream");
			errorGobbler.start();
			errorGobbler.join();

			// any output
			outputGobbler = new StreamGobbler(log, p
					.getInputStream(), "InputStream");
			outputGobbler.start();
			outputGobbler.join();
			
			retValue =  p.waitFor();

		}catch (InterruptedException e) {

			// �����׳����õ����߿�������������
			log.error("=== Shell InterruptedException:  ", e);
			throw e;
		} catch (Exception x) {
			log.error("=== Shell Exception:  ", x);
			x.printStackTrace();
		}finally{
			//����ɱ�����̣���������ظ�ִ�С�
			p.destroy();
		}

		if(outputGobbler.runResult.equals("0")){
			retValue = TASK_SUCCESSED;
		} else {
			retValue = TASK_FAILED;
		}
		
		return retValue;
	}
	/**
	 * ��������shellִ�� Title: execute1 Description: ֧��linux unix
	 * 
	 * @param map
	 * @return
	 * @throws InterruptedException
	 */
	public int executeShell( Map<String, String> map ) throws InterruptedException {
		System.out.println( "executeShell---------------->>" );
		int retValue = -1;
		Process p = null;
		Runtime rt = Runtime.getRuntime();
		// String shellCommand = "sh " + shellWorkdir + " " + map.get(
		// "tableName" ) + " " + map.get( "fileName" ) + " "
		// + map.get( "filedList" );
		String shellCommand = map.get( "command" );
		System.out.println( "shellCommand : " + shellCommand );
		try {
			p = rt.exec( shellCommand );
			retValue = p.waitFor();
		} catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println( e.getMessage() );
		} finally {
			// ����ɱ�����̣���������ظ�ִ�С�
			p.destroy();
		}
		System.out.println( "shell retValue 0:�ɹ�  ����ʧ�� " + retValue );
		return retValue;

	}
}


class StreamGobbler extends Thread {
	// This variable represents the message
	InputStream is;

	// This variable represents the type(ERROR and OUTPUT)
	String type;
	
	String runResult ;  //�˶����

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
				runResult = line; //�õ����һ�����ֵ��shell�ű������һ��Ҫecho 0
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