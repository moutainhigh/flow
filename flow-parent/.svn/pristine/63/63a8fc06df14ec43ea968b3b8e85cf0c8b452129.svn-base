package com.aspire.etl.flowmonitor.dialog;

import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class LogMessageDialog extends JDialog 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane pan;
	private JTable tab;
	
	public LogMessageDialog(JFrame parent) 
	{
		super(parent, true);
		tab = new JTable();
		pan = new JScrollPane(tab);
		setContentPane(pan);
		this.setSize(1000, 800);
		int a = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int b = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int) (a - this.getWidth()) / 2, (int) (b - this
				.getHeight()) / 2);
	}

	public JTable getTable() {
		return tab;
	}

	public void setTable(JTable tab) {
		this.tab = tab;
	}
}
