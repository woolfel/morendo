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
 * BetaMemory stores the matches
 */
public class BetaMemoryImpl implements BetaMemory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Index index = null;

	protected Map<Fact, Fact> matches = null;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public BetaMemoryImpl(Index index, Rete engine) {
		super();
		this.index = index;
		matches = (Map<Fact, Fact>) engine.newMap();
	}

	/**
	 * Return the index of the beta memory
	 * @return
	 */
	public Index getIndex() {
		return this.index;
	}

	/**
	 * Get the array of facts
	 * @return
	 */
	public Fact[] getLeftFacts() {
		return this.index.getFacts();
	}

	/**
	 * Return the array containing the facts entering
	 * the right input that matched
	 * @return
	 */
	public Iterator<Fact> iterateRightFacts() {
		return (Iterator<Fact>) this.matches.keySet().iterator();
	}

	/**
	 * The method will check to see if the fact has
	 * previously matched
	 * @param rightfacts
	 * @return
	 */
	public boolean matched(Fact rightfact) {
		return this.matches.containsKey(rightfact);
	}

	/**
	 * Add a match to the list
	 * @param rightfacts
	 */
	public void addMatch(Fact rightfact) {
		this.matches.put(rightfact, null);
	}

	public void removeMatch(Fact rightfact) {
		this.matches.remove(rightfact);
	}

	/**
	 * clear will clear the memory
	 */
	public void clear() {
		this.matches.clear();
		this.index = null;
	}

	/**
	 * method simply returns the size
	 */
	public int matchCount() {
		return matches.size();
	}

	/**
	 * The implementation will append the facts for the left followed
	 * by double colon "::" and then the matches from the right
	 */
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		for (int idx = 0; idx < this.index.getFacts().length; idx++) {
			if (idx > 0) {
				buf.append(", ");
			}
			buf.append(this.index.getFacts()[idx].getFactId());
		}
		buf.append(": ");
		Iterator<Fact> itr = matches.keySet().iterator();
		while (itr.hasNext()) {
			Fact f = (Fact) itr.next();
			buf.append(f.getFactId() + ", ");
		}
		return buf.toString();
	}
}
