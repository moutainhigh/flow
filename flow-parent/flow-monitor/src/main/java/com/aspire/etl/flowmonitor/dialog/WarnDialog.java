package com.aspire.etl.flowmonitor.dialog;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.aspire.etl.flowmonitor.FlowMonitor;

public class WarnDialog extends JDialog implements ActionListener 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    
    private FlowMonitor monitor;
    private String sql = "select * from OAM_ALERT_INFO where floor((sysdate-OCCURTIME)*24*60)<3 order by OCCURTIME desc";
    
    public WarnDialog(JFrame parent, FlowMonitor monitor)
    {
    	super(parent, true);
    	this.monitor = monitor;
    	
    	jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField1.setText("3");
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("\u544a\u8b66\u914d\u7f6e"));
        jLabel1.setText("\u67e5\u770b");

        jLabel2.setText("\u5206\u949f\u4ee5\u524d\u7684\u544a\u8b66\u4fe1\u606f");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("\u786e\u5b9a");

        jButton2.setText("\u53d6\u6d88");

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
                        .add(57, 57, 57)
                        .add(jButton1)
                        .add(47, 47, 47)
                        .add(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    	
    	int a = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int b = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int)(a - this.getWidth())/2, (int)(b - this.getHeight())/2);
		this.setResizable(false); 
		this.setTitle("告警查询配置对话框");
		
		jButton1.addActionListener(this);
		jButton2.addActionListener(this);
    }

	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == jButton1)
		{
			sql = "select * from OAM_ALERT_INFO where";
			String s = null;
			if (this.jTextField1.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "时间不能为空！", "系统警告", JOptionPane.WARNING_MESSAGE);
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
				sql = sql + " floor((sysdate-OCCURTIME)*24*60)<"+t + " order by OCCURTIME desc";
				
				monitor.initTable();
				this.dispose();
			}
		}

		if (e.getSource() == this.jButton2)
		{
			this.dispose();
		}
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
