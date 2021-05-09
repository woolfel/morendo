package org.jamocha.rete.exception;

/**
 * FunctionException should be used to report errors registering functions
 * or runtime exceptions. The existing functions will need to be refactored
 * to use this.
 * 
 * @author Peter Lin
 */
public class FunctionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FunctionException() {
	}

	public FunctionException(String message) {
		super(message);
	}

}
