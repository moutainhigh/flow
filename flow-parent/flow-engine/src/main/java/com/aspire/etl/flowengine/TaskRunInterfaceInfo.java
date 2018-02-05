/*
 * TaskRunInterfaceInfo.java
 *
 * Created on 2008-2-19 21:12:42 by luoqi
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskAttribute;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.TimeUtils;

/**
 * 组装调用任务插件的接口信息
 * 
 * @author 罗奇
 * @since 2008-3-19
 *        <p>
 *        提供C++程序需要的命令行接口字符串，
 * @author 罗奇
 * @since 2008-3-26
 *        <p>
 *        增加传给java程序的接口map
 * @author wuzhuokun
 * @since 2008-5-8
 *        <p>
 *        增加动态参数用变量,另外参数只传系统参数
 */
public class TaskRunInterfaceInfo {
	String enginePlugin = "";

	Integer taskID;
	Logger log = null;
	String taskflowName;
	Integer taskflowID;
	String enginePluginDir;

	FlowMetaData flowMetaData = null;

	Map<String, Object> interfaceMap = new HashMap<String, Object>();

	public TaskRunInterfaceInfo(FlowMetaData flowMetaData, Taskflow taskflow,
			Task task) throws Exception {
		this.flowMetaData = flowMetaData;
		this.log = Logger.getLogger(taskflow.getTaskflow());
		// 将与流程相关的信息放入接口map
		interfaceMap.put("REDO_FLAG", taskflow.getRedoFlag() + "");

		taskflowName = taskflow.getTaskflow();
		interfaceMap.put("TASKFLOW", taskflowName);
		taskflowID = taskflow.getTaskflowID();//chenhaitao
		Date endtime = TimeUtils.getNewTime(taskflow.getStatTime(), taskflow
				.getStepType(), taskflow.getStep());
		
		String statTime = TimeUtils.toChar(taskflow.getStatTime(),
		"yyyyMMddHHmmss");
		log.info("STATTIME:" + statTime);
		interfaceMap.put("STATTIME", statTime);

		interfaceMap
				.put("ENDTIME", TimeUtils.toChar(endtime, "yyyyMMddHHmmss"));

		interfaceMap.put("FILELOGLEVEL", taskflow.getFileLogLevel() + "");

		interfaceMap.put("DBLOGLEVEL", taskflow.getDbLogLevel() + "");

		taskID = task.getTaskID();
		// 将与任务相关的信息放入接口map
		interfaceMap.put("TASKID", taskID + "");

		interfaceMap.put("TASK_TYPE", task.getTaskType());

		interfaceMap.put("TASK", task.getTask());

		interfaceMap.put("ALERT_ID", task.getAlertID());

		interfaceMap.put("PERFORMANCE_ID", task.getPerformanceID());
		/*interfaceMap.put("host", "192.168.98.141");
		interfaceMap.put("username", "ftpuser");
		interfaceMap.put("password", "071359cht");
		interfaceMap.put("port", 21);*/
		/*interfaceMap.put("basePath", "/home/ftpuser/www/images/20171016/");
		interfaceMap.put("filePath", "/Users/chenhaitao/Desktop");*/
		/*interfaceMap.put("taskFlowId", "7");
		interfaceMap.put("startTime", "2017-10-13 16:56:48");*/
		
		//添加任务流的周期类型
		String taskFlowStepType =  taskflow.getStepType();
		interfaceMap.put("STEP_TYPE", taskFlowStepType);
		if("AD-HOC".equals(taskflow.getMemo())) interfaceMap.put("AD_HOC", "AD-HOC");
        
		enginePlugin = flowMetaData.queryTaskType(task.getTaskType())
				.getEnginePlugin();
		enginePluginDir = flowMetaData.queryTaskType(task.getTaskType())
				.getEnginePluginJar();
		/**
		 * 如果插件是Scp那么需要根据taskFlowId去查询出文件名称
		 */
		/*if(enginePlugin.equals("com.aspire.etl.plugins.Scp")){
			flowMetaData.getTaskHistoryByFlowIdAndStartTime(map);
		}*/
		interfaceMap.put("TASK_EXEC", enginePlugin);

		interfaceMap.put("TASK_EXEC_TYPE", flowMetaData.queryTaskType(
				task.getTaskType()).getEnginePluginType());
		log.info("FLOW_ID:" + task.getTaskflowID()+"");
		interfaceMap.put("FLOW_ID", task.getTaskflowID()+"");
		
		// 将任务的动态参数都放到接口map中 ，要防止与系统参数重名。
		
		interfaceMap.putAll(flowMetaData
				.queryTaskAttributeDynMap(task.getTaskID(),statTime));
	}

	public Map<String, Object> getInterfaceMap() {
		// 将系统参数都都放到接口map中
		interfaceMap.putAll(flowMetaData.querySysConfigMap());
		

		return interfaceMap;
	}

	public String toCmdLineParamStr() {
		StringBuffer paramStrBuffer = new StringBuffer();
		// paramStrBuffer.append(" ");
		Set<String> keySet = interfaceMap.keySet();

		int i = 1;
		// paramStrBuffer.append("\"");

		// 可变的动态参数取sysconfig
		String value = "";
		for (String key : keySet) {
			
				paramStrBuffer.append(key.toUpperCase());
				paramStrBuffer.append("=");
				value = (String)interfaceMap.get(key);
				if (value != null && value.startsWith("${")) {// 有动态参数
					value = value.substring(2, value.length() - 1);
					paramStrBuffer.append(flowMetaData
							.querySysConfigValue(value));
				} else {
					paramStrBuffer.append(value);
				}

				paramStrBuffer.append("#");
			

		}

		// 将系统参数放在后面，这样这命令行上将看不到这部分。
		// 取特定的SYS
		Map<String, String> sysConfigMap = flowMetaData
				.querySysConfigMap("SYS");

		Set<String> sysConfigKeySet = sysConfigMap.keySet();

		for (String key : sysConfigKeySet) {
			paramStrBuffer.append(key.toUpperCase());
			paramStrBuffer.append("=");
			paramStrBuffer.append(sysConfigMap.get(key));

			if (i < sysConfigKeySet.size()) {
				paramStrBuffer.append("#");
			}
			i++;
		}

		// paramStrBuffer.append("\"");

		return paramStrBuffer.toString();
	}

	public String getValue(String key) {		
		for (String elem : interfaceMap.keySet()) {
			if (elem.equalsIgnoreCase(key)) {
				return (String) interfaceMap.get(elem);
			}
		}
		return "";
	}

	public String getEnginePlugin() {
		return enginePlugin;
	}

	public void setEnginePlugin(String enginePlugin) {
		this.enginePlugin = enginePlugin;
	}

	public String getTaskflowName() {
		return taskflowName;
	}

	public void setTaskflowName(String taskflowName) {
		this.taskflowName = taskflowName;
	}

	public Integer getTaskID() {
		return taskID;
	}

	public void setTaskID(Integer taskID) {
		this.taskID = taskID;
	}

	public String getEnginePluginDir() {
		return this.enginePluginDir;
	}

	public Integer getTaskflowID() {
		return taskflowID;
	}

	public void setTaskflowID(Integer taskflowID) {
		this.taskflowID = taskflowID;
	}
	
}
