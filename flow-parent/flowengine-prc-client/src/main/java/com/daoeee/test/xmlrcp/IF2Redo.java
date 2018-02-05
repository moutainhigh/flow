 package com.daoeee.test.xmlrcp;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class IF2Redo {
	
	public static void main(String[] args) {
        try {
            
        	 	//����XmlRpcClient
        	
            XmlRpcClient client = new XmlRpcClient();
        		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        		config.setServerURL(new URL(Constants.SERVER_URL)); //���÷������˵�ַ
            client.setConfig(config);
           
            //����Զ�̷���
            
            {
            		//��װ����
	            Vector<Object> params = new Vector<Object>();
	            params.addElement(Integer.valueOf(5));
	            params.addElement(new SimpleDateFormat("yyyyMMdd").parse("20170411"));
	            params.addElement(new SimpleDateFormat("yyyyMMdd").parse("20170411"));
	            
	            String result =(String) client.execute("command.redo", params);
	            System.out.println("result:"+result);
            }
            
        } catch (Exception e) {
        		e.printStackTrace();
        	} 
    }


}