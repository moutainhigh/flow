package com.aspire.etl.flowdefine;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import com.aspire.etl.flowdefine.TaskHistory;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.tool.Utils;


public class TaskHistory implements Serializable, Cloneable {
	
	public static final String FAILED = "F";

	public static final String RUNNING = "R";

	public static final String COMPLETE = "C";
	
	private static final long serialVersionUID = 2L;

	private Long id;
	private Integer taskID;
	private Integer taskflowID;
	private String task = "";
	private String taskType = TaskType.NULL;

	private String status = null;
	private Date startTime = null;
	private Date endTime = null;
	private String file = null;

	private Date createTime = null;
	private String createBy = null;
	private String description = "";
	
	// add by wuyaxue 20170829  增加周期的信息
	private String statTime = null;
	private String endStatTime = null;
	private String nextStatTime = null;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	//19 digital,  e.g. 1492768219717036672
	public void setRandomId() {
		this.id = System.currentTimeMillis()*1000000 
				+ new Random().nextInt(1000000);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}


	private String memo = "";
	

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
	public TaskHistory() {
	}

	public String print() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n----------------task---------------\n");
		sb.append("|taskID       = ").append(taskID).append("\n");
		sb.append("|task         = ").append(task).append("\n");
		sb.append("|description  = ").append(description).append("\n");
		sb.append("|taskflowID   = ").append(taskflowID).append("\n");
		sb.append("|taskType     = ").append(taskType).append("\n");
		sb.append("|status       = ").append(status).append("\n");
		sb.append("|statTime     = ").append(statTime).append("\n");
		sb.append("|endStatTime  = ").append(endStatTime).append("\n");
		sb.append("|nextStatTime = ").append(nextStatTime).append("\n");
		sb.append("-----------------------------------\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		return "[id:" + this.id + ", flowid:" + this.taskflowID 
				+ ", status:" + this.status + ", start:" + this.startTime 
				+ ", end:" + this.getEndTime() +  "]";
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	// add by wuyaxue 20170829 
	public String getStatTime() {
		return statTime;
	}

	public void setStatTime(String statTime) {
		this.statTime = statTime;
	}

	public String getEndStatTime() {
		return endStatTime;
	}

	public void setEndStatTime(String endStatTime) {
		this.endStatTime = endStatTime;
	}

	public String getNextStatTime() {
		return nextStatTime;
	}

	public void setNextStatTime(String nextStatTime) {
		this.nextStatTime = nextStatTime;
	}
    // end 
	
	public Object clone() {   
        TaskHistory o = null;   
        try {   
            o = (TaskHistory) super.clone();
            o.setTaskID(Utils.getRandomIntValue());
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
        return o;   
    }

}
