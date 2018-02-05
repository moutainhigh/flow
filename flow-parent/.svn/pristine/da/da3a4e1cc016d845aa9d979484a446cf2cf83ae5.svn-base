/*
 * FIFO.java
 *
 * Created on 2008-2-19 17:43:06 by luoqi
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
package com.aspire.etl.tool;

import java.util.LinkedList;

/**
 * @author luoqi
 * 通用的泛型”先进先出队列“
 * @param <E>
 */
public class FIFO<E> {
	//
	LinkedList<E> deque = new LinkedList<E>();

	public synchronized void add(E object) {
		deque.addLast(object);
	}

	public synchronized int size() {
		return deque.size();
	}

	public synchronized E getFirst() {
		return deque.getFirst();
	}

	public synchronized E pop() {
		return deque.removeFirst();
	}

	public synchronized void headBackToTail() {
		deque.addLast(deque.removeFirst());
	}

	public synchronized boolean exist(E object) {
		return deque.contains(object);
	}

	/* (non-Javadoc)
	 * 为FIFO队列定制的toString()方法
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {		
		Object[] array = deque.toArray();
		StringBuffer sb = new StringBuffer();
		sb.append("[--->");
		for (int i = array.length-1; i >=0 ; i--) {			
			sb.append("(");
			sb.append(array[i]);
			sb.append(")");
			sb.append("--->");
			
		}
		sb.append("]");
		return sb.toString();
	}

}
