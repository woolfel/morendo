/**
 * 
 */
package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.util.ArrayList;
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
 * @author Dave Woodman - 9th May 2021
 * 
 * Implements CLIPS rest$ function
 * 
 * Returns all but fist value from a multifield value
 *
 */
public class RestFunction implements Serializable, Function {
	
	private static final long serialVersionUID = 1L;
	public static final String REST = "rest$";

	public RestFunction() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Object val = new Object[0];
		List rlist = new ArrayList();
		if (params != null && params.length == 1) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				bp.resolveBinding(engine);
			}
			Object list = null;
			if (params[0] instanceof ValueParam) {
				list = ((ValueParam) params[1]).getValue();
			} else {
				list = params[0].getValue(engine, Constants.OBJECT_TYPE);
				if (list == null) // Oh, Let's try an array instead...
					// Could also do this... list = (Object)params[0].getValue();
					list = params[0].getValue(engine, Constants.ARRAY_TYPE);
			}
			if (list.getClass().isArray()) {
				Object[] lval = (Object[]) list;
				
				if (lval.length > 1) {
					for (int indx = 1; indx < lval.length; indx++) {
						rlist.add(lval[indx]);
					}
				} 
			} 
		} 		
		val = rlist.toArray();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.ARRAY_TYPE, val);
		
		ret.addReturnValue(rv);
		
		return ret;
	}

	public int getReturnType() {
		return Constants.ARRAY_TYPE;
	}

	public String getName() {
		return REST;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam[].class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(rest$ <list>)";
	}

}
