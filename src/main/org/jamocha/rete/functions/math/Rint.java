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
 * Rint returns the double value closest to the argument.
 */
public class Rint implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String RINT = "rint";

	
	public Rint() {
		super();
	}

	public int getReturnType() {
		return Constants.BIG_DECIMAL;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		BigDecimal bdval = new BigDecimal(0);
		if (params.length == 1) {
			if (params[0] instanceof ValueParam) {
				ValueParam n = (ValueParam) params[0];
				bdval = n.getBigDecimalValue();
			} else {
				bdval = new BigDecimal( params[0].getValue(engine, Constants.BIG_DECIMAL).toString() );
			}
			double bdh = bdval.doubleValue();
			bdval = BigDecimal.valueOf(bdh);	
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BIG_DECIMAL,
				bdval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return RINT;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(rint");
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
			return "(rint <expression>)\n" +
			"Function description:\n" +
			"\tReturns the double value closest to the argument";
		}
	}
}
