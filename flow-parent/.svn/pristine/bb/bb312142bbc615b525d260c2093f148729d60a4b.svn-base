package com.aspire.etl.uic;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public class DirectoryEntry extends ConnectionEntry {

	private String path;

	public static final String Tag = "Directory";

	public DirectoryEntry() {
		super(Tag);
	}

	public DirectoryEntry(String path) {
		this();
		this.path = path;
	}

	public Element getXMLElement() {
		DefaultElement elm = new DefaultElement(this.getConnectionModeName());
		DefaultElement e_path = new DefaultElement("path");
		e_path.setText(path);
		DefaultElement e_logon_time = new DefaultElement("logon_time");
		e_logon_time.setText("" + logonTime);
		elm.add(e_path);
		elm.add(e_logon_time);
		return elm;
	}

	public void setXMLElement(Element elm) {
		this.path = elm.elementTextTrim("path");
		if (elm.elementTextTrim("logon_time") != null) {
			this.logonTime = Long
					.parseLong(elm.elementTextTrim("logon_time"));
		} else {
			this.logonTime = System.currentTimeMillis();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return path;
	}

}