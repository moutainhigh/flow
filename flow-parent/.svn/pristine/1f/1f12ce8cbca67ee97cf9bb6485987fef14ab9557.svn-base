package com.aspire.etl.uic;


import java.awt.Component;
import java.io.File;

import javax.swing.JOptionPane;

/**
 * 
 * @author wangcaiping
 * @since2007-12-02
 */
public class WcpMessageBox {
    public static void informSaving(Component comp, File f) {
	if (f == null) {
	    JOptionPane.showMessageDialog(comp, "No file saved!",
		    "Saving completed", JOptionPane.WARNING_MESSAGE);
	    return;
	}
	JOptionPane.showMessageDialog(comp, "Successfully saved file:\n"
		+ f.getAbsolutePath(), "Saving completed",
		JOptionPane.INFORMATION_MESSAGE);
    }

    public static void informSaving(Component comp, File[] fs) {
	if ((fs == null) || (fs.length == 0)) {
	    JOptionPane.showMessageDialog(comp, "No file saved!",
		    "Saving completed", JOptionPane.WARNING_MESSAGE);
	    return;
	}
	StringBuffer sb = new StringBuffer();
	sb.append("Successfully saved files:");
	for (File f : fs) {
	    sb.append("\n  ");
	    sb.append(f.getAbsolutePath());
	}
	JOptionPane.showMessageDialog(comp, sb.toString(), "Saving completed",
		JOptionPane.INFORMATION_MESSAGE);
    }

    public static void postException(Component comp, Exception e) {
	JOptionPane.showMessageDialog(comp, e.getMessage(), e.toString(),
		JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component comp, String warning) {
	JOptionPane.showMessageDialog(comp, warning, "Warning",
		JOptionPane.WARNING_MESSAGE);
    }

    public static void inform(Component comp, String inform) {
	JOptionPane.showMessageDialog(comp, inform, "Information",
		JOptionPane.INFORMATION_MESSAGE);
    }

    public static void postError(Component comp, String error) {
	JOptionPane.showMessageDialog(comp, error, "Error",
		JOptionPane.ERROR_MESSAGE);
    }
}
