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
package org.jamocha.rete.functions.time;

import java.io.Serializable;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

/**
 * After function is used to evaluate 2 time values. The time can be a date,
 * calendar or long millisecond time. It isn't meant to be used to be used
 * for Conditional Elements like (after (date (day "tuesday") ) ).
 * That type of logic is difficult to support and problematic. Until someone
 * figures out an efficient way to implement that type of temporal logic
 * safely, efficiently and reliably, it will remain impractical.
 * 
 * For further reading, look at Allen's paper, which tries to address
 * those issues with transitive closure. At this time, his approach only
 * works for a limited number of cases and is not performant.
 * 
 * @author Peter Lin
 */
public class AfterFunction extends AbstractTimeFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String AFTER = "after";
	
	public AfterFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean eval = Boolean.FALSE;
		
		if (params != null && params.length == 2) {
			long time1 = getMillisecondTime(params[0].getValue(engine, Constants.OBJECT_TYPE));
			long time2 = getMillisecondTime(params[1].getValue(engine, Constants.OBJECT_TYPE));
			if (time1 > time2) {
				eval = Boolean.TRUE;
			}
		}

		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = 
			new DefaultReturnValue(Constants.BOOLEAN_OBJECT, eval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return AFTER;
	}

	public Class<?>[] getParameter() {
		return new Class[]{Object.class, Object.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(after ");
			for (int idx=0; idx < params.length; idx++) {
				if (idx > 0) {
					buf.append(" ");
				}
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam)params[idx];
					buf.append(bp.toPPString());
				} else if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 fp = (FunctionParam2)params[idx];
					buf.append(fp.toPPString());
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(after <time1> <time2>) \r the first time is after the second time.";
		}
	}

}
