package com.aspire.etl.flowmonitor.beans;

import java.util.Date;

public class FlowBean implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object  taskFlow;  
	private Object task;
	private String redo_flag;
	private String logLevel;
	private String descpription;
	private Date startTime;
	private Date endTime;
	private Date occurTime;
	
	public String getDescpription() {
		return descpription;
	}
	public void setDescpription(String descpription) {
		this.descpription = descpription;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	public Date getOccurTime() {
		return occurTime;
	}
	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}
	public String getRedo_flag() {
		return redo_flag;
	}
	public void setRedo_flag(String redo_flag) {
		this.redo_flag = redo_flag;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Object getTask() {
		return task;
	}
	public void setTask(Object task) {
		this.task = task;
	}
	public Object getTaskFlow() {
		return taskFlow;
	}
	public void setTaskFlow(Object taskFlow) {
		this.taskFlow = taskFlow;
	}            	
}
