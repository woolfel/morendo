package woolfel.examples.function;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class HelloFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String HELLO = "hello";

	public HelloFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector returnVec = new DefaultReturnVector();
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				DefaultReturnValue value = new DefaultReturnValue(Constants.STRING_TYPE, params[idx].getStringValue());
				returnVec.addReturnValue(value);
			}
		}
		return returnVec;
	}

	public String getName() {
		return HELLO;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return null;
	}

}
