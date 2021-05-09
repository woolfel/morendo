package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class OrFunction implements Function, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String OR = "or";

	public OrFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		boolean eq = false;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				if (eq) {
					break;
				} else {
					// or expects nested functions
					if (params[idx] instanceof FunctionParam2) {
						FunctionParam2 n = (FunctionParam2) params[idx];
						n.setEngine(engine);
						n.lookUpFunction();
						ReturnVector rval = (ReturnVector) n.getValue();
						eq = rval.firstReturnValue().getBooleanValue();
					}
				}
			}
		}
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(eq));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return OR;
	}

	public Class[] getParameter() {
		return new Class[]{ ValueParam[].class };
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(or <expression>)";
	}

}
