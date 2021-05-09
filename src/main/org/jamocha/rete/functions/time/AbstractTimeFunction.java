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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Base class with commonly used methods related to time.
 * 
 * @author Peter Lin
 *
 */
public abstract class AbstractTimeFunction {
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat();
	
	protected long getMillisecondTime(Object date) {
		if (date instanceof Date) {
			return ((Date)date).getTime();
		} else if (date instanceof Calendar) {
			return ((Calendar)date).getTimeInMillis();
		} else if (date instanceof BigDecimal) {
			return ((BigDecimal)date).longValue();
		} else if (date instanceof BigInteger) {
			return ((BigInteger)date).longValue();
		} else {
			return new Long(date.toString()).longValue();
		}
	}
	
	protected Date getDate(Object value) {
		if (value instanceof Date) {
			return (Date)value;
		} else if (value instanceof Calendar) {
			return ((Calendar)value).getTime();
		} else if (value instanceof BigDecimal) {
			return new Date( ((BigDecimal)value).longValue() );
		} else if (value instanceof BigInteger) {
			return new Date( ((BigInteger)value).longValue() );
		} else if (value instanceof String) {
			try {
				return dateFormat.parse( (String)value );
			} catch (ParseException e) {
				return null;
			}
		} else {
			return null;
		}
	}
}
