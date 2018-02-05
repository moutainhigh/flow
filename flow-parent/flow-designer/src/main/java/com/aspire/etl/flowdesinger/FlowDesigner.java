package com.aspire.etl.flowdesinger;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import com.aspire.etl.analyse.ReportAnalyse;
import com.aspire.etl.flowdefine.Category;
import com.aspire.etl.flowdefine.SysConfig;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.flowmonitor.ConsoleDialog;
import com.aspire.etl.flowmonitor.FlowMonitor;
import com.aspire.etl.metadatamanager.frame.MainFrame;
import com.aspire.etl.tool.Utils;
import com.aspire.etl.tool.XmlConfig;
import com.aspire.etl.uic.LogonDialog;
import com.aspire.etl.uic.OutlineTaskflowDialog;
import com.aspire.etl.uic.TaskDialog;
import com.aspire.etl.uic.TaskflowDialog;
import com.aspire.etl.uic.WcpAction;
import com.aspire.etl.uic.WcpFileFilter;
import com.aspire.etl.uic.WcpImagePool;
import com.aspire.etl.uic.WcpMessageBox;
import com.l2fprod.common.swing.JOutlookBar;
import com.l2fprod.common.swing.PercentLayout;



/**
 * 
 * @author 罗奇
 * @since 2008-3-20
 *        <p>
 *        1.规范图标命名，插件面板图标改为32*32。
 *        </p>
 *        <p>
 *        2.将插件面板改为outlook风格，增加分类目录。tasktype对应增加catalog属性
 *        </p>
 *        <p>
 *        3.插件显示的任务插件全部动态插入，包括插件面板上的分类目录。
 *        </p>
 * @author 罗奇
 * @since 2008-3-21
 *        <P>
 *        1.打开流程编辑时，禁止相同的流程开两个窗口，跳转到已打开的窗口。
 *        </p>
 * @author 罗奇
 * @since 2008-3-22
 *        <p>
 *        1.新建流程时检查同名流程是否存在，并提示。 2.将任务动态参数对话框改成动态加载
 *        </p>
 * 
 * @author 罗奇
 * @since 2008-3-23
 *        <p>
 *        1.程序标题栏加图标,同时将所有图标路径都改为images开头。
 *        </p>
 *        <p>
 *        2.在空白界面上拖拉时提示先打开流程。 3."加载流程"改为"打开流程"
 *        </p>
 *        <p>
 *        4.增加"导入流程"和"导出流程"对话框
 *        </p>
 * 
 * @author 罗奇
 * @since 2008-3-24
 *        <p>
 *        1.插件面板上的分类目录增加排序 2.在标题栏提示当前元数据库信息
 *        </p>
 * 
 * @author 罗奇
 * @since 2008-3-27
 *        <p>
 *        1.修改ant脚本 2.将任务配置对话框的代码独立出去，改为从plugins目录加载任务配置对话框的jar包。
 *        </p>
 * 
 * @author 罗奇
 * @since 2008-3-28
 *        <p>
 *        1.将FlowMetaData的代码独立出去，改为从lib目录加载jar包。
 *        </p>
 * @author 罗奇
 * @since 2008-3-30
 *        <p>
 *        1.区分内部插件和外部插件，将外部插件的配置完全放在plugins目录下，不用修改系统已有的插件配置。
 *        </p>
 * 
 * 
 * @author wangcaiping
 * @since 2008-8-8
 *        <p>
 *        喜迎奥运，将设计器流程面板从InternalFrame改为TabbedPane
 *        </p>
 * @since 2008-8-12
 *        <p>
 *        将UI公用类封装，修改设计器初始化方法
 *        </p>
 * 
 * @author 罗奇
 * @since 2009-12-22
 *        <p>
 *        1.增加血统分析和影响分析功能。
 *        2.增加直接从大纲流程上打开流程。
 *        </p>
 *        
 * @author jiangts
 * @since 2009-12-29
 *        <p>
 *        1.切换到大纲时，插件面板要更换（当为大纲流程时，插件面板只显示大纲插件，否则为其他类型的常用C++插件和第三方插件）
 *          方法：initOutlookBar()
 *        </p>        
 */
public class FlowDesigner extends JFrame implements WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String copyright = "\n\n------------------------------------------------------\n作者：罗奇，王才平 \n版本：2.0.0.0";

	static FlowDesigner flowDesigner = null;

	private final static Logger log =Logger.getLogger(FlowDesigner.class);
	
	public static FlowDesigner getInstance() {
		if (flowDesigner == null) {
			return new FlowDesigner();
		} else {
			return flowDesigner;
		}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				flowDesigner = new FlowDesigner();
			}
		});
	}

	private FlowMetaData flowMetaData;

	private JTabbedPane desktop;

	private TaskflowDialog dlgTaskflow;

	private TaskDialog dlgTask;

	

	private WcpAction openOutlineFlowAction, editCellAction,
			removeAction, saveAction, undoAction, redoAction, importFlowAction,
			exportFlowAction, copyAction, pasteAction, zoomInAction,
			zoomOutAction, zoomResetAction, infoAction, expImgAction,
			lineageAnalysisAction, impactAnalysisAction,openMetaDataManagerAction,
			openFlowMonitorAction,openFlowConsoleAction,
			taskSearchAction,newOutlineFlowAction,outlineFlowListAction,
			importOutlineFlowAction,exportOutlineFlowAction,
			checkDataFlowAction,generateDataFlowAction,horizontalLayoutAction,logonAction,logoutAction;

	private JPopupMenu popupMenu;

	private JOutlookBar outlookBar;
	
	private JTabbedPane tabbedPane;

