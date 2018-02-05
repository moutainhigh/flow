package com.aspire.etl.flowdefine;

/**
 * @author luoqi 映射ETL元数据库里的rpt_task_type表的一行数据
 */
public class TaskType {
	public static final String PLSQL = "plsql";

	public static final String DATASYN = "datasyn";

	public static final String SHELL = "shell";

	public static final String BACKUP = "backup";

	public static final String STAT = "stat";

	public static final String COMPRESS = "compress";

	public static final String NULL = "null";

	public static final String LOAD = "load";

	public static final String FORMAT = "format";

	public static final String AUDITOR = "auditor";

	public static final String EMAIL = "email";
	
	Integer taskTypeID;

	String taskType = "";

	String enginePlugin = "";

	String enginePluginType = "";
	
	String enginePluginJar = "";
	
	String description = "";
	
	String largeIcon = "";
	 
	String smallIcon = "";
	
	//组件面板上的分类目录
	Integer categoryID = 1;
	
	String designerPlugin = "";
	
	String designerPluginJar = "";
	
	String secondCategory = "";
	
	public TaskType() {
	}

	public TaskType(Integer taskTypeID, String taskType, String enginePlugin) {
		this(taskTypeID, taskType, enginePlugin, "");
	}
	public TaskType(Integer taskTypeID, String taskType, String description, String enginePlugin) {
		this(taskTypeID, taskType,description, enginePlugin,"","");
	}
		
	public TaskType(Integer taskTypeID, String taskType, String description, String enginePlugin,String largeIcon,String smallIcon) {
		super();
		this.taskTypeID = taskTypeID;
		this.taskType = taskType;
		this.enginePlugin = enginePlugin;
		this.description = description;
		this.largeIcon = largeIcon;
		this.smallIcon = smallIcon;
	}

	@Override
	public String toString() {
		return taskType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getEnginePlugin() {
		return enginePlugin;
	}

	public void setEnginePlugin(String enginePlugin) {
		this.enginePlugin = enginePlugin;
	}

	public String getLargeIcon() {
		return largeIcon;
	}

	public void setLargeIcon(String largeIcon) {
		this.largeIcon = largeIcon;
	}

	public String getSmallIcon() {
		return smallIcon;
	}

	public void setSmallIcon(String smallIcon) {
		this.smallIcon = smallIcon;
	}

	public Integer getTaskTypeID() {
		return taskTypeID;
	}

	public void setTaskTypeID(Integer taskTypeID) {
		this.taskTypeID = taskTypeID;
	}

	public String getDesignerPlugin() {
		return designerPlugin;
	}

	public void setDesignerPlugin(String designerPlugin) {
		this.designerPlugin = designerPlugin;
	}

	public Integer getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(Integer catalogID) {
		this.categoryID = catalogID;
	}

	public String getSecondCategory() {
		return secondCategory;
	}

	public void setSecondCategory(String secondcategory) {
		this.secondCategory = secondcategory;
	}

	public String getEnginePluginType() {
		return enginePluginType;
	}

	public void setEnginePluginType(String enginePluginType) {
		this.enginePluginType = enginePluginType;
	}

	public String getDesignerPluginJar() {
		return designerPluginJar;
	}

	public void setDesignerPluginJar(String designerPluginJar) {
		this.designerPluginJar = designerPluginJar;
	}

	public String getEnginePluginJar() {
		return enginePluginJar;
	}

	public void setEnginePluginJar(String enginePluginJar) {
		this.enginePluginJar = enginePluginJar;
	}

}
