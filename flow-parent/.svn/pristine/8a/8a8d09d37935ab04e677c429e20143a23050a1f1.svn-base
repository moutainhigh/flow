package com.aspire.etl.flowengine;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

import javax.servlet.ServletException;
import javax.sound.sampled.Port;

import org.apache.xmlrpc.webserver.ServletWebServer;

import com.aspire.etl.flowengine.xmlrpc.RpcServlet;

public class LockTest {
	private  static int Port = 8080;
	public static void main(String[] args) throws ServletException, IOException {
		RpcServlet servlet=new RpcServlet();
        ServletWebServer webServer = new ServletWebServer(servlet, Port);
        webServer.start();
        System.out.println("Server is running!! port:" + Port);
        /*ZkSessionManager sessionManager = new DefaultZkSessionManager("192.168.98.143:2181",3000); 
        Lock myLock = new ReentrantZkLock("/test-locks",sessionManager);
        myLock.lock();
        try {
			while(true){
				System.out.println(8081+ "-adad");
				Thread.sleep(5000);
			}
        	prin();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			myLock.unlock();
		}*/
        prin();
	}
	
	public static void prin() {
		/*while(true){
			System.out.println(8080+ "-adad");
			Thread.sleep(5000);
		}*/
//		ZkSessionManager sessionManager = new DefaultZkSessionManager("192.168.98.143:2181",3000); 
//        final Lock myLock = new ReentrantZkLock("/test-locks",sessionManager);
       // myLock.lock();
        new Thread(new Runnable() {
			public void run() {
				//myLock.lock();
				try {
					while(true){
						System.out.println(Port+ "-adad");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
