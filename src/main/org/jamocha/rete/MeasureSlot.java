/*
 * Copyright 2002-2009 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jamocha.rete;

public class MeasureSlot extends BaseSlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Defmeasure defmeasure = null;

	public MeasureSlot(Defmeasure measure) {
		this.defmeasure = measure;
		this.setName(measure.getMeasureLabel());
	}
	
	public Defmeasure getDefmeasure() {
		return defmeasure;
	}
	
	public Object clone() {
		MeasureSlot clone = new MeasureSlot(this.defmeasure);
		clone.setValueType(this.getValueType());
		clone.setId(this.getId());
		return clone;
	}
	
	public String toPPString() {
		return defmeasure.toPPString();
	}
}
