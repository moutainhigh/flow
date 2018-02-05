/*
 * TaskAttribute.java
 *
 * Created on 2008-2-4 12:04:22 by luoqi
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
package com.aspire.etl.flowdefine;

import java.io.Serializable;

import com.aspire.etl.tool.Utils;

/**
 * @author luoqi 映射ETL元数据库里的rpt_task_attribute表的一行数据
 */
public class TaskAttribute implements Serializable, Cloneable{

	Integer attributeID;

	Integer taskID;

	String key = "";

	String value = "";
	
	private static final long serialVersionUID = 56L;
	
	public TaskAttribute() {

	}

	public TaskAttribute(Integer attributeID, Integer taskID, String key,
			String value) {
		super();
		// TODO Auto-generated constructor stub
		this.attributeID = attributeID;
		this.taskID = taskID;
		this.key = key;
		this.value = value;
	}
	
	public Object clone() {   
		TaskAttribute o = null;   
        try {   
            o = (TaskAttribute) super.clone();
            o.setAttributeID(Utils.getRandomIntValue());
            o.setKey(this.key);
            o.setValue(this.value);
            o.setTaskID(this.taskID);
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
        return o;   
    }
	
	@Override
	public String toString() {
		return "(ID=" + attributeID + ")" + key + "=" + value; 
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getAttributeID() {
		return attributeID;
	}

	public void setAttributeID(Integer attributeID) {
		this.attributeID = attributeID;
	}

	public Integer getTaskID() {
		return taskID;
	}

	public void setTaskID(Integer taskID) {
		this.taskID = taskID;
	}

	public String getValue() {
		return value==null?"":value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
