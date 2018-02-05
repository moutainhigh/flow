package com.aspire.etl.flowmonitor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

import com.aspire.etl.flowmonitor.dialog.TaskSimpleDialog;
import com.aspire.etl.flowmonitor.dialog.TaskflowDialog;
import com.aspire.etl.flowdefine.Link;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.tool.TimeUtils;
import com.aspire.etl.uic.WcpImagePool;

/**
 * 
 * @author wangcaiping
 * @since 2008-4-4
 * 
 * @author 罗奇
 * @since 2008-4-17 1.修改定时刷新功能 2.增加流程启动和停止功能 3.将图片目录从数字改为名字
 */
public class FlowFrame extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	static Logger log;

	private JGraph graph;

	private Taskflow taskflow;

	private TaskflowDialog dlgTaskflow;

	private int iconWidth = 36;

	private int iconHeight = 36;

	private ImageIcon icon = null;

	private JScrollPane sp;

	private HashMap<Integer, DefaultGraphCell> taskIcons;

	public static boolean XMLCONN = false;

	private Xmlrpc xmlrpc;

	private boolean isApplet;
	
	private FlowMonitor mainFrame;
	
//	大纲组ID,初始化大纲组的时候就定的是0.
	private static int outlineGroupId = 0;
	
	private Object[] selectCells ;
	
	
	private JPopupMenu taskPopupMenu = new JPopupMenu();
	
	JPopupMenu  outlinePopupMenu =  new JPopupMenu();
	JPopupMenu  taskflowPopupMenu =  new JPopupMenu();
	
	
	public FlowFrame(FlowMonitor mainFrame,Taskflow tf, TaskflowDialog dlg2, Xmlrpc xmlrpc,
			boolean isApplet) {
		super(null, true, true, true, true);
		
		this.mainFrame = mainFrame;
		
		this.setTitle(getTitle(tf));
		this.xmlrpc = xmlrpc;
		this.isApplet = isApplet;
		log = Logger.getLogger("FlowMonitor");

		setSize(300, 300);
		// variables
		this.taskflow = tf;
		this.dlgTaskflow = dlg2;
		// initialize graph
		initGraph();
		// Update ToolBar based on Selection Changes
		sp = new JScrollPane(graph);
		this.getContentPane().add(sp);
		// meta data
		// this.flowMetaData = fmd;
		this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		//
		taskIcons = new HashMap<Integer, DefaultGraphCell>();
		
		FlowFrame.this.mainFrame.initTaskPopupMenu(FlowFrame.this,taskPopupMenu);
		
		FlowFrame.this.mainFrame.initTaskflowPopupMenu(FlowFrame.this.taskflow,taskflowPopupMenu);
		
		
		FlowFrame.this.mainFrame.initOutlinePopupMenu(FlowFrame.this.taskflow,outlinePopupMenu);
		
	}
	

	public String getTitle(Taskflow tf) {
		
		if (tf.getGroupID() == this.outlineGroupId){
			return tf.toString();
		}
		
		String title = tf.toString() +"  "+ TimeUtils.toChar(tf.getStatTime()) + "  （"
		+ tf.getStepType() + "）";
		
		if (tf.getSuspend() == Taskflow.SUSPEND_YES) {
			title = title + " '禁用'  ";
		} else {
			if (tf.getStatus() == Taskflow.STOPPED) {
				title =  title + " '停止'  ";
						
			} else if (tf.getStatus() == Taskflow.FAILED) {
				title =  title + " '失败' ";
			} else if (tf.getStatus() == Taskflow.QUEUE) {
				title =  title + " '等待'  ";
			} else if (tf.getStatus() == Taskflow.READY) {
				title =  title + " '就绪'  ";
			} else if (tf.getStatus() == Taskflow.RUNNING) {
				title =  title + " '运行'  ";
			} else if (tf.getStatus() == Taskflow.SUCCESSED) {
				title =  title + " '成功'  ";
			} else {
				title =  title + " '未知状态' ";
			}
			
		}
		return title;
	}

	private void initGraph() {
		graph = new JGraph(new MyModel());
		// 阻止单独拖动连线的port
		graph.setDisconnectable(false);
		// 显示网格线
		graph.setGridEnabled(true);
		graph.setGridVisible(true);
		graph.setPortsVisible(true);
		graph.setHandleColor(Color.RED);
		graph.setDragEnabled(false);
		graph.setDropEnabled(false);
		graph.setEditable(false);
		graph.setMoveable(false);
		graph.addMouseListener(new WcpMouseHandler());
		graph.setMarqueeHandler(new MyMarqueeHandler());
	}
	
	

	public void openTaskflow() {
		
		Thread thread = new Thread(){

			public void run(){
				
				try{
					graph.getModel().remove(graph.getGraphLayoutCache().getCells(true, true,true, true));
					// List<Task> taskList = flowMetaData.queryTaskList(taskflow
					// .getTaskflowID());
					List<Task> taskList = null;
					try {
						taskList = Xmlrpc.getInstance().queryTaskList(
								taskflow.getTaskflowID());
					} catch (XmlRpcException e) {
						e.printStackTrace();
					}
					// List<Link> linkList = flowMetaData.queryLinkList(taskflow
					// .getTaskflowID());
					List<Link> linkList = null;
					try {
						linkList = Xmlrpc.getInstance().queryLinkList(
								taskflow.getTaskflowID());
					} catch (XmlRpcException e) {
						e.printStackTrace();
					}
					// tasks
					for (Task task : taskList) {
						DefaultGraphCell c = drawTask(task);
						taskIcons.put(task.getTaskID(), c);
					}
					// links
					for (Link link : linkList) {
						DefaultGraphCell cellFrom = taskIcons.get(link.getFromTaskID());
						DefaultGraphCell cellTo = taskIcons.get(link.getToTaskID());
						drawLink(link, (DefaultPort) cellFrom.getChildAt(0),
								(DefaultPort) cellTo.getChildAt(0));
					}

				}catch (Exception e){
				}

			}
		};
		thread.start();
	}

	public void refreshTaskflow() {
		
		Thread thread = new Thread(){

			public void run(){
				try{
					Date flowStatTime;
					try {
						flowStatTime = taskflow.getStatTime();
						// flowMetaData.loadTaskflowInfo(taskflow.getTaskflowID());
						// taskflow = flowMetaData.queryTaskflow(taskflow.getTaskflowID());
						// Xmlrpc.getInstance().loadTaskflowInfo(taskflow.getTaskflowID());
						taskflow = Xmlrpc.getInstance().queryTaskflow(
								taskflow.getTaskflowID());
						log.debug(taskflow.getTaskflow() + " suspend="
								+ taskflow.getSuspend() + " status=" + taskflow.getStatus());
					} catch (XmlRpcException e) {
						log.error(e);
						return;
					}
					graph.clearSelection();
					Iterator iter = taskIcons.entrySet().iterator();
					while (iter.hasNext()) {
						Entry ent = (Entry) iter.next();
						DefaultGraphCell cell = (DefaultGraphCell) ent.getValue();
						//Task old_t = (Task) cell.getUserObject();
						int taskId = Integer.parseInt(ent.getKey().toString());
						// Task task = flowMetaData.queryTask(taskId);
						Task task = null;
						try {
							task = Xmlrpc.getInstance().queryTask(taskId);
						} catch (XmlRpcException e) {
							log.error(e);
							return;
						}
						// if (old_t.getStatus() != task.getStatus()) {

						setTaskIcon(cell, task);
						cell.setUserObject(task);
						if (null != flowStatTime) {
							if (taskflow.getStatTime().compareTo(flowStatTime) != 0) {
								FlowFrame.this.setTitle(getTitle(taskflow));
							}
						}

						// }
					}
					graph.getGraphLayoutCache().reload();
					graph.repaint();

				}catch(Exception ee){}
			}
		};
		thread.start();
			
	}

	private void setTaskIcon(DefaultGraphCell cell, Task task) throws Exception {

		String iconName = task.getTaskType().toLowerCase();

		String strDir = "plugins/";
		if (isApplet) {
			strDir = System.getProperty("java.io.tmpdir")
					+ "etl\\FlowMonitor\\" + strDir;
		}

		String url1 = "images/" + task.getTaskType().toLowerCase() + "/";
		String url2 = strDir + task.getTaskType().toLowerCase() + "/images/";
		String url3 = "images/default/";

		if (task.getSuspend() == Task.SUSPEND_YES) {
			getIcon(url1, url2, url3 + "suspend.png", iconName + "_suspend.png");
		} else if (task.getSuspend() == Task.SUSPEND_NO) {
			if (task.getStatus() == Task.SUCCESSED) {
				getIcon(url1, url2, url3 + "successed.png", iconName
						+ "_successed.png");
			} else if (task.getStatus() == Task.RUNNING) {
				getIcon(url1, url2, url3 + "running.png", iconName
						+ "_running.png");
			} else if (task.getStatus() == Task.READY) {
				getIcon(url1, url2, url3 + "ready.png", iconName + "_ready.png");
			} else if (task.getStatus() == Task.FAILED) {
				getIcon(url1, url2, url3 + "failed.png", iconName
						+ "_failed.png");
			} else if (task.getStatus() == Task.QUEUE) {
				getIcon(url1, url2, url3 + "queue.png", iconName + "_queue.png");
			} else if (task.getStatus() == Task.STOPPED) {
				getIcon(url1, url2, url3 + "stopped.png", iconName
						+ "_stopped.png");
			}
		}

		if (icon != null) {
			GraphConstants.setIcon(cell.getAttributes(), icon);
		}
	}

	private void getIcon(String url1, String url2, String path, String name) {
		icon = WcpImagePool.getIcon(this.getClass(), url1 + name);
		
		if (icon == null) {
			icon = WcpImagePool.getIconOfPath(url2 + name);
			
			if (icon == null) {
				icon = WcpImagePool.getIcon(this.getClass(), path);
				
			}
		}
		// System.err.println(url1);
		// System.err.println(url2);
		// System.err.println(path);
		// System.err.println(name);
		// System.err.println(icon==null);
	}

	private DefaultGraphCell drawTask(Task task) throws Exception {
		// Create vertex with the given name
		DefaultGraphCell cell = new DefaultGraphCell(task);
		AttributeMap am = cell.getAttributes();
		// Set bounds
		GraphConstants.setBounds(am, new Rectangle2D.Double(task.getXPos(),
				task.getYPos(), iconWidth, iconHeight));
		setTaskIcon(cell, task);
		// 设置透明
		GraphConstants.setOpaque(am, false);
		// 让节点图标自动大小，已便于显示长的label。
		// 更完善的方案是，定制view，图标和label分开
		// GraphConstants.setAutoSize(am, true);
		GraphConstants.setResize(am, true);

		// 阻止graph打开缺省的cell编辑框
		GraphConstants.setEditable(am, false);
		// Add a Floating Port
		cell.addPort();
		graph.getGraphLayoutCache().insert(cell);
		
		return cell;
	}

	private void drawLink(Link link, DefaultPort source, DefaultPort target) throws Exception{

		// Construct Edge with no label
		DefaultEdge edge = new DefaultEdge(link);
		edge.setSource(source);
		edge.setTarget(target);
		GraphConstants.setLineEnd(edge.getAttributes(),
				GraphConstants.ARROW_CLASSIC);
		GraphConstants.setEditable(edge.getAttributes(), false);
		GraphConstants.setConnectable(edge.getAttributes(), false);
		graph.getGraphLayoutCache().insertEdge(edge, source, target);
	}

	public void edit() throws Exception {
		Task task = getSelectedTask();
		if (task != null) {
			if (xmlrpc.queryTaskflow(task.getTaskflowID()).getGroupID() ==outlineGroupId){
				
				mainFrame.openTaskflow(xmlrpc.queryTaskflow(task.getTask()));
			} else {
				TaskSimpleDialog dialog = new TaskSimpleDialog(mainFrame);
				dialog.loadValue(taskflow,task);
				dialog.setLocationRelativeTo(mainFrame);
				dialog.setVisible(true);
			}
			// flowMetaData.queryTask(task.getTaskID());
			// dlgTask.loadValue(task);
			// dlgTask.setVisible(true);
		} else {
			// 刷新内存
			// flowMetaData.loadTaskflowInfo(taskflow.getTaskflowID());
			// 获取最新taskflow
			// taskflow = flowMetaData.queryTaskflow(taskflow.getTaskflowID());
			// Xmlrpc.getInstance().loadTaskflowInfo(taskflow.getTaskflowID());
			if (taskflow.getGroupID() != outlineGroupId && xmlrpc.isPermit("MONITOR_EDIT")) {
				taskflow = Xmlrpc.getInstance().queryTaskflow(
						taskflow.getTaskflowID());
				log.debug("status:" + taskflow.getStatus());
				dlgTaskflow.loadValue(taskflow);
				dlgTaskflow.setVisible(true);
				this.mainFrame.refreshTaskflow();
			}
		}
	}

	private Task getSelectedTask() {
		if (graph.isSelectionEmpty()) {
			return null;
		} else {
			// edit task
			Object obj = graph.getSelectionCell();
			if ((obj != null) && (obj instanceof DefaultGraphCell)) {
				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object uo = cell.getUserObject();
				if (uo instanceof Task) {
					return (Task) uo;
				}
			}
		}
		return null;
	}

	// class to handle mouse double click
	private class WcpMouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			if ((me.getButton() == MouseEvent.BUTTON1)
					&& (me.getClickCount() >= 2)) {
				try {
					edit();
				} catch (MetaDataException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					//双击之后，graph的指针被修改成十字形指针，所以要修改成默认
					graph.setCursor(Cursor.getDefaultCursor());
				}
			}
		}

		public void mouseReleased(MouseEvent me) {
			Object[] cells = graph.getSelectionCells();
			if (cells != null)
				for (Object cell : cells) {
					if (!(cell instanceof DefaultEdge)
							&& cell instanceof DefaultGraphCell) {
						// 更新任务节点的位置信息
						Rectangle2D rect = graph.getCellBounds(cell);
						DefaultGraphCell flowGraphCell = (DefaultGraphCell) cell;
						Task task = (Task) flowGraphCell.getUserObject();
						task.setXPos((int) rect.getX());
						task.setYPos((int) rect.getY());
					}
				}
		}
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
	
	public static int countWidthByString(String taskName){
		int iWidth=100;
		int taskNameLength = taskName.getBytes().length;
		iWidth = taskNameLength*8 <100?iWidth:taskNameLength*8 ;		
		return iWidth;
	}
	
	
