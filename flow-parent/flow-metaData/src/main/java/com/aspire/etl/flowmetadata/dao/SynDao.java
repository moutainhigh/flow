package com.aspire.etl.flowmetadata.dao;

import java.util.Date;
import java.util.List;

import com.aspire.etl.flowdefine.*;


/**
 * 
 * 
 */
public interface SynDao {

	/**
	 * 单独增加一个category
	 * 
	 * @param category
	 * @throws Exception
	 */
	public void insert(Category category) throws Exception;
	
	/**
	 * 单独增加
	 * @param user
	 * @throws Exception
	 */
	public void insert(User user) throws Exception;
	
	/**
	 * 单独增加
	 * @param role
	 * @throws Exception
	 */
	public void insert(Role role) throws Exception;
	
	/**
	 * 单独增加
	 * @param right
	 * @throws Exception
	 */
	public void insert(Right right) throws Exception;
	/**
	 * 单独增加
	 * @param userRole
	 * @throws Exception
	 */
	public void insert(UserRole userRole) throws Exception;
	
	/**
	 * 单独增加
	 * @param roleRight
	 * @throws Exception
	 */
	public void insert(RoleRight roleRight) throws Exception;
	
	/**
	 * 单独增加
	 * @param taskflowUser
	 * @throws Exception
	 */
	public void insert(TaskflowUser taskflowUser) throws Exception;

	/**
	 * 单独增加一个taskflowGroup
	 * @param taskflowGroup
	 * @throws Exception
	 */
	public void insert(TaskflowGroup taskflowGroup) throws Exception;

	/**
	 * 单独增加一个link,只有可能被设计器调用
	 * 
	 * @param link
	 * @throws Exception
	 */
	public void insert(Link link) throws Exception;

	/**
	 * 单独增加一个Task,只有可能被设计器调用
	 * 
	 * @param task
	 * @throws Exception
	 */
	public void insert(Task task) throws Exception;

	/**
	 * 单独增加一个Task注释,只有可能被设计器调用
	 * 
	 * @param note
	 * @throws Exception
	 */
	public void insert(Note note) throws Exception;

	/**
	 * 单独增加一个任务动态参数,只有可能被设计器调用
	 * 
	 * @param taskAttribute
	 * @throws Exception
	 */
	public void insert(TaskAttribute taskAttribute) throws Exception;

	/**
	 * 单独增加一个ETL流程,只有可能被设计器调用
	 * 
	 * @param taskflow
	 * @throws Exception
	 */
	public void insert(Taskflow taskflow) throws Exception;

	/**
	 * 单独增加一个任务类型,只有可能被设计器调用
	 * 
	 * @param taskType
	 * @throws Exception
	 */
	public void insert(TaskType taskType) throws Exception;

	/**
	 * 单独增加一个周期类型,只有可能被设计器调用
	 * 
	 * @param stepType
	 * @throws Exception
	 */
	public void insert(StepType stepType) throws Exception;

	/**
	 * 单独增加一个系统配置参数,只有可能被设计器调用
	 * 
	 * @param sysConfig
	 * @throws Exception
	 */
	public void insert(SysConfig sysConfig) throws Exception;

	/**
	 * 单独删除一个category
	 * 
	 * @param category
	 * @throws Exception
	 */
	public void delete(Category category) throws Exception;
	
	/**
	 * 单独删除一个user
	 * @param user
	 * @throws Exception
	 */
	public void delete(User user) throws Exception;
	
	/**
	 * 单独删除一个role
	 * @param role
	 * @throws Exception
	 */
	public void delete(Role role) throws Exception;
	
	/**
	 * 单独删除
	 * @param right
	 * @throws Exception
	 */
	public void delete(Right right) throws Exception;
	/**
	 * 单独删除
	 * @param userRole
	 * @throws Exception
	 */
	public void delete(UserRole userRole) throws Exception;
	
