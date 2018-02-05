package com.aspire.flow.test;

public class ThreadFlow extends Thread {
	public ThreadFlow(String flowId, String taskId, String status) {
		super();
		this.flowId = flowId;
		this.taskId = taskId;
		this.status = status;
	}
	private String flowId;
	private String taskId;
	private String status;
	private boolean flag = false;
	public void run() {
		FlowTest flowTest = new FlowTest();
		while (!flag) {
			flowTest.prin(flowId, taskId, status);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				flag = true;
			}
		}
	}
}
