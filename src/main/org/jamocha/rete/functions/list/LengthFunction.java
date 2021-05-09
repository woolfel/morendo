package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.util.List;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * Function for creating a multislot with 1 or more literals
 * @author Peter Lin
 *
 */
public class LengthFunction implements Serializable, Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LENGTH = "length$";
	
	public LengthFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		int size = 0;
		Object val = null;
		if (params != null && params.length == 1) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[0];
				bp.resolveBinding(engine);
			}
			if (params[0] instanceof ValueParam) {
				val = params[0].getValue();
			} else {
				val = params[0].getValue(engine, Constants.ARRAY_TYPE);
			}
			if (val.getClass().isArray()) {
				Object[] ary = (Object[])val;
				size = ary.length;
			}
			if (val instanceof List<?>) {
				size = ((List<?>)val).size();
			}
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.INTEGER_OBJECT,
				new Integer(size));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return LENGTH;
	}

	public Class[] getParameter() {
		return new Class[]{ValueParam[].class};
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(length$ <list>)";
	}

}