//	 MarqueeHandler that Connects Vertices and Displays PopupMenus
	private class MyMarqueeHandler extends BasicMarqueeHandler {

		private boolean leftClicked = false;
		// Holds the Start and the Current Point
		
		@SuppressWarnings("unused")
		private Point2D start, current;

		// Holds the First and the Current Port
		private PortView port, firstPort;

		private Cursor handCursor;
		
		private int x,y;

		public MyMarqueeHandler() {
			handCursor = new Cursor(Cursor.HAND_CURSOR);
		}

//		 Override to Gain Control (for PopupMenu and ConnectMode)
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

			try{
				// If Right Mouse Button

				//用于右键复制功能而临时保存当前选定的对象，因为弹出右键之后，选取的对象被丢失了
				selectCells = graph.getSelectionCells();
				if (SwingUtilities.isLeftMouseButton(e)) {
					leftClicked = true;
				}
				if (port != null && graph.isPortsVisible()) {
					// Remember Start Location
					start = graph.toScreen(port.getLocation());
					// Remember First Port
					firstPort = port;
				} else {
					// Call Superclass
					super.mousePressed(e);
				}
			}catch(Exception ee){}
		}

		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				x = e.getX();
				y = e.getY();
				//解决为了直接右键点击弹出popup菜单时，不能正确得到当前选择的对象，而延时了100毫秒，并重新取得当前选择的对象
				Thread thread = new Thread(){
					@SuppressWarnings("static-access")
					public void run(){
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if (!leftClicked) {
							selectCells = graph.getSelectionCells();
						}
						
						if (selectCells!= null && selectCells.length > 0 && graph.getSelectionCells().length > 0){
							DefaultGraphCell flowGraphCell = (DefaultGraphCell)graph.getSelectionCells()[0];
							Object obj = flowGraphCell.getUserObject();
							if (obj instanceof Task) {
								/*Task task = (Task)obj; //做了以下限制就无法对多个Task进行操作了*/
								taskPopupMenu.show(graph, x, y);
							}
						} else {
							if(FlowFrame.this.taskflow.getGroupID() != FlowFrame.this.outlineGroupId ){
								taskflowPopupMenu.show(graph, x, y);
							} else {
								outlinePopupMenu.show(graph, x, y);
							}

						}
				
						//最后重设置点击鼠标左键的状态为false;
						leftClicked = false;
					}
				};
				thread.start();
			} 
			
			
			// If Valid Event, Current and First Port
			if (e != null && port != null && firstPort != null
					&& firstPort != port) {
				// Then Establish Connection
				e.consume();
				// Else Re the Graph
			} else{
			try{
				graph.repaint();
			}catch(Exception ee){}
				
			}
			// Reset Global Vars
			firstPort = port = null;
			start = current = null;

			// Call Superclass
			super.mouseReleased(e);

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
					try{
						paintConnector(Color.black, graph.getBackground(), g);
					}catch(Exception ee){}
					// If Port was found then Point to Port Location
					port = newPort;
					if (port != null)
						current = graph.toScreen(port.getLocation());
					// no Port was found then Point to Mouse Location
					else
						current = graph.snap(e.getPoint());
					// Xor-Paint the new Connector
					try{
						paintConnector(graph.getBackground(), Color.black, g);
					}catch(Exception ee){}
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


		// Show Special Cursor if Over Port
		public void mouseMoved(MouseEvent e) {
			
			//用于任务节点复制
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
			//不做画线处理
			/*// Set Foreground
			g.setColor(fg);
			// Set Xor-Mode Color
			g.setXORMode(bg);
			// Highlight the Current Port
			paintPort(graph.getGraphics());
			// If Valid First Port, Start and Current Point
			if (firstPort != null && start != null && current != null)
				// Then Draw A Line From Start to Current Point
				g.drawLine((int) start.getX(), (int) start.getY(),
						(int) current.getX(), (int) current.getY());*/
		}
/*
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
		}*/
	}

	
	
	public Taskflow getTaskflow() {
		return taskflow;
	}

	public JGraph getGraph() {
		return graph;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public JScrollPane getSp() {
		return sp;
	}

	public void setSp(JScrollPane sp) {
		this.sp = sp;
	}

	public void setGraph(JGraph graph) {
		this.graph = graph;
	}

	public void setTaskflow(Taskflow taskflow) {
		this.taskflow = taskflow;
	}


	public JPopupMenu getTaskPopupMenu() {
		return taskPopupMenu;
	}


	public Object[] getSelectCells() {
		return selectCells;
	}

}
