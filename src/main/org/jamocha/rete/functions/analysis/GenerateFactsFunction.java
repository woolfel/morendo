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

import java.util.ArrayList;
import java.util.Iterator;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.util.GenerateFacts;
import org.jamocha.rete.util.IOUtilities;

/**
 * @author Peter Lin
 *
 * Generate facts will call the utility class with the Rule object
 * and return an Object[] array of facts. Depending on the rule,
 * there should be one or more deffacts or object instances. The way
 * to use this is to bind the result or add it to a list.
 */
public class GenerateFactsFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String GENERATEFACTS = "generate-facts";

	
	public GenerateFactsFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	@SuppressWarnings("rawtypes")
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		boolean echo = false;
		ArrayList facts = null;
		String output = null;
		if (params != null && params.length >= 1) {
			Defrule r = (Defrule)engine.getCurrentFocus().findRule(
					params[0].getStringValue());
			if (params.length == 2) {
				if (params[1].getBooleanValue()) {
					echo = true;
				}
			}
			// if there's 3 parameters, it means we should save the fact
			// to a file
			if (params.length == 3) {
				output = params[2].getStringValue();
			}
			facts = GenerateFacts.generateFacts(r,engine);
			if (facts.size() > 0) {
				if (echo) {
					Iterator itr = facts.iterator();
					while (itr.hasNext()) {
						Object data = itr.next();
						if (data instanceof Deffact) {
							Deffact f = (Deffact)data;
							engine.writeMessage( f.toFactString() );
						} else {
							engine.writeMessage(data.toString());
						}
					}
				}
				if (output != null) {
					// we need to save facts to a file
					IOUtilities.saveFacts(facts, output);
				}
				DefaultReturnValue rv = new DefaultReturnValue(
						Constants.OBJECT_TYPE, facts.toArray());
				ret.addReturnValue(rv);
			} else {
				DefaultReturnValue rv = new DefaultReturnValue(
						Constants.BOOLEAN_OBJECT, Boolean.FALSE);
				ret.addReturnValue(rv);
			}
		}
		return ret;
	}

	public String getName() {
		return GENERATEFACTS;
	}

	/**
	 * The function does not take any parameters
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		if (indents > 0) {
			StringBuffer buf = new StringBuffer();
			for (int idx = 0; idx < indents; idx++) {
				buf.append(" ");
			}
			buf.append("(generate-facts)");
			return buf.toString();
		} else {
			return "(generate-facts [<rule> [true | false] [output])\n" +
			"Function description:\n" +
			"\tGenerates the trigger facts for a single rule\n";
		}
	}
}
