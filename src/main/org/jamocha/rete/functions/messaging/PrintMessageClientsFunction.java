package org.jamocha.rete.functions.messaging;

import java.util.Iterator;

import org.jamocha.messaging.MessageClient;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.DefglobalMap;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class PrintMessageClientsFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PRINT_MSG_CLIENTS = "pprint-msg-clients";

	public PrintMessageClientsFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefglobalMap globals = engine.getDefglobalMap();
		Iterator iterator = globals.getValueIterator();
		while (iterator.hasNext()) {
			Object value = iterator.next();
			if (value instanceof MessageClient) {
				MessageClient client = (MessageClient)value;
				String msg = "InitialContextFactory: " + client.getInitialContextFactory() + Constants.LINEBREAK +
				"  ConnectionFactory: " + client.getConnectionFactory() + Constants.LINEBREAK +
				"  URL: " + client.getProviderURL() + Constants.LINEBREAK +
				"  Topic: " + client.getTopic() + Constants.LINEBREAK +
				"  User: " + client.getSecurityCredentials() + Constants.LINEBREAK +
				"  name: " + client.getName() + Constants.LINEBREAK;
				engine.writeMessage(msg, "t");
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return PRINT_MSG_CLIENTS;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(pprint-msg-clients)";
	}

}
