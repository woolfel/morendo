package org.jamocha.logging;

import javax.servlet.ServletContext;

public class ServletLogger implements Logger {

	protected ServletContext servletContext = null;
	
	public ServletLogger(ServletContext context) {
		this.servletContext = context;
	}

	public void debug(String msg) {
		this.servletContext.log("Debug: " + msg);
	}

	public void debug(Exception exc) {
		this.servletContext.log("Debug: ", exc);
	}

	public void fatal(String msg) {
		this.servletContext.log("Fatal: " + msg);
	}

	public void fatal(Exception exc) {
		this.servletContext.log("Fatal: ", exc);
	}

	public void info(String msg) {
		this.servletContext.log("Info: " + msg);
	}

	public void info(Exception exc) {
		this.servletContext.log("Info: ", exc);
	}

	public void warn(String msg) {
		this.servletContext.log("Warn: " + msg);
	}

	public void warn(Exception exc) {
		this.servletContext.log("Warn: ", exc);
	}

}
