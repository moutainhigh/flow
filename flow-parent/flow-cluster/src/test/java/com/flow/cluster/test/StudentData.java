package com.flow.cluster.test;

public class StudentData<T extends StudentData> implements Comparable<T>{
	
	private int age;
	private String name;
	
	@Override
	public int compareTo(StudentData data) {
		return this.age - data.getAge();
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
