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

public class IF5SubmitCircle {
	
	public static void main(String[] args) {
        try {
            
            XmlRpcClient client = new XmlRpcClient();
        		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        		config.setServerURL(new URL(Constants.SERVER_URL)); 
            client.setConfig(config);
            
            
            {
	            Vector<Object> params = populateParam();
	            
	            String result =(String) client.execute("command.submitCircleTaskflow", params);
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
		flow.setTaskflowID(5);
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
		
		flow.setTaskflow("Remote_Task_Flow");
		flow.setGroupID(1);
		flow.setStepType("MI"); 
		flow.setStep(2);
		//flow.setStepType("D");
		//flow.setStep(1);
		flow.setDescription("DESC: Remote_Task");
		flow.setMemo("Meno: Remote_Task");
		
		params.add(JSONObject.fromObject(flow).toString());
		
		Task task1 = null;
		TaskAttribute attribute11 = null;
		TaskAttribute attribute12 = null;
		{
		    Task task = new Task();
		    task.setTaskID(8); //ID
		    task.setTaskflowID(flow.getTaskflowID());
		    task.setStatus(Task.READY);
		    task.setSuspend(0);
		    task.setRunStartTime(null);
		    task.setRunEndTime(null); //
		    task.setTask("Remote_Sell"); 
		    task.setTaskType("Shell");
		    task.setPlantime(0); 
		    task.setIsRoot(1); 
		    task.setAlertID(null);
		    task.setPerformanceID(null);
		    task.setXPos(0);
		    task.setYPos(0);
		    task.setDescription("DESC:Remote_Sell");
		    task.setMemo("Memo:Remote_Sell");
		    task1 = task;
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(81);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("Shell_WorkDir");
		    		attribute.setValue("/bin");
		    		attribute11 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(82);
		    		attribute.setTaskID(task.getTaskID());
		    		attribute.setKey("Shell_Name");
		    		attribute.setValue("pwd");
		    		attribute12 = attribute;
		    }
		}

		Task task2 = null;
		TaskAttribute attribute21 = null;
		TaskAttribute attribute22 = null;
		{
		    Task task = new Task();
		    task.setTaskID(9); //ID
		    task.setTaskflowID(flow.getTaskflowID());
		    task.setStatus(Task.READY);
		    task.setSuspend(0);
		    task.setRunStartTime(null);
		    task.setRunEndTime(null); //
		    task.setTask("Remote_task2"); 
		    task.setTaskType("Shell");
		    task.setPlantime(0); 
		    task.setIsRoot(0); 
		    task.setAlertID(null);
		    task.setPerformanceID(null);
		    task.setXPos(0);
		    task.setYPos(0);
		    task.setDescription("DESC:Remote");
		    task.setMemo("Memo:Remote");
		    task2 = task;


		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(91);
		    		attribute.setTaskID(task2.getTaskID());
		    		attribute.setKey("Shell_WorkDir");
		    		attribute.setValue("/bin");
		    		attribute21 = attribute;
		    }
		    {
		    		TaskAttribute attribute = new TaskAttribute();
		    		attribute.setAttributeID(92);
		    		attribute.setTaskID(task2.getTaskID());
		    		attribute.setKey("Shell_Name");
		    		attribute.setValue("pwd");
		    		attribute22 = attribute;
		    }
		}
		
		params.add(JSONArray.fromObject(new Task[] {task1, task2}).toString());
		
		TaskAttribute[] taskAttributes = new TaskAttribute[] {
				attribute11, attribute12, attribute21, attribute22};
		params.add(JSONArray.fromObject(taskAttributes).toString());
		
		Link link = new Link();
		link.setLinkID(51);
		link.setFromTaskID(task1.getTaskID());
		link.setToTaskID(task2.getTaskID());
		params.add(JSONArray.fromObject(new Link[] {link}).toString());
		
		return params;
	}


}