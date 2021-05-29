package org.jamocha.logging;

import org.apache.log4j.Logger;

/**
 * This version of the logger is designed for situations where the engine
 * is embedded. This allows the application to configure log4j, instead
 * of using the default logger.
 * 
 * @author Peter Lin
 *
 */
public class EmbeddedLogger extends DefaultLogger {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmbeddedLogger(Class<?> theclazz) {
		super();
		this.log = Logger.getLogger(theclazz);
	}

}
