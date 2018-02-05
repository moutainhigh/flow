/*
 * taskflowContext.java
 *
 * Created on 2008-1-25 10:26:40 by luoqi
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
package com.aspire.etl.flowengine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.aspire.concurrent.ExecutorService;
import com.aspire.concurrent.Executors;
import com.aspire.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.TimeUtils;
import com.aspire.etl.flowdefine.TaskHistory;

/**
 * @author ����
 * @since 2008-3-19
 *        <p>
 *        ETL����ʵ���࣬ȷ�����̰���ȷ��·��˳��ִ�С�
 *        <p>
 *        ��Ҫ���ܣ� ����Ԫ�����Զ�������������������������Զ��л����������̡�
 *        <p>
 *        �Զ��ϵ������������˹�����״̬�� ����ʵ���ɹ���ɺ��Զ���ʼ��������ʵ����
 *        <p>
 *        �������й����д���ָ����ڵ����ʱ�Զ��������ԣ����������������̲��˳��������޷�����Ԫ���ݿ⡣
 *        <p>
 *        ��ע��ͬһ�����ڶ��߳�ִ��������ʱû��ʵ�֡�
 * 
 * @author ����
 * @since 2008-3-25
 *        <p>
 *        ֧�ֵ���C++��JAVA��д��������
 * 
 * @author ����
 * @since 2008-4-17
 *        <p>
 *        ��������������ֹͣ����
 * 
 * @author wuzhuokun
 * @since 2008-05-08
 *        <p>
 *        �޸���������ʱ���¼�������
 * 
 * @author wuzhuokun
 * @since 2008-05-27
 *        <p>
 *        �������̺�����ĸ��¿�ʼ�ͽ���ʱ��
 * @author wuzhuokun
 * @since 2008-06-18
 *        <p>
 *        �޸�bug �������ڴ������޸��ˣ������ݿ�����������û���޸ģ� 1��stattime û�лָ� 2��scene_stattimeû�����
 *        3��redo_flag���û���޸Ļ�0 4��������ʼʱ��ͽ���ʱ��û�����
 * 
 * @author ���棬���
 * @since 2009-7-1
 *        <p>
 *        1.����setDone()�������Ա����������̲߳��������߳��˳���2.ȡ����suspend��־���ж�
 */

public class TaskflowThread extends Thread {
	private static int FLOW_SLEEP_TIME = 1 * 60 * 1000;

	private static int TASK_TRY_SLEEP_TIME = 1 * 60 * 1000;

	private static int TASK_SLEEP_TIME = 1 * 1000;

	Taskflow taskflow;
	int poolSize;
	boolean isDone = false;
	
	boolean isRedoSleep = false;

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	private ExecutorService taskThreadPool = null;

	
	List<Task> rootTaskList = null;

	int tryCount = 0;

	Logger log;

	FlowMetaData flowMetaData;

	public TaskflowThread(Taskflow taskflow) throws Exception {
		super();
		this.taskflow = taskflow;
		this.log = Logger.getLogger(taskflow.getTaskflow());
		poolSize = taskflow.getThreadnum();
		this.taskThreadPool = Executors.newFixedThreadPool(poolSize);
		try {
			flowMetaData = FlowMetaData.getInstance();
		} catch (Exception e) {
			log.fatal("��ȡԪ���ݳ���" + e);
			e.printStackTrace();
			throw e;
		}
		log.info("��ʼ�������̳߳�,��󲢷���������" + poolSize);

		// ������ֵ
		try {
			FLOW_SLEEP_TIME = Integer.parseInt(flowMetaData
					.querySysConfigValue("FLOW_SLEEP_TIME")) * 1000;
		} catch (Exception e) {
			log.error(e);
			log.warn("��ʼ�����̼��ʱ�����,ϵͳ����ΪĬ��ֵ" + FLOW_SLEEP_TIME);
		}
		try {
			TASK_TRY_SLEEP_TIME = Integer.parseInt(flowMetaData
					.querySysConfigValue("TASK_TRY_SLEEP_TIME")) * 1000;
		} catch (Exception e) {
			log.error(e);
			log.warn("��ʼ���������Լ��ʱ�����,ϵͳ����ΪĬ��ֵ" + TASK_TRY_SLEEP_TIME);
		}
		try {
			TASK_SLEEP_TIME = Integer.parseInt(flowMetaData
					.querySysConfigValue("TASK_SLEEP_TIME")) * 1000;
		} catch (Exception e) {
			log.error(e);
			log.warn("��ʼ��������ʱ�����,ϵͳ����ΪĬ��ֵ" + TASK_SLEEP_TIME);
		}
	}

