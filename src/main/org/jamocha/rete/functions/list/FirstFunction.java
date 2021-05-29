/**
 * 
 */
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
 * @author Dave Woodman 9 May 2021
 * 
 * Implements CLIPS multislot function first$
 * 
 * Returns first value from a multifield  
 *
 */
public class FirstFunction implements Serializable, Function {

	
	private static final long serialVersionUID = 1L;
	public static final String FIRST = "first$";
	
	public FirstFunction() {
		super();
	}
	
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Object val = null;
		if (params != null && params.length == 1) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[0];
				bp.resolveBinding(engine);
			}
			Object list = null;

			if (params[0] instanceof ValueParam) {
				list = ((ValueParam)params[0]).getValue();
			} else {
				list = params[0].getValue(engine, Constants.OBJECT_TYPE);
				if (list == null )  // Oh, Let's try an array instead...
					// Could also do this... list = (Object)params[0].getValue();
					list = params[0].getValue(engine, Constants.ARRAY_TYPE);
			}
			if (list.getClass().isArray()) {
				Object[] lval = (Object[])list;
				if (lval.length > 0)
					val = lval[0];
				else
					val = ""; // Should never get here!
			} else val = list; // Last item
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE,
				val);
		ret.addReturnValue(rv);
		return ret;
	}		
	
	public String getName() {
		return FIRST;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam[].class};
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(first$ <list>)";
	}
}
