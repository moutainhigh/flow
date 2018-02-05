/*
 * TaskflowJDialog.java
 *
 * Created on 2008年2月19日, 下午2:56
 *
 */
//2008-05-22 wuzhuokun 修改参数时改为调用xmlrpc
package com.aspire.etl.flowmonitor.dialog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.aspire.etl.flowmonitor.Xmlrpc;
import com.aspire.etl.flowdefine.StepType;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.tool.TimeUtils;

/**
 * 
 * @author x_lixin_a
 */
public class TaskflowDialog extends javax.swing.JDialog {
	
	static org.apache.log4j.Logger log;
	
	public static final String FLAG_ZERO = "0";

	public static final String FLAG_ONE = "1";

	public static final String FLAG_TWO = "2";

	public static final String FLAG_THREE = "3";

	public static final int FLAG_INT_ZERO = 0;

	public static final int FLAG_INT_ONE = 1;

	public static final int FLAG_INT_TWO = 2;

	public static final int FLAG_INT_THREE = 3;

	public static final String statTimeTitle = "流程进度时间";
	
	public static final String TIME_ERROR_MESSAGE_START_TIME = statTimeTitle + "输入错误! \n您输入的时间不是一个正确的时间或输入的时间格式不是yyyy-MM-dd HH:mm:ss格式！";

	public static final String TIME_ERROR_MESSAGE_SCENE_START_TIME = "场景时间输入错误!\n您输入的时间不是一个正确的时间或输入的时间格式不是yyyy-MM-dd HH:mm:ss格式！";

	public static final String TIME_ERROR_MESSAGE_REDO_START_TIME = "重做起始时间输入错误!\n您输入的时间不是一个正确的时间或输入的时间格式不是yyyy-MM-dd HH:mm:ss格式！";

	public static final String TIME_ERROR_MESSAGE_REDO_END_TIME = "重做结束时间输入错误!\n您输入的时间不是一个正确的时间或输入的时间格式不是yyyy-MM-dd HH:mm:ss格式！";

	public static final String TIME_ERROR_TITLE_MESSAGE = "输入错误!";

	public static final String ERROR_MESSAGE_SCENE_START_TIME = statTimeTitle + "应该小于场景时间！";

	public static final String ERROR_MESSAGE_REDO_START_TIME = statTimeTitle + "应该等于重做开始时间！";

	public static final String ERROR_MESSAGE_REDO_END_TIME = statTimeTitle + "应该小于或等于重做结束时间！";

	public static final String TASK_FLOW_TEXT_FIELD_ERROR_MESSAGE = "任务流名字不可以输入中文字符!!!";

	public static final String TIME_00 = "00";

	public static final String TIME_01 = "01";
	
	private boolean statTimeIsNull ;
	
	private String statTime;
	
