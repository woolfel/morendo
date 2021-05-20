package org.jamocha.rete.functions.macro;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class GenerateMacroFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String GENERATE_MACRO = "generate-macro";

	public GenerateMacroFunction() {
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean generate = Boolean.FALSE;
		if (params != null && params.length > 0) {
			MacroGenerator generator = new MacroGenerator();
			for (int idx=0; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					String classname = ((ValueParam)params[idx]).getStringValue();
					Defclass defclass = engine.findDefclassByName(classname);
					generator.generateMacros(defclass, engine.getCurrentFocus());
				}
			}
			generate = Boolean.TRUE;
		}
		DefaultReturnVector rv = new DefaultReturnVector();
		DefaultReturnValue rval = new DefaultReturnValue(Constants.BOOLEAN_OBJECT,generate);
		rv.addReturnValue(rval);
		return rv;
	}

	public String getName() {
		return GENERATE_MACRO;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(generate-macro <classname>)";
	}

}
