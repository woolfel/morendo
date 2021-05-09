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
 * Cube interface defines the methods to access the dimensions and measures of
 * a given cube. It is inspired by OLAP products and borrows heavily from those
 * concepts. A cube can have 1 or more dimensions and 1 or more measures that
 * calculates a numeric value from a numeric dimension.
 * 
 * Similar to Analysis Service and other OLAP products, we can create a cube from
 * 1 or more deftemplates using a left outer join. This means a cube can have one
 * or more Hash indexes based on the joins. When facts are added to the cube, it
 * should only calculate the joins, but not the measures. Once the measure is
 * calculated, we can cache the value. Until a rule uses the measure, we should
 * not pre-calculate all possible values. That approach has been used in older
 * OLAP products, which has been proven to scale poorly.
 * 
 * The two main problems with precalculating all measures for all dimension, is
 * the space requirement grows exponentially. The second is the computation time
 * also increases exponentially with the number of dimensions and dataset size.
 * 
 * @author Peter Lin
 */
public interface Cube {
	String getName();
	void setName(String name);
	
	String getDescription();
	void setDescription(String text);
	
	CubeDimension[] getDimensions();
	void setDimensions(CubeDimension[] dimensions);
	CubeDimension getDimension(String name);
	
	Measure[] getMeasures();
	void setMeasures(Measure[] measures);
	
	Defmeasure[] getDefmeasures();
	void setDefmeasures(Defmeasure[] defmeasures);
	Defmeasure getMeasure(String name);
	
	void addData(Fact[] data, Rete engine);
	void removeData(Fact[] data);
	
	CubeBinding getBinding(String variableName);
	CubeBinding getBindingBySlot(String slotName);
	
	void setProfileQuery(boolean profile);
	boolean profileQuery();
	
	void setProfileIndex(boolean profile);
	boolean profileIndex();
	
	boolean compileCube(Rete engine);
	
	String toPPString();
}
