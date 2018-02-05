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

import com.aspire.etl.flowdefine.Category;

public class Category implements Comparable{
	Integer ID ;

	String name = "";

	int order ;

	public Category() {
		
	}
	
	public Category(Integer id, String name, int order) {
		super();
		this.ID = id;
		this.name = name;
		this.order = order;
	}

	
	public int compareTo(Object o) {
		Category other = (Category)o;
		if(order < other.order)
			return -1;
		if(order > other.order)
			return 1;
		return 0;
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer id) {
		ID = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
