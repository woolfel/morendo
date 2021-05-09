package org.jamocha.rete.exception;

/**
 * The exception is thrown if a class is already associated with an
 * existing defclass or deftemplate.
 * 
 * @author Peter Lin
 */
public class TemplateAssociationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TemplateAssociationException(String message) {
		super(message);
	}

}
