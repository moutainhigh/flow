package com.aspire.etl.flowmonitor.beans;

import java.io.Serializable;
import java.util.Date;

public class WarnBean implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String hostIp;
	private String source;
	private String appendId;
	private String description;
	private Date occurTime;
	private String currently;
	
	public String getAppendId() {
		return appendId;
	}
	public void setAppendId(String appendId) {
		this.appendId = appendId;
	}
	public String getCurrently() {
		return currently;
	}
	public void setCurrently(String currently) {
		this.currently = currently;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	public Date getOccurTime() {
		return occurTime;
	}
	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
}
