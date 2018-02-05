/*
 * AuditorJDialog.java
 *
 * Created on 2008��2��13��, ����2:21
 */

package com.aspire.etl.plugins;

import java.util.HashMap;
import java.util.Map;

import com.aspire.etl.plugins.ETLConstants;

/**
 * 
 * @author x_lixin_a
 */
public class ShellDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, String> attributeMap;

	/** Creates new form AuditorFrame */
	public ShellDialog() {
		this.setTitle("SHELL���öԻ���");

		this.setResizable(false);

		initComponents();

	}

	public ShellDialog(java.awt.Frame parent, boolean modal,
			Map<String, String> map) {

		super(parent, modal);

		this.setTitle("SHELL���öԻ���");

		this.setResizable(false);

		this.setLocationRelativeTo(null);

		initComponents();

		loadValue(map);

	}

	public ShellDialog(java.awt.Frame parent) {

		super(parent, true);

		this.setTitle("SHELL���öԻ���");

		this.setResizable(false);

		initComponents();
	}

	/**
	 * װ��ȱʡ�������
	 */
	public void loadValue(Map<String, String> map) {
		this.attributeMap = map;
		this.Shell_NameTF.setText((String) map.get(ETLConstants.SHELL_NAME));
		this.Shell_WorkDirTF.setText((String) map
				.get(ETLConstants.SHELL_WORKDIR));
	}

	/** Creates new form AuditorJDialog */
	// public AuditorJDialog(java.awt.Frame parent, boolean modal) {
	// super(parent, modal);
	// initComponents();
	// }
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" ���ɵĴ���
    // <editor-fold defaultstate="collapsed" desc=" ���ɵĴ��� ">//GEN-BEGIN:initComponents
    private void initComponents() {
        javax.swing.JLabel Shell_NameL;

        jPanel1 = new javax.swing.JPanel();
        Shell_WorkDirL = new javax.swing.JLabel();
        Shell_WorkDirTF = new javax.swing.JTextField();
        Shell_NameL = new javax.swing.JLabel();
        Shell_NameTF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "\u57fa\u672c"));
        Shell_WorkDirL.setText("\u5de5\u4f5c\u8def\u5f84\uff1a");
        Shell_WorkDirL.setToolTipText("");

        Shell_WorkDirTF.setToolTipText("shell\u811a\u672c\u5de5\u4f5c\u8def\u5f84");

        Shell_NameL.setText("\u811a\u672c\u540d\u79f0\uff1a");
        Shell_NameL.setToolTipText("");

        Shell_NameTF.setToolTipText("shell\u811a\u672c\u540d\u79f0");

        jLabel1.setText("\u63d0\u793a\uff1ashell\u811a\u672c\u6700\u540e\u4e00\u884c\u4ee3\u7801\u5fc5\u987b\u662fecho 0");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(Shell_NameL)
                            .add(Shell_WorkDirL))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(Shell_WorkDirTF)
                            .add(Shell_NameTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 210, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jLabel1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(Shell_NameL)
                    .add(Shell_NameTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(16, 16, 16)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(Shell_WorkDirL)
                    .add(Shell_WorkDirTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 24, Short.MAX_VALUE)
                .add(jLabel1))
        );

        jButton1.setText("\u786e\u8ba4");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("\u53d6\u6d88");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(105, Short.MAX_VALUE)
                .add(jButton1)
                .add(23, 23, 23)
                .add(jButton2)
                .add(88, 88, 88))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
		// TODO ���ڴ˴��������Ĵ������룺
		this.dispose();
	}// GEN-LAST:event_jButton2ActionPerformed

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
		// TODO ���ڴ˴��������Ĵ������룺
		attributeMap.put(ETLConstants.SHELL_NAME, this.Shell_NameTF.getText());
		attributeMap.put(ETLConstants.SHELL_WORKDIR, this.Shell_WorkDirTF
				.getText());
		this.dispose();
	}// GEN-LAST:event_jButton1ActionPerformed

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				HashMap<String, String> paramap = new HashMap<String, String>();

				initParaMap(paramap);

				ShellDialog dialog = new ShellDialog(new javax.swing.JFrame());
				dialog.loadValue(paramap);
				dialog.setVisible(true);
			}
		});
	}

	private static void initParaMap(Map<String, String> paramap) {
		paramap.put(ETLConstants.SHELL_NAME, "tesh.sh");
		paramap.put(ETLConstants.SHELL_WORKDIR, "./../test/");
	}

    // �������� - �������޸�//GEN-BEGIN:variables
    private javax.swing.JTextField Shell_NameTF;
    private javax.swing.JLabel Shell_WorkDirL;
    private javax.swing.JTextField Shell_WorkDirTF;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // ������������//GEN-END:variables

}