	/**
	 * 单独删除
	 * @param roleRight
	 * @throws Exception
	 */
	public void delete(RoleRight roleRight) throws Exception;
	
	/**
	 * 单独删除
	 * @param taskflowUser
	 * @throws Exception
	 */
	public void delete(TaskflowUser taskflowUser) throws Exception;
	
	/**
	 * 单独删除一个taskflowGroup
	 * @param taskflowGroup
	 * @throws Exception
	 */
	public void delete(TaskflowGroup taskflowGroup) throws Exception;
	
	/**
	 * 单独删除一个link,只有可能被设计器调用
	 * 
	 * @param link
	 * @throws Exception
	 */
	public void delete(Link link) throws Exception;

	/**
	 * 单独删除一个Task,只有可能被设计器调用
	 * 
	 * @param task
	 * @throws Exception
	 */
	public void delete(Task task) throws Exception;

	/**
	 * 单独删除一个Task注释,只有可能被设计器调用
	 * 
	 * @param note
	 * @throws Exception
	 */
	public void delete(Note note) throws Exception;

	/**
	 * 单独删除一个任务动态参数,只有可能被设计器调用
	 * 
	 * @param taskAttribute
	 * @throws Exception
	 */
	public void delete(TaskAttribute taskAttribute) throws Exception;

	/**
	 * 单独删除一个任务类型,只有可能被设计器调用
	 * 
	 * @param taskType
	 * @throws Exception
	 */
	public void delete(TaskType taskType) throws Exception;

	/**
	 * 单独删除一个周期类型,只有可能被设计器调用
	 * 
	 * @param stepType
	 * @throws Exception
	 */
	public void delete(StepType stepType) throws Exception;

	/**
	 * 单独删除一个系统配置参数,只有可能被设计器调用
	 * 
	 * @param sysConfig
	 * @throws Exception
	 */
	public void delete(SysConfig sysConfig) throws Exception;

	/**
	 * 更新category
	 * 
	 * @param category
	 * @return
	 * @throws Exception
	 */
	public void update(Category category) throws Exception;
	
	/**
	 * 单独更新
	 * @param user
	 * @throws Exception
	 */
	public void update(User user) throws Exception;
	
	/**
	 * 单独更新
	 * @param role
	 * @throws Exception
	 */
	public void update(Role role) throws Exception;
	
	/**
	 * 单独更新
	 * @param right
	 * @throws Exception
	 */
	public void update(Right right) throws Exception;
	/**
	 * 单独更新
	 * @param userRole
	 * @throws Exception
	 */
	public void update(UserRole userRole) throws Exception;
	
	/**
	 * 单独更新
	 * @param roleRight
	 * @throws Exception
	 */
	public void update(RoleRight roleRight) throws Exception;
	
	/**
	 * 单独更新
	 * @param taskflowUser
	 * @throws Exception
	 */
	public void update(TaskflowUser taskflowUser) throws Exception;

	/**
	 * 更新taskflowGroup
	 * @param taskflowGroup
	 * @throws Exception
	 */
	public void update(TaskflowGroup taskflowGroup) throws Exception;

	/**
	 * 更新Link
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	public void update(Link link) throws Exception;

	/**
	 * 更新Task
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	public void update(Task task) throws Exception;

	/**
	 * 更新Note
	 * 
	 * @param note
	 * @return
	 * @throws Exception
	 */
	public void update(Note note) throws Exception;

	/**
	 * 单独更新一个任务动态参数,只有可能被设计器调用
	 * 
	 * @param taskAttribute
	 * @return
	 * @throws Exception
	 */
	public void update(TaskAttribute taskAttribute) throws Exception;

	/**
	 * 更新TaskType
	 * 
	 * @param taskType
	 * @return
	 * @throws Exception
	 */
	public void update(TaskType taskType) throws Exception;

	/**
	 * 更新StepType
	 * 
	 * @param stepType
	 * @return
	 * @throws Exception
	 */
	public void update(StepType stepType) throws Exception;

