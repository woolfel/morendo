package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class IsNotNilFunction implements Function, Serializable  {
	
	private static final long serialVersionUID = 1L;
	public static final String ISNOTNIL = "is-not-nil";
	
	public IsNotNilFunction() {
		super();
	}
	
	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}
	

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean eq = Boolean.TRUE;
		Boolean err = Boolean.FALSE;
		Object val = null;
		int index = 0;
		BoundParam bp = null; 
		DefaultReturnValue rv;
		
		if (params != null && params.length > 0) {
			for(index = 0; index < params.length; index++) {
				bp = (BoundParam) params[index];
				val = bp.getValue();
				if (val == null) {
					eq = Boolean.FALSE;
					val = engine.getBinding(bp.getVariableName()); 
				}
				if (val == null) {
						err = Boolean.TRUE;
						break;
				}
				if (val.equals(Constants.NIL_SYMBOL)) {
					eq = Boolean.FALSE;
					break;
				}
			}
		} else err = Boolean.TRUE;
		
		DefaultReturnVector ret = new DefaultReturnVector();		 
		rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, eq);
		ret.addReturnValue(rv);
		if(err) {
			rv = new DefaultReturnValue(Constants.STRING_TYPE,
					"Parameter error: " + bp.getVariableName());
			ret.addReturnValue(rv);
		}
		return ret;
	}


	public String getName() {
		return ISNOTNIL;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(is-not-nil <binding>)+\n" +
				"Function description:\n" +
				"\tCompares the bindings against 'nil'\n" +
				"\treturns true if all bindings are not nil, false if one or more are nil" +
				" or in the event of a variable not being bound.";
	}

}
