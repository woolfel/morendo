package org.jamocha.rete.functions.macro;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * TODO
 * Need to finish this function one day
 * 
 * @author peter
 *
 */
public class CompileClassMacroFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String COMPILE_MACRO = "compile-class-macro";

	public CompileClassMacroFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		return null;
	}

	public String getName() {
		return COMPILE_MACRO;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(compile-class-macro <classname>)";
	}

}
