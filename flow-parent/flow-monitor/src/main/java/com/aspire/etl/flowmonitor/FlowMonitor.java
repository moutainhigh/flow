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
 * @author ����
 * @since 2008-4-17 1.�޸Ķ�ʱˢ�¹��� 2.��������������ֹͣ����
 * 
 * @author jiangts
 * @since 2019-01-19 ���������޸ģ�
 * 				1.initTree()������ͼ������߼��޸ģ������TaskflowGroup�����ð�ͼ�꣬Taskflowʹ������ͼ��
 * 			    2.initTreeModel�������� ��jtree������������
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

	// ����IP
	protected String hostIp = "10.1.3.194";

	// ����˿�
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

	// �Ҽ��˵�
	private JMenuItem left;

	private JMenuItem right;

	private JMenuItem view;

	private JPopupMenu popup = new JPopupMenu();

	private JPopupMenu leftTreePopupMenu = new JPopupMenu();
	
	// ������
	private JMenuItem runG;

	private JMenuItem stopG;

	private JPopupMenu popupG = new JPopupMenu();
	
	private JPopupMenu outlinelistPopup = new JPopupMenu();

	private JDesktopPane desktop;

	public static JButton refreshJB = new JButton();
	public static JButton startJB = new JButton();
	public static JButton stopJB = new JButton();

	private String refreshTitle = "ˢ��";
	private String startTitle = "��������";
	private String stopTitle = "ֹͣ����";
	private String suspendTitle = "��������";
	private String cancelSuspendTitle = "��������";
	private String startAllTitle = "������������";
	private String stopAllTitle = "ֹͣ��������";
	private String suspendAllTitle = "������������";
	private String cancelSuspendAllTitle = "������������";
	private String redoTitle = "��������";
	private String wakeupTitle = "��������";
	private String skipTaskflowTitle = "������ǰѡ������";
	private String skipTaskflow2Title = "������ǰ����";
	private String skipTaskTitle = "������ǰѡ������";
	private String suspendTaskTitle = "���õ�ǰѡ������";
	private String cancelSuspendTaskTitle = "���õ�ǰѡ������";
	private String taskAttributeTitle = "����������";
	
	private TaskflowDialog dlgTaskflow;

	private final int refreshInterval = 5;

	JCheckBox autoRefreshCheckBox = new JCheckBox();

	private Timer timer;
	
	private JPopupMenu warnpopup;
	
	private JScrollPane warnscroll;
	
	private JTextArea warnarea;
	
	private WarnDialog warnDialog;
	
	JButton outlineListBtn = new JButton("����б�");

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
	
//	�����ID,��ʼ��������ʱ��Ͷ�����0.
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
		
