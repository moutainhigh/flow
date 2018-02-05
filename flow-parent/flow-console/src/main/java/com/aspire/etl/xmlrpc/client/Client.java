package com.aspire.etl.xmlrpc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.PasswordField;

/**
 * 2009-07-03 不再提供针对单个任务的操作。删除starttask 和 stoptask 命令。
 * 记得这个类，还有webclient用了。接口改了可能也需要修改。另：webclient可考虑藏在展现门户中。
 * 
 * 2010-06-07 增加打包命令，增量升级命令，全量升级命令 add by jiangts
 * @author jiangts
 * 
 * 2010-12-28 添加list和listtask的增强功能:
 * listtask 可排序（状态排序按：成功,运行,失败,等待,就绪,停止），可按状态查询单个流程或所有流程的任务信息
 * 对升级流程增加日志记录，记时什么时候发生过升级操作
 * @author jiangts
 * 
 * 2011-01-01 增加了静默登录
 * @author jiangts
 *
 */
public class Client implements Serializable {
	
	static final long serialVersionUID = System.currentTimeMillis();
	
	static XmlRpcClient xmlRpcClient = null;
	
	public static boolean connect = false;	

	static String applicationName = "";
	
	static String ip = "";
	
	static String port = "";
	
	static String username = "";
	
	static String password = "";
	
	private static Logger log = Logger.getLogger(Client.class);
	
	public Client(String ip, String port, String username, String password) {
		Client.ip = ip;
		Client.port = port;
		Client.username = username;
		Client.password = password;
		try {
			connect();
		} catch (MalformedURLException e) {
			System.out.println(e);
		}
		connect = testConnect();
	}

