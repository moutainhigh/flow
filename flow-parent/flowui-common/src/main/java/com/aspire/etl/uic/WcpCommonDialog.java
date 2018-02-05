package com.aspire.etl.uic;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author wangcaiping
 * @since 20080910
 */
public class WcpCommonDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
    private Container cont;

    private JButton btnOk;

    public WcpCommonDialog(JFrame parent, String title, boolean modal,
	    int closeOp) {
	super(parent, title, modal);
	this.setDefaultCloseOperation(closeOp);
	this.setLocationRelativeTo(parent);
	cont = this.getContentPane();
	cont.setLayout(new BorderLayout());
	JPanel pnlBtn = createButtonPanel();
	cont.add(pnlBtn, BorderLayout.SOUTH);
    }

    protected void addCenterPanel(JPanel pnl) {
	if (pnl != null) {
	    cont.add(pnl, BorderLayout.CENTER);
	    cont.validate();
	}
    }

    protected void addOkHandler(ActionListener al) {
	btnOk.addActionListener(al);
    }

    private JPanel createButtonPanel() {
	JPanel pnl = new JPanel();
	btnOk = new JButton("确定");
	JButton btnCancel = new JButton("取消");
	btnCancel.addActionListener(new CancelHandler());
	pnl.add(btnOk);
	pnl.add(btnCancel);
	return pnl;
    }

    protected void addItem(int x, int y, int w, int h, JComponent comp,
	    JPanel pnl, GridBagConstraints g) {
	g.gridx = x;
	g.gridy = y;
	g.gridwidth = w;
	g.gridheight = h;
	pnl.add(comp, g);
    }

    private class CancelHandler implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    int closeOp = WcpCommonDialog.this.getDefaultCloseOperation();
	    if (closeOp == JDialog.HIDE_ON_CLOSE) {
		WcpCommonDialog.this.setVisible(false);
	    } else if (closeOp == JDialog.DISPOSE_ON_CLOSE) {
		WcpCommonDialog.this.dispose();
	    }
	}
    }
}
