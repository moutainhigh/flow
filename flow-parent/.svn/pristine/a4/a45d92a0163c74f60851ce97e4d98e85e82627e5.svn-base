package com.aspire.etl.xmlrpc.client;

import java.util.Comparator;


public class TaskComparator implements Comparator {
	
	/*成功,运行,失败,等待,就绪,停止*/
	private static String[]  statusOrders = {"成功","运行","失败","等待","就绪","停止"};//status:-1失败,0就绪,1运行,2成功,3等待,4停止
	
	private int orderFieldIndx = 3; //按状态
	
	public TaskComparator(){
		
	}
	public TaskComparator(int orderFieldIndx){
		this.orderFieldIndx = orderFieldIndx;
	}
	
	public int compare(Object arg0, Object arg1) {
		
		String task1Status = ((String[])arg0)[orderFieldIndx];
		String task2Status = ((String[])arg1)[orderFieldIndx];
		if (orderFieldIndx == 3){ //按状态
			int task1Index = getIndex(task1Status);
			int task2Index = getIndex(task2Status);
			return task1Index - task2Index;
		} else {
			return task1Status.compareToIgnoreCase(task2Status);
		}
		
	}
	
	private static int getIndex(String value){
		for(int i = 0; i < statusOrders.length; i++){
			if (statusOrders[i].equals(value)){
				return i;
			}
		}
		return -1;
	}
	
	public int getOrderFieldIndx() {
		return orderFieldIndx;
	}
	public void setOrderFieldIndx(int orderFieldIndx) {
		this.orderFieldIndx = orderFieldIndx;
	}

}


