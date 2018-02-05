/*
 * Log.java
 *
 * Created on 2008-1-25 17:03:19 by luoqi
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
package com.aspire.etl.flowdefine;

import java.util.Date;

import com.aspire.etl.flowdefine.Taskflow;

/**
 * @author luoqi 映射ETL元数据库里的rpt_log表的一行数据
 */
public class Log {

	String taskflow;

	String task;

	int redoFlag;

	String logLevel;

	Date statTime;
	
	String description;

	Date occurTime;

	public Log(Taskflow taskflow, String description) {
		this(taskflow.getTaskflow(), null, taskflow.getRedoFlag(), "INFO",
				description, taskflow.getStatTime());

	}

	public Log(String taskflow, String task, int redoFlag, String logLevel,
			String description, Date statTime) {
		super();
		this.taskflow = taskflow;
		this.task = task;
		this.redoFlag = redoFlag;
		this.logLevel = logLevel;
		this.statTime = statTime;
		this.description = description;
		this.occurTime = new Date();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getRedoFlag() {
		return redoFlag;
	}

	public void setRedoFlag(int redoFlag) {
		this.redoFlag = redoFlag;
	}

	public Date getStatTime() {
		return statTime;
	}

	public void setStatTime(Date statTime) {
		this.statTime = statTime;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getTaskflow() {
		return taskflow;
	}

	public void setTaskflow(String taskflow) {
		this.taskflow = taskflow;
	}

}
