 package com.aspire.flow.cluster.startup;

import java.util.Date;
import java.util.Vector;

import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskAttribute;
import com.aspire.etl.flowdefine.Taskflow;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class IF1Submit {
	

	public Vector<Object> populateParam() {
		Vector<Object> params = new Vector<Object>();
		
		Taskflow flow = new Taskflow();
		flow.setTaskflowID(12);
		flow.setStatTime(new Date()); //
		flow.setSceneStatTime(null);
		flow.setStatus(Taskflow.READY);
		flow.setSuspend(0); //
		flow.setRedoFlag(0);
		flow.setRedoStartTime(null);
		flow.setRedoEndTime(null);
		flow.setFileLogLevel("DEBUG");
		flow.setDbLogLevel("OFF");
		flow.setThreadnum(1);
		flow.setRunStartTime(null);
		flow.setRunEndTime(null);
		
		flow.setTaskflow("12");
		flow.setGroupID(1);
		flow.setStepType("MI"); //MI分钟，D天
		flow.setStep(2); //2分钟
		flow.setDescription("DESC: Remote_Task");
		flow.setMemo("Meno: Remote_Task");
		
		params.add(JSONObject.fromObject(flow).toString());
		
		Task task1 = null;
		TaskAttribute attribute11 = null;
		TaskAttribute attribute12 = null;
		{
		    Task task = new Task();
		    task.setTaskID(21); //ID
		    task.setTaskflowID(flow.getTaskflowID());
		    task.setStatus(Task.READY);
		    task.setSuspend(0);
		    task.setRunStartTime(null);
		    task.setRunEndTime(null); //
		    /*task.setTask("Remote_Sell_提数"); //任务名
		    task.setTaskType("Shell");*/
		    task.setTask("Remote_FTP_上传文件"); //任务名
		    task.setTaskType("Ftp");
		    task.setPlantime(0); //预计任务完成时间(分钟数),0-不判断/N-判断超时'
		    task.setIsRoot(1); //是否为ETL流程起点(0不是,1是)
		    task.setAlertID(null);
		    task.setPerformanceID(null);
		    task.setXPos(0);
		    task.setYPos(0);
		    task.setDescription("DESC:Remote_FTP_上传文件");
		    task.setMemo("Memo:Remote_FTP_上传文件");
		    task1 = task;
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(90);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("Shell_WorkDir");
		    		attribute.setValue("/bin");
		    		attribute11 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(91);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("Shell_Name");
		    		attribute.setValue("pwd");
		    		attribute12 = attribute;
		    }
		}

		Task task2 = null;
		TaskAttribute attribute21 = null;
		TaskAttribute attribute22 = null;
		TaskAttribute attribute23 = null;
		TaskAttribute attribute24 = null;
		TaskAttribute attribute25 = null;
		TaskAttribute attribute26 = null;
		TaskAttribute attribute27 = null;
		TaskAttribute attribute28 = null;
		TaskAttribute attribute29 = null;
		{
		    Task task = new Task();
		    task.setTaskID(22); //ID
		    task.setTaskflowID(flow.getTaskflowID());
		    task.setStatus(Task.READY);
		    task.setSuspend(0);
		    task.setRunStartTime(null);
		    task.setRunEndTime(null); //
		    task.setTask("Remote_邮件通知"); //任务名
		    task.setTaskType("EMAIL");
		    task.setPlantime(0); //预计任务完成时间(分钟数),0-不判断/N-判断超时'
		    task.setIsRoot(0); //是否为ETL流程起点(0不是,1是)
		    task.setAlertID(null);
		    task.setPerformanceID(null);
		    task.setXPos(0);
		    task.setYPos(0);
		    task.setDescription("DESC:Remote_邮件通知");
		    task.setMemo("Memo:Remote_邮件通知");
		    task2 = task;

		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(120);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("SMTP_SERVER");
		    		attribute.setValue("smtp.qq.com");
		    		attribute27 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(121);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("USER");
		    		attribute.setValue("3116760687@qq.com");
		    		attribute21 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(122);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("PASSWORD");
		    		attribute.setValue("lxcqbmpnxvmideif");
		    		attribute22 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(123);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("FROM");
		    		attribute.setValue("3116760687@qq.com");
		    		attribute23 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(124);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("TO");
		    		attribute.setValue("3116760687@qq.com");
		    		attribute24 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(125);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("SUBJECT");
		    		attribute.setValue("remote:"+new Date());
		    		attribute25 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(126);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("CONTENT");
		    		attribute.setValue("CONTENT-1");
		    		attribute26 = attribute;
		    }
		}
		
		params.add(JSONArray.fromObject(new Task[] {task1, task2}).toString());
		
		TaskAttribute[] taskAttributes = new TaskAttribute[] {
				attribute11, attribute12, attribute21, attribute22, attribute23, attribute24, attribute25
				, attribute26, attribute27};
		params.add(JSONArray.fromObject(taskAttributes).toString());
		
		Link link = new Link();
		link.setLinkID(57);
		link.setFromTaskID(task1.getTaskID());
		link.setToTaskID(task2.getTaskID());
		params.add(JSONArray.fromObject(new Link[] {link}).toString());
		
		return params;
	}


}