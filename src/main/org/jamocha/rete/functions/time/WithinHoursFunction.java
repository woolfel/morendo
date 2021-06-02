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
import org.jamocha.rete.FunctionParam2;
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
public class WithinHoursFunction extends AbstractTimeFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String WITHIN_HOURS = "within-hours";
	protected GregorianCalendar calendar1 = new GregorianCalendar();
	protected GregorianCalendar calendar2 = new GregorianCalendar();
	protected GregorianCalendar calendar3 = new GregorianCalendar();
	
	public WithinHoursFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean eval = Boolean.FALSE;
		if (params != null && params.length == 3) {
			int interval = params[0].getIntValue();
			Date date1 = null;
			if (params[1] instanceof ValueParam) {
				date1 = this.getDate(params[1].getValue());
			} else if (params[1] instanceof BoundParam) {
				date1 = this.getDate(engine.getBinding( ((BoundParam)params[1]).getVariableName()));
			} else if (params[1] instanceof FunctionParam2) {
				date1 = this.getDate( ((FunctionParam2)params[1]).getValue(engine, Constants.DATE_TYPE));
			}
			Date date2 = null;
			if (params[2] instanceof ValueParam) {
				date2 = this.getDate(params[2].getValue());
			} else if (params[2] instanceof BoundParam) {
				date2 = this.getDate(engine.getBinding( ((BoundParam)params[2]).getVariableName()));
			} else if (params[2] instanceof FunctionParam2) {
				date2 = this.getDate( ((FunctionParam2)params[2]).getValue(engine, Constants.DATE_TYPE));
			}
			if (date1 != null && date2 != null) {
				calendar1.setTime(date1);
				calendar2.setTime(date2);
				calendar3.setTime(date1);
				calendar3.add(Calendar.HOUR_OF_DAY, interval);
				if (calendar2.compareTo(calendar1) >= 0 && calendar2.compareTo(calendar3) <= 0) {
					eval = Boolean.TRUE;
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = 
			new DefaultReturnValue(Constants.BOOLEAN_OBJECT, eval);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return WITHIN_HOURS;
	}

	public Class<?>[] getParameter() {
		return new Class[]{Date.class, Date.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(within-hours <date> <date>)";
	}

}
