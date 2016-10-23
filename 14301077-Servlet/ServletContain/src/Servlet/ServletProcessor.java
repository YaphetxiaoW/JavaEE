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

	public void process(Request request, Response response) {

		String uri = request.getUri();
		//String servletName = uri.substring(uri.lastIndexOf("/") + 1);

		String servletName=getServeletName(uri);
		// ������������ڴ�ָ��JAR�ļ���Ŀ¼������
		URLClassLoader loader = null;
		try {
			URLStreamHandler streamHandler = null;
			URL[] urls = new URL[1];
			File classPath = new File(Constants.WEB_ROOT);
			String repository = (new URL("file", null,
					classPath.getCanonicalPath() + File.separator)).toString();
			// �����������
			urls[0] = new URL(null, repository, streamHandler);  
		    loader = new URLClassLoader(urls); 
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		Class myClass = null;
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
	
	//����web.xml�ļ�
	public String getServeletName(String url)
	 {
		 String ServeletName = null;
		
		 Element element = null;
		  // ����ʹ�þ���·��
		  File f = new File("web.xml");

		  DocumentBuilder db = null;
		  DocumentBuilderFactory dbf = null;
		  try {
		   dbf = DocumentBuilderFactory.newInstance();
		   db = dbf.newDocumentBuilder();
		   Document dt = db.parse(f);
		   
		   // �õ�һ��elment��Ԫ��
		   element = dt.getDocumentElement();
		   
		   // ��ø�Ԫ���µ��ӽڵ�
		   NodeList childNodes = element.getChildNodes();
		   
		   NodeList theNodeList = null;

		   // ������Щ�ӽڵ�
		   for (int i = 0; i < childNodes.getLength(); i++) 
		   {
		    // ���ÿ����Ӧλ��i�Ľ��
		    Node node1 = childNodes.item(i);
		    
		    //ƥ��ÿһ��servelet-mapping���uri
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
		        	Sname = detail.getTextContent();
		        }
		        	
		    }
		   
		   NodeList theNodeList1 = null;
		   
		   //�ٴα�����ƥ���Ӧservelet-class
		   for (int i = 0; i < childNodes.getLength(); i++) {
			    // ���ÿ����Ӧλ��i�Ľ��
			    Node node1 = childNodes.item(i);
			    
			    //ƥ��ÿһ��servelet���uri
		        if("servlet".equals(node1.getNodeName())) {
			    	
			    	NodeList nodeDetail = node1.getChildNodes();
				     
				     for (int j = 0; j < nodeDetail.getLength(); j++) {
				      Node detail = nodeDetail.item(j);
				      
				      if ("servlet-name".equals(detail.getNodeName())) // ƥ��url��Ӧ����
				    	  if (Sname.equals(detail.getTextContent()))//���ƥ�䵽�����ýڵ�ȡ��
				    		  theNodeList1 = nodeDetail;
				  }
			   }
		   }
		 //ȡ��serveletname
		   for (int j = 0; j < theNodeList1.getLength(); j++) {
		        Node detail = theNodeList1.item(j);
		      
		        if ("servlet-class".equals(detail.getNodeName())) // ƥ��
		        {
		        	ServeletName = detail.getTextContent();
		        }
		        	
		    }
		}

		  catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return ServeletName;
	 }
}