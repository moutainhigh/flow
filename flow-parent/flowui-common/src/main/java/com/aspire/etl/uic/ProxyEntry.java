package com.aspire.etl.uic;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public class ProxyEntry extends ConnectionEntry {
	private String server;

	private String port;

	private String dbAlias;

	public static final String Tag = "Proxy";

	public ProxyEntry() {
		super(Tag);
	}

	public ProxyEntry(String server, String port, String dbAlias) {
		this();
		this.server = server;
		this.port = port;
		this.dbAlias = dbAlias;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Element getXMLElement() {
		DefaultElement elm = new DefaultElement(this.getConnectionModeName());
		DefaultElement e_serv = new DefaultElement("Server");
		e_serv.setText(server);
		DefaultElement e_dba = new DefaultElement("DBAlias");
		e_dba.setText(dbAlias);
		DefaultElement e_port = new DefaultElement("Port");
		e_port.setText(port);
		DefaultElement e_logon_time = new DefaultElement("logon_time");
		e_logon_time.setText("" + logonTime);

		elm.add(e_serv);
		elm.add(e_port);
		elm.add(e_dba);
		elm.add(e_logon_time);

		return elm;
	}

	public void setXMLElement(Element elm) {
		this.server = elm.elementTextTrim("Server");
		this.port = elm.elementTextTrim("Port");
		this.dbAlias = elm.elementTextTrim("DBAlias");
		if (elm.elementTextTrim("logon_time") != null) {
			this.logonTime = Long
					.parseLong(elm.elementTextTrim("logon_time"));
		} else {
			this.logonTime = System.currentTimeMillis();
		}
	}

	public String getDbAlias() {
		return dbAlias;
	}

	public void setDbAlias(String dbAlias) {
		this.dbAlias = dbAlias;
	}

	public String toString() {
		return server + ":" + port + ":" + dbAlias;
	}
}