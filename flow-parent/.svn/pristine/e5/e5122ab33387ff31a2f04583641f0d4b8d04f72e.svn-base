

package com.aspire.flow.api.data;

import java.util.Vector;

import com.aspire.flow.helper.StringHelper;


 /**
  * ZK Job数据节点抽象类
  * @author chenhaitao
  *
  * @param <T>
  */
public abstract class AbstractJobData<T extends AbstractJobData> implements Comparable<T> {

    private String groupName;

    private String jobName;

   /* private String jarFileName;

    private String packagesToScan;

    private String jobCron;*/

    private String containerType = "Common";

    private String jobState = "Shutdown";

    //private String misfirePolicy = "None";

    //private String jobOperationLogId;

    private String operationResult;

   // private String originalJarFileName;

    private String jobOperation;

    private Long version;

    private String errorMessage;
    
    private Vector<Object> params;
    
    private String excuteStatus;
    public void incrementVersion() {
        if (version == null || version == Long.MAX_VALUE) {
            this.version = 1L;
        } else {
            this.version++;
        }
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


    public String getJobState() {
        return jobState;
    }

    public void setJobState(String jobState) {
        this.jobState = jobState;
    }


    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public String getJobOperation() {
        return jobOperation;
    }

    public void setJobOperation(String jobOperation) {
        this.jobOperation = jobOperation;
    }
    
    public Vector<Object> getParams() {
		return params;
	}

	public void setParams(Vector<Object> params) {
		this.params = params;
	}
	
	

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public String getExcuteStatus() {
		return excuteStatus;
	}

	public void setExcuteStatus(String excuteStatus) {
		this.excuteStatus = excuteStatus;
	}

	@Override
    public int compareTo(AbstractJobData data) {
        return (groupName + "." + jobName).compareTo(data.getGroupName() + "." + data.getJobName());
    }

    public void prepareOperation() {
        this.operationResult = "Waiting";
        this.errorMessage = null;
    }

    public void clearOperationLog() {
        //this.jobOperationLogId = null;
        this.operationResult = null;
        this.errorMessage = null;
    }

    public boolean isOperated() {
        return StringHelper.isEmpty(this.jobOperation)
                && this.operationResult != null && !this.operationResult.equals("Waiting");
    }

    public void init() {
        setJobState("Shutdown");
        setJobOperation(null);
       // setOriginalJarFileName(null);
        setOperationResult("Success");
    }

    public void operateSuccess() {
        this.operationResult = "Success";
        this.jobOperation = null;
    }

    public void operateFailed(String errorMessage) {
        this.operationResult = "Failed";
        this.errorMessage = errorMessage;
        this.jobOperation = null;
    }


    public boolean isStart() {
        return jobOperation != null && jobOperation.equals("Start");
    }

    public boolean isStartup() {
        return jobState != null && jobState.equals("Startup");
    }

    public boolean isRestart() {
        return jobOperation != null && jobOperation.equals("Restart");
    }

    public boolean isPause() {
        return jobOperation != null && jobOperation.equals("Pause");
    }

    public boolean isUnknownOperation() {
        return !isStart() && !isRestart() && !isPause();
    }

    @Override
    public String toString() {
        return "JobData {" +
                "groupName='" + groupName + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobState='" + jobState + '\'' +
                ", operationResult='" + operationResult + '\'' +
                ", jobOperation='" + jobOperation + '\'' +
                ", version=" + version +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
