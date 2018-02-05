package com.aspire.etl.flowdefine;

import java.io.Serializable;
import java.util.Date;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.tool.Utils;

/**
 * @author luoqi 映射ETL元数据库里的rpt_task表的一行数据
 *  luoqi 增加QUEUE，STOPPED两个状态  
 * 
 * @author jiangts 
 * @since 2011-01-01
 *  <p>
 *  为配合设计器的复制粘贴需要，新增了以下三个属性：
 *	private java.util.ArrayList<TaskAttribute> taskAttrList;
 *	private Taskflow taskflow;
 *	private Taskflow memberTaskflow;
 */
public class Task implements Serializable, Cloneable {
	
	public static final int FAILED = -1;
	
	public static final int READY = 0;

	public static final int RUNNING = 1;

	public static final int SUCCESSED = 2;

	public static final int QUEUE = 3;  //任务线程已被放入线程池排队
	
	public static final int STOPPED = 4;  //新建的任务
	
	
	public static final int SUSPEND_NO = 0;

	public static final int SUSPEND_YES = 1;
	

	public static final int ROOT_STEP = 1;

	public static final int NORMAL_STEP = 0;
	

	public static final int DEFAULT_PLAN_TIME = 5;
	

	public static final int DEFAULT_XPOS = 300;

	public static final int DEFAULT_YPOS = 300;

	public static String LOGDIR = "";
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	Integer taskID;

	Integer taskflowID;

	String task = "";

	String taskType = TaskType.NULL;

	int status = STOPPED;

	int plantime = 5;

	int isRoot = ROOT_STEP;

	int suspend = SUSPEND_NO;

	String description = "";

	String alertID = "";

	String performanceID = "";

	int xPos = DEFAULT_XPOS;

	int yPos = DEFAULT_YPOS;

	Date runStartTime = null;

	Date runEndTime = null;
	
