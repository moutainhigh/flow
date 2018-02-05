package com.aspire.etl.flowengine.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import com.aspire.etl.flowmetadata.dao.FlowMetaData;

public class RpcServlet extends XmlRpcServlet {
	
	final static long serialVersionUID = System.currentTimeMillis();
	
	private boolean isAuthenticated(String pUserName, String pPassword) {
		
		try {
			return true;
			//return FlowMetaData.getInstance().login(pUserName, pPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
    }
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
        PropertyHandlerMapping mapping
            = (PropertyHandlerMapping) super.newXmlRpcHandlerMapping();
        AbstractReflectiveHandlerMapping.AuthenticationHandler handler =
            new AbstractReflectiveHandlerMapping.AuthenticationHandler(){
                    public boolean isAuthorized(XmlRpcRequest pRequest){
                        XmlRpcHttpRequestConfig config =
                            (XmlRpcHttpRequestConfig) pRequest.getConfig();
                        if("command.isTaskflowStopped".equals(pRequest.getMethodName())){
                        	//从C插件发过来的请求，直接放过。
                        	return true;
                        }
                        return isAuthenticated(config.getBasicUserName(),
                            config.getBasicPassword());
                    };
            };
        mapping.setAuthenticationHandler(handler);
        
        return mapping;
    }
}
