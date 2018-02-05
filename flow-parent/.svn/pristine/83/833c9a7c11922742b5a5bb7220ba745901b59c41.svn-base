package com.aspire.etl.uic;

import org.dom4j.Element;

public abstract class ConnectionEntry {
	
    private String connectionModeName;

    protected long logonTime;
    
    public abstract Element getXMLElement();

    public abstract void setXMLElement(Element element);

    public abstract String toString();
    
    public ConnectionEntry(String cmn) {
	this.connectionModeName = cmn;
    }

    public String getConnectionModeName() {
	return connectionModeName;
    }
    
    public long getLogonTime(){
    	return logonTime;
    }
    
    public void setLogonTime(long date){
    	logonTime = date;
    }
    
}