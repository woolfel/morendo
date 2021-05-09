package org.jamocha.rete.functions.list;

import java.io.Serializable;
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
 * Function splits the string by space and then eagerly checks if part of the
 * string matches any entries in the Set. This is a type of fuzzy matching
 * where user response doesn't have to match the keys exactly.
 * 
 * @author peter
 *
 */
public class EagerSetContainsFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MAPCONTAINS = "eager-set-contains";
	
	public EagerSetContainsFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		Object rl = null;
		String key = null;
		Boolean contain = new Boolean(false);
		if (params != null && params.length == 2) {
			Set map = null;
			rl = params[0].getValue();
			key = params[1].getStringValue().toLowerCase();
			if (rl instanceof Set) {
				map = (Set)rl;
			}
			if (map != null && key != null) {
				String[] tokens = key.split(" ");
				String teststr = "";
				for (int i=0; i < tokens.length; i++) {
					teststr += " " + tokens[i];
					contain = map.contains(teststr.trim());
					if (contain) {
						break;
					}
				}
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

	public Class[] getParameter() {
		return new Class[]{ValueParam.class,ValueParam.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(eager-set-contains <set> <string>)";
	}

}
