/*
 * Copyright 2002-2009 Jamocha
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

import org.jamocha.rete.Cube;
import org.jamocha.rete.CubeBinding;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;

public class MaxMeasure implements AggregateMeasure {

	public static final String MAX = "max";
	
	public MaxMeasure() {
		super();
	}

	public BigDecimal calculate(Rete engine, Cube cube, Object[] data, CubeBinding binding) {
		if (data != null && data.length > 0) {
			Index first = (Index)data[0];
			Object fvalue = first.getFacts()[binding.getLeftRow()].getSlotValue(binding.getLeftIndex());
			BigDecimal max = new BigDecimal(fvalue.toString());
			for (int idx=0; idx < data.length; idx++) {
				Index facts = (Index)data[idx];
				Object value = facts.getFacts()[binding.getLeftRow()].getSlotValue(binding.getLeftIndex());
				if (value instanceof Number) {
					Number n = (Number)value;
					max = max.max(new BigDecimal(n.doubleValue()));
				} else if (value instanceof BigDecimal) {
					BigDecimal bd = (BigDecimal)value;
					max = max.max(bd);
				} else if (value instanceof BigInteger) {
					BigInteger bi = (BigInteger)value;
					max = max.max(new BigDecimal(bi.longValue()));
				}
			}
			return max;
		} else {
			return new BigDecimal(0);
		}
	}
	
	public String getDescription() {
		return "Returns the max value of a given dataset from a multi-dimensional cube query.";
	}

	public String getMeasureName() {
		return MAX;
	}

}
