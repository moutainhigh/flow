package com.aspire.etl.analyse;

import java.util.LinkedHashMap;

public class PackagePO {
	
	private String packageName;					//包名称
	
	private String packagebody;
	
	private LinkedHashMap<String,String> functionMap ;  //方法集合<方法名称，方法体>
	
	
	public PackagePO(){
		this.functionMap = new LinkedHashMap();
	}

	public LinkedHashMap getFunctionMap() {
		return functionMap;
	}

	public void setFunctionMap(LinkedHashMap functionMap) {
		this.functionMap = functionMap;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public void putFunction(String funcName,String funcBody){
		this.functionMap.put(funcName, funcBody);
	}
	
	public String getFunctionBody(String funcName){
		return String.valueOf(functionMap.get(funcName));
	}

	public String getPackagebody() {
		return packagebody;
	}

	public void setPackagebody(String packagebody) {
		this.packagebody = packagebody;
	}
	
	
}