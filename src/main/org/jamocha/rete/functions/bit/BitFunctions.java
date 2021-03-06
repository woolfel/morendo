package org.jamocha.rete.functions.bit;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class BitFunctions implements FunctionGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Function> funcs = new ArrayList<Function>();

	public BitFunctions() {
	}

	public String getName() {
		return BitFunctions.class.getSimpleName();
	}

	public List<Function> listFunctions() {
		return funcs;
	}

	public void loadFunctions(Rete engine) {
		BitAndFunction bitand = new BitAndFunction();
		engine.declareFunction(bitand);
		funcs.add(bitand);
		BitOrFunction bitor = new BitOrFunction();
		engine.declareFunction(bitor);
		funcs.add(bitor);
		BitNotFunction bitnot = new BitNotFunction();
		engine.declareFunction(bitnot);
		funcs.add(bitnot);
	}

}
