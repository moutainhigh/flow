package com.aspire.etl.flowengine;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskHistory;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowengine.taskcontext.CmdLineContext;
import com.aspire.etl.flowengine.taskcontext.JavaContext;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.Utils;

public class TaskContext implements Runnable {
	
	Logger log = null;
	FlowMetaData flowMetaData = null;
	Task task = null;
	Taskflow taskflow = null;
	
	public TaskContext(Taskflow taskflow, Task task) {
		this.log = Logger.getLogger(taskflow.getTaskflow());
		//this.log = Utils.initFileLog(taskflow.getTaskflow(), "/Users/chenhaitao/Desktop/log/" , taskflow.getTaskflow(), "INFO");
		this.task = task;
		this.taskflow = taskflow;
		try {
			flowMetaData = FlowMetaData.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		//�����̼߳����̳߳�ʱ������ִ�У�ֻ�б��̳߳�ѡ��ִ��ʱ���Ż�ִ�е����
		try {
		MDC.put("TASK", task.getTask());
		log.info("��ʼִ������ID=\"" + task.getTaskID() + "\",name=\""
				+ task.getTask() + "\"," + task.getDescription() + "\"");
		
		// ���������״̬�ȱ�ʾΪrunning�������С�		
		Long historyId = updateTaskToRunning();		

		// ��ʼ���ӿڲ�����
		TaskRunInterfaceInfo taskRunInfo = new TaskRunInterfaceInfo(flowMetaData, taskflow, task);		
		
		String execType = flowMetaData.queryTaskType(task.getTaskType())
				.getEnginePluginType();
		Boolean call = false;
		if (execType.equalsIgnoreCase("C++")) {
			// �����ⲿC++������ɣ���CmdLineContext��ִ��
			CmdLineContext taskContext = new CmdLineContext(taskRunInfo);
			taskContext.call();
		} else if (execType.equalsIgnoreCase("JAVA")) {
			// ������java��ʵ�֣�������������ͬ��java����������С�
			//JavaContext taskContext = new JavaContext(taskRunInfo);
			JavaContext taskContext = new JavaContext(taskRunInfo, historyId);
			call = taskContext.call();
		}else{
			log.error("����Ĳ������:"+execType);
			//������״̬��Ϊʧ��
			flowMetaData.updateTaskStatus(task, Task.FAILED);
			return;
		}
		if(call){
			log.info("���ִ������ID=\"" + task.getTaskID() + "\",name=\""
					+ task.getTask() + "\"," + task.getDescription() + "\"");
		}else{
			log.info("����ִ��ʧ�ܣ�ID=\"" + task.getTaskID() + "\",name=\""
					+ task.getTask() + "\"," + task.getDescription() + "\"");
			flowMetaData.updateTaskStatus(task, Task.FAILED);//���������Ϊʧ��
			return;
		}
		} catch (Exception e1) {
			log.error("ִ���������:" + e1);
			e1.printStackTrace();
		}
		//TODO:�������������״̬���� 

	}

	private Long updateTaskToRunning() throws MetaDataException {		
		log.debug("������\"" + task + "\"��״̬��ʾΪrunning");
		flowMetaData.updateTaskStatus(task, Task.RUNNING);
		flowMetaData.updateRunStartTimeOfTask(task.getTaskID(), new Date());
		
		//Long history = flowMetaData.saveHistory(task.getTaskID()+"", TaskHistory.RUNNING+"");
		
		return Long.valueOf(0L);
	}


}
