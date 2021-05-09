package org.jamocha.rete.functions.agent;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class UnregisterAgentFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String UNREGISTER_AGENT = "unregister-agent";

	public UnregisterAgentFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		if (params != null && params.length == 6) {
			AgentEntry entry = new AgentEntry();
			entry.setIPAddress(params[0].getStringValue());
			entry.setHostname(params[1].getStringValue());
			entry.setApplication(params[2].getStringValue());
			entry.setAgentApplicationName(params[3].getStringValue());
			entry.setAgentApplicationVersion(params[4].getStringValue());
			long time = params[5].getLongValue();
			entry.setTimestamp(time);
			AgentRegistry.removeAgent(entry);
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return UNREGISTER_AGENT;
	}

	public Class[] getParameter() {
		return new Class[]{String[].class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(unregister-agent <ipaddress> <hostname> <application> <agent app name> <agent app version>)";
	}

}
