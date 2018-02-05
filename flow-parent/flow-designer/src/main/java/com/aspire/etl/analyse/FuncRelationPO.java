package com.aspire.etl.analyse;

import java.util.ArrayList;
import java.util.List;


public class FuncRelationPO {
	
	//函数名
	private String funcName;
	
	private Integer funcID;
	
	//来源表
	private List<String> sourceTableList;
	
	//目标表
	private List<String> targetTableList;
	

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	
	public List<String> getSourceTableList() {
		return sourceTableList;
	}

	public void setSourceTableList(List<String> sourceTableList) {
		this.sourceTableList = sourceTableList;
	}

	public List<String> getTargetTableList() {
		return targetTableList;
	}

	public void setTargetTableList(List<String> targetTableList) {
		this.targetTableList = targetTableList;
	}

	public void addTargetTables(List<String> targetTableList_) {
		if (targetTableList == null){
			targetTableList = new ArrayList<String>();
		}
		targetTableList.addAll(targetTableList_);
		
	}
	
	public void addSourceTables(List<String> sourceTableList_) {
		if (sourceTableList == null){
			sourceTableList = new ArrayList<String>();
		}
		sourceTableList.addAll(sourceTableList_);
		
	}

	public Integer getFuncID() {
		return funcID;
	}

	public void setFuncID(Integer funcID) {
		this.funcID = funcID;
	}
	
	
	
	
}
