package com.aspire.flow.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperFactory {
	private static CuratorFramework client;
	private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);

	public static CuratorFramework create() {
		if (client != null) {
			client = CuratorFrameworkFactory.newClient("192.168.98.143:2181", retryPolicy);
			client.start();
		}
		return client;
	}
}
