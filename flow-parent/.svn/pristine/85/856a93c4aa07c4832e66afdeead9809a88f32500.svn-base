package com.aspire.etl.flowdesinger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;

import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.Note;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.layout.HorizontalLayout;
import com.aspire.etl.layout.NodeLink;
import com.aspire.etl.layout.TaskNode;
import com.aspire.etl.tool.Utils;
import com.aspire.etl.uic.TaskDialog;
import com.aspire.etl.uic.WcpAction;
import com.aspire.etl.uic.WcpFileFilter;
import com.aspire.etl.uic.WcpImagePool;
import com.aspire.etl.uic.WcpMessageBox;

/**
 * 元数据影响分析对话框
 * 
 * @author 罗奇
 * @since 2009-12-22
 *        <p>
 *        1.。
 *        <p>
 *        2.。

 * 
 */
public class MetadataAnalysisWindow extends JFrame implements WindowListener   {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JGraph graph;

	JScrollPane jScrollPane;
	
	private FlowMetaData flowMetaData;

	private TaskDialog taskDialog;

	private int iconWidth = 50;

	private int iconHeight = 50;

	private double grahpScale = 1.0;
	
	private static final HorizontalLayout horizontalLayout = new HorizontalLayout();
	
	private JPopupMenu popupMenu;
	
	private WcpAction expImgAction, taskSearchAction;
	private TaskSearchDialog taskSearcher;
	
	List<TaskNode> taskNodeList ;
	List<NodeLink> nodeLinkList ;
	List<Task>     taskList;

