package org.jamocha.rete.functions.query;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rule.GraphQuery;
import org.jamocha.rule.Query;

/**
 * Running a graphQuery is different than a regular query in a couple of ways.
 * The first is the graph data isn't added to the main engine working memory.
 * Second is the query can return either a list of nodes or edges.
 * 
 * @author peter
 *
 */
public class RunGraphQueryFunction implements Function {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String RUN_QUERY = "run-graph-query";

	public RunGraphQueryFunction() {
	}

	@SuppressWarnings("rawtypes")
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		List results = new ArrayList();
		if (params.length > 0) {
			String bindname = ((BoundParam)params[0]).getVariableName();
			String name = params[1].getStringValue();
			Parameter[] queryParams = new Parameter[params.length -2];
			System.arraycopy(params, 2, queryParams, 0, queryParams.length);
			Query query = engine.getGraphQuery(name);
			Object bval = engine.getBinding(bindname);
			Deffact[] facts = (Deffact[])bval;
			if (facts != null && query != null) {
				((GraphQuery)query).setGraphData(facts);
				results = query.executeQuery(engine, engine.getWorkingMemory(), queryParams);
			}
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE, results);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return RUN_QUERY;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{String[].class};
	}

	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(run-graph-query <graph data> <query name> <parametes>)";
	}

}
