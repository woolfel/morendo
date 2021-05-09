/*
  * Copyright 2002-2009 Jamocha
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

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.util.TopologyCostCalculation;

/**
 * @author Peter Lin
 * 
 */
public class TopologyCostFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TOPOLOGY_COST = "topology-cost";
    TopologyCostCalculation costFunction = new TopologyCostCalculation();

	/**
	 * 
	 */
	public TopologyCostFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		if (params != null && params.length > 0) {
			String ruleName = null;
            for (int idx=0; idx < params.length; idx++) {
                if (params[0] instanceof ValueParam) {
                    ValueParam n = (ValueParam) params[0];
                    ruleName = n.getStringValue();
                } else if (params[0] instanceof BoundParam) {
                    BoundParam bp = (BoundParam) params[0];
                    ruleName = (String)bp.getValue(engine, Constants.STRING_TYPE);
                }
                Defrule r = (Defrule)engine.getCurrentFocus().findRule(ruleName);
                costFunction.calculateCost(engine, r, engine.getRootNode());
            }
		}
		return ret;
	}

	public String getName() {
		return TOPOLOGY_COST;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(topology-cost <literal>+)\n" +
			"Function description:\n" +
			"\tCalculates the topology cost of one or more rules and sets the" +
			"\n\tRule.costValue.";
	}

}
