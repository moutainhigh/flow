package com.aspire.etl.flowmonitor.dialog;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.aspire.etl.flowmonitor.dao.FlowDao;

public class TaskflowListDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	JTable table = null;
	

	ResultSet rs = null;
	
	ResultSetModel model = null;

	JScrollPane sp2 = null;

	JPanel contentPane = new JPanel();

	private int flashTime = 5;

	Robot robot = null;
	
	private Timer timer;

	static Logger log = Logger.getLogger("FlowMonitor");

	public int getFlashTime() {
		return flashTime;
	}

	public void setFlashTime(int flashTime) {
		this.flashTime = flashTime;
	}
	
	private FlowDao dao;

	public TaskflowListDialog(JFrame parent, int flashTime, FlowDao dao) {
		super(parent, "流程列表 (" + flashTime + "秒自动刷新)", true);
		this.flashTime = flashTime;
		this.dao = dao;
		log.info("流程列表以" + flashTime + "秒速度刷新");
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			log.error(e1);
		}
		contentPane.setLayout(new BorderLayout());
		try {
			//System.out.println("1");
			rs = dao.getFlowListRs();
			//System.out.println("3");
			model = new ResultSetModel();
			model.parseResultSet(rs);
			table = new JTable(model);
			//table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			table.getColumnModel().getColumn(1).setPreferredWidth(300);
			table.getColumnModel().getColumn(6).setPreferredWidth(150);
			sp2 = new JScrollPane(table);
			//System.out.println("2");
		} catch (SQLException e) {
			log.error(e);
		}
		contentPane.add(sp2);
		setContentPane(contentPane);
		this.setSize(800, 600);
		int a = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int b = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int) (a - this.getWidth()) / 2, (int) (b - this
				.getHeight()) / 2);
		this.setResizable(false);
		timer = new Timer(this.flashTime * 1000, new AutoRefreshHandler());
		timer.start();
		this.setVisible(true);
	}
	
	private class AutoRefreshHandler extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			log.debug("刷新taskflow list");
			try {
				if (rs != null) {
					rs.close();
					rs = dao.getFlowListRs();
					model.parseResultSet(rs);
					table.updateUI();
					table.getColumnModel().getColumn(1).setPreferredWidth(300);
					table.getColumnModel().getColumn(6).setPreferredWidth(150);
				}
			} catch (SQLException e) {
				log.error(e);
			}
		}
	}

}

class ResultSetModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] colNames = new String[0];

	private ArrayList data = new ArrayList();

	public void parseResultSet(ResultSet rs) throws SQLException {
		data.clear();

		ResultSetMetaData rmd = rs.getMetaData();
		int colCount = rmd.getColumnCount();
		colNames = new String[colCount];
		for (int i = 1; i <= colCount; i++)
			colNames[i - 1] = rmd.getColumnName(i);
		while (rs.next()) {
			String[] values = new String[colCount];
			for (int col = 1; col <= colCount; col++)
			{
				if (col == 3)
				{
					String suspend = rs.getString(col);
					if (suspend.equals("0"))
						values[col - 1] = "正常";
					else
						values[col - 1] = "挂起";
				}
				else if (col == 4)
				{
					String stat = rs.getString(col);
					if (stat.equals("0"))
						values[col - 1] = "就绪";
					else if (stat.equals("1"))
						values[col - 1] = "运行";
					else if (stat.equals("2"))
						values[col - 1] = "成功";
					else if (stat.equals("-1"))
						values[col - 1] = "失败";
					else if (stat.equals("3"))
						values[col - 1] = "等待";
					else if (stat.equals("4"))
						values[col - 1] = "停止";
				}
				else					
					values[col - 1] = rs.getString(col);
			}
			data.add(values);
		}

		fireTableStructureChanged();
	}

	public Class getColumnClass(int c) {
		return String.class;
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public String getColumnName(int index) {
		return colNames[index];
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int row, int col) {
		String[] values = (String[]) data.get(row);
		return values[col];
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public void clear() {
		data.clear();
		colNames = new String[0];
		fireTableStructureChanged();
	}
}
