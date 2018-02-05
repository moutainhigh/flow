package com.aspire.etl.flowmonitor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.jgraph.graph.DefaultGraphCell;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowdefine.TaskflowGroup;
import com.aspire.etl.flowmonitor.CheckButtonTree.ButtonRenderer;
import com.aspire.etl.flowmonitor.CheckButtonTree.NodeSelectionListener;
import com.aspire.etl.flowmonitor.beans.WarnBean;
import com.aspire.etl.flowmonitor.dao.FlowDao;
import com.aspire.etl.flowmonitor.dialog.ConsoleDialog;
import com.aspire.etl.flowmonitor.dialog.LogDialog;
import com.aspire.etl.flowmonitor.dialog.MonitorLogonDialog;
import com.aspire.etl.flowmonitor.dialog.TaskFlowRedoDialog;
import com.aspire.etl.flowmonitor.dialog.TaskSimpleDialog;
import com.aspire.etl.flowmonitor.dialog.TaskflowDialog;
import com.aspire.etl.flowmonitor.dialog.WarnDialog;
import com.aspire.etl.flowmonitor.table_sort.SortManager;
import com.aspire.etl.tool.Utils;
import com.aspire.etl.uic.WcpAction;
import com.aspire.etl.uic.WcpImagePool;
import com.aspire.etl.uic.WcpMessageBox;

/**
 * 
 * @author wangcaiping
 * @since 2008-4-4
 * 
 * @author 罗奇
 * @since 2008-4-17 1.修改定时刷新功能 2.增加流程启动和停止功能
 * 
 * @author jiangts
 * @since 2019-01-19 做了以下修改：
 * 				1.initTree()方法，图标绘制逻辑修改：如果是TaskflowGroup，则用包图标，Taskflow使用流程图标
 * 			    2.initTreeModel（）函数 对jtree进行了排序处理
 * 	
 */
public class FlowMonitor extends JFrame {
	/**
	 * 
	 */
	private static Logger log;

	private static final long serialVersionUID = 1L;

	// public static void main(String[] args)
	// {
	// FlowMonitor fm = FlowMonitor.getInstance();
	// }

	private BufferedReader socketReader;

	private PrintWriter socketWriter;

	// 引擎IP
	protected String hostIp = "10.1.3.194";

	// 引擎端口
	protected int hostPort = 8080;

	private String msg = "test";
	

	private final int FRM_WIDTH = 880;

	private final int FRM_HEIGHT = 660;

	private FlowDao dao;

	private JTable table;

	private DefaultTableModel model;

	private JTree tree;

	@SuppressWarnings("unused")
	private SortManager sort;

	private DefaultMutableTreeNode root_node = new DefaultMutableTreeNode("");

	private DefaultMutableTreeNode operation_node;

	private DefaultMutableTreeNode flow_node;

	private DefaultTreeModel treeModel;

	private DefaultTreeCellRenderer renderer;

	private JMenuItem log_config;
	
	private JMenuItem warn_config;

	//private JMenuItem flow_list;

	private JMenu config_menu;
	
	private JMenu windows_menu;

	private JMenuBar bar;

	// 右键菜单
	private JMenuItem left;

	private JMenuItem right;

	private JMenuItem view;

	private JPopupMenu popup = new JPopupMenu();

	private JPopupMenu leftTreePopupMenu = new JPopupMenu();
	
	// 流程组
	private JMenuItem runG;

	private JMenuItem stopG;

	private JPopupMenu popupG = new JPopupMenu();
	
	private JPopupMenu outlinelistPopup = new JPopupMenu();

	private JDesktopPane desktop;

	public static JButton refreshJB = new JButton();
	public static JButton startJB = new JButton();
	public static JButton stopJB = new JButton();

	private String refreshTitle = "刷新";
	private String startTitle = "启动流程";
	private String stopTitle = "停止流程";
	private String suspendTitle = "禁用流程";
	private String cancelSuspendTitle = "启用流程";
	private String startAllTitle = "启动所有流程";
	private String stopAllTitle = "停止所有流程";
	private String suspendAllTitle = "禁用所有流程";
	private String cancelSuspendAllTitle = "启用所有流程";
	private String redoTitle = "重做流程";
	private String wakeupTitle = "唤醒流程";
	private String skipTaskflowTitle = "跳过当前选定流程";
	private String skipTaskflow2Title = "跳过当前流程";
	private String skipTaskTitle = "跳过当前选定任务";
	private String suspendTaskTitle = "禁用当前选定任务";
	private String cancelSuspendTaskTitle = "启用当前选定任务";
	private String taskAttributeTitle = "打开任务属性";
	
	private TaskflowDialog dlgTaskflow;

	private final int refreshInterval = 5;

	JCheckBox autoRefreshCheckBox = new JCheckBox();

	private Timer timer;
	
	private JPopupMenu warnpopup;
	
	private JScrollPane warnscroll;
	
	private JTextArea warnarea;
	
	private WarnDialog warnDialog;
	
	JButton outlineListBtn = new JButton("大纲列表");

	JScrollPane sp1;
	JScrollPane sp2;

	JSplitPane cp;
	JSplitPane mp;

	JTextField refreshIntervalTextField = new JTextField(3);

	Container cont = null;

	// private static FlowMonitor monitor;
	//	
	//		
	// public static FlowMonitor getInstance()
	// {
	// if (monitor == null)
	// {
	// monitor = new FlowMonitor();
	// }
	// return monitor;
	// }

	private Xmlrpc xmlrpc;

	private LogDialog logConfig;

	private boolean isApplet;
	
	Taskflow taskflow;
	
//	大纲组ID,初始化大纲组的时候就定的是0.
	private static int outlineGroupId = 0;
	
	TaskFlowRedoDialog redoDialog = new TaskFlowRedoDialog(FlowMonitor.this,true);
	   
