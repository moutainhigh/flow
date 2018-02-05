package com.aspire.etl.uic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.uic.TaskDialog;
import com.aspire.etl.uic.WcpMessageBox;

/**
 * 
 * @author jiangts
 * @since 2009-12-30
 *        <p>
 *        任务节点搜索
 *        </p>
 */
public class TaskSearchDialog extends JDialog {

    private JList lstFlows;

    private DefaultListModel lmFlows;

    private JButton btnOk, btnCancel, btnSearch;
    
    private JTextField	edtTaskName;

    private boolean isApproved;

    private FlowMetaData flowMetaData;

    private String userID;

    public static void main(String[] args) {
    	FlowMetaData flowMetaData = null;
    	
    	try {
			FlowMetaData.init("oracle.jdbc.driver.OracleDriver", "spms_rdp", "spms_rdp",
					"jdbc:oracle:thin:@10.1.3.105:1521:ora9i");
			flowMetaData = FlowMetaData.getInstance();
		} catch (MetaDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		TaskSearchDialog dialog = new TaskSearchDialog(null,flowMetaData,"admin");
		dialog.showDialog();
    }

    public TaskSearchDialog(JFrame parent, FlowMetaData fmd, String userID) {
	super(parent, "任务搜索", true);
	this.setSize(600, 400);
	this.setLocationRelativeTo(parent);
	this.userID = userID;
	this.flowMetaData = fmd;
	Container cont = this.getContentPane();
	cont.setLayout(new BorderLayout());
	ButtonHandler bh = new ButtonHandler();
	
	
	JPanel pnl = new JPanel();
	edtTaskName = new JTextField(30);
	btnSearch = new JButton("搜索");
	btnSearch.addActionListener(bh);
	pnl.add(new JLabel("任务名称： "));
	pnl.add(edtTaskName);
	pnl.add(btnSearch);

	cont.add(pnl, BorderLayout.NORTH);
	
	this.lmFlows = new DefaultListModel();
	this.lstFlows = new JList(lmFlows);
	
	//设置为单选模式
	lstFlows.setSelectionMode(0);
	
	TaskflowCellRenderer tcr = new TaskflowCellRenderer();
	lstFlows.setCellRenderer(tcr);
	lstFlows.addMouseListener(new ListHandler());
	JScrollPane sp = new JScrollPane(lstFlows);
	cont.add(sp, BorderLayout.CENTER);
	
	btnOk = new JButton("定 位");
	btnOk.addActionListener(bh);

	btnCancel = new JButton("关 闭");
	btnCancel.addActionListener(bh);

	pnl = new JPanel();
	pnl.add(btnOk);
	pnl.add(btnCancel);

	cont.add(pnl, BorderLayout.SOUTH);
	this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	initTaskList();
    }

    private void initTaskList() {
    	
    	
    	List taskList = flowMetaData.queryTaskList(887496314);
    	
    	System.out.println(taskList.size());
    	
    /*	ComparatorTaskflow comparator=new ComparatorTaskflow();
    	Collections.sort(tfs, comparator);

    	lmFlows.removeAllElements();
    	Iterator iter = tfs.iterator();
    	Taskflow tf;
    	while (iter.hasNext()) {
    		tf = (Taskflow) iter.next();
    		boolean b = flowMetaData.isTaskflowUser(userID, tf.getTaskflowID());
    		TaskflowView tv = new TaskflowView(tf, b);
    		lmFlows.addElement(tv);
    	}*/
    }

    public void showDialog() {
    	initTaskList();
    	this.setVisible(true);
    }

    public Taskflow[] getSelectedTaskflows() {
    	Object[] objs = lstFlows.getSelectedValues();
    	int len = objs.length;
    	Taskflow[] tfs = new Taskflow[len];
    	for (int i = 0; i < len; i++) {
    		tfs[i] = ((TaskflowView) objs[i]).getTaskflow();
    	}
    	return tfs;
    }

    public boolean isApproved() {
	return isApproved;
    }

    private class ButtonHandler implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    JButton btn = (JButton) arg0.getSource();
	    if (btn == btnOk) {
		isApproved = true;
		TaskSearchDialog.this.setVisible(false);
	    } else if (btn == btnCancel) {
		isApproved = false;
		TaskSearchDialog.this.dispose();
	    }
	}
    }

    private class ListHandler extends MouseAdapter {
	public void mouseClicked(MouseEvent me) {
	    Object obj = lstFlows.getSelectedValue();
	    if (obj == null) {
		return;
	    }
	    int cnt = me.getClickCount();
	    // check authorization
	    if (cnt >= 2) {
		isApproved = true;
		TaskSearchDialog.this.setVisible(false);
	    }
	}
    }

    private class TaskflowView {
	private Taskflow taskflow;

	private boolean editable;

	public TaskflowView(Taskflow tf, boolean editable) {
	    this.taskflow = tf;
	    this.editable = editable;
	}

	public String toString() {
	    return taskflow.toString();
	}

	public boolean isEditable() {
	    return editable;
	}

	public void setEditable(boolean editable) {
	    this.editable = editable;
	}

	public Taskflow getTaskflow() {
	    return taskflow;
	}

	public void setTaskflow(Taskflow taskflow) {
	    this.taskflow = taskflow;
	}
    }

    private class TaskflowCellRenderer extends JLabel implements
	    ListCellRenderer {

	public Component getListCellRendererComponent(JList list, Object value,
		int index, boolean isSelected, boolean cellHasFocus) {
	    TaskflowView tv = (TaskflowView) value;
	    setText(tv.toString());
	    if (tv.isEditable()) {
		setForeground(Color.RED);
	    } else {
		setForeground(list.getForeground());
	    }

	    if (isSelected) {
		setBackground(list.getSelectionBackground());
	    } else {
		setBackground(list.getBackground());
	    }
	    setOpaque(true);
	    return this;
	}
    }
    
    class ComparatorTaskflow implements Comparator{

    	 public int compare(Object arg0, Object arg1) {
	    	 Taskflow t0=(Taskflow)arg0;
	    	 Taskflow t1=(Taskflow)arg1;
	    	 int flag=t0.getTaskflow().compareTo(t1.getTaskflow());
	    	 
	    	 return flag;
    	 }
}
}
