package com.aspire.etl.flowdefine;

/**
 * @author luoqi 映射ETL元数据库里的rpt_task_type表的一行数据
 */
public class SysConfig {

	Integer ID;
	
	String configName;

	String configValue;

	String configDesc;
	
	String stage;
	
	String type;
	
	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SysConfig() {
	}

//	public SysConfig(String configName, String configValue) {
//		this(configName, configValue, "");
//	}

//	public SysConfig(String configName, String configValue, String configDesc) {
//		super();
//		this.configName = configName;
//		this.configValue = configValue;
//		this.configDesc = configDesc;
//	}
	
	public SysConfig(Integer id, String configName, String configValue, String configDesc) {
		super();
		this.ID = id;
		this.configName = configName;
		this.configValue = configValue;
		this.configDesc = configDesc;
	}
	
//	public String makeKey(){
//		return makeKey(this.configName);
//	}
	
//	public String makeKey(String configName){
//		return "[" + configName+ "]";
//	}
	
	@Override
	public String toString() {
		return configName;
	}

	public String getConfigDesc() {
		return configDesc;
	}

	public void setConfigDesc(String description) {
		this.configDesc = description;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String taskType) {
		this.configName = taskType;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String exec) {
		this.configValue = exec;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer id) {
		ID = id;
	}

}
