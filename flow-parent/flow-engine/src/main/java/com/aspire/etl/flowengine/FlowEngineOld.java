/*
 * FlowEngine.java
 *
 * Created on 2008-1-25 17:03:19 by luoqi
 *
 * Copyright (c) 2001-2008 ASPire Technologies, Inc.
 * 6/F,IER BUILDING, SOUTH AREA,SHENZHEN HI-TECH INDUSTRIAL PARK Mail Box:11# 12#.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ASPire Technologies, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Aspire.
 */
package com.aspire.etl.flowengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.aspire.etl.flowdefine.Category;
import com.aspire.etl.flowdefine.SysConfig;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskAttribute;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowengine.xmlrpc.Server;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.Utils;
import com.aspire.etl.tool.XmlConfig;

/**
 * @author ����
 * @since 2008-3-19
 *        <p>
 *        ���������ִ����ڣ�server logger ���ڼ�¼��������־ ���̳�ʼ���ú�ÿ�����̶�����һ��ͬ��logger�����ڼ�¼������־��
 *        ������־�������̱������趨������������ļ���Ҳ������������ݿ⡣
 * 
 * @author ����
 * @since 2008-3-24
 *        <p>
 *        1.���Ӷ�java������������Ĵ���
 * 
 * @author ����
 * @since 2008-3-25
 *        <p>
 *        1.���°���Ŀ¼�ṹ�����Ķ������ļ���ʼ��·��
 * 
 * @author ����
 * @since 2008-3-30
 *        <p>
 *        1.�����ڲ�������ⲿ��������ⲿ�����������ȫ����pluginsĿ¼�£������޸�ϵͳ���еĲ�����á�
 *        
 * @author wuzhuokun
 * @since 2008-5-22
 *        <p>
 *        1.����rpc��������
 *  
 *         *        
 * @author ���棬���
 * @since 2009-7-1
 *        <p>
 *        1.�޸�����ͼ�����㷨��ʹ����ͼ�������ɲ������У������ò�������
 *        2.���Ӷ����̴�ٵ�֧�֡�
 *          �������̴��������̱��������С�
 *          
 * @author ���棬���
 * @since 2009-7-3 
 *        <p>
 *        1.�޸�start��stop�����start��Ϊʵ�������̣߳�stop��Ϊʵ��ֹͣ�̡߳�
 *        2.����suspend������ܣ��޸�suspend��־λ��
 *        3.��װFlowEngine�����п������������ֱ�ӵ�������������ԡ��������ƣ���Ҫ�������������������������ι�������ʱ���ţ�
 * @author ����
 * @since 2010-1-13 
 *        <p>
 *        1.�޸��״����вŸ��²���Ŀ¼��Ԥ����sysconfig�����˹��������޷��޸ĵ��������޷����е�bug��
 *        2.����һ�����ؿ�ѡ����<metadataPath>��FlowEngine.xml��,��������DB����������������debug��
 *              
 */
public class FlowEngineOld {
	private static final String FLOW_ENGINE = "FlowEngine";

	private static final String SERVER_XML = "./cfg/FlowEngine.xml";
	
	static FlowMetaData flowMetaData = null;
	
	private static int XMLRPC_PORT = 8080;
	
	private static String XMLRPC_USERNAME = "username";
	
	private static String XMLRPC_PASSWORD = "password";

	static Logger log;
	
	public static Map<String,TaskflowThread> taskflowThreadPool = new HashMap<String,TaskflowThread>(); 
	
	static FlowEngineOld flowEngine = null;
	
	private boolean engineeringMode = false;
	
	static  int redoTimes = 0;
		
	private FlowEngineOld(){
		
	}
	
	public static FlowEngineOld getInstance(){
		if(flowEngine==null){
			flowEngine =  new FlowEngineOld();	
		}
		return flowEngine;
	}
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		

//        DBTools tool = new DBTools();
//        tool.executeUpdate("DELETE FROM RPT_LINK WHERE ID_LINK IN (51)");  
//        tool.executeUpdate("DELETE FROM RPT_TASK_ATTRIBUTE WHERE ID_TASK IN (8, 9)");
//        tool.executeUpdate("DELETE FROM RPT_TASK_TEMPLATE WHERE ID_TASK IN (8, 9)");
//        tool.executeUpdate("DELETE FROM RPT_TASK WHERE ID_TASK IN (8, 9)");
//        tool.executeUpdate("DELETE FROM RPT_TASKFLOW_TEMPLATE WHERE ID_TASKFLOW = 5");
//        tool.executeUpdate("DELETE FROM RPT_TASKFLOW WHERE ID_TASKFLOW = 5");
//        tool.close();  
		
