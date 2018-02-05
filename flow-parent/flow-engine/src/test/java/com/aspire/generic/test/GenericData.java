package com.aspire.generic.test;

public abstract class GenericData<T extends Comparable<T>> extends AbstractData{
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
}
