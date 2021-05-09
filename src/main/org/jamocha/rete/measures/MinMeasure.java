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
import java.util.List;

import org.jamocha.rete.Cube;
import org.jamocha.rete.CubeBinding;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;

public class MinMeasure implements AggregateMeasure {

	public static final String MIN = "min";
	
	public MinMeasure() {
	}

	public BigDecimal calculate(Rete engine, Cube cube, Object[] data, CubeBinding binding) {
		if (data != null && data.length > 0) {
			Index first = (Index)data[0];
			Object fvalue = first.getFacts()[binding.getLeftRow()].getSlotValue(binding.getLeftIndex());
			BigDecimal min = new BigDecimal(fvalue.toString());
			for (int idx=1; idx < data.length; idx++) {
				Index facts = (Index)data[idx];
				Object value = facts.getFacts()[binding.getLeftRow()].getSlotValue(binding.getLeftIndex());
				if (value instanceof Number) {
					Number n = (Number)value;
					min = min.min(new BigDecimal(n.doubleValue()));
				} else if (value instanceof BigDecimal) {
					BigDecimal bd = (BigDecimal)value;
					min = min.min(bd);
				} else if (value instanceof BigInteger) {
					BigInteger bi = (BigInteger)value;
					min = min.min(new BigDecimal(bi.longValue()));
				}
			}
			return min;
		} else {
			return new BigDecimal(0);
		}
	}
	
	public String getDescription() {
		return "Returns the min value of a given dataset from a multi-dimensional cube query.";
	}

	public String getMeasureName() {
		return MIN;
	}

}
