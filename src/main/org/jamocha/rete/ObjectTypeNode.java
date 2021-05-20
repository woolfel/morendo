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
import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.query.QueryObjTypeNode;


/**
 * @author Peter Lin
 *
 * ObjectTypeNode is the input node for a specific type. The node
 * is created with the appropriate Class. A couple of important notes
 * about the implementation of ObjectTypeNode.
 * 
 * <ul>
 *   <li> the assertFact method does not check the deftemplate matches
 * the fact. this is because of inheritance.
 *   <li> WorkingMemoryImpl checks to see if the fact's deftemplate
 * has parents. If it does, it will keep checking to see if there is
 * an ObjectTypeNode for the parent.
 *   <li> if the template has a parent, it will assert it. this means
 *   <li> any patterns for parent templates will attempt to pattern
 * match
 * </ul>
 */
public class ObjectTypeNode extends BaseAlpha implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Class that defines object type
     */
    private Template deftemplate = null;
    
    /**
     * HashMap entries for unique AlphaNodes
     */
    @SuppressWarnings("rawtypes")
	private Map nodeHashMap = null;
    
    /**
     * Second ArrayList for all nodes that do not use ==, null operators
     */
    protected BaseNode[] nonHashNodes = new BaseNode[0];
    
    private boolean useNodeHash = false;
    
    /**
     * before the operators included not nill, but for it to work properly
     * we have to first test the slot is not nill, and then do the hash
     * lookup. not sure that it's worth it, so removed it instead.
     */
    public static final int[] operators = {Constants.EQUAL,
            Constants.NILL};
    
	/**
	 * 
	 */
	public ObjectTypeNode(int id, Template deftemp, Rete engine) {
		super(id);
        this.deftemplate = deftemp;
        nodeHashMap = engine.newLocalMap();
	}

    public Template getDeftemplate(){
        return this.deftemplate;
    }
    
    /**
     * clear the memory. for now the method does not
     * remove all the successor nodes. need to think it over a bit.
     */
    public void clear(WorkingMemory mem){
    	AlphaMemory am = (AlphaMemory) mem.getAlphaMemory(this);
    	am.clear();
    }

    /**
     * method to clear the successors. method doesn't iterate over
     * the succesors and clear them individually.
     */
    public void clearSuccessors() {
    	for (int idx=0; idx < this.successorNodes.length; idx++) {
    		this.successorNodes[idx].removeAllSuccessors();
    	}
    	this.successorNodes = new BaseNode[0];
    	this.nonHashNodes = new BaseNode[0];
    	this.nodeHashMap.clear();
    }
    
    /**
     * assert the fact and propogate. ObjectTypeNode does not call
     * assertEvent, since it's not that important and doesn't really
     * help debugging.
     * @param fact
     * @param engine
     */
    public void assertFact(Fact fact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        // ObjectTypeNode doesn't bother checking the deftemplate.
        ((AlphaMemory) mem.getAlphaMemory(this)).addPartialMatch(fact);
		// if the number of succesor nodes is less than (slot count * opCount)
    	if (useNodeHash) {
    		this.assertWithHash(fact, engine, mem);
    	} else {
    		this.assertAllSuccessors(fact, engine, mem);
    	}
    }

    /**
	 * assert using HashMap approach
	 * 
	 * @param fact
	 * @param engine
	 * @param mem
	 */
    public void assertWithHash(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
    	BaseSlot[] slots = this.deftemplate.getAllSlots();
    	for (int idx=0; idx < slots.length; idx++) {
    		if (slots[idx].getNodeCount() > 0) {
    			Object slotValue = fact.getSlotValue(idx);
    			if (slotValue == null) {
    				slotValue = Constants.NIL_SYMBOL;
    			}
    			CompositeIndex compIndex = new CompositeIndex(
    					slots[idx].getName(), Constants.EQUAL, slotValue);
    			
    			BaseNode node = (BaseNode)this.nodeHashMap.get(compIndex);
    			if (node != null) {
    	            if (node instanceof BaseAlpha){
    	                ((BaseAlpha)node).assertFact(fact,engine,mem);
    	            } else if (node instanceof BaseJoin){
    	                ((BaseJoin)node).assertRight(fact,engine,mem);
    	            } else if (node instanceof TerminalNode) {
    	                Index inx = new Index(new Fact[]{fact});
    	            	((TerminalNode)node).assertFacts(inx,engine,mem);
    	            }
    			}
    		}
    	}
    	
    	// iterate over all other nodes
    	for (int idx=0; idx < nonHashNodes.length; idx++) {
    		BaseNode node = nonHashNodes[idx];
            if (node instanceof BaseAlpha){
                ((BaseAlpha)node).assertFact(fact,engine,mem);
            } else if (node instanceof BaseJoin){
                ((BaseJoin)node).assertRight(fact,engine,mem);
            } else if (node instanceof TerminalNode) {
                Index inx = new Index(new Fact[]{fact});
            	((TerminalNode)node).assertFacts(inx,engine,mem);
            }
    	}
    }
 
    /**
     * Propogate the fact using the normal way of iterating over the
     * successors and calling assert on AlphaNodes and assertRight on
     * BetaNodes.
     * @param fact
     * @param engine
     * @param mem
     * @throws AssertException
     */
    public void assertAllSuccessors(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            Object node = this.successorNodes[idx];
            if (node instanceof BaseAlpha){
                ((BaseAlpha)node).assertFact(fact,engine,mem);
            } else if (node instanceof BaseJoin){
                ((BaseJoin)node).assertRight(fact,engine,mem);
            } else if (node instanceof TerminalNode) {
                Index inx = new Index(new Fact[]{fact});
            	((TerminalNode)node).assertFacts(inx,engine,mem);
            }
        }
    }
    
    /**
     * Retract the fact to the succeeding nodes. ObjectTypeNode does not call
     * assertEvent, since it's not that important and doesn't really
     * help debugging.
     * @param fact
     * @param engine
     */
    public void retractFact(Fact fact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        if (fact.getDeftemplate() == this.deftemplate){
            ((AlphaMemory)mem.getAlphaMemory(this)).removePartialMatch(fact);
            for (int idx=0; idx < this.successorNodes.length; idx++) {
                Object node = this.successorNodes[idx];
                if (node instanceof BaseAlpha){
                    ((BaseAlpha)node).retractFact(fact,engine,mem);
                } else if (node instanceof BaseJoin){
                    ((BaseJoin)node).retractRight(fact,engine,mem);
                }
            }
        }
    }

    /**
     * return the number of successor nodes
     * @return
     */
    public int getSuccessorCount(){
        return this.successorNodes.length;
    }

    public int getStandardCostValue() {
    	int cost = 0;
    	cost = this.successorNodes.length;
    	for (int idx=0; idx < successorNodes.length; idx++) {
    		if (successorNodes[idx] instanceof BaseJoin) {
    			cost = cost + 4;
    		} else {
    			cost++;
    		}
    	}
    	return cost;
    }
    /**
     * The method returns the successor count taking into consideration
     * the alpha node hashing. It is used by the topology cost function.
     * @return
     */
    public int getOptimizedCostValue() {
    	if (this.useNodeHash) {
        	int cost = 1;
        	for (int idx=0; idx < nonHashNodes.length; idx++) {
        		if (nonHashNodes[idx] instanceof BaseJoin) {
        			cost = cost + 4;
        		} else {
        			cost++;
        		}
        	}
        	return cost;
    	} else {
    		return getStandardCostValue();
    	}
    }
    
    /**
     * Add a successor node
     */
    @SuppressWarnings("unchecked")
	public void addSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem) 
    throws AssertException 
    {
    	if (node instanceof AlphaNode) {
    		AlphaNode alphaNode = (AlphaNode)node;
    		if (alphaNode.getOperator() == Constants.EQUAL) {
        		nodeHashMap.put(alphaNode.getHashIndex(), alphaNode);
        		// increment the slot use count
        		this.deftemplate.getSlot(alphaNode.slot.getId()).incrementNodeCount();
    		}
    	} else {
    		// in all other cases, we add it to the second list of child nodes
    		if (!containsNode(this.nonHashNodes, node)) {
    			this.nonHashNodes = ConversionUtils.add(this.nonHashNodes, node);
    		}
    	}
    	// always add it to the base successor array, which as all child nodes
        if (!containsNode(this.successorNodes,node)) {
            addNode(node);
        }
        if (this.successorNodes.length > (this.deftemplate.getSlotsUsed() * 2)) {
        	this.useNodeHash = true;
        }
        // if there are matches, we propogate the facts to 
        // the new successor only
        AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
        if (alpha.size() > 0){
            @SuppressWarnings("rawtypes")
			Iterator itr = alpha.iterator();
            while (itr.hasNext()){
                Fact f = (Fact)itr.next();
                if (node instanceof BaseAlpha) {
                    BaseAlpha next = (BaseAlpha) node;
                    next.assertFact(f,engine,mem);
                } else if (node instanceof BaseJoin) {
                    BaseJoin next = (BaseJoin) node;
                    next.assertRight(f,engine,mem);
                } else if (node instanceof TerminalNode) {
                    TerminalNode t = (TerminalNode)node;
                    Index inx = new Index(new Fact[]{f});
                    t.assertFacts(inx, engine, mem);
                }
            }
        }
    }
    
    public boolean removeNode(BaseNode n) {
    	boolean rem = super.removeNode(n);
    	ConversionUtils.remove(this.nonHashNodes,n);
    	if (n instanceof AlphaNode) {
        	this.nodeHashMap.remove(((AlphaNode)n).getHashIndex());
    	}
    	return rem;
    }
    
    /**
     * For the ObjectTypeNode, the method just returns toString
     */
    public String hashString() {
        return toString();
    }
    
    /**
     * this returns name of the deftemplate
     */
    public String toString(){
        return "ObjectTypeNode( " + this.deftemplate.getName() + " ) -";
    }

    /**
     * this returns name of the deftemplate
     */
    public String toPPString(){
        return " Template( " + this.deftemplate.getName() + " )";
    }
    
    public Object[] getSuccessorNodes() {
    	return successorNodes;
    }
    
    public QueryObjTypeNode createQueryObjTypeNode(Rete engine) {
    	QueryObjTypeNode clone = new QueryObjTypeNode(engine,this);
    	clone.successorNodes = new BaseNode[0];
    	clone.useCount = 0;
    	return clone;
    }
}
