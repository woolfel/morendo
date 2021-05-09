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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class AddMinutesFunction extends AbstractTimeFunction implements
		Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ADD_MINUTES = "add-minutes";
	protected GregorianCalendar calendar = new GregorianCalendar();

	public AddMinutesFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Date date = null;
		if (params != null && params.length == 2) {
			int minutes = params[0].getIntValue();
			date = this.getDate(params[1].getValue());
			if (date != null) {
				calendar.setTime(date);
				calendar.add(Calendar.MINUTE, minutes);
				date = calendar.getTime();
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = 
			new DefaultReturnValue(Constants.DATE_TYPE, date);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return ADD_MINUTES;
	}

	public Class[] getParameter() {
		return new Class[]{Date.class};
	}

	public int getReturnType() {
		return Constants.DATE_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(add-minutes <minutes> <date>)";
	}

}
