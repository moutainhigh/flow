package com.aspire.etl.flowmonitor.dialog;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.graph.DefaultGraphCell;

import com.aspire.etl.flowmonitor.FlowFrame;
import com.aspire.etl.flowmonitor.FlowMonitor;
import com.aspire.etl.flowmonitor.beans.FlowBean;
import com.aspire.etl.flowmonitor.dao.FlowDao;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;

public class LogDialog extends JDialog implements java.awt.event.ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sql = "select * from rpt_log where floor((sysdate-occurtime)*24*60)<3 and loglevel in ('DEBUG') order by occurtime";
	
	private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    
    private FlowMonitor monitor;
    private FlowDao dao;
    private DefaultTableModel model;
    
    private JPopupMenu logpopup;
    private JTextArea logarea;
    private JScrollPane logscroll;
    
    private LogMessageDialog logMessage;
	
	public LogDialog(JFrame parent, FlowMonitor monitor, FlowDao dao)
	{
		super(parent,true);
		this.monitor = monitor;
		this.dao = dao;
		
		
		jPanel1 = new javax.swing.JPanel();
		jTextField1 = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox1.setSelected(true);
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCheckBox7 = new javax.swing.JCheckBox();
        
        logpopup = new JPopupMenu();
        logarea = new JTextArea(8,30);
        logarea.setEditable(false);
        logarea.setLineWrap(true);
        logscroll = new JScrollPane();
        logscroll.setViewportView(logarea);		
		logpopup.add(logscroll);

        jTextField1.setText("3");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel1.setText("\u67e5\u770b");

        jLabel2.setText("\u5206\u949f\u4ee5\u524d\u7684\u65e5\u5fd7\u4fe1\u606f");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(41, 41, 41)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel2)
                .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel1))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("\u8bf7\u9009\u62e9\u65e5\u5fd7\u7ea7\u522b"));
        jCheckBox1.setText("DEBUG");
        jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBox2.setText("INFO");
        jCheckBox2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox2.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBox3.setText("WARN");
        jCheckBox3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox3.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBox4.setText("ERROR");
        jCheckBox4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox4.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBox5.setText("FATAL");
        jCheckBox5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox5.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBox6.setText("OFF");
        jCheckBox6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox6.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(31, 31, 31)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCheckBox1)
                    .add(jCheckBox4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 60, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCheckBox2)
                    .add(jCheckBox5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 47, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCheckBox3)
                    .add(jCheckBox6))
                .add(35, 35, 35))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCheckBox1)
                    .add(jCheckBox2)
                    .add(jCheckBox3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCheckBox4)
                    .add(jCheckBox5)
                    .add(jCheckBox6))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("\u786e\u5b9a");

        jButton2.setText("\u53d6\u6d88");

        jCheckBox7.setText("查看时不弹出");
        jCheckBox7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox7.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(91, Short.MAX_VALUE)
                        .add(jButton1)
                        .add(35, 35, 35)
                        .add(jButton2)
                        .add(17, 17, 17)
                        .add(jCheckBox7)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2)
                    .add(jCheckBox7))
                .addContainerGap())
        );
        pack();
		int a = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int b = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int)(a - this.getWidth())/2, (int)(b - this.getHeight())/2);
		this.setResizable(false); 
		this.setTitle("日志查询配置对话框");
		
		jButton1.addActionListener(this);
		jButton2.addActionListener(this);
	}
	
	
	public void actionPerformed(ActionEvent e) 
	{	
		List <String> list = new ArrayList<String>();
		if (e.getSource() == jButton1)
		{
			sql = "select * from rpt_log where";
			String s = null;
			if (this.jTextField1.getText().equals("") || (!this.jCheckBox1.isSelected() && !this.jCheckBox2.isSelected() && !this.jCheckBox3.isSelected() && !this.jCheckBox4.isSelected() && !this.jCheckBox5.isSelected() && !this.jCheckBox6.isSelected()))
			{
				JOptionPane.showMessageDialog(this, "时间或级别不能为空！", "系统警告", JOptionPane.WARNING_MESSAGE);
			}
			else
			{	
				s = this.jTextField1.getText();
				int t = 0;
				try {
					t = Integer.parseInt(s);
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(this, "时间必须为正整数！", "系统警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				sql = sql + " floor((sysdate-occurtime)*24*60)<"+t+" and loglevel in (";
				
				if (this.jCheckBox1.isSelected())
				{
					list.add(this.jCheckBox1.getText());
				}
				if (this.jCheckBox2.isSelected())
				{
					list.add(this.jCheckBox2.getText());
				}
				if (this.jCheckBox3.isSelected())
				{
					list.add(this.jCheckBox3.getText());
				}
				if (this.jCheckBox4.isSelected())
				{
					list.add(this.jCheckBox4.getText());
				}
				if (this.jCheckBox5.isSelected())
				{
					list.add(this.jCheckBox5.getText());
				}
				if (this.jCheckBox6.isSelected())
				{
					list.add(this.jCheckBox6.getText());
				}
				for (int i = 0; i < list.size(); i++)
				{
					if (i == list.size() - 1)
					{
						sql = sql + "'" + list.get(i) + "')" ;
					}
					else
					{
						sql = sql + "'" + list.get(i) + "'" + ",";
					}		
				}
				showLog();
				this.dispose();
			}
		}	
		if (e.getSource() == this.jButton2)
		{
			this.dispose();
		}
	}

	/*public void showLog()
	{
		// System.out.println(config.getSql());
		try {
			DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) monitor.getTree()
					.getLastSelectedPathComponent();
			if (curNode != null)
			{
				if (curNode.getUserObject() instanceof Taskflow) {// 流程
					sql = sql
							+ " and taskflow='"
							+ ((Taskflow) curNode.getUserObject())
									.getTaskflow() + "' order by occurtime";
					System.out.println(sql);
				}
				Vector<FlowBean> v = dao.getConfigMessage(sql);
				// String[] str =
				// {"ETL流程名称","任务名称","重做标记","日志级别","日志的详细描述内容","任务流开始时间","任务流结束时间","日志发生时间"};
				// model = new DefaultTableModel(str, 0);
				Object[] message;
				if (v == null || v.isEmpty()) {
					JOptionPane.showMessageDialog(null, "没有符合条件的日志！",
							"系统信息", JOptionPane.ERROR_MESSAGE);
				} else {
					this.dispose();
					logMessage = new LogMessageDialog(monitor);
					logMessage.setTitle(String.valueOf((Taskflow) curNode
							.getUserObject())
							+ "流程的日志信息");
					String[] header = new String[]{"ETL流程名称", "任务名称", "重做标记", "日志级别", "日志的详细描述内容", "流程开始时间", "流程结束时间", "日志发生时间"};
					model = new DefaultTableModel(header, 0);
					for (int i = 0; i < v.size(); i++) {
						message = new String[8];
						FlowBean bean = v.get(i);
						message[0] = bean.getTaskFlow();
						message[1] = bean.getTask();
						message[2] = String.valueOf(bean.getRedo_flag());
						message[3] = bean.getLogLevel();
						message[4] = bean.getDescpription();
						message[5] = String.valueOf(bean.getStartTime());
						message[6] = String.valueOf(bean.getEndTime());
						message[7] = String.valueOf(bean.getOccurTime());
						model.addRow(message);
						
					}
					logMessage.getTable().setModel(model);
					TableColumnModel tcm = logMessage.getTable().getColumnModel();
					tcm.getColumn(0).setMaxWidth(150);
					tcm.getColumn(1).setMaxWidth(80);
					tcm.getColumn(2).setMaxWidth(50);
					tcm.getColumn(3).setMaxWidth(70);
					tcm.getColumn(4).setMaxWidth(450);
					tcm.getColumn(5).setMaxWidth(200);
					tcm.getColumn(6).setMaxWidth(200);
					tcm.getColumn(7).setMaxWidth(200);
//					logMessage.getTable().setShowGrid(false);
					logMessage.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					logMessage.getTable().getTableHeader().setReorderingAllowed(false);
					logMessage.getTable().addMouseListener(new TableListener());
					logMessage.setVisible(true);
					// table.setModel(model);
				}
			}
			else
				JOptionPane.showMessageDialog(this, "请选中一个流程！", "系统警告", JOptionPane.WARNING_MESSAGE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	public void showLog()
	{
		// System.out.println(config.getSql());
		try {
			
			FlowFrame ff = (FlowFrame) monitor.getDesktop().getSelectedFrame();
			if (ff != null) {
				
				Object[] cells = ff.getSelectCells();
				StringBuffer sb = new StringBuffer();
				for(Object obj : cells){
					DefaultGraphCell cell = (DefaultGraphCell) obj;
					Object uo = cell.getUserObject();
					// tasks
					if (uo instanceof Task) {
						Task task = (Task)uo;
						sb.append("'" + task.getTask() + "',");
					}
				}
				String tasks = sb.toString();
				if (tasks.length() > 0){
					tasks = " and task in (" + tasks.substring(0, tasks.length() - 1) + ")";
					sql = sql
					+ " and taskflow='"
					+ ff.getTaskflow().getTaskflow() + "' "+ tasks + " order by task,occurtime";
					
				} else {
					sql = sql
					+ " and taskflow='"
					+ ff.getTaskflow().getTaskflow() + "' order by occurtime";
				}
				
				
				System.out.println(sql);
				Vector<FlowBean> v = dao.getConfigMessage(sql);
				// String[] str =
				// {"ETL流程名称","任务名称","重做标记","日志级别","日志的详细描述内容","任务流开始时间","任务流结束时间","日志发生时间"};
				// model = new DefaultTableModel(str, 0);
				Object[] message;
				if (v == null || v.isEmpty()) {
					JOptionPane.showMessageDialog(null, "没有符合条件的日志！",
							"系统信息", JOptionPane.ERROR_MESSAGE);
				} else {
					this.dispose();
					logMessage = new LogMessageDialog(monitor);
					logMessage.setTitle(ff.getTaskflow().getTaskflow()
							+ "流程的日志信息");
					String[] header = new String[]{"ETL流程名称", "任务名称", "重做标记", "日志级别", "日志的详细描述内容", "流程开始时间", "流程结束时间", "日志发生时间"};
					model = new DefaultTableModel(header, 0);
					for (int i = 0; i < v.size(); i++) {
						message = new String[8];
						FlowBean bean = v.get(i);
						message[0] = bean.getTaskFlow();
						message[1] = bean.getTask();
						message[2] = String.valueOf(bean.getRedo_flag());
						message[3] = bean.getLogLevel();
						message[4] = bean.getDescpription();
						message[5] = String.valueOf(bean.getStartTime());
						message[6] = String.valueOf(bean.getEndTime());
						message[7] = String.valueOf(bean.getOccurTime());
						model.addRow(message);
						
					}
					logMessage.getTable().setModel(model);
					TableColumnModel tcm = logMessage.getTable().getColumnModel();
					tcm.getColumn(0).setMaxWidth(150);
					tcm.getColumn(1).setMaxWidth(80);
					tcm.getColumn(2).setMaxWidth(50);
					tcm.getColumn(3).setMaxWidth(70);
					tcm.getColumn(4).setMaxWidth(450);
					tcm.getColumn(5).setMaxWidth(200);
					tcm.getColumn(6).setMaxWidth(200);
					tcm.getColumn(7).setMaxWidth(200);
//					logMessage.getTable().setShowGrid(false);
					logMessage.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					logMessage.getTable().getTableHeader().setReorderingAllowed(false);
					logMessage.getTable().addMouseListener(new TableListener());
					logMessage.setVisible(true);
					// table.setModel(model);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private class TableListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if (logMessage.getTable().getSelectedRow() != -1) {
				if (logMessage.getTable().getSelectedColumn() == 4 && e.getButton() == 1) {
					logarea.setText(
							(String) logMessage.getTable()
									.getValueAt(logMessage.getTable().getSelectedRow(), 4));
					
					logpopup.show(logMessage.getTable(), e.getX(),	e.getY());
				}
			}
		}
	}

	public javax.swing.JCheckBox getJCheckBox7() {
		return jCheckBox7;
	}


	public void setJCheckBox7(javax.swing.JCheckBox checkBox7) {
		jCheckBox7 = checkBox7;
	}
	
}

