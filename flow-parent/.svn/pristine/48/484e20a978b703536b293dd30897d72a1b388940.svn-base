 package com.daoeee.test.xmlrcp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class IF1SubmitFormProperties {

	public static void main(String[] args) {
        try {
            
        	
            XmlRpcClient client = new XmlRpcClient();
        		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        		//e.g. http://localhost:9090/Rpc/command
        		config.setServerURL(new URL(Constants.SERVER_URL)); 
            client.setConfig(config);
            	
            ResourceBundle props = loadConfig();
        	
        		Vector<Object> params = new Vector<Object>();
        		params.add(props.getString("flowJsonStr"));
        		params.add(props.getString("tasksJsonStr"));
        		params.add(props.getString("taskAttributeJsonStr"));
        		params.add(props.getString("linkJsonStr"));
        		
            String result = (String) client.execute("command.submitTaskflow", params);
			System.out.println("result:"+result);
            
        } catch (MalformedURLException e) {
        		e.printStackTrace();
        	} catch (XmlRpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
        	}
    }


	private static ResourceBundle loadConfig() {
		ResourceBundle config = 
				ResourceBundle.getBundle("com/daoeee/test/xmlrcp/1");
		
		System.out.println("**flowJsonStr=" + config.getString("flowJsonStr"));
		System.out.println("**tasksJsonStr=" + config.getString("tasksJsonStr"));
		System.out.println("**taskAttributeJsonStr=" + config.getString("taskAttributeJsonStr"));
		System.out.println("**linkJsonStr=" + config.getString("linkJsonStr"));
		return config;
	}

}