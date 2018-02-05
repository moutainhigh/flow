package com.flow.cluster.test;


public class AbstractGenericData<E extends AbstractGenericData, T extends Comparable<T>> implements Comparable<E>{
	public AbstractGenericData() {
		super();
	}
	private T data;
	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(E e) {
		return data.compareTo((T) e.getData());
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
}
