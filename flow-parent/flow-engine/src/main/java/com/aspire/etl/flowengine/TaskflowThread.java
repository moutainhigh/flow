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
 * @author 罗奇
 * @since 2008-3-19
 *        <p>
 *        ETL流程实现类，确保流程按正确的路径顺序执行。
 *        <p>
 *        主要功能： 根据元数据自动多周期重做，流程重做完后自动切换回正常流程。
 *        <p>
 *        自动断点续作，不用人工更改状态。 流程实例成功完成后，自动初始化新流程实例。
 *        <p>
 *        流程运行过程中错误恢复，节点出错时自动无限重试，除非致命错误，流程不退出。例如无法连接元数据库。
 *        <p>
 *        备注：同一流程内多线程执行任务，暂时没有实现。
 * 
 * @author 罗奇
 * @since 2008-3-25
 *        <p>
 *        支持调用C++或JAVA编写的任务插件
 * 
 * @author 罗奇
 * @since 2008-4-17
 *        <p>
 *        增加流程启动和停止控制
 * 
 * @author wuzhuokun
 * @since 2008-05-08
 *        <p>
 *        修改流程启动时重新加载流程
 * 
 * @author wuzhuokun
 * @since 2008-05-27
 *        <p>
 *        增加流程和任务的更新开始和结束时间
 * @author wuzhuokun
 * @since 2008-06-18
 *        <p>
 *        修改bug 重做后内存数据修改了，但数据库中下面数据没有修改： 1、stattime 没有恢复 2、scene_stattime没有清空
 *        3、redo_flag标记没有修改回0 4、重做开始时间和结束时间没有清空
 * 
 * @author 罗奇，李宝钰
 * @since 2009-7-1
 *        <p>
 *        1.增加setDone()方法，以便让引擎主线程操作流程线程退出。2.取消对suspend标志的判断
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
			log.fatal("读取元数据出错！" + e);
			e.printStackTrace();
			throw e;
		}
		log.info("初始化任务线程池,最大并发任务数：" + poolSize);

		// 常量赋值
		try {
			FLOW_SLEEP_TIME = Integer.parseInt(flowMetaData
					.querySysConfigValue("FLOW_SLEEP_TIME")) * 1000;
		} catch (Exception e) {
			log.error(e);
			log.warn("初始化流程间隔时间出错,系统设置为默认值" + FLOW_SLEEP_TIME);
		}
		try {
			TASK_TRY_SLEEP_TIME = Integer.parseInt(flowMetaData
					.querySysConfigValue("TASK_TRY_SLEEP_TIME")) * 1000;
		} catch (Exception e) {
			log.error(e);
			log.warn("初始化任务重试间隔时间出错,系统设置为默认值" + TASK_TRY_SLEEP_TIME);
		}
		try {
			TASK_SLEEP_TIME = Integer.parseInt(flowMetaData
					.querySysConfigValue("TASK_SLEEP_TIME")) * 1000;
		} catch (Exception e) {
			log.error(e);
			log.warn("初始化任务间隔时间出错,系统设置为默认值" + TASK_SLEEP_TIME);
		}
	}

	public void run() {
		log.info("线程启动。");
		
		if (taskflow.getStatus() == Taskflow.RUNNING) {
			log.info("当前流程状态为"+"RUNNING");
			// 保护措施
			// 如果刚启动时，流程处于running状态，则说明引擎是非正常关闭的。
			// 处理措施：将流程和它下面的状态为running和QUEUE的任务都改为READY状态。
			repairStatusOnlyWhenStart();
		}
		
		log.info("按流程周期循环处理，直到收到Console发来的停止命令，线程退出。");
		while (isDone != true) {
			log.debug("isDone=" + isDone);
			// 当统计结束时间为空时，流程退出执行
			if (isStatTimeNull(taskflow)) {
				// 将流程的状态改为失败。
				log.info("统计结束时间为空时，流程退出执行");
				updateReadyTaskBackToStopped(taskflow);
				break;
			}

			// 判断是否流程是否要启动重做，
			if (taskflow.getRedoFlag() == Taskflow.REDO_NO) {
				log.debug("正常执行:");
				run(taskflow);
			} else {
				log.debug("重做:");
				redo(taskflow);
			}
			
			if(isDone){
				log.info("检测到退出信号，流程退出执行。");
				break;
			}else{				
				try {
					Thread.sleep(FLOW_SLEEP_TIME);
				} catch (InterruptedException e) {
					log.info("流程间歇期被唤醒！");
				}				
			}

		}// end while
		
		if(isDone){
			updateTaskflowAndAllTaskToStoped(taskflow);
		}
		
		// ETL流程线程退出之前，关闭线程池
		log.info("关闭任务线程池。");
		//taskThreadPool.shutdown();
		taskThreadPool.shutdownNow();
		log.info("线程停止");
	}

	private boolean hasNotCompeleteFatherTaskflow(Taskflow taskflow) {
		boolean isExist = false;
		// 从流程大纲中流程依赖关系来判断该流程是否可运行
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
			log.debug("stattime为空,流程不能运行");
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
			log.debug("统计结束时间:" + TimeUtils.toChar(endTime) + "晚于当前系统时间："
					+ TimeUtils.toChar(new Date())+",流程不能运行。");
		}else{
			log.debug("统计结束时间:" + TimeUtils.toChar(endTime) + "小于当前系统时间："
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
			
			log.info("上次运行引擎应该是被异常中断的，将流程本身，以及它里面所有状态为RUNNING和QUEUE的任务改为READY");

		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("操作元数据库发生异常！");
		}
	}

	/**
	 * 重做ETL流程。
	 * 
	 * @throws InterruptedException
	 * @throws MetaDataException
	 */
	private void redo(Taskflow taskflow) {
		log.info("流程" + taskflow.getTaskflow() + "需要重做["
				+ TimeUtils.toChar(taskflow.getRedoStartTime()) + ","
				+ TimeUtils.toChar(taskflow.getRedoEndTime()) + "]");

		Date currStatTime = taskflow.getStatTime();
		Date redoEndTime = taskflow.getRedoEndTime();
                
		do {
			
			if (isDone) {
				log.info("检测到退出信号，退出重做。");
				break;
			}else {
				if (isRedoSleep){
					try {
						Thread.sleep(FLOW_SLEEP_TIME);
					} catch (InterruptedException e) {
						log.info("流程间歇期被唤醒！");
					}					
				}								
			}
			
			isRedoSleep = false;
			
			run(taskflow);
			
			currStatTime = taskflow.getStatTime();

		} while (currStatTime.getTime() <= redoEndTime.getTime());
		
		if (isDone){
			log.info("流程" + taskflow.getTaskflow() + "重做结束["
					+ TimeUtils.toChar(taskflow.getRedoStartTime()) + ","
					+ TimeUtils.toChar(taskflow.getRedoEndTime()) + "]");
	
			resetRedoFlag(taskflow);
		}

	}
	
	private void remenberRedoStatus(Taskflow taskflow){
		try {
			log.info("下次流程启动时，将从"+TimeUtils.toChar(taskflow.getStatTime())+"开始重做");
			flowMetaData.updateRedoStartTimeOfTaskflow(
					taskflow.getTaskflowID(), taskflow.getStatTime());
		} catch (MetaDataException e) {
			log.warn("操作元数据库发生异常！");
			e.printStackTrace();
		}
		taskflow.setRedoStartTime(taskflow.getStatTime());
	}
	
	private void resetRedoFlag(Taskflow taskflow) {
		try {
			log.info("重做结束后,将重做标志复位");
			flowMetaData.updateRedoFlagOfTaskflow(taskflow.getTaskflowID(),
					Taskflow.REDO_NO);
			taskflow.setRedoFlag(Taskflow.REDO_NO);

			log.info("恢复到重做开始之前的进度");
			flowMetaData.updateStatTimeOfTaskflow(taskflow.getTaskflowID(),
					taskflow.getSceneStatTime());
			taskflow.setStatTime(taskflow.getSceneStatTime());

			log.info("将场景时间,重做开始时间和重做结束时间复位");
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
			log.warn("操作元数据库发生异常！");
		}
	}

	/**
	 * 启动ETL流程
	 * 
	 * @throws InterruptedException
	 * @throws MetaDataException
	 */
	private void run(Taskflow taskflow) {
		try {
			if (isDone) {
				log.info("检测到退出信号，退出执行。");
				return;
			}
			// 当系统时间小于统计结束时间时。
			if (isSystemTimeLessThenStatEndTime(taskflow)) {
				log.debug("系统时间小于统计结束时间");
				//将流程状态改为排队，客户端显示成等待
				updateTaskflowToQueue(taskflow);
				isRedoSleep = true;				
				return;
			}
			// 存在未完成的父流程
			if (hasNotCompeleteFatherTaskflow(taskflow)) {
				log.debug("存在未完成的父流程");
				//将流程状态改为排队，客户端显示成等待
				updateTaskflowToQueue(taskflow);
				isRedoSleep = true;
				return;
			}
			
			initMDC(taskflow);
			log.debug(taskflow.print());

			log.debug("将流程的状态置为RUNNING");
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
			
			log.info("开始选取可运行的任务列表，直到流程里所有任务都成功");
			while (!flowMetaData.isAllTaskSuccess(taskflow.getTaskflowID())) {
				if (isDone){
					break;
				}

				// ADD by wuyaxue 20170828: 增加判断一次性任务是否有执行错误任务，如果有则退出
			    if ( existFailedTask(taskflow) ) {
			    	if ( "AD-HOC".equals(taskflow.getMemo())) {
			    		// 记录流程执行错误记录
			    		/*flowMetaData.updateHistoryStatus( histId, TaskHistory.FAILED, 
			    				                          statTime, endStatTime, endStatTime, "");*/
			    		flowMetaData.updateHistoryStatus( histId, TaskHistory.FAILED, 
		                          statTime, endStatTime, endStatTime, fileName);
						log.debug("-----一次性任务流程中存在失败的任务----退出调度 ");
						
						isDone = true ;
						/*需要增加通知zk,任务失败的逻辑*/
			    		break;
			    	}
			    	/*新增周期性任务如果有执行失败的任务,则退出 by chenhaitao 2017-10-25*/
			    	if("CYC".equals(taskflow.getMemo())){
			    		flowMetaData.updateHistoryStatus( histId, TaskHistory.FAILED, 
		                          statTime, endStatTime, endStatTime, fileName);
			    		log.debug("------周期性任务流程中存在失败的任务-----退出调度");
			    		isDone = true ;
			    		/*需要增加通知zk,任务失败的逻辑*/
			    		break;
			    	}
			    }
				
				log.debug("查出当前流程中所有满足可运行条件的任务列表");
				List<Task> runableTaskList = getRunableTaskList(taskflow);

				/* del by wuyaxue 20170828   删除不必要的代码
				if(runableTaskList.size()==0){//hqw 解决日志无限打印
					log.debug("-----当前流程中没有满足可运行条件的任务列表----退出！");
					isDone = true;
					return;
				}
				this.taskThreadPool = Executors.newFixedThreadPool(poolSize);
				*/
				
				// 将可运行的任务放入线程池，注意：此时将任务的状态修改为QUEUE。
				for (Task te : runableTaskList) {
					updateTaskToQueue(te);
					log.debug("任务"+te.getTask()+"放入线程池排队");
					Runnable r = new TaskContext(taskflow, te);
					//可以考虑换用优先级线程池
					taskThreadPool.execute(r);
				}

				/* del by wuyaxue 2017/08/28  删除此判断
				taskThreadPool.shutdown();
		        try {//hqw 出现线程没执行完就重新扫描又把正在执行的任务加入线程池情况;等待直到所有任务完成  
		        	taskThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);  
		        } catch (InterruptedException e) {  
		            e.printStackTrace();  
		        }  
		        */
				
				//选取间隔时间
				try {
					//
					int taskSelectInterval = 30 ;
					String strTaskSelectInterval = flowMetaData.querySysConfigValue("SELECT_TASK_INTERVAL");
					if(strTaskSelectInterval.equalsIgnoreCase("")){
						log.warn("缺少系统参数SELECT_TASK_INTERVAL，使用缺省值30秒。");
					}else{
						taskSelectInterval = Integer.parseInt(strTaskSelectInterval);
					}
					
					Thread.sleep(taskSelectInterval*1000);
				} catch (InterruptedException e) {
					log.info("从选任务的休眠中被唤醒！");	
					/*如果是手动停止,直接返回*/
					return;
				}
				log.debug("接着再选可运行的任务");
			}

			if (isDone){
				log.info("检测到退出信号，退出任务选取。");
				// add by wuyaxue 20170828  更新流程执行历史为异常
	    		flowMetaData.updateHistoryStatus(histId, TaskHistory.FAILED, statTime, endStatTime, endStatTime, fileName);
				return;
			}

			log.debug("======  MEMO:  " + taskflow.getMemo() );

			// 如果本实例的全部任务都成功，则准备好下一个流程实例。因前面的while循环有可能是被用户中断而退出，重复检查。
			if (flowMetaData.isAllTaskSuccess(taskflow.getTaskflowID())) {
				log.info(taskflow + "流程执行完成！统计时间:"
						+ TimeUtils.toChar(taskflow.getStatTime()));
				
				// add by wuyaxue 20170828  更新流程执行历史为成功
	    		flowMetaData.updateHistoryStatus(histId, TaskHistory.COMPLETE, statTime, endStatTime, endStatTime, fileName);
				
				prepareNewEntity(taskflow);
				if ("AD-HOC".equals(taskflow.getMemo())) {
					log.info("======  AD-HOC  task, exit ==== ");
					isDone = true;
				}
			}else{
				log.info("流程退出执行");				
			}

			log.debug(taskflow.print());
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("操作元数据库发生异常！",e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			log.warn("操作元数据库发生异常!!!！",e1);
		}
		
	}

	private void updateTaskflowToRunning(Taskflow taskflow)
			throws MetaDataException {
		// 将流程的状态改为running
		flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.RUNNING);
		flowMetaData.updateRunStartTimeOfTaskflow(taskflow.getTaskflowID(),	new Date());
	}
	
	private void updateTaskflowToQueue(Taskflow taskflow)
	throws MetaDataException {
		// 将流程的状态改为queue
		log.debug("将流程的状态改为QUEUE");		
		flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.QUEUE);

}
	private void updateTaskToQueue(Task task) {		
		try {
			log.info("将任务\"" + task + "\"的状态标示为QUEUE");
			flowMetaData.updateTaskStatus(task, Task.QUEUE);
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("操作元数据库发生异常！");
		}
		
	}
	private void updateReadyTaskBackToStopped(Taskflow taskflow) {		
		try {
			log.info("将流程\"" + taskflow + "\"下的READY状态的任务，重新改成STOPPED");
			flowMetaData.resetTaskStatusByTaskflowID(taskflow.getTaskflowID(),Task.READY,Task.STOPPED);			
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("操作元数据库发生异常！");
		}
		
	}
	private void updateTaskflowToFailed(Taskflow taskflow) {
		// 将流程的状态改为running
		log.debug("将流程的状态改为FAILED");

		try {
			flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.FAILED);
			flowMetaData.updateRunEndTimeOfTaskflow(taskflow.getTaskflowID(),
					new Date());
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("操作元数据库发生异常！");
		}

	}
	
	private void updateTaskflowAndAllTaskToStoped(Taskflow taskflow) {
		log.info("将流程的状态改为STOPPED");

		try {
			flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.STOPPED);
			flowMetaData.updateRunEndTimeOfTaskflow(taskflow.getTaskflowID(),
					new Date());
			log.info("将流程下所有不是成功或失败状态的任务改为STOPPED");
			List<Task> alltask = flowMetaData.queryTaskList(taskflow
					.getTaskflowID());
			for (Task task : alltask) {
				if (task.getStatus() != Task.SUCCESSED&&task.getStatus() != Task.FAILED) {
					flowMetaData.updateTaskStatus(task,Task.STOPPED);
				}
			}
		} catch (MetaDataException e) {
			e.printStackTrace();
			log.warn("操作元数据库发生异常！");
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
		// 如果所有节点都已成功，则取下一个流程实例
		flowMetaData.updateStatusOfTaskflow(taskflow, Taskflow.SUCCESSED);

		flowMetaData.initNextTaskflowEntity(taskflow);
		log.info("下一个ETL流程实例\"" + taskflow + "\" "
				+ TimeUtils.toChar(taskflow.getStatTime()));

	}

	/**
	 * 找出可以运行的任务列表 条件：状态为READY或FAILED 的任务，且它的父任务都已经SUCCESSED。
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
			/* del by wuyaxue 20170828  删除不必要的记录
			//hqw 增加重试次数过滤bl
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
	 * 检查任务流中任务是否有失败的
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
