package com.aspire.thread.test;

public class ThreadTest {
	public static void main(String[] args) {
		/*MyThread myThread = new MyThread();
		Thread thread = new Thread(myThread,"1");
		Thread thread2 = new Thread(myThread,"2");
		Thread thread3 = new Thread(myThread,"3");
		thread.start();
		thread2.start();
		thread3.start();*/
		int AVAILABLE_PROCESSOR = Runtime.getRuntime().availableProcessors();
		System.out.println(AVAILABLE_PROCESSOR);
	}
}	
