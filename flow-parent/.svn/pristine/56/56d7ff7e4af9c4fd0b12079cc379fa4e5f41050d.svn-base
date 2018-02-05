package com.aspire.etl.analyse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TablePO {
	
	private String tableName;
	
	private List<TablePO> fromTableList;
	
	private int sourceType;
	
	private String sql; 
	
	private String functionName;
	
	private List<String> funcNameList;
	
	private HashMap<String,List<TablePO>> tableRefFuncList ; //表与函数的关系，key为函数,value为表名List
	
	private String comment;


	public TablePO(){
		
	}
	
	public TablePO(String tableName){
		this.tableName = tableName;
	}

	public TablePO(String tableName, String comment) {
		this.tableName = tableName;
		this.comment = comment;
	}

	public String toString(){
		return "tableName = " + tableName;
	}
	
	public List<TablePO> getFromTableList() {
		
		LinkedHashMap map = new LinkedHashMap();
		ArrayList list = new ArrayList();
		if (fromTableList != null){
			for(TablePO table : fromTableList){
				map.put(table.getTableName(), table);
			}
			Iterator it = map.entrySet().iterator();
			while(it.hasNext()){
				list.add((TablePO) ((Map.Entry)it.next()).getValue());
			}
			
			return list;
			
		} else {
			return null;
		}
		
	}

	public void setFromTableList(List fromTableList) {
		this.fromTableList = fromTableList;
	}

	public void addFromTableList(List fromTableList) {
		if (this.fromTableList == null){
			this.fromTableList = new ArrayList();
		}
		this.fromTableList.addAll(fromTableList);
	}

	public void addFromTable(TablePO table) {
		if (this.fromTableList == null){
			this.fromTableList = new ArrayList();
		}
		this.fromTableList.add(table);
	}

	
	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public List<String> getFuncNameList() {
		return funcNameList;
	}

	public void setFuncNameList(List<String> funcNameList) {
		this.funcNameList = funcNameList;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getFullName(){
		return tableName + (!comment.equals("") ? ("(" + this.comment + ")") : "");
	}
	
	/**
	 * 增加表与函数的关系
	 * @param key
	 * @param tableList
	 */
	public void addTableRefFuncList(String key,List<TablePO> tableList){
		if (tableRefFuncList == null){
			tableRefFuncList = new HashMap();
		}
		tableRefFuncList.put(key, tableList);
	}

	public HashMap getTableRefFuncList() {
		return tableRefFuncList;
	}

	public void setTableRefFuncList(HashMap tableRefFuncList) {
		this.tableRefFuncList = tableRefFuncList;
	}
}
