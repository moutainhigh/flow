package com.aspire.etl.uic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aspire.etl.flowdefine.StepType;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowdefine.TaskflowGroup;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.MQDocument;

/**
 * 
 * @author wangcaiping
 * @since 20080910
 */
public class TaskflowDialog extends WcpCommonDialog {
	
	private static final long serialVersionUID = 1L;
	
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
    	
	TaskflowDialog td = new TaskflowDialog(null, flowMetaData);
	td.setVisible(true);
    }

    private JTextField txtName, txtDesc, txtStep;

    private JComboBox cbCycle, cbGroup, cbThreadnum;

    private JTextArea txtRemark;

    private Taskflow taskflow;

    private FlowMetaData flowMetaData;

    private boolean result;

    public TaskflowDialog(JFrame parent, FlowMetaData fmd) {
	super(parent, "流程配置对话框", true, JDialog.HIDE_ON_CLOSE);
	this.flowMetaData = fmd;
	this.addCenterPanel(createMainPanel());
	this.addOkHandler(new SaveHandler());
	this.pack();
	this.setResizable(false);
    }

    private JPanel createMainPanel() {
	JPanel pnl = new JPanel(new GridBagLayout());
	pnl.setBorder(BorderFactory.createTitledBorder("流程属性"));
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.insets = new Insets(5, 5, 5, 5);

	addItem(0, 0, 1, 1, new JLabel("流程名:"), pnl, gbc);
	txtName = new JTextField(30);
	addItem(1, 0, 3, 1, txtName, pnl, gbc);
	addItem(0, 1, 1, 1, new JLabel("描述:"), pnl, gbc);
	txtDesc = new JTextField(30);
	addItem(1, 1, 3, 1, txtDesc, pnl, gbc);
	addItem(0, 2, 1, 1, new JLabel("周期类型:"), pnl, gbc);
	cbCycle = new JComboBox();
	initCycleList();
	gbc.ipadx = 80;
	addItem(1, 2, 1, 1, cbCycle, pnl, gbc);
	gbc.ipadx = 0;
	addItem(2, 2, 1, 1, new JLabel("周期步长:"), pnl, gbc);
	txtStep = new JTextField(6);
	MQDocument mqDoc = new MQDocument();
	mqDoc.setCharLimit("[0-9]");
	txtStep.setDocument(mqDoc);
	addItem(3, 2, 1, 1, txtStep, pnl, gbc);
	addItem(0, 3, 1, 1, new JLabel("分组:"), pnl, gbc);
	cbGroup = new JComboBox();
	initGroupList();
	gbc.ipadx = 0;
	addItem(1, 3, 1, 1, cbGroup, pnl, gbc);
	
	//并发数选项初始化
	cbThreadnum = new JComboBox();
	cbThreadnum.addItem("1");
	cbThreadnum.addItem("2");
	cbThreadnum.addItem("4");
	cbThreadnum.addItem("6");
	cbThreadnum.addItem("8");
	
	addItem(2, 3, 1, 1, new JLabel("并发数:"), pnl, gbc);
	addItem(3, 3, 1, 1, cbThreadnum, pnl, gbc);
	gbc.ipadx = 0;
	addItem(0, 4, 1, 1, new JLabel("备注:"), pnl, gbc);
	txtRemark = new JTextArea(7, 30);
	txtRemark.setLineWrap(true);
	
	JScrollPane sp = new JScrollPane(txtRemark);
	addItem(1, 4, 3, 3, sp, pnl, gbc);
	return pnl;
    }

    public boolean showDialog(Taskflow tf) {
	result = false;
	if (tf != null){
	loadValue(tf);
	}
	
	this.setVisible(true);
	return result;
    }

    private void initCycleList() {
	List lst = flowMetaData.queryStepTypeList();
	Iterator it = lst.iterator();
	while (it.hasNext()) {
	    StepType st = (StepType) it.next();
	    if (st.getFlag() == StepType.DISPLAY_YES) {
		cbCycle.addItem(st);
	    }
	}
    }

    private void initGroupList() {
	List lst = flowMetaData.queryTaskflowGroupList();
	Iterator it = lst.iterator();
	while (it.hasNext()) {
	    TaskflowGroup tg = (TaskflowGroup) it.next();
	    //过滤掉大纲组：
	    if (tg.getGroupID() != 0){
	    	cbGroup.addItem(tg);
	    }
	}
    }

    private void loadValue(Taskflow taskflow) {
	this.taskflow = taskflow;
	MQDocument mqDoc = new MQDocument();
	mqDoc.setCharLimit("[^\u4e00-\u9fa5]");
	this.txtName.setDocument(mqDoc);
	this.txtName.setText(taskflow.getTaskflow());
	this.txtDesc.setText(taskflow.getDescription());
	this.txtStep.setText("" + taskflow.getStep());
	this.txtRemark.setText(taskflow.getMemo());
	
	this.cbThreadnum.setSelectedItem(taskflow.getThreadnum()+ "");
	StepType st = flowMetaData.queryStepType(taskflow.getStepType());
	if (st != null) {
	    cbCycle.setSelectedItem(st);
	}
	TaskflowGroup tg = flowMetaData.queryTaskflowGroup(taskflow
		.getGroupID());
	if (tg != null) {
	    cbGroup.setSelectedItem(tg);
	}
    }

    private class SaveHandler implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    String strName = txtName.getText().trim();

	    try {
	    	// 查一下元数据库,是否有同名的流程，有则返回ID
	    	Integer id = flowMetaData.getTaskflowIDbyName(strName);
	    	if (id != null) {
	    		// 找到
	    		if (!id.equals(taskflow.getTaskflowID())) {
	    			// 并且ID与当前ID不同，说明同名流程已存在
	    			WcpMessageBox.warn(TaskflowDialog.this, "名称为" + strName
	    					+ "的流程已存在,请重新命名!");
	    			result = false;
	    			return;
	    		}
	    	}
	    } catch (Exception e) {
		e.printStackTrace();
		WcpMessageBox.postException(TaskflowDialog.this, e);
		result = false;
		return;
	    }

	    if (strName.equals("")){
	    	WcpMessageBox.warn(TaskflowDialog.this, "流程名不能为空,请重新输入!");
	    	txtName.requestFocus();
	    	result = false;
	    	return;
	    } else if (txtDesc.getText().trim().equals("")){
	    	WcpMessageBox.warn(TaskflowDialog.this, "流程描述不能为空,请重新输入!");
	    	txtDesc.requestFocus();
	    	result = false;
	    	return;
	    } else if (Integer.parseInt(txtStep.getText()) > 10000){
	    	WcpMessageBox.warn(TaskflowDialog.this, "周期步长的值超出输入范围[0-9999],请重新输入!");
	    	txtStep.requestFocus();
	    	result = false;
	    	return;
	    }
	    
	    //设置大纲中的任务名称
	    Task outlineTask = flowMetaData.queryOutlineTaskByTaskflow(taskflow);
	    if (outlineTask != null){
	    	outlineTask.setTask(strName);
	    	outlineTask.setDescription(txtDesc.getText().trim());
	    }
	    
	    taskflow.setTaskflow(strName);
	    StepType stepType = (StepType) cbCycle.getSelectedItem();
	    TaskflowGroup group = (TaskflowGroup) cbGroup.getSelectedItem();
	    taskflow.setDescription(txtDesc.getText().trim());
	    taskflow.setStepType(stepType.getStepType());
	    taskflow.setGroupID(group.getGroupID());
	    taskflow.setStep(Integer.parseInt(txtStep.getText().trim()));
	    taskflow.setMemo(txtRemark.getText().trim());
	    taskflow.setThreadnum(Integer.parseInt(String.valueOf(cbThreadnum.getSelectedItem())));
	    result = true;
	    setVisible(false);
	}
    }
}