	/**
	 * 修改系统参数，可能被设计器调用,必须。
	 * 
	 * @return
	 * @throws Exception
	 */
	public void update(SysConfig sysConfig) throws Exception;

	/**
	 * 单独删除一个流程,只有可能被设计器调用,不能单独调用,必须和delTask(),delLink(),delTaskAttribute()一起用。
	 * 
	 * @param taskflow
	 * @throws Exception
	 */
	public void delete(Taskflow taskflow) throws Exception;
	/**
	 * 删除一个流程,已经相关的link,task,等.
	 * 
	 * @param taskflow
	 * @throws Exception
	 */
	public void deleteTaskflowInfo(Integer taskflowID) throws Exception;
	/**
	 * 更新流程
	 * 
	 * @param taskflow
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public void update(Taskflow taskflow) throws Exception;

	public void loadTaskflows() throws Exception;

	public void loadTasks() throws Exception;

	public void loadTaskAttributes() throws Exception;

	public void loadLinks() throws Exception;

	public void loadTaskTypes() throws Exception;

	public void loadStepTypes() throws Exception;
	
	public void loadCategorys() throws Exception;
	
	public void loadUsers() throws Exception;
	
	public void loadRoles() throws Exception;
	
	public void loadRights() throws Exception;
	
	public void loadUserRoles() throws Exception;
	
	public void loadRoleRights() throws Exception;
	
	public void loadTaskflowUsers() throws Exception;
	
	public void loadTaskflowGroups() throws Exception;

	public void loadSysConfigs() throws Exception;

	public void loadNotes() throws Exception;

	/**
	 * 判断是否存在同名但ID不同的流程。
	 * 设计器保存流程时调用。
	 * @param taskflowName
	 * @return
	 * @throws Exception
	 */
	public boolean isSameNameTaskflowExist(Integer taskflowID,String taskflowName) throws Exception;

	/**
	 * 按流程名称取流程ID。
	 * 设计器保存流程时调用。
	 * @param taskflowName
	 * @return
	 * @throws Exception
	 */
	public Integer getTaskflowIDbyName(String taskflowName) throws Exception;

	/**
	 * 加载所有流程以及流程相关的任务，任务参数和连接
	 * 
	 * @throws Exception
	 */
	public void loadAllTaskflowInfo() throws Exception;
	
	/**
	 * 加载单个流程以及流程相关的任务，任务参数和连接
	 * 
	 * @param taskflowName
	 */
	public void loadTaskflowInfo(Integer taskflowID) throws Exception ;
		
	/**
	 * 保存单个流程以及流程相关的任务，任务参数和连接
	 * 
	 * @param taskflowName
	 */
	public void saveTaskflowInfo(Integer taskflowID) throws Exception;

	/**
	 * 保存所有StepType
	 * @throws Exception
	 */
	public void saveStepTypes() throws Exception;
	/**
	 * 保存所有TaskType
	 * @throws Exception
	 */
	public void saveTaskTypes() throws Exception;
	/**
	 * 保存所有SysConfig
	 * @throws Exception
	 */
	public void saveSysConfigs() throws Exception;
	/**
	 * 保存所有Category
	 * @throws Exception
	 */
	public void saveCategorys() throws Exception;

	
	public void saveUsers() throws Exception;
	
	public void saveRoles() throws Exception;
	
	public void saveRights() throws Exception;
	
	public void saveUserRoles() throws Exception;
	
	public void saveRoleRights() throws Exception;
	
	public void saveTaskflowUsers() throws Exception;

	/**
	 * 保存所有TaskflowGroup
	 * @throws Exception
	 */
	public void saveTaskflowGroups() throws Exception;
	
	public Task getTask(Integer taskID) throws Exception;
	
	public Taskflow getTaskflow(Integer taskflowID) throws Exception;
	
	public void updateStatTimeOfTaskflow(Integer taskflowID,
			Date statTime) throws Exception;
	
	public void updateThreadnumOfTaskflow(Integer taskflowID,
			int threadnum) throws Exception;
	
