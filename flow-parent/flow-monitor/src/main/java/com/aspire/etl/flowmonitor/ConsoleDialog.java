package com.aspire.etl.flowmonitor;


import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import javax.swing.text.StyledDocument;


import com.aspire.etl.flowmonitor.Xmlrpc;
import com.aspire.etl.flowmonitor.dialog.MonitorLogonDialog;

import com.aspire.etl.xmlrpc.client.Client;




public class ConsoleDialog extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JTextPane jTextPane;
	private javax.swing.JScrollPane jScrollPane;
	private javax.swing.JLabel jLabel;
	private javax.swing.JLabel jLabe2;
	private javax.swing.JTextField commandField;
	private javax.swing.JButton jBtnSubmit;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;
	private Xmlrpc xmlrpc;
	
	private Client client ;
	
	public static void main(String[] args){
		/*ConsoleDialog dialog = new ConsoleDialog();
		dialog.commandField.setRequestFocusEnabled(true);
		dialog.show();*/
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
    	    public void run() {
    		JFrame.setDefaultLookAndFeelDecorated(true);
    		//new FlowMonitor(false);
    		new ConsoleDialog("127.0.0.1","4040","admin","admin");
    	    }
    	});
	}
	
	public ConsoleDialog(String ip,String port,String user,String password) {
		super();
		MonitorLogonDialog dlg = new MonitorLogonDialog();
		dlg.setIP(ip);
		dlg.setPort(port);
		dlg.setUser(user);
		dlg.setPassword(password);
		int result = dlg.showDialog();
		if (result == MonitorLogonDialog.SUCCESS_RESULT){
			init();
			this.setVisible(true);
			
		} else {
			this.dispose();
		}
		dlg.dispose();
	}
	
	public ConsoleDialog(){
		init();
	}
	
	@SuppressWarnings("static-access")
	private void init(){
		
		client = new Client(xmlrpc.getInstance().IP,xmlrpc.getInstance().PORT,xmlrpc.getInstance().USERNAME,xmlrpc.getInstance().PASSWORD);				
		
		this.xmlrpc = Xmlrpc.getInstance();
		jPanel1 = new javax.swing.JPanel();
		
		jPanel1.setLayout(new FlowLayout(FlowLayout.CENTER));

		jScrollPane = new javax.swing.JScrollPane();

		
		jTextPane = new javax.swing.JTextPane();
		jTextPane.setText("");
		jTextPane.setEditable(false);
		jTextPane.setForeground(Color.WHITE);
		jTextPane.setBackground(Color.BLACK);
		
		jScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("命令行信息输出："));
		jScrollPane.setPreferredSize(new Dimension(800,520));
		jScrollPane.setViewportView(jTextPane);
		jScrollPane.add(jPanel1);
		
		jPanel2 = new javax.swing.JPanel();
		jPanel2.setLayout(new BorderLayout());
		commandField = new javax.swing.JTextField(50);
		jPanel3 = new javax.swing.JPanel();
		jLabe2 = new javax.swing.JLabel();
		jLabe2.setText("查看命令请输入：help");
		jPanel3.add(jLabe2);
		jPanel2.add(jPanel3,BorderLayout.WEST);
		jLabel = new javax.swing.JLabel();
		jLabel.setText("命令：");
		jBtnSubmit = new javax.swing.JButton();
		jBtnSubmit.setText("提交");
		jButton2 = new javax.swing.JButton();
		jButton2.setText("清空");
		jButton3 = new javax.swing.JButton();
		jButton3.setText("导出");
		jButton3.setEnabled(false);
		//jPanel2.add(jLabe2);
		
		jPanel4 = new javax.swing.JPanel();
		jPanel4.setLayout(new FlowLayout(FlowLayout.CENTER));
		jPanel4.add(jLabel);
		jPanel4.add(commandField);
		jPanel4.add(jBtnSubmit);
		jPanel4.add(jButton2);
		jPanel4.add(jButton3);
		jPanel2.add(jPanel4,BorderLayout.CENTER);
		this.setLayout(new BorderLayout());
		
		this.add(jPanel2, BorderLayout.NORTH);	
		this.add(jScrollPane, BorderLayout.CENTER);
		this.setSize(1024, 600);
		
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		int a = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int b = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int)(a - this.getWidth())/2, (int)(b - this.getHeight())/2);
		this.setResizable(true);
		this.setTitle("命令行流程监控器");
		
		jBtnSubmit.addActionListener(new SubmitListener());
		jButton2.addActionListener(new Empty());
		jButton3.addActionListener(new Export());
		this.getRootPane().setDefaultButton(jBtnSubmit);
		this.commandField.setRequestFocusEnabled(true);
	}
	
	private class Export implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) 
		{
			if(!jTextPane.getText().equals(""))
			{
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File("."));
				jfc.setSelectedFile(new File("log.log"));	
				 if (jfc.showSaveDialog(ConsoleDialog.this) == JFileChooser.APPROVE_OPTION) 
			 	    {
			 	    	String path = jfc.getSelectedFile().getAbsolutePath();
			 	    	PrintWriter pw = null;
			 	    	try {
							pw = new PrintWriter(new BufferedWriter(new FileWriter(path)));
							pw.println(jTextPane.getText());
							pw.flush();
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally
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
			}
			
		}
	}
	
	
	private class Empty implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) 
		{
			jTextPane.setText("");
			jButton3.setEnabled(false);
		}
	}
	
	private class SubmitListener implements ActionListener
	{

		@SuppressWarnings("static-access")
		public void actionPerformed(ActionEvent arg0) 
		{
			String text=commandField.getText();
			if(!text.equals(""))
			{
				String[] commands=text.split(" ");				
				
				String columnMessage=client.execute(commands);
				
				//jTextPane.ap
				appendStringToTextPane(getCurrDateTime()+" > "+text+"\n"+columnMessage+"\n",jTextPane);
				jButton3.setEnabled(true);
				commandField.select(0, commandField.getText().length());
			}else
			{
				JOptionPane.showMessageDialog(ConsoleDialog.this, "  请输入命令！", "系统提示", JOptionPane.ERROR_MESSAGE);
			}
		}
		public void appendStringToTextPane(String str,JTextPane textPane){
//			得到与编辑器关联的模型，赋给styleddocument
			StyledDocument doc = (StyledDocument)textPane.getDocument();
			Style style = doc.addStyle("StyleName", null);
			
			Font font = new Font("宋体",Font.PLAIN,12);
			StyleConstants.setFontSize(style, font.getSize());
			StyleConstants.setFontFamily(style, font.getName());
			
			textPane.setCaretPosition(doc.getLength());
			try {
				
				doc.insertString(doc.getLength(), str, style);
				Rectangle r = null;
					r = textPane.modelToView(doc.getLength());
				if (r != null)
				{
					textPane.scrollRectToVisible(r);
				}
			} catch (BadLocationException e) {}
		}
	}	
	
	private static String getCurrDateTime() {

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}



}
