package com.aspire.thread.test;

public class DeadLock implements Runnable{
    private int a;
    private int b;
    public DeadLock(int a, int b) {
       super();
       this.a = a;
       this.b = b;
    }
    @Override
    public void run() {
       synchronized (Integer.valueOf(a)) {
           synchronized (Integer.valueOf(b)) {
              System.out.println("a+b="+(a+b));
           }
       }
    }
    public static void main(String[] args) {
    	for(int i = 0; i < 1000; ++i){
           new Thread(new DeadLock(1, 2)).start();
           new Thread(new DeadLock(2, 1)).start();
    	}
    }
}
