package com.flow.cluster.test;

public class IntegerTest {
	private static Integer num;
	public static void main(String[] args) {
		int i = get();
		System.out.println(i);
	}
	public static int get(){
		try {
			num = 1/0;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			num = 1;
		}
		return num;
	}
}
