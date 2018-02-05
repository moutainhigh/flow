package com.aspire.flow.test;

import com.aspire.flow.api.service.MasterSlaveJobSummaryService;
import com.aspire.flow.api.service.impl.MasterSlaveJobSummaryServiceImpl;
import com.aspire.flow.entity.MasterSlaveJobSummary;

public class MasterSlaveJobSummaryTest {
	public void update(){
		MasterSlaveJobSummary jobSummary = new MasterSlaveJobSummary();
		jobSummary.setFlowId("004flowid");
		jobSummary.setStatus("Startup");
		jobSummary.setTaskId("004taskid");
		jobSummary.setJobOperation("Start");
		jobSummary.setGroupName("004flowid");
		jobSummary.setJobName("004taskid");
		MasterSlaveJobSummaryService service = new MasterSlaveJobSummaryServiceImpl();
		service.saveJobSummary(jobSummary);
	}
	
	public static void main(String[] args) {
		MasterSlaveJobSummaryTest test = new MasterSlaveJobSummaryTest();
		test.update();
	}
}
