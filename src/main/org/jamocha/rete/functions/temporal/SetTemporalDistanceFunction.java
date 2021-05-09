package org.jamocha.rete.functions.temporal;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.Template;
import org.jamocha.rete.ValueParam;

public class SetTemporalDistanceFunction implements Serializable, Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SET_TEMPORAL_DISTANCE = "set-temporal-distance";
	
	public SetTemporalDistanceFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		if (params != null && params.length == 2) {
			String template = params[0].getStringValue();
			BigDecimal sec = params[1].getBigDecimalValue();
			Template templ = engine.findTemplate(template);
			if (templ != null) {
				templ.setTemporalDistance(sec.intValue() * 1000);
				DefaultReturnValue ret = new DefaultReturnValue(Constants.BIG_DECIMAL,sec);
				rv.addReturnValue(ret);
			}
		}
		return rv;
	}

	public String getName() {
		return SET_TEMPORAL_DISTANCE;
	}

	public Class[] getParameter() {
		return new Class[]{ValueParam.class, ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BIG_DECIMAL;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(set-temporal-distance <deftemplate> <seconds>)";
	}

}
