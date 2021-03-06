/*
 * Copyright 2002-2010 Peter Lin
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

import java.util.Iterator;
import java.util.Map;

/**
 * @author Peter Lin
 * HashedAlphaMemory2 is different in that it has 2 levels of
 * indexing. The first handles equal to comparisons. The second
 * level handles not equal to.
 */
public class HashedNeqAlphaMemory extends HashedAlphaMemoryImpl {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public HashedNeqAlphaMemory(String name, Rete engine) {
		super(name, engine);
	}

	/**
     * addPartialMatch stores the fact with the factId as the
     * key.
	 */
	@SuppressWarnings("unchecked")
	public int addPartialMatch(NotEqHashIndex index, Fact fact, Rete engine) {
		Map<HashIndex, Map<?, ?>> matches = (Map<HashIndex, Map<?, ?>>) this.memory.get(index);
        int count = 0;
		if (matches == null) {
			count = this.addNewPartialMatch(index,fact, engine);
		} else {
			Map<Fact, Fact> submatch = (Map<Fact, Fact>)matches.get(index.getSubIndex());
			if (submatch == null) {
				submatch = (Map<Fact, Fact>)engine.newMap();
				submatch.put(fact,fact);
				matches.put(index.getSubIndex(),submatch);
                count = matches.size();
			} else {
				submatch.put(fact,fact);
                count = submatch.size();
			}
		}
		this.counter++;
        return count;
	}
	
	@SuppressWarnings("unchecked")
	public int addNewPartialMatch(NotEqHashIndex index, Fact fact, Rete engine) {
		Map<HashIndex, Map<?, ?>> matches = (Map<HashIndex, Map<?, ?>>)engine.newMap();
		Map<Fact, Fact> submatch = (Map<Fact, Fact>)engine.newMap();
		submatch.put(fact,fact);
		matches.put(index.getSubIndex(),submatch);
		this.memory.put(index,matches);
        return 1;
	}

	/**
     * clear the memory.
	 */
	public void clear() {
		Iterator<HashIndex> itr = this.memory.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Map<?, ?> matches = (Map<?, ?>) this.memory.get(key);
			Iterator<?> itr2 = matches.keySet().iterator();
			while (itr2.hasNext()) {
				Object subkey = itr2.next();
				Map<?, ?> submatch = (Map<?, ?>)matches.get(subkey);
				submatch.clear();
			}
			matches.clear();
		}
        this.memory.clear();
	}

	public boolean isPartialMatch(NotEqHashIndex index, Fact fact) {
		@SuppressWarnings("unchecked")
		Map<HashIndex,Map<?, ?>> match = (Map<HashIndex, Map<?, ?>>) this.memory.get(index);
		if (match != null) {
			Map<?, ?> submatch = (Map<?, ?>)match.get(index.getSubIndex());
			if (submatch != null) {
				return submatch.containsKey(fact);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
     * remove a partial match from the memory
	 */
	@SuppressWarnings("unchecked")
	public int removePartialMatch(NotEqHashIndex index, Fact fact) {
		Map<HashIndex,Map<?, ?>> match = (Map<HashIndex, Map<?, ?>>) this.memory.get(index);
		if (match != null) {
			Map<?, ?> submatch = (Map<?, ?>)match.get(index.getSubIndex());
			submatch.remove(fact);
			if (submatch.size() == 0) {
				match.remove(index.getSubIndex());
			}
			this.counter--;
            return submatch.size();
		}
        return -1;
	}

    /**
     * Return the number of memories of all hash buckets
     */
	@SuppressWarnings("unchecked")
	public int size() {
    	Iterator<HashIndex> itr = this.memory.keySet().iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		Map<HashIndex,Map<?, ?>> matches = (Map<HashIndex,Map<?, ?>>) this.memory.get(itr.next());
    		Iterator<?> itr2 = matches.keySet().iterator();
    		while (itr2.hasNext()) {
    			EqHashIndex ehi = (EqHashIndex)itr2.next();
                Map<?, ?> submatch = (Map<?, ?>)matches.get(ehi);
        		count += submatch.size();
    		}
    	}
        return count;
    }

    public int bucketCount() {
    	return this.memory.size();
    }
    
    /**
     * Return an iterator of the values
     */
   	@SuppressWarnings("unchecked")
	public Object[] iterator(NotEqHashIndex index) {
    	Map<HashIndex,Map<?, ?>> matches = (Map<HashIndex, Map<?, ?>>) this.memory.get(index);
    	Object[] list = new Object[this.counter];
    	Object[] trim = null;
    	int idz = 0;
		if (matches != null) {
			Iterator<?> itr = matches.keySet().iterator();
			while (itr.hasNext()) {
				Object key = itr.next();
				// if the key doesn't match the subindex, we
				// add it to the list. If it matches, we exclude
				// it.
				if (!index.getSubIndex().equals(key)) {
					Map<?, ?> submatch = (Map<?, ?>)matches.get(key);
					Iterator<?> itr2 = submatch.keySet().iterator();
					while (itr2.hasNext()) {
						list[idz] = itr2.next();
						idz++;
					}
				}
				trim = new Object[idz];
				System.arraycopy(list,0,trim,0,idz);
			}
			list = null;
	        return trim;
		} else {
			return null;
		}
    }
    
    /**
     * if there are zero matches for the NotEqHashIndex2, the method
     * return true. If there are matches, the method returns false.
     * False means there's 1 or more matches
     * @param index
     * @return
     */
   	@SuppressWarnings("unchecked")
	public boolean zeroMatch(NotEqHashIndex index) {
    	Map<HashIndex,Map<?, ?>> matches = (Map<HashIndex, Map<?, ?>>) this.memory.get(index);
        int idz = 0;
        if (matches != null) {
            Iterator<?> itr = matches.keySet().iterator();
            while (itr.hasNext()) {
                Object key = itr.next();
                // if the key doesn't match the subindex, add it to the
                // counter.
                if (!index.getSubIndex().equals(key)) {
                    Map<?, ?> submatch = (Map<?, ?>)matches.get(key);
                    idz += submatch.size();
                }
                if (idz > 0) {
                    break;
                }
            }
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * return an arraylist with all the facts
     * @return
     */
    @SuppressWarnings("unchecked")
	public Object[] iterateAll() {
    	Object[] facts = new Object[this.counter];
    	Iterator<HashIndex> itr = this.memory.keySet().iterator();
    	int idx = 0;
    	while (itr.hasNext()) {
    		Map<HashIndex,Map<?, ?>> matches = (Map<HashIndex, Map<?, ?>>) this.memory.get(itr.next());
    		Iterator<?> itr2 = matches.keySet().iterator();
    		while (itr2.hasNext()) {
    			Map<?, ?> submatch = (Map<?, ?>)matches.get(itr2.next());
    			Iterator<?> itr3 = submatch.values().iterator();
    			while (itr3.hasNext()) {
        			facts[idx] = itr3.next();
        			idx++;
    			}
    		}
    	}
    	Object[] trim = new Object[idx];
    	System.arraycopy(facts,0,trim,0,idx);
    	facts = null;
    	return trim;
    }
}
