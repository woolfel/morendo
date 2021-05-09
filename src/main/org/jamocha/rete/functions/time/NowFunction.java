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
package org.jamocha.rete.functions.time;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ShellBoundParam;


/**
 * @author Peter Lin
 * 
 * Now will create a new Date object and return it.
 */
public class NowFunction implements Function, Serializable {

    public static final String NOW = "now";

    /**
	 * 
	 */
	public NowFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.LONG_OBJECT;
	}

	/**
	 * The method expects an array of ShellBoundParam. The method will use
	 * StringBuffer to resolve the binding and print out 1 binding per
	 * line.
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Date now = new Date();
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = 
			new DefaultReturnValue(Constants.OBJECT_TYPE,now);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return NOW;
	}

	public Class[] getParameter() {
        return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(now)";
	}
}
