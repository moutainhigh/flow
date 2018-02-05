package com.aspire.thread.test;

public class SyncThread extends Thread {  
    private static String sync = "";  
    private String methodType = "";  
          
    public SyncThread(String methodType) {  
        this.methodType = methodType;  
    }  
  
    private static void method(String s){  
        synchronized(sync){  
          sync=s;  
            System.out.println(s);  
            while(true);  
        }  
    }  
      
    public void method1(){  
        method("method1");  
    }  
      
    public static void staticMethod1(){  
        method("staticMethod1");  
    }  
      
      
    @Override  
    public void run() {  
        // TODO Auto-generated method stub  
        if(methodType.equals("static"))  
            staticMethod1();  
        else  
            method1();  
    }  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
        SyncThread sample1 = new SyncThread("nostatic");  
        SyncThread sample2 = new SyncThread("static");  
        sample1.start();  
        sample2.start();  
    }  
  
}  
