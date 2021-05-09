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
 * Floor returns the greatest integer smaller or equal to a value.
 */
public class Floor implements Function, Serializable {

	public static final String FLOOR = "floor";

	/**
	 * 
	 */
	public Floor() {
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
					bdval = n.getBigDecimalValue();
				} else {
					bdval = new BigDecimal( params[idx].getValue(engine, Constants.BIG_DECIMAL).toString());
				}
				bd = bd.valueOf(bdval.intValue());
				bd = bdval.subtract(bd);
				if (bd.doubleValue() > 0) 
					bdval = bdval.valueOf(bdval.intValue());
				else if (bd.doubleValue() < 0) 
					bdval = bdval.valueOf(bdval.intValue() - 1);
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BIG_DECIMAL,
				bdval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return FLOOR;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length >= 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(floor");
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
			return "(floor <expression>)\n" +
			"Function description:\n" +
			"\tReturns the greatest integer smaller or equal to the numerical value \n" + 
			"\treturned by <expression>.";
		}
	}
}
