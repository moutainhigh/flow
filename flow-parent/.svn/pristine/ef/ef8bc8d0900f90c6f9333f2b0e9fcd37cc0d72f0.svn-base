package com.aspire.etl.flowmetadata.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.aspire.etl.flowdefine.Category;
import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.Note;
import com.aspire.etl.flowdefine.Right;
import com.aspire.etl.flowdefine.Role;
import com.aspire.etl.flowdefine.RoleRight;
import com.aspire.etl.flowdefine.StepType;
import com.aspire.etl.flowdefine.SysConfig;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskAttribute;
import com.aspire.etl.flowdefine.TaskHistory;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowdefine.TaskflowGroup;
import com.aspire.etl.flowdefine.TaskflowUser;
import com.aspire.etl.flowdefine.User;
import com.aspire.etl.flowdefine.UserRole;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.DBDaoImpl;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.flowmetadata.dao.SynDao;
import com.aspire.etl.tool.ConnInfo;
import com.aspire.etl.tool.KeyUtil;
import com.aspire.etl.tool.SqlMapUtil;

public class DBDaoImpl implements SynDao {
	
	final static String key = "2ec71bbe2ccaee954a4762569ef7b045";
	
	public DBDaoImpl(ConnInfo connInfo,String ibatisConfigFile) throws Exception{
		SqlMapUtil.init(connInfo,ibatisConfigFile);		
	}
	
	@SuppressWarnings("unchecked")
	
	public void loadTaskflowInfo(Integer taskflowID) throws Exception {
		//Taskflow
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("taskflowID", taskflowID);
		Taskflow taskflow = (Taskflow)SqlMapUtil.executeQueryForObject("getTaskflow", map);
		FlowMetaData.getInstance().taskflowMap.put(taskflow.getTaskflowID(), taskflow);
		//Note
		List<Note> list = SqlMapUtil.executeQueryForList("getNote", map);
		for(Note note : list){
			FlowMetaData.getInstance().noteMap.put(note.getNoteID(), note);
		}
		//Task
		List<Task> listTask = SqlMapUtil.executeQueryForList("getTaskOfTaskFlow", map);
		for (Task task : listTask) {
			FlowMetaData.getInstance().taskMap.put(task.getTaskID(), task);
		}
	}
		
	public void saveTaskflowInfo(Integer taskflowID) throws Exception {
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		Taskflow taskflow = flowMetaData.queryTaskflow(taskflowID);
		insert(taskflow);
		
		//新加
		List<Task> taskList = flowMetaData.queryTaskList(taskflowID);
		for (Task task : taskList) {
			this.insert(task);
			
			List<TaskAttribute> taskAttributeList = flowMetaData.queryTaskAttributeList(task.getTaskID());
			for(TaskAttribute taskAttribute : taskAttributeList){
				this.insert(taskAttribute);
			}
		}
		
		List<Link> linkList = flowMetaData.queryLinkList(taskflowID);
		for(Link link : linkList){
			this.insert(link);
		}
		
		List<Note> noteList = flowMetaData.queryNoteList(taskflowID);
		for (Note note : noteList) {
			this.insert(note);	
		}		
	}
	
