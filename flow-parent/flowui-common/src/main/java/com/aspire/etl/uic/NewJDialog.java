/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJDialog.java
 *
 * Created on 2010-4-20, 15:21:51
 */

package com.aspire.etl.uic;

/**
 *
 * @author x_jiangts
 */
public class NewJDialog extends javax.swing.JDialog {

    /** Creates new form NewJDialog */
    public NewJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbDbType = new javax.swing.JComboBox();
        btnHist = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtServer = new javax.swing.JTextField();
        txtPort = new javax.swing.JTextField();
        txtSID = new javax.swing.JTextField();
        txtDBUser = new javax.swing.JTextField();
        txtDBPswd = new javax.swing.JTextField();
        txtCharSet = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnl.setBorder(javax.swing.BorderFactory.createTitledBorder("���ݿ���������"));
        pnl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("���ݿ�����:");
        pnl.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, -1, -1));

        cbDbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        pnl.add(cbDbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 210, -1));

        btnHist.setText("...");
        btnHist.setToolTipText("��ʷ��Ϣ");
        pnl.add(btnHist, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 20, -1));

        jLabel2.setText("������:");
        pnl.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, -1, -1));
        pnl.add(txtServer, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 60, 210, -1));
        pnl.add(txtPort, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 120, -1));
        pnl.add(txtSID, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, 50, -1));
        pnl.add(txtDBUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 120, 210, -1));
        pnl.add(txtDBPswd, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 210, -1));

        txtCharSet.setText("GBK");
        pnl.add(txtCharSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 180, 210, -1));

        jLabel4.setText("���ݿ���:");
        pnl.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, -1, -1));

        jLabel5.setText("�û�:");
        pnl.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, -1, -1));

        jLabel6.setText("����:");
        pnl.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 150, -1, 20));

        jLabel7.setText("�ַ���:");
        pnl.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, -1, -1));

        jLabel3.setText("�˿�:");
        pnl.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, -1, -1));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(70, 70, 70)
                .add(pnl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 483, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(210, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(56, 56, 56)
                .add(pnl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 212, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(292, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewJDialog dialog = new NewJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHist;
    private javax.swing.JComboBox cbDbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel pnl;
    private javax.swing.JTextField txtCharSet;
    private javax.swing.JTextField txtDBPswd;
    private javax.swing.JTextField txtDBUser;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtSID;
    private javax.swing.JTextField txtServer;
    // End of variables declaration//GEN-END:variables

}