package org.jamocha.rete.functions.string;

import java.io.Serializable;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

public class StringContainsFunction implements Function, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String STRING_CONTAINS = "str-contains";
    
    
    public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        Boolean contain = Boolean.FALSE;
        if (params != null && params.length == 2) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[0];
				bp.resolveBinding(engine);
			}
			if (params[1] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[1];
				bp.resolveBinding(engine);
			}
            String val = params[0].getStringValue();
            String pt = params[1].getStringValue();
            contain = val.contains(pt);
        }
        DefaultReturnVector ret = new DefaultReturnVector();
        DefaultReturnValue rv = new DefaultReturnValue(
                Constants.BOOLEAN_OBJECT, contain);
        ret.addReturnValue(rv);
        return ret;
    }

    public String getName() {
        return STRING_CONTAINS;
    }

	public Class<?>[] getParameter() {
        return new Class[]{ValueParam.class,ValueParam.class};
    }

    public int getReturnType() {
        return Constants.BOOLEAN_OBJECT;
    }

    public String toPPString(Parameter[] params, int indents) {
        return "(str-contains [string] [pattern])";
    }

}
