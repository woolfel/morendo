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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.measures.AggregateMeasure;
import org.jamocha.rete.measures.Measure;
import org.jamocha.rete.util.NodeUtils;
import org.jamocha.rete.util.ProfileStats;

/**
 * @author Peter Lin
 * 
 * HashedBetaNode indexes the right input to improve cross product performance.
 */
public class CubeQueryBNode extends BaseJoin {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private CubeFact cubeFact = null;
    private Binding[] numericBindings = null;
    private CubeBinding[] measureBindings = null;
    private CubeBinding[] dimensionBindings = null;

    public CubeQueryBNode(int id) {
        super(id);
    }
    
    public void setNumericBindings(Binding[] bindings) {
    	this.numericBindings = bindings;
    }
    
    public Binding[] getNumericBindings() {
    	return this.numericBindings;
    }
    
    public void setMeasureBindings(CubeBinding[] measureBindings) {
    	this.measureBindings = measureBindings;
    }
    
    public CubeBinding[] getMeasureBindings() {
    	return this.measureBindings;
    }
    
    public void setDimensionBindings(CubeBinding[] dimensionBindings) {
    	this.dimensionBindings = dimensionBindings;
    }
    
    public CubeBinding[] getDimensionBindings() {
    	return this.dimensionBindings;
    }

    /**
     * assertLeft takes an array of facts. Since the next join may be joining
     * against one or more objects, we need to pass all previously matched
     * facts.
     * 
     * @param factInstance
     * @param engine
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
            throws AssertException {
    	Map leftmem = (Map) mem.getBetaLeftMemory(this);
    	// first we create the hashIndex and put it in the left memory
        EqHashIndex eqIndex = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        Map values = (Map)leftmem.get(eqIndex);
        if (values == null) {
        	values = engine.newMap();
        	leftmem.put(eqIndex, values);
        }
        values.put(linx, linx);

        // if we have a CubeFact proceed, otherwise do nothing
        if (cubeFact != null) {
        	// if the CubeFact has propagated down, we need to check for cached
        	// value before querying the cube
        	CubeHashMemoryImpl rightmem = (CubeHashMemoryImpl) mem.getBetaRightMemory(this);
        	if (rightmem.count(eqIndex) > 0) {
        		// cached version already exists, so just propagate
        		Iterator itr = rightmem.iterator(eqIndex);
        		while (itr.hasNext()) {
        			Fact f = (Fact)itr.next();
        			this.propagateAssert(linx.add(f), engine, mem);
        		}
        	} else {
        		Cube c = (Cube)cubeFact.getObjectInstance();
    			// create the ResultsetFact
        		ResultsetFact rsFact = cubeFact.createResultsetFact(engine);
        		Object[] result = queryCube(linx, c, engine, mem, rsFact);
        		if (result != null && result.length > 0) {
            		rsFact.setResultsetData(result);
            		this.executeMeasures(c, engine, mem, rsFact);
            		// cache the ResultsetFact
            		rightmem.addPartialMatch(eqIndex, rsFact, engine);
            		this.propagateAssert(linx.add(rsFact), engine, mem);
        		}
        	}
        }
    }

    /**
     * Since a Cube is asserted when it is defined, it will always propagate
     * down the network before other facts. This means when the right input
     * is triggered, we "might" not need to iterate over the left memories.
     * For now it will, but that's one thing we can optimize later on.
     * @param factInstance
     * @param engine
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws AssertException {
    	// first set the reference to CubeFact
    	this.cubeFact = (CubeFact)rfact;
    	CubeHashMemoryImpl rightmem = (CubeHashMemoryImpl) mem.getBetaRightMemory(this);

        // query the cube for the data
        Cube cube = (Cube)cubeFact.getObjectInstance();

    	Map leftmem = (Map) mem.getBetaLeftMemory(this);
        // Get the partial matches on the side side using the EqHashIndex as the key
        Iterator indexItr = leftmem.keySet().iterator();
        while (indexItr.hasNext()) {
        	EqHashIndex eqIndex = (EqHashIndex)indexItr.next();
        	Map values = (Map)leftmem.get(eqIndex);
        	// if the EqHashIndex has values, we execute the query once
        	if (values.size() > 0) {
        		// we create a new 
        		ArrayList matchValues = new ArrayList(values.values());
        		// get the first match, so we can use it to query the cube
        		Index linx = (Index)matchValues.get(0);
    			// create the ResultsetFact
        		ResultsetFact rsFact = cubeFact.createResultsetFact(engine);
        		// query the cube
        		Object[] result = queryCube(linx, cube, engine, mem, rsFact);
        		if (result != null && result.length > 0) {
            		// set the result data
            		rsFact.setResultsetData(result);
            		this.executeMeasures(cube, engine, mem, rsFact);
            		// cache the ResultsetFact
            		rightmem.addPartialMatch(eqIndex, rsFact, engine);
            		// iterate over all Index with the given EqHashIndex
            		for (int idx=0; idx < matchValues.size(); idx++) {
            			linx = (Index)matchValues.get(idx);
                		this.propagateAssert(linx.add(rsFact), engine, mem);
            		}
        		}
        		break;
        	}
        }
    }

    /**
     * Retracting left uses EqHashIndex to look up the cached fact and propagates it
     * down the network. Note there's going to be only 1 ResultsetFact for a given
     * EqHashIndex.
     * @param factInstance
     * @param engine
     */
    @SuppressWarnings("rawtypes")
	public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
            throws RetractException {
    	// Get the Left memory, which is a Map
    	Map leftmem = (Map) mem.getBetaLeftMemory(this);
    	// Create the EqHashIndex
        EqHashIndex eqIndex = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        // Get the entries matching the EqHashIndex from the Map
        Map entries = (Map)leftmem.get(eqIndex);
        // remove the Index from the entries
        entries.remove(linx);
        // Get the CubeHashMemory for the right side
        CubeHashMemoryImpl rightmem = (CubeHashMemoryImpl) mem.getBetaRightMemory(this);
        // get Iterator for the cached ResultsetFact
        Iterator itr = rightmem.iterator(eqIndex);
        if (itr != null) {
            while (itr.hasNext()) {
                propagateRetract(linx.add((Fact) itr.next()), engine, mem);
            }
        }
    }

