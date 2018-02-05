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
		//任务线程加入线程池时，不会执行，只有被线程池选中执行时，才会执行到这里。
		try {
		MDC.put("TASK", task.getTask());
		log.info("开始执行任务：ID=\"" + task.getTaskID() + "\",name=\""
				+ task.getTask() + "\"," + task.getDescription() + "\"");
		
		// 将该任务的状态先标示为running，再运行。		
		Long historyId = updateTaskToRunning();		

		// 初始化接口参数串
		TaskRunInterfaceInfo taskRunInfo = new TaskRunInterfaceInfo(flowMetaData, taskflow, task);		
		
		String execType = flowMetaData.queryTaskType(task.getTaskType())
				.getEnginePluginType();
		Boolean call = false;
		if (execType.equalsIgnoreCase("C++")) {
			// 任务靠外部C++程序完成，用CmdLineContext来执行
			CmdLineContext taskContext = new CmdLineContext(taskRunInfo);
			taskContext.call();
		} else if (execType.equalsIgnoreCase("JAVA")) {
			// 任务用java类实现，在流程引擎相同的java虚拟机内运行。
			//JavaContext taskContext = new JavaContext(taskRunInfo);
			JavaContext taskContext = new JavaContext(taskRunInfo, historyId);
			call = taskContext.call();
		}else{
			log.error("错误的插件类型:"+execType);
			//将任务状态改为失败
			flowMetaData.updateTaskStatus(task, Task.FAILED);
			return;
		}
		if(call){
			log.info("完成执行任务：ID=\"" + task.getTaskID() + "\",name=\""
					+ task.getTask() + "\"," + task.getDescription() + "\"");
		}else{
			log.info("任务执行失败：ID=\"" + task.getTaskID() + "\",name=\""
					+ task.getTask() + "\"," + task.getDescription() + "\"");
			flowMetaData.updateTaskStatus(task, Task.FAILED);//将任务更新为失败
			return;
		}
		} catch (Exception e1) {
			log.error("执行任务出错:" + e1);
			e1.printStackTrace();
		}
		//TODO:在这里更新任务状态更好 

	}

	private Long updateTaskToRunning() throws MetaDataException {		
		log.debug("将任务\"" + task + "\"的状态标示为running");
		flowMetaData.updateTaskStatus(task, Task.RUNNING);
		flowMetaData.updateRunStartTimeOfTask(task.getTaskID(), new Date());
		
		//Long history = flowMetaData.saveHistory(task.getTaskID()+"", TaskHistory.RUNNING+"");
		
		return Long.valueOf(0L);
	}


}
