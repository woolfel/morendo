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
package org.jamocha.rete.query;

import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Evaluate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * 
 * @author Peter Lin
 *
 */
public class QueryIntraFactNode extends QueryBaseAlphaCondition {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Slot leftSlot = null;
    private Slot rightSlot = null;
    protected String hashstring = null;

    public QueryIntraFactNode(int id) {
        super(id);
    }
    
    /**
     * Set the operator using the int value
     * @param opr
     */
    public void setOperator(int opr) {
        this.operator = opr;
    }

    /**
     * Set the slot id. The slot id is the deftemplate slot id
     * @param id
     */
    public void setSlot(Slot sl) {
        this.leftSlot = sl;
    }

    public void setRightSlot(Slot sl) {
        this.rightSlot = sl;
    }
    
    public void assertFact(Fact factInstance, Rete engine, WorkingMemory mem)
            throws AssertException {
        if (evaluate(factInstance)) {
            propogateAssert(factInstance, engine, mem);
        }
    }

    public boolean evaluate(Fact factInstance) {
        return Evaluate.evaluate(this.operator,
                factInstance.getSlotValue(this.leftSlot.getId()),
                factInstance.getSlotValue(this.rightSlot.getId()));
    }
    
    public String hashString() {
        if (this.hashstring == null) {
            this.hashstring = this.leftSlot.getName() + ":" + this.operator + ":"
            + this.rightSlot.getName();
        }
        return this.hashstring;
    }

    public String toPPString() {
        return this.leftSlot.getName() + " " +
        ConversionUtils.getPPOperator(this.operator) + " "
        + this.rightSlot.getName()+ " - useCount=" +
        this.useCount;
    }

    public String toString() {
        return this.leftSlot.getName() + " " +
        ConversionUtils.getPPOperator(this.operator) + " "
        + this.rightSlot.getName()+ " - useCount=" +
        this.useCount;
    }

	public QueryBaseAlpha clone(Rete engine, Defquery query) {
		QueryIntraFactNode clone = new QueryIntraFactNode(engine.nextNodeId());
		clone.operator = this.operator;
		clone.leftSlot = (Slot)this.leftSlot.clone();
		clone.rightSlot = (Slot)this.rightSlot.clone();
		for (int i=0; i < this.successorNodes.length; i++) {
    		if (this.successorNodes[i] instanceof QueryBaseAlpha) {
    			clone.successorNodes[i] = ((QueryBaseAlpha)this.successorNodes[i]).clone(engine, query);
    		} else if (this.successorNodes[i] instanceof QueryBaseJoin) {
    			clone.successorNodes[i] = ((QueryBaseJoin)this.successorNodes[i]).clone(engine, query);
    		} else if (this.successorNodes[i] instanceof QueryResultNode) {
    			clone.successorNodes[i] = ((QueryResultNode)this.successorNodes[i]).clone(engine, query);
    		}
		}
		return clone;
	}
}
