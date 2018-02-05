package com.aspire.etl.flowengine.xmlrpc;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.LevelRangeFilter;

import com.alibaba.fastjson.JSON;
import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.StepType;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskAttribute;
import com.aspire.etl.flowdefine.TaskHistory;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowdefine.TaskflowGroup;
import com.aspire.etl.flowengine.AbstractConfig;
import com.aspire.etl.flowengine.FlowEngine;
import com.aspire.etl.flowengine.MyConfigurator;
import com.aspire.etl.flowengine.TaskflowThread;
import com.aspire.etl.flowengine.taskcontext.ImportPropContext;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.ConnInfo;
import com.aspire.etl.tool.SqlMapUtil;
import com.aspire.etl.tool.TimeUtils;
import com.aspire.etl.tool.Utils;
import com.aspire.flow.api.MasterSlaveApiFactory;
import com.aspire.flow.api.data.MasterSlaveJobData;
import com.aspire.flow.api.impl.MasterSlaveApiFactoryImpl;
import com.aspire.flow.cluster.startup.IF1Submit;
import com.aspire.flow.entity.MasterSlaveJobSummary;
import com.aspire.flow.helper.ReflectHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * rpc服务类
 * 
 * @author wuzhuokun
 *
 *         2008-06-03 wuzhuokun 修改日志级别时即时生效
 *
 *         2009-07-3 luoqi ,libaoyu, 在修改流程状态机后对应修改相关命令。
 *
 *         2011-01-01 jiangtansen
 *         增加了刷新系统配置信息（refreshSysconfig）和流程配置信息（refreshTaskflow）的命令，
 *         增加了查询流程是否可以运行的命令（queryTaskflowToRun） 2011-01-10 jiangtansen
 *         修改forceUpdateStatTime命令的前置条件为就绪状态，修正：如果流程中的某些任务已经运行了，强制修改进度之后，
 *         这些任务不行再运行，这样的业务逻辑有问题
 *         修改reset命令的前置条件为stopped状态，修正：如果流程已经是启动，reset之后，当流程运行起来，流程还是显示就绪
 */

public class Command extends AbstractConfig{
	private static final int DEFAULT_SESSION_TIMEOUT_MS = Integer.getInteger("curator-default-session-timeout", 60 * 1000);

    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = Integer.getInteger("curator-default-connection-timeout", 15 * 1000);

