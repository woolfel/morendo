package org.jamocha.rete.functions.cube;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Cube;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class UnProfileCubeFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String UNPROFILE_CUBE = "unprofile-cube";

	public UnProfileCubeFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean profile = false;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				String cubename = params[idx].getStringValue();
				Cube c = engine.getCube(cubename);
				if (c != null) {
					c.setProfileQuery(false);
					profile = true;
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT,
				new Boolean(profile));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return UNPROFILE_CUBE;
	}

	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(unprofile-cube <name>)";
	}

}
