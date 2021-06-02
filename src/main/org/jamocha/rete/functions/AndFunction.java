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

public class AndFunction implements Function, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String AND = "and";

	public AndFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Boolean eq = Boolean.TRUE;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				if (!eq) {
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
				Constants.BOOLEAN_OBJECT, eq);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return AND;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ ValueParam[].class };
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null) {
			StringBuffer buf = new StringBuffer();
			for (int idx=0; idx < params.length; idx++) {
				if (idx > 0) {
					buf.append(" && ");
				}
				if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 fp = (FunctionParam2)params[idx];
					buf.append( fp.toPPString() );
				}
			}
			return buf.toString();
		} else {
			return "(and <expression>)";
		}
	}

}