	private boolean statTimeIsChange;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TaskflowDialog(java.awt.Frame parent) {
		super(parent, true);
		try {
			initComponents();
			this.setTitle("ETL流程控制对话框");
		} catch (Exception ex) {
			Logger.getLogger(TaskflowDialog.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		log = org.apache.log4j.Logger.getLogger("FlowMonitor");
	}

	public TaskflowDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		try {
			initComponents();
			this.setTitle("ETL流程配置对话框");
		} catch (Exception ex) {
			Logger.getLogger(TaskflowDialog.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		log = org.apache.log4j.Logger.getLogger("FlowMonitor");
	}

	public void loadValue(Taskflow taskflow) throws Exception {
		this.taskflow = taskflow;

                this.taskflowName.setText(taskflow.getTaskflow() + "(" + taskflow.getDescription() +")");
                
		// 周期类型
		this.stepType = Xmlrpc.getInstance().queryStepType(
				this.taskflow.getStepType());
		this.stepTypeLValue.setText(this.stepType.getStepName());
                this.stepValue.setText("" + taskflow.getStep());

		// 流程进度时间
		this.statTimeTF.setText(TimeUtils.toChar(this.taskflow.getStatTime()));
		
		statTimeIsNull = statTimeTF.getText().trim().equals("");
		statTime = statTimeTF.getText();
		statTimeTF.setEditable(statTimeIsNull);
		// 线程数
		this.cbThreadnum.setSelectedItem(String.valueOf(taskflow.getThreadnum()));
		// 文件日志级别
		if (this.taskflow.getFileLogLevel().equals("DEBUG")) {
			this.fileLogLevelRB1.setSelected(true);
		} else if (this.taskflow.getFileLogLevel().equals("INFO")) {
			this.fileLogLevelRB2.setSelected(true);
		} else if (this.taskflow.getFileLogLevel().equals("WARN")) {
			this.fileLogLevelRB3.setSelected(true);
		} else if (this.taskflow.getFileLogLevel().equals("ERROR")) {
			this.fileLogLevelRB4.setSelected(true);
		} else if (this.taskflow.getFileLogLevel().equals("FATAL")) {
			this.fileLogLevelRB5.setSelected(true);
		} else if (this.taskflow.getFileLogLevel().equals("OFF")) {
			this.fileLogLevelRB6.setSelected(true);
		}
		// 数据库日志级别
		if (this.taskflow.getDbLogLevel().equals("DEBUG")) {
			this.dbLogLevelRB1.setSelected(true);
		} else if (this.taskflow.getDbLogLevel().equals("INFO")) {
			this.dbLogLevelRB2.setSelected(true);
		} else if (this.taskflow.getDbLogLevel().equals("WARN")) {
			this.dbLogLevelRB3.setSelected(true);
		} else if (this.taskflow.getDbLogLevel().equals("ERROR")) {
			this.dbLogLevelRB4.setSelected(true);
		} else if (this.taskflow.getDbLogLevel().equals("FATAL")) {
			this.dbLogLevelRB5.setSelected(true);
		} else if (this.taskflow.getDbLogLevel().equals("OFF")) {
			this.dbLogLevelRB6.setSelected(true);
		}

		// 禁用状态
		if (this.taskflow.getSuspend() == 0)
			this.isSuspendedLValue.setText("否");
		else if (this.taskflow.getSuspend() == 1)
			this.isSuspendedLValue.setText("是");
		// 运行状态
		if (this.taskflow.getStatus() == 0) {
			this.statusLValue1.setText("就绪");
		} else if (this.taskflow.getStatus() == 1) {
			this.statusLValue1.setText("运行");
		} else if (this.taskflow.getStatus() == 2) {
			this.statusLValue1.setText("成功");
		} else if (this.taskflow.getStatus() == -1) {
			this.statusLValue1.setText("失败");
		} else if (this.taskflow.getStatus() == 4) {
			this.statusLValue1.setText("停止");
		}
		
		if (this.taskflow.getSuspend() == 1)
			this.statusLValue1.setText("停止");

		// 运行开始时间
		this.runStartTimeLValue.setText(TimeUtils.toChar(this.taskflow
				.getRunStartTime()));
		// 运行结束时间
		this.runEndTimeLValue.setText(TimeUtils.toChar(this.taskflow
				.getRunEndTime()));
                //重做区间
                this.redoStartTimeValue.setText(TimeUtils.toChar(this.taskflow.getRedoStartTime()));
                this.redoEndTimeValue.setText(TimeUtils.toChar(this.taskflow.getRedoEndTime()));

       // this.memoTA.setText(taskflow.getMemo());
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" 生成的代码
	// <editor-fold defaultstate="collapsed" desc=" 生成的代码
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        statTimeL = new javax.swing.JLabel();
        statTimeTF = new javax.swing.JTextField();
        threadnumL = new javax.swing.JLabel();
        fileLogLevelL = new javax.swing.JLabel();
        dbLogLevelL = new javax.swing.JLabel();
        fileLogLevelRB6 = new javax.swing.JRadioButton();
        fileLogLevelRB2 = new javax.swing.JRadioButton();
        fileLogLevelRB4 = new javax.swing.JRadioButton();
        fileLogLevelRB1 = new javax.swing.JRadioButton();
        dbLogLevelRB6 = new javax.swing.JRadioButton();
        dbLogLevelRB2 = new javax.swing.JRadioButton();
        dbLogLevelRB4 = new javax.swing.JRadioButton();
        dbLogLevelRB1 = new javax.swing.JRadioButton();
        fileLogLevelRB3 = new javax.swing.JRadioButton();
        dbLogLevelRB3 = new javax.swing.JRadioButton();
        fileLogLevelRB5 = new javax.swing.JRadioButton();
        dbLogLevelRB5 = new javax.swing.JRadioButton();
        //并发数选项初始化
        cbThreadnum = new javax.swing.JComboBox();
        stepTypeL = new javax.swing.JLabel();
        stepTypeLValue = new javax.swing.JLabel();
        taskflowName = new javax.swing.JTextField();
        statTimeL1 = new javax.swing.JLabel();
        stepTypeL1 = new javax.swing.JLabel();
        stepValue = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        runStartTimeL = new javax.swing.JLabel();
        runEndTimeL = new javax.swing.JLabel();
        isSuspendedL = new javax.swing.JLabel();
        statusL1 = new javax.swing.JLabel();
        statusLValue1 = new javax.swing.JLabel();
        runStartTimeLValue = new javax.swing.JLabel();
        runEndTimeLValue = new javax.swing.JLabel();
        isSuspendedLValue = new javax.swing.JLabel();
        runEndTimeL1 = new javax.swing.JLabel();
        redoStartTimeValue = new javax.swing.JLabel();
        redoEndTimeValue = new javax.swing.JLabel();
        runStartTimeL1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("流程"));

        statTimeL.setText("当前进度：");

        statTimeTF.setEditable(false);
        statTimeTF.setToolTipText("统计时间,输入时请使用半角输入时间,格式必须为 yyyy-MM-dd HH:mm:ss");
        statTimeTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statTimeTFMouseClicked(evt);
            }
        });

