package org.jamocha.rete.functions.list;

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

/**
 * Function for creating a string from a multifield. This extends the CLIPS
 * function of the same name by taking multiple multifields as parameters as
 * opposed to a single string.
 * 
 * @author Dave Woodman
 *
 */

public class ImplodeFunction implements Serializable, Function {

	/**
	 * Creates a String from a multifield
	 */
	private static final long serialVersionUID = 1L;
	public static final String IMPLODE = "implode$";

	public ImplodeFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		String retStr = new String();
		Object list = null;
		for (int idx = 0; idx < params.length; idx++) {
			if (params[idx] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[idx];
				bp.resolveBinding(engine);
			}
			if (params[idx] instanceof ValueParam) {
				list = params[idx].getValue();
			} else {
				list = params[idx].getValue(engine, Constants.ARRAY_TYPE);
			}
			if (list.getClass().isArray()) {
				Object[] r = (Object[]) list;
				for (int indx = 0; indx < r.length; indx++) {
					retStr = retStr.concat(r[indx].toString().trim().concat(" "));
				}
			} else {
				retStr.concat(list.toString().trim().concat(" "));
			}
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.STRING_TYPE, retStr.trim());

		ret.addReturnValue(rv);

		return ret;
	}

	public String getName() {
		return IMPLODE;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(implode$ <multifield>+)";
	}

}