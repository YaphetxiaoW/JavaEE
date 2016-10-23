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
		// 类加载器，用于从指定JAR文件或目录加载类
		URLClassLoader loader = null;
		try {
			URLStreamHandler streamHandler = null;
			URL[] urls = new URL[1];
			File classPath = new File(Constants.WEB_ROOT);
			String repository = (new URL("file", null,
					classPath.getCanonicalPath() + File.separator)).toString();
			// 创建类加载器
			urls[0] = new URL(null, repository, streamHandler);  
		    loader = new URLClassLoader(urls); 
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		Class myClass = null;
		try {
			// 加载对应的servlet类
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
		}

		Servlet servlet = null;

		try {
			// 生产servlet实例
			servlet = (Servlet) myClass.newInstance();
			// 执行ervlet的service方法
			servlet.service((ServletRequest) request,
					(ServletResponse) response);
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}

	}
	
	//解析web.xml文件
	public String getServeletName(String url)
	 {
		 String ServeletName = null;
		
		 Element element = null;
		  // 可以使用绝对路径
		  File f = new File("web.xml");

		  DocumentBuilder db = null;
		  DocumentBuilderFactory dbf = null;
		  try {
		   dbf = DocumentBuilderFactory.newInstance();
		   db = dbf.newDocumentBuilder();
		   Document dt = db.parse(f);
		   
		   // 得到一个elment根元素
		   element = dt.getDocumentElement();
		   
		   // 获得根元素下的子节点
		   NodeList childNodes = element.getChildNodes();
		   
		   NodeList theNodeList = null;

		   // 遍历这些子节点
		   for (int i = 0; i < childNodes.getLength(); i++) 
		   {
		    // 获得每个对应位置i的结点
		    Node node1 = childNodes.item(i);
		    
		    //匹配每一个servelet-mapping里的uri
	        if("servlet-mapping".equals(node1.getNodeName())) 
	        {
		    	
		    	NodeList nodeDetail = node1.getChildNodes();
			     
			    for (int j = 0; j < nodeDetail.getLength(); j++) 
			    {
			        Node detail = nodeDetail.item(j);
			      
			        if ("url-pattern".equals(detail.getNodeName()))
			        {
			        	// 匹配url
			        	if (url.equals(detail.getTextContent()))//如果匹配到，将该节点取出
			    	    	theNodeList=nodeDetail;
			        }
			    	    
			     }
		    }
		  }
		   
		   String Sname=null;
		   //取出名字
		   for (int j = 0; j < theNodeList.getLength(); j++) 
		   {
		        Node detail = theNodeList.item(j);
		      
		        if ("servlet-name".equals(detail.getNodeName())) // 匹配name
		        {
		        	Sname = detail.getTextContent();
		        }
		        	
		    }
		   
		   NodeList theNodeList1 = null;
		   
		   //再次遍历，匹配对应servelet-class
		   for (int i = 0; i < childNodes.getLength(); i++) {
			    // 获得每个对应位置i的结点
			    Node node1 = childNodes.item(i);
			    
			    //匹配每一个servelet里的uri
		        if("servlet".equals(node1.getNodeName())) {
			    	
			    	NodeList nodeDetail = node1.getChildNodes();
				     
				     for (int j = 0; j < nodeDetail.getLength(); j++) {
				      Node detail = nodeDetail.item(j);
				      
				      if ("servlet-name".equals(detail.getNodeName())) // 匹配url对应名字
				    	  if (Sname.equals(detail.getTextContent()))//如果匹配到，将该节点取出
				    		  theNodeList1 = nodeDetail;
				  }
			   }
		   }
		 //取出serveletname
		   for (int j = 0; j < theNodeList1.getLength(); j++) {
		        Node detail = theNodeList1.item(j);
		      
		        if ("servlet-class".equals(detail.getNodeName())) // 匹配
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