        threadnumL.setText("线程数：");

        fileLogLevelL.setText("文件日志级别：");

        dbLogLevelL.setText("数据库日志级别：");

        buttonGroup1.add(fileLogLevelRB6);
        fileLogLevelRB6.setText("OFF");
        fileLogLevelRB6.setToolTipText("记录文件日志级别，最小为0，最大为3，0表示不写到文件，1表示写运行日志到文件,2表示写错误日志到文件，3表示写debug日志到文件(def:2)");

        buttonGroup1.add(fileLogLevelRB2);
        fileLogLevelRB2.setSelected(true);
        fileLogLevelRB2.setText("INFO");

        buttonGroup1.add(fileLogLevelRB4);
        fileLogLevelRB4.setText("ERROR");

        buttonGroup1.add(fileLogLevelRB1);
        fileLogLevelRB1.setText("DEBUG");

        buttonGroup2.add(dbLogLevelRB6);
        dbLogLevelRB6.setText("OFF");
        dbLogLevelRB6.setToolTipText("记录DB日志级别，最小为0，最大为3，0表示不写到数据库，1表示写运行日志到数据库,2表示写错误日志到数据库，3表示写debug日志到数据库(def:0)");

        buttonGroup2.add(dbLogLevelRB2);
        dbLogLevelRB2.setSelected(true);
        dbLogLevelRB2.setText("INFO");

        buttonGroup2.add(dbLogLevelRB4);
        dbLogLevelRB4.setText("ERROR");

        buttonGroup2.add(dbLogLevelRB1);
        dbLogLevelRB1.setText("DEBUG");

        buttonGroup1.add(fileLogLevelRB3);
        fileLogLevelRB3.setText("WARN");

        buttonGroup2.add(dbLogLevelRB3);
        dbLogLevelRB3.setText("WARN");

        buttonGroup1.add(fileLogLevelRB5);
        fileLogLevelRB5.setText("FATAL");

        buttonGroup2.add(dbLogLevelRB5);
        dbLogLevelRB5.setText("FATAL");

        cbThreadnum.addItem("1");
        cbThreadnum.addItem("2");
        cbThreadnum.addItem("4");
        cbThreadnum.addItem("6");
        cbThreadnum.addItem("8");
        cbThreadnum.setToolTipText("线程数，只可以输入数字");

        stepTypeL.setText("周期类型：");

        stepTypeLValue.setText("日");

