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
 * Function for creating a multifield from a string. This extends the CLIPS
 * function of the same name by taking multiple strings as parameters as opposed
 * to a single string.
 * 
 * @author Dave Woodman
 *
 */
public class ExplodeFunction implements Serializable, Function {

	/**
	 * Creates a muitiield from a string
	 */
	private static final long serialVersionUID = 1L;
	public static final String EXPLODE = "explode$";

	public ExplodeFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		String sourceStr = new String();
		for (int idx = 0; idx < params.length; idx++) {
			if (params[idx] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[idx];
				bp.resolveBinding(engine);
			}
			sourceStr = sourceStr.concat(" ").concat(params[idx].getValue(engine, Constants.OBJECT_TYPE).toString());
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.ARRAY_TYPE, sourceStr.trim().split(" +"));

		ret.addReturnValue(rv);

		return ret;
	}

	public String getName() {
		return EXPLODE;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public int getReturnType() {
		return Constants.ARRAY_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(explode$ <string-expression>+)";
	}

}
