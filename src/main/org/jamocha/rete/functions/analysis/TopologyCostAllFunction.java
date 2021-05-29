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
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Module;
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
public class TopologyCostAllFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TOPOLOGY_COST_ALL = "topology-cost-all";
    private TopologyCostCalculation costFunction = new TopologyCostCalculation();

	/**
	 * 
	 */
	public TopologyCostAllFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	@SuppressWarnings("rawtypes")
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
        Collection modules = engine.getWorkingMemory().getModules();
        Iterator itr = modules.iterator();
        while (itr.hasNext()) {
            Module m = (Module)itr.next();
            Collection rules = m.getAllRules();
            Iterator itrRules = rules.iterator();
            while (itrRules.hasNext()) {
                Defrule r = (Defrule)itrRules.next();
                costFunction.calculateCost(engine, r, engine.getRootNode());
            }
        }
		return ret;
	}

	public String getName() {
		return TOPOLOGY_COST_ALL;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(topology-cost-all )\n" +
			"Function description:\n" +
			"\tCalculates the topology cost of all rules and sets Rule.costValue.";
	}

}
