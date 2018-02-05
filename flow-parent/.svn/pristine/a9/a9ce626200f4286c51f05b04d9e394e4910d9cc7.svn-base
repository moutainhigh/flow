/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aspire.flow.entity;

import java.util.Vector;

/**
 * 
 * @author chenhaitao
 *
 */
public class AbstractJobSummary {
	private String flowId;
	private String taskId;
	private String status;
	private String jobOperation;
	private String groupName;
	private String jobName;
	private Vector<Object> params;
	private String excuteStatus;
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isStart() {
		return jobOperation != null && jobOperation.equals("Start");
	}

	public boolean isStartup() {
		return status != null && status.equals("Startup");
	}

	public boolean isRestart() {
		return jobOperation != null && jobOperation.equals("Restart");
	}

	public boolean isPause() {
		return jobOperation != null && jobOperation.equals("Pause");
	}

	public String getJobOperation() {
		return jobOperation;
	}

	public void setJobOperation(String jobOperation) {
		this.jobOperation = jobOperation;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Vector<Object> getParams() {
		return params;
	}

	public void setParams(Vector<Object> params) {
		this.params = params;
	}

	public String getExcuteStatus() {
		return excuteStatus;
	}

	public void setExcuteStatus(String excuteStatus) {
		this.excuteStatus = excuteStatus;
	}

	
}
