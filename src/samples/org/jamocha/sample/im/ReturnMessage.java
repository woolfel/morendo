package org.jamocha.sample.im;

import java.io.Serializable;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

/**
 * ReturnMessage is a dummy function and isn't implemented. It's here so that
 * people can run the sample rules in Jamocha. To make it work for real,
 * the executeFunction method needs to be implemented.
 * 
 * @author Peter Lin
 *
 */
public class ReturnMessage implements Function, Serializable {

	public static final String RETURN_MESSAGE = "return-msg";
	
	public ReturnMessage() {
		super();
	}

	/**
	 * the method is not implemented. to get it to work for real, the method
	 * needs to get a JMS client and send the message to a return queue.
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		if (params != null && params.length == 1) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[0];
				Message msg = (Message)bp.getObjectRef();
				msg.setMessageStatus(Message.RETURNED);
				System.out.println("Message returned, user does not exist");
			}
		}
		return rv;
	}

	public String getName() {
		return RETURN_MESSAGE;
	}

	public Class[] getParameter() {
		return new Class[]{Object.class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(return-msg <Object>)";
	}

}
