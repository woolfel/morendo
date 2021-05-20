package org.jamocha.rete.functions.cube;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Cube;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class UnProfileCubeIndexFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String UNPROFILE_CUBE_INDEX = "unprofile-cube-index";

	public UnProfileCubeIndexFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean profile = Boolean.FALSE;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				String cubename = params[idx].getStringValue();
				Cube c = engine.getCube(cubename);
				if (c != null) {
					c.setProfileIndex(false);
					profile = Boolean.TRUE;
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, profile);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return UNPROFILE_CUBE_INDEX;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(unprofile-cube-index <name>)";
	}

}
