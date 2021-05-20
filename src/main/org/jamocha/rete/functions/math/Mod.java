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
 * Mod returns 
 */
public class Mod implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String MOD = "mod";


	public Mod() {
		super();
	}

	public int getReturnType() {
		return Constants.BIG_DECIMAL;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		BigDecimal right = new BigDecimal(0);
		BigDecimal bdval = new BigDecimal(0);
		if (params.length == 2) {
			if (params[0] instanceof ValueParam) {
				bdval = params[0].getBigDecimalValue();
			} else {
				bdval = new BigDecimal( params[0].getValue(engine, Constants.BIG_DECIMAL).toString() );
			}
			if (params[1] instanceof ValueParam) {
				right = params[1].getBigDecimalValue();
			}else { 
				right = new BigDecimal( params[1].getValue(engine, Constants.BIG_DECIMAL).toString() );
			}
			bdval = bdval.remainder(right);
			bdval = bdval.abs();
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = 
			new DefaultReturnValue(Constants.BIG_DECIMAL,bdval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return MOD;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(mod");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getStringValue());
				} else {
					buf.append(" " + params[idx].getStringValue());
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(mod (<literal> | <binding>)+)\n" +
			"Function description:\n" +
			"\tReturns the remainder of the result of dividing\n" +
			"\tits first argument by its second argument.";
		}
	}
}
