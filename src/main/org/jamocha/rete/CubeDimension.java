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

import java.util.List;
import java.util.Map;

public interface CubeDimension {
	
	public String getName();
	public void setName(String name);

	/**
	 * if the slot mapped to the dimension is used to join
	 * this method returns true.
	 * @return
	 */
	public boolean isJoined();
	public void setJoined(boolean joined);
	
	/**
	 * If a rule uses the dimension, we can set the dimension to 
	 * auto index when the rule is compiled.
	 * @return
	 */
	public boolean isAutoIndex();
	public void setAutoIndex(boolean index);

	public List<?> getDeftemplates();
	public void setDeftemplates(List<?> deftemplates);
	
	public Binding getBinding();
	public void setBinding(Binding binding);
	
	public String getVariableName();
	public void setVariableName(String variable);
	
	/**
	 * Method is responsible for generating an index for the
	 * dataset by the dimension. The most obvious type of
	 * index is a token index, similar to Sybase IQ.
	 * @param index
	 */
	public void indexData(Index index, Rete engine);
	public Map<?, ?> getData(Object value, boolean negated);
	public Map<?, ?> getData(Object value, int operator);
	
	boolean profile();
	void setProfile(boolean profile);
	
	String toPPString();
}
