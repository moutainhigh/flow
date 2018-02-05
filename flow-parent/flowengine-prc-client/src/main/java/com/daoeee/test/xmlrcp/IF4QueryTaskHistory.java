package com.daoeee.test.xmlrcp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.aspire.etl.flowdefine.TaskHistory;

import net.sf.json.JSONArray;

public class IF4QueryTaskHistory {
	
	public static void main(String[] args) {
        try {
            
        	 	//创建XmlRpcClient
        	
            XmlRpcClient client = new XmlRpcClient();
        		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        		config.setServerURL(new URL(Constants.SERVER_URL)); //设置服务器端地址
            client.setConfig(config);
            
            //调用远程方法
            
            {
            		//组装参数
	            Vector<Object> params = new Vector<Object>();
	            params.addElement(Integer.valueOf(3));
	            
	            String result =(String) client.execute("command.queryTaskHistory", params);
	            
	            TaskHistory[] tasks = 
	            		(TaskHistory[])JSONArray.toArray(JSONArray.fromObject(result), TaskHistory.class);
		    		for(TaskHistory taskhis : tasks){
		    			System.out.println(taskhis);
		    		}
            }
            
        } catch (MalformedURLException e) {
        		e.printStackTrace();
        	} catch (XmlRpcException e) {
            e.printStackTrace();
        }
    }



}