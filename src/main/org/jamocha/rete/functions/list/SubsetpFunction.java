package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.util.Objects;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class SubsetpFunction  implements Serializable, Function {

	/**
	 * Checks if one multifield is a subset of the other
	 */
	private static final long serialVersionUID = 1L;
	public static final String SUBSETP = "subsetp";

	public SubsetpFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Object checkList = null;
		Object list2Check = null;
		Boolean result = Boolean.FALSE;
		if (params != null && params.length <= 2) {
			// first is subject to check
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				bp.resolveBinding(engine);
			}
			
			if(params[0] instanceof ValueParam) {
				checkList = params[0].getValue();
			} else {
				checkList = params[0].getValue(engine, Constants.ARRAY_TYPE);
			}
			
			// second is the list to be checked
			if (params[1] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				bp.resolveBinding(engine);
			}
			if(params[1] instanceof ValueParam) {
				list2Check = params[1].getValue();
			} else {
				list2Check = params[1].getValue(engine, Constants.ARRAY_TYPE);
			}
			
			if(checkList.getClass().isArray() && list2Check.getClass().isArray()) {
				Object[] cl = (Object[])checkList;
				Object[] l2c = (Object[])list2Check;
				
				result = Boolean.TRUE;
				for (Object check : cl) {
					Boolean found = false;
					for(Object o2Check : l2c) {
						if(Objects.equals(check, o2Check)) {
							found = true;
							break;
						}
					}
					if(!found) { // Missed one, set result and bail
						result = Boolean.FALSE;
						break; 
					}
				}
			} 
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, result);

		ret.addReturnValue(rv);

		return ret;
	}

	public String getName() {
		return SUBSETP;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(aubsetp <multifield> <multifield>)\n checks that first mutlifield is a ssubset of the second";
	}

}