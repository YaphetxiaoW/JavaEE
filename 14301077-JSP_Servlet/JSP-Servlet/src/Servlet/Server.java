package Servlet;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import JSP.*;

public class Server {

	// 关闭服务命令
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	public static void main(String[] args) {
		Server server = new Server();
		// 等待连接请求
		server.await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8888;
		try {
			// 服务器套接字对象
			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// 循环等待请求

		while (true) {

			Socket socket = null;

			InputStream input = null;

			OutputStream output = null;

			try {
				// 等待连接，连接成功后，返回一个Socket对象
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				// 创建Request对象并解析
				Request request = new Request(input);
				request.parse();
				// 检查是否是关闭服务命令
				if (request.getUri().equals(SHUTDOWN_COMMAND)) {
					break;
				}

				// 创建 Response 对象
				Response response = new Response(output);
				response.setRequest(request);

				String uri=request.getUri();					
				String jspName = uri.substring(uri.lastIndexOf("/") + 1);

				if (request.getUri().endsWith(".jsp")&&isExistJSP(jspName)) {
					//存在jsp文件									
					JSPparse  jsp=new JSPparse(jspName);													
					// servlet请求										
					ServletProcessor processor = new ServletProcessor();					
					String servletName=jspName.split(".jsp")[0]+"Servlet";			
					processor.process(request, response,servletName);
					
				} else {
					// 静态资源请求
					StaticResourceProcessor processor = new StaticResourceProcessor();
					processor.process(request, response);
				}
				// 关闭 socket
				socket.close();

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public boolean isExistJSP(String jspName){
		
		File file = new File(Constants.JSP_ROOT + jspName);	
		return file.exists();	
	}
}
