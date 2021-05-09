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
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class TimeFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList funcs = new ArrayList();
	
	public TimeFunctions() {
		super();
	}

	public String getName() {
		return TimeFunctions.class.getSimpleName();
	}

	public void loadFunctions(Rete engine) {
		AddSecondsFunction addsec = new AddSecondsFunction();
		engine.declareFunction(addsec);
		funcs.add(addsec);
		AddMinutesFunction addmin = new AddMinutesFunction();
		engine.declareFunction(addmin);
		funcs.add(addmin);
		AddHoursFunction addhr = new AddHoursFunction();
		engine.declareFunction(addhr);
		funcs.add(addhr);
		AfterFunction after = new AfterFunction();
		engine.declareFunction(after);
		funcs.add(after);
		BeforeFunction before = new BeforeFunction();
		engine.declareFunction(before);
		funcs.add(before);
		EqDayFunction eqday = new EqDayFunction();
		engine.declareFunction(eqday);
		funcs.add(eqday);
		EqHourFunction eqhour = new EqHourFunction();
		engine.declareFunction(eqhour);
		funcs.add(eqhour);
		EqMinuteFunction eqminute = new EqMinuteFunction();
		engine.declareFunction(eqminute);
		funcs.add(eqminute);
		EqMonthFunction eqmonth = new EqMonthFunction();
		engine.declareFunction(eqmonth);
		funcs.add(eqmonth);
		EqSecondFunction eqsec = new EqSecondFunction();
		engine.declareFunction(eqsec);
		funcs.add(eqsec);
		EqYearFunction eqyr = new EqYearFunction();
		engine.declareFunction(eqyr);
		funcs.add(eqyr);
		MillisecondTime mstime = new MillisecondTime();
		engine.declareFunction(mstime);
		funcs.add(mstime);
		NowFunction now = new NowFunction();
		engine.declareFunction(now);
		funcs.add(now);
		BetweenFunction betwn = new BetweenFunction();
		engine.declareFunction(betwn);
		funcs.add(betwn);
		WithinDaysFunction withinD = new WithinDaysFunction();
		engine.declareFunction(withinD);
		funcs.add(withinD);
		WithinHoursFunction withinH = new WithinHoursFunction();
		engine.declareFunction(withinH);
		funcs.add(withinH);
		WithinMinutesFunction withinM = new WithinMinutesFunction();
		engine.declareFunction(withinM);
		funcs.add(withinM);
		WithinSecondsFunction withinS = new WithinSecondsFunction();
		engine.declareFunction(withinS);
		funcs.add(withinS);
	}

	public List listFunctions() {
		return funcs;
	}
}
