/*
 * Copyright 2002-2008 Peter Lin
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

/**
 * @author Peter Lin
 *
 * TemporalHashedAlphaMem is the hashed alpha memory for Temporal nodes.
 * We use a linkedHashMap so that we can easily remove the expired facts.
 * This means at the top of the list are older facts and the bottom has
 * the newer facts. We only need to remove facts that are older than the
 * current timestamp - time window. The equal would be this.
 * 
 * if ( (currentTime - time window) > fact.timestamp )
 * 
 * Rather than keep a timestamp of when the fact entered the join node,
 * we assume the elapsed time between the time the fact entered the
 * engine and when it activated the node is less than 1 second. Keeping
 * a timestamp of when the fact activated the node is too costly and
 * isn't practical. This means for each fact, there would be n timestamps,
 * where n is the number of temporal nodes for the given object type.
 * 
 * If we look at the number of temporal node timestamps the engine
 * would need to maintain would be this.
 * 
 * f * n = number of temporal timestamps
 * 
 * f = number of facts
 * n = number of temporal nodes
 * 
 * If we have 100,000 facts and 100 temporal nodes, the engine would
 * maintain 10,000,000 timestamps. clearly that isn't scalable and
 * would have a significant impact.
 */
public class TemporalHashedAlphaMem implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Map<HashIndex, Map<Object, Object>> memory = null;
    
    protected int counter = 0;
    
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public TemporalHashedAlphaMem(String name, Rete engine) {
		super();
		memory = (Map<HashIndex, Map<Object, Object>>) engine.newLinkedHashmap(name);
	}

	/**
     * addPartialMatch stores the fact with the factId as the
     * key.
	 */
	public void addPartialMatch(HashIndex index, Fact fact, Rete engine) {
		Map<Object, Object> matches = (Map<Object, Object>)this.memory.get(index);
		if (matches == null) {
			this.addNewPartialMatch(index,fact,engine);
		} else {
			matches.put(fact,fact);
		}
		this.counter++;
	}
	
	@SuppressWarnings("unchecked")
	public void addNewPartialMatch(HashIndex index, Fact fact, Rete engine) {
		Map<Object, Object> matches = (Map<Object, Object>) engine.newMap();
		matches.put(fact,fact);
		this.memory.put(index,matches);
	}

	/**
     * clear the memory.
	 */
	public void clear() {
		Iterator<?> itr = this.memory.values().iterator();
		while (itr.hasNext()) {
			((Map<?, ?>)itr.next()).clear();
		}
        this.memory.clear();
	}

	public boolean isPartialMatch(HashIndex index, Fact fact) {
		Map<?, ?> list = (Map<?, ?>)this.memory.get(index);
		if (list != null) {
			return list.containsKey(fact);
		} else {
			return false;
		}
	}
	
	/**
     * remove a partial match from the memory
	 */
	public int removePartialMatch(HashIndex index, Fact fact) {
		Map<?, ?> list = (Map<?, ?>)this.memory.get(index);
		list.remove(fact);
		if (list.size() == 0) {
			this.memory.remove(index);
		}
		this.counter--;
        return list.size();
	}

    /**
     * Return the number of memories of all hash buckets
     */
	public int size() {
    	Iterator<?> itr = this.memory.keySet().iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		Map<?, ?> matches = (Map<?, ?>)this.memory.get(itr.next());
    		count += matches.size();
    	}
        return count;
    }

    public int bucketCount() {
    	return this.counter;
    }
    
    /**
     * Return an iterator of the values
     */
	public Iterator<?> iterator(HashIndex index) {
    	Map<?, ?> list = (Map<?, ?>)this.memory.get(index);
		if (list != null) {
            // we have to create a new ArrayList with the values
            // so the iterator will work correctly. if we didn't
            // do this, we might get a NullPointerException or a
            // possibly a concurrent modification exception, since
            // the node could be still iterating over the facts
            // as stale facts are removed.
            ArrayList<?> rlist = new ArrayList<Object>(list.values());
	        return rlist.iterator();
		} else {
			return null;
		}
    }
    
	public int count(HashIndex index) {
    	Map<?, ?> list = (Map<?, ?>)this.memory.get(index);
    	if (list != null) {
    		return list.size();
    	} else {
    		return 0;
    	}
    }
    
    /**
     * return an arraylist with all the facts
     * @return
     */
	public Object[] iterateAll() {
    	Object[] all = new Object[this.counter];
    	Iterator<?> itr = this.memory.keySet().iterator();
    	int idx = 0;
    	while (itr.hasNext()) {
    		Map<?, ?> f = (Map<?, ?>)this.memory.get(itr.next());
    		Iterator<?> itr2 = f.values().iterator();
    		while (itr2.hasNext()) {
        		all[idx] = itr2.next();
        		idx++;
    		}
    	}
    	return all;
    }
}
