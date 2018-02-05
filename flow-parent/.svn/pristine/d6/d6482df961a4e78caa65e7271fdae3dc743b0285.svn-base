package com.aspire.etl.uic;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author wangcaiping
 * @since2007-11-30
 */
public class WcpFileFilter extends FileFilter {
    private String[] postfixes;

    public WcpFileFilter() {
    }

    public WcpFileFilter(String[] pfs) {
	this.postfixes = pfs;
    }

    @Override
    public boolean accept(File f) {
	if (f.isDirectory()) {
	    return true;
	}
	if ((postfixes != null) && (postfixes.length > 0)) {
	    for (String str : postfixes) {
		if (f.getName().toUpperCase().endsWith(str.toUpperCase())) {
		    return true;
		}
	    }
	    return false;
	} else {
	    return true;
	}
    }

    @Override
    public String getDescription() {
	if ((postfixes != null) && (postfixes.length > 0)) {
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < postfixes.length; i++) {
		if (i != 0) {
		    sb.append("|");
		}
		sb.append("*.");
		sb.append(postfixes[i]);
	    }
	    return sb.toString();
	}
	return null;
    }
}