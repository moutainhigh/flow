package com.aspire.etl.flowdefine;

import java.io.Serializable;
import java.util.Date;

import com.aspire.etl.flowdefine.StepType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.tool.TimeUtils;

/**
 * @author luoqi 映射ETL元数据库里的rpt_taskflow表的一行数据
 * 20080430 wuzhuokun 修改新建流程的默认值为STOPPED状态,因为新建时没时间,如果运行会出现空指针异常.
 * 
 * @author jiangts 
 * @since 2011-01-01
 *  <p>
 *  为配合设计器的复制粘贴需要，修改 Taskflow implements Serializable
 */
public class Taskflow implements Serializable,Comparable{
	public static final int FAILED = -1;  

	public static final int READY = 0;

	public static final int RUNNING = 1;

	public static final int SUCCESSED = 2;
	
	public static final int QUEUE = 3;  //保留值
	
	public static final int STOPPED = 4;  //

	

	public static final int REDO_NO = 0;

	public static final int REDO_YES = 1;
	

	public static final int SUSPEND_NO = 0;

	public static final int SUSPEND_YES = 1;
	

	public static final int DEFAULT_THREAD_NUM = 1;

	public static final int DEFAULT_STEP_NUM = 1;

	Integer taskflowID;
	
	Integer groupID = new Integer(1);
	
	String taskflow = "";

	String stepType = StepType.DAY;

	int step = 1;

	Date statTime = null;

	int status = STOPPED;

	int suspend = SUSPEND_NO;

	String description = "";

	int redoFlag = REDO_NO;

	Date sceneStatTime = null;

	Date redoStartTime = null;

	Date redoEndTime = null;

	String fileLogLevel = "INFO";

	String dbLogLevel = "OFF";

	int threadnum = 1;

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

	public Taskflow() {

	}

	public Taskflow(Integer taskflowID, String taskflow, Date statTime) {
		this(taskflowID, taskflow, TimeUtils.DAY, DEFAULT_STEP_NUM, statTime, READY,
				SUSPEND_YES, taskflow, REDO_NO, statTime, null, null,
				DEFAULT_THREAD_NUM, new Integer(0));
	}

	public Taskflow(Integer taskflowID, String taskflow, Date statTime, String stepType, int step) {
		this(taskflowID, taskflow, stepType, step, statTime, READY, SUSPEND_YES, taskflow,
				REDO_NO, statTime, null, null, DEFAULT_THREAD_NUM, new Integer(0));
	}

	public Taskflow(Integer taskflowID, String taskflow, String stepType, Date statTime, int step,
			int status, int suspend, String description, int redoFlag) {
		this(taskflowID, taskflow, stepType, step, statTime, status, suspend, description,
				redoFlag, statTime, null, null, DEFAULT_THREAD_NUM, new Integer(0));
	}

	public Taskflow(Integer taskflowID, String taskflow, Date statTime, String stepType, int step,
			int redoFlag, Date sceneStatTime, Date redoStartTime,
			Date redoEndTime) {
		this(taskflowID, taskflow, stepType, step, statTime, READY, SUSPEND_YES, taskflow,
				redoFlag, sceneStatTime, redoStartTime, redoEndTime,
				DEFAULT_THREAD_NUM, new Integer(0));
	}
	
	public Taskflow(Integer taskflowID, String taskflow, String stepType, int step, Date statTime,
			int status, int suspend, String description, int redoFlag,
			Date sceneStatTime, Date redoStartTime, Date redoEndTime,
			int threadnum) {
		this(taskflowID, taskflow, stepType, step, statTime, status, suspend, description, redoFlag,
				sceneStatTime, redoStartTime, redoEndTime,
				threadnum, new Integer(0));
	}

	public Taskflow(Integer taskflowID, String taskflow, String stepType, int step, Date statTime,
			int status, int suspend, String description, int redoFlag,
			Date sceneStatTime, Date redoStartTime, Date redoEndTime,
			int threadnum, Integer groupID) {
		this.taskflowID = taskflowID;
		this.taskflow = taskflow;
		this.stepType = stepType;
		this.step = step;
		this.statTime = statTime;
		this.status = status;
		this.suspend = suspend;
		this.description = description;
		this.redoFlag = redoFlag;
		this.sceneStatTime = sceneStatTime;
		this.redoStartTime = redoStartTime;
		this.redoEndTime = redoEndTime;
		this.threadnum = threadnum;
		this.runStartTime = new Date();
		this.runEndTime = new Date();
		this.groupID = groupID;
	}
	
