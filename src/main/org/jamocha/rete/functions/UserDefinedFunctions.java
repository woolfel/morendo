package org.jamocha.rete.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class UserDefinedFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private ArrayList funcs = new ArrayList();
	public static final String USER_DEFINED_FUNCTIONS = "User Defined Functions";
	
	public UserDefinedFunctions() {
	}

	public String getName() {
		return USER_DEFINED_FUNCTIONS;
	}

	@SuppressWarnings("rawtypes")
	public List listFunctions() {
		return this.funcs;
	}

	/**
	 * method is not implemented, since user defined functions aren't
	 * loaded by the engine. They are loaded by the user.
	 */
	public void loadFunctions(Rete engine) {
	}

	@SuppressWarnings("unchecked")
	public void addFunction(Function f) {
		this.funcs.add(f);
	}
}
