package com.aspire.etl.flowmonitor.dialog;

import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import com.aspire.etl.flowmonitor.FlowFrame;
import com.aspire.etl.flowmonitor.Xmlrpc;
import com.aspire.etl.uic.ConnectionEntry;
import com.aspire.etl.uic.XMLRPCEntry;

public class MonitorLogonDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JPasswordField jTextField4;
    
    public static void main(String[] args) {
    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
    	    public void run() {
    		JFrame.setDefaultLookAndFeelDecorated(true);
    		MonitorLogonDialog dlg = new MonitorLogonDialog();
    		dlg.setVisible(true);
    	    }
    	});
    }
    
    private HashMap<String, ConnectionEntry> hmLogon;
    
    private JPopupMenu pmDirect;
    
    private final String logonHistFile;
    
    
    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = -1;

    @SuppressWarnings("unused")
	private int result = FAILURE_RESULT;
    
	
	public  MonitorLogonDialog()
	{				
		result = FAILURE_RESULT;
		
		this.setModal(true);
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		logonHistFile = tmpDir + "Monitor_Logon.xml";
		initLogonHist();
		
		jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("流程监控器登录");
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("\u914d\u7f6e\u4fe1\u606f"));
        jLabel1.setText("引擎IP\uff1a");

        jLabel2.setText("监控端口\uff1a");

        jLabel3.setText(" \u7528\u6237\u540d\uff1a");

        jLabel4.setText(" \u5bc6\u7801\uff1a");
        
        jButton3.setText("...");
        jButton3.setMargin(new Insets(0, 1, 0, 1));
        jButton3.addMouseListener(new ViewHistoryHandler(jButton3, pmDirect));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel4)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextField4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jTextField2)
                        .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("\u8fde\u63a5");

        jButton2.setText("\u53d6\u6d88");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(70, 70, 70)
                        .add(jButton1)
                        .add(47, 47, 47)
                        .add(jButton2))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton1))
                .addContainerGap())
        );
        pack();
		
		int a = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int b = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int)(a - this.getWidth())/2, (int)(b - this.getHeight())/2);
		
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.jTextField1.addKeyListener(new EnterListener());
		this.jTextField2.addKeyListener(new EnterListener());
		this.jTextField3.addKeyListener(new EnterListener());
		this.jTextField4.addKeyListener(new EnterListener());
		this.jButton1.addActionListener(new OpenDesignerHandler());
		this.jButton2.addActionListener(new ExitHandler());
		this.jButton3.addKeyListener(new EnterListener());
	}
	
	public int showDialog() {
		this.setVisible(true);
		return result;
	    }
	
	private class EnterListener extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				jButton1.doClick();
			}
		}
	}
	
	private void initLogonHist() {
		hmLogon = new HashMap<String, ConnectionEntry>();
		pmDirect = new JPopupMenu();
		File f = new File(logonHistFile);
		if (!f.exists()) {
		    return;
		}
		// get all logon histories
		SAXReader reader = new SAXReader();
		try {
		    Document doc = reader.read(f);
		    Element root = doc.getRootElement();
		    ConnectionEntry ce = null;
		    List elmxrs = root.elements("XMLRPC");
		    Iterator iter1 = elmxrs.iterator();
		    Element elmEnt;
		    FillHistoryHandler fhh = new FillHistoryHandler();
		    JMenuItem mi;
		    while (iter1.hasNext()) {
			elmEnt = (Element) iter1.next();
			ce = new XMLRPCEntry();
			ce.setXMLElement(elmEnt);
			hmLogon.put(ce.toString(), ce);
			mi = pmDirect.add(ce.toString());
			mi.addActionListener(fhh);
		    }
		} catch (DocumentException e) {
		//} catch (MalformedURLException e) {
		}
    }
	
	private void recordLogon(ConnectionEntry ce) {
		// XML Document
		Element root = new DefaultElement("Connections");
		hmLogon.put(ce.toString(), ce);
		Iterator iter = hmLogon.values().iterator();
		ConnectionEntry item;
		while (iter.hasNext()) {
		    item = (XMLRPCEntry) iter.next();
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
		    e.printStackTrace();
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
		    XMLRPCEntry se = (XMLRPCEntry)ce;
		    jTextField1.setText(se.getIp());
		    jTextField2.setText(se.getPort());
		    jTextField3.setText(se.getUser());
		}
	}
	    
    private class OpenDesignerHandler implements ActionListener {
	   	public void actionPerformed(ActionEvent arg0) {
	   	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	   		public void run() 
	   		{
	   		    JFrame.setDefaultLookAndFeelDecorated(false);

	   			String ip = jTextField1.getText().trim();
	    		char[] chDBPswd = jTextField4.getPassword();
	    		String pass = new String(chDBPswd);
	    		String port = jTextField2.getText().trim();
	    		String user = jTextField3.getText().trim();
	    		// record logon history
	    		ConnectionEntry ce = new XMLRPCEntry(ip, port, user);
	    		recordLogon(ce);

	    		if (!jTextField1.getText().equals("") && !jTextField2.getText().equals("") && !jTextField3.getText().equals("") && !pass.equals(""))
	    		{
	    			Xmlrpc.getInstance().init(jTextField1.getText(), jTextField2.getText(), jTextField3.getText(),pass);
	    			boolean isOK = Xmlrpc.getInstance().testConn();
	    			
	    			//测试
	    			if(isOK){
	    				result = SUCCESS_RESULT;
	    				//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));  
	    				jButton1.setEnabled(false);
	    				MonitorLogonDialog.this.dispose();
	    				FlowFrame.XMLCONN = true;
	    				/*FlowMonitor monitor = new FlowMonitor(isApplet);
	    				monitor.setVisible(true);*/
	    				//setCursor(Cursor.getDefaultCursor());
	    				
	    			}else{
	    				JOptionPane.showMessageDialog(MonitorLogonDialog.this, "连接失败，请检查检查连接信息或流程引擎是否启动！", "系统消息", JOptionPane.INFORMATION_MESSAGE);
	    			}
	    		}
	    		else
	    			JOptionPane.showMessageDialog(MonitorLogonDialog.this, "IP、端口、用户名或密码不能为空！", "系统消息", JOptionPane.ERROR_MESSAGE);
	    		}
	   	   });
	   	}
    }
	    
    private class ExitHandler implements ActionListener {
	    public void actionPerformed(ActionEvent arg0) {
	        MonitorLogonDialog.this.dispose();
	   	}
    }
	public void setIP(String strIP){
	    jTextField1.setText(strIP);	    
	}
	public void setPort(String strPort){
	    jTextField2.setText(strPort);
	    
	}
	public void setUser(String user){
	    jTextField3.setText(user);
	}
	public void setPassword(String password){
	    jTextField4.setText(password);
	}

	public int getResult() {
		return result;
	}
}
