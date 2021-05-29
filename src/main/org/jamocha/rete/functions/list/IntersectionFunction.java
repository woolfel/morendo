package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * 
 * 
 * @author Peter Lin
 *
 */
public class IntersectionFunction implements Serializable, Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String INTERSECTION = "intersection$";
	
	public IntersectionFunction() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Object[] result = null;
		if (params != null && params.length == 2) {
			Object array1 = params[0].getValue(engine, Constants.OBJECT_TYPE);
			Object array2 = params[1].getValue(engine, Constants.OBJECT_TYPE);
			if (array1 instanceof Object[] && array2 instanceof Object[]) {
				Set set1 = convertToList((Object[])array1);
				Set set2 = convertToList((Object[])array2);
				set1.retainAll(set2);
				result = set1.toArray();
			}
		}
		
		DefaultReturnValue rv = new DefaultReturnValue(Constants.ARRAY_TYPE,
				result);
		ret.addReturnValue(rv);
		return ret;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Set convertToList(Object[] array) {
		Set data = new HashSet();
		for (int idx=0; idx < array.length; idx++) {
			data.add(array[idx]);
		}
		return data;
	}
	public String getName() {
		return INTERSECTION;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam[].class};
	}

	public int getReturnType() {
		return Constants.ARRAY_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(intersection$ <list> <list>)";
	}

}