	/**
	 * 升级专用!!导入已有流程
	 * @param taskflow
	 * @param inTaskList
	 * @param inNoteList
	 * @param inLinkList
	 * @param inTaskAttributeList
	 * @throws Exception
	 */
	public void importOldTaskflowInfo(Taskflow taskflow, List<Task> inTaskList, List<Note> inNoteList, List<Link> inLinkList, List<TaskAttribute> inTaskAttributeList) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflow.getTaskflowID());
		//存储中的task
		List<Task> dbTaskList = SqlMapUtil.executeQueryForList("getTaskOfTaskFlow", map);
		//需要更新的task
		List<Task> updateTaskList = new ArrayList<Task>();
		//需要删除的task
		List<Task> delTaskList = new ArrayList<Task>();
		//需要增加的task
		List<Task> addTaskList = new ArrayList<Task>();
		//更新的task
		boolean inM = false;
		for(Task dbTask : dbTaskList){
			inM = false;
			for(Task memoryTask : inTaskList){
				if(memoryTask.getTaskID().equals(dbTask.getTaskID())){
					updateTaskList.add(memoryTask);
					inM = true;
					break;
				}
			}
			//删除的task
			if(!inM){
				delTaskList.add(dbTask);
			}
		}
		//增加的task
		for(Task memoryTask : inTaskList){
			if(!updateTaskList.contains(memoryTask)){
				addTaskList.add(memoryTask);
			}
		}
		
		//开始update工作了!!
		//删note 存储中的note
		List<Note> noteList = SqlMapUtil.executeQueryForList("getNote", map);
		if(noteList != null){
			for (Note note : noteList) {
				delete(note);
			}
		}
		//删除link 存储中的link
		List<Link> linkList = SqlMapUtil.executeQueryForList("getLinkOfTaskflow", map);
		for (Link link : linkList) {
			delete(link);
		}
		//删除taskAttribute
		List<TaskAttribute> allTaskAttribute = new ArrayList<TaskAttribute>();
		for(Task dbTask : dbTaskList){
			HashMap taskIDMap = new HashMap();
			taskIDMap.put("taskID", dbTask.getTaskID());
			allTaskAttribute.addAll(SqlMapUtil.executeQueryForList("queryTaskAttributeList", taskIDMap));
		}
		for (TaskAttribute taskAttribute : allTaskAttribute) {
			delete(taskAttribute);
		}
		//删除需要在存储器删除的task
		for (Task task : delTaskList) {
			delete(task);
		}
		//更新taskflow
		SqlMapUtil.executeUpdate("updateTaskflowTemplate", taskflow);
		//新增task
		for (Task task : addTaskList) {
			this.insert(task);
		}
		//更新task
		for (Task task : updateTaskList) {
			update(task);
		}
		for(Link link : inLinkList){
			this.insert(link);
		}		
		for (Note note : inNoteList) {
			this.insert(note);	
		}
		for (TaskAttribute taskAttribute : inTaskAttributeList) {
			this.insert(taskAttribute);	
		}
	}
	
	/**
	 * 升级专用!!导入新流程
	 * @param taskflow
	 * @param inTaskList
	 * @param inNoteList
	 * @param inLinkList
	 * @param inTaskAttributeList
	 * @throws Exception
	 */
	public void importNewTaskflowInfo(Taskflow taskflow, List<Task> inTaskList, List<Note> inNoteList, List<Link> inLinkList, List<TaskAttribute> inTaskAttributeList) throws Exception {
		
		//System.out.println(taskflow.print());

		insert(taskflow);
		
		//新加
		for (Task task : inTaskList) {
			this.insert(task);
		}
		for(Link link : inLinkList){
			this.insert(link);
		}
		for (Note note : inNoteList) {
			this.insert(note);	
		}
		for (TaskAttribute taskAttribute : inTaskAttributeList) {
			this.insert(taskAttribute);	
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updateTaskflowInfo(Integer taskflowID) throws Exception {
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		//内存的task
		List<Task> memoryTaskList = flowMetaData.queryTaskList(taskflowID);
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		//存储中的task
		List<Task> dbTaskList = SqlMapUtil.executeQueryForList("getTaskOfTaskFlow", map);
		//需要更新的task
		List<Task> updateTaskList = new ArrayList<Task>();
		//需要删除的task
		List<Task> delTaskList = new ArrayList<Task>();
		//需要增加的task
		List<Task> addTaskList = new ArrayList<Task>();
		//更新的task
		boolean inM = false;
		for(Task dbTask : dbTaskList){
			inM = false;
			for(Task memoryTask : memoryTaskList){
				if(memoryTask.getTaskID().equals(dbTask.getTaskID())){
					updateTaskList.add(memoryTask);
					inM = true;
					break;
				}
			}
			//删除的task
			if(!inM){
				delTaskList.add(dbTask);
			}
		}
		//增加的task
		for(Task memoryTask : memoryTaskList){
			if(!updateTaskList.contains(memoryTask)){
				addTaskList.add(memoryTask);
			}
		}		
		
		//开始update工作了!!
		//删note 存储中的note
		List<Note> noteList = SqlMapUtil.executeQueryForList("getNote", map);
		if(noteList != null){
			for (Note note : noteList) {
				delete(note);
			}
		}
		//删除link 存储中的link
		List<Link> linkList = SqlMapUtil.executeQueryForList("getLinkOfTaskflow", map);
		for (Link link : linkList) {
			delete(link);
		}
		//删除需要在存储器删除的task
		for (Task task : delTaskList) {
			//删除TaskAttribute
			List<TaskAttribute> taskAttributeList = flowMetaData.queryTaskAttributeList(task.getTaskID());
			for (TaskAttribute taskAttribute : taskAttributeList) {
				delete(taskAttribute);
			}
			
			//2009-12-30 罗奇 如果是大纲流程，在删除节点的时候要把对应的同名流程也删除,XmlDaoImpl 暂时不考虑。
			//if(flowMetaData.isOutlineTask(task.getTask())){
			//此时这些task在内存中已经没有了，因此不能使用上述语句判断。
			if(flowMetaData.isOutlineTaskflow(task.getTaskflowID())){
				Taskflow taskflow = flowMetaData.queryTaskflow(task.getTask());
				
				flowMetaData.deleteTaskflowOnRespository(taskflow
						.getTaskflowID());
				flowMetaData.deleteTaskflowInMemory(taskflow
						.getTaskflowID());
			}
			//2009-12-30
			
			delete(task);			
		}
		//更新taskflow
		Taskflow taskflow = flowMetaData.queryTaskflow(taskflowID);
		update(taskflow);
		//新增task
		for (Task task : addTaskList) {
			this.insert(task);			
			List<TaskAttribute> taskAttributeList = flowMetaData.queryTaskAttributeList(task.getTaskID());
			for(TaskAttribute taskAttribute : taskAttributeList){
				this.insert(taskAttribute);
			}
		}
		//更新task
		for (Task task : updateTaskList) {
			update(task);
			//删除TaskAttribute
			List<TaskAttribute> taskAttributeList = flowMetaData.queryTaskAttributeList(task.getTaskID());
			for (TaskAttribute taskAttribute : taskAttributeList) {
				delete(taskAttribute);
			}
			//新增TaskAttribute
			taskAttributeList = flowMetaData.queryTaskAttributeList(task.getTaskID());
			for(TaskAttribute taskAttribute : taskAttributeList){
				this.insert(taskAttribute);
			}
		}
		linkList = flowMetaData.queryLinkList(taskflowID);
		for(Link link : linkList){
			this.insert(link);
		}
		
		noteList = flowMetaData.queryNoteList(taskflowID);
		for (Note note : noteList) {
			this.insert(note);	
		}
	}
	
	public void delete(Link link) throws Exception {
		SqlMapUtil.executeUpdate("delLink", link);
	}

	public void delete(Task task) throws Exception {
		SqlMapUtil.executeUpdate("delTaskTemplate", task);
		SqlMapUtil.executeUpdate("delTask", task);
	}

	public void delete(TaskAttribute taskAttribute) throws Exception {
		SqlMapUtil.executeUpdate("delTaskAttribute", taskAttribute);
	}

	public void delete(Taskflow taskflow) throws Exception {
		//删除taskflow
		SqlMapUtil.executeUpdate("delTaskflowTemplate", taskflow);
		SqlMapUtil.executeUpdate("delTaskflow", taskflow);
	}

	public void insert(Link link) throws Exception {
		SqlMapUtil.executeUpdate("addLink", link);
	}

	public void insert(Task task) throws Exception {
		SqlMapUtil.executeUpdate("addTaskTemplate", task);
		SqlMapUtil.executeUpdate("addTask", task);
	}

	public void insert(Note note) throws Exception {
		SqlMapUtil.executeUpdate("addNote", note);
	}

	public void insert(TaskAttribute taskAttribute) throws Exception {
		SqlMapUtil.executeUpdate("addTaskAttribute", taskAttribute);
	}

	public void insert(Taskflow taskflow) throws Exception {
		SqlMapUtil.executeUpdate("addTaskflowTemplate", taskflow);
		SqlMapUtil.executeUpdate("addTaskflow", taskflow);
	}

	public void insert(TaskType taskType) throws Exception {
		SqlMapUtil.executeUpdate("addTaskType", taskType);
	}

	public void insert(StepType stepType) throws Exception {
		SqlMapUtil.executeUpdate("addStepType", stepType);
	}

	public void insert(SysConfig sysConfig) throws Exception {
		SqlMapUtil.executeUpdate("addSysConfig", sysConfig);
	}

	public void update(TaskAttribute taskAttribute) throws Exception {
		SqlMapUtil.executeUpdate("updateTaskAttribute", taskAttribute);
	}

	public void update(Task task) throws Exception {
		SqlMapUtil.executeUpdate("updateTaskTemplate", task);
	}

	public void update(Taskflow taskflow) throws Exception {
		SqlMapUtil.executeUpdate("updateTaskflowTemplate", taskflow);
	}

	public void update(SysConfig sysConfig) throws Exception {
		SqlMapUtil.executeUpdate("updateSysConfig", sysConfig);
	}

	public void loadAllTaskflowInfo() throws Exception {
	
		this.loadTaskflows();		
		
		this.loadTasks();		
		
//		this.loadStepTypes();
				
//		this.loadTaskTypes();
				
		this.loadLinks();
				
		this.loadTaskAttributes();
				
		this.loadSysConfigs();
				
		this.loadNotes();
		
//		this.loadCategorys();
		
	}

	public void delete(Note note) throws Exception {
		SqlMapUtil.executeUpdate("delNote", note);
	}

	public void delete(TaskType taskType) throws Exception {
		SqlMapUtil.executeUpdate("delTaskType", taskType);
	}

	public void delete(StepType stepType) throws Exception {
		SqlMapUtil.executeUpdate("delStepType", stepType);
	}

	public void delete(SysConfig sysConfig) throws Exception {
		SqlMapUtil.executeUpdate("delSysConfig", sysConfig);
	}

	public void update(Link link) throws Exception {
		SqlMapUtil.executeUpdate("updateLink", link);
	}

	public void update(Note note) throws Exception {
		SqlMapUtil.executeUpdate("updateNote", note);
	}

	public void update(TaskType taskType) throws Exception {
		SqlMapUtil.executeUpdate("updateTaskType", taskType);
	}

	public void update(StepType stepType) throws Exception {
		SqlMapUtil.executeUpdate("updateStepType", stepType);
	}

	@SuppressWarnings("unchecked")
	public void loadLinks() throws Exception {
		List<Link> list = SqlMapUtil.executeQueryForList("getAllLink", "");
		FlowMetaData.getInstance().linkMap.clear();
		for(Link link : list){
			FlowMetaData.getInstance().linkMap.put(link.getLinkID(), link);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadNotes() throws Exception {
		List<Note> list = SqlMapUtil.executeQueryForList("getAllNote", "");
		FlowMetaData.getInstance().noteMap.clear();
		for(Note note : list){
			FlowMetaData.getInstance().noteMap.put(note.getNoteID(), note);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadStepTypes() throws Exception {
		List<StepType> list = SqlMapUtil.executeQueryForList("getAllStepType", "");
		for (StepType stepType : list) {
			FlowMetaData.getInstance().stepTypeMap.put(stepType.getStepType(), stepType);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadSysConfigs() throws Exception {
		List<SysConfig> list = SqlMapUtil.executeQueryForList("getAllSysConfig", "");
		FlowMetaData.getInstance().sysConfigMap.clear();
		for(SysConfig sysConfig : list){
			FlowMetaData.getInstance().sysConfigMap.put(sysConfig.getID(), sysConfig);
		}
	}
	
	public void refreshSysconfig() throws Exception {
		List<SysConfig> list = SqlMapUtil.executeQueryForList("getAllSysConfig", "");
		
		for(SysConfig sysConfig : list){
			FlowMetaData.getInstance().sysConfigMap.put(sysConfig.getID(), sysConfig);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadTasks() throws Exception {
		List<Task> list = SqlMapUtil.executeQueryForList("getAllTask", "");
		FlowMetaData.getInstance().taskMap.clear();
		for (Task task : list) {
			FlowMetaData.getInstance().taskMap.put(task.getTaskID(), task);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadTaskAttributes() throws Exception {
		List<TaskAttribute> list = SqlMapUtil.executeQueryForList("getAllTaskAttribute", "");
		FlowMetaData.getInstance().taskAttributeMap.clear();
		for(TaskAttribute taskAttribute : list){
			FlowMetaData.getInstance().taskAttributeMap.put(taskAttribute.getAttributeID(), taskAttribute);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadTaskTypes() throws Exception {
		List<TaskType> list = SqlMapUtil.executeQueryForList("getAllTaskType", "");
		for (TaskType taskType : list) {
			FlowMetaData.getInstance().taskTypeMap.put(taskType.getTaskTypeID(), taskType);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadTaskflows() throws Exception {
		List<Taskflow> list = SqlMapUtil.executeQueryForList("getAllTaskflow", "");
		FlowMetaData.getInstance().taskflowMap.clear();
		for (Taskflow taskflow : list) {
			System.out.println( taskflow.toString() );
			FlowMetaData.getInstance().taskflowMap.put(taskflow.getTaskflowID(), taskflow);
		}
	}
	

	public void saveStepTypes() throws Exception {
//		SqlMapUtil.executeUpdate("delAllStepType", "");
//		for (StepType element : FlowMetaData.getInstance().stepTypeMap.values()) {
//			this.insert(element);
//		}
	}

	public void saveSysConfigs() throws Exception {
		SqlMapUtil.executeUpdate("delAllSysConfig", "");
		for (SysConfig element : FlowMetaData.getInstance().querySysConfigList()) {
			this.insert(element);
		}
	}

	public void saveTaskTypes() throws Exception {
//		SqlMapUtil.executeUpdate("delAllTaskType", "");
//		for (TaskType element : FlowMetaData.getInstance().queryTaskTypeList()) {
//			this.insert(element);
//		}
	}

	
	public void deleteTaskflowInfo(Integer taskflowID) throws Exception {
		FlowMetaData flowMetaData =FlowMetaData.getInstance();
		Taskflow taskflow = flowMetaData.queryTaskflow(taskflowID);
		// 删note
		List<Note> noteList = flowMetaData.queryNoteList(taskflow.getTaskflowID());
		if(noteList != null)
			for (Note note : noteList) {
				delete(note);
			}
		
		//删除link
		List<Link> linkList = flowMetaData.queryLinkList(taskflow.getTaskflowID());
		for (Link link : linkList) {
			delete(link);
		}
		// 删任务
		List<Task> taskList = flowMetaData.queryTaskList(taskflow.getTaskflowID());
		for (Task task : taskList) {
			//删除TaskAttribute
			List<TaskAttribute> taskAttributeList = flowMetaData.queryTaskAttributeList(task.getTaskID());
			for (TaskAttribute taskAttribute : taskAttributeList) {
				delete(taskAttribute);
			}
			delete(task);
		}
		//删除流程后，数据库里如果设定了级联删除，其他表中外键关联的数据也会自动删除，
		//
		delete(taskflow);
	}

	@SuppressWarnings("unchecked")
	public boolean isSameNameTaskflowExist(Integer taskflowID,String taskflowName) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("taskflow", taskflowName);
		List<Taskflow> list = SqlMapUtil.executeQueryForList("getTaskflowByIDAndName", map);
		if(list != null && !list.isEmpty())
			return true;
		return false;
	}

	public Integer getTaskflowIDbyName(String taskflowName) throws Exception {		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("taskflow", taskflowName);
		return (Integer)SqlMapUtil.executeQueryForObject("queryTaskflowIDbyName", map);
	}

	@SuppressWarnings("unchecked")
	public void loadCategorys() throws Exception {
		List<Category> list = SqlMapUtil.executeQueryForList("getAllCategory", "");
		for (Category category : list) {
			FlowMetaData.getInstance().categoryMap.put(category.getID(), category);
		}
	}

	public void delete(Category category) throws Exception {
		SqlMapUtil.executeUpdate("delCategory", category);
	}

	public void insert(Category category) throws Exception {
		SqlMapUtil.executeUpdate("addCategory", category);
	}

	public void update(Category category) throws Exception {
		SqlMapUtil.executeUpdate("updateCategory", category);
	}

	public void saveCategorys() throws Exception {
//		SqlMapUtil.executeUpdate("delAllCategory", "");
//		for (Category element : FlowMetaData.getInstance().categoryMap.values()) {
//			this.insert(element);
//		}
	}

	public void saveTaskflowGroups() throws Exception {
		SqlMapUtil.executeUpdate("delAllTaskflowGroup", "");
		for (TaskflowGroup element : FlowMetaData.getInstance().taskflowGroupMap.values()) {
			this.insert(element);
		}
	}

	public void delete(TaskflowGroup taskflowGroup) throws Exception {
		SqlMapUtil.executeUpdate("delTaskflowGroup", taskflowGroup);
	}

	public void insert(TaskflowGroup taskflowGroup) throws Exception {
		SqlMapUtil.executeUpdate("addTaskflowGroup", taskflowGroup);
	}
	
	@SuppressWarnings("unchecked")
	public void loadTaskflowGroups() throws Exception {
		List<TaskflowGroup> list = SqlMapUtil.executeQueryForList("getAllTaskflowGroup", "");
		FlowMetaData.getInstance().taskflowGroupMap.clear();
		for (TaskflowGroup taskflowGroup : list) {
			FlowMetaData.getInstance().taskflowGroupMap.put(taskflowGroup.getGroupID(), taskflowGroup);
		}
	}

	public void update(TaskflowGroup taskflowGroup) throws Exception {
		SqlMapUtil.executeUpdate("updateTaskflowGroup", taskflowGroup);
	}

	public Task getTask(Integer taskID) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("taskID", taskID);
		return (Task)SqlMapUtil.executeQueryForObject("getTask", map);
	}

	public Taskflow getTaskflow(Integer taskflowID) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("taskflowID", taskflowID);
		return (Taskflow)SqlMapUtil.executeQueryForObject("getTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateDbLogLevelOfTaskflow(Integer taskflowID, String dbLogLevel) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("dbLogLevel", dbLogLevel);
		SqlMapUtil.executeUpdate("updateDbLogLevelOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateFileLogLevelOfTaskflow(Integer taskflowID, String fileLogLevel) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("fileLogLevel", fileLogLevel);
		SqlMapUtil.executeUpdate("updateFileLogLevelOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateRedoEndTimeOfTaskflow(Integer taskflowID, Date redoEndTime) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("redoEndTime", redoEndTime);
		SqlMapUtil.executeUpdate("updateRedoEndTimeOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateRedoFlagOfTaskflow(Integer taskflowID, int redoFlag) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("redoFlag", redoFlag);
		SqlMapUtil.executeUpdate("updateRedoFlagOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateRedoStartTimeOfTaskflow(Integer taskflowID, Date redoStartTime) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("redoStartTime", redoStartTime);
		SqlMapUtil.executeUpdate("updateRedoStartTimeOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateRunEndTimeOfTaskflow(Integer taskflowID, Date runEndTime) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("runEndTime", runEndTime);
		SqlMapUtil.executeUpdate("updateRunEndTimeOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateRunEndTimeOfTask(Integer taskID, Date runEndTime) throws Exception {
		HashMap map = new HashMap();
		map.put("taskID", taskID);
		map.put("runEndTime", runEndTime);
		SqlMapUtil.executeUpdate("updateRunEndTimeOfTask", map);		
	}

	@SuppressWarnings("unchecked")
	public void updateRunStartTimeOfTaskflow(Integer taskflowID, Date runStartTime) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("runStartTime", runStartTime);
		SqlMapUtil.executeUpdate("updateRunStartTimeOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateRunStartTimeOfTask(Integer taskID, Date runStartTime) throws Exception {
		HashMap map = new HashMap();
		map.put("taskID", taskID);
		map.put("runStartTime", runStartTime);
		SqlMapUtil.executeUpdate("updateRunStartTimeOfTask", map);
	}
	@SuppressWarnings("unchecked")
	public void updateSceneStatTimeOfTaskflow(Integer taskflowID, Date sceneStatTime) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("sceneStatTime", sceneStatTime);
		SqlMapUtil.executeUpdate("updateSceneStatTimeOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateStatTimeOfTaskflow(Integer taskflowID, Date statTime) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("statTime", statTime);
		SqlMapUtil.executeUpdate("updateStatTimeOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateThreadnumOfTaskflow(Integer taskflowID, int threadnum) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("threadnum", threadnum);
		SqlMapUtil.executeUpdate("updateThreadnumOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateSuspendOfTaskflow(Integer taskflowID, int newSuspend) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("suspend", newSuspend);
		SqlMapUtil.executeUpdate("updateSuspendOfTaskflow", map);
	}

	@SuppressWarnings("unchecked")
	public void updateSuspendOfTask(Integer taskID, int newSuspend) throws Exception {
		HashMap map = new HashMap();
		map.put("taskID", taskID);
		map.put("suspend", newSuspend);
		SqlMapUtil.executeUpdate("updateSuspendOfTask", map);
	}

	@SuppressWarnings("unchecked")
	public void updateStatusOfTask(Integer taskID, int newStatus) throws Exception {
		HashMap map = new HashMap();
		map.put("taskID", taskID);
		map.put("status", newStatus);
		SqlMapUtil.executeUpdate("updateStatusOfTask", map);
	}

	@SuppressWarnings("unchecked")
	public void updateStatusOfTaskflow(Integer taskflowID, int newStatus) throws Exception {
		HashMap map = new HashMap();
		map.put("taskflowID", taskflowID);
		map.put("status", newStatus);
		SqlMapUtil.executeUpdate("updateStatusOfTaskflow", map);
	}
	
	/*@SuppressWarnings("unchecked")
	public void updateHistoryStatus(Long historyId, String file, String status) throws Exception {
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("id", historyId);
		map2.put("endTime", new Date());
		map2.put("file", file);
		map2.put("status", status);
		SqlMapUtil.executeUpdate("updateStatusOfTaskHistory", map2);
	}*/
	@SuppressWarnings("unchecked")
	public void updateHistoryStatus(Long historyId, String status, 
			      String statTime, String endStatTime, String nextStatTime, String fileName) throws Exception {
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		//DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

		map2.put("id", historyId);
		map2.put("endTime", new Date());
		map2.put("file", fileName);
		map2.put("status", status);
		map2.put("statTime", statTime);
		map2.put("endStatTime", endStatTime);
		map2.put("nextStatTime", nextStatTime);
		
		SqlMapUtil.executeUpdate("updateStatusOfTaskHistory", map2);
	}
	
	/*@SuppressWarnings("unchecked")
	public Long saveHistory(String flowIdStr, String taskIdStr, String flowName, String status)
			throws Exception {
		
		
		TaskHistory history;
		history = new TaskHistory();
		
		//history.setRandomId();
		history.setTaskflowID(Integer.valueOf(flowIdStr));
		history.setTaskID(Integer.valueOf(taskIdStr));
		history.setTask(flowName);
		history.setTaskType("DataFetch");
		
		history.setStatus(status);
		history.setStartTime(new Date());
		history.setEndTime(null);
		history.setFile("");
		history.setDescription("");
		
		SqlMapUtil.executeUpdate("insertTaskHistory", history);

		List<TaskHistory> list = SqlMapUtil.executeQueryForList("getAllTaskHistoryByTaskflowId", history.getTaskflowID());
		if (list == null || list.size() <= 0) 
			throw new MetaDataException("Error: getAllTaskHistoryByTaskflowId, flowid=" + flowIdStr);
		
		return list.get(0).getId();
	}*/
	
	@SuppressWarnings("unchecked")
	public Long saveHistory(String flowIdStr, String taskIdStr, String flowName, String status,
			                String statTime, String endStatTime, String nextStatTime )
			throws Exception {
		
		
		TaskHistory taskHist;
		taskHist = new TaskHistory();
		
		//history.setRandomId();
		taskHist.setTaskflowID(Integer.valueOf(flowIdStr));
		taskHist.setTaskID(Integer.valueOf(taskIdStr));
		taskHist.setTask(flowName);
		taskHist.setTaskType("DataFetch");
		taskHist.setCreateTime(new Date());
		
		taskHist.setStatus(status);
		taskHist.setStartTime(new Date());
		taskHist.setEndTime(null);
		//taskHist.setFile("");
		taskHist.setFile(flowName);
		taskHist.setDescription("");
		
		taskHist.setStatTime(statTime);
		taskHist.setEndStatTime(endStatTime);
		taskHist.setNextStatTime(nextStatTime);
		
		SqlMapUtil.executeUpdate("insertTaskHistory", taskHist);

		List<TaskHistory> list = SqlMapUtil.executeQueryForList("getAllTaskHistoryByTaskflowId", taskHist.getTaskflowID());
		if (list == null || list.size() <= 0) 
			throw new MetaDataException("Error: getAllTaskHistoryByTaskflowId, flowid=" + flowIdStr);
		
		return list.get(0).getId();
	}
	//hqw 获取历史执行次数(失败或执行中并且不是重做)
	 public int getCountTaskHistoryByTaskflowId(Integer taskflowID) throws Exception{
		return (Integer)SqlMapUtil.executeQueryForObject("getCountTaskHistoryByTaskflowId", taskflowID);
	}
	@SuppressWarnings("unchecked")
	public void updateTaskflowTime(Integer taskflowID, Date newTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	public void delete(User user) throws Exception {
		SqlMapUtil.executeUpdate("delUser", user);
	}

	@SuppressWarnings("unchecked")
	public void delete(Role role) throws Exception {
		SqlMapUtil.executeUpdate("delRole", role);
	}

	public void delete(Right right) throws Exception {
		SqlMapUtil.executeUpdate("delRight", right);
	}

	public void delete(UserRole userRole) throws Exception {
		SqlMapUtil.executeUpdate("delUserRole", userRole);
	}

	public void delete(RoleRight roleRight) throws Exception {
		SqlMapUtil.executeUpdate("delRoleRight", roleRight);
	}

	public void delete(TaskflowUser taskflowUser) throws Exception {
		SqlMapUtil.executeUpdate("delTaskflowUser", taskflowUser);
	}

	public void insert(User user) throws Exception {
		String password = new String(user.getPassword());
		user.setPassword(new KeyUtil().encrpyt(DBDaoImpl.key, user.getPassword()));
		SqlMapUtil.executeUpdate("addUser", user);
		user.setPassword(password);
	}

	public void insert(Role role) throws Exception {
		SqlMapUtil.executeUpdate("addRole", role);
	}

	public void insert(Right right) throws Exception {
		SqlMapUtil.executeUpdate("addRight", right);
	}

	public void insert(UserRole userRole) throws Exception {
		SqlMapUtil.executeUpdate("addUserRole", userRole);
	}

	public void insert(RoleRight roleRight) throws Exception {
		SqlMapUtil.executeUpdate("addRoleRight", roleRight);
	}

	public void insert(TaskflowUser taskflowUser) throws Exception {
		SqlMapUtil.executeUpdate("addTaskflowUser", taskflowUser);
	}

	@SuppressWarnings("unchecked")
	public void loadRights() throws Exception {
		List<Right> list = SqlMapUtil.executeQueryForList("getAllRight", "");
		FlowMetaData.getInstance().rightMap.clear();
		for (Right right : list) {
			FlowMetaData.getInstance().rightMap.put(right.getID(), right);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadRoleRights() throws Exception {
		List<RoleRight> list = SqlMapUtil.executeQueryForList("getAllRoleRight", "");
		FlowMetaData.getInstance().roleRightMap.clear();
		for (RoleRight roleRight : list) {
			FlowMetaData.getInstance().roleRightMap.put(roleRight.getRoleID() + "_" + roleRight.getRightID() + "", roleRight);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadRoles() throws Exception {
		List<Role> list = SqlMapUtil.executeQueryForList("getAllRole", "");
		FlowMetaData.getInstance().roleMap.clear();
		for (Role role : list) {
			FlowMetaData.getInstance().roleMap.put(role.getID(), role);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadTaskflowUsers() throws Exception {
		List<TaskflowUser> list = SqlMapUtil.executeQueryForList("getAllTaskflowUser", "");
		FlowMetaData.getInstance().taskflowUserMap.clear();
		for (TaskflowUser taskflowUser : list) {
			FlowMetaData.getInstance().taskflowUserMap.put(taskflowUser.getTaskflowID(), taskflowUser);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadUserRoles() throws Exception {
		List<UserRole> list = SqlMapUtil.executeQueryForList("getAllUserRole", "");
		FlowMetaData.getInstance().userRoleMap.clear();
		for (UserRole userRole : list) {
			FlowMetaData.getInstance().userRoleMap.put(userRole.getUserID() + userRole.getRoleID(), userRole);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadUsers() throws Exception {
		List<User> list = SqlMapUtil.executeQueryForList("getAllUser", "");
		FlowMetaData.getInstance().userMap.clear();
		for (User user : list) {
			user.setPassword(new String(new KeyUtil().decrpyt(DBDaoImpl.key, user.getPassword())));
			FlowMetaData.getInstance().userMap.put(user.getID(), user);
		}
	}

	@SuppressWarnings("unchecked")
	public void saveRights() throws Exception {
		SqlMapUtil.executeUpdate("delAllRight", "");
		for (Right element : FlowMetaData.getInstance().rightMap.values()) {
			this.insert(element);
		}
	}

	public void saveRoleRights() throws Exception {
		SqlMapUtil.executeUpdate("delAllRoleRight", "");
		for (RoleRight element : FlowMetaData.getInstance().roleRightMap.values()) {
			this.insert(element);
		}
	}

	public void saveRoles() throws Exception {
		SqlMapUtil.executeUpdate("delAllRole", "");
		for (Role element : FlowMetaData.getInstance().roleMap.values()) {
			this.insert(element);
		}
	}

	public void saveTaskflowUsers() throws Exception {
		SqlMapUtil.executeUpdate("delAllTaskflowUser", "");
		for (TaskflowUser element : FlowMetaData.getInstance().taskflowUserMap.values()) {
			this.insert(element);
		}
	}

	public void saveUserRoles() throws Exception {
		SqlMapUtil.executeUpdate("delAllUserRole", "");
		for (UserRole element : FlowMetaData.getInstance().userRoleMap.values()) {
			this.insert(element);
		}
	}

	public void saveUsers() throws Exception {
		SqlMapUtil.executeUpdate("delAllUser", "");
		for (User element : FlowMetaData.getInstance().userMap.values()) {
			this.insert(element);
		}
	}

	public void update(User user) throws Exception {
		String password = new String(user.getPassword());
		user.setPassword(new KeyUtil().encrpyt(DBDaoImpl.key, user.getPassword()));		
		SqlMapUtil.executeUpdate("updateUser", user);
		user.setPassword(password);
	}

	public void update(Role role) throws Exception {
		SqlMapUtil.executeUpdate("updateRole", role);
	}

	public void update(Right right) throws Exception {
		SqlMapUtil.executeUpdate("updateRight", right);
	}

	public void update(UserRole userRole) throws Exception {
		SqlMapUtil.executeUpdate("updateUserRole", userRole);
	}

	public void update(RoleRight roleRight) throws Exception {
		SqlMapUtil.executeUpdate("updateRoleRight", roleRight);
	}

	public void update(TaskflowUser taskflowUser) throws Exception {
		SqlMapUtil.executeUpdate("updateTaskflowUser", taskflowUser);
	}
	
	public static void main(String[] args){
		System.out.println(new KeyUtil().encrpyt(key, "admin"));
	}
	
	/**
	 * 按流程ID获取所有任务的信息
	 * @param taskflowId
	 * @return
	 * @throws Exception
	 */
	public List<Task> getTasksInTaskflow(int taskflowId) throws Exception {
		List<Task> list = SqlMapUtil.executeQueryForList("getAllTaskByTaskflowId", taskflowId);
		return list;
	}

	/**
	 * 按流程ID获取所有任务的参数属性
	 * @param taskflowId
	 * @return
	 * @throws Exception
	 */
	public List<TaskAttribute> getTaskAttributesInTaskflow(int taskflowId) throws Exception {
		List<TaskAttribute> list = SqlMapUtil.executeQueryForList("getAllTaskAttributeByTaskflowId", taskflowId);
		return list;
	}
	
	/**
	 * 按流程ID获取所有注释
	 * @param taskflowId
	 * @return
	 * @throws Exception
	 */
	public List<Note> getNotesInTaskflow(int taskflowId) throws Exception  {
		List<Note> list = SqlMapUtil.executeQueryForList("getAllNoteByTaskflowId", taskflowId);
		return list;
	}
	
	/**
	 * 按流程ID获取所有Link信息
	 * @param taskflowId
	 * @return
	 * @throws Exception
	 */
	public List<Link> getLinksInTaskflow(int taskflowId) throws Exception {
		List<Link> list = SqlMapUtil.executeQueryForList("getAllLinkByTaskflowId", taskflowId);
		return list;
	}
	
}