	public void updateFileLogLevelOfTaskflow(Integer taskflowID,
			String fileLogLevel) throws Exception;
	
	public void updateDbLogLevelOfTaskflow(Integer taskflowID,
			String dbLogLevel) throws Exception;
	
	public void updateRedoFlagOfTaskflow(Integer taskflowID,
			int redoFlag) throws Exception;
	
	public void updateSceneStatTimeOfTaskflow(Integer taskflowID,
			Date sceneStatTime) throws Exception;
	
	public void updateRedoStartTimeOfTaskflow(Integer taskflowID,
			Date redoStartTime) throws Exception;
	
	public void updateRedoEndTimeOfTaskflow(Integer taskflowID,
			Date redoEndTime) throws Exception;
	
	public void updateRunStartTimeOfTaskflow(Integer taskflowID,
			Date runStartTime) throws Exception;

	public void updateRunStartTimeOfTask(Integer taskID,
			Date runStartTime) throws Exception;

	public void updateRunEndTimeOfTaskflow(Integer taskflowID,
			Date runEndTime) throws Exception;

	public void updateRunEndTimeOfTask(Integer taskID,
			Date runEndTime) throws Exception;

	public void updateTaskflowInfo(Integer taskflowID) throws Exception;

	public void updateSuspendOfTask(Integer taskID, int newSuspend) throws Exception;

	public void updateSuspendOfTaskflow(Integer taskflowID, int newSuspend) throws Exception;

	public void updateStatusOfTask(Integer taskID, int newStatus) throws Exception;

	public void updateStatusOfTaskflow(Integer taskflowID, int newStatus) throws Exception;

	public void updateTaskflowTime(Integer taskflowID, Date newTime) throws Exception;

	public void importNewTaskflowInfo(Taskflow taskflow, List<Task> inTaskList, List<Note> inNoteList, List<Link> inLinkList, List<TaskAttribute> inTaskAttributeList) throws Exception ;

	public void importOldTaskflowInfo(Taskflow taskflow, List<Task> inTaskList, List<Note> inNoteList, List<Link> inLinkList, List<TaskAttribute> inTaskAttributeList) throws Exception ;

	/**
	 * 按流程ID获取所有任务的信息
	 * @param taskflowId
	 * @return
	 * @throws Exception
	 */
	public List<Task> getTasksInTaskflow(int taskflowId) throws Exception ;

	/**
	 * 按流程ID获取所有任务的参数属性
	 * @param taskflowId
	 * @return
	 * @throws Exception
	 */
	public List<TaskAttribute> getTaskAttributesInTaskflow(int taskflowId) throws Exception ;
	
	/**
	 * 按流程ID获取所有注释
	 * @param taskflowId
	 * @return
	 * @throws Exception
	 */
	public List<Note> getNotesInTaskflow(int taskflowId) throws Exception ;

	/**
	 * 按流程ID获取所有Link信息
	 * @param taskflowId
	 * @return
	 * @throws Exception
	 */
	public List<Link> getLinksInTaskflow(int taskflowId) throws Exception ;
	
	/**
	 * 刷新系统配置信息
	 * @throws Exception
	 */
	public void refreshSysconfig() throws Exception ;
	
	//public void updateHistoryStatus(Long historyId,String file, String status) throws Exception;
	public void updateHistoryStatus(Long historyId,String status, String statTime, String endStatTime, String nextStatTime, String fileName) throws Exception;
	public Long saveHistory(String flowIdStr, String taskIdStr, String flowName, String status, String statTime, String endTime, String nextStatTime) throws Exception;
	//public Long saveHistory(String flowIdStr, String taskIdStr, String flowName, String status) throws Exception;
	/**
	 * hqw 获取历史执行次数(失败或执行中并且不是重做)
	 * @throws Exception
	 */
	 public int getCountTaskHistoryByTaskflowId(Integer taskflowID) throws Exception;
}
