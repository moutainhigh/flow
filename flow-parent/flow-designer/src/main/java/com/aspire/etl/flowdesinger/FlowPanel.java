package com.aspire.etl.flowdesinger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphTransferable;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

import com.aspire.etl.analyse.ReportAnalyse;
import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.Note;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskAttribute;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.layout.HorizontalLayout;
import com.aspire.etl.layout.NodeLink;
import com.aspire.etl.layout.TaskNode;
import com.aspire.etl.tool.TimeUtils;
import com.aspire.etl.tool.Utils;
import com.aspire.etl.uic.OutlineTaskflowDialog;
import com.aspire.etl.uic.TaskDialog;
import com.aspire.etl.uic.TaskflowDialog;
import com.aspire.etl.uic.WcpImagePool;
import com.aspire.etl.uic.WcpMessageBox;
import com.jgraph.layout.JGraphFacade;

/**
 * 
 * @author wangcaiping
 * @since 2008-2-26
 * 
 * @author ����
 * @since 2008-3-19
 *        <p>
 *        1.��Ԫ���ݸı䣬�����ж��󶼸�Ϊ���������ID��ʾ�������޸�����Ԫ���ݷ��ʽӿڡ�
 *        <p>
 *        2.�������߹��ܣ���ֹ�������ڵ���ظ����ߣ��������ߡ�
 *        <p>
 *        3.��Ϊsave�˵�һ���Դ��̱��棬�м���ɾ�Ķ�ֻ�����ڴ�.
 *        <p>
 *        4.ֻҪ���رյ�ǰ�༭���ڣ��������֧��undo��redo��Ԫ����Ҳ֧��,��ʹ�Ѵ��̶�û��ϵ��
 *        <p>
 *        5.��ֹ�����߽�����ı�Դ��Ŀ�ꡣ
 *        <p>
 *        6.�½���task,Ĭ��������Ϊ����.
 * 
 * @author ����
 * @since 2008-3-21
 *        <p>
 *        1.�޸�save������ͬ����ͬID�����̱���ʱ��ʾ���ǡ�
 * 
 * @author ����
 * @since 2008-3-22
 *        <p>
 *        1.�ýڵ�ͼ���Զ���С���ѱ�����ʾ����label��
 * 
 * @author ����
 * @since 2008-5-14
 *        <p>
 *        1.�Զ���portview���ı�ê���С��
 *  
 * @author ����
 * @since 2009-8-4
 *        <p>
 *        1.��������ʱͬʱ���������б�
 * <p>
 * ��ע���½�taskflow,task,link,taskAttribute�ȶ���ʱ�� ����ID�õ���Random.nextInt()��
 * 
 * @author jiangts
 * @since 2009-12-29
 *        <p>
 *        1.��ֹ���˫���հ������������öԻ����������öԻ�����ڴ����ͼ������
 *        2.�޸��Ҽ�����ʱ��ʧ����ڵ�
 *        
 * @author jiangts
 * @since 2010-07-29
 *        <p>
 *        1.�޸���������ֻ�����������ļ�飬�����˶�����ID�ļ��
 *        2.�޸�ճ���Ĺ��ܣ���ͨ���̵�����ֻ������ͨ����ճ�����ڴ����ʧЧ��������̵�����Ҳһ������������ͨ����ճ��
 *
 * @author jiangts
 * @since 2011-01-01
 *        <p>
 *        1.�����˸���ճ�����ܣ�֧�ֿ������֮ǰ���и��ƣ�
 *          ԭ������ʱ��Ҫ�ӵ�ǰ��Ԫ���ݻ�ȡ��ص�Taskflow��TaskAttribute��
 *          �����һ���µ�Ԫ�����и������ݹ���ʱ���������û����ص�Ԫ������Ϣ�����Ծ��Ҳ������޷�ճ�����ݹ�����
 *          �޸ĳɣ��ڸ���ʱ����Taskflow��TaskAttribute��Ϣset������Task�У�ճ��ʱֱ�Ӵ�taskȡTaskflow��TaskAttribute��Ϣ
 *          ���ڸ���ʱ��Ҫ���л��������õ����Զ���Ҫ֧�����л���
 *          	1.֮ǰ����Rectangle2D.Double�࣬��������֧�����л���
 *          	  �����ڵ�ǰĿ¼���½��˸�Doubile�࣬Rectangle2D.Double����Ʒ��
 *              2.Task��taskAttrList����java.util.ArrayList;
 *              3.Taskflow��implements��Serializable;
 *         	     
 */
