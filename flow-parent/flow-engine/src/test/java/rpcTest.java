import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.xmlrpc.webserver.ServletWebServer;

import com.aspire.etl.flowengine.xmlrpc.RpcServlet;

public class rpcTest {
	public static void main(String[] args) throws ServletException, IOException {
		int port = 9090;
		RpcServlet servlet=new RpcServlet();
		ServletWebServer webServer = new ServletWebServer(servlet, port);
		webServer.start();
		System.out.println("asa");
	}
}
