/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aspire.flow.scheduler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.aspire.flow.test.ThreadFlow;

/**
 * 手动控制的调度管理器默认实现,用于与Console交互.
 * 
 * @author chenhaitao
 *
 */
public class DefaultManualScheduleManager implements ManualScheduleManager {
	public DefaultManualScheduleManager() {
		super();
	}

	public static ConcurrentHashMap<String, XmlRpcClient> rpcClientPool = new ConcurrentHashMap<String, XmlRpcClient>();

	@Override
	public void startupManual(String flowId, String taskId, String status, String nodeIp, Integer port,
			String excuteStatus, Vector<Object> params) {
		XmlRpcClient xmlRpcClient = null;
		/*try {
			threadFlow = taskflowThreadPool.get(flowId);
			// 如果线程不存在或者并不存活
			if (threadFlow == null || !threadFlow.isAlive()) {
				threadFlow = new ThreadFlow(flowId, taskId, status);
				ThreadFlow putIfAbsent = taskflowThreadPool.putIfAbsent(flowId, threadFlow);
				if (putIfAbsent != null) {
					threadFlow = putIfAbsent;
				}
			}
			threadFlow.start();
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		try {
			xmlRpcClient = rpcClientPool.get(nodeIp + ":" + port);
			if (xmlRpcClient == null) {
				xmlRpcClient = new XmlRpcClient();
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				// 设置服务器端地址，如http://localhost:9090/Rpc/command
				config.setServerURL(new URL("http://" + nodeIp + ":" + port + "/Rpc/command"));
				xmlRpcClient.setConfig(config);
				XmlRpcClient putIfAbsent = rpcClientPool.putIfAbsent(nodeIp + ":" + port, xmlRpcClient);
				if (putIfAbsent != null) {
					xmlRpcClient = putIfAbsent;
				}
			}
			/*XmlRpcClient client = new XmlRpcClient();
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			// 设置服务器端地址，如http://localhost:9090/Rpc/command
			config.setServerURL(new URL("http://" + nodeId + ":" + port + "/Rpc/command"));
			client.setConfig(config);*/
			if(excuteStatus.equals("Start") || excuteStatus.equals("Submit")){
				/* 提交任务begin */
				String result =(String) xmlRpcClient.execute("command.startTaskflowForCluster", params);
				System.out.println("result:"+result);
				/* 提交任务end */
			}
			if(excuteStatus.equals("Redo")){
				String result =(String) xmlRpcClient.execute("command.redoForCluster", params);
				System.out.println("result:"+result);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void shutdown(String group) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void shutdown(String group, String name) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<String> getGroupList() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> getNameList(String group) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScheduleStatus getScheduleStatus(String group, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 停止任务
	 */
	@Override
	public int shutdown(String jobName, String nodeIp, Integer port) {
		XmlRpcClient xmlRpcClient = null;
		Vector<Object> params = new Vector<Object>();
		params.add(jobName);
		int flag = 0;
		try {
			xmlRpcClient = rpcClientPool.get(nodeIp + ":" + port);
			if (xmlRpcClient == null) {
				xmlRpcClient = new XmlRpcClient();
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				// 设置服务器端地址，如http://localhost:9090/Rpc/command
				config.setServerURL(new URL("http://" + nodeIp + ":" + port + "/Rpc/command"));
				xmlRpcClient.setConfig(config);
			}else{
				rpcClientPool.remove(xmlRpcClient);
			}
			String result = (String)xmlRpcClient.execute("command.stopTaskflowForCluster", params);
			if(!result.equals("成功! stopTaskflow " + jobName)){
				flag = 0;
			}else{
				flag = 1;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		return flag;
	}

}