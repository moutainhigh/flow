package com.aspire.etl.flowengine;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.daoeee.test.xmlrcp.Constants;

public class Test {
	public static void main(String[] args) throws MalformedURLException, XmlRpcException {
		/*
		 * try { FlowMetaData flowMetaData = FlowMetaData.getInstance();
		 * System.out.println(flowMetaData); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
		XmlRpcClient client = new XmlRpcClient();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		// e.g. http://localhost:9090/Rpc/command
		config.setServerURL(new URL("http://172.16.129.1:9098/Rpc/command"));
		client.setConfig(config);
		Vector<Object> params = new Vector<Object>();
		params.add("12");
		//client.execute("command.startTaskflow", params);
		client.execute("command.stopTaskflow", params);
		//client.execute("command.redo", params);
	}
}
