package Servlet;

import java.io.File;

public class Constants {
	public static final String WEB_ROOT = System.getProperty("user.dir")
			+ File.separator + "webroot";
	public static final String PART_ROOT = System.getProperty("user.dir")
			+ File.separator + "part"+"/";
	
	public static final String JSP_ROOT = System.getProperty("user.dir")
			+ File.separator + "jsp"+"/";
	
	public static final String JSP_JAVA_ROOT = System.getProperty("user.dir")
			+ File.separator + "work"+"/";
}
