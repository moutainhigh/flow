 package com.daoeee.test.xmlrcp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
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

public class IF3QueryFlow {
	
	public static void main(String[] args) {
        try {
            
        	 	//����XmlRpcClient
        	
            XmlRpcClient client = new XmlRpcClient();
        		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            //config.setServerURL(new URL("http://192.168.1.106:9090/Rpc/command")); //���÷������˵�ַ
        		//config.setServerURL(new URL("http://192.168.1.100:9090/Rpc/command")); //���÷������˵�ַ
        		config.setServerURL(new URL("http://localhost:9090/Rpc/command")); //���÷������˵�ַ
            client.setConfig(config);
            
            //����Զ�̷���
            
            {
	            Vector<String> params = new Vector<String>();
	            String result =(String) client.execute("command.alive", params);
	            System.out.println("result:"+result);
            }
            
            //����Զ�̷���
            
            {
            		//��װ����
	            Vector<Object> params = new Vector<Object>();
	            params.addElement(Integer.valueOf(5));
	            
	            HashMap result =((HashMap) client.execute("command.getTaskflow", params));
	            System.out.println("result:"+result);
	            System.out.println("status:"+result.get("status")); 
	            //��μ�Taskflow�������壬��Taskflow.SUCCESSED	            
            }
            
        } catch (MalformedURLException e) {
        		e.printStackTrace();
        	} catch (XmlRpcException e) {
            e.printStackTrace();
        }
    }

}