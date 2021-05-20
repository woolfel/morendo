package org.jamocha.rete.functions.bit;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class BitAndFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String BIT_AND = "bit-and";

	public BitAndFunction() {
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector returnVector = new DefaultReturnVector();
		int value = 0;
		if (params != null) {
			if (params[0] instanceof ValueParam) {
				value = ((ValueParam)params[0]).getIntValue();
			} else if (params[0] instanceof BoundParam) {
				Object v = ((BoundParam)params[0]).getValue(engine, Constants.OBJECT_TYPE);
				if (v instanceof Number) {
					value = ((Number)v).intValue();
				}
			}
			// iterate over the parameters
			for (int i=1; i < params.length; i++) {
				int intval = 0;
				if (params[i] instanceof ValueParam) {
					intval = ((ValueParam)params[i]).getIntValue();
				} else if (params[i] instanceof BoundParam) {
					Object v = ((BoundParam)params[0]).getValue(engine, Constants.OBJECT_TYPE);
					if (v instanceof Number) {
						intval = ((Number)v).intValue();
					}
				}
				value = value & intval;
			}
			DefaultReturnValue returnVal = new DefaultReturnValue(Constants.INTEGER_OBJECT, Integer.valueOf(value));
			returnVector.addReturnValue(returnVal);
		}
		return returnVector;
	}

	public String getName() {
		return BIT_AND;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam.class, ValueParam.class};
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(" + BIT_AND + " <int>*)";
	}

}
