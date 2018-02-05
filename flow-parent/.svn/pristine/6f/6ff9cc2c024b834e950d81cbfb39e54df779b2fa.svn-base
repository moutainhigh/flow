package com.aspire.etl.uic;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import javax.swing.JComboBox;

/**
 * 登录
 * 
 * @since 20080704
 * @author wangcaiping
 * 
 *         修改登录历史的顺序，最近登录信息排在最前面，
 *         修改函数为：initLogonHist（增加排序），FillHistoryHandler（设置登录时间）
 *         *Entry类对setXMLElement和setXMLElement做了登录时间修改
 * @since 20080704
 * @author wangcaiping
 * 
 */
public class LogonDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		LogonDialog ld = new LogonDialog("Designer", true);
		int i = ld.showDialog();
		System.out.println("result: " + i);
	}

	private JTextField txtPSServer, txtPort, txtPSPort, txtServer, txtUser, txtDBUser, txtSID, txtAlias, txtPath;

	private JPasswordField txtDBPswd, txtPswd;

	private JPanel pnlDB, pnlDBMain, pnlProxy, pnlDirect, pnlFile, pnlMain;

	private JPopupMenu pmDirect, pmProxy, pmDirectory;

	private javax.swing.JButton btnHist;
	private javax.swing.JComboBox cbDbType;// 数据库类型 ：oracle,mysql
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel lbCharSet;
	private javax.swing.JPanel pnl;
	private javax.swing.JTextField txtCharSet;

	private final String logonHistFile;

	private HashMap<String, ConnectionEntry> hmLogon;

	private JButton btnOk;

	private String title;

	private FlowMetaData flowMetaData;

	private String userID;

	private String password;

	public static final int SUCCESS_RESULT = 0;

	public static final int FAILURE_RESULT = -1;

	private int result;

	private JProgressBar progressBar;

	private boolean isEngineRunning;

	private String engineServer, enginePort;

	private int repositoryType, connectType;

	public final static int DB_REPOSITORY = 1;

	public final static int FS_REPOSITORY = 2;

	public final static int DIRECT_CONNECTION = 1;

	public final static int PROXY_CONNECTION = 2;

	public final static int DIRECTORY_CONNECTION = 3;

	public final static String[] DB_TYPES = { "Oracle", "MySQL" };

	private String repositoryAddress;

	public LogonDialog(String title, String logonFile, boolean needFileSystem) {
		this.setTitle(title + " - 登录    ");
		this.setModal(true);
		this.setResizable(false);
		this.setSize(500, 465);
		this.setLocationRelativeTo(null);
		this.title = title;
		this.result = FAILURE_RESULT;
		this.isEngineRunning = false;
		Container cont = this.getContentPane();
		cont.setLayout(new BorderLayout());
		// init logon history
		this.logonHistFile = logonFile;
		initLogonHist();
		// create panels
		pnlMain = new JPanel(new BorderLayout());
		pnlDB = createDBPanel();
		// database panel is default
		pnlMain.add(pnlDB);
		repositoryType = DB_REPOSITORY;
		// buttons
		progressBar = new JProgressBar(0, 5);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		JPanel pnlBtn = createButtonPanel();
		JPanel pSouth = new JPanel(new BorderLayout());
		pSouth.add(pnlBtn, BorderLayout.CENTER);
		pSouth.add(progressBar, BorderLayout.SOUTH);
		JPanel pAuth = createAuthorizationPanel();
		JPanel p = new JPanel(new BorderLayout());
		p.add(pnlMain, BorderLayout.CENTER);
		p.add(pAuth, BorderLayout.SOUTH);
		// if file system is needed,show selection panel
		if (needFileSystem) {
			JPanel pS = createDestinationPanel();
			p.add(pS, BorderLayout.NORTH);
			pnlFile = createFilePanel();
		}
		cont.add(p, BorderLayout.CENTER);
		cont.add(pSouth, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public LogonDialog(String title, boolean fileSystem) {

		this(title, System.getProperty("java.io.tmpdir") + "ETL_Logon.xml", fileSystem);
	}

	private JPanel createButtonPanel() {
		JPanel pnl = new JPanel();
		btnOk = new JButton("确定");
		this.getRootPane().setDefaultButton(btnOk);
		JButton btnCancel = new JButton("取消");
		JButton btnClear = new JButton("清除登录历史");
		btnClear.addActionListener(new ClearHistoryHandler());
		pnl.add(btnOk);
		pnl.add(btnCancel);
		pnl.add(btnClear);
		btnOk.addActionListener(new OpenHandler());
		btnCancel.addActionListener(new ExitHandler());
		return pnl;
	}

	private JPanel createAuthorizationPanel() {
		JPanel pnl = new JPanel(new GridLayout(1, 2));
		pnl.setBorder(BorderFactory.createTitledBorder("操作授权"));
		JLabel lblUser = new JLabel("授权用户:");
		txtUser = new JTextField(10);
		JPanel p1 = new JPanel();
		p1.add(lblUser);
		p1.add(txtUser);
		JLabel lblPswd = new JLabel("授权密码:");
		txtPswd = new JPasswordField(10);
		JPanel p2 = new JPanel();
		p2.add(lblPswd);
		p2.add(txtPswd);
		pnl.add(p1);
		pnl.add(p2);
		return pnl;
	}

	@SuppressWarnings({ "unchecked", "null" })
	private void initLogonHist() {
		hmLogon = new HashMap<String, ConnectionEntry>();
		pmDirect = new JPopupMenu();
		pmProxy = new JPopupMenu();
		pmDirectory = new JPopupMenu();
		File f = new File(logonHistFile);

		if (!f.exists()) {
			return;
		}
		// get all logon histories
		SAXReader reader = new SAXReader();
		try {
			ArrayList<ConnectionEntry> historys = new ArrayList<ConnectionEntry>();
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			ConnectionEntry ce = null;
			List elmDirects = root.elements(DirectEntry.Tag);
			Iterator iter1 = elmDirects.iterator();
			Element elmEnt;
			FillHistoryHandler fhh = new FillHistoryHandler();
			JMenuItem mi = null;
			while (iter1.hasNext()) {
				elmEnt = (Element) iter1.next();
				ce = new DirectEntry();
				ce.setXMLElement(elmEnt);
				hmLogon.put(ce.toString(), ce);
				historys.add(ce);
			}

			entryListSort(historys);
			for (ConnectionEntry entry : historys) {
				mi = pmDirect.add(entry.toString());
				mi.addActionListener(fhh);
			}
			historys.clear();

			List elmProxys = root.elements(ProxyEntry.Tag);

			Iterator iter2 = elmProxys.iterator();
			while (iter2.hasNext()) {
				elmEnt = (Element) iter2.next();
				ce = new ProxyEntry();
				ce.setXMLElement(elmEnt);
				hmLogon.put(ce.toString(), ce);
				historys.add(ce);
			}

			entryListSort(historys);
			for (ConnectionEntry entry : historys) {
				mi = pmProxy.add(entry.toString());
				mi.addActionListener(fhh);
			}
			historys.clear();

			List elmDirs = root.elements(DirectoryEntry.Tag);
			Iterator iter3 = elmDirs.iterator();
			while (iter3.hasNext()) {
				elmEnt = (Element) iter3.next();
				ce = new DirectoryEntry();
				ce.setXMLElement(elmEnt);
				hmLogon.put(ce.toString(), ce);
				historys.add(ce);
			}

			entryListSort(historys);
			for (ConnectionEntry entry : historys) {
				mi = pmDirectory.add(entry.toString());
				mi.addActionListener(fhh);
			}
			historys.clear();

		} catch (DocumentException e) {
		}
	}

	@SuppressWarnings("unchecked")
	private void recordLogon(ConnectionEntry ce) {
		ce.setLogonTime(System.currentTimeMillis());
		hmLogon.put(ce.toString(), ce);
		// XML Document
		Element root = new DefaultElement("Connections");
		Iterator iter = hmLogon.values().iterator();
		ConnectionEntry item;
		while (iter.hasNext()) {
			item = (ConnectionEntry) iter.next();
			root.add(item.getXMLElement());
		}
		Document document = DocumentHelper.createDocument();
		document.setRootElement(root);
		File f = new File(logonHistFile);
		try {
			FileWriter fw = new FileWriter(f);
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");
			XMLWriter xw = new XMLWriter(fw, format);
			xw.write(document);
			xw.close();
			fw.close();
		} catch (IOException e) {
		}
	}

	private JPanel createDestinationPanel() {
		JPanel pnl = new JPanel(new GridLayout(1, 2));
		pnl.setBorder(BorderFactory.createTitledBorder("元数据存储目标"));
		JRadioButton rbDB = new JRadioButton("数据库");
		JRadioButton rbFile = new JRadioButton("文件系统");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbDB);
		bg.add(rbFile);
		pnl.add(rbDB);
		pnl.add(rbFile);
		rbDB.setSelected(true);
		DestinationSelectionHandler dsh1 = new DestinationSelectionHandler(DB_REPOSITORY);
		rbDB.addActionListener(dsh1);
		DestinationSelectionHandler dsh2 = new DestinationSelectionHandler(FS_REPOSITORY);
		rbFile.addActionListener(dsh2);
		return pnl;
	}

	private JPanel createProxyPanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(2, 2, 2, 2);
		pnl.setBorder(BorderFactory.createTitledBorder("代理服务器属性"));
		txtPSServer = new JTextField(20);
		txtPSPort = new JTextField(20);
		txtAlias = new JTextField(20);
		JButton btnHist = new JButton("...");
		btnHist.setToolTipText("历史信息");
		btnHist.setMargin(new Insets(0, 1, 0, 1));
		btnHist.addMouseListener(new ViewHistoryHandler(btnHist, pmProxy));
		addInputRow("服务器:", txtPSServer, 0, pnl, gbc, btnHist);
		addInputRow("端口:", txtPSPort, 1, pnl, gbc, null);
		addInputRow("数据库别名:", txtAlias, 2, pnl, gbc, null);
		return pnl;
	}

	private JPanel createDirectPanel() {
		pnl = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		cbDbType = new javax.swing.JComboBox();
		btnHist = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		txtServer = new javax.swing.JTextField();
		txtPort = new javax.swing.JTextField();
		txtSID = new javax.swing.JTextField();
		txtDBUser = new javax.swing.JTextField();
		txtDBPswd = new javax.swing.JPasswordField();
		txtCharSet = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		lbCharSet = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		pnl.setBorder(javax.swing.BorderFactory.createTitledBorder("数据库连接属性"));
		pnl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		jLabel1.setText("数据库类型:");
		pnl.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, -1, -1));

		cbDbType.setModel(
				new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		pnl.add(cbDbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 210, -1));

		btnHist.setText("...");
		btnHist.setToolTipText("历史信息");
		btnHist.addMouseListener(new ViewHistoryHandler(btnHist, pmDirect));
		pnl.add(btnHist, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 20, -1));

		jLabel2.setText("服务器:");
		pnl.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, -1, -1));
		pnl.add(txtServer, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 60, 210, -1));

		pnl.add(txtSID, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 120, -1));
		txtPort.setText("1521");
		pnl.add(txtPort, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, 50, -1));
		pnl.add(txtDBUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 120, 210, -1));
		pnl.add(txtDBPswd, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 210, -1));

		txtCharSet.setText("GBK");
		pnl.add(txtCharSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 180, 210, -1));

		jLabel4.setText("数据库名:");
		pnl.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, -1, -1));

		jLabel5.setText("用户:");
		pnl.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, -1, -1));

		jLabel6.setText("密码:");
		pnl.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 150, -1, 20));

		lbCharSet.setText("字符集:");
		pnl.add(lbCharSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, -1, -1));

		jLabel3.setText("端口:");
		pnl.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, -1, -1));
		lbCharSet.setVisible(false);
		txtCharSet.setVisible(false);
		return pnl;
	}

	private JPanel createDBPanel() {
		JPanel pnl = new JPanel(new BorderLayout());
		JPanel pr = new JPanel(new GridLayout(1, 2));
		pr.setBorder(BorderFactory.createTitledBorder("连接方式"));
		ButtonGroup bg = new ButtonGroup();
		JRadioButton rbProxy = new JRadioButton("代理服务器");
		JRadioButton rbDirect = new JRadioButton("直接连接");
		pr.add(rbProxy);
		pr.add(rbDirect);
		rbProxy.setSelected(true);
		bg.add(rbProxy);
		bg.add(rbDirect);
		pnlProxy = createProxyPanel();
		pnlDirect = createDirectPanel();
		ConnectionSelectionHandler csh1 = new ConnectionSelectionHandler(PROXY_CONNECTION);
		rbProxy.addActionListener(csh1);
		ConnectionSelectionHandler csh2 = new ConnectionSelectionHandler(DIRECT_CONNECTION);
		rbDirect.addActionListener(csh2);
		pnl.add(pr, BorderLayout.NORTH);
		pnlDBMain = new JPanel(new BorderLayout());
		pnlDBMain.add(pnlProxy);
		connectType = PROXY_CONNECTION;
		pnl.add(pnlDBMain, BorderLayout.CENTER);

		// 设置数据库类型
		cbDbType.setModel(new javax.swing.DefaultComboBoxModel(DB_TYPES));
		cbDbType.addActionListener(new CbDbTypeOnChangeHandler());

		return pnl;
	}

	private JPanel createFilePanel() {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		pnl.setBorder(BorderFactory.createTitledBorder("文件属性"));
		txtPath = new JTextField(30);

		JButton btnHist = new JButton("...");
		btnHist.setMargin(new Insets(0, 1, 0, 1));
		btnHist.setToolTipText("历史信息");
		btnHist.addMouseListener(new ViewHistoryHandler(btnHist, pmDirectory));

		JButton btnBrowse = new JButton("目录选择");
		btnBrowse.setMargin(new Insets(0, 1, 0, 1));
		btnBrowse.addActionListener(new BrowseFileHandler());

		pnl.add(new JLabel("目录:"));
		pnl.add(txtPath);
		pnl.add(btnHist);
		pnl.add(btnBrowse);
		return pnl;
	}

	private void addInputRow(String lbl, JComponent txt, int row, JPanel pnl, GridBagConstraints g, JButton btn) {
		JLabel lblFile = new JLabel(lbl);
		g.gridy = row;
		g.gridx = 0;
		g.gridwidth = 1;
		pnl.add(lblFile, g);
		g.gridx = 1;
		g.gridwidth = 3;
		pnl.add(txt, g);
		if (btn != null) {
			g.gridx = 4;
			g.gridwidth = 1;
			pnl.add(btn, g);
		}
	}

	private boolean testEngine() {
		try {
			engineServer = flowMetaData.querySysConfigValue("SYS_COMPUTER_IP");
			enginePort = flowMetaData.querySysConfigValue("XMLRPC_PORT");
			int port = Integer.parseInt(enginePort);
			Socket socket = null;

			socket = new Socket(engineServer, port);
			if (socket.isConnected()) {
				return true;
			}
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	private class DestinationSelectionHandler implements ActionListener {
		private int repository;

		public DestinationSelectionHandler(int rep) {
			this.repository = rep;
		}

		public void actionPerformed(ActionEvent arg0) {
			repositoryType = repository;
			pnlMain.removeAll();
			if (repositoryType == DB_REPOSITORY) {
				pnlMain.add(pnlDB);
			} else if (repositoryType == FS_REPOSITORY) {
				pnlMain.add(pnlFile);
			}
			pnlMain.revalidate();
			pnlMain.repaint();
		}
	}

	private class ConnectionSelectionHandler implements ActionListener {
		private int conn;

		public ConnectionSelectionHandler(int conn) {
			this.conn = conn;
		}

		public void actionPerformed(ActionEvent arg0) {
			connectType = conn;
			pnlDBMain.removeAll();
			if (connectType == PROXY_CONNECTION) {
				pnlDBMain.add(pnlProxy);
			} else if (connectType == DIRECT_CONNECTION) {
				pnlDBMain.add(pnlDirect);
			}
			pnlDBMain.revalidate();
			pnlDBMain.repaint();
		}
	}

	private class BrowseFileHandler implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser jfc = new JFileChooser();
			jfc.setCurrentDirectory(new File(txtPath.getText()));
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (jfc.showOpenDialog(LogonDialog.this) == JFileChooser.APPROVE_OPTION) {
				txtPath.setText(jfc.getSelectedFile().getAbsolutePath());
			}
		}
	}

	public int showDialog() {
		this.setVisible(true);
		return result;
	}

	private class OpenRunnable implements Runnable {
		public void run() {
			// waiting...
			btnOk.setEnabled(false);
			LogonDialog.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			try {
				progressBar.setValue(1);
				progressBar.setString("登录元数据库...");
				if (repositoryType == DB_REPOSITORY) {
					if (connectType == PROXY_CONNECTION) {
						// connect by password Proxy
						String strServer = txtPSServer.getText().trim();
						String strPort = txtPSPort.getText().trim();
						String strAlias = txtAlias.getText().trim();
						FlowMetaData.init(strServer, strPort, strAlias);
						repositoryAddress = strAlias + "@" + strServer + ":" + strPort;
						// record logon history
						ConnectionEntry ce = new ProxyEntry(strServer, strPort, strAlias);
						recordLogon(ce);
					} else if (connectType == DIRECT_CONNECTION) {
						// connect by database direct access

						String strDriver = "oracle.jdbc.driver.OracleDriver";
						String strDBUser = txtDBUser.getText().trim();
						char[] chDBPswd = txtDBPswd.getPassword();
						String strDBPswd = new String(chDBPswd);
						String strServer = txtServer.getText().trim();
						String strPort = txtPort.getText().trim();
						String strSID = txtSID.getText().trim();
						String strDbType = cbDbType.getSelectedItem().toString().trim();
						String strCharSet = txtCharSet.getText().trim();
						String strURL = "jdbc:oracle:thin:@" + strServer + ":" + strPort + ":" + strSID;

						// mysql数据库连接信息配置：
						if (strDbType.equals("MySQL")) {
							strDriver = "org.gjt.mm.mysql.Driver";
							if (strCharSet.equals("")) {
								strCharSet = "GBK";
							}
							strURL = "jdbc:mysql://" + strServer + ":" + strPort + "/" + strSID + "?characterEncoding="
									+ strCharSet;
						}

						FlowMetaData.init(strDriver, strDBUser, strDBPswd, strURL);

						repositoryAddress = strDBUser + "@" + strServer + ":" + strPort;
						// record logon history
						ConnectionEntry ce = new DirectEntry(strDriver, strServer, strPort, strSID, strDBUser,
								strDbType, strCharSet);
						recordLogon(ce);
					}
				} else if (repositoryType == FS_REPOSITORY) {
					// connect by file system
					String strPath = txtPath.getText();
					if ((strPath != null) && (strPath.trim().equals("") == false)) {
						connectType = DIRECT_CONNECTION;
						repositoryAddress = strPath;
						ConnectionEntry ce = new DirectoryEntry(strPath);
						recordLogon(ce);
						FlowMetaData.init(strPath.trim());
					}
				}
				Thread.sleep(500);
				progressBar.setValue(2);
				progressBar.setString("初始化元数据...");
				flowMetaData = FlowMetaData.getInstance();

				flowMetaData.loadAllTaskflowInfo();

			} catch (Exception e) {
				WcpMessageBox.postException(LogonDialog.this, e);
				btnOk.setEnabled(true);
				LogonDialog.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				result = FAILURE_RESULT;
				progressBar.setValue(0);
				progressBar.setString("");
				return;
			}
			if (flowMetaData == null) {
				WcpMessageBox.postError(LogonDialog.this, "元数据库初始化错误！");
				btnOk.setEnabled(true);
				LogonDialog.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				result = FAILURE_RESULT;
				return;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			progressBar.setValue(3);
			progressBar.setString("校验用户授权...");
			userID = txtUser.getText().trim();
			char[] chPswd = txtPswd.getPassword();
			password = new String(chPswd);
			boolean b = flowMetaData.login(userID, password);
			if (!b) {
				WcpMessageBox.postError(LogonDialog.this, "访问" + title + "的授权用户或密码错误！");
				btnOk.setEnabled(true);
				LogonDialog.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				result = FAILURE_RESULT;
				return;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			progressBar.setValue(4);
			progressBar.setString("检查引擎运行状态...");

			// if (repositoryType == DB_REPOSITORY){
			isEngineRunning = testEngine();
			// }
			result = SUCCESS_RESULT;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			progressBar.setValue(5);
			progressBar.setString("启动" + title + "...");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			LogonDialog.this.setVisible(false);
		}
	}

	private class OpenHandler implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			Thread t = new Thread(new OpenRunnable());
			t.start();
		}
	}

	private class CbDbTypeOnChangeHandler implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			JComboBox cbDbType = (JComboBox) arg0.getSource();
			if (cbDbType.getSelectedItem().toString().equalsIgnoreCase("Oracle")) {
				LogonDialog.this.txtPort.setText("1521");
				LogonDialog.this.txtCharSet.setVisible(false);
				LogonDialog.this.lbCharSet.setVisible(false);
			} else {
				LogonDialog.this.txtPort.setText("3306");
				LogonDialog.this.txtCharSet.setVisible(true);
				LogonDialog.this.lbCharSet.setVisible(true);
			}
		}
	}

	private class ExitHandler implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			LogonDialog.this.dispose();
		}
	}

	private class ViewHistoryHandler extends MouseAdapter {
		private JComponent comp;

		private JPopupMenu popup;

		public ViewHistoryHandler(JComponent dest, JPopupMenu pm) {
			this.comp = dest;
			this.popup = pm;
		}

		public void mousePressed(MouseEvent me) {
			popup.show(comp, me.getX(), me.getY());
		}
	}

	private class FillHistoryHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String str = ae.getActionCommand();
			ConnectionEntry ce = hmLogon.get(str);

			if (ce == null) {
				return;
			}
			if (repositoryType == DB_REPOSITORY) {
				if (connectType == PROXY_CONNECTION) {
					ProxyEntry se = (ProxyEntry) ce;
					txtPSServer.setText(se.getServer());
					txtPSPort.setText(se.getPort());
					txtAlias.setText(se.getDbAlias());
					se.setLogonTime(System.currentTimeMillis());
				}
				if (connectType == DIRECT_CONNECTION) {
					DirectEntry se = (DirectEntry) ce;

					if (se.getDbType() != null) {
						cbDbType.setSelectedItem(se.getDbType());
					}
					txtServer.setText(se.getServer());
					txtPort.setText(se.getPort());
					txtSID.setText(se.getSID());
					txtDBUser.setText(se.getUser());
					if (se.getCharSet() != null && se.getDbType() != null && se.getDbType().equalsIgnoreCase("MySQL")) {
						txtCharSet.setText(se.getCharSet());
					}
					se.setLogonTime(System.currentTimeMillis());
				}
			} else {
				DirectoryEntry se = (DirectoryEntry) ce;
				txtPath.setText(se.getPath());
				se.setLogonTime(System.currentTimeMillis());
			}
		}
	}

	private class ClearHistoryHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			File f = new File(logonHistFile);
			if (f.exists()) {
				f.delete();
				hmLogon.clear();
				pmProxy.removeAll();
				pmDirect.removeAll();
				pmDirectory.removeAll();
			}
		}
	}

	public FlowMetaData getFlowMetaData() {
		return flowMetaData;
	}

	public String getUserID() {
		return userID;
	}

	public String getPassword() {
		return password;
	}

	public boolean isEngineRunning() {
		return isEngineRunning;
	}

	public String getEnginePort() {
		return enginePort;
	}

	public String getEngineServer() {
		return engineServer;
	}

	public int getConnectType() {
		return connectType;
	}

	public int getRepositoryType() {
		return repositoryType;
	}

	public String getRepositoryAddress() {
		return repositoryAddress;
	}

	public String getDenseDescription() {
		StringBuffer sb = new StringBuffer();
		if (this.repositoryType == DB_REPOSITORY) {
			appendProperty("元数据库", "数据库系统", sb);
			sb.append("\n");
			if (this.connectType == DIRECT_CONNECTION) {
				appendProperty("连接方式", "直接连接", sb);
			} else if (this.connectType == PROXY_CONNECTION) {
				appendProperty("连接方式", "代理连接", sb);
			} else if (this.repositoryType == FS_REPOSITORY) {
				appendProperty("元数据库", "文件系统", sb);
				sb.append("\n");
				appendProperty("连接方式", "直接连接", sb);
			}
		}
		sb.append("\n");
		appendProperty("连接地址", repositoryAddress, sb);
		sb.append("\n");
		appendProperty(title + "用户", userID, sb);
		return sb.toString();
	}

	private void appendProperty(String desc, String val, StringBuffer sb) {
		sb.append("[");
		sb.append(desc);
		sb.append("]");
		sb.append(": ");
		sb.append(val);
	}

	private void entryListSort(ArrayList<ConnectionEntry> historys) {

		Object[] entryList = historys.toArray();
		Arrays.sort(entryList, new EntryComparator());
		historys.clear();
		for (Object obj : entryList) {
			historys.add((ConnectionEntry) obj);
		}
	}

	private class EntryComparator implements Comparator {

		public int compare(final Object o1, final Object o2) {
			final ConnectionEntry p1 = (ConnectionEntry) o1;
			final ConnectionEntry p2 = (ConnectionEntry) o2;

			if (p1.getLogonTime() < p2.getLogonTime())
				return 1;
			else
				return 0;
		}
	}
}