	public MetadataAnalysisWindow(String title, FlowMetaData fmd) {
		super(title);
		this.setSize(800, 600);

		// meta data
		this.flowMetaData = fmd;

		this.taskDialog = new TaskDialog(this, flowMetaData);
		taskDialog.setLocationRelativeTo(this);

		horizontalLayout.setTaskNodeXInterval(50);
		horizontalLayout.setTaskNodeYInterval(70);
		
		expImgAction = new ExpImageAction();
//		任务搜索
		taskSearchAction = new TaskSearchAction();
		taskSearcher = new TaskSearchDialog(this, flowMetaData);

		initPopupMenus();
		
		initGraph();
		
		jScrollPane = new JScrollPane(graph);
		this.add(jScrollPane, BorderLayout.CENTER);
		
		this.setLocationRelativeTo(null);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	private void initPopupMenus() {
		popupMenu = new JPopupMenu();		

		// 增加元数据影响分析 2009-12-22 罗奇
		popupMenu.addSeparator();
		addPopupMenuItem(expImgAction, popupMenu);
		addPopupMenuItem(taskSearchAction, popupMenu);
		
	}
	
	
	private void addPopupMenuItem(WcpAction action, JPopupMenu pm) {
		JMenuItem mi = new JMenuItem(action);
		pm.add(mi);
	}
	private void initGraph() {
		graph = new JGraph(new MyModel());
		// 阻止单独拖动连线的port
		graph.setDisconnectable(false);
	
		graph.addMouseListener(new WcpMouseHandler());
		graph.addMouseWheelListener(new WcpMouseWheelHandler());
		graph.setBackground(Color.lightGray);
		graph.setHandleColor(Color.RED);
		graph.setDragEnabled(false);
	}
	public List<TaskNode> createTaskNodeList(List<Task> taskList){
		List<TaskNode> taskNodeList = new ArrayList<TaskNode>();
		for (Task task : taskList) {
			taskNodeList.add(new TaskNode(""+task.getTaskID(),HorizontalLayout.countWidthByString(task.getDescription())));
		}
		return taskNodeList;		
	}
	
	public List<NodeLink> createNodeLinkList(List<Link> linkList){
		List<NodeLink> nodeLinkList = new ArrayList<NodeLink>();
		for (Link link : linkList) {
			nodeLinkList.add(new NodeLink(""+link.getFromTaskID(),""+link.getToTaskID()));
		}
		return nodeLinkList;		
	}
	
	/**
	 * 
	 * @param taskNodeList
	 * @param taskList
	 * @return
	 */
	public List<Task> updatePositionForTaskList(List<TaskNode> taskNodeList,List<Task> taskList){
		for(TaskNode taskNode:taskNodeList){
			for (Task task : taskList) {
				if(taskNode.getName().equalsIgnoreCase(""+task.getTaskID())){
					task.setXPos(taskNode.getX());
					task.setYPos(taskNode.getY());
				}
			}
		}
		return taskList;		
	}
	
	@SuppressWarnings("unchecked")
	public void drawAnalysisGraph(Task taskForAnalysis, String AnalysisType) {
		//initialize graph
		//initGraph();
		
		taskList = new ArrayList<Task>();
		List<Link> linkList = new ArrayList<Link>();
		
		if (AnalysisType.equalsIgnoreCase("LineageAnalysis")) {
			flowMetaData.queryLineageTask(taskForAnalysis,taskList,linkList);
		} else if (AnalysisType.equalsIgnoreCase("ImpactAnalysis")) {
			flowMetaData.queryImpactTask(taskForAnalysis,taskList,linkList);
		}
		//补上自己作为根节点
		taskList.add(taskForAnalysis);
		
		//horizontalLayout对taskNodeList做了去重处理，则重任务节点时，也要对taskList进行去重处理，不然会导致独立的任务节点 x_jiangts
		deRepeateTask(taskList);

		//将Task和Link，转换成布局类需要的TaskNode和NodeLink
		taskNodeList = createTaskNodeList(taskList);
		nodeLinkList = createNodeLinkList(linkList);
		
		//清除相邻并且相同的节点
		deSameAdjacentNode(taskNodeList,nodeLinkList);
		
		
		//开始布局，布局的结果是更新了TaskNode中的x,y坐标。
		horizontalLayout.setLayout(taskNodeList,nodeLinkList);
		
		
		//清除tasklist不在taskNodeList存在的元素
		delNotExistsTask(taskNodeList,taskList);
		
		//重新建立link列表
		linkList.clear();
		linkList.addAll(getLinkList(nodeLinkList));
		
		delNoLinkTask(taskList,linkList);
		//将布局得到的x,y坐标更新到task中。
		//会导致内存中的元数据库被改。
		//taskList = updatePositionForTaskList(taskNodeList,taskList);
		
		HashMap<Integer, DefaultGraphCell> taskID2cellMap = new HashMap<Integer, DefaultGraphCell>();
		
		
		// tasks
		for (Task task : taskList) {
			DefaultGraphCell c = drawTask(task);
			taskID2cellMap.put(task.getTaskID(), c);
		}
		// links
		for (Link link : linkList) {
			DefaultGraphCell cellFrom = taskID2cellMap
					.get(link.getFromTaskID());

			DefaultGraphCell cellTo = taskID2cellMap.get(link.getToTaskID());

			if (cellTo != null && cellFrom != null) {
				drawLink(link, (DefaultPort) cellFrom.getChildAt(0),
						(DefaultPort) cellTo.getChildAt(0));
			}
		}

	}

	/**
	 * 清除没有link线的task
	 * @param taskList
	 * @param linkList
	 */
	private void delNoLinkTask(List<Task> taskList, List<Link> linkList) {
		for(int i = 0; i < taskList.size(); i++){
			boolean isExists = false;
			Task task = taskList.get(i);
			for (Link link : linkList) {
				if(("" + link.getFromTaskID()).equals("" + task.getTaskID()) || ("" + link.getToTaskID()).equals("" + task.getTaskID())){
					isExists = true;
					break;
				}
			}
			if (!isExists){
				taskList.remove(i);
				i--;
			}
		}
		
	}
	/**
	 * 将从nodeLinkList得到Link列表
	 * @param nodeLinkList
	 * @param linkList
	 */
	private List<Link> getLinkList(List<NodeLink> nodeLinkList) {
		
		 List<Link> linkList = new ArrayList<Link>();
		for (NodeLink nodeLink : nodeLinkList) {
			linkList.add(new Link(Utils.getRandomIntValue(),Integer.parseInt(nodeLink.getFrom()),Integer.parseInt(nodeLink.getTo())));
		}
		return linkList;	
	}
	/**
	 * 清除独立任务
	 * @param taskNodeList
	 * @param taskList
	 */
	private void delNotExistsTask(List<TaskNode> taskNodeList, List<Task> taskList) {
		Boolean isExists = false;
		for(int i = 0; i < taskList.size(); i++){
			Task task = taskList.get(i);
			isExists = false;
			for(TaskNode node: taskNodeList){
				  if (node.getName().equals("" + task.getTaskID())){
					  isExists = true;
				  }
			}
			if (!isExists){
				taskList.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * 删除相邻重复的节点 
	 * 1.找出from和to的task一样的link，得到fromID和toID
	 * 2.得到from集合：fromID
	 *   得到to集合：  toID来找出所有link的from为toID的ID
	 * 3.删除所有link的from为toID的link
	 *   删除fromID和toID的link  
	 * 4.删除toID的节点
	 * 5.from集合和to集合进行link : to的一个节点要跟每个from的节点进行link
	 * 6.清除掉link中缺少from或to（from或to所对应的TaskNode不存在）的link ,后续delNoLinkTask方法会清除这些缺失link信息的TaskNode
	 * @param taskNodeList
	 * @param nodeLinkList
	 * @author jiangts  add 2010-01-14
	 */
	private void deSameAdjacentNode(List<TaskNode> taskNodeList, List<NodeLink> nodeLinkList) {

		//System.out.println("deSameAdjacentNode taskNodeList.size=" + taskNodeList.size() + " , nodeLinkList.size="+ nodeLinkList.size());
		StringBuffer fromID = new StringBuffer();
		StringBuffer toID = new StringBuffer();
		List<String> fromIDList = new ArrayList<String>();
		List<String> toIDList = new ArrayList<String>();
		
		/** 1.找出from和to的task一样的link，*/
		
		for(int ii = 0; ii < nodeLinkList.size(); ii++){
			NodeLink link = nodeLinkList.get(ii);
			
			Task fromTask = flowMetaData.queryTask(Integer.parseInt(link.getFrom()));
			Task toTask = flowMetaData.queryTask(Integer.parseInt(link.getTo()));
			
			//重复的表插件有可能超出2个以上，如果直接把两个重复的link放入list中再做删除操作会有问题，得按遍历所有link，一个一个往下清除才行
			if (fromTask.getTaskType().equals(FlowDesignerConstants.TASK_TYPE_TABLE) && 
					toTask.getTaskType().equals(FlowDesignerConstants.TASK_TYPE_TABLE) &&
					fromTask.getTask().equals((toTask.getTask()))){
				//sameNodLinkList.add(link);


				//得到fromID和toID
				fromID.delete(0, fromID.length());
				toID.delete(0, toID.length());
				fromID.append(link.getFrom());
				toID.append(link.getTo());

				/** 2.得到from集合：fromID
				 *   得到to集合：  toID来找出所有link的from为toID的ID */
				//清除集合
				fromIDList.clear();
				toIDList.clear();

				//得到from集合
				fromIDList.add(fromID.toString());

				//得到to集合
				for(NodeLink nodeLink:nodeLinkList){
					if(nodeLink.getFrom().equals(toID.toString())){
						toIDList.add(nodeLink.getTo());
					}
				}

				/** 3.删除所有link的from为toID的link
				 *   删除fromID和toID的link   */
				NodeLink nodeLink = null;
				for(int i = 0; i < nodeLinkList.size(); i++){
					nodeLink = nodeLinkList.get(i);
					//删除所有link的from为toID的link  , 删除fromID和toID的link
					if(nodeLink.getFrom().equals(toID.toString())
							||(nodeLink.getFrom().equals(fromID.toString()) && nodeLink.getTo().equals(toID.toString()))
					){
						//System.err.println("nodeLinkList.remove(i) " + nodeLink);
						nodeLinkList.remove(i);
						//System.err.println("nodeLinkList.size " + nodeLinkList.size());
						i--;
					}
				}

				/** 4.删除toID的节点*/
				for(int i = 0; i < taskNodeList.size(); i++){
					if(taskNodeList.get(i).getName().equals(toID.toString())){
						//System.err.println("taskNodeList.remove(i) " + taskNodeList.get(i).getName());
						taskNodeList.remove(i);

						//System.err.println("taskNodeList.size " + taskNodeList.size());
						i--;
					}
				}

				/** 5.from集合和to集合进行link : to的一个节点要跟每个from的节点进行link*/
				for(String from :fromIDList){
					for(String to :toIDList){
						//System.err.println("add Link:" + new NodeLink(from,to));
						nodeLinkList.add(new NodeLink(from,to));
					}
				}
				ii--;
			}
		}
		
		for(int i = 0; i < nodeLinkList.size(); i++){
			NodeLink nodeLink = nodeLinkList.get(i);
			boolean isFromExists = false;
			boolean isToExists = false;
			for(TaskNode taskNode : taskNodeList){
				if(nodeLink.getFrom().equals(taskNode.getName())){
					isFromExists = true;
					continue;
				}
				if(nodeLink.getTo().equals(taskNode.getName())){
					isToExists = true;
					continue;
				}
			}
			
			if(!(isFromExists && isToExists)){
				nodeLinkList.remove(i);
				i--;
			}
			
		}
	}
	
	
	/**
	 * 删除相邻重复的节点 
	 * 1.找出from和to的task一样的link，得到fromID和toID
	 * 2.得到from集合：fromID来找出所有link的to为fromID的ID
	 *   得到to集合：  toID来找出所有link的from为toID的ID
	 * 3.删除所有link的to为fromID的link
	 *   删除所有link的from为toID的link
	 *   删除fromID和toID的link  
	 * 4.删除toID的节点
	 * 5.from集合和to集合进行link : to的一个节点要跟每个from的节点进行link
	 * @param taskNodeList
	 * @param nodeLinkList
	 * @author jiangts  add 2010-01-14
	 */
	private void deSameAdjacentNode2(List<TaskNode> taskNodeList, List<NodeLink> nodeLinkList) {
		List<NodeLink> sameNodLinkList = new ArrayList<NodeLink>();
		StringBuffer fromID = new StringBuffer();
		StringBuffer toID = new StringBuffer();
		List<String> fromIDList = new ArrayList<String>();
		List<String> toIDList = new ArrayList<String>();
		
		/** 1.找出from和to的task一样的link，*/
		for(NodeLink link:nodeLinkList){
			if (flowMetaData.queryTask(Integer.parseInt(link.getFrom())).getTask().equals(
					(flowMetaData.queryTask(Integer.parseInt(link.getTo())).getTask()))){
				sameNodLinkList.add(link);
			}
		}
		
		for(NodeLink link:sameNodLinkList){
			
			//得到fromID和toID
			fromID.delete(0, fromID.length());
			toID.delete(0, toID.length());
			fromID.append(link.getFrom());
			toID.append(link.getTo());
		
			/** 2.得到from集合：fromID来找出所有link的to为fromID的from
			//   得到to集合：  toID来找出所有link的from为toID的to */
			//清除集合
			fromIDList.clear();
			toIDList.clear();
			
			//得到from集合
			for(NodeLink nodeLink:nodeLinkList){
				if(nodeLink.getTo().equals(fromID.toString())){
					fromIDList.add(nodeLink.getFrom());
				}
			}
			//得到to集合
			for(NodeLink nodeLink:nodeLinkList){
				if(nodeLink.getFrom().equals(toID.toString())){
					toIDList.add(nodeLink.getTo());
				}
			}
			
		 /** 3.删除所有link的to为fromID的link 	
		 //   删除所有link的from为toID的link
		 //   删除fromID和toID的link  */
			NodeLink nodeLink = null;
		   for(int i = 0; i < nodeLinkList.size(); i++){
			   nodeLink = nodeLinkList.get(i);
			   //删除所有link的to为fromID的link, 删除所有link的from为toID的link, 删除fromID和toID的link
			   if(nodeLink.getTo().equals(fromID.toString())
					   || nodeLink.getFrom().equals(toID.toString())
					   ||(nodeLink.getFrom().equals(fromID.toString()) && nodeLink.getTo().equals(toID.toString()))
					   ){
				   nodeLinkList.remove(i);
				   i--;
			   }
		   }
			
		/** 4.删除toID的节点*/
		   for(int i = 0; i < taskNodeList.size(); i++){
			   if(taskNodeList.get(i).getName().equals(toID.toString())){
				   taskNodeList.remove(i);
				   i--;
			   }
		   }
		   
		/** 5.from集合和to集合进行link : to的一个节点要跟每个from的节点进行link*/
			for(String to :toIDList){
				for(String from :fromIDList){
					nodeLinkList.add(new NodeLink(from,to));
				}
			}
		}
	}
	
	
	

	/**
	 * 删除重复的TaskNode
	 * @param taskNodeList
	 * @author jiangts  add 2010-01-13
	 */
	@SuppressWarnings("unchecked")
	private void deRepeateTask(List<Task> taskList){
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		//System.out.println("去重前" + taskNodeList.size() + " " + StringUtil.getString(taskNodeList) );
		for(Task task :taskList){
			map.put(task.getTaskID(),task);
		}
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){
			list.add(((Map.Entry)it.next()).getValue());
		}
		taskList.clear();
		taskList.addAll(list);
		//System.out.println("去重后" + list.size()+ " " + StringUtil.getString(list) );
	}


	private DefaultGraphCell drawTask(Task task) {

		// Create vertex with the given name
		DefaultGraphCell cell = new DefaultGraphCell(task);
		AttributeMap am = cell.getAttributes();
		// Set bounds
		int xPos = 0;
		int yPos = 0;

		for(TaskNode taskNode:taskNodeList){			
			if(taskNode.getName().equalsIgnoreCase(""+task.getTaskID())){
				//用布局算法得到的坐标来画图
				xPos = taskNode.getX();
				yPos = taskNode.getY();
			}
		}

		GraphConstants.setBounds(am, new Rectangle2D.Double(xPos,
				yPos, HorizontalLayout.countWidthByString(task.getDescription()) , iconHeight + 20));
		TaskType tt = flowMetaData.queryTaskType(task.getTaskType());
		if (tt == null) {
			WcpMessageBox.postError(this, "缺少类型为'" + task.getTaskType()
					+ "'的插件，请先安装插件");
			return null;
		}

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

		// 阻止graph打开缺省的cell编辑框
		GraphConstants.setEditable(am, false);
		// Add a Floating Port

		cell.addPort();
		graph.getGraphLayoutCache().insert(cell);


		return cell;
	}

	/**
	 * @author wangcaiping,20080701
	 * @param link
	 * @param source
	 * @param target
	 * @return
	 */
	private DefaultEdge drawLink(Link link, DefaultPort source,
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

	public void edit() throws MetaDataException {
		if (!graph.isSelectionEmpty()) {
			// edit task
			Object obj = graph.getSelectionCell();
			if ((obj != null) && (obj instanceof DefaultGraphCell)) {
				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object userObj = cell.getUserObject();
				if (userObj instanceof Task) {
					Task task = (Task) userObj;
					taskDialog.showDialog(task);
				} else if (userObj instanceof Note) {
					Note nt = (Note) userObj;
					String str = JOptionPane.showInputDialog("Note", nt
							.getValue());
					if (str != null) {
						nt.setValue(str);
					}
				}
				graph.getGraphLayoutCache()
						.editCell(cell, cell.getAttributes());
			}
		}
	}

	// class to handle mouse double click
	private class WcpMouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			if ((me.getButton() == MouseEvent.BUTTON1)
					&& (me.getClickCount() >= 2)) {
				try {
					edit();
				} catch (MetaDataException e) {
					//WcpMessageBox.postException(MetadataAnalysisWindow.this, e);
				}
			}
			if (SwingUtilities.isRightMouseButton(me)) {
				popupMenu.show(graph, me.getX(), me.getY());
				// Else if in ConnectMode and Remembered Port is Valid
			}
		}
		public void mouseReleased(MouseEvent me) {
			//do nothing
		}
	}

	private class WcpMouseWheelHandler implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent arg0) {
			int mod = arg0.getModifiers();
			Point pt = arg0.getPoint();
			if (mod != InputEvent.CTRL_MASK) {
				return;
			}
			int rot = arg0.getWheelRotation();
			if (rot < 0) {
				// scroll up, zoon out
				zoomOut(pt);
			} else if (rot > 0) {
				// scroll down, zoom in
				zoomIn(pt);
			}
		}
	}

