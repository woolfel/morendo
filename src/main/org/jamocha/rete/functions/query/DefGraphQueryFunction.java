package org.jamocha.rete.functions.query;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rule.GraphQuery;

public class DefGraphQueryFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFQUERY = "defgraphquery";
	
	public DefGraphQueryFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean add = true;
		if (params.length == 1 && params[0].getValue() instanceof GraphQuery) {
			GraphQuery query = (GraphQuery) params[0].getValue();
			add = engine.getGraphQueryCompiler().addQuery(query);
		} else {
			add = false;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(add));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return DEFQUERY;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(defgraphquery <query-name> (declare (variables <binding>)+) (CE)+ )" +
					"" +
					"(defgraphquery <query-name> \"optional_comment\" "+  
					"	(variables ) 		; inputs for the query (LHS)" + 
					"	(pattern_1) 		; Left-Hand Side (LHS)" + 
					"	(pattern_2) 		; of the rule consisting of elements" +
					"	...					; before the \"=>\"" + 
					"	...					" + 
					"	...					" + 
					"	(pattern_N)" +
					"" +
					"Be sure all your parentheses balance or you will get error messages!";
		}
	}

}
