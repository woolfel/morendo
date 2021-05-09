package org.jamocha.logging;

/**
 * Generic logger interface. Just in case we need to switch the logger
 * to use standard jvm logging, instead of log4j. Though why anyone
 * uses jvm logging over log4j is a mystery to me. log4j is better.
 * @author Peter Lin
 *
 */
public interface Logger {
	public void debug(String msg);
	
	public void debug(Exception exc);
	
	public void fatal(String msg);
	
	public void fatal(Exception exc);
	
	public void info(String msg);
	
	public void info(Exception exc);
	
	public void warn(String msg);
	
	public void warn(Exception msg);
}