	public void zoomReset() {
		if (grahpScale != 1.0) {
			grahpScale = 1.0;
			graph.setScale(1);
		}
	}

	public void zoomIn(Point pt) {
		grahpScale += 0.1;
		if (grahpScale > 2.0) {
			grahpScale = 2.0;
		} else {
			if (pt != null) {
				graph.setScale(grahpScale, pt);
			} else {
				graph.setScale(grahpScale);
			}
		}
	}

	public void zoomOut(Point pt) {
		grahpScale -= 0.1;
		if (grahpScale < 0.3) {
			grahpScale = 0.3;
		} else {
			if (pt != null) {
				graph.setScale(grahpScale, pt);
			} else {
				graph.setScale(grahpScale);
			}
		}
	}

	public void zoomOut() {
		zoomOut(null);
	}

	public void zoomIn() {
		zoomIn(null);
	}

	private class MyModel extends DefaultGraphModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// Override Superclass Method
		public boolean acceptsSource(Object edge, Object port) {
			// Source only Valid if not Equal Target
			return (((Edge) edge).getTarget() != port);
		}

		// Override Superclass Method
		public boolean acceptsTarget(Object edge, Object port) {
			// Target only Valid if not Equal Source
			return (((Edge) edge).getSource() != port);
		}
	}

	public JGraph getGraph() {
		return graph;
	}

	public void exportImage(File f) throws IOException {
		BufferedImage img = graph.getImage(Color.WHITE, 10);
		String postfix = "JPG";
		String name = f.getName().trim();
		int idx = name.lastIndexOf('.');
		if (idx > 0) {
			postfix = name.substring(idx);
			if ((postfix == null) || (postfix.trim().equals(""))) {
				postfix = "JPG";
			} else if (!(postfix.equalsIgnoreCase("PNG")
					|| postfix.equalsIgnoreCase("JPG") || postfix
					.equalsIgnoreCase("GIF"))) {
				postfix = "JPG";
			}
		}
		ImageIO.write(img, postfix, f);
	}
	private class ExpImageAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JFileChooser jfc;

		public ExpImageAction() {
			super("导出图片...", FlowDesigner.class, "images/expimg.png");
			WcpFileFilter wff = new WcpFileFilter(new String[] { "PNG", "JPG",
					"GIF" });
			jfc = new JFileChooser();
			jfc.setFileFilter(wff);
		}

		public void actionPerformed(ActionEvent ae) {
			jfc.setSelectedFile(new File(MetadataAnalysisWindow.this.getTitle() + ".JPG")); // 这里设置
			if (jfc.showSaveDialog(MetadataAnalysisWindow.this) == JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				try {
					exportImage(f);
					WcpMessageBox.informSaving(MetadataAnalysisWindow.this, f);
				} catch (IOException e) {
					WcpMessageBox.postException(MetadataAnalysisWindow.this, e);
				}
			}
		}
	}
	
	/**
	 * 任务搜索
	 * @author jiangts
	 *
	 */
	private class TaskSearchAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public TaskSearchAction() {
			super("搜索", FlowDesigner.class, "images/open.png");
		}

		public void actionPerformed(ActionEvent ae) {
				taskSearcher.showDialog(taskList);
				if (!taskSearcher.isApproved()) {
					return;
				}
				Task resultTask = taskSearcher.getSelectedTask();
				//开始定位
				Object[] cells = graph.getGraphLayoutCache().getCells(false, true,
						false, false);
				for (int i = 0; i < cells.length; i++) {
					DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
					
					Object obj = flowGraphCell.getUserObject();
					if (obj instanceof Task) {
						Task task = (Task) obj;
						//如果任务节点描述与搜索出来任务节点描述一致，而选取该任务节点：
						if (resultTask.getDescription().equals(task.getDescription())){
							graph.setSelectionCell(cells[i]);
							
							//得到cell的x,y并滚动面板
							Rectangle2D rect = ((Rectangle2D)flowGraphCell.getAttributes().get(GraphConstants.BOUNDS));
							
							String xstr = String.valueOf(rect.getX());
							String ystr = String.valueOf(rect.getY());
							int x = Integer.parseInt(xstr.substring(0,xstr.indexOf(".")));
							int y = Integer.parseInt(ystr.substring(0,ystr.indexOf(".")));
							jScrollPane.getHorizontalScrollBar().setValue(x);
							jScrollPane.getVerticalScrollBar().setValue(y);
							break;
						}
						
					} 
				}
		}
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	

	public void windowClosing(WindowEvent arg0) {
			this.dispose();
	}
}
