/*
 * FlowMetaData.java
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
package com.aspire.etl.flowmetadata.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.dom4j.DocumentException;

import com.aspire.etl.flowdefine.Category;
import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.Note;
import com.aspire.etl.flowdefine.Right;
import com.aspire.etl.flowdefine.Role;
import com.aspire.etl.flowdefine.RoleRight;
import com.aspire.etl.flowdefine.StepType;
import com.aspire.etl.flowdefine.SysConfig;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskAttribute;
import com.aspire.etl.flowdefine.TaskHistory;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowdefine.TaskflowGroup;
import com.aspire.etl.flowdefine.TaskflowUser;
import com.aspire.etl.flowdefine.User;
import com.aspire.etl.flowdefine.UserRole;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.ZipUtil;
import com.aspire.etl.tool.ConnInfo;
import com.aspire.etl.tool.Connect;
import com.aspire.etl.tool.SqlMapUtil;
import com.aspire.etl.tool.TimeUtils;
import com.aspire.etl.tool.Utils;

/**
 * 
 * 提供访问所有ETL元数据的增删改插方法。 单例，保证只有一份数据。 因为会有多个线程同时调用，所以所有方法都是synchronized。
 * 不处理异常，都抛给调用者处理，便于调用者统一写日志。 load类的方法从xml文件或数据库里将元数据加载到内存。
 * query类的方法是提供给调用者的查询接口，从内存中查询。
 * update类的方法直接将元数据更新回xml文件或数据库，再更新内存。流程引擎更新进度和状态时用。
 * insert类的方法是提供给调用者的新增接口，单个插入到内存。而后由设计器触发按流程统一保存。 delete类的方法是从内存中删除元数据。
 * save类的方法将内存中的元数据保存到xml文件或数据库里。都是先将目的地的同ID数据删除，再插入，
 * 多人同时操作同一个元数据库时后面的人会覆盖前面的人！！ 新增add方法(直接对单条记录保存到xml文件或数据库。（wuzhuokun 20080429）
 * （按目前的设计，设计器不能和引擎同时连接一个元数据库，否则设计器保存会覆盖引擎的更新，此处需要改进）
 * 
 * @author 罗奇
 * @since 2008-01-25
 * 
 *        调用者： 流程设计器，流程引擎，流程监控器
 * 
 * @author 罗奇
 * @since 2008-03-27
 *        <p>
 *        1.改为独立打包，以jar包形式提供给流程设计器，流程引擎，流程监控器等使用。
 * 
 * @author wuzhuokun
 * @since 2008-05-08
 *        <p>
 *        1.增加方法querySysConfigMap(String stage)stage匹配的系统参数。
 *        2.增加加载单个taskflow的方法: public synchronized Taskflow loadTaskflow(Integer
 *        taskflowID)
 * @author wuzhuokun
 * @since 2008-05-22
 *        <p>
 *        1.修改更新数据库时同时更新内存
 * @author wuzhuokun
 * @since 2008-06-02
 *        <p>
 *        1.增加两方法queryAllTaskflowStatus() queryTaskflowGroupStatus(Integer
 *        groupID)
 * 
 * @author 罗奇，李宝钰
 * @since 2009-07-2
 *        <p>
 *        1.增加根据大纲判断流程是否能运行的方法isTaskflowCanRunJudgeByOutline(Taskflow taskflow)
 * 
 * @author jiangts
 * @since 2010-07-28
 *        <p>
 *        1.修复了queryTaskAttributeDynMap方法的bug，该方法原来只替换了一个动态变量值，后面的动态变量未替换成实际值
 * 
 */
public class FlowMetaData {

	private static FlowMetaData singleFlowMetaData = null;

	// ETL流程元数据的内存映射，load类方法将相关数据都加载进来，运行过程中 先更新数据库或xml文件，再更新这里。
	// 流程的信息
	HashMap<Integer, Taskflow> taskflowMap = new HashMap<Integer, Taskflow>();

	// 任务的信息
	HashMap<Integer, Task> taskMap = new HashMap<Integer, Task>();

	// 任务类型信息
	HashMap<Integer, TaskType> taskTypeMap = new HashMap<Integer, TaskType>();

	// 周期类型信息
	HashMap<String, StepType> stepTypeMap = new HashMap<String, StepType>();

	// 任务链接信息
	HashMap<Integer, Link> linkMap = new HashMap<Integer, Link>();

	// 任务参数信息
	HashMap<Integer, TaskAttribute> taskAttributeMap = new HashMap<Integer, TaskAttribute>();

	// 系统参数信息
	HashMap<Integer, SysConfig> sysConfigMap = new HashMap<Integer, SysConfig>();

	HashMap<Integer, Note> noteMap = new HashMap<Integer, Note>();

	// 组件面板的任务分类信息
	HashMap<Integer, Category> categoryMap = new HashMap<Integer, Category>();

	// 组件面板的任务分类信息
	HashMap<Integer, TaskflowGroup> taskflowGroupMap = new HashMap<Integer, TaskflowGroup>();

	HashMap<String, User> userMap = new HashMap<String, User>();

	HashMap<Integer, Role> roleMap = new HashMap<Integer, Role>();

	HashMap<Integer, Right> rightMap = new HashMap<Integer, Right>();

	HashMap<String, UserRole> userRoleMap = new HashMap<String, UserRole>();

	HashMap<String, RoleRight> roleRightMap = new HashMap<String, RoleRight>();

	HashMap<Integer, TaskflowUser> taskflowUserMap = new HashMap<Integer, TaskflowUser>();
	private Vector<Object> populateParam;
	// 元数据库的DAO
	private static SynDao dao = null;

	// 连接信息串
	private static String metaDataConectionInfo = "";

	// 连接信息
	private static ConnInfo connInfo = null;

	// 密码安全管理ip
	private static String securityIP = "";

	// 密码安全管理端口
	private static String securityPort = "0";

	// 大纲组ID,初始化大纲组的时候就定的是0.
	public static int outlineGroupId = 0;

	// 目录连接方式：元数据从目录中取
	public static int DIRECTORY_CONNECTION = 0;

	// 数据库直接连接方式：元数据从数据库中取
	public static int DB_CONNECTION = 1;

	// 代理连接方式：元数据从目录中取
	public static int PROXY_CONNECTION = 2;

	// 元数据连接方式：
	private static int metadataConnectType = 0;

	private FlowMetaData() {

	}

	/**
	 * 获取FlowMetaData的实例
	 * 
	 * @return
	 * @throws Exception
	 */
	public static FlowMetaData getInstance() throws Exception {
		if (singleFlowMetaData == null) {
			// init();
			throw new Exception("请先调用init()方法!");
		}
		return singleFlowMetaData;
	}

	/**
	 * 用默认路径下的配置文件初始化FlowMetaData，getInstance()之前应该先初始化。
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws Exception
	 * @throws MetaDataException
	 */
	// public static void init() throws FileNotFoundException,
	// DocumentException,
	// Exception, MetaDataException {
	// init("./cfg/metadata.xml");
	// }
	/**
	 * 用指定路径初始化FlowMetaData，getInstance()之前应该先调用此方法。
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws Exception
	 * @throws MetaDataException
	 */
	public static void init(String metadataPath)
			throws FileNotFoundException, DocumentException, Exception, MetaDataException {
		singleFlowMetaData = new FlowMetaData();
		dao = new XmlDaoImpl(metadataPath);
		singleFlowMetaData.loadBasicInfo();
		// 文件系统增加默认admin用户
		User user = new User("admin", "admin", "admin");
		singleFlowMetaData.userMap.put("admin", user);
		// 判断有没有taskflow文件夹
		File file = new File(metadataPath + "/taskflow");
		if (!file.exists()) {
			file.mkdirs();
		}
		// 判断有没有common文件夹
		file = new File(metadataPath + "/common");
		if (!file.exists()) {
			file.mkdirs();
		}

		metaDataConectionInfo = metadataPath;

		metadataConnectType = DIRECTORY_CONNECTION;
	}

	/**
	 * 用指定数据库连接信息初始化FlowMetaData，getInstance()之前应该先调用此方法。
	 * 
	 * @param dbDriver
	 * @param dbUser
	 * @param dbPassword
	 * @param dbURL
	 * @throws MetaDataException
	 * @throws Exception
	 */
	public static void init(String dbDriver, String dbUser, String dbPassword, String dbURL) throws MetaDataException {
		connInfo = new ConnInfo();
		connInfo.setDbDriver(dbDriver);
		connInfo.setDbUser(dbUser);
		connInfo.setDbPassword(dbPassword);
		connInfo.setDbURL(dbURL);
		singleFlowMetaData = new FlowMetaData();
		try {
			dao = new DBDaoImpl(connInfo, "ibatis.xml");
			singleFlowMetaData.loadBasicInfo();
		} catch (Exception e) {

			e.printStackTrace();

			singleFlowMetaData = null;
			if (e instanceof SQLException) {
				throw new MetaDataException(e.getMessage() + " 数据库连接失败!!" + dbDriver + " " + dbURL + " " + dbUser);
			} else if (e instanceof DocumentException) {
				throw new MetaDataException(e.getMessage() + " 解释XML文件出错，请检查common中的xml文件");
			}
			throw new MetaDataException(e.getMessage() + " 加载基础数据出错，请检查数据库连接或配置信息");
		}

		metaDataConectionInfo = "[" + connInfo.getDbURL() + " - user=" + connInfo.getDbUser() + "]";
		metadataConnectType = DB_CONNECTION;
	}

	/**
	 * 用指定密码安全管理连接信息初始化FlowMetaData，getInstance()之前应该先调用此方法。
	 * 
	 * @param ip
	 * @param port
	 * @param db_alias
	 * @throws MetaDataException
	 * @throws Exception
	 */
	public static void init(String ip, String port, String db_alias) throws MetaDataException {
		securityIP = ip;
		securityPort = port;
		Connect connect = new Connect();

		connInfo = new ConnInfo();
		try {
			System.out.println(connect.getDbDriver());
			connInfo = connect.getConnInfo(ip, Integer.parseInt(port), db_alias);
		} catch (NumberFormatException e) {
			throw new MetaDataException(e.getMessage() + " 端口错误 " + port);
		} catch (IOException e) {
			throw new MetaDataException("连接ProxyServer失败,请检查ProxyServer是否已启动! \n详细信息: " + e.getMessage() + ", IP=" + ip
					+ ", port=" + port + ", db_alias=" + db_alias);
		}
		singleFlowMetaData = new FlowMetaData();
		if (!connInfo.getStatus()) {
			throw new MetaDataException("获取数据源" + db_alias + "的详细信息失败! 错误原因: " + connInfo.getErrInfo());
		}
		try {
			dao = new DBDaoImpl(connInfo, "ibatis.xml");
			singleFlowMetaData.loadBasicInfo();
		} catch (Exception e) {
			singleFlowMetaData = null;
			if (e instanceof SQLException) {
				throw new MetaDataException(e.getMessage() + " 连接数据库失败!!" + connInfo.getDbDriver() + " "
						+ connInfo.getDbURL() + " " + connInfo.getDbUser());
			} else if (e instanceof DocumentException) {
				throw new MetaDataException(e.getMessage() + " 解释XML文件出错，请检查common中的xml文件");
			}
			throw new MetaDataException(e.getMessage() + " 加载基础数据出错，请检查数据库连接或配置信息");
		}

		metaDataConectionInfo = "[" + connInfo.getDbURL() + " - user=" + connInfo.getDbUser() + "]";
		metadataConnectType = PROXY_CONNECTION;
	}

	/**
	 * 以Map<key,value>的形式返回指定任务的所有动态参数，查内存
	 * 
	 * @param taskID
	 * @return
	 */
	public synchronized Map<String, String> queryTaskAttributeMap(Integer taskID) {
		Map<String, String> attributeMap = new HashMap<String, String>();

		for (TaskAttribute element : this.taskAttributeMap.values()) {
			if (element.getTaskID().equals(taskID)) {
				attributeMap.put(element.getKey(), element.getValue());
			}
		}
		return attributeMap;
	}

	/**
	 * 以Map<key,value>的形式返回指定任务的所有动态参数，查内存(如果是带${}的动态变量,换成动态后返回)
	 * 
	 * @param taskID
	 * @return
	 * @author jiangts
	 * @since 2010-07-28
	 *        <p>
	 *        1.修复了queryTaskAttributeDynMap方法的bug，该方法原来只替换了一个动态变量值，
	 *        后面的动态变量未替换成实际值
	 */
	public synchronized Map<String, String> queryTaskAttributeDynMap(Integer taskID, String statTime) {
		Map<String, String> attributeMap = new HashMap<String, String>();

		for (TaskAttribute element : this.taskAttributeMap.values()) {
			String s = null;
			String value = null;
			if (element.getTaskID().equals(taskID)) {
				value = element.getValue();

				StringBuffer valueBuff = new StringBuffer(value);
				int start = -1;
				int end = -1;
				String key = "";

				start = valueBuff.indexOf("${");
				end = valueBuff.indexOf("}");

				while (start > -1 && end > -1) {
					key = valueBuff.substring(start + 2, end);

					s = this.querySysConfigValue(key);
					if (s == null || s.equals("")) {
						s = "没找到对应的值!!!";
					}

					valueBuff.replace(start, end + 1, s);

					start = valueBuff.indexOf("${");
					end = valueBuff.indexOf("}");
				}

				// 设置时间进度
				element.setValue(setStatTimePattern(valueBuff.toString(), statTime));

				/*
				 * 以下代码存在问题，只替换了第一个动态变量 if ((value != null) &&
				 * (value.indexOf("${") > -1) && (value.indexOf("}") > -1)) {
				 * String tmp = value.substring(value.indexOf("${") + 2, value
				 * .indexOf("}")); s = this.querySysConfigValue(tmp); if (s !=
				 * null && !s.equals("")) { element.setValue(value .substring(0,
				 * value.indexOf("${")) + s + value.substring(value.indexOf("}")
				 * + 1)); } else { element.setValue(value + "没找到对应的值!!!"); }
				 * attributeMap.put(element.getKey(), element.getValue());
				 * 
				 * 
				 * } else {
				 */

				attributeMap.put(element.getKey(), element.getValue());
				// }
			}
		}
		return attributeMap;
	}

	/**
	 * 设置时间进度，将%yyyy%转换成具体的年份等等 20100201000000
	 * 
	 * @param tmpStr
	 * @param statTime
	 * @return
	 */
	public static String setStatTimePattern(String tmpStr, String statTime) {

		if (tmpStr.indexOf("%yyyy%") > -1) {
			tmpStr = tmpStr.replace("%yyyy%", statTime.substring(0, 4));
		}
		if (tmpStr.indexOf("%mm%") > -1) {
			tmpStr = tmpStr.replace("%mm%", statTime.substring(4, 6));
		}
		if (tmpStr.indexOf("%dd%") > -1) {
			tmpStr = tmpStr.replace("%dd%", statTime.substring(6, 8));
		}
		if (tmpStr.indexOf("%hh%") > -1) {
			tmpStr = tmpStr.replace("%hh%", statTime.substring(8, 10));
		}
		if (tmpStr.indexOf("%mi%") > -1) {
			tmpStr = tmpStr.replace("%mi%", statTime.substring(10, 12));
		}
		if (tmpStr.indexOf("%ss%") > -1) {
			tmpStr = tmpStr.replace("%ss%", statTime.substring(12, 14));
		}

		return tmpStr;
	}

	/**
	 * 查询任务的指定动态参数
	 * 
	 * @param taskID
	 *            任务ID
	 * @param key
	 *            动态参数的key
	 * @return 动态参数的value
	 */
	public synchronized TaskAttribute queryTaskAttribute(Integer taskID, String key) {
		TaskAttribute taskAttribute = null;
		for (TaskAttribute element : taskAttributeMap.values()) {
			if (element.getTaskID().equals(taskID) && element.getKey().equals(key)) {
				taskAttribute = element;
				break;
			}
		}
		return taskAttribute;
	}

	/**
	 * 查询任务的指定的动态参数(如果是带${}的动态变量,换成动态后返回)
	 * 
	 * @param taskID
	 *            任务ID
	 * @param key
	 *            动态参数的key
	 * @return 动态参数的value
	 */
	public synchronized TaskAttribute queryTaskAttributeDyn(Integer taskID, String key) {
		TaskAttribute taskAttribute = null;
		String s = null;
		String value = null;
		for (TaskAttribute element : taskAttributeMap.values()) {
			if (element.getTaskID().equals(taskID) && element.getKey().equals(key)) {
				value = element.getValue();
				if ((value != null) && (value.indexOf("${") > -1) && (value.indexOf("}") > -1)) {
					String tmp = value.substring(value.indexOf("${") + 2, value.indexOf("}"));
					s = this.querySysConfigValue(tmp);
					if (s != null && !s.equals("")) {
						if (value.indexOf("}") == value.length() - 1)
							element.setValue(value.substring(0, value.indexOf("${")) + s
									+ value.substring(value.indexOf("}") + 1));
						else
							element.setValue(s + value.substring(value.indexOf("}") + 1));
					} else {
						element.setValue(value + "没找到对应的值!!!");
					}
					taskAttribute = element;
				} else {
					taskAttribute = element;
				}
				break;
			}
		}
		return taskAttribute;
	}

