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

import java.lang.Math;

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
 * @author Nikolaus Koemm, Christian Ebert
 * @author Peter Lin
 * 
 * Returns the value of the first argument raised to the power of the following arguments.
 */
@SuppressWarnings("serial")
public class Pow implements Function, Serializable {

	public static final String POW = "pow";

	/**
	 * 
	 */
	public Pow() {
		super();
	}

	public int getReturnType() {
		return Constants.BIG_DECIMAL;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		BigDecimal bdval = new BigDecimal(0);
		BigDecimal bd = new BigDecimal(0);
		if (params != null) {
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					ValueParam n = (ValueParam) params[idx];
					bd = n.getBigDecimalValue();
				} else {
					bd = new BigDecimal(params[idx].getValue(engine, Constants.BIG_DECIMAL).toString());
				}
				if (idx == 0) bdval = bdval.add(bd);
				else {
					double bdh = Math.pow(bdval.doubleValue(), bd.doubleValue());
					bdval = BigDecimal.valueOf(bdh);
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BIG_DECIMAL,
				bdval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return POW;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(pow");
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
			return "(pow (<literal> | <binding>)+)\n" +
			"Function description:\n" +
			"\tRaises its first argument to the power of the\n" +
			"\tfollowing arguments.";
		}
	}
}
