/*
 * Copyright 2002-2010 Jamocha
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
package org.jamocha.rete.measures;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Cube;
import org.jamocha.rete.CubeBinding;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;

public class StandardDeviationMeasure implements AggregateMeasure {

	public static final String STANDARD_DEVIATION = "stdev";
	
	public StandardDeviationMeasure() {
		super();
	}

	public BigDecimal calculate(Rete engine, Cube cube, Object[] data, CubeBinding binding) {
		if (data != null) {
			ArrayList<Object> values = new ArrayList<Object>();
			for (int idx=0; idx < data.length; idx++) {
				Index facts = (Index)data[idx];
				Object value = facts.getFacts()[binding.getLeftRow()].getSlotValue(binding.getLeftIndex());
				values.add(value);
			}
			return this.calculate(engine, cube, values);
		} else {
			return new BigDecimal(0);
		}
	}
	
	/**
	 * The current implementation uses the following equation for standard deviation.
	 * s2 = S(x - m)2/N
	 * x - data value
	 * m - mean
	 * N - number of data elements
	 * S - the set of 
	 * s2 - deviation squared
	 * 
	 * @param engine
	 * @param cube
	 * @param data
	 * @return
	 */
	protected BigDecimal calculate(Rete engine, Cube cube, List<Object> data) {
		// first calculate the average
		BigDecimal sum = new BigDecimal(0);
		for (int idx=0; idx < data.size(); idx++) {
			Object value = data.get(idx);
			if (value instanceof Number) {
				Number n = (Number)value;
				sum = sum.add(new BigDecimal(n.doubleValue()));
			} else if (value instanceof BigDecimal) {
				BigDecimal bd = (BigDecimal)value;
				sum = sum.add(bd);
			} else if (value instanceof BigInteger) {
				BigInteger bi = (BigInteger)value;
				sum = sum.add(new BigDecimal(bi.longValue()));
			}
		}
		BigDecimal average = sum.divide(new BigDecimal(data.size()), 30, RoundingMode.DOWN);
		sum = new BigDecimal(0);
		// now calculate the deviation from the average
		for (int idx=0; idx < data.size(); idx++) {
			Object value = data.get(idx);
			if (value instanceof Number) {
				Number n = (Number)value;
				BigDecimal dev = new BigDecimal(n.doubleValue()).subtract(average);
				sum = sum.add(dev.pow(2));
			} else if (value instanceof BigDecimal) {
				BigDecimal dev = ((BigDecimal)value).subtract(average);
				sum = sum.add(dev.pow(2));
			} else if (value instanceof BigInteger) {
				BigDecimal dev = new BigDecimal(value.toString()).subtract(average);
				sum = sum.add(dev.pow(2));
			}
		}
		BigDecimal dev = sum.divide(new BigDecimal(data.size()),20, RoundingMode.DOWN);
		return new BigDecimal( Math.sqrt(dev.doubleValue()) );
	}
	
	public String getDescription() {
		return "Returns the standard deviation for a given dataset.";
	}

	public String getMeasureName() {
		return STANDARD_DEVIATION;
	}

}
