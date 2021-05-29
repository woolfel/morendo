package org.jamocha.rete.functions.analysis;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.util.PartialMatchCalculation;

public class PartialMatchCostFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String PARTIAL_MATCH_COST = "partial-match-cost";
	
	private PartialMatchCalculation calculation = new PartialMatchCalculation();
	
	public PartialMatchCostFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean success = Boolean.FALSE;
		if (params != null && params.length > 0) {
			String rulename = params[0].getStringValue();
			Defrule rule = (Defrule)engine.getCurrentFocus().findRule(rulename);
			calculation.calculatePartialMatchCost(engine, rule);
			success = Boolean.TRUE;
		}
		DefaultReturnVector returnVector = new DefaultReturnVector();
		DefaultReturnValue returnVal = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, success);
		returnVector.addReturnValue(returnVal);
		return returnVector;
	}

	public String getName() {
		return PARTIAL_MATCH_COST;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(partial-match-cost <rulename>)";
	}

}
