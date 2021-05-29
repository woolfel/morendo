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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Cube;
import org.jamocha.rete.CubeBinding;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;

/**
 * Median is the value in the middle of a set of numbers. It is different
 * than mean, which is commonly known as average. If the list has an even
 * number of elements, the median is the average of the two center values.
 * If the size is odd, the median is the value in the middle.
 * 
 * @author Peter Lin
 *
 */
public class MedianMeasure implements AggregateMeasure {

	public static final String MEDIAN = "median";
	
	public MedianMeasure() {
	}

	protected BigDecimal calculate(Rete engine, Cube cube, List<BigDecimal> data) {
		if (data != null) {
			java.util.Collections.sort(data);
			int size = data.size();
			if (size % 2 == 1) {
				// it's odd
				int div = size / 2;
				Object value = data.get(div);
				if (value instanceof BigDecimal) {
					return (BigDecimal)value;
				} else if (value instanceof Number) {
					return new BigDecimal( ((Number)value).doubleValue() );
				} else {
					return new BigDecimal( value.toString() );
				}
			} else {
				int div = size / 2;
				Object val1 = data.get(div);
				Object val2 = data.get(div - 1);
				BigDecimal total = addValues(val1, val2);
				return total.divide(new BigDecimal(2), 30, RoundingMode.DOWN);
			}
		}
		return new BigDecimal(0);
	}

	/**
	 * Method extracts the values and call an internal protected method to calculate the median
	 */
	public BigDecimal calculate(Rete engine, Cube cube, Object[] data, CubeBinding binding) {
		if (data != null) {
			ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
			for (int idx=0; idx < data.length; idx++) {
				Index facts = (Index)data[idx];
				BigDecimal value = (BigDecimal) facts.getFacts()[binding.getLeftRow()].getSlotValue(binding.getLeftIndex());
				values.add(value);
			}
			return this.calculate(engine, cube, values);
		} else {
			return new BigDecimal(0);
		}
	}
	
	protected BigDecimal addValues(Object val1, Object val2) {
		BigDecimal bd1 = null;
		BigDecimal bd2 = null;
		if (val1 instanceof BigDecimal) {
			bd1 = (BigDecimal)val1;
		} else {
			bd1 = new BigDecimal( val1.toString());
		}
		if (val2 instanceof BigDecimal) {
			bd2 = (BigDecimal)val2;
		} else {
			bd2 = new BigDecimal( val2.toString());
		}
		
		return bd1.add(bd2);
	}
	
	public String getDescription() {
		return "Median is the value in the middle of a dataset.";
	}

	public String getMeasureName() {
		return MEDIAN;
	}

}