	public static void main(String[] args) throws IOException{
		
		//加入静态登录
		if (args.length > 0){ // user/passwd@127.0.0.1:9090
			String params = args[0];
			String usernames =  params.substring(0,params.indexOf("/"));
			String passwords =  params.substring(params.indexOf("/") + 1,params.indexOf("@"));
			String ips =  params.substring(params.indexOf("@") + 1,params.indexOf(":"));
			String ports =  params.substring(params.indexOf(":") + 1,params.length());
			
			ip = ips;
			applicationName = new String(ip);
			password =passwords;
			port = ports;
			username = usernames;
			System.out.println(username+"," 
					+ password+","
					+ ip+","
					+ port);
			connecting();
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(!connect){
			System.out.print("请输入引擎IP:");
			ip = br.readLine();
			applicationName = new String(ip);
			System.out.print("请输入监控端口:");
			port = br.readLine();
			System.out.print("请输入用户名:");
			username = br.readLine();
//			System.out.print("请输入密码:");
//			password = br.readLine();
			char[] p = PasswordField.getPassword(System.in, "请输入密码: ");
			if(p == null){
				p = new char[0];
			}
			password = new String(p);
//		System.out.println("IP=" + ip);
//		System.out.println("端口=" + port);
//		System.out.println("用户名=" + username);
//		System.out.println("密码=" + password);
			System.out.println("正在进行连接...");
//		 create configuration
//		String ip = "127.0.0.1";
//		String port = "8080";
//		String username = "username";
//		String password = "password";
			
			connecting();
		}
		try{
			applicationName = (String)xmlRpcClient.execute("command.application", new Object[0]);
		}catch(Exception e){
			System.out.println("取应用名失败!");
		}
		Object[] in = null;
		String result = "error commond";
		String command = null;
		System.out.println("连接成功!!");
		System.out.print(applicationName + ">");
		while ((command = br.readLine()) != null) {
			if (command.equals(""))
				continue;
			if (command.equalsIgnoreCase("quit"))
				break;
			if (command.equalsIgnoreCase("exit"))
				break;
			in = command.split(" ");
			result = execute(in);
			System.out.println(result);
			System.out.print(applicationName + ">");
		}
	}

	
	private static void connecting(){
//		连接
		try{
			connect();
			//转移一下
			PrintStream ps = System.err;
			System.setErr(new PrintStream(new java.io.ByteArrayOutputStream()));
			//测试连接
			if(testConnect()){
				connect = true;
			}
			System.setErr(ps);
		}catch(Exception e){
			//连接失败!!
			System.out.println("连接失败!!系统不能连接到IP(" + ip + ")和端口(" + port + "),请检查ETL引擎是否启动或IP端口是否正确");
			System.out.println(e);
		}
	}
	
	
	public static boolean testConnect() {
		try{
			int iPort = Integer.parseInt(port);
			if(iPort > 65535){
				System.out.println("端口号错误!!(端口须为数字且在0-65535之间)");
				System.out.println();
				return false;
			}
		}catch(Exception e){
			System.out.println("端口号错误!!(端口须为数字且在0-65535之间)");
			System.out.println();
			return false;
		}
		try{
			//xmlRpcClient.execute("command.alive", new Object[0]);
			Vector<Object> params = new Vector<Object>();
			String execute = (String)xmlRpcClient.execute("command.alive", params);
			System.out.println(execute);
			return true;
		}catch(Exception e){
			if(e.getMessage().indexOf("Not authorized") > -1){
				System.out.println("用户名或密码错误!!请重新登录!!");
			}else if(e.getMessage().indexOf("Connection refused") > -1 || e.getMessage().indexOf("Connection timed out") > -1){
				System.out.println("连接失败!!系统不能连接到IP(" + ip + ")和端口(" + port + "),请检查ETL引擎是否启动或IP端口是否正确");
			}else{
				System.out.println("连接失败!!系统不能连接到IP(" + ip + ")和端口(" + port + "),用户(" + username + "),请检查ETL引擎是否启动或IP端口是否正确");
			}
			System.out.println();
		}
		return false;
	}

	private static void connect() throws MalformedURLException {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		//config.setServerURL(new URL("http://" + ip + ":" + port + "/xmlrpc"));
		config.setServerURL(new URL("http://localhost:9090/Rpc/command"));
		config.setEnabledForExtensions(true);
		config.setConnectionTimeout(60 * 1000);
		config.setReplyTimeout(60 * 1000);
		config.setBasicUserName(username);
		config.setBasicPassword(password);
		xmlRpcClient = new XmlRpcClient();

		// use Commons HttpClient as transport
		//xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
		// set configuration
		xmlRpcClient.setConfig(config);
	}

	@SuppressWarnings("unchecked") 
	public static String execute(Object[] in) {
		//去空格
		in = trimSpace(in);
		
		String result = "";
		Object[] params = new Object[0];
		try{
			if("help".equalsIgnoreCase((String)in[0]) || "?".equals((String)in[0])){
				result = printHelp();				
			}else if("!help".equalsIgnoreCase((String)in[0])){
				result = pringInternalHelp();
			}else if("listall".equalsIgnoreCase((String)in[0])||"la".equalsIgnoreCase((String)in[0])){
				if(in.length == 3 && in[1].equals("-s")){//listall [-s status]
					String status = String.valueOf(in[2]);
					
					//status: failed(失败),ready(就绪),running(运行),successed(成功),waitting(等待),stopped(停止)
					//status:-1失败,0就绪,1运行,2成功,3等待,4停止
					if ("failed".startsWith(status.toLowerCase())){
						status = "-1";
					} else if ("ready".startsWith(status.toLowerCase())){
						status = "0";
					} else if ("running".startsWith(status.toLowerCase())){
						status = "1";
					} else if ("successed".startsWith(status.toLowerCase())){
						status = "2";
					} else if ("waitting".startsWith(status.toLowerCase())){
						status = "3";
					} else if ("stopped".startsWith(status.toLowerCase())){
						status = "4";
					} 
					
					HashMap listMap = new HashMap();
					HashMap taskflowMap = (HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listall", params);
					Iterator it = taskflowMap.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						if (String.valueOf(((HashMap)entry.getValue()).get("status")).equals(status)){
							listMap.put(entry.getKey(), entry.getValue());
						}
					}
					HashMap groupMap = (HashMap<String, String>)xmlRpcClient.execute("command.getTaskflowGroups", params);
					result = toListAllString(listMap,groupMap);
				} else
				if(in.length == 2){//listall [taskflowName]
					String taskflowName = String.valueOf(in[1]);
					taskflowName = "[\\s\\S]*" + taskflowName.toLowerCase().replace("*", "[\\s\\S]*") + "[\\s\\S]*";
					HashMap listMap = new HashMap();
					HashMap taskflowMap = (HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listall", params);
					Iterator it = taskflowMap.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						if (String.valueOf(((HashMap)entry.getValue()).get("taskflow")).toLowerCase().matches(taskflowName)){
							listMap.put(entry.getKey(), entry.getValue());
						}
					}
					HashMap groupMap = (HashMap<String, String>)xmlRpcClient.execute("command.getTaskflowGroups", params);
					result = toListAllString(listMap,groupMap);
				} else {
					result = toListAllString((HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listall", params), 
							(HashMap<String, String>)xmlRpcClient.execute("command.getTaskflowGroups", params));
				}
				
			}else if("list".equalsIgnoreCase((String)in[0])||"l".equalsIgnoreCase((String)in[0])){
				
				//list [-s status]
				if(in.length == 3 && in[1].equals("-s")){
					String status = String.valueOf(in[2]);
					
					//status: failed(失败),ready(就绪),running(运行),successed(成功),waitting(等待),stopped(停止)
					//status:-1失败,0就绪,1运行,2成功,3等待,4停止
					if ("failed".startsWith(status.toLowerCase())){
						status = "-1";
					} else if ("ready".startsWith(status.toLowerCase())){
						status = "0";
					} else if ("running".startsWith(status.toLowerCase())){
						status = "1";
					} else if ("successed".startsWith(status.toLowerCase())){
						status = "2";
					} else if ("waitting".startsWith(status.toLowerCase())){
						status = "3";
					} else if ("stopped".startsWith(status.toLowerCase())){
						status = "4";
					} 
					
					HashMap listMap = new HashMap();
					HashMap taskflowMap = (HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.list", params);
					Iterator it = taskflowMap.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						//如果找到，则放入列表中   (String)flow.get("status")
						if (String.valueOf(((HashMap)entry.getValue()).get("status")).equals(status)){
							listMap.put(entry.getKey(), entry.getValue());
						}
					}
					HashMap groupMap = (HashMap<String, String>)xmlRpcClient.execute("command.getTaskflowGroups", params);
					result = toListString(listMap,groupMap);
				} else //list [taskflowName]
				if(in.length == 2){
					String taskflowName = String.valueOf(in[1]);
					taskflowName = "[\\s\\S]*" + taskflowName.toLowerCase().replace("*", "[\\s\\S]*") + "[\\s\\S]*";
					HashMap listMap = new HashMap();
					HashMap taskflowMap = (HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.list", params);
					Iterator it = taskflowMap.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						//如果找到，则放入列表中
						if (String.valueOf(((HashMap)entry.getValue()).get("taskflow")).toLowerCase().matches(taskflowName)){
							listMap.put(entry.getKey(), entry.getValue());
						}
					}
					HashMap groupMap = (HashMap<String, String>)xmlRpcClient.execute("command.getTaskflowGroups", params);
					result = toListString(listMap,groupMap);
				} else {//查询所有
					result = toListString((HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.list", params), 
							(HashMap<String, String>)xmlRpcClient.execute("command.getTaskflowGroups", params));
				}
				
			}else if("startall".equalsIgnoreCase((String)in[0])){
				result = (String) xmlRpcClient.execute("command.startAllTaskflow", params);
			}else if("stopall".equalsIgnoreCase((String)in[0])){
				result = (String) xmlRpcClient.execute("command.stopAllTaskflow", params);
			}else if("wakeupall".equalsIgnoreCase((String)in[0])){
				result = (String) xmlRpcClient.execute("command.wakeupAllTaskflow", params);				
			}else if("suspendall".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				if("true".equalsIgnoreCase((String)in[1])||"false".equalsIgnoreCase((String)in[1])){
					params = new Object[]{new Boolean((String)in[1])};
					result = (String) xmlRpcClient.execute("command.suspendAllTaskflow", params);
				}else{
					return "第二个参数 只能是true 或者 false";
				}
			}else if("start".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.startTaskflow", params);
			}else if("stop".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.stopTaskflow", params);
			}else if("wakeup".equalsIgnoreCase((String)in[0])||"w".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.wakeupTaskflow", params);				
			}else if("suspend".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 3)
					return "参数错误";
				if("true".equalsIgnoreCase((String)in[2])||"false".equalsIgnoreCase((String)in[2])){
					params = new Object[]{(String)in[1],new Boolean((String)in[2])};
					result = (String) xmlRpcClient.execute("command.suspendTaskflow", params);
				}else{
					return "第三个参数 只能是true 或者 false";
				}
				
			}else if("suspendtask".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 4)
					return "参数错误";
								
				if("true".equalsIgnoreCase((String)in[3])||"false".equalsIgnoreCase((String)in[3])){
					params = new Object[]{(String)in[1],(String)in[2],new Boolean((String)in[3])};
					result = (String) xmlRpcClient.execute("command.suspendTask", params);					
				}else{
					return "第四个参数 只能是true 或者 false";
				}
			}else if("desc".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.descTaskflow", params);
				if(result == null || result.equals(""))
					result = "-";
			}else if("listgroup".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				
				result = toListAllString((HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listTaskflowGroup", params), 
						(HashMap<String, String>)xmlRpcClient.execute("command.getTaskflowGroups", params));
				
			}else if("startgroup".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.startTaskflowGroup", params);
			}else if("stopgroup".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.stopTaskflowGroup", params);
			}else if("wakeupgroup".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.wakeupTaskflowGroup", params);				
			}else if("suspendgroup".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 3)
					return "参数错误";
				
				if("true".equalsIgnoreCase((String)in[2])||"false".equalsIgnoreCase((String)in[2])){
					params = new Object[]{(String)in[1],new Boolean((String)in[2])};
					result = (String) xmlRpcClient.execute("command.suspendTaskflowGroup", params);
				}else{
					return "第三个参数 只能是true 或者 false";
				}
			}else if("listtask".equalsIgnoreCase((String)in[0])||"lt".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length < 2){
					return "参数错误";
				} 
				if (in.length == 2){//只查询任务信息
				params = new Object[]{(String)in[1]};
				result = toListTaskString((HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listTask", params), 
						(String)in[1],3);
				} else {// listtask -s [status]            --listtask taskflow -o [字段Id：1，2，3...] 
					
					if (in.length == 4 && String.valueOf(in[2]).equals("-o")){
						int orderFiledIndex = Integer.parseInt(String.valueOf(in[3]));
						params = new Object[]{(String)in[1]};
						result = toListTaskString((HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listTask", params), 
								(String)in[1],orderFiledIndex-1);
					} else {
						String status = "-1";
						if (in.length == 3){
							status = String.valueOf(in[2]);
						} else {
							status = String.valueOf(in[3]);
						}
						//status: failed(失败),ready(就绪),running(运行),successed(成功),waitting(等待),stopped(停止)
						//status:-1失败,0就绪,1运行,2成功,3等待,4停止
						if ("failed".startsWith(status.toLowerCase())){
							status = "-1";
						} else if ("ready".startsWith(status.toLowerCase())){
							status = "0";
						} else if ("running".startsWith(status.toLowerCase())){
							status = "1";
						} else if ("successed".startsWith(status.toLowerCase())){
							status = "2";
						} else if ("waitting".startsWith(status.toLowerCase())){
							status = "3";
						} else if ("stopped".startsWith(status.toLowerCase())){
							status = "4";
						} 
						ArrayList<String[]> resultList = new ArrayList<String[]>();

						if (in.length == 3){
							HashMap<Integer, HashMap<String, String>> taskflowMap = 
								(HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listall", params);
							for(HashMap flow : taskflowMap.values()){
								String taskflowName = (String)flow.get("taskflow");
								params = new Object[]{taskflowName};
								HashMap<Integer, HashMap<String, String>> resultMap = 
									(HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listTask", params);
								appendTaskInfoList(resultList,resultMap,status,taskflowName);
							}
						} else{
							String taskflowName = (String)in[1];
							params = new Object[]{taskflowName};
							HashMap<Integer, HashMap<String, String>> resultMap = 
								(HashMap<Integer, HashMap<String, String>>)xmlRpcClient.execute("command.listTask", params);
							appendTaskInfoList(resultList,resultMap,status,taskflowName);
						}
						result = toListTaskString2(resultList);
					}
				}
			}else if("updatetime".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 3)
					return "参数错误";
				Date date = null;
				try{
					date = toDate((String)in[2]);
				}catch(Exception e){
					return "日期格式错误";
				}
				params = new Object[]{(String)in[1], date};
				result = (String) xmlRpcClient.execute("command.updateStatTime", params);
			}else if("forceupdatetime".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 3)
					return "参数错误";
				Date date = null;
				try{
					date = toDate((String)in[2]);
				}catch(Exception e){
					return "日期格式错误";
				}
				params = new Object[]{(String)in[1], date};
				result = (String) xmlRpcClient.execute("command.forceUpdateStatTime", params);
			}else if("updatefilelog".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 3)
					return "参数错误";
				String logString = ",DEBUG,INFO,WARN,ERROR,FATAL,OFF,";
				if(logString.indexOf("," + ((String)in[2]).toUpperCase() + ",") == -1){
					return "日志级别只能是DEBUG,INFO,WARN,ERROR,FATAL,OFF";
				}
				params = new Object[]{(String)in[1], ((String)in[2]).toUpperCase()};
				result = (String) xmlRpcClient.execute("command.updateFileLogLevel", params);
			}else if("updatedblog".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 3)
					return "参数错误";
				String logString = ",DEBUG,INFO,WARN,ERROR,FATAL,OFF,";
				if(logString.indexOf("," + ((String)in[2]).toUpperCase() + ",") == -1){
					return "日志级别只能是DEBUG,INFO,WARN,ERROR,FATAL,OFF";
				}
				params = new Object[]{(String)in[1], ((String)in[2]).toUpperCase()};
				result = (String) xmlRpcClient.execute("command.updateDbLogLevel", params);
			}else if("updatethread".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 3)
					return "参数错误";
				try{
					Integer.parseInt((String)in[2]);
				}catch(Exception e){
					return "thread 只能是数字";
				}
				params = new Object[]{(String)in[1], new Integer((String)in[2])};
				result = (String) xmlRpcClient.execute("command.updateThreadnum", params);
			}else if("redo".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 4)
					return "参数错误";
				Date startDate = null;
				Date endDate = null;
				try{
					startDate = toDate((String)in[2]);
					endDate = toDate((String)in[3]);
					if(endDate.before(startDate)){
						return "结束时间必须大于开始时间";
					}
				}catch(Exception e){
					return "日期格式错误";
				}
				params = new Object[]{(String)in[1], startDate, endDate};
				result = (String) xmlRpcClient.execute("command.redo", params);
			}else if("application".equalsIgnoreCase((String)in[0])){
				result = (String) xmlRpcClient.execute("command.application", params);
			}else if("alive".equalsIgnoreCase((String)in[0])){
				result = (String) xmlRpcClient.execute("command.alive", params);
			}else if("version".equalsIgnoreCase((String)in[0])){
				result = (String) xmlRpcClient.execute("command.version", params);
			}else if("reset".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length != 2)
					return "参数错误";
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.resetTaskflow", params);
			}else if("skip".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length < 2)
					return "参数错误";
				if (in.length == 2){
				params = new Object[]{(String)in[1]};
				result = (String) xmlRpcClient.execute("command.skipTaskflow", params);
				} else if (in.length == 3){
					params = new Object[]{(String)in[1],(String)in[2]};
					result = (String) xmlRpcClient.execute("command.skipTask", params);
				}
			}else if("export".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length < 3){
					return "参数错误";
				}
				//if (in.length == 3){
				String path = (String)in[1];
				String taskflows = (String)in[2];
				result = exportTaskflow(path,taskflows.split(","));
				//} 
			}else if("exportall".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length < 2)
					return "参数错误";
				if (in.length == 2){
					String path = (String)in[1];
					result = exportAllTaskflow(path);
				} 
			}else if("upgrade".equalsIgnoreCase((String)in[0])){
				//需封装一个函数
				if(in.length < 2)
					return "参数错误";
				if (in.length == 2){
					String path = (String)in[1];
					result = upgrade(path,"inc");
				}else if (in.length == 3){
					String path = (String)in[1];
					String param = (String)in[2];
					result = upgrade(path,param);
				} 
			}else if("whoami".equalsIgnoreCase((String)in[0])){
				result = "当前引擎的IP为" + ip + ",端口号为" + port;
			}else if("closeserver".equalsIgnoreCase((String)in[0])){
				try{
					result = (String) xmlRpcClient.execute("command.closeserver", params);
				}catch( Exception e){
					result = "成功! closeServer";
				}
			}else if("refresh".equalsIgnoreCase((String)in[0])){  //刷新功能，重新加载系统配置信息和流程信息
				if (in.length == 2){//refresh sysconfig	刷新系统配置信息
					String param2 = (String)in[1];
					if (param2.equals("sysconfig")){//刷新系统配置信息
						params = new Object[]{};
						result = (String) xmlRpcClient.execute("command.refreshSysconfig", params);
					} else {
						return "参数错误";
					}
				}else if (in.length == 3){//refresh taskflow <流程名>	刷新流程信息--从数据库中加载流程信息，流程必须处于停止状态
					String param2 = (String)in[1];
					if (param2.equals("taskflow")){//刷新流程信息
						params = new Object[]{(String)in[2]};
						result = (String) xmlRpcClient.execute("command.refreshTaskflow", params);
					} else {
						return "参数错误";
					}
				} else {
					return "参数错误";
				}
			}else if("querytaskflowtorun".equalsIgnoreCase((String)in[0])){  //刷新功能，重新加载系统配置信息和流程信息
				if (in.length == 2){//refresh sysconfig	刷新系统配置信息
					params = new Object[]{(String)in[1]};
					result = (String) xmlRpcClient.execute("command.queryTaskflowToRun", params);
				} else {
					return "参数错误";
				}
			}else{
				result = in[0] + " not found";
			}
		}catch(Exception e){
			result = e.getMessage();
		}		
		return result;
	}

	private static String exportAllTaskflow(String path) {
		String result = "导出成功!";
		try {
			HashMap<String,String> map = (HashMap<String,String>)xmlRpcClient.execute("command.getConnInfo", new Object[]{});
			FlowMetaData.init(map.get("dbDriver"), map.get("dbUser"), map.get("dbPassword"),
					map.get("dbURL"));
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			flowMetaData.loadBasicInfo();
			flowMetaData.loadAllTaskflowInfo();
			
			boolean b = flowMetaData.zipAllTaskflow(path);
			
		} catch (XmlRpcException e) {
			result = "导出失败,原因:" + e.getMessage();
			
		} catch (MetaDataException e) {
			result = "导出失败,原因:" + e.getMessage();
		} catch (Exception e) {
			result = "导出失败,原因:" + e.getMessage();
		}
		return result;
		
	}
	private static String exportTaskflow(String path,String[] flowNameList) {
		String result = "导出成功!";
		try {
			HashMap<String,String> map = (HashMap<String,String>)xmlRpcClient.execute("command.getConnInfo", new Object[]{});
			FlowMetaData.init(map.get("dbDriver"), map.get("dbUser"), map.get("dbPassword"),
					map.get("dbURL"));
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			flowMetaData.loadBasicInfo();
			flowMetaData.loadAllTaskflowInfo();
			List<Integer> flowIDList = new ArrayList<Integer>();
			for(String flowName:flowNameList){
				flowIDList.add(flowMetaData.queryTaskflow(flowName).getTaskflowID());
			}
			
			boolean b = flowMetaData.zipTaskflow(path,flowIDList);
			
		} catch (XmlRpcException e) {
			result = "导出失败,原因:" + e.getMessage();
			
		} catch (MetaDataException e) {
			result = "导出失败,原因:" + e.getMessage();
		} catch (Exception e) {
			result = "导出失败,原因:" + e.getMessage();
		}
		return result;
		
	}
	
	private static String upgrade(String path, String param){
		
		boolean isAllUpgrade = false;
		if (param.equals("total")){
			isAllUpgrade = true;
		}
		
		String result = "升级成功！请重启引擎，以便让您的更新生效！";
		try {
			HashMap<String,String> map = (HashMap<String,String>)xmlRpcClient.execute("command.getConnInfo", new Object[]{});
			FlowMetaData.init(map.get("dbDriver"), map.get("dbUser"), map.get("dbPassword"),
					map.get("dbURL"));
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			flowMetaData.loadBasicInfo();
			flowMetaData.loadAllTaskflowInfo();
			
			flowMetaData.upgrade(path,isAllUpgrade);
			log.info("已完成流程升级，元数据包为：" + path + ",更新方式：" + (isAllUpgrade?"全量":"增量"));
		} catch (XmlRpcException e) {
			result = "升级失败,原因:" + e.getMessage();
			log.error("元数据包为：" + path + ",更新方式：" + (isAllUpgrade?"全量":"增量") 
					+ ",升级失败，" , e);
		} catch (MetaDataException e) {
			result = "升级失败,原因:" + e.getMessage();
			log.error("元数据包为：" + path + ",更新方式：" + (isAllUpgrade?"全量":"增量") 
					+ ",升级失败，" , e);
		} catch (Exception e) {
			result = "升级失败,原因:" + e.getMessage();
			log.error("元数据包为：" + path + ",更新方式：" + (isAllUpgrade?"全量":"增量") 
					+ ",升级失败，" , e);
		}
		return result;
	}

	private static Object[] trimSpace(Object[] in) {
		List list = new ArrayList();
		for(int i = 0; i < in.length; ++i){
			if(in[i] != null && !in[i].equals("")){
				list.add(in[i]);
			}
		}
		in = list.toArray();
		return in;
	}

	private static String pringInternalHelp() {
		StringBuffer tips = new StringBuffer();
		tips.append(" 以下命令只做测试用途，正式环境慎用! \n");
		
		tips.append("reset").append("\n").append("  使用方式：reset <流程名>\n").append("  说明：重置流程,恢复流程为就绪状态").append("\n");
		tips.append("skip").append("\n").append("  使用方式：skip <流程名> [任务名]\n").append("  说明：跳过指定流程或流程任务的当前进度，即强制当前进度成功，并初始化下一个流程实例；流程必须处于STOP状态才能执行此命令\n").append("  范例：1.跳过指定流程:skip taskflow1 \t2.跳过指定流程的任务：skip taskflow1 task2\n");
		tips.append("forceupdatetime").append("\n").append("  使用方式：forceupdatetime <流程名> <yyyyMMdd(HHmmss)>\n").append("  说明：强制修改指定流程的时间进度,当步长类型为S：秒；MI：分；H：时,(HHmmss)必须输入,\n    当步长类型为D：日；W：周；TD：旬；HM：半月；M：月；SE：季；HY：半年；Y：年,(HHmmss)可以为空\n").append("  范例：forceupdatetime taskflow1 20080808000000").append("\n");
		
		tips.append("export").append("\n").append("  使用方式：export <导出路径和文件名> <流程名>[,流程名1,流程名2...]\n").append("  说明：导出指定流程，导出文件为xml，以zip包存储，多个流程以逗号分隔").append("\n").append("  范例：1.export /opt/aspire/product/etl_bas/ETL_PART_TASKFLOW.zip etl_taskflow1,etl_taskflow2\n");
		tips.append("exportall").append("\n").append("  使用方式：exportall <导出路径和文件名>\n").append("  说明：导出所有流程，导出文件为xml，以zip包存储，多个流程以逗号分隔").append("\n").append("  范例：1.exportall /opt/aspire/product/etl_bas/ETL_ALL_TASKFLOW.zip\n");
		tips.append("upgrade").append("\n").append("  使用方式：upgrade <升级包文件路径和文件名> [total]\n").append("  说明：升级流程，升级包为zip文件,全量更新加上total参数即可").append("\n").append("  范例：1.增量更新: upgrade /opt/aspire/product/etl_bas/ETL_PART_TASKFLOW.zip\n        2.全量更新: upgrade /opt/aspire/product/etl_bas/ETL_ALL_TASKFLOW.zip total\n");

		tips.append("refresh").append("\n").append("  使用方式：refresh sysconfig\n").append("  说明：刷新系统配置信息，从数据库中加载系统配置信息").append("\n");
		tips.append("  使用方式：refresh taskflow <流程名>\n").append("  说明：刷新流程信息，从数据库中加载流程信息，流程必须处于停止状态").append("\n");
		
		tips.append("whoami").append("\n").append("  使用方式：whoami\n").append("  说明：查看当前引擎的IP和端口号").append("\n");
		tips.append("closeserver").append("\n").append("  使用方式：closeserver\n").append("  说明：停止引擎").append("\n");
		return  tips.toString();
	}

	private static String printHelp() {
		StringBuffer tips = new StringBuffer();
		tips.append("list").append("\n").append("  使用方式：list [流程名] [-s status]\n")
		.append("  说明：查看所有流程列表，或指定流程名的流程列表.\n  status: failed(失败),ready(就绪),running(运行),successed(成功),waitting(等待),stopped(停止)")
		.append("\n")
		.append("  范例1：普通查询：list taskflow1 \r\n  范例12：按状态查询：list -s running ").append("\n");
		tips.append("l ").append("\n").append("  使用方式：l [流程名] [-s status]\n").append("  说明：等同命令list").append("\n");

		tips.append("listall").append("\n").append("  使用方式：listall [流程名] [-s status]\n").append("  说明：查看所有流程详细列表，或指定流程名的流程详细列表.\n  status:-1失败,0就绪,1运行,2成功,3等待,4停止").append("\n");
		tips.append("la ").append("\n").append("  使用方式：la [流程名] [-s status]\n").append("  说明：等同命令listall").append("\n");
		
		tips.append("listtask").append("\n")
		.append("  使用方式1：listtask <流程名>").append("\n")
		.append("  说明：查看指定流程的任务列表").append("\n")
		.append("  使用方式2：listtask <流程名> -o <列ID>").append("\n")
		.append("  说明：查看指定流程的任务列表，按某列进行排序，列ID从1开始").append("\n")
		.append("  范例：查看taskflow1的任务列表，并按第1列排序:listtask taskflow1 -o 1").append("\n")
		.append("  使用方式3：listtask [流程名] -s <status>").append("\n")
		.append("  status: failed(失败),ready(就绪),running(运行),successed(成功),waitting(等待),stopped(停止)").append("\n")
		.append("  说明：按状态查看所有流程或指定流程的任务列表").append("\n")
		.append("  范例1：查看taskflow1流程中正在运行的所有任务列表: listtask taskflow1 -s running").append("\n")
		.append("  范例2：查看所有流程中正在运行的所有任务列表:       listtask -s running").append("\n");
		
		tips.append("lt ").append("\n").append("  使用方式：lt\n").append("  说明：等同命令listtask").append("\n");
		tips.append("startall").append("\n").append("  使用方式：startall\n").append("  说明：启动所有流程").append("\n");
		tips.append("stopall").append("\n").append("  使用方式：stopall\n").append("  说明：停止所有流程").append("\n");
		tips.append("suspendall").append("\n").append("  使用方式：suspendall <true or false>\n").append("  说明：禁用所有流程").append("\n");
		tips.append("wakeupall").append("\n").append("  使用方式：wakeupall\n").append("  说明：中断所有流程的休眠").append("\n");
		
		tips.append("start").append("\n").append("  使用方式：start <流程名>\n").append("  说明：启动指定的流程").append("\n");
		tips.append("stop").append("\n").append("  使用方式：stop <流程名>\n").append("  说明：停止指定的流程").append("\n");
		tips.append("suspend").append("\n").append("  使用方式：suspend <流程名> <true or false>\n").append("  说明：禁用指定的流程").append("\n");
		tips.append("wakeup").append("\n").append("  使用方式：wakeup <流程名>\n").append("  说明：中断指定流程的休眠").append("\n");
		tips.append("w ").append("\n").append("  使用方式：w <流程名>\n").append("  说明：等同命令wakeup").append("\n");
		
		tips.append("suspendtask").append("\n").append("  使用方式：suspendtask <流程名> <任务名> <true or false>\n").append("  说明：禁用指定的任务").append("\n");
		
		tips.append("desc").append("\n").append("  使用方式：desc <流程名>\n").append("  说明：查看流程中文说明").append("\n");
		
		/*tips.append("listgroup").append("\n").append("  使用方式：listgroup <流程组名>\n").append("  说明：查看指定的流程组").append("\n");
		tips.append("startgroup").append("\n").append("  使用方式：startgroup <流程组名>\n").append("  说明：启动指定的流程组").append("\n");
		tips.append("stopgroup").append("\n").append("  使用方式：stopgroup <流程组名>\n").append("  说明：停止指定的流程组").append("\n");
		tips.append("suspendgroup").append("\n").append("  使用方式：suspendgroup <流程组名> <true or false>\n").append("  说明：禁用指定的流程组").append("\n");
		tips.append("wakeupgroup").append("\n").append("  使用方式：wakeupgroup <流程组名>\n").append("  说明：中断指定流程组的休眠").append("\n");
		*/
		tips.append("updatetime").append("\n").append("  使用方式：updatetime <流程名> <yyyyMMdd(HHmmss)>\n")
			.append("  说明：修改指定流程的时间进度,当步长类型为S：秒；MI：分；H：时,(HHmmss)必须输入,\n    当步长类型为D：日；W：周；TD：旬；HM：半月；M：月；SE：季；HY：半年；Y：年,(HHmmss)可以为空\n")
			.append("  范例：updatetime taskflow1 20080808000000").append("\n");
		
		tips.append("updatefilelog").append("\n").append("  使用方式：updatefilelog <流程名> <LOG LEVEL>\n")
		.append("  说明：修改指定流程的文件日志级别(取值范围：DEBUG,INFO,WARN,ERROR,FATAL,OFF)\n")
		.append("  范例：updatefilelog taskflow1 DEBUG").append("\n");
		
		tips.append("updatedblog").append("\n").append("  使用方式：updatedblog <流程名> <LOG LEVEL>\n")
		.append("  说明：修改指定流程的数据库日志级别(取值范围：DEBUG,INFO,WARN,ERROR,FATAL,OFF)\n").append("  范例：updatedblog taskflow1 DEBUG")
		.append("\n");
		
		tips.append("updatethread <流程名> <threadnum>").append("  修改线程数").append("\n");
		
		tips.append("redo").append("\n").append("  使用方式：redo <流程名> <yyyyMMdd(HHmmss)> <yyyyMMdd(HHmmss)>\n")
		.append("  说明：重做指定流程,需要设定重做开始的时间进度和重做结束的时间进度,当步长类型为S：秒；MI：分；H：时,(HHmmss)必须输入,\n    当步长类型为D：日；W：周；TD：旬；HM：半月；M：月；SE：季；HY：半年；Y：年,(HHmmss)可以为空\n")
		.append("注意：流程进度为空，不能重做；流程必须处于READY状态才能修改重做信息；修改后还手工启动流程\n").append("  范例：redo taskflow1 20080801000000 20080808000000").append("\n");
		
		tips.append("querytaskflowtorun").append("\n").append("  使用方式：queryTaskflowToRun <流程名>\n").append("  说明：查看流程能否运行").append("\n");
		tips.append("version").append("\n").append("  使用方式：version\n").append("  说明：查看版本").append("\n");
		tips.append("quit").append("\n").append("  使用方式：quit\n").append("  说明：退出").append("\n");
		return tips.toString();
	}

	private static Date toDate(String s) throws ParseException {
		if(s.length() == 8)
			s = s + "000000";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.parse(s);
	}
	
	@SuppressWarnings("unchecked") 
	private static String toListString(HashMap<Integer, HashMap<String, String>> flowMap, HashMap<String, String> groupMap) {
		List<String[]> list = new ArrayList<String[]>();
		String[] ss = new String[]{"流程", /*"流程组","并发度",*/ "周期", "步长", "状态", /*"日志级别(文件/数据库)",*/ "当前进度", "重做区间"};
		list.add(ss);
		ss = new String[]{"----",/* "------", "------",*/"----", "----", "----", /*"---------------------",*/ "--------", "--------"};
		list.add(ss);
		//重新排序
		List<String> keyList = new ArrayList<String>();
		HashMap<String, HashMap<String, String>> sortMap = new HashMap<String, HashMap<String, String>>();
		for(HashMap flow : flowMap.values()){
			keyList.add((String)flow.get("taskflow"));
			sortMap.put((String)flow.get("taskflow"), flow);
		}
		Collections.sort(keyList);
		HashMap<String, String> flow = null;
		for(String key : keyList){
			flow = sortMap.get(key);
			ss = new String[]{
					(String)flow.get("taskflow"), 
					/*"" + groupMap.get((String)flow.get("groupID")),*/
					/*(String)flow.get("threadnum"),*/
					getStepType((String)flow.get("stepType")),
					(String)flow.get("step"),
					transTaskflowStatus((String)flow.get("status")),
					/*(String)flow.get("fileLogLevel") + "/" + (String)flow.get("dbLogLevel"), */
					getTime((String)flow.get("stepType"), (String)flow.get("statTime")),
					getTime((String)flow.get("stepType"), (String)flow.get("redoStartTime")) + " - " + getTime((String)flow.get("stepType"), (String)flow.get("redoEndTime"))
					};
			list.add(ss);
		}
		Integer[] length = {new Integer(0), /*new Integer(0), new Integer(0),*/ new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0)};
		for(String[] line : list){
			for(int i = 0; i < length.length; i++){
				if(line[i].getBytes().length > length[i])
					length[i] = new Integer(line[i].getBytes().length);
			}
		}
		String retString = "";
		for(String[] line : list){
			retString = retString + toLine(line, length) + "\n";
		}
		return retString;
	}
	private static String toListAllString(HashMap<Integer, HashMap<String, String>> flowMap, HashMap<String, String> groupMap) {
		List<String[]> list = new ArrayList<String[]>();
		String[] ss = new String[]{"流程", "流程组","并发度",  "周期", "步长", "禁用", "状态", "日志级别(文件/数据库)", "当前进度", "重做区间"};
		list.add(ss);
		ss = new String[]{"----", "------", "------","----", "----", "----", "----", "---------------------", "--------", "--------"};
		list.add(ss);
		//重新排序
		List<String> keyList = new ArrayList<String>();
		HashMap<String, HashMap<String, String>> sortMap = new HashMap<String, HashMap<String, String>>();
		for(HashMap flow : flowMap.values()){
			keyList.add((String)flow.get("taskflow"));
			sortMap.put((String)flow.get("taskflow"), flow);
		}
		Collections.sort(keyList);
		HashMap<String, String> flow = null;
		for(String key : keyList){
			flow = sortMap.get(key);
			ss = new String[]{(String)flow.get("taskflow"), 
					"" + groupMap.get((String)flow.get("groupID")), 
					(String)flow.get("threadnum"),
					getStepType((String)flow.get("stepType")),
					(String)flow.get("step"),
					getSuspend((String)flow.get("suspend")),
					transTaskflowStatus((String)flow.get("status")),					
					(String)flow.get("fileLogLevel") + "/" + (String)flow.get("dbLogLevel"), 
					getTime((String)flow.get("stepType"), (String)flow.get("statTime")), 
					getTime((String)flow.get("stepType"), (String)flow.get("redoStartTime")) + " - " + getTime((String)flow.get("stepType"), (String)flow.get("redoEndTime"))
					};
			list.add(ss);
		}
		Integer[] length = {new Integer(0), new Integer(0),new Integer(0),  new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0)};
		for(String[] line : list){
			for(int i = 0; i < length.length; i++){
				if(line[i].getBytes().length > length[i])
					length[i] = new Integer(line[i].getBytes().length);
			}
		}
		String retString = "";
		for(String[] line : list){
			retString = retString + toLine(line, length) + "\n";
		}
		return retString;
	}

	private static String toListTaskString(HashMap<Integer, HashMap<String, String>> taskMap, String taskflowName, int orderFieldIndex) {
		List<String[]> list = new ArrayList<String[]>();
		String[] ss = new String[]{"流程", "任务", "禁用", "状态", "类别", "说明"};
		list.add(ss);
		ss = new String[]{"----", "----", "----", "----", "----", "----"};
		list.add(ss);
		
		List<String[]> tasklist  = new ArrayList<String[]>();
		for(HashMap task : taskMap.values()){
			ss = new String[]{taskflowName, 
					(String)task.get("task"), 
					getSuspend((String)task.get("suspend")),
					transTaskStatus((String)task.get("status")),
					(String)task.get("taskType"), 
					(String)task.get("description")};
			tasklist.add(ss);
		}
		
		Collections.sort(tasklist,new TaskComparator(orderFieldIndex));
		
		list.addAll(tasklist);
		
		Integer[] length = {new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0)};
		for(String[] line : list){
			for(int i = 0; i < length.length; i++){
				if(line[i].getBytes().length > length[i])
					length[i] = new Integer(line[i].getBytes().length);
			}
		}
		String retString = "";
		for(String[] line : list){
			retString = retString + toLine(line, length) + "\n";
		}
		return retString;
	}
	
	private static void appendTaskInfoList(List<String[]> list,HashMap<Integer, HashMap<String, String>> taskMap,String status, String taskflowName) {
		String[] ss = null;
		for(HashMap task : taskMap.values()){
			String taskStatus = (String)task.get("status");
			if (taskStatus.equals(status)){  //匹配到相同的状态才放到list中
			ss = new String[]{taskflowName, 
					(String)task.get("task"), 
					getSuspend((String)task.get("suspend")),
					transTaskStatus(taskStatus),
					(String)task.get("taskType"), 
					(String)task.get("description")};
			list.add(ss);
			}
		}
	}
	
	private static String toListTaskString2(List<String[]> list) {
		String[] ss = new String[]{"流程", "任务", "禁用", "状态", "类别", "说明"};
		list.add(0,ss);
		ss = new String[]{"----", "----", "----", "----", "----", "----"};
		list.add(1,ss);
		
		Integer[] length = {new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0)};
		for(String[] line : list){
			for(int i = 0; i < length.length; i++){
				if(line[i].getBytes().length > length[i])
					length[i] = new Integer(line[i].getBytes().length);
			}
		}
		String retString = "";
		for(String[] line : list){
			retString = retString + toLine(line, length) + "\n";
		}
		return retString;
	}
	
	private static String getSuspend(String s) {
		String suspend = "-";
		if("0".equals(s))
			suspend = "否";
		else if("1".equals(s))
			suspend = "是";
		return suspend;
	}
	
	private static String transTaskflowStatus( String s) {
		if("-".equals(s))
			return s;
		
		String status = "-";
		if("0".equals(s)){
			status = "就绪";}
		else if("1".equals(s)){
			status = "运行";}
		else if("2".equals(s)){
			status = "成功";}
		else if("3".equals(s)){
			status = "等待";}
		else if("4".equals(s)){
			status = "停止";}
		else if("-1".equals(s)){
			status = "失败";	}
		
		return status;
	}
	private static String transTaskStatus( String s) {
		if("-".equals(s))
			return s;
		
		String status = "-";
		if("0".equals(s)){
			status = "就绪";}
		else if("1".equals(s)){
			status = "运行";}
		else if("2".equals(s)){
			status = "成功";}
		else if("3".equals(s)){
			status = "排队";}
		else if("4".equals(s)){
			status = "停止";}
		else if("-1".equals(s)){
			status = "失败";	}
		
		return status;
	}
	private static String getTime(String stepType, String s) {
		if("S".equals(stepType) || "MI".equals(stepType) || "H".equals(stepType)){//时以上
			return s;
		}else{//日以上
			if(s != null && s.length() > 10){
				return s.substring(0, 10);
			}else{
				return s;
			}
		}
	}
	
	private static String getStepType(String s) {
		String stepType = "未知";
		if("S".equals(s))
			stepType = "秒";
		else if("MI".equals(s))
			stepType = "分";
		else if("H".equals(s))
			stepType = "时";
		else if("D".equals(s))
			stepType = "日";
		else if("W".equals(s))
			stepType = "周";
		else if("TD".equals(s))
			stepType = "旬";
		else if("HM".equals(s))
			stepType = "半月";
		else if("M".equals(s))
			stepType = "月";
		else if("SE".equals(s))
			stepType = "季";
		else if("HY".equals(s))
			stepType = "半年";
		else if("Y".equals(s))
			stepType = "年";
		return stepType;
	}

	private static String toLine(String[] ss, Integer[] length){
		if(ss.length != length.length)
			return "长度错误";
		StringBuffer line = new StringBuffer("                                                                                                                                                            ");
		int totleLength = 0;
		for(int i = 0; i < ss.length; i++){
			line.insert(totleLength, ss[i]);
			totleLength = totleLength + length[i] + 2 - ss[i].getBytes().length + ss[i].length();//line.toString().getBytes().length + line.length();
		}
		return line.substring(0, totleLength);
	}

	
}
