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

import java.util.Map;

import org.jamocha.rete.exception.RetractException;
import org.jamocha.rule.Rule;

/**
 * @author woolfel
 * 
 * TemporalTNode is the temporal version of TerminalNode. The main difference
 * is the node will check the facts. If all facts have not expired, the activation
 * gets added to the agenda. If it's expired, the expired facts get retracted and
 * no activation is created.
 */
public class TemporalTNode extends TerminalNode2 {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean temporal = false;
    
    /**
     * @param id
     * @param rl
     */
    public TemporalTNode(int id, Rule rl) {
        super(id, rl);
    }

    public boolean isTemporal() {
        return temporal;
    }

    public void setTemporal(boolean temporal) {
        this.temporal = temporal;
    }
    
    /**
     * Method will call checkFacts() first to make sure none of the facts have
     * expired. An activation is only created if the facts are valid.
     * @param facts
     * @param engine
     */
	@SuppressWarnings("unchecked")
	public void assertFacts(Index inx, Rete engine, WorkingMemory mem) {
        // first check the facts and make sure they didn't expire
        if (checkFacts(inx,engine,mem)) {
            LinkedActivation act = new LinkedActivation(this.theRule, inx);
            act.setTerminalNode(this);
            if (this.temporal) {
                engine.fireActivation(act);
            } else {
                Map<Index, LinkedActivation> tmem = (Map<Index, LinkedActivation>) mem.getTerminalMemory(this);
                tmem.put(inx, act);
                // add the activation to the current module's activation list.
                engine.getAgenda().addActivation(act);
            }
        }
    }

    /**
     * if all the facts have not expired, the method returns true. If a fact has
     * expired, the method will retract the fact.
     * @param inx
     * @param engine
     * @param mem
     * @return
     */
    protected boolean checkFacts(Index inx, Rete engine, WorkingMemory mem) {
        Fact[] facts = inx.getFacts();
        boolean fresh = true;
        long current = System.currentTimeMillis();
        for (int idx=0; idx < facts.length; idx++) {
            if (facts[idx] instanceof TemporalFact) {
                TemporalDeffact tf = (TemporalDeffact)facts[idx];
                if (tf.getExpirationTime() < current) {
                    // the fact has expired
                    fresh = false;
                    try {
                        engine.retractFact(tf);
                    } catch (RetractException e) {
                        // we do nothing
                    }
                }
            }
        }
        return fresh;
    }

}
