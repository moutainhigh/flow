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
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class IF1Submit {
	
	public static void main(String[] args) {
        try {
            
        	 	//创建XmlRpcClient
        	
            XmlRpcClient client = new XmlRpcClient();
        		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        		//设置服务器端地址，如http://localhost:9090/Rpc/command
        		config.setServerURL(new URL(Constants.SERVER_URL)); 
            client.setConfig(config);
            
            //调用远程方法
            
            {
            		//调用参数依次放入Vector<Object>，
            	    //参数定义（包括参数个数，次序和数据类型）请参见具体接口描述
	            Vector<Object> params = new Vector<Object>();
	            /*params.addElement("string-param");
	            params.addElement(Integer.valueOf(3));*/
	            
	            //command.alive为接口名
	            //返回值数数据类型请参见具体接口描述
	            String result =(String) client.execute("command.alive", params);
	            System.out.println("result:"+result);
            }
            
            //调用远程方法
            
            {
            		//组装参数
	            Vector<Object> params = populateParam();
	            
	            String result =(String) client.execute("command.submitTaskflow", params);
	            System.out.println("result:"+result);
            }
            
        } catch (MalformedURLException e) {
        		e.printStackTrace();
        	} catch (XmlRpcException e) {
            e.printStackTrace();
        }
    }

	private static Vector<Object> populateParam() {
		Vector<Object> params = new Vector<Object>();
		
		Taskflow flow = new Taskflow();
		flow.setTaskflowID(6);
		flow.setStatTime(new Date()); //
		flow.setSceneStatTime(null);
		flow.setStatus(Taskflow.READY);
		flow.setSuspend(0); //
		flow.setRedoFlag(0);
		flow.setRedoStartTime(null);
		flow.setRedoEndTime(null);
		flow.setFileLogLevel("DEBUG");
		flow.setDbLogLevel("OFF");
		flow.setThreadnum(1);
		flow.setRunStartTime(null);
		flow.setRunEndTime(null);
		
		flow.setTaskflow("Remote_Task_Flow1");
		flow.setGroupID(0);
		flow.setStepType("MI"); //MI分钟，D天
		flow.setStep(2); //2分钟
		flow.setDescription("DESC: Remote_Task1");
		flow.setMemo("Meno: Remote_Task1");
		
		//params.add(JSONObject.fromObject(flow).toString());
		params.add(JSON.toJSONString(flow));
		
		Task task1 = null;
		TaskAttribute attribute11 = null;
		TaskAttribute attribute12 = null;
		{
		    Task task = new Task();
		    task.setTaskID(10); //ID
		    task.setTaskflowID(flow.getTaskflowID());
		    task.setStatus(Task.READY);
		    task.setSuspend(0);
		    task.setRunStartTime(null);
		    task.setRunEndTime(null); //
		    task.setTask("Remote_Sell_提数10"); //任务名
		    task.setTaskType("Shell");
		    task.setPlantime(0); //预计任务完成时间(分钟数),0-不判断/N-判断超时'
		    task.setIsRoot(1); //是否为ETL流程起点(0不是,1是)
		    task.setAlertID(null);
		    task.setPerformanceID(null);
		    task.setXPos(0);
		    task.setYPos(0);
		    task.setDescription("DESC:Remote_Sell_提数10");
		    task.setMemo("Memo:Remote_Sell_提数10");
		    task1 = task;
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(83);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("Shell_WorkDir");
		    		attribute.setValue("/bin");
		    		attribute11 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(84);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("Shell_Name");
		    		attribute.setValue("pwd");
		    		attribute12 = attribute;
		    }
		}

		Task task2 = null;
		TaskAttribute attribute21 = null;
		TaskAttribute attribute22 = null;
		TaskAttribute attribute23 = null;
		TaskAttribute attribute24 = null;
		TaskAttribute attribute25 = null;
		TaskAttribute attribute26 = null;
		TaskAttribute attribute27 = null;
		TaskAttribute attribute28 = null;
		TaskAttribute attribute29 = null;
		{
		    Task task = new Task();
		    task.setTaskID(11); //ID
		    task.setTaskflowID(flow.getTaskflowID());
		    task.setStatus(Task.READY);
		    task.setSuspend(0);
		    task.setRunStartTime(null);
		    task.setRunEndTime(null); //
		    task.setTask("Remote_邮件通知11"); //任务名
		    task.setTaskType("EMAIL");
		    task.setPlantime(0); //预计任务完成时间(分钟数),0-不判断/N-判断超时'
		    task.setIsRoot(0); //是否为ETL流程起点(0不是,1是)
		    task.setAlertID(null);
		    task.setPerformanceID(null);
		    task.setXPos(0);
		    task.setYPos(0);
		    task.setDescription("DESC:Remote_邮件通知11");
		    task.setMemo("Memo:Remote_邮件通知11");
		    task2 = task;

		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(104);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("SMTP_SERVER");
		    		attribute.setValue("smtp.qq.com");
		    		attribute27 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(98);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("USER");
		    		attribute.setValue("3116760687@qq.com");
		    		attribute21 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(99);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("PASSWORD");
		    		attribute.setValue("lxcqbmpnxvmideif");
		    		attribute22 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(100);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("FROM");
		    		attribute.setValue("3116760687@qq.com");
		    		attribute23 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(101);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("TO");
		    		attribute.setValue("3116760687@qq.com");
		    		attribute24 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(102);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("SUBJECT");
		    		attribute.setValue("remote:"+new Date());
		    		attribute25 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(103);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("CONTENT");
		    		attribute.setValue("CONTENT-1");
		    		attribute26 = attribute;
		    }
		}
		
		//params.add(JSONArray.fromObject(new Task[] {task1, task2}).toString());
		params.add(JSON.toJSONString(new Task[] {task1, task2}));
		TaskAttribute[] taskAttributes = new TaskAttribute[] {
				attribute11, attribute12, attribute21, attribute22, attribute23, attribute24, attribute25
				, attribute26, attribute27};
		//params.add(JSONArray.fromObject(taskAttributes).toString());
		params.add(JSON.toJSONString(taskAttributes));
		Link link = new Link();
		link.setLinkID(51);
		link.setFromTaskID(task1.getTaskID());
		link.setToTaskID(task2.getTaskID());
		//params.add(JSONArray.fromObject(new Link[] {link}).toString());
		params.add(JSON.toJSONString(new Link[] {link}));
		return params;
	}


}