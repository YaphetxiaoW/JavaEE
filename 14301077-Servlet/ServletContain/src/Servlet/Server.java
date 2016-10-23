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
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

				if (IsExistServlet(request.getUri().toString())) {
					// servlet����
					ServletProcessor processor = new ServletProcessor();
					processor.process(request, response);
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

	public boolean IsExistServlet(String url) {

		Element element = null;
		// ����ʹ�þ���·��
		File f = new File("web.xml");

		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(f);

			// �õ�һ��elment��Ԫ��
			element = dt.getDocumentElement();

			// ��ø�Ԫ���µ��ӽڵ�
			NodeList childNodes = element.getChildNodes();

			NodeList theNodeList = null;

			// ������Щ�ӽڵ�
			for (int i = 0; i < childNodes.getLength(); i++) {
				// ���ÿ����Ӧλ��i�Ľ��
				Node node1 = childNodes.item(i);

				// ƥ��ÿһ��servelet-mapping���uri
				   if("servlet-mapping".equals(node1.getNodeName())) 
			        {
				    	
				    	NodeList nodeDetail = node1.getChildNodes();
					     
					    for (int j = 0; j < nodeDetail.getLength(); j++) 
					    {
					        Node detail = nodeDetail.item(j);
					      
					        if ("url-pattern".equals(detail.getNodeName()))
					        {
					        	// ƥ��url
					        	if (url.equals(detail.getTextContent()))//���ƥ�䵽�����ýڵ�ȡ��
					    	    	theNodeList=nodeDetail;
					        }
					    	    
					     }
				    }
				  }
				   
				   String Sname=null;
				   //ȡ������
				   for (int j = 0; j < theNodeList.getLength(); j++) 
				   {
				        Node detail = theNodeList.item(j);
				      
				        if ("servlet-name".equals(detail.getNodeName())) // ƥ��name
				        {
				        	return true;
				        }			        	
				    }
				   
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
}
