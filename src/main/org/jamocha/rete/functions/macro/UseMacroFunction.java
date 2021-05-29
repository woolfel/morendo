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

public class UseMacroFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String USE_MACRO = "use-macro";

	public UseMacroFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean use = Boolean.FALSE;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					String classname = ((ValueParam)params[idx]).getStringValue();
					Defclass defclass = engine.findDefclassByName(classname);
					defclass.loadMacros(engine.getCurrentFocus().getModuleClassLoader());
				}
			}
			use = Boolean.TRUE;
		}
		DefaultReturnVector rv = new DefaultReturnVector();
		DefaultReturnValue rval = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, use);
		rv.addReturnValue(rval);
		return rv;
	}

	public String getName() {
		return USE_MACRO;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(use-macro <classname>)";
	}

}
