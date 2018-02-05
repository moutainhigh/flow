/*
 * StepType.java
 *
 * Created on 2008-2-22 12:02:39 by luoqi
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

import com.aspire.etl.flowdefine.StepType;

public class StepType implements Comparable{
	public static final String SECOND = "S";
	public static final String MINUTE = "MI";
	public static final String HOUR = "H";
	public static final String DAY = "D";
	public static final String WEEK = "W";
	public static final String TENDAY = "TD";
	public static final String HALF_MONTH = "HM";
	public static final String MONTH = "M";
	public static final String SEASON = "SE";
	public static final String HALF_YEAR = "HY";
	public static final String YEAR = "Y";

	public static final int DISPLAY_NO = 0;

	public static final int DISPLAY_YES = 1;

	String stepType = "";

	String stepName = "";

	int flag = 0;
	
	int order ;

	public StepType() {
		
	}
	
	public StepType(String stepType, String stepName) {
		this(stepType, stepName, DISPLAY_YES,1);
	}

	public StepType(String stepType, String stepName, int flag,int order) {
		super();
		this.stepType = stepType;
		this.stepName = stepName;
		this.flag = flag;
		this.order = order;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getStepType() {
		return stepType;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int compareTo(Object o) {
		StepType other = (StepType)o;
		if(order < other.order)
			return -1;
		if(order > other.order)
			return 1;
		return 0;
	}
	
	public String makeKey() {
		return stepName;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return stepName;
	}
}
