package com.daoeee.test.xmlrcp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskAttribute;
import com.aspire.etl.flowdefine.Taskflow;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Step2Start {
	
	public static void main(String[] args) {
        try {
            
        	 	//创建XmlRpcClient
        	
            XmlRpcClient client = new XmlRpcClient();
        		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            //config.setServerURL(new URL("http://192.168.1.100:9090/Rpc/command")); //设置服务器端地址
        		config.setServerURL(new URL("http://localhost:9090/Rpc/command"));
            client.setConfig(config);
            
            //调用远程方法
            
            {
            		//组装参数
	            Vector<String> params = new Vector<String>();
	            params.addElement("Remote_Task_Flow");
	            
	            String result =(String) client.execute("command.startTaskflow", params);
	            System.out.println("result:"+result);
            }
            
        } catch (MalformedURLException e) {
        		e.printStackTrace();
        	} catch (XmlRpcException e) {
            e.printStackTrace();
        }
    }



}