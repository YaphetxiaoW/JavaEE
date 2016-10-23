package Servlet;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ServletProcessor {

	public void process(Request request, Response response,String servletName) {

		//String uri = request.getUri();
		//String servletName = uri.substring(uri.lastIndexOf("/") + 1);

		//String servletName=getServeletName(uri);
		// ������������ڴ�ָ��JAR�ļ���Ŀ¼������
		URLClassLoader loader = null;
		try {
			URLStreamHandler streamHandler = null;
			URL[] urls = new URL[1];
			File classPath = new File(Constants.JSP_JAVA_ROOT);
			String repository = (new URL("file", null,
					classPath.getCanonicalPath() + File.separator)).toString();
			
			// �����������
			urls[0] = new URL(null, repository, streamHandler);  
		    loader = new URLClassLoader(urls); 
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		Class<?> myClass = null;
		try {
			// ���ض�Ӧ��servlet��
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
		}

		Servlet servlet = null;

		try {
			// ����servletʵ��
			servlet = (Servlet) myClass.newInstance();
			// ִ��ervlet��service����
			servlet.service((ServletRequest) request,
					(ServletResponse) response);
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}

	}
}