//		 ��ʼ����־
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
		
		this.setTitle("ETL���̼����   " + Xmlrpc.USERNAME + "@" + Xmlrpc.IP + ":"
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

		// �õ�����IP�Ͷ˿�
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
		
		// �õ�ˢ������ֹͣ��ť����
		
		cont.add(jb, BorderLayout.PAGE_START);

		// menubar

		bar = new JMenuBar();
		config_menu = new JMenu("��Ϣ��ѯ");
		// config = new JMenuItem("��������");
		log_config = new JMenuItem("��־��ѯ");
		warn_config = new JMenuItem("�澯��ѯ");
		// config.addMouseListener(new ConfigMenuListener());
		log_config.addMouseListener(new LogConfigMenuListener());
		warn_config.addMouseListener(new WarnConfigMenuListener());
		//flow_list.addMouseListener(new FlowListListener());
		// config_menu.add(config);
		config_menu.add(log_config);
		config_menu.add(warn_config);
		//config_menu.add(flow_list);
		//bar.add(config_menu);
	/*	windows_menu = new JMenu("����");
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
				System.out.println("�˳�����");
				FlowMonitor.this.dispose();
			}
		});*/
		
		JMenuItem refreshMenu  = new JMenuItem(new RefreshAction());
		refreshMenu.getAction().setEnabled(true);
		leftTreePopupMenu.add(refreshMenu);
		
		//��󻯴���
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
		view = new JMenuItem("�鿴���ݿ���־");
		// addButton(tb, new StartFlowAction());
		view.addActionListener(new LogAction());
		// left.addMouseListener(new PopupMenuListener());
		try {
			if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
				left = new JMenuItem("��������");
				right = new JMenuItem("ֹͣ����");
				left.addActionListener(new StartFlowAction());
				right.addActionListener(new StopFlowAction());
				if (startJB.isEnabled())
					popup.add(left);
				else if(stopJB.isEnabled())
					popup.add(right);
			}
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.error("initPopupMenu,����",e);
		}
		popup.add(view);
	}
	
	private void initPopupG()
	{
		popupG.removeAll();
		runG = new JMenuItem("����������");
		stopG = new JMenuItem("ֹͣ������");
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
			log.error("getTaskflowPopupMenu,����",e);
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
			log.error("getTaskflowPopupMenu,����",e);
		}
	}
	
	/**
	 * ��ʼ������ĵ����˵����ִ�ٵ��������ͨ���̵�����˵����������Ϊ���̿��ƵĲ˵�����ͨ�������򵯳���������Ĳ˵�
	 * @param flowframe
	 * @param popupMenu
	 */
	public void initTaskPopupMenu(FlowFrame flowframe,JPopupMenu popupMenu){
		popupMenu.removeAll();
		try {
			if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
				
				if (flowframe.getTaskflow().getGroupID() == outlineGroupId){//�������ڵ㵯�����̿��ƶԻ���
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
				} else { //��ͨ���̣��򵯳�
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
			log.error("getTaskflowPopupMenu,����",e);
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
			// // ��ʼ��Ԫ����
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
			
			//��groupID��������
			Collections.sort(aGroupList,new ComparatorTaskflowGroup());
			
			for (Iterator taskflowGroup = aGroupList.iterator(); taskflowGroup
					.hasNext();) {
				TaskflowGroup aGroup = (TaskflowGroup) taskflowGroup.next();
				
				if (aGroup.getGroupID() != outlineGroupId){
					continue;
				}
				aGroup.setGroupName("���̴��");
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
					// ������������ȥ��
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
			log.error("initTreeModel,����",e);
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
		tree.expandRow(1);// ����,���ܶԵ���
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
		
		aPanel.add(new JLabel("  ��ʱˢ��:"));
		aPanel.add(autoRefreshCheckBox);
		aPanel.add(refreshIntervalTextField);
		aPanel.add(new JLabel("��          "));

		JLabel lab0 = new JLabel("ͼ��˵����");
		JLabel lab1 = new JLabel("����     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_ready.png"), JLabel.CENTER);
		JLabel lab2 = new JLabel("����     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_running.png"), JLabel.CENTER);
		JLabel lab3 = new JLabel("�ɹ�     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_successed.png"), JLabel.CENTER);
		JLabel lab4 = new JLabel("ʧ��     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_failed.png"), JLabel.CENTER);
		JLabel lab5 = new JLabel("ֹͣ     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_stopped.png"), JLabel.CENTER);		
		JLabel lab6 = new JLabel("�Ŷ�     ", WcpImagePool.getIcon(this
				.getClass(), "images/eg_queue.png"), JLabel.CENTER);	
		JLabel lab7 = new JLabel("����     ", WcpImagePool.getIcon(this
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
	// // ��ʼ��Ԫ����
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
					if (curNode.getUserObject() instanceof Taskflow) {// ����
						try {
							Taskflow taskflow = Xmlrpc.getInstance().queryTaskflow(((Taskflow) curNode.getUserObject()).getTaskflow());
							openTaskflow(taskflow);
						} catch (XmlRpcException e1) {
							e1.printStackTrace();
						}
					} else if (curNode.getUserObject() instanceof TaskflowGroup) {
						// ������
					}
					////timer.start();
				}
				if (e.getButton() == 3) {	
					/*timer.stop();
					if (curNode.getUserObject() instanceof TaskflowGroup) {// ������
						initPopupG();
						popupG.show(tree, e.getX(), e.getY());// �����Ҽ��˵�
					}					
					else if (curNode.getUserObject() instanceof Taskflow) {// ����
						popup.show(tree, e.getX(), e.getY());// �����Ҽ��˵�
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
					"ȷ��Ҫ�����µ����̽���" + operationStr + "������\r\n" + sb, "ȷ��" + operationStr ,
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
					log.error(taskName + ",����" + operationStr + "ʱ����",e1);
					JOptionPane.showMessageDialog(FlowMonitor.this, e1.getMessage(), "ϵͳ��Ϣ",
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
						"ȷ��Ҫ�����µ��������" + operationStr + "������\r\n" + sb, "ȷ��" + operationStr ,
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
					log.error(taskName + ",����" + operationStr + "ʱ����",e1);
					JOptionPane.showMessageDialog(FlowMonitor.this, e1.getMessage(), "ϵͳ��Ϣ",
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

		// ��������Ƿ�����
		if (checkFlowEngine(this.hostIp, this.hostPort)) {

			// ����ֹͣ������ť״̬
			try {
				// if (flowMetaData.loadSuspendOfTaskflow(tf.getTaskflowID()) ==
				// 1)// ����
				if (xmlrpc.isPermit("MONITOR_RUN_STOP")) {
					if (tf.getStatus() == Taskflow.STOPPED)// ����
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
				log.error("openTaskflow,����",e);
				e.printStackTrace();
			}
			refreshJB.setEnabled(true);
			autoRefreshCheckBox.setEnabled(true);
		} else {
			startJB.setEnabled(false);
			stopJB.setEnabled(false);
			refreshJB.setEnabled(false);
			autoRefreshCheckBox.setEnabled(false);
			JOptionPane.showMessageDialog(this, "����δ������", "ϵͳ��Ϣ",
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
	
	

	// ��������Ƿ�����
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
			log.error("sendToServer,����",e);
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
			log.error("tearDownConnection,����",e);
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
							initTable();//ˢ�¸澯
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
				log.warn("�����ļ�ϵͳ���в��ܲ�ѯ�澯��");
				return;
			}
			Vector<WarnBean> v = dao.getWarnMessage(warnDialog.getSql());
			String[] str = { "�澯����", "�澯����ϸ��������", "�澯����ʱ��" };
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
				JOptionPane.showMessageDialog(this, "û�з��������ĸ澯��Ϣ��", "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}*/
		} catch (SQLException e) {
			/*JOptionPane.showMessageDialog(this, "��ȡ�澯��Ϣ����", "ϵͳ��Ϣ",
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
			super("�鿴��־");
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
						"ȷ��Ҫֹͣ��ǰ������\r\n" , "ȷ��",
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
						log.error("StopFlowAction,����",e);
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
						"ȷ��Ҫ������ǰ������\r\n" , "ȷ��",
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
							JOptionPane.showMessageDialog(FlowMonitor.this, e.getMessage(), "ϵͳ��Ϣ",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						log.error("RedoFlowAction,����",e);
						JOptionPane.showMessageDialog(FlowMonitor.this, e.getMessage(), "ϵͳ��Ϣ",
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
						"ȷ��Ҫ���ѵ�ǰ������\r\n" , "ȷ��",
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
						"ȷ��Ҫ������ǰ������\r\n" , "ȷ��",
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
						log.error("StartFlowAction,����",e);
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
						"ȷ��Ҫ���õ�ǰ������\r\n" , "ȷ��",
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
						log.error("StartFlowAction,����",e);
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
						"ȷ��Ҫ���õ�ǰ������\r\n" , "ȷ��",
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
						log.error("StartFlowAction,����",e);
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
							"ȷ��Ҫ��������������\r\n" , "ȷ��",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						String result = xmlrpc.startAllTaskflow();
						JOptionPane.showMessageDialog(FlowMonitor.this, result, "ϵͳ��Ϣ",
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
					log.error("StartFlowAction,����",e);
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
							"ȷ��Ҫֹͣ����������\r\n" , "ȷ��",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						String result = xmlrpc.stopAllTaskflow();
						JOptionPane.showMessageDialog(FlowMonitor.this, result, "ϵͳ��Ϣ",
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
					log.error("StartFlowAction,����",e);
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
							"ȷ��Ҫ��������������\r\n" , "ȷ��",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						String result = xmlrpc.suspendAllTaskflow();
						
						if (result.indexOf("�ɹ�") > -1) {
							JOptionPane.showMessageDialog(FlowMonitor.this, "�ɹ�ִ�н����������̲���", "ϵͳ��Ϣ",
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(FlowMonitor.this, result, "ϵͳ��Ϣ",
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
					log.error("StartFlowAction,����",e);
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
							"ȷ��Ҫ��������������\r\n" , "ȷ��",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						String result = xmlrpc.cancelSuspendAllTaskflow();
						if (result.indexOf("�ɹ�") > -1) {
							JOptionPane.showMessageDialog(FlowMonitor.this, "�ɹ�ִ�������������̲���", "ϵͳ��Ϣ",
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(FlowMonitor.this, result, "ϵͳ��Ϣ",
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
					log.error("StartFlowAction,����",e);
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
					log.error("StartGroupAction,����",e);
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
					log.error("StopGroupAction,����",e);
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
								//�������ǲ���STOPPED��ʱ���ˢ��
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
								//System.out.println("ˢ�£�" + ff.getTaskflow().getTaskflow()+",ʱ�䣺"+TimeUtils.toChar(new Date()));
							}
						}
					} catch (XmlRpcException e) {
						e.printStackTrace();
						log.error("AutoRefreshHandler,����",e);
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
			super("�Զ�ˢ��", FlowMonitor.class, "images/auto_refresh.png");
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
//			super("����", FlowMonitor.class, "images/settings.png");
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
	// JOptionPane.showMessageDialog(null, "IP��ַ��" + Xmlrpc.IP
	// + "\n�˿ںţ�" + Xmlrpc.PORT + "\n�û�����"
	// + Xmlrpc.USERNAME + "\n���� ��" + Xmlrpc.PASSWORD,
	// "ϵͳ��Ϣ", JOptionPane.INFORMATION_MESSAGE);
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
					log.error("�Զ�ˢ��ʱ�䲻��ת��Ϊ����,�����б���3��ˢ��");
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
			// �жϵ�ǰ���̵Ľ���ʱ���Ƿ�Ϊ��
			taskflow = Xmlrpc.getInstance().queryTaskflow(
					taskflow.getTaskflowID());
		
			if (null != taskflow.getStatTime()) {
				log.debug("��������" + taskflow.getDescription());
				
				try {
					String result = Xmlrpc.getInstance().startTaskflow(
							taskflow.getTaskflow());
					log.debug(result);
					
					if (result.indexOf("����ʹ��suspend����������") > -1){
						JOptionPane.showMessageDialog(this, result.replace("ʹ��suspend����������", "��������"), "ϵͳ��Ϣ",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (XmlRpcException e) {
					log.error(e);
					JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this,
						TaskflowDialog.statTimeTitle + "Ϊ�գ�������������������" + TaskflowDialog.statTimeTitle + "��", "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
				dlgTaskflow.loadValue(taskflow);
				dlgTaskflow.setVisible(true);
				this.refreshTaskflow();
				try {
					String result = Xmlrpc.getInstance().startTaskflow(
							taskflow.getTaskflow());
					log.debug(result);
					JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
					this.refreshTaskflow();
				} catch (XmlRpcException e) {
					log.error(e);
					JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
							JOptionPane.ERROR_MESSAGE);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("startTaskflow,����",e);
			JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
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
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("startGroup,����",e);
			JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void stopTaskflow(Taskflow taskflow) {
		try {
			log.debug("ֹͣ����" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().stopTaskflow(
						taskflow.getTaskflow());
				log.debug(result);
				JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("stopTaskflow,����",e);
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
				
				if (result.indexOf("�ɹ�") > -1 ){
					JOptionPane.showMessageDialog(this, "�ɹ���������" + taskflow.getTaskflow(), "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
				}
				
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("stopTaskflow,����",e);
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
				if (result.indexOf("�ɹ�") > -1 ){
					JOptionPane.showMessageDialog(this, "�ɹ���������" + taskflow.getTaskflow(), "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("stopTaskflow,����",e);
		}
	}
	
	public void wakeupTaskflow(Taskflow taskflow) {
		try {
			log.debug("��������" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().wakeupTaskflow(
						taskflow.getTaskflow());
				log.debug(result);
				JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("wakeupTaskflow,����",e);
		}
	}
	

	public void skipTaskflow(Taskflow taskflow) {
		try {
			log.debug("��������" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().skipTaskflow(
						taskflow.getTaskflow());
				log.debug(result);
				JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("wakeupTaskflow,����",e);
		}
	}
	
	public void skipTask(Taskflow taskflow ,Task task) {
		try {
			log.debug("������������" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().skipTask(
						taskflow.getTaskflow(),task.getTask());
				log.debug(result);
				JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("wakeupTaskflow,����",e);
		}
	}
	
	public void suspendTask(Taskflow taskflow ,Task task) {
		try {
			log.debug("������������" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().suspendTask(
						taskflow.getTaskflow(),task.getTask());
				log.debug(result);
				if (result.indexOf("�ɹ�") > -1 ){
					JOptionPane.showMessageDialog(this, "�ɹ�������������" + task.getTask(), "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("suspendTask,����",e);
		}
	}
	
	public void cancelSuspendTask(Taskflow taskflow ,Task task) {
		try {
			log.debug("������������" + taskflow.getDescription());
			// flowMetaData.updateSuspendOfTaskflow(taskflow.getTaskflowID(),
			// Taskflow.SUSPEND_YES);
			try {
				String result = Xmlrpc.getInstance().cancelSuspendTask(
						taskflow.getTaskflow(),task.getTask());
				log.debug(result);
				if (result.indexOf("�ɹ�") > -1 ){
					JOptionPane.showMessageDialog(this, "�ɹ�������������" + task.getTask(), "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, result, "ϵͳ��Ϣ",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (XmlRpcException e) {
				log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("cancelSuspendTask,����",e);
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
				JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("stopGroup,����",e);
			JOptionPane.showMessageDialog(this, e.getMessage(), "ϵͳ��Ϣ",
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
