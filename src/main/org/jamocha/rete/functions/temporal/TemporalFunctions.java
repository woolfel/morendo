package org.jamocha.rete.functions.temporal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class TemporalFunctions implements Serializable, FunctionGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private ArrayList funcs = new ArrayList();

	public TemporalFunctions() {
		super();
	}

	public String getName() {
		return TemporalFunctions.class.getSimpleName();
	}

	@SuppressWarnings("rawtypes")
	public List listFunctions() {
		return funcs;
	}

	@SuppressWarnings("unchecked")
	public void loadFunctions(Rete engine) {
		CalculateTemporalDistanceFunction ctd = new CalculateTemporalDistanceFunction();
		funcs.add(ctd);
		engine.declareFunction(ctd);
		SetTemporalDistanceFunction setdist = new SetTemporalDistanceFunction();
		funcs.add(setdist);
		engine.declareFunction(setdist);
	}

}
