/*
 * Copyright 2002-2009 Peter Lin
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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

/**
 * @author Peter Lin
 * 
 * Now will create a new Date object and return it.
 */
public class BetweenFunction extends AbstractTimeFunction implements Function, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String BETWEEN = "between";

    /**
	 * 
	 */
	public BetweenFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	/**
	 * The method expects an array of ShellBoundParam. The method will use
	 * StringBuffer to resolve the binding and print out 1 binding per
	 * line.
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean eval = Boolean.FALSE;
		if (params != null && params.length == 3) {
			Object one = params[0].getValue(engine, Constants.OBJECT_TYPE);
			Object two = params[1].getValue(engine, Constants.OBJECT_TYPE);
			Object three = params[2].getValue(engine, Constants.OBJECT_TYPE);
			long begin = getMillisecondTime(one);
			long end = getMillisecondTime(two);
			long time = getMillisecondTime(three);
			if (begin < time && time < end) {
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
		return BETWEEN;
	}

	public Class<?>[] getParameter() {
        return new Class[]{Object.class,Object.class,Object.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(between <begin> <end> <time>) \r\n the parameters can be Date, Calendar or long value.";
	}
}
