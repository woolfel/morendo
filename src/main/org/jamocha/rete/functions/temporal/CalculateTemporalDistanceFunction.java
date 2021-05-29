package org.jamocha.rete.functions.temporal;

import java.io.Serializable;
import java.util.Collection;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class CalculateTemporalDistanceFunction implements Serializable,
		Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CALCULATE_DISTANCE = "calculate-temporal-distance";
	
	public CalculateTemporalDistanceFunction() {
		super();
	}

	/**
	 * function isn't implemented yet. need to implement temporal distance
	 * calculation utility first
	 */
		public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean successful = Boolean.FALSE;
		TemporalCalculation calculation = new TemporalCalculation();
		Collection<?> rules = engine.getCurrentFocus().getAllRules();
		successful = calculation.calcuateDistance(engine, rules);
		DefaultReturnVector rv = new DefaultReturnVector();
		DefaultReturnValue value = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, successful);
		rv.addReturnValue(value);
		return rv;
	}

	public String getName() {
		return CALCULATE_DISTANCE;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class, ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(calculate-temporal-distance <file> <outputfile>)";
	}

}