    /**
     * Retract right removes all partial matches, since there's
     * only going to be 1 instance of the cube
     * @param factInstance
     * @param engine
     */
    @SuppressWarnings("rawtypes")
	public void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws RetractException {
    	CubeHashMemoryImpl rightmem = (CubeHashMemoryImpl) mem.getBetaRightMemory(this);
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
    	// we iterate over the cache right memories
    	Object[] cachedResults = rightmem.iterateAll();
    	for (int idx=0; idx < cachedResults.length; idx++) {
    		Fact right = (Fact)cachedResults[idx];
    		EqHashIndex eqIndex = new EqHashIndex(NodeUtils.getRightValues(this.binds,right));
    		Map partialMatches = (Map)leftmem.get(eqIndex);
    		Iterator pmItr = partialMatches.values().iterator();
    		while (pmItr.hasNext()) {
    			Index match = (Index)pmItr.next();
    			this.propagateRetract(match.add(right), engine, mem);
    		}
    	}
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object[] queryCube(Index linx, Cube c, Rete engine, WorkingMemory mem, ResultsetFact resultFact) {
		java.util.Set result = new java.util.HashSet();
		Map firstResult = null;
		// execute query
		if (c.profileQuery()) {
			ProfileStats.startCubeQuery();
		}
		
		if (this.binds.length > 0) {
			Binding b = this.binds[0];
			CubeDimension dimension = c.getDimensions()[b.getRightIndex()];
			String parameter = linx.getFacts()[b.leftrow].getSlotValue(b.leftIndex).toString();
			resultFact.setSlotValue(b.rightIndex, parameter);
			firstResult = (Map)dimension.getData(parameter, b.negated());
			if (firstResult != null && firstResult.size() > 0) {
				result.addAll(firstResult.keySet());
			}
		}
		
		// we find the intersection of the all the results
		if (this.binds.length > 1 && result.size() > 0) {
			for (int idx=1; idx < this.binds.length; idx++) {
				Binding b = this.binds[idx];
				CubeDimension dimension = c.getDimensions()[b.getRightIndex()];
				String parameter = linx.getFacts()[b.leftrow].getSlotValue(b.leftIndex).toString();
				resultFact.setSlotValue(b.rightIndex, parameter);
				Map data = null;
				data = (Map)dimension.getData(parameter, b.negated());
				if (data != null) {
					result.retainAll(data.keySet());
				}
			}
		}
		
		// iterate over the numeric bindings that evaluate >, <, >=, <=
		if (this.numericBindings != null && this.numericBindings.length > 0) {
			for (int idx=0; idx < this.numericBindings.length; idx++) {
				Binding2 b = (Binding2)numericBindings[idx];
				CubeDimension dimension = c.getDimensions()[b.getRightIndex()];
				Object parameter = null;
				if (b.getQueryValue() != null) {
					parameter = b.getQueryValue();
				} else {
					parameter = linx.getFacts()[b.getLeftRow()].getSlotValue(b.getLeftIndex());
				}
				resultFact.setSlotValue(b.rightIndex, parameter);
				Map data = null;
				data = (Map)dimension.getData(parameter, b.getOperator());
				if (data != null) {
					result.retainAll(data.keySet());
				}
			}
		}
		if (c.profileQuery()) {
			ProfileStats.endCubeQuery();
		}
		Object[] resultArray = new Object[result.size()];
    	return result.toArray(resultArray);
    }
    
    /**
     * Method iterates over the measures and executes each one.
     * @param linx
     * @param c
     * @param engine
     * @param mem
     * @param resultFact
     */
    protected void executeMeasures(Cube c, Rete engine, WorkingMemory mem, ResultsetFact resultFact) {
    	if (this.measureBindings != null && this.measureBindings.length > 0) {
    		for (int idx=0; idx < this.measureBindings.length; idx++) {
        		CubeBinding cbinding = this.measureBindings[idx];
        		int measureIndex = cbinding.getRightIndex();
        		Defmeasure defmeasure = (Defmeasure)cubeFact.getSlotValue(measureIndex);
        		Measure msr = defmeasure.getMeasure();
        		BigDecimal calculatedValue = null;
        		if (msr instanceof AggregateMeasure) {
        			AggregateMeasure aggrMsr = (AggregateMeasure)msr;
        			calculatedValue = aggrMsr.calculate(engine, c, resultFact.getResultsetData(), cbinding);
        			resultFact.setSlotValue(cbinding.rightIndex, calculatedValue);
        		}
    		}
    	}
    }
    
    /**
     * Method will use the right binding to perform the evaluation of the join.
     * Since we are building joins similar to how CLIPS and other rule engines
     * handle it, it means 95% of the time the right fact list only has 1 fact.
     * 
     * @param leftlist
     * @param right
     * @return
     */
    public boolean evaluate(Fact[] leftlist, Fact right) {
        boolean eval = true;
        // we iterate over the binds and evaluate the facts
        for (int idx = 0; idx < this.binds.length; idx++) {
            // we got the binding
            Binding bnd = binds[idx];
            eval = bnd.evaluate(leftlist, right);
            if (!eval) {
                break;
            }
        }
        return eval;
    }

    /**
     * Basic implementation will return string format of the betaNode
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int idx = 0; idx < this.binds.length; idx++) {
            if (idx > 0) {
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toBindString());
        }
        return buf.toString();
    }

    /**
     * returns the node named + node id and the bindings in a string format
     */
    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        buf.append("CubeQueryBNode-" + this.nodeID + "> ");
        for (int idx = 0; idx < this.binds.length; idx++) {
            if (idx > 0) {
                buf.append(" && ");
            }
            if (this.binds[idx] != null) {
                buf.append(this.binds[idx].toPPString());
            }
        }
        return buf.toString();
    }
}