	public Taskflow(Integer taskflowID, String taskflow, String stepType, int step, Date statTime,
			int status, int suspend, String description, int redoFlag,
			Date sceneStatTime, Date redoStartTime, Date redoEndTime,
			int threadnum, Integer groupID, String memo) {
		this.taskflowID = taskflowID;
		this.taskflow = taskflow;
		this.stepType = stepType;
		this.step = step;
		this.statTime = statTime;
		this.status = status;
		this.suspend = suspend;
		this.description = description;
		this.redoFlag = redoFlag;
		this.sceneStatTime = sceneStatTime;
		this.redoStartTime = redoStartTime;
		this.redoEndTime = redoEndTime;
		this.threadnum = threadnum;
		this.runStartTime = new Date();
		this.runEndTime = new Date();
		this.groupID = groupID;
		this.memo = memo;
	}

	public String print() {
		String statusStr = "";
		switch (status) {
		case Taskflow.READY:
			statusStr="READY";
			break;
		case Taskflow.FAILED:
			statusStr="FAILED";
			break;
		case Taskflow.SUCCESSED:
			statusStr="SUCCESSED";
			break;
		case Taskflow.RUNNING:
			statusStr="RUNNING";
			break;
		case Taskflow.STOPPED:
			statusStr="STOPPED";
			break;
		case Taskflow.QUEUE:
			statusStr="QUEUE";
			break;
		default:
			statusStr=status+"";
			break;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\n----------------taskflow---------------\n");
		sb.append("|taskflow    = ").append(taskflow).append("\n");
		sb.append("|description = ").append(description).append("\n");
		sb.append("|stepType    = ").append(stepType).append("\n");
		sb.append("|step        = ").append(step).append("\n");
		sb.append("|statTime    = ").append(TimeUtils.toChar(statTime)).append(
				"\n");
		sb.append("|suspend     = ").append(suspend).append("\n");
		sb.append("|status      = ").append(statusStr).append("\n");
		sb.append("|--------------------------------------\n");
		sb.append("|redoFlag      = ").append(redoFlag).append("\n");
		sb.append("|sceneStatTime = ").append(
				(null == sceneStatTime) ? "null" : TimeUtils
						.toChar(sceneStatTime)).append("\n");
		sb.append("|redoStartTime = ").append(
				(null == redoStartTime) ? "null" : TimeUtils
						.toChar(redoStartTime)).append("\n");
		sb.append("|redoEndTime   = ").append(
				(null == redoEndTime) ? "null" : TimeUtils.toChar(redoEndTime))
				.append("\n");
		sb.append("---------------------------------------\n");

		return sb.toString();

	}

	@Override
	public String toString() {
		return "" + taskflow + " ["+description+"]";
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

	public Date getRedoEndTime() {
		return redoEndTime;
	}

	public void setRedoEndTime(Date redoEndTime) {
		this.redoEndTime = redoEndTime;
	}

	public int getRedoFlag() {
		return redoFlag;
	}

	public void setRedoFlag(int redoFlag) {
		this.redoFlag = redoFlag;
	}

	public Date getRedoStartTime() {
		return redoStartTime;
	}

	public void setRedoStartTime(Date redoStartTime) {
		this.redoStartTime = redoStartTime;
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

	public Date getSceneStatTime() {
		return sceneStatTime;
	}

	public void setSceneStatTime(Date sceneStatTime) {
		this.sceneStatTime = sceneStatTime;
	}

	public Date getStatTime() {
		return statTime;
	}

	public void setStatTime(Date statTime) {
		this.statTime = statTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStepType() {
		return stepType;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public int getSuspend() {
		return suspend;
	}

	public void setSuspend(int suspend) {
		this.suspend = suspend;
	}

	public String getTaskflow() {
		return taskflow;
	}

	public void setTaskflow(String taskflow) {
		this.taskflow = taskflow;
	}

	public int getThreadnum() {
		return threadnum;
	}

	public void setThreadnum(int threadnum) {
		this.threadnum = threadnum;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getDbLogLevel() {
		return dbLogLevel;
	}

	public void setDbLogLevel(String dbLogLevel) {
		this.dbLogLevel = dbLogLevel;
	}

	public String getFileLogLevel() {
		return fileLogLevel;
	}

	public void setFileLogLevel(String fileLogLevel) {
		this.fileLogLevel = fileLogLevel;
	}

	public Integer getTaskflowID() {
		return taskflowID;
	}

	public void setTaskflowID(Integer taskflowID) {
		this.taskflowID = taskflowID;
	}

	public Integer getGroupID() {
		return groupID;
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}
	
	public int compareTo(Object o) {
		Taskflow other = (Taskflow)o;
		if(taskflow.compareToIgnoreCase(other.taskflow) <= -1)
			return -1;
		else
			return 1;
	}
}
