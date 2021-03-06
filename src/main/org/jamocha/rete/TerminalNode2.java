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

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * TerminalNode2 is different than TerminalNode in that it uses
 * a different Activation implementation. Rather than use BasicActivation,
 * it uses LinkedActivation.
 */
public class TerminalNode2 extends TerminalNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param id
	 */
	public TerminalNode2(int id, Rule rl) {
		super(id, rl);
		this.theRule = rl;
	}

	/**
	 * The terminal nodes doesn't have a memory, so the method
	 * does nothing.
	 */
	public void clear(WorkingMemory mem) {
		Map<?, ?> tmem = (Map<?, ?>) mem.getTerminalMemory(this);
		if (tmem != null) {
			tmem.clear();
		}
	}

	/**
	 * @param facts
	 * @param engine
	 */
	public void assertFacts(Index inx, Rete engine, WorkingMemory mem) {
		LinkedActivation act = new LinkedActivation(this.theRule, inx);
		act.setTerminalNode(this);
		Map<Index, Activation> tmem =  mem.getTerminalMemory(this);
		tmem.put(inx, act);
		// add the activation to the current module's activation list.
		engine.getAgenda().addActivation(act);
	}

	/**
	 * @param facts
	 * @param engine
	 */
	public void retractFacts(Index inx, Rete engine, WorkingMemory mem) {
		Map<?, ?> tmem = (Map<?, ?>) mem.getTerminalMemory(this);
        LinkedActivation act = (LinkedActivation) tmem.remove(inx);
		if (act != null) {
            engine.getAgenda().removeActivation(act);
		}
	}

	/**
	 * Return the Rule object associated with this terminal node
	 * @return
	 */
	public Rule getRule() {
		return this.theRule;
	}

	/**
	 * Remove the LinkedActivation from TerminalNode2. This is necessary
	 * when the activation is fired and the actions executed.
	 * @param LinkedActivation
	 */
	public void removeActivation(WorkingMemory mem, LinkedActivation activation) {
		Map<?, ?> tmem = (Map<?, ?>) mem.getTerminalMemory(this);
		tmem.remove(activation.getIndex());
	}

    /**
     * method does not apply to termial nodes. therefore it's not implemented
     */
    public void addSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem)
    throws AssertException {
    }

	/**
	 * return the name of the rule
	 */
	public String toString() {
		return this.theRule.getName();
	}

	/**
	 * return the name of the rule
	 */
	public String toPPString() {
		return this.theRule.getName();
	}
}
