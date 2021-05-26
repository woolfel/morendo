package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class BottomFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String BOTTOM = "bottom";
	
	public BottomFunction() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		Object rl = null;
		if (params != null && params.length == 2) {
			int count = params[0].getBigIntegerValue().intValue();
			rl = params[1].getValue(engine, Constants.OBJECT_TYPE);
			if (rl instanceof List) {
				List list = (List)rl;
				if (list.size() > count) {
					List newlist = new ArrayList();
					int s = list.size();
					int c = s - count;
					for (int idx=c; idx < s; idx++) {
						newlist.add(list.get(idx));
					}
					rl = newlist;
				}
			} else if (rl.getClass().isArray()) {
				Object[] list = (Object[])rl;
				if (list.length > count) {
					Object[] newlist = new Object[count];
					int s = list.length;
					int c = s - count;
					int ctr = 0;
					for (int idx=c; idx < s; idx++) {
						newlist[ctr] = list[idx];
						ctr++;
					}
					rl = newlist;
				}
			}
		}
		DefaultReturnValue val = new DefaultReturnValue(
				Constants.LIST_TYPE, rl);
		rv.addReturnValue(val);
		return rv;
	}

	public String getName() {
		return BOTTOM;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam.class,ValueParam.class};
	}

	public int getReturnType() {
		return Constants.LIST_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(bottom <number> <list>)";
	}

}
