/*
 * Copyright 2006-2008 Jamocha
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
package org.jamocha.rete.functions.math;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Nikolaus Koemm
 * @author Peter Lin
 * 
 * If its ony argument is even, Evenp returns true.
 */
public class Evenp implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String EVENP = "evenp";

	
	public Evenp() {
		super();
	}

	public int getReturnType() {
		return Constants.BIG_DECIMAL;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		BigDecimal bdval = new BigDecimal(0);
		Boolean eval = Boolean.FALSE;
		if (params.length == 1) {
			if (params[0] instanceof ValueParam) {
	            bdval = (BigDecimal)params[0].getValue(engine, Constants.BIG_DECIMAL);
			} else {
				bdval = new BigDecimal( params[0].getValue(engine, Constants.BIG_DECIMAL).toString());
			}
			double bdh = bdval.doubleValue();
			if (bdh % 2 == 0){
				eval = Boolean.TRUE;
			}

		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, eval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return EVENP;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(evenp");
				int idx = 0;
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getStringValue());
				} else {
					buf.append(" " + params[idx].getStringValue());
				}
			buf.append(")");
			return buf.toString();
		} else {
			return "(evenp <expression>)\n" +
			"Function description:\n" +
			"\tReturns true, if its only argument is even.";
		}
	}
}
