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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.util.ProfileStats;

public class Defdimension implements CubeDimension {
	
	private String name;
	private boolean joined = false;
	private boolean autoIndex = false;
	private Binding binding = null;
	private List<Deftemplate> deftemplates = new ArrayList<Deftemplate>();
	private String variableName;
	private Map<Object, Object> tokenIndex = null;
	private boolean profile = false;
	
	@SuppressWarnings("unchecked")
	public Defdimension(Rete engine) {
		super();
		tokenIndex = (Map<Object, Object>) engine.newLocalMap();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isJoined() {
		return joined;
	}

	public void setJoined(boolean joined) {
		this.joined = joined;
	}

	public List<Deftemplate> getDeftemplates() {
		return deftemplates;
	}

	public void setDeftemplates(List<Deftemplate> deftemplates) {
		this.deftemplates = deftemplates;
	}
	
	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}
	
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
	public boolean isAutoIndex() {
		return autoIndex;
	}

	public void setAutoIndex(boolean autoIndex) {
		this.autoIndex = autoIndex;
	}
	
	/**
	 * Current implementation gets the value for the left row + column and creates a
	 * token index. This is inspired by sybase IQ, which is a column based database.
	 */
	@SuppressWarnings({ "unchecked" })
	public void indexData(Index index, Rete engine) {
		if (profile) {
			ProfileStats.startCubeIndex();
			Object key = index.getFacts()[this.binding.leftrow].getSlotValue(this.binding.leftIndex);
			Map<Object, Object> value = (Map<Object, Object>)this.tokenIndex.get(key);
			if (value == null) {
				value = (Map<Object, Object>) engine.newLocalMap();
				this.tokenIndex.put(key, value);
			}
			value.put(index, index);
			ProfileStats.endCubeIndex();
		} else {
			Object key = index.getFacts()[this.binding.leftrow].getSlotValue(this.binding.leftIndex);
			Map<Object, Object> value = (Map<Object, Object>)this.tokenIndex.get(key);
			if (value == null) {
				value = (Map<Object, Object>) engine.newLocalMap();
				this.tokenIndex.put(key, value);
			}
			value.put(index, index);
		}
	}
	
	public boolean profile() {
		return this.profile;
	}
	
	public void setProfile(boolean profile) {
		this.profile = profile;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getData(Object value, boolean negated) {
		if (!negated) {
			return (Map<Object, Object>)tokenIndex.get(value);
		} else {
			Map<Object, Object> results = new HashMap<Object, Object>();
			Iterator<Object> keyIterator = this.tokenIndex.keySet().iterator();
			while (keyIterator.hasNext()) {
				String key = (String)keyIterator.next();
				if (!key.equals(value)) {
					Map<Object, Object>values = (Map<Object, Object>)this.tokenIndex.get(key);
					results.putAll(values);
				}
			}
			return results;
		}
	}
	
	/**
	 * method will only return data if the value is an instance
	 * of Number of a subclass.
	 */
	public Map<?, ?> getData(Object value, int operator) {
		if (value instanceof Number) {
			Number n = (Number)value;
			switch (operator) {
				case Constants.GREATER:
					return queryGreater(n);
				case Constants.LESS:
					return queryLesser(n);
				case Constants.GREATEREQUAL:
					return queryGreaterEqual(n);
				case Constants.LESSEQUAL:
					return queryLesserEqual(n);
				case Constants.NOTEQUAL:
					return getData(value,true);
				default:
					return getData(value, false);
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked" })
	protected Map<?, ?> queryGreater(Number value) {
		Map<Object, Object> matches = new HashMap<Object, Object>();
		Iterator<Object> keyIterator = this.tokenIndex.keySet().iterator();
		while (keyIterator.hasNext()) {
			Object key = keyIterator.next();
			if (key instanceof Number) {
				Number v = (Number)key;
				if (Evaluate.evaluateGreater(v, value)) {
					Map<Object, Object> data = (Map<Object, Object>)tokenIndex.get(key);
					matches.putAll(data);
				}
			}
		}
		if (matches.size() > 0) {
			return matches;
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked" })
	protected Map<Object, Object> queryLesser(Number value) {
		Map<Object, Object> matches = new HashMap<Object, Object>();
		Iterator<Object> keyIterator = this.tokenIndex.keySet().iterator();
		while (keyIterator.hasNext()) {
			Object key = keyIterator.next();
			if (key instanceof Number) {
				Number v = (Number)key;
				if (Evaluate.evaluateLess(v, value)) {
					Map<Object, Object> data = (Map<Object, Object>)tokenIndex.get(key);
					matches.putAll(data);
				}
			}
		}
		if (matches.size() > 0) {
			return matches;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	protected Map<Object, Object> queryGreaterEqual(Number value) {
		Map<Object, Object> matches = new HashMap<Object, Object>();
		Iterator<Object> keyIterator = this.tokenIndex.keySet().iterator();
		while (keyIterator.hasNext()) {
			Object key = keyIterator.next();
			if (key instanceof Number) {
				Number v = (Number)key;
				if (Evaluate.evaluateGreaterEqual(v, value)) {
					Map<Object, Object> data = (Map<Object, Object>)tokenIndex.get(key);
					matches.putAll(data);
				}
			}
		}
		if (matches.size() > 0) {
			return matches;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	protected Map<Object,Object> queryLesserEqual(Number value) {
		Map<Object,Object> matches = new HashMap<Object, Object>();
		Iterator<Object> keyIterator = this.tokenIndex.keySet().iterator();
		while (keyIterator.hasNext()) {
			Object key = keyIterator.next();
			if (key instanceof Number) {
				Number v = (Number)key;
				if (Evaluate.evaluateLessEqual(v, value)) {
					Map<Object,Object> data = (Map<Object,Object>)tokenIndex.get(key);
					matches.putAll(data);
				}
			}
		}
		if (matches.size() > 0) {
			return matches;
		} else {
			return null;
		}
	}
	
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("dimension " + this.name + " ?" + this.variableName);
		buf.append(" : autoIndex(" + this.autoIndex + ")");
		if (tokenIndex != null) {
			buf.append(" : index size(" + tokenIndex.size() + ")");
		} else {
			buf.append(" : index size(0)");
		}
		if (binding != null && binding.leftrow > -1) {
			buf.append(" : row(" + binding.leftrow + "), col(" + binding.leftIndex + ")");
		}
		return buf.toString();
	}
}
