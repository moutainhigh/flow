package com.aspire.etl.uic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.MetaDataException;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.MQDocument;

/**
 * 大纲配置对话框
 * @author jiangts
 * @since 20091231
 */
public class OutlineTaskflowDialog extends WcpCommonDialog {
	
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
    	
	OutlineTaskflowDialog td = new OutlineTaskflowDialog(null, flowMetaData);
	td.setVisible(true);
    }

    private JTextField txtName, txtDesc;

    private JTextArea txtRemark;

    private Taskflow outlineTaskflow;

    private FlowMetaData flowMetaData;

    private boolean result;

    public OutlineTaskflowDialog(JFrame parent, FlowMetaData fmd) {
	super(parent, "大纲配置对话框", true, JDialog.HIDE_ON_CLOSE);
	this.flowMetaData = fmd;
	this.addCenterPanel(createMainPanel());
	this.addOkHandler(new SaveHandler());
	this.pack();
	this.setResizable(false);
    }

    private JPanel createMainPanel() {
	JPanel pnl = new JPanel(new GridBagLayout());
	pnl.setBorder(BorderFactory.createTitledBorder("大纲属性"));
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.insets = new Insets(5, 5, 5, 5);

	addItem(0, 0, 1, 1, new JLabel("名称:"), pnl, gbc);
	txtName = new JTextField(30);
	addItem(1, 0, 3, 1, txtName, pnl, gbc);
	addItem(0, 1, 1, 1, new JLabel("描述:"), pnl, gbc);
	txtDesc = new JTextField(30);
	addItem(1, 1, 3, 1, txtDesc, pnl, gbc);
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


    private void loadValue(Taskflow outlineTaskflow) {
	this.outlineTaskflow = outlineTaskflow;
	MQDocument mqDoc = new MQDocument();
	mqDoc.setCharLimit("[^\u4e00-\u9fa5]");
	this.txtName.setDocument(mqDoc);
	this.txtName.setText(outlineTaskflow.getTaskflow());
	this.txtDesc.setText(outlineTaskflow.getDescription());
	this.txtRemark.setText(outlineTaskflow.getMemo());
    }

    private class SaveHandler implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    String strName = txtName.getText().trim();

	    try {
		// 查一下元数据库,是否有同名的流程，有则返回ID
		Integer id = flowMetaData.getTaskflowIDbyName(strName);
		if (id != null) {
		    // 找到
		    if (!id.equals(outlineTaskflow.getTaskflowID())) {
			// 并且ID与当前ID不同，说明同名流程已存在
			WcpMessageBox.warn(OutlineTaskflowDialog.this, "名称为" + strName
				+ "的大纲已存在,请重新命名!");
			result = false;
			return;
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
		WcpMessageBox.postException(OutlineTaskflowDialog.this, e);
		result = false;
		return;
	    }
	    
	    if (strName.equals("")){
	    	WcpMessageBox.warn(OutlineTaskflowDialog.this, "大纲名称不能为空,请重新输入!");
	    	txtName.requestFocus();
	    	result = false;
	    	return;
	    }

	    outlineTaskflow.setTaskflow(strName);
	    
	    Task outlineTask = flowMetaData.queryOutlineTaskByTaskflow(outlineTaskflow);
	    
	    if (outlineTask != null){
	    	outlineTask.setTask(strName);
	    	outlineTask.setDescription(txtDesc.getText().trim());
	    }
	    outlineTaskflow.setGroupID(0);
	    outlineTaskflow.setDescription(txtDesc.getText().trim());
	    outlineTaskflow.setMemo(txtRemark.getText().trim());
	    result = true;
	    setVisible(false);
	}
    }
}
