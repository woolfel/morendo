/*
 * Copyright 2002-2010 Peter Lin
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
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * The purpose of the function is to print out the names of the rules
 * and the comment.
 */
public class RulesFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String RULES = "rules";
	public static final String LISTRULES = "list-defrules";
	
	public RulesFunction() {
		super();
	}

	public String getName() {
		return LISTRULES;
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Collection<?> rules = engine.getCurrentFocus().getAllRules();
		int count = rules.size();
		Iterator<?> itr = rules.iterator();
		while (itr.hasNext()) {
			Rule r = (Rule)itr.next();
			engine.writeMessage(r.getName() + " \"" + r.getComment() +
					"\" salience:" + r.getSalience() +
					" version:" + r.getVersion() +
					" no-agenda:" + r.getNoAgenda() +
                    " temporal-activation:" + r.isTemporalActivation() +
                    " cost-value:" + r.getCostValue() + "\r\n", "t");
		}
		engine.writeMessage("for a total of " + count +"\r\n","t");
		DefaultReturnVector rv = new DefaultReturnVector();
		return rv;
	}

	public Class<?>[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(list-defrules)";
	}
}