		/*XmlConfig config = new XmlConfig(SERVER_XML);
		
		FlowEngine flowEngine = FlowEngine.getInstance();
		flowEngine.init(config.readSingleNodeValue("//config/jdbc/driver")
				, config.readSingleNodeValue("//config/jdbc/user")
				, config.readSingleNodeValue("//config/jdbc/password")
				, config.readSingleNodeValue("//config/jdbc/url")
				, config.readSingleNodeValue("//config/level"));
		String st = config.readSingleNodeValue("//config/redoTimes");
		Task.LOGDIR = config.readSingleNodeValue("//config/logDir");
		redoTimes =Integer.valueOf(st.isEmpty()?"0":st);*/
		flowEngine = FlowEngineOld.getInstance();
		String confPath = "/Users/chenhaitao/Documents/workspace/flow/flow-parent/flow-engine/src/main/resources/cfg";
		FlowCfg cfg = FlowEngineCfgLoader.load(confPath);
		Map<String, String> configs = cfg.getConfigs();
		flowEngine.init(configs.get("jdbc.driver"),
				configs.get("jdbc.username"),
				configs.get("jdbc.password"),
				configs.get("jdbc.url"),
				configs.get("level"));
		flowEngine.start();
	}
	

	public void start() throws Exception{
		/*ZkSessionManager sessionManager = new DefaultZkSessionManager("192.168.98.143:2181",3000); 
		Lock myLock = new ReentrantZkLock("/test-locks",sessionManager);*/
		//TODO DY RCP�ӿ�disable��
		log.info("��������Ƿ�������");	
		/*if(flowEngine.isStarted()){
			log.error("�Ѿ�������������,Ĭ��ֻ������������");	
			return;
		}*/
		
		log.info("����������");
		initExternalPlugins();

		//TODO DY RCP�ӿ�disable��
		//log.info("����������");
		startRpcServer();
		
		log.info("��������");
		//myLock.lock();
		startServer();
		
		//д����Ŀ¼
		//Ϊ��ֹ��Ϊ����סRPT_SYS_CONFIG������������ʧ�ܣ������̷߳�ʽ�����¡�
		Thread updateProgPath = new Thread(){
			public void run(){
				try{
					String path = System.getProperty("user.dir");
					if(path != null && !"".equals(path)){
						path = path.substring(0, path.lastIndexOf(System.getProperty("file.separator")));
					}
					SysConfig sc = flowMetaData.querySysConfig("SYS_PROG_PATH");
					if(sc.getConfigValue() == null||sc.getConfigValue()==""){
						log.info("�״����и��²���Ŀ¼");
						sc.setConfigValue(path);
						flowMetaData.update(sc);
					}
				}catch (Exception e){
				}
			}
		};
		updateProgPath.start();
		
		log.info("�������");
	}

	private boolean isStarted() {
		//�ж��Ƿ�����������
		try{
			String IP = "";
			Socket socket = null;
			if(engineeringMode){
				IP = "127.0.0.1";
				socket = new Socket(IP, Integer.parseInt(flowMetaData.querySysConfigValue("XMLRPC_PORT")));
			}else{
				
				IP = flowMetaData.querySysConfigValue("SYS_COMPUTER_IP");
				socket = new Socket("127.0.0.1", Integer.parseInt(flowMetaData.querySysConfigValue("XMLRPC_PORT")));
			}
			if(socket.isConnected()){
				//������,�����Ѿ�������,�˳�
				System.out.println("�Ѿ�������������(" + IP + ":" + flowMetaData.querySysConfigValue("XMLRPC_PORT") + "),Ĭ��ֻ������������");
				
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
			//����,��������
		}
	}
	

	public void init(String dbDriver, String dbUser, String dbPassword,
			String dbURL, String logLevel) throws FileNotFoundException, DocumentException {
		
		log = Utils.initFileLog(FLOW_ENGINE, Task.LOGDIR, FLOW_ENGINE, logLevel); //ԭд����Ŀ¼�ĳɿ����� hqw
		log.info("\n\n");
		log.info("��ʼ��������......");
		
		log.info("��ʼ��Ԫ���ݿ�");

		try {
			FlowMetaData.init(dbDriver, dbUser, dbPassword, dbURL + "?useUnicode=true&amp;characterEncoding=utf-8");
			flowMetaData = FlowMetaData.getInstance();
			
			//���ػ���Ԫ����
			//flowMetaData.loadBasicInfo();//init�Ѿ����ع���,����û��Ҫ�ټ���һ��  by�º��� 2017-09-19

			// ����ȫ������
			flowMetaData.loadAllTaskflowInfo();
			
			//Ӳ�������task attribut
			//flowMetaData.insert(new TaskAttribute (1, 2, "attr-1-key", "attr-1-value"));
			
			//TODO DY Ӳ�����޸�������Ϣ
			//flowMetaData.updateTaskStatus(2, Task.READY);
			//flowMetaData.updateTaskStatus(3, Task.READY);
			flowMetaData.updateStatTimeOfTaskflow(4, new Date());

		} catch (MetaDataException e) {
			log.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		
	}	
	
	//TODO DY deprecated������ɾ��
	@Deprecated
	private void init2(String logLevel,String ip, String port, String db_alias,String metadataPath) throws FileNotFoundException, DocumentException {
			
		// ��������־û�г�ʼ����֮ǰ���ȳ�ʼ��һ��������logger	
		log = Utils.initFileLog(FLOW_ENGINE, "./log", FLOW_ENGINE, logLevel);
		log.info("\n\n");
		log.info("��ʼ��������......");
		
		try {
			//FlowMetaData.init("oracle.jdbc.driver.OracleDriver", "report_1", "report_1", "jdbc:oracle:thin:@10.1.3.105:1521:ora9i");
			log.info("��ʼ��Ԫ���ݿ�");
			if(metadataPath==null||metadataPath.equals("")){
				FlowMetaData.init(ip,port,db_alias);
			}else{
				engineeringMode = true;
				System.out.println("���˹���ģʽ��ʵ������ʱ���ܻ����ļ�ϵͳ����������Ԫ���ݿ⣡");
				log.warn("���˹���ģʽ��ʵ������ʱ���ܻ����ļ�ϵͳ����������Ԫ���ݿ⣡");
				FlowMetaData.init(metadataPath);
			}
			flowMetaData = FlowMetaData.getInstance();
			
			//���ػ���Ԫ����
			flowMetaData.loadBasicInfo();

			// ����ȫ������
			flowMetaData.loadAllTaskflowInfo();
			
			if(engineeringMode){
				updateAllTaskflowToSingelTask();
			}

		} catch (MetaDataException e) {
			log.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	private void startRpcServer() throws Exception {
		//Server rpc = new Server();
		try{
			XMLRPC_PORT = Integer.parseInt(flowMetaData.querySysConfigValue("XMLRPC_PORT"));
			log.info("�����˿ڣ�"+XMLRPC_PORT);
		}catch(Exception e){
			log.error(e);
			log.warn("��ȡXMLRPC_PORTʧ��,ϵͳ��ʹ��Ĭ��ֵ" + XMLRPC_PORT);
		}
		if(flowMetaData.querySysConfigValue("XMLRPC_USERNAME") == null)
			log.warn("��ȡXMLRPC_USERNAME,ϵͳ��ʹ��Ĭ��ֵ" + XMLRPC_USERNAME);
		else
			XMLRPC_USERNAME = flowMetaData.querySysConfigValue("XMLRPC_USERNAME");
		if(flowMetaData.querySysConfigValue("XMLRPC_PASSWORD") == null)
			log.warn("��ȡXMLRPC_PASSWORD,ϵͳ��ʹ��Ĭ��ֵ" + XMLRPC_PASSWORD);
		else
			XMLRPC_PASSWORD = flowMetaData.querySysConfigValue("XMLRPC_PASSWORD");
		/*rpc.init(XMLRPC_PORT, XMLRPC_USERNAME, XMLRPC_PASSWORD);
		
		rpc.start();*/
	}
	
	private void initExternalPlugins() throws FileNotFoundException,
			DocumentException {
		File[] childDirArray = Utils.scanChildDir("./plugins");

		// �����ȡplugin.xml
		for (File pluginDir : childDirArray) {
			File[] pluginFileArray = pluginDir.listFiles(new FilenameFilter() {
				public boolean accept(File f, String name) {
					if (name.equalsIgnoreCase("plugin.xml")) {
						return true;
					}
					return false;
				}
			});

			if (pluginFileArray != null && pluginFileArray.length !=0 ) {
				if (pluginFileArray[0] != null) {
					String pluginFile = pluginFileArray[0].getAbsolutePath();

					XmlConfig pluginFileConfig = new XmlConfig(pluginFile);
					String name = pluginFileConfig
							.readSingleNodeValue("//plugin/name");
					String description = pluginFileConfig
							.readSingleNodeValue("//plugin/description");
					String category = pluginFileConfig
							.readSingleNodeValue("//plugin/category");
					String type = pluginFileConfig
							.readSingleNodeValue("//plugin/type");
					String classname = pluginFileConfig
							.readSingleNodeValue("//plugin/classname");
					String library = pluginFileConfig
							.readSingleNodeValue("//plugin/library");

					TaskType taskTypeBean = flowMetaData.queryTaskType(name);
					if (taskTypeBean == null) {
						TaskType tmpTaskType = new TaskType();
						tmpTaskType.setTaskTypeID(Utils.getRandomIntValue());
						tmpTaskType.setTaskType(name);

						Category categoryBean = flowMetaData
								.queryCategory(category);
						if (categoryBean == null) {
							// ������಻����,�½�һ����ʱCategory
							Category tmpCategory = new Category();
							tmpCategory.setID(Utils.getRandomIntValue());
							tmpCategory.setName(category);
							tmpCategory.setOrder(99);

							// ���浽�ڴ棬���ǲ����浽Ԫ���ݿ���
							flowMetaData.insert(tmpCategory);

							tmpTaskType.setCategoryID(tmpCategory.getID());
						} else {
							// ��������Ѵ���
							tmpTaskType.setCategoryID(categoryBean.getID());
						}
						tmpTaskType.setDescription(description);
						tmpTaskType.setEnginePlugin(classname);
						tmpTaskType.setEnginePluginJar(pluginDir.getPath()+"/"+library);						
						tmpTaskType.setEnginePluginType(type);

						// ���浽�ڴ棬���ǲ����浽Ԫ���ݿ���
						flowMetaData.insert(tmpTaskType);
					} else {
						String msg = "Ԫ���ݿ����Ѿ�������Ϊ\"" + name + "\"�Ĳ�����뽫��Ĳ��������";
						log.error(msg);
						System.out.println(msg);
						System.exit(0);
					}
				}

			}
		}
	}
	private void initLoggerForAllTaskflow(){
		//����������
		List<Taskflow> taskflowList = flowMetaData.queryAllTaskflow();	
		log.info("��ʼ����"+taskflowList.size()+"�����̡�");
		// Ϊÿ�����̳�ʼ��һ��logger
		MyConfigurator.configure(taskflowList,flowMetaData);
		
	}
	
	private void updateAllTaskflowToSingelTask(){
		//����������
		List<Taskflow> taskflowList = flowMetaData.queryAllTaskflow();	
		log.info("���������̶���ʱ��Ϊ������ִ�е�ģʽ��");
		// Ϊÿ�����̳�ʼ��һ��logger
		for (Taskflow taskflow : taskflowList) {
			taskflow.setThreadnum(1);
		}		
	}
	private void startServer() {
			log.info("Ϊÿһ�����̶�׼����logger");
			initLoggerForAllTaskflow();
			
			// �����в����ڴ�����̽M�ķǽ�������
			log.info("��Ԫ���ݿ���������δ�����õ����̡����������̳��⡣");
			List<Taskflow> taskflowList = flowMetaData.queryAllSuspendNoTaskflowAndNotInOutlineGroup();		
			
			// ÿ����������һ���߳�
			for (Taskflow taskflow : taskflowList) {
				if(taskflow.getStatus()==Taskflow.STOPPED){
					log.info(taskflow.getTaskflow()+"����״̬��STOPPED�����治�Զ����������̣����ֹ�start");
				}else{
					log.info("��������"+taskflow.getTaskflow());
					startNewTaskflowThread(taskflow);
				}
				
			}

	}
	
	public TaskflowThread startNewTaskflowThread(String taskflowName) {
		Taskflow taskflowToStart = flowMetaData.queryTaskflow(taskflowName);
		if(taskflowToStart!=null){					
			return startNewTaskflowThread(taskflowToStart);
		}else{
			return null;
		}
	}

	private TaskflowThread startNewTaskflowThread(Taskflow taskflow) {
		TaskflowThread taskflowContext = null;
		try {
			taskflowContext = new TaskflowThread(taskflow);
			
			taskflowContext.setName(taskflow.getTaskflow());
			taskflowContext
					.setUncaughtExceptionHandler(new SimpleThreadExceptionHandler());

			//���������������STOPPED������ĸ�ΪREADY
			flowMetaData.resetTaskStatusByTaskflowID(taskflow.getTaskflowID(),Task.STOPPED,Task.READY);
			
			taskflowContext.start();
			
			//ÿ��Ϊһ��������������һ�����̣߳��ͷ���Map�У��Ժ�������������ҳ�����Ӧ���̶߳�������
			taskflowThreadPool.put(taskflow.getTaskflow(), taskflowContext);
			
		} catch (Exception e) {
			log.error("����" + taskflow + "����ʧ�ܣ�" + e);
			e.printStackTrace();
		}
		return taskflowContext;
	}

	class SimpleThreadExceptionHandler implements
			Thread.UncaughtExceptionHandler {

		public void uncaughtException(Thread t, Throwable e) {
			System.out.printf("UncaughtException: [%s]%s at line %d of  %s %n", t.getName(), e
					.toString(), e.getStackTrace()[0].getLineNumber(), e
					.getStackTrace()[0].getFileName());

			log.error(e);
		}

	}

	public static Logger getLog() {
		return log;
	}
}