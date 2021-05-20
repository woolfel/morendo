package org.jamocha.rete.functions.query;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class QueryTimeFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String QUERY_TIME = "query-time";
	
	public QueryTimeFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		long time = 0;
		if (params != null && params.length > 0) {
			String name = params[0].getStringValue();
			time = engine.getQueryTime(name);
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.LONG_OBJECT, Long.valueOf(time));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return QUERY_TIME;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.LONG_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(query-time <query name>)";
	}

}
