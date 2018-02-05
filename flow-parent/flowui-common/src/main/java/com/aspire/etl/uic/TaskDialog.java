package com.aspire.etl.uic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aspire.etl.uic.WcpMessageBox;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.MQDocument;
import com.aspire.etl.tool.Utils;

/**
 * 
 * @author wangcaiping
 * @since 20080909
 */
public class TaskDialog extends WcpCommonDialog {
	
	private static final long serialVersionUID = 1L;
	
    private Task task;

    private Map<String, String> attributeMap;

    private FlowMetaData flowMetaData;

    private JTextField txtType, txtName, txtAlert, txtPerf, txtDesc, txtFlowName;

    private JTextArea txtRemark;

    private JFrame parentFrame;
    
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
    	
		TaskDialog td = new TaskDialog(null, flowMetaData);
		//td.showDialog(flowMetaData.queryTask(320240922));
		td.setVisible(true);
    }

    public TaskDialog(JFrame parent, FlowMetaData fmd) {
	super(parent, "任务配置对话框", true, JDialog.HIDE_ON_CLOSE);
	this.flowMetaData = fmd;
	this.parentFrame = parent;
	this.addCenterPanel(createMainPanel());
	this.addOkHandler(new SaveHandler());
	
	this.setSize(new Dimension(500,600));
	this.pack();
	this.setResizable(false);
    }

    private void loadValue(Task task) {
	this.task = task;
	TaskType taskType = flowMetaData.queryTaskType(task.getTaskType());
	this.attributeMap = flowMetaData
		.queryTaskAttributeMap(task.getTaskID());
	String taskTypeDesc = taskType.getDescription();
	MQDocument mqDoc = new MQDocument();
	mqDoc.setCharLimit("[^\u4e00-\u9fa5]");
	this.txtType.setText(taskTypeDesc);
	this.txtName.setDocument(mqDoc);
	this.txtName.setText(task.getTask());
	Taskflow taskFlow = flowMetaData.queryTaskflow(task.getTaskflowID());
	this.txtFlowName.setText(taskFlow.getTaskflow()+ "(" + taskFlow.getDescription() + ")");
	this.txtFlowName.setEditable(false);
	this.txtAlert.setText(task.getAlertID());
	this.txtPerf.setText(task.getPerformanceID());
	this.txtDesc.setText(task.getDescription());
	this.txtRemark.setText(task.getMemo());
	this.txtRemark.setLineWrap(true);
    }

    private JPanel createMainPanel() {
	JPanel pnl = new JPanel(new GridBagLayout());
	pnl.setBorder(BorderFactory.createTitledBorder("任务属性"));
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.insets = new Insets(5, 6, 5, 5);

	SynInputHandler sih = new SynInputHandler();
	JLabel lblName = new JLabel("任务类型:");
	txtType = new JTextField(30);
	txtType.setEditable(false);
	JButton btnDP = new JButton("参数列表...");
	btnDP.addActionListener(new ViewParametersHandler());
	addItem(0, 0, 1, 1, lblName, pnl, gbc);
	addItem(1, 0, 2, 1, txtType, pnl, gbc);
	addItem(3, 0, 1, 1, btnDP, pnl, gbc);
	
	
	txtFlowName = new JTextField(40);//加宽流程名显示长度
	txtFlowName.setEditable(false);
	addItem(0, 1, 1, 1, new JLabel("所属流程:"), pnl, gbc);
	addItem(1, 1, 3, 1, txtFlowName, pnl, gbc);
	
	txtName = new JTextField(30);
	addItem(0, 2, 1, 1, new JLabel("任务名:"), pnl, gbc);
	addItem(1, 2, 3, 1, txtName, pnl, gbc);
	addItem(0, 3, 1, 1, new JLabel("告警ID:"), pnl, gbc);
	txtAlert = new JTextField(15);
	txtAlert.setToolTipText("双击可同步任务名");
	txtAlert.addMouseListener(sih);
	addItem(1, 3, 3, 1, txtAlert, pnl, gbc);
	addItem(0, 4, 1, 1, new JLabel("性能ID:"), pnl, gbc);
	txtPerf = new JTextField(15);
	txtPerf.setToolTipText("双击可同步任务名");
	txtPerf.addMouseListener(sih);
	addItem(1, 4, 3, 1, txtPerf, pnl, gbc);
	addItem(0, 5, 1, 1, new JLabel("描述:"), pnl, gbc);
	txtDesc = new JTextField(30);
	addItem(1, 5, 3, 1, txtDesc, pnl, gbc);
	JLabel lblRemark = new JLabel("备注:");
	addItem(0, 6, 1, 1, lblRemark, pnl, gbc);
	txtRemark = new JTextArea(7, 30);
	JScrollPane sp = new JScrollPane(txtRemark);
	addItem(1, 6, 3, 3, sp, pnl, gbc);
	return pnl;
    }

    public boolean showDialog(Task t) {
	loadValue(t);
	this.setVisible(true);
	return true;
    }

    private class SaveHandler implements ActionListener {
    	public void actionPerformed(ActionEvent arg0) {
    		boolean result = true; 
    		if (txtName.getText().equals("")){
    			WcpMessageBox.warn(TaskDialog.this, "任务名不能为空,请重新输入!");
    			txtName.requestFocus();
    			result = false;
    		} else if (txtDesc.getText().trim().equals("")){
    			WcpMessageBox.warn(TaskDialog.this, "描述不能为空,请重新输入!");
    			txtDesc.requestFocus();
    			result = false;    			
    		} else if (txtAlert.getText().trim().length() > 15){
    			WcpMessageBox.warn(TaskDialog.this, "告警ID的值超出可输入长度(0-15),请重新输入!");
    			txtAlert.requestFocus();
    			result = false;
    		}else if (txtPerf.getText().trim().length() > 15){
    			WcpMessageBox.warn(TaskDialog.this, "性能ID的值超出可输入长度(0-15),请重新输入!");
    			txtPerf.requestFocus();
    			result = false;
    		}
    		if (result) {
    			task.setTask(txtName.getText().trim());
    			task.setAlertID(txtAlert.getText().trim());
    			task.setPerformanceID(txtPerf.getText().trim());
    			task.setDescription(txtDesc.getText().trim());
    			task.setMemo(txtRemark.getText().trim());
    			TaskDialog.this.setVisible(false);
    		}
    	}
    }

    private class SynInputHandler extends MouseAdapter {
	public void mouseClicked(MouseEvent me) {
	    int cnt = me.getClickCount();
	    if (cnt >= 2) {
		JTextField txt = (JTextField) me.getSource();
		txt.setText(txtName.getText());
	    }
	}
    }

    private class ViewParametersHandler implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    String taskTypeName = task.getTaskType();
	    TaskType taskType = flowMetaData.queryTaskType(taskTypeName);
	    String className = taskType.getDesignerPlugin();

	    String desingerPluginDir = taskType.getDesignerPluginJar();
	    String errorMsg = "从" + desingerPluginDir + "\n加载插件" + className
		    + "失败!\n";
	    try {
		Class dialogClass = Utils.loadClassByName(desingerPluginDir,
			className);
		Constructor cDialogConstructor = dialogClass
			.getConstructor(new Class[] { java.awt.Frame.class });
		JDialog paramDialog = (JDialog) cDialogConstructor
			.newInstance(new Object[] { parentFrame });
		Method mLoadValue = dialogClass.getMethod("loadValue",
			new Class[] { java.util.Map.class });
		mLoadValue.invoke(paramDialog, new Object[] { attributeMap });

		paramDialog.setLocationRelativeTo(TaskDialog.this);
		paramDialog.setVisible(true);

		flowMetaData.insertTaskAttributeMap(task.getTaskID(),
			attributeMap);
	    } catch (MalformedURLException e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    } catch (ClassNotFoundException e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    } catch (InstantiationException e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    } catch (IllegalAccessException e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    } catch (SecurityException e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    } catch (NoSuchMethodException e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    } catch (IllegalArgumentException e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    } catch (InvocationTargetException e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    } catch (Exception e) {
	    	e.printStackTrace();
		WcpMessageBox.postError(TaskDialog.this, errorMsg
			+ e.getMessage());
	    }
	}
    }
}
