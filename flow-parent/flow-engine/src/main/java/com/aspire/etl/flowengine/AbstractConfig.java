package com.aspire.etl.flowengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.aspire.flow.exception.FlowException;
import com.aspire.flow.support.Config;

public abstract class AbstractConfig {
	protected static String confDir;
	protected static Properties properties;
	protected static Config config;
	protected static String rootDir;
	public void init(){
		String userDir = System.getProperty("user.dir").replace("\\", "/");
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
		config = new Config();
		config.setPort(getPort());
		config.setZookeeperAddresses(getZookeeperAddresses());
		config.setNodeModel(properties.getProperty("node.mode", "masterSlave"));
		config.setProperties(properties);
	}
	private static void loadProperties() {
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
	
	public String getPort(){
		return properties.getProperty("listen.port");
	}
	public String getZookeeperAddresses() {
		return properties.getProperty("zookeeper.addresses", "localhost:2181");
	}
}
