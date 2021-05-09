package org.jamocha.rete.functions;

import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.measures.Measure;

public class ListMeasuresFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MEASURES = "measures";
	
	public ListMeasuresFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		List measures = engine.getAllMeasures();
		int counter = 0;
		for (int idx=0; idx < measures.size(); idx++) {
			Measure m = (Measure)measures.get(idx);
			engine.writeMessage("  " + m.getMeasureName() + Constants.LINEBREAK,
			"t");
			counter++;
			
		}
		engine.writeMessage(counter + " measures" + Constants.LINEBREAK, "t");
		return new DefaultReturnVector();
	}

	public String getName() {
		return MEASURES;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(measures)";
	}

}
