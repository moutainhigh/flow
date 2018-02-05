package com.aspire.generic.test;

import java.util.ArrayList;
import java.util.List;

public class ArrayTest {
	public static void main(String[] args) {
//		Apple apple = new Apple();
//		Fruit fruit = new Fruit();
//		Orange orange = new Orange();
//		Object[] o = new Object[]{apple};
//		System.out.println(o);
//		Class c1 = new ArrayList<String>().getClass();
//		Class c2 = new ArrayList<Integer>().getClass();
//        System.out.println(c1 + "--" + c2 + "--" + (c1 == c2)); 
		int x = 0;
		switch (x) {
		case 0:
			try {
				int y = x/0;
			} catch (Exception e) {
				System.out.println("“Ï≥£");
				e.printStackTrace();
			}
			break;
		case 1:
			System.out.println("dada");
		    break;
		}
		
		System.out.println("dsd");
	}
}