    public static void main(String[] args) {
    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
    	    public void run() {
    		JFrame.setDefaultLookAndFeelDecorated(true);
    		//new FlowMonitor(false);
    		new FlowMonitor("127.0.0.1","4040","admin","admin");
    	    }
    	});
    }

	public FlowMonitor(String ip,String port,String user,String password) {
		super();
		MonitorLogonDialog dlg = new MonitorLogonDialog();
		dlg.setIP(ip);
		dlg.setPort(port);
		dlg.setUser(user);
		dlg.setPassword(password);
		
		if (dlg.showDialog() == MonitorLogonDialog.SUCCESS_RESULT){
			init(isApplet);
			this.setVisible(true);
			
		}else {
			this.dispose();
		}
	}
	
	public FlowMonitor(boolean isApplet) {
		super();
		MonitorLogonDialog dlg = new MonitorLogonDialog();
		
		if (dlg.showDialog() == MonitorLogonDialog.SUCCESS_RESULT){
			init(isApplet);
			this.setVisible(true);
		}
	}
	
	private void init(boolean isApplet){
		this.isApplet = isApplet;
		
//		 初始化日志
		if (this.isApplet) {
			
			String strDir = System.getProperty("java.io.tmpdir")
					+ "etl\\FlowMonitor\\log";
			File f = new File(strDir);
			f.mkdir();
			log = Utils.initFileLog("FlowMonitor", strDir + "\\",
					"FlowMonitor", "ALL");
		} else {
			log = Utils.initFileLog("FlowMonitor", "./log/", "FlowMonitor",
					"ALL");
		}
		
		this.xmlrpc = Xmlrpc.getInstance();
		
		this.setTitle("ETL流程监控器   " + Xmlrpc.USERNAME + "@" + Xmlrpc.IP + ":"
				+ Xmlrpc.PORT);
		
		this.dao = new FlowDao();
		
		this.logConfig = new LogDialog(this, this, dao);
		this.warnDialog = new WarnDialog(this, this);
		this.setSize(FRM_WIDTH, FRM_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		desktop = new JDesktopPane();
		cont = this.getContentPane();
		cont.setLayout(new BorderLayout());

		// 得到引擎IP和端口
		// this.initFlowMetaDate();
		// hostIp = flowMetaData.querySysConfigValue("SYS_COMPUTER_IP");
		// hostPort =
		// Integer.parseInt(flowMetaData.querySysConfigValue("SYS_SOCKET_PORT"));

		// toolbar
		JToolBar jb = createToolBar();
		for(int i = 0; i < jb.getComponentCount(); i++){
			Object obj = jb.getComponent(i);
			if (obj  instanceof JButton){
				JButton button =  (JButton) obj;
				if (button.getToolTipText().indexOf(refreshTitle) > -1){
					refreshJB = button;
				} else if (button.getToolTipText().indexOf(stopTitle) > -1){
					stopJB = button;
				} else if (button.getToolTipText().indexOf(startTitle) > -1){
					startJB = button;
				} 
			}
		}
		
		// 得到刷新启动停止按钮对象
		
		cont.add(jb, BorderLayout.PAGE_START);

		// menubar

		bar = new JMenuBar();
		config_menu = new JMenu("信息查询");
		// config = new JMenuItem("引擎配置");
		log_config = new JMenuItem("日志查询");
		warn_config = new JMenuItem("告警查询");
		// config.addMouseListener(new ConfigMenuListener());
		log_config.addMouseListener(new LogConfigMenuListener());
		warn_config.addMouseListener(new WarnConfigMenuListener());
		//flow_list.addMouseListener(new FlowListListener());
		// config_menu.add(config);
		config_menu.add(log_config);
		config_menu.add(warn_config);
		//config_menu.add(flow_list);
		//bar.add(config_menu);
	/*	windows_menu = new JMenu("窗口");
		bar.add(windows_menu);*/
		//this.setJMenuBar(bar);
		//tree = new JTree();
		//initTreeModel();	
		//must();
		initTree();
		timer = new Timer(refreshInterval * 1000, new AutoRefreshHandler());
		warnpopup = new JPopupMenu();
		warnarea = new JTextArea(8,30);
		warnarea.setEditable(false);
		warnarea.setLineWrap(true);
		warnscroll = new JScrollPane();
		warnscroll.setViewportView(warnarea);		
		warnpopup.add(warnscroll);
		table.addMouseListener(new TableListener());
		table.addMouseListener(new FrameMouseListener());
		this.addMouseListener(new FrameMouseListener());
		//sp1.addMouseListener(new FrameMouseListener());
		sp2.addMouseListener(new FrameMouseListener());
		desktop.addMouseListener(new FrameMouseListener());
		cp.addMouseListener(new FrameMouseListener());
		mp.addMouseListener(new FrameMouseListener());
		cont.addMouseListener(new FrameMouseListener());
		
		/*this.addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent e) {
				System.out.println("退出窗口");
				FlowMonitor.this.dispose();
			}
		});*/
		
		JMenuItem refreshMenu  = new JMenuItem(new RefreshAction());
		refreshMenu.getAction().setEnabled(true);
		leftTreePopupMenu.add(refreshMenu);
		
		//最大化窗体
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		initTable();
		
		refreshOutlineList();
		
		int newInteval = Integer.parseInt(refreshIntervalTextField
				.getText());
		timer.setDelay(newInteval * 1000);
		timer.start();
		
	}

	@SuppressWarnings("serial")
	public void initTree() {
		
		try {
			List<Taskflow> aTaskflowList = Xmlrpc.getInstance()
			.queryTaskflowInGroup(this.outlineGroupId);

			Collections.sort(aTaskflowList);
			for (Iterator iter = aTaskflowList.iterator(); iter.hasNext();) {
				Taskflow aTaskflow = (Taskflow) iter.next();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
				}
				openTaskflow(aTaskflow);
			}

		} catch (XmlRpcException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		// tree
		//sp1 = new JScrollPane(tree);
		cp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp1, desktop);
		cp.setDividerLocation((int) (FRM_WIDTH / 8));
		//cp.setDividerLocation((int) (1 * FRM_HEIGHT / 100));
		cp.setOneTouchExpandable(true);
		// log list
		table = new JTable();
		// this.initTable();
		sp2 = new JScrollPane(table);
		mp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, desktop, sp2);
		mp.setDividerLocation((int) (99 * FRM_HEIGHT / 100));
		mp.setOneTouchExpandable(true);
		cont.add(mp, BorderLayout.CENTER);
		
		JDialog.setDefaultLookAndFeelDecorated(true);
		dlgTaskflow = new TaskflowDialog(this);
		dlgTaskflow.setLocationRelativeTo(this);
		cont.repaint();
		this.repaint();
		this.setVisible(false);
		this.setVisible(true);
		// timer
		// timer = new Timer(refreshInterval * 1000, new AutoRefreshHandler());
	}
	
	private void initPopupMenu()
	{
		popup.removeAll();
		view = new JMenuItem("查看数据库日志");
		// addButton(tb, new StartFlowAction());
		view.addActionListener(new LogAction());
		// left.addMouseListener(new PopupMenuListener());
		try {
			if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
				left = new JMenuItem("启动流程");
				right = new JMenuItem("停止流程");
				left.addActionListener(new StartFlowAction());
				right.addActionListener(new StopFlowAction());
				if (startJB.isEnabled())
					popup.add(left);
				else if(stopJB.isEnabled())
					popup.add(right);
			}
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.error("initPopupMenu,出错：",e);
		}
		popup.add(view);
	}
	
	private void initPopupG()
	{
		popupG.removeAll();
		runG = new JMenuItem("启动流程组");
		stopG = new JMenuItem("停止流程组");
		runG.addActionListener(new StartGroupAction());
		stopG.addActionListener(new StopGroupAction());
		if (startJB.isEnabled())
			popupG.add(runG);
		else if(stopJB.isEnabled())
			popupG.add(stopG);	
	}
	

	public void initTaskflowPopupMenu(Taskflow taskflow,JPopupMenu popupMenu)
	{
		popupMenu.removeAll();
		// left.addMouseListener(new PopupMenuListener());
		try {
			if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
				JMenuItem startMenu = new JMenuItem(new StartFlowAction());
				startMenu.getAction().setEnabled(true);
				popupMenu.add(startMenu);
				
				JMenuItem stopMenu  = new JMenuItem(new StopFlowAction());
				stopMenu.getAction().setEnabled(true);
				popupMenu.add(stopMenu);
				
				JMenuItem redoMenu  = new JMenuItem(new RedoFlowAction());
				redoMenu.getAction().setEnabled(true);
				popupMenu.add(redoMenu);
				
				JMenuItem wakeupMenu  = new JMenuItem(new WakeupFlowAction());
				wakeupMenu.getAction().setEnabled(true);
				popupMenu.add(wakeupMenu);
				
				popupMenu.addSeparator();
				JMenuItem suspendMenu  = new JMenuItem(new SuspendFlowAction());
				stopMenu.getAction().setEnabled(true);
				popupMenu.add(suspendMenu);
				JMenuItem cancelSuspendMenu  = new JMenuItem(new CancelSuspendFlowAction());
				stopMenu.getAction().setEnabled(true);
				popupMenu.add(cancelSuspendMenu);
				
				/*popupMenu.addSeparator();
				JMenuItem skipMenu  = new JMenuItem(new SkipFlowAction());
				skipMenu.getAction().setEnabled(true);
				popupMenu.add(skipMenu);*/
				
				
				popupMenu.addSeparator();
				JMenuItem taskLogMenu  = new JMenuItem(new LogAction());
				taskLogMenu.getAction().setEnabled(true);
				popupMenu.add(taskLogMenu);
				
				JMenuItem refreshMenu  = new JMenuItem(new RefreshTaskFlowAction());
				refreshMenu.getAction().setEnabled(true);
				popupMenu.add(refreshMenu);
				
			}
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.error("getTaskflowPopupMenu,出错：",e);
		}
	}
	
	public void initOutlinePopupMenu(Taskflow taskflow,JPopupMenu popupMenu)
	{
		popupMenu.removeAll();
		// left.addMouseListener(new PopupMenuListener());
		try {
			if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
				JMenuItem startMenu = new JMenuItem(new StartAllFlowAction());
				startMenu.getAction().setEnabled(true);
				popupMenu.add(startMenu);
				
				JMenuItem stopMenu  = new JMenuItem(new StopAllFlowAction());
				stopMenu.getAction().setEnabled(true);
				popupMenu.add(stopMenu);
				
				popupMenu.addSeparator();
				
				JMenuItem suspendAllMenu  = new JMenuItem(new SuspendAllFlowAction());
				stopMenu.getAction().setEnabled(true);
				popupMenu.add(suspendAllMenu);
				JMenuItem cancelSuspendAllMenu  = new JMenuItem(new CancelSuspendAllFlowAction());
				stopMenu.getAction().setEnabled(true);
				popupMenu.add(cancelSuspendAllMenu);
				
				popupMenu.addSeparator();
				JMenuItem refreshMenu  = new JMenuItem(new RefreshTaskFlowAction());
				refreshMenu.getAction().setEnabled(true);
				popupMenu.add(refreshMenu);
				
			}
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.error("getTaskflowPopupMenu,出错：",e);
		}
	}
	
	/**
	 * 初始化任务的弹出菜单，分大纲的任务和普通流程的任务菜单，大纲任务为流程控制的菜单，普通的任务，则弹出跳过任务的菜单
	 * @param flowframe
	 * @param popupMenu
	 */
	public void initTaskPopupMenu(FlowFrame flowframe,JPopupMenu popupMenu){
		popupMenu.removeAll();
		try {
			if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
				
				if (flowframe.getTaskflow().getGroupID() == outlineGroupId){//大纲任务节点弹出流程控制对话框
					JMenuItem startMenu = new JMenuItem(startTitle);
					startMenu.addActionListener(new OutlineTaskAction(flowframe.getSelectCells(),startTitle));
					popupMenu.add(startMenu);
					
					JMenuItem stopMenu  = new JMenuItem(stopTitle);
					stopMenu.addActionListener(new OutlineTaskAction(flowframe.getSelectCells(),stopTitle));
					popupMenu.add(stopMenu);
					
					JMenuItem wakeupMenu  = new JMenuItem(wakeupTitle);
					wakeupMenu.addActionListener(new OutlineTaskAction(flowframe.getSelectCells(),wakeupTitle));
					popupMenu.add(wakeupMenu);
					
					popupMenu.addSeparator();
					JMenuItem suspendMenu  = new JMenuItem(suspendTitle);
					suspendMenu.addActionListener(new OutlineTaskAction(flowframe.getSelectCells(),suspendTitle));
					popupMenu.add(suspendMenu);

					JMenuItem cancelSuspendMenu  = new JMenuItem(cancelSuspendTitle);
					cancelSuspendMenu.addActionListener(new OutlineTaskAction(flowframe.getSelectCells(),cancelSuspendTitle));
					popupMenu.add(cancelSuspendMenu);
					/*popupMenu.addSeparator();
					JMenuItem skipMenu  = new JMenuItem(skipTaskflowTitle);
					skipMenu.addActionListener(new OutlineTaskAction(flowframe.getSelectCells(),skipTaskflowTitle));
					popupMenu.add(skipMenu);*/
				} else { //普通流程，则弹出
					JMenuItem skipMenu  = new JMenuItem(skipTaskTitle);
					skipMenu.addActionListener(new TaskAction(flowframe.getSelectCells(),skipTaskTitle));
					popupMenu.add(skipMenu);
					
					popupMenu.addSeparator();
					JMenuItem suspendMenu  = new JMenuItem(suspendTaskTitle);
					suspendMenu.addActionListener(new TaskAction(flowframe.getSelectCells(),suspendTaskTitle));
					popupMenu.add(suspendMenu);

					JMenuItem cancelSuspendMenu  = new JMenuItem(cancelSuspendTaskTitle);
					cancelSuspendMenu.addActionListener(new TaskAction(flowframe.getSelectCells(),cancelSuspendTaskTitle));
					popupMenu.add(cancelSuspendMenu);
					
					popupMenu.addSeparator();
					
					JMenuItem taskLogMenu  = new JMenuItem(new LogAction());
					taskLogMenu.getAction().setEnabled(true);
					popupMenu.add(taskLogMenu);
					
					JMenuItem taskAttributeMenu  = new JMenuItem(taskAttributeTitle);
					taskAttributeMenu.addActionListener(new TaskAction(flowframe.getSelectCells(),taskAttributeTitle));
					popupMenu.add(taskAttributeMenu);
					
					
				}
				
				popupMenu.addSeparator();
				JMenuItem refreshMenu  = new JMenuItem(new RefreshTaskFlowAction());
				refreshMenu.getAction().setEnabled(true);
				popupMenu.add(refreshMenu);
				
			}
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.error("getTaskflowPopupMenu,出错：",e);
		}
		
	}
	
	private void refreshOutlineList(){
		
		List<JMenuItem> menuItems = new ArrayList<JMenuItem>();
		
		try {
		List<Taskflow> aTaskflowList = Xmlrpc.getInstance()
			.queryTaskflowInGroup(this.outlineGroupId);
		
		Collections.sort(aTaskflowList);
		for (Iterator iter = aTaskflowList.iterator(); iter.hasNext();) {
			Taskflow aTaskflow = (Taskflow) iter.next();


			JMenuItem menuItem = new JMenuItem(aTaskflow.toString());
			menuItem.addActionListener(new OpenTaskflowAction(aTaskflow.getTaskflow()));

			menuItems.add(menuItem);
		}
		
		outlinelistPopup.removeAll();
		
		for(JMenuItem item : menuItems){
			outlinelistPopup.add(item);
		}
		
		} catch (XmlRpcException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void initTreeModel() {

		// renderer = new DefaultTreeCellRenderer();

		// XmlConfig config = new XmlConfig("./cfg/FlowMonitor.xml");

		try {
			// String metadataConfigPath = config
			// .readSingelNodeValue("//config/metadataConfigFile");
			// // 初始化元数据
			// if (metadataConfigPath.equals("")) {
			// FlowMetaData.init();
			// } else {
			// FlowMetaData.init(metadataConfigPath);
			// }
			// flowMetaData = FlowMetaData.getInstance();
			// flowMetaData.loadBasicInfo();
			// flowMetaData.loadAllTaskflowInfo();

			// List<TaskflowGroup> aGroupList =
			// flowMetaData.queryTaskflowGroupList();
			List<TaskflowGroup> aGroupList = Xmlrpc.getInstance()
					.queryTaskflowGroupList();
			
			//按groupID进行排序
			Collections.sort(aGroupList,new ComparatorTaskflowGroup());
			
			for (Iterator taskflowGroup = aGroupList.iterator(); taskflowGroup
					.hasNext();) {
				TaskflowGroup aGroup = (TaskflowGroup) taskflowGroup.next();
				
				if (aGroup.getGroupID() != outlineGroupId){
					continue;
				}
				aGroup.setGroupName("流程大纲");
				operation_node = new DefaultMutableTreeNode(aGroup);
				
				// List <Taskflow> aTaskflowList =
				// flowMetaData.queryTaskflowInGroup(aGroup.getGroupID());
				List<Taskflow> aTaskflowList = Xmlrpc.getInstance()
						.queryTaskflowInGroup(aGroup.getGroupID());
				
				Collections.sort(aTaskflowList);
				for (Iterator iter = aTaskflowList.iterator(); iter.hasNext();) {
					Taskflow aFlow = (Taskflow) iter.next();
					flow_node = new DefaultMutableTreeNode(aFlow);
					operation_node.add(flow_node);
					// 第三级的任务去掉
					// List <Task> aTaskList =
					// flowMetaData.queryTaskList(aFlow.getTaskflowID());
					// for (Iterator iterator = aTaskList.iterator();
					// iterator.hasNext();) {
					// Task aTask = (Task) iterator.next();
					// String aTask_name = aTask.getTask();
					// assignment_node = new ButtonNode(aTask_name);
					// flow_node.add(assignment_node);
					// }
				}
				root_node.add(operation_node);
			}
			treeModel = new DefaultTreeModel(root_node);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("initTreeModel,出错：",e);
		}
		tree.setModel(treeModel);		
		
	}
	
	 class ComparatorTaskflowGroup implements Comparator{
	    	public int compare(Object arg0, Object arg1) {
	    		TaskflowGroup t0=(TaskflowGroup)arg0;
	    		TaskflowGroup t1=(TaskflowGroup)arg1;
	    		int flag= new Long(t0.getGroupID())
                .compareTo(new Long(t1.getGroupID()));
	    		return flag;
	    	}
	    }
	
	private void must()
	{
		tree.setRootVisible(false);

		// DefaultTreeModel mode = new DefaultTreeModel(root_node);
		// tree.setModel(mode);

		tree.setCellRenderer(new ButtonRenderer());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.addMouseListener(new NodeSelectionListener(tree));
		tree.expandRow(1);// 技巧,不能对调。
		tree.expandRow(0);
	}

	private JToolBar createToolBar() {
		JToolBar tb = new JToolBar();
		/*addButton(tb, new ConsoleAction());
		tb.addSeparator();*/
		/*
		 addButton(tb, new RefreshAction());
		tb.addSeparator();
		addButton(tb, new StopFlowAction());
		tb.addSeparator();
		addButton(tb, new StartFlowAction());
		*/
		/*WcpAction ara = new AutoRefreshAction();
		
		JToggleButton autoRef = new JToggleButton(ara);
		autoRef.setToolTipText(ara.getText());
		autoRef.setText(null);*/
		// tb.add(autoRef);
		// tb.addSeparator();
		// addButton(tb, new SettingAction());

		JPanel aPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		outlineListBtn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		outlineListBtn.addActionListener(new RefreshAction());
		
		aPanel.add(outlineListBtn);
		
		autoRefreshCheckBox.setSelected(true);
		autoRefreshCheckBox.setEnabled(false);

		refreshIntervalTextField.setText(String.valueOf(refreshInterval));
		refreshIntervalTextField.addFocusListener(new IntevalFocusListener());
		autoRefreshCheckBox.addActionListener(new AutoRefreshCheckBoxHandler());
		
		aPanel.add(new JLabel("  定时刷新:"));
		aPanel.add(autoRefreshCheckBox);
		aPanel.add(refreshIntervalTextField);
		aPanel.add(new JLabel("秒          "));

		JLabel lab0 = new JLabel("图例说明：");
		JLabel lab1 = new JLabel("就绪     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_ready.png"), JLabel.CENTER);
		JLabel lab2 = new JLabel("运行     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_running.png"), JLabel.CENTER);
		JLabel lab3 = new JLabel("成功     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_successed.png"), JLabel.CENTER);
		JLabel lab4 = new JLabel("失败     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_failed.png"), JLabel.CENTER);
		JLabel lab5 = new JLabel("停止     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_stopped.png"), JLabel.CENTER);		
		JLabel lab6 = new JLabel("排队     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_queue.png"), JLabel.CENTER);	
		JLabel lab7 = new JLabel("禁用     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_suspend.png"), JLabel.CENTER);			
		aPanel.add(lab0);
		aPanel.add(lab1);
		aPanel.add(lab2);
		aPanel.add(lab3);
		aPanel.add(lab4);
		aPanel.add(lab5);
		aPanel.add(lab6);
		aPanel.add(lab7);
		tb.add(aPanel);
		return tb;
	}

	@SuppressWarnings("unused")
	private void addButton(JToolBar tb, WcpAction wa) {
		JButton btn = tb.add(wa);
		btn.setToolTipText(wa.getText());
		btn.setText(null);
	}

	// private void initFlowMetaDate() {
	// XmlConfig config = new XmlConfig("./cfg/FlowMonitor.xml");
	//
	// try {
	// String metadataConfigPath = config
	// .readSingelNodeValue("//config/metadataConfigFile");
	// // 初始化元数据
	// if (metadataConfigPath.equals("")) {
	// FlowMetaData.init();
	// } else {
	// FlowMetaData.init(metadataConfigPath);
	// }
	// int i = 0;
	// Taskflow tf = null;
	// String flowName = null;
	// Vector<String> flow_name = new Vector<String>();
	//
	// // List<TaskflowGroup> aGroupList =
	// // flowMetaData.queryTaskflowGroupList();
	// List<TaskflowGroup> aGroupList = Xmlrpc.getInstance()
	// .queryTaskflowGroupList();
	// for (Iterator taskflowGroup = aGroupList.iterator(); taskflowGroup
	// .hasNext();) {
	// TaskflowGroup aGroup = (TaskflowGroup) taskflowGroup.next();
	// // List <Taskflow> aTaskflowList =
	// // flowMetaData.queryTaskflowInGroup(aGroup.getGroupID());
	// List<Taskflow> aTaskflowList = Xmlrpc.getInstance()
	// .queryTaskflowInGroup(aGroup.getGroupID());
	// flow = new Taskflow[aTaskflowList.size()];
	// for (Iterator iter = aTaskflowList.iterator(); iter.hasNext();) {
	// tf = (Taskflow) iter.next();
	// flow[i] = tf;
	// flowName = tf.getTaskflow();
	// flow_name.add(flowName);
	// i++;
	// }
	// }
	// } catch (FileNotFoundException e1) {
	// e1.printStackTrace();
	// } catch (DocumentException e1) {
	// e1.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private class TreeSlectionHandler extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			// int i = 0;
			// path = new TreePath[flowNode.size()];
			// for(int m =0; m < flowNode.size(); m++)
			// {
			// DefaultMutableTreeNode node = flowNode.get(m);
			// TreePath visiblePath = new TreePath(((DefaultTreeModel)
			// tree.getModel()).getPathToRoot(node));
			// path[i] = visiblePath;
			// i++;
			// }
			// TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
			int cnt = e.getClickCount();
			int selRow = tree.getRowForLocation(e.getX(), e.getY());
			if (selRow != -1) {
				DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (cnt >= 2) {
					if (curNode.getUserObject() instanceof Taskflow) {// 流程
						try {
							Taskflow taskflow = Xmlrpc.getInstance().queryTaskflow(((Taskflow) curNode.getUserObject()).getTaskflow());
							openTaskflow(taskflow);
						} catch (XmlRpcException e1) {
							e1.printStackTrace();
						}
					} else if (curNode.getUserObject() instanceof TaskflowGroup) {
						// 流程组
					}
					////timer.start();
				}
				if (e.getButton() == 3) {	
					/*timer.stop();
					if (curNode.getUserObject() instanceof TaskflowGroup) {// 流程组
						initPopupG();
						popupG.show(tree, e.getX(), e.getY());// 弹出右键菜单
					}					
					else if (curNode.getUserObject() instanceof Taskflow) {// 流程
						popup.show(tree, e.getX(), e.getY());// 弹出右键菜单
					} */
					
					//
				}
				
				/*if (e.getButton() == 1){
					//timer.start();
				}*/
			}
			
			if (SwingUtilities.isRightMouseButton(e)) {
				leftTreePopupMenu.show(sp1, e.getX(), e.getY());
			}
		}
	}

	// private class PopupMenuListener implements MouseListener
	// {
	// private Taskflow[] flow;
	// private TreePath[] path;
	//		
	// public PopupMenuListener()
	// {
	// Taskflow tf = null;
	// String flowName = null;
	// Vector <String> flow_name = new Vector<String>();
	// flow = new Taskflow[4];
	// List<TaskflowGroup> aGroupList = flowMetaData.queryTaskflowGroupList();
	// for (Iterator taskflowGroup = aGroupList.iterator();
	// taskflowGroup.hasNext();)
	// {
	// int i = 0;
	// path = new TreePath[5];
	// TaskflowGroup aGroup = (TaskflowGroup) taskflowGroup.next();
	// List <Taskflow> aTaskflowList =
	// flowMetaData.queryTaskflowInGroup(aGroup.getGroupID());
	// for (Iterator iter = aTaskflowList.iterator(); iter.hasNext();)
	// {
	// tf = (Taskflow) iter.next();
	// flow[i] = tf;
	// flowName = tf.getTaskflow();
	// flow_name.add(flowName);
	// i++;
	// }
	// }
	// }
	//
	// public void mouseClicked(MouseEvent arg0) {
	//			
	// }
	//
	// public void mouseEntered(MouseEvent arg0) {
	//			
	// }
	//
	// public void mouseExited(MouseEvent arg0) {
	//			
	// }
	//
	// public void mousePressed(MouseEvent e)
	// {
	// int i = 0;
	// for(int m =0; m < tree.getFlowNode().size(); m++)
	// {
	// DefaultMutableTreeNode node = tree.getFlowNode().get(m);
	// TreePath visiblePath = new TreePath(((DefaultTreeModel)
	// tree.getModel()).getPathToRoot(node));
	// path[i] = visiblePath;
	// i++;
	// }
	// int selRow = tree.getRowForLocation(e.getX(), e.getY());
	// TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	// if(selRow != -1)
	// {
	// System.out.println("111111111111111");
	// System.out.println(selPath);
	// System.out.println(path[0]);
	// if (flow[0] != null && selPath.equals(path[0]))
	// {
	// System.out.println("22222222222222");
	// if (e.getSource() == left)
	// {
	// System.out.println("33333333333333333333");
	// openTaskflow(flow[0]);
	// }
	// }
	// if (flow[1] != null && selPath.equals(path[1]))
	// {
	// if (e.getSource() == left && e.getClickCount() == 1)
	// {
	// openTaskflow(flow[1]);
	// }
	// }
	// if (flow[2] != null && selPath.equals(path[2]))
	// {
	// if (e.getSource() == left && e.getClickCount() == 1)
	// {
	// openTaskflow(flow[2]);
	// }
	// }
	// if (flow[3] != null && selPath.equals(path[3]))
	// {
	// if (e.getSource() == left && e.getClickCount() == 1)
	// {
	// openTaskflow(flow[3]);
	// }
	// }
	// }
	// }
	//
	// public void mouseReleased(MouseEvent arg0) {
	//			
	// }
	//		
	// }

	
	private class OutlineTaskAction implements ActionListener{
		
		private Object[] cells;
		
		private String operationStr;
		
		public OutlineTaskAction(Object[] cells,String operationStr){
			this.cells = cells;
			this.operationStr = operationStr;
		}
		
		public void actionPerformed(ActionEvent e) {

			List<Task> taskList = new ArrayList<Task>();
			StringBuffer sb = new StringBuffer();
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff == null) {
				return ;
			}
			cells = ff.getSelectCells();
			
			for(Object obj : cells){
				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object uo = cell.getUserObject();
				// tasks
				if (uo instanceof Task) {
					Task task = (Task)uo;
					taskList.add(task);
					sb.append(task.getTask() + " ["+ task.getDescription() + "]" +  "\r\n");
				}
			}
			
			
			if (taskList.size() == 0){
				return ;
			}
			
			int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
					"确定要对以下的流程进行" + operationStr + "操作？\r\n" + sb, "确定" + operationStr ,
					JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.YES_OPTION) {
				String taskName = "";
				try{
					FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					for(Task task : taskList){
						taskName = task.getTask();
						Taskflow taskflow = Xmlrpc.getInstance().queryTaskflow(taskName);
						if (operationStr.equals(FlowMonitor.this.startTitle)){
							startTaskflow(taskflow);
						}else if (operationStr.equals(FlowMonitor.this.stopTitle)){
							stopTaskflow(taskflow);
						}/*else if (operationStr.equals(FlowMonitor.this.redoTitle)){

						}*/
						else if (operationStr.equals(FlowMonitor.this.suspendTitle)){
							suspendTaskflow(taskflow);
						}
						else if (operationStr.equals(FlowMonitor.this.cancelSuspendTitle)){
							cancelSuspendTaskflow(taskflow);
						}
						else if (operationStr.equals(FlowMonitor.this.wakeupTitle)){
							wakeupTaskflow(taskflow);
						}else if (operationStr.equals(FlowMonitor.this.skipTaskflowTitle)){
							skipTaskflow(taskflow);
						}
					}
				}catch(Exception e1){
					log.error(taskName + ",进行" + operationStr + "时出错：",e1);
					JOptionPane.showMessageDialog(FlowMonitor.this, e1.getMessage(), "系统信息",
							JOptionPane.ERROR_MESSAGE);
				} finally{
					if (ff != null) {
						ff.refreshTaskflow();	
						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						ff.addMouseListener(new FrameMouseListener());
					}
					FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
				}
			}
		}
		
	}

	private class TaskAction implements ActionListener{

		private Object[] cells;

		private String operationStr;

		public TaskAction(Object[] cells,String operationStr){
			this.cells = cells;
			this.operationStr = operationStr;
		}

		public void actionPerformed(ActionEvent e) {

			List<Task> taskList = new ArrayList<Task>();
			StringBuffer sb = new StringBuffer();
			
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff == null) {
				return ;
			}
			cells = ff.getSelectCells();
			
			for(Object obj : cells){
				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object uo = cell.getUserObject();
				// tasks
				if (uo instanceof Task) {
					Task task = (Task)uo;
					taskList.add(task);
					sb.append(task.getTask() + " ["+ task.getDescription() + "]" +  "\r\n");
				}
			}
			
			if (taskList.size() == 0){
				return ;
			}
			int ret = JOptionPane.YES_OPTION; 
			if (!operationStr.equals(FlowMonitor.this.taskAttributeTitle)){
				ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
						"确定要对以下的任务进行" + operationStr + "操作？\r\n" + sb, "确定" + operationStr ,
						JOptionPane.YES_NO_OPTION);
			}
			
			if (ret == JOptionPane.YES_OPTION) {
				String taskName = "";
				try{
					FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					for(Task task : taskList){
						taskName = task.getTask();
						taskName = taskflow.getTaskflow() + "." + taskName;
						if (operationStr.equals(FlowMonitor.this.skipTaskTitle)){
							skipTask(taskflow,task);
						} else if (operationStr.equals(FlowMonitor.this.suspendTaskTitle)){
							suspendTask(taskflow,task);
						} else if (operationStr.equals(FlowMonitor.this.cancelSuspendTaskTitle)){
							cancelSuspendTask(taskflow,task);
						} else if (operationStr.equals(FlowMonitor.this.taskAttributeTitle)){
							TaskSimpleDialog dialog = new TaskSimpleDialog(FlowMonitor.this);
							dialog.loadValue(taskflow,task);
							dialog.setLocationRelativeTo(FlowMonitor.this);
							dialog.setVisible(true);
						}
						
						
					}
				}catch(Exception e1){
					log.error(taskName + ",进行" + operationStr + "时出错：",e1);
					JOptionPane.showMessageDialog(FlowMonitor.this, e1.getMessage(), "系统信息",
							JOptionPane.ERROR_MESSAGE);
				} finally{
					if (ff != null) {
						ff.refreshTaskflow();	
						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						ff.addMouseListener(new FrameMouseListener());
					}
					FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
				}
			}
		}
		
	}
	
	private class IntevalFocusListener implements FocusListener {

		public void focusGained(FocusEvent e) {

		}

		public void focusLost(FocusEvent e) {
			if (autoRefreshCheckBox.isSelected()) {
				timer.stop();
				int newInteval = Integer.parseInt(refreshIntervalTextField
						.getText());
				timer.setDelay(newInteval * 1000);
				timer.restart();
			} 
		}

	}
	
	public void openTaskflow(Taskflow tf) {
		
		taskflow = tf;
		Thread thread = new Thread(){
			public void run(){
				openTaskflow2(taskflow);
			}
		};
		thread.start();
	}

	public void openTaskflow2(Taskflow tf) {
		if (tf == null) {
			return;
		}

		// 检查引擎是否启动
		if (checkFlowEngine(this.hostIp, this.hostPort)) {

			// 设置停止启动按钮状态
			try {
				// if (flowMetaData.loadSuspendOfTaskflow(tf.getTaskflowID()) ==
				// 1)// 挂起
				if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
					if (tf.getStatus() == Taskflow.STOPPED)// 挂起
					{
						startJB.setEnabled(true);
						stopJB.setEnabled(false);
						initPopupMenu();
					} else {
						startJB.setEnabled(false);
						stopJB.setEnabled(true);
						initPopupMenu();
					}
				}
			} catch (Exception e) {
				log.error("openTaskflow,出错：",e);
				e.printStackTrace();
			}
			refreshJB.setEnabled(true);
			autoRefreshCheckBox.setEnabled(true);
		} else {
			startJB.setEnabled(false);
			stopJB.setEnabled(false);
			refreshJB.setEnabled(false);
			autoRefreshCheckBox.setEnabled(false);
			JOptionPane.showMessageDialog(this, "引擎未启动！", "系统信息",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			
			JInternalFrame frm  = frames[i];
			FlowFrame ff = (FlowFrame) frm;
			if (ff.getTaskflow().getGroupID() == outlineGroupId ){
				
				if (ff.getTaskflow().getTaskflow().equals(tf.getTaskflow())) {
					try {
						frm.setSelected(true);
						frm.addMouseListener(new FrameMouseListener());
						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						return;
					} catch (PropertyVetoException e) {
						WcpMessageBox.postException(FlowMonitor.this, e);
					}
				}
				continue;
			} else if (tf.getGroupID() != outlineGroupId ){
				try {
					frm.setSelected(true);
					//frm.addMouseListener(new FrameMouseListener());
					ff.setTaskflow(tf);
					ff.setTitle(ff.getTitle(tf));
					ff.openTaskflow();
					return;
				} catch (PropertyVetoException e) {
					WcpMessageBox.postException(FlowMonitor.this, e);
				}
			}
		}

		FlowFrame ff = addFlowFrame(tf);
		ff.addMouseListener(new FrameMouseListener());
		if (ff != null) {
			ff.openTaskflow();
		}
	}
	
	

	// 检查引擎是否启动
	public Boolean checkFlowEngine(String ip, int port) {
		// if (testConnection(ip,port) == -1)
		// return false;

		// sendToServer();
		// tearDownConnection();
		return true;
	}

	public String sendToServer() {
		String repy = "";
		try {
			//System.out.println(this.msg);
			socketWriter.println(this.msg);
			socketWriter.flush();
			String line = null;
			while ((line = socketReader.readLine()) != null) {
				repy = line;
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("sendToServer,出错：",e);
		}
		return repy;
	}

	public int testConnection(String ip, int port) {
		try {
			@SuppressWarnings("unused")
			Socket client = new Socket(ip, port);
			// BufferedReader socketReader = new BufferedReader(new
			// InputStreamReader(client.getInputStream()));
			// PrintWriter socketWriter = new
			// PrintWriter(client.getOutputStream());
		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

	public void tearDownConnection() {
		try {
			socketWriter.close();
			socketReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			log.error("tearDownConnection,出错：",e);
		}
	}
	
	
	public void refreshTaskflow(){
		FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
		if (ff != null) {
			ff.refreshTaskflow();	
			ff.setTitle(ff.getTitle(ff.getTaskflow()));
			ff.addMouseListener(new FrameMouseListener());
		}
	}

	private FlowFrame addFlowFrame(Taskflow tf) {
		if (tf == null) {
			return null;
		}
		FlowFrame ff = new FlowFrame(this,tf, dlgTaskflow, xmlrpc, isApplet);
		ff.addMouseListener(new FrameMouseListener());
		ff.getContentPane().addMouseListener(new FrameMouseListener());
		ff.getSp().addMouseListener(new FrameMouseListener());
		ff.getGraph().addMouseListener(new FrameMouseListener());
		desktop.add(ff);
		ff.setVisible(true);
		try {
			ff.setSelected(true);
		} catch (PropertyVetoException e) {
			WcpMessageBox.postException(this, e);
			return null;
		}
		try {
			ff.setMaximum(true);
		} catch (PropertyVetoException e1) {
			WcpMessageBox.postException(FlowMonitor.this, e1);
		}
		return ff;
	}
	
	
	private class RefreshTaskFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RefreshTaskFlowAction() {
			super(refreshTitle, FlowMonitor.class, "images/refresh.png");
			this.setEnabled(false);
		}

		public void actionPerformed(ActionEvent ae) {
			refreshTaskflow();
		}
	}
	

	
	private class RefreshAction extends WcpAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public RefreshAction() {
			super(refreshTitle, FlowMonitor.class, "images/refresh.png");
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent ae) {
					
					//refreshTree();

					/*FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
					if (ff != null) {
						ff.refreshTaskflow();	
						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						ff.addMouseListener(new FrameMouseListener());
					}*/
					outlinelistPopup.show(outlineListBtn, outlineListBtn.getX() - 5, outlineListBtn.getY() + 12);
					new Thread(){
						public void run(){
							initTable();//刷新告警
						}
					}.start();
		}
	}
	
	private void refreshTree()
	{
		int count = treeModel.getChildCount(root_node);
		for (int i = 0; i < count; i++)
		{
			DefaultMutableTreeNode flowGrouplist = (DefaultMutableTreeNode) treeModel.getChild(root_node, 0);
			treeModel.removeNodeFromParent(flowGrouplist);
		}
		initTreeModel();
		tree.expandRow(0);
	}
	
	public void initTable()
	{
		try {
			if(dao==null){
				log.warn("基于文件系统运行不能查询告警！");
				return;
			}
			Vector<WarnBean> v = dao.getWarnMessage(warnDialog.getSql());
			String[] str = { "告警主机", "告警的详细描述内容", "告警发生时间" };
			model = new DefaultTableModel(str, 0);
			Object[] message;
			if (v != null){
				for (int i = v.size() - 1; i >= 0; i--) {
					// System.out.println("33333333333333");
					message = new String[3];
					WarnBean bean = v.get(i);
					message[0] = bean.getHostIp();
					message[1] = bean.getDescription();
					message[2] = bean.getOccurTime().toString();
					model.addRow(message);
				}
			}
			table.setModel(model);
			sort = new SortManager(table);
			TableColumnModel tcm = table.getColumnModel();
			tcm.getColumn(0).setMaxWidth(100);
			tcm.getColumn(2).setMaxWidth(200);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.getTableHeader().setReorderingAllowed(false);
			/*if (v == null || v.isEmpty()) {
				JOptionPane.showMessageDialog(this, "没有符合条件的告警信息！", "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}*/
		} catch (SQLException e) {
			/*JOptionPane.showMessageDialog(this, "读取告警信息出错！", "系统信息",
					JOptionPane.ERROR_MESSAGE);*/
		}
	}
	
	public static void sleep(){
		try {
			Thread.sleep(70);
		} catch (InterruptedException e1) {
		}
	}

	private class TableListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (table.getSelectedRow() != -1) {
				if (table.getSelectedColumn() == 1 && e.getButton() == 1) {
					warnarea.setText(
							(String) table
									.getValueAt(table.getSelectedRow(), 1));
					
					warnpopup.show(table, e.getX(),	e.getY());
				}
			}
		}
	}

	private class LogAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LogAction() {
			super("查看日志");
		}

		public void actionPerformed(ActionEvent ae) {

			if (logConfig.getJCheckBox7().isSelected()) {
				logConfig.showLog();
			} else {
				logConfig.setVisible(true);
			}
			////timer.start();
		}
	}

	private class StopFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StopFlowAction() {
			super(stopTitle, FlowMonitor.class, "images/pause.png");
			this.setEnabled(false);
		}

		public void actionPerformed(ActionEvent ae) {

			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {

				int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
						"确定要停止当前流程吗？\r\n" , "确定",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					try {
						stopTaskflow(ff.getTaskflow());
						ff.refreshTaskflow();

						sleep();

						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						ff.addMouseListener(new FrameMouseListener());

						if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
							startJB.setEnabled(true);
							stopJB.setEnabled(false);
							initPopupMenu();
						}
					} catch (XmlRpcException e) {
						e.printStackTrace();
						log.error("StopFlowAction,出错：",e);
					} finally{
						FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
					}
				}
			}
			////timer.start();
		}
	}
	private class RedoFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public RedoFlowAction() {
			super(redoTitle, FlowMonitor.class, "images/play.png");
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent ae) {

			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
						"确定要重做当前流程吗？\r\n" , "确定",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					try {
						try {
							/*Taskflow taskflow = ff.getTaskflow();
						String result = Xmlrpc.getInstance().redo(taskflow.getTaskflowID(),new Date(),new Date());*/

							redoDialog.loadValue(ff.getTaskflow());
							redoDialog.setLocationRelativeTo(FlowMonitor.this);
							redoDialog.setVisible(true);
						} catch (XmlRpcException e) {
							log.error(e);
							JOptionPane.showMessageDialog(FlowMonitor.this, e.getMessage(), "系统信息",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						log.error("RedoFlowAction,出错：",e);
						JOptionPane.showMessageDialog(FlowMonitor.this, e.getMessage(), "系统信息",
								JOptionPane.ERROR_MESSAGE);
					}


					ff.refreshTaskflow();

					sleep();

					ff.setTitle(ff.getTitle(ff.getTaskflow()));
					ff.addMouseListener(new FrameMouseListener());
				}
			}
			////timer.start();
		}
	}
	
	private class WakeupFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public WakeupFlowAction() {
			super(wakeupTitle, FlowMonitor.class, "images/play.png");
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent ae) {

			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
						"确定要唤醒当前流程吗？\r\n" , "确定",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					wakeupTaskflow(ff.getTaskflow());
					ff.refreshTaskflow();
					ff.setTitle(ff.getTitle(ff.getTaskflow()));
					ff.addMouseListener(new FrameMouseListener());
				}
			}
			/////timer.start();
		}
	}
	

	private class SkipFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public SkipFlowAction() {
			super(skipTaskflow2Title, FlowMonitor.class, "images/play.png");
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent ae) {
			
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				skipTaskflow(ff.getTaskflow());
				sleep();
				ff.refreshTaskflow();
				ff.setTitle(ff.getTitle(ff.getTaskflow()));
				ff.addMouseListener(new FrameMouseListener());
			}
			/////timer.start();
		}
	}

	@SuppressWarnings("unused")
	private class SkipTaskAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public SkipTaskAction() {
			super(skipTaskTitle, FlowMonitor.class, "images/play.png");
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent ae) {
			
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				//skipTask(ff.getTaskflow());
				sleep();
				ff.refreshTaskflow();
				ff.setTitle(ff.getTitle(ff.getTaskflow()));
				ff.addMouseListener(new FrameMouseListener());
			}
			/////timer.start();
		}
	}

	private class StartFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StartFlowAction() {
			super(startTitle, FlowMonitor.class, "images/play.png");
			this.setEnabled(false);
		}

		public void actionPerformed(ActionEvent ae) {

			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
						"确定要启动当前流程吗？\r\n" , "确定",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					startTaskflow(ff.getTaskflow());
					ff.refreshTaskflow();
					sleep();
					ff.setTitle(ff.getTitle(ff.getTaskflow()));
					ff.addMouseListener(new FrameMouseListener());
					try {
						if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
							startJB.setEnabled(false);
							stopJB.setEnabled(true);
							initPopupMenu();
						}
					} catch (XmlRpcException e) {
						e.printStackTrace();
						log.error("StartFlowAction,出错：",e);
					}finally{
						FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
					}
				}
			}
			////timer.start();
		}
	}
	
	private class SuspendFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public SuspendFlowAction() {
			super(suspendTitle, FlowMonitor.class, "images/stop.png");
			this.setEnabled(true);
		}
		
		public void actionPerformed(ActionEvent ae) {
			
			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
						"确定要禁用当前流程吗？\r\n" , "确定",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					suspendTaskflow(ff.getTaskflow());
					ff.refreshTaskflow();
					sleep();
					ff.setTitle(ff.getTitle(ff.getTaskflow()));
					ff.addMouseListener(new FrameMouseListener());
					try {
						if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
							startJB.setEnabled(false);
							stopJB.setEnabled(true);
							initPopupMenu();
						}
					} catch (XmlRpcException e) {
						e.printStackTrace();
						log.error("StartFlowAction,出错：",e);
					}finally{
						FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
					}
				}
			}
			////timer.start();
		}
	}
	
	private class CancelSuspendFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public CancelSuspendFlowAction() {
			super(cancelSuspendTitle, FlowMonitor.class, "images/play.png");
			this.setEnabled(true);
		}
		
		public void actionPerformed(ActionEvent ae) {
			
			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
						"确定要启用当前流程吗？\r\n" , "确定",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					cancelSuspendTaskflow(ff.getTaskflow());
					ff.refreshTaskflow();
					sleep();
					ff.setTitle(ff.getTitle(ff.getTaskflow()));
					ff.addMouseListener(new FrameMouseListener());
					try {
						if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
							startJB.setEnabled(false);
							stopJB.setEnabled(true);
							initPopupMenu();
						}
					} catch (XmlRpcException e) {
						e.printStackTrace();
						log.error("StartFlowAction,出错：",e);
					}finally{
						FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
					}
				}
			}
			////timer.start();
		}
	}
	
	private class StartAllFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public StartAllFlowAction() {
			super(startAllTitle, FlowMonitor.class, "images/play.png");
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent ae) {
			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				try {
					int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
							"确定要启动所有流程吗？\r\n" , "确定",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						String result = xmlrpc.startAllTaskflow();
						JOptionPane.showMessageDialog(FlowMonitor.this, result, "系统信息",
								JOptionPane.INFORMATION_MESSAGE);

						ff.refreshTaskflow();
						sleep();
						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						ff.addMouseListener(new FrameMouseListener());

						if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
							startJB.setEnabled(false);
							stopJB.setEnabled(true);
							initPopupMenu();
						}
					}
				} catch (XmlRpcException e) {
					e.printStackTrace();
					log.error("StartFlowAction,出错：",e);
				}finally{
					FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
				}
			}
			////timer.start();
		}
	}
	
	private class StopAllFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public StopAllFlowAction() {
			super(stopAllTitle, FlowMonitor.class, "images/stop.png");
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent ae) {
			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				try {
					int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
							"确定要停止所有流程吗？\r\n" , "确定",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						String result = xmlrpc.stopAllTaskflow();
						JOptionPane.showMessageDialog(FlowMonitor.this, result, "系统信息",
								JOptionPane.INFORMATION_MESSAGE);
						ff.refreshTaskflow();
						sleep();
						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						ff.addMouseListener(new FrameMouseListener());

						if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
							startJB.setEnabled(false);
							stopJB.setEnabled(true);
							initPopupMenu();
						}
					}
				} catch (XmlRpcException e) {
					e.printStackTrace();
					log.error("StartFlowAction,出错：",e);
				}finally{
					FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
				}
			}
			////timer.start();
		}
	}
	
	private class SuspendAllFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public SuspendAllFlowAction() {
			super(suspendAllTitle, FlowMonitor.class, "images/stop.png");
			this.setEnabled(true);
		}
		
		public void actionPerformed(ActionEvent ae) {
			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				try {
					int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
							"确定要禁用所有流程吗？\r\n" , "确定",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						String result = xmlrpc.suspendAllTaskflow();
						
						if (result.indexOf("成功") > -1) {
							JOptionPane.showMessageDialog(FlowMonitor.this, "成功执行禁用所有流程操作", "系统信息",
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(FlowMonitor.this, result, "系统信息",
									JOptionPane.INFORMATION_MESSAGE);
						}
						ff.refreshTaskflow();
						sleep();
						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						ff.addMouseListener(new FrameMouseListener());
						
						if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
							startJB.setEnabled(false);
							stopJB.setEnabled(true);
							initPopupMenu();
						}
					}
				} catch (XmlRpcException e) {
					e.printStackTrace();
					log.error("StartFlowAction,出错：",e);
				}finally{
					FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
				}
			}
			////timer.start();
		}
	}
	
	private class CancelSuspendAllFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public CancelSuspendAllFlowAction() {
			super(cancelSuspendAllTitle, FlowMonitor.class, "images/play.png");
			this.setEnabled(true);
		}
		
		public void actionPerformed(ActionEvent ae) {
			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
			if (ff != null) {
				try {
					int ret = JOptionPane.showConfirmDialog(FlowMonitor.this,
							"确定要启用所有流程吗？\r\n" , "确定",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						String result = xmlrpc.cancelSuspendAllTaskflow();
						if (result.indexOf("成功") > -1) {
							JOptionPane.showMessageDialog(FlowMonitor.this, "成功执行启用所有流程操作", "系统信息",
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(FlowMonitor.this, result, "系统信息",
									JOptionPane.INFORMATION_MESSAGE);
						}
						ff.refreshTaskflow();
						sleep();
						ff.setTitle(ff.getTitle(ff.getTaskflow()));
						ff.addMouseListener(new FrameMouseListener());
						
						if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
							startJB.setEnabled(false);
							stopJB.setEnabled(true);
							initPopupMenu();
						}
					}
				} catch (XmlRpcException e) {
					e.printStackTrace();
					log.error("StartFlowAction,出错：",e);
				}finally{
					FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
				}
			}
			////timer.start();
		}
	}

	private class StartGroupAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StartGroupAction() {
			super(startTitle, FlowMonitor.class, "images/play.png");
			this.setEnabled(false);
		}

		public void actionPerformed(ActionEvent ae) {
			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (curNode.getUserObject() instanceof TaskflowGroup) {
				startGroup((TaskflowGroup) curNode.getUserObject());
				try {
					if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
						startJB.setEnabled(false);
						stopJB.setEnabled(true);
						initPopupG();
					}
				} catch (XmlRpcException e) {
					e.printStackTrace();
					log.error("StartGroupAction,出错：",e);
				} finally{
					FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
				}
			}
			//timer.start();
		}
	}

	private class StopGroupAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StopGroupAction() {
			super(stopTitle, FlowMonitor.class, "images/pause.png");
			this.setEnabled(false);
		}

		public void actionPerformed(ActionEvent ae) {
			FlowMonitor.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (curNode.getUserObject() instanceof TaskflowGroup) {
				stopGroup((TaskflowGroup) curNode.getUserObject());
				try {
					if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
						startJB.setEnabled(true);
						stopJB.setEnabled(false);
						initPopupG();
					}
				} catch (XmlRpcException e) {
					e.printStackTrace();
					log.error("StopGroupAction,出错：",e);
				}finally{
					FlowMonitor.this.setCursor(Cursor.getDefaultCursor());
				}
			}
			//timer.start();
		}
	}

	private class AutoRefreshHandler extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			Thread thread = new Thread(){
				public void run(){
					
					try {
						FlowFrame ff = (FlowFrame) desktop.getSelectedFrame();
						
						if (ff != null){
							ff.setTaskflow(Xmlrpc.getInstance().queryTaskflow(ff.getTaskflow().getTaskflow()));
							if (ff.getTaskflow().getStatus()!=Taskflow.STOPPED || ff.getTaskflow().getGroupID() == outlineGroupId) {
								//当流程是不是STOPPED的时候才刷新
								ff.refreshTaskflow();
								
								ff.setTitle(ff.getTitle(ff.getTaskflow()));
								ff.addMouseListener(new FrameMouseListener());

								if (ff.getTaskflow().getStatus() == Taskflow.STOPPED)
								{
									startJB.setEnabled(true);
									stopJB.setEnabled(false);
									initPopupMenu();
								}
								else{
									startJB.setEnabled(false);
									stopJB.setEnabled(true);
									initPopupMenu();
								}
								//System.out.println("刷新：" + ff.getTaskflow().getTaskflow()+",时间："+TimeUtils.toChar(new Date()));
							}
						}
					} catch (XmlRpcException e) {
						e.printStackTrace();
						log.error("AutoRefreshHandler,出错：",e);
					}
					
					
				}
			};
			thread.start();
		}
	}

	private class AutoRefreshCheckBoxHandler extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			JCheckBox cb = (JCheckBox) ae.getSource();
			if (cb.isSelected()) {
				int newInteval = Integer.parseInt(refreshIntervalTextField
						.getText());
				timer.setDelay(newInteval * 1000);
				timer.start();
			} else {
				timer.stop();
			}
		}
	}
	private class OpenTaskflowAction implements ActionListener {

		private String taskflow ; 
		private static final long serialVersionUID = 1L;

		public OpenTaskflowAction(String taskflow) {
			this.taskflow = taskflow;
		}

		public void actionPerformed(ActionEvent ae) {
			try {
				Taskflow aTaskflow = Xmlrpc.getInstance().queryTaskflow(taskflow);
				openTaskflow(aTaskflow);
				
				Thread thread = new Thread(){
					public void run(){
						refreshOutlineList();
					}
				};
				thread.start();
			} catch (XmlRpcException e1) {
				e1.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	private class AutoRefreshAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AutoRefreshAction() {
			super("自动刷新", FlowMonitor.class, "images/auto_refresh.png");
		}

		public void actionPerformed(ActionEvent ae) {
			JToggleButton tb = (JToggleButton) ae.getSource();
			if (tb.isSelected()) {
				//timer.start();
			} else {
				timer.stop();
			}
		}
	}

//	private class SettingAction extends WcpAction {
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 1L;
//
//		public SettingAction() {
//			super("设置", FlowMonitor.class, "images/settings.png");
//		}
//
//		public void actionPerformed(ActionEvent ae) {
//
//		}
//	}

	// private class ConfigMenuListener extends MouseAdapter {
	// public void mousePressed(MouseEvent e) {
	// if (e.getButton() == 1) {
	// if (FlowFrame.XMLCONN) {
	// JOptionPane.showMessageDialog(null, "IP地址：" + Xmlrpc.IP
	// + "\n端口号：" + Xmlrpc.PORT + "\n用户名："
	// + Xmlrpc.USERNAME + "\n密码 ：" + Xmlrpc.PASSWORD,
	// "系统消息", JOptionPane.INFORMATION_MESSAGE);
	// }
	// // else{
	// // MonitorLogonDialog config = new
	// // MonitorLogonDialog(FlowMonitor.this);
	// // config.setVisible(true);
	// // }
	// }
	// }
	// }

	private class LogConfigMenuListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == 1) {
				logConfig.setVisible(true);
			}
		}
	}
	
	private class WarnConfigMenuListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == 1) {
				warnDialog.setVisible(true);
			}
		}
	}

	@SuppressWarnings("unused")
	private class FlowListListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {

			ConsoleDialog cmd = new ConsoleDialog(FlowMonitor.this);
			cmd.setVisible(true);
		}
		
	}
	
	/*
	private class FlowListListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == 1) {
				int flashTime = 3;
				try {
					flashTime = Integer.parseInt(refreshIntervalTextField
							.getText());
				} catch (Exception ne) {
					log.error(ne);
					log.error("自动刷新时间不能转换为整数,流程列表将以3秒刷新");
				}
				TaskflowListDialog tld = new TaskflowListDialog(
						FlowMonitor.this, flashTime, dao);
				// tld.setVisible(true);
			}
		}
	}
	
	*/
	
	private class FrameMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			//timer.start();
		}
	}

	public JTree getTree() {
		return tree;
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}

	public void startTaskflow(Taskflow taskflow) {
		try {
			// 判断当前流程的进度时间是否为空
			taskflow = Xmlrpc.getInstance().queryTaskflow(
					taskflow.getTaskflowID());
		
			if (null != taskflow.getStatTime()) {
				log.debug("启动流程" + taskflow.getDescription());
				
				try {
					String result = Xmlrpc.getInstance().startTaskflow(
							taskflow.getTaskflow());
					log.debug(result);
					
					if (result.indexOf("请先使用suspend命令解除禁用") > -1){
						JOptionPane.showMessageDialog(this, result.replace("使用suspend命令解除禁用", "启用流程"), "系统信息",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, result, "系统信息",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (XmlRpcException e) {
					log.error(e);
					JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this,
						TaskflowDialog.statTimeTitle + "为空，不能启动，请先设置" + TaskflowDialog.statTimeTitle + "！", "系统信息",
						JOptionPane.ERROR_MESSAGE);
				dlgTaskflow.loadValue(taskflow);
				dlgTaskflow.setVisible(true);
				this.refreshTaskflow();
				try {
					String result = Xmlrpc.getInstance().startTaskflow(
							taskflow.getTaskflow());
					log.debug(result);
					JOptionPane.showMessageDialog(this, result, "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
					this.refreshTaskflow();
				} catch (XmlRpcException e) {
					log.error(e);
					JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
							JOptionPane.ERROR_MESSAGE);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("startTaskflow,出错：",e);
			JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void startGroup(TaskflowGroup group) {
		try {
			try {
				String result = Xmlrpc.getInstance().startTaskflowGroup(
						group.getGroupName());
				log.debug(result);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("startGroup,出错：",e);
			JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void stopTaskflow(Taskflow taskflow) {
		try {
			log.debug("停止流程" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().stopTaskflow(
						taskflow.getTaskflow());
				log.debug(result);
				JOptionPane.showMessageDialog(this, result, "系统信息",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("stopTaskflow,出错：",e);
		}
	}
	
	
	public void suspendTaskflow(Taskflow taskflow) {
		try {
			log.debug(suspendTitle + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().suspendTaskflow(
						taskflow.getTaskflow());
				log.debug(result);
				
				if (result.indexOf("成功") > -1 ){
					JOptionPane.showMessageDialog(this, "成功禁用流程" + taskflow.getTaskflow(), "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, result, "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
				}
				
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("stopTaskflow,出错：",e);
		}
	}
	
	
	public void cancelSuspendTaskflow(Taskflow taskflow) {
		try {
			log.debug(cancelSuspendTitle + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().cancelSuspendTaskflow(
						taskflow.getTaskflow());
				log.debug(result);
				if (result.indexOf("成功") > -1 ){
					JOptionPane.showMessageDialog(this, "成功启用流程" + taskflow.getTaskflow(), "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, result, "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("stopTaskflow,出错：",e);
		}
	}
	
	public void wakeupTaskflow(Taskflow taskflow) {
		try {
			log.debug("唤醒流程" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().wakeupTaskflow(
						taskflow.getTaskflow());
				log.debug(result);
				JOptionPane.showMessageDialog(this, result, "系统信息",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("wakeupTaskflow,出错：",e);
		}
	}
	

	public void skipTaskflow(Taskflow taskflow) {
		try {
			log.debug("跳过流程" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().skipTaskflow(
						taskflow.getTaskflow());
				log.debug(result);
				JOptionPane.showMessageDialog(this, result, "系统信息",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("wakeupTaskflow,出错：",e);
		}
	}
	
	public void skipTask(Taskflow taskflow ,Task task) {
		try {
			log.debug("跳过流程任务" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().skipTask(
						taskflow.getTaskflow(),task.getTask());
				log.debug(result);
				JOptionPane.showMessageDialog(this, result, "系统信息",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("wakeupTaskflow,出错：",e);
		}
	}
	
	public void suspendTask(Taskflow taskflow ,Task task) {
		try {
			log.debug("禁用流程任务" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().suspendTask(
						taskflow.getTaskflow(),task.getTask());
				log.debug(result);
				if (result.indexOf("成功") > -1 ){
					JOptionPane.showMessageDialog(this, "成功禁用流程任务" + task.getTask(), "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, result, "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("suspendTask,出错：",e);
		}
	}
	
	public void cancelSuspendTask(Taskflow taskflow ,Task task) {
		try {
			log.debug("启用流程任务" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().cancelSuspendTask(
						taskflow.getTaskflow(),task.getTask());
				log.debug(result);
				if (result.indexOf("成功") > -1 ){
					JOptionPane.showMessageDialog(this, "成功启用流程任务" + task.getTask(), "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, result, "系统信息",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("cancelSuspendTask,出错：",e);
		}
	}

	public void stopGroup(TaskflowGroup group) {
		try {
			try {
				String result = Xmlrpc.getInstance().stopTaskflowGroup(
						group.getGroupName());
				log.debug(result);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("stopGroup,出错：",e);
			JOptionPane.showMessageDialog(this, e.getMessage(), "系统信息",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static Logger getLog() {
		return log;
	}

	public JDesktopPane getDesktop() {
		return desktop;
	}
	
	
}