	/**
	 * 一次性保存所有的TaskType，会先删除元数库里所有的TaskType
	 * 
	 * @throws MetaDataException
	 */
	public synchronized void saveAllTaskType() throws MetaDataException {
		try {
			dao.saveTaskTypes();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 一次性保存所有的SysConfig，会先删除元数库里所有的SysConfig
	 * 
	 * @throws MetaDataException
	 */
	public synchronized void saveAllSysConfig() throws MetaDataException {
		try {
			dao.saveSysConfigs();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 一次性保存所有的StepType，会先删除元数库里所有的StepType
	 * 
	 * @throws MetaDataException
	 */
	public synchronized void saveAllStepType() throws MetaDataException {
		try {
			dao.saveStepTypes();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 一次性保存所有的Category，会先删除元数库里所有的Category
	 * 
	 * @throws MetaDataException
	 */
	public synchronized void saveAllCategory() throws MetaDataException {
		try {
			dao.saveCategorys();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void saveAllUser() throws MetaDataException {
		try {
			dao.saveUsers();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void saveAllRole() throws MetaDataException {
		try {
			dao.saveRoles();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void saveAllRight() throws MetaDataException {
		try {
			dao.saveRights();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void saveAllUserRole() throws MetaDataException {
		try {
			dao.saveUserRoles();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void saveAllRoleRight() throws MetaDataException {
		try {
			dao.saveRoleRights();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void saveAllTaskflowUser() throws MetaDataException {
		try {
			dao.saveTaskflowUsers();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 保存流程组
	 * 
	 * @throws MetaDataException
	 */
	public synchronized void saveAllTaskflowGroup() throws MetaDataException {
		try {
			dao.saveTaskflowGroups();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 取元数据库的相关信息。目标是file，则返回路径，否则返回数据库连接串
	 * 
	 * @return
	 */
	public synchronized String getMetadataRepositoryInfo() {
		return FlowMetaData.metaDataConectionInfo;

	}

	public synchronized void update(Taskflow taskflow) throws MetaDataException {
		try {
			dao.update(taskflow);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(Link link) throws MetaDataException {
		try {
			dao.update(link);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(Category category) throws MetaDataException {
		try {
			dao.update(category);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(User user) throws MetaDataException {
		try {
			dao.update(user);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(Role role) throws MetaDataException {
		try {
			dao.update(role);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(Right right) throws MetaDataException {
		try {
			dao.update(right);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(UserRole userRole) throws MetaDataException {
		try {
			dao.update(userRole);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(RoleRight roleRight) throws MetaDataException {
		try {
			dao.update(roleRight);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(TaskflowUser taskflowUser) throws MetaDataException {
		try {
			dao.update(taskflowUser);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 更新流程组
	 * 
	 * @param category
	 * @throws MetaDataException
	 */
	public synchronized void update(TaskflowGroup taskflowGroup) throws MetaDataException {
		try {
			dao.update(taskflowGroup);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(Note note) throws MetaDataException {
		try {
			dao.update(note);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(StepType stepType) throws MetaDataException {
		try {
			dao.update(stepType);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(SysConfig sysConfig) throws MetaDataException {
		try {
			dao.update(sysConfig);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(Task task) throws MetaDataException {
		try {
			dao.update(task);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(TaskAttribute taskAttribute) throws MetaDataException {
		try {
			dao.update(taskAttribute);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void update(TaskType taskType) throws MetaDataException {
		try {
			dao.update(taskType);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 查某个任务的全部参数
	 * 
	 * @param taskID
	 *            任务ID
	 * @return List<TaskAttribute>
	 * @throws MetaDataException
	 */
	public synchronized List<TaskAttribute> queryTaskAttributeList(Integer taskID) {
		List<TaskAttribute> taskAttributeList = new ArrayList<TaskAttribute>();

		for (TaskAttribute element : taskAttributeMap.values()) {
			if (element.getTaskID().equals(taskID)) {
				taskAttributeList.add(element);
			}
		}
		return taskAttributeList;
	}

	/**
	 * 查某个任务的全部参数(如果是带${}的动态变量,换成动态后返回)
	 * 
	 * @param taskID
	 *            任务ID
	 * @return List<TaskAttribute>
	 * @throws MetaDataException
	 */
	public synchronized List<TaskAttribute> queryTaskAttributeDynList(Integer taskID) {
		List<TaskAttribute> taskAttributeList = new ArrayList<TaskAttribute>();
		String s = null;
		String value = null;
		for (TaskAttribute element : taskAttributeMap.values()) {
			if (element.getTaskID().equals(taskID)) {
				value = element.getValue();
				if ((value != null) && (value.indexOf("${") > -1) && (value.indexOf("}") > -1)) {
					String tmp = value.substring(value.indexOf("${") + 2, value.indexOf("}"));
					s = this.querySysConfigValue(tmp);
					if (s != null && !s.equals("")) {
						element.setValue(
								value.substring(0, value.indexOf("${")) + s + value.substring(value.indexOf("}") + 1));
					} else {
						element.setValue(value + "没找到对应的值!!!");
					}
					taskAttributeList.add(element);
				} else {
					taskAttributeList.add(element);
				}
			}
		}
		return taskAttributeList;
	}

	/**
	 * 查所有流程的基本信息
	 * 
	 * @return List<Taskflow>
	 */
	public synchronized List<Taskflow> queryAllTaskflow() {
		List<Taskflow> taskflowList = new ArrayList<Taskflow>();

		for (Taskflow element : taskflowMap.values()) {
			taskflowList.add(element);
		}
		return taskflowList;
	}

	/**
	 * 查所有不属于大纲流程組的流程,并且不是suspend_yes.
	 * 
	 * @return List<Taskflow>
	 */
	public synchronized List<Taskflow> queryAllSuspendNoTaskflowAndNotInOutlineGroup() {
		List<Taskflow> taskflowList = new ArrayList<Taskflow>();

		for (Taskflow element : taskflowMap.values()) {
			if (element.getGroupID() != outlineGroupId) {
				if (element.getSuspend() == Taskflow.SUSPEND_NO) {
					taskflowList.add(element);
				}
			}

		}
		return taskflowList;
	}

	/**
	 * 查所有不属于大纲流程組的流程
	 * 
	 * @return List<Taskflow>
	 */
	public synchronized List<Taskflow> queryAllTaskflowNotInOutlineGroup() {
		List<Taskflow> taskflowList = new ArrayList<Taskflow>();

		for (Taskflow element : taskflowMap.values()) {
			if (element.getGroupID() != outlineGroupId) {
				taskflowList.add(element);
			}
		}
		return taskflowList;
	}

	/**
	 * 根据key模糊查询匹配的流程
	 * 
	 * @return List<Taskflow>
	 */
	public synchronized List<Taskflow> queryTaskflowByKey(String key) {
		List<Taskflow> taskflowList = new ArrayList<Taskflow>();

		for (Taskflow element : taskflowMap.values()) {
			if (key != null && element.getTaskflow().toLowerCase().indexOf(key.toLowerCase()) > -1) {
				taskflowList.add(element);
			}
		}
		return taskflowList;
	}

	/**
	 * 查询所有用户
	 * 
	 * @return
	 */
	public synchronized List<User> queryAllUser() {
		return new ArrayList<User>(userMap.values());
	}

	/**
	 * 查询所有角色
	 * 
	 * @return
	 */
	public synchronized List<Role> queryAllRole() {
		return new ArrayList<Role>(roleMap.values());
	}

	/**
	 * 查询所有权限
	 * 
	 * @return
	 */
	public synchronized List<Right> queryAllRight() {
		return new ArrayList<Right>(rightMap.values());
	}

	/**
	 * 查询用户的流程
	 * 
	 * @param userID
	 * @return
	 */
	public synchronized List<Taskflow> queryTaskflowOfUser(String userID) {
		List<Taskflow> list = new ArrayList<Taskflow>();
		Taskflow tf = null;
		for (TaskflowUser tu : taskflowUserMap.values()) {
			if (tu.getUserID().equals(userID)) {
				tf = this.queryTaskflow(tu.getTaskflowID());
				if (tf != null) {
					list.add(tf);
				}
			}
		}
		return list;
	}

	/**
	 * 查询属于指定group的所有流程
	 * 
	 * @param groupID
	 * @return
	 */
	public synchronized List<Taskflow> queryTaskflowInGroup(Integer groupID) {
		List<Taskflow> taskflowList = new ArrayList<Taskflow>();

		for (Taskflow element : taskflowMap.values()) {
			if (element.getGroupID().equals(groupID)) {
				taskflowList.add(element);
			}
		}
		return taskflowList;
	}

	/**
	 * 查所有流程的名字
	 * 
	 * @return 流程名的列表
	 */
	public synchronized List<String> queryAllTaskflowName() {
		List<String> taskflowList = new ArrayList<String>();

		for (Taskflow element : taskflowMap.values()) {
			taskflowList.add(element.getTaskflow());
		}
		return taskflowList;
	}

	/**
	 * 查指定任务的所有子任务
	 * 
	 * @param task
	 * @return 子任务的列表
	 */
	public synchronized List<Task> queryAllChildTask(Task task) {

		List<Task> childTaskList = new ArrayList<Task>();

		// 根据link找出该任务的所有子任务
		for (Link link : linkMap.values()) {
			if (link.getFromTaskID().equals(task.getTaskID())) {
				childTaskList.add(taskMap.get(link.getToTaskID()));
			}
		}

		return childTaskList;
	}

	/**
	 * 查指定任务的所有父任务
	 * 
	 * @param task
	 * @return 父任务的列表
	 */
	public synchronized List<Task> queryAllFatherTask(Task task) {

		List<Task> fatherTaskList = new ArrayList<Task>();

		// 根据link找出该任务的所有父任务
		for (Link link : linkMap.values()) {
			if (link.getToTaskID().equals(task.getTaskID())) {
				fatherTaskList.add(taskMap.get(link.getFromTaskID()));
			}
		}

		return fatherTaskList;
	}

	/**
	 * 按流程ID查询流程基本信息
	 * 
	 * @param taskflowID
	 * @return
	 */
	public synchronized Taskflow queryTaskflow(Integer taskflowID) {

		for (Taskflow element : taskflowMap.values()) {
			if (element.getTaskflowID().equals(taskflowID)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * 直接从存储上按流程名字查询流程基本信息
	 * 
	 * @param taskflowName
	 * @return
	 */
	public synchronized Taskflow findTaskflow(Integer taskflowID) {

		Taskflow taskflow = null;

		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return taskflow;

	}

	/**
	 * 直接从存储上按流程名字查询流程基本信息
	 * 
	 * @param taskflowName
	 * @return
	 */
	public synchronized SysConfig findSysConfig(String sysConfigKey) {
		try {
			dao.loadSysConfigs();
			return querySysConfig(sysConfigKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 按流程名字查询流程基本信息
	 * 
	 * @param taskflowName
	 * @return
	 */
	public synchronized Taskflow queryTaskflow(String taskflowName) {

		for (Taskflow element : taskflowMap.values()) {
			if (element.getTaskflow().equals(taskflowName)) {
				return element;
			}
			/*if ((element.getTaskflowID() + "").equals(taskflowName)) {
				return element;
			}*/
		}
		return null;
	}

	/**
	 * 将指定流程和流程中的所有任务重置到READY状态
	 * 
	 * @param taskflowID
	 * @throws MetaDataException
	 */
	public synchronized void resetTaskflowStatus(Integer taskflowID) throws MetaDataException {
		try {
			Taskflow tf = queryTaskflow(taskflowID);
			// dao.update(tf);
			dao.updateStatusOfTaskflow(taskflowID, Taskflow.READY);
			tf.setStatus(Taskflow.READY);
			resetTaskStatus(taskflowID);

		} catch (Exception e) {

			throw new MetaDataException(e);
		}
		return;
	}

	/**
	 * 查询指定流程的全部链接
	 * 
	 * @param taskflowID
	 * @return
	 */
	public synchronized List<Link> queryLinkList(Integer taskflowID) {
		Set<Link> linkSet = new HashSet<Link>();
		List<Link> linkList = new ArrayList<Link>();

		for (Link link : linkMap.values()) {
			Task fromTask = taskMap.get(link.getFromTaskID());

			if (fromTask != null && fromTask.getTaskflowID().equals(taskflowID))
				linkSet.add(link);
		}
		linkList.addAll(linkSet);
		return linkList;
	}

	/**
	 * 查找两个task之间的link
	 * 
	 * @param TaskID1
	 * @param TaskID2
	 * @return
	 */
	public synchronized Link queryLinkBetweenTasks(Integer TaskID1, Integer TaskID2) {
		Link retLink = null;

		for (Link link : linkMap.values()) {
			if (link.getFromTaskID().equals(TaskID1) && link.getToTaskID().equals(TaskID2)
					|| (link.getFromTaskID().equals(TaskID2)) && link.getToTaskID().equals(TaskID1)) {

				retLink = link;
			}
		}

		return retLink;
	}

	/**
	 * 按方向查找两个task之间的link,
	 * 
	 * @param fromTask
	 * @param toTask
	 * @return
	 */
	public synchronized Link queryLinkBetweenTasksByDirection(Task fromTask, Task toTask) {
		Link retLink = null;

		for (Link link : linkMap.values()) {
			if (link.getFromTaskID().equals(fromTask.getTaskID()) && link.getToTaskID().equals(toTask.getTaskID())) {
				retLink = link;
				break;
			}
		}

		return retLink;
	}

	/**
	 * 查询指定流程的任务列表
	 * 
	 * @param taskflowID
	 * @return List<Task>
	 */
	public synchronized List<Task> queryTaskList(Integer taskflowID) {
		List<Task> taskList = new ArrayList<Task>();

		for (Task element : taskMap.values()) {
			if (element.getTaskflowID().equals(taskflowID)) {
				taskList.add(element);
			}
		}
		return taskList;
	}

	/**
	 * 通过关键字模糊查询全部任务列表，可能查出多个不同流程的任务。
	 * 
	 * @param keyword
	 *            搜索关键字，模糊匹配任务名或任务描述。
	 * @return List<Task>
	 */
	public synchronized List<Task> queryTaskListBykeyword(String keyword) {
		List<Task> taskList = new ArrayList<Task>();

		for (Task element : taskMap.values()) {
			if (element.getTask().contains(keyword) || element.getDescription().contains(keyword)) {
				taskList.add(element);
			}
		}
		return taskList;
	}

	/**
	 * 通过关键字模糊查询指定流程中的任务列表
	 * 
	 * @param keyword
	 *            搜索关键字，模糊匹配任务名或任务描述。
	 * @return List<Task>
	 */
	public synchronized List<Task> queryTaskListBykeyword(String taskflowName, String keyword) {
		List<Task> taskList = new ArrayList<Task>();
		Taskflow taskflow = this.queryTaskflow(taskflowName);
		for (Task element : taskMap.values()) {
			if (element.getTaskflowID().equals(taskflow.getTaskflowID())
					&& (element.getTask().contains(keyword) || element.getDescription().contains(keyword))) {
				taskList.add(element);
			}
		}
		return taskList;
	}

	/**
	 * 将指定流程中某种状态的任务重置为另外一种状态
	 * 
	 * @param taskflowID
	 *            流程ID
	 * @param oldStatus
	 *            旧状态
	 * @param newStatus
	 *            新状态
	 * @throws MetaDataException
	 */
	public synchronized void resetTaskStatusByTaskflowID(Integer taskflowID, int oldStatus, int newStatus)
			throws MetaDataException {

		for (Task element : taskMap.values()) {
			if (element.getTaskflowID().equals(taskflowID)) {
				if (element.getStatus() == oldStatus) {
					updateTaskStatus(element, newStatus);
				}
			}
		}

	}

	/**
	 * 获取TaskType的列表
	 * 
	 * @return List<TaskType>
	 */
	public synchronized List<TaskType> queryTaskTypeList() {
		List<TaskType> taskTypeList = new ArrayList<TaskType>();

		for (TaskType element : taskTypeMap.values()) {
			taskTypeList.add(element);
		}
		return taskTypeList;
	}

	/**
	 * 当前运行的任务
	 * 
	 * @param taskflowID
	 * @return
	 * 
	 * 		public synchronized Task queryRunningTask(Integer taskflowID) {
	 *         List<Task> taskList = this.queryTaskList(taskflowID); Task task =
	 *         null; for (Task element : taskList) { if (task == null) { if
	 *         (element.getRunStartTime() != null) task = element; continue; }
	 *         if (element.getRunStartTime() != null &&
	 *         element.getRunStartTime().after(task.getRunStartTime())) task =
	 *         element; } return task; }
	 */
	/**
	 * 按第一分类查询TaskType
	 * 
	 * @param categoryID
	 *            组件面板分类ID
	 * @return 该分类下的所有TaskType
	 */
	public synchronized List<TaskType> queryTaskTypeListByCategory(Integer categoryID) {
		List<TaskType> taskTypeList = new ArrayList<TaskType>();

		for (TaskType element : taskTypeMap.values()) {
			if (element.getCategoryID().equals(categoryID)) {
				taskTypeList.add(element);
			}
		}
		return taskTypeList;
	}

	/**
	 * 按第二分类查询TaskType
	 * 
	 * @param categoryID
	 *            组件面板分类ID
	 * @return 该分类下的所有TaskType
	 */
	public synchronized List<TaskType> queryTaskTypeListBySecondCategory(Integer categoryID) {
		List<TaskType> taskTypeList = new ArrayList<TaskType>();

		for (TaskType element : taskTypeMap.values()) {
			Category secondCategory = queryCategory(element.getSecondCategory());
			if (secondCategory == null) {
				continue;
			}
			if (secondCategory.getID().equals(categoryID)) {
				taskTypeList.add(element);
			}
		}
		return taskTypeList;
	}

	/**
	 * 获取全部的组件面板分类
	 * 
	 * @return List<Category>
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<Category> queryCategoryList() {
		List<Category> categoryList = new ArrayList<Category>();

		for (Category category : categoryMap.values()) {
			categoryList.add(category);
		}
		Collections.sort(categoryList);
		return categoryList;
	}

	/**
	 * 获取全部的group
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<TaskflowGroup> queryTaskflowGroupList() {
		List<TaskflowGroup> taskflowGroupList = new ArrayList<TaskflowGroup>();
		for (TaskflowGroup taskflowGroup : taskflowGroupMap.values()) {
			taskflowGroupList.add(taskflowGroup);
		}
		Collections.sort(taskflowGroupList);
		return taskflowGroupList;
	}

	/**
	 * 按名字查找group
	 * 
	 * @param taskflowGroupName
	 *            group名
	 * @return TaskflowGroup
	 */
	public synchronized TaskflowGroup queryTaskflowGroup(String taskflowGroupName) {

		for (TaskflowGroup taskflowGroup : taskflowGroupMap.values()) {
			if (taskflowGroup.getGroupName().equalsIgnoreCase(taskflowGroupName)) {
				return taskflowGroup;
			}
		}

		return null;
	}

	/**
	 * 按ID查找group
	 * 
	 * @param taskflowGroupName
	 *            groupID
	 * @return TaskflowGroup
	 */
	public synchronized TaskflowGroup queryTaskflowGroup(Integer taskflowGroupID) {

		for (TaskflowGroup taskflowGroup : taskflowGroupMap.values()) {
			if (taskflowGroup.getGroupID().equals(taskflowGroupID)) {
				return taskflowGroup;
			}
		}

		return null;
	}

	/**
	 * 按名字查找组件面板分类
	 * 
	 * @param categoryName
	 *            分类名
	 * @return Category
	 */
	public synchronized Category queryCategory(String categoryName) {

		for (Category category : categoryMap.values()) {
			if (category.getName().equalsIgnoreCase(categoryName)) {
				return category;
			}
		}

		return null;
	}

	/**
	 * 按ID查找组件面板分类
	 * 
	 * @param categoryID
	 *            分类名
	 * @return Category
	 */
	public synchronized Category queryCategory(Integer categoryID) {
		return categoryMap.get(categoryID);
	}

	public synchronized User queryUser(String userID) {
		return userMap.get(userID);
	}

	public synchronized Role queryRole(Integer roleID) {
		return roleMap.get(roleID);
	}

	public synchronized Right queryRight(Integer rightID) {
		return rightMap.get(rightID);
	}

	public synchronized UserRole queryUserRole(String userID, Integer roleID) {
		return userRoleMap.get(userID + roleID);
	}

	/**
	 * 查询用户所有角色
	 * 
	 * @param userID
	 * @return
	 */
	public synchronized List<Role> queryRoleOfUser(String userID) {
		List<Role> list = new ArrayList<Role>();
		for (UserRole element : userRoleMap.values()) {
			if (element.getUserID().equals(userID)) {
				list.add(roleMap.get(element.getRoleID()));
			}
		}
		return list;
	}

	public synchronized RoleRight queryRoleRight(Integer roleID, Integer rightID) {
		return roleRightMap.get(roleID + "_" + rightID);
	}

	/**
	 * 查询角色所有权限
	 * 
	 * @param roleID
	 * @return
	 */
	public synchronized List<Right> queryRightOfRole(Integer roleID) {
		List<Right> list = new ArrayList<Right>();
		for (RoleRight element : roleRightMap.values()) {
			if (element.getRoleID().equals(roleID)) {
				list.add(rightMap.get(element.getRightID()));
			}
		}
		return list;
	}

	/**
	 * 查询用户的所有权限
	 * 
	 * @param userID
	 * @return
	 */
	public synchronized List<Right> queryRightOfUser(String userID) {
		List<Role> roleList = queryRoleOfUser(userID);
		Set<Right> rightSet = new HashSet<Right>();
		for (Role role : roleList) {
			rightSet.addAll(queryRightOfRole(role.getID()));
		}
		List<Right> rightList = new ArrayList<Right>();
		rightList.addAll(rightSet);
		return rightList;
	}

	public synchronized TaskflowUser queryTaskflowUser(Integer taskflowID) {
		return taskflowUserMap.get(taskflowID);
	}

	public synchronized List<TaskflowUser> queryTaskflowUserList() {
		ArrayList<TaskflowUser> list = new ArrayList<TaskflowUser>();
		for (TaskflowUser element : taskflowUserMap.values()) {
			list.add(element);
		}
		return list;
	}

	/**
	 * 查询指定流程的全部Note的列表
	 * 
	 * @param taskflowID
	 * @return List<Note>
	 */
	public synchronized List<Note> queryNoteList(Integer taskflowID) {
		ArrayList<Note> noteList = new ArrayList<Note>();
		for (Note element : noteMap.values()) {
			if (element.getTaskflowID().equals(taskflowID)) {
				noteList.add(element);
			}
		}
		return noteList;
	}

	/**
	 * 查询指定流程的任务个数
	 * 
	 * @param taskflowID
	 * @return
	 */
	public synchronized int queryTaskCount(Integer taskflowID) {
		int taskCount = 0;

		for (Task element : taskMap.values()) {
			if (element.getTaskflowID().equals(taskflowID)) {
				taskCount++;
			}
		}
		return taskCount;
	}

	/**
	 * 更新流程的进度，新进度根据当前进度，周期类型和周期步长三个参数计算出。 如果异常，当前进度不变。
	 * 
	 * @param taskflow
	 * @throws MetaDataException
	 */
	public synchronized void updateTaskflowTime(Taskflow taskflow) throws MetaDataException {
		Date newTime = TimeUtils.getNewTime(taskflow.getStatTime(), taskflow.getStepType(), taskflow.getStep());
		try {
			dao.updateStatTimeOfTaskflow(taskflow.getTaskflowID(), newTime);
			taskflow.setStatTime(newTime);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 更新普通的流程状态，同时更新大纲任务的状态
	 * 
	 * @param taskflow
	 *            流程
	 * @param newStatus
	 *            新状态
	 * @throws MetaDataException
	 */
	public synchronized void updateStatusOfTaskflow(Taskflow taskflow, int newStatus) throws MetaDataException {
		try {
			dao.updateStatusOfTaskflow(taskflow.getTaskflowID(), newStatus);
			taskflow.setStatus(newStatus);

			// 同时修改大纲任务的状态：
			Task taskInOutline = queryOutlineTaskByTaskflow(taskflow);
			if (taskInOutline != null) {
				dao.updateStatusOfTask(taskInOutline.getTaskID(), newStatus);
				taskInOutline.setStatus(newStatus);
			}

		} catch (Exception e) {
			throw new MetaDataException(e);
		}

		return;
	}

	/**
	 * 更新流程状态
	 * 
	 * @param taskflowName
	 * @param newStatus
	 * @throws MetaDataException
	 */
	public synchronized void updateStatusOfTaskflow(String taskflowName, int newStatus) throws MetaDataException {
		Taskflow taskflow = queryTaskflow(taskflowName);
		try {
			updateStatusOfTaskflow(taskflow, newStatus);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}

		return;
	}

	/**
	 * 初始化流程的下一个实例 1.流程进度递增 2.重置流程及其中所有任务的状态为READY
	 * 
	 * @param taskflow
	 * @param status
	 * @return
	 */
	public synchronized boolean initNextTaskflowEntity(Taskflow taskflow) throws MetaDataException {
		boolean isOK = true;
		// 流程进度递增
		updateTaskflowTime(taskflow);

		// 重置流程及任务的状态
		resetTaskflowStatus(taskflow.getTaskflowID());

		return isOK;
	}

	/**
	 * 按taskID 更新任务状态
	 * 
	 * @param taskID
	 * @param newStatus
	 *            新状态
	 * @return
	 * @throws MetaDataException
	 */
	public synchronized boolean updateTaskStatus(Integer taskID, int newStatus) throws MetaDataException {
		boolean isOK = true;
		Task task = queryTask(taskID);
		updateTaskStatus(task, newStatus);

		return isOK;
	}

	/**
	 * 更新任务状态
	 * 
	 * @param task
	 * @param newStatus
	 * @throws MetaDataException
	 */
	public synchronized void updateTaskStatus(Task task, int newStatus) throws MetaDataException {
		try {
			dao.updateStatusOfTask(task.getTaskID(), newStatus);
			task.setStatus(newStatus);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return;
	}

	/*
	 * public synchronized Long saveHistory(String taskIdStr, String status)
	 * throws MetaDataException { Long historyId = null; try { Task task =
	 * this.queryTask(Integer.valueOf(taskIdStr)); historyId =
	 * dao.saveHistory(task.getTaskflowID() + "", taskIdStr, "", status); }
	 * catch (Exception e) { throw new MetaDataException(e); } return historyId;
	 * }
	 */
	public synchronized Long saveHistory(String taskflowIdStr, String status, String statTime, String endstatTime,
			String nextStatTime) throws MetaDataException {
		Long historyId = null;
		try {
			// Task task = this.queryTask(Integer.valueOf(taskIdStr));
			historyId = dao.saveHistory(taskflowIdStr, "0", "", status, statTime, endstatTime, nextStatTime);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return historyId;
	}
	public synchronized Long saveHistoryWithName(String taskflowIdStr, String status, String statTime, String endstatTime,
			String nextStatTime,String filename) throws MetaDataException {
		Long historyId = null;
		try {
			// Task task = this.queryTask(Integer.valueOf(taskIdStr));
			historyId = dao.saveHistory(taskflowIdStr, "0", filename, status, statTime, endstatTime, nextStatTime);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return historyId;
	}

	/*
	 * public synchronized void updateHistoryStatus(Long historyId, String file,
	 * String status) throws MetaDataException { try {
	 * dao.updateHistoryStatus(historyId, file, status); } catch (Exception e) {
	 * throw new MetaDataException(e); } return; }
	 */
	public synchronized void updateHistoryStatus(Long historyId, String status, String statTime, String endStatTime,
			String nextStatTime, String fileName) throws MetaDataException {
		try {
			dao.updateHistoryStatus(historyId, status, statTime, endStatTime, nextStatTime, fileName);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return;
	}

	/**
	 * 从数据库加载任务禁用状态
	 * 
	 * @param taskID
	 * @return
	 * @throws MetaDataException
	 */
	public synchronized int loadSuspendOfTask(Integer taskID) throws MetaDataException {
		Task task = null;
		try {
			task = dao.getTask(taskID);
			// taskMap.put(task.getTaskID(), task);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return task.getSuspend();
	}

	/**
	 * 从数据库加载任务状态
	 * 
	 * @param taskID
	 * @return
	 * @throws MetaDataException
	 */
	public synchronized int loadStatusOfTask(Integer taskID) throws MetaDataException {
		Task task = null;
		try {
			task = dao.getTask(taskID);
			// taskMap.put(task.getTaskID(), task);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return task.getStatus();
	}

	/**
	 * 从数据库加载流程状态
	 * 
	 * @param taskflowID
	 * @return 流程状态
	 * @throws MetaDataException
	 */
	public synchronized int loadStatusOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
			// taskflowMap.put(taskflow.getTaskflowID(), taskflow);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow.getStatus();
	}

	/**
	 * 更新任务禁用状态
	 * 
	 * @param taskID
	 * @param newSuspend
	 * @throws MetaDataException
	 */
	public synchronized void updateSuspendOfTask(Integer taskID, int newSuspend) throws MetaDataException {
		try {
			dao.updateSuspendOfTask(taskID, newSuspend);
			Task task = queryTask(taskID);
			if (task != null) {
				task.setSuspend(newSuspend);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 更新大纲节点的禁用状态
	 * 
	 * @param taskID
	 * @param newSuspend
	 * @throws MetaDataException
	 */
	public synchronized void updateSuspendOfOutlineTask(String taskName, int newSuspend) throws MetaDataException {
		try {
			// 在大纲流程中找出以此名称命名的任务

			// 找出所有大纲流程
			List<Taskflow> outlineTaskflowList = queryTaskflowInGroup(outlineGroupId);

			Task taskInOutline = null;
			if (null != outlineTaskflowList && outlineTaskflowList.size() > 0) {
				// 在所有大纲流程中找出以参数taskName命名的大纲任务
				for (Taskflow outlineTaskflow : outlineTaskflowList) {
					taskInOutline = queryTaskByName(outlineTaskflow.getTaskflowID(), taskName);

					if (taskInOutline != null) {
						// 找到一个大纲
						// 按照设计约束，不能有同名流程，所以可以直接退出
						break;
					}
				}

				dao.updateSuspendOfTask(taskInOutline.getTaskID(), newSuspend);

				taskInOutline.setSuspend(newSuspend);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 修改某个流程组中所有流程在大纲中对应节点的禁用状态
	 * 
	 * @param taskID
	 * @param newSuspend
	 * @throws MetaDataException
	 */
	public synchronized void updateSuspendOfOutlineTaskByTaskflowGroup(String taskflowGroupName, int newSuspend)
			throws MetaDataException {
		try {
			// 找出所有流程
			List<Taskflow> taskflowList = queryTaskflowInGroup(queryTaskflowGroup(taskflowGroupName).getGroupID());

			String taskInOutlineName = null;

			// 修改大纲流程中以流程名相同名称的大纲任务的禁用状态
			for (Taskflow taskflow : taskflowList) {
				taskInOutlineName = taskflow.getTaskflow();
				updateSuspendOfOutlineTask(taskInOutlineName, newSuspend);
			}

		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 从数据库加载流程禁用状态
	 * 
	 * @param taskflowID
	 * @return
	 * @throws MetaDataException
	 */
	public synchronized int loadSuspendOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
			// taskflowMap.put(taskflow.getTaskflowID(),taskflow);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow.getSuspend();
	}

	/**
	 * 更新流程禁用状态
	 * 
	 * @param taskflowID
	 * @param newSuspend
	 * @throws MetaDataException
	 */
	public synchronized void updateSuspendOfTaskflow(Integer taskflowID, int newSuspend) throws MetaDataException {
		for (Task element : queryTaskList(taskflowID)) {
			updateSuspendOfTask(element.getTaskID(), newSuspend);
		}
		try {
			dao.updateSuspendOfTaskflow(taskflowID, newSuspend);
			// 同时修改大纲任务的状态：
			if (queryOutlineTaskByTaskflowID(taskflowID) != null) {
				dao.updateStatusOfTask(queryOutlineTaskByTaskflowID(taskflowID).getTaskID(), newSuspend);
			}
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setSuspend(newSuspend);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 按taskflowGroup更新suspend
	 * 
	 * @param taskflowGroupID
	 * @param newSuspend
	 * @throws MetaDataException
	 */
	public synchronized void updateSuspendOfTaskflowByGroup(Integer taskflowGroupID, int newSuspend)
			throws MetaDataException {
		List<Taskflow> taskflowList = queryTaskflowInGroup(taskflowGroupID);
		for (Taskflow item : taskflowList) {
			updateSuspendOfTaskflow(item.getTaskflowID(), newSuspend);
		}
	}

	/**
	 * 更新所有流程的suspend
	 * 
	 * @param newSuspend
	 * @throws MetaDataException
	 */
	public synchronized void updateSuspendOfAllTaskflow(int newSuspend) throws MetaDataException {
		List<Taskflow> taskflowList = queryAllTaskflow();
		for (Taskflow item : taskflowList) {
			updateSuspendOfTaskflow(item.getTaskflowID(), newSuspend);
		}
	}

	/**
	 * 插入指定任务的动态参数列表,设计器从任务配置对话框得到参数map之后, 调用此方法更新任务动态参数. 修改记录：对value进行了trim()
	 * by jiangts
	 * 
	 * @param taskID
	 * @param attributeMap
	 */
	public synchronized void insertTaskAttributeMap(Integer taskID, Map<String, String> attributeMap) {

		for (String key : attributeMap.keySet()) {
			TaskAttribute taskAttribute = queryTaskAttribute(taskID, key);
			if (taskAttribute != null) {
				// 修改
				taskAttribute.setValue(attributeMap.get(key).trim());

			} else {
				// 插入
				TaskAttribute ta = new TaskAttribute(Utils.getRandomIntValue(), taskID, key,
						attributeMap.get(key).trim());
				this.taskAttributeMap.put(ta.getAttributeID(), ta);
			}
		}

	}

	/**
	 * 判断流程中所有未完成的节点是否都是Suspened状态.
	 * 
	 * @param taskflow
	 * @return boolean
	 */
	public synchronized boolean isAllNodeSuspened(Taskflow taskflow) {
		boolean isOK = true;
		// 找出所有未完成的节点
		List<Task> uncompeleteTaskList = queryUncompeleteTaskList(taskflow.getTaskflowID());

		// 判断节点的状态，只要有一个为正常，就返回false。
		for (Task element : uncompeleteTaskList) {
			if (element.getSuspend() == Task.SUSPEND_NO) {
				isOK = false;
				break;
			}
		}
		return isOK;

	}

	/**
	 * 判断指定任务的所有父节点都已成功
	 * 
	 * @param task
	 * @return
	 */
	public synchronized boolean isAllFatherNodeSuccessed(Task task) {
		boolean isOK = true;
		List<Task> fatherTaskList = queryAllFatherTask(task);

		// 判断父节点的状态，只要有一个不成功，就返回false。
		if(fatherTaskList != null && fatherTaskList .size() > 0){
			for (Task fatherTask : fatherTaskList) {
				if (fatherTask.getSuspend() == Task.SUSPEND_YES) {
					// 如果父节点是禁用的，则认为成功。
					continue;
				}
				if (fatherTask.getStatus() != Task.SUCCESSED) {
					isOK = false;
					break;
				}
			}
		}
		return isOK;
	}

	/**
	 * 判断节点是否为根节点
	 * 
	 * @param task
	 * @return
	 */
	public synchronized boolean isRootTask(Task task) {
		boolean isRoot = false;
		List<Task> fatherTaskList = queryAllFatherTask(task);

		if (fatherTaskList.size() == 0) {
			isRoot = true;
		}
		return isRoot;
	}

	/**
	 * 判断节点是否为叶子节点
	 * 
	 * @param task
	 * @return
	 */
	public synchronized boolean isLeafTask(Task task) {
		boolean isLeaf = false;
		List<Task> childTaskList = queryAllChildTask(task);

		if (childTaskList.size() == 0) {
			isLeaf = true;
		}
		return isLeaf;
	}

	/**
	 * 查出指定流程中的全部未完成的任务列表
	 * 
	 * @param taskflowID
	 * @return List<Task>
	 */
	public synchronized List<Task> queryUncompeleteTaskList(Integer taskflowID) {
		List<Task> uncompeleteTaskList = new ArrayList<Task>();

		for (Task element : taskMap.values()) {
			if (element.getTaskflowID().equals(taskflowID)) {
				if (element.getStatus() != Task.SUCCESSED) {
					uncompeleteTaskList.add(element);
				}
			}
		}
		return uncompeleteTaskList;
	}

	/**
	 * 查询指定流程中的根节点列表
	 * 
	 * @param taskflowID
	 * @return List<Task>
	 */
	public synchronized List<Task> queryRootTaskList(Integer taskflowID) {
		List<Task> rootList = new ArrayList<Task>();

		List<Task> taskList = queryTaskList(taskflowID);

		for (Task task : taskList) {
			if (isRootTask(task)) {
				rootList.add(task);
			}
		}
		return rootList;
	}

	/**
	 * 按ID查TaskType
	 * 
	 * @param taskTypeID
	 * @return
	 */
	public synchronized TaskType queryTaskType(Integer taskTypeID) {
		return taskTypeMap.get(taskTypeID);
	}

	/**
	 * 按名称查TaskType
	 * 
	 * @param taskTypeName
	 * @return
	 */
	public synchronized TaskType queryTaskType(String taskTypeName) {
		TaskType taskType = null;
		for (TaskType element : taskTypeMap.values()) {
			if (element.getTaskType().equalsIgnoreCase(taskTypeName)) {
				taskType = element;
				break;
			}
		}
		return taskType;
	}

	/**
	 * 查询周期类型
	 * 
	 * @param stepTypeID
	 * @return
	 */
	public synchronized StepType queryStepType(String stepType) {
		return stepTypeMap.get(stepType);
	}

	/**
	 * 查询全部的周期类型
	 * 
	 * @return List<StepType>
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<StepType> queryStepTypeList() {
		List<StepType> stepTypeList = new ArrayList<StepType>();

		for (StepType element : stepTypeMap.values()) {
			stepTypeList.add(element);
		}
		Collections.sort(stepTypeList);
		return stepTypeList;
	}

	/**
	 * 查询全部的系统参数
	 * 
	 * @return List<SysConfig>
	 */
	public synchronized List<SysConfig> querySysConfigList() {
		List<SysConfig> list = new ArrayList<SysConfig>();
		list.addAll(sysConfigMap.values());
		return list;
	}

	/**
	 * 按taskID找Task
	 * 
	 * @param taskID
	 * @return task
	 */
	public synchronized Task queryTask(Integer taskID) {
		return taskMap.get(taskID);
	}

	/**
	 * 将指定流程ID的所有任务状态都改为READY
	 * 
	 * @param taskflowID
	 * @return
	 * @throws MetaDataException
	 */
	public synchronized boolean resetTaskStatus(Integer taskflowID) throws MetaDataException {
		boolean isOK = true;
		List<Task> taskList = queryTaskList(taskflowID);

		for (Task element : taskList) {
			updateTaskStatus(element, Task.READY);
			element.setStatus(Task.READY);
		}
		return isOK;
	}

	/**
	 * 判断是否流程中的所有任务都已经完成.
	 * 
	 * @param taskflowID
	 * @return
	 */
	public synchronized boolean isAllTaskSuccess(Integer taskflowID) {
		boolean isOK = true;
		List<Task> taskList = queryTaskList(taskflowID);

		for (Task element : taskList) {
			if (element.getStatus() != Task.SUCCESSED) {
				isOK = false;
			}
		}
		return isOK;
	}

	public synchronized Task queryTaskByName(int taskflowId, String taskName) {

		List<Task> taskList = queryTaskList(taskflowId);

		// 查询任务名为该流程名的任务
		for (Task t : taskList) {
			if (t.getTask().equals(taskName)) {
				return t;
			}
		}

		return null;
	}

	@SuppressWarnings("static-access")
	public synchronized boolean isOutlineTaskflow(Integer taskflowID) {
		boolean isOK = false;
		Taskflow taskflow = queryTaskflow(taskflowID);
		if (taskflow.getGroupID() == this.outlineGroupId) {
			isOK = true;
		}
		return isOK;
	}

	public synchronized boolean isOutlineTask(String taskName) {
		boolean isOK = false;

		// 在大纲流程中找出以此名称命名的任务
		// 找出所有大纲流程
		List<Taskflow> outlineTaskflowList = queryTaskflowInGroup(outlineGroupId);

		Task taskInOutline = null;

		// 在所有大纲流程中找出以参数命名的大纲任务
		for (Taskflow outlineTaskflow : outlineTaskflowList) {

			taskInOutline = queryTaskByName(outlineTaskflow.getTaskflowID(), taskName);

			if (taskInOutline != null) {
				// 找到一个
				// 按照设计约束，不能有同名流程，所以可以直接退出
				isOK = true;
				break;
			}
		}

		return isOK;

	}

	public synchronized boolean isTaskflowCanRunJudgeByOutline(Taskflow taskflow) {
		boolean isCanRun = true;
		// taskflowName ： 流程的英文名

		Task taskInOutline = queryOutlineTaskByTaskflow(taskflow);

		// 通过找到的大纲任务，找出该大纲任务的父节点
		if (taskInOutline == null) {
			// 兼容以前的流程，因此流程不在大纲中也照样运行
			isCanRun = true;
		} else {
			List<Task> fatherTaskList = queryAllFatherTask(taskInOutline);

			for (Task t : fatherTaskList) {

				// 通过流程名，找出所依赖的流程对象
				Taskflow fatherTaskflow = queryTaskflow(t.getTask());

				if (fatherTaskflow.getSuspend() == Taskflow.SUSPEND_YES) {
					// 如果某个父流程是禁用的，则不参与判断
					continue;
				}
				// 通过流程时间做判断，如果要运行的流程的结束时间大于任何一个父流程的进度时间，则不能运行。
				if (TimeUtils.getNewTime(taskflow.getStatTime(), taskflow.getStepType(), taskflow.getStep())
						.getTime() > fatherTaskflow.getStatTime().getTime()) {
					isCanRun = false;
					break;
				}
			}

		}

		return isCanRun;

	}

	/**
	 * 查出指定流程中的全部未完成的父流程列表
	 * 
	 * @param taskflowID
	 * @return List<Task>
	 */
	public List<Taskflow> queryUncompeleteAllFatherTaskflowList(Taskflow taskflow) {

		List<Taskflow> list = new ArrayList<Taskflow>();

		Task taskInOutline = queryOutlineTaskByTaskflow(taskflow);

		// 通过找到的大纲任务，找出该大纲任务的父节点
		if (taskInOutline == null) {
			// 兼容以前的流程，因此流程不在大纲中也照样运行
		} else {
			List<Task> fatherTaskList = queryAllFatherTask(taskInOutline);

			for (Task t : fatherTaskList) {

				// 通过流程名，找出所依赖的流程对象
				Taskflow fatherTaskflow = queryTaskflow(t.getTask());

				if (fatherTaskflow.getSuspend() == Taskflow.SUSPEND_YES) {
					// 如果某个父流程是禁用的，则不参与判断
					continue;
				}
				// 通过流程时间做判断，如果要运行的流程的结束时间大于任何一个父流程的进度时间，则不能运行。
				if (TimeUtils.getNewTime(taskflow.getStatTime(), taskflow.getStepType(), taskflow.getStep())
						.getTime() > fatherTaskflow.getStatTime().getTime()) {
					list.add(fatherTaskflow);
				}
			}
		}

		return list;

	}

	public Task queryOutlineTaskByTaskflow(Taskflow taskflow) {
		// 用流程名，在大纲流程中找出以此名称命名的任务
		// 找出所有大纲流程
		List<Taskflow> outlineTaskflowList = queryTaskflowInGroup(outlineGroupId);

		Task taskInOutline = null;

		// 在所有大纲流程中找出以taskflow的流程名命名的大纲任务

		for (Taskflow outlineTaskflow : outlineTaskflowList) {

			taskInOutline = queryTaskByName(outlineTaskflow.getTaskflowID(), taskflow.getTaskflow());

			if (taskInOutline != null) {
				// 找到一个大纲
				// 按照设计约束，不能有同名流程，所以可以直接退出
				break;
			}
		}
		return taskInOutline;
	}

	public Task queryOutlineTaskByTaskflowID(Integer taskflowID) {
		return queryOutlineTaskByTaskflow(queryTaskflow(taskflowID));
	}

	public Taskflow queryTaskflowByOutlineTask(Task outLineTask) {
		Taskflow taskflow = null;
		taskflow = this.queryTaskflow(outLineTask.getTask());

		return taskflow;
	}

	/**
	 * 加载基础数据,测试
	 */
	// public synchronized void loadTestBasicData() {
	// // 制造一些测试数据: taskTypeMap，sysConfigMap，stepTypeMap
	// //
	// 空任务:null，对帐:auditor,格式化:format,入库:load,聚合:compress,同步:datasyn,统计:stat，备份:backup，
	// // 以下为预留
	// // 脚本：shell，存储过程:plsql,SQL语句:sql,邮件:email,文件传输:ftp,抽取:extract
	// TaskType tt0 = new TaskType("null", "空任务", "");
	// TaskType tt1 = new TaskType("auditor", "对账",
	// "../../bin/autitor.exe");
	// TaskType tt2 = new TaskType("format", "格式化",
	// "../../bin/dataformat.exe");
	// TaskType tt3 = new TaskType("load", "入库", "../../bin/load.exe");
	// TaskType tt4 = new TaskType("PLSQL", "聚合", "../../bin/callproc.exe");
	// TaskType tt5 = new TaskType("datasyn", "同步",
	// "../../bin/datasyn.exe");
	// TaskType tt6 = new TaskType("compress", "聚合",
	// "../../bin/compress.exe");
	// TaskType tt7 = new TaskType("stat", "统计", "../../bin/stat.exe");
	//
	// taskTypeMap.put(tt0.getTaskType(), tt0);
	// taskTypeMap.put(tt1.getTaskType(), tt1);
	// taskTypeMap.put(tt2.getTaskType(), tt2);
	// taskTypeMap.put(tt3.getTaskType(), tt3);
	// taskTypeMap.put(tt4.getTaskType(), tt4);
	// taskTypeMap.put(tt5.getTaskType(), tt5);
	// taskTypeMap.put(tt6.getTaskType(), tt6);
	// taskTypeMap.put(tt7.getTaskType(), tt7);
	//
	// // #######
	//
	// SysConfig sc2 = new SysConfig("LogToOAM", "1");
	// SysConfig sc4 = new SysConfig("dbconn_cfg",
	// "../../cfg/dbconection.ini");
	// SysConfig sc5 = new SysConfig("LogPath", "../../log");
	// SysConfig sc6 = new SysConfig("Interval", "2");
	// SysConfig sc7 = new SysConfig("RunFlag", "1");
	// SysConfig sc8 = new SysConfig("ExitCtrlFile", "../../control.ini");
	//
	// // sysConfigMap.put(sc2.getConfigName(), sc2.getConfigValue());
	// // sysConfigMap.put(sc4.getConfigName(), sc4.getConfigValue());
	// // sysConfigMap.put(sc5.getConfigName(), sc5.getConfigValue());
	// // sysConfigMap.put(sc6.getConfigName(), sc6.getConfigValue());
	// // sysConfigMap.put(sc7.getConfigName(), sc7.getConfigValue());
	// // sysConfigMap.put(sc8.getConfigName(), sc8.getConfigValue());
	//
	// StepType st1 = new StepType(StepType.SECOND, "秒",
	// StepType.DISPLAY_NO,
	// 1);
	// StepType st2 = new StepType(StepType.MINUTE, "分",
	// StepType.DISPLAY_NO,
	// 2);
	// StepType st3 = new StepType(StepType.HOUR, "时", StepType.DISPLAY_NO,
	// 3);
	// StepType st4 = new StepType(StepType.DAY, "日", StepType.DISPLAY_YES,
	// 4);
	// StepType st5 = new StepType(StepType.WEEK, "周", StepType.DISPLAY_YES,
	// 5);
	// StepType st6 = new StepType(StepType.HALF_MONTH, "半月",
	// StepType.DISPLAY_NO, 6);
	// StepType st7 = new StepType(StepType.MONTH, "月",
	// StepType.DISPLAY_YES,
	// 7);
	// StepType st8 = new StepType(StepType.SEASON, "季",
	// StepType.DISPLAY_NO,
	// 8);
	// StepType st9 = new StepType(StepType.HALF_YEAR, "半年",
	// StepType.DISPLAY_NO, 9);
	// StepType st10 = new StepType(StepType.YEAR, "年",
	// StepType.DISPLAY_YES,
	// 10);
	//
	// stepTypeMap.put(st1.getStepType(), st1);
	// stepTypeMap.put(st2.getStepType(), st2);
	// stepTypeMap.put(st3.getStepType(), st3);
	// stepTypeMap.put(st4.getStepType(), st4);
	// stepTypeMap.put(st5.getStepType(), st5);
	// stepTypeMap.put(st6.getStepType(), st6);
	// stepTypeMap.put(st7.getStepType(), st7);
	// stepTypeMap.put(st8.getStepType(), st8);
	// stepTypeMap.put(st9.getStepType(), st9);
	// stepTypeMap.put(st10.getStepType(), st10);
	//
	// }
	//
	// /**
	// * 测试用
	// */
	// public synchronized void loadTestData() {
	// // 制造一些测试数据：taskflowMap
	//
	// Date startDate = TimeUtils.toDate("20080101000000",
	// "yyyyMMddHHmmss");
	// Date sceneStartTime = TimeUtils.toDate("20080201000000",
	// "yyyyMMddHHmmss");
	// Date redoStartTime = TimeUtils.toDate("20080101000000",
	// "yyyyMMddHHmmss");
	// Date redoEndTime = TimeUtils.toDate("20080101000000",
	// "yyyyMMddHHmmss");
	//
	// // Taskflow tf1 = new Taskflow("TestFlow", startDate, "D",2);
	//
	// Taskflow tf1 = new Taskflow("TestFlow", startDate, "D", 1,
	// Taskflow.REDO_YES, sceneStartTime, redoStartTime, redoEndTime);
	// tf1.setFileLogLevel("DEBUG");
	//
	// // Date startDate = TimeUtils.toDate("20080101000000",
	// // "yyyyMMddHHmmss");
	//
	// // Taskflow tf1 = new Taskflow("TestFlow", startDate, "D",2);
	// taskflowMap.put(tf1.makeKey("TestFlow"), tf1);
	//
	// Task task0 = new Task(tf1.getTaskflow(), "start", "null", 200, 20);
	// task0.setIsRoot(1);
	// Task task1 = new Task(tf1.getTaskflow(), "datasyn", "datasyn", 400,
	// 20);
	// task1.setIsRoot(1);
	// Task task2 = new Task(tf1.getTaskflow(), "auditor", "auditor", 200,
	// 100);
	// Task task3 = new Task(tf1.getTaskflow(), "format", "format", 200,
	// 200);
	// Task task4 = new Task(tf1.getTaskflow(), "load", "load", 200, 300);
	// Task task5 = new Task(tf1.getTaskflow(), "aggregate", "compress",
	// 200,
	// 400);
	// Task task6 = new Task(tf1.getTaskflow(), "aggregate1", "compress",
	// 300,
	// 500);
	// Task task7 = new Task(tf1.getTaskflow(), "aggregate2", "compress",
	// 100,
	// 500);
	// Task task8 = new Task(tf1.getTaskflow(), "aggregate3", "compress",
	// 300,
	// 550);
	// Task task9 = new Task(tf1.getTaskflow(), "aggregate4", "compress",
	// 200,
	// 600);
	// Task task10 = new Task(tf1.getTaskflow(), "judgeFunction",
	// "compress",
	// 400, 300);
	// task10.setIsRoot(1);
	//
	// taskMap.put(task0.makeKey(), task0);
	// taskMap.put(task1.makeKey(), task1);
	// taskMap.put(task2.makeKey(), task2);
	// taskMap.put(task3.makeKey(), task3);
	// taskMap.put(task4.makeKey(), task4);
	// taskMap.put(task5.makeKey(), task5);
	// taskMap.put(task6.makeKey(), task6);
	// taskMap.put(task7.makeKey(), task7);
	// taskMap.put(task8.makeKey(), task8);
	// taskMap.put(task9.makeKey(), task9);
	// taskMap.put(task10.makeKey(tf1.getTaskflow(), "judgeFunction"),
	// task10);
	//
	// TaskAttribute ta1 = new TaskAttribute(tf1.getTaskflow(), task0
	// .getTask(), "CON_SELECT", "select * from dual", 0);
	// TaskAttribute ta2 = new TaskAttribute(tf1.getTaskflow(), task1
	// .getTask(), "CALL_AUDITOR", "1", 0);
	// TaskAttribute ta3 = new TaskAttribute(tf1.getTaskflow(), task2
	// .getTask(), "CHECKFILE_DELIMIT", "|", 0);
	// TaskAttribute ta4 = new TaskAttribute(tf1.getTaskflow(), task3
	// .getTask(), "CHECKFILE_NUM", "1", 0);
	// TaskAttribute ta5 = new TaskAttribute(tf1.getTaskflow(), task4
	// .getTask(), "CHECKFILE_ENDFLAG", "null", 0);
	// TaskAttribute ta6 = new TaskAttribute(tf1.getTaskflow(), task5
	// .getTask(), "CHECKFILE_NAMEPOS", "3", 0);
	//
	// taskAttributeMap.put(ta1.makeKey(), ta1);
	// taskAttributeMap.put(ta2.makeKey(), ta2);
	// taskAttributeMap.put(ta3.makeKey(), ta3);
	// taskAttributeMap.put(ta4.makeKey(), ta4);
	// taskAttributeMap.put(ta5.makeKey(), ta5);
	// taskAttributeMap.put(ta6.makeKey(), ta6);
	//
	// //
	// 空任务:null，对帐:auditor,格式化:format,入库:load,聚合:compress,同步:datasyn,统计:stat，备份:backup，
	// // 以下为预留
	// // 脚本：shell，存储过程:plsql,SQL语句:sql,邮件:email,文件传输:ftp,抽取:extract
	// TaskType tt0 = new TaskType("null", "空任务", "");
	// TaskType tt1 = new TaskType("auditor", "对账",
	// "../../bin/autitor.exe");
	// TaskType tt2 = new TaskType("format", "格式化",
	// "../../bin/dataformat.exe");
	// TaskType tt3 = new TaskType("load", "入库", "../../bin/load.exe");
	// TaskType tt4 = new TaskType("PLSQL", "聚合", "../../bin/callproc.exe");
	// TaskType tt5 = new TaskType("datasyn", "同步",
	// "../../bin/datasyn.exe");
	// TaskType tt6 = new TaskType("compress", "聚合",
	// "../../bin/compress.exe");
	// TaskType tt7 = new TaskType("stat", "统计", "../../bin/stat.exe");
	//
	// taskTypeMap.put(tt0.toString(), tt0);
	// taskTypeMap.put(tt1.toString(), tt1);
	// taskTypeMap.put(tt2.toString(), tt2);
	// taskTypeMap.put(tt3.toString(), tt3);
	// taskTypeMap.put(tt4.toString(), tt4);
	// taskTypeMap.put(tt5.toString(), tt5);
	// taskTypeMap.put(tt6.toString(), tt6);
	// taskTypeMap.put(tt7.toString(), tt7);
	//
	// Link link0 = new Link("TestFlow", "start", "auditor", "");
	// Link link1 = new Link("TestFlow", "datasyn", "load", "");
	// Link link2 = new Link("TestFlow", "auditor", "format", "");
	// Link link3 = new Link("TestFlow", "format", "load", "");
	// Link link4 = new Link("TestFlow", "load", "aggregate", "");
	// Link link5 = new Link("TestFlow", "aggregate", "aggregate1", "");
	// Link link6 = new Link("TestFlow", "aggregate", "aggregate2", "");
	// Link link7 = new Link("TestFlow", "aggregate1", "aggregate3", "");
	// Link link8 = new Link("TestFlow", "aggregate3", "aggregate4", "");
	// Link link9 = new Link("TestFlow", "aggregate2", "aggregate4", "");
	// Link link10 = new Link("TestFlow", "judgeFunction", "aggregate1",
	// "");
	// // Link link11 = new Link("TestFlow", "start", "datasyn", "");
	// // Link link12 = new Link("TestFlow", "start", "judgeFuncion", "");
	// Link link13 = new Link("TestFlow", "aggregate2", "aggregate1", "");
	//
	// linkMap.put(link0.makeKey(), link0);
	// linkMap.put(link1.makeKey(), link1);
	// linkMap.put(link2.makeKey(), link2);
	// linkMap.put(link3.makeKey(), link3);
	// linkMap.put(link4.makeKey(), link4);
	// linkMap.put(link5.makeKey(), link5);
	// linkMap.put(link6.makeKey(), link6);
	// linkMap.put(link7.makeKey(), link7);
	// linkMap.put(link8.makeKey(), link8);
	// linkMap.put(link9.makeKey(), link9);
	// linkMap.put(link10.makeKey(), link10);
	// // linkMap.put(link11.makeKey(), link11);
	// // linkMap.put(link12.makeKey(), link12);
	// linkMap.put(link13.makeKey(), link13);
	//
	// // #######
	//
	// SysConfig sc2 = new SysConfig("LogToOAM", "1");
	// SysConfig sc4 = new SysConfig("dbconn_cfg",
	// "../../cfg/dbconection.ini");
	// SysConfig sc5 = new SysConfig("LogPath", "../../log");
	// SysConfig sc6 = new SysConfig("Interval", "2");
	// SysConfig sc7 = new SysConfig("RunFlag", "1");
	// SysConfig sc8 = new SysConfig("ExitCtrlFile", "../../control.ini");
	//
	// // sysConfigMap.put(sc2.getConfigName(), sc2.getConfigValue());
	// // sysConfigMap.put(sc4.getConfigName(), sc4.getConfigValue());
	// // sysConfigMap.put(sc5.getConfigName(), sc5.getConfigValue());
	// // sysConfigMap.put(sc6.getConfigName(), sc6.getConfigValue());
	// // sysConfigMap.put(sc7.getConfigName(), sc7.getConfigValue());
	// // sysConfigMap.put(sc8.getConfigName(), sc8.getConfigValue());
	//
	// StepType st1 = new StepType(StepType.SECOND, "秒",
	// StepType.DISPLAY_NO,
	// 1);
	// StepType st2 = new StepType(StepType.MINUTE, "分",
	// StepType.DISPLAY_NO,
	// 2);
	// StepType st3 = new StepType(StepType.HOUR, "时", StepType.DISPLAY_NO,
	// 3);
	// StepType st4 = new StepType(StepType.DAY, "日", StepType.DISPLAY_YES,
	// 4);
	// StepType st5 = new StepType(StepType.WEEK, "周", StepType.DISPLAY_YES,
	// 5);
	// StepType st6 = new StepType(StepType.HALF_MONTH, "半月",
	// StepType.DISPLAY_NO, 6);
	// StepType st7 = new StepType(StepType.MONTH, "月",
	// StepType.DISPLAY_YES,
	// 7);
	// StepType st8 = new StepType(StepType.SEASON, "季",
	// StepType.DISPLAY_NO,
	// 8);
	// StepType st9 = new StepType(StepType.HALF_YEAR, "半年",
	// StepType.DISPLAY_NO, 9);
	// StepType st10 = new StepType(StepType.YEAR, "年",
	// StepType.DISPLAY_YES,
	// 10);
	//
	// stepTypeMap.put(st1.getStepType(), st1);
	// stepTypeMap.put(st2.getStepType(), st2);
	// stepTypeMap.put(st3.getStepType(), st3);
	// stepTypeMap.put(st4.getStepType(), st4);
	// stepTypeMap.put(st5.getStepType(), st5);
	// stepTypeMap.put(st6.getStepType(), st6);
	// stepTypeMap.put(st7.getStepType(), st7);
	// stepTypeMap.put(st8.getStepType(), st8);
	// stepTypeMap.put(st9.getStepType(), st9);
	// stepTypeMap.put(st10.getStepType(), st10);
	//
	// }
	//
	// // 再加一个
	// public synchronized void loadTestData2() {
	// // 制造一些测试数据
	//
	// Date startDate = TimeUtils.toDate("20080102000000",
	// "yyyyMMddHHmmss");
	// Date sceneStartTime = TimeUtils.toDate("20080201000000",
	// "yyyyMMddHHmmss");
	// Date redoStartTime = TimeUtils.toDate("20080102000000",
	// "yyyyMMddHHmmss");
	// Date redoEndTime = TimeUtils.toDate("20080102000000",
	// "yyyyMMddHHmmss");
	//
	// // Taskflow tf2 = new Taskflow("TestFlow2", startDate, "D",2);
	//
	// Taskflow tf2 = new Taskflow("TestFlow2", startDate, "D", 1,
	// Taskflow.REDO_YES, sceneStartTime, redoStartTime, redoEndTime);
	// tf2.setFileLogLevel("DEBUG");
	//
	// // Date startDate = TimeUtils.toDate("20080102000000",
	// // "yyyyMMddHHmmss");
	//
	// // Taskflow tf2 = new Taskflow("TestFlow2", startDate, "D",2);
	// taskflowMap.put(tf2.makeKey("TestFlow2"), tf2);
	//
	// Task task0 = new Task(tf2.getTaskflow(), "start", "null", 200, 20);
	// task0.setIsRoot(1);
	// Task task1 = new Task(tf2.getTaskflow(), "datasyn", "datasyn", 400,
	// 20);
	// task1.setIsRoot(1);
	// Task task2 = new Task(tf2.getTaskflow(), "auditor", "auditor", 200,
	// 100);
	// Task task3 = new Task(tf2.getTaskflow(), "format", "format", 200,
	// 200);
	// Task task4 = new Task(tf2.getTaskflow(), "load", "load", 200, 300);
	// Task task5 = new Task(tf2.getTaskflow(), "aggregate", "compress",
	// 200,
	// 400);
	// Task task6 = new Task(tf2.getTaskflow(), "aggregate1", "compress",
	// 300,
	// 500);
	// Task task7 = new Task(tf2.getTaskflow(), "aggregate2", "compress",
	// 100,
	// 500);
	// Task task8 = new Task(tf2.getTaskflow(), "aggregate3", "compress",
	// 300,
	// 550);
	// Task task9 = new Task(tf2.getTaskflow(), "aggregate4", "compress",
	// 200,
	// 600);
	// Task task10 = new Task(tf2.getTaskflow(), "judgeFunction",
	// "compress",
	// 400, 300);
	// task10.setIsRoot(1);
	//
	// taskMap.put(task0.makeKey(), task0);
	// taskMap.put(task1.makeKey(), task1);
	// taskMap.put(task2.makeKey(), task2);
	// taskMap.put(task3.makeKey(), task3);
	// taskMap.put(task4.makeKey(), task4);
	// taskMap.put(task5.makeKey(), task5);
	// taskMap.put(task6.makeKey(), task6);
	// taskMap.put(task7.makeKey(), task7);
	// taskMap.put(task8.makeKey(), task8);
	// taskMap.put(task9.makeKey(), task9);
	// taskMap.put(task10.makeKey(tf2.getTaskflow(), "judgeFunction"),
	// task10);
	//
	// TaskAttribute ta1 = new TaskAttribute(tf2.getTaskflow(), task0
	// .getTask(), "CON_SELECT", "select * from dual", 0);
	// TaskAttribute ta2 = new TaskAttribute(tf2.getTaskflow(), task1
	// .getTask(), "CALL_AUDITOR", "1", 0);
	// TaskAttribute ta3 = new TaskAttribute(tf2.getTaskflow(), task2
	// .getTask(), "CHECKFILE_DELIMIT", "|", 0);
	// TaskAttribute ta4 = new TaskAttribute(tf2.getTaskflow(), task3
	// .getTask(), "CHECKFILE_NUM", "1", 0);
	// TaskAttribute ta5 = new TaskAttribute(tf2.getTaskflow(), task4
	// .getTask(), "CHECKFILE_ENDFLAG", "null", 0);
	// TaskAttribute ta6 = new TaskAttribute(tf2.getTaskflow(), task5
	// .getTask(), "CHECKFILE_NAMEPOS", "3", 0);
	//
	// taskAttributeMap.put(ta1.makeKey(), ta1);
	// taskAttributeMap.put(ta2.makeKey(), ta2);
	// taskAttributeMap.put(ta3.makeKey(), ta3);
	// taskAttributeMap.put(ta4.makeKey(), ta4);
	// taskAttributeMap.put(ta5.makeKey(), ta5);
	// taskAttributeMap.put(ta6.makeKey(), ta6);
	//
	// //
	// 空任务:null，对帐:auditor,格式化:format,入库:load,聚合:compress,同步:datasyn,统计:stat，备份:backup，
	// // 以下为预留
	// // 脚本：shell，存储过程:plsql,SQL语句:sql,邮件:email,文件传输:ftp,抽取:extract
	// TaskType tt0 = new TaskType("null", "空任务", "");
	// TaskType tt1 = new TaskType("auditor", "对账",
	// "../../bin/autitor.exe");
	// TaskType tt2 = new TaskType("format", "格式化",
	// "../../bin/dataformat.exe");
	// TaskType tt3 = new TaskType("load", "入库", "../../bin/load.exe");
	// TaskType tt4 = new TaskType("PLSQL", "聚合", "../../bin/callproc.exe");
	// TaskType tt5 = new TaskType("datasyn", "同步",
	// "../../bin/datasyn.exe");
	// TaskType tt6 = new TaskType("compress", "聚合",
	// "../../bin/compress.exe");
	// TaskType tt7 = new TaskType("stat", "统计", "../../bin/stat.exe");
	//
	// taskTypeMap.put(tt0.toString(), tt0);
	// taskTypeMap.put(tt1.toString(), tt1);
	// taskTypeMap.put(tt2.toString(), tt2);
	// taskTypeMap.put(tt3.toString(), tt3);
	// taskTypeMap.put(tt4.toString(), tt4);
	// taskTypeMap.put(tt5.toString(), tt5);
	// taskTypeMap.put(tt6.toString(), tt6);
	// taskTypeMap.put(tt7.toString(), tt7);
	//
	// Link link0 = new Link("TestFlow2", "start", "auditor", "");
	// Link link1 = new Link("TestFlow2", "datasyn", "load", "");
	// Link link2 = new Link("TestFlow2", "auditor", "format", "");
	// Link link3 = new Link("TestFlow2", "format", "load", "");
	// Link link4 = new Link("TestFlow2", "load", "aggregate", "");
	// Link link5 = new Link("TestFlow2", "aggregate", "aggregate1", "");
	// Link link6 = new Link("TestFlow2", "aggregate", "aggregate2", "");
	// Link link7 = new Link("TestFlow2", "aggregate1", "aggregate3", "");
	// Link link8 = new Link("TestFlow2", "aggregate3", "aggregate4", "");
	// Link link9 = new Link("TestFlow2", "aggregate2", "aggregate4", "");
	// Link link10 = new Link("TestFlow2", "judgeFunction", "aggregate1",
	// "");
	// // Link link11 = new Link("TestFlow2", "start", "datasyn", "");
	// // Link link12 = new Link("TestFlow2", "start", "judgeFuncion", "");
	// Link link13 = new Link("TestFlow2", "aggregate2", "aggregate1", "");
	//
	// linkMap.put(link0.makeKey(), link0);
	// linkMap.put(link1.makeKey(), link1);
	// linkMap.put(link2.makeKey(), link2);
	// linkMap.put(link3.makeKey(), link3);
	// linkMap.put(link4.makeKey(), link4);
	// linkMap.put(link5.makeKey(), link5);
	// linkMap.put(link6.makeKey(), link6);
	// linkMap.put(link7.makeKey(), link7);
	// linkMap.put(link8.makeKey(), link8);
	// linkMap.put(link9.makeKey(), link9);
	// linkMap.put(link10.makeKey(), link10);
	// // linkMap.put(link11.makeKey(), link11);
	// // linkMap.put(link12.makeKey(), link12);
	// linkMap.put(link13.makeKey(), link13);
	//
	// // #######
	//
	// SysConfig sc2 = new SysConfig("LogToOAM", "1");
	// SysConfig sc4 = new SysConfig("dbconn_cfg",
	// "../../cfg/dbconection.ini");
	// SysConfig sc5 = new SysConfig("LogPath", "../../log");
	// SysConfig sc6 = new SysConfig("Interval", "2");
	// SysConfig sc7 = new SysConfig("RunFlag", "1");
	// SysConfig sc8 = new SysConfig("ExitCtrlFile", "../../control.ini");
	//
	// // sysConfigMap.put(sc2.getConfigName(), sc2.getConfigValue());
	// // sysConfigMap.put(sc4.getConfigName(), sc4.getConfigValue());
	// // sysConfigMap.put(sc5.getConfigName(), sc5.getConfigValue());
	// // sysConfigMap.put(sc6.getConfigName(), sc6.getConfigValue());
	// // sysConfigMap.put(sc7.getConfigName(), sc7.getConfigValue());
	// // sysConfigMap.put(sc8.getConfigName(), sc8.getConfigValue());
	//
	// StepType st1 = new StepType(StepType.SECOND, "秒",
	// StepType.DISPLAY_NO,
	// 1);
	// StepType st2 = new StepType(StepType.MINUTE, "分",
	// StepType.DISPLAY_NO,
	// 2);
	// StepType st3 = new StepType(StepType.HOUR, "时", StepType.DISPLAY_NO,
	// 3);
	// StepType st4 = new StepType(StepType.DAY, "日", StepType.DISPLAY_YES,
	// 4);
	// StepType st5 = new StepType(StepType.WEEK, "周", StepType.DISPLAY_YES,
	// 5);
	// StepType st6 = new StepType(StepType.HALF_MONTH, "半月",
	// StepType.DISPLAY_NO, 6);
	// StepType st7 = new StepType(StepType.MONTH, "月",
	// StepType.DISPLAY_YES,
	// 7);
	// StepType st8 = new StepType(StepType.SEASON, "季",
	// StepType.DISPLAY_NO,
	// 8);
	// StepType st9 = new StepType(StepType.HALF_YEAR, "半年",
	// StepType.DISPLAY_NO, 9);
	// StepType st10 = new StepType(StepType.YEAR, "年",
	// StepType.DISPLAY_YES,
	// 10);
	//
	// stepTypeMap.put(st1.getStepType(), st1);
	// stepTypeMap.put(st2.getStepType(), st2);
	// stepTypeMap.put(st3.getStepType(), st3);
	// stepTypeMap.put(st4.getStepType(), st4);
	// stepTypeMap.put(st5.getStepType(), st5);
	// stepTypeMap.put(st6.getStepType(), st6);
	// stepTypeMap.put(st7.getStepType(), st7);
	// stepTypeMap.put(st8.getStepType(), st8);
	// stepTypeMap.put(st9.getStepType(), st9);
	// stepTypeMap.put(st10.getStepType(), st10);
	//
	// }
	/**
	 * 通过系统参数的key,查找系统参数的value
	 * 
	 * @param sysConfigKey
	 * @return
	 */
	public synchronized String querySysConfigValue(String sysConfigKey) {
		String value = "";
		for (SysConfig config : sysConfigMap.values()) {
			if (config.getConfigName().equals(sysConfigKey)) {
				return config.getConfigValue();
			}
		}
		return value;
	}

	/**
	 * 通过系统参数的key,查找系统参数
	 * 
	 * @param sysConfigKey
	 * @return
	 */
	public synchronized SysConfig querySysConfig(String sysConfigKey) {
		for (SysConfig config : sysConfigMap.values()) {
			if (config.getConfigName().equals(sysConfigKey)) {
				return config;
			}
		}
		return null;
	}

	/**
	 * 通过系统参数的ID,查找系统参数
	 * 
	 * @param sysConfigID
	 * @return
	 */
	public synchronized SysConfig querySysConfig(Integer sysConfigID) {
		return sysConfigMap.get(sysConfigID);
	}

	/**
	 * 以Map<ConfigName,ConfigValue>的形式返回所有的系统参数
	 * 
	 * @return
	 */
	public synchronized Map<String, String> querySysConfigMap() {
		Map<String, String> map = new HashMap<String, String>();
		for (SysConfig config : sysConfigMap.values()) {
			map.put(config.getConfigName(), config.getConfigValue());
		}
		return map;
	}

	/**
	 * 以Map<ConfigName,ConfigValue>的形式返回与stage匹配的系统参数
	 * 
	 * @param stage
	 * @return
	 */
	public synchronized Map<String, String> querySysConfigMap(String stage) {
		Map<String, String> map = new HashMap<String, String>();
		for (SysConfig config : sysConfigMap.values()) {
			if (stage.equals(config.getStage())) {
				map.put(config.getConfigName(), config.getConfigValue());
			}
		}
		return map;
	}

	// public synchronized void insert(Taskflow taskflow) {
	// taskflowMap.put(taskflow.getTaskflowID(), taskflow);
	// }

	public synchronized void insert(Taskflow taskflow, String userID) {
		taskflowMap.put(taskflow.getTaskflowID(), taskflow);
		taskflowUserMap.put(taskflow.getTaskflowID(), new TaskflowUser(taskflow.getTaskflowID(), userID));
	}

	/**
	 * 检查是否有同名的流程.
	 * 
	 * @param taskflowName
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean isSameNameTaskflowExist(String taskflowName) throws Exception {
		boolean isExist = false;
		Taskflow taskflow = queryTaskflow(taskflowName);
		if (taskflow == null && getTaskflowIDbyName(taskflowName) == null) {
			isExist = false;
		} else {
			isExist = true;
		}
		return isExist;
	}

	/**
	 * @param taskflowName
	 * @return
	 * @throws Exception
	 */
	public synchronized Integer getTaskflowIDbyName(String taskflowName) throws Exception {
		return dao.getTaskflowIDbyName(taskflowName);
	}

	/**
	 * 从内存中删除
	 * 
	 * @param taskflowID
	 */
	public synchronized void deleteTaskflowInMemory(Integer taskflowID) {
		// 删link
		List<Link> linkList = queryLinkList(taskflowID);
		if (linkList != null) {
			for (Link link : linkList) {
				delete(link);
			}
		}

		// 删task
		List<Task> taskList = queryTaskList(taskflowID);
		if (taskList != null) {
			for (Task task : taskList) {
				/*
				 * // 删除TaskAttribute
				 * 
				 * List<TaskAttribute> taskAttributeList =
				 * queryTaskAttributeList(task .getTaskID()); if
				 * (taskAttributeList != null) { for (TaskAttribute
				 * taskAttribute : taskAttributeList) { delete(taskAttribute); }
				 * }
				 */
				delete(task);
			}
		}
		// 删note
		List<Note> noteList = queryNoteList(taskflowID);
		if (noteList != null) {
			for (Note note : noteList) {
				delete(note);
			}
		}
		taskflowMap.remove(taskflowID);

		taskflowUserMap.remove(taskflowID);
	}

	public synchronized void insert(Task task) {
		taskMap.put(task.getTaskID(), task);

	}

	public synchronized void delete(Task task) {
		taskMap.remove(task.getTaskID());
	}

	public synchronized void insert(TaskAttribute taskAttribute) {
		taskAttributeMap.put(taskAttribute.getAttributeID(), taskAttribute);
	}

	public synchronized void insert(TaskType taskType) {
		taskTypeMap.put(taskType.getTaskTypeID(), taskType);
	}

	public synchronized void insert(Category category) {
		categoryMap.put(category.getID(), category);
	}

	public synchronized void insert(User user) {
		userMap.put(user.getID(), user);
	}

	public synchronized void insert(Role role) {
		roleMap.put(role.getID(), role);
	}

	public synchronized void insert(Right right) {
		rightMap.put(right.getID(), right);
	}

	public synchronized void insert(UserRole userRole) {
		userRoleMap.put(userRole.getUserID() + userRole.getRoleID(), userRole);
	}

	public synchronized void insert(RoleRight roleRight) {
		roleRightMap.put(roleRight.getRoleID() + "_" + roleRight.getRightID(), roleRight);
	}

	public synchronized void insert(TaskflowUser taskflowUser) {
		taskflowUserMap.put(taskflowUser.getTaskflowID(), taskflowUser);
	}

	public synchronized void insert(TaskflowGroup taskflowGroup) {
		taskflowGroupMap.put(taskflowGroup.getGroupID(), taskflowGroup);
	}

	public synchronized void delete(TaskAttribute taskAttribute) {
		taskAttributeMap.remove(taskAttribute.getAttributeID());
	}

	public synchronized void insert(Link link) {
		linkMap.put(link.getLinkID(), link);
	}

	public synchronized void insert(Note note) {
		noteMap.put(note.getNoteID(), note);
	}

	public synchronized void insert(StepType stepType) {
		stepTypeMap.put(stepType.makeKey(), stepType);
	}

	public synchronized void insert(SysConfig sysConfig) {
		sysConfigMap.put(sysConfig.getID(), sysConfig);
	}

	public synchronized boolean insertSysConfig(SysConfig sysConfig) {
		try {
			dao.insert(sysConfig);
			return true;
		} catch (SQLException e) {

		} catch (Exception e) {
		}
		return false;
	}

	public synchronized void delete(Link link) {
		linkMap.remove(link.getLinkID());
	}

	public synchronized void delete(Category category) {
		categoryMap.remove(category.getID());
	}

	public synchronized void delete(User user) {
		userMap.remove(user.getID());
	}

	public synchronized void delete(Role role) {
		roleMap.remove(role.getID());
	}

	public synchronized void delete(Right right) {
		rightMap.remove(right.getID());
	}

	public synchronized void delete(UserRole userRole) {
		userRoleMap.remove(userRole.getUserID() + userRole.getRoleID());
	}

	public synchronized void delete(RoleRight roleRight) {
		roleRightMap.remove(roleRight.getRoleID() + "_" + roleRight.getRightID());
	}

	public synchronized void delete(TaskflowUser taskflowUser) {
		taskflowUserMap.remove(taskflowUser.getTaskflowID());
	}

	public synchronized void delete(TaskflowGroup taskflowGroup) {
		taskflowGroupMap.remove(taskflowGroup.getGroupID());
	}

	public synchronized void delete(Note note) {
		noteMap.remove(note.getNoteID());
	}

	public synchronized void delete(StepType stepType) {
		stepTypeMap.remove(stepType.makeKey());
	}

	public synchronized void delete(SysConfig sysConfig) {
		sysConfigMap.remove(sysConfig.getID());
	}

	public synchronized void deleteSysConfig(SysConfig sysConfig) {
		try {
			dao.delete(sysConfig);
		} catch (SQLException e) {

		} catch (Exception e) {
		}
	}

	public synchronized void delete(TaskType taskType) {
		taskTypeMap.remove(taskType.getTaskTypeID());
	}

	/**
	 * 取元数据的database连接.
	 * 
	 * @return
	 */
	public synchronized ConnInfo getConnInfo() {
		return connInfo;
	}

	/**
	 * 加载所有流程以及流程相关的任务，任务参数和连接 可以调用此方法实现数据库或xml文件与内存同步
	 * 
	 * @throws Exception
	 */
	public synchronized void loadAllTaskflowInfo() throws MetaDataException {
		try {
			dao.loadAllTaskflowInfo();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 加载指定流程以及流程相关的任务，任务参数和连接 可以调用此方法实现数据库或xml文件与内存同步
	 * 
	 * @throws Exception
	 */
	public synchronized void loadTaskflowInfo(Integer taskflowID) throws MetaDataException {
		try {
			dao.loadTaskflowInfo(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 加载放入数据库的流程以及流程相关的任务，任务参数和连接
	 * 
	 * @throws Exception
	 * @return list, 每个元素为flow name，为新加入数据库的
	 */
	public synchronized List<String> loadNewTaskflowInfo() throws MetaDataException {
		List<String> result = new ArrayList<String>();
		try {
			@SuppressWarnings("unchecked")
			List<Taskflow> flows = SqlMapUtil.executeQueryForList("getAllTaskflow", "");
			for (Taskflow taskflow : flows) {
				// 如果taskflow已存在，不加载
				if (this.taskflowMap.get(taskflow.getTaskflowID()) != null)
					continue;

				// 加载Taskflow
				this.loadTaskflow(taskflow.getTaskflowID());

				// 加载Taskflow下的link，note，task, attribute
				taskflow.setStatus(Taskflow.STOPPED); // 只有停止状态的taskflow可以更新
				dao.updateStatusOfTaskflow(taskflow.getTaskflowID(), Taskflow.STOPPED);
				this.updateTaskflowFromDb(taskflow.getTaskflowID());

				// 恢复
				taskflow = this.queryTaskflow(taskflow.getTaskflowID());
				taskflow.setStatus(Taskflow.READY);
				dao.updateStatusOfTaskflow(taskflow.getTaskflowID(), Taskflow.READY);

				result.add(taskflow.getTaskflow());
			}
			return result;
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 加载指定流程
	 * 
	 * @param taskflowID
	 * @return
	 * @throws MetaDataException
	 */
	public synchronized Taskflow loadTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
			if (taskflow != null) {
				taskflowMap.put(taskflow.getTaskflowID(), taskflow);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow;
	}

	/**
	 * 从xml文件导入流程以及流程相关的任务，任务参数和连接 ，用于设计器导入xml流程定义到内存。
	 * 
	 * @throws Exception
	 */
	public synchronized void importTaskflowInfo(String xmlFileName) throws MetaDataException {
		try {
			new XmlDaoImpl("").loadTaskflowInfo(xmlFileName);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 导出指定流程以及流程相关的任务，任务参数和连接到xml文件 ，用于设计器导出流程定义到xml文件,默认导出到指定目录。
	 * 
	 * @throws Exception
	 */
	public synchronized void exportTaskflowInfo(Integer taskflowID, String path) throws MetaDataException {
		try {
			new XmlDaoImpl("").saveTaskflowInfo(taskflowID, path);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 导出所有流程及其他到xml文件到指定目录。
	 * 
	 * @throws Exception
	 */
	public synchronized void exportAll(String path) throws MetaDataException {
		try {
			new XmlDaoImpl("").saveAll(path);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 导出所有流程及其他到xml文件到指定目录。
	 * 
	 * @throws Exception
	 */
	public synchronized void importAll(String dbDriver, String dbUser, String dbPassword, String dbURL)
			throws MetaDataException {

		throw new MetaDataException();

	}

	/**
	 * 加载基础数据,如周期类型,系统参数,任务类型等
	 * 
	 * @throws MetaDataException
	 */
	public synchronized void loadBasicInfo() throws MetaDataException {
		try {
			// dao.loadStepTypes();
			/*new XmlDaoImpl("").loadStepTypesFromSource();
			dao.loadSysConfigs();
			// dao.loadTaskTypes();
			new XmlDaoImpl("").loadTaskTypesFromSource();
			// dao.loadCategorys();
			new XmlDaoImpl("").loadCategorysFromSource();*/
			dao.loadTaskflowGroups();
			// 加载权限信息
			dao.loadUsers();
			dao.loadRoles();
			dao.loadRights();
			dao.loadUserRoles();
			dao.loadRoleRights();
			dao.loadTaskflowUsers();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 保存单个流程以及流程相关的任务，任务参数和连接
	 * 
	 * @param taskflowName
	 */
	public synchronized void saveTaskflowInfo(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			// 读取出错,没有taskflow
		}
		try {
			if (taskflow == null) {// 数据库里没有,就是全新的,执行全新插入流程
				dao.saveTaskflowInfo(taskflowID);
			} else {

				dao.updateTaskflowInfo(taskflowID);

				// updatetaskflowInfo方法不保存线程数，原因线程是保存在rpt_taskflow表中，而不是rpt_taskflow_template
				Taskflow taskflow2 = queryTaskflow(taskflowID);
				dao.updateThreadnumOfTaskflow(taskflowID, taskflow2.getThreadnum());
			}
			saveAllTaskflowUser();
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	/**
	 * 直接从更新到数据库
	 * 
	 * @param taskflowID
	 * @throws MetaDataException
	 */
	public synchronized void deleteTaskflowOnRespository(Integer taskflowID) throws MetaDataException {
		try {
			dao.deleteTaskflowInfo(taskflowID);
			TaskflowUser tu = queryTaskflowUser(taskflowID);
			if (tu != null) {
				dao.delete(tu);
			}
		} catch (Exception e) {

			throw new MetaDataException();
		}
	}

	/**
	 * 删除流程组下的所有流程
	 * 
	 * @param groupID
	 */
	public synchronized void deleteTaskflowInRroup(Integer groupID) {
		for (Taskflow element : taskflowMap.values()) {
			if (element.getGroupID().equals(groupID)) {
				taskflowMap.remove(element.getTaskflowID());
				taskflowUserMap.remove(element.getTaskflowID());
			}
		}
	}

	public synchronized Date loadStatTimeOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? null : taskflow.getStatTime();
	}

	public synchronized void updateStatTimeOfTaskflow(Integer taskflowID, Date statTime) throws MetaDataException {
		try {
			dao.updateStatTimeOfTaskflow(taskflowID, statTime);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setStatTime(statTime);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized int loadThreadnumOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? -1 : taskflow.getThreadnum();
	}

	public synchronized void updateThreadnumOfTaskflow(Integer taskflowID, int threadnum) throws MetaDataException {
		try {
			dao.updateThreadnumOfTaskflow(taskflowID, threadnum);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setThreadnum(threadnum);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void redo(Integer taskflowID, Date redoStartTime, Date redoEndTime) throws MetaDataException {
		updateRedoFlagOfTaskflow(taskflowID, Taskflow.REDO_YES);
		updateSceneStatTimeOfTaskflow(taskflowID, loadStatTimeOfTaskflow(taskflowID));
		updateStatTimeOfTaskflow(taskflowID, redoStartTime);
		updateRedoStartTimeOfTaskflow(taskflowID, redoStartTime);
		updateRedoEndTimeOfTaskflow(taskflowID, redoEndTime);
		List<Task> list = queryTaskList(taskflowID);
		for (Task task : list) {
			updateTaskStatus(task, Task.READY);
		}
		this.updateStatusOfTaskflow(queryTaskflow(taskflowID), Taskflow.READY);
	}

	public synchronized void reredo(Integer taskflowID, Date redoStartTime, Date redoEndTime) throws MetaDataException {
		updateStatTimeOfTaskflow(taskflowID, redoStartTime);
		updateRedoStartTimeOfTaskflow(taskflowID, redoStartTime);
		updateRedoEndTimeOfTaskflow(taskflowID, redoEndTime);
		List<Task> list = queryTaskList(taskflowID);
		for (Task task : list) {
			updateTaskStatus(task, Task.READY);
		}
		this.updateStatusOfTaskflow(queryTaskflow(taskflowID), Taskflow.READY);
	}

	public synchronized String loadFileLogLevelOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? null : taskflow.getFileLogLevel();
	}

	public synchronized void updateFileLogLevelOfTaskflow(Integer taskflowID, String fileLogLevel)
			throws MetaDataException {
		try {
			dao.updateFileLogLevelOfTaskflow(taskflowID, fileLogLevel);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setFileLogLevel(fileLogLevel);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized String loadDbLogLevelOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? null : taskflow.getDbLogLevel();
	}

	public synchronized void updateDbLogLevelOfTaskflow(Integer taskflowID, String dbLogLevel)
			throws MetaDataException {
		try {
			dao.updateDbLogLevelOfTaskflow(taskflowID, dbLogLevel);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setDbLogLevel(dbLogLevel);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized int loadRedoFlagOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? -1 : taskflow.getRedoFlag();
	}

	public synchronized void updateRedoFlagOfTaskflow(Integer taskflowID, int redoFlag) throws MetaDataException {
		try {
			dao.updateRedoFlagOfTaskflow(taskflowID, redoFlag);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setRedoFlag(redoFlag);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized Date loadSceneStatTimeOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? null : taskflow.getSceneStatTime();
	}

	public synchronized void updateSceneStatTimeOfTaskflow(Integer taskflowID, Date sceneStatTime)
			throws MetaDataException {
		try {
			dao.updateSceneStatTimeOfTaskflow(taskflowID, sceneStatTime);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setSceneStatTime(sceneStatTime);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized Date loadRedoStartTimeOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? null : taskflow.getRedoStartTime();
	}

	public synchronized void updateRedoStartTimeOfTaskflow(Integer taskflowID, Date redoStartTime)
			throws MetaDataException {
		try {
			dao.updateRedoStartTimeOfTaskflow(taskflowID, redoStartTime);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setRedoStartTime(redoStartTime);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized Date loadRedoEndTimeOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? null : taskflow.getRedoEndTime();
	}

	public synchronized void updateRedoEndTimeOfTaskflow(Integer taskflowID, Date redoEndTime)
			throws MetaDataException {
		try {
			dao.updateRedoEndTimeOfTaskflow(taskflowID, redoEndTime);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setRedoEndTime(redoEndTime);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized Date loadRunStartTimeOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? null : taskflow.getRunStartTime();
	}

	public synchronized Date loadRunStartTimeOfTask(Integer taskID) throws MetaDataException {
		Task task = null;
		try {
			task = dao.getTask(taskID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return task == null ? null : task.getRunStartTime();
	}

	public synchronized void updateRunStartTimeOfTaskflow(Integer taskflowID, Date runStartTime)
			throws MetaDataException {
		try {
			dao.updateRunStartTimeOfTaskflow(taskflowID, runStartTime);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setRunStartTime(runStartTime);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void updateRunStartTimeOfTask(Integer taskID, Date runStartTime) throws MetaDataException {
		try {
			dao.updateRunStartTimeOfTask(taskID, runStartTime);
			Task task = queryTask(taskID);
			if (task != null) {
				task.setRunStartTime(runStartTime);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized Date loadRunEndTimeOfTaskflow(Integer taskflowID) throws MetaDataException {
		Taskflow taskflow = null;
		try {
			taskflow = dao.getTaskflow(taskflowID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return taskflow == null ? null : taskflow.getRunEndTime();
	}

	public synchronized Date loadRunEndTimeOfTask(Integer taskID) throws MetaDataException {
		Task task = null;
		try {
			task = dao.getTask(taskID);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return task == null ? null : task.getRunEndTime();
	}

	public synchronized void updateRunEndTimeOfTaskflow(Integer taskflowID, Date runEndTime) throws MetaDataException {
		try {
			dao.updateRunEndTimeOfTaskflow(taskflowID, runEndTime);
			Taskflow taskflow = queryTaskflow(taskflowID);
			if (taskflow != null) {
				taskflow.setRunEndTime(runEndTime);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	public synchronized void updateRunEndTimeOfTask(Integer taskID, Date runEndTime) throws MetaDataException {
		try {
			dao.updateRunEndTimeOfTask(taskID, runEndTime);
			Task task = queryTask(taskID);
			if (task != null) {
				task.setRunEndTime(runEndTime);
			}
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
	}

	// public synchronized void add(Category category) throws
	// MetaDataException {
	// try {
	// dao.insert(category);
	// } catch (Exception e) {
	// throw new MetaDataException(e);
	// }
	// }
	//
	// public synchronized void add(StepType stepType) throws
	// MetaDataException {
	// try {
	// dao.insert(stepType);
	// } catch (Exception e) {
	// throw new MetaDataException(e);
	// }
	// }
	//
	// public synchronized void add(SysConfig sysConfig) throws
	// MetaDataException {
	// try {
	// dao.insert(sysConfig);
	// } catch (Exception e) {
	// throw new MetaDataException(e);
	// }
	// }
	//
	// public synchronized void add(TaskflowGroup taskflowGroup) throws
	// MetaDataException {
	// try {
	// dao.insert(taskflowGroup);
	// } catch (Exception e) {
	// throw new MetaDataException(e);
	// }
	// }
	//
	// public synchronized void add(TaskType taskType) throws
	// MetaDataException {
	// try {
	// dao.insert(taskType);
	// } catch (Exception e) {
	// throw new MetaDataException(e);
	// }
	// }

	/**
	 * 查询所有流程状态,如果其中有一只是运行就返回运行
	 * 
	 * @return
	 */
	public int queryAllTaskflowStatus() {
		List<Taskflow> list = null;
		list = queryAllTaskflow();
		for (Taskflow taskflow : list) {
			if (taskflow.getStatus() == Taskflow.SUSPEND_NO)
				return Taskflow.SUSPEND_NO;
		}
		return Taskflow.SUSPEND_YES;
	}

	/**
	 * 查询指定组的流程状态,如果其中有一只是运行就返回运行
	 * 
	 * @param groupID
	 * @return
	 */
	public int queryTaskflowGroupStatus(Integer groupID) {
		List<Taskflow> list = null;
		list = queryTaskflowInGroup(groupID);
		for (Taskflow taskflow : list) {
			if (taskflow.getStatus() == Taskflow.SUSPEND_NO)
				return Taskflow.SUSPEND_NO;
		}
		return Taskflow.SUSPEND_YES;
	}

	// test
	public static void main(String[] args) throws Exception {
		FlowMetaData.init("192.168.1.100", "9000", "ETL_BAS");
		FlowMetaData flowMetaData = FlowMetaData.getInstance();

		// 加载基础元数据
		flowMetaData.loadBasicInfo();
	}

	public boolean login(String userID, String password) {
		User user = queryUser(userID);
		if (user == null) {
			return false;
		}
		if (!password.equals(user.getPassword())) {
			return false;
		}
		return true;
	}

	/**
	 * 验证是否有权限
	 * 
	 * @param userID
	 * @param function
	 * @return
	 */
	public boolean isPermit(String userID, String name) {
		User user = queryUser(userID);
		if (user == null || name == null) {
			return false;
		}
		// 超级无敌管理员,拥有至高无尚的权限
		if ("admin".equals(user.getID())) {
			return true;
		}
		List<Right> list = queryRightOfUser(userID);
		for (Right right : list) {
			if (name.equals(right.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 用户是否对流程有权限
	 * 
	 * @param userID
	 * @param TaskflowID
	 * @return
	 */
	public boolean isTaskflowUser(String userID, Integer TaskflowID) {
		User user = queryUser(userID);
		if (user == null || TaskflowID == null) {
			return false;
		}
		// 超级无敌管理员,拥有至高无尚的权限
		if ("admin".equals(user.getID())) {
			return true;
		}
		TaskflowUser taskflowUser = this.queryTaskflowUser(TaskflowID);
		if (taskflowUser != null && taskflowUser.getUserID().equals(userID)) {
			return true;
		}
		return false;
	}

	/**
	 * 打包所有流程
	 * 
	 * @param zipfileName
	 *            zip文件名(全路径)
	 * @return
	 * @throws MetaDataException
	 */
	public boolean zipAllTaskflow(String zipfileName) throws MetaDataException {
		try {
			new XmlDaoImpl("").zipTaskflow2(zipfileName, null);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return true;
	}

	/**
	 * 打包指定流程
	 * 
	 * @param zipfileName
	 *            zip文件名(全路径)
	 * @param flowIDList
	 *            List里存放需要打包的流程ID
	 * @return
	 * @throws MetaDataException
	 */
	public boolean zipTaskflow(String zipfileName, List<Integer> flowIDList) throws MetaDataException {
		try {
			new XmlDaoImpl("").zipTaskflow2(zipfileName, flowIDList);
		} catch (Exception e) {
			throw new MetaDataException(e);
		}
		return true;
	}

	/**
	 * zip包导入数据库
	 * 
	 * @param zipfileName
	 *            zip文件名(全路径)
	 * @param isAllUpgrade
	 *            是否全量(true:全量; false:增量)
	 * @return String成功的流程,格式:taskflow1,taskflow2...
	 * @throws MetaDataException
	 */
	public String upgrade(String zipfileName, boolean isAllUpgrade) throws MetaDataException {
		String path = System.getProperty("java.io.tmpdir") + "/" + System.currentTimeMillis();
		String result = "";
		List<Map> mapList = new ArrayList<Map>();
		XmlDaoImpl xmlDao = new XmlDaoImpl("");
		ArrayList<String> fileNameList = new ArrayList<String>();

		ZipUtil.unZip(zipfileName, path);
		ZipUtil.getFileNamesFromDir(fileNameList, path);
		String currentFileName = "";

		try {
			for (String fileName : fileNameList) {

				currentFileName = new File(fileName).getName();

				// 只处理xml
				if (!fileName.endsWith(".xml")) {
					if (fileName.endsWith("readme.txt")) {
						String contents = ZipUtil.fileToString(fileName);
						/*
						 * 选择增量时不管它有没有标识，都更新原来的流程 if (sb.toString().indexOf(
						 * "all export") > -1) { if (!isAllUpgrade) { throw new
						 * MetaDataException( "增量升级只能用增量压缩包,请检查压缩包"); } } else
						 */if (contents.indexOf("increment export") > -1) {// 全量时做一下判断
							if (isAllUpgrade) {
								throw new MetaDataException("全量升级只能用全量压缩包,请检查压缩包");
							}
						}
					}
					continue;
				}

				if (fileName.endsWith("sysconfig.xml")) {
					List<SysConfig> sysConfigList = xmlDao.getSysConfigList(ZipUtil.fileToString(fileName));
					List<SysConfig> dbSysConfiglist = this.querySysConfigList();
					// 增量只插入不存在的信息
					for (SysConfig sysconfig : sysConfigList) {
						// System.out.println("xxxxx--->" +
						// sysconfig.getConfigName() + " " + sysconfig);
						boolean isExists = false;
						for (SysConfig config : dbSysConfiglist) {
							if (config.getConfigName().equals(sysconfig.getConfigName())) {
								isExists = true;
								break;
							}
						}

						if (!isExists) {
							dao.insert(sysconfig);
						}
					}

				} else if (fileName.endsWith("taskflowgroup.xml")) {
					List<TaskflowGroup> taskflowGroupList = xmlDao.getTaskflowGroupList(ZipUtil.fileToString(fileName));
					List<TaskflowGroup> dbTaskflowGroupList = this.queryTaskflowGroupList();
					if (!isAllUpgrade) {// 增量只插入不存在的信息
						for (TaskflowGroup taskflowGroup : dbTaskflowGroupList) {
							dao.delete(taskflowGroup);
						}
						for (TaskflowGroup taskflowGroup : taskflowGroupList) {
							dao.insert(taskflowGroup);
						}
					} else {
						for (TaskflowGroup taskflowGroup : taskflowGroupList) {
							boolean isExists = false;
							for (TaskflowGroup dbtaskflowGroup : dbTaskflowGroupList) {
								if (dbtaskflowGroup.getGroupID().equals(taskflowGroup.getGroupID())) {
									isExists = true;
									break;
								}
							}

							if (!isExists) {
								dao.insert(taskflowGroup);
							}
						}
					}

				} else if (fileName.endsWith("taskflowuser.xml")) {
					List<TaskflowUser> taskflowUserList = xmlDao.getTaskflowUserList(ZipUtil.fileToString(fileName));
					List<TaskflowUser> dbTaskflowUserlist = this.queryTaskflowUserList();
					if (!isAllUpgrade) {// 增量只插入不存在的信息
						for (TaskflowUser taskflowUser : dbTaskflowUserlist) {
							dao.delete(taskflowUser);
						}
						for (TaskflowUser taskflowUser : taskflowUserList) {
							dao.insert(taskflowUser);
						}
					} else {
						for (TaskflowUser taskflowUser : taskflowUserList) {
							// boolean isExists = false;
							/*
							 * for(TaskflowUser dbTaskflowUser :
							 * dbTaskflowUserlist){ if
							 * (dbTaskflowUser.getTaskflowID() ==
							 * taskflowUser.getTaskflowID()) { isExists = true;
							 * break; } }
							 */

							if (queryTaskflowUser(taskflowUser.getTaskflowID()) == null) {
								dao.insert(taskflowUser);
							}
						}
					}

				} else { // 获取流程数据：
					// 解释XML
					Map map = xmlDao.parseXML(ZipUtil.fileToString(fileName));
					mapList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MetaDataException("解释XML文件：" + currentFileName + "时出错: " + e.getMessage());
		} finally {
			ZipUtil.delDir(path);
			new File(path).delete();
		}

		// System.out.println("mapList.size() = " + mapList.size());
		if (mapList.size() == 0) {
			throw new MetaDataException("压缩包中并未包含xml流程文件！");
		}

		// 处理taskflow们
		List<Taskflow> allInTaskflow = new ArrayList<Taskflow>();
		List<Taskflow> oldAllTaskflow = queryAllTaskflow();
		List<Map> outlineList = new ArrayList<Map>();
		List<Map> taskflowList = new ArrayList<Map>();

		// 分捡大纲流程和普通流程
		for (Map map : mapList) {
			Taskflow newTaskflow = (Taskflow) map.get("inTaskflow");
			allInTaskflow.add(newTaskflow);
			if (newTaskflow.getGroupID() == outlineGroupId) {// 大纲流程
				outlineList.add(map);
			} else { // 普通流程
				taskflowList.add(map);
			}
		}

		if (isAllUpgrade) {// 全量,需要删除压缩包里没有的流程

			// 需要删除的流程列表：
			List<Taskflow> toRemoveTaskflow = new ArrayList<Taskflow>();
			for (Taskflow taskflow : allInTaskflow) {
				for (Taskflow oldTaskflow : oldAllTaskflow) {
					if (taskflow.getTaskflowID().equals(oldTaskflow.getTaskflowID())) {
						toRemoveTaskflow.add(oldTaskflow);
					}
				}
				result = result + "," + taskflow.getTaskflow();
			}
			// 数据库有,包里没有的
			oldAllTaskflow.removeAll(toRemoveTaskflow);
			for (Taskflow taskflow : oldAllTaskflow) {
				try {
					dao.deleteTaskflowInfo(taskflow.getTaskflowID());
					dao.delete(new TaskflowUser(taskflow.getTaskflowID(), "admin"));
				} catch (Exception e) {
					throw new MetaDataException("删除流程\"" + taskflow.getTaskflow() + "\"出错:" + e);
				}
			}
		}

		// 先导入大纲流程：
		importTaskflows(dao, outlineList);

		this.loadAllTaskflowInfo();
		if (getFirstOutlineFlow() == null) {
			throw new MetaDataException("流程导入失败: 未找到相应的大纲。");
		}

		// 再导入普通流程：
		importTaskflows(dao, taskflowList);

		result = result.replaceFirst(",", "");
		return result;
	}

	/**
	 * 从数据库取数据更新内存的流程信息（include:task,note,link）
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean updateTaskflowFromDb(int taskflowID) throws Exception {

		// 如果元数据连接方式是目录，则不进行刷新操作
		if (this.metadataConnectType == this.DIRECTORY_CONNECTION) {
			return false;
		}

		// 按流程ID从数据库获取流程，任务，任务参数属性，注释，link
		Taskflow inTaskflow = null;
		List<Task> inTaskList = new ArrayList<Task>();
		List<Note> inNoteList = new ArrayList<Note>();
		List<Link> inLinkList = new ArrayList<Link>();
		List<TaskAttribute> inTaskAttributeList = new ArrayList<TaskAttribute>();

		inTaskflow = dao.getTaskflow(taskflowID);
		if (inTaskflow.getStatus() != Taskflow.STOPPED) { // 流程必须处于停止状态，否则返回
			return false;
		}
		inTaskList = dao.getTasksInTaskflow(taskflowID);
		inNoteList = dao.getNotesInTaskflow(taskflowID);
		inLinkList = dao.getLinksInTaskflow(taskflowID);
		inTaskAttributeList = dao.getTaskAttributesInTaskflow(taskflowID);

		List<Task> memTaskList = queryTaskList(taskflowID);
		Set<Integer> keySet = new HashSet<Integer>();
		HashMap<Integer, String> curTaskMap = new HashMap<Integer, String>(); // 当前任务的Map，用于link和taskattribute时查找任务ID
		for (Task task : memTaskList) {
			curTaskMap.put(task.getTaskID(), "");
		}
		memTaskList.clear();

		// 更新link：
		{
			keySet.clear();
			Iterator<Entry<Integer, Link>> it = linkMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Link> entry = it.next();
				Link link = entry.getValue();
				if (curTaskMap.get(link.getFromTaskID()) != null || curTaskMap.get(link.getToTaskID()) != null) {
					keySet.add(entry.getKey());
				}
			}
			for (Integer key : keySet) {
				linkMap.remove(key);
			}
			for (Link link : inLinkList) {
				linkMap.put(link.getLinkID(), link);
			}
		}
		// 更新taskAttribute：
		{
			keySet.clear();
			Iterator<Entry<Integer, TaskAttribute>> it = taskAttributeMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, TaskAttribute> entry = it.next();
				TaskAttribute taskAttribute = entry.getValue();
				if (curTaskMap.get(taskAttribute.getTaskID()) != null) {
					keySet.add(entry.getKey());
				}
			}
			for (Integer key : keySet) {
				taskAttributeMap.remove(key);
			}
			for (TaskAttribute taskAttribute : inTaskAttributeList) {
				taskAttributeMap.put(taskAttribute.getAttributeID(), taskAttribute);
			}
		}

		// 更新Task：
		{
			keySet.clear();
			Iterator<Entry<Integer, Task>> it = taskMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Task> entry = it.next();
				Task task = entry.getValue();
				if (task.getTaskflowID() == taskflowID) {
					keySet.add(entry.getKey());
				}
			}
			for (Integer key : keySet) {
				taskMap.remove(key);
			}
			for (Task task : inTaskList) {
				taskMap.put(task.getTaskID(), task);
			}
		}

		// 更新注释：
		{
			keySet.clear();
			Iterator<Entry<Integer, Note>> it = noteMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Note> entry = it.next();
				Note note = entry.getValue();
				if (note.getTaskflowID() == taskflowID) {
					keySet.add(entry.getKey());
				}
			}
			for (Integer key : keySet) {
				noteMap.remove(key);
			}
			for (Note note : inNoteList) {
				noteMap.put(note.getNoteID(), note);
			}
		}

		taskflowMap.remove(taskflowID);
		taskflowMap.put(taskflowID, inTaskflow);

		return true;
	}

	public void refreshSysconfig() throws Exception {

		// 如果元数据连接方式是目录，则不进行刷新操作
		if (this.metadataConnectType == this.DIRECTORY_CONNECTION) {
			return;
		}

		dao.refreshSysconfig();
	}

	/**
	 * 解释流程xml文件，return map 内容如下：<br>
	 * key:inTaskflow, value:Taskflow<br>
	 * key:inTaskList, value:List&lt;Task><br>
	 * key:inNoteList, value:List&lt;Note><br>
	 * key:inLinkList, value:List&lt;Link><br>
	 * key:inTaskAttributeList,value:List&lt;TaskAttribute><br>
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public Map parseTaskflowXML(String fileName) throws Exception {
		return new XmlDaoImpl("").parseXML(ZipUtil.fileToString(fileName));
	}

	/**
	 * zip包导入数据库
	 * 
	 * @param zipfileName
	 *            zip文件名(全路径)
	 * @param isAllUpgrade
	 *            是否全量(true:全量; false:增量)
	 * @return String成功的流程,格式:taskflow1,taskflow2...
	 * @throws MetaDataException
	 *//*
		 * private String upgradeByUnzip(String zipfileName, boolean
		 * isAllUpgrade) throws MetaDataException {
		 * 
		 * 
		 * String path = System.getProperty("java.io.tmpdir") + "/" +
		 * System.currentTimeMillis(); String result = ""; List<Map> mapList =
		 * new ArrayList<Map>(); XmlDaoImpl xmlDao = new XmlDaoImpl("");
		 * ArrayList<String> fileNameList = new ArrayList<String>();
		 * 
		 * ZipUtil.unZip(zipfileName, path);
		 * ZipUtil.getFileNamesFromDir(fileNameList,path); String
		 * currentFileName = "";
		 * 
		 * 
		 * try { for(String fileName : fileNameList){
		 * 
		 * currentFileName = new File(fileName).getName();
		 * 
		 * // 只处理xml if (!fileName.endsWith(".xml")) { if
		 * (fileName.endsWith("readme.txt")) { String contents =
		 * ZipUtil.fileToString(fileName); 选择增量时不管它有没有标识，都更新原来的流程 if
		 * (sb.toString().indexOf("all export") > -1) { if (!isAllUpgrade) {
		 * throw new MetaDataException( "增量升级只能用增量压缩包,请检查压缩包"); } } else if
		 * (contents.indexOf("increment export") > -1) {//全量时做一下判断 if
		 * (isAllUpgrade) { throw new MetaDataException( "全量升级只能用全量压缩包,请检查压缩包");
		 * } } } continue; }
		 * 
		 * if (fileName.endsWith("SysConfig.xml")){ List<SysConfig>
		 * sysConfigList =
		 * xmlDao.getSysConfigList(ZipUtil.fileToString(fileName));
		 * List<SysConfig> dbSysConfiglist = this.querySysConfigList();
		 * //增量只插入不存在的信息 for(SysConfig sysconfig : sysConfigList){ boolean
		 * isExists = false; for(SysConfig config : dbSysConfiglist){ if
		 * (config.getConfigName().equals(sysconfig.getConfigName())){ isExists
		 * = true; break; } }
		 * 
		 * if (!isExists){ dao.insert(sysconfig); } }
		 * 
		 * } else if (fileName.endsWith("TaskflowGroup.xml")){
		 * List<TaskflowGroup> taskflowGroupList =
		 * xmlDao.getTaskflowGroupList(ZipUtil.fileToString(fileName));
		 * List<TaskflowGroup> dbTaskflowGroupList =
		 * this.queryTaskflowGroupList(); if (!isAllUpgrade){//增量只插入不存在的信息
		 * for(TaskflowGroup taskflowGroup : dbTaskflowGroupList){
		 * dao.delete(taskflowGroup); } for(TaskflowGroup taskflowGroup :
		 * taskflowGroupList){ dao.insert(taskflowGroup); } }else{
		 * for(TaskflowGroup taskflowGroup : taskflowGroupList){ boolean
		 * isExists = false; for(TaskflowGroup dbtaskflowGroup :
		 * dbTaskflowGroupList){ if
		 * (dbtaskflowGroup.getGroupID().equals(taskflowGroup.getGroupID())){
		 * isExists = true; break; } }
		 * 
		 * if (!isExists){ dao.insert(taskflowGroup); } } }
		 * 
		 * 
		 * } else if (fileName.endsWith("TaskflowUser.xml")){ List<TaskflowUser>
		 * taskflowUserList =
		 * xmlDao.getTaskflowUserList(ZipUtil.fileToString(fileName));
		 * List<TaskflowUser> dbTaskflowUserlist = this.queryTaskflowUserList();
		 * if (!isAllUpgrade){//增量只插入不存在的信息 for(TaskflowUser taskflowUser :
		 * dbTaskflowUserlist){ dao.delete(taskflowUser); } for(TaskflowUser
		 * taskflowUser : taskflowUserList){ dao.insert(taskflowUser); } }else{
		 * for(TaskflowUser taskflowUser : taskflowUserList){ boolean isExists =
		 * false; for(TaskflowUser dbTaskflowUser : dbTaskflowUserlist){ if
		 * (dbTaskflowUser.getTaskflowID() == taskflowUser.getTaskflowID() &&
		 * dbTaskflowUser.getUserID().equals(taskflowUser.getUserID()) ){
		 * isExists = true; break; } }
		 * 
		 * if (!isExists){ dao.insert(taskflowUser); } } }
		 * 
		 * } else { //获取流程数据： // 解释XML Map map =
		 * xmlDao.parseXML2(ZipUtil.fileToString(fileName)); mapList.add(map); }
		 * } } catch (Exception e) { throw new MetaDataException("解释XML文件："+
		 * currentFileName + "时出错: " + e.getMessage()); }finally{
		 * ZipUtil.delDir(path); }
		 * 
		 * 
		 * // 处理taskflow们 List<Taskflow> allInTaskflow = new
		 * ArrayList<Taskflow>(); List<Taskflow> oldAllTaskflow =
		 * queryAllTaskflow(); List<Map> outlineList = new ArrayList<Map>();
		 * List<Map> taskflowList = new ArrayList<Map>();
		 * 
		 * //分捡大纲流程和普通流程 for (Map map : mapList) { Taskflow newTaskflow =
		 * (Taskflow) map.get("inTaskflow"); allInTaskflow.add(newTaskflow); if
		 * (newTaskflow.getGroupID() == outlineGroupId){//大纲流程
		 * outlineList.add(map); } else { //普通流程 taskflowList.add(map); } }
		 * 
		 * if (isAllUpgrade) {// 全量,需要删除压缩包里没有的流程
		 * 
		 * //需要删除的流程列表： List<Taskflow> toRemoveTaskflow = new
		 * ArrayList<Taskflow>(); for (Taskflow taskflow : allInTaskflow) { for
		 * (Taskflow oldTaskflow : oldAllTaskflow) { if
		 * (taskflow.getTaskflowID() .equals(oldTaskflow.getTaskflowID())) {
		 * toRemoveTaskflow.add(oldTaskflow); } } result = result + "," +
		 * taskflow.getTaskflow(); } // 数据库有,包里没有的
		 * oldAllTaskflow.removeAll(toRemoveTaskflow); for (Taskflow taskflow :
		 * oldAllTaskflow) { try {
		 * dao.deleteTaskflowInfo(taskflow.getTaskflowID()); dao.delete(new
		 * TaskflowUser(taskflow.getTaskflowID(), "admin")); } catch (Exception
		 * e) { throw new MetaDataException("删除流程\"" + taskflow.getTaskflow() +
		 * "\"出错:" + e); } } }
		 * 
		 * //先导入大纲流程： importTaskflows(dao,outlineList);
		 * 
		 * this.loadAllTaskflowInfo(); if (getFirstOutlineFlow() == null){ throw
		 * new MetaDataException("流程导入失败: 未找到相应的大纲。"); }
		 * 
		 * //再导入普通流程： importTaskflows(dao,taskflowList);
		 * 
		 * result = result.replaceFirst(",", ""); return result; }
		 */

	private void addOutlineTask(Taskflow outlineTaskFlow) {
		Task task = new Task(Utils.getRandomIntValue(), outlineTaskFlow.getTaskflowID(), "Outline" + 0, "Outline");
		task.setXPos(50);
		task.setYPos(50);
		task.setAlertID(String.valueOf(task.getTaskID()));
		task.setPerformanceID(String.valueOf(task.getTaskID()));
		task.setDescription(outlineTaskFlow.getDescription());
		task.setTask(outlineTaskFlow.getTaskflow());
	}

	/**
	 * 查询第一个大纲流程,找不到返回null
	 * 
	 * @return
	 */
	private Taskflow getFirstOutlineFlow() {
		List<Taskflow> list = this.queryAllTaskflow();
		for (Taskflow taskflow : list) {
			if (taskflow.getGroupID() == outlineGroupId) {
				return taskflow;
			}
		}

		return null;
	}

	/**
	 * 导入流程
	 * 
	 * @param dao
	 * @param mapList
	 * @throws MetaDataException
	 */
	@SuppressWarnings("unchecked")
	private void importTaskflows(SynDao dao, List<Map> mapList) throws MetaDataException {
		for (Map map : mapList) {
			Taskflow newTaskflow = (Taskflow) map.get("inTaskflow");
			Taskflow dbTaskflow = loadTaskflow(newTaskflow.getTaskflowID());
			if (dbTaskflow == null) {// 新的,执行全新插入
				try {
					if (newTaskflow.getGroupID() != outlineGroupId
							&& this.queryOutlineTaskByTaskflow(newTaskflow) == null) {
						addOutlineTask(getFirstOutlineFlow());
					}

					dao.importNewTaskflowInfo(newTaskflow, (List) map.get("inTaskList"), (List) map.get("inNoteList"),
							(List) map.get("inLinkList"), (List) map.get("inTaskAttributeList"));
					if (this.queryTaskflowUser(newTaskflow.getTaskflowID()) == null) {
						dao.insert(new TaskflowUser(newTaskflow.getTaskflowID(), "admin"));
					}
				} catch (Exception e) {
					throw new MetaDataException(e);
				}
			} else {// 修改的taskflow
				try {
					dao.importOldTaskflowInfo(newTaskflow, (List) map.get("inTaskList"), (List) map.get("inNoteList"),
							(List) map.get("inLinkList"), (List) map.get("inTaskAttributeList"));
					/*
					 * dao.update(new TaskflowUser(newTaskflow.getTaskflowID(),
					 * "admin"));
					 */
				} catch (Exception e) {
					e.printStackTrace();
					throw new MetaDataException(e);
				}
			}
		}
	}

	/**
	 * 压缩包信息 返加格式：全量/增量/未知压缩包,包含x个文件:\r\nfile1\r\nfile2...
	 * 
	 * @param zipfileName
	 * @return
	 * @throws MetaDataException
	 */
	public String zipInfo(String zipfileName) throws MetaDataException {
		File zipfile = new File(zipfileName);
		List<String> list = new ArrayList<String>();
		String info = "";
		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(zipfile));
			ZipEntry entry = null;
			try {
				while ((entry = in.getNextEntry()) != null) {
					// 说明文件
					if (!entry.getName().endsWith(".xml")) {
						list.add(0, entry.getName());
						if (entry.getName().equals("readme.txt")) {
							StringBuffer sb = new StringBuffer();
							byte[] buf = new byte[1024];
							int len;
							while ((len = in.read(buf)) > 0) {
								sb.append(new String(buf, 0, len));
							}
							if (sb.toString().indexOf("all export") > -1) {
								info = "全量压缩包";
							} else if (sb.toString().indexOf("increment export") > -1) {
								info = "增量压缩包";
							} else {
								info = "未知压缩包";
							}
						}
						continue;
					}
					list.add(entry.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new MetaDataException(entry.getName() + "文件格式错误:", e);
			}
		} catch (FileNotFoundException e) {
			throw new MetaDataException(new FileNotFoundException(zipfileName + "文件不存在"));
		}
		info = info + ",包含" + list.size() + "个文件:\r\n";
		for (String s : list) {
			info = info + s + "\r\n";
		}
		return info;
	}

	public static String getSecurityIP() {
		return securityIP;
	}

	public static void setSecurityIP(String securityIP) {
		FlowMetaData.securityIP = securityIP;
	}

	public static String getSecurityPort() {
		return securityPort;
	}

	public static void setSecurityPort(String securityPort) {
		FlowMetaData.securityPort = securityPort;
	}

	@SuppressWarnings("unused")
	private String listToString(List list) {
		StringBuffer sb = new StringBuffer();
		for (Object obj : list) {
			sb.append(obj.toString()).append(" | ");
		}
		return sb.toString();
	}

	/**
	 * 将制定流程和它里面的所有任务的状态都强制改为newStatus。
	 * 
	 * @param taskflow
	 * @param newStatus
	 */
	public void updateTaskflowAndAllTask(Taskflow taskflow, int newStatus) {
		try {
			updateStatusOfTaskflow(taskflow, newStatus);

			List<Task> alltask = queryTaskList(taskflow.getTaskflowID());
			for (Task task : alltask) {
				updateTaskStatus(task, newStatus);
			}
		} catch (MetaDataException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 影响分析 即查指定任务影响的所有子任务,以及递归查找子任务的子任务
	 * 
	 * @param fatherTask
	 *            in
	 * @param impactTaskList
	 *            所有影响的子任务列表 out
	 * @param impactLinkList
	 *            所有影响的子任务列表之间的Link out
	 */
	public synchronized void queryImpactTask(Task fatherTask, List<Task> impactTaskList, List<Link> impactLinkList) {
		List<Task> childTaskList = new ArrayList<Task>();

		// 根据link在流程中找出fatherTask的所有子任务
		for (Link link : linkMap.values()) {

			if (link.getFromTaskID().equals(fatherTask.getTaskID())) {
				//// 同一个流程中找到一个子任务
				Task aChildTask = taskMap.get(link.getToTaskID());
				if (aChildTask == null) {
					// 新加到设计器的节点尚未保存，在这里会是null。
					continue;
				} else {
					// 加到当前层次的子任务列表
					childTaskList.add(aChildTask);

					if (!impactTaskList.contains(aChildTask)) {
						// 同时加到总的子任务列表
						impactTaskList.add(aChildTask);
					}

					if (!impactLinkList.contains(link)) {
						impactLinkList.add(link);
					}
				}
			}
		}

		// 同名不同ID的任务，并且各自所属流程有父子关系，则认为也是子任务。
		if (fatherTask.getTaskType().equalsIgnoreCase("Table")) {
			List<Task> childTaskListBetweenTaskflow = queryChildTaskListBetweenTaskflowBySameName(fatherTask,
					impactLinkList);
			childTaskList.addAll(childTaskListBetweenTaskflow);
			impactTaskList.addAll(childTaskListBetweenTaskflow);
		}
		// 递归查找当前层次的子任务列表中每个任务的子任务。
		for (Task aTask : childTaskList) {
			queryImpactTask(aTask, impactTaskList, impactLinkList);
		}

	}

	/*
	 * 通过相同的任务名在流程之间查找子任务。 同名不同ID的任务，并且各自所属流程有父子关系，则认为也是子任务。
	 */
	public List<Task> queryChildTaskListBetweenTaskflowBySameName(Task atask, List<Link> impactLinkList) {
		List<Task> childTaskListBetweenTaskflow = new ArrayList<Task>();
		for (Task element : taskMap.values()) {
			if (element.getTask().equals(atask.getTask()) && !element.getTaskID().equals(atask.getTaskID())) {
				// 任务同名
				// 查出所属流程
				Taskflow aflow = queryTaskflow(atask.getTaskflowID());
				Taskflow bflow = queryTaskflow(element.getTaskflowID());

				// 再查流程再大纲中对应的节点
				Task outlineTask1 = this.queryOutlineTaskByTaskflow(aflow);
				Task outlineTask2 = this.queryOutlineTaskByTaskflow(bflow);

				if (queryLinkBetweenTasksByDirection(outlineTask1, outlineTask2) != null) {
					// 各自所属流程有父子关系
					childTaskListBetweenTaskflow.add(element);

					// 新建一个虚拟跨流程的link,并不保存，只用于画元数据影响分析图
					Link virtualLink = new Link();
					virtualLink.setLinkID(Utils.getRandomIntValue());
					virtualLink.setFromTaskID(atask.getTaskID());
					virtualLink.setToTaskID(element.getTaskID());

					impactLinkList.add(virtualLink);
				}
			}
		}
		return childTaskListBetweenTaskflow;
	}

	/**
	 * 血统分析 即查指定任务影响的所有父任务,以及递归查找父任务的父任务
	 * 
	 * @param childTask
	 *            in
	 * @param lineageTaskList
	 *            所有有血缘关系的父任务列表 out
	 * @param lineageLinkList
	 *            所有有血缘关系的父任务列表之间的Link out
	 */
	public synchronized void queryLineageTask(Task childTask, List<Task> lineageTaskList, List<Link> lineageLinkList) {
		List<Task> fatherTaskList = new ArrayList<Task>();

		// 根据link在流程中找出childTask的所有父任务
		for (Link link : linkMap.values()) {

			if (link.getToTaskID().equals(childTask.getTaskID())) {

				//// 同一个流程中找到一个父任务
				Task aFatherTask = taskMap.get(link.getFromTaskID());
				if (aFatherTask == null) {
					// 新加到设计器的节点尚未保存，在这里会是null。
					continue;
				} else {
					// 加到当前层次的父任务列表
					fatherTaskList.add(aFatherTask);

					if (!lineageTaskList.contains(aFatherTask)) {
						// 同时加到总的父任务列表
						lineageTaskList.add(aFatherTask);

					}

					if (!lineageLinkList.contains(link)) {
						lineageLinkList.add(link);
					}
				}
			}
		}

		// 同名不同ID的任务，并且各自所属流程有子-父关系，则认为也是父任务。
		if (childTask.getTaskType().equalsIgnoreCase("Table")) {
			List<Task> fatherTaskListBetweenTaskflow = queryFatherTaskListBetweenTaskflowBySameName(childTask,
					lineageLinkList);
			fatherTaskList.addAll(fatherTaskListBetweenTaskflow);
			lineageTaskList.addAll(fatherTaskListBetweenTaskflow);
		}
		// 递归查找当前层次的父任务列表中每个任务的父任务。
		for (Task aTask : fatherTaskList) {
			queryLineageTask(aTask, lineageTaskList, lineageLinkList);
		}

	}

	/*
	 * 通过相同的任务名在流程之间查找父任务。 同名不同ID的任务，并且各自所属流程有子-父关系，则认为也是父任务。
	 */
	public List<Task> queryFatherTaskListBetweenTaskflowBySameName(Task atask, List<Link> lineageLinkList) {
		List<Task> fatherTaskListBetweenTaskflow = new ArrayList<Task>();
		for (Task element : taskMap.values()) {
			if (element.getTask().equals(atask.getTask()) && !element.getTaskID().equals(atask.getTaskID())) {
				// 任务同名
				// 查出所属流程
				Taskflow aflow = queryTaskflow(atask.getTaskflowID());
				Taskflow bflow = queryTaskflow(element.getTaskflowID());

				// 再查流程再大纲中对应的节点
				Task outlineTask1 = this.queryOutlineTaskByTaskflow(aflow);
				Task outlineTask2 = this.queryOutlineTaskByTaskflow(bflow);

				if (queryLinkBetweenTasksByDirection(outlineTask2, outlineTask1) != null) {
					// 各自所属流程有父子关系
					fatherTaskListBetweenTaskflow.add(element);

					// 新建一个虚拟跨流程的link,并不保存，只用于画元数据影响分析图
					Link virtualLink = new Link();
					virtualLink.setLinkID(Utils.getRandomIntValue());
					virtualLink.setFromTaskID(element.getTaskID());
					virtualLink.setToTaskID(atask.getTaskID());

					lineageLinkList.add(virtualLink);
				}
			}
		}
		return fatherTaskListBetweenTaskflow;
	}

	/**
	 * 获取元数据库连接方式
	 * 
	 * @return
	 */
	public static int getMetadataConnectType() {
		return metadataConnectType;
	}

	/**
	 * 返回最新100条 TODO
	 * 
	 * @throws Exception
	 */
	public List<TaskHistory> getTaskHistory(int flowId) throws Exception {
		return SqlMapUtil.executeQueryForList("getAllTaskHistoryByTaskflowId", flowId);
	}

	public int getCountTaskHistoryByTaskflowId(Integer taskFlowId) throws Exception {
		return dao.getCountTaskHistoryByTaskflowId(taskFlowId);
	}

	/**
	 * 查询生成的文件名
	 * 
	 * @author chenhaitao
	 * @throws Exception
	 */
	public List<TaskHistory> getTaskHistoryByFlowIdAndStartTime(Map<String, Object> map) throws Exception {
		return SqlMapUtil.executeQueryForList("getTaskHistoryByFlowIdAndStartTime", map);
	}

	public synchronized boolean updateTaskFlowStatus(int taskFlowId, int status) throws Exception {
		// TODO Auto-generated method stub
		updateTaskflowAndAllTask(queryTaskflow(taskFlowId), status);
		return true;
	}
	public int getTaskHistoryNumByFlowId(Map<String, Object> map) throws Exception{
		return (Integer) SqlMapUtil.executeQueryForObject("getTaskHistoryNumByFlowId", map);
	}

	public Vector<Object> getPopulateParam() {
		return populateParam;
	}

	public void setPopulateParam(Vector<Object> populateParam) {
		this.populateParam = populateParam;
	}
	
}