//	private TaskflowChooser taskflowChooser;
	private OutlineTaskflowChooser outlineTaskflowChooser;
	
	private TaskSearchDialog taskSearcher;

	private String userID;
	
	private String password;

	//引擎是否处于运行状态
	private boolean isEngineRunning;
	
	//是否正在编辑
	private boolean isEditing;
	
	private String subTitle = "";

	private String denseDescription;
	
	//流程监控器
	private FlowMonitor monitor;
	
	//命令行流程监控器
	private ConsoleDialog console;
	
	//元数据管理窗口
	private MainFrame mf;
	
	/**
	 * 报表分析
	 */
	private ReportAnalyse reportAnalyse;
	
	private TaskTypeComparator taskTypeComparater = new TaskTypeComparator();

	/**
	 * 流程设计器
	 * 
	 * @author wangcaiping,20080708
	 * @param fmd
	 */
	public FlowDesigner() {
		logon();
	}

	private void logon(){

		LogonDialog ld = new LogonDialog("流程设计器", true);
		if (ld.showDialog() == LogonDialog.SUCCESS_RESULT) {
			//reportAnalyse = new ReportAnalyse();
			JFrame.setDefaultLookAndFeelDecorated(false);
			this.flowMetaData = ld.getFlowMetaData();
			isEngineRunning =  ld.isEngineRunning();
			if (isEngineRunning) {
				subTitle = "(引擎已经启动)";
				String alert = "引擎 <" + ld.getEngineServer() + ":"
						+ ld.getEnginePort() + "> 已启动。" + "\n设计器将无法保存所有修改操作！";
				WcpMessageBox.warn(this, alert);
			} else if (ld.FS_REPOSITORY != ld.getRepositoryType()){//如果引擎没有运行，判断有没有正在编辑中，如果没有，则提示用户是否要编辑

				SysConfig sysConfig = flowMetaData.querySysConfig("CURRENT_DESIGNER_HOST");
				if (sysConfig != null){
					String hostName = sysConfig.getConfigValue();
					if (!hostName.equals(getHostName())){
						subTitle = "(主机 <" + sysConfig.getConfigValue() + "> 正在编辑流程)";
						String alert ="主机 <" + sysConfig.getConfigValue() + "> 正在编辑流程。" + "\n设计器将无法保存所有修改操作！";
						WcpMessageBox.warn(this, alert);
						isEditing = false;
						isEngineRunning = true;
					} else {
						isEditing = true;
						isEngineRunning = false;
					}
				} else {

					int ret = JOptionPane.showConfirmDialog(FlowDesigner.this,
							"是否要编辑流程?", "确认?", JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						boolean isOk = flowMetaData.insertSysConfig(getHostConfig());
						if (isOk){
							isEditing = true;
							isEngineRunning = false;
						} else {
							SysConfig sysConf = flowMetaData.findSysConfig("CURRENT_DESIGNER_HOST");
							subTitle = "(主机 <" + sysConf.getConfigValue() + "> 正在编辑流程)";
							String alert ="主机 <" + sysConf.getConfigValue() + "> 正在编辑流程。" + "\n设计器将无法保存所有修改操作！";
							WcpMessageBox.warn(this, alert);
							isEditing = false;
							isEngineRunning = true;
						}
					} else {
						subTitle = "";
						isEditing = false;
						isEngineRunning = true;
					}
				}

			} else {
				isEditing = true;
				isEngineRunning = false;
			}
			
			init(ld.getFlowMetaData(), ld.getUserID(),ld.getPassword(), isEngineRunning);
			this.setVisible(true);
			denseDescription = ld.getDenseDescription() + copyright;
			//reportAnalyse.start();
		}
		ld.dispose();
	
	}
	
	 private static String getHostName(){
	    	try {
	    		return InetAddress.getLocalHost().getHostName();
	    	} catch (UnknownHostException e) {
	    	}

	    	return null;
	    }   
	
	private void init(FlowMetaData fmd, String user,String password, boolean isEngineRunning) {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		this.userID = user;
		this.password = password;
		this.isEngineRunning = isEngineRunning;
		desktop = new JTabbedPane();
		TabMouseHandler tmh = new TabMouseHandler();
		desktop.addMouseListener(tmh);
		desktop.addMouseMotionListener(tmh);
		this.getContentPane().add(desktop, BorderLayout.CENTER);

		this.flowMetaData = fmd;
		// initialize plugins
		try {
			initExternalPlugins();
		} catch (FileNotFoundException fnfe) {
			//fnfe.printStackTrace();
			log.error("init()出错：",fnfe);
		} catch (DocumentException de) {
			//de.printStackTrace();
			log.error("init()出错：",de);
		}
		// actions and buttons
		try {
			outlookBar = new JOutlookBar();
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("插件面板", outlookBar);
			this.getContentPane().add(tabbedPane, BorderLayout.WEST);
		} catch (Exception e) {
			//e.printStackTrace();
			log.error("init()出错：",e);
		}

		initActions();
		initToolBar();
		initMenuBar();
		initPopupMenus();
		// task bar
		initOutlookBar();

		// meta data
		// dialog
		JDialog.setDefaultLookAndFeelDecorated(true);
		dlgTaskflow = new TaskflowDialog(this, flowMetaData);
		dlgTaskflow.setLocationRelativeTo(this);
		dlgTask = new TaskDialog(this, flowMetaData);
		dlgTask.setLocationRelativeTo(this);
		
		// taskflow chooser
		outlineTaskflowChooser = new OutlineTaskflowChooser(this, flowMetaData, userID,
				isEngineRunning);
		
		taskSearcher = new TaskSearchDialog(this, flowMetaData);
		// ui
		this.setIconImage(WcpImagePool.getImage(this.getClass(),
				"images/taskflow.png"));
		String title = "ETL流程设计器";
		
		if (isEngineRunning || !isEditing) {
			title += " -只读" + subTitle;
		} 
		
		
		this.setTitle(title);
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.addWindowListener(this);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		List<Taskflow> tfs = flowMetaData.queryTaskflowInGroup(FlowDesignerConstants.OUTLINE_GROUP_ID);
		
		for (Taskflow tf : tfs) {
			openTaskflow(tf);
		}
	}

	private void initExternalPlugins() throws FileNotFoundException,
			DocumentException {
		File[] childDirArray = Utils.scanChildDir("plugins");
		if ((childDirArray == null) || (childDirArray.length == 0)) {
			return;
		}
		// 逐个读取plugin.xml
		FilenameFilter ff = new FilenameFilter() {
			public boolean accept(File f, String name) {
				if (name.equalsIgnoreCase("plugin.xml")) {
					return true;
				}
				return false;
			}
		};
		for (File pd : childDirArray) {
			File[] pluginFileArray = pd.listFiles(ff);
			if (pluginFileArray != null && pluginFileArray.length != 0) {
				if (pluginFileArray[0] != null) {
					String pluginFile = pluginFileArray[0].getAbsolutePath();

					XmlConfig pluginFileConfig = new XmlConfig(pluginFile);
					String name = pluginFileConfig
							.readSingleNodeValue("//plugin/name");
					String description = pluginFileConfig
							.readSingleNodeValue("//plugin/description");
					String largeIcon = pluginFileConfig
							.readSingleNodeValue("//plugin/large_icon");
					String smallIcon = pluginFileConfig
							.readSingleNodeValue("//plugin/small_icon");
					String category = pluginFileConfig
							.readSingleNodeValue("//plugin/category");
					String classname = pluginFileConfig
							.readSingleNodeValue("//plugin/classname");
					String library = pluginFileConfig
							.readSingleNodeValue("//plugin/library");
					String secondCategory = pluginFileConfig
					.readSingleNodeValue("//plugin/second_category");

					TaskType taskTypeBean = flowMetaData.queryTaskType(name);
					if (taskTypeBean == null) {
						TaskType tmpTaskType = new TaskType();
						tmpTaskType.setTaskTypeID(Utils.getRandomIntValue());
						tmpTaskType.setTaskType(name);

						Category categoryBean = flowMetaData
								.queryCategory(category);
						if (categoryBean == null) {
							// 如果分类不存在,新建一个临时Category
							Category tmpCategory = new Category();
							tmpCategory.setID(Utils.getRandomIntValue());
							tmpCategory.setName(category);
							tmpCategory.setOrder(99);
							

							// 保存到内存，但是不保存到元数据库里
							flowMetaData.insert(tmpCategory);

							tmpTaskType.setCategoryID(tmpCategory.getID());
						} else {
							// 如果分类已存在
							tmpTaskType.setCategoryID(categoryBean.getID());
						}
						tmpTaskType.setDescription(description);
						tmpTaskType.setDesignerPlugin(classname);
						tmpTaskType.setDesignerPluginJar("plugins/" + name
								+ "/" + library);
						tmpTaskType.setLargeIcon(pluginFileArray[0].getParent()
								+ "/" + largeIcon);
						tmpTaskType.setSmallIcon(pluginFileArray[0].getParent()
								+ "/" + smallIcon);
						
						tmpTaskType.setSecondCategory(secondCategory);
						
						// 保存到内存，但是不保存到元数据库里
						flowMetaData.insert(tmpTaskType);
					} else {
						String msg = "元数据库里已经有名称为\"" + name + "\"的插件，请将你的插件改名！";
						WcpMessageBox.warn(FlowDesigner.this, msg);
					}
				}
			}
		}
	}

	/**
	 * Check Authorization.
	 * 
	 * @author wangcaiping,20080718
	 * 
	 */
	private void initActions() {
		// flow
		openOutlineFlowAction = new OpenOutlineFlowAction();
		openOutlineFlowAction.setEnabled(true);
//		loadFlowAction = new OpenFlowAction();
//		loadFlowAction.setEnabled(flowMetaData
//				.isPermit(userID, "DESIGNER_OPEN"));
		saveAction = new SaveAction();

		// edit
		editCellAction = new EditAction();
		copyAction = new CopyAction();
		pasteAction = new PasteAction();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		removeAction = new RemoveAction();
		importFlowAction = new ImportFlowAction();
		importFlowAction.setEnabled(flowMetaData.isPermit(userID,
				"DESIGNER_XML_IMPORT"));
		exportFlowAction = new ExportFlowAction();
		horizontalLayoutAction = new HorizontalLayoutAction();
		checkDataFlowAction = new CheckDataFlowAction();
		generateDataFlowAction = new GenerateDataFlowAction();
		
		importOutlineFlowAction = new ImportOutlineFlowAction();
		importOutlineFlowAction.setEnabled(flowMetaData.isPermit(userID,
		"DESIGNER_XML_IMPORT"));
		exportOutlineFlowAction = new ExportOutlineFlowAction();
		expImgAction = new ExpImageAction();
		// view
		zoomInAction = new ZoomInAction();
		zoomOutAction = new ZoomOutAction();
		zoomResetAction = new ZoomResetAction();
		logonAction = new LogonAction();
		logoutAction = new LogoutAction();
		// info
		infoAction = new InfoAction();
		//血统分析
		lineageAnalysisAction = new LineageAnalysisAction();
		
		// 影响分析
		impactAnalysisAction = new ImpactAnalysisAction();
		
		//任务搜索
		taskSearchAction = new TaskSearchAction();
		
		openMetaDataManagerAction = new OpenMetaDataManagerAction();
		
		openFlowMonitorAction = new OpenFlowMonitorAction();
		openFlowConsoleAction = new OpenFlowConsoleAction();
		newOutlineFlowAction = new NewOutlineFlowAction();
		outlineFlowListAction = new OutlineFlowListAction();
		setActionsEnabled(false);
		
	}

	private void setActionsEnabled(boolean b) {
		// flow
		saveAction.setEnabled(/*b && */(!isEngineRunning));
		
		newOutlineFlowAction.setEnabled(/*b && */(!isEngineRunning));
		importOutlineFlowAction.setEnabled(/*b && */(!isEngineRunning));
		importFlowAction.setEnabled(/*b && */(!isEngineRunning));
		//outlineFlowListAction.setEnabled(!isEngineRunning);
		// edit
		editCellAction.setEnabled(b);
		copyAction.setEnabled(b);
		pasteAction.setEnabled(b);
		taskSearchAction.setEnabled(b);
		undoAction.setEnabled(b);
		redoAction.setEnabled(b);
		removeAction.setEnabled(b);
		exportFlowAction.setEnabled(b);
		horizontalLayoutAction.setEnabled(b);
		exportOutlineFlowAction.setEnabled(b);
		expImgAction.setEnabled(b);
		// view
		zoomInAction.setEnabled(b);
		zoomOutAction.setEnabled(b);
		zoomResetAction.setEnabled(b);
		
		lineageAnalysisAction.setEnabled(b);
		impactAnalysisAction.setEnabled(b);
		taskSearchAction.setEnabled(b);
		
	}

	private void initToolBar() {
		JPanel pnlTB = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// edit
		JToolBar tb2 = new JToolBar();
	//	addButton(editCellAction, tb2);
		addButton(removeAction, tb2);
		addButton(copyAction, tb2);
		addButton(pasteAction, tb2);
		addButton(undoAction, tb2);
		addButton(redoAction, tb2);
		// task flow
		JToolBar tb1 = new JToolBar();
		addButton(outlineFlowListAction, tb1);
		//addButton(loadFlowAction, tb1);
		addButton(saveAction, tb1);

		// 流程导入，导出
		JToolBar tb3 = new JToolBar();
		//addButton(importFlowAction, tb3);
		addButton(exportFlowAction, tb3);
		addButton(expImgAction, tb3);

		// view
		JToolBar tb4 = new JToolBar();
		addButton(zoomInAction, tb4);
		addButton(zoomOutAction, tb4);
		addButton(zoomResetAction, tb4);

		// help
		JToolBar tb5 = new JToolBar();
		addButton(infoAction, tb5);

		//
		JToolBar tb6 = new JToolBar();
		addButton(openMetaDataManagerAction, tb6);
		addButton(openFlowMonitorAction, tb6);
		addButton(openFlowConsoleAction, tb6);
		//addButton(newOutlineFlowAction, tb6);
	

		// add all tool bars
		pnlTB.add(tb1);
		tb1.setFloatable(false);
		pnlTB.add(tb2);
		tb2.setFloatable(false);
		pnlTB.add(tb3);
		tb3.setFloatable(false);
		pnlTB.add(tb4);
		tb4.setFloatable(false);

		pnlTB.add(tb6);
		tb6.setFloatable(false);

		pnlTB.add(tb5);
		tb5.setFloatable(false);

		this.getContentPane().add(pnlTB, BorderLayout.PAGE_START);
	}

	private void initMenuBar() {
		JMenuBar mb = new JMenuBar();
		// edit
		JMenu muEdit = new JMenu("编辑");
		addMenuItem(removeAction, muEdit);
		addMenuItem(copyAction, muEdit);
		addMenuItem(pasteAction, muEdit);
		addMenuItem(taskSearchAction, muEdit);//只有在JMenuItem注册了菜单项，快捷键太生效
		//addMenuItem(editCellAction, muEdit);
		addMenuItem(saveAction, muEdit);
	// outline
		JMenu muOutline = new JMenu("大纲");
		
		addMenuItem(newOutlineFlowAction, muOutline);
		addMenuItem(outlineFlowListAction, muOutline);
		//addMenuItem(openOutlineFlowAction, muFlow);
		muOutline.addSeparator();
		addMenuItem(importOutlineFlowAction, muOutline);
		addMenuItem(exportOutlineFlowAction, muOutline);
		
		//addMenuItem(loadFlowAction, muFlow);
		muOutline.addSeparator();
		addMenuItem(expImgAction, muOutline);
		
		muOutline.addSeparator();
		addMenuItem(logonAction, muOutline);
		addMenuItem(logoutAction, muOutline);
		
		//taskflow
		JMenu muFlow = new JMenu("流程");
		
		muFlow.addSeparator();
		addMenuItem(importFlowAction, muFlow);
		addMenuItem(exportFlowAction, muFlow);
		
		muFlow.addSeparator();
		addMenuItem(expImgAction, muFlow);
		
		
	//	addMenuItem(checkDataFlowAction, muFlow);
	//	addMenuItem(generateDataFlowAction, muFlow);
		
		// view
		JMenu muView = new JMenu("视图");
		addMenuItem(zoomInAction, muView);
		addMenuItem(zoomOutAction, muView);
		addMenuItem(zoomResetAction, muView);
		muView.addSeparator();
		addMenuItem(horizontalLayoutAction, muView);
		
		// help
		JMenu muHelp = new JMenu("帮助");
		addMenuItem(infoAction, muHelp);

		JMenu muProgram = new JMenu("程序");
		addMenuItem(openMetaDataManagerAction, muProgram);
		addMenuItem(openFlowMonitorAction, muProgram);
		addMenuItem(openFlowConsoleAction, muProgram);
	
		// add all menus
		mb.add(muOutline);
		mb.add(muFlow);
		mb.add(muEdit);
		mb.add(muView);
		mb.add(muProgram);
		mb.add(muHelp);
		this.setJMenuBar(mb);
	}

	private void initPopupMenus() {
		popupMenu = new JPopupMenu();
		addPopupMenuItem(copyAction, popupMenu);
		addPopupMenuItem(pasteAction, popupMenu);
		addPopupMenuItem(editCellAction, popupMenu);
		addPopupMenuItem(removeAction, popupMenu);
		
		//增加任务节点搜索 2009-12-30 jiangts
		addPopupMenuItem(taskSearchAction, popupMenu);
		
		popupMenu.addSeparator();
		addPopupMenuItem(saveAction, popupMenu);

		// 增加元数据影响分析 2009-12-22 罗奇
		popupMenu.addSeparator();
		addPopupMenuItem(lineageAnalysisAction, popupMenu);
		addPopupMenuItem(impactAnalysisAction, popupMenu);
		
		
	}

	private void addMenuItem(WcpAction action, JMenu mu) {
		JMenuItem mi = new JMenuItem(action);
		mu.add(mi);
	}

	private void addButton(WcpAction action, JToolBar tb) {
		JButton btn = tb.add(action);
		btn.setToolTipText(action.getText());
	}

	private void addPopupMenuItem(WcpAction action, JPopupMenu pm) {
		JMenuItem mi = new JMenuItem(action);
		pm.add(mi);
	}
	
	
	
	/**
	 * 初始化左侧插件面板
	 * 修改项：1.切换到大纲时，插件面板要更换（当为大纲流程时，插件面板只显示大纲插件，否则为其他类型的常用C++插件和第三方插件）
	 */
	private void initOutlookBar() {
		
		
		//清除outlookBar
		outlookBar.removeAll();
		
		boolean isOutline = false;

		//判断当前打开窗口是否为大纲流程窗口，应用启动默认为大纲流程
		Component comp = desktop.getSelectedComponent();
		
		if (comp != null) {
			FlowPanel ff = (FlowPanel) comp;

			if (ff.getTaskflow().getGroupID().equals(FlowDesignerConstants.OUTLINE_GROUP_ID)){
				isOutline = true;
			}

			// 加载元数据库里已经定义的tasktype
			List<Category> catalogList = flowMetaData.queryCategoryList();
			for (Category catalog : catalogList) {

				//如果当前为大纲流程，但却不是大纲插件，则不生成outlook菜单
				if (isOutline && !catalog.getName().equals("大纲插件")){
					continue;
				} else if (!isOutline && catalog.getName().equals("大纲插件")){//如果当前不为大纲流程，但却是大纲插件，则不生成outlook菜单
					continue;
				}

				List<TaskType> typeList = flowMetaData.queryTaskTypeListByCategory(catalog
						.getID());
				
				List secondtypeList = flowMetaData.queryTaskTypeListBySecondCategory(catalog.getID());
				typeList.addAll(secondtypeList);
				Object[] arrays = typeList.toArray();
				Arrays.sort(arrays,taskTypeComparater);
				typeList.clear();
				for(Object obj: arrays){
					typeList.add((TaskType)obj);
				}
				
				
				Iterator iter = typeList.iterator();
				ArrayList<AddTaskAction> taskActions = new ArrayList<AddTaskAction>();

				while (iter.hasNext()) {
					TaskType tt = (TaskType) iter.next();
					// faint,20080716
					if (catalog.getID() == 1) {
						// 常用插件
						AddTaskAction ata = new AddTaskAction(tt);
						taskActions.add(ata);
					} else {
						// 网络插件和第三方插件
						AddTaskAction ata = new AddTaskAction(tt, true);
						taskActions.add(ata);
					}
				}
				// add note tab
				if (catalog.getName().equals("常用JAVA插件")) {
					AddTaskAction ana = new AddTaskAction();
					taskActions.add(ana);
				}

				outlookBar.addTab(catalog.getName(),
						outlookbarCatagoryAddButton(taskActions));
			}
		}
	}

	private JScrollPane outlookbarCatagoryAddButton(
			ArrayList<AddTaskAction> taskActionList) {
		JPanel panel = new JPanel();
		panel.setLayout(new PercentLayout(PercentLayout.VERTICAL, 0));
		panel.setOpaque(false);

		Iterator iter = taskActionList.iterator();
		while (iter.hasNext()) {
			addTaskButton((AddTaskAction) iter.next(), panel);
		}

		JScrollPane scroll = outlookBar.makeScrollPane(panel);
		return scroll;
	}

	private void addTaskButton(AddTaskAction action, Container container) {
		JButton button = new JButton(action.getText(), (ImageIcon) action
				.getValue(AbstractAction.SMALL_ICON));
		try {
			button.setUI((ButtonUI) Class.forName(
					(String) UIManager.get("OutlookButtonUI")).newInstance());
		} catch (Exception e) {
			WcpMessageBox.postException(this, e);
			log.error("addTaskButton()出错：",e);
		}
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setMaximumSize(new Dimension(100, 50));
		ButtonDragHandler bdh = new ButtonDragHandler(action.getTaskType(),
				action.isNote());

		button.addMouseMotionListener(bdh);
		button.addMouseListener(bdh);
		container.add(button);
	}

	public FlowPanel addFlowPanel(Taskflow tf) {
		if (tf == null) {
			return null;
		}
		FlowPanel fp = new FlowPanel(tf, popupMenu, dlgTask, dlgTaskflow,
				flowMetaData);
		if (tf.getGroupID() == FlowDesignerConstants.OUTLINE_GROUP_ID) {
			desktop.addTab(tf.getTaskflow() + FlowDesignerConstants.OUTLINE_PREFIX, new WcpCloseIcon(), fp, tf.toString());
		} else {
			desktop.addTab(tf.getTaskflow(), new WcpCloseIcon(), fp, tf.toString());
		}
		desktop.setSelectedComponent(fp);
		initOutlookBar();
		return fp;
	}

	public void openTaskflow(Taskflow taskflow) {
		
		if (taskflow == null) {
			return;
		}
		Component[] pnls = desktop.getComponents();
		int len = pnls.length;
		boolean exist = false;
		for (int i = 0; i < len; i++) {
			FlowPanel fp = (FlowPanel) pnls[i];
			Taskflow tf = fp.getTaskflow();
			if (tf.toString().equals(taskflow.toString())) {
				desktop.setSelectedIndex(i);
				exist = true;
				break;
			}
		}
		if (!exist) {
			FlowPanel fp = addFlowPanel(taskflow);
			fp.openTaskflow();
		}
		// check authorization,wangcaiping,20080728
		boolean b = flowMetaData.isTaskflowUser(userID, taskflow
				.getTaskflowID());
		if (!b) {
			WcpMessageBox.warn(this, "打开的流程\"" + taskflow.getTaskflow()
					+ "\"不属于当前用户\"" + userID + "\",\n当前用户对该流程仅有浏览权限.");
		}
		setActionsEnabled(b);
		initOutlookBar();
		
	}
	
	/**
	 * 修改tab页签的标题
	 * @param oldTitle
	 * @param newTitle
	 */
	public void changeTabTitle(String oldTitle,String newTitle){
		int tabIndex = desktop.indexOfTab(oldTitle);
		//desktop.getTitleAt(tabIndex);
		if (tabIndex > -1){
			desktop.setTitleAt(tabIndex, newTitle);
		}
	}
	
	/**
	 * 通过标题删除Tab页签
	 * @param title
	 */
	public void removeTabByTitle(String title){
		int tabIndex = desktop.indexOfTab(title);
		if (tabIndex > -1){
			desktop.removeTabAt(tabIndex);
		}
	}
	
	/**
	 * 判断是否为大纲配置文件
	 * @param xmlFileName
	 * @return
	 */
	public boolean isOutlineFlowFile(String xmlFileName){
		boolean isOutline = false;
		
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(xmlFileName));
			Node groupNode = doc.selectSingleNode("/taskflow/groupID");
			if (Integer.parseInt(groupNode.getStringValue()) == FlowDesignerConstants.OUTLINE_GROUP_ID){
				isOutline = true;
				return isOutline;
			}
		/*} catch (MalformedURLException e) {
			//e.printStackTrace();
			log.error("isOutlineFlowFile()出错：",e);*/
		} catch (DocumentException e) {
			//e.printStackTrace();
			log.error("isOutlineFlowFile()出错：",e);
		}
		
		return isOutline;
	}
	
	/**
	 * 查询第一个大纲流程,找不到返回null
	 * @return
	 */
	private Taskflow getFirstOutlineFlow(){
		List<Taskflow> list = flowMetaData.queryAllTaskflow();
		for(Taskflow taskflow :list){
			if (taskflow.getGroupID() == FlowDesignerConstants.OUTLINE_GROUP_ID){
				return taskflow;
			}
		}
		
		return null;
	}
	
	public FlowPanel getFirstOutlineFlowPanel(Taskflow taskflow){
		Component[] pnls = desktop.getComponents();
		int len = pnls.length;
		for (int i = 0; i < len; i++) {
			FlowPanel fp = (FlowPanel) pnls[i];
			Taskflow tf = fp.getTaskflow();
			if (tf.getGroupID() == FlowDesignerConstants.OUTLINE_GROUP_ID 
					&& tf.getTaskflow().equals(taskflow.getTaskflow())) {
				return fp;
			}
		}
		return null;
	}
	
	
	/**
	 * 判断XML文件中的任务所对应的流程在内存中是否已经存在
	 * @param xmlFileName
	 * @return
	 */
	public Taskflow existsTaskFlowInMemory(String xmlFileName){
		
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(xmlFileName));
			List<Node> taskNodeList = doc.selectNodes("/taskflow/task");
			for(Node taskNode: taskNodeList){
				String taskName = taskNode.selectSingleNode("name").getStringValue();
				return flowMetaData.queryTaskflow(taskName);
			}
			
		/*} catch (MalformedURLException e) {
			//e.printStackTrace();
			log.error("existsTaskFlowInMemory()出错：",e);*/
		} catch (DocumentException e) {
			//e.printStackTrace();
			log.error("existsTaskFlowInMemory()出错：",e);
		}
		
		return null;
	}
	
	public boolean save(){
		boolean isSuccess = false;
		Component comp = desktop.getSelectedComponent();
		String taskflowName = "";
		if (comp != null) {
			FlowPanel ff = (FlowPanel) comp;
			try {
				ff.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				taskflowName = ff.getTaskflow().getTaskflow();
				//如果是保存大纲流程，则把所有打开的流程都保存
				if (ff.getTaskflow().getGroupID() == FlowDesignerConstants.OUTLINE_GROUP_ID){
					
					for(Component panel : desktop.getComponents()){
						FlowPanel flowPanel = (FlowPanel) panel;
						
						//只对普通流程做保存动作
						if (flowPanel.getTaskflow().getGroupID() != FlowDesignerConstants.OUTLINE_GROUP_ID){
							flowPanel.save(userID);
						}
					}
				}
				
				if (ff.save(userID)) {
					isSuccess = true;
					WcpMessageBox.inform(FlowDesigner.this, "流程保存成功!");
					
				} 
			} catch (Exception e) {
				//e.printStackTrace();// test
				log.error("taskflow:[" + taskflowName + "] save() error：",e);
				// 保存失败时，删除内存中流程
				flowMetaData.deleteTaskflowInMemory(ff.getTaskflow()
						.getTaskflowID());
				WcpMessageBox.postException(FlowDesigner.this, e);
			} finally {
				ff.setCursor(new Cursor(
						Cursor.DEFAULT_CURSOR));
			}
		}
		
		return isSuccess;
	
	}

	private class OpenOutlineFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public OpenOutlineFlowAction() {
			super("打开所有大纲", FlowDesigner.class, "images/taskflow.png");
		}

		public void actionPerformed(ActionEvent ae) {
			List<Taskflow> tfs = flowMetaData.queryTaskflowInGroup(FlowDesignerConstants.OUTLINE_GROUP_ID);
			for (Taskflow tf : tfs) {
				openTaskflow(tf);
			}
			FlowDesigner.this.initOutlookBar();
		}
	}

	private class ImportOutlineFlowAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public ImportOutlineFlowAction() {
			super("从xml文件导入大纲", FlowDesigner.class, "images/btn_imp.png");
		}

		public void actionPerformed(ActionEvent ae) {

			try {
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

				String taskflowXmlFilePath = null;
				String taskflowXmlFileName = null;
				String taskflowName = null;

				JFileChooser chooser = new JFileChooser(); // 文件选择器
				chooser
						.setFileFilter(new WcpFileFilter(new String[] { "xml" }));
				// chooser.setCurrentDirectory(new File("c:/"));
				int result = chooser.showOpenDialog(FlowDesigner.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					taskflowXmlFilePath = chooser.getSelectedFile()
							.getAbsolutePath();
					taskflowXmlFileName = chooser.getSelectedFile().getName();
					String[] tmp = taskflowXmlFileName.split(".xml");

					taskflowName = tmp[0];

					//先通过解析xml文件中的流程类型是否为大纲，如果不是大纲提示错误：
					if (FlowDesigner.this.isOutlineFlowFile(taskflowXmlFilePath)){

						// 先查一下元数据库,是否有同名的流程，有则返回ID
						Integer id = flowMetaData.getTaskflowIDbyName(taskflowName);

						if (id != null) {// 同名流程已存在,不导入
							JOptionPane.showMessageDialog(FlowDesigner.this,
							"同名大纲已存在！请修改大纲名称后再导入!");

						} else {
							// 再判断同ID的流程是否存在，如果存在则提示不允许覆盖！

							//先判断任务所对应的流程是否已经存在，如果存在提示不能导入：
							Taskflow taskflow = existsTaskFlowInMemory(taskflowXmlFilePath);
							if(taskflow != null){
								String outlineFlowName = flowMetaData.queryTaskflowByOutlineTask(flowMetaData.queryOutlineTaskByTaskflow(taskflow)).getTaskflow();
								JOptionPane.showMessageDialog(FlowDesigner.this,
								"xml文件中的任务名已经在大纲[" + outlineFlowName + "]中存在，请修改文件内容再重新导入!");

							} else{

								// 先导入到内存
								flowMetaData.importTaskflowInfo(taskflowXmlFilePath);

								//自动建好大纲中的任务所对应的流程（不处理）

								// 再打开编辑窗口，在点击保存后才真正导入到元数据库中。
								openTaskflow(flowMetaData.queryTaskflow(taskflowName));
							}
						}
					} else {//非大纲配置文件，提示错误
						JOptionPane.showMessageDialog(FlowDesigner.this,
						"当前选择的xml文件并非为大纲配置文件，请重新选择!");
					}
				}

			} catch (MetaDataException e) {
				log.error(e);
				WcpMessageBox.postException(FlowDesigner.this, e);
			} catch (Exception e) {
				WcpMessageBox.postException(FlowDesigner.this, e);
			}finally{
				FlowDesigner.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

		}
	}
	

	private class ExportOutlineFlowAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public ExportOutlineFlowAction() {
			super("大纲导出为xml文件...", FlowDesigner.class, "images/btn_exp.png");
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

				FlowPanel FlowPanel = (FlowPanel) comp;
				FlowDesigner.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setDialogTitle("请选择导出到的目录");
				if (jfc.showSaveDialog(FlowDesigner.this) == JFileChooser.APPROVE_OPTION) {
					String path = jfc.getSelectedFile().getAbsolutePath();
					//Taskflow taskflowToExport = FlowPanel.getTaskflow();
					try {
						/*flowMetaData.exportTaskflowInfo(taskflowToExport
								.getTaskflowID(), path);*/
						
						//直接从graph取流程信息，这样只读时可以导出xml
						FlowPanel.saveTaskflowInfo(path);
					} catch (Exception e) {
						WcpMessageBox.postException(FlowDesigner.this, e);
					}
				}

			} else {
				WcpMessageBox.warn(FlowDesigner.this, "不能在空白界面上操作！请先打开一个流程！");
			}
		}
	}
	
	
	private class ImportFlowAction extends WcpAction {
		private static final long serialVersionUID = 1L;
		
		public ImportFlowAction() {
			super("从xml文件导入流程", FlowDesigner.class, "images/btn_imp.png");
		}
		
		public void actionPerformed(ActionEvent ae) {
			
			try {
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				
				String taskflowXmlFilePath = null;
				String taskflowXmlFileName = null;
				String taskflowName = null;
				int taskflowID = 0;
				JFileChooser chooser = new JFileChooser(); // 文件选择器
				chooser
				.setFileFilter(new WcpFileFilter(new String[] { "xml" }));
				// chooser.setCurrentDirectory(new File("c:/"));
				int result = chooser.showOpenDialog(FlowDesigner.this);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					taskflowXmlFilePath = chooser.getSelectedFile()
					.getAbsolutePath();
					taskflowXmlFileName = chooser.getSelectedFile().getName();
					String[] tmp = taskflowXmlFileName.split(".xml");
					
					taskflowName = tmp[0];
					
					Taskflow xmlTaskflow = null;
					Taskflow targetOutlineTaskflow = null;
					boolean isNewTaskflow = false;   //如果没有在大纲流程出现任务名，则为新的流程
					boolean targetOutlineOpened = false;
					
					Map map = flowMetaData.parseTaskflowXML(taskflowXmlFilePath);
					
					xmlTaskflow = (Taskflow)map.get("inTaskflow") ;
					
					if (xmlTaskflow.getTaskflow().equals("")){
						JOptionPane.showMessageDialog(FlowDesigner.this,
						"xml文件内容不是有效的流程文件,请重新选择!");
						return ;
					} 
					
					List<Taskflow> outlineTaskflowList = flowMetaData.queryTaskflowInGroup(FlowDesignerConstants.OUTLINE_GROUP_ID);
					
					if(outlineTaskflowList.size() == 0){
						JOptionPane.showMessageDialog(FlowDesigner.this,
						"xml文件内容不能导入，请先创建大纲!");
						return ;
					}
						
					
					taskflowName = xmlTaskflow.getTaskflow();
					taskflowID = xmlTaskflow.getTaskflowID();
					//如果流程未在大纲中找到相应的任务节点，并且大纲超过两个时，提示用户选择大纲
					if(flowMetaData.queryOutlineTaskByTaskflow(xmlTaskflow) == null){
						isNewTaskflow = true;
						if(outlineTaskflowList.size() > 1){
							
							OutlineChooser outlineChooser = new OutlineChooser(FlowDesigner.this, flowMetaData, userID,isEngineRunning);
							outlineChooser.showDialog();
							if (!outlineChooser.isApproved()) {
								return;
							}
							
							Taskflow[] tfs = outlineChooser.getSelectedTaskflows();
							targetOutlineTaskflow = tfs[0];
						}
					}
					
					//先通过解析xml文件中的流程类型是否为普通流程，如果不是普通流程提示错误：
					if (!FlowDesigner.this.isOutlineFlowFile(taskflowXmlFilePath)){
						
						// 先查一下元数据库,是否存在有相同ID的流程信息
						Taskflow tf = flowMetaData.queryTaskflow(taskflowID);
						Integer id = flowMetaData.getTaskflowIDbyName(taskflowName);
						if (tf != null || id != null) {// 流程已存在,不导入
							JOptionPane.showMessageDialog(FlowDesigner.this,
							"该流程已存在！请重新选择流程文件!");

						} else {
							
							
							// 再判断同ID的流程是否存在，如果存在则提示不允许覆盖！
							// 先导入到内存
							flowMetaData.importTaskflowInfo(taskflowXmlFilePath);
							Taskflow newTaskflow = flowMetaData.queryTaskflow(taskflowID);
							
							//判断在大纲中是否存在流程，不存在则自动创建一个大纲任务，并提示调整大纲中的任务节点。（通过搜索定位，并调整位置）
							if(isNewTaskflow){
								//flowMetaData.insert(arg0)
								
								if (targetOutlineTaskflow == null){//如果只有一个大纲流程，则直接选择它
									
									targetOutlineTaskflow = getFirstOutlineFlow();
								}
									
									//如果已经打开有大纲面板，则插入到第一个大纲面板中，否则插入到查询出来的第一个大纲中
									FlowPanel outlineFlowPanel = getFirstOutlineFlowPanel(targetOutlineTaskflow);
									if (outlineFlowPanel != null){
										//outlineFlow = outlineFlowPanel.getTaskflow();
										outlineFlowPanel.addOutlineTask(null,targetOutlineTaskflow, newTaskflow);
									} else{
										Task task = new Task();
										task.setTaskID(Utils.getRandomIntValue());
										task.setAlertID(String.valueOf(task.getTaskID()));
										task.setPerformanceID(String.valueOf(task.getTaskID()));
										task.setTask(newTaskflow.getTaskflow());
										task.setTaskflowID(targetOutlineTaskflow.getTaskflowID());
										task.setDescription(newTaskflow.getDescription());
										task.setTaskType(FlowDesignerConstants.TASK_TYPE_OUTLINE);
										flowMetaData.insert(task);
									}
									
									JOptionPane.showMessageDialog(FlowDesigner.this,
									"该流程在大纲[" + targetOutlineTaskflow.getTaskflow()+ "]中自动增加了大纲任务节点，请打开大纲调整位置(通过搜索定位到该任务节点，并调整位置).");
							}
							
							
							// 再打开编辑窗口，在点击保存后才真正导入到元数据库中。
							openTaskflow(flowMetaData.queryTaskflow(taskflowName));
						}
					}else{//不是普通流程提示错误
						JOptionPane.showMessageDialog(FlowDesigner.this,
						"当前选择的xml文件为大纲配置文件，不能导入到普通流程中，请重新选择!");
					}
				}
				
			} catch (MetaDataException e) {
				log.error(e);
				WcpMessageBox.postException(FlowDesigner.this, e);
			} catch (Exception e) {
				WcpMessageBox.postException(FlowDesigner.this, e);
			}finally{
				FlowDesigner.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			
		}
	}
	
	private class ExportFlowAction extends WcpAction {
		private static final long serialVersionUID = 1L;
		
		public ExportFlowAction() {
			super("流程导出为xml文件...", FlowDesigner.class, "images/btn_exp.png");
		}
		
		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				
				FlowPanel FlowPanel = (FlowPanel) comp;
				FlowDesigner.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setDialogTitle("请选择导出到的目录");
				if (jfc.showSaveDialog(FlowDesigner.this) == JFileChooser.APPROVE_OPTION) {
					String path = jfc.getSelectedFile().getAbsolutePath();
					//Taskflow taskflowToExport = FlowPanel.getTaskflow();
					try {
						/*flowMetaData.exportTaskflowInfo(taskflowToExport
								.getTaskflowID(), path);*/
						
						//直接从graph取流程信息，这样只读时可以导出xml
						FlowPanel.saveTaskflowInfo(path);
					} catch (Exception e) {
						WcpMessageBox.postException(FlowDesigner.this, e);
					}
				}
				
			} else {
				WcpMessageBox.warn(FlowDesigner.this, "不能在空白界面上操作！请先打开一个流程！");
			}
		}
	}

	private class CheckDataFlowAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public CheckDataFlowAction() {
			super("检查数据流图节点...", FlowDesigner.class, "images/btn_exp.png");
		}

		public void actionPerformed(ActionEvent ae) {
			if (!reportAnalyse.isLoadedPackage()){
				WcpMessageBox.inform(FlowDesigner.this, "正在加载REPORT数据库信息，请稍后再重试");
				return ;
			}
			
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				FlowPanel flowPanel = (FlowPanel) comp;
				
				//只处理普通流程
				if (!flowPanel.getTaskflow().getGroupID().equals(FlowDesignerConstants.OUTLINE_GROUP_ID)){
					flowPanel.checkDataflow();
				}
				
				FlowDesigner.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
			} else {
				WcpMessageBox.warn(FlowDesigner.this, "不能在空白界面上操作！请先打开一个流程！");
			}
		}
	}
	
	
	private class GenerateDataFlowAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public GenerateDataFlowAction() {
			super("生成数据流图节点...", FlowDesigner.class, "images/btn_exp.png");
		}

		public void actionPerformed(ActionEvent ae) {
			
			if (!reportAnalyse.isLoadedPackage()){
				WcpMessageBox.inform(FlowDesigner.this, "正在加载REPORT数据库信息，请稍后再重试");
				return ;
			}
			
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				FlowPanel flowPanel = (FlowPanel) comp;
				
				//只处理普通流程
				if (!flowPanel.getTaskflow().getGroupID().equals(FlowDesignerConstants.OUTLINE_GROUP_ID)){
					flowPanel.generateDataflow();
				}
				
				FlowDesigner.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
			} else {
				WcpMessageBox.warn(FlowDesigner.this, "不能在空白界面上操作！请先打开一个流程！");
			}
		}
	}
	
	
	private class HorizontalLayoutAction extends WcpAction {
		private static final long serialVersionUID = 1L;
		
		public HorizontalLayoutAction() {
			super("水平方向层次布局", FlowDesigner.class, "images/btn_exp.png");
		}
		
		public void actionPerformed(ActionEvent ae) {
			
			
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel flowPanel = (FlowPanel) comp;
				flowPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				flowPanel.horizontalLayout();
				flowPanel.setCursor(Cursor.getDefaultCursor());
			} else {
				WcpMessageBox.warn(FlowDesigner.this, "不能在空白界面上操作！请先打开一个流程！");
			}
		}
	}
	
	
	private class AddTaskAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		private String taskType;

		private boolean isNote;

		public AddTaskAction(TaskType tt) {
			super(tt.getDescription(), FlowDesigner.class, tt.getSmallIcon());
			this.taskType = tt.getTaskType();
			isNote = false;
		}

		public AddTaskAction(TaskType tt, boolean isPlugin) {
			super(tt.getDescription(), tt.getSmallIcon(), true);
			this.taskType = tt.getTaskType();
			isNote = false;
		}

		public AddTaskAction() {
			super("注释", FlowDesigner.class, "images/note_32.png");
			this.isNote = true;
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				ff.addTask(taskType);
			}
		}

		public String getTaskType() {
			return taskType;
		}

		public boolean isNote() {
			return isNote;
		}
	}

	/*private class OpenFlowAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public OpenFlowAction() {
			super("打开大纲流程", FlowDesigner.class, "images/open.png");
		}

		public void actionPerformed(ActionEvent ae) {
			taskflowChooser.showDialog();
			if (!taskflowChooser.isApproved()) {
				return;
			}
			
			List<Taskflow> tfs = flowMetaData.queryTaskflowInGroup(FlowDesignerConstants.OUTLINE_GROUP_ID);
			for (Taskflow tf : tfs) {
				openTaskflow(tf);
			}
		}
	}*/

	private class EditAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public EditAction() {
			super("编辑", FlowDesigner.class, "images/edit.png");
		}

		public void actionPerformed(ActionEvent arg0) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				try {
					ff.edit(false);
				} catch (MetaDataException e) {
					log.error(e);
					WcpMessageBox.postException(FlowDesigner.this, e);
				}
			}
		}
	}

	private class RemoveAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public RemoveAction() {
			super("删除", FlowDesigner.class, "images/btn_del.png");
			this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		}

		public void actionPerformed(ActionEvent arg0) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				
				//选择的对象不为空时，才提示
				if (ff.getGraph().getSelectionCell() != null){
				int ret = -1;
				//如果为大纲流程
				if(ff.getTaskflow().getGroupID() == FlowDesignerConstants.OUTLINE_GROUP_ID){
					ret = JOptionPane.showConfirmDialog(FlowDesigner.this,
							"如果选定的对象为流程任务，则相应的流程也同时被删除,确定删除选定对象?", "删除?", JOptionPane.YES_NO_OPTION);
				} else {
					ret = JOptionPane.showConfirmDialog(FlowDesigner.this,
							"确定删除选定对象?", "删除?", JOptionPane.YES_NO_OPTION);
				}
				
				if (ret == JOptionPane.OK_OPTION) {
					ff.remove();
				}
				
				}
			}
		}
	}

	private class SaveAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public SaveAction() {
			super("保存", FlowDesigner.class, "images/save.png");
			this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent arg0) {
			try{
				FlowDesigner.this.setCursor(Cursor.WAIT_CURSOR);
				FlowDesigner.this.save();
			}catch(Exception e){}
			finally{
				FlowDesigner.this.setCursor(Cursor.DEFAULT_CURSOR);
			}
		}
	}

	private class LineageAnalysisAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public LineageAnalysisAction() {
			super("血统分析", FlowDesigner.class, "images/left.png");
		}

		public void actionPerformed(ActionEvent arg0) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;

				Object obj = ff.getGraph().getSelectionCell();
				if ((obj != null) && (obj instanceof DefaultGraphCell)) {
					DefaultGraphCell cell = (DefaultGraphCell) obj;
					Object userObj = cell.getUserObject();
					if ((userObj != null) && userObj instanceof Task) {
						Task task = (Task) userObj;
						Taskflow taskFlow = flowMetaData.queryTaskflow(task.getTaskflowID());
						MetadataAnalysisWindow metadataAnalysisWindow = 
							new MetadataAnalysisWindow("[" + taskFlow.getTaskflow() + "." + task.getDescription() + "]元数据血统分析",flowMetaData);

						metadataAnalysisWindow.drawAnalysisGraph(task,
								"LineageAnalysis");
						metadataAnalysisWindow.setVisible(true);
					}

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
			
			//只有在JMenuItem注册了菜单项，快捷键太生效,在initMenuBar()方法中注册菜单
			this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
					ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			
			if (comp != null) {
				
				FlowPanel ff = (FlowPanel) comp;
				taskSearcher.showDialog(ff.getTaskflow());
				if (!taskSearcher.isApproved()) {
					return;
				}
				Task resultTask = taskSearcher.getSelectedTask();
				//开始定位
				JGraph graph = ff.getGraph();
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
							ff.getScrollPane().getHorizontalScrollBar().setValue(x);
							ff.getScrollPane().getVerticalScrollBar().setValue(y);
							graph.repaint();
							break;
						}
						
					} 
				}
			}
		}
	}

	private class ImpactAnalysisAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public ImpactAnalysisAction() {
			super("影响分析", FlowDesigner.class, "images/right.png");
		}

		public void actionPerformed(ActionEvent arg0) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;

				Object obj = ff.getGraph().getSelectionCell();
				if ((obj != null) && (obj instanceof DefaultGraphCell)) {
					DefaultGraphCell cell = (DefaultGraphCell) obj;
					Object userObj = cell.getUserObject();
					if ((userObj != null) &&  userObj instanceof Task) {
						Task task = (Task) userObj;
						Taskflow taskFlow = flowMetaData.queryTaskflow(task.getTaskflowID());
						MetadataAnalysisWindow metadataAnalysisWindow = 
							new MetadataAnalysisWindow("[" + taskFlow.getTaskflow() + "." + task.getDescription() + "]元数据影响分析",flowMetaData);

						metadataAnalysisWindow.drawAnalysisGraph(task,
								"ImpactAnalysis");
						metadataAnalysisWindow.setVisible(true);
					}

				}
			}

		}
	}

	private class TabMouseHandler implements MouseListener, MouseMotionListener {
		private WcpCloseIcon wci;

		private boolean isOverIcon;

		private int tabIndex;

		public TabMouseHandler() {
			isOverIcon = false;
			tabIndex = -1;
		}

		public void mouseMoved(MouseEvent me) {
			tabIndex = desktop.indexAtLocation(me.getX(), me.getY());
			if (tabIndex < 0) {
				return;
			}
			wci = (WcpCloseIcon) desktop.getIconAt(tabIndex);
			isOverIcon = wci.contains(me.getX(), me.getY());
			if (isOverIcon) {
				wci.setLightColor(true);
				desktop.repaint();
			} else {
				wci.setLightColor(false);
				desktop.repaint();
			}
		}

		public void mouseClicked(MouseEvent arg0) {
			
			
			if (tabIndex < 0) {
				return;
			}
			
			//FlowDesigner.this.initOutlookBar();
			
			FlowPanel fp = (FlowPanel) desktop.getComponentAt(tabIndex);
			Taskflow tf = fp.getTaskflow();
			boolean b = flowMetaData.isTaskflowUser(userID, tf.getTaskflowID());
			
			// if icon is clicked, close the selected tab
			if (!isOverIcon) {
				setActionsEnabled(b);
				return;
			}

			if (!b || isEngineRunning) {
				desktop.removeTabAt(tabIndex);
				FlowDesigner.this.initOutlookBar();
				return;
			}

			if (fp.isEdited()){
				int ret = JOptionPane.showConfirmDialog(FlowDesigner.this,
						"保存当前流程设计?", "保存?", JOptionPane.YES_NO_CANCEL_OPTION);
				if (ret == JOptionPane.OK_OPTION) {
					try {
						
						fp.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						fp.save(userID);
					
					} catch (Exception e) {
						WcpMessageBox.postException(FlowDesigner.this, e);
					}finally{
						fp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				}
				//如果选择否，则撤消操作，重新加载流程信息：
				else if (ret == JOptionPane.NO_OPTION) {
					try {
						flowMetaData.loadTaskflowInfo(tf.getTaskflowID());
						Taskflow curTaskflow = flowMetaData.queryTaskflow(tf.getTaskflowID());
						
						//修改outlineTask的描述
						Task outlineTask = flowMetaData.queryOutlineTaskByTaskflow(curTaskflow);
						if (outlineTask != null){
							outlineTask.setTask(curTaskflow.getTaskflow());
							outlineTask.setDescription(curTaskflow.getDescription());
						}
					} catch (MetaDataException e) {
						//e.printStackTrace();
						log.error("TabMouseHandler.mouseClicked()出错：",e);
					}
				}
			}
			
			desktop.removeTabAt(tabIndex);
			
			FlowDesigner.this.initOutlookBar();
		
		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
			if (tabIndex < 0) {
				return;
			}
			FlowDesigner.this.initOutlookBar();
		}

		public void mouseDragged(MouseEvent arg0) {
		}
	}

	private class CopyAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Action action;

		public CopyAction() {
			super("复制", FlowDesigner.class, "images/btn_copy.png");
			this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					ActionEvent.CTRL_MASK));
			this.action = TransferHandler.getCopyAction();
		}

		public void actionPerformed(ActionEvent ae) {
			
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				
				ff.postAction(action, ae);
			}
		}
	}
	

	private class PasteAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public PasteAction() {
			super("粘贴", FlowDesigner.class, "images/btn_paste.png");
			this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				ff.paste();
			}
		}
	}

	private class UndoAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UndoAction() {
			super("撤销", FlowDesigner.class, "images/btn_undo.png");
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				try {
					ff.undo();
				} catch (CannotUndoException e) {
					WcpMessageBox.postException(FlowDesigner.this, e);
				}
			}
		}
	}

	private class RedoAction extends WcpAction {
		private static final long serialVersionUID = 1L;

		public RedoAction() {
			super("恢复", FlowDesigner.class, "images/btn_redo.png");
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				try {
					ff.redo();
				} catch (CannotRedoException e) {
					WcpMessageBox.postException(FlowDesigner.this, e);
				}
			}
		}
	}

	private class ZoomInAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ZoomInAction() {
			super("放大", FlowDesigner.class, "images/zoom_in.png");
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				ff.zoomIn();
			}
		}
	}

	private class ZoomOutAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ZoomOutAction() {
			super("缩小", FlowDesigner.class, "images/zoom_out.png");
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				ff.zoomOut();
			}
		}
	}

	private class ZoomResetAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ZoomResetAction() {
			super("重置", FlowDesigner.class, "images/zoom_reset.png");
		}

		public void actionPerformed(ActionEvent ae) {
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				ff.zoomReset();
			}
		}
	}
	
	private class LogonAction extends WcpAction {
		
		private static final long serialVersionUID = 1L;
		
		public LogonAction() {
			super("重新登录", FlowDesigner.class, "images/btn_logout.png");
		}
		
		public void actionPerformed(ActionEvent ae) {
			int ret = JOptionPane.showConfirmDialog(FlowDesigner.this,
					"重新登录将导致所有未保存的设计丢失?\r\n确认要重新登录?", "重新登录?", JOptionPane.YES_NO_CANCEL_OPTION);
			if (ret == JOptionPane.YES_OPTION) {
				if (isEditing){
					flowMetaData.deleteSysConfig(getHostConfig());
				}
				FlowDesigner.this.dispose();
				if (monitor != null) monitor.dispose();
				if (console != null) console.dispose();
				if (mf != null) mf.dispose();
				
				flowDesigner = new FlowDesigner();
				/*String user_dir = System.getProperty("user.dir");
				Runtime rt = Runtime.getRuntime();
				Process p = null;
				String[] command = null;
					//windows系统
					//到目录
					log.info("Shell_WorkDir:" + user_dir);
//					command = "CMD.EXE /k cd " + workDir + "& " + shell;
					command = new String[]{"CMD.EXE", "/C", "start start.bat"};
					try {
						String tmp_dir = System.getProperty("java.io.tmpdir");
						String java_home = (String) SysProb.getEnv().get("JAVA_HOME");
						p = rt.exec(command, new String[]{"java.io.tmpdir=" + tmp_dir,"JAVA_HOME=" + java_home,"PATH=%PATH%;" + java_home + "/bin"}, new File(user_dir));
						Thread.sleep(500);
					} catch (Exception e) {
						log.error(e);
					}*/
				
			}
		}
	}
	
	private class LogoutAction extends WcpAction {
		
		private static final long serialVersionUID = 1L;
		
		public LogoutAction() {
			super("关闭", FlowDesigner.class, "images/btn_exit.png");
		}
		
		public void actionPerformed(ActionEvent ae) {
			windowClosing(null);
		}
	}

	private class InfoAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InfoAction() {
			super("设计属性", FlowDesigner.class, "images/info.png");
		}

		public void actionPerformed(ActionEvent ae) {
			WcpMessageBox.inform(FlowDesigner.this, denseDescription);
		}
	}
	
	private class NewOutlineFlowAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NewOutlineFlowAction() {
			super("新建大纲", FlowDesigner.class, "images/btn_new.png");
		}

		public void actionPerformed(ActionEvent ae) {
			Taskflow newTaskflow = new Taskflow();
			newTaskflow.setTaskflowID(Utils.getRandomIntValue());
			
			//设置为大纲流程组
			newTaskflow.setGroupID(FlowDesignerConstants.OUTLINE_GROUP_ID);
			
			boolean b = new OutlineTaskflowDialog(FlowDesigner.this,flowMetaData).showDialog(newTaskflow);
			if (b) {
				flowMetaData.insert(newTaskflow, userID);
				addFlowPanel(newTaskflow);
				setActionsEnabled(true);
			}
		}
	}

	
	private class OutlineFlowListAction extends WcpAction {
		
		private static final long serialVersionUID = 1L;

		public OutlineFlowListAction() {
			super("大纲列表", FlowDesigner.class, "images/taskflow.png");
		}

		public void actionPerformed(ActionEvent ae) {
			outlineTaskflowChooser.showDialog();
			if (!outlineTaskflowChooser.isApproved()) {
				return;
			}
			Taskflow[] tfs = outlineTaskflowChooser.getSelectedTaskflows();
			for (Taskflow tf : tfs) {
				openTaskflow(tf);
			}
		}
	}
	
	private class OpenFlowMonitorAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private boolean isOpening = false;

		public OpenFlowMonitorAction() {
			super("流程监控器", FlowDesigner.class, "images/FlowMonitor.png");
		}

		public void actionPerformed(ActionEvent ae) {
			if (!isOpening) {
				isOpening = true;
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				Thread thread = new Thread(){
					public void run(){
						if (monitor == null || !monitor.isShowing()){
							monitor = 
								new FlowMonitor(flowMetaData.querySysConfigValue("SYS_COMPUTER_IP"),
										flowMetaData.querySysConfigValue("XMLRPC_PORT"),
										FlowDesigner.this.getUserID(),
										FlowDesigner.this.getPassword()); 
						} else {
							monitor.show();
						}
						FlowDesigner.this.setCursor(Cursor.getDefaultCursor());
						isOpening = false;
					
					}
				};
				thread.start();
			}
		}
	}
	
	private class OpenFlowConsoleAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private boolean isOpening = false;
		
		public OpenFlowConsoleAction() {
			super("命令行流程监控器", FlowDesigner.class, "images/xmlrpc_16.png");
		}
		
		public void actionPerformed(ActionEvent ae) {
			if (!isOpening) {
				isOpening = true;
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				Thread thread = new Thread(){
					public void run(){
						if (console == null || !console.isShowing()){
						console = 
						new ConsoleDialog(flowMetaData.querySysConfigValue("SYS_COMPUTER_IP"),
								flowMetaData.querySysConfigValue("XMLRPC_PORT"),
								FlowDesigner.this.getUserID(),
								FlowDesigner.this.getPassword()); 
						} else {
							console.show();
						}
						FlowDesigner.this.setCursor(Cursor.getDefaultCursor());
						isOpening = false;
						
					}
				};
				thread.start();
			}
		}
	}


	private class OpenMetaDataManagerAction extends WcpAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private boolean isOpening = false;
		
		public OpenMetaDataManagerAction() {
			super("元数据管理", FlowDesigner.class, "images/MetaDataManager.png");
		}

		public void actionPerformed(ActionEvent ae) {
			if (!isOpening) {
				isOpening = true;
				FlowDesigner.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				Thread thread = new Thread(){
					public void run(){
						
						if (mf == null || !mf.isShowing()){
							
							mf = new MainFrame(flowMetaData,FlowDesigner.this.getUserID(),FlowDesigner.this.getDenseDescription());
						}  else {
							mf.show();
						}
						FlowDesigner.this.setCursor(Cursor.getDefaultCursor());
						isOpening = false;
						mf.setVisible(true);
					}
				};
				thread.start();

			}
		}
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
			
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				jfc.setSelectedFile(new File(ff.getTaskflow().getTaskflow() + ".JPG"));
				
				if (jfc.showSaveDialog(FlowDesigner.this) == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					try {
						ff.exportImage(f);
						WcpMessageBox.informSaving(FlowDesigner.this, f);
					} catch (IOException e) {
						WcpMessageBox.postException(FlowDesigner.this, e);
					}
				}
			}
		}
	}

	private class ButtonDragHandler implements MouseMotionListener,
			MouseListener {
		private Cursor moveCursor;

		private Cursor defaultCursor;

		private String taskType;

		private boolean isNote;
		
		boolean isDrag = false;

		public ButtonDragHandler(String taskType, boolean isNote) {
			moveCursor = DragSource.DefaultMoveDrop;
			defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			this.taskType = taskType;
			this.isNote = isNote;
		}

		public void mouseDragged(MouseEvent me) {
			FlowDesigner.this.setCursor(moveCursor);
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				FlowPanel ff = (FlowPanel) comp;
				ff.setCursor(moveCursor);
				ff.getGraph().setCursor(moveCursor);
				isDrag = true;
			} else {
				WcpMessageBox
						.warn(FlowDesigner.this, "不能在空白界面上操作，请先打开流程或新建流程！");
				FlowDesigner.this.setCursor(defaultCursor);
			}
		}

		public void mouseClicked(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {

			if (!isDrag){
				return ;
			}
			
			Component comp = desktop.getSelectedComponent();
			if (comp != null) {
				comp.setCursor(defaultCursor);
				FlowPanel ff = (FlowPanel) comp;
				
				//设置为面板状态为：已做修改
				ff.setEdited(true);
				
				if (isNote) {
					ff.addNote();
				} else {
					
					//如果是ETL流程插件，则弹出新建流程对话框
					if (taskType.equals(FlowDesignerConstants.TASK_TYPE_OUTLINE)){
						Point mousePosition = ff.getGraph().getMousePosition();
						Taskflow taskflow = ff.addNewTaskFlow();
						Taskflow outlineTaskFlow = ff.getTaskflow();
						if (taskflow != null){
							
							ff.addOutlineTask(mousePosition,outlineTaskFlow,taskflow);
						}
					} else {
						ff.addTask(taskType);
					}
				}
				ff.setCursor(defaultCursor);
				ff.getGraph().setCursor(defaultCursor);
				isDrag = false;
			} else {
				WcpMessageBox
						.warn(FlowDesigner.this, "不能在空白界面上操作，请先打开流程或新建流程！");
			}
			FlowDesigner.this.setCursor(defaultCursor);
		}

		public void mouseMoved(MouseEvent arg0) {

		}
	}

	public void windowActivated(WindowEvent arg0) {
	}

	public void windowClosed(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
		int cnt = desktop.getTabCount();
		if (cnt == 0) {
			this.dispose();
			System.exit(0);
			return;
		}
		int ret = JOptionPane.showConfirmDialog(this,
				"关闭流程设计器将导致所有未保存的设计丢失！\n确认要关闭流程设计器？", "关闭确认",
				JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.YES_OPTION) {
			if (isEditing){
				flowMetaData.deleteSysConfig(getHostConfig());
			}
			this.dispose();
			System.exit(0);
		}
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}

	public String getDenseDescription() {
		return denseDescription;
	}

	public String getUserID() {
		return userID;
	}
	public String getPassword() {
		return password;
	}

	public ReportAnalyse getReportAnalyse() {
		return reportAnalyse;
	}

	public boolean isEngineRunning() {
		return isEngineRunning;
	}
	
	private SysConfig getHostConfig(){
		SysConfig newSysConfig = new SysConfig();
		newSysConfig.setConfigDesc("当前正编辑流程设计器的主机名");
		newSysConfig.setConfigName("CURRENT_DESIGNER_HOST");
		newSysConfig.setConfigValue(getHostName());
		newSysConfig.setID(0);
		newSysConfig.setStage("DESIGN");
		newSysConfig.setType("DESIGN");
		
		return newSysConfig;
	}
	
	private class TaskTypeComparator implements Comparator{

	    public int compare(final Object o1,final Object o2) {
	        final TaskType p1=(TaskType)o1;
	        final TaskType p2=(TaskType)o2;

	       if(p1.getDescription().compareTo(p2.getDescription()) > 0)
	           return 1;
	       else
	          return 0;
	       }
	}
}
