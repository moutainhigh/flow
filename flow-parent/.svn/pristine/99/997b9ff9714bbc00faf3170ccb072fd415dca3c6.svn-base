package com.aspire.flow.cluster.startup;

import java.util.Vector;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.aspire.flow.api.MasterSlaveApiFactory;
import com.aspire.flow.api.data.MasterSlaveJobData;
import com.aspire.flow.api.impl.MasterSlaveApiFactoryImpl;
import com.aspire.flow.entity.MasterSlaveJob;
import com.aspire.flow.entity.MasterSlaveJobSummary;
import com.aspire.flow.helper.ReflectHelper;

public class SendJob {
	private CuratorFramework client;
	private static final int DEFAULT_SESSION_TIMEOUT_MS = Integer.getInteger("curator-default-session-timeout", 60 * 1000);

    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = Integer.getInteger("curator-default-connection-timeout", 15 * 1000);

    private static final RetryPolicy DEFAULT_RETRY_POLICY = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);
    
    public SendJob(CuratorFramework client){
    	this.client = client;
    }
	public static void main(String[] args) {
		MasterSlaveJobSummary jobSummary = new MasterSlaveJobSummary();
		IF1Submit if1Submit = new IF1Submit();
		Vector<Object> populateParam = if1Submit.populateParam();
		jobSummary.setFlowId("12");
		/*jobSummary.setStatus("Startup");
		jobSummary.setTaskId("004taskid");*/
		jobSummary.setJobOperation("Pause");
		jobSummary.setParams(populateParam);
		//jobSummary.setJobOperation("Pause");
		jobSummary.setGroupName("12");
		jobSummary.setExcuteStatus("Pause");
		/*jobSummary.setJobName("004taskid");*/
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.98.148:2181", DEFAULT_SESSION_TIMEOUT_MS, DEFAULT_CONNECTION_TIMEOUT_MS, DEFAULT_RETRY_POLICY);
		client.start();
		MasterSlaveApiFactory masterSlaveApiFactory = new MasterSlaveApiFactoryImpl(client);
		MasterSlaveJobData masterSlaveJobData = masterSlaveApiFactory.jobApi().getJob(jobSummary.getFlowId(), jobSummary.getJobName());
        MasterSlaveJobData.Data data;
        if (masterSlaveJobData == null) {
            data = new MasterSlaveJobData.Data();
        } else {
            data = masterSlaveJobData.getData();
        }
        //set data
        ReflectHelper.copyFieldValuesSkipNull(jobSummary, data);
       /* MasterSlaveJob masterSlaveJob = masterSlaveJobService.getJob( jobSummary.getGroupName(),  jobSummary.getJobName());
        data.setJobOperationLogId(masterSlaveJobLogService.saveJobLog(masterSlaveJobSummary));
        data.setPackagesToScan(masterSlaveJob.getPackagesToScan());
        data.setContainerType(masterSlaveJob.getContainerType());*/
        //set state to Executing
        /*MasterSlaveJobSummary param = new MasterSlaveJobSummary();
        param.setGroupName(data.getGroupName());
        param.setJobName(data.getJobName());
        MasterSlaveJobSummary masterSlaveJobSummaryInDb = baseDao.getUnique(MasterSlaveJobSummary.class, param);
        masterSlaveJobSummaryInDb.setJobState("Executing");
        baseDao.update(masterSlaveJobSummaryInDb);*/
        //send job
        masterSlaveApiFactory.jobApi().saveJob(jobSummary.getFlowId(), jobSummary.getJobName(), data);
		
	}
	
	public void executeJob(MasterSlaveJobSummary jobSummary,Vector<Object> populateParam){
		MasterSlaveApiFactory masterSlaveApiFactory = new MasterSlaveApiFactoryImpl(client);
		MasterSlaveJobData masterSlaveJobData = masterSlaveApiFactory.jobApi().getJob(jobSummary.getFlowId(), jobSummary.getJobName());
        MasterSlaveJobData.Data data;
        if (masterSlaveJobData == null) {
            data = new MasterSlaveJobData.Data();
        } else {
            data = masterSlaveJobData.getData();
        }
        //set data
        ReflectHelper.copyFieldValuesSkipNull(jobSummary, data);
        //send job
        masterSlaveApiFactory.jobApi().saveJob(jobSummary.getFlowId(), jobSummary.getJobName(), data);
	}
}
