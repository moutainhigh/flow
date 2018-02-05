package com.aspire.etl.uic;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public class DirectEntry extends ConnectionEntry {

	private String driver;

	private String dbType; // 数据库类型

	private String charSet; // mysql的字符集

	private String user;

	private String server;

	private String port;

	private String SID;

	public static final String Tag = "Direct";

	public DirectEntry() {
		super(Tag);
	}

	public DirectEntry(String driver, String server, String port, String sid,
			String user, String dbType, String charSet) {
		this();
		this.driver = driver;
		this.server = server;
		this.user = user;
		this.port = port;
		this.SID = sid;
		this.dbType = dbType;
		this.charSet = charSet;
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

	public String getSID() {
		return SID;
	}

	public void setSID(String sid) {
		SID = sid;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Element getXMLElement() {
		DefaultElement elm = new DefaultElement(this.getConnectionModeName());
		DefaultElement e_dri = new DefaultElement("Driver");
		e_dri.setText(driver);
		DefaultElement e_serv = new DefaultElement("Server");
		e_serv.setText(server);
		DefaultElement e_u = new DefaultElement("User");
		e_u.setText(user);
		DefaultElement e_sid = new DefaultElement("SID");
		e_sid.setText(SID);
		DefaultElement e_port = new DefaultElement("Port");
		e_port.setText(port);
		DefaultElement e_charSet = new DefaultElement("CharSet");
		e_charSet.setText(charSet == null ? "" : charSet);
		DefaultElement e_dbType = new DefaultElement("DbType");
		e_dbType.setText(dbType == null ? "" : dbType);
		DefaultElement e_logon_time = new DefaultElement("logon_time");
		e_logon_time.setText("" + logonTime);
		elm.add(e_dri);
		elm.add(e_serv);
		elm.add(e_port);
		elm.add(e_u);
		elm.add(e_sid);
		elm.add(e_charSet);
		elm.add(e_dbType);
		elm.add(e_logon_time);
		return elm;
	}

	public void setXMLElement(Element elm) {
		this.driver = elm.elementTextTrim("Driver");
		this.server = elm.elementTextTrim("Server");
		this.port = elm.elementTextTrim("Port");
		this.user = elm.elementTextTrim("User");
		this.SID = elm.elementTextTrim("SID");
		this.charSet = elm.elementTextTrim("CharSet");
		this.dbType = elm.elementTextTrim("DbType");
		if (elm.elementTextTrim("logon_time") != null) {
			this.logonTime = Long
					.parseLong(elm.elementTextTrim("logon_time"));
		} else {
			this.logonTime = System.currentTimeMillis();
		}
	}

	public String toString() {
		return user + "@" + server + ":" + port + ":" + SID;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

}