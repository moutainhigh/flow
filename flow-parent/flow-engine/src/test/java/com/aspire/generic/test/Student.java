package com.aspire.generic.test;

public class Student {
	public static class Factory{
		public void out(){
			System.out.println("sdsd");
		}
	}
	
	public static void main(String[] args) {
		new Student.Factory().out();
	}
}
