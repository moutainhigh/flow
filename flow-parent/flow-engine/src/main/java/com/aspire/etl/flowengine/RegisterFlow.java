package com.aspire.etl.flowengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.dom4j.DocumentException;

import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.XmlConfig;
import com.aspire.flow.api.MasterSlaveApiFactory;
import com.aspire.flow.api.data.MasterSlaveJobData;
import com.aspire.flow.api.impl.MasterSlaveApiFactoryImpl;
import com.aspire.flow.cluster.startup.BootStrap;
import com.aspire.flow.entity.MasterSlaveJobSummary;
import com.aspire.flow.exception.FlowException;
import com.aspire.flow.helper.LoggerHelper;
import com.aspire.flow.helper.ReflectHelper;
import com.aspire.flow.support.Config;

import net.sf.json.JSONObject;

/**
 * 将任务流注册到zk
 * 
 * @author chenhaitao
 *
 */
public class RegisterFlow extends AbstractConfig{
	private static final String SERVER_XML = "./cfg/FlowEngine.xml";
	static FlowMetaData flowMetaData = null;
	static RegisterFlow registerFlow = null;
	private static final int DEFAULT_SESSION_TIMEOUT_MS = Integer.getInteger("curator-default-session-timeout",
			60 * 1000);

	private static final int DEFAULT_CONNECTION_TIMEOUT_MS = Integer.getInteger("curator-default-connection-timeout",
			15 * 1000);
	private static final RetryPolicy DEFAULT_RETRY_POLICY = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);

	public static void main(String[] args) throws FileNotFoundException, DocumentException {
		XmlConfig config = new XmlConfig(SERVER_XML);
		registerFlow = RegisterFlow.getInstance();
		registerFlow.init(config.readSingleNodeValue("//config/jdbc/driver"), config.readSingleNodeValue("//config/jdbc/user"),
				config.readSingleNodeValue("//config/jdbc/password"), config.readSingleNodeValue("//config/jdbc/url"));
	}

	public void init(String dbDriver, String dbUser, String dbPassword, String dbURL) {
		try {
			FlowMetaData.init(dbDriver, dbUser, dbPassword, dbURL + "?useUnicode=true&amp;characterEncoding=utf-8");
			flowMetaData = FlowMetaData.getInstance();
			// 加载全部流程
			flowMetaData.loadAllTaskflowInfo();
			LoggerHelper.info("开始将数据库中的任务流添加到zk...");
			LoggerHelper.info("从元数据库中搜索出未被禁用的流程。大纲组的流程除外。");
			List<Taskflow> taskflowList = flowMetaData.queryAllSuspendNoTaskflowAndNotInOutlineGroup();
			MasterSlaveJobSummary jobSummary = new MasterSlaveJobSummary();
			//初始化zk,服务端配置信息
			init();
			String connectStr = config.getZookeeperAddresses();
			CuratorFramework client = CuratorFrameworkFactory.newClient(connectStr, DEFAULT_SESSION_TIMEOUT_MS,
					DEFAULT_CONNECTION_TIMEOUT_MS, DEFAULT_RETRY_POLICY);
			client.start();
			for (Taskflow taskflow : taskflowList) {
				if(taskflow.getStatus()==Taskflow.STOPPED){
					LoggerHelper.info(taskflow.getTaskflow()+"流程状态是STOPPED，引擎不自动启动该流程，请手工start");
				}else{
					Vector<Object> populateParam = new Vector<Object>();
					jobSummary.setFlowId(taskflow.getTaskflowID() + "");
					jobSummary.setStatus(taskflow.getStatus() + "");
					jobSummary.setGroupName(taskflow.getTaskflowID() + "");
					jobSummary.setJobOperation("Start");
					jobSummary.setExcuteStatus("Start");
					populateParam.add(JSONObject.fromObject(taskflow).toString());
					jobSummary.setParams(populateParam);
					MasterSlaveApiFactory masterSlaveApiFactory = new MasterSlaveApiFactoryImpl(client);
					MasterSlaveJobData masterSlaveJobData = masterSlaveApiFactory.jobApi().getJob(jobSummary.getFlowId(),
							jobSummary.getJobName());
					MasterSlaveJobData.Data data;
					if (masterSlaveJobData == null) {
						data = new MasterSlaveJobData.Data();
					} else {
						data = masterSlaveJobData.getData();
					}
					ReflectHelper.copyFieldValuesSkipNull(jobSummary, data);
					masterSlaveApiFactory.jobApi().saveJob(jobSummary.getFlowId(), jobSummary.getJobName(), data);
				}
			}
			LoggerHelper.info("任务流添加到zk结束...");
		} catch (MetaDataException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static RegisterFlow getInstance(){
		if(registerFlow==null){
			registerFlow =  new RegisterFlow();	
		}
		return registerFlow;
	}
}
