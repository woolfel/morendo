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

public class TopFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TOP = "top";
	
	public TopFunction() {
		super();
	}

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
					for (int idx=0; idx < count; idx++) {
						newlist.add(list.get(idx));
					}
					rl = newlist;
				}
			} else if (rl.getClass().isArray()) {
				Object[] list = (Object[])rl;
				if (list.length > count) {
					Object[] newlist = new Object[count];
					for (int idx=0; idx < count; idx++) {
						newlist[idx] = list[idx];
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
		return TOP;
	}

	public Class[] getParameter() {
		return new Class[]{ValueParam.class,ValueParam.class};
	}

	public int getReturnType() {
		return Constants.LIST_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(top <number> <list>)";
	}

}
