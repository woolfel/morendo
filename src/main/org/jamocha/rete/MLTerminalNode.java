/*
 * Copyright 2002-2009 Peter Lin
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

import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * MLTerminalNode provides Modication logic functionality.
 */
public class MLTerminalNode extends TerminalNode2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean noAgenda = false;

	/**
	 * @param id
	 */
	public MLTerminalNode(int id, Rule rl) {
		super(id, rl);
		this.theRule = rl;
	}

	/**
	 * The implementation checks to see if the rule is active before it tries to
	 * assert the fact. It checks in the following order.
	 * 1. is the expiration date greater than zero
	 * 2. is the current time > the effective date
	 * 3. is the current time < the expiration date
	 * @param facts
	 * @param engine
	 */
	public void assertFacts(Index inx, Rete engine, WorkingMemory mem) {
		long time = System.currentTimeMillis();
		boolean add = true;
		// if the time is less than effective date or greater than expiration date
		// it is not currently effective, so we don't add the activation to the agenda
		if (this.theRule.getEffectiveDate() > 0 && this.theRule.getExpirationDate() > 0){
			if (time < this.theRule.getEffectiveDate()
				|| time > this.theRule.getExpirationDate()) {
				add = false;
			}
		}
		if (add) {
			LinkedActivation act = new LinkedActivation(this.theRule, inx);
			if (!this.noAgenda) {
				act.setTerminalNode(this);
				Map<Index, Activation> tmem =  mem.getTerminalMemory(this);
				tmem.put(act.getIndex(), act);
				// add the activation to the current module's activation list.
				engine.getAgenda().addActivation(act);
			} else {
		        engine.fireActivation(act);
			}
		}
	}

	/**
	 * The implementation is similar to TerminalNode3 and checks the effective
	 * and expiration date. If the activation is not null, it means it is
	 * still in the agenda and hasn't been executed yet. In that case, we remove
	 * the activation. If it isn't in the agenda, it means it already fired.
	 * that means we need to add ModificationActivation, which executes the
	 * corrective action defined by the rule.
	 * @param facts
	 * @param engine
	 */
	public void retractFacts(Index inx, Rete engine, WorkingMemory mem) {
		long time = System.currentTimeMillis();
        Map<Index, Activation> tmem =  mem.getTerminalMemory(this);
        LinkedActivation act = (LinkedActivation) tmem.remove(inx);
        if (act != null) {
            engine.getAgenda().removeActivation(act);
        } else {
        	// we add a new ModificationActivation to the agenda
        	ModificationActivation modact = new ModificationActivation(this.theRule, inx);
        	modact.setTerminalNode(this);
        	tmem.put(modact.getIndex(), modact);
        	engine.getAgenda().addActivation(modact);
        }
	}
	
	public void setNoAgenda(boolean noAgenda) {
		this.noAgenda = noAgenda;
	}
	
	public boolean getNoAgenda() {
		return this.noAgenda;
	}
}
