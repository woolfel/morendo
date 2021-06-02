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

public class BitNotFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String BIT_NOT = "bit-not";

	public BitNotFunction() {
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector returnVector = new DefaultReturnVector();
		int value = 0;
		if (params != null && params.length == 2) {
			if (params[0] instanceof ValueParam) {
				value = ((ValueParam)params[0]).getIntValue();
			} else if (params[0] instanceof BoundParam) {
				Object v = ((BoundParam)params[0]).getValue(engine, Constants.OBJECT_TYPE);
				if (v instanceof Number) {
					value = ((Number)v).intValue();
				}
			}
			value = ~value;
		}
		DefaultReturnValue returnValue = new DefaultReturnValue(Constants.INTEGER_OBJECT, Integer.valueOf(value));
		returnVector.addReturnValue(returnValue);
		return returnVector;
	}

	public String getName() {
		return BIT_NOT;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class, ValueParam.class};
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return null;
	}

}
