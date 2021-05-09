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
package org.jamocha.rete.measures;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jamocha.rete.Cube;
import org.jamocha.rete.CubeBinding;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;

public class AverageMeasure implements AggregateMeasure {

	public static final String AVERAGE = "average";
	
	public AverageMeasure() {
		super();
	}

	/**
	 * Method will use the CubeBining to extract the value and calculate the average
	 */
	public BigDecimal calculate(Rete engine, Cube cube, Object[] data, CubeBinding binding) {
		if (data != null) {
			BigDecimal sum = new BigDecimal(0);
			for (int idx=0; idx < data.length; idx++) {
				Index facts = (Index)data[idx];
				Object value = facts.getFacts()[binding.getLeftRow()].getSlotValue(binding.getLeftIndex());
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
			BigDecimal average = sum.divide(new BigDecimal(data.length), 30,BigDecimal.ROUND_DOWN);
			return average;
		} else {
			return new BigDecimal(0);
		}
	}

	public String getDescription() {
		return "The average of a given dataset";
	}

	public String getMeasureName() {
		return AVERAGE;
	}
}
