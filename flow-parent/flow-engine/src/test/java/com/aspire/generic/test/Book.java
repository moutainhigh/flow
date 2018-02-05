package com.aspire.generic.test;

public class Book<T extends Book> implements Comparable<T> {
	private Integer num = 0;
	@Override
	public int compareTo(T o) {
		// TODO Auto-generated method stub
		return this.num - o.getNum();
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	
}
