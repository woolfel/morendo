package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.util.Map;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * Function will test if a string is in a Map
 * 
 * @author peter
 *
 */
public class MapContainsFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MAPCONTAINS = "map-contains";
	
	public MapContainsFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		Object rl = null;
		String key = null;
		Boolean contain = Boolean.FALSE;
		if (params != null && params.length == 2) {
			Map<?, ?> map = null;
			rl = params[0].getValue();
			key = params[1].getStringValue();
			if (rl instanceof Map) {
				map = (Map<?, ?>)rl;
			}
			if (map != null && key != null) {
				contain = map.containsKey(key);
			}
		}
		DefaultReturnValue val = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, contain);
		rv.addReturnValue(val);
		return rv;
	}

	public String getName() {
		return MAPCONTAINS;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class,ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(map-contains <map> <string>)";
	}

}
