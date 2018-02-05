package com.aspire.etl.flowmetadata.dao;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.aspire.etl.flowdefine.*;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.ZipUtil;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.flowmetadata.dao.SynDao;
import com.aspire.etl.flowmetadata.dao.XmlDaoImpl;
import com.aspire.etl.tool.SqlMapUtil;
import com.aspire.etl.tool.TimeUtils;

/**
 * XML实现类
 * @author wuzhuokun
 *
 */
// 2008-05-21 wuzhuokun xml读取时如果有中文路径会报错,把read(String)方法换成read(File)可以解决
// 2008-05-21 wuzhuokun 修改动态参数的读取和写入保存原来回车换行等格式.
public class XmlDaoImpl implements SynDao {
	String repositoryPath = "";

	FlowMetaData flowMetaData ; 
		
	public XmlDaoImpl(String repositoryPath) {
		this.repositoryPath = repositoryPath;
		try {
			flowMetaData = FlowMetaData.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadTaskflows() throws Exception {
		
	}

	public void loadTasks() throws Exception {
		// 什么都不干
	}

	public void loadTaskAttributes() throws Exception {
		// 什么都不干
	}

	public void loadLinks() throws Exception {
		// 什么都不干
	}
	
	public void loadTaskTypes() throws Exception {
		loadTaskTypesFromSource();
//		List<TaskType> taskTypeList = new ArrayList<TaskType>();
//
//		SAXReader reader = new SAXReader();
//
//		Document document = reader.read(new File(this.repositoryPath
//				+ "/common/tasktype.xml"));
//
//		// 取根节点<tasktypes>
//		Element taskTypesElement = document.getRootElement();
//		// System.out.println("<" + taskTypesElement.getName() + ">");
//		// 遍历根节点<tasktypes>的所有子节点
//		for (Iterator i = taskTypesElement.elementIterator(); i.hasNext();) {
//
//			Element taskTypeNode = (Element) i.next();
//			// System.out.println(" <" + taskTypeNode.getName() + ">"
//			// + taskTypeNode.getTextTrim());
//
//			TaskType newTaskType = new TaskType();
//
//			// 遍历<tasktype>的子元素
//			for (Iterator ChildIterator = taskTypeNode.elementIterator(); ChildIterator
//					.hasNext();) {
//				Element taskTypeChildElement = (Element) ChildIterator.next();
//				// System.out.println(" <" + taskTypeChildElement.getName() +
//				// ">"
//				// + taskTypeChildElement.getTextTrim());
//
//				if ("id".equals(taskTypeChildElement.getName())) {
//					newTaskType.setTaskTypeID(Integer
//							.parseInt(taskTypeChildElement.getTextTrim()));
//				}
//
//				if ("name".equals(taskTypeChildElement.getName())) {
//					newTaskType.setTaskType(taskTypeChildElement.getTextTrim());
//
//				}
//				if ("engine_plugin_type".equals(taskTypeChildElement.getName())) {
//					newTaskType.setEnginePluginType(taskTypeChildElement
//							.getTextTrim());
//
//				}
//				if ("engine_plugin".equals(taskTypeChildElement.getName())) {
//					newTaskType.setEnginePlugin(taskTypeChildElement
//							.getTextTrim());
//
//				}
//				if ("engine_plugin_jar".equals(taskTypeChildElement.getName())) {
//					newTaskType.setEnginePluginJar(taskTypeChildElement
//							.getTextTrim());
//
//				}
//
//				if ("description".equals(taskTypeChildElement.getName())) {
//					newTaskType.setDescription(taskTypeChildElement
//							.getTextTrim());
//
//				}
//				if ("large_icon".equals(taskTypeChildElement.getName())) {
//					newTaskType
//							.setLargeIcon(taskTypeChildElement.getTextTrim());
//
//				}
//				if ("small_icon".equals(taskTypeChildElement.getName())) {
//					newTaskType
//							.setSmallIcon(taskTypeChildElement.getTextTrim());
//				}
//				if ("category_id".equals(taskTypeChildElement.getName())) {
//					newTaskType.setCategoryID(Integer
//							.parseInt(taskTypeChildElement.getTextTrim()));
//
//				}
//				if ("designer_plugin".equals(taskTypeChildElement.getName())) {
//					newTaskType.setDesignerPlugin(taskTypeChildElement
//							.getTextTrim());
//				}
//				if ("designer_plugin_jar"
//						.equals(taskTypeChildElement.getName())) {
//					newTaskType.setDesignerPluginJar(taskTypeChildElement
//							.getTextTrim());
//				}
//				taskTypeList.add(newTaskType);
//			}// end
//
//		}
//		for (TaskType taskType : taskTypeList) {
//			FlowMetaData.getInstance().taskTypeMap.put(
//					taskType.getTaskTypeID(), taskType);
//		}
	}

	/**
	 * 从jar包读
	 */
	public void loadTaskTypesFromSource() throws Exception {
		List<TaskType> taskTypeList = new ArrayList<TaskType>();

		SAXReader reader = new SAXReader();

//		Document document = reader.read(ClassLoader.getSystemResourceAsStream("common/tasktype.xml"));
		Document document = reader.read(XmlDaoImpl.class.getResource("common/tasktype.xml"));

		// 取根节点<tasktypes>
		Element taskTypesElement = document.getRootElement();
		// System.out.println("<" + taskTypesElement.getName() + ">");
		// 遍历根节点<tasktypes>的所有子节点
		for (Iterator i = taskTypesElement.elementIterator(); i.hasNext();) {

			Element taskTypeNode = (Element) i.next();
			// System.out.println(" <" + taskTypeNode.getName() + ">"
			// + taskTypeNode.getTextTrim());

			TaskType newTaskType = new TaskType();

			// 遍历<tasktype>的子元素
			for (Iterator ChildIterator = taskTypeNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element taskTypeChildElement = (Element) ChildIterator.next();
				// System.out.println(" <" + taskTypeChildElement.getName() +
				// ">"
				// + taskTypeChildElement.getTextTrim());

				if ("id".equals(taskTypeChildElement.getName())) {
					newTaskType.setTaskTypeID(Integer
							.parseInt(taskTypeChildElement.getTextTrim()));
				}

				if ("name".equals(taskTypeChildElement.getName())) {
					newTaskType.setTaskType(taskTypeChildElement.getTextTrim());

				}
				if ("engine_plugin_type".equals(taskTypeChildElement.getName())) {
					newTaskType.setEnginePluginType(taskTypeChildElement
							.getTextTrim());

				}
				if ("engine_plugin".equals(taskTypeChildElement.getName())) {
					newTaskType.setEnginePlugin(taskTypeChildElement
							.getTextTrim());

				}
				if ("engine_plugin_jar".equals(taskTypeChildElement.getName())) {
					newTaskType.setEnginePluginJar(taskTypeChildElement
							.getTextTrim());

				}

				if ("description".equals(taskTypeChildElement.getName())) {
					newTaskType.setDescription(taskTypeChildElement
							.getTextTrim());

				}
				if ("large_icon".equals(taskTypeChildElement.getName())) {
					newTaskType
							.setLargeIcon(taskTypeChildElement.getTextTrim());

				}
				if ("small_icon".equals(taskTypeChildElement.getName())) {
					newTaskType
							.setSmallIcon(taskTypeChildElement.getTextTrim());
				}
				if ("category_id".equals(taskTypeChildElement.getName())) {
					newTaskType.setCategoryID(Integer
							.parseInt(taskTypeChildElement.getTextTrim()));

				}
				if ("designer_plugin".equals(taskTypeChildElement.getName())) {
					newTaskType.setDesignerPlugin(taskTypeChildElement
							.getTextTrim());
				}
				if ("designer_plugin_jar"
						.equals(taskTypeChildElement.getName())) {
					newTaskType.setDesignerPluginJar(taskTypeChildElement
							.getTextTrim());
				}
			}// end
			taskTypeList.add(newTaskType);
		}
		for (TaskType taskType : taskTypeList) {
			FlowMetaData.getInstance().taskTypeMap.put(
					taskType.getTaskTypeID(), taskType);
		}
	}

	public void loadStepTypes() throws Exception {
		loadStepTypesFromSource();
//		List<StepType> stepTypeList = new ArrayList<StepType>();
//		SAXReader reader = new SAXReader();
//
//		Document document = reader.read(new File(this.repositoryPath
//				+ "/common/steptype.xml"));
//
//		// 取根节点<steptypes>
//		Element stepTypesElement = document.getRootElement();
//		// System.out.println("<" + stepTypesElement.getName() + ">");
//		// 遍历根节点<steptypes>的所有子节点
//		for (Iterator i = stepTypesElement.elementIterator(); i.hasNext();) {
//
//			Element stepTypeNode = (Element) i.next();
//			// System.out.println(" <" + stepTypeNode.getName() + ">"
//			// + stepTypeNode.getTextTrim());
//
//			StepType newStepType = new StepType();
//
//			// 遍历<steptype>的子元素
//			for (Iterator ChildIterator = stepTypeNode.elementIterator(); ChildIterator
//					.hasNext();) {
//				Element stepTypeChildElement = (Element) ChildIterator.next();
//				// System.out.println(" <" + stepTypeChildElement.getName() +
//				// ">"
//				// + stepTypeChildElement.getTextTrim());
//
//				if ("type".equals(stepTypeChildElement.getName())) {
//					newStepType.setStepType(stepTypeChildElement.getTextTrim());
//
//				}
//				if ("name".equals(stepTypeChildElement.getName())) {
//					newStepType.setStepName(stepTypeChildElement.getTextTrim());
//
//				}
//				if ("flag".equals(stepTypeChildElement.getName())) {
//					newStepType.setFlag(Integer.parseInt(stepTypeChildElement
//							.getTextTrim()));
//
//				}
//				if ("order".equals(stepTypeChildElement.getName())) {
//					newStepType.setOrder(Integer.parseInt(stepTypeChildElement
//							.getTextTrim()));
//
//				}
//
//				stepTypeList.add(newStepType);
//			}// end
//
//		}
//		for (StepType stepType : stepTypeList) {
//			FlowMetaData.getInstance().stepTypeMap.put(stepType.makeKey(),
//					stepType);
//		}

	}
	
	/**
	 * 从jar包读取
	 * @throws Exception
	 */
	public void loadStepTypesFromSource() throws Exception {
		List<StepType> stepTypeList = new ArrayList<StepType>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(XmlDaoImpl.class.getResource("common/steptype.xml"));

		// 取根节点<steptypes>
		Element stepTypesElement = document.getRootElement();
		// System.out.println("<" + stepTypesElement.getName() + ">");
		// 遍历根节点<steptypes>的所有子节点
		for (Iterator i = stepTypesElement.elementIterator(); i.hasNext();) {

			Element stepTypeNode = (Element) i.next();
			// System.out.println(" <" + stepTypeNode.getName() + ">"
			// + stepTypeNode.getTextTrim());

			StepType newStepType = new StepType();

			// 遍历<steptype>的子元素
			for (Iterator ChildIterator = stepTypeNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element stepTypeChildElement = (Element) ChildIterator.next();
				// System.out.println(" <" + stepTypeChildElement.getName() +
				// ">"
				// + stepTypeChildElement.getTextTrim());

				if ("type".equals(stepTypeChildElement.getName())) {
					newStepType.setStepType(stepTypeChildElement.getTextTrim());

				}
				if ("name".equals(stepTypeChildElement.getName())) {
					newStepType.setStepName(stepTypeChildElement.getTextTrim());

				}
				if ("flag".equals(stepTypeChildElement.getName())) {
					newStepType.setFlag(Integer.parseInt(stepTypeChildElement
							.getTextTrim()));

				}
				if ("order".equals(stepTypeChildElement.getName())) {
					newStepType.setOrder(Integer.parseInt(stepTypeChildElement
							.getTextTrim()));

				}
				
			}// end
			stepTypeList.add(newStepType);

		}
		for (StepType stepType : stepTypeList) {
			FlowMetaData.getInstance().stepTypeMap.put(stepType.getStepType(),
					stepType);
		}

	}

	public void loadCategorys() throws Exception {
		loadCategorysFromSource();
//		List<Category> categoryList = new ArrayList<Category>();
//		SAXReader reader = new SAXReader();
//
//		Document document = reader.read(new File(this.repositoryPath
//				+ "/common/category.xml"));
//
//		// 取根节点<categorys>
//		Element categorysElement = document.getRootElement();
//		// System.out.println("<" + categorysElement.getName() + ">");
//		// 遍历根节点<categorys>的所有子节点
//		for (Iterator i = categorysElement.elementIterator(); i.hasNext();) {
//
//			Element categoryNode = (Element) i.next();
//			// System.out.println(" <" + categoryNode.getName() + ">"
//			// + categoryNode.getTextTrim());
//
//			Category newCategory = new Category();
//
//			// 遍历<category>的子元素
//			for (Iterator ChildIterator = categoryNode.elementIterator(); ChildIterator
//					.hasNext();) {
//				Element categoryChildElement = (Element) ChildIterator.next();
//				// System.out.println(" <" + categoryChildElement.getName() +
//				// ">"
//				// + categoryChildElement.getTextTrim());
//
//				if ("id".equals(categoryChildElement.getName())) {
//					newCategory.setID(Integer.parseInt(categoryChildElement
//							.getTextTrim()));
//
//				}
//				if ("name".equals(categoryChildElement.getName())) {
//					newCategory.setName(categoryChildElement.getTextTrim());
//
//				}
//				if ("order".equals(categoryChildElement.getName())) {
//					newCategory.setOrder(Integer.parseInt(categoryChildElement
//							.getTextTrim()));
//
//				}
//
//				categoryList.add(newCategory);
//			}// end
//
//		}
//		for (Category category : categoryList) {
//			FlowMetaData.getInstance().categoryMap.put(category.getID(),
//					category);
//		}

	}
	
	/**
	 * 从jar包读取
	 * @throws Exception
	 */
	public void loadCategorysFromSource() throws Exception {
		List<Category> categoryList = new ArrayList<Category>();
		SAXReader reader = new SAXReader();

		Document document = reader.read(XmlDaoImpl.class.getResource("common/category.xml"));

		// 取根节点<categorys>
		Element categorysElement = document.getRootElement();
		// System.out.println("<" + categorysElement.getName() + ">");
		// 遍历根节点<categorys>的所有子节点
		for (Iterator i = categorysElement.elementIterator(); i.hasNext();) {

			Element categoryNode = (Element) i.next();
			// System.out.println(" <" + categoryNode.getName() + ">"
			// + categoryNode.getTextTrim());

			Category newCategory = new Category();

			// 遍历<category>的子元素
			for (Iterator ChildIterator = categoryNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element categoryChildElement = (Element) ChildIterator.next();
				// System.out.println(" <" + categoryChildElement.getName() +
				// ">"
				// + categoryChildElement.getTextTrim());

				if ("id".equals(categoryChildElement.getName())) {
					newCategory.setID(Integer.parseInt(categoryChildElement
							.getTextTrim()));

				}
				if ("name".equals(categoryChildElement.getName())) {
					newCategory.setName(categoryChildElement.getTextTrim());

				}
				if ("order".equals(categoryChildElement.getName())) {
					newCategory.setOrder(Integer.parseInt(categoryChildElement
							.getTextTrim()));

				}
			}// end
			categoryList.add(newCategory);
		}
		for (Category category : categoryList) {
			FlowMetaData.getInstance().categoryMap.put(category.getID(),
					category);
		}

	}
	
	
	/**
	 * 从xml内容中获取SysConfig
	 * @param xmlstring
	 * @return
	 * @throws Exception
	 */
	public List<SysConfig> getSysConfigList(String xmlstring)  throws Exception{
		
		//System.out.println(xmlstring)
		;
		List<SysConfig> sysconfigList = new ArrayList<SysConfig>();
		SAXReader reader = new SAXReader();
		InputStream is = new ByteArrayInputStream(xmlstring.getBytes());
		Document document = reader.read(is);

		// 取根节点
		Element sysconfigsElement = document.getRootElement();
		
		for (Iterator i = sysconfigsElement.elementIterator(); i.hasNext();) {

			Element sysconfigNode = (Element) i.next();

			SysConfig sysconfig = new SysConfig();

			// 遍历<category>的子元素
			for (Iterator ChildIterator = sysconfigNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element categoryChildElement = (Element) ChildIterator.next();

				if ("id".equals(categoryChildElement.getName())) {
					sysconfig.setID(Integer.parseInt(categoryChildElement
							.getTextTrim()));
				}
				if ("configname".equals(categoryChildElement.getName())) {
					sysconfig.setConfigName(categoryChildElement.getTextTrim());
				}
				if ("configvalue".equals(categoryChildElement.getName())) {
					sysconfig.setConfigValue(categoryChildElement
							.getTextTrim());
				}
				if ("configdesc".equals(categoryChildElement.getName())) {
					sysconfig.setConfigDesc(categoryChildElement
							.getTextTrim());
				}
				if ("stage".equals(categoryChildElement.getName())) {
					sysconfig.setStage(categoryChildElement
							.getTextTrim());
				}
				if ("type".equals(categoryChildElement.getName())) {
					sysconfig.setType(categoryChildElement
							.getTextTrim());
				}
			}// end
			sysconfigList.add(sysconfig);
		}
		
		return sysconfigList;
	}
	
	/**
	 * 从xml内容中获取TaskflowGroup
	 * @param xmlstring
	 * @return
	 * @throws Exception
	 */
	public List<TaskflowGroup> getTaskflowGroupList(String xmlstring)  throws Exception{
		
		//System.out.println(xmlstring);
		
		
		List<TaskflowGroup> taskflowGroup = new ArrayList<TaskflowGroup>();
		SAXReader reader = new SAXReader();
		InputStream is = new ByteArrayInputStream(xmlstring.getBytes());

		Document document = reader.read(is);

		// 取根节点
		Element taskflowgroupsElement = document.getRootElement();
		
		for (Iterator i = taskflowgroupsElement.elementIterator(); i.hasNext();) {

			Element taskflowgroupNode = (Element) i.next();

			TaskflowGroup taskflowgroup = new TaskflowGroup();

			// 遍历<category>的子元素
			for (Iterator ChildIterator = taskflowgroupNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element taskflowgroupChildElement = (Element) ChildIterator.next();

				if ("id".equals(taskflowgroupChildElement.getName())) {
					taskflowgroup.setGroupID(Integer.parseInt(taskflowgroupChildElement
							.getTextTrim()));
				}
				if ("name".equals(taskflowgroupChildElement.getName())) {
					taskflowgroup.setGroupName(taskflowgroupChildElement.getTextTrim());
				}
				if ("order".equals(taskflowgroupChildElement.getName())) {
					taskflowgroup.setGroupOrder(Integer.parseInt(taskflowgroupChildElement
							.getTextTrim()));
				}
				if ("groupDesc".equals(taskflowgroupChildElement.getName())) {
					taskflowgroup.setGroupDesc(taskflowgroupChildElement
							.getTextTrim());
				}
			}// end
			taskflowGroup.add(taskflowgroup);
		}
		
		return taskflowGroup;
	}
	
	/**
	 * 从xml内容中获取SysConfig
	 * @param xmlstring
	 * @return
	 * @throws Exception
	 */
	public List<TaskflowUser> getTaskflowUserList(String xmlstring)  throws Exception{
		
		//System.out.println(xmlstring)
		;
		List<TaskflowUser> taskflowUserlist = new ArrayList<TaskflowUser>();
		SAXReader reader = new SAXReader();
		InputStream is = new ByteArrayInputStream(xmlstring.getBytes());
		Document document = reader.read(is);

		// 取根节点
		Element taskflowusersElement = document.getRootElement();
		
		for (Iterator i = taskflowusersElement.elementIterator(); i.hasNext();) {

			Element taskflowuserNode = (Element) i.next();

			TaskflowUser taskflowuser = new TaskflowUser();

			// 遍历<category>的子元素
			for (Iterator ChildIterator = taskflowuserNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element taskflowuserChildElement = (Element) ChildIterator.next();

				if ("taskflowID".equals(taskflowuserChildElement.getName())) {
					taskflowuser.setTaskflowID(Integer.parseInt(taskflowuserChildElement
							.getTextTrim()));
				}
				if ("userID".equals(taskflowuserChildElement.getName())) {
					taskflowuser.setUserID(taskflowuserChildElement
							.getTextTrim());
				}
			}// end
			taskflowUserlist.add(taskflowuser);
		}
		
		return taskflowUserlist;
	}

	public void loadSysConfigs() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = null;
//		如果common目录下的文件存在，则加载它，否则加载jar包中的配置文件
		if (XmlDaoImpl.class.getResource("common/sysconfig.xml") != null){
			document = reader.read(XmlDaoImpl.class.getResource("common/sysconfig.xml"));
			loadSysConfigs(document);
		}  
		
		if (new File(this.repositoryPath + "/common/sysconfig.xml").exists()){
			document = reader.read(new File(this.repositoryPath + "/common/sysconfig.xml"));
			loadSysConfigs(document);
		} 
		
		
	}
	
	private void loadSysConfigs(Document document) throws Exception{
//		 取根节点
		Element sysconfigsElement = document.getRootElement();
		
		for (Iterator i = sysconfigsElement.elementIterator(); i.hasNext();) {

			Element sysconfigNode = (Element) i.next();

			SysConfig sysconfig = new SysConfig();

			// 遍历<category>的子元素
			for (Iterator ChildIterator = sysconfigNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element categoryChildElement = (Element) ChildIterator.next();

				if ("id".equals(categoryChildElement.getName())) {
					sysconfig.setID(Integer.parseInt(categoryChildElement
							.getTextTrim()));
				}
				if ("configname".equals(categoryChildElement.getName())) {
					sysconfig.setConfigName(categoryChildElement.getTextTrim());
				}
				if ("configvalue".equals(categoryChildElement.getName())) {
					sysconfig.setConfigValue(categoryChildElement
							.getTextTrim());
				}
				if ("configdesc".equals(categoryChildElement.getName())) {
					sysconfig.setConfigDesc(categoryChildElement
							.getTextTrim());
				}
				if ("stage".equals(categoryChildElement.getName())) {
					sysconfig.setStage(categoryChildElement
							.getTextTrim());
				}
				if ("type".equals(categoryChildElement.getName())) {
					sysconfig.setType(categoryChildElement
							.getTextTrim());
				}
			}// end
			FlowMetaData.getInstance().sysConfigMap.put(sysconfig.getID(), sysconfig);
		}
	}


	public void loadTaskflowInfo(Integer taskflowID) throws Exception {
		String taskflowName = FlowMetaData.getInstance().queryTaskflow(taskflowID).getTaskflow();
		String tasfflowFilePath = this.repositoryPath + "/taskflow/"+taskflowName+".xml";
		loadTaskflowInfo(tasfflowFilePath);		
	}
	

	public void loadAllTaskflowInfo() throws Exception {

		// 遍历
		File dir = new File(this.repositoryPath + "/taskflow/");

		// 只要xml文件，并且不要读取_bak文件
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if ((name.indexOf(".xml") != -1)
						&& (name.indexOf(".bak") == -1)) {
					return true;
				}
				return false;
			}
		});
		for (int i = 0; i < files.length; i++) {
			System.out.println("正在加载流程文件：" + i+ " " + files[i].getAbsolutePath());
			loadTaskflowInfo(files[i].getAbsolutePath());
		}

	}
	
		
	public void loadTaskflowInfo(String taskflowFile) throws Exception {
		SAXReader reader = new SAXReader();

		Document document = reader.read(new File(taskflowFile));

		// 取根节点<taskflow>
		Element taskflowElement = document.getRootElement();

		// System.out.println("<" + taskflowElement.getName() + ">");
		Taskflow newTaskflow = new Taskflow();

		// 遍历根节点<taskflow>的所有子节点
		for (Iterator i = taskflowElement.elementIterator(); i.hasNext();) {

			Element taskflowChildElement = (Element) i.next();
			// System.out.println(" <" + taskflowChildElement.getName() + ">"
			// + taskflowChildElement.getTextTrim());

			if ("id".equals(taskflowChildElement.getName())) {
				newTaskflow.setTaskflowID(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("name".equals(taskflowChildElement.getName())) {
				newTaskflow.setTaskflow(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("stepType".equals(taskflowChildElement.getName())) {
				newTaskflow.setStepType(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("step".equals(taskflowChildElement.getName())) {
				newTaskflow.setStep(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("statTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setStatTime(TimeUtils.toDate(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("status".equals(taskflowChildElement.getName())) {
				newTaskflow.setStatus(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("suspend".equals(taskflowChildElement.getName())) {
				newTaskflow.setSuspend(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("description".equals(taskflowChildElement.getName())) {
				newTaskflow.setDescription(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("memo".equals(taskflowChildElement.getName())) {
				newTaskflow.setMemo(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("redoFlag".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoFlag(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("sceneStatTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setSceneStatTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("redoStartTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoStartTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("redoEndTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoEndTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("fileLogLevel".equals(taskflowChildElement.getName())) {
				newTaskflow.setFileLogLevel(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("dbLogLevel".equals(taskflowChildElement.getName())) {
				newTaskflow.setDbLogLevel(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("threadnum".equals(taskflowChildElement.getName())) {
				newTaskflow.setThreadnum(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("runStartTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRunStartTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("runEndTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRunEndTime(TimeUtils.toDate(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("groupID".equals(taskflowChildElement.getName())) {
				newTaskflow.setGroupID(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			// 基本元素处理完，接下来处理<task>复合元素

			if ("task".equals(taskflowChildElement.getName())) {
				// 如果是task子元素
				Task newTask = new Task();
				newTask.setTaskflowID(newTaskflow.getTaskflowID());

				// 遍历<task>元素的子节点
				for (Iterator taskChildIterator = taskflowChildElement
						.elementIterator(); taskChildIterator.hasNext();) {
					Element taskChildElement = (Element) taskChildIterator
							.next();

					// System.out.println(" <" + taskChildElement.getName() +
					// ">"
					// + taskChildElement.getTextTrim());

					if ("id".equals(taskChildElement.getName())) {
						newTask.setTaskID(Integer.parseInt(taskChildElement
								.getTextTrim()));
					}
					if ("name".equals(taskChildElement.getName())) {
						newTask.setTask(taskChildElement.getTextTrim());
					}
					if ("taskType".equals(taskChildElement.getName())) {
						newTask.setTaskType(taskChildElement.getTextTrim());

					}
					if ("status".equals(taskChildElement.getName())) {
						newTask.setStatus(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("plantime".equals(taskChildElement.getName())) {
						newTask.setPlantime(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("isRoot".equals(taskChildElement.getName())) {
						newTask.setIsRoot(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("suspend".equals(taskChildElement.getName())) {
						newTask.setSuspend(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("description".equals(taskChildElement.getName())) {
						newTask.setDescription(taskChildElement.getTextTrim());

					}
					if ("memo".equals(taskChildElement.getName())) {
						newTask.setMemo(taskChildElement.getTextTrim());

					}
					if ("alertID".equals(taskChildElement.getName())) {
						newTask.setAlertID(taskChildElement.getTextTrim());

					}
					if ("performanceID".equals(taskChildElement.getName())) {
						newTask
								.setPerformanceID(taskChildElement
										.getTextTrim());

					}
					if ("xPos".equals(taskChildElement.getName())) {
						newTask.setXPos(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("yPos".equals(taskChildElement.getName())) {
						newTask.setYPos(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("runStartTime".equals(taskChildElement.getName())) {
						newTask.setRunStartTime(TimeUtils
								.toDate(taskChildElement.getTextTrim()));

					}
					if ("runEndTime".equals(taskChildElement.getName())) {
						newTask.setRunEndTime(TimeUtils.toDate(taskChildElement
								.getTextTrim()));

					}
					if ("attribute".equals(taskChildElement.getName())) {
						// 如果是<attribute>元素
						TaskAttribute newTaskAttribute = new TaskAttribute();

						newTaskAttribute.setTaskID(newTask.getTaskID());

						// 遍历<attribute>的子元素
						for (Iterator attributeChildIterator = taskChildElement
								.elementIterator(); attributeChildIterator
								.hasNext();) {
							Element attributeChildElement = (Element) attributeChildIterator
									.next();
							// System.out.println(" <"
							// + attributeChildElement.getName() + ">"
							// + attributeChildElement.getTextTrim());
							if ("id".equals(attributeChildElement.getName())) {
								newTaskAttribute.setAttributeID(Integer
										.parseInt(attributeChildElement
												.getTextTrim()));

							}
							if ("key".equals(attributeChildElement.getName())) {
								newTaskAttribute.setKey(attributeChildElement
										.getTextTrim());

							}
							if ("value".equals(attributeChildElement.getName())) {
								newTaskAttribute.setValue(attributeChildElement.getText());

							}
							FlowMetaData.getInstance().taskAttributeMap.put(
									newTaskAttribute.getAttributeID(),
									newTaskAttribute);
						}// end 遍历<attribute>的子元素

					}// end if ("attribue"

					FlowMetaData.getInstance().taskMap.put(newTask.getTaskID(),
							newTask);
				}// end 遍历<task>的子元素

			} // end if ("task"

			if ("link".equals(taskflowChildElement.getName())) {
				// 如果是link元素
				Link newLink = new Link();
				// newLink.setTaskflow(newTaskflow.getTaskflow());

				// 遍历link的子元素
				for (Iterator linkChildIterator = taskflowChildElement
						.elementIterator(); linkChildIterator.hasNext();) {
					Element linkChildElement = (Element) linkChildIterator
							.next();
					// System.out.println(" <" + linkChildElement.getName() +
					// ">"
					// + linkChildElement.getTextTrim());

					if ("id".equals(linkChildElement.getName())) {
						newLink.setLinkID(Integer.parseInt(linkChildElement
								.getTextTrim()));

					}
					if ("from".equals(linkChildElement.getName())) {
						newLink.setFromTaskID(Integer.parseInt(linkChildElement
								.getTextTrim()));

					}
					if ("to".equals(linkChildElement.getName())) {
						newLink.setToTaskID(Integer.parseInt(linkChildElement
								.getTextTrim()));

					}
					FlowMetaData.getInstance().linkMap.put(newLink.getLinkID(),
							newLink);
				}// end 遍历link的子元素

			}// end if ("link"
			if ("note".equals(taskflowChildElement.getName())) {
				// 如果是link元素
				Note newNote = new Note();
				newNote.setTaskflowID(newTaskflow.getTaskflowID());
			//遍历note的子元素
				for (Iterator noteChildIterator = taskflowChildElement
						.elementIterator(); noteChildIterator.hasNext();) {
					Element noteChildElement = (Element) noteChildIterator
					.next();
					// System.out.println(" <" + linkChildElement.getName() +
					// ">"
					// + linkChildElement.getTextTrim());
					
					if ("id".equals(noteChildElement.getName())) {
						newNote.setNoteID(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
					if ("value".equals(noteChildElement.getName())) {
						newNote.setValue(noteChildElement
								.getTextTrim());						
					}
					if ("x".equals(noteChildElement.getName())) {
						newNote.setXPos(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
					if ("y".equals(noteChildElement.getName())) {
						newNote.setYPos(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
					if ("width".equals(noteChildElement.getName())) {
						newNote.setWidth(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
					if ("height".equals(noteChildElement.getName())) {
						newNote.setHeight(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
				}// end 遍历note的子元素
				FlowMetaData.getInstance().noteMap.put(newNote.getNoteID(),
						newNote);
			}// end if ("note"
		}// 遍历根节点<taskflow>的所有子节点
		FlowMetaData.getInstance().taskflowMap.put(newTaskflow.getTaskflowID(),
				newTaskflow);

	}
	
	
/*	*//**
	 * 解释XML
	 * map.put("inTaskflow", inTaskflow);
	 * map.put("inTaskList", inTaskList);
	 * map.put("inNoteList", inNoteList);
	 * map.put("inLinkList", inLinkList);
	 * map.put("inTaskAttributeList", inTaskAttributeList);
	 * @param xmlFile
	 * @return
	 * @throws Exception
	 *//*
	public Map parseXML2(String xmlContext) throws Exception {
		Taskflow inTaskflow;
		List<Task> inTaskList = new ArrayList<Task>();
		List<Note> inNoteList = new ArrayList<Note>();
		List<Link> inLinkList = new ArrayList<Link>();
		List<TaskAttribute> inTaskAttributeList = new ArrayList<TaskAttribute>();
		
		SAXReader reader = new SAXReader();
		XStream xstream = new XStream(new DomDriver());   
		xstream.alias("Taskflow", Taskflow.class);
		xstream.alias("Task", Task.class);
		xstream.alias("TaskAttribute", TaskAttribute.class);
		xstream.alias("Link", Link.class);
		xstream.alias("Note", Note.class);
		
		Document document = reader.read(new StringReader(xmlContext));
		
		// 取根节点<taskflow>
		Element taskflowElement = document.getRootElement();
		
		// System.out.println("<" + taskflowElement.getName() + ">");
		Taskflow newTaskflow =(Taskflow)xstream.fromXML(taskflowElement.asXML());
		
		// 遍历根节点<taskflow>的所有子节点
		for (Iterator i = taskflowElement.elementIterator(); i.hasNext();) {
			
			
			Element taskflowChildElement = (Element) i.next();
			
			
			// 基本元素处理完，接下来处理<task>复合元素
			
			if ("Task".equals(taskflowChildElement.getName())) {
				// 如果是task子元素
				Task newTask =(Task)xstream.fromXML(taskflowChildElement.asXML());
				// 遍历<task>元素的子节点
				for (Iterator taskChildIterator = taskflowChildElement
						.elementIterator(); taskChildIterator.hasNext();) {
					Element taskChildElement = (Element) taskChildIterator
					.next();
					
					if ("TaskAttribute".equals(taskChildElement.getName())) {
						// 如果是<attribute>元素
						TaskAttribute newTaskAttribute = (TaskAttribute)xstream.fromXML(taskChildElement.asXML());
						inTaskAttributeList.add(newTaskAttribute);
					}// end if ("attribue"
				}// end 遍历<task>的子元素
				inTaskList.add(newTask);
			} // end if ("task"
			
			if ("Link".equals(taskflowChildElement.getName())) {
				// 如果是link元素
				Link newLink = (Link)xstream.fromXML(taskflowChildElement.asXML());
				inLinkList.add(newLink);
			}// end if ("link"
			if ("Note".equals(taskflowChildElement.getName())) {
				// 如果是link元素
				Note newNote = (Note)xstream.fromXML(taskflowChildElement.asXML());
				inNoteList.add(newNote);
			}// end if ("note"
			
			
		}
		
		inTaskflow = newTaskflow;
		Map map = new HashMap();
		map.put("inTaskflow", inTaskflow);
		map.put("inTaskList", inTaskList);
		map.put("inNoteList", inNoteList);
		map.put("inLinkList", inLinkList);
		map.put("inTaskAttributeList", inTaskAttributeList);
		return map;
	}
*/	/**
	 * 解释XML
	 * map.put("inTaskflow", inTaskflow);
	 * map.put("inTaskList", inTaskList);
	 * map.put("inNoteList", inNoteList);
	 * map.put("inLinkList", inLinkList);
	 * map.put("inTaskAttributeList", inTaskAttributeList);
	 * @param xmlFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map parseXML(String xmlContext) throws Exception {		
		Taskflow inTaskflow;
		List<Task> inTaskList = new ArrayList<Task>();
		List<Note> inNoteList = new ArrayList<Note>();
		List<Link> inLinkList = new ArrayList<Link>();
		List<TaskAttribute> inTaskAttributeList = new ArrayList<TaskAttribute>();
		
		SAXReader reader = new SAXReader();

		Document document = reader.read(new StringReader(xmlContext));

		// 取根节点<taskflow>
		Element taskflowElement = document.getRootElement();

		// System.out.println("<" + taskflowElement.getName() + ">");
		Taskflow newTaskflow = new Taskflow();

		// 遍历根节点<taskflow>的所有子节点
		for (Iterator i = taskflowElement.elementIterator(); i.hasNext();) {

			Element taskflowChildElement = (Element) i.next();
			// System.out.println(" <" + taskflowChildElement.getName() + ">"
			// + taskflowChildElement.getTextTrim());

			if ("id".equals(taskflowChildElement.getName())) {
				newTaskflow.setTaskflowID(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("name".equals(taskflowChildElement.getName())) {
				newTaskflow.setTaskflow(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("stepType".equals(taskflowChildElement.getName())) {
				newTaskflow.setStepType(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("step".equals(taskflowChildElement.getName())) {
				newTaskflow.setStep(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("statTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setStatTime(TimeUtils.toDate(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("status".equals(taskflowChildElement.getName())) {
				newTaskflow.setStatus(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("suspend".equals(taskflowChildElement.getName())) {
				newTaskflow.setSuspend(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("description".equals(taskflowChildElement.getName())) {
				newTaskflow.setDescription(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("memo".equals(taskflowChildElement.getName())) {
				newTaskflow.setMemo(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("redoFlag".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoFlag(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("sceneStatTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setSceneStatTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("redoStartTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoStartTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("redoEndTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoEndTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("fileLogLevel".equals(taskflowChildElement.getName())) {
				newTaskflow.setFileLogLevel(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("dbLogLevel".equals(taskflowChildElement.getName())) {
				newTaskflow.setDbLogLevel(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("threadnum".equals(taskflowChildElement.getName())) {
				newTaskflow.setThreadnum(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("runStartTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRunStartTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("runEndTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRunEndTime(TimeUtils.toDate(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("groupID".equals(taskflowChildElement.getName())) {
				newTaskflow.setGroupID(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			// 基本元素处理完，接下来处理<task>复合元素

			if ("task".equals(taskflowChildElement.getName())) {
				// 如果是task子元素
				Task newTask = new Task();
				newTask.setTaskflowID(newTaskflow.getTaskflowID());

				// 遍历<task>元素的子节点
				for (Iterator taskChildIterator = taskflowChildElement
						.elementIterator(); taskChildIterator.hasNext();) {
					Element taskChildElement = (Element) taskChildIterator
							.next();

					// System.out.println(" <" + taskChildElement.getName() +
					// ">"
					// + taskChildElement.getTextTrim());

					if ("id".equals(taskChildElement.getName())) {
						newTask.setTaskID(Integer.parseInt(taskChildElement
								.getTextTrim()));
					}
					if ("name".equals(taskChildElement.getName())) {
						newTask.setTask(taskChildElement.getTextTrim());
					}
					if ("taskType".equals(taskChildElement.getName())) {
						newTask.setTaskType(taskChildElement.getTextTrim());

					}
					if ("status".equals(taskChildElement.getName())) {
						newTask.setStatus(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("plantime".equals(taskChildElement.getName())) {
						newTask.setPlantime(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("isRoot".equals(taskChildElement.getName())) {
						newTask.setIsRoot(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("suspend".equals(taskChildElement.getName())) {
						newTask.setSuspend(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("description".equals(taskChildElement.getName())) {
						newTask.setDescription(taskChildElement.getTextTrim());

					}
					if ("memo".equals(taskChildElement.getName())) {
						newTask.setMemo(taskChildElement.getTextTrim());

					}
					if ("alertID".equals(taskChildElement.getName())) {
						newTask.setAlertID(taskChildElement.getTextTrim());

					}
					if ("performanceID".equals(taskChildElement.getName())) {
						newTask
								.setPerformanceID(taskChildElement
										.getTextTrim());

					}
					if ("xPos".equals(taskChildElement.getName())) {
						newTask.setXPos(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("yPos".equals(taskChildElement.getName())) {
						newTask.setYPos(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("runStartTime".equals(taskChildElement.getName())) {
						newTask.setRunStartTime(TimeUtils
								.toDate(taskChildElement.getTextTrim()));

					}
					if ("runEndTime".equals(taskChildElement.getName())) {
						newTask.setRunEndTime(TimeUtils.toDate(taskChildElement
								.getTextTrim()));

					}
					if ("attribute".equals(taskChildElement.getName())) {
						// 如果是<attribute>元素
						TaskAttribute newTaskAttribute = new TaskAttribute();

						newTaskAttribute.setTaskID(newTask.getTaskID());

						// 遍历<attribute>的子元素
						for (Iterator attributeChildIterator = taskChildElement
								.elementIterator(); attributeChildIterator
								.hasNext();) {
							Element attributeChildElement = (Element) attributeChildIterator
									.next();
							// System.out.println(" <"
							// + attributeChildElement.getName() + ">"
							// + attributeChildElement.getTextTrim());
							if ("id".equals(attributeChildElement.getName())) {
								newTaskAttribute.setAttributeID(Integer
										.parseInt(attributeChildElement
												.getTextTrim()));

							}
							if ("key".equals(attributeChildElement.getName())) {
								newTaskAttribute.setKey(attributeChildElement
										.getTextTrim());

							}
							if ("value".equals(attributeChildElement.getName())) {
								newTaskAttribute.setValue(attributeChildElement.getText());

							}							
						}// end 遍历<attribute>的子元素
						inTaskAttributeList.add(newTaskAttribute);
					}// end if ("attribue"
				}// end 遍历<task>的子元素
				inTaskList.add(newTask);
			} // end if ("task"

			if ("link".equals(taskflowChildElement.getName())) {
				// 如果是link元素
				Link newLink = new Link();
				// newLink.setTaskflow(newTaskflow.getTaskflow());

				// 遍历link的子元素
				for (Iterator linkChildIterator = taskflowChildElement
						.elementIterator(); linkChildIterator.hasNext();) {
					Element linkChildElement = (Element) linkChildIterator
							.next();
					// System.out.println(" <" + linkChildElement.getName() +
					// ">"
					// + linkChildElement.getTextTrim());

					if ("id".equals(linkChildElement.getName())) {
						newLink.setLinkID(Integer.parseInt(linkChildElement
								.getTextTrim()));

					}
					if ("from".equals(linkChildElement.getName())) {
						newLink.setFromTaskID(Integer.parseInt(linkChildElement
								.getTextTrim()));

					}
					if ("to".equals(linkChildElement.getName())) {
						newLink.setToTaskID(Integer.parseInt(linkChildElement
								.getTextTrim()));

					}
				}// end 遍历link的子元素
				inLinkList.add(newLink);
			}// end if ("link"
			if ("note".equals(taskflowChildElement.getName())) {
				// 如果是link元素
				Note newNote = new Note();
				newNote.setTaskflowID(newTaskflow.getTaskflowID());
			//遍历note的子元素
				for (Iterator noteChildIterator = taskflowChildElement
						.elementIterator(); noteChildIterator.hasNext();) {
					Element noteChildElement = (Element) noteChildIterator
					.next();
					// System.out.println(" <" + linkChildElement.getName() +
					// ">"
					// + linkChildElement.getTextTrim());
					
					if ("id".equals(noteChildElement.getName())) {
						newNote.setNoteID(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
					if ("value".equals(noteChildElement.getName())) {
						newNote.setValue(noteChildElement
								.getTextTrim());						
					}
					if ("x".equals(noteChildElement.getName())) {
						newNote.setXPos(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
					if ("y".equals(noteChildElement.getName())) {
						newNote.setYPos(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
					if ("width".equals(noteChildElement.getName())) {
						newNote.setWidth(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
					if ("height".equals(noteChildElement.getName())) {
						newNote.setHeight(Integer.parseInt(noteChildElement
								.getTextTrim()));						
					}
				}// end 遍历note的子元素
				inNoteList.add(newNote);
			}// end if ("note"

		}// 遍历根节点<taskflow>的所有子节点
		inTaskflow = newTaskflow;
		Map map = new HashMap();
		map.put("inTaskflow", inTaskflow);
		map.put("inTaskList", inTaskList);
		map.put("inNoteList", inNoteList);
		map.put("inLinkList", inLinkList);
		map.put("inTaskAttributeList", inTaskAttributeList);
		return map;
	}
	
	public void saveAll(String path) throws Exception {
		String flowPath = path + "/taskflow";
		String commonPath = path + "/common";
		File file = new File(flowPath);
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(commonPath);
		if(!file.exists()){
			file.mkdirs();
		}
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		List<Taskflow> list = flowMetaData.queryAllTaskflow();
		for(Taskflow taskflow : list){
			saveTaskflowInfo(taskflow.getTaskflowID(), flowPath);
		}
		this.saveCategorys(commonPath);
		this.saveStepTypes(commonPath);
		this.saveSysConfigs(commonPath,null);
		this.saveTaskflowGroups(commonPath);
		this.saveTaskTypes(commonPath);
	}
	
	
	public void saveAllData(String path) throws Exception {
		String flowPath = path + "/taskflow";
		String commonPath = path + "/common";
		File file = new File(flowPath);
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(commonPath);
		if(!file.exists()){
			file.mkdirs();
		}
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		
		//所有流程
		List<Taskflow> list = flowMetaData.queryAllTaskflow();
		for(Taskflow taskflow : list){
			saveTaskflowInfo(taskflow.getTaskflowID(), flowPath);
		}
		
		this.saveSysConfigs(commonPath,null);
		this.saveTaskflowGroups(commonPath);
		this.saveRights(commonPath);
		this.saveRoleRights(commonPath);
		this.saveRoles(commonPath);
		this.saveTaskflowUsers(commonPath);
		this.saveUserRoles(commonPath);
		this.saveUsers(commonPath);
	}
	
	
	public void exportAllData(String path) throws Exception {
		String flowPath = path + "/taskflow";
		String commonPath = path + "/common";
		File file = new File(flowPath);
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(commonPath);
		if(!file.exists()){
			file.mkdirs();
		}
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		
		//所有流程
		List<Taskflow> list = flowMetaData.queryAllTaskflow();
		for(Taskflow taskflow : list){
			saveTaskflowInfo(taskflow.getTaskflowID(), flowPath);
		}
		//导出所有FLOW的系统参数
		this.saveFlowSysConfigs(commonPath);
		
		//导出流程组
		this.saveTaskflowGroups(commonPath);
		
		//导出流程组
		this.saveTaskflowUsers(commonPath);

	}
	
	/**
	 * 分目录导出taskflow和common配置信息
	 * @param zipfileName
	 * @param flowIDList
	 * @throws Exception
	 */
	public void zipTaskflow2(String zipfileName, List<Integer> flowIDList) throws Exception{
		
		String path = System.getProperty("java.io.tmpdir") + "/" + System.currentTimeMillis();
		String flowPath = path + "/taskflow";
		String commonPath = path + "/common";
		List<String> xmlFileNameList = new ArrayList<String>();
		StringBuffer outMsgbuf = new StringBuffer();
		
		File file = new File(flowPath);
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(commonPath);
		if(!file.exists()){
			file.mkdirs();
		}
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		
		//导出所有FLOW的系统参数
		this.saveFlowSysConfigs(commonPath);
		
		//导出流程组
		this.saveTaskflowGroups(commonPath);
		
		//导出流程用户关联信息
		this.saveTaskflowUsers(commonPath);
		xmlFileNameList.add("common/" + "sysconfig.xml");
		xmlFileNameList.add("common/" + "taskflowgroup.xml");
		xmlFileNameList.add("common/" + "taskflowuser.xml");
		
		
		//导出所有流程
		List<Taskflow> list = flowMetaData.queryAllTaskflow();
		
		//排序一下
		Object[] objects = list.toArray();
		Arrays.sort(objects);
		list.clear();
		for(Object obj: objects){
			list.add((Taskflow)obj);
		}
		
		
		for(Taskflow taskflow : list){
			if(flowIDList != null && !flowIDList.contains(taskflow.getTaskflowID())){
				continue;
			}
			xmlFileNameList.add( "taskflow/" + taskflow.getTaskflow() + ".xml");
			saveTaskflowInfo(taskflow.getTaskflowID(), flowPath);
		}
		
		//导出包信息：
		if(flowIDList == null){//全量
			outMsgbuf.append("all export\r\n");
		}else{
			outMsgbuf.append("increment export\r\n");
		}
		outMsgbuf.append("total " + (xmlFileNameList.size() +  3)  + " files\r\n");
		outMsgbuf.append("-----------------------\r\n");
		for(String s : xmlFileNameList){
			outMsgbuf.append(s + "\r\n");
		}
		ZipUtil.stringTofile(outMsgbuf.toString(),path + "/readme.txt");
		
		ZipUtil.zip(path, zipfileName);
		ZipUtil.delDir(path);
		new File(path).delete();
		
	}
	
	/**
	 * 打包taskflow
	 * @param zipfileName
	 * @throws Exception
	 */
	public void zipTaskflow(String zipfileName, List<Integer> flowIDList) throws Exception{
		File file = new File(zipfileName);
		if(!file.exists()){
			file.createNewFile();
		}
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		List<Taskflow> list = flowMetaData.queryAllTaskflow();
		List<String> xmlFileNameList = new ArrayList<String>();
		for(Taskflow taskflow : list){
			if(flowIDList != null && !flowIDList.contains(taskflow.getTaskflowID())){
				continue;
			}
			Document document = DocumentHelper.createDocument();
			Element taskflowElement = document.addElement("taskflow");

			taskflowElement.addElement("id").addText(
					taskflow.getTaskflowID() + "");
			taskflowElement.addElement("name").addText(taskflow.getTaskflow());
			taskflowElement.addElement("stepType").addText(
					taskflow.getStepType());
			taskflowElement.addElement("step").addText(taskflow.getStep() + "");
			taskflowElement.addElement("statTime").addText(
					(taskflow.getStatTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getStatTime()));
			taskflowElement.addElement("status").addText(
					taskflow.getStatus() + "");
			taskflowElement.addElement("suspend").addText(
					taskflow.getSuspend() + "");
			taskflowElement.addElement("description").addText(
					taskflow.getDescription());
			taskflowElement.addElement("memo").addText(
					taskflow.getMemo());
			taskflowElement.addElement("redoFlag").addText(
					taskflow.getRedoFlag() + "");
			taskflowElement.addElement("sceneStatTime").addText(
					(taskflow.getSceneStatTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getSceneStatTime()));
			taskflowElement.addElement("redoStartTime").addText(
					(taskflow.getRedoStartTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRedoStartTime()));
			taskflowElement.addElement("redoEndTime").addText(
					(taskflow.getRedoEndTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRedoEndTime()));
			taskflowElement.addElement("fileLogLevel").addText(
					taskflow.getFileLogLevel());
			taskflowElement.addElement("dbLogLevel").addText(
					taskflow.getDbLogLevel());
			taskflowElement.addElement("threadnum").addText(
					taskflow.getThreadnum() + "");
			taskflowElement.addElement("runStartTime").addText(
					(taskflow.getRunStartTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRunStartTime()));
			taskflowElement.addElement("runEndTime").addText(
					(taskflow.getRunEndTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRunEndTime()));
			taskflowElement.addElement("groupID").addText(
					taskflow.getGroupID() + "");
			List<Task> taskList = flowMetaData.queryTaskList(taskflow
					.getTaskflowID());

			Element taskElement = null;
			if (taskList != null) {
				for (Iterator iter = taskList.iterator(); iter.hasNext();) {
					Task task = (Task) iter.next();
					taskElement = taskflowElement.addElement("task");
					taskElement.addElement("id").addText(task.getTaskID() + "");
					taskElement.addElement("name").addText(task.getTask());
					taskElement.addElement("taskType").addText(
							task.getTaskType());
					taskElement.addElement("status").addText(
							task.getStatus() + "");
					taskElement.addElement("plantime").addText(
							task.getPlantime() + "");
					taskElement.addElement("isRoot").addText(
							task.getIsRoot() + "");
					taskElement.addElement("suspend").addText(
							task.getSuspend() + "");
					taskElement.addElement("description").addText(
							task.getDescription());
					taskElement.addElement("memo").addText(
							task.getMemo());
					taskElement.addElement("alertID").addText(
							task.getAlertID() + "");
					taskElement.addElement("performanceID").addText(
							task.getPerformanceID() + "");
					taskElement.addElement("xPos").addText(task.getXPos() + "");
					taskElement.addElement("yPos").addText(task.getYPos() + "");
					taskElement.addElement("runStartTime").addText(
							(task.getRunStartTime() == null) ? "" : TimeUtils
									.toChar(task.getRunStartTime()));
					taskElement.addElement("runEndTime").addText(
							(task.getRunEndTime() == null) ? "" : TimeUtils
									.toChar(task.getRunEndTime()));

					List<TaskAttribute> aList = flowMetaData
							.queryTaskAttributeList(task.getTaskID());
					Element attributeElement = null;
					if (aList != null) {
						for (Iterator iterator = aList.iterator(); iterator
								.hasNext();) {
							TaskAttribute attr = (TaskAttribute) iterator
									.next();
							attributeElement = taskElement
									.addElement("attribute");

							attributeElement.addElement("id").addText(
									attr.getAttributeID() + "");
							attributeElement.addElement("key").addText(
									attr.getKey());
							attributeElement.addElement("value").addText(
									attr.getValue());
						}
					}

				}
			}
			List<Link> linkList = flowMetaData.queryLinkList(taskflow
					.getTaskflowID());
			if (linkList != null) {
				Element linkElement = null;
				for (Iterator iter = linkList.iterator(); iter.hasNext();) {
					Link element = (Link) iter.next();
					linkElement = taskflowElement.addElement("link");
					linkElement.addElement("id").addText(
							element.getLinkID() + "");
					linkElement.addElement("from").addText(
							element.getFromTaskID() + "");
					linkElement.addElement("to").addText(
							element.getToTaskID() + "");
				}
			}
			List<Note> noteList = flowMetaData.queryNoteList(taskflow.getTaskflowID());
			if (noteList != null) {
				Element noteElement = null;
				for (Iterator iter = noteList.iterator(); iter.hasNext();) {
					Note element = (Note) iter.next();
					noteElement = taskflowElement.addElement("note");
					noteElement.addElement("id").addText(
							element.getNoteID() + "");
					noteElement.addElement("value").addText(
							element.getValue() + "");
					noteElement.addElement("x").addText(
							element.getXPos() + "");
					noteElement.addElement("y").addText(
							element.getYPos() + "");
					noteElement.addElement("width").addText(
							element.getWidth() + "");
					noteElement.addElement("height").addText(
							element.getHeight() + "");
				}
			}

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");
			format.setTrimText(false);
			// lets write to a file
			StringWriter stringWriter = new StringWriter();
			XMLWriter writer = new XMLWriter(stringWriter, format);
			writer.write(document);
			writer.close();
			stringWriter.close();
			xmlFileNameList.add(taskflow.getTaskflow() + ".xml");
			ZipEntry entry = new ZipEntry(taskflow.getTaskflow() + ".xml");
			zipOut.putNextEntry(entry);
			DataOutputStream dataOs = new DataOutputStream(zipOut);
			dataOs.write(stringWriter.getBuffer().toString().getBytes());
			dataOs.flush();
		}
		//包信息
		ZipEntry entry = new ZipEntry("readme.txt");
		zipOut.putNextEntry(entry);
		DataOutputStream dataOs = new DataOutputStream(zipOut);
		if(flowIDList == null){//全量
			dataOs.write("all export".getBytes());
		}else{
			dataOs.write("increment export".getBytes());
		}
		dataOs.write(new byte[]{13, 10});
		dataOs.write(("total " + xmlFileNameList.size() + " files").getBytes());
		dataOs.write(new byte[]{13, 10});
		dataOs.write(("-----------------------").getBytes());
		dataOs.write(new byte[]{13, 10});
		for(String s : xmlFileNameList){
			dataOs.write(s.getBytes());
			dataOs.write(new byte[]{13, 10});
		}
		dataOs.flush();
		zipOut.flush();
		zipOut.close();
	}

	public void saveTaskflowInfo(Integer taskflowID, String path)
	throws Exception {
		FlowMetaData flowMetaData = FlowMetaData.getInstance();

		Taskflow taskflow = flowMetaData.queryTaskflow(taskflowID);
		if (taskflow != null) {
			Document document = DocumentHelper.createDocument();
			Element taskflowElement = document.addElement("taskflow");

			taskflowElement.addElement("id").addText(
					taskflow.getTaskflowID() + "");
			taskflowElement.addElement("name").addText(taskflow.getTaskflow());
			taskflowElement.addElement("stepType").addText(
					taskflow.getStepType());
			taskflowElement.addElement("step").addText(taskflow.getStep() + "");
			taskflowElement.addElement("statTime").addText(
					(taskflow.getStatTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getStatTime()));
			taskflowElement.addElement("status").addText(
					taskflow.getStatus() + "");
			taskflowElement.addElement("suspend").addText(
					taskflow.getSuspend() + "");
			taskflowElement.addElement("description").addText(
					taskflow.getDescription());
			taskflowElement.addElement("memo").addText(
					taskflow.getMemo());
			taskflowElement.addElement("redoFlag").addText(
					taskflow.getRedoFlag() + "");
			taskflowElement.addElement("sceneStatTime").addText(
					(taskflow.getSceneStatTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getSceneStatTime()));
			taskflowElement.addElement("redoStartTime").addText(
					(taskflow.getRedoStartTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRedoStartTime()));
			taskflowElement.addElement("redoEndTime").addText(
					(taskflow.getRedoEndTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRedoEndTime()));
			taskflowElement.addElement("fileLogLevel").addText(
					taskflow.getFileLogLevel());
			taskflowElement.addElement("dbLogLevel").addText(
					taskflow.getDbLogLevel());
			taskflowElement.addElement("threadnum").addText(
					taskflow.getThreadnum() + "");
			taskflowElement.addElement("runStartTime").addText(
					(taskflow.getRunStartTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRunStartTime()));
			taskflowElement.addElement("runEndTime").addText(
					(taskflow.getRunEndTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRunEndTime()));
			taskflowElement.addElement("groupID").addText(
					taskflow.getGroupID() + "");
			List<Task> taskList = flowMetaData.queryTaskList(taskflow
					.getTaskflowID());

			Element taskElement = null;
			if (taskList != null) {
				for (Iterator iter = taskList.iterator(); iter.hasNext();) {
					Task task = (Task) iter.next();
					taskElement = taskflowElement.addElement("task");
					taskElement.addElement("id").addText(task.getTaskID() + "");
					taskElement.addElement("name").addText(task.getTask());
					taskElement.addElement("taskType").addText(
							task.getTaskType());
					taskElement.addElement("status").addText(
							task.getStatus() + "");
					taskElement.addElement("plantime").addText(
							task.getPlantime() + "");
					taskElement.addElement("isRoot").addText(
							task.getIsRoot() + "");
					taskElement.addElement("suspend").addText(
							task.getSuspend() + "");
					taskElement.addElement("description").addText(
							task.getDescription());
					taskElement.addElement("memo").addText(
							task.getMemo());
					taskElement.addElement("alertID").addText(
							task.getAlertID() + "");
					taskElement.addElement("performanceID").addText(
							task.getPerformanceID() + "");
					taskElement.addElement("xPos").addText(task.getXPos() + "");
					taskElement.addElement("yPos").addText(task.getYPos() + "");
					taskElement.addElement("runStartTime").addText(
							(task.getRunStartTime() == null) ? "" : TimeUtils
									.toChar(task.getRunStartTime()));
					taskElement.addElement("runEndTime").addText(
							(task.getRunEndTime() == null) ? "" : TimeUtils
									.toChar(task.getRunEndTime()));

					List<TaskAttribute> aList = flowMetaData
					.queryTaskAttributeList(task.getTaskID());
					Element attributeElement = null;
					if (aList != null) {
						for (Iterator iterator = aList.iterator(); iterator
						.hasNext();) {
							TaskAttribute attr = (TaskAttribute) iterator
							.next();
							attributeElement = taskElement
							.addElement("attribute");

							attributeElement.addElement("id").addText(
									attr.getAttributeID() + "");
							attributeElement.addElement("key").addText(
									attr.getKey());
							attributeElement.addElement("value").addText(
									attr.getValue());
						}
					}

				}
			}
			List<Link> linkList = flowMetaData.queryLinkList(taskflow
					.getTaskflowID());
			if (linkList != null) {
				Element linkElement = null;
				for (Iterator iter = linkList.iterator(); iter.hasNext();) {
					Link element = (Link) iter.next();
					linkElement = taskflowElement.addElement("link");
					linkElement.addElement("id").addText(
							element.getLinkID() + "");
					linkElement.addElement("from").addText(
							element.getFromTaskID() + "");
					linkElement.addElement("to").addText(
							element.getToTaskID() + "");
				}
			}

			List<Note> noteList = flowMetaData.queryNoteList(taskflow.getTaskflowID());
			if (noteList != null) {
				Element noteElement = null;
				for (Iterator iter = noteList.iterator(); iter.hasNext();) {
					Note element = (Note) iter.next();
					noteElement = taskflowElement.addElement("note");
					noteElement.addElement("id").addText(
							element.getNoteID() + "");
					noteElement.addElement("value").addText(
							element.getValue() + "");
					noteElement.addElement("x").addText(
							element.getXPos() + "");
					noteElement.addElement("y").addText(
							element.getYPos() + "");
					noteElement.addElement("width").addText(
							element.getWidth() + "");
					noteElement.addElement("height").addText(
							element.getHeight() + "");
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");
			format.setTrimText(false);
			// lets write to a file
			FileWriter fileWriter = new FileWriter(path + "/"
					+ taskflow.getTaskflow() + ".xml");
			XMLWriter writer = new XMLWriter(fileWriter, format);

			writer.write(document);
			writer.close();
			fileWriter.close();
		}

	}

	public void delete(Link link) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(Task task) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(Note note) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(TaskAttribute taskAttribute) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(TaskType taskType) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(StepType stepType) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(SysConfig sysConfig) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(Taskflow taskflow) throws Exception {
		// TODO Auto-generated method stub

	}

	public void insert(Link link) throws Exception {
		// TODO Auto-generated method stub

	}

	public void insert(Task task) throws Exception {
		// TODO Auto-generated method stub

	}

	public void insert(Note note) throws Exception {
		// TODO Auto-generated method stub

	}

	public void insert(TaskAttribute taskAttribute) throws Exception {
		// TODO Auto-generated method stub

	}

	public void insert(Taskflow taskflow) throws Exception {
		// TODO Auto-generated method stub

	}

	public void insert(TaskType taskType) throws Exception {
		// TODO Auto-generated method stub

	}

	public void insert(StepType stepType) throws Exception {
		// TODO Auto-generated method stub

	}

	public void insert(SysConfig sysConfig) throws Exception {
		// TODO Auto-generated method stub

	}

	public void update(Link link) throws Exception {
		// TODO Auto-generated method stub

	}

	public void update(Task task) throws Exception {
		saveTaskflowInfo(task.getTaskflowID());

	}

	public void update(Note note) throws Exception {
		//	
	}

	public void update(TaskAttribute taskAttribute) throws Exception {
		//

	}

	public void update(TaskType taskType) throws Exception {

	}

	public void update(StepType stepType) throws Exception {
		// TODO Auto-generated method stub

	}

	public void update(SysConfig sysConfig) throws Exception {
		// TODO Auto-generated method stub

	}
	
	@SuppressWarnings("unchecked")
	public void updateHistoryStatus(Long historyId, String status, String statTime, String endStatTime, String nextStatTime, String fileName) throws Exception {
		
	}
	
	@SuppressWarnings("unchecked")
	public Long saveHistory(String flowIdStr, String taskIdStr, String flowName, String status, String statTime, String endStatTime, String nextStatTime)
			throws Exception {
		return null;
	}

	public void update(Taskflow taskflow) throws Exception {
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		String tasfflowFilePath = this.repositoryPath + "/taskflow/"+taskflow.getTaskflow()+".xml";
		if (taskflow != null) {
			Document document = DocumentHelper.createDocument();
			Element taskflowElement = document.addElement("taskflow");

			taskflowElement.addElement("id").addText(
					taskflow.getTaskflowID() + "");
			taskflowElement.addElement("name").addText(taskflow.getTaskflow());
			taskflowElement.addElement("stepType").addText(
					taskflow.getStepType());
			taskflowElement.addElement("step").addText(taskflow.getStep() + "");
			taskflowElement.addElement("statTime").addText(
					(taskflow.getStatTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getStatTime()));
			taskflowElement.addElement("status").addText(
					taskflow.getStatus() + "");
			taskflowElement.addElement("suspend").addText(
					taskflow.getSuspend() + "");
			taskflowElement.addElement("description").addText(
					taskflow.getDescription());
			taskflowElement.addElement("memo").addText(
					taskflow.getMemo());
			taskflowElement.addElement("redoFlag").addText(
					taskflow.getRedoFlag() + "");
			taskflowElement.addElement("sceneStatTime").addText(
					(taskflow.getSceneStatTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getSceneStatTime()));
			taskflowElement.addElement("redoStartTime").addText(
					(taskflow.getRedoStartTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRedoStartTime()));
			taskflowElement.addElement("redoEndTime").addText(
					(taskflow.getRedoEndTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRedoEndTime()));
			taskflowElement.addElement("fileLogLevel").addText(
					taskflow.getFileLogLevel());
			taskflowElement.addElement("dbLogLevel").addText(
					taskflow.getDbLogLevel());
			taskflowElement.addElement("threadnum").addText(
					taskflow.getThreadnum() + "");
			taskflowElement.addElement("runStartTime").addText(
					(taskflow.getRunStartTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRunStartTime()));
			taskflowElement.addElement("runEndTime").addText(
					(taskflow.getRunEndTime() == null) ? "" : TimeUtils
							.toChar(taskflow.getRunEndTime()));
			taskflowElement.addElement("groupID").addText(
					taskflow.getGroupID() + "");
			List<Task> taskList = flowMetaData.queryTaskList(taskflow
					.getTaskflowID());

			Element taskElement = null;
			if (taskList != null) {
				for (Iterator iter = taskList.iterator(); iter.hasNext();) {
					Task task = (Task) iter.next();
					taskElement = taskflowElement.addElement("task");
					taskElement.addElement("id").addText(task.getTaskID() + "");
					taskElement.addElement("name").addText(task.getTask());
					taskElement.addElement("taskType").addText(
							task.getTaskType());
					taskElement.addElement("status").addText(
							task.getStatus() + "");
					taskElement.addElement("plantime").addText(
							task.getPlantime() + "");
					taskElement.addElement("isRoot").addText(
							task.getIsRoot() + "");
					taskElement.addElement("suspend").addText(
							task.getSuspend() + "");
					taskElement.addElement("description").addText(
							task.getDescription());
					taskElement.addElement("memo").addText(
							task.getMemo());
					taskElement.addElement("alertID").addText(
							task.getAlertID() + "");
					taskElement.addElement("performanceID").addText(
							task.getPerformanceID() + "");
					taskElement.addElement("xPos").addText(task.getXPos() + "");
					taskElement.addElement("yPos").addText(task.getYPos() + "");
					taskElement.addElement("runStartTime").addText(
							(task.getRunStartTime() == null) ? "" : TimeUtils
									.toChar(task.getRunStartTime()));
					taskElement.addElement("runEndTime").addText(
							(task.getRunEndTime() == null) ? "" : TimeUtils
									.toChar(task.getRunEndTime()));

					List<TaskAttribute> aList = flowMetaData
							.queryTaskAttributeList(task.getTaskID());
					Element attributeElement = null;
					if (aList != null) {
						for (Iterator iterator = aList.iterator(); iterator
								.hasNext();) {
							TaskAttribute attr = (TaskAttribute) iterator
									.next();
							attributeElement = taskElement
									.addElement("attribute");

							attributeElement.addElement("id").addText(
									attr.getAttributeID() + "");
							attributeElement.addElement("key").addText(
									attr.getKey());
							attributeElement.addElement("value").addText(
									attr.getValue());
						}
					}

				}
			}
			List<Link> linkList = flowMetaData.queryLinkList(taskflow
					.getTaskflowID());
			if (linkList != null) {
				Element linkElement = null;
				for (Iterator iter = linkList.iterator(); iter.hasNext();) {
					Link element = (Link) iter.next();
					linkElement = taskflowElement.addElement("link");
					linkElement.addElement("id").addText(
							element.getLinkID() + "");
					linkElement.addElement("from").addText(
							element.getFromTaskID() + "");
					linkElement.addElement("to").addText(
							element.getToTaskID() + "");
				}
			}

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");
			// lets write to a file
			FileWriter fileWriter = new FileWriter(tasfflowFilePath);
			XMLWriter writer = new XMLWriter(fileWriter, format);

			writer.write(document);
			writer.close();
			fileWriter.close();
		}
	}

	public void loadNotes() throws Exception {
		// TODO Auto-generated method stub
	}

	public void saveStepTypes() throws Exception {
		this.saveStepTypes(this.repositoryPath
				+ "/common");
	}
	
	public void saveStepTypes(String path) throws Exception {
//		FlowMetaData flowMetaData = FlowMetaData.getInstance();
//
//		Document document = DocumentHelper.createDocument();
//		Element steptypesElement = document.addElement("steptypes");
//
//		List<StepType> stepTypeList = flowMetaData.queryStepTypeList();
//		Element steptypeElement = null;
//		for (Iterator iter = stepTypeList.iterator(); iter.hasNext();) {
//			StepType stepType = (StepType) iter.next();
//			steptypeElement = steptypesElement.addElement("steptype");
//			steptypeElement.addElement("type").addText(stepType.getStepType());
//			steptypeElement.addElement("name").addText(stepType.getStepName());
//			steptypeElement.addElement("flag").addText(stepType.getFlag() + "");
//			steptypeElement.addElement("order").addText(
//					stepType.getOrder() + "");
//
//		}
//
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		format.setEncoding("GBK");
//		// lets write to a file
//		XMLWriter writer = new XMLWriter(new FileWriter(path + "/steptype.xml"), format);
//
//		writer.write(document);
//		writer.close();

	}

	public void saveCategorys() throws Exception {
		this.saveCategorys(this.repositoryPath
				+ "/common");
	}
	
	public void saveCategorys(String path) throws Exception {
//		FlowMetaData flowMetaData = FlowMetaData.getInstance();
//
//		Document document = DocumentHelper.createDocument();
//		Element categorysElement = document.addElement("categorys");
//
//		List<Category> categoryList = flowMetaData.queryCategoryList();
//		Element categoryElement = null;
//		for (Iterator iter = categoryList.iterator(); iter.hasNext();) {
//			Category category = (Category) iter.next();
//			categoryElement = categorysElement.addElement("category");
//			categoryElement.addElement("id").addText(category.getID() + "");
//			categoryElement.addElement("name").addText(category.getName());
//			categoryElement.addElement("order").addText(
//					category.getOrder() + "");
//
//		}
//
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		format.setEncoding("GBK");
//		// lets write to a file
//		XMLWriter writer = new XMLWriter(new FileWriter(path + "/category.xml"), format);
//
//		writer.write(document);
//		writer.close();

	}
	
	public void saveSysConfigs() throws Exception {
		saveSysConfigs(this.repositoryPath+ "/common",null);
	}
	
	public void saveFlowSysConfigs(String path) throws Exception {
		saveSysConfigs(path,"FLOW");
		
	}
	
	public void saveFlowSysConfigs() throws Exception {
		saveFlowSysConfigs(this.repositoryPath);
		
	}
	
	public void saveSysConfigs(String path, String type) throws Exception {
		
		FlowMetaData flowMetaData = FlowMetaData.getInstance();

		Document document = DocumentHelper.createDocument();
		Element sysConfigElement = document.addElement("sysconfigs");

		List<SysConfig> sysConfigList = flowMetaData.querySysConfigList();
		Element steptypeElement = null;
		for (Iterator iter = sysConfigList.iterator(); iter.hasNext();) {
			SysConfig sysConfig = (SysConfig) iter.next();
			if (type != null && !sysConfig.getType().equals(type) && !sysConfig.getStage().equals(type)){
				continue;
			}
			steptypeElement = sysConfigElement.addElement("sysconfig");
			steptypeElement.addElement("id").addText(sysConfig.getID() + "");
			steptypeElement.addElement("configname").addText(
					sysConfig.getConfigName() + "");
			steptypeElement.addElement("configvalue").addText(
					sysConfig.getConfigValue() + "");
			steptypeElement.addElement("configdesc").addText(
					sysConfig.getConfigDesc() + "");
			steptypeElement.addElement("stage").addText(
					sysConfig.getStage() + "");
			steptypeElement.addElement("type")
					.addText(sysConfig.getType() + "");
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(path + "/sysconfig.xml"), format);

		writer.write(document);
		writer.close();
	}

	public void saveTaskTypes() throws Exception {
		this.saveTaskTypes(this.repositoryPath
				+ "/common");
	}
	
	public void saveTaskTypes(String path) throws Exception {
//		FlowMetaData flowMetaData = FlowMetaData.getInstance();
//
//		Document document = DocumentHelper.createDocument();
//		Element tasktypesElement = document.addElement("tasktypes");
//
//		List<TaskType> taskTypeList = flowMetaData.queryTaskTypeList();
//		Element tasktypeElement = null;
//		for (Iterator iter = taskTypeList.iterator(); iter.hasNext();) {
//			TaskType taskType = (TaskType) iter.next();
//			tasktypeElement = tasktypesElement.addElement("tasktype");
//			tasktypeElement.addElement("id").addText(
//					taskType.getTaskTypeID() + "");
//			tasktypeElement.addElement("name").addText(
//					taskType.getTaskType() + "");
//			tasktypeElement.addElement("engine_plugin_type").addText(
//					taskType.getEnginePluginType() + "");
//			tasktypeElement.addElement("engine_plugin").addText(
//					taskType.getEnginePlugin() + "");
//			if (taskType.getEnginePluginJar() != null) {
//				tasktypeElement.addElement("engine_plugin_jar").addText(
//						taskType.getEnginePluginJar() + "");
//			}
//			tasktypeElement.addElement("description").addText(
//					taskType.getDescription() + "");
//			tasktypeElement.addElement("large_icon").addText(
//					taskType.getLargeIcon() + "");
//			tasktypeElement.addElement("small_icon").addText(
//					taskType.getSmallIcon() + "");
//			tasktypeElement.addElement("category_id").addText(
//					taskType.getCategoryID() + "");
//			tasktypeElement.addElement("designer_plugin").addText(
//					taskType.getDesignerPlugin() + "");
//			tasktypeElement.addElement("designer_plugin_jar").addText(
//					taskType.getDesignerPluginJar() + "");
//		}
//
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		format.setEncoding("GBK");
//		// lets write to a file
//		XMLWriter writer = new XMLWriter(new FileWriter(path + "/tasktype.xml"), format);
//
//		writer.write(document);
//		writer.close();

	}

	public void deleteTaskflowInfo(Integer taskflowID) throws Exception {
		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		Taskflow taskflow = flowMetaData.queryTaskflow(taskflowID);
		File file = new File(flowMetaData.getMetadataRepositoryInfo()
				+ "/taskflow/" + taskflow.getTaskflow() + ".xml");

		file.delete();

	}

	public boolean isSameNameTaskflowExist(Integer taskflowID,
			String taskflowName) throws Exception {
		boolean isExist = false;
		File file = new File(FlowMetaData.getInstance()
				.getMetadataRepositoryInfo()
				+ "/taskflow/" + taskflowName + ".xml");
		SAXReader reader = new SAXReader();

		Document document = reader.read(file);

		// 取节点taskflow/id
		String name = document.selectSingleNode("//taskflow/name").getText();

		// 取节点taskflow/id
		Integer id = Integer.parseInt(document
				.selectSingleNode("//taskflow/id").getText());

		if (taskflowName.equals(name) && taskflowID.equals(id)) {
			isExist = false;
		} else {
			isExist = true;
		}

		return isExist;
	}

	public Integer getTaskflowIDbyName(String taskflowName) throws Exception {
		File file = new File(FlowMetaData.getInstance()
				.getMetadataRepositoryInfo()
				+ "/taskflow/" + taskflowName + ".xml");
		if (!file.exists()) {
			return null;
		}
		SAXReader reader = new SAXReader();

		Document document = reader.read(file);

		// 取节点taskflow/id
		String name = document.selectSingleNode("//taskflow/name").getText();

		// 取节点taskflow/id
		Integer id = Integer.parseInt(document
				.selectSingleNode("//taskflow/id").getText());

		if (taskflowName.equals(name)) {
			return id;
		} else {
			return null;
		}

	}

	public void saveTaskflowInfo(Integer taskflowID) throws Exception {
		// TODO Auto-generated method stub
		saveTaskflowInfo(taskflowID, this.repositoryPath + "/taskflow");

	}

	public void insert(Category category) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(Category category) throws Exception {
		// TODO Auto-generated method stub

	}

	public void update(Category category) throws Exception {
		// TODO Auto-generated method stub

	}

	public void delete(TaskflowGroup taskflowGroup) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void insert(TaskflowGroup taskflowGroup) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void loadTaskflowGroups() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = null;
//		如果common目录下的文件存在，则加载它，否则加载jar包中的配置文件
		
		if (XmlDaoImpl.class.getResource("common/taskflowgroup.xml") != null){
			document = reader.read(XmlDaoImpl.class.getResource("common/taskflowgroup.xml"));
			loadTaskflowGroups(document);
		}
		
		if (new File(this.repositoryPath + "/common/taskflowgroup.xml").exists()){
			document = reader.read(new File(this.repositoryPath + "/common/taskflowgroup.xml"));
			loadTaskflowGroups(document);
		} 
		
	}
	
	private void loadTaskflowGroups(Document document) throws Exception {
		
//		 取根节点
		Element taskflowgroupsElement = document.getRootElement();
		
		for (Iterator i = taskflowgroupsElement.elementIterator(); i.hasNext();) {

			Element taskflowgroupNode = (Element) i.next();

			TaskflowGroup taskflowgroup = new TaskflowGroup();

			// 遍历<category>的子元素
			for (Iterator ChildIterator = taskflowgroupNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element taskflowgroupChildElement = (Element) ChildIterator.next();

				if ("id".equals(taskflowgroupChildElement.getName())) {
					taskflowgroup.setGroupID(Integer.parseInt(taskflowgroupChildElement
							.getTextTrim()));
				}
				if ("name".equals(taskflowgroupChildElement.getName())) {
					taskflowgroup.setGroupName(taskflowgroupChildElement.getTextTrim());
				}
				if ("order".equals(taskflowgroupChildElement.getName())) {
					taskflowgroup.setGroupOrder(Integer.parseInt(taskflowgroupChildElement
							.getTextTrim()));
				}
				if ("groupDesc".equals(taskflowgroupChildElement.getName())) {
					taskflowgroup.setGroupDesc(taskflowgroupChildElement
							.getTextTrim());
				}
			}// end
			FlowMetaData.getInstance().taskflowGroupMap.put(taskflowgroup.getGroupID(), taskflowgroup);
		}
	}

	public void saveTaskflowGroups() throws Exception {
		this.saveTaskflowGroups(this.repositoryPath
				+ "/common");
	}
	
	
	public void saveTaskflowGroups(String path) throws Exception {
		FlowMetaData flowMetaData = FlowMetaData.getInstance();

		Document document = DocumentHelper.createDocument();
		Element taskflowGroupsElement = document.addElement("taskflowgroups");

		List<TaskflowGroup> taskflowGroupList = flowMetaData.queryTaskflowGroupList();
		Element taskflowGroupElement = null;
		for (Iterator iter = taskflowGroupList.iterator(); iter.hasNext();) {
			TaskflowGroup taskflowGroup = (TaskflowGroup) iter.next();
			taskflowGroupElement = taskflowGroupsElement.addElement("taskflowgroup");
			taskflowGroupElement.addElement("id").addText(taskflowGroup.getGroupID() + "");
			taskflowGroupElement.addElement("name").addText(taskflowGroup.getGroupName());
			taskflowGroupElement.addElement("order").addText(taskflowGroup.getGroupOrder() + "");
			taskflowGroupElement.addElement("groupDesc").addText(taskflowGroup.getGroupDesc() == null ? "" : taskflowGroup.getGroupDesc());

		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(path + "/taskflowgroup.xml"), format);

		writer.write(document);
		writer.close();
	}

	public void update(TaskflowGroup taskflowGroup) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public Task getTask(Integer taskID) throws Exception {
//		String taskflowName = FlowMetaData.getInstance().queryTaskflow(taskflowID).getTaskflow();
		//找出task所在的taskflow文件
		String taskflowName = FlowMetaData.getInstance().queryTaskflow(FlowMetaData.getInstance().queryTask(taskID).getTaskflowID()).getTaskflow();
		String tasfflowFilePath = this.repositoryPath + "/taskflow/"+taskflowName+".xml";

		SAXReader reader = new SAXReader();

		Document document = reader.read(new File(tasfflowFilePath));

		// 取根节点<taskflow>
		Element taskflowElement = document.getRootElement();

		// System.out.println("<" + taskflowElement.getName() + ">");
		Taskflow newTaskflow = new Taskflow();
		
		Task curTask = null;

		// 遍历根节点<taskflow>的所有子节点
		for (Iterator i = taskflowElement.elementIterator(); i.hasNext();) {
			Element taskflowChildElement = (Element) i.next();
			// System.out.println(" <" + taskflowChildElement.getName() + ">"
			// + taskflowChildElement.getTextTrim());

			if ("id".equals(taskflowChildElement.getName())) {
				newTaskflow.setTaskflowID(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("name".equals(taskflowChildElement.getName())) {
				newTaskflow.setTaskflow(taskflowChildElement.getTextTrim());
				continue;
			}
			// 基本元素处理完，接下来处理<task>复合元素

			if ("task".equals(taskflowChildElement.getName())) {

				// 如果是task子元素
				Task newTask = new Task();
				newTask.setTaskflowID(newTaskflow.getTaskflowID());

				// 遍历<task>元素的子节点
				for (Iterator taskChildIterator = taskflowChildElement
						.elementIterator(); taskChildIterator.hasNext();) {
					Element taskChildElement = (Element) taskChildIterator
							.next();

					// System.out.println(" <" + taskChildElement.getName() +
					// ">"
					// + taskChildElement.getTextTrim());

					if ("id".equals(taskChildElement.getName())) {
						newTask.setTaskID(Integer.parseInt(taskChildElement
								.getTextTrim()));
					}
					if ("name".equals(taskChildElement.getName())) {
						newTask.setTask(taskChildElement.getTextTrim());
					}
					if ("taskType".equals(taskChildElement.getName())) {
						newTask.setTaskType(taskChildElement.getTextTrim());

					}
					if ("status".equals(taskChildElement.getName())) {
						newTask.setStatus(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("plantime".equals(taskChildElement.getName())) {
						newTask.setPlantime(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("isRoot".equals(taskChildElement.getName())) {
						newTask.setIsRoot(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("suspend".equals(taskChildElement.getName())) {
						newTask.setSuspend(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("description".equals(taskChildElement.getName())) {
						newTask.setDescription(taskChildElement.getTextTrim());

					}
					if ("memo".equals(taskChildElement.getName())) {
						newTask.setMemo(taskChildElement.getTextTrim());

					}
					if ("alertID".equals(taskChildElement.getName())) {
						newTask.setAlertID(taskChildElement.getTextTrim());

					}
					if ("performanceID".equals(taskChildElement.getName())) {
						newTask
								.setPerformanceID(taskChildElement
										.getTextTrim());

					}
					if ("xPos".equals(taskChildElement.getName())) {
						newTask.setXPos(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("yPos".equals(taskChildElement.getName())) {
						newTask.setYPos(Integer.parseInt(taskChildElement
								.getTextTrim()));

					}
					if ("runStartTime".equals(taskChildElement.getName())) {
						newTask.setRunStartTime(TimeUtils
								.toDate(taskChildElement.getTextTrim()));

					}
					if ("runEndTime".equals(taskChildElement.getName())) {
						newTask.setRunEndTime(TimeUtils.toDate(taskChildElement
								.getTextTrim()));

					}
				}// end 遍历<task>的子元素

				if(newTask.getTaskID().equals(taskID)){
					curTask = newTask;
				}
			} // end if ("task"

		}// 遍历根节点<taskflow>的所有子节点
		
		return curTask;
	}

	public Taskflow getTaskflow(Integer taskflowID) throws Exception {
		
		String taskflowName = FlowMetaData.getInstance().queryTaskflow(taskflowID).getTaskflow();
		String tasfflowFilePath = this.repositoryPath + "/taskflow/"+taskflowName+".xml";

		SAXReader reader = new SAXReader();
		
		if(!new File(tasfflowFilePath).exists()){
			return null;
		} 
		
		Document document = reader.read(new File(tasfflowFilePath));

		// 取根节点<taskflow>
		Element taskflowElement = document.getRootElement();

		// System.out.println("<" + taskflowElement.getName() + ">");
		Taskflow newTaskflow = new Taskflow();

		// 遍历根节点<taskflow>的所有子节点
		for (Iterator i = taskflowElement.elementIterator(); i.hasNext();) {

			Element taskflowChildElement = (Element) i.next();
			// System.out.println(" <" + taskflowChildElement.getName() + ">"
			// + taskflowChildElement.getTextTrim());

			if ("id".equals(taskflowChildElement.getName())) {
				newTaskflow.setTaskflowID(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("name".equals(taskflowChildElement.getName())) {
				newTaskflow.setTaskflow(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("stepType".equals(taskflowChildElement.getName())) {
				newTaskflow.setStepType(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("step".equals(taskflowChildElement.getName())) {
				newTaskflow.setStep(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("statTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setStatTime(TimeUtils.toDate(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("status".equals(taskflowChildElement.getName())) {
				newTaskflow.setStatus(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("suspend".equals(taskflowChildElement.getName())) {
				newTaskflow.setSuspend(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("description".equals(taskflowChildElement.getName())) {
				newTaskflow.setDescription(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("memo".equals(taskflowChildElement.getName())) {
				newTaskflow.setMemo(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("redoFlag".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoFlag(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("sceneStatTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setSceneStatTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("redoStartTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoStartTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("redoEndTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRedoEndTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("fileLogLevel".equals(taskflowChildElement.getName())) {
				newTaskflow.setFileLogLevel(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("dbLogLevel".equals(taskflowChildElement.getName())) {
				newTaskflow.setDbLogLevel(taskflowChildElement.getTextTrim());
				continue;
			}
			if ("threadnum".equals(taskflowChildElement.getName())) {
				newTaskflow.setThreadnum(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("runStartTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRunStartTime(TimeUtils
						.toDate(taskflowChildElement.getTextTrim()));
				continue;
			}
			if ("runEndTime".equals(taskflowChildElement.getName())) {
				newTaskflow.setRunEndTime(TimeUtils.toDate(taskflowChildElement
						.getTextTrim()));
				continue;
			}
			if ("groupID".equals(taskflowChildElement.getName())) {
				newTaskflow.setGroupID(Integer.parseInt(taskflowChildElement
						.getTextTrim()));
				continue;
			}
		}// 遍历根节点<taskflow>的所有子节点
		
		return newTaskflow;
	}

	public static void main(String[] args) throws Exception{
//		XmlDaoImpl xml = new XmlDaoImpl("./repository");
//		xml.loadTaskflowGroups();
//		for(TaskflowGroup group :FlowMetaData.getInstance().taskflowGroupMap.values()){
//			System.out.println(group.getGroupID() + " " + group.getGroupName() + " " + group.getGroupOrder());
//			group.setGroupID(group.getGroupID() + 123);
//			group.setGroupName(group.getGroupName() + "save");
//			group.setGroupOrder(group.getGroupID() + 123);
//		}
//		xml.saveTaskflowGroups();
		XmlDaoImpl xml = new XmlDaoImpl("./repository");
		xml.loadAllTaskflowInfo();
		//System.out.println(FlowMetaData.getInstance().taskflowMap.size());
//		Task task = xml.getTask(1695703780);
//		System.out.println(task.getTask());
		
		Taskflow taskflow = xml.getTaskflow(-1063483083);
		//System.out.println(taskflow.getTaskflow());
		taskflow.setTaskflow("oh yeah");
		xml.update(taskflow);
		
	}

	public void updateDbLogLevelOfTaskflow(Integer taskflowID, String dbLogLevel) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setDbLogLevel(dbLogLevel);
		this.saveTaskflowInfo(taskflowID);
		
	}

	public void updateFileLogLevelOfTaskflow(Integer taskflowID, String fileLogLevel) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setFileLogLevel(fileLogLevel);
		this.saveTaskflowInfo(taskflowID);
		
	}

	/*public void updateRedoEndTimeOfTaskflow(Integer taskflowID, Date redoEndTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateRedoFlagOfTaskflow(Integer taskflowID, int redoFlag) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateRedoStartTimeOfTaskflow(Integer taskflowID, Date redoStartTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateRunEndTimeOfTaskflow(Integer taskflowID, Date runEndTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateRunStartTimeOfTaskflow(Integer taskflowID, Date runStartTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateSceneStatTimeOfTaskflow(Integer taskflowID, Date sceneStatTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateStatTimeOfTaskflow(Integer taskflowID, Date statTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateThreadnumOfTaskflow(Integer taskflowID, int threadnum) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateSuspendOfTask(Integer taskID, int newSuspend) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateSuspendOfTaskflow(Integer taskflowID, int newSuspend) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateStatusOfTask(Integer taskID, int newStatus) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateStatusOfTaskflow(Integer taskflowID, int newStatus) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateTaskflowTime(Integer taskflowID, Date newTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateRunEndTimeOfTask(Integer taskID, Date runEndTime) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateRunStartTimeOfTask(Integer taskID, Date runStartTime) throws Exception {
		// TODO Auto-generated method stub
		
	}*/
	

	@SuppressWarnings("unchecked")
	public void updateRedoEndTimeOfTaskflow(Integer taskflowID, Date redoEndTime) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setRedoEndTime(redoEndTime);
		this.saveTaskflowInfo(taskflowID);
		
		
	}

	@SuppressWarnings("unchecked")
	public void updateRedoFlagOfTaskflow(Integer taskflowID, int redoFlag) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setRedoFlag(redoFlag);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateRedoStartTimeOfTaskflow(Integer taskflowID, Date redoStartTime) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setRedoStartTime(redoStartTime);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateRunEndTimeOfTaskflow(Integer taskflowID, Date runEndTime) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setRunEndTime(runEndTime);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateRunEndTimeOfTask(Integer taskID, Date runEndTime) throws Exception {
		Task task = flowMetaData.taskMap.get(taskID);
		task.setRunEndTime(runEndTime);
		this.saveTaskflowInfo(task.getTaskflowID());
	}

	@SuppressWarnings("unchecked")
	public void updateRunStartTimeOfTaskflow(Integer taskflowID, Date runStartTime) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setRunStartTime(runStartTime);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateRunStartTimeOfTask(Integer taskID, Date runStartTime) throws Exception {
		Task task = flowMetaData.taskMap.get(taskID);
		task.setRunStartTime(runStartTime);
		this.saveTaskflowInfo(task.getTaskflowID());
	}
	@SuppressWarnings("unchecked")
	public void updateSceneStatTimeOfTaskflow(Integer taskflowID, Date sceneStatTime) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setSceneStatTime(sceneStatTime);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateStatTimeOfTaskflow(Integer taskflowID, Date statTime) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setStatTime(statTime);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateThreadnumOfTaskflow(Integer taskflowID, int threadnum) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setThreadnum(threadnum);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateSuspendOfTaskflow(Integer taskflowID, int newSuspend) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setSuspend(newSuspend);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateSuspendOfTask(Integer taskID, int newSuspend) throws Exception {
		Task task = flowMetaData.taskMap.get(taskID);
		task.setSuspend(newSuspend);
		this.saveTaskflowInfo(task.getTaskflowID());
	}

	@SuppressWarnings("unchecked")
	public void updateStatusOfTask(Integer taskID, int newStatus) throws Exception {
		Task task = flowMetaData.taskMap.get(taskID);
		task.setStatus(newStatus);
		this.saveTaskflowInfo(task.getTaskflowID());
	}

	@SuppressWarnings("unchecked")
	public void updateStatusOfTaskflow(Integer taskflowID, int newStatus) throws Exception {
		Taskflow taskflow = flowMetaData.taskflowMap.get(taskflowID);
		taskflow.setStatus(newStatus);
		this.saveTaskflowInfo(taskflowID);
	}

	@SuppressWarnings("unchecked")
	public void updateTaskflowTime(Integer taskflowID, Date newTime) throws Exception {
		// TODO Auto-generated method stub
		
	}
	

	public void updateTaskflowInfo(Integer taskflowID) throws Exception {
		//直接保存就可以
		saveTaskflowInfo(taskflowID);
	}


	public void delete(User user) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void delete(Role role) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void delete(Right right) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void delete(UserRole userRole) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void delete(RoleRight roleRight) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void delete(TaskflowUser taskflowUser) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void insert(User user) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void insert(Role role) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void insert(Right right) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void insert(UserRole userRole) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void insert(RoleRight roleRight) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void insert(TaskflowUser taskflowUser) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void loadRights() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = null;
		
//		如果common目录下的文件存在，则加载它，否则加载jar包中的配置文件
		if (XmlDaoImpl.class.getResource("common/right.xml") != null){
			document = reader.read(XmlDaoImpl.class.getResource("common/right.xml"));
			loadRights(document);
		}
		
		if (new File(this.repositoryPath + "/common/right.xml").exists()){
			document = reader.read(new File(this.repositoryPath + "/common/right.xml"));
			loadRights(document);
			
		} 
	}
	
	private void loadRights(Document document) throws Exception {
//		 取根节点
		Element rightsElement = document.getRootElement();
		
		for (Iterator i = rightsElement.elementIterator(); i.hasNext();) {

			Element rightNode = (Element) i.next();

			Right right = new Right();

			// 遍历<category>的子元素
			for (Iterator ChildIterator = rightNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element rightChildElement = (Element) ChildIterator.next();

				if ("id".equals(rightChildElement.getName())) {
					right.setID(Integer.parseInt(rightChildElement
							.getTextTrim()));
				}
				if ("name".equals(rightChildElement.getName())) {
					right.setName(rightChildElement.getTextTrim());
				}
				if ("desc".equals(rightChildElement.getName())) {
					right.setDescription(rightChildElement
							.getTextTrim());
				}
			}// end
			FlowMetaData.getInstance().rightMap.put(right.getID(), right);
		}
	}

	public void loadRoleRights() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = null;
//		如果common目录下的文件存在，则加载它，否则加载jar包中的配置文件
		if (XmlDaoImpl.class.getResource("common/roleright.xml") != null){
			document = reader.read(XmlDaoImpl.class.getResource("common/roleright.xml"));
			loadRoleRights(document);
		}
		
		if (new File(this.repositoryPath + "/common/roleright.xml").exists()){
			document = reader.read(new File(this.repositoryPath + "/common/roleright.xml"));
			loadRoleRights(document);
		} 
	}
	
	private void loadRoleRights(Document document) throws Exception {
		
//		 取根节点
		Element rolerightsElement = document.getRootElement();
		
		for (Iterator i = rolerightsElement.elementIterator(); i.hasNext();) {

			Element rolerightNode = (Element) i.next();

			RoleRight roleright = new RoleRight();

			// 遍历子元素
			for (Iterator ChildIterator = rolerightNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element rolerightChildElement = (Element) ChildIterator.next();
				
				if ("roleID".equals(rolerightChildElement.getName())) {
					roleright.setRoleID(Integer.parseInt(rolerightChildElement
							.getTextTrim()));
				}
				if ("rightID".equals(rolerightChildElement.getName())) {
					roleright.setRightID(Integer.parseInt(rolerightChildElement.getTextTrim()));
				}
			}// end
			FlowMetaData.getInstance().roleRightMap.put(roleright.getRoleID() + "_" + roleright.getRightID() + "", roleright);
		}
	}

	public void loadRoles() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = null;
		
//		如果common目录下的文件存在，则加载它，否则加载jar包中的配置文件
		if (XmlDaoImpl.class.getResource("common/role.xml") != null){
			document = reader.read(XmlDaoImpl.class.getResource("common/role.xml"));
			loadRoles(document);
		}
		
		if (new File(this.repositoryPath + "/common/role.xml").exists()){
			document = reader.read(new File(this.repositoryPath + "/common/role.xml"));
			loadRoles(document);
		} 
	}
	
	private void loadRoles(Document document) throws Exception {

//		取根节点
		Element rolesElement = document.getRootElement();

		for (Iterator i = rolesElement.elementIterator(); i.hasNext();) {

			Element roleNode = (Element) i.next();

			Role role = new Role();

			// 遍历子元素
			for (Iterator ChildIterator = roleNode.elementIterator(); ChildIterator
			.hasNext();) {
				Element roleChildElement = (Element) ChildIterator.next();

				if ("id".equals(roleChildElement.getName())) {
					role.setID(Integer.parseInt(roleChildElement
							.getTextTrim()));
				}
				if ("name".equals(roleChildElement.getName())) {
					role.setName(roleChildElement.getTextTrim());
				}
				if ("desc".equals(roleChildElement.getName())) {
					role.setDescription(roleChildElement
							.getTextTrim());
				}
				FlowMetaData.getInstance().roleMap.put(role.getID(), role);
			}// end
		}
	}

	public void loadTaskflowUsers() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = null;
		
//		如果common目录下的文件存在，则加载它，否则加载jar包中的配置文件
		if (XmlDaoImpl.class.getResource("common/taskflowuser.xml") != null){
			document = reader.read(XmlDaoImpl.class.getResource("common/taskflowuser.xml"));
			loadTaskflowUsers(document);
		}
		
		if (new File(this.repositoryPath + "/common/taskflowuser.xml").exists()){
			document = reader.read(new File(this.repositoryPath + "/common/taskflowuser.xml"));
			loadTaskflowUsers(document);
		} 
	}
	
	private void loadTaskflowUsers(Document document) throws Exception {
//		取根节点
		Element taskflowusersElement = document.getRootElement();
		
		for (Iterator i = taskflowusersElement.elementIterator(); i.hasNext();) {

			Element taskflowuserNode = (Element) i.next();

			TaskflowUser taskflowuser = new TaskflowUser();

			// 遍历<category>的子元素
			for (Iterator ChildIterator = taskflowuserNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element taskflowuserChildElement = (Element) ChildIterator.next();

				if ("taskflowID".equals(taskflowuserChildElement.getName())) {
					taskflowuser.setTaskflowID(Integer.parseInt(taskflowuserChildElement
							.getTextTrim()));
				}
				if ("userID".equals(taskflowuserChildElement.getName())) {
					taskflowuser.setUserID(taskflowuserChildElement
							.getTextTrim());
				}
			}// end
			FlowMetaData.getInstance().taskflowUserMap.put(taskflowuser.getTaskflowID(), taskflowuser);
		}
	}

	public void loadUserRoles() throws Exception {

		//先加载jar包中的内容
		SAXReader reader = new SAXReader();
		Document document = null;
		
		//如果common目录下的文件存在，则加载它，否则加载jar包中的配置文件
		if (XmlDaoImpl.class.getResource("common/userrole.xml") != null){
			document = reader.read(XmlDaoImpl.class.getResource("common/userrole.xml"));
			loadUserRoles(document);
		}
		
		if (new File(this.repositoryPath + "/common/userrole.xml").exists()){
			document = reader.read(new File(this.repositoryPath + "/common/userrole.xml"));
			loadUserRoles(document);
		} 
	}
	
	private void loadUserRoles(Document document) throws Exception {
//		取根节点
		Element userrolesElement = document.getRootElement();
		
		for (Iterator i = userrolesElement.elementIterator(); i.hasNext();) {

			Element userroleNode = (Element) i.next();

			UserRole userrole = new UserRole();
			
			// 遍历<category>的子元素
			for (Iterator ChildIterator = userroleNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element userroleChildElement = (Element) ChildIterator.next();

				if ("userID".equals(userroleChildElement.getName())) {
					userrole.setUserID(userroleChildElement
							.getTextTrim());
				}
				if ("roleID".equals(userroleChildElement.getName())) {
					userrole.setRoleID(Integer.parseInt(userroleChildElement
							.getTextTrim()));
				}
				FlowMetaData.getInstance().userRoleMap.put(userrole.getUserID() + userrole.getRoleID(), userrole);
			}// end
		}
		
	}

	public void loadUsers() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = null;
		
//		如果common目录下的文件存在，则加载它，否则加载jar包中的配置文件
		if (XmlDaoImpl.class.getResource("common/user.xml") != null){
			document = reader.read(XmlDaoImpl.class.getResource("common/user.xml"));
			loadUsers(document);
		}
		
		if (new File(this.repositoryPath + "/common/user.xml").exists()){
			document = reader.read(new File(this.repositoryPath + "/common/user.xml"));
			loadUsers(document);
		} 
	}
	
	private void loadUsers(Document document) throws Exception {
//		取根节点
		Element usersElement = document.getRootElement();
		
		for (Iterator i = usersElement.elementIterator(); i.hasNext();) {

			Element userNode = (Element) i.next();

			User user = new User();
			
			// 遍历<category>的子元素
			for (Iterator ChildIterator = userNode.elementIterator(); ChildIterator
					.hasNext();) {
				Element userChildElement = (Element) ChildIterator.next();
				/*private String ID;
				
				private String name;
				
				private String password;*/
				if ("id".equals(userChildElement.getName())) {
					user.setID(userChildElement
							.getTextTrim());
				}
				if ("name".equals(userChildElement.getName())) {
					user.setName(userChildElement
							.getTextTrim());
				}
				if ("password".equals(userChildElement.getName())) {
					user.setPassword(userChildElement
							.getTextTrim());
				}
				FlowMetaData.getInstance().userMap.put(user.getID(), user);
			}// end
		}
	}

	public void saveRights() throws Exception {
		saveRights(this.repositoryPath+ "/common/");
	}

	@SuppressWarnings("unchecked")
	public void saveRights(String path) throws Exception {

		Document document = DocumentHelper.createDocument();
		Element rightsElement = document.addElement("rights");

		List<Right> rightList = map2list(FlowMetaData.getInstance().rightMap);
		Element rightElement = null;
		
		for (Iterator iter = rightList.iterator(); iter.hasNext();) {
			Right right = (Right) iter.next();
			rightElement = rightsElement.addElement("right");
			rightElement.addElement("id").addText(right.getID() + "");
			rightElement.addElement("name").addText(right.getName());
			rightElement.addElement("desc").addText(right.getDescription());
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(path + "/right.xml"), format);

		writer.write(document);
		writer.close();
	
		
	}

	public void saveRoleRights() throws Exception {
		saveRoleRights(this.repositoryPath+ "/common/");
	}
	
	@SuppressWarnings("unchecked")
	public void saveRoleRights(String path) throws Exception {

		Document document = DocumentHelper.createDocument();
		Element rightsElement = document.addElement("rolerights");

		List<RoleRight> rolerightList = map2list(FlowMetaData.getInstance().roleRightMap);
		Element rolerightElement = null;
		
		for (Iterator iter = rolerightList.iterator(); iter.hasNext();) {
			RoleRight roleright = (RoleRight) iter.next();
			rolerightElement = rightsElement.addElement("roleright");
			rolerightElement.addElement("roleID").addText(roleright.getRoleID() + "");
			rolerightElement.addElement("rightID").addText(roleright.getRightID() + "");
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(path + "/roleright.xml"), format);

		writer.write(document);
		writer.close();
	
	}
	
	public void saveRoles() throws Exception {
		saveRoles(this.repositoryPath+ "/common/");
	}
	
	@SuppressWarnings("unchecked")
	public void saveRoles(String path) throws Exception {
		
		Document document = DocumentHelper.createDocument();
		Element rolesElement = document.addElement("roles");

		List<Role> roleList = map2list(FlowMetaData.getInstance().roleMap);
		Element roleElement = null;
		
		for (Iterator iter = roleList.iterator(); iter.hasNext();) {
			Role role = (Role) iter.next();
			roleElement = rolesElement.addElement("role");
			roleElement.addElement("id").addText(role.getID() + "");
			roleElement.addElement("name").addText(role.getName() + "");
			roleElement.addElement("desc").addText(role.getDescription() + "");
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(path + "/role.xml"), format);

		writer.write(document);
		writer.close();
		
	}

	public void saveTaskflowUsers() throws Exception {
		saveTaskflowUsers(this.repositoryPath + "/common");
		
	}
	
	@SuppressWarnings("unchecked")
	public void saveTaskflowUsers(String path) throws Exception {
		
		Document document = DocumentHelper.createDocument();
		Element rolesElement = document.addElement("taskflowusers");

		List<TaskflowUser> taskflowuserList = map2list(FlowMetaData.getInstance().taskflowUserMap);
		Element taskflowuserElement = null;
		
		for (Iterator iter = taskflowuserList.iterator(); iter.hasNext();) {
			TaskflowUser taskflowuser = (TaskflowUser) iter.next();
			taskflowuserElement = rolesElement.addElement("taskflowuser");
			taskflowuserElement.addElement("taskflowID").addText(taskflowuser.getTaskflowID() + "");
			taskflowuserElement.addElement("userID").addText(taskflowuser.getUserID());
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(path + "/taskflowuser.xml"), format);

		writer.write(document);
		writer.close();
		
	}
	public void saveUserRoles() throws Exception {
		saveUserRoles(this.repositoryPath + "/common");
		
	}
	
	@SuppressWarnings("unchecked")
	public void saveUserRoles(String path) throws Exception {
		
		Document document = DocumentHelper.createDocument();
		Element rolesElement = document.addElement("userroles");

		List<UserRole> userroleList = map2list(FlowMetaData.getInstance().userRoleMap);
		Element userroleElement = null;
		
		for (Iterator iter = userroleList.iterator(); iter.hasNext();) {
			UserRole userrole = (UserRole) iter.next();
			userroleElement = rolesElement.addElement("userrole");
			userroleElement.addElement("roleID").addText(userrole.getRoleID() + "");
			userroleElement.addElement("userID").addText(userrole.getUserID());
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(path + "/userrole.xml"), format);

		writer.write(document);
		writer.close();
		
	}
	
	public void saveUsers() throws Exception {
		saveUsers(this.repositoryPath + "/common");
		
	}
	
	@SuppressWarnings("unchecked")
	public void saveUsers(String path) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element usersElement = document.addElement("users");

		List<User> userList = map2list(FlowMetaData.getInstance().userMap);
		Element userElement = null;
		
		for (Iterator iter = userList.iterator(); iter.hasNext();) {
			User user = (User) iter.next();
			userElement = usersElement.addElement("user");
			userElement.addElement("id").addText(user.getID());
			userElement.addElement("name").addText(user.getName());
			userElement.addElement("password").addText(user.getPassword());
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(path + "/user.xml"), format);

		writer.write(document);
		writer.close();
		
	}

	public void update(User user) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void update(Role role) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void update(Right right) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void update(UserRole userRole) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void update(RoleRight roleRight) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void update(TaskflowUser taskflowUser) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	public List map2list(Map map){
		Iterator it = map.entrySet().iterator();
		List list = new ArrayList();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			list.add(entry.getValue());
		}
		return list;
	}
	
	
	public String getStringFromInputStream(InputStream in) throws Exception {
		StringBuffer sb = new StringBuffer();
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			sb.append(new String(buf, 0, len));
		}
		System.err.println(sb);
		return sb.toString();
	}
	
	
	
	public boolean toXmlFile(String xml,String fileName){
		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			
			pw.println("<?xml version=\"1.0\" encoding=\"GBK\"?>");
			pw.println(xml);
			pw.flush();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
		finally
		{
			try
			{
				pw.close();
			}
			catch (Exception e)
			{
			}
		}
		
	}
	

	/**
	 * 升级专用!!导入已有流程
	 * @param taskflow
	 * @param inTaskList
	 * @param inNoteList
	 * @param inLinkList
	 * @param inTaskAttributeList
	 * @throws Exception
	 */
	public void importOldTaskflowInfo(Taskflow taskflow, List<Task> inTaskList, List<Note> inNoteList, List<Link> inLinkList, List<TaskAttribute> inTaskAttributeList) throws Exception {
		/*HashMap map = new HashMap();
		map.put("taskflowID", taskflow.getTaskflowID());
		//存储中的task
		List<Task> dbTaskList = SqlMapUtil.executeQueryForList("getTaskOfTaskFlow", map);
		//需要更新的task
		List<Task> updateTaskList = new ArrayList<Task>();
		//需要删除的task
		List<Task> delTaskList = new ArrayList<Task>();
		//需要增加的task
		List<Task> addTaskList = new ArrayList<Task>();
		//更新的task
		boolean inM = false;
		for(Task dbTask : dbTaskList){
			inM = false;
			for(Task memoryTask : inTaskList){
				if(memoryTask.getTaskID().equals(dbTask.getTaskID())){
					updateTaskList.add(memoryTask);
					inM = true;
					break;
				}
			}
			//删除的task
			if(!inM){
				delTaskList.add(dbTask);
			}
		}
		//增加的task
		for(Task memoryTask : inTaskList){
			if(!updateTaskList.contains(memoryTask)){
				addTaskList.add(memoryTask);
			}
		}
		
		//开始update工作了!!
		//删note 存储中的note
		List<Note> noteList = SqlMapUtil.executeQueryForList("getNote", map);
		if(noteList != null){
			for (Note note : noteList) {
				delete(note);
			}
		}
		//删除link 存储中的link
		List<Link> linkList = SqlMapUtil.executeQueryForList("getLinkOfTaskflow", map);
		for (Link link : linkList) {
			delete(link);
		}
		//删除taskAttribute
		List<TaskAttribute> allTaskAttribute = new ArrayList<TaskAttribute>();
		for(Task dbTask : dbTaskList){
			HashMap taskIDMap = new HashMap();
			taskIDMap.put("taskID", dbTask.getTaskID());
			allTaskAttribute.addAll(SqlMapUtil.executeQueryForList("queryTaskAttributeList", taskIDMap));
		}
		for (TaskAttribute taskAttribute : allTaskAttribute) {
			delete(taskAttribute);
		}
		//删除需要在存储器删除的task
		for (Task task : delTaskList) {
			delete(task);
		}
		//更新taskflow
		SqlMapUtil.executeUpdate("updateTaskflowTemplate", taskflow);
		//新增task
		for (Task task : addTaskList) {
			this.insert(task);
		}
		//更新task
		for (Task task : updateTaskList) {
			update(task);
		}
		for(Link link : inLinkList){
			this.insert(link);
		}		
		for (Note note : inNoteList) {
			this.insert(note);	
		}
		for (TaskAttribute taskAttribute : inTaskAttributeList) {
			this.insert(taskAttribute);	
		}*/
	}
	
	/**
	 * 升级专用!!导入新流程
	 * @param taskflow
	 * @param inTaskList
	 * @param inNoteList
	 * @param inLinkList
	 * @param inTaskAttributeList
	 * @throws Exception
	 */
	public void importNewTaskflowInfo(Taskflow taskflow, List<Task> inTaskList, List<Note> inNoteList, List<Link> inLinkList, List<TaskAttribute> inTaskAttributeList) throws Exception {
		insert(taskflow);
		
	/*	//新加
		for (Task task : inTaskList) {
			this.insert(task);
		}
		for(Link link : inLinkList){
			this.insert(link);
		}
		for (Note note : inNoteList) {
			this.insert(note);	
		}
		for (TaskAttribute taskAttribute : inTaskAttributeList) {
			this.insert(taskAttribute);	
		}*/
	}

	public List<Link> getLinksInTaskflow(int taskflowId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Note> getNotesInTaskflow(int taskflowId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TaskAttribute> getTaskAttributesInTaskflow(int taskflowId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Task> getTasksInTaskflow(int taskflowId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshSysconfig() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public int getCountTaskHistoryByTaskflowId(Integer taskflowID) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateStatTimeOfTaskflowNew(Integer taskflowID, String time) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
