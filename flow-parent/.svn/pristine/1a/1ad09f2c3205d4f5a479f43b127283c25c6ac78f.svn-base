package com.aspire.etl.uic;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * 
 * @author wangcaiping
 * @since 2008-2-16
 */
public abstract class WcpAction extends AbstractAction {
    public WcpAction(String text) {
	super(text);
    }

    public WcpAction(String text, Class cls, String icon_url) {
	super(text, WcpImagePool.getIcon(cls, icon_url));
    }

    public WcpAction(String text, String icon_url) {
	super(text, WcpImagePool.getIcon(icon_url));
    }

    public WcpAction(String text, String icon_path, boolean isPath) {
	super(text, WcpImagePool.getIconOfPath(icon_path));
    }

    public WcpAction(String text, Icon icon) {
	super(text, icon);
    }

    public WcpAction(String text, Icon icon, String desc) {
	super(text, icon);
	putValue(SHORT_DESCRIPTION, desc);
    }

    public WcpAction(String text, Icon icon, String desc, int mnemonic) {
	super(text, icon);
	putValue(SHORT_DESCRIPTION, desc);
	putValue(MNEMONIC_KEY, mnemonic);
    }

    protected void setAccelerator(KeyStroke ks) {
	putValue(ACCELERATOR_KEY, ks);
    }

    public String getDescription() {
	return (String) getValue(SHORT_DESCRIPTION);
    }

    public String getText() {
	return (String) getValue(NAME);
    }

    public abstract void actionPerformed(ActionEvent ae);
}