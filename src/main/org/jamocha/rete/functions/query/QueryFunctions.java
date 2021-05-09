package org.jamocha.rete.functions.query;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class QueryFunctions implements FunctionGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList funcs = new ArrayList();

	public QueryFunctions() {
		super();
	}
	
	public String getName() {
		return QueryFunctions.class.getSimpleName();
	}

	public List listFunctions() {
		return funcs;
	}

	public void loadFunctions(Rete engine) {
		DefqueryFunction dquery = new DefqueryFunction();
		engine.declareFunction(dquery);
		funcs.add(dquery);
		QueryTimeFunction queryTime = new QueryTimeFunction();
		engine.declareFunction(queryTime);
		funcs.add(queryTime);
		RunQueryFunction runquery = new RunQueryFunction();
		engine.declareFunction(runquery);
		funcs.add(runquery);
		RunGraphQueryFunction rungr = new RunGraphQueryFunction();
		engine.declareFunction(rungr);
		funcs.add(rungr);
		UnWatchQueryFunction unwatch = new UnWatchQueryFunction();
		engine.declareFunction(unwatch);
		funcs.add(unwatch);
		WatchQueryFunction watchq = new WatchQueryFunction();
		engine.declareFunction(watchq);
		funcs.add(watchq);
		DefGraphQueryFunction dgquery = new DefGraphQueryFunction();
		engine.declareFunction(dgquery);
		funcs.add(dgquery);
	}

}
