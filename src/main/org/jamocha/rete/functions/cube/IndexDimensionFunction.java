package org.jamocha.rete.functions.cube;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Defcube;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class IndexDimensionFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String INDEX_DIMENSION = "index-dimension";
	
	public IndexDimensionFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean index = Boolean.FALSE;
		if (params != null && params.length >= 2) {
			String cubename = params[0].getStringValue();
			Defcube cube = (Defcube)engine.getCube(cubename);
			for (int i=1; i < params.length; i++) {
				String dimension = params[i].getStringValue();
				cube.getDimension(dimension).setAutoIndex(true);
			}
			index = Boolean.TRUE;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, index);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return INDEX_DIMENSION;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{String.class, String[].class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(index-dimension <cube> <dimension>+)";
	}

}
