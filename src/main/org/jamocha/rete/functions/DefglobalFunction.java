/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;


/**
 * @author Peter Lin
 *
 */
public class DefglobalFunction implements Serializable, Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String DEFGLOBAL = "defglobal";
	
	/**
	 * 
	 */
	public DefglobalFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        DefaultReturnVector ret = new DefaultReturnVector();
        Object value = Constants.NIL_SYMBOL;
        if (params != null && params.length > 0) {
            String varname = ((BoundParam)params[0]).getVariableName();
            if (params.length == 2) {
                value = params[1].getValue();
            }
            engine.declareDefglobal(varname, value);
        } else {
            value = "false";
        }
        DefaultReturnValue rval = new DefaultReturnValue(Constants.OBJECT_TYPE,value);
        ret.addReturnValue(rval);
		return ret;
	}

	public String getName() {
		return DEFGLOBAL;
	}

	public Class[] getParameter() {
		return null;
	}

	public String toPPString(Parameter[] params, int indents) {
		// TODO Auto-generated method stub
		return null;
	}

}
