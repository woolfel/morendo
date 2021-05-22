package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class MemberTestFunction implements Serializable, Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MEMBER_TEST = "member$";
	
	public MemberTestFunction() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		// int index = -1; Unsued
		Boolean member = Boolean.FALSE;
		if (params != null && params.length == 2) {
			Object item = params[0].getValue();
			Object l = params[1];
			if (l instanceof ValueParam) {
				Object list = ((ValueParam)l).getValue();
				if (list.getClass().isArray()) {
					Object[] ary = (Object[])list;
					for (int idx=0; idx < ary.length; idx++) {
						if (ary[idx].equals(item)) {
							// index = idx;
							member = Boolean.TRUE;
							break;
						}
					}
				}
			} else {
				Object list = params[1].getValue(engine, Constants.OBJECT_TYPE);
				if (list.getClass().isArray()) {
					Object[] ary = (Object[])list;
					for (int idx=0; idx < ary.length; idx++) {
						if (ary[idx].equals(item)) {
							// index = idx;
							member = Boolean.TRUE;
							break;
						}
					}
				} else if (list instanceof List) {
					List alist = (List)list;
					member = alist.contains(item);
				}
			}
		}
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, member);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return MEMBER_TEST;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam[].class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(member$ <single> <list>)";
	}

}
