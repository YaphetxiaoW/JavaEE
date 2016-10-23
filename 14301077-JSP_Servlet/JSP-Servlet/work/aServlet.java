import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class aServlet implements Servlet {
	public void init(ServletConfig config) throws ServletException {
		System.out.println("init");
	}

	public void destroy() {
		System.out.println("destroy");
	}

	public String getServletInfo() {
		return null;
	}

	public ServletConfig getServletConfig() {
		return null;
	}

        public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

           

  response.setContentType("  text/html;charset=UTF-8 ");out.println("     <html>	             <head>         <title>hello</title>		 ");		    int a=1;		 ;out.println("		        </head>        <body>               hello Java JSP！现在时间是:");out.println(new java.util.Date());out.println("       </body>    </html>");}}