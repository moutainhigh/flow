package com.aspire.etl.flowmonitor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.StepType;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowdefine.TaskflowGroup;
import com.aspire.etl.tool.TimeUtils;

public class Xmlrpc {
	
	public static String IP = "";
	public static String PORT = "";
	public static String USERNAME = "";
	public static String PASSWORD = "";

	static Logger log;

	private static Xmlrpc xmlrpcClinet = null;

	private static XmlRpcClient client = null;

	private Xmlrpc() {
//		init();
	}

	public static Xmlrpc getInstance() {
		if (xmlrpcClinet == null) {
			xmlrpcClinet = new Xmlrpc();
		}
		return xmlrpcClinet;
	}

	private void init() {
		log = Logger.getLogger("FlowMonitor");
		// create configuration
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL("http://"
					+ IP
					+ ":"
					+ PORT + "/xmlrpc"));
			config.setEnabledForExtensions(true);
			config.setConnectionTimeout(60 * 1000);
			config.setReplyTimeout(60 * 1000);
			config.setBasicUserName(USERNAME);
			config.setBasicPassword(PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e);
		}
		Xmlrpc.client = new XmlRpcClient();
		// use Commons HttpClient as transport
		client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
		// set configuration
		client.setConfig(config);
	}
	
	public void init(String ip, String port, String username, String password) {
		IP = ip;
		PORT = port;
		USERNAME = username;
		PASSWORD = password;
		init();
	}
	
	public boolean testConn(){
		//测试连接
		try{
			client.execute("command.alive", new Object[0]);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public String startTaskflow(String taskflowName) throws XmlRpcException {
		Object[] params = new Object[] { taskflowName };
		return (String) client.execute("command.startTaskflow", params);
	}

	public String startAllTaskflow() throws XmlRpcException {
		Object[] params = new Object[0];
		return (String) client.execute("command.startAllTaskflow", params);
	}

	public String stopTaskflow(String taskflowName) throws XmlRpcException {
		Object[] params = new Object[] { taskflowName };
		return (String) client.execute("command.stopTaskflow", params);
	}
	
	public String suspendTaskflow(String taskflowName) throws XmlRpcException {
		Object[] params = new Object[]{taskflowName,new Boolean(true)};
		return (String) client.execute("command.suspendTaskflow", params);
	}
	
	public String cancelSuspendTaskflow(String taskflowName) throws XmlRpcException {
		Object[] params = new Object[]{taskflowName,new Boolean(false)};
		return (String) client.execute("command.suspendTaskflow", params);
	}
	
	public String stopAllTaskflow() throws XmlRpcException {
		Object[] params = new Object[0];
		return (String) client.execute("command.stopAllTaskflow", params);
	}
	
	public String suspendAllTaskflow() throws XmlRpcException {
		Object[] params = new Object[]{new Boolean(true)};
		return (String) client.execute("command.suspendAllTaskflow", params);
	}
	
	public String cancelSuspendAllTaskflow() throws XmlRpcException {
		Object[] params = new Object[]{new Boolean(false)};
		return (String) client.execute("command.suspendAllTaskflow", params);
	}
	
	public String wakeupTaskflow(String taskflowName) throws XmlRpcException {
		Object[] params = new Object[] { taskflowName };
		return (String) client.execute("command.wakeupTaskflow", params);
	}
	
	public String startTaskflowGroup(String groupName) throws XmlRpcException {
		Object[] params = new Object[] { groupName };
		return (String) client.execute("command.startTaskflowGroup", params);
	}
	
	//跳过流程
	public String skipTaskflow(String taskflowName)throws XmlRpcException {
		Object[] params = new Object[] {taskflowName};
		return (String) client.execute("command.skipTaskflow", params);
	}
	
	//跳过任务
	public String skipTask(String taskflowName, String taskName)throws XmlRpcException {
		Object[] params = new Object[] { taskflowName, taskName};
		return (String) client.execute("command.skipTask", params);
	}
	//禁用流程任务
	public String suspendTask(String taskflowName, String taskName)throws XmlRpcException {
		Object[] params = new Object[]{taskflowName,taskName,new Boolean(true)};
		return (String) client.execute("command.suspendTask", params);
	}
	
	//启用流程任务
	public String cancelSuspendTask(String taskflowName, String taskName)throws XmlRpcException {
		Object[] params = new Object[]{taskflowName,taskName,new Boolean(false)};
		return (String) client.execute("command.suspendTask", params);
	}

	public String stopTaskflowGroup(String groupName) throws XmlRpcException {
		Object[] params = new Object[] { groupName };
		return (String) client.execute("command.stopTaskflowGroup", params);
	}

	// 线程数
	public String updateThreadnum(int taskflowID, int threadnum)
			throws XmlRpcException {
		Object[] params = new Object[] { new Integer(taskflowID),
				new Integer(threadnum) };
		return (String) client.execute("command.updateThreadnum", params);
	}
	
	public String resetTaskflow(String taskflow) throws XmlRpcException {
		Object[] params = new Object[]{taskflow};
		return (String) client.execute("command.resetTaskflow", params);
	}

	// 流程进度时间
	public String updateStatTime(int taskflowID, Date statTime)
			throws XmlRpcException {
		Object[] params = new Object[] { new Integer(taskflowID), statTime };
		return (String) client.execute("command.updateStatTime", params);
	}
	
	public String forceUpdateTime(String taskflow, Date statTime) 	throws XmlRpcException {
		Object[] params = new Object[] {taskflow, statTime };
		return (String) client.execute("command.forceUpdateStatTime", params);
		 
	}

	// 场景时间
	public String updateSceneStatTime(int taskflowID, Date sceneStatTime)
			throws XmlRpcException {
		if (sceneStatTime != null) {
			Object[] params = new Object[] { new Integer(taskflowID),
					sceneStatTime };
			return (String) client.execute("command.updateSceneStatTime",
					params);
		} else {
			Object[] params = new Object[] { new Integer(taskflowID) };
			return (String) client.execute("command.updateSceneStatTime",
					params);
		}
	}

	// 重做开始时间
	public String updateRedoStartTime(int taskflowID, Date redoStartTime)
			throws XmlRpcException {
		if (redoStartTime != null) {
			Object[] params = new Object[] { new Integer(taskflowID),
					redoStartTime };
			return (String) client.execute("command.updateRedoStartTime",
					params);
		} else {
			Object[] params = new Object[] { new Integer(taskflowID) };
			return (String) client.execute("command.updateRedoStartTime",
					params);
		}
	}

	// 重做结束时间
	public String updateRedoEndTime(int taskflowID, Date redoEndTime)
			throws XmlRpcException {
		if (redoEndTime != null) {
			Object[] params = new Object[] { new Integer(taskflowID),
					redoEndTime };
			return (String) client.execute("command.updateRedoEndTime", params);
		} else {
			Object[] params = new Object[] { new Integer(taskflowID) };
			return (String) client.execute("command.updateRedoEndTime", params);
		}
	}

	// 是否重做
	public String updateRedoFlag(int taskflowID, int redoFlag)
			throws XmlRpcException {
		Object[] params = new Object[] { new Integer(taskflowID),
				new Integer(redoFlag) };
		return (String) client.execute("command.updateRedoFlag", params);
	}
	
	
	
	//重做
	public String redo(Integer taskflowID, Date redoStartTime, Date redoEndTime) throws XmlRpcException {
		
		Object[] params = new Object[] { new Integer(taskflowID), redoStartTime, redoEndTime };
		return (String) client.execute("command.redo", params);
	}
	
	
//	重做
	public String redo(String taskflow, Date redoStartTime, Date redoEndTime) throws XmlRpcException {
		
	/*	Object[] params1 = new Object[] {taskflow};
		client.execute("command.stopTaskflow", params1);
		client.execute("command.resetTaskflow", params1);*/
		
		Object[] params = new Object[] { taskflow, redoStartTime, redoEndTime };
		return (String) client.execute("command.redo", params);
	}

	// 文件日志级别
	public String updateFileLogLevel(int taskflowID, String fileLogLevel)
			throws XmlRpcException {
		Object[] params = new Object[] { new Integer(taskflowID), fileLogLevel };
		return (String) client.execute("command.updateFileLogLevel", params);
	}

	// 数据库日志级别
	public String updateDbLogLevel(int taskflowID, String dbLogLevel)
			throws XmlRpcException {
		Object[] params = new Object[] { new Integer(taskflowID), dbLogLevel };
		return (String) client.execute("command.updateDbLogLevel", params);
	}
	
	// 获取流程组
	public List<TaskflowGroup> queryTaskflowGroupList() throws XmlRpcException{
		Object[] params = new Object[]{};
		HashMap<String, String> map = (HashMap<String, String>)client.execute("command.getTaskflowGroups", params);
		List<TaskflowGroup> list = mapToGroup(map);
		return list;
	}
	
	// 取组内流程
	public List<Taskflow> queryTaskflowInGroup(Integer groupID) throws XmlRpcException{
		Object[] params = new Object[]{groupID};
		HashMap<Integer, HashMap<String, String>> map = (HashMap<Integer, HashMap<String, String>>)client.execute("command.getTaskflowOfGroup", params);
		List<Taskflow> list = mapToTaskflowNew(map);
		return list;		
	}
	
	// 取流程下的任务
	public List<Task> queryTaskList(Integer taskflowID) throws XmlRpcException{
		Object[] params = new Object[]{taskflowID};
		HashMap<Integer, HashMap<String, String>> map = (HashMap<Integer, HashMap<String, String>>)client.execute("command.getTaskOfTaskflow", params);
		List<Task> list = mapToTaskNew(map);
		return list;
	}
	
	// 取流程的link
	public List<Link> queryLinkList(Integer taskflowID) throws XmlRpcException{
		Object[] params = new Object[]{taskflowID};
		HashMap<Integer, HashMap<String, String>> map = (HashMap<Integer, HashMap<String, String>>)client.execute("command.getLinkOfTaskflow", params);
		List<Link> list = mapToLink(map);
		return list;
	}
	
	// 取流程
	public Taskflow queryTaskflow(Integer taskflowID) throws XmlRpcException{
		Object[] params = new Object[]{taskflowID};
		HashMap<String, String> map = (HashMap<String, String>)client.execute("command.getTaskflow", params);
		return mapToTaskflow(map);
	}
	
	//取边接信息
	public HashMap<String, String> queryConnInfo() throws XmlRpcException{
		Object[] params = new Object[]{};
		HashMap<String, String> map = (HashMap<String, String>)client.execute("command.getConnInfo", params);
		return map;
	}
	
	//取流程
	public Taskflow queryTaskflow(String taskflowName) throws XmlRpcException{
		Object[] params = new Object[]{taskflowName};
		HashMap<String, String> map = (HashMap<String, String>)client.execute("command.getTaskflow", params);
		return mapToTaskflow(map);
	}
	
	//判断权限
	public Boolean isPermit(String name) throws XmlRpcException{
		Object[] params = new Object[]{USERNAME, name};
		return (Boolean)client.execute("command.isPermit", params);
	}
	
	//重新加载流程信息
//	public String loadTaskflowInfo(Integer taskflowID) throws XmlRpcException{
//		Object[] params = new Object[]{taskflowID};
//		String info = (String)client.execute("command.loadTaskflowInfo", params);
//		return info;
//	}
	
	//取任务
	public Task queryTask(Integer taskID) throws XmlRpcException{
		Object[] params = new Object[]{taskID};
		HashMap<String, String> map = (HashMap<String, String>)client.execute("command.getTask", params);
		return mapToTask(map);
	}
	
	//取任务类型
	public TaskType queryTaskType(String TaskTypeName) throws XmlRpcException{
		TaskType taskType = null;
		Object[] params = new Object[]{TaskTypeName};
		HashMap<String, String> map = (HashMap<String, String>)client.execute("command.getTaskType", params);
		return mapToTaskType(map);
	}
	
	//取任务类型
	public StepType queryStepType(String stepType) throws XmlRpcException{
		TaskType taskType = null;
		Object[] params = new Object[]{stepType};
		HashMap<String, String> map = (HashMap<String, String>)client.execute("command.getStepType", params);
		return mapToStepType(map);
	}

	private List<Link> mapToLink(HashMap<Integer, HashMap<String, String>> map) {
		List<Link> list = new ArrayList<Link>();
		Link link = null;
		for(HashMap<String, String> linkMap : map.values()){
			link = new Link(new Integer(linkMap.get("linkID")), new Integer(linkMap.get("fromTaskID")), new Integer(linkMap.get("toTaskID")));
			list.add(link);
		}
		return list;
	}

	private List<Task> mapToTaskNew(HashMap<Integer, HashMap<String, String>> map) {
		List<Task> list = new ArrayList<Task>();
		Task task = null;
		for(HashMap<String, String> taskMap : map.values()){
			task = new Task(new Integer(taskMap.get("taskID")), new Integer(taskMap.get("taskflowID")),
					(String)(taskMap.get("task")), (String)(taskMap.get("taskType")),
					new Integer(taskMap.get("status")), new Integer(taskMap.get("plantime")),
					new Integer(taskMap.get("isRoot")), new Integer(taskMap.get("suspend")),
					(String)(taskMap.get("description")), (String)(taskMap.get("performanceID")),
					(String)(taskMap.get("alertID")), new Integer(taskMap.get("xPos")),
					new Integer(taskMap.get("yPos")));
			task.setRunStartTime(TimeUtils.toDate((String)(taskMap.get("runStartTime"))));
			task.setRunEndTime(TimeUtils.toDate((String)(taskMap.get("runEndTime"))));
			list.add(task);
		}
		return list;
	}
	
	private Task mapToTask(HashMap<String, String> taskMap) {
		Task task = null;
		task = new Task(new Integer(taskMap.get("taskID")), new Integer(taskMap.get("taskflowID")),
					(String)(taskMap.get("task")), (String)(taskMap.get("taskType")),
					new Integer(taskMap.get("status")), new Integer(taskMap.get("plantime")),
					new Integer(taskMap.get("isRoot")), new Integer(taskMap.get("suspend")),
					(String)(taskMap.get("description")), (String)(taskMap.get("performanceID")),
					(String)(taskMap.get("alertID")), new Integer(taskMap.get("xPos")),
					new Integer(taskMap.get("yPos")));
		task.setRunStartTime(TimeUtils.toDate((String)(taskMap.get("runStartTime"))));
		task.setRunEndTime(TimeUtils.toDate((String)(taskMap.get("runEndTime"))));
		return task;
	}

	private List<Taskflow> mapToTaskflowNew(HashMap<Integer, HashMap<String, String>> map) {
		List<Taskflow> list = new ArrayList<Taskflow>();
		Taskflow flow = null;
		for(HashMap<String, String> flowMap : map.values()){
			flow = new Taskflow(new Integer(flowMap.get("taskflowID")), (String)flowMap.get("taskflow"), null);
			flow.setSuspend(Integer.parseInt((String)flowMap.get("suspend")));
			flow.setGroupID(new Integer(flowMap.get("groupID")));
			flow.setStepType((String)(flowMap.get("stepType")));
			flow.setStep(new Integer(flowMap.get("step")));
			flow.setStatTime(TimeUtils.toDate((String)(flowMap.get("statTime"))));
			flow.setStatus(new Integer(flowMap.get("status")));
			flow.setDescription((String)(flowMap.get("description")));
			flow.setRedoFlag(new Integer(flowMap.get("redoFlag")));
			flow.setSceneStatTime(TimeUtils.toDate((String)(flowMap.get("sceneStatTime"))));
			flow.setRedoStartTime(TimeUtils.toDate((String)(flowMap.get("redoStartTime"))));
			flow.setRedoEndTime(TimeUtils.toDate((String)(flowMap.get("redoEndTime"))));
			flow.setFileLogLevel((String)(flowMap.get("fileLogLevel")));
			flow.setDbLogLevel((String)(flowMap.get("dbLogLevel")));
			flow.setThreadnum(new Integer(flowMap.get("threadnum")));
			flow.setRunStartTime(TimeUtils.toDate((String)(flowMap.get("runStartTime"))));
			flow.setRunEndTime(TimeUtils.toDate((String)(flowMap.get("runEndTime"))));
			list.add(flow);
		}
		return list;
	}
	
	private Taskflow mapToTaskflow(HashMap<String, String> flowMap) {
		Taskflow flow = null;
		flow = new Taskflow(new Integer(flowMap.get("taskflowID")), (String)flowMap.get("taskflow"), null);
		flow.setSuspend(Integer.parseInt((String)flowMap.get("suspend")));
		flow.setGroupID(new Integer(flowMap.get("groupID")));
		flow.setStepType((String)(flowMap.get("stepType")));
		flow.setStep(new Integer(flowMap.get("step")));
		flow.setStatTime(TimeUtils.toDate((String)(flowMap.get("statTime"))));
		flow.setStatus(new Integer(flowMap.get("status")));
		flow.setDescription((String)(flowMap.get("description")));
		flow.setRedoFlag(new Integer(flowMap.get("redoFlag")));
		flow.setSceneStatTime(TimeUtils.toDate((String)(flowMap.get("sceneStatTime"))));
		flow.setRedoStartTime(TimeUtils.toDate((String)(flowMap.get("redoStartTime"))));
		flow.setRedoEndTime(TimeUtils.toDate((String)(flowMap.get("redoEndTime"))));
		flow.setFileLogLevel((String)(flowMap.get("fileLogLevel")));
		flow.setDbLogLevel((String)(flowMap.get("dbLogLevel")));
		flow.setThreadnum(new Integer(flowMap.get("threadnum")));
		flow.setRunStartTime(TimeUtils.toDate((String)(flowMap.get("runStartTime"))));
		flow.setRunEndTime(TimeUtils.toDate((String)(flowMap.get("runEndTime"))));
		return flow;
	}
	
	private TaskType mapToTaskType(HashMap<String, String> typeMap) {
		TaskType taskType = null;
		taskType = new TaskType();
		taskType.setTaskTypeID(new Integer(typeMap.get("taskTypeID")));
		taskType.setTaskType((String)(typeMap.get("taskType")));
		taskType.setEnginePlugin((String)(typeMap.get("enginePlugin")));
		taskType.setEnginePluginType((String)(typeMap.get("enginePluginType")));
		taskType.setEnginePluginJar((String)(typeMap.get("enginePluginJar")));
		taskType.setDescription((String)(typeMap.get("description")));
		taskType.setLargeIcon((String)(typeMap.get("largeIcon")));
		taskType.setSmallIcon((String)(typeMap.get("smallIcon")));
		taskType.setCategoryID(new Integer(typeMap.get("categoryID")));
		taskType.setDesignerPlugin((String)(typeMap.get("designerPlugin")));
		taskType.setDesignerPluginJar((String)(typeMap.get("designerPluginJar")));
		return taskType;
	}
	
	private StepType mapToStepType(HashMap<String, String> typeMap) {
		StepType stepType = null;
		stepType = new StepType();
		stepType.setStepType((String)(typeMap.get("stepType")));
		stepType.setStepName((String)(typeMap.get("stepName")));
		stepType.setFlag(new Integer(typeMap.get("flag")));
		stepType.setOrder(new Integer(typeMap.get("order")));
		return stepType;
	}

	private List<TaskflowGroup> mapToGroup(HashMap<String, String> map) {
		List<TaskflowGroup> list = new ArrayList<TaskflowGroup>();
		for(String id : map.keySet()){
			list.add(new TaskflowGroup(new Integer(id), (String)map.get(id), 0));
		}
		return list;
	}
	
	public void test(){
		try {
			for(int i = 0; i < 1000; i++)
				client.execute("command.getTaskOfTaskflow", new Object[]{new Integer("1751614189")});
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	

	

}
