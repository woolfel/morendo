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

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * AbstractTemporalNode is the base class for all temporal joins. For performance
 * reasons, we will have several subclasses.
 */
public abstract class AbstractTemporalNode extends BaseJoin {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The relative elapsed time for the left side of the join.
     * The default value is 0 to indicate it has no time window
     */
    protected int leftElapsedTime = 0;
    /**
     * the relative elapsed time for the right side of the join
     * The default value is 0 to indicate it has no time window
     */
    protected int rightElapsedTime = 0;
    
    public AbstractTemporalNode(int id) {
        super(id);
    }

    /**
     * assertLeft takes an array of facts. Since the next join may be joining
     * against one or more objects, we need to pass all previously matched
     * facts.
     * 
     * @param factInstance
     * @param engine
     */
    public abstract void assertLeft(Index linx, Rete engine, WorkingMemory mem)
            throws AssertException;

    /**
     * Assert from the right side is always going to be from an Alpha node.
     * 
     * @param factInstance
     * @param engine
     */
    public abstract void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws AssertException;

    /**
     * Retracting from the left requires that we propogate the
     * 
     * @param factInstance
     * @param engine
     */
    public abstract void retractLeft(Index linx, Rete engine, WorkingMemory mem)
            throws RetractException;

    /**
     * Retract from the right works in the following order. 1. remove the fact
     * from the right memory 2. check which left memory matched 3. propogate the
     * retract
     * 
     * @param factInstance
     * @param engine
     */
    public abstract void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws RetractException;

    /**
     * evaluate will first compare the timestamp of the last fact in the fact
     * array of the left and make sure the fact is still fresh. if it is not
     * fresh, the method returns false.
     * @param leftlist
     * @param right
     * @return
     */
    public boolean evaluate(Fact[] leftlist, Fact right, long time) {
        boolean eval = true;
        // first we compare the timestamp of the last fact in the
        // fact array. the last fact should be the fact with a
        // relative time window
        if (leftlist[leftlist.length - 1].timeStamp() > time) {
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
        } else {
            return false;
        }
    }
    
    protected long getRightTime() {
        long time;
        long ts = System.currentTimeMillis();
        if (this.rightElapsedTime > 0) {
            time = ts - this.rightElapsedTime;
        } else {
            time = 9223372036854775807L;
        }
        return time;
    }
    
    protected long getLeftTime() {
        long time;
        if (this.leftElapsedTime > 0) {
            time = System.currentTimeMillis() - this.leftElapsedTime;
        } else {
            time = 9223372036854775807L;
        }
        return time;
    }

    /**
     * Basic implementation will return string format of the betaNode
     */
    public abstract String toString();

    /**
     * returns the node named + node id and the bindings in a string format
     */
    public abstract String toPPString();

    public int getLeftElapsedTime() {
        return leftElapsedTime;
    }

    public void setLeftElapsedTime(int leftElapsedTime) {
        this.leftElapsedTime = leftElapsedTime;
    }

    public int getRightElapsedTime() {
        return rightElapsedTime;
    }

    public void setRightElapsedTime(int rightElapsedTime) {
        this.rightElapsedTime = rightElapsedTime;
    }
}
