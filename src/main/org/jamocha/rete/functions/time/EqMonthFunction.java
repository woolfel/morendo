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
 * Function will compare two dates down to the minute. That means it will lop off the seconds
 * and milliseconds. This makes it handy for time comparisons that don't need full millisecond
 * precision. An example would be to group facts by minute.
 * 
 * @author Peter Lin
 */
public class EqMonthFunction extends AbstractTimeFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String EQ_MONTH = "eq-month";
	protected GregorianCalendar calendar1 = new GregorianCalendar();
	protected GregorianCalendar calendar2 = new GregorianCalendar();
	
	public EqMonthFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean eval = false;
		if (params != null && params.length == 2) {
			Date date1 = null;
			if (params[0] instanceof ValueParam) {
				date1 = this.getDate(params[0].getValue());
			} else if (params[0] instanceof BoundParam) {
				date1 = this.getDate(engine.getBinding( ((BoundParam)params[0]).getVariableName()));
			}
			Date date2 = null;
			if (params[1] instanceof ValueParam) {
				date2 = this.getDate(params[1].getValue());
			} else if (params[1] instanceof BoundParam) {
				date2 = this.getDate(engine.getBinding( ((BoundParam)params[1]).getVariableName()));
			}
			if (date1 != null && date2 != null) {
				calendar1.setTime(date1);
				calendar2.setTime(date2);
				if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
						calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)) {
					eval = true;
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = 
			new DefaultReturnValue(Constants.BOOLEAN_OBJECT, new Boolean(eval));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return EQ_MONTH;
	}

	public Class[] getParameter() {
		return new Class[]{Date.class, Date.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(eq-month <date> <date>)";
	}

}
