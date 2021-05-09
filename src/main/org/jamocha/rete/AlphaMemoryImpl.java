/*
 * Copyright 2002-2008 Peter Lin
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
 * 
 */
package org.jamocha.rete;

import java.util.Map;
import java.util.Iterator;

/**
 * @author Peter Lin
 *
 * Basic implementation of Alpha memory. It uses HashMap for storing
 * the indexes.
 */
public class AlphaMemoryImpl implements AlphaMemory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map memory = null;

	/**
	 * 
	 */
	public AlphaMemoryImpl(String name, Rete engine) {
		super();
		memory = engine.newAlphaMemoryMap(name);
	}

	/**
	 * addPartialMatch stores the fact with the factId as the
	 * key.
	 */
	public void addPartialMatch(Fact fact) {
		this.memory.put(fact, fact);
	}

	/**
	 * clear the memory.
	 */
	public void clear() {
		this.memory.clear();
	}

	/**
	 * remove a partial match from the memory
	 */
	public Object removePartialMatch(Fact fact) {
		return this.memory.remove(fact);
	}

	/**
	 * Return the size of the memory
	 */
	public int size() {
		return this.memory.size();
	}

	/**
	 * Return an iterator of the values
	 */
	public Iterator iterator() {
		return this.memory.keySet().iterator();
	}
}