	String memo = "";

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		if(memo == null){
			this.memo = "";
		}else{
			this.memo = memo;
		}
	}
	
	//参数列表，只在复制时使用
	private java.util.ArrayList<TaskAttribute> taskAttrList;
	
	//任务所在的流程，只在复制时使用
	private Taskflow taskflow;
	
	//大纲任务所指向的流程，只在复制时使用
	private Taskflow memberTaskflow;

	public Task() {
	}

	public Task(Integer taskID, Integer taskflowID, String task, String taskType, int xPos,
			int yPos) {
		this(taskID, taskflowID, task, taskType, STOPPED, DEFAULT_PLAN_TIME, ROOT_STEP,
				SUSPEND_NO, taskflowID + "." + task, task, task, xPos, yPos);
	}

	public Task(Integer taskID, Integer taskflowID, String task, String taskType) {
		this(taskID, taskflowID, task, taskType, STOPPED, DEFAULT_PLAN_TIME, ROOT_STEP,
				SUSPEND_NO);
	}

	public Task(Integer taskID, Integer taskflowID, String task, String taskType, int status) {
		this(taskID, taskflowID, task, taskType, status, DEFAULT_PLAN_TIME, ROOT_STEP,
				SUSPEND_NO);
	}

	public Task(Integer taskID, Integer taskflowID, String task, String taskType, int status,
			int plantime, int isRoot, int suspended) {
		this(taskID, taskflowID, task, taskType, status, plantime, isRoot, suspended,
				taskflowID + "." + task, task, task, DEFAULT_XPOS, DEFAULT_YPOS);

	}

	public Task(Integer taskID, Integer taskflowID, String task, String taskType, int status,
			int plantime, int isRoot, int suspended, String description,
			String performanceID, String alertID) {
		this(taskID, taskflowID, task, taskType, status, plantime, isRoot, suspended,
				description, performanceID, alertID, DEFAULT_XPOS, DEFAULT_YPOS);
	}

	public Task(Integer taskID, Integer taskflowID, String task, String taskType, int status,
			int plantime, int isRoot, int suspended, String description,
			String performanceID, String alertID, int xPos, int yPos) {
		this.taskID = taskID;
		this.taskflowID = taskflowID;
		this.task = task;
		this.taskType = taskType;
		this.status = status;
		this.plantime = plantime;
		this.isRoot = isRoot;
		this.suspend = suspended;
		this.description = description;
		this.alertID = alertID;
		this.performanceID = performanceID;
		this.xPos = xPos;
		this.yPos = yPos;
		this.runStartTime = null;
		this.runEndTime = null;

	}
	
	public Task(Integer taskID, Integer taskflowID, String task, String taskType, int status,
			int plantime, int isRoot, int suspended, String description,
			String performanceID, String alertID, int xPos, int yPos, String memo) {
		this.taskID = taskID;
		this.taskflowID = taskflowID;
		this.task = task;
		this.taskType = taskType;
		this.status = status;
		this.plantime = plantime;
		this.isRoot = isRoot;
		this.suspend = suspended;
		this.description = description;
		this.alertID = alertID;
		this.performanceID = performanceID;
		this.xPos = xPos;
		this.yPos = yPos;
		this.runStartTime = null;
		this.runEndTime = null;
		this.memo = memo;
	}

	public String makeKey(String taskflow, String task) {
		return "[" + taskflow + "].[" + task + "]";
	}

	public String print() {
		String statusStr = "";
		switch (status) {
		case Task.READY:
			statusStr="READY";
			break;
		case Task.FAILED:
			statusStr="FAILED";
			break;
		case Task.SUCCESSED:
			statusStr="SUCCESSED";
			break;
		case Task.RUNNING:
			statusStr="RUNNING";
			break;
		case Task.STOPPED:
			statusStr="STOPPED";
			break;
		case Task.QUEUE:
			statusStr="QUEUE";
			break;
		default:
			statusStr=status+"";
			break;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\n----------------task---------------\n");
		sb.append("|taskID      = ").append(taskID).append("\n");
		sb.append("|task        = ").append(task).append("\n");
		sb.append("|description = ").append(description).append("\n");
		sb.append("|taskflowID  = ").append(taskflowID).append("\n");
		sb.append("|taskType    = ").append(taskType).append("\n");
		sb.append("|suspend     = ").append(suspend).append("\n");
		sb.append("|isRoot      = ").append(isRoot).append("\n");
		sb.append("|status      = ").append(statusStr).append("\n");
		sb.append("-----------------------------------\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.description;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if(description == null){
			this.description = "";
		}else{
			this.description = description;
		}
	}

	public int getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(int isRoot) {
		this.isRoot = isRoot;
	}

	public String getPerformanceID() {
		return performanceID;
	}

	public void setPerformanceID(String performanceID) {
		this.performanceID = performanceID;
	}

	public int getPlantime() {
		return plantime;
	}

	public void setPlantime(int plantime) {
		this.plantime = plantime;
	}

	public Date getRunEndTime() {
		return runEndTime;
	}

	public void setRunEndTime(Date runEndTime) {
		this.runEndTime = runEndTime;
	}

	public Date getRunStartTime() {
		return runStartTime;
	}

	public void setRunStartTime(Date runStartTime) {
		this.runStartTime = runStartTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSuspend() {
		return suspend;
	}

	public void setSuspend(int suspend) {
		this.suspend = suspend;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public Integer getTaskflowID() {
		return taskflowID;
	}

	public void setTaskflowID(Integer taskflowID) {
		this.taskflowID = taskflowID;
	}

	public Integer getTaskID() {
		return taskID;
	}

	public void setTaskID(Integer taskID) {
		this.taskID = taskID;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public int getXPos() {
		return xPos;
	}

	public void setXPos(int pos) {
		xPos = pos;
	}

	public int getYPos() {
		return yPos;
	}

	public void setYPos(int pos) {
		yPos = pos;
	}
	
	public Object clone() {   
        Task o = null;   
        try {   
            o = (Task) super.clone();
            o.setTaskID(Utils.getRandomIntValue());
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
        return o;   
    }

	public java.util.ArrayList<TaskAttribute> getTaskAttrList() {
		return taskAttrList;
	}

	public void setTaskAttrList(java.util.ArrayList<TaskAttribute> taskAttrList) {
		this.taskAttrList = taskAttrList;
	}

	public Taskflow getTaskflow() {
		return taskflow;
	}

	public void setTaskflow(Taskflow taskflow) {
		this.taskflow = taskflow;
	}

	public Taskflow getMemberTaskflow() {
		return memberTaskflow;
	}

	public void setMemberTaskflow(Taskflow memberTaskflow) {
		this.memberTaskflow = memberTaskflow;
	}
}
