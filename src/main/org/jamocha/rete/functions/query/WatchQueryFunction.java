package org.jamocha.rete.functions.query;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class WatchQueryFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String WATCH_QUERY = "watch-query";
	
	public WatchQueryFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Boolean watch = Boolean.FALSE;
		if (params != null && params.length > 0) {
			for (int i=0; i < params.length; i++) {
				String name = params[i].getStringValue();
				engine.setWatchQuery(name);
			}
			watch = Boolean.TRUE;
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, watch);
		ret.addReturnValue(rv);
		return ret;	}

	public String getName() {
		return WATCH_QUERY;
	}

	public Class<?>[] getParameter() {
		return new Class[]{String.class, String.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(watch-query <query name>)";
	}

}
