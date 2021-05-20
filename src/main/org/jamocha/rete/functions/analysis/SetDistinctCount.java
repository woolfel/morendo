package org.jamocha.rete.functions.analysis;

import java.math.BigDecimal;

import org.jamocha.rete.BaseSlot;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.Template;
import org.jamocha.rete.ValueParam;

public class SetDistinctCount implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SET_DISTINCT_COUNT = "set-distinct-count";
	
	public SetDistinctCount() {
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean success = Boolean.FALSE;
		if (params != null && params.length == 3) {
			String template = params[0].getStringValue();
			String slotname = params[1].getStringValue();
			BigDecimal count = params[2].getBigDecimalValue();
			Template deftemplate = engine.findTemplate(template);
			BaseSlot sl = deftemplate.getSlot(slotname);
			sl.setDistinctCount(count.longValue());
			success = Boolean.TRUE;
		}
		DefaultReturnVector returnVector = new DefaultReturnVector();
		DefaultReturnValue returnVal = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, success);
		returnVector.addReturnValue(returnVal);
		return returnVector;
	}

	public String getName() {
		return SET_DISTINCT_COUNT;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam.class, ValueParam.class, ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(set-distinct-coount <template> <slot> <distinct-count>)";
	}

}
