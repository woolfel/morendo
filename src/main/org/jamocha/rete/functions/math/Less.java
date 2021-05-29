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
 * @author Peter Lin
 *
 * Less will compare 2 or more numeric values and return true if the
 * (n-1)th value is less than the nth.
 */
public class Less implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LESS = "less";


	public Less() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean eval = Boolean.TRUE;
		BigDecimal left = null;
		BigDecimal right = null;
		if (params != null) {
			if (params[0] instanceof ValueParam) {
				left = params[0].getBigDecimalValue();
			} else {
				left = new BigDecimal( params[0].getValue(engine, Constants.BIG_DECIMAL).toString());
			}
			for (int idx=1; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					right = params[idx].getBigDecimalValue();
				} else {
					right = new BigDecimal( params[idx].getValue(engine, Constants.BIG_DECIMAL).toString() );
				}
	            eval = (left.doubleValue() < right.doubleValue());
	            if (!eval) {
	            	break;
	            } else {
	            	left = right;
	            }
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, eval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return LESS;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(<");
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
			return "(< (<literal> | <binding>)+)\n" +
					"Function description:\n" +
					"\t Returns the symbol TRUE if for all its arguments, " +
					"argument \n \t n-1 is less than argument n";
		}
	}
}
