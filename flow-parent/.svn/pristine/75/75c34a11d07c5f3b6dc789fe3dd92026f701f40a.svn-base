package com.aspire.etl.flowdesinger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
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
import javax.swing.ListCellRenderer;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.uic.WcpMessageBox;

/**
 * 
 * 大纲列表，用于删除大纲操作
 */
public class OutlineTaskflowChooser extends JDialog {

	private static final long serialVersionUID = 1L;

	private JList lstFlows;

    private DefaultListModel lmFlows;

    private JButton btnOk, btnCancel, btnDelete;

    private boolean isApproved;

    private FlowMetaData flowMetaData;

    private String userID;

    private boolean isEngineRunning;
    
    public static void main(String[] args) {
    	FlowMetaData flowMetaData = null;
    	
    	try {
			FlowMetaData.init("oracle.jdbc.driver.OracleDriver", "spms_rdp", "spms_rdp",
					"jdbc:oracle:thin:@10.1.3.105:1521:ora9i");
			flowMetaData = FlowMetaData.getInstance();
			flowMetaData.loadAllTaskflowInfo();
		} catch (MetaDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OutlineTaskflowChooser dialog = new OutlineTaskflowChooser(null,flowMetaData,"admin",false);
		dialog.showDialog();
    }

    public OutlineTaskflowChooser(JFrame parent, FlowMetaData fmd, String userID,
	    boolean running) {
	super(parent, "大纲列表", true);
	this.setSize(600, 400);
	this.setLocationRelativeTo(parent);
	this.userID = userID;
	this.flowMetaData = fmd;
	this.lmFlows = new DefaultListModel();
	this.lstFlows = new JList(lmFlows);
	this.isEngineRunning = running;
	TaskflowCellRenderer tcr = new TaskflowCellRenderer();
	lstFlows.setCellRenderer(tcr);
	lstFlows.addMouseListener(new ListHandler());
	JScrollPane sp = new JScrollPane(lstFlows);
	Container cont = this.getContentPane();
	cont.setLayout(new BorderLayout());
	cont.add(sp, BorderLayout.CENTER);
	ButtonHandler bh = new ButtonHandler();


	btnOk = new JButton("打开选中的大纲");
	btnOk.addActionListener(bh);

	btnCancel = new JButton("关闭");
	btnCancel.addActionListener(bh);

	btnDelete = new JButton("删除选中的大纲");
	btnDelete.addActionListener(bh);
	btnDelete.setEnabled(!running);

	JPanel pnl = new JPanel();
	pnl.add(btnOk);
	pnl.add(btnDelete);
	pnl.add(btnCancel);

	cont.add(pnl, BorderLayout.SOUTH);
	this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	initTaskList();
    }

    private void initTaskList() {
    	
	List tfs = flowMetaData.queryAllTaskflow();
	ComparatorTaskflow comparator=new ComparatorTaskflow();
    Collections.sort(tfs, comparator);

	lmFlows.removeAllElements();
	Iterator iter = tfs.iterator();
	Taskflow tf;
	while (iter.hasNext()) {
	    tf = (Taskflow) iter.next();

	    if (tf.getGroupID() == 0){//只显示大纲列表
	    	boolean b = flowMetaData.isTaskflowUser(userID, tf.getTaskflowID());
	    	TaskflowView tv = new TaskflowView(tf, b);
	    	lmFlows.addElement(tv);
	    }
	}
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
	    tfs[i] = ((TaskflowView) objs[i]).getOutlineTaskflow();
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
	    	OutlineTaskflowChooser.this.setVisible(false);
	    } else if (btn == btnCancel) {
	    	isApproved = false;
	    	OutlineTaskflowChooser.this.dispose();
	    } else if (btn == btnDelete) {
	    	Object obj = lstFlows.getSelectedValue();
	    	if (obj == null) {
	    		return;
	    	}
	    	TaskflowView tv = (TaskflowView) obj;
	    	Taskflow outlineTaskflow = tv.getOutlineTaskflow();
	    	try {
	    		int ret = JOptionPane.showConfirmDialog(
	    				OutlineTaskflowChooser.this, "你确认要删除大纲" + outlineTaskflow + "吗？",
	    				"是否删除?", JOptionPane.YES_NO_OPTION);
	    		if (ret == JOptionPane.OK_OPTION) {
	    			OutlineTaskflowChooser.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    			
	    			//删除大纲页签
	    			FlowDesigner.getInstance().removeTabByTitle(outlineTaskflow.getTaskflow() + FlowDesignerConstants.OUTLINE_PREFIX);
	    			
	    			//删除大纲中的流程,同时删除流程页签
	    			List<Task> outlineTaskList = flowMetaData.queryTaskList(outlineTaskflow.getTaskflowID());
	    			for(Task outlineTask : outlineTaskList){
	    				Taskflow taskflow = flowMetaData.queryTaskflow(outlineTask.getTask());
	    				flowMetaData.deleteTaskflowOnRespository(taskflow.getTaskflowID());
	    				flowMetaData.deleteTaskflowInMemory(taskflow.getTaskflowID());
	    				//删除流程页签
		    			FlowDesigner.getInstance().removeTabByTitle(taskflow.getTaskflow());
	    			}
	    			
	    			
	    			flowMetaData.deleteTaskflowOnRespository(outlineTaskflow
	    					.getTaskflowID());
	    			flowMetaData.deleteTaskflowInMemory(outlineTaskflow
	    					.getTaskflowID());
	    			lmFlows.removeElement(tv);

	    			
	    			
	    		}
	    	} catch (MetaDataException e) {
	    		WcpMessageBox.postException(OutlineTaskflowChooser.this, e);
	    	}finally{
	    		OutlineTaskflowChooser.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    	}
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
	    if (cnt == 1) {
		TaskflowView tv = (TaskflowView) obj;
		btnDelete.setEnabled(tv.isEditable() && (!isEngineRunning));
	    } else if (cnt >= 2) {
		isApproved = true;
		OutlineTaskflowChooser.this.setVisible(false);
	    }
	}
    }

    private class TaskflowView {
	private Taskflow outlineTaskflow;

	private boolean editable;

	public TaskflowView(Taskflow tf, boolean editable) {
	    this.outlineTaskflow = tf;
	    this.editable = editable;
	}

	public String toString() {
	    return outlineTaskflow.toString();
	}

	public boolean isEditable() {
	    return editable;
	}

	public void setEditable(boolean editable) {
	    this.editable = editable;
	}

	public Taskflow getOutlineTaskflow() {
	    return outlineTaskflow;
	}

	public void setOutlineTaskflow(Taskflow outlineTaskflow) {
	    this.outlineTaskflow = outlineTaskflow;
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