public class FlowPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger log =Logger.getLogger(FlowPanel.class);
	
	private JGraph graph;

	private Taskflow taskflow;

	private FlowMetaData flowMetaData;

	private TaskDialog dlgTask;

	private TaskflowDialog dlgTaskflow;

	private GraphUndoManager undoManager;

	private JPopupMenu popupMenu;

	private int taskId;

	private int iconWidth = 50;

	private int iconHeight = 50;

	// �Զ����ê���С
	private int portSize = 9;

	private double grahpScale = 1.0;
	
	private Object[] selectCells ;
	
	private JScrollPane scrollPane; 
	
	private boolean isEdited;
	
	//��ǰ������ڵ�x,y�㣬����ճ��ʱʹ��
	private int xPoint;
	private int yPoint;
	
	//���޸Ĺ��������б����ڱ����٣���ǰ���̶�Ӧ�Ĵ�٣��������̵ļ�������
	private Set<Taskflow> editedFlowList;

	public FlowPanel(Taskflow tf, JPopupMenu pm, TaskDialog dlg1,
			TaskflowDialog dlg2, FlowMetaData fmd) {
		super(new BorderLayout());
		// variables
		this.taskflow = tf;
		this.popupMenu = pm;
		this.dlgTask = dlg1;
		this.dlgTaskflow = dlg2;
		// initialize graph
		initGraph();
		// Update ToolBar based on Selection Changes
		scrollPane = new JScrollPane(graph);
		this.add(scrollPane, BorderLayout.CENTER);
		// meta data
		this.flowMetaData = fmd;
		// task id
		taskId = 0;
		
		editedFlowList = new HashSet<Taskflow>();
		
	}

	
	private void initGraph() {
		graph = new JGraph(new MyModel());
		// ��ֹ�����϶����ߵ�port
		graph.setDisconnectable(false);
		// ��ʾ������
		graph.setGridEnabled(true);
		graph.setGridVisible(true);
		graph.setPortsVisible(true);
		graph.setHandleColor(Color.RED);
		// ȱʡ��ê��̫С��ʹ���Զ����PortView.
		graph.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * Constructs a new instance of a PortView view for the
			 * specified object
			 */
			protected PortView createPortView(Port p) {
				return new MyPortView(p, portSize);
			}
		});
		// enable editing NOTE without final RETURN keystroke
		graph.setInvokesStopCellEditing(true);
		graph.setMarqueeHandler(new MyMarqueeHandler());
		graph.addMouseListener(new WcpMouseHandler());
		graph.addMouseWheelListener(new WcpMouseWheelHandler());
		undoManager = new GraphUndoManager();
		graph.getModel().addUndoableEditListener(undoManager);
	}

	public void openTaskflow() {
		List<Task> taskList = flowMetaData.queryTaskList(taskflow
				.getTaskflowID());
		List<Link> linkList = flowMetaData.queryLinkList(taskflow
				.getTaskflowID());
		// notes
		List<Note> noteList = flowMetaData.queryNoteList(taskflow
				.getTaskflowID());
	
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
		// notes
		for (Note nt : noteList) {
			DefaultGraphCell c = drawNote(nt);
			taskID2cellMap.put(nt.getNoteID(), c);
		}
	}

	public void openTaskflow(String taskflowName) {
		Taskflow taskflow = flowMetaData.queryTaskflow(taskflowName);
		List<Task> taskList = flowMetaData.queryTaskList(taskflow
				.getTaskflowID());
		List<Link> linkList = flowMetaData.queryLinkList(taskflow
				.getTaskflowID());
		// notes
		List<Note> noteList = flowMetaData.queryNoteList(taskflow
				.getTaskflowID());

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
		// notes
		for (Note nt : noteList) {
			DefaultGraphCell c = drawNote(nt);
			taskID2cellMap.put(nt.getNoteID(), c);
		}
	}
	
	public Taskflow addNewTaskFlow(){
		Taskflow newTaskflow = new Taskflow();
		newTaskflow.setTaskflowID(Utils.getRandomIntValue());
		dlgTaskflow.setTitle("�½����̶Ի���");
		boolean b = dlgTaskflow.showDialog(newTaskflow);
		if (b) {
			flowMetaData.insert(newTaskflow, FlowDesigner.getInstance().getUserID());
			isEdited = true;
			//��editedFlowList������޸ĵ������б�
			editedFlowList.add(newTaskflow);
			dlgTaskflow.setTitle("�������öԻ���");
			return newTaskflow;
		} else {
			dlgTaskflow.setTitle("�������öԻ���");
			return null;
		}
		
	}

	private DefaultGraphCell drawTask(Task task) {
		// Create vertex with the given name
		DefaultGraphCell cell = new DefaultGraphCell(task);
		AttributeMap am = cell.getAttributes();
		// Set bounds
		
		GraphConstants.setBounds(am, new Double(task.getXPos(),
				task.getYPos(), HorizontalLayout.countWidthByString(task.getDescription()) , iconHeight + 20));
		TaskType tt = flowMetaData.queryTaskType(task.getTaskType());
		if (tt == null) {
			WcpMessageBox.postError(this, "ȱ������Ϊ'" + task.getTaskType()
					+ "'�Ĳ�������Ȱ�װ���");
			return null;
		}
		ImageIcon icon = null;
		if (tt.getCategoryID() == 1) {
			// ����C++���
			icon = WcpImagePool.getIcon(this.getClass(), tt.getLargeIcon());
		} else {
			// �������͵��������
			icon = WcpImagePool.getIconOfPath(tt.getLargeIcon());
		}
		if (icon != null) {
			GraphConstants.setIcon(am, icon);
		}
		// ����͸��
		GraphConstants.setOpaque(am, false);

		GraphConstants.setSizeable(am, true);

		// �ýڵ�ͼ���Զ���С���ѱ�����ʾ����label��
		//GraphConstants.setAutoSize(am, true);
		// GraphConstants.setResize(am, true);

		// ��ֹgraph��ȱʡ��cell�༭��
		GraphConstants.setEditable(am, false);
		// Add a Floating Port

		cell.addPort();
		graph.getGraphLayoutCache().insert(cell);
		return cell;
	}

	public Task addTask(String taskType) {
		Task task = new Task(Utils.getRandomIntValue(), taskflow
				.getTaskflowID(), taskType + taskId, taskType);
		Point pt = graph.getMousePosition();
		if (pt != null) {
			task.setXPos((int) pt.getX());
			task.setYPos((int) pt.getY());
		}
		
		//Ĭ�ϸ澯ID������IDΪ��ǰ��taskID
		task.setAlertID(String.valueOf(task.getTaskID()));
		task.setPerformanceID(String.valueOf(task.getTaskID()));
		
		task.setDescription(flowMetaData.queryTaskType(taskType)
				.getDescription()
				+ taskId);

		taskId++;
		drawTask(task);
		return task;
	}
	
	
	/**
	 * ��Ӵ�������½��������̣�
	 * @param outlineTaskFlow --���
	 * @param taskflow   --�½���������
	 */
	public void addOutlineTask(Point mousePosition, Taskflow outlineTaskFlow, Taskflow taskflow) {

		Task task = new Task(Utils.getRandomIntValue(), outlineTaskFlow
				.getTaskflowID(), FlowDesignerConstants.TASK_TYPE_OUTLINE + taskId, FlowDesignerConstants.TASK_TYPE_OUTLINE);
		
		if (mousePosition != null) {
			task.setXPos((int) mousePosition.getX());
			task.setYPos((int) mousePosition.getY());
		}
		task.setAlertID(String.valueOf(task.getTaskID()));
		task.setPerformanceID(String.valueOf(task.getTaskID()));
		task.setDescription(taskflow.getDescription());
		task.setTask(taskflow.getTaskflow());
		flowMetaData.insert(task);
		taskId++;
		drawTask(task);
		isEdited = true;
	}

	/**
	 * @author wangcaiping,20080620
	 * 
	 */
	public void addNote() {
		Note nt = new Note(Utils.getRandomIntValue(), taskflow.getTaskflowID(),
				"NOTE", 0, 0, 80, 30);
		Point pt = graph.getMousePosition();
		if (pt != null) {
			nt.setXPos((int) pt.getX());
			nt.setYPos((int) pt.getY());
		}
		// flowMetaData.insert(nt);
		drawNote(nt);
	}

	/**
	 * @author wangcaiping, 20080620
	 * @param nt
	 * @return
	 */
	private DefaultGraphCell drawNote(Note nt) {
		DefaultGraphCell noteCell = new DefaultGraphCell(nt);
		GraphConstants.setAutoSize(noteCell.getAttributes(), true);
		// GraphConstants.setSizeable(noteCell.getAttributes(), true);
		// GraphConstants.setResize(noteCell.getAttributes(), true);
		GraphConstants.setBounds(noteCell.getAttributes(),
				new Double(nt.getXPos(), nt.getYPos(), nt
						.getWidth(), nt.getHeight()));
		GraphConstants.setGradientColor(noteCell.getAttributes(), Color.ORANGE);
		GraphConstants.setOpaque(noteCell.getAttributes(), true);
		GraphConstants.setEditable(noteCell.getAttributes(), false);
		graph.getGraphLayoutCache().insert(noteCell);
		return noteCell;
	}

	// Insert a new Edge between source and target
	public void addLink(DefaultPort source, DefaultPort target) {
		DefaultGraphCell srcCell = (DefaultGraphCell) source.getParent();
		DefaultGraphCell tgtCell = (DefaultGraphCell) target.getParent();
		Link newLink = new Link();

		Task fromTask = ((Task) srcCell.getUserObject());
		newLink.setFromTaskID(fromTask.getTaskID());

		Task toTask = ((Task) tgtCell.getUserObject());
		newLink.setToTaskID(toTask.getTaskID());

		Link link = flowMetaData.queryLinkBetweenTasks(fromTask.getTaskID(),
				toTask.getTaskID());
		// ����task֮�䲻����link�������ӡ�
		if (link == null) {
			newLink.setLinkID(Utils.getRandomIntValue());
			flowMetaData.insert(newLink);
			// ��ͷָ��Ķ�����ͨ�ڵ㣬���Ǹ��ڵ㡣
			toTask.setIsRoot(Task.NORMAL_STEP);
			drawLink(newLink, source, target);
		}
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
				GraphConstants.ARROW_CLASSIC);
		GraphConstants.setEditable(edge.getAttributes(), false);
		GraphConstants.setConnectable(edge.getAttributes(), false);
		graph.getGraphLayoutCache().insertEdge(edge, source, target);
		return edge;
	}
	/**
	 * �޸�
	 */
	public void edit(boolean isEditTaskFlow) throws MetaDataException {
		
		if (!graph.isSelectionEmpty()) {
			// edit task
			Object obj = graph.getSelectionCell();
			if ((obj != null) && (obj instanceof DefaultGraphCell)) {
				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object userObj = cell.getUserObject();
				if (userObj instanceof Task) {
					Task task = (Task) userObj;
					//TODO ��Ҫ��������TaskType
					if(task.getTaskType().equalsIgnoreCase(FlowDesignerConstants.TASK_TYPE_OUTLINE)){

						if (isEditTaskFlow){//�򿪸ô��������������

							if (flowMetaData.queryTaskflow(task.getTask()) == null){//��������������Ӧ�������Ҳ������򵯳��½��Ի���
								Taskflow newTaskflow = new Taskflow();
								newTaskflow.setTaskflowID(Utils.getRandomIntValue());
								boolean b = dlgTaskflow.showDialog(newTaskflow);
								if (b) {
									flowMetaData.insert(newTaskflow, FlowDesigner.getInstance().getUserID());
									task.setTask(newTaskflow.getTaskflow());
									task.setDescription(newTaskflow.getDescription());
									isEdited = true;
									//��editedFlowList������޸ĵ������б�
									editedFlowList.add(newTaskflow);
								}
							} else{
								
								//���������δ���̣��򵯳��Ի���ʾ������ȷ�����̣�Ȼ���ٴ�������ƴ���
								if (!FlowDesigner.getInstance().isEngineRunning() && flowMetaData.findTaskflow(flowMetaData.queryTaskflow(task.getTask()).getTaskflowID()) == null){
									int ret = -1;
									//���Ϊ�������,��ʾ���̣�
									ret = JOptionPane.showConfirmDialog(this,
											"�½���������δ����,����֮��ſ��Դ�,�Ƿ����ڴ��̣�", "����?", JOptionPane.YES_NO_OPTION);
									if (ret == JOptionPane.OK_OPTION) {
										boolean isOk = FlowDesigner.getInstance().save();
										//flowMetaData.saveTaskflowInfo(flowMetaData.queryTaskflow(task.getTask()).getTaskflowID());
										//��FlowDesignerʵ���Ĵ����̵ķ���
										if (isOk){
											FlowDesigner.getInstance().openTaskflow(flowMetaData.queryTaskflow(task.getTask()));
										}
									}
								} else {
									FlowDesigner.getInstance().openTaskflow(flowMetaData.queryTaskflow(task.getTask()));
								}
							}
						} else {//���������öԻ���
								
								String oldTabTitle = task.getTask();
								String newTabTitle = "";
								Taskflow taskflow = flowMetaData.queryTaskflow(task.getTask());
								
								if (taskflow == null){//��������������Ӧ�������Ҳ������򵯳��½��Ի���
									taskflow = new Taskflow();
									taskflow.setTaskflowID(Utils.getRandomIntValue());
								}
								
								int taskflowID = taskflow.getTaskflowID();
								boolean b = dlgTaskflow.showDialog(flowMetaData.queryTaskflow(task.getTask()));
								if (b) {
									task.setTask(flowMetaData.queryTaskflow(taskflowID).getTaskflow());
									task.setDescription(flowMetaData.queryTaskflow(taskflowID).getDescription());
									newTabTitle = task.getTask();
									FlowDesigner.getInstance().changeTabTitle(oldTabTitle, newTabTitle);
									//��editedFlowList������޸ĵ������б�
									editedFlowList.add(taskflow);
									isEdited = true;
								}
							}	

					}else{//������򿪲�����öԻ���
						boolean b = dlgTask.showDialog(task);
						if (b) {
							isEdited = true;
						}
					}

					graph.getGraphLayoutCache().editCell(cell, cell.getAttributes());
					GraphConstants.setBounds(cell.getAttributes(), new Double(task.getXPos(),
							task.getYPos(), HorizontalLayout.countWidthByString(task.getDescription()) , iconHeight + 20));

				} else if (userObj instanceof Note) {
					
					Note nt = (Note) userObj;
					String str = JOptionPane.showInputDialog("Note", nt
							.getValue());
					if (str != null) {
						nt.setValue(str);
						isEdited = true;
					}
					
					graph.getGraphLayoutCache().editCell(cell, cell.getAttributes());
					GraphConstants.setBounds(cell.getAttributes(), new Double(nt.getXPos(),
							nt.getYPos(), HorizontalLayout.countWidthByString(str),nt.getHeight()));
				}
				
				
				graph.getGraphLayoutCache().reload();
				graph.repaint();
			}
		} else {//������հ״������޸Ĵ�ٻ����̵����öԻ���
			
			if (taskflow.getGroupID() == FlowDesignerConstants.OUTLINE_GROUP_ID){//�򿪴���������öԻ���
				
				String title = taskflow.getTaskflow();
				OutlineTaskflowDialog dlgOutlineTaskflow = new OutlineTaskflowDialog(FlowDesigner.getInstance(),flowMetaData);
				boolean isOk = dlgOutlineTaskflow.showDialog(taskflow);
				if (isOk){//����TabPane�ı���
					FlowDesigner.getInstance().changeTabTitle(title + FlowDesignerConstants.OUTLINE_PREFIX, taskflow.getTaskflow() + FlowDesignerConstants.OUTLINE_PREFIX);
					isEdited = true;
				}
			} else {
				String title = taskflow.getTaskflow();
				boolean isOk = dlgTaskflow.showDialog(taskflow);
				if (isOk){//����TabPane�ı���
					FlowDesigner.getInstance().changeTabTitle(title, taskflow.getTaskflow());
					isEdited = true;
					//��editedFlowList������޸ĵ������б�
					editedFlowList.add(taskflow);
				}
			}
		}
	}	
	
	@SuppressWarnings("unchecked")
	public void remove() {
		if (!graph.isSelectionEmpty()) {
			ArrayList<Object> al = new ArrayList<Object>();
			Object[] cells = graph.getSelectionCells();
			cells = graph.getDescendants(cells);
			for (Object obj : cells) {
				al.add(obj);
				if ((obj != null) && (obj instanceof DefaultPort)) {
					DefaultPort dp = (DefaultPort) obj;
					Set edges = dp.getEdges();
					al.addAll(edges);
				}
			}
			cells = al.toArray();

			// ɾ��FlowMetaData�е���ض���
			try {
				for (Object e : cells) {
					if (e instanceof DefaultPort) {
						continue;
					}
					if (e instanceof DefaultEdge) {
						DefaultEdge edge = (DefaultEdge) e;
						Link link = (Link) edge.getUserObject();
						flowMetaData.delete(link);
						continue;
					} else if (e instanceof DefaultGraphCell) {
						DefaultGraphCell cell = (DefaultGraphCell) e;
						Object obj = cell.getUserObject();
						if (obj instanceof Task) {
							Task task = (Task) obj;
							flowMetaData.delete(task);
							
							//�ж��Ƿ�Ϊ��������,����ǣ���ر�������̴���
							if (flowMetaData.queryTaskflow(task.getTask()) != null){
								FlowDesigner.getInstance().removeTabByTitle(task.getTask());
							}
							
						} else if (obj instanceof Note) {
							Note nt = (Note) obj;
							flowMetaData.delete(nt);
						}
					}
				}

				// �����ж����нڵ��Ƿ�Ϊ���ڵ�
				List<Task> taskList = flowMetaData.queryTaskList(taskflow
						.getTaskflowID());
				for (Iterator iter = taskList.iterator(); iter.hasNext();) {
					Task task = (Task) iter.next();
					if (flowMetaData.isRootTask(task)) {
						task.setIsRoot(Task.ROOT_STEP);
					} else {
						task.setIsRoot(Task.NORMAL_STEP);
					}
				}

			} catch (Exception e1) {
				WcpMessageBox.postError(this, "ɾ����������'" + e1.getMessage()
						+ "'");
				//e1.printStackTrace();
				log.error("remove()����",e1);
			}
			graph.getModel().remove(cells);
		}
	}

	/**
	 * �������̵����ݿ���
	 * 
	 * ע�����̱༭������ֻ�Ƕ���������ڴ�������ɾ�ĵĶ���������Ҫ�ڱ༭��ɺ������ر���һ�ε�ǰ�༭�����̵����ݿ⡣ ������ԣ�
	 * 1����Ҫ���ڴ����޶����ݿ����еĶ���ɾ���� 2���ڴ����ж����ݿ����޵Ķ�����롣 2���ڴ��к����ݿ��ж��еĶ����޸����̬�����ݡ�
	 * 
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean save(String userID) throws Exception {
		Integer editTaskflowID = this.taskflow.getTaskflowID();
		Integer id;
		// �Ȳ�һ��Ԫ���ݿ�,�Ƿ���ͬ�������̣����򷵻�ID
		id = flowMetaData.getTaskflowIDbyName(this.taskflow.getTaskflow());
		if (id != null) {// ����ͬ������
			if (!editTaskflowID.equals(id)) {
				// ͬ����ͬID����������һ��ͬ������
				WcpMessageBox.postError(this, "ͬ�������Ѵ���!\n���޸��������ƺ��ٱ���!");
				// ��ͬ�⸲�ǣ���ֱ���˳�����Ϊϵͳ������ͬ�����̲�ͬID�����̴��ڡ�
				return false;
			}
		}
		
		// ������нڵ��λ�ã���������Ӧ��task����
		Object[] cells = graph.getGraphLayoutCache().getCells(false, true,
				false, false);
		
		//��ȡͬ����������
		ArrayList<String> addedTasks = new ArrayList<String>();
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		for (int i = 0; i < cells.length; i++) {
			DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
			Object obj = flowGraphCell.getUserObject();
			if (obj instanceof Task) {
				Task task = (Task) obj;
				//����
				//addedTasks.add(task.getTask());
				Integer count = map.get(task.getTask());
				if (count == null){
					map.put(task.getTask(), 1);
				} else {
					map.put(task.getTask(), count + 1);
				}
			} 
		}
		
		Iterator it = map.entrySet().iterator();
		StringBuffer sb = new StringBuffer();
		while(it.hasNext()){
			Map.Entry<String,Integer> entry = (Map.Entry<String,Integer>)it.next();
			if (entry.getValue() > 1){
				addedTasks.add(entry.getKey());
				sb.append(entry.getKey() + "\n");
			}
		}
		
		if (!sb.toString().equals("")){
			WcpMessageBox.warn(this, "�����д���ͬ������,������������,���޸��������ƺ��ٱ���!\n" +sb.toString());
			return false;
		}
		
		
		 //����Ǵ�����̣��ж��������Ƿ��Ѿ�����
		if(taskflow.getGroupID().equals(FlowDesignerConstants.OUTLINE_GROUP_ID)){

			for(Taskflow taskflow: editedFlowList){
				if(flowMetaData.findTaskflow(taskflow.getTaskflowID()) == null){
					id = flowMetaData.getTaskflowIDbyName(taskflow.getTaskflow());
					if (id != null) {// ����ͬ������
						if (!editTaskflowID.equals(id)) {
							// ͬ����ͬID����������һ��ͬ������
							WcpMessageBox.postError(this, "����Ϊ\"" + taskflow.getTaskflow() + "\"�������Ѵ���,\n���޸��������ƺ��ٱ���!");
							// ��ͬ�⸲�ǣ���ֱ���˳�����Ϊϵͳ������ͬ�����̲�ͬID�����̴��ڡ�
							return false;
						}
					}
				}
			}
		}
		
		//�������̣�
		for (int i = 0; i < cells.length; i++) {
			DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
			Object obj = flowGraphCell.getUserObject();
			if (obj instanceof Task) {
				Task task = (Task) obj;
				
				//����
				flowMetaData.insert(task);
			} else if (obj instanceof Note) {
				Note nt = (Note) obj;
				Rectangle2D rect = graph.getCellBounds(flowGraphCell);
				nt.setXPos((int) rect.getX());
				nt.setYPos((int) rect.getY());
				nt.setWidth((int) rect.getWidth());
				nt.setHeight((int) rect.getHeight());
				flowMetaData.insert(nt);
			}
		}
		
		//����Ǵ�����̣����������޸ĵ���������������̣���ټ������棩
		if(taskflow.getGroupID().equals(FlowDesignerConstants.OUTLINE_GROUP_ID)){
			for(Taskflow taskflow: editedFlowList){
				flowMetaData.saveTaskflowInfo(taskflow.getTaskflowID());
			}
		}
		
		// ����������ӵ���Ϣ����������Ӧ��link����
		cells = graph.getGraphLayoutCache().getCells(false, false, false, true);
		for (int i = 0; i < cells.length; i++) {
			DefaultEdge flowEdge = (DefaultEdge) cells[i];
			Link link = (Link) flowEdge.getUserObject();
			// ����
			flowMetaData.insert(link);
		}
		
		// ����ǰ�༭��taskflow���FlowMetaData
		flowMetaData.insert(this.taskflow, userID);
		// 2008-05-14 wuzhuokun ������ѭ���ж�
		Integer cycleTaskID = checkCycle(flowMetaData
				.queryLinkList(editTaskflowID));
		if (cycleTaskID != null) {
			Task cycleTask = flowMetaData.queryTask(cycleTaskID);
			WcpMessageBox.warn(this, "�����д�����ѭ���������" + cycleTask.getTask() + "("
					+ cycleTask.getDescription() + ")���޸���������ٱ���!");
			return false;
		}
		
		//�ȱ����������ڴ��
		if (taskflow.getGroupID() != FlowDesignerConstants.OUTLINE_GROUP_ID){//���Ǵ�ٲű���
			Task outlineTask = flowMetaData.queryOutlineTaskByTaskflow(taskflow);
			
			if (outlineTask != null && outlineTask.getTaskflowID() != null){
				flowMetaData.saveTaskflowInfo(outlineTask.getTaskflowID());
			}
		}
		//���浱ǰ������Ϣ
		
		flowMetaData.saveTaskflowInfo(editTaskflowID);

		isEdited = false;
		editedFlowList = new HashSet<Taskflow>();
		
		return true;
	}

	private Integer checkCycle(List<Link> name) {
		Integer taskID = null;
		try {
			Set<Integer> children = null;// ��������ӽڵ�
			Set<Integer> nextSet = null;// �����һ�ӽڵ�
			Set<Integer> allNextSet = new HashSet<Integer>();// �����һ�������ӽڵ�
			for (Link link : name) {
				children = new HashSet<Integer>();
				nextSet = queryNext(link.getFromTaskID(), name);
				children.addAll(nextSet);
				for (int i = 0; i <= name.size(); i++) {// ��ֹ��ѭ��
					allNextSet.clear();
					for (Integer ID : children) {
						nextSet = queryNext(ID, name);
						allNextSet.addAll(nextSet);
					}
					int size = children.size();
					children.addAll(allNextSet);
					if (size == children.size())// �޽ڵ���,�˳�ѭ��
						break;
				}
				if (children.contains(link.getFromTaskID())) {
					return link.getFromTaskID();
				}
			}
		} catch (Exception e) {
			WcpMessageBox.postException(this, e);
		}
		return taskID;

	}

	private Set<Integer> queryNext(Integer ID, List<Link> linkList) {
		Set<Integer> set = new HashSet<Integer>();
		for (Link link : linkList) {
			if (link.getFromTaskID().equals(ID)) {
				set.add(link.getToTaskID());
			}
		}
		return set;
	}

	private class MyPortView extends PortView {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int myPortSize = 9;

		public MyPortView(Object cell, int portSize) {
			super(cell);
			myPortSize = portSize;
		}

		public Rectangle2D getBounds() {

			Point2D loc = getLocation();
			double x = 0;
			double y = 0;
			if (loc != null) {
				x = loc.getX();
				y = loc.getY();
			}
			Rectangle2D bounds = new Double(x, y, 0, 0);
			bounds.setFrame(bounds.getX() - myPortSize / 2, bounds.getY()
					- myPortSize / 2, myPortSize, myPortSize);
			return bounds;
		}
	}

	// MarqueeHandler that Connects Vertices and Displays PopupMenus
	private class MyMarqueeHandler extends BasicMarqueeHandler {

		// Holds the Start and the Current Point
		private Point2D start, current;

		// Holds the First and the Current Port
		private PortView port, firstPort;

		private Cursor handCursor;

		public MyMarqueeHandler() {
			handCursor = new Cursor(Cursor.HAND_CURSOR);
		}

		// Override to Gain Control (for PopupMenu and ConnectMode)
		public boolean isForceMarqueeEvent(MouseEvent e) {
			if (e.isShiftDown())
				return false;
			// If Right Mouse Button we want to Display the PopupMenu
			if (SwingUtilities.isRightMouseButton(e))
				// Return Immediately
				return true;
			// Find and Remember Port
			port = getSourcePortAt(e.getPoint());
			// If Port Found and in ConnectMode (=Ports Visible)
			if (port != null && graph.isPortsVisible())
				return true;
			// Else Call Superclass
			return super.isForceMarqueeEvent(e);
		}

		// Display PopupMenu or Remember Start Location and First Port
		public void mousePressed(final MouseEvent e) {
			// If Right Mouse Button
			
			//�����Ҽ����ƹ��ܶ���ʱ���浱ǰѡ���Ķ�����Ϊ�����Ҽ�֮��ѡȡ�Ķ��󱻶�ʧ��
			selectCells = graph.getSelectionCells();
			
			if (SwingUtilities.isRightMouseButton(e)) {
				
				popupMenu.show(graph, e.getX(), e.getY());
				// Else if in ConnectMode and Remembered Port is Valid
			} else if (port != null && graph.isPortsVisible()) {
				// Remember Start Location
				start = graph.toScreen(port.getLocation());
				// Remember First Port
				firstPort = port;
			} else {
				// Call Superclass
				super.mousePressed(e);
			}
		}

		// Find Port under Mouse and Repaint Connector
		public void mouseDragged(MouseEvent e) {
			// If remembered Start Point is Valid
			if (start != null) {
				// Fetch Graphics from Graph
				Graphics g = graph.getGraphics();
				// Reset Remembered Port
				PortView newPort = getTargetPortAt(e.getPoint());
				// Do not flicker (repaint only on real changes)
				if (newPort == null || newPort != port) {
					// Xor-Paint the old Connector (Hide old Connector)
					paintConnector(Color.black, graph.getBackground(), g);
					// If Port was found then Point to Port Location
					port = newPort;
					if (port != null)
						current = graph.toScreen(port.getLocation());
					// no Port was found then Point to Mouse Location
					else
						current = graph.snap(e.getPoint());
					// Xor-Paint the new Connector
					paintConnector(graph.getBackground(), Color.black, g);
				}
			}
			// Call Superclass
			super.mouseDragged(e);
		}

		private PortView getSourcePortAt(Point2D point) {
			// Disable jumping
			graph.setJumpToDefaultPort(false);
			PortView result;
			try {
				// Find a Port View in Model Coordinates and Remember
				result = graph.getPortViewAt(point.getX(), point.getY());
			} finally {
				graph.setJumpToDefaultPort(true);
			}
			return result;
		}

		// Find a Cell at point and Return its first Port as a PortView
		private PortView getTargetPortAt(Point2D point) {
			// Find a Port View in Model Coordinates and Remember
			return graph.getPortViewAt(point.getX(), point.getY());
		}

		// Connect the First Port and the Current Port in the Graph or Repaint
		public void mouseReleased(MouseEvent e) {
			
			// If Valid Event, Current and First Port
			if (e != null && port != null && firstPort != null
					&& firstPort != port) {
				// Then Establish Connection
				addLink((DefaultPort) firstPort.getCell(), (DefaultPort) port
						.getCell());
				e.consume();
				// Else Repaint the Graph
			} else
				graph.repaint();
			// Reset Global Vars
			firstPort = port = null;
			start = current = null;

			// Call Superclass
			super.mouseReleased(e);
			
			//�������x,y�㣬ʹ��ճ��ʱʹ��
			xPoint = e.getX();
			yPoint = e.getY();
			
		}

		// Show Special Cursor if Over Port
		public void mouseMoved(MouseEvent e) {
			
			//��������ڵ㸴��
			if (graph.getSelectionCells()!= null){
				selectCells = graph.getSelectionCells();
			}
			// Check Mode and Find Port
			if (e != null && getSourcePortAt(e.getPoint()) != null
					&& graph.isPortsVisible()) {
				// Set Cusor on Graph (Automatically Reset)
				graph.setCursor(handCursor);
				// signal the BasicGraphUI's MouseHandle to stop further event
				// processing.
				e.consume();
			} else {
				// Call Superclass
				super.mouseMoved(e);
			}
		}

		// Use Xor-Mode on Graphics to Paint Connector
		private void paintConnector(Color fg, Color bg, Graphics g) {
			// Set Foreground
			g.setColor(fg);
			// Set Xor-Mode Color
			g.setXORMode(bg);
			// Highlight the Current Port
			paintPort(graph.getGraphics());
			// If Valid First Port, Start and Current Point
			if (firstPort != null && start != null && current != null)
				// Then Draw A Line From Start to Current Point
				g.drawLine((int) start.getX(), (int) start.getY(),
						(int) current.getX(), (int) current.getY());
		}

		// Use the Preview Flag to Draw a Highlighted Port
		private void paintPort(Graphics g) {
			// If Current Port is Valid
			if (port != null) {
				// If Not Floating Port...
				boolean o = (GraphConstants.getOffset(port.getAllAttributes()) != null);
				// ...Then use Parent's Bounds
				Rectangle2D r = (o) ? port.getBounds() : port.getParentView()
						.getBounds();
				// Scale from Model to Screen
				r = graph.toScreen((Rectangle2D) r.clone());
				// Add Space For the Highlight Border
				r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r
						.getHeight() + 6);
				// Paint Port in Preview (=Highlight) Mode
				graph.getUI().paintCell(g, port, r, true);
			}
		}
	}

	
	// class to handle mouse double click
	private class WcpMouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			//���˫���¼�����
			if ((me.getButton() == MouseEvent.BUTTON1)
					&& (me.getClickCount() >= 2)) {
				try {
					edit(true);
				} catch (MetaDataException e) {
					log.error(e);
					WcpMessageBox.postException(FlowPanel.this, e);
				}
				finally{
					//˫��֮��graph��ָ�뱻�޸ĳ�ʮ����ָ�룬����Ҫ�޸ĳ�Ĭ��
					graph.setCursor(Cursor.getDefaultCursor());
				}
			}
		}

		public void mouseReleased(MouseEvent me) {
			Object[] cells = graph.getSelectionCells();
			
			if (cells != null){
				for (Object cell : cells) {
					if (!(cell instanceof DefaultEdge)
							&& cell instanceof DefaultGraphCell) {
						// ��������ڵ��λ����Ϣ
						Rectangle2D rect = graph.getCellBounds(cell);
						DefaultGraphCell flowGraphCell = (DefaultGraphCell) cell;
						Object obj = flowGraphCell.getUserObject();
						if (obj instanceof Task) {
							Task task = (Task) obj;
							task.setXPos((int) rect.getX());
							task.setYPos((int) rect.getY());
							
							isEdited = true;
						}
					}
				}
			}
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

	public void undo() {
		try{
			undoManager.undo(graph.getGraphLayoutCache());
		}
		catch(Exception e){

		}
	}

	public void redo() {
		try{
			undoManager.redo(graph.getGraphLayoutCache());
		}
		catch(Exception e){

		}
	}

	public Taskflow getTaskflow() {
		return taskflow;
	}

	public JGraph getGraph() {
		return graph;
	}

	/**
	 * �����¼�
	 * @author wangcaiping 20080703
	 * @param act
	 * @param e
	 */
	public void postAction(Action act, ActionEvent e) {

		try{
			
			//���ڵ��Ҽ�����popup�˵�ʱ�����ֻѡȡ�����Ķ��󣬵�����������ʧ�������ڴ�����������ʱ����ѡȡ�Ķ���ѡȡ�Ķ����ڵ���popup�˵��Ѿ���������
			if (selectCells != null){
				graph.setSelectionCells(selectCells);

				//������������Ƶ�task�����У�����ճ��ʱ����Ԫ�����в�ѯ������������㲻ͬ�������֮����и���
				for (Object obj : selectCells) {
					if (obj instanceof DefaultGraphCell) {
						DefaultGraphCell cell = (DefaultGraphCell) obj;
						Object cellObj = cell.getUserObject();
						if (cellObj instanceof Task) {
							
							Task task = (Task) cellObj;
							List<TaskAttribute> list = flowMetaData.queryTaskAttributeList(task.getTaskID());
							ArrayList<TaskAttribute> attrlist = new ArrayList<TaskAttribute>();
							for(TaskAttribute taskatt : list){
								attrlist.add(taskatt);
							}
							
							//���������set��Task
							task.setTaskAttrList(attrlist);
							Taskflow taskflow = flowMetaData.queryTaskflow(task.getTaskflowID());
							
							//����������Ӧ��������Ϣset��Task
							if (taskflow.getGroupID() == flowMetaData.outlineGroupId){
								task.setMemberTaskflow(flowMetaData.queryTaskflowByOutlineTask(task));
							}
							
							//���������ڵ�������Ϣset��Task
							task.setTaskflow(taskflow);
						} 
					}
				}
			}
			e = new ActionEvent(graph, e.getID(), e.getActionCommand(), e
					.getModifiers());
			act.actionPerformed(e);
			
		}catch(Exception ex){
			
		}
	}

	/**
	 * paste operation
	 * 
	 * @author wangcaiping 20080703
	 * 
	 */
	public void paste() {
		Clipboard cb = graph.getToolkit().getSystemClipboard();
		if (cb == null) {
			return;
		}
		Object obj = null;
		try {
			obj = cb.getData(GraphTransferable.dataFlavor);
		} catch (UnsupportedFlavorException e) {
			//WcpMessageBox.postException(this, e);
		} catch (IOException e) {
			//WcpMessageBox.postException(this, e);
		}
		if (obj == null) {
			return;
		}
		GraphTransferable gt = (GraphTransferable) obj;
		HashMap<Task, Task> copiedTasks = new HashMap<Task, Task>();
		HashMap<DefaultGraphCell, DefaultGraphCell> copiedCells = new HashMap<DefaultGraphCell, DefaultGraphCell>();
		Object[] cells = gt.getCells();
		List cellList = new ArrayList();
		
		Task taskPos = getTaskMinPosition(cells);
		
		for (Object oc : cells) {
			DefaultGraphCell cell = (DefaultGraphCell) oc;
			Object uo = cell.getUserObject();
			// tasks
			if (uo instanceof Task) {
				Task t = (Task) uo;
				
				//����жϣ�������̵�������������ͨ�����н���ճ������֮Ҳһ��
				
				Taskflow taskflowInTask = t.getTaskflow();
				if (taskflowInTask == null){
					taskflowInTask = flowMetaData.queryTaskflow(t.getTaskflowID());
				}
				
				if (taskflowInTask != null){
					if ((taskflow.getGroupID() == FlowDesignerConstants.OUTLINE_GROUP_ID
							&& taskflowInTask.getGroupID() != FlowDesignerConstants.OUTLINE_GROUP_ID)
							||
							(taskflow.getGroupID() != FlowDesignerConstants.OUTLINE_GROUP_ID
									&& taskflowInTask.getGroupID() == FlowDesignerConstants.OUTLINE_GROUP_ID)){
						break;
					}
				}
				//�������ĸ���Ϊ���̼���ĸ��ƣ�����ͨ���̸�����������
				if (t.getTaskType().equals(FlowDesignerConstants.TASK_TYPE_OUTLINE)){
					
					Taskflow taskflow = t.getMemberTaskflow(); //flowMetaData.queryTaskflow(t.getTask());
					if (taskflow == null){
						 taskflow = flowMetaData.queryTaskflow(t.getTask());
					}
					
					Taskflow newTaskflow = new Taskflow();
					newTaskflow.setTaskflowID(Utils.getRandomIntValue());
					newTaskflow.setTaskflow("Copy-" + t.getTask());
					newTaskflow.setDescription("Copy-" + t.getDescription());				
					newTaskflow.setMemo(taskflow.getMemo());
					newTaskflow.setThreadnum(taskflow.getThreadnum());
					newTaskflow.setGroupID(taskflow.getGroupID());
					newTaskflow.setStep(taskflow.getStep());
					newTaskflow.setStepType(taskflow.getStepType());
					flowMetaData.insert(newTaskflow, FlowDesigner.getInstance().getUserID());
					
					//��editedFlowList������޸ĵ������б�
					editedFlowList.add(newTaskflow);
				} 
				
				Task new_t = (Task) t.clone();
				new_t.setTaskflowID(this.taskflow.getTaskflowID());
				new_t.setAlertID(""+new_t.getTaskID());
				new_t.setPerformanceID(""+new_t.getTaskID());
				
				if (t.getTaskType().equals(FlowDesignerConstants.TASK_TYPE_OUTLINE)){
					new_t.setTask("Copy-" + t.getTask());
					new_t.setDescription("Copy-" + t.getDescription());
				}
				
				//new_t.setXPos(t.getXPos() + 80);
				//new_t.setYPos(t.getYPos() + 80);
				
				new_t.setXPos(xPoint + t.getXPos() - taskPos.getXPos());
				new_t.setYPos(yPoint + t.getYPos() - taskPos.getYPos());
				//new_t.setXPos(xPoint);
				//new_t.setYPos(yPoint);
				
				DefaultGraphCell new_cell = drawTask(new_t);
				copiedTasks.put(t, new_t);
				copiedCells.put(cell, new_cell);
				cellList.add(new_cell);
				//������������б�
				java.util.List<TaskAttribute> oldTaskAttributeList = t.getTaskAttrList();//flowMetaData.queryTaskAttributeList(t.getTaskID());
				if (oldTaskAttributeList == null){
					oldTaskAttributeList = flowMetaData.queryTaskAttributeList(t.getTaskID());
				}
				
				for (TaskAttribute oldAttribute : oldTaskAttributeList) {
					TaskAttribute newAttribute = (TaskAttribute) oldAttribute
					.clone();
					newAttribute.setTaskID(new_t.getTaskID());
					flowMetaData.insert(newAttribute);
					
				}
			}
			// notes
			else if (uo instanceof Note) {
				Note n = (Note) uo;
				Note new_n = (Note) n.clone();
				//new_n.setXPos(n.getXPos() + 80);
				//new_n.setYPos(n.getYPos() + 80);
//				new_n.setXPos(xPoint);
//				new_n.setYPos(yPoint);
				new_n.setXPos(xPoint + n.getXPos() - taskPos.getXPos());
				new_n.setYPos(yPoint + n.getYPos() - taskPos.getYPos());
				drawNote(new_n);
				cellList.add(new_n);
			}
		}
		
		graph.setSelectionCells(cellList.toArray());
		
		// links
		ConnectionSet cs = gt.getConnectionSet();
		Set edges = cs.getEdges();
		Iterator iter = edges.iterator();
		while (iter.hasNext()) {
			DefaultEdge de = (DefaultEdge) iter.next();
			DefaultPort srcPort = (DefaultPort) de.getSource();
			DefaultGraphCell srcCell = (DefaultGraphCell) srcPort.getParent();
			DefaultPort tgtPort = (DefaultPort) de.getTarget();
			DefaultGraphCell tgtCell = (DefaultGraphCell) tgtPort.getParent();
			DefaultGraphCell newSrcCell = copiedCells.get(srcCell);
			DefaultGraphCell newTgtCell = copiedCells.get(tgtCell);
			if ((newSrcCell != null) && (newTgtCell != null)) {
				DefaultPort newSrcPort = (DefaultPort) newSrcCell.getChildAt(0);
				DefaultPort newTgtPort = (DefaultPort) newTgtCell.getChildAt(0);
				addLink(newSrcPort, newTgtPort);
			}
		}
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
	
	/**
	 * ��ȡ�������Сλ
	 * @param cells
	 * @return
	 */
	private Task getTaskMinPosition(Object[] cells){
		int x = 1000; 
		int y = 1000;
		for (Object oc : cells) {
			DefaultGraphCell cell = (DefaultGraphCell) oc;
			Object uo = cell.getUserObject();
			// tasks
			if (uo instanceof Task) {
				Task t = (Task) uo;
				if (t.getXPos() < x){
					x = t.getXPos();
				}
				if (t.getYPos() < y){
					y = t.getYPos();
				}

			}
			// notes
			else if (uo instanceof Note) {
				Note t = (Note) uo;
				if (t.getXPos() < x){
					x = t.getXPos();
				}
				if (t.getYPos() < y){
					y = t.getYPos();
				}
			}
		}
		Task task = new Task();
		task.setXPos(x);
		task.setYPos(y);
		return task;
	}

	public Object[] getSelectCells() {
		return selectCells;
	}

	public void setSelectCells(Object[] selectCells) {
		this.selectCells = selectCells;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}


	public boolean isEdited() {
		return isEdited;
	}


	public void setEdited(boolean isEdited) {
		this.isEdited = isEdited;
	}

	public void saveTaskflowInfo(String path)
	throws Exception {
		List<Task> taskList = new ArrayList<Task>();
		List<Link> linkList = new ArrayList<Link>();
		List<Note> noteList = new ArrayList<Note>();
		
		Object[] cells = graph.getGraphLayoutCache().getCells(false, true,
				false, false);
		for (int i = 0; i < cells.length; i++) {
			DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
			Object obj = flowGraphCell.getUserObject();
			if (obj instanceof Task) {
				Task task = (Task) obj;
				taskList.add(task);
			} else if (obj instanceof Note) {
				Note nt = (Note) obj;
				noteList.add(nt);
			}
		}
		
		cells = graph.getGraphLayoutCache().getCells(false, false, false, true);
		for (int i = 0; i < cells.length; i++) {
			DefaultEdge flowEdge = (DefaultEdge) cells[i];
			Link link = (Link) flowEdge.getUserObject();
			linkList.add(link);
		}
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

	/**
	 * ���������ͼ�ڵ����Ч��
	 * ͨ�����ڵ���ӽڵ㣬����Ϊ������ͼ�����ȱʧ������ӣ�������Ч��������ͼ��������ʾ
	 * �÷���ֻ����ԱȽ���ͨ�ĺ�����������ӵĺ������綯̬SQL�����������SQL��䣬���������
	 *
	 */
	public void checkDataflow() {
		// TODO Auto-generated method stub
		
	}


	/**
	 * ����������ͼ
	 *ͨ���ڵ�����ΪCOMPRESS�ĺ�����Ϣ����ȡ�������ݿ��е���Դ���Ŀ���
	 */
	public void generateDataflow() {
		
		System.out.println("��������������ͼ...");
		ReportAnalyse reportAnalyse = FlowDesigner.getInstance().getReportAnalyse();
		
		System.out.println("���²���...");
		reportAnalyse.updateTaskFlow(this);
		System.out.println("��������������ͼ...");
			
	}
	
	public void horizontalLayout(){
		
		try{
		List<TaskNode> taskNodeList = new ArrayList<TaskNode>();
		List<NodeLink> nodeLinkList = new ArrayList<NodeLink>();
		HorizontalLayout horizontalLayout = new HorizontalLayout();
//		�������Task����Ϣ
		Object[] cells = graph.getGraphLayoutCache().getCells(false, true,false, false);
		for (int i = 0; i < cells.length; i++) {
			DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
			Object obj = flowGraphCell.getUserObject();
			if (obj instanceof Task) {
				Task task = (Task) obj;
				taskNodeList.add(new TaskNode(""+task.getTaskID(),HorizontalLayout.countWidthByString(task.getDescription())));
			}
		}

		//����������ӵ���Ϣ��
		cells = graph.getGraphLayoutCache().getCells(false, false, false, true);
		for (int i = 0; i < cells.length; i++) {
			DefaultEdge flowEdge = (DefaultEdge) cells[i];
			Link link = (Link) flowEdge.getUserObject();
			nodeLinkList.add(new NodeLink(""+link.getFromTaskID(),""+link.getToTaskID()));
		}
		
//		���²���
		horizontalLayout.setTaskNodeXInterval(50);
		horizontalLayout.setTaskNodeYInterval(70);
		horizontalLayout.setLayout(taskNodeList,nodeLinkList);
		cells = graph.getGraphLayoutCache().getCells(false, true,false, false);
		for (int i = 0; i < cells.length; i++) {
			DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
			Object obj = flowGraphCell.getUserObject();
			if (obj instanceof Task) {
				Task task = (Task) obj;

				//�����ڴ��е�����
				TaskNode taskNode = queryTaskNode(taskNodeList,String.valueOf(task.getTaskID()));
				task.setXPos(taskNode.getX());
				task.setYPos(taskNode.getY());
				//flowGraphCell.ad
				/*//����graph�����꣺
				GraphConstants.setBounds(flowGraphCell.getAttributes(), new Rectangle2D.Double(task.getXPos(),
						task.getYPos(), GraphConstants.getBounds(flowGraphCell.getAttributes()).getWidth() , 
						GraphConstants.getBounds(flowGraphCell.getAttributes()).getHeight()));
				
				//GraphConstants.setOffset(flowGraphCell.getAttributes(), new Point2D.Float(task.getXPos(),task.getYPos()));
				graph.getGraphLayoutCache().reload();
				graph.repaint();*/
			}
		}
		
		//ˢ������ڵ�λ�ã��и�С���������ͼԪ����嶥�������ţ�����д��޸ģ�
		JGraphFacade facade = new JGraphFacade(graph);
		boolean directed = facade.isDirected();
		facade.setDirected(false);
		facade.setIgnoresUnconnectedCells(true);
		facade.setIgnoresCellsInGroups(true);
		facade.setIgnoresHiddenCells(true);
		facade.setDirected(true);
		facade.setLocations(cells, getLocations(cells));
		facade.setDirected(directed);
		Map map = facade.createNestedMap(true, true);
		graph.getGraphLayoutCache().edit(map, null, null, null);
		
		//System.out.println("��������ͼˢ��...");
		}catch(Exception e){
			
		}

	}
	
	
	public double[][] getLocations(Object[] cells) {
		double[][] locations = new double[cells.length][2];
		for (int i = 0; i < cells.length; i++) {
			
			DefaultGraphCell flowGraphCell = (DefaultGraphCell) cells[i];
			Object obj = flowGraphCell.getUserObject();
			Task task = (Task) obj;
			locations[i][0] = task.getXPos();
			locations[i][1] = task.getYPos();
		}
		return locations;
	}
	
	/**
	 * ��ѯTaskNode
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

}
