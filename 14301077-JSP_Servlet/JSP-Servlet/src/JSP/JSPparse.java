package JSP;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.tools.*;

import Servlet.*;

public class JSPparse {

	String jspContent = "";
	String servletContent = "";
	String jspName = null;

	ArrayList<String> stack = new ArrayList<String>();
	int top = 0;

	public JSPparse(String jspName) {
		this.jspName = jspName;

		jspRead();
		createServlet();
	}

	// 读取jsp文件
	public void jspRead() {
		File file = new File(Constants.JSP_ROOT + jspName);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			// 一次读入一行，直到读入null为文件结束
			while ((line = reader.readLine()) != null) {
				
				if(line.contains("<%@")){
					line=" <%@ text/html;charset=UTF-8 %>";
				}


				jspContent = jspContent + line;
			}

			StringBuffer buf = new StringBuffer(jspContent);
			// StringBuffer servlet= new StringBuffer();

			for (int i = 0; i < buf.length(); i++) {
				if (buf.charAt(i) == '<' && buf.charAt(i + 1) == '%'
						&& buf.charAt(i + 2) == '@') {
					stack.add("<%@");
					top++;
					// servlet.append("response.setContentType(\"");

				} else if (buf.charAt(i) == '<' && buf.charAt(i + 1) == '%'
						&& buf.charAt(i + 2) == '=') {
					stack.add("<%=");
					top++;
					// servlet.append("out.println(");

				} else if (buf.charAt(i) == '<' && buf.charAt(i + 1) == '%') {
					stack.add("<%");
					top++;

				} else if (buf.charAt(i) == '%' && buf.charAt(i + 1) == '>') {

					if (stack.get(top - 1).equals("<%@")) {
						buf.replace(i, i + 2, "**");
						stack.remove(--top);

					} else if (stack.get(top - 1).equals("<%=")) {
						buf.replace(i, i + 2, "$$");
						stack.remove(--top);

					} else if (stack.get(top - 1).equals("<%")) {
						buf.replace(i, i + 2, "&&");
						stack.remove(--top);

					}
				}
			}

			jspContent = buf.toString();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// jsp转化成Servlet文件
	public void createServlet() {

		// part
		File file1 = new File(Constants.PART_ROOT + "part.txt");
		BufferedReader reader = null;
		String part = "";
		try {
			reader = new BufferedReader(new FileReader(file1));
			String line = null;

			while ((line = reader.readLine()) != null) {
				part = part + line + "\n";
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}


		part = part.replace("PrimitiveServlet ", jspName.split(".jsp")[0]
				+ "Servlet ");

		jspContent = jspContent.replace("<%@", " response.setContentType(\" ");
		jspContent = jspContent.replace("<%=", "\");out.println(");
		jspContent = jspContent.replace("<%", "\");");
		jspContent = jspContent.replace("**", "\");out.println(\"");
		jspContent = jspContent.replace("$$", ");out.println(\"");
		jspContent = jspContent.replace("&&", ";out.println(\"");

        //生成java文件路径
		String fileName = Constants.JSP_JAVA_ROOT +jspName.split(".jsp")[0]
				+ "Servlet.java";
		File file2 = new File(fileName);
		FileWriter writer = null;
		try {
				
			if (!file2.exists()) {	
				file2.createNewFile();	
			}
				
			writer = new FileWriter(file2);
			writer.write(part);
			writer.write("\n");
			writer.write(jspContent);
			writer.write("\");");
			writer.write("}}");

			writer.close();
			
			// 获得系统编译器
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			// 读入源文件
			Iterable fileObject = fileManager.getJavaFileObjects(fileName);
			// 编译
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList("-d", "work"),
					null, fileObject);
			task.call();

			fileManager.close();
			
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		}


	}
}
