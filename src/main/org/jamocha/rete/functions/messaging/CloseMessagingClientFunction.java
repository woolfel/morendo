package org.jamocha.rete.functions.messaging;

import org.jamocha.messaging.MessageClient;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class CloseMessagingClientFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CLOSE_MSG_CLIENT = "close-message-client";

	public CloseMessagingClientFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		if (params != null && params.length > 0) {
			for (int i=0; i < params.length; i++) {
				Object value = engine.getDefglobalValue(params[i].getStringValue());
				if (value instanceof MessageClient) {
					((MessageClient)value).close();
					engine.removeDefglobal(params[i].getStringValue());
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return CLOSE_MSG_CLIENT;
	}

	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(" + CLOSE_MSG_CLIENT + " <client name>)";
	}

}
