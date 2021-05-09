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

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * IntraFactNode is specialized node for handling comparison between slots of
 * the same fact. Right now it only handles comparison between 2 slots. It
 * needs to be enhanced to handle 2 or more comparisons.
 * 
 * First we need to make it extend BaseAlpha instead and make it handle an
 * array.
 * @author woolfel
 *
 */
public class IntraFactNode extends BaseAlpha2 {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Slot leftSlot = null;
    private Slot rightSlot = null;
    protected String hashstring = null;

    public IntraFactNode(int id) {
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
            AlphaMemory alpha = (AlphaMemory) mem.getAlphaMemory(this);
            alpha.addPartialMatch(factInstance);
            // if watch is on, we notify the engine. Rather than
            // create an event class here, we let Rete do that.
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

    public void retractFact(Fact factInstance, Rete engine, WorkingMemory mem)
            throws RetractException {
        AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
        if (alpha.removePartialMatch(factInstance) != null) {
            // if watch is on, we notify the engine. Rather than
            // create an event class here, we let Rete do that.
            propogateRetract(factInstance,engine,mem);
        }
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

}
