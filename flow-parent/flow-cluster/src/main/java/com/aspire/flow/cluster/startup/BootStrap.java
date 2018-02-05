package com.aspire.flow.cluster.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import com.aspire.flow.exception.ConfigException;
import com.aspire.flow.exception.FlowException;
import com.aspire.flow.helper.ReflectHelper;
import com.aspire.flow.scanner.ApplicationClassLoader;
import com.aspire.flow.scanner.ApplicationClassLoaderFactory;
import com.aspire.flow.support.Config;

public final class BootStrap {

	private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

	private static final ApplicationClassLoader applicationClassLoader;

	private static String rootDir;

	private static final String confDir;

	private static final String libDir;

	private static final String jobDir;

	private static Properties properties;

	private static Object nodeInstance;
	
	private static Config config;

	public static void main(String[] args) throws Exception {
		start();
	}

	static {
		String userDir = System.getProperty("user.dir").replace("\\", "/");
		// 用于本地测试
		rootDir = userDir + "/target";
		File binDir = new File(rootDir + "/bin");
		if (!binDir.exists()) {
			// 用于线上部署环境
			rootDir = userDir.substring(0, userDir.lastIndexOf("/"));
			binDir = new File(rootDir + "/bin");
			if (!binDir.exists()) {
				throw new FlowException(new IllegalArgumentException("can't find bin path."));
			}
		}
		confDir = rootDir + "/conf";
		libDir = rootDir + "/lib";
		jobDir = rootDir + "/job";

		ApplicationClassLoaderFactory.setSystemClassLoader(systemClassLoader);

		applicationClassLoader = ApplicationClassLoaderFactory.getNodeApplicationClassLoader();

		Thread.currentThread().setContextClassLoader(applicationClassLoader);

		/*properties = new Properties();

		loadProperties();*/
	}

	public static void start() throws Exception {
		properties = config.getProperties();
		String nodeClassName;
		if ("masterSlave".equals(getNodeMode())) {
			nodeClassName = "com.aspire.flow.cluster.node.MasterSlaveNode";
		} else {
			throw new ConfigException();
		}
		Class<?> nodeClass = applicationClassLoader.loadClass(nodeClassName);
		Constructor<?> nodeConstructor = nodeClass.getConstructor();
		nodeInstance = nodeConstructor.newInstance();
		Method joinMethod = ReflectHelper.getInheritMethod(nodeClass, "join");
		joinMethod.invoke(nodeInstance);
		//Thread.sleep(Long.MAX_VALUE);
		/*StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        schedulerFactory.initialize(properties);
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();*/
	}

	public static String getNodeMode() {
		//return properties.getProperty("node.mode", "masterSlave");
		return config.getNodeModel();
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
	}*/

	public static Properties properties() {
		return new Properties(properties);
	}

	public static String getZookeeperAddresses() {
		//return properties.getProperty("zookeeper.addresses", "localhost:2181");
		return config.getZookeeperAddresses();
	}
	
	public static String getPort(){
		//return properties.getProperty("listen.port");
		return config.getPort();
	}

	public static Config getConfig() {
		return config;
	}

	public static void setConfig(Config config) {
		BootStrap.config = config;
	}
	
}
