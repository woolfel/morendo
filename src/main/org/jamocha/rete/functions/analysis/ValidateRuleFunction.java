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
package org.jamocha.rete.functions.analysis;

import java.io.Serializable;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 * 
 * WatchFunction allows users to watch different engine process, like
 * activations, facts and rules.
 */
public class ValidateRuleFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final String VALIDATE_RULE = "validate-rule";
	

	public ValidateRuleFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        DefaultReturnVector ret = new DefaultReturnVector();
        boolean val = false;
        if (params != null && params.length == 1) {
        	if (params[0].getBooleanValue()) {
        		engine.setValidateRules(true);
        		val = true;
        	} else if (!params[0].getBooleanValue()) {
        		engine.setValidateRules(false);
        		val = true;
        	}
        }
		DefaultReturnValue rv = 
			new DefaultReturnValue(Constants.BOOLEAN_OBJECT,val);
		ret.addReturnValue(rv);
        return ret;
	}
    
	public String getName() {
		return VALIDATE_RULE;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(validate-rule [true|false])";
	}

}
