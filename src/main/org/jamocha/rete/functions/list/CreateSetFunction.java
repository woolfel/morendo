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
 * Function for creating a Java Set from strings
 * @author Peter Lin
 *
 */
public class CreateSetFunction implements Serializable, Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CREATE_SET = "create-set$";
	
	public CreateSetFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Set<String> stringset = new HashSet<String>();
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					stringset.add(params[idx].getStringValue());
				}
			}
			if (params[0].getValue() instanceof String) {
				DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE,
						stringset);
				ret.addReturnValue(rv);
			}
		}
		return ret;
	}

	public String getName() {
		return CREATE_SET;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam[].class};
	}

	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(create-set$ <value>+)";
	}

}
