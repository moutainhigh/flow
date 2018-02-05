package com.aspire.etl.uic;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public class XMLRPCEntry extends ConnectionEntry {
    private String ip;

    private String user;

    private String port;

    public static final String Tag = "XMLRPC";

    public XMLRPCEntry() {
	super(Tag);
    }

    public XMLRPCEntry(String ip, String port, String user) {
	this();
	this.ip = ip;
	this.user = user;
	this.port = port;
    }

    public String getIp() {
	return ip;
    }

    public void setIp(String ip) {
	this.ip = ip;
    }

    public String getPort() {
	return port;
    }

    public void setPort(String port) {
	this.port = port;
    }

    public String getUser() {
	return user;
    }

    public void setUser(String user) {
	this.user = user;
    }

    public Element getXMLElement() {
	DefaultElement elm = new DefaultElement(this.getConnectionModeName());
	DefaultElement e_ip = new DefaultElement("Ip");
	e_ip.setText(ip);
	DefaultElement e_u = new DefaultElement("User");
	e_u.setText(user);
	DefaultElement e_port = new DefaultElement("Port");
	e_port.setText(port);
	elm.add(e_ip);
	elm.add(e_port);
	elm.add(e_u);
	return elm;
    }

    public void setXMLElement(Element elm) {
	this.ip = elm.elementTextTrim("Ip");
	this.port = elm.elementTextTrim("Port");
	this.user = elm.elementTextTrim("User");
    }

    public String toString() {
	return user + "@" + ip + ":" + port;
    }
}
