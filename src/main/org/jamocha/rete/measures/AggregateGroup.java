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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Rete;

public class AggregateGroup implements MeasureGroup {

	public static final String AGGREGATE_GROUP = "aggregate group";
	private List<AggregateMeasure> measures = new ArrayList<AggregateMeasure>();
	
	public AggregateGroup() {
		super();
	}

	public String getGroupName() {
		return AGGREGATE_GROUP;
	}

	public List<AggregateMeasure> getMeasures() {
		return measures;
	}

	public void loadMeasures(Rete engine) {
		AverageMeasure ave = new AverageMeasure();
		engine.declareMeasure(ave);
		measures.add(ave);
		EightyPercentMeasure eightper = new EightyPercentMeasure();
		engine.declareMeasure(eightper);
		measures.add(eightper);
		MedianMeasure median = new MedianMeasure();
		engine.declareMeasure(median);
		measures.add(median);
		MaxMeasure max = new MaxMeasure();
		engine.declareMeasure(max);
		measures.add(max);
		MinMeasure min = new MinMeasure();
		engine.declareMeasure(min);
		measures.add(min);
		NinetyPercentMeasure nineper = new NinetyPercentMeasure();
		engine.declareMeasure(nineper);
		measures.add(nineper);
		SeventyPercentMeasure sevenper = new SeventyPercentMeasure();
		engine.declareMeasure(sevenper);
		measures.add(sevenper);
		StandardDeviationMeasure stndev = new StandardDeviationMeasure();
		engine.declareMeasure(stndev);
		measures.add(stndev);
		SumMeasure sum = new SumMeasure();
		engine.declareMeasure(sum);
		measures.add(sum);
	}

}
