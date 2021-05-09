package org.jamocha.rete.functions.query;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rule.Query;

public class RunQueryFunction implements Function {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String RUN_QUERY = "run-query";

	public RunQueryFunction() {
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		List results = new ArrayList();
		if (params.length > 0) {
			String name = params[0].getStringValue();
			Parameter[] queryParams = new Parameter[params.length -1];
			System.arraycopy(params, 1, queryParams, 0, queryParams.length);
			Query query = engine.getDefquery(name);
			results = query.executeQuery(engine, engine.getWorkingMemory(), queryParams);
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE, results);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return RUN_QUERY;
	}

	public Class[] getParameter() {
		return new Class[]{String[].class};
	}

	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(run-query <query name> <parametes>)";
	}

}
