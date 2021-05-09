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

import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rule.Defrule;

public class AverageCostFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String AVERAGE_COST = "average-cost";

	public AverageCostFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		int total = 0;
		Collection rules = engine.getCurrentFocus().getAllRules();
		Iterator ruleItr = rules.iterator();
		while (ruleItr.hasNext()) {
			Defrule rule = (Defrule)ruleItr.next();
			total += rule.getCostValue();
		}
		int average = total/rules.size();
		DefaultReturnValue v = new DefaultReturnValue(Constants.INTEGER_OBJECT, new Integer(average));
		ret.addReturnValue(v);
		return ret;
	}

	public String getName() {
		return AVERAGE_COST;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(average-cost)";
	}

}
