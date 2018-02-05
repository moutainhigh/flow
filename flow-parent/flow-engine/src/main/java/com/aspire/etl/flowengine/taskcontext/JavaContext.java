/*
 * JavaContext.java
 *
 * Created on 2008-1-25 17:08:42 by luoqi
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
package com.aspire.etl.flowengine.taskcontext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskHistory;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowengine.TaskRunInterfaceInfo;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.Utils;

/**
 * 执行JAVA编写的任务上下文环境。动态加载java class,
 * 用java编写的ETL插件, 必须有以下入口方法.
 *  public int execute(Map map)
 *  传给ETL插件的map,包含有taskflow和task的基本信息,系统参数信息，还有全部的任务动态参数
 * 
 * @author 罗奇
 * @since 2008-3-24 
 */
public class JavaContext {
	TaskRunInterfaceInfo taskRunInfo = null;
	Long historyId = null;
	Logger log  = null;
	
	public JavaContext(TaskRunInterfaceInfo info) {
		super();
		// TODO Auto-generated constructor stub
		this.taskRunInfo = info;
		log = Logger.getLogger(taskRunInfo.getTaskflowName());
	}
	
	public JavaContext(TaskRunInterfaceInfo info, Long historyId) {
		super();
		// TODO Auto-generated constructor stub
		this.taskRunInfo = info;
		this.historyId = historyId;
		log = Logger.getLogger(taskRunInfo.getTaskflowName());
	}


	public Boolean call() {
		boolean isOK = false;
		// TODO Auto-generated method stub
		FlowMetaData flowMetaData;
		int retValue = Task.FAILED;
		
		String classDir = taskRunInfo.getEnginePluginDir();
		String className = taskRunInfo.getEnginePlugin();
	
		try {		
			Class pluginClass = Utils.loadClassByName(classDir, className);
			Object pluginObject = null;
			
			Constructor constructor = pluginClass.getConstructor();
			pluginObject = constructor.newInstance(new Object[] {});

			Method mLoadValue = pluginClass.getMethod("execute",
					new Class[] { java.util.Map.class });
			retValue = (Integer) mLoadValue.invoke(pluginObject,
					new Object[] { taskRunInfo.getInterfaceMap() });
			
			isOK = (retValue==Task.SUCCESSED)?true:false;

		} catch (MalformedURLException e1) {
			log.error(e1+"调用"+classDir+" 下的"+className+"失败！",e1);
		} catch (ClassNotFoundException e1) {
			log.error(e1+"从目录"+classDir+" 加载"+className+"失败！",e1);
		} catch (InstantiationException e1) {
			log.error(e1+"调用"+classDir+" 下的"+className+"失败！",e1);
		} catch (IllegalAccessException e1) {
			log.error(e1+"调用"+classDir+" 下的"+className+"失败！",e1);
		} catch (IllegalArgumentException e1) {
			log.error(e1+"调用"+classDir+" 下的"+className+"失败！",e1);
		} catch (InvocationTargetException e1) {//线程强行中止会抛出该异常
			//log.error(e1+"调用"+classDir+" 下的"+className+"失败！",e1);
		} catch (IllegalMonitorStateException e1) {
			//线程强行中止会抛出该异常
		} catch (NoSuchMethodException e1) {
			log.error(e1+"调用"+classDir+" 下的"+className+"失败！",e1);
		} catch (Exception e1) {
			log.error(e1+"调用"+classDir+" 下的"+className+"失败！",e1);
		}
		
		try {
			flowMetaData = FlowMetaData.getInstance();

			// 用执行结果更新任务的状态，
			//TODO:要判断retValue是否是合法的任务状态
			flowMetaData.updateTaskStatus(taskRunInfo.getTaskID(), retValue);
			flowMetaData.updateRunEndTimeOfTask(taskRunInfo.getTaskID(), new Date());
			//flowMetaData.updateRedoFlagOfTaskflow(Integer.parseInt(taskRunInfo.getTaskflowName()), Taskflow.REDO_NO);//hqw 执行完不管是不是重做都把状态置成no
			flowMetaData.updateRedoFlagOfTaskflow(taskRunInfo.getTaskflowID(), Taskflow.REDO_NO);//chenhaitao
			String taskHistoryStatus = "";
			if(isOK) taskHistoryStatus = TaskHistory.COMPLETE;
			else taskHistoryStatus = TaskHistory.FAILED;
			
			/*String file = (String) taskRunInfo.getInterfaceMap().get("STATTIME");
			
			log.info("taskHistoryStatus = "+taskHistoryStatus+" retValue="+retValue + "file=" + file);
			flowMetaData.updateHistoryStatus(historyId, file, taskHistoryStatus);*/
			
		} catch (MetaDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return isOK;
	}

}
