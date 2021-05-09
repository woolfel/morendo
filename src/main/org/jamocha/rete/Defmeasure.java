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
package org.jamocha.rete;

import org.jamocha.rete.measures.Measure;

/**
 * Defmeasure is used by the compiler to parse the declaration. DefmeasureFunction
 * will then use it to configure the Cube and lookup the actual measure function.
 * 
 * @author Peter Lin
 */
public class Defmeasure {
	
	private String measureName;
	private String measureLabel;
	private String variableName;
	private Measure measure;
	private CubeDimension dimension;
	
	public Defmeasure() {
		super();
	}
	
	public String getMeasureName() {
		return measureName;
	}

	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}

	public String getMeasureLabel() {
		return measureLabel;
	}

	public void setMeasureLabel(String measureLabel) {
		this.measureLabel = measureLabel;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}
	
	public CubeDimension getDimension() {
		return dimension;
	}

	public void setDimension(CubeDimension dimension) {
		this.dimension = dimension;
	}
	
	public String toPPString() {
		return "measure " + this.measureLabel + " (function " + this.measureName + " ?" + variableName + ")";
	}
}
