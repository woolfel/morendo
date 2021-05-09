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

import org.jamocha.rete.Cube;
import org.jamocha.rete.CubeBinding;
import org.jamocha.rete.Rete;

public interface AggregateMeasure extends Measure {
	
	/**
	 * Concrete implementation needs to use the CubeBinding to access the value
	 * to calculate the measure
	 * @param engine
	 * @param cube
	 * @param data
	 * @param binding
	 * @return
	 */
	BigDecimal calculate(Rete engine, Cube cube, Object[] data, CubeBinding binding);
}