        taskflowName.setEditable(false);
        taskflowName.setToolTipText("统计时间,输入时请使用半角输入时间,格式必须为 yyyy-MM-dd HH:mm:ss");
        taskflowName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                taskflowNameMouseClicked(evt);
            }
        });
        taskflowName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taskflowNameActionPerformed(evt);
            }
        });

        statTimeL1.setText("流程名称：");

        stepTypeL1.setText("周期步长：");

        stepValue.setText("1");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(statTimeL1)
                    .add(fileLogLevelL)
                    .add(statTimeL)
                    .add(dbLogLevelL))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fileLogLevelRB1)
                            .add(dbLogLevelRB1))
                        .add(18, 18, 18)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(dbLogLevelRB2)
                            .add(fileLogLevelRB2))
                        .add(14, 14, 14)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(fileLogLevelRB3)
                            .add(dbLogLevelRB3))
                        .add(18, 18, 18)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                                .add(dbLogLevelRB4)
                                .add(18, 18, 18)
                                .add(dbLogLevelRB5))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(fileLogLevelRB4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(fileLogLevelRB5)))
                        .add(18, 18, 18)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, dbLogLevelRB6)
                            .add(fileLogLevelRB6)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(statTimeTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 230, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(threadnumL)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbThreadnum, 0, 133, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(taskflowName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 259, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(stepTypeL)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stepTypeLValue)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(stepTypeL1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stepValue)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statTimeL1)
                    .add(taskflowName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(stepTypeL)
                    .add(stepTypeLValue)
                    .add(stepTypeL1)
                    .add(stepValue))
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(14, 14, 14)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(statTimeTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(statTimeL)
                            .add(threadnumL)
                            .add(cbThreadnum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(fileLogLevelRB1)
                            .add(fileLogLevelRB2)
                            .add(fileLogLevelRB3))
                        .add(0, 0, 0)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(dbLogLevelRB2)
                                .add(dbLogLevelL)
                                .add(dbLogLevelRB3))
                            .add(dbLogLevelRB1)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(fileLogLevelRB4)
                            .add(fileLogLevelRB6)
                            .add(fileLogLevelRB5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(dbLogLevelRB5)
                                .add(dbLogLevelRB4))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, dbLogLevelRB6)))))
            .add(jPanel1Layout.createSequentialGroup()
                .add(74, 74, 74)
                .add(fileLogLevelL)
                .add(27, 27, 27))
        );

        jButton1.setText("确认");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("取消");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("最近一次运行信息"));

        runStartTimeL.setText("运行开始时间：");

        runEndTimeL.setText("运行结束时间：");

        isSuspendedL.setText("是否挂起：");

        statusL1.setText("运行状态：");

        statusLValue1.setText("READY");

        runStartTimeLValue.setText("2008-04-25 10:32:30");

        runEndTimeLValue.setText("2008-04-25 10:32:32");

        isSuspendedLValue.setText("TRUE");

        runEndTimeL1.setText("重做开始时间：");

        redoStartTimeValue.setText("2008-04-25 10:32:32");

        redoEndTimeValue.setText("2008-04-25 10:32:32");

        runStartTimeL1.setText("重做结束时间：");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(runStartTimeL)
                            .add(runEndTimeL))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(runStartTimeLValue)
                            .add(runEndTimeLValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(runEndTimeL1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(redoStartTimeValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(runStartTimeL1)
                    .add(isSuspendedL)
                    .add(statusL1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(isSuspendedLValue)
                    .add(statusLValue1)
                    .add(redoEndTimeValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(64, 64, 64))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(runStartTimeL)
                            .add(isSuspendedLValue)
                            .add(runStartTimeLValue))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(runEndTimeL)
                            .add(statusLValue1)
                            .add(runEndTimeLValue)))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(isSuspendedL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(statusL1)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(runEndTimeL1)
                    .add(redoStartTimeValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(runStartTimeL1)
                    .add(redoEndTimeValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {isSuspendedL, runEndTimeL, runStartTimeL, statusL1}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(124, 124, 124)
                        .add(jButton1)
                        .add(124, 124, 124)
                        .add(jButton2))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void taskflowNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taskflowNameMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_taskflowNameMouseClicked

    private void taskflowNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taskflowNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_taskflowNameActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed

    	int isSuspend = 1;
    	boolean isPassValidate = true;
    	try {
    		Taskflow tf = Xmlrpc.getInstance().queryTaskflow(this.taskflow.getTaskflowID());
    		isSuspend = tf.getStatus();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	// 判断流程是否停止
    	if (isSuspend == Taskflow.RUNNING || isSuspend == Taskflow.QUEUE) {
    		JOptionPane.showMessageDialog(this, "流程正在运行中，不能修改，请先停止流程！",
    				TIME_ERROR_TITLE_MESSAGE, JOptionPane.ERROR_MESSAGE);
    		return ;
    	} else {
    		if (this.statTimeTF.getText().equals("")) {
    			JOptionPane
    			.showMessageDialog(this, statTimeTitle + "不可以为空！",
    					TIME_ERROR_TITLE_MESSAGE,
    					JOptionPane.ERROR_MESSAGE);
    			isPassValidate = false;
    		} else {
    			// 秒
    			String statTimeTFValue = TimeUtils.toChar(TimeUtils
    					.toDate(statTimeTF.getText()));
    			String statTimeSecond = statTimeTFValue.substring(17, 19);
    			boolean isStatTimeSecondNot00 = false;
    			if (!TIME_00.equals(statTimeSecond)) {
    				isStatTimeSecondNot00 = true;
    			}
    			// 分
    			String statTimeMinute = statTimeTFValue.substring(14, 16);
    			boolean isStatTimeMinuteNot00 = false;
    			if (!TIME_00.equals(statTimeMinute)) {
    				isStatTimeMinuteNot00 = true;
    			}
    			// 时
    			String statTimeHour = statTimeTFValue.substring(11, 13);
    			boolean isStatTimeHourNot00 = false;
    			if (!TIME_00.equals(statTimeHour)) {
    				isStatTimeHourNot00 = true;
    			}
    			// 日
    			String statTimeDay = statTimeTFValue.substring(8, 10);
    			// 月
    			String statTimeMonth = statTimeTFValue.substring(5, 7);
    			if (StepType.MINUTE.equals(this.stepType.getStepType())) {
    				boolean isRight = true;
    				StringBuffer buffer = new StringBuffer();
    				if (isStatTimeSecondNot00) {
    					buffer.append(statTimeTitle + " : 秒字段应该为零!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (!isRight) {
    					JOptionPane.showMessageDialog(this, buffer
    							.toString(), TIME_ERROR_TITLE_MESSAGE,
    							JOptionPane.ERROR_MESSAGE);
    				}
    			} else if (StepType.HOUR
    					.equals(this.stepType.getStepType())) {
    				boolean isRight = true;
    				StringBuffer buffer = new StringBuffer();
    				if (isStatTimeSecondNot00 || isStatTimeMinuteNot00) {
    					buffer.append(statTimeTitle + " : 分、秒字段应该为零!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (!isRight) {
    					JOptionPane.showMessageDialog(this, buffer
    							.toString(), TIME_ERROR_TITLE_MESSAGE,
    							JOptionPane.ERROR_MESSAGE);
    				}
    			} else if (StepType.DAY.equals(this.stepType.getStepType())
    					|| StepType.WEEK
    					.equals(this.stepType.getStepType())) {
    				boolean isRight = true;
    				StringBuffer buffer = new StringBuffer();
    				if (isStatTimeSecondNot00 || isStatTimeMinuteNot00
    						|| isStatTimeHourNot00) {
    					buffer.append(statTimeTitle + " : 时、分、秒字段应该为零!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (!isRight) {
    					JOptionPane.showMessageDialog(this, buffer
    							.toString(), TIME_ERROR_TITLE_MESSAGE,
    							JOptionPane.ERROR_MESSAGE);
    				}
    			} else if (StepType.HALF_MONTH.equals(this.stepType
    					.getStepType())) {

    				boolean isRight = true;
    				StringBuffer buffer = new StringBuffer();
    				if (!TIME_01.equals(statTimeDay)
    						&& !"16".equals(statTimeDay)) {
    					buffer.append(statTimeTitle + " : 半月只能为每月1号和16号!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (isStatTimeSecondNot00 || isStatTimeMinuteNot00
    						|| isStatTimeHourNot00) {
    					buffer.append(statTimeTitle + " : 时、分、秒字段应该为零!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (!isRight) {
    					JOptionPane.showMessageDialog(this, buffer
    							.toString(), TIME_ERROR_TITLE_MESSAGE,
    							JOptionPane.ERROR_MESSAGE);
    				}
    			} else if (StepType.MONTH.equals(this.stepType
    					.getStepType())) {
    				boolean isRight = true;
    				StringBuffer buffer = new StringBuffer();
    				if (!TIME_01.equals(statTimeDay)) {
    					buffer.append(statTimeTitle + " : 月只能为1号!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (isStatTimeSecondNot00 || isStatTimeMinuteNot00
    						|| isStatTimeHourNot00) {
    					buffer.append(statTimeTitle + " : 时、分、秒字段应该为零!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (!isRight) {
    					JOptionPane.showMessageDialog(this, buffer
    							.toString(), TIME_ERROR_TITLE_MESSAGE,
    							JOptionPane.ERROR_MESSAGE);
    				}
    			} else if (StepType.SEASON.equals(this.stepType
    					.getStepType())) {
    				boolean isRight = true;
    				StringBuffer buffer = new StringBuffer();
    				if (!TIME_01.equals(statTimeDay)
    						|| (!TIME_01.equals(statTimeMonth)
    								&& !"04".equals(statTimeMonth)
    								&& !"07".equals(statTimeMonth) && !"10"
    								.equals(statTimeMonth))) {
    					buffer.append(statTimeTitle + " : 季只能为1月1号,4月1号,7月1号,10月1号!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (isStatTimeSecondNot00 || isStatTimeMinuteNot00
    						|| isStatTimeHourNot00) {
    					buffer.append(statTimeTitle + " : 时、分、秒字段应该为零!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (!isRight) {
    					JOptionPane.showMessageDialog(this, buffer
    							.toString(), TIME_ERROR_TITLE_MESSAGE,
    							JOptionPane.ERROR_MESSAGE);
    				}
    			} else if (StepType.HALF_YEAR.equals(this.stepType
    					.getStepType())) {
    				boolean isRight = true;
    				StringBuffer buffer = new StringBuffer();
    				if (!TIME_01.equals(statTimeDay)
    						|| (!TIME_01.equals(statTimeMonth) && !"07"
    								.equals(statTimeMonth))) {
    					buffer.append(statTimeTitle + " : 半年只能为1月1号、7月1号!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (isStatTimeSecondNot00 || isStatTimeMinuteNot00
    						|| isStatTimeHourNot00) {
    					buffer.append(statTimeTitle + " : 时、分、秒字段应该为零!\n");
    					isPassValidate = false;
    					isRight = false;
    				}

    				if (!isRight) {
    					JOptionPane.showMessageDialog(this, buffer
    							.toString(), TIME_ERROR_TITLE_MESSAGE,
    							JOptionPane.ERROR_MESSAGE);
    				}
    			} else if (StepType.YEAR
    					.equals(this.stepType.getStepType())) {
    				boolean isRight = true;
    				StringBuffer buffer = new StringBuffer();
    				if (!TIME_01.equals(statTimeDay)
    						|| !TIME_01.equals(statTimeMonth)) {
    					buffer.append(statTimeTitle + " : 年只能为1月1号!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (isStatTimeSecondNot00 || isStatTimeMinuteNot00
    						|| isStatTimeHourNot00) {
    					buffer.append(statTimeTitle + " : 时、分、秒字段应该为零!\n");
    					isPassValidate = false;
    					isRight = false;
    				}
    				if (!isRight) {
    					JOptionPane.showMessageDialog(this, buffer
    							.toString(), TIME_ERROR_TITLE_MESSAGE,
    							JOptionPane.ERROR_MESSAGE);
    				}
    			}
    		}
    	}
    	
    	if (isPassValidate) {
    		// 最后保存
    		try {
    			String result = "";
    			// 线程数
    			int threadnum = Integer.parseInt(String.valueOf(cbThreadnum.getSelectedItem()));
    			if (taskflow.getThreadnum() != threadnum){
    				result = Xmlrpc.getInstance().updateThreadnum(taskflow.getTaskflowID(), threadnum);
    				log.info("修改线程数的处理结果：" + result);
    				JOptionPane.showMessageDialog(this, result
    						.toString(),  "系统提示",
    						JOptionPane.INFORMATION_MESSAGE);
    			}

    			//流程进度时间
    			if (statTimeIsNull){
    				result = Xmlrpc.getInstance().resetTaskflow(taskflow.getTaskflow());
    				log.info(result);
    				result = Xmlrpc.getInstance().updateStatTime(taskflow.getTaskflowID(), TimeUtils.toDate(this.statTimeTF.getText()));
    				JOptionPane.showMessageDialog(this, result
    						.toString(),  "系统提示",
    						JOptionPane.INFORMATION_MESSAGE);
    				
    			}else if (statTimeIsChange){
    			/*	result = Xmlrpc.getInstance().forceUpdateTime(taskflow.getTaskflow(), TimeUtils.toDate(this.statTimeTF.getText()));
    				JOptionPane.showMessageDialog(this, result
    						.toString(),  "系统提示",
    						JOptionPane.INFORMATION_MESSAGE);*/
    			}
    			log.debug(result);
    			// 文件日志级别
    			String fileLogLeve = "DEBUG";
    			if (buttonGroup1.getSelection() == fileLogLevelRB1
    					.getModel())
//  				FlowMetaData.getInstance()
//  				.updateFileLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "DEBUG");
    				fileLogLeve = "DEBUG";
    			else if (buttonGroup1.getSelection() == fileLogLevelRB2
    					.getModel())
//  				FlowMetaData.getInstance()
//  				.updateFileLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "INFO");
    				fileLogLeve = "INFO";
    			else if (buttonGroup1.getSelection() == fileLogLevelRB3
    					.getModel())
//  				FlowMetaData.getInstance()
//  				.updateFileLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "WARN");
    				fileLogLeve = "WARN";
    			else if (buttonGroup1.getSelection() == fileLogLevelRB4
    					.getModel())
//  				FlowMetaData.getInstance()
//  				.updateFileLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "ERROR");
    				fileLogLeve = "ERROR";
    			else if (buttonGroup1.getSelection() == fileLogLevelRB5
    					.getModel())
//  				FlowMetaData.getInstance()
//  				.updateFileLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "FATAL");
    				fileLogLeve = "FATAL";
    			else if (buttonGroup1.getSelection() == fileLogLevelRB6
    					.getModel())
//  				FlowMetaData.getInstance()
//  				.updateFileLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "OFF");
    				fileLogLeve = "OFF";
    			if (!taskflow.getFileLogLevel().equals(fileLogLeve)){
    				result = Xmlrpc.getInstance().updateFileLogLevel(taskflow.getTaskflowID(), fileLogLeve);
    				JOptionPane.showMessageDialog(this, result
							.toString(),  "系统提示",
							JOptionPane.INFORMATION_MESSAGE);
    				log.info(result);
    			}
    			// 数据库日志级别
    			String dbLogLeve = "DEBUG";
    			if (buttonGroup2.getSelection() == dbLogLevelRB1.getModel())
//  				FlowMetaData.getInstance().updateDbLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "DEBUG");
    				dbLogLeve = "DEBUG";
    			else if (buttonGroup2.getSelection() == dbLogLevelRB2
    					.getModel())
//  				FlowMetaData.getInstance().updateDbLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "INFO");
    				dbLogLeve = "INFO";
    			else if (buttonGroup2.getSelection() == dbLogLevelRB3
    					.getModel())
//  				FlowMetaData.getInstance().updateDbLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "WARN");
    				dbLogLeve = "WARN";
    			else if (buttonGroup2.getSelection() == dbLogLevelRB4
    					.getModel())
//  				FlowMetaData.getInstance().updateDbLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "ERROR");
    				dbLogLeve = "ERROR";
    			else if (buttonGroup2.getSelection() == dbLogLevelRB5
    					.getModel())
//  				FlowMetaData.getInstance().updateDbLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "FATAL");
    				dbLogLeve = "FATAL";
    			else if (buttonGroup2.getSelection() == dbLogLevelRB6
    					.getModel())
//  				FlowMetaData.getInstance().updateDbLogLevelOfTaskflow(
//  				this.taskflow.getTaskflowID(), "OFF");
    				dbLogLeve = "OFF";
    			if (!taskflow.getDbLogLevel().equals(dbLogLeve)){
    				result = Xmlrpc.getInstance().updateDbLogLevel(taskflow.getTaskflowID(), dbLogLeve);
    				log.info(result);
    				JOptionPane.showMessageDialog(this, result
    						.toString(), "系统提示",
    						JOptionPane.INFORMATION_MESSAGE);
    			}
    			//
    			this.setIsConfirm(true);
    			this.setVisible(false);
    		} catch (Exception e) {
    			e.printStackTrace();
    			log.error(e);
    		}
    	}

    }// GEN-LAST:event_jButton1ActionPerformed

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
		this.setIsConfirm(false);
		this.setVisible(false);
	}// GEN-LAST:event_jButton2ActionPerformed


	private void statTimeTFMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_statTimeTFMouseClicked
		
		if (!statTimeIsNull){
			return ;
		}
		
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		if (!this.statTimeTF.getText().equals("")) {
			gregorianCalendar.setTime(TimeUtils.toDate(this.statTimeTF
					.getText()));
			Calendar c = JDateChooser.showDialog(null, "请选择日期", gregorianCalendar,
					this.stepType);
			if (c != null) {
				
				this.statTimeTF.setText(TimeUtils.toChar(c.getTime()));
			}
		} else {
			gregorianCalendar = null;
			Calendar c = JDateChooser.showDialog(null, "请选择日期", gregorianCalendar,
					this.stepType);
			if (c != null) {
				this.statTimeTF.setText(TimeUtils.toChar(c.getTime()));
			}
		}
		
		if (!statTime.equals(statTimeTF.getText())){
			statTimeIsChange = true;
		}

	}// GEN-LAST:event_statTimeTFMouseClicked


	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				@SuppressWarnings("unused")
				Date date = new Date(System.currentTimeMillis());
				Date startDate = TimeUtils.toDate("20080101000000",
						"yyyyMMddHHmmss");
				Date sceneStartTime = TimeUtils.toDate("20080201000000",
						"yyyyMMddHHmmss");
				Date redoStartTime = TimeUtils.toDate("20080101000000",
						"yyyyMMddHHmmss");
				Date redoEndTime = TimeUtils.toDate("20080101000000",
						"yyyyMMddHHmmss");
				Taskflow taskflow1 = new Taskflow(new Random().nextInt(),
						"TestFlow", StepType.YEAR, 1, startDate, 1, 1,
						"TestFlow", 1, sceneStartTime, redoStartTime,
						redoEndTime, 10);
				taskflow1.setDbLogLevel("DEBUG");
				taskflow1.setFileLogLevel("OFF");

				TaskflowDialog dialog = new TaskflowDialog(
						new javax.swing.JFrame());
				try {
					dialog.loadValue(taskflow1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);

			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cbThreadnum;
    private javax.swing.JLabel dbLogLevelL;
    private javax.swing.JRadioButton dbLogLevelRB1;
    private javax.swing.JRadioButton dbLogLevelRB2;
    private javax.swing.JRadioButton dbLogLevelRB3;
    private javax.swing.JRadioButton dbLogLevelRB4;
    private javax.swing.JRadioButton dbLogLevelRB5;
    private javax.swing.JRadioButton dbLogLevelRB6;
    private javax.swing.JLabel fileLogLevelL;
    private javax.swing.JRadioButton fileLogLevelRB1;
    private javax.swing.JRadioButton fileLogLevelRB2;
    private javax.swing.JRadioButton fileLogLevelRB3;
    private javax.swing.JRadioButton fileLogLevelRB4;
    private javax.swing.JRadioButton fileLogLevelRB5;
    private javax.swing.JRadioButton fileLogLevelRB6;
    private javax.swing.JLabel isSuspendedL;
    private javax.swing.JLabel isSuspendedLValue;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel redoEndTimeValue;
    private javax.swing.JLabel redoStartTimeValue;
    private javax.swing.JLabel runEndTimeL;
    private javax.swing.JLabel runEndTimeL1;
    private javax.swing.JLabel runEndTimeLValue;
    private javax.swing.JLabel runStartTimeL;
    private javax.swing.JLabel runStartTimeL1;
    private javax.swing.JLabel runStartTimeLValue;
    private javax.swing.JLabel statTimeL;
    private javax.swing.JLabel statTimeL1;
    private javax.swing.JTextField statTimeTF;
    private javax.swing.JLabel statusL1;
    private javax.swing.JLabel statusLValue1;
    private javax.swing.JLabel stepTypeL;
    private javax.swing.JLabel stepTypeL1;
    private javax.swing.JLabel stepTypeLValue;
    private javax.swing.JLabel stepValue;
    private javax.swing.JTextField taskflowName;
    private javax.swing.JLabel threadnumL;
    // End of variables declaration//GEN-END:variables
	private Taskflow taskflow;
	// 周期类型
	private StepType stepType;
	private boolean isConfirm = false;

	public boolean getIsConfirm() {
		return isConfirm;
	}

	public void setIsConfirm(boolean isConfirm) {
		this.isConfirm = isConfirm;
	}

	public Taskflow getTaskflow() {
		return taskflow;
	}

}
