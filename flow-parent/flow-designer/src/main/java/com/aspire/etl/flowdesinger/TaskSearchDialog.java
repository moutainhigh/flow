package com.aspire.etl.flowdesinger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.jgraph.JGraph;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
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

	private static final long serialVersionUID = 1L;

	private JList lstFlows;

    private DefaultListModel lmFlows;

    private JButton btnOk, btnCancel, btnSearch;
    
    private JTextField	edtTaskName;

    private boolean isApproved;

    private FlowMetaData flowMetaData;
    
    private Taskflow taskflow;
    
    private List<Task> taskList;

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
    	
		TaskSearchDialog dialog = new TaskSearchDialog(null,flowMetaData);
		//dialog.showDialog(null);
    }

    public TaskSearchDialog(JFrame parent, FlowMetaData fmd) {
	super(parent, "任务搜索", true);
	this.setSize(600, 400);
	this.setLocationRelativeTo(parent);
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
	
	btnOk = new JButton("定位");
	btnOk.addActionListener(bh);

	btnCancel = new JButton("关闭");
	btnCancel.addActionListener(bh);

	pnl = new JPanel();
	pnl.add(btnOk);
	pnl.add(btnCancel);

	cont.add(pnl, BorderLayout.SOUTH);
	this.getRootPane().setDefaultButton(btnSearch);
	this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	//initTaskList();
    }

    @SuppressWarnings("unchecked")
	private void initTaskList() {
    	List<Task> taskList = null;
    	
    	if (taskflow != null){
    		if (edtTaskName.getText().equals("")){
    			taskList = flowMetaData.queryTaskList(taskflow.getTaskflowID());
    		} else {
    			taskList = flowMetaData.queryTaskListBykeyword(taskflow.getTaskflow(),edtTaskName.getText());
    		}

    		ComparatorTask comparator=new ComparatorTask();
    		Collections.sort(taskList, comparator);

    		lmFlows.removeAllElements();
    		Iterator iter = taskList.iterator();
    		Task task;
    		while (iter.hasNext()) {
    			task = (Task) iter.next();
    			TaskView tv = new TaskView(task);
    			lmFlows.addElement(tv);
    		}
    	}
    }
    
    private void initTaskList(List<Task> taskList) {

    		List<Task> tasksList = null;
    		
    		if (edtTaskName.getText().equals("")){
    			tasksList = taskList;
    		} else {
	    		tasksList = searchTaskList(edtTaskName.getText());
	    	}
	    	ComparatorTask comparator=new ComparatorTask();
	        Collections.sort(tasksList, comparator);

	    	lmFlows.removeAllElements();
	    	Iterator iter = tasksList.iterator();
	    	Task task;
	    	while (iter.hasNext()) {
	    		task = (Task) iter.next();
	    		TaskView tv = new TaskView(task);
	    		lmFlows.addElement(tv);
	    	}
    }

    
    private List<Task> searchTaskList(String taskName) {
    	List<Task> list = new ArrayList<Task>();
    	for(Task task: taskList){
    		if ((task.getTask().indexOf(taskName) > -1) || (task.getDescription().indexOf(taskName) > -1)){
    			list.add(task);
    		}
    	}
    	
    	return list;
    }
    public void showDialog(Taskflow taskflow) {
    	this.taskflow = taskflow;
    	initTaskList();
    	this.setVisible(true);
    }
    
    public void showDialog(List<Task> taskList) {
    	this.taskList = taskList;
    	initTaskList(taskList);
    	this.setVisible(true);
    }

    public Task getSelectedTask() {
    	Task task = ((TaskView)lstFlows.getSelectedValue()).getTask();
    	return task;
    }

    public boolean isApproved() {
	return isApproved;
    }

    private class ButtonHandler implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    JButton btn = (JButton) arg0.getSource();
	    
	    
	    if (btn == btnSearch){
	    	List<Task> tasksList = null;
	    	if (taskList == null){
	    		tasksList = flowMetaData.queryTaskListBykeyword(taskflow.getTaskflow(),edtTaskName.getText());
	    	} else {
	    		tasksList = searchTaskList(edtTaskName.getText());
	    	}
	    	ComparatorTask comparator=new ComparatorTask();
	        Collections.sort(tasksList, comparator);

	    	lmFlows.removeAllElements();
	    	Iterator iter = tasksList.iterator();
	    	Task task;
	    	while (iter.hasNext()) {
	    		task = (Task) iter.next();
	    		TaskView tv = new TaskView(task);
	    		lmFlows.addElement(tv);
	    	}
	    	
	    } else if (btn == btnOk) {
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

    private class TaskView {
	private Task task;

	public TaskView(Task tf) {
	    this.task = tf;
	}

	public String toString() {
	    return task.getTask() + " ["+ task.getDescription() + "]";
	}

	public Task getTask() {
	    return task;
	}

	public void setTask(Task task) {
	    this.task = task;
	}
    }

    private class TaskflowCellRenderer extends JLabel implements
    ListCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value,
    			int index, boolean isSelected, boolean cellHasFocus) {
    		TaskView tv = (TaskView) value;
    		setText(tv.toString());
    		setForeground(list.getForeground());
    		
    		setForeground(Color.RED);
    		
    		if (isSelected) {
    			setBackground(list.getSelectionBackground());
    		} else {
    			setBackground(list.getBackground());
    		}
    		setOpaque(true);
    		return this;
    	}
    }
    
    
    class ComparatorTask implements Comparator{

    	 public int compare(Object arg0, Object arg1) {
	    	 Task t0=(Task)arg0;
	    	 Task t1=(Task)arg1;
	    	 int flag=t0.getTask().compareTo(t1.getTask());
	    	 
	    	 return flag;
    	 }
    }
}
