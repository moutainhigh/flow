package com.aspire.etl.analyse;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdesinger.FlowDesignerConstants;
import com.aspire.etl.flowdesinger.FlowPanel;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.layout.HorizontalLayout;
import com.aspire.etl.layout.NodeLink;
import com.aspire.etl.layout.TaskNode;
import com.aspire.etl.metadatamanager.bean.DBConnInfo;
import com.aspire.etl.metadatamanager.dao.DBConnInfoDao;
import com.aspire.etl.tool.Utils;
import com.aspire.etl.uic.WcpImagePool;
import com.aspire.etl.uic.WcpMessageBox;

public class ReportAnalyse extends Thread {
	
	private static Logger logger = Logger.getLogger(ReportAnalyse.class);
	
	
	private static List<String> userObjectList;
	
	private boolean isLoadedPackage;
	
	private ReportDAO reportDAO ;

	private List<PackagePO> packageList = new ArrayList<PackagePO>();
	
	//private JGraph graph;
	
	
	public ReportAnalyse() {
		
//			从代理服务器上获取REPORT的数据源信息：
			DBConnInfoDao dbConnInfoDao = null;
			try {
				dbConnInfoDao = new DBConnInfoDao(FlowMetaData.getInstance(), null);
				dbConnInfoDao.init();
				DBConnInfo dbConnInfo = dbConnInfoDao.getDBConnInfo("REPORT");
				reportDAO = new ReportDAO(dbConnInfo);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void main(String[] args){
		
			FlowMetaData flowMetaData = null;
	    	
	    	try {
				FlowMetaData.init("oracle.jdbc.driver.OracleDriver", "spms_rdp", "spms_rdp",
						"jdbc:oracle:thin:@10.1.3.105:1521:ora9i");
				flowMetaData = FlowMetaData.getInstance();
				flowMetaData.loadAllTaskflowInfo();
				
			} catch (MetaDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		ReportAnalyse analyse = new ReportAnalyse();
		//analyse.start();
		analyse.init();
		FuncRelationPO po = analyse.getFuncRelation("spms_moniter_report.fun_a_sp_momt_h", analyse.getFunctionBody("spms_moniter_report.fun_a_sp_momt_h"));
		System.out.println(StringUtil.getString(po.getSourceTableList()));
		System.err.println(StringUtil.getString(po.getTargetTableList()));
		
		
	}
	
	
	public void init(){
		isLoadedPackage = false;
		userObjectList = reportDAO.getUserObjectList();
		getPackageList();
		isLoadedPackage = true;
	}
	
	public void run(){
		init();
	}
	
	
	
	public boolean updateTaskFlow(FlowPanel flowPanel){
		
		//函数与表的关系列表
		List<FuncRelationPO> funcTableRelList = new ArrayList<FuncRelationPO>();
		List<TaskNode> taskNodeList = new ArrayList<TaskNode>();
		List<NodeLink> nodeLinkList = new ArrayList<NodeLink>();
		List<NodeLink> oldNodeLinkList = new ArrayList<NodeLink>();
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		List tableNodeList = new ArrayList();
		try {

			//1.聚合任务的来源表和目标表：
			Object[] cells = flowPanel.getGraph().getGraphLayoutCache().getCells(false, true, false, false);
			for (int i = 0; i < cells.length; i++) {
				DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
				Object obj = flowGraphCell.getUserObject();
				if (obj instanceof Task) {
					Task task = (Task) obj;

					//只取出聚合类型的Task
					if (task.getTaskType().equals(FlowDesignerConstants.TASK_TYPE_COMPRESS)){
						String funcName = FlowMetaData.getInstance().queryTaskAttributeMap(task.getTaskID()).get("Action");
						String funcBody = getFunctionBody(funcName);
						if (funcBody != null){
							FuncRelationPO funcRel = getFuncRelation(funcName, funcBody);
							funcRel.setFuncID(task.getTaskID());
							funcTableRelList.add(funcRel);
							System.out.println(funcName + " : " + StringUtil.getString(funcRel.getSourceTableList()) + " --> " + StringUtil.getString(funcRel.getTargetTableList()));
						}
					}
				}
			}

			//删除来源表和目标表,并重新对聚合节点画link线
			
			//对流程节点进行排序...
			System.out.println("对流程节点进行排序...");

			//查出所有Task的信息
			cells = flowPanel.getGraph().getGraphLayoutCache().getCells(false, true,false, false);
			for (int i = 0; i < cells.length; i++) {
				DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
				Object obj = flowGraphCell.getUserObject();
				if (obj instanceof Task) {
					Task task = (Task) obj;
					
					if (task.getTaskType().equals(FlowDesignerConstants.TASK_TYPE_TABLE)){
						tableNodeList.add(flowGraphCell);
					}
					taskNodeList.add(new TaskNode(""+task.getTaskID(),HorizontalLayout.countWidthByString(task.getTask())));
				}
			}

			//查出所有链接的信息：
			cells = flowPanel.getGraph().getGraphLayoutCache().getCells(false, false, false, true);
			for (int i = 0; i < cells.length; i++) {
				DefaultEdge flowEdge = (DefaultEdge) cells[i];
				Link link = (Link) flowEdge.getUserObject();
				nodeLinkList.add(new NodeLink(""+link.getFromTaskID(),""+link.getToTaskID()));
			}
			
			//删除流程节点和link线，并补上聚合与聚合之间的link线：
			 removeTableLink(tableNodeList,taskNodeList,nodeLinkList);
			
			 //删除任务节点：graph.getModel().remove(cells);
			 flowPanel.getGraph().getModel().remove(tableNodeList.toArray());
			 
			
			//重新布局
			horizontalLayout.setLayout(taskNodeList,nodeLinkList);

			//保存旧的link线
			/*oldNodeLinkList.addAll(nodeLinkList);*/
			
			//得到重新布局之后的列表
			List nodeOrderList = horizontalLayout.getNodeOrderList();
			
			for(int i = 0; i < nodeOrderList.size(); i++){
				
				TaskNode[] taskNodes = (TaskNode[])nodeOrderList.get(i);
				
				for(TaskNode taskNode : taskNodes){
					ArrayList<Task> sourceTaskList = new ArrayList<Task>(); 
					ArrayList<Task> targetTaskList = new ArrayList<Task>(); 
					
					//funcTableRelList 迭代函数与表的关系列表：
					for(FuncRelationPO relation: funcTableRelList){
						if (relation.getFuncID().equals(taskNode.getName())){//找到节点所对应的函数找到关系PO对象
							List<String> sourceTables = relation.getSourceTableList();
							List<String> targetTables = relation.getTargetTableList();
							
//							生成Task并drawTask,生成Link并draw
							
							//生成来源节点
							for(String sourceTable : sourceTables){
								Task task = flowPanel.addTask("Table");
								task.setTask(sourceTable);
								task.setDescription(sourceTable);
								if (!sourceTaskList.contains(task)){
									sourceTaskList.add(task);
									taskNodeList.add(new TaskNode(""+task.getTaskID(),HorizontalLayout.countWidthByString(task.getTask())));
								}
								flowPanel.addLink(queryGraphByTaskID(flowPanel.getGraph(),task.getTaskID()), queryGraphByTaskID(flowPanel.getGraph(),relation.getFuncID()));
								nodeLinkList.add(new NodeLink("" + task.getTaskID(),"" + relation.getFuncID()));
							}
							
							//生成目标节点
							for(String targetTable : targetTables){
								Task task = flowPanel.addTask("Table");
								task.setTask(targetTable);
								task.setDescription(targetTable);
								if (!targetTaskList.contains(task)){
									targetTaskList.add(task);
									taskNodeList.add(new TaskNode(""+task.getTaskID(),HorizontalLayout.countWidthByString(task.getTask())));
								}
								flowPanel.addLink(queryGraphByTaskID(flowPanel.getGraph(),relation.getFuncID()), queryGraphByTaskID(flowPanel.getGraph(),task.getTaskID()));
								nodeLinkList.add(new NodeLink("" + relation.getFuncID(), "" + task.getTaskID()));
							}
							
							break;
						}

					}
				}
				
				
			}
			
			/*//清除旧的连接线，oldNodeLinkList
			oldNodeLinkList*/
			
			//更新已存在的任务节点坐标：
			cells = flowPanel.getGraph().getGraphLayoutCache().getCells(false, true,false, false);
			for (int i = 0; i < cells.length; i++) {
				DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
				Object obj = flowGraphCell.getUserObject();
				if (obj instanceof Task) {
					Task task = (Task) obj;

					//更新内存中的坐标
					TaskNode taskNode = queryTaskNode(taskNodeList,String.valueOf(task.getTaskID()));
					task.setXPos(taskNode.getX());
					task.setYPos(taskNode.getY());

					//更新graph的坐标：
					GraphConstants.setBounds(flowGraphCell.getAttributes(), new Rectangle2D.Double(task.getXPos(),
							task.getYPos(), GraphConstants.getBounds(flowGraphCell.getAttributes()).getWidth() , 
							GraphConstants.getBounds(flowGraphCell.getAttributes()).getHeight()));

				}
			}

			System.out.println("刷新流程图...");
			flowPanel.getGraph().getGraphLayoutCache().reload();
			flowPanel.getGraph().repaint();

			return true;

		} catch (Exception e) {
			return false;
		}
		
		
	}
	
	/**
	 * 删除数据流节点和link线，并补上聚合与聚合之间的link线
	 * @param tableNodeList
	 * @param taskNodeList
	 * @param nodeLinkList
	 */
	private void removeTableLink(List tableNodeList, List<TaskNode> taskNodeList, List<NodeLink> nodeLinkList) {
		
		List<NodeLink> toLinkList = new ArrayList<NodeLink>();
		List<NodeLink> fromLinkList = new ArrayList<NodeLink>();
		
		for(Object obj: tableNodeList){
			Task tableTask = (Task)obj;
			toLinkList = queryLinkListByToName(nodeLinkList,tableTask.getTaskID() + "");
			fromLinkList = queryLinkListByFromName(nodeLinkList,tableTask.getTaskID() + "");
			
			
			for(int i =0; i < toLinkList.size(); i++){
				for(int j =0; j < fromLinkList.size(); j++){
					
					//增加聚合与聚合之间的link线 ，toLink的每个from和fromLink的每个to建立link线
					nodeLinkList.add(new NodeLink(toLinkList.get(i).getFrom(),fromLinkList.get(j).getTo()));
					
					if (i == 0){//第一次时才删除fromLink中的列表
						removeNodeLink(nodeLinkList,fromLinkList.get(j));
					}
				}
				
				//删除toLink
				removeNodeLink(nodeLinkList,toLinkList.get(i));
			}
			
			//删除Table节点
			removeTaskNode(taskNodeList,tableTask.getTaskID() + "");
			
		}
		
	}
	
	
	private List<NodeLink> queryLinkListByToName(List<NodeLink> nodeLinkList, String name){
		List<NodeLink> linkList = new ArrayList<NodeLink>();
		for(NodeLink nodeLink: nodeLinkList){
			if(nodeLink.getTo().equals(name)){
				linkList.add(nodeLink);
			}
		}
		return linkList;
	}
	

	private DefaultPort queryGraphByTaskID(JGraph graph, Integer taskID){
		Object[] cells = graph.getGraphLayoutCache().getCells(false, true, false, false);
		for (int i = 0; i < cells.length; i++) {
			DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
			Object obj = flowGraphCell.getUserObject();
			if (obj instanceof Task) {
				Task task = (Task) obj;
				if (task.getTaskID() == taskID){
					return (DefaultPort)flowGraphCell.getChildAt(0);
				}
			}
		}
		return null;
	}
	
	private List<NodeLink> queryLinkListByFromName(List<NodeLink> nodeLinkList, String name){
		List<NodeLink> linkList = new ArrayList<NodeLink>();
		for(NodeLink nodeLink: nodeLinkList){
			if(nodeLink.getFrom().equals(name)){
				linkList.add(nodeLink);
			}
		}
		return linkList;
	}
	
	/**
	 * 删除任务节点
	 * @param node
	 * @return
	 */
	private void removeTaskNode(List<TaskNode> taskNodeList, String name){
		for(int i =0 ; i < taskNodeList.size(); i++){
			TaskNode taskNode = (TaskNode)taskNodeList.get(i);
			if (taskNode.getName().equals(name)){
				taskNodeList.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * 删除任务节点
	 * @param node
	 * @return
	 */
	private void removeNodeLink(List<NodeLink> nodeLinkList, NodeLink link){
		for(int i =0 ; i < nodeLinkList.size(); i++){
			NodeLink nodeLink = (NodeLink)nodeLinkList.get(i);
			if (nodeLink.getFrom().equals(link.getFrom()) && nodeLink.getTo().equals(nodeLink.getTo())){
				nodeLinkList.remove(i);
				i--;
			}
		}
	}

	/**
	 * 查询TaskNode
	 * @param taskNodeList
	 * @param taskName
	 * @return
	 */
	public TaskNode queryTaskNode(List<TaskNode> taskNodeList, String taskName){
		for(TaskNode taskNode : taskNodeList){
			if (taskNode.getName().equals(taskName)){
				return taskNode;
			}
		}
		return null;
	}
	
	
	
	private DefaultGraphCell drawTask(JGraph graph,Task task) {
		
		try {
//			 Create vertex with the given name
			DefaultGraphCell cell = new DefaultGraphCell(task);
			AttributeMap am = cell.getAttributes();
			// Set bounds
			
			GraphConstants.setBounds(am, new Rectangle2D.Double(task.getXPos(),
					task.getYPos(), HorizontalLayout.countWidthByString(task.getDescription()) , 50 + 20));
			TaskType tt;
			tt = FlowMetaData.getInstance().queryTaskType(task.getTaskType());
			ImageIcon icon = null;
			if (tt.getCategoryID() == 1) {
				// 常用C++插件
				icon = WcpImagePool.getIcon(this.getClass(), tt.getLargeIcon());
			} else {
				// 网络插件和第三方插件
				icon = WcpImagePool.getIconOfPath(tt.getLargeIcon());
			}
			if (icon != null) {
				GraphConstants.setIcon(am, icon);
			}
			// 设置透明
			GraphConstants.setOpaque(am, false);

			GraphConstants.setSizeable(am, true);

			// 让节点图标自动大小，已便于显示长的label。
			//GraphConstants.setAutoSize(am, true);
			// GraphConstants.setResize(am, true);

			// 阻止graph打开缺省的cell编辑框
			GraphConstants.setEditable(am, false);
			// Add a Floating Port

			cell.addPort();
			graph.getGraphLayoutCache().insert(cell);
			return cell;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}

	/**
	 * @author wangcaiping,20080701
	 * @param link
	 * @param source
	 * @param target
	 * @return
	 */
	private DefaultEdge drawLink(JGraph graph, Link link, DefaultPort source,
			DefaultPort target) {
		// Construct Edge with no label
		DefaultEdge edge = new DefaultEdge(link);
		edge.setSource(source);
		edge.setTarget(target);
		GraphConstants.setLineEnd(edge.getAttributes(),
				GraphConstants.ARROW_SIMPLE);
		GraphConstants.setEditable(edge.getAttributes(), false);
		GraphConstants.setConnectable(edge.getAttributes(), false);
		graph.getGraphLayoutCache().insertEdge(edge, source, target);
		return edge;
	}
	
	
	/**
	 * 获取包信息列表
	 */
	 void getPackageList(){
		
		System.out.println("正在加载数据库中的package内容...");
		List<HashMap> list = reportDAO.getPackageBody();
		PackagePO packagePO = null;
		String packageName = "";
		String funcName = "";
		StringBuffer sqlBuff = new StringBuffer();
		StringBuffer funcBuff = new StringBuffer();
		StringBuffer funcTmpBuff = new StringBuffer();
		StringBuffer tmpBuff = new StringBuffer();
		
		int funcStartIndex = 0;
		int funcEndIndex = 0;
		
		boolean isBodyGetting = false;
		
		
		//先将加载包的源代码，然后去除注释再分解函数，因为可能有些函数是被注释掉了
		for(HashMap map : list){
			
			if (!map.get("NAME").equals(packageName)){//读取包的第一行时创建包PO对象
				
				if (isBodyGetting){//如果正在获取函数体的内容，当获取最后一个函数的最后一行时，没有下一个Function所以要处理
					sqlBuff.append("/\r\n\r\n");
					packagePO.setPackagebody(StringUtil.clearSQLComment(sqlBuff.toString()));
					packageList.add(packagePO);
					isBodyGetting = false;
				}
				packagePO = new PackagePO();
				sqlBuff.delete(0, sqlBuff.length());
				packageName = String.valueOf(map.get("NAME"));
				logger.info("正在加载package:" + packageName);
				packagePO.setPackageName(packageName.toLowerCase());
			} 
			isBodyGetting = true;
			sqlBuff.append(String.valueOf(map.get("TEXT")).replace("\n", "") + "\r\n");
		}
		
		//最后还需要处理剩下的内容
		if (isBodyGetting){//如果正在获取函数体的内容，当获取最后一个函数的最后一行时，没有下一个Function所以要处理
			sqlBuff.append("\r\n\r\n");
			packagePO.setPackagebody(StringUtil.clearSQLComment(sqlBuff.toString()));
			packageList.add(packagePO);
			isBodyGetting = false;
		}
		logger.info("结束加载package:" + packageName);
		//分析函数：
		for(PackagePO packPO: packageList){
			sqlBuff.delete(0, sqlBuff.length());
			funcTmpBuff.delete(0, funcTmpBuff.length());
			
			sqlBuff.append(packPO.getPackagebody());
			
			while (sqlBuff.length() > 0) {
				funcBuff.delete(0, funcBuff.length());
				funcTmpBuff.delete(0, funcTmpBuff.length());
				
				funcStartIndex = sqlBuff.indexOf("FUNCTION");
				if (funcStartIndex == -1){
					sqlBuff.delete(0, sqlBuff.length());
					continue;
				}

				funcEndIndex   = sqlBuff.indexOf("FUNCTION",funcStartIndex + 10);
				if (funcEndIndex == -1){
					funcEndIndex = sqlBuff.length();
				}
				
				
				funcBuff.append(sqlBuff.substring(funcStartIndex,funcEndIndex));
				funcTmpBuff.append(funcBuff);
				
				funcTmpBuff.delete(0, funcTmpBuff.indexOf("FUNCTION") + 8);

				tmpBuff.delete(0, tmpBuff.length());
				tmpBuff.append(funcTmpBuff.toString().trim());

				funcTmpBuff.delete(0, funcTmpBuff.length());
				funcTmpBuff.append(tmpBuff);
				//System.out.println(funcTmpBuff);

				if (funcTmpBuff.indexOf("(") > -1){
					funcName = funcTmpBuff.substring(0,funcTmpBuff.indexOf("("));
				}  else if (funcTmpBuff.indexOf(" ") > -1){
					funcName = funcTmpBuff.substring(0,funcTmpBuff.indexOf(" "));
				} else if (funcTmpBuff.indexOf("\t") > -1){
					funcName = funcTmpBuff.substring(0,funcTmpBuff.indexOf("\t"));
				} else if (funcTmpBuff.indexOf("\r") > -1){
					funcName = funcTmpBuff.substring(0,funcTmpBuff.indexOf("\r"));
				} else if (funcTmpBuff.indexOf("\n") > -1){
					funcName = funcTmpBuff.substring(0,funcTmpBuff.indexOf("\n"));
				} else {
					funcName = funcTmpBuff.toString();
				}
				//System.out.println("【" + funcName.trim() + "】" + " : " + funcBuff.toString().trim());
				logger.info("正在分析包【" + packPO.getPackageName() + "】函数：" + funcName.trim()  + " sqlBuff.length: " + sqlBuff.length() + " start:" + funcStartIndex +  "end :" + funcEndIndex);
				
				packPO.putFunction(funcName.trim(),  funcBuff.toString().trim());
				sqlBuff.delete(0, funcEndIndex);
				logger.info("end, sqlBuff.length:" + sqlBuff.length());
			}
		}
		
		System.out.println("package内容加载完毕...");
	}
	
	
	
	/**
	 * 获取方法体
	 * @param funcName   (包名.函数名)
	 * @return
	 */
	public String getFunctionBody(String funcName){
		String funcName2 = funcName.trim();
		if (funcName.indexOf(".") > -1){
			funcName2 = funcName.substring(funcName.indexOf(".") + 1);
		}
		
		for(PackagePO packPO: packageList){
			String sql = packPO.getFunctionBody(funcName2);
			if (sql != null && !sql.equals("null")){
				return sql; 
			}
		}
		
		return null;
	}
	
	public FuncRelationPO getFuncRelation(String funcName, String funcBody){
		FuncRelationPO funcRel = new FuncRelationPO();
		funcRel.setFuncName(funcName);
		List<String> insertSqlList = getInsertSQLList(funcBody);
		
		logger.debug("enter insertSqlList = " + StringUtil.getString(insertSqlList) );
		
		for(String insertSql : insertSqlList){
			String selectSql = insertSql.substring(insertSql.toLowerCase().indexOf("select"));
			funcRel.addSourceTables(getSourceTableList(selectSql));
			funcRel.addTargetTables(getTargetTableList(insertSql));
		}
		
		return funcRel;
	}

	private List<String> getTargetTableList(String insertSql) {

		logger.info("enter getTargetTableList" );

		//	sql = DbUtil.clearSQLComment(sql);

		List<String> tablesList = new ArrayList<String>();
		logger.debug("insertSql = " + insertSql);


		insertSql = insertSql.substring(insertSql.toLowerCase().indexOf("insert"));//从第一个insert语句开始解释
		String str2 = insertSql.trim().toLowerCase();
		logger.debug(str2);

		str2 = str2.substring(str2.indexOf("insert") + 6).trim();
		if (str2.startsWith("into")){
			str2 = str2.substring(str2.indexOf("into") + 4).trim();
		}

		@SuppressWarnings("unused")
		String tableName = "";
		if (str2.indexOf("(") > -1){
			tableName = str2.substring(0,str2.indexOf("("));
		}  else if (str2.indexOf(" ") > -1){
			tableName = str2.substring(0,str2.indexOf(" "));
		} else if (str2.indexOf("\t") > -1){
			tableName = str2.substring(0,str2.indexOf("\t"));
		} else if (str2.indexOf("\r") > -1){
			tableName = str2.substring(0,str2.indexOf("\r"));
		} else if (str2.indexOf("\n") > -1){
			tableName = str2.substring(0,str2.indexOf("\n"));
		} else {
			tableName = str2.toString();
		}
		tablesList.add(tableName);

		return tablesList;

	}


	/**
	 * 获取来源表列表
	 * @param sql
	 * @return
	 */
	public List<String> getSourceTableList(String selectSql){
		logger.info("enter getFromTableList" );
		
	//	sql = DbUtil.clearSQLComment(sql);
		
		List<String> tablesList = new ArrayList<String>();
			logger.debug("selectSql = " + selectSql);
			List<String> list = searchTableNames(selectSql);
			
			tablesList.addAll(list);
		
		return tablesList;
	}
	
	
	/**
	 * 获取插入语句  ,
	 * 如 1.insert into table_name select * from table_name2;
	 *    2.insert into table_name values(field1,field2);(未实现)
	 *    3.merge into table_name using table_name2 ...(未实现)
	 *    4.merge into table_name using (select * from table_name2)...(未实现)
	 * @param sql
	 * @return
	 */
	public List<String> getInsertSQLList(String sql){
		
		List<String> sqlList = new ArrayList<String>();
			if (sql.toLowerCase().indexOf("insert") > 0){
				sql = sql.substring(sql.toLowerCase().indexOf("insert"));//从第一个insert语句开始解释
				String[] sqls = sql.split(";");
				for(String str : sqls){
					String str2 = str.trim().toLowerCase();
					logger.debug(str2);
					if (str2.startsWith("insert") && str2.indexOf("from") > -1 ){ 

						str2 = str2.substring(str2.indexOf("insert") + 6).trim();
						if (str2.startsWith("into")){
							str2 = str2.substring(str2.indexOf("into") + 4).trim();
						}

						@SuppressWarnings("unused")
						String tableName = "";
						if (str2.indexOf("(") > -1){
							tableName = str2.substring(0,str2.indexOf("("));
						}  else if (str2.indexOf(" ") > -1){
							tableName = str2.substring(0,str2.indexOf(" "));
						} else if (str2.indexOf("\t") > -1){
							tableName = str2.substring(0,str2.indexOf("\t"));
						} else if (str2.indexOf("\r") > -1){
							tableName = str2.substring(0,str2.indexOf("\r"));
						} else if (str2.indexOf("\n") > -1){
							tableName = str2.substring(0,str2.indexOf("\n"));
						} else {
							tableName = str2.toString();
						}

						sqlList.add(str.trim());
					}
				}
			}
		return sqlList;
	}
	
	
	/**
	 * 找出表名，先将sql通过Token进行分词，然后跟现有的表名进行匹配，
	 * 包含在includeList中或不以D开头的表，并排除掉excludeList中的表
	 * @param tableList
	 * @param includeList
	 * @param excludeList
	 */
	public static List<String> searchTableNames(String selectSql){
		    logger.info("enter searchTableNames");
			LinkedHashSet<String> set = new LinkedHashSet<String>();
			List<String> list = new ArrayList<String>();
			
			StringTokenizer st = new StringTokenizer(selectSql," ",false);
			
			while( st.hasMoreElements()){
				String str = st.nextToken().trim();
				logger.debug("str = " + str);
				if(str.length() < 3){
					continue;
				}

				for(String tableName : userObjectList){

					if (str.toUpperCase().equals(tableName)){
							set.add(tableName);
					}
				}
			}
			
			list.addAll(set);
			
			return list;
	}
	
	
	public boolean isLoadedPackage() {
		return isLoadedPackage;
	}


	public void setLoadedPackage(boolean isLoadedPackage) {
		this.isLoadedPackage = isLoadedPackage;
	}

	
}