    private static final RetryPolicy DEFAULT_RETRY_POLICY = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);
    private CuratorFramework client = null;
	static Logger log;
	static int OutlineGroupID = 0;

	public Command() {
		if (log == null) {
			try {
				log = Utils.initFileLog("xmlrpc", "./log", "xmlrpc",
						FlowMetaData.getInstance().querySysConfigValue("CONSOLE_LEVEL"));
			} catch (Exception e) {
				log = Utils.initFileLog("xmlrpc", "./log", "xmlrpc", "DEBUG");
				System.out.println("初始化xmlrpc.log出错,默认以DEBUG输出");
				e.printStackTrace();
			}
		}
	}

	public String alive() {
		log.info("alive");
		return "引擎正常!";
	}

	public String wakeupAllTaskflow() {
		log.info("wakeupAllTaskflow");
		for (TaskflowThread tfc : FlowEngine.taskflowThreadPool.values()) {
			wakeupTaskflow(tfc.getName());
		}
		return "成功! wakeupAllTaskflow ";
	}

	public String wakeupTaskflowGroup(String taskflowGroupName) {
		log.info("wakeupTaskflowGroup " + taskflowGroupName);
		try {
			FlowMetaData flowMetadata = FlowMetaData.getInstance();
			TaskflowGroup taskflowGroup = flowMetadata.queryTaskflowGroup(taskflowGroupName);

			if (taskflowGroup == null)
				return "没有对应的" + taskflowGroupName;

			List<Taskflow> taskflowList = flowMetadata.queryTaskflowInGroup(taskflowGroup.getGroupID());

			for (Taskflow t : taskflowList) {
				wakeupTaskflow(t.getTaskflow());
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! wakeupTaskflowGroup " + taskflowGroupName;
		}
		return "成功! wakeupTaskflowGroup " + taskflowGroupName;
	}
	/**
	 * 集群模式下启动任务流
	 * @author chenhaitao
	 * @param flowJsonStr
	 * @return
	 */
	public String startTaskflowForCluster(String flowJsonStr){
		final Taskflow flow = (Taskflow) JSONObject.toBean(JSONObject.fromObject(flowJsonStr), Taskflow.class);
		String taskflowName = flow.getTaskflow();
		log.info("startTaskflow " + taskflowName);
		try {
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			long t1 = new Date().getTime();
			Taskflow aTaskflow = flowMetaData.queryTaskflow(taskflowName);
			long t2 = new Date().getTime();
			log.info("从数据库查出流程用时= " + (t2 - t1));
			if (aTaskflow == null) {
				return "失败! 没有该流程 " + taskflowName;
			}
			if (aTaskflow.getGroupID() == OutlineGroupID) {
				// 流程属于大纲组，则返回失败。
				return "失败! 不能启动大纲流程 " + taskflowName;
			}
			if (aTaskflow.getSuspend() == Taskflow.SUSPEND_YES) {
				return "失败! 不能启动被禁用的流程 " + taskflowName + ",请先使用suspend命令解除禁用。";
			}
			if (aTaskflow.getStatTime() == null) {
				return "失败! 不能启动流程进度为空的流程，请使用updatetime指令设置流程开始时间";
			}

			if (isTaskflowThreadAlive(taskflowName)) {
				return "流程：" + taskflowName + "已经启动，不能重复启动。";
			}
			Logger log4new = Logger.getLogger(taskflowName);
			log.info("logger为: " + log4new);
			Appender appender = log4new.getAppender(taskflowName);
			log.info("appender为: " + appender);
			//如果appender为空说明,taskflowName命名的日志没有初始化,需要再次初始化 
			if (appender == null) {
				String logPath = Task.LOGDIR + taskflowName;
				MyConfigurator.makeDirs(logPath);
				log4new = Utils.initFileLog(taskflowName, logPath, taskflowName, aTaskflow.getFileLogLevel());
			}
			log4new.info("startTaskflow(" + taskflowName + ")");

			FlowEngine.getInstance().startNewTaskflowThread(taskflowName);

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! startTaskflow " + taskflowName;
		}
		return "成功! startTaskflow " + taskflowName;
	}
	/*public String startTaskflow(String taskflowName) {
		log.info("startTaskflow " + taskflowName);

		try {
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			long t1 = new Date().getTime();
			Taskflow aTaskflow = flowMetaData.queryTaskflow(taskflowName);
			long t2 = new Date().getTime();
			log.info("从数据库查出流程用时= " + (t2 - t1));
			if (aTaskflow == null) {
				return "失败! 没有该流程 " + taskflowName;
			}
			if (aTaskflow.getGroupID() == OutlineGroupID) {
				// 流程属于大纲组，则返回失败。
				return "失败! 不能启动大纲流程 " + taskflowName;
			}
			if (aTaskflow.getSuspend() == Taskflow.SUSPEND_YES) {
				return "失败! 不能启动被禁用的流程 " + taskflowName + ",请先使用suspend命令解除禁用。";
			}
			if (aTaskflow.getStatTime() == null) {
				return "失败! 不能启动流程进度为空的流程，请使用updatetime指令设置流程开始时间";
			}

			if (isTaskflowThreadAlive(taskflowName)) {
				return "流程：" + taskflowName + "已经启动，不能重复启动。";
			}
			
			 * else {
			 * 
			 * // 取当前流程的logger Logger log4new = Logger.getLogger(taskflowName);
			 * // if () //TODO log4new = Utils.initFileLog(taskflowName,
			 * "./log", taskflowName, aTaskflow.getFileLogLevel());
			 * log4new.info("startTaskflow(" + taskflowName + ")");
			 * 
			 * // 为这个流程的启动一个线程
			 * FlowEngine.getInstance().startNewTaskflowThread(taskflowName); }
			 
			Logger log4new = Logger.getLogger(taskflowName);
			log.info("logger为: " + log4new);
			Appender appender = log4new.getAppender(taskflowName);
			log.info("appender为: " + appender);
			 如果appender为空说明,taskflowName命名的日志没有初始化,需要再次初始化 
			if (appender == null) {
				String logPath = Task.LOGDIR + taskflowName;
				MyConfigurator.makeDirs(logPath);
				log4new = Utils.initFileLog(taskflowName, logPath, taskflowName, aTaskflow.getFileLogLevel());
			}
			log4new.info("startTaskflow(" + taskflowName + ")");

			FlowEngine.getInstance().startNewTaskflowThread(taskflowName);

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! startTaskflow " + taskflowName;
		}
		return "成功! startTaskflow " + taskflowName;
	}*/
	public String startTaskflow(String taskflowName) {
		log.info("startTaskflow " + taskflowName);
		try {
			MasterSlaveJobSummary jobSummary = new MasterSlaveJobSummary();
			jobSummary.setFlowId(taskflowName);
			/*jobSummary.setStatus("Startup");
			jobSummary.setTaskId("004taskid");*/
			jobSummary.setJobOperation("Start");
			//jobSummary.setJobOperation("Pause");
			jobSummary.setGroupName(taskflowName);
			jobSummary.setExcuteStatus("Start");
			/*jobSummary.setJobName("004taskid");*/
			zkDataUtil(jobSummary);
			/*FlowMetaData flowMetaData = FlowMetaData.getInstance();
			long t1 = new Date().getTime();
			Taskflow aTaskflow = flowMetaData.queryTaskflow(taskflowName);
			long t2 = new Date().getTime();
			log.info("从数据库查出流程用时= " + (t2 - t1));
			if (aTaskflow == null) {
				return "失败! 没有该流程 " + taskflowName;
			}
			if (aTaskflow.getGroupID() == OutlineGroupID) {
				// 流程属于大纲组，则返回失败。
				return "失败! 不能启动大纲流程 " + taskflowName;
			}
			if (aTaskflow.getSuspend() == Taskflow.SUSPEND_YES) {
				return "失败! 不能启动被禁用的流程 " + taskflowName + ",请先使用suspend命令解除禁用。";
			}
			if (aTaskflow.getStatTime() == null) {
				return "失败! 不能启动流程进度为空的流程，请使用updatetime指令设置流程开始时间";
			}

			if (isTaskflowThreadAlive(taskflowName)) {
				return "流程：" + taskflowName + "已经启动，不能重复启动。";
			}
			
			 * else {
			 * 
			 * // 取当前流程的logger Logger log4new = Logger.getLogger(taskflowName);
			 * // if () //TODO log4new = Utils.initFileLog(taskflowName,
			 * "./log", taskflowName, aTaskflow.getFileLogLevel());
			 * log4new.info("startTaskflow(" + taskflowName + ")");
			 * 
			 * // 为这个流程的启动一个线程
			 * FlowEngine.getInstance().startNewTaskflowThread(taskflowName); }
			 
			Logger log4new = Logger.getLogger(taskflowName);
			log.info("logger为: " + log4new);
			Appender appender = log4new.getAppender(taskflowName);
			log.info("appender为: " + appender);
			 如果appender为空说明,taskflowName命名的日志没有初始化,需要再次初始化 
			if (appender == null) {
				String logPath = Task.LOGDIR + taskflowName;
				MyConfigurator.makeDirs(logPath);
				log4new = Utils.initFileLog(taskflowName, logPath, taskflowName, aTaskflow.getFileLogLevel());
			}
			log4new.info("startTaskflow(" + taskflowName + ")");

			FlowEngine.getInstance().startNewTaskflowThread(taskflowName);*/

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! startTaskflow " + taskflowName;
		}
		return "成功! startTaskflow " + taskflowName;
	}

	public String startTaskflowGroup(String taskflowGroupName) {

		log.info("startTaskflowGroup " + taskflowGroupName);
		try {
			FlowMetaData flowMetadata = FlowMetaData.getInstance();
			TaskflowGroup taskflowGroup = flowMetadata.queryTaskflowGroup(taskflowGroupName);

			if (taskflowGroup == null)
				return "没有对应的" + taskflowGroupName;

			List<Taskflow> taskflowList = flowMetadata.queryTaskflowInGroup(taskflowGroup.getGroupID());

			for (Taskflow t : taskflowList) {
				startTaskflow(t.getTaskflow());
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! startTaskflow " + taskflowGroupName;
		}
		return "成功! startTaskflow " + taskflowGroupName;
	}

	public String stopTaskflowGroup(String taskflowGroupName) {

		log.info("stopTaskflowGroup " + taskflowGroupName);
		try {
			FlowMetaData flowMetadata = FlowMetaData.getInstance();
			TaskflowGroup taskflowGroup = flowMetadata.queryTaskflowGroup(taskflowGroupName);

			if (taskflowGroup == null)
				return "没有对应的" + taskflowGroupName;

			List<Taskflow> taskflowList = flowMetadata.queryTaskflowInGroup(taskflowGroup.getGroupID());

			for (Taskflow t : taskflowList) {
				stopTaskflow(t.getTaskflow());
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! stopTaskflowGroup " + taskflowGroupName;
		}
		return "成功! stopTaskflowGroup " + taskflowGroupName;
	}

	public String wakeupTaskflow(String taskflowName) {
		log.info("wakeupTaskflow " + taskflowName);
		try {
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			Taskflow aTaskflow = flowMetaData.queryTaskflow(taskflowName);
			if (aTaskflow == null) {
				return "失败! 没有该流程 " + taskflowName;
			}

			if (aTaskflow.getStatus() == Taskflow.STOPPED) {
				return "失败! 不能wakeup 停止的流程 " + taskflowName;
			}

			// 找回这个流程的线程句柄
			TaskflowThread thread = FlowEngine.taskflowThreadPool.get(taskflowName);

			// 打断线程的sleeping
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! wakeupTaskflow " + taskflowName;
		}
		return "成功! wakeupTaskflow " + taskflowName;
	}

	/**
	 * 重置流程:1、任务都改为ready。2、流程改为ready
	 * 
	 * @param taskflow
	 * @return
	 */
	public String resetTaskflow(String taskflow) {
		log.warn("resetTaskflow " + taskflow);
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflow);
			Taskflow taskflow1 = FlowMetaData.getInstance().queryTaskflow(taskflowID);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflow;
			if (taskflow1.getStatus() != Taskflow.STOPPED) {
				return "失败! 流程必须处于STOP状态才能执行此命令";
			}
			FlowMetaData.getInstance().resetTaskflowStatus(taskflowID);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! reset taskflow " + taskflow;
		}
		return "成功! reset taskflow " + taskflow;
	}

	public String stopAllTaskflow() {
		log.info("stopAllTaskflow");
		for (TaskflowThread tfc : FlowEngine.taskflowThreadPool.values()) {
			stopTaskflow(tfc.getName());
		}
		return "成功! stopAllTaskflow ";
	}

	public String startAllTaskflow() {
		log.info("startAllTaskflow");
		// 所有不属于大纲流程組的流程,并且不是suspend_yes。
		try {
			FlowMetaData flowMetadata = FlowMetaData.getInstance();
			List<Taskflow> taskflowList = flowMetadata.queryAllSuspendNoTaskflowAndNotInOutlineGroup();

			for (Taskflow t : taskflowList) {
				startTaskflow(t.getTaskflow());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! startAllTaskflow ";
		}

		return "成功! startAllTaskflow ";
	}

	public String suspendAllTaskflow(Boolean isSuspend) {
		log.info("suspendAllTaskflow " + isSuspend);
		// 所有不属于大纲流程組的流程。
		try {
			FlowMetaData flowMetadata = FlowMetaData.getInstance();
			List<Taskflow> taskflowList = flowMetadata.queryAllTaskflowNotInOutlineGroup();

			for (Taskflow t : taskflowList) {
				suspendTaskflow(t.getTaskflow(), isSuspend);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! suspendAllTaskflow ";
		}
		return "成功! suspendAllTaskflow " + isSuspend;
	}

	public String suspendTaskflowGroup(String taskflowGroupName, Boolean isSuspend) {
		log.info("suspendTaskflowGroup " + taskflowGroupName + " " + isSuspend);
		try {
			FlowMetaData flowMetadata = FlowMetaData.getInstance();
			TaskflowGroup taskflowGroup = flowMetadata.queryTaskflowGroup(taskflowGroupName);
			if (taskflowGroup == null) {

				return "没有对应的" + taskflowGroupName;
			}

			List<Taskflow> taskflowList = flowMetadata.queryTaskflowInGroup(taskflowGroup.getGroupID());

			boolean canSuspend = true;
			String aliveTaskflowName = "";

			for (Taskflow t : taskflowList) {

				if (isTaskflowThreadAlive(t.getTaskflow())) {
					canSuspend = false;
					aliveTaskflowName += t.getTaskflow() + "\r\n";
				}
			}

			if (!canSuspend) {
				return "以下流程的线程正在运行，不能修改suspend状态，请先停止该流程：" + aliveTaskflowName;
			} else {
				if (isSuspend) {
					flowMetadata.updateSuspendOfTaskflowByGroup(taskflowGroup.getGroupID(), Taskflow.SUSPEND_YES);
					flowMetadata.updateSuspendOfOutlineTaskByTaskflowGroup(taskflowGroupName, Task.SUSPEND_YES);
				} else {
					flowMetadata.updateSuspendOfTaskflowByGroup(taskflowGroup.getGroupID(), Taskflow.SUSPEND_NO);
					flowMetadata.updateSuspendOfOutlineTaskByTaskflowGroup(taskflowGroupName, Task.SUSPEND_YES);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! suspendTaskflowGroup " + taskflowGroupName + " " + isSuspend;
		}
		return "成功! suspendTaskflowGroup " + taskflowGroupName + " " + isSuspend;
	}

	public String suspendTaskflow(String taskflow, Boolean isSuspend) {
		log.info("suspendTaskflow " + taskflow + " " + isSuspend);
		try {
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			Integer taskflowID = flowMetaData.getTaskflowIDbyName(taskflow);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflow;

			if (isTaskflowThreadAlive(taskflow)) {
				return "流程：" + taskflow + "流程正在运行，不能修改suspend状态，请先STOP该流程。";
			} else {
				if (isSuspend) {
					flowMetaData.updateSuspendOfTaskflow(taskflowID, Taskflow.SUSPEND_YES);
					flowMetaData.updateSuspendOfOutlineTask(taskflow, Task.SUSPEND_YES);
				} else {
					flowMetaData.updateSuspendOfTaskflow(taskflowID, Taskflow.SUSPEND_NO);
					flowMetaData.updateSuspendOfOutlineTask(taskflow, Task.SUSPEND_NO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! suspendTaskflow " + taskflow + " " + isSuspend;
		}
		return "成功! suspendTaskflow " + taskflow + " " + isSuspend;
	}

	public String suspendTask(String taskflow, String task, Boolean isSuspend) {
		log.info("suspendTask " + taskflow + " " + task + " " + isSuspend);
		String retMsg = "";
		try {
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			Integer taskflowID = flowMetaData.getTaskflowIDbyName(taskflow);
			if (taskflowID == null) {
				return "失败! 没有该流程 " + taskflow;
			}
			Task aTask = flowMetaData.queryTaskByName(taskflowID, task);
			if (aTask == null) {
				return "失败! 没有该任务 " + task;
			}
			if (isTaskflowThreadAlive(taskflow)) {
				return "流程：" + taskflow + "流程正在运行，不能禁用任务，请先STOP该流程。";
			} else {
				if (isSuspend) {
					flowMetaData.updateSuspendOfTask(aTask.getTaskID(), Task.SUSPEND_YES);
					retMsg = "已禁用任务" + task;
				} else {
					flowMetaData.updateSuspendOfTask(aTask.getTaskID(), Task.SUSPEND_NO);
					retMsg = "已启用任务" + task;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! suspendTask " + taskflow + " " + task + " " + retMsg;
		}
		return "成功! suspendTask " + taskflow + " " + task + " " + retMsg;
	}

	public String skipTask(String taskflow, String task) {
		log.info("skipTask " + taskflow + " " + task);
		String retMsg = "";
		try {
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			Integer taskflowID = flowMetaData.getTaskflowIDbyName(taskflow);
			if (taskflowID == null) {
				return "失败! 没有该流程 " + taskflow;
			}
			Task aTask = flowMetaData.queryTaskByName(taskflowID, task);
			if (aTask == null) {
				return "失败! 没有该任务 " + task;
			}
			flowMetaData.updateTaskStatus(aTask, Task.SUCCESSED);

			retMsg = "已跳过任务" + task;

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! skipTask " + taskflow + " " + task + " " + retMsg;
		}
		return "成功! skipTask " + taskflow + " " + task + " " + retMsg;
	}

	/*public String stopTaskflow(String taskflow) {
		log.info("stopTaskflow " + taskflow);
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflow);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflow;

			// 找回这个流程的线程句柄
			TaskflowThread thread = FlowEngine.taskflowThreadPool.get(taskflow);
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			Taskflow taskFlow = flowMetaData.queryTaskflow(taskflow);
			int status = taskFlow.getStatus();
			log.info("任务流status: " + status);
			if(status == Taskflow.RUNNING){ //任务处于running状态
				return "流程已经处于running状态,不可以停止";
			}
			// 优雅退出,不在这里修改STOP状态，在流程线程的run方法的最后修改。
			if (thread != null && thread.isAlive()) {
				thread.setDone(true);
				thread.interrupt();
				FlowEngine.taskflowThreadPool.remove(taskflow);// 将线程从缓存中移除 by
																// chenhaitao
			} else {
				if (thread != null) {
					log.info(thread + "是否存活: " + thread.isAlive());
				} else {
					log.info(thread + "不存在");
				}
				return "流程已经是停止的! stopTaskflow " + taskflow;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! stopTaskflow " + taskflow;
		}
		return "成功! stopTaskflow " + taskflow;
	}*/
	
	public String stopTaskflowForCluster(String taskflow){
		log.info("stopTaskflow " + taskflow);
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflow);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflow;

			// 找回这个流程的线程句柄
			TaskflowThread thread = FlowEngine.taskflowThreadPool.get(taskflow);
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			Taskflow taskFlow = flowMetaData.queryTaskflow(taskflow);
			int status = taskFlow.getStatus();
			log.info("任务流status: " + status);
			if(status == Taskflow.RUNNING){ //任务处于running状态
				return "流程已经处于running状态,不可以停止";
			}
			// 优雅退出,不在这里修改STOP状态，在流程线程的run方法的最后修改。
			if (thread != null && thread.isAlive()) {
				thread.setDone(true);
				thread.interrupt();
				FlowEngine.taskflowThreadPool.remove(taskflow);// 将线程从缓存中移除 by
																// chenhaitao
			} else {
				if (thread != null) {
					log.info(thread + "是否存活: " + thread.isAlive());
				} else {
					log.info(thread + "不存在");
				}
				return "流程已经是停止的! stopTaskflow " + taskflow;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! stopTaskflow " + taskflow;
		}
		return "成功! stopTaskflow " + taskflow;
	}
	public String stopTaskflow(String taskflow) {
		log.info("stopTaskflow " + taskflow);
		try {
			MasterSlaveJobSummary jobSummary = new MasterSlaveJobSummary();
			jobSummary.setFlowId(taskflow);
			/*jobSummary.setStatus("Startup");
			jobSummary.setTaskId("004taskid");*/
			jobSummary.setJobOperation("Pause");
			//jobSummary.setJobOperation("Pause");
			jobSummary.setGroupName(taskflow);
			jobSummary.setExcuteStatus("Pause");
			/*jobSummary.setJobName("004taskid");*/
			zkDataUtil(jobSummary);
			/*Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflow);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflow;

			// 找回这个流程的线程句柄
			TaskflowThread thread = FlowEngine.taskflowThreadPool.get(taskflow);
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			Taskflow taskFlow = flowMetaData.queryTaskflow(taskflow);
			int status = taskFlow.getStatus();
			log.info("任务流status: " + status);
			if(status == Taskflow.RUNNING){ //任务处于running状态
				return "流程已经处于running状态,不可以停止";
			}
			// 优雅退出,不在这里修改STOP状态，在流程线程的run方法的最后修改。
			if (thread != null && thread.isAlive()) {
				thread.setDone(true);
				thread.interrupt();
				FlowEngine.taskflowThreadPool.remove(taskflow);// 将线程从缓存中移除 by
																// chenhaitao
			} else {
				if (thread != null) {
					log.info(thread + "是否存活: " + thread.isAlive());
				} else {
					log.info(thread + "不存在");
				}
				return "流程已经是停止的! stopTaskflow " + taskflow;
			}*/

		} catch (Exception e) {
			e.printStackTrace();
			return "失败! stopTaskflow " + taskflow;
		}
		return "成功! stopTaskflow " + taskflow;
	}

	public String descTaskflow(String taskflowName) {
		log.info("descTaskflow " + taskflowName);
		Taskflow taskflow = null;
		try {
			taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowName);
			if (taskflow == null)
				return "失败! 没有该流程 " + taskflowName;
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! desc Taskflow " + taskflow;
		}
		return taskflow.getDescription() == null ? "" : taskflow.getDescription();
	}

	// 修改并发线程数
	public String updateThreadnum(Integer taskflowID, int threadnum) {
		log.info("updateThreadnum " + taskflowID + " " + threadnum);
		try {
			FlowMetaData.getInstance().updateThreadnumOfTaskflow(taskflowID, threadnum);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改线程数失败";
		}
		return "成功! 线程数改为 " + threadnum + "，流程重新start之后生效!";
	}

	// 修改并发线程数
	public String updateThreadnum(String taskflowName, int threadnum) {
		log.info("updateThreadnum " + taskflowName + " " + threadnum);
		try {

			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflowName);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflowName;
			return updateThreadnum(taskflowID, threadnum);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改线程数失败";
		}
	}

	// 修改流程进度
	public String updateStatTime(String taskflow, Date statTime) {
		log.info("updateStatTime " + taskflow + " " + statTime);
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflow);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflow;
			return updateStatTime(taskflowID, statTime);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改流程进度失败";
		}
	}

	// 修改流程进度
	public String updateStatTime(Integer taskflowID, Date statTime) {
		log.info("updateStatTime " + taskflowID + " " + statTime);
		try {
			Taskflow taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowID);
			if (taskflow.getStatTime() != null)
				return "失败! 流程已有流程进度，不能重复设置";
			FlowMetaData.getInstance().updateStatTimeOfTaskflow(taskflowID, statTime);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改流程进度失败";
		}
		return "成功! 流程进度改为 " + TimeUtils.toChar(statTime);
	}

	// 强制修改流程进度
	public String forceUpdateStatTime(String taskflow, Date statTime) {
		log.info("forceUpdateStatTime " + taskflow + " " + statTime);
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflow);
			if (taskflowID == null) {
				return "失败! 没有该流程 " + taskflow;
			}

			Taskflow taskflow1 = FlowMetaData.getInstance().queryTaskflow(taskflowID);
			if (taskflow1.getStatus() != Taskflow.READY) {
				return "失败! 流程必须处于READY状态才能强制修改流程进度";
			}
			FlowMetaData.getInstance().updateStatTimeOfTaskflow(taskflowID, statTime);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 强制修改流程进度失败";
		}
		return "成功! 流程进度被强制改为 " + TimeUtils.toChar(statTime);
	}

	/*
	 * //场景时间 public String updateSceneStatTime(int taskflowID, Date
	 * sceneStatTime){ log.info("updateSceneStatTime " + taskflowID + " " +
	 * sceneStatTime); try {
	 * FlowMetaData.getInstance().updateSceneStatTimeOfTaskflow(taskflowID,
	 * sceneStatTime); } catch (Exception e) { e.printStackTrace(); return
	 * "失败! 修改场景时间失败"; } return "成功! 场景时间改为 " + sceneStatTime; }
	 * 
	 * //场景时间清空 public String updateSceneStatTime(int taskflowID){ log.info(
	 * "updateSceneStatTime " + taskflowID); try {
	 * FlowMetaData.getInstance().updateSceneStatTimeOfTaskflow(taskflowID,
	 * null); } catch (Exception e) { e.printStackTrace(); return "失败! 修改场景时间失败"
	 * ; } return "成功! 场景时间改为空"; }
	 * 
	 * //重做开始时间 public String updateRedoStartTime(int taskflowID, Date
	 * redoStartTime){ log.info("updateRedoStartTime " + taskflowID + " " +
	 * redoStartTime); try {
	 * FlowMetaData.getInstance().updateRedoStartTimeOfTaskflow(taskflowID,
	 * redoStartTime); } catch (Exception e) { e.printStackTrace(); return
	 * "失败! 修改重做开始时间失败"; } return "成功! 重做开始时间改为 " + redoStartTime; }
	 * 
	 * //重做开始时间清空 public String updateRedoStartTime(int taskflowID){ log.info(
	 * "updateRedoStartTime " + taskflowID); try {
	 * FlowMetaData.getInstance().updateRedoStartTimeOfTaskflow(taskflowID,
	 * null); } catch (Exception e) { e.printStackTrace(); return
	 * "失败! 修改重做开始时间失败"; } return "成功! 重做开始时间改为空"; }
	 * 
	 * //重做结束时间 public String updateRedoEndTime(int taskflowID, Date
	 * redoEndTime){ log.info("updateRedoEndTime " + taskflowID + " " +
	 * redoEndTime); try {
	 * FlowMetaData.getInstance().updateRedoEndTimeOfTaskflow(taskflowID,
	 * redoEndTime); } catch (Exception e) { e.printStackTrace(); return
	 * "失败! 修改重做结束时间失败"; } return "成功! 重做结束时间改为 " + redoEndTime; }
	 * 
	 * //重做结束时间清空 public String updateRedoEndTime(int taskflowID){ log.info(
	 * "updateRedoEndTime " + taskflowID); try {
	 * FlowMetaData.getInstance().updateRedoEndTimeOfTaskflow(taskflowID, null);
	 * } catch (Exception e) { e.printStackTrace(); return "失败! 修改重做结束时间失败"; }
	 * return "成功! 重做结束时间改为空"; }
	 * 
	 * //是否重做 public String updateRedoFlag(int taskflowID, int redoFlag){
	 * log.info("updateRedoFlag " + taskflowID + " " + redoFlag); try {
	 * FlowMetaData.getInstance().updateRedoFlagOfTaskflow(taskflowID,
	 * redoFlag); } catch (Exception e) { e.printStackTrace(); return
	 * "失败! 修改是否重做失败"; } return "成功! 是否重做改为 " + redoFlag; }
	 */
	// 文件日志级别
	public String updateFileLogLevel(int taskflowID, String fileLogLevel) {
		log.info("updateFileLogLevel " + taskflowID + " " + fileLogLevel);
		try {
			FlowMetaData.getInstance().updateFileLogLevelOfTaskflow(taskflowID, fileLogLevel);
			Taskflow taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowID);
			if (taskflow != null) {
				Logger log = Logger.getLogger(taskflow.getTaskflow());
				Appender appender = log.getAppender(taskflow.getTaskflow());
				LevelRangeFilter filter = (LevelRangeFilter) appender.getFilter();
				filter.setLevelMin(Level.toLevel(fileLogLevel));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改文件日志级别失败";
		}
		return "成功!文件日志级别改为 " + fileLogLevel;
	}

	// 文件日志级别
	public String updateFileLogLevel(String taskflowName, String fileLogLevel) {
		log.info("updateFileLogLevel " + taskflowName + " " + fileLogLevel);
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflowName);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflowName;
			return updateFileLogLevel(taskflowID, fileLogLevel);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改文件日志级别失败";
		}
	}

	// 数据库日志级别
	public String updateDbLogLevel(int taskflowID, String dbLogLevel) {
		log.info("updateDbLogLevel " + taskflowID + " " + dbLogLevel);
		try {
			FlowMetaData.getInstance().updateDbLogLevelOfTaskflow(taskflowID, dbLogLevel);
			Taskflow taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowID);
			if (taskflow != null) {
				Logger log = Logger.getLogger(taskflow.getTaskflow());
				Appender appender = log.getAppender(taskflow.getTaskflow() + "jdbc");
				LevelRangeFilter filter = (LevelRangeFilter) appender.getFilter();
				filter.setLevelMin(Level.toLevel(dbLogLevel));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改数据库日志级别失败";
		}
		return "成功!数据库日志级别改为 " + dbLogLevel;
	}

	// 数据库日志级别
	public String updateDbLogLevel(String taskflowName, String dbLogLevel) {
		log.info("updateDbLogLevel " + taskflowName + " " + dbLogLevel);
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflowName);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflowName;
			updateDbLogLevel(taskflowID, dbLogLevel);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改数据库日志级别失败";
		}
		return "成功!数据库日志级别改为 " + dbLogLevel;
	}

	// 重做
	public String redo(Integer taskflowID, Date redoStartTime, Date redoEndTime) {
		log.info("redo " + taskflowID + " " + redoStartTime + " " + redoEndTime);

		try {
			Taskflow taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowID);
			if (taskflow.getStatTime() == null) {
				return "失败! 流程进度为空，不能重做";
			}
			/*
			 * if (isTaskflowThreadAlive(taskflow.getTaskflow())){ return
			 * "失败! 流程正在启动，必须stop后才能修改重做标志"; }
			 */

			boolean checkAdHocStatus = (Taskflow.STOPPED == taskflow.getStatus())
					&& ("AD-HOC".equals(taskflow.getMemo()));

			// 将一次性任务的重做时间提前一天
			/*
			 * if("AD-HOC".equals(taskflow.getMemo())) { SimpleDateFormat dft =
			 * new SimpleDateFormat("yyyy-MM-dd"); Calendar date =
			 * Calendar.getInstance(); date.setTime(redoStartTime);
			 * date.set(Calendar.DATE, date.get(Calendar.DATE) - 1); Date
			 * newDate = dft.parse(dft.format(date.getTime()));
			 * 
			 * redoStartTime = newDate; log.info("redo new date" +
			 * redoStartTime); }
			 */

			boolean checkStatus = (Taskflow.READY == taskflow.getStatus()) || (Taskflow.QUEUE == taskflow.getStatus())
					|| checkAdHocStatus;
			if (!checkStatus) {
				return "失败! 流程处于状态(" + taskflow.getStatus() + "), 不能重做";
				// 流程处于STOPED状态时，可能部分任务已经执行
			}

			/*
			 * if(taskflow.getRedoFlag() == Taskflow.REDO_NO){
			 * if(!redoStartTime.before(taskflow.getStatTime())) return
			 * "失败! 重做开始时间必须小于流程进度"; if(!checkRedoTime(redoStartTime,
			 * redoEndTime, taskflow.getStepType(), taskflow.getStep())) return
			 * "失败! 重做时间范围必须是流程周期的整数倍"; redo(taskflowID, redoStartTime,
			 * redoEndTime, taskflow); }else{
			 * if(taskflow.getStatTime().getTime() !=
			 * taskflow.getRedoStartTime().getTime()) return
			 * "失败! 重做流程已开始运行，不能重做"; if(!checkRedoTime(redoStartTime,
			 * redoEndTime, taskflow.getStepType(), taskflow.getStep())) return
			 * "失败! 重做时间范围必须是流程周期的整数倍";
			 * if(!redoStartTime.before(taskflow.getSceneStatTime())) return
			 * "失败! 重做开始时间必须小于场景时间" +
			 * TimeUtils.toChar(taskflow.getSceneStatTime()); redo(taskflowID,
			 * redoStartTime, redoEndTime, taskflow); }
			 */
			log.info("redo not valide running..");
			redo(taskflowID, redoStartTime, redoEndTime, taskflow);// 任何时间都可以重做，LEO
																	// updated
																	// by
																	// 2017-06-20
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改流程重做失败";
		}
		return "成功! 修改流程重做成功";
	}

	/*private void redo(Integer taskflowID, Date redoStartTime, Date redoEndTime, Taskflow taskflow)
			throws MetaDataException, Exception {
		FlowMetaData.getInstance().redo(taskflowID, redoStartTime, redoEndTime);

		// 重新启动一次性任务
		if ("AD-HOC".equals(taskflow.getMemo())) {
			//FlowEngine.getInstance().startNewTaskflowThread(taskflow.getTaskflow());
			// 找回这个流程的线程句柄
			TaskflowThread thread = FlowEngine.taskflowThreadPool.get(taskflow.getTaskflow());
			log.info("thread:" + thread);
			if (thread != null && !thread.isAlive()) {
				log.info("thread:" + "是否存活" + thread.isAlive());
				FlowEngine.taskflowThreadPool.remove(taskflow.getTaskflow());// 将线程从缓存中移除 by
																// chenhaitao
			}
			startTaskflow(taskflow.getTaskflow());
		}
	}*/
	
	private void redo(Integer taskflowID, Date redoStartTime, Date redoEndTime, Taskflow taskflow)
			throws MetaDataException, Exception {
		FlowMetaData.getInstance().redo(taskflowID, redoStartTime, redoEndTime);
		MasterSlaveJobSummary jobSummary = new MasterSlaveJobSummary();
		jobSummary.setFlowId(taskflowID + "");
		/*jobSummary.setStatus("Startup");
		jobSummary.setTaskId("004taskid");*/
		jobSummary.setJobOperation("Start");
		//jobSummary.setJobOperation("Pause");
		jobSummary.setGroupName(taskflowID + "");
		jobSummary.setExcuteStatus("Redo");
		/*jobSummary.setJobName("004taskid");*/
		zkDataUtil(jobSummary);
		// 重新启动一次性任务
		/*if ("AD-HOC".equals(taskflow.getMemo())) {
			//FlowEngine.getInstance().startNewTaskflowThread(taskflow.getTaskflow());
			// 找回这个流程的线程句柄
			TaskflowThread thread = FlowEngine.taskflowThreadPool.get(taskflow.getTaskflow());
			log.info("thread:" + thread);
			if (thread != null && !thread.isAlive()) {
				log.info("thread:" + "是否存活" + thread.isAlive());
				FlowEngine.taskflowThreadPool.remove(taskflow.getTaskflow());// 将线程从缓存中移除 by
																// chenhaitao
			}
			startTaskflow(taskflow.getTaskflow());
		}*/
	}
	
	private void redoForCluster(String flowJsonStr){
		final Taskflow taskflow = (Taskflow) JSONObject.toBean(JSONObject.fromObject(flowJsonStr), Taskflow.class);
		if ("AD-HOC".equals(taskflow.getMemo())) {
			//FlowEngine.getInstance().startNewTaskflowThread(taskflow.getTaskflow());
			// 找回这个流程的线程句柄
			TaskflowThread thread = FlowEngine.taskflowThreadPool.get(taskflow.getTaskflow());
			log.info("thread:" + thread);
			if (thread != null && !thread.isAlive()) {
				log.info("thread:" + "是否存活" + thread.isAlive());
				FlowEngine.taskflowThreadPool.remove(taskflow.getTaskflow());// 将线程从缓存中移除 by
																// chenhaitao
			}
			startTaskflowForCluster(taskflow.getTaskflow());
		}
	}

	private boolean checkRedoTime(Date redoStartTime, Date redoEndTime, String stepType, int step) {
		Date start = new Date(redoStartTime.getTime());
		Date end = new Date(redoEndTime.getTime());
		while (start.getTime() != end.getTime()) {
			if (end.before(start))
				return false;
			start = TimeUtils.getNewTime(start, stepType, step);
		}
		return true;
	}

	// 重做
	public String redo(String taskflowName, Date redoStartTime, Date redoEndTime) {
		log.info("redo " + taskflowName + " " + redoStartTime + " " + redoEndTime);
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflowName);
			if (taskflowID == null)
				return "失败! 没有该流程 " + taskflowName;
			return redo(taskflowID, redoStartTime, redoEndTime);
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 修改流程重做失败";
		}
	}

	// 跳过
	public String skipTaskflow(String taskflowName) {
		log.warn("skip " + taskflowName);
		String retMsg = "";
		try {
			Taskflow taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowName);

			if (taskflow.getStatus() != Taskflow.STOPPED) {
				return "失败! 流程必须处于STOP状态才能执行此命令";
			}
			retMsg = "跳过当前进度:" + TimeUtils.toChar(taskflow.getStatTime());
			FlowMetaData.getInstance().initNextTaskflowEntity(taskflow);

			// 强制生成的新实例的状态置为STOPPED。
			FlowMetaData.getInstance().updateTaskflowAndAllTask(taskflow, Taskflow.STOPPED);

			retMsg = retMsg + ",新进度：" + TimeUtils.toChar(taskflow.getStatTime());
		} catch (Exception e) {
			e.printStackTrace();
			return "失败! 跳过流程失败";
		}
		return retMsg;
	}

	public HashMap<Integer, HashMap<String, String>> list() {
		return listall();
	}

	public HashMap<Integer, HashMap<String, String>> listall() {
		log.info("list ");
		List<Taskflow> list = null;
		HashMap<Integer, HashMap<String, String>> retMap = new HashMap<Integer, HashMap<String, String>>();
		try {
			list = FlowMetaData.getInstance().queryAllTaskflow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		for (Taskflow taskflow : list) {
			if (taskflow.getGroupID() == OutlineGroupID) {
				// 如果流程属于大纲组，则不在list里显示。
				continue;
			}
			map = field2Map(taskflow);

			retMap.put(taskflow.getTaskflowID(), map);
		}
		return retMap;
	}

	public HashMap<Integer, HashMap<String, String>> listTaskflowGroup(String groupName) {
		log.info("listTaskflowGroup ");
		List<Taskflow> list = null;
		HashMap<Integer, HashMap<String, String>> retMap = new HashMap<Integer, HashMap<String, String>>();
		try {
			TaskflowGroup group = FlowMetaData.getInstance().queryTaskflowGroup(groupName);
			list = FlowMetaData.getInstance().queryTaskflowInGroup(group.getGroupID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		for (Taskflow taskflow : list) {

			map = field2Map(taskflow);

			retMap.put(taskflow.getTaskflowID(), map);
		}
		return retMap;
	}

	public HashMap<Integer, HashMap<String, String>> listTask(String taskflowName) throws Exception {
		log.info("listTask " + " " + taskflowName);
		List<Task> list = null;
		HashMap<Integer, HashMap<String, String>> retMap = new HashMap<Integer, HashMap<String, String>>();
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflowName);
			list = FlowMetaData.getInstance().queryTaskList(taskflowID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("失败! listTask " + taskflowName);
		}
		HashMap<String, String> map = new HashMap<String, String>();
		for (Task task : list) {
			map = field2Map(task);
			retMap.put(task.getTaskID(), map);
		}
		return retMap;
	}

	public String application() throws Exception {
		log.info("application");
		try {
			return FlowMetaData.getInstance().querySysConfigValue("SYS_PROVINCE");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("unknow application");
		}
	}

	public String loadTaskflowInfo(Integer taskflowID) throws Exception {
		log.info("loadTaskflowInfo " + taskflowID);
		try {
			FlowMetaData.getInstance().loadTaskflowInfo(taskflowID);
			return "load taskflow info success";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("cann't load taskflow info");
		}
	}

	public String refreshTaskflow(String taskflowName) {
		log.info("refresh taskflow " + taskflowName);
		try {
			Taskflow taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowName);
			Integer taskflowID = taskflow.getTaskflowID();

			if (taskflowID == null) {
				return "失败! 没有该流程 " + taskflow;
			}

			if (taskflow.getStatus() == Taskflow.STOPPED) { // 流程处于stop状态，才可以执行刷新操作

				boolean isOk = FlowMetaData.getInstance().updateTaskflowFromDb(taskflowID);
				if (isOk) {
					return "refresh taskflow " + taskflow + " successed";
				} else {
					return "cann't refresh taskflow";
				}

			} else {
				return "失败! 流程[" + taskflow + "]必须处于停止状态";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "refresh taskflow " + taskflowName + " failed";
		}
	}

	public String refreshSysconfig() {
		log.info("refresh sysconfig");
		try {

			FlowMetaData.getInstance().refreshSysconfig();

			return "refresh sysconfig" + " successed";
		} catch (Exception e) {
			e.printStackTrace();
			return "refresh sysconfig" + " failed";
		}
	}

	public String queryTaskflowToRun(String taskflowName) {
		log.info("queryTaskflowToRun " + taskflowName);
		String result = "";
		try {
			Taskflow taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowName);
			// 当系统时间小于统计结束时间时。
			result = isSystemTimeLessThenStatEndTime(taskflow);
			// 存在未完成的父流程
			result = result + "" + hasNotCompeleteFatherTaskflow(taskflow);

			if (result.equals("")) {
				result = "当前流程可运行：流程当前进度时间在允许的运行时间范围内,同时不存在未完成的父流程。";
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "queryTaskflowToRun" + taskflowName + " failed";
		}
	}

	private String isSystemTimeLessThenStatEndTime(Taskflow taskflow) {

		if (taskflow.getStatTime() == null) {
			return "流程进度时间判断不通过，原因流程进度时间为空";
		}

		Date endTime = TimeUtils.getNewTime(taskflow.getStatTime(), taskflow.getStepType(), taskflow.getStep());
		if (new Date().getTime() < endTime.getTime()) {
			return ("流程进度时间判断不通过，原因统计结束时间:" + TimeUtils.toChar(endTime) + "晚于当前系统时间：" + TimeUtils.toChar(new Date()));
		} else {
			return "";

		}
	}

	private String hasNotCompeleteFatherTaskflow(Taskflow taskflow) {
		StringBuffer sb = new StringBuffer();
		// 从流程大纲中流程依赖关系来判断该流程是否可运行
		try {
			List<Taskflow> taskflowList = FlowMetaData.getInstance().queryUncompeleteAllFatherTaskflowList(taskflow);
			new ArrayList<Taskflow>();
			;
			if (taskflowList.size() > 0) {
				sb.append("存在未完成的父流程：");
			}
			for (int i = 0; i < taskflowList.size(); i++) {
				sb.append(taskflowList.get(i).getTaskflow());
				sb.append(i == taskflowList.size() - 1 ? "" : ",");
			}

		} catch (Exception e) {
		}
		return sb.toString();
	}

	public String version() {
		log.info("version ");

		String strVersion = "";
		try {
			strVersion = FlowMetaData.getInstance().querySysConfigValue("ETL_BAS_VERSION");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strVersion;
	}

	public String closeserver() {
		log.info("closeserver ");
		// 强制退出当前java进程
		System.exit(0);

		return "server closed";
	}

	public HashMap<String, String> getTaskflow(Integer taskflowID) throws Exception {
		log.debug("getTaskflow " + " " + taskflowID);
		Taskflow taskflow = null;
		try {
			taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowID);
		} catch (Exception e) {
			e.printStackTrace();
			// throw e;
		}
		HashMap<String, String> map = null;
		if (taskflow != null) {
			map = field2Map(taskflow);
		}
		return map;
	}

	public HashMap<String, String> getTaskflow(String taskflowName) throws Exception {
		log.debug("getTaskflow " + " " + taskflowName);
		Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflowName);
		return getTaskflow(taskflowID);
	}

	public HashMap<String, String> getTask(Integer taskID) throws Exception {
		log.debug("getTask " + " " + taskID);
		Task task = null;
		try {
			task = FlowMetaData.getInstance().queryTask(taskID);
		} catch (Exception e) {
			e.printStackTrace();
			// throw e;
		}
		HashMap<String, String> map = null;
		if (task != null) {
			map = field2Map(task);
		}
		return map;
	}

	/**
	 * 提供给C/C++插件的查询接口，用于查询任务是否已停止 *
	 * 
	 * @param taskflowName
	 *            taskName
	 * @return 0：未停止，1：已停止；
	 * @throws Exception
	 */
	public String isTaskflowStopped(String taskflowName, String taskName) throws Exception {
		log.debug("isTaskflowStopped " + taskflowName);
		String stopped = "0";
		try {
			Taskflow taskflow = FlowMetaData.getInstance().queryTaskflow(taskflowName);
			// 目前只要判断流程是否停止就可以了，不用判断任务。
			if (taskflow.getStatus() == Taskflow.STOPPED) {
				stopped = "1";
			}
		} catch (Exception e) {
			log.warn("isTaskflowStopped 异常:" + e);
			e.printStackTrace();
		}
		return stopped;
	}

	/*
	 * public String getSuspendOfTask(String taskflowName, String taskName) {
	 * String suspend = ""; log.debug("getSuspendOfTask " + " " + taskflowName +
	 * " " + taskName); try { Taskflow taskflow =
	 * FlowMetaData.getInstance().queryTaskflow(taskflowName); if(taskflow ==
	 * null) return "找不到taskflow:" + taskflowName; Task task = null; List<Task>
	 * list =
	 * FlowMetaData.getInstance().queryTaskList(taskflow.getTaskflowID());
	 * for(Task element : list){ if(element.getTask().equals(taskName)){ task =
	 * element; break; } } if(task == null){ return "找不到taskflow:" +
	 * taskflowName; } suspend = getSuspendOfTask(task.getTaskID()); } catch
	 * (Exception e) { e.printStackTrace(); //throw e; } return suspend; }
	 * 
	 * public String getSuspendOfTask(Integer taskID) { log.debug(
	 * "getSuspendOfTask " + " " + taskID); String suspend = "-1"; Task task =
	 * null; try { task = FlowMetaData.getInstance().queryTask(taskID); } catch
	 * (Exception e) { e.printStackTrace(); //throw e; } if(task != null){
	 * suspend = task.getSuspend() + ""; } return suspend; }
	 */
	public HashMap<String, String> getTaskType(String taskTypeName) throws Exception {
		log.debug("getTaskType " + " " + taskTypeName);
		TaskType taskType = FlowMetaData.getInstance().queryTaskType(taskTypeName);
		HashMap<String, String> map = null;
		if (taskType != null) {
			map = field2Map(taskType);
		}
		return map;
	}

	public HashMap<String, String> getStepType(String stepType) throws Exception {
		log.debug("getStepType " + " " + stepType);
		StepType step = FlowMetaData.getInstance().queryStepType(stepType);
		HashMap<String, String> map = null;
		if (step != null) {
			map = field2Map(step);
		}
		return map;
	}

	public HashMap<String, String> getConnInfo() {
		log.debug("getConnInfo ");
		ConnInfo connInfo = null;
		try {
			connInfo = FlowMetaData.getInstance().getConnInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> map = null;
		if (connInfo != null) {
			map = field2Map(connInfo);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> getTaskflowGroups() {
		log.debug("getTaskflowGroups ");
		List<TaskflowGroup> list = null;
		try {
			list = FlowMetaData.getInstance().queryTaskflowGroupList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		if (list != null) {
			Collections.sort(list);
			for (TaskflowGroup group : list) {
				map.put(Integer.toString(group.getGroupID()), group.getGroupName());
			}
		}
		return map;
	}

	public HashMap<Integer, HashMap<String, String>> getTaskflowOfGroup(Integer groupID) {
		log.debug("getTaskflowOfGroup " + " " + groupID);
		List<Taskflow> flowList = null;
		HashMap<Integer, HashMap<String, String>> retMap = new HashMap<Integer, HashMap<String, String>>();
		try {
			flowList = FlowMetaData.getInstance().queryTaskflowInGroup(groupID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		if (flowList != null) {
			for (Taskflow taskflow : flowList) {
				map = field2Map(taskflow);
				retMap.put(taskflow.getTaskflowID(), map);
			}
		}
		return retMap;
	}

	public HashMap<Integer, HashMap<String, String>> getTaskOfTaskflow(Integer taskflowID) {
		log.debug("getTaskOfTaskflow " + " " + taskflowID);
		List<Task> taskList = null;
		HashMap<Integer, HashMap<String, String>> retMap = new HashMap<Integer, HashMap<String, String>>();
		try {
			taskList = FlowMetaData.getInstance().queryTaskList(taskflowID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> map = null;
		if (taskList != null) {
			for (Task task : taskList) {
				map = field2Map(task);
				retMap.put(task.getTaskID(), map);
			}
		}
		return retMap;
	}

	public HashMap<Integer, HashMap<String, String>> getLinkOfTaskflow(Integer taskflowID) {
		log.debug("getLinkOfTaskflow " + " " + taskflowID);
		List<Link> linkList = null;
		HashMap<Integer, HashMap<String, String>> retMap = new HashMap<Integer, HashMap<String, String>>();
		try {
			linkList = FlowMetaData.getInstance().queryLinkList(taskflowID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> map = null;
		if (linkList != null) {
			for (Link link : linkList) {
				map = field2Map(link);
				retMap.put(link.getLinkID(), map);
			}
		}
		return retMap;
	}

	/**
	 * 判断是否有权限
	 * 
	 * @param userID
	 * @param name
	 * @return
	 */
	public Boolean isPermit(String userID, String name) {
		try {
			return FlowMetaData.getInstance().isPermit(userID, name);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private HashMap<String, String> field2Map(Object c) {
		HashMap<String, String> map = new HashMap<String, String>();
		Method[] m = c.getClass().getMethods();
		String methodString = "";
		for (Method method : m) {
			methodString = method.getName();
			if (!methodString.startsWith("get")) {
				continue;
			}
			try {
				map.put((methodString.charAt(3) + "").toLowerCase() + methodString.substring(4),
						filter(method.invoke(c, new Object[] {})));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	// 简单过滤
	private String filter(Object o) {
		String s = "";
		if (o == null) {
			s = "";
			return s;
		}
		if (o instanceof java.util.Date) {
			s = TimeUtils.toChar((java.util.Date) o);
		} else {
			s = o.toString();
		}
		return s;
	}

	private boolean isTaskflowThreadAlive(String taskflowName) {

		boolean isAlive = false;

		TaskflowThread taskflowContext = FlowEngine.taskflowThreadPool.get(taskflowName);
		log.info("线程:" + taskflowContext);
		if (taskflowContext != null && !taskflowContext.isAlive()) {
			log.info("线程是否存活:" + taskflowContext.isAlive());
			FlowEngine.taskflowThreadPool.remove(taskflowName);// 将线程从缓存中移除 by chenhaitao
			//isAlive = true;
		}
		return isAlive;
	}

	public String submitTaskflow(String flowJsonStr, String tasksJsonStr, String taskAttributeJsonStr,
			String linkJsonStr) {

		log.info("submitTaskflow()");
		log.info("flowJsonStr=" + flowJsonStr);
		log.info("tasksJsonStr=" + tasksJsonStr);
		log.info("taskAttributeJsonStr=" + taskAttributeJsonStr);
		log.info("linkJsonStr=" + linkJsonStr);

		try {
			long t1 = new Date().getTime();
			final Taskflow flow = (Taskflow) JSONObject.toBean(JSONObject.fromObject(flowJsonStr), Taskflow.class);

			Task[] tasks = (Task[]) JSONArray.toArray(JSONArray.fromObject(tasksJsonStr), Task.class);

			TaskAttribute[] attributes = (TaskAttribute[]) JSONArray.toArray(JSONArray.fromObject(taskAttributeJsonStr),
					TaskAttribute.class);

			Link[] links = (Link[]) JSONArray.toArray(JSONArray.fromObject(linkJsonStr), Link.class);
			long t2 = new Date().getTime();
			log.info("josn 转换用时 ====>" + (t2 - t1));
			FlowMetaData.getInstance().insert(flow, "admin");
			for (Task task : tasks) {
				FlowMetaData.getInstance().insert(task);
			}
			for (TaskAttribute attribute : attributes) {
				FlowMetaData.getInstance().insert(attribute);
			}
			for (Link link : links) {
				FlowMetaData.getInstance().insert(link);
			}
			long t3 = new Date().getTime();
			log.info("存入内存用时 ====>" + (t3 - t2));
			/*
			 * FlowMetaData.getInstance().saveTaskflowInfo(flow.getTaskflowID())
			 * ; long t4 = new Date().getTime(); log.info("存入数据库用时 ====>" + (t4
			 * - t3)); this.startTaskflow(flow.getTaskflow()); long t5 = new
			 * Date().getTime(); log.info("启动流程用时 ====>" + (t5 - t4));
			 */
			new Thread() {
				public void run() {
					long t3 = new Date().getTime();
					try {
						FlowMetaData.getInstance().saveTaskflowInfo(flow.getTaskflowID());
					} catch (MetaDataException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					long t4 = new Date().getTime();
					log.info("存入数据库用时 ====>" + (t4 - t3));
					//startTaskflow(flow.getTaskflow());
					long t5 = new Date().getTime();
					log.info("启动流程用时 ====>" + (t5 - t4));
					MasterSlaveJobSummary jobSummary = new MasterSlaveJobSummary();
					Vector<Object> populateParam = new Vector<Object>();
					populateParam.add(flowJsonStr);
					jobSummary.setFlowId(flow.getTaskflow());
					/*jobSummary.setStatus("Startup");
					jobSummary.setTaskId("004taskid");*/
					jobSummary.setJobOperation("Start");
					//jobSummary.setJobOperation("Pause");
					jobSummary.setGroupName(flow.getTaskflow());
					jobSummary.setExcuteStatus("Submit");
					jobSummary.setParams(populateParam);
					zkDataUtil(jobSummary);
				}

			}.start();
			return "Y";
		} catch (Exception e) {
			log.info("submitTaskflow  :", e);
			return "F";
		}
	}

	/**
	 * TaskHistory, 返回最新100条
	 */
	public String queryTaskHistory(Integer flowId) {
		String result = null;
		try {
			List<TaskHistory> historys = FlowMetaData.getInstance().getTaskHistory(flowId);
			if (historys == null)
				return "F";
			result = JSONArray.fromObject(historys).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * TaskHistory, 返回最新100条
	 */
	public String queryTaskHistory(String flowId) {
		return this.queryTaskHistory(Integer.valueOf(flowId));
	}

	/**
	 * 导入数数据信息rpc接口
	 * 
	 * Title: createHiveTable Description:
	 * 
	 * @param interfaceMap
	 * @return
	 */
	public String executeShell(Map<String, String> interfaceMap) {
		Boolean isOK = false;
		String result = "FAIL";
		ImportPropContext importPropContext = new ImportPropContext();
		try {
			isOK = importPropContext.call(interfaceMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isOK) {
			result = "SUCCESS";
		}
		return result;

	}

	/**
	 * 
	 * Title: executeTaskStatus Description:
	 * 
	 * @param interfaceMap
	 * @return
	 */
	public String executeTaskStatus(Map<String, String> interfaceMap) {
		Boolean isOK = false;
		String result = "FAIL";
		int taskId = Integer.parseInt(interfaceMap.get("taskId"));
		int status = Integer.parseInt(interfaceMap.get("status"));
		try {
			isOK = FlowMetaData.getInstance().updateTaskStatus(taskId, status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isOK) {
			result = "SUCCESS";
		}
		return result;

	}

	/**
	 * 
	 * Title: executeTaskStatus Description: 停止恢复接口 hqw 20170830
	 * 
	 * @param interfaceMap
	 * @return
	 */
	public String executeTaskFlowStatus(Map<String, String> interfaceMap) {
		Boolean isOK = false;
		String result = "FAIL";
		int taskId = Integer.parseInt(interfaceMap.get("taskId"));
		int status = Integer.parseInt(interfaceMap.get("status"));
		try {
			isOK = FlowMetaData.getInstance().updateTaskFlowStatus(taskId, status);

			if (isOK) {
				if (Task.READY == status && !isTaskflowThreadAlive(taskId + "")) {
					result = startTaskflow(taskId + "");
				} else if (Task.STOPPED == status) {
					result = stopTaskflow(taskId + "");
				}
				// result = "SUCCESS";
			}
		} catch (Exception e) {
			result = e.getMessage();
			e.printStackTrace();
		}
		return result;

	}
	
	public void zkDataUtil(MasterSlaveJobSummary jobSummary){
		if(client == null){
			String connectStr = config.getZookeeperAddresses();
			client = CuratorFrameworkFactory.newClient(connectStr, DEFAULT_SESSION_TIMEOUT_MS, DEFAULT_CONNECTION_TIMEOUT_MS, DEFAULT_RETRY_POLICY);
			client.start();
		}
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
	
	public String listTaskstr(String taskflowName) throws Exception{
		log.info("listTask " + " " + taskflowName);
		List<Task> list = null;
		try {
			Integer taskflowID = FlowMetaData.getInstance().getTaskflowIDbyName(taskflowName);
			list = FlowMetaData.getInstance().queryTaskList(taskflowID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("失败! listTask " + taskflowName);
		}
		return JSON.toJSONString(list);
	}
	
	public String listallstr(){
		log.info("list ");
		List<Taskflow> list=null;
		List<Taskflow> relist = new ArrayList<Taskflow>();
		try {
		  list = FlowMetaData.getInstance().queryAllTaskflow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(list!=null&&list.size()>0)
		for(Taskflow taskflow : list){
			if(taskflow.getGroupID()== OutlineGroupID){
				//如果流程属于大纲组，则不在list里显示
				continue;
			}
			relist.add(taskflow);
		}
		return JSON.toJSONString(relist);
//		return relist;
	}
	public String liststr(){
		return listallstr();
	} 
}
