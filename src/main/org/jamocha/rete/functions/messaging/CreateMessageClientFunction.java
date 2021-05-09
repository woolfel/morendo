package org.jamocha.rete.functions.messaging;

import org.jamocha.messaging.BasicClient;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class CreateMessageClientFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CREATE_MESSAGE_CLIENT = "create-message-client";

	public CreateMessageClientFunction() {
		super();
	}

	/**
	 * <ul>
	 * <li> JNDI InitialContextFactory
	 * <li> Provider URL
	 * <li> connection factory: TopicConnectionFactory
	 * <li> Topic
	 * <li> Username
	 * <li> Password
	 * <li> client name
	 * </ul>
	 *  
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean created = false;
		if (params != null && params.length == 7) {
			BasicClient client = new BasicClient();
			client.setRete(engine);
			client.setInitialContextFactory(params[0].getStringValue());
			client.setProviderURL(params[1].getStringValue());
			client.setConnectionFactory(params[2].getStringValue());
			client.setTopic(params[3].getStringValue());
			client.setSecurityPrinciple(params[4].getStringValue());
			client.setSecurityCredentials(params[5].getStringValue());
			client.init();
			
			String clientName = params[6].getStringValue();
			engine.declareDefglobal(clientName, client);
			created = true;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(created));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return CREATE_MESSAGE_CLIENT;
	}

	public Class[] getParameter() {
		return new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(create-message-client <jndi> <provider url> <connection factory> <topic> <username> <password> <*client instance name*>)";
	}

}
