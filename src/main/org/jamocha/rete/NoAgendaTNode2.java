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

import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * NoAgendaTNode2 is similar to NoAgendaTNode with one difference. The rule
 * may be deploy, but isn't effective until a given time. The terminal node
 * will only create the activation if the current time is between the effective
 * and expiration date of the rule.
 */
public class NoAgendaTNode2 extends NoAgendaTNode {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * @param id
	 */
	public NoAgendaTNode2(int id, Rule rl) {
		super(id,rl);
        this.theRule = rl;
	}

    /**
     * @param facts
     * @param engine
     */
    public void assertFacts(Index inx, Rete engine, WorkingMemory mem){
		long time = System.currentTimeMillis();
		if (this.theRule.getExpirationDate() > 0
				&& time > this.theRule.getEffectiveDate()
				&& time < this.theRule.getExpirationDate()) {
			LinkedActivation act = new LinkedActivation(this.theRule, inx);
			act.setTerminalNode(this);
			// fire the activation immediately
			engine.fireActivation(act);
		}
    }
    
}
