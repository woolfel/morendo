package org.jamocha.rete.query;

import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;

public abstract class QueryBaseNot extends QueryBaseJoin {
	
	public QueryBaseNot(int nodeId) {
		super(nodeId);
	}
	
	public abstract void executeJoin(Rete engine, WorkingMemory mem) throws AssertException;
}
