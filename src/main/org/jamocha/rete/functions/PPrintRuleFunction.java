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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * The function will print out the rule in a pretty format. Note the
 * format may not be identicle to what the user wrote. It is a normalized
 * and cleaned up format.
 */
public class PPrintRuleFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PPRULES = "ppdefrule";
	
	/**
	 * 
	 */
	public PPrintRuleFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				Rule rls = 
					engine.getCurrentFocus().findRule(params[idx].getStringValue());
				engine.writeMessage(rls.toPPString(),"t");
			}
		}
		DefaultReturnVector rv = new DefaultReturnVector();
		return rv;
	}

	public String getName() {
		return PPRULES;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(ppdefrule <name>)";
	}
}
