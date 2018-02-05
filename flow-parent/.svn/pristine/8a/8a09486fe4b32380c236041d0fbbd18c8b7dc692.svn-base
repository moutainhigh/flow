package com.aspire.etl.flowengine.xmlrpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.xmlrpc.webserver.ServletWebServer;

import com.aspire.flow.exception.FlowException;
import com.aspire.flow.support.Config;

public class Server {
	public static int PORT = 8080;

	public static String USERNAME = "username";

	public static String PASSWORD = "password";
	private static String rootDir;
	private static String confDir;
	private static Properties properties;
	private Config config;
	public Server(Config config) {
		this.config = config;
	}

	public void init(int port, String userName, String password) {
		Server.PORT = port;
		Server.USERNAME = userName;
		Server.PASSWORD = password;
	}

	public void start() throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		/**
		 * 端口从配置文件读取
		 * 
		 * @author chenhaitao
		 */
		/*String userDir = System.getProperty("user.dir").replace("\\", "/");
		// 用于本地测试
		rootDir = userDir + "/target";
		File confFile = new File(rootDir + "/conf");
		if (!confFile.exists()) {
			// 用于线上部署环境
			rootDir = userDir.substring(0, userDir.lastIndexOf("/"));
			confFile = new File(rootDir + "/conf");
			if (!confFile.exists()) {
				throw new FlowException(new IllegalArgumentException("can't find bin path."));
			}
		}
		confDir = rootDir + "/conf";
		properties = new Properties();
		loadProperties();
		String str = getPort();
		Integer port = Integer.valueOf(str);*/
		Integer port = Integer.valueOf(config.getPort());
		RpcServlet servlet = new RpcServlet();
		ServletWebServer webServer = new ServletWebServer(servlet, port);
		webServer.start();
		System.out.println("Server is running!! port:" + port);
	}
	
	/*private static void loadProperties() {
		try {
			File confFile = new File(confDir);
			if (!confFile.exists()) {
				throw new FlowException(new IllegalArgumentException("can't find conf path."));
			}
			File[] propertiesFiles = confFile.listFiles();
			for (File propertiesFile : propertiesFiles) {
				if (propertiesFile.getName().endsWith(".properties")) {
					properties.load(new FileInputStream(propertiesFile));
				}
			}
		} catch (IOException e) {
			throw new FlowException(e);
		}
	}
	public static String getPort(){
		return properties.getProperty("listen.port");
	}*/
}
