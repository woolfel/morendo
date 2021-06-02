package org.jamocha.rete.functions.cube;

import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class ListCubesFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LIST_DEFCUBES = "list-defcubes";
	public static final String CUBES = "cubes";
	
	public ListCubesFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		List<?> cubes = engine.getCubes();
		Iterator<?> iterator = cubes.iterator();
		while (iterator.hasNext()) {
			String cubeName = (String)iterator.next();
			engine.writeMessage(cubeName + Constants.LINEBREAK,
			"t");
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return LIST_DEFCUBES;
	}

	public Class<?>[] getParameter() {
		return new Class[0];
	}

	public int getReturnType() {
		return 0;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(list-defcubes)";
	}

}
