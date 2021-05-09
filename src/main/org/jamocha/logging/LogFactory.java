package org.jamocha.logging;

import javax.servlet.ServletContext;

import org.apache.log4j.PropertyConfigurator;

public class LogFactory {

	public static final int SERVLET = 1000;
	public static final int EMBEDDED = 2000;
	public static final int LOG4J = 3000;
	
	private static boolean configuredLog4J = false;
	private static int mode = LOG4J;
	private static ServletContext servletContext = null;
	
	public LogFactory() {
	}

	
	public static Logger createLogger(Class clazz) {
		if (mode == LOG4J) {
			if (!configuredLog4J) {
				PropertyConfigurator.configure("log4j.properties");
				configuredLog4J = true;
			}
			return new EmbeddedLogger(clazz);
		} else if (mode == SERVLET) {
			return new ServletLogger(servletContext);
		} else {
			return new DefaultLogger();
		}
	}

	public static void setServletContext(ServletContext context) {
		servletContext = context;
		mode = SERVLET;
	}

	public static boolean isConfiguredLog4J() {
		return configuredLog4J;
	}

	public static void setConfiguredLog4J(boolean configuredLog4J) {
		LogFactory.configuredLog4J = configuredLog4J;
	}
	
	
}
