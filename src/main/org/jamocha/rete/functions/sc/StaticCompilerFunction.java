package org.jamocha.rete.functions.sc;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class StaticCompilerFunction implements Function {

	/**
	 * StaticCompiler will take the current RETE network and produce
	 * a statically compiled version, which uses the objects directly.
	 * An important note about statically compiled RETE networks that
	 * use the object directly, it is not thread safe! This means
	 * modifications to java object instances must first retract the
	 * object before making any changes. If this rule is violated,
	 * retract will fail. If some other thread modifies the object,
	 * it is the user's responsibility handle it appropriately.
	 * Failure to follow these rules will result in memory leaks and
	 * incorrect results. In other words, the application won't get
	 * the results you expect.
	 * 
	 * Statically compiled RETE is an advanced feature for advanced
	 * users that have a solid understanding of expert systems. Users
	 * that are not familiar with expert systems or do not have a
	 * deep understanding, should not use statically compiled RETE.
	 * 
	 * For applications that need to notify the rule engine of
	 * changes, the set methods must do the following.
	 * 
	 * 1. compare the new value to the old value.
	 * 2. if the new value is different, retract the object
	 * 3. replace the existing value with the new value
	 * 4. assert the object
	 * 
	 * For statically compiled RETE, the activation will call the
	 * rule engine methods directly instead of using the built-in
	 * functions used by the interpreted engine.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String STATIC_COMPILER = "static-compiler";

	public StaticCompilerFunction() {
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		return new DefaultReturnVector();
	}

	public String getName() {
		return STATIC_COMPILER;
	}

	public Class<?>[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(" + STATIC_COMPILER + ")";
	}

}