	public void run() {
		log.info("�߳�������");
		
		if (taskflow.getStatus() == Taskflow.RUNNING) {
			log.info("��ǰ����״̬Ϊ"+"RUNNING");
			// ������ʩ
			// ���������ʱ�����̴���running״̬����˵�������Ƿ������رյġ�
			// �����ʩ�������̺��������״̬Ϊrunning��QUEUE�����񶼸�ΪREADY״̬��
			repairStatusOnlyWhenStart();
		}
		
		log.info("����������ѭ������ֱ���յ�Console������ֹͣ����߳��˳���");
		while (isDone != true) {
			log.debug("isDone=" + isDone);
			// ��ͳ�ƽ���ʱ��Ϊ��ʱ�������˳�ִ��
			if (isStatTimeNull(taskflow)) {
				// �����̵�״̬��Ϊʧ�ܡ�
				log.info("ͳ�ƽ���ʱ��Ϊ��ʱ�������˳�ִ��");
				updateReadyTaskBackToStopped(taskflow);
				break;
			}

			// �ж��Ƿ������Ƿ�Ҫ����������
			if (taskflow.getRedoFlag() == Taskflow.REDO_NO) {
				log.debug("����ִ��:");
				run(taskflow);
			} else {
				log.debug("����:");
				redo(taskflow);
			}
			
			if(isDone){
				log.info("��⵽�˳��źţ������˳�ִ�С�");
				break;
			}else{				
				try {
					Thread.sleep(FLOW_SLEEP_TIME);
				} catch (InterruptedException e) {
					log.info("���̼�Ъ�ڱ����ѣ�");
				}				
			}

		}// end while
		
		if(isDone){
			updateTaskflowAndAllTaskToStoped(taskflow);
		}
		
		// ETL�����߳��˳�֮ǰ���ر��̳߳�
		log.info("�ر������̳߳ء�");
		//taskThreadPool.shutdown();
		taskThreadPool.shutdownNow();
		log.info("�߳�ֹͣ");
	}

	private boolean hasNotCompeleteFatherTaskflow(Taskflow taskflow) {
		boolean isExist = false;
		// �����̴��������������ϵ���жϸ������Ƿ������
		if (flowMetaData.isTaskflowCanRunJudgeByOutline(taskflow)) {
			isExist = false;
		} else {
			isExist = true;
		}
		return isExist;
	}

	private boolean isStatTimeNull(Taskflow taskflow) {
		boolean isNull = false;
		if (taskflow.getStatTime() == null || taskflow.getStatTime().equals("")) {
			log.debug("stattimeΪ��,���̲�������");
			isNull = true;
		}
		return isNull;
	}
public static void main(String[] args) {
	String endTime = TimeUtils.toChar(TimeUtils.getNewTime(new Date("2017-01-30"), "M", 1));
	System.out.println(endTime);
}
	private boolean isSystemTimeLessThenStatEndTime(Taskflow taskflow) {
		boolean isOK = false;
		Date endTime = TimeUtils.getNewTime(taskflow.getStatTime(), taskflow
				.getStepType(), taskflow.getStep());
		if (new Date().getTime() < endTime.getTime()) {
			isOK = true;
			log.debug("ͳ�ƽ���ʱ��:" + TimeUtils.toChar(endTime) + "���ڵ�ǰϵͳʱ�䣺"
					+ TimeUtils.toChar(new Date())+",���̲������С�");
		}else{
			log.debug("ͳ�ƽ���ʱ��:" + TimeUtils.toChar(endTime) + "С�ڵ�ǰϵͳʱ�䣺"
					+ TimeUtils.toChar(new Date()));
			
		}
		return isOK;
	}

