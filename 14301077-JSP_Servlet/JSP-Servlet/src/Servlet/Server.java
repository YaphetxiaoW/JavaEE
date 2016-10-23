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

	// �رշ�������
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	public static void main(String[] args) {
		Server server = new Server();
		// �ȴ���������
		server.await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8888;
		try {
			// �������׽��ֶ���
			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// ѭ���ȴ�����

		while (true) {

			Socket socket = null;

			InputStream input = null;

			OutputStream output = null;

			try {
				// �ȴ����ӣ����ӳɹ��󣬷���һ��Socket����
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				// ����Request���󲢽���
				Request request = new Request(input);
				request.parse();
				// ����Ƿ��ǹرշ�������
				if (request.getUri().equals(SHUTDOWN_COMMAND)) {
					break;
				}

				// ���� Response ����
				Response response = new Response(output);
				response.setRequest(request);

				String uri=request.getUri();					
				String jspName = uri.substring(uri.lastIndexOf("/") + 1);

				if (request.getUri().endsWith(".jsp")&&isExistJSP(jspName)) {
					//����jsp�ļ�									
					JSPparse  jsp=new JSPparse(jspName);													
					// servlet����										
					ServletProcessor processor = new ServletProcessor();					
					String servletName=jspName.split(".jsp")[0]+"Servlet";			
					processor.process(request, response,servletName);
					
				} else {
					// ��̬��Դ����
					StaticResourceProcessor processor = new StaticResourceProcessor();
					processor.process(request, response);
				}
				// �ر� socket
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
