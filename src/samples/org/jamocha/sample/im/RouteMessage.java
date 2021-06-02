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
 * RouteMessage is a dummy function and isn't implemented. It's here so that
 * people can run the sample rules in Jamocha. To make it work for real,
 * the executeFunction method needs to be implemented.
 * 
 * @author Peter Lin
 */
public class RouteMessage implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ROUTE_MESSAGE = "route-msg";
	
	public RouteMessage() {
		super();
	}

	/**
	 * This method is not implemented. If it was a real function, the method
	 * would get a JMS client and route the message to the correct topic for
	 * delivery.
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		if (params != null && params.length == 1) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[0];
				Message msg = (Message)bp.getObjectRef();
				msg.setMessageStatus(Message.RECEIVED);
				System.out.println("message recieved!");
			}
		}
		return rv;
	}

	public String getName() {
		return ROUTE_MESSAGE;
	}

	public Class<?>[] getParameter() {
		return new Class[]{Object.class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(route-msg <Object>)";
	}

}
