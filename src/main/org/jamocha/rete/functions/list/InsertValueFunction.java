package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * 
 * @author Peter Lin
 *
 */
public class InsertValueFunction implements Serializable, Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String INSERT_VALUE = "insert$";
	
	public InsertValueFunction() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Object value = new Object[0];
		if (params != null && params.length >= 3) {
			List returnlist = new ArrayList();
			Object list = null;
			if (params[0] instanceof ValueParam) {
				list = params[0].getValue();
			} else {
				list = params[0].getValue(engine, Constants.ARRAY_TYPE);
			}
			if (list.getClass().isArray()) {
				Object[] r = (Object[])list;
				for (int idx=0; idx < r.length; idx++) {
					returnlist.add(r[idx]);
				}
			} else {
				returnlist.add(list);
			}
			int startIndex = 0;
			if (params[1] instanceof ValueParam) {
				startIndex = params[1].getBigDecimalValue().intValue();
			} else {
				BigDecimal bval = new BigDecimal(params[1].getValue(engine, Constants.INT_PRIM_TYPE).toString());
				startIndex = bval.intValue();
			}
			Object add = null;
			if (params[2] instanceof ValueParam) {
				add = params[2].getValue();
			} else if (params[2] instanceof BoundParam) {
				add = ((BoundParam)params[2]).getValue(engine, Constants.ARRAY_TYPE);
			} else if (params[2] instanceof FunctionParam2) {
				add = ((FunctionParam2)params[2]).getValue(engine, Constants.ARRAY_TYPE);
			}
			if (add.getClass().isArray()) {
				Object[] ar = (Object[])add;
				List inlist = new ArrayList();
				for (int idx=0; idx < ar.length; idx++) {
					inlist.add(ar[idx]);
				}
				returnlist.addAll(startIndex,inlist);
			} else {
				returnlist.add(startIndex,add);
			}
			value = returnlist.toArray();
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.ARRAY_TYPE,
				value);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return INSERT_VALUE;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam[].class};
	}

	public int getReturnType() {
		return Constants.ARRAY_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(insert$ <list> <begin-index> <sing-or-list>)";
	}

}