	private void repairStatusOnlyWhenStart() {
		try {
			flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.READY);
			flowMetaData.resetTaskStatusByTaskflowID(taskflow.getTaskflowID(),
					Task.RUNNING, Task.READY);
			flowMetaData.resetTaskStatusByTaskflowID(taskflow.getTaskflowID(),
					Task.QUEUE, Task.READY);
			
			log.info("�ϴ���������Ӧ���Ǳ��쳣�жϵģ������̱����Լ�����������״̬ΪRUNNING��QUEUE�������ΪREADY");

		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("����Ԫ���ݿⷢ���쳣��");
		}
	}

	/**
	 * ����ETL���̡�
	 * 
	 * @throws InterruptedException
	 * @throws MetaDataException
	 */
	private void redo(Taskflow taskflow) {
		log.info("����" + taskflow.getTaskflow() + "��Ҫ����["
				+ TimeUtils.toChar(taskflow.getRedoStartTime()) + ","
				+ TimeUtils.toChar(taskflow.getRedoEndTime()) + "]");

		Date currStatTime = taskflow.getStatTime();
		Date redoEndTime = taskflow.getRedoEndTime();
                
		do {
			
			if (isDone) {
				log.info("��⵽�˳��źţ��˳�������");
				break;
			}else {
				if (isRedoSleep){
					try {
						Thread.sleep(FLOW_SLEEP_TIME);
					} catch (InterruptedException e) {
						log.info("���̼�Ъ�ڱ����ѣ�");
					}					
				}								
			}
			
			isRedoSleep = false;
			
			run(taskflow);
			
			currStatTime = taskflow.getStatTime();

		} while (currStatTime.getTime() <= redoEndTime.getTime());
		
		if (isDone){
			log.info("����" + taskflow.getTaskflow() + "��������["
					+ TimeUtils.toChar(taskflow.getRedoStartTime()) + ","
					+ TimeUtils.toChar(taskflow.getRedoEndTime()) + "]");
	
			resetRedoFlag(taskflow);
		}

	}
	
	private void remenberRedoStatus(Taskflow taskflow){
		try {
			log.info("�´���������ʱ������"+TimeUtils.toChar(taskflow.getStatTime())+"��ʼ����");
			flowMetaData.updateRedoStartTimeOfTaskflow(
					taskflow.getTaskflowID(), taskflow.getStatTime());
		} catch (MetaDataException e) {
			log.warn("����Ԫ���ݿⷢ���쳣��");
			e.printStackTrace();
		}
		taskflow.setRedoStartTime(taskflow.getStatTime());
	}
	
	private void resetRedoFlag(Taskflow taskflow) {
		try {
			log.info("����������,��������־��λ");
			flowMetaData.updateRedoFlagOfTaskflow(taskflow.getTaskflowID(),
					Taskflow.REDO_NO);
			taskflow.setRedoFlag(Taskflow.REDO_NO);

			log.info("�ָ���������ʼ֮ǰ�Ľ���");
			flowMetaData.updateStatTimeOfTaskflow(taskflow.getTaskflowID(),
					taskflow.getSceneStatTime());
			taskflow.setStatTime(taskflow.getSceneStatTime());

			log.info("������ʱ��,������ʼʱ�����������ʱ�临λ");
			flowMetaData.updateSceneStatTimeOfTaskflow(
					taskflow.getTaskflowID(), null);
			taskflow.setSceneStatTime(null);
			flowMetaData.updateRedoStartTimeOfTaskflow(
					taskflow.getTaskflowID(), null);
			taskflow.setRedoStartTime(null);
			flowMetaData.updateRedoEndTimeOfTaskflow(taskflow.getTaskflowID(),
					null);
			taskflow.setRedoEndTime(null);
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("����Ԫ���ݿⷢ���쳣��");
		}
	}

	/**
	 * ����ETL����
	 * 
	 * @throws InterruptedException
	 * @throws MetaDataException
	 */
	private void run(Taskflow taskflow) {
		try {
			if (isDone) {
				log.info("��⵽�˳��źţ��˳�ִ�С�");
				return;
			}
			// ��ϵͳʱ��С��ͳ�ƽ���ʱ��ʱ��
			if (isSystemTimeLessThenStatEndTime(taskflow)) {
				log.debug("ϵͳʱ��С��ͳ�ƽ���ʱ��");
				//������״̬��Ϊ�Ŷӣ��ͻ�����ʾ�ɵȴ�
				updateTaskflowToQueue(taskflow);
				isRedoSleep = true;				
				return;
			}
			// ����δ��ɵĸ�����
			if (hasNotCompeleteFatherTaskflow(taskflow)) {
				log.debug("����δ��ɵĸ�����");
				//������״̬��Ϊ�Ŷӣ��ͻ�����ʾ�ɵȴ�
				updateTaskflowToQueue(taskflow);
				isRedoSleep = true;
				return;
			}
			
			initMDC(taskflow);
			log.debug(taskflow.print());

			log.debug("�����̵�״̬��ΪRUNNING");
			updateTaskflowToRunning(taskflow);
//			String statTime = "" + taskflow.getStatTime();
			String statTime = TimeUtils.toChar(taskflow.getStatTime());
			String endStatTime = TimeUtils.toChar(TimeUtils.getNewTime(taskflow
					.getStatTime(), taskflow.getStepType(), taskflow.getStep()));
			
			String fileName ;
			if ( "MI".equals(taskflow.getStepType())){
				fileName = TimeUtils.toChar(taskflow.getStatTime(), "yyyyMMddHHmm");	
			}
			else{
				fileName = TimeUtils.toChar(taskflow.getStatTime(), "yyyyMMdd");
			}
			
			/*Long histId = flowMetaData.saveHistory(taskflow.getTaskflowID()+"", 
                    TaskHistory.RUNNING+"", statTime, endStatTime, endStatTime);*/
			Long histId = flowMetaData.saveHistoryWithName(taskflow.getTaskflowID()+"", 
					TaskHistory.RUNNING+"", statTime, endStatTime, endStatTime,fileName);
			
			log.info("��ʼѡȡ�����е������б�ֱ���������������񶼳ɹ�");
			while (!flowMetaData.isAllTaskSuccess(taskflow.getTaskflowID())) {
				if (isDone){
					break;
				}

				// ADD by wuyaxue 20170828: �����ж�һ���������Ƿ���ִ�д���������������˳�
			    if ( existFailedTask(taskflow) ) {
			    	if ( "AD-HOC".equals(taskflow.getMemo())) {
			    		// ��¼����ִ�д����¼
			    		/*flowMetaData.updateHistoryStatus( histId, TaskHistory.FAILED, 
			    				                          statTime, endStatTime, endStatTime, "");*/
			    		flowMetaData.updateHistoryStatus( histId, TaskHistory.FAILED, 
		                          statTime, endStatTime, endStatTime, fileName);
						log.debug("-----һ�������������д���ʧ�ܵ�����----�˳����� ");
						
						isDone = true ;
						/*��Ҫ����֪ͨzk,����ʧ�ܵ��߼�*/
			    		break;
			    	}
			    	/*�������������������ִ��ʧ�ܵ�����,���˳� by chenhaitao 2017-10-25*/
			    	if("CYC".equals(taskflow.getMemo())){
			    		flowMetaData.updateHistoryStatus( histId, TaskHistory.FAILED, 
		                          statTime, endStatTime, endStatTime, fileName);
			    		log.debug("------���������������д���ʧ�ܵ�����-----�˳�����");
			    		isDone = true ;
			    		/*��Ҫ����֪ͨzk,����ʧ�ܵ��߼�*/
			    		break;
			    	}
			    }
				
				log.debug("�����ǰ������������������������������б�");
				List<Task> runableTaskList = getRunableTaskList(taskflow);

				/* del by wuyaxue 20170828   ɾ������Ҫ�Ĵ���
				if(runableTaskList.size()==0){//hqw �����־���޴�ӡ
					log.debug("-----��ǰ������û����������������������б�----�˳���");
					isDone = true;
					return;
				}
				this.taskThreadPool = Executors.newFixedThreadPool(poolSize);
				*/
				
				// �������е���������̳߳أ�ע�⣺��ʱ�������״̬�޸�ΪQUEUE��
				for (Task te : runableTaskList) {
					updateTaskToQueue(te);
					log.debug("����"+te.getTask()+"�����̳߳��Ŷ�");
					Runnable r = new TaskContext(taskflow, te);
					//���Կ��ǻ������ȼ��̳߳�
					taskThreadPool.execute(r);
				}

				/* del by wuyaxue 2017/08/28  ɾ�����ж�
				taskThreadPool.shutdown();
		        try {//hqw �����߳�ûִ���������ɨ���ְ�����ִ�е���������̳߳����;�ȴ�ֱ�������������  
		        	taskThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);  
		        } catch (InterruptedException e) {  
		            e.printStackTrace();  
		        }  
		        */
				
				//ѡȡ���ʱ��
				try {
					//
					int taskSelectInterval = 30 ;
					String strTaskSelectInterval = flowMetaData.querySysConfigValue("SELECT_TASK_INTERVAL");
					if(strTaskSelectInterval.equalsIgnoreCase("")){
						log.warn("ȱ��ϵͳ����SELECT_TASK_INTERVAL��ʹ��ȱʡֵ30�롣");
					}else{
						taskSelectInterval = Integer.parseInt(strTaskSelectInterval);
					}
					
					Thread.sleep(taskSelectInterval*1000);
				} catch (InterruptedException e) {
					log.info("��ѡ����������б����ѣ�");	
					/*������ֶ�ֹͣ,ֱ�ӷ���*/
					return;
				}
				log.debug("������ѡ�����е�����");
			}

			if (isDone){
				log.info("��⵽�˳��źţ��˳�����ѡȡ��");
				// add by wuyaxue 20170828  ��������ִ����ʷΪ�쳣
	    		flowMetaData.updateHistoryStatus(histId, TaskHistory.FAILED, statTime, endStatTime, endStatTime, fileName);
				return;
			}

			log.debug("======  MEMO:  " + taskflow.getMemo() );

			// �����ʵ����ȫ�����񶼳ɹ�����׼������һ������ʵ������ǰ���whileѭ���п����Ǳ��û��ж϶��˳����ظ���顣
			if (flowMetaData.isAllTaskSuccess(taskflow.getTaskflowID())) {
				log.info(taskflow + "����ִ����ɣ�ͳ��ʱ��:"
						+ TimeUtils.toChar(taskflow.getStatTime()));
				
				// add by wuyaxue 20170828  ��������ִ����ʷΪ�ɹ�
	    		flowMetaData.updateHistoryStatus(histId, TaskHistory.COMPLETE, statTime, endStatTime, endStatTime, fileName);
				
				prepareNewEntity(taskflow);
				if ("AD-HOC".equals(taskflow.getMemo())) {
					log.info("======  AD-HOC  task, exit ==== ");
					isDone = true;
				}
			}else{
				log.info("�����˳�ִ��");				
			}

			log.debug(taskflow.print());
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("����Ԫ���ݿⷢ���쳣��",e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			log.warn("����Ԫ���ݿⷢ���쳣!!!��",e1);
		}
		
	}

	private void updateTaskflowToRunning(Taskflow taskflow)
			throws MetaDataException {
		// �����̵�״̬��Ϊrunning
		flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.RUNNING);
		flowMetaData.updateRunStartTimeOfTaskflow(taskflow.getTaskflowID(),	new Date());
	}
	
	private void updateTaskflowToQueue(Taskflow taskflow)
	throws MetaDataException {
		// �����̵�״̬��Ϊqueue
		log.debug("�����̵�״̬��ΪQUEUE");		
		flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.QUEUE);

}
	private void updateTaskToQueue(Task task) {		
		try {
			log.info("������\"" + task + "\"��״̬��ʾΪQUEUE");
			flowMetaData.updateTaskStatus(task, Task.QUEUE);
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("����Ԫ���ݿⷢ���쳣��");
		}
		
	}
	private void updateReadyTaskBackToStopped(Taskflow taskflow) {		
		try {
			log.info("������\"" + taskflow + "\"�µ�READY״̬���������¸ĳ�STOPPED");
			flowMetaData.resetTaskStatusByTaskflowID(taskflow.getTaskflowID(),Task.READY,Task.STOPPED);			
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("����Ԫ���ݿⷢ���쳣��");
		}
		
	}
	private void updateTaskflowToFailed(Taskflow taskflow) {
		// �����̵�״̬��Ϊrunning
		log.debug("�����̵�״̬��ΪFAILED");

		try {
			flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.FAILED);
			flowMetaData.updateRunEndTimeOfTaskflow(taskflow.getTaskflowID(),
					new Date());
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("����Ԫ���ݿⷢ���쳣��");
		}

	}
	
	private void updateTaskflowAndAllTaskToStoped(Taskflow taskflow) {
		log.info("�����̵�״̬��ΪSTOPPED");

		try {
			flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.STOPPED);
			flowMetaData.updateRunEndTimeOfTaskflow(taskflow.getTaskflowID(),
					new Date());
			log.info("�����������в��ǳɹ���ʧ��״̬�������ΪSTOPPED");
			List<Task> alltask = flowMetaData.queryTaskList(taskflow
					.getTaskflowID());
			for (Task task : alltask) {
				if (task.getStatus() != Task.SUCCESSED&&task.getStatus() != Task.FAILED) {
					flowMetaData.updateTaskStatus(task,Task.STOPPED);
				}
			}
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("����Ԫ���ݿⷢ���쳣��");
		}

	}
	
	private void initMDC(Taskflow taskflow) {
		MDC.put("TASKFLOW", taskflow.getTaskflow());
		MDC.put("REDO_FLAG", taskflow.getRedoFlag());
		MDC.put("STATTIME", TimeUtils.toChar(taskflow.getStatTime()));
		MDC.put("ENDTIME", TimeUtils.toChar(TimeUtils.getNewTime(taskflow
				.getStatTime(), taskflow.getStepType(), taskflow.getStep())));
	}

	private void prepareNewEntity(Taskflow taskflow) throws MetaDataException {
		// ������нڵ㶼�ѳɹ�����ȡ��һ������ʵ��
		flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.SUCCESSED);

		flowMetaData.initNextTaskflowEntity(taskflow);
		log.info("��һ��ETL����ʵ��\"" + taskflow + "\" "
				+ TimeUtils.toChar(taskflow.getStatTime()));

	}

	/**
	 * �ҳ��������е������б� ������״̬ΪREADY��FAILED �����������ĸ������Ѿ�SUCCESSED��
	 * 
	 * @param taskflow
	 * @return
	 * @throws Exception 
	 */
	private List<Task> getRunableTaskList(Taskflow taskflow) throws Exception {
		List<Task> alltask = flowMetaData.queryTaskList(taskflow
				.getTaskflowID());
		List<Task> retList = new ArrayList<Task>();

		/* del by wuyaxue 20170828 
		boolean bl = flowMetaData.getCountTaskHistoryByTaskflowId(taskflow.getTaskflowID())<=FlowEngine.redoTimes;
		*/
		
		for (Task te : alltask) {
			/* del by wuyaxue 20170828  ɾ������Ҫ�ļ�¼
			//hqw �������Դ�������bl
			if (flowMetaData.isAllFatherNodeSuccessed(te) && bl){
				retList.add(te);
			}
			*/
			if (flowMetaData.isAllFatherNodeSuccessed(te)
					&& (te.getStatus() == Task.READY||te.getStatus() == Task.FAILED)) {
				retList.add(te);
			}
		}

		return retList;
	}

	
	/**
	 * Add by wuyaxue 20170828  
	 * ����������������Ƿ���ʧ�ܵ�
	 * 
	 */
	private boolean existFailedTask (Taskflow taskflow) throws Exception {
		List<Task> alltask = flowMetaData.queryTaskList(taskflow.getTaskflowID());
		
		for (Task task : alltask) {
			if ( Task.FAILED == task.getStatus() ){
				return true ;
			}
		}

		return false;
	}